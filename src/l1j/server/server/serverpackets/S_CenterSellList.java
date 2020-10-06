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

import l1j.server.server.Opcodes;
import l1j.server.server.datatables.CenterTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.center.L1Center;
import l1j.server.server.templates.L1CenterItem;
import l1j.server.server.templates.L1Item;
import l1j.server.server.world.L1World;

public class S_CenterSellList extends ServerBasePacket {


	/**
	 * 店の品物リストを表示する。キャラクターがBUYボタンを押した时に送る。
	 */
	public S_CenterSellList(int objId) {
		writeC(Opcodes.S_OPCODE_SHOWSHOPBUYLIST);
		writeD(objId);

		L1Object npcObj = L1World.getInstance().findObject(objId);
		if (!(npcObj instanceof L1NpcInstance)) {
			writeH(0);
			return;
		}
		int npcId = ((L1NpcInstance) npcObj).getNpcTemplate().get_npcId();

		//L1TaxCalculator calc = new L1TaxCalculator(npcId);
		L1Center shop = CenterTable.getInstance().get(npcId);
		List<L1CenterItem> shopItems = shop.getSellingItems();

		writeH(shopItems.size());

		// L1ItemInstanceのgetStatusBytesを利用するため
		L1ItemInstance dummy = new L1ItemInstance();

		for (int i = 0; i < shopItems.size(); i++) {
			L1CenterItem shopItem = shopItems.get(i);
			L1Item item = shopItem.getItem();
			int price = shopItem.getPrice();
			writeD(i);
			writeH(shopItem.getItem().getGfxId());
			writeD(price);
			final StringBuilder item_name = new StringBuilder();
			if (shopItem.getEnlvl() != 0){
				item_name.append(String.format("+%d ", shopItem.getEnlvl()));
			}
			item_name.append(item.getName());
			if (shopItem.getPackCount() > 1) {
				item_name.append(String.format("(%d)", shopItem.getPackCount()));
			}
			if (shopItem.get_time() > 0){
				item_name.append(String.format(" %d小时", shopItem.get_time()));
			}
			writeS(item_name.toString());
			L1Item template = ItemTable
					.getInstance().getTemplate(item.getItemId());
			if (template == null) {
				writeC(0);
			} else {
				dummy.setItem(template);
				byte[] status = dummy.getStatusBytes();
				writeC(status.length);
				for (byte b : status) {
					writeC(b);
				}
			}
		}
		writeH(2336); // 0x00:kaimo 0x01:pearl 0x07:adena
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}
}