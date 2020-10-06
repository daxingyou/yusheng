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
package l1j.server.server.model.Instance;

/*import java.util.ArrayList;
import java.util.logging.Logger;
*/
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Npc;

public class L1DwarfInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1DwarfInstance.class);
/*
	private ArrayList _tpLocs;

	private int _tpId;*/

	/**
	 * @param template
	 */
	public L1DwarfInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance player) {
		L1Attack attack = new L1Attack(player, this);
		// 命中判断 
		if (attack.calcHit()) {
			attack.calcDamage();
			attack.calcStaffOfMana();
			attack.addPcPoisonAttack(player, this);
		}
		// 命中判断 
		attack.calcHit();
		attack.action();
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		if (talking != null) {
			S_NPCTalkReturn talk = null;
			if (player.getLevel() < 5) { // 修改等级小于5不能使用仓库 
				talk = new S_NPCTalkReturn(talking, objid, 2);
			} else {
				talk = new S_NPCTalkReturn(talking, objid, 1);
			}
			player.sendPackets(talk);
		} else {
			_log.info("No actions for Dwarf id : " + objid);
		}
	}

	private int getTemplateid() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onFinalAction(L1PcInstance player, String Action) {
//		int objid = getTemplateid();
		if (Action.equalsIgnoreCase("retrieve")) {
		} else if (Action.equalsIgnoreCase("retrieve-pledge")) {
			if (player.getClanname().equalsIgnoreCase(" ")) {
				S_ServerMessage talk = new S_ServerMessage(
						(S_ServerMessage.NO_PLEDGE), Action);
				player.sendPackets(talk);
			} else {
				
			}
		}
	}
}
