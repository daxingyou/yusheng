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

import java.text.SimpleDateFormat;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_NewCharPacket extends ServerBasePacket {
	private static final String _S__25_NEWCHARPACK = "[S] New Char Packet";

	public S_NewCharPacket(L1PcInstance pc) {
		buildPacket(pc);
	}

	private void buildPacket(L1PcInstance pc) {
		this.writeC(Opcodes.S_OPCODE_NEWCHARPACK);
        this.writeS(pc.getName());
        this.writeS("");
        this.writeC(pc.getType());
        this.writeC(pc.get_sex());
        this.writeH(pc.getLawful());
        this.writeH(pc.getMaxHp());
        this.writeH(pc.getMaxMp());
        this.writeC(pc.getAc());
        this.writeC(pc.getLevel());
        this.writeC(pc.getStr());
        this.writeC(pc.getDex());
        this.writeC(pc.getCon());
        this.writeC(pc.getWis());
        this.writeC(pc.getCha());
        this.writeC(pc.getInt());

        // 大于0为GM权限
        this.writeC(0x00);

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int time = Integer.parseInt(sdf.format(System.currentTimeMillis())
                .replace("-", ""));

        String times = Integer.toHexString(time);
        if (times.length() < 8) {
            times = "0" + times;
        }
        // cb a5 31 01 131a295
        this.writeC(Integer.decode("0x" + times.substring(6, 8)));
        this.writeC(Integer.decode("0x" + times.substring(4, 6)));
        this.writeC(Integer.decode("0x" + times.substring(2, 4)));
        this.writeC(Integer.decode("0x" + times.substring(0, 2)));

        // 解决发现外挂中断连线 by aplus
        int checkcode = pc.getLevel() ^ pc.getStr() ^ pc.getDex() ^ pc.getCon()
                ^ pc.getWis() ^ pc.getCha() ^ pc.getInt();
        this.writeC(checkcode & 0xFF);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return _S__25_NEWCHARPACK;
	}

}
