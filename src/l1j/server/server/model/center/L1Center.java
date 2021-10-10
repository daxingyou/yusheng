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
package l1j.server.server.model.center;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javolution.util.FastTable;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1CenterItem;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.IntRange;

//import net.l1j.Config;
//import net.l1j.server.datatables.CastleTable;

public class L1Center {
	private final int _npcId;
	private final List<L1CenterItem> _sellingItems;
	private final List<L1CenterItem> _purchasingItems;
	private static final Random _random = new Random();

	public L1Center(int npcId, List<L1CenterItem> sellingItems,
			List<L1CenterItem> purchasingItems) {
		if (sellingItems == null || purchasingItems == null) {
			throw new NullPointerException();
		}

		_npcId = npcId;
		_sellingItems = sellingItems;
		_purchasingItems = purchasingItems;

	}

	public int getNpcId() {
		return _npcId;
	}

	public List<L1CenterItem> getSellingItems() {
		return _sellingItems;
	}

	/**
	 * この商店で、指定されたアイテムが買取可能な狀態であるかを返す。
	 * 
	 * @param item
	 * @return アイテムが買取可能であればtrue
	 */
	private boolean isPurchaseableItem(L1ItemInstance item) {
		if (item == null) {
			return false;
		}
		if (item.isEquipped()) { // 裝備中であれば不可
			return false;
		}
		if (item.getEnchantLevel() != 0) { // 強化(or弱化)されていれば不可
			return false;
		}
		/*
		 * if (item.getBless() >= 128) { // 封印された装備 return false; }
		 */

		return true;
	}

	private L1CenterItem getPurchasingItem(int itemId) {
		for (L1CenterItem shopItem : _purchasingItems) {
			if (shopItem.getItemId() == itemId) {
				return shopItem;
			}
		}
		return null;
	}

	public L1AssessedItem assessItem(L1ItemInstance item) {
		L1CenterItem shopItem = getPurchasingItem(item.getItemId());
		if (shopItem == null) {
			return null;
		}
		return new L1AssessedItem(item.getId(), getAssessedPrice(shopItem));
	}

	private int getAssessedPrice(L1CenterItem item) {
		return (int) (item.getPrice() /** Config.RATE_SHOP_PURCHASING_PRICE */
		/ item.getPackCount());
	}

	/**
	 * インベントリ內の買取可能アイテムを查定する。
	 * 
	 * @param inv
	 *            查定對象のインベントリ
	 * @return 查定された買取可能アイテムのリスト
	 */
	public List<L1AssessedItem> assessItems(L1PcInventory inv) {
		List<L1AssessedItem> result = new FastTable<L1AssessedItem>();
		for (L1CenterItem item : _purchasingItems) {
			for (L1ItemInstance targetItem : inv.findItemsId(item.getItemId())) {
				if (!isPurchaseableItem(targetItem)) {
					continue;
				}

				result.add(new L1AssessedItem(targetItem.getId(),
						getAssessedPrice(item)));
			}
		}
		return result;
	}

