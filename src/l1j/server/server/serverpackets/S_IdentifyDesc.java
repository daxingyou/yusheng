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

public class S_IdentifyDesc extends ServerBasePacket {



	/**
	 * 确认使用时表示
	 */
	public S_IdentifyDesc(L1ItemInstance item) {
		buildPacket(item);
	}

	private void buildPacket(L1ItemInstance item) {
		writeC(Opcodes.S_OPCODE_IDENTIFYDESC);
		writeH(item.getItem().getItemDescId());

		StringBuilder name = new StringBuilder();

		if (item.getBless() == 0) {
			name.append("$227 "); // 祝福
		} else if (item.getBless() == 2) {
			name.append("$228 "); // 咒
		}

		name.append(item.getItem().getNameId());

		if (item.getItem().getType2() == 1) { // weapon
			writeH(134); // \f1%0：小打击%1 大打击%2
			writeC(3);
			writeS(name.toString());
			/*删除writeS(item.getItem().getDmgSmall()
					+ "+" + item.getEnchantLevel());
			writeS(item.getItem().getDmgLarge()
					+ "+" + item.getEnchantLevel());删除*/
			// 魔法武器显示修正 
			if (item.getEnchant() != 0 || item.getHolyEnchant() != 0) {
				writeS(item.getItem().getDmgSmall() 
					+ "+" + item.getEnchantLevel() 
					+ " (" + (item.getEnchant() + item.getHolyEnchant()) + ")");
				writeS(item.getItem().getDmgLarge() 
					+ "+" + item.getEnchantLevel() 
					+ " (" + (item.getEnchant() + item.getHolyEnchant()) + ")");
			} else {
				writeS(item.getItem().getDmgSmall() + "+" + item.getEnchantLevel());
				writeS(item.getItem().getDmgLarge() + "+" + item.getEnchantLevel());
			}
			// 魔法武器显示修正  end
		} else if (item.getItem().getType2() == 2) { // armor
			if (item.getItem().getItemId() == 20383) { // 骑马用
				writeH(137); // \f1%0：使用可能回%1Ａ重%2‵
				writeC(3);
				writeS(name.toString());
				writeS(String.valueOf(item.getChargeCount()));
			} else {
				writeH(135); // \f1%0：防御力%1 防御具
				writeC(2);
				writeS(name.toString());
				writeS(Math.abs(item.getItem().get_ac())
						+ "+" + item.getEnchantLevel());
				// 铠甲显示修正 
				if (item.getEnchant() != 0) {
					writeS(Math.abs(item.getItem().get_ac()) + "+" + item.getEnchantLevel()  + " (" + item.getEnchant() + ")");
				} else {
					writeS(Math.abs(item.getItem().get_ac()) + "+" + item.getEnchantLevel());
				}
				// 铠甲显示修正  end
			}

		} else if (item.getItem().getType2() == 0) { // etcitem
			if (item.getItem().getType() == 1) { // wand
				writeH(137); // \f1%0：使用可能回数%1〔重%2〕
				writeC(3);
				writeS(name.toString());
				writeS(String.valueOf(item.getChargeCount()));
			} 
			// 灯油 
			else if (item.getItem().getItemId() == 40003) {
				writeH(138);
				writeC(2);
				name.append(": $231 "); // 残燃料
				name.append(String.valueOf(item.getChargeCount()));
				writeS(name.toString());
			}
			// 灯油  end
			else if (item.getItem().getType() == 2) { // light
				writeH(138);
				writeC(2);
				name.append(": $231 "); // 残燃料
				// 删除name.append(0); // < 残燃料(未实装)
				// 显示木炭数 
				name.append(String.valueOf(item.getChargeCount()));
				// 显示木炭数  end
				writeS(name.toString());
			} else if (item.getItem().getType() == 7) { // food
				writeH(136); // \f1%0：满腹度%1〔重%2〕
				writeC(3);
				writeS(name.toString());
				writeS(String.valueOf(item.getItem().getFoodVolume()));
			} else {
				writeH(138); // \f1%0：〔重%1〕
				writeC(2);
				writeS(name.toString());
			}
			writeS(String.valueOf(item.getWeight()));
		}
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}
