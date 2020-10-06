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

public class S_SystemMessage extends ServerBasePacket {
	private static final String S_SYSTEM_MESSAGE = "[S] S_SystemMessage";


	/**
	 * 存在表示。
	 * nameid($xxx)含场合一方使用。
	 * 
	 * @param msg - 表示文字列
	 */
	public S_SystemMessage(String msg) {
		writeC(Opcodes.S_OPCODE_GLOBALCHAT);
		writeC(0x09);
		writeS(msg);
	}

	/**
	 * 存在表示。
	 * 
	 * @param msg - 表示文字列
	 * @param nameid - 文字列nameid($xxx)含场合true。
	 */
	public S_SystemMessage(String msg, boolean nameid) {
		writeC(Opcodes.S_OPCODE_NPCSHOUT);
		writeC(2);
		writeD(0);
		writeS(msg);
		// NPCnameid解释利用
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return S_SYSTEM_MESSAGE;
	}
}
