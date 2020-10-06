/* This program is free software; you can redistribute it and/or modify
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

import static l1j.server.server.model.Instance.L1PcInstance.REGENSTATE_MOVE;
import l1j.server.AcceleratorChecker;
import l1j.server.Config;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.Dungeon;
import l1j.server.server.model.DungeonRandom;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.serverpackets.S_Lock;
import l1j.server.server.serverpackets.S_MoveCharPacket;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_MoveChar extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_MoveChar.class);

	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };

	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };
	// 调查用
/*	private void sendMapTileLog(L1PcInstance pc) {
		pc.sendPackets(new S_SystemMessage(pc.getMap().toString(
				pc.getLocation())));
	}*/

	// 移动
	public C_MoveChar(byte decrypt[], LineageClient _client)
			throws Exception {
		super(decrypt);
		try {
			int locx = readH();
			int locy = readH();
			int heading = readC();

			L1PcInstance pc = _client.getActiveChar();
			if (pc == null) {
				return;
			}
			
			if (pc.isOnlyStopMove()) {
				return;
			}
			
			if (pc.isPrivateShop()|| pc.isDead() ) {
				return;
			}
			
			if (pc.isStop()) {
				return;
			}

			if (pc.isTeleport()) { // 处理中
				return;
			}
			
			pc.setCheck(false);
			
			// 移动前位置
			final int oleLocx = pc.getX();
			final int oleLocy = pc.getY();
						
			pc.setOleLocX(oleLocx);
			pc.setOleLocY(oleLocy);
						
			if (Config.CHECK_MOVE_INTERVAL) {
				final int result = pc.speed_Attack().checkIntervalmove();
				if (result == AcceleratorChecker.R_DISPOSED) {
					if (!pc.isGm()) {
						//WriteLogTxt.Recording("行走加速记录","玩家"+this._activeChar.getName()+"加速");
						pc.sendPackets(new S_SystemMessage("防加速机制有效!"));
						pc.sendPackets(new S_Lock());
						return;
						//isError = true;
					}else {
						pc.sendPackets(new S_SystemMessage("防加速机制有效!"));
					}				
				}
			}				
			//if (_client.getLanguage() == 3) {
				//heading ^= 0x49;
			//}
			if (heading>7) {
				return;
			}
			if (heading<0) {
				return;
			}
			
			boolean iserror = false;
			if (_client.getLanguage() != 3) {
				if ((locx != oleLocx) || (locy != oleLocy)) {
					iserror = true;
				}
			}	
			// 移动后位置
			final int newlocx = oleLocx + HEADING_TABLE_X[heading];
			final int newlocy = oleLocy + HEADING_TABLE_Y[heading];
			for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 1)) {
				if (obj instanceof L1PcInstance) {
					L1PcInstance tgpc = (L1PcInstance)obj;
					if (tgpc.isGhost()) {
						continue;
					}
					if (tgpc.isGmInvis()) {
						continue;
					}
					if (pc.isGhost()) {
						continue;
					}
					if (pc.isGmInvis()) {
						continue;
					}
					if (tgpc.isDead()) {
						continue;
					}
					if (tgpc.getX()==newlocx&&tgpc.getY()==newlocy) {
						iserror = true;
						break;
					}
				}
				if (obj instanceof L1MonsterInstance) {
					L1MonsterInstance tgmob = (L1MonsterInstance)obj;
					if (tgmob.isDead()) {
						continue;
					}
					if (tgmob.getX()==newlocx&&tgmob.getY()==newlocy) {
						iserror = true;
						break;
					}
				}
			}
	/*		if (pc.getWarid()!=0) {
				if (!pc.getMap().isPassable2(newlocx, newlocy)) {
					iserror = true;
				}
			}*/
			
			if (iserror) {
				pc.sendPackets(new S_Lock());
				return;
			}
			pc.killSkillEffectTimer(L1SkillId.MEDITATION);

			if (!pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) { // 中
				pc.setRegenState(REGENSTATE_MOVE);
			}
			
			if (!pc.isGmInvis()) {
				pc.getMap().setPassable(pc.getLocation(), true);
			}	

			if (Dungeon.getInstance().dg(newlocx, newlocy, pc.getMap().getId(), pc)) { // 场合
				return;
			}
			if (DungeonRandom.getInstance().dg(newlocx, newlocy, pc.getMap().getId(),
					pc)) { // 先地
				return;
			}
			
			if (!pc.getPowerMap().isPassable2(newlocx, newlocy)) {
				pc.sendPackets(new S_SystemMessage("客户端不匹配，请到官网下载专用客户端"));
				pc.sendPackets(new S_Lock());
				return;
			}
			if (pc.isCheckFZ()) {
				WriteLogTxt.Recording(pc.getName()+"移动","变身ID"+pc.getTempCharGfx()+" 武器"+pc.getWeapon().getLogViewName()+"移动");
			}

			pc.getLocation().set(newlocx, newlocy);
			pc.setHeading(heading);
			if (!pc.isGmInvis() && !pc.isGhost() && !pc.isInvisble()) {
				pc.broadcastPacket(new S_MoveCharPacket(pc));
			}

			// sendMapTileLog(pc); // 移动先情报送(调查用)

			L1WorldTraps.getInstance().onPlayerMoved(pc);

			if (!pc.isGmInvis()&&!pc.isGhost()) {
				pc.getMap().setPassable(pc.getLocation(), false);
			}
			// user.UpdateObject(); // 可视范围内全更新
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}finally {
			this.over();
		}

	}
}