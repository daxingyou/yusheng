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


import l1j.server.server.Opcodes;


// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_Doors extends ServerBasePacket {

	private static final String S_DOORS = "[S] S_Doors";


	public S_Doors(int X, int Y, int TYPE, int Status) {
		buildPacket(X, Y, TYPE, Status);
	}

	private void buildPacket(int X, int Y, int TYPE, int Status) {
		writeC(Opcodes.S_OPCODE_ATTRIBUTE);
		writeH(X);
		writeH(Y);
		writeC(TYPE); // 方向 0: ／ 1: ＼
		writeC(Status);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return S_DOORS;
	}
}
