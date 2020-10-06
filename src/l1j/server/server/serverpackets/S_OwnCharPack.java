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

public class S_OwnCharPack extends ServerBasePacket {

	private static final String S_OWN_CHAR_PACK = "[S] S_OwnCharPack";

	private static final int STATUS_INVISIBLE = 2;
    private static final int STATUS_PC = 4;
    // private static final int STATUS_FREEZE = 8;
    private static final int STATUS_BRAVE = 16;
    private static final int STATUS_ELFBRAVE = 32;
    private static final int STATUS_FASTMOVABLE = 64;
    private static final int STATUS_GHOST = 128;


	public S_OwnCharPack(L1PcInstance pc) {
		buildPacket(pc);
	}
	
	private void buildPacket(L1PcInstance pc) {
		int status = STATUS_PC;

        if (pc.isInvisble() || pc.isGmInvis()) {
            status |= STATUS_INVISIBLE;
        }
        if (pc.isBrave()) {
            status |= STATUS_BRAVE;
        }
        if (pc.isElfBrave()) {
            // エルヴンワッフルの场合は、STATUS_BRAVEとSTATUS_ELFBRAVEを立てる。
            // STATUS_ELFBRAVEのみでは效果が无い？
            status |= STATUS_BRAVE;
            status |= STATUS_ELFBRAVE;
        }
        if (pc.isFastMovable()) {
            status |= STATUS_FASTMOVABLE;
        }
        if (pc.isGhost()) {
            status |= STATUS_GHOST;
        }

        this.writeC(Opcodes.S_OPCODE_CHARPACK);
        this.writeH(pc.getX());
        this.writeH(pc.getY());
        this.writeD(pc.getId());
        if (pc.isDead()) {
            this.writeH(pc.getTempCharGfxAtDead());

        } else {
            this.writeH(pc.getTempCharGfx());
        }
        if (pc.isDead()) {
            this.writeC(pc.getStatus());
        } else {
            this.writeC(pc.getCurrentWeapon());
        }
        this.writeC(pc.getHeading());
        // writeC(addbyte);
        this.writeC(pc.getPcLight());
        this.writeC(pc.getMoveSpeed());
        this.writeD((int) pc.getExp());
        this.writeH(pc.getLawful());

		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(pc.getName());
		//stringBuilder.append(pc.getViewName());

        this.writeS(stringBuilder.toString());
        this.writeS(pc.getTitle());
        this.writeC(status); // 状态
        this.writeD(pc.getClanid());
        this.writeS(pc.getClanname()); // 血盟名称
        this.writeS(null); // 主人名称
        /** 血盟推荐 by:42621391 2014年8月20日13:22:38 */
        if (pc.getClanid() == 0)
            writeC(176);
          else {
            switch (pc.getClanRank()) {
            case 3:
              writeC(48);
              break;
            case 2:
            case 7:
              writeC(20);
              break;
            case 5:
            case 8:
              writeC(80);
              break;
            case 6:
            case 9:
              writeC(96);
              break;
            case 4:
            case 10:
              writeC(160);
            }
          }
        //结束
        if (pc.isInParty()) {// 队伍中
            this.writeC(100 * pc.getCurrentHp() / pc.getMaxHp()); // HP显示
        } else {
            this.writeC(0xff); // HP显示
        }
        this.writeC(0x00); // タルクック距离(通り)
        this.writeC(0x00); // LV
        this.writeC(0x00);
        this.writeC(0xff);
        this.writeC(0xff);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return S_OWN_CHAR_PACK;
	}

}