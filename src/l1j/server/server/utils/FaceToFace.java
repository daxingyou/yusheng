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

package l1j.server.server.utils;

import java.util.List;

import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.world.L1World;

// Referenced classes of package l1j.server.server.utils:
// FaceToFace

public class FaceToFace {


	private FaceToFace() {
	}
	public static boolean faceToFacenpc(L1PcInstance pc,L1NpcInstance npc) {
		if (pc.getHeading() == 0){
			return npc.getHeading() == 4;
		}
		if (pc.getHeading() == 1){
			return npc.getHeading() == 5;
		}
		if (pc.getHeading() == 2){
			return npc.getHeading() == 6;
		}
		if (pc.getHeading() == 3){
			return npc.getHeading() == 7;
		}
		if (pc.getHeading() == 4){
			return npc.getHeading() == 0;
		}
		if (pc.getHeading() == 5){
			return npc.getHeading() == 1;
		}
		if (pc.getHeading() == 6){
			return npc.getHeading() == 2;
		}
		if (pc.getHeading() == 7){
			return npc.getHeading() == 3;
		}
		return false;
	}
	public static L1PcInstance faceToFace(L1PcInstance pc) {
		int pcX = pc.getX();
		int pcY = pc.getY();
		int pcHeading = pc.getHeading();
		List<L1PcInstance> players = L1World.getInstance().getVisiblePlayer(pc,
				1);

		if (players.size() == 0) { // 1以内PC居场合
			pc.sendPackets(new S_ServerMessage(93)); // \f1谁。
			return null;
		}
		for (L1PcInstance target : players) {
			if (target.isGmInvis()) {
				continue;
			}
			if (target.isDead()) {
				continue;
			}
			int targetX = target.getX();
			int targetY = target.getY();
			int targetHeading = target.getHeading();
			if (pcHeading == 0 && pcX == targetX && pcY == (targetY + 1)) {
				if (targetHeading == 4) {
					return target;
				} else {
					pc.sendPackets(new S_ServerMessage(91, target.getName())); // \f1%0见。
					return null;
				}
			} else if (pcHeading == 1 && pcX == (targetX - 1)
					&& pcY == (targetY + 1)) {
				if (targetHeading == 5) {
					return target;
				} else {
					pc.sendPackets(new S_ServerMessage(91, target.getName())); // \f1%0见。
					return null;
				}
			} else if (pcHeading == 2 && pcX == (targetX - 1) && pcY == targetY) {
				if (targetHeading == 6) {
					return target;
				} else {
					pc.sendPackets(new S_ServerMessage(91, target.getName())); // \f1%0见。
					return null;
				}
			} else if (pcHeading == 3 && pcX == (targetX - 1)
					&& pcY == (targetY - 1)) {
				if (targetHeading == 7) {
					return target;
				} else {
					pc.sendPackets(new S_ServerMessage(91, target.getName())); // \f1%0见。
					return null;
				}
			} else if (pcHeading == 4 && pcX == targetX && pcY == (targetY - 1)) {
				if (targetHeading == 0) {
					return target;
				} else {
					pc.sendPackets(new S_ServerMessage(91, target.getName())); // \f1%0见。
					return null;
				}
			} else if (pcHeading == 5 && pcX == (targetX + 1)
					&& pcY == (targetY - 1)) {
				if (targetHeading == 1) {
					return target;
				} else {
					pc.sendPackets(new S_ServerMessage(91, target.getName())); // \f1%0见。
					return null;
				}
			} else if (pcHeading == 6 && pcX == (targetX + 1) && pcY == targetY) {
				if (targetHeading == 2) {
					return target;
				} else {
					pc.sendPackets(new S_ServerMessage(91, target.getName())); // \f1%0见。
					return null;
				}
			} else if (pcHeading == 7 && pcX == (targetX + 1)
					&& pcY == (targetY + 1)) {
				if (targetHeading == 3) {
					return target;
				} else {
					pc.sendPackets(new S_ServerMessage(91, target.getName())); // \f1%0见。
					return null;
				}
			}
		}
		pc.sendPackets(new S_ServerMessage(93)); // \f1谁。
		return null;
	}
}
