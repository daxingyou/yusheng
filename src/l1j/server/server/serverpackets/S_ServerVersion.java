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

public class S_ServerVersion extends ServerBasePacket {
	private static final String S_SERVER_VERSION = "[S] ServerVersion";

	public S_ServerVersion(final int language) {
        this.writeC(Opcodes.S_OPCODE_SERVERVERSION);
        this.writeC(0x00);// 允许登入
        this.writeC(0x01);
        this.writeD(0x0734fd33);// server verion:dc 87 01 00(保留参考用)
        this.writeD(0x0734fd30);// cache verion:d1 87 01 00(保留参考用)
        this.writeD(0x77cf6eba);// auth verion:01 ee 00 00(保留参考用)
        this.writeD(0x0734fd31);// npc verion:7f 87 01 00(保留参考用)
        this.writeD(0x00);
        this.writeC(0x00);
        this.writeC(0x00);
        this.writeC(language);
        this.writeD(0x77d82);
        this.writeD((int) (System.currentTimeMillis() / 1000L));
        this.writeH(0x01);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return S_SERVER_VERSION;
	}
}
