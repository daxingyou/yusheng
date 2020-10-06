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

import java.util.List;

import l1j.server.Config;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1TaxCalculator;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1ShopItem;
import l1j.server.server.world.L1World;

public class S_ShopSellList extends ServerBasePacket {


	/**
	 * 店品物表示。BUY押时送。
	 */
	public S_ShopSellList(int objId) {
		writeC(Opcodes.S_OPCODE_SHOWSHOPBUYLIST);
		writeD(objId);

		L1Object npcObj = L1World.getInstance().findObject(objId);
		if (!(npcObj instanceof L1NpcInstance)) {
			writeH(0);
			return;
		}
		int npcId = ((L1NpcInstance) npcObj).getNpcTemplate().get_npcId();

		L1TaxCalculator calc = new L1TaxCalculator(npcId);
		L1Shop shop = ShopTable.getInstance().get(npcId);
		if (shop == null) {
			writeH(0);
			return;
		}
		List<L1ShopItem> shopItems = shop.getSellingItems();

		writeH(shopItems.size());
		
		// L1ItemInstancegetStatusBytes利用
		L1ItemInstance dummy = new L1ItemInstance();

		for (int i = 0; i < shopItems.size(); i++) {
			L1ShopItem shopItem = shopItems.get(i);
			L1Item item = shopItem.getItem();
			long price = calc.layTax((int)(shopItem.getPrice() * Config.RATE_SHOP_SELLING_PRICE));
			if (npcId == 8889 || npcId == 8890){
				price = shopItem.getPrice();
			}
			writeD(i);
			writeH(shopItem.getItem().getGfxId());
			writeD(price);
			final StringBuilder item_name = new StringBuilder();
			if (shopItem.getEnchantLevel() != 0){
				item_name.append(String.format("+%d ", shopItem.getEnchantLevel()));
			}
			item_name.append(item.getName());
			if (shopItem.getPackCount() > 1) {
				item_name.append(String.format("(%d)", shopItem.getPackCount()));
			}
			if (shopItem.getTime() > 0){
				item_name.append(String.format(" %d小时", shopItem.getTime()));
			}
			writeS(item_name.toString());
			L1Item template = ItemTable
					.getInstance().getTemplate(item.getItemId());
			if (template == null) {
				writeC(0);
			} else {
				dummy.setItem(template);
				dummy.setEnchantLevel(shopItem.getEnchantLevel());
				byte[] status = dummy.getStatusBytes();
				writeC(status.length);
				for (byte b : status) {
					writeC(b);
				}
			}
		}
		this.writeH(0x0007);
	}

	@Override
	public byte[] getContent(){
		return getBytes();
	}
}
