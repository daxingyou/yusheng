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
import l1j.server.server.model.Instance.L1ItemInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket

public class S_DropItem extends ServerBasePacket {

	private static final String _S__OB_DropItem = "[S] S_DropItem";


	public S_DropItem(L1ItemInstance item) {
		buildPacket(item);
	}

	private void buildPacket(L1ItemInstance item) {
		// int addbyte = 0;
		// int addbyte1 = 1;
		// int addbyte2 = 13;
		// int setting = 4;
		writeC(Opcodes.S_OPCODE_CHARPACK);
		writeH(item.getX());
		writeH(item.getY());
		writeD(item.getId());
		writeH(item.getItem().getGroundGfxId());
		writeC(0);
		writeC(0);
		//删除writeC(0);
		//灯 
		if ((item.getItem().getItemId() == 40001 || 
			item.getItem().getItemId() == 40002 || 
			item.getItem().getItemId() == 40005) && item.getEnchantLevel() != 0) {//灯类打开状态
			writeC(item.getItem().getLightRange());//显示亮光
		} else {
			writeC(0);
		}
		//灯  end
		writeC(0);
		writeD(item.getCount());
		writeC(0);
		writeC(0);
		if (item.getCount() > 1) {
			writeS(item.getItem().getName() + " (" + item.getCount() + ")");
		} else {
			// 道具地上显示 
			int isId = item.isIdentified() ? 1 : 0;
			if (item.getItem().getItemId() == 20383 && isId != 0) // 军马头盔
				writeS(item.getItem().getName() + " [" + item.getChargeCount() + "]");
			else if ((item.getItem().getItemId() == 40006 || 
			 item.getItem().getItemId() == 40007 || 
			 item.getItem().getItemId() == 40008 || 
			 item.getItem().getItemId() == 40009 || 
			 item.getItem().getItemId() == 140006 || 
			 item.getItem().getItemId() == 140008) && isId != 0) // 魔杖类
				writeS(item.getItem().getName() + " (" + item.getChargeCount() + ")");
			else if ((item.getItem().getItemId() == 40001 || 
			 item.getItem().getItemId() == 40002 || 
			 item.getItem().getItemId() == 40004 || 
			 item.getItem().getItemId() == 40005) && item.getEnchantLevel() != 0) // 灯类
				writeS(item.getItem().getName() + " ($10)");
			else if ((item.getItem().getItemId() == 40001 || 
			 item.getItem().getItemId() == 40002) && item.getChargeCount() <= 0) // 灯类
				writeS(item.getItem().getName() + " ($11)");
			else
			// 道具地上显示  end
			writeS(item.getItem().getName());
		}
		writeC(0);
		writeD(0);
		writeD(0);
		writeC(255);
		writeC(0);
		writeC(0);
		writeC(0);
		writeH(65535);
		// writeD(0x401799a);
		writeD(0);
		writeC(8);
		writeC(0);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return _S__OB_DropItem;
	}

}
