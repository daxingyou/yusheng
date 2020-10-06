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



import l1j.server.AcceleratorChecker;
import l1j.server.Config;
import l1j.server.server.WriteLogTxt;
//import l1j.server.server.ClientThread;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.mina.LineageClient;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.world.L1World;
import static l1j.server.server.model.skill.L1SkillId.*;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_UseSkill extends ClientBasePacket {

	public C_UseSkill(byte abyte0[], LineageClient _client) throws Exception {
		super(abyte0);
		final int row = readC();
		final int column = readC();
		//int skillId = (row * 8) + column + 1;
		final int skillId = (row << 3) + column + 1;
		String charName = null;
		String message = null;
		int targetId = 0;
		int targetX = 0;
		int targetY = 0;
		L1PcInstance pc = _client.getActiveChar();

		if (pc.isStop()) {
			return;
		}
		if (pc.isTeleport() || pc.isDead()||pc.isGhost()) {
			return;
		}
		if (pc.getMapId() == 5140) { // 化屋敷
			pc.sendPackets(new S_ServerMessage(563)); // \f1使。
			return;
		}
		if (!pc.isSkillMastery(skillId)) {
			return;
		}
		if (pc.isPrivateShop()) {
			return;
		}
		
/*		if (pc.isCheck()) {	
			if (skillId == TELEPORT || skillId == MASS_TELEPORT) {
			}else {
				pc.sendPackets(new S_SystemMessage("防挂系统：传送后必须移动下或者攻击一下，才可以使用技能，谢谢配合！"));
				return;
			}		
		}*/
		// 攻态确认
		if (pc.getInventory().getWeight240() >= 197) { // 重量
			pc.sendPackets(new S_ServerMessage(110)); // 
			if (skillId == TELEPORT || skillId == MASS_TELEPORT) {
				pc.sendPackets(new S_Paralysis(
						S_Paralysis.TYPE_TELEPORT_UNLOCK,0, false));
			}		
			return;
		}

		// 要求间隔
		if (Config.CHECK_SPELL_INTERVAL) {
			int result;
			if (SkillsTable.getInstance().getTemplate(skillId).getActid() == 18) {
				result = pc.speed_Attack()
						.checkIntervalskilldir();
			} else {
				result = pc.speed_Attack().checkIntervalskillnodir();
			}
			if (result == AcceleratorChecker.R_DISPOSED) {
				return;
			}
		}

		// 增加三重矢武器判断 
		if (skillId == TRIPLE_ARROW && pc.getWeapon().getItem().getType1() != 20) {
			pc.sendPackets(new S_ServerMessage(1008));
			return;
		}
		// 增加三重矢武器判断  end

		if (abyte0.length > 4) {
			try {
				if (skillId == CALL_CLAN || skillId == RUN_CLAN) { // 、
					charName = readS();
				} else if (skillId == TRUE_TARGET) { // 
					targetId = readD();
					targetX = readH();
					targetY = readH();
					message = readS();
				} else if (skillId == TELEPORT || skillId == MASS_TELEPORT) { // 、
					targetId = readH(); // MapID
					targetX = readH();
                    targetY = readH();
                    pc.setTempBookmarkMapID((short)targetId);
                    pc.setTempBookmarkLocX(targetX);
                    pc.setTempBookmarkLocY(targetY);
				} else if (skillId == FIRE_WALL || skillId == LIFE_STREAM) { // 、
					targetX = readH();
					targetY = readH();
				} else {
					targetId = readD();
					targetX = readH();
					targetY = readH();
				}
			} catch (Exception e) {
				// _log.log(Level.SEVERE, "", e);
			}
		}

		if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) { //  解除
			pc.killSkillEffectTimer(ABSOLUTE_BARRIER);
			pc.startHpRegeneration();
			pc.startMpRegeneration();
//			pc.startMpRegenerationByDoll();
		}
		pc.killSkillEffectTimer(MEDITATION);

		try {
			if (skillId == CALL_CLAN || skillId == RUN_CLAN) { // 、
				if (charName.isEmpty()) {
					// 名前空场合弹
					return;
				}

				L1PcInstance target = L1World.getInstance().getPlayer(charName);

				if (target == null) {
					// 正确未调查
					pc.sendPackets(new S_ServerMessage(73, charName)); // \f1%0。
					return;
				}
				if (pc.getClanid() != target.getClanid()) {
					pc.sendPackets(new S_ServerMessage(414)); // 同血盟员。
					return;
				}
				targetId = target.getId();
			}
			if (pc.isCheckFZ()) {
				WriteLogTxt.Recording(pc.getName()+"施法","变身ID"+pc.getTempCharGfx()+" 武器"+pc.getWeapon().getLogViewName()+"施法技能ID"+skillId+"动作");
			}
			L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(pc, skillId, targetId, targetX, targetY,
					message, 0, L1SkillUse.TYPE_NORMAL);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
