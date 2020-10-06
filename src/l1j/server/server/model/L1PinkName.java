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
package l1j.server.server.model;

import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.WarTimeController;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_PinkName;

// Referenced classes of package l1j.server.server.model:
// L1PinkName

public class L1PinkName {

	private L1PinkName() {
	}
	
	public static void stopPinkName(L1PcInstance attacker) {
		attacker.sendPackets(new S_PinkName(attacker.getId(), 0));
		attacker.broadcastPacket(new S_PinkName(attacker.getId(), 0));
	}

	public static void onAction(L1Character cha, L1Character attacker) {
		if (attacker == null || cha == null) {
			return;
		}
		
		

/*		if (!(cha instanceof L1PcInstance)) {
			return;
		}*/
/*		L1PcInstance attacker = (L1PcInstance) cha;
		if (player == attacker) {
			return;
		}*/
		if (!(attacker instanceof L1PcInstance)&&!(attacker instanceof L1SummonInstance)&&!(attacker instanceof L1PetInstance)) {
			return;
		}
		if (!(cha instanceof L1PcInstance)&&!(cha instanceof L1SummonInstance)&&!(cha instanceof L1PetInstance)) {
			return;
		}
//		System.out.println("1攻击者:"+attacker.getName());
		boolean is_now_war = false;
		int castle_id = L1CastleLocation.getCastleIdByArea(cha.getLocation());
		if (castle_id != 0) { // 旗内居
			is_now_war = WarTimeController.getInstance().isNowWar(castle_id);
		}
//		System.out.println("2攻击者:"+attacker.getName());
		if (cha.getLawful() >= 0
				&& // player, attacker共青
				!cha.isPinkName() && attacker.getLawful() >= 0
				&& !attacker.isPinkName()) {
			if (cha.getZoneType() == 0 && // 共、战争时间内旗内
					attacker.getZoneType() == 0 && is_now_war == false) {
//				System.out.println("粉名对象："+attacker.getName());
				attacker.setPinkSec(30);
				if (attacker instanceof L1PcInstance) {
					((L1PcInstance)attacker).sendPackets(new S_PinkName(attacker.getId(), attacker.getPinkSec()));
				}		
				attacker.broadcastPacket(new S_PinkName(attacker.getId(),
						attacker.getPinkSec()));
			}
		}
//		System.out.println("3攻击者:"+attacker.getName());
	}
}