	/**
	 * プレイヤーへアイテムを販賣できることを保証する。
	 * 
	 * @return 何らかの理由でアイテムを販賣できない場合、false
	 */
	private boolean ensureSell(L1PcInstance pc, L1CenterBuyOrderList orderList) {
		int price = orderList.getTotalPrice();
		// オーバーフローチェック
		if (!IntRange.includes(price, 0, 100000000)) {
			// 總販賣價格は%dアデナを超過できません。
			pc.sendPackets(new S_ServerMessage(904, "100,000,000"));
			return false;
		}
		// 購入できるかチェック
		if (!pc.getInventory().checkItem(L1ItemId.TIANBAO, price)) {
			System.out.println(price);
			// \f1アデナが不足しています。
			pc.sendPackets(new S_SystemMessage("元宝 数量不足。"));
			return false;
		}
		// 重量チェック
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			// アイテムが重すぎて、これ以上持てません。
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		// 個數チェック
		int totalCount = pc.getInventory().getSize();
		for (L1CenterBuyOrder order : orderList.getList()) {
			L1Item temp = order.getItem().getItem();
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(temp.getItemId())) {
					totalCount += 1;
				}
			} else {
				totalCount += 1;
			}
		}
		if (totalCount > 180) {
			// \f1一人のキャラクターが持って步けるアイテムは最大180個までです。
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		return true;
	}

	/**
	 * 販賣取引
	 */
	private void sellItems(L1PcInventory inv, L1CenterBuyOrderList orderList) {
		/*
		 * if (!inv.consumeItem(L1ItemId.TIANBAO, orderList .getTotalPrice())) {
		 * throw new IllegalStateException("消费必须使用天宝哦。"); }
		 */
		final int priceTax = orderList.getTotalPrice();
		if (priceTax <= 0) {
			return;
		}
		if (!inv.consumeItem(L1ItemId.TIANBAO, priceTax)) {
			return;// 购买物品时金币不足
		}

		for (L1CenterBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			// System.out.println("获取物品ID成功");
			int amount = order.getCount();
			// System.out.println("获取物品数量成功");
			int level = order.getItem().getEnlvl();

			int time = order.getItem().get_time();
			// System.out.println("获取物品强化值成功");
			L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
			// System.out.println("步骤1成功");
			item.setCount(amount);
			// System.out.println("步骤2成功");
			item.setIdentified(true);
			// System.out.println("步骤3成功");
			item.setEnchantLevel(level);

			if (time > 0) {
				final Calendar cal = Calendar.getInstance();
				cal.add(Calendar.HOUR, time);
				item.set_time(new Timestamp(cal.getTimeInMillis()));
			}
			if (item.getItemId() == 21200) {
				item.setUpdateHP(_random.nextInt(100) + 1);
				item.set_updateMP(_random.nextInt(100) + 1);
				item.set_updateHPR(_random.nextInt(10) + 1);
				item.set_updateMPR(_random.nextInt(10) + 1);
				item.set_updateSP(_random.nextInt(3) + 1);
				if (_random.nextInt(100) > 50) {
					item.set_updateDMG(_random.nextInt(5) + 1);
					item.set_updateHOTDMG(_random.nextInt(5) + 1);
				} else {
					item.set_updateBOWDMG(_random.nextInt(5) + 1);
					item.set_updateHOTBOWDMG(_random.nextInt(5) + 1);
				}
				inv.getOwner().getInventory()
						.updateItem(item, L1PcInventory.COL_PVE);
				inv.getOwner().getInventory()
						.saveItem(item, L1PcInventory.COL_PVE);
				// ----------------------更新数据库------------------------
			}
			// System.out.println("步骤4成功");
			inv.storeItem(item);
			WriteLogTxt.Recording("NPC元宝商人购买记录", "玩家"
					+ inv.getOwner().getName() + "花费" + priceTax + "元宝购入物品"
					+ item.getName() + "(" + item.getCount() + ")" + "。");
			// System.out.println("生成物品成功");
			/*
			 * if (_npcId == 70068 || _npcId == 70020) {
			 * item.setIdentified(false); //Random random = new Random(); int
			 * chance = RandomArrayList.getInc(100, 1); if (chance <= 15) {
			 * item.setEnchantLevel(-2); } else if (chance <= 30) {
			 * item.setEnchantLevel(-1); } else if (chance <= 70) {
			 * item.setEnchantLevel(0); } else if (chance <= 87) {
			 * item.setEnchantLevel(RandomArrayList.getInc(2, 1)); } else if
			 * (chance <= 97) { item.setEnchantLevel(RandomArrayList.getInc(3,
			 * 3)); } else if (chance <= 99) { item.setEnchantLevel(6); } else
			 * if (chance == 100) { item.setEnchantLevel(7); } }
			 */
		}
	}

	/**
	 * プレイヤーに、L1CenterBuyOrderListに記載されたアイテムを販賣する。
	 * 
	 * @param pc
	 *            販賣するプレイヤー
	 * @param orderList
	 *            販賣すべきアイテムが記載されたL1CenterBuyOrderList
	 */
	public void sellItems(L1PcInstance pc, L1CenterBuyOrderList orderList) {
		if (!ensureSell(pc, orderList)) {
			return;
		}
		// System.out.println("orderlist2交易数据:" + pc.getName() +
		// orderList+" 数据接收");
		sellItems(pc.getInventory(), orderList);
		// System.out.println("orderlist2交易数据接收成功");
		// payTax(orderList);
	}

	/**
	 * L1CenterSellOrderListに記載されたアイテムを買い取る。
	 * 
	 * @param orderList
	 *            買い取るべきアイテムと價格が記載されたL1CenterSellOrderList
	 */
	public void buyItems(L1CenterSellOrderList orderList) {
		L1PcInventory inv = orderList.getPc().getInventory();
		int totalPrice = 0;
		for (L1CenterSellOrder order : orderList.getList()) {
			final L1ItemInstance item = inv.getItem(order.getItem()
					.getTargetId());
			long count = inv.removeItem(order.getItem().getTargetId(),
					order.getCount());
			final long oneitemprice = order.getItem().getAssessedPrice()
					* count;
			totalPrice += oneitemprice;
			WriteLogTxt.Recording("NPC元宝商人卖出记录", "玩家"
					+ inv.getOwner().getName() + "卖出物品" + item.getName() + "("
					+ item.getCount() + ")" + "获得元宝" + oneitemprice + "。");
		}

		totalPrice = IntRange.ensure(totalPrice, 0, 2000000000);
		if (0 < totalPrice) {
			inv.storeItem(L1ItemId.TIANBAO, totalPrice);
		}
	}

	public L1CenterBuyOrderList newBuyOrderList() {
		return new L1CenterBuyOrderList(this);
	}

	public L1CenterSellOrderList newSellOrderList(L1PcInstance pc) {
		return new L1CenterSellOrderList(this, pc);
	}
}
