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
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_Ability extends ServerBasePacket {
	
	private static final String S_ABILITY = "[S] S_Ability";
	//private byte[] _byte = null;

	public S_Ability(L1PcInstance pc, int type, boolean equipped) {
		buildPacket(pc,type, equipped);
	}

	private void buildPacket(L1PcInstance pc,int type, boolean equipped) {
		writeC(Opcodes.S_OPCODE_ABILITY);
		writeC(type); // 1:ROTC 5:ROSC
		if (equipped) {
			writeC(0x01);
		} else {
			writeC(0x00);
		}
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return S_ABILITY;
	}
}
