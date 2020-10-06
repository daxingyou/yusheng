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

import l1j.server.Config;
import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket, S_OtherCharPacks

public class S_OtherCharPacks extends ServerBasePacket {

	private static final String S_OTHER_CHAR_PACKS = "[S] S_OtherCharPacks";


	private static final int STATUS_INVISIBLE = 2;
	private static final int STATUS_PC = 4;
/*	private static final int STATUS_BRAVE = 16;
	private static final int STATUS_ELFBRAVE = 32;*/
	private static final int STATUS_FASTMOVABLE = 64;

	
	public S_OtherCharPacks(L1PcInstance pc) {
		int status = STATUS_PC;
		// 删除int light = pc.isLightOn() ? 14 : 0;
		// 修正瞬移灯类效果不消失 
		int light = pc.getPcLight();
		if (light == 20) {
			light = 0;
		}
		// 修正瞬移灯类效果不消失  end

		if (pc.isInvisble()) {
			status |= STATUS_INVISIBLE;
			light = 0; // 状态场合OFF
		}
		if (pc.getBraveSpeed() != 0) { // 2段加速效果
            status |= pc.getBraveSpeed() * 16;
        }
		if (pc.isFastMovable()) {
			status |= STATUS_FASTMOVABLE;
		}

		// int addbyte = 0;
		// int addbyte1 = 1;

		writeC(Opcodes.S_OPCODE_CHARPACK);
		writeH(pc.getX());
		writeH(pc.getY());
		writeD(pc.getId());
		writeH(pc.isDead() ? pc.getTempCharGfxAtDead() : pc.getTempCharGfx());
		if (pc.isDead()) {
			writeC(pc.getStatus());
		} else {
			writeC(pc.getCurrentWeapon());
		}
		writeC(pc.getHeading());
		// writeC(0); // makes char invis (0x01), cannot move. spells display
		writeC(light); // status (0x01 = running)
		writeC(pc.getMoveSpeed());
		writeD(0x0000); // exp
		// writeC(0x00);
		writeH(pc.getLawful());
		if (pc.getMapId() == Config.HUODONGMAPID){
			writeS("余生蒙面人");
			writeS("");
		}else{
			writeS(pc.getName());
			writeS(pc.getTitle());
		}
		writeC(status);
		if (pc.getMapId() == Config.HUODONGMAPID){
			writeD(0);
			writeS(""); // 名
		}else{
			writeD(pc.getClanid());
			writeS(pc.getClanname()); // 名
		}
		writeS(null); // ？
		writeC(0); // ？
		/*
		 * if(pc.is_isInParty()) // 中 { writeC(100 * pc.get_currentHp() /
		 * pc.get_maxHp()); } else { writeC(0xFF); }
		 */
		writeC(0xFF);
		writeC(0); // 距离(通)
		writeC(0); // PC = 0, Mon = Lv
		writeC(0);
		writeC(0xFF);
		writeC(0xFF);
	}
	

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return S_OTHER_CHAR_PACKS;
	}

}