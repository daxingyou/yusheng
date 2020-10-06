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
import l1j.server.server.model.L1Character;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_MoveCharPacket extends ServerBasePacket {

	private static final String _S__1F_MOVECHARPACKET = "[S] S_MoveCharPacket";

	private static final byte[] HEADING_TABLE_XR = { 0, -1, -1, -1, 0, 1, 1, 1 };
	  private static final byte[] HEADING_TABLE_YR = { 1, 1, 0, -1, -1, -1, 0, 1 };

	public S_MoveCharPacket(L1Character cha) {
		int locx = cha.getX();
	    int locy = cha.getY();
	    int heading = cha.getHeading();
	    locx += HEADING_TABLE_XR[heading];
	    locy += HEADING_TABLE_YR[heading];
		writeC(Opcodes.S_OPCODE_MOVEOBJECT);
		writeD(cha.getId());
		writeH(locx);
		writeH(locy);
		writeC(cha.getHeading());
		writeC(0x80);
/*		writeC(129);
		writeD(0);*/
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return _S__1F_MOVECHARPACKET;
	}
}