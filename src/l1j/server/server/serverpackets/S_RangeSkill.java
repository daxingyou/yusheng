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

package l1j.server.server.serverpackets;
import java.util.concurrent.atomic.AtomicInteger;


import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Character;

// Referenced classes of package net.l1j.server.serverpackets:
// ServerBasePacket

public class S_RangeSkill extends ServerBasePacket {

	private static final String S_RANGE_SKILL = "[S] S_RangeSkill";


	private static AtomicInteger _sequentialNumber = new AtomicInteger(0);


	public static final byte TYPE_NODIR = 0;

	public static final byte TYPE_DIR = 8;

	public S_RangeSkill(L1Character cha, L1Character[] target, int spellgfx,
			int actionId, int type) {
		buildPacket(cha, target, spellgfx, actionId, type);
	}

	private void buildPacket(L1Character cha, L1Character[] target,
			int spellgfx, int actionId, int type) {
		writeC(Opcodes.S_OPCODE_RANGESKILLS);
		writeC(actionId);
		writeD(cha.getId());
		writeH(cha.getX());
		writeH(cha.getY());
		if (type == TYPE_NODIR) {
			writeC(cha.getHeading());
		} else if (type == TYPE_DIR) {
			int newHeading = calcheading(cha.getX(), cha.getY(),
					target[0].getX(), target[0].getY());
			cha.setHeading(newHeading);
			writeC(cha.getHeading());
		}
		//System.out.println("使用S_RangeSkill封包");
		writeD(_sequentialNumber.incrementAndGet()); // 番号がダブらないように送る。
		writeH(spellgfx);
		writeC(type); // 0:范围 6:远距离 8:范围&远距离
		writeH(0);
		writeH(target.length);
		for (short i = 0; i < target.length; i++) {
			writeD(target[i].getId());
			writeC(0x20); // 0:ダメージモーションあり 0以外:なし
		}
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	private static int calcheading(int myx, int myy, int tx, int ty) {
		if (tx > myx && ty > myy) {
			return 3;
		} else if (tx < myx && ty < myy) {
			return 7;
		} else if (tx > myx && ty == myy) {
			return 2;
		} else if (tx < myx && ty == myy) {
			return 6;
		} else if (tx == myx && ty < myy) {
			return 0;
		} else if (tx == myx && ty > myy) {
			return 4;
		} else if (tx < myx && ty > myy) {
			return 5;
		} else if (tx > myx && ty < myy) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public String getType() {
		return S_RANGE_SKILL;
	}

}