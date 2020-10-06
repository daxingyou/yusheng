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

public class S_CharPacks extends ServerBasePacket {
	private static final String S_CHAR_PACKS = "[S] S_CharPacks";

	/**
     * 角色资讯
     * 
     * @param name
     * @param clanName
     * @param type
     * @param sex
     * @param lawful
     * @param hp
     * @param mp
     * @param ac
     * @param lv
     * @param str
     * @param dex
     * @param con
     * @param wis
     * @param cha
     * @param intel
     */
    public S_CharPacks(final String name, final String clanName,
            final int type, final int sex, final int lawful, final int hp,
            final int mp, final int ac, final int lv, final int str,
            final int dex, final int con, final int wis, final int cha,
            final int intel, final int time) {
        writeC(Opcodes.S_OPCODE_CHARLIST);
        writeS(name);
        writeS(clanName);
        writeC(type);
        writeC(sex);
        writeH(lawful);
        writeH(hp);
        writeH(mp);

        if (ac > 0x0a) {// 10
            writeC(0x0a);// 10

        } else {
            writeC(ac);
        }

        if (lv > 0x7f) {// 127
            writeC(0x7f);// 127

        } else {
            writeC(lv);
        }

        if (str > 0x7f) {// 127
            writeC(0x7f);// 127
        } else {
            writeC(str);
        }
        if (dex > 0x7f) {// 127
            writeC(0x7f);// 127
        } else {
            writeC(dex);
        }
        if (con > 0x7f) {// 127
            writeC(0x7f);// 127
        } else {
            writeC(con);
        }
        if (wis > 0x7f) {// 127
            writeC(0x7f);// 127
        } else {
            writeC(wis);
        }
        if (cha > 0x7f) {// 127
            writeC(0x7f);// 127
        } else {
            writeC(cha);
        }
        if (intel > 0x7f) {// 127
            writeC(0x7f);// 127
        } else {
            writeC(intel);
        }

        // 大于0为GM权限
        this.writeC(0x00);

        String times = Integer.toHexString(time);
        if (times.length() < 8) {
            times = "0" + times;
        }
        // cb a5 31 01 131a295
        writeC(Integer.decode("0x" + times.substring(6, 8)));
        writeC(Integer.decode("0x" + times.substring(4, 6)));
        writeC(Integer.decode("0x" + times.substring(2, 4)));
        writeC(Integer.decode("0x" + times.substring(0, 2)));

        // 解决发现外挂中断连线 by aplus
        int checkcode = lv ^ str ^ dex ^ con ^ wis ^ cha ^ intel;
        writeC(checkcode & 0xff);
    }

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return S_CHAR_PACKS;
	}
}
