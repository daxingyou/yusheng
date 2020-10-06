/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.server.server.clientpackets;



import static l1j.server.server.model.Instance.L1PcInstance.REGENSTATE_ATTACK;

import java.util.Calendar;

import l1j.server.AcceleratorChecker;
import l1j.server.Config;
import l1j.server.server.WriteLogTxt;
//import l1j.server.server.ClientThread;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_AttackPacketPc;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Attack extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Attack.class);

	public C_Attack(byte[] decrypt, LineageClient _client) {
		super(decrypt);
		try {
			final int targetId = readD();
			final int x = readH();
			final int y = readH();

			final L1PcInstance pc = _client.getActiveChar();

			pc.isFoeSlayer(false);
			
			if (pc == null || pc.isGhost() || pc.isDead() || pc.isTeleport()) {
				return;
			}
			if (pc.isStop()) {
				return;
			}
			if (pc.isPrivateShop()) {
				return;
			}
			final L1Object target = L1World.getInstance().findObject(targetId);
			// 攻态确认
			if (pc.getInventory().getWeight240() >= 197) { // 重量
				pc.sendPackets(new S_ServerMessage(110)); // \f1重。
				return;
			}

			if (pc.isInvisble()) { // 、中
				return;
			}

			if (pc.isInvisDelay()) { // 中
				return;
			}

			if (target instanceof L1Character) {
				if (target.getMapId() != pc.getMapId()
						|| pc.getLocation().getLineDistance(target.getLocation()) > 20D) { // 异常场所终了
					return;
				}
			}

			if (target instanceof L1NpcInstance) {
				if (((L1NpcInstance) target).getHiddenStatus() != 0) { // 地中、飞
					return;
				}
			}

			// 攻要求间隔
			if (Config.CHECK_ATTACK_INTERVAL) {
				final int result = pc.speed_Attack().checkIntervalattack();
				if (result == AcceleratorChecker.R_DISPOSED) {
					// _log.error("要求角色攻击:速度异常(" + pc.getName() + ")");
					return;
				}
			}
			if (pc.isCheckFZ()) {
				WriteLogTxt.Recording(pc.getName()+"攻击","变身ID"+pc.getTempCharGfx()+" 武器"+pc.getWeapon().getLogViewName()+"攻击动作");
			}

			// 攻场合理
			if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) { //  解除
				pc.killSkillEffectTimer(L1SkillId.ABSOLUTE_BARRIER);
				pc.startHpRegeneration();
				pc.startMpRegeneration();
//				pc.startMpRegenerationByDoll();
			}
			pc.killSkillEffectTimer(L1SkillId.MEDITATION);

			pc.delInvis(); // 透明态解除

			pc.setRegenState(REGENSTATE_ATTACK);
			
			pc.setCheck(false);
			
			/**
			 * 弱点曝光
			 */
			if (pc.get_weaknss() != 0) {
				long h_time = Calendar.getInstance().getTimeInMillis() / 1000;// 换算为秒
				if (h_time - pc.get_weaknss_t() > 16) {
					pc.set_weaknss(0, 0);
				}
			}

			if (target != null && (target instanceof L1Character)&&!((L1Character) target).isDead()) { // 补上&& !injustice 判断 
				//System.out.println("攻击输出开始！");
				target.onAction(pc);
				//System.out.println("攻击输出完毕！");
			} else { // 空攻				
				pc.setHeading(pc.targetDirection(x, y)); // 向
				pc.sendPackets(new S_ChangeHeading(pc));
				pc.sendPacketsAll(new S_AttackPacketPc(pc));
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}finally {
			this.over();
		}

	}
}
