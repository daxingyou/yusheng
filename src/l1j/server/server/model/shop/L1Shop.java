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
package l1j.server.server.model.shop;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Random;//宠物随机血量 
import java.util.ArrayList;
import java.util.List;

import l1j.server.Config;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.PetTable;//宠物 
import l1j.server.server.datatables.TownTable;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1TaxCalculator;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_ItemName;//宠物 
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Castle;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Pet;//宠物 
import l1j.server.server.templates.L1ShopItem;
import l1j.server.server.utils.IntRange;
import l1j.server.server.world.L1World;

public class L1Shop {
	private final int _npcId;
	private final List<L1ShopItem> _sellingItems;
	private final List<L1ShopItem> _purchasingItems;

	public L1Shop(int npcId, List<L1ShopItem> sellingItems,
			List<L1ShopItem> purchasingItems) {
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

	public List<L1ShopItem> getSellingItems() {
		return _sellingItems;
	}

	/**
	 * 商店、指定买取可能状态返。
	 * 
	 * @param item
	 * @return 买取可能true
	 */
	private boolean isPurchaseableItem(L1ItemInstance item) {
		if (item == null) {
			return false;
		}
		if (item.isEquipped()) { // 装备中不可
			return false;
		}
		if (item.getEnchantLevel() != 0) { // 强化(or弱化)不可
			return false;
		}

		return true;
	}

	private L1ShopItem getPurchasingItem(int itemId) {
		for (L1ShopItem shopItem : _purchasingItems) {
			if (shopItem.getItemId() == itemId) {
				return shopItem;
			}
		}
		return null;
	}

	public L1AssessedItem assessItem(L1ItemInstance item) {
		L1ShopItem shopItem = getPurchasingItem(item.getItemId());
		if (shopItem == null) {
			return null;
		}
		return new L1AssessedItem(item.getId(), getAssessedPrice(shopItem));
	}

	private int getAssessedPrice(L1ShopItem item) {
		return (int) (item.getPrice() * Config.RATE_SHOP_PURCHASING_PRICE / item
				.getPackCount());
	}

	/**
	 * 内买取可能查定。
	 * 
	 * @param inv
	 *            查定对像
	 * @return 查定买取可能
	 */
	public List<L1AssessedItem> assessItems(L1PcInventory inv) {
		List<L1AssessedItem> result = new ArrayList<L1AssessedItem>();
		for (L1ShopItem item : _purchasingItems) {
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
	 * 贩卖保证。
	 * 
	 * @return 何理由贩卖场合、false
	 */
	private boolean ensureSell(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		long price = orderList.getTotalPriceTaxIncluded();
		if (this._npcId == 8889 || this._npcId == 8892){
			price = orderList.getTotalPrice();
		}
		//
		if (price > 2000000000) {
			pc.sendPackets(new S_ServerMessage(904, "2000000000"));
			return false;
		}
/*		if (!IntRange.includes(price, 0, 2000000000)) {
			// 总贩卖价格%d超过。
			pc.sendPackets(new S_ServerMessage(904, "2000000000"));
			return false;
		}*/
		int oederItemId = L1ItemId.ADENA;
		if (this._npcId == 8889){
			oederItemId = 10018;
		}else if (this._npcId == 8892){
			oederItemId = 10019;
		}
		// 购入
		if (!pc.getInventory().checkItem(oederItemId, price)) {
			// \f1不足。
			//pc.sendPackets(new S_ServerMessage(189));
			final L1Item oederItem = ItemTable.getInstance().getTemplate(oederItemId);
			pc.sendPackets(new S_SystemMessage(oederItem.getName() + "不足!"));
			return false;
		}
		// 重量
		int currentWeight = pc.getInventory().getWeight() * 1000;
		if (currentWeight + orderList.getTotalWeight() > pc.getMaxWeight() * 1000) {
			// 重、以上持。
			pc.sendPackets(new S_ServerMessage(82));
			return false;
		}
		// 个数
		int totalCount = pc.getInventory().getSize();
		for (L1ShopBuyOrder order : orderList.getList()) {
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
			// \f1一人持步最大180个。
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		//购买数量限制 by
		int Count = 0;//购买数量
		int new_price = 0;//购买金额
		int set_count = 0;//可购买数量
		for (L1ShopBuyOrder order : orderList.getList()) {
			L1Item temp = order.getItem().getItem();
				Count += 1;
				new_price += orderList.getTotalPrice();
		}
		set_count = 2000000000 / new_price;
		if(Count > set_count) {
			pc.sendPackets(new S_ServerMessage(936));
			return false;
		}
		//购买数量限制 by end
		return true;
	}

	/**
	 * 地域税纳税处理 城要塞除城城国税10%纳税
	 * 
	 * @param orderList
	 */
	private void payCastleTax(L1ShopBuyOrderList orderList) {
		L1TaxCalculator calc = orderList.getTaxCalculator();

		long price = orderList.getTotalPrice();

		int castleId = L1CastleLocation.getCastleIdByNpcid(_npcId);
		long castleTax = calc.calcCastleTaxPrice(price);
		long nationalTax = calc.calcNationalTaxPrice(price);
		// 城城场合国税
		
		if (castleId != 0 && castleTax > 0) {
			L1Castle castle = CastleTable.getInstance().getCastleTable(castleId);
			if (castle != null){
				synchronized (castle) {
					long money = castle.getPublicMoney();
					if (2000000000 > money) {
						money = money + castleTax + nationalTax;
						castle.setPublicMoney(money);
						CastleTable.getInstance().updateCastle(castle);
					}
				}
			}
		}
		/*
		if (castleId == L1CastleLocation.GIRAN_CASTLE_ID) {
			castleTax += nationalTax;
			nationalTax = 0;
		}

		/*
		if (castleId != 0 && castleTax > 0) {
			L1Castle castle = CastleTable.getInstance().getCastleTable(castleId);

			synchronized (castle) {
				long money = castle.getPublicMoney();
				if (2000000000 > money) {
					money = money + castleTax;
					castle.setPublicMoney(money);
					CastleTable.getInstance().updateCastle(castle);
				}
			}

			if (nationalTax > 0) {
				L1Castle aden = CastleTable.getInstance().getCastleTable(
						L1CastleLocation.GIRAN_CASTLE_ID);
				synchronized (aden) {
					long money = aden.getPublicMoney();
					if (2000000000 > money) {
						money = money + nationalTax;
						aden.setPublicMoney(money);
						CastleTable.getInstance().updateCastle(aden);
					}
				}
			}
		}*/
	}

	/**
	 * 税纳税处理 战争税10%要塞公金。
	 * 
	 * @param orderList
	 */
	private void payDiadTax(L1ShopBuyOrderList orderList) {
		L1TaxCalculator calc = orderList.getTaxCalculator();

		long price = orderList.getTotalPrice();

		// 税
		long diadTax = calc.calcDiadTaxPrice(price);
		if (diadTax <= 0) {
			return;
		}
		int castleId = L1CastleLocation.getCastleIdByNpcid(_npcId);
		//L1Castle castle = CastleTable.getInstance().getCastleTable(L1CastleLocation.GIRAN_CASTLE_ID);
		L1Castle castle = CastleTable.getInstance().getCastleTable(castleId);
		if (castle != null){
			synchronized (castle) {
				long money = castle.getPublicMoney();
				if (2000000000 > money) {
					money = money + diadTax;
					castle.setPublicMoney(money);
					CastleTable.getInstance().updateCastle(castle);
				}
			}
		}
	}

	/**
	 * 町税纳税处理
	 * 
	 * @param orderList
	 */
	private void payTownTax(L1ShopBuyOrderList orderList) {
		long price = orderList.getTotalPrice();

		// 町卖上
		if (!L1World.getInstance().isProcessingContributionTotal()) {
			int town_id = L1TownLocation.getTownIdByNpcid(_npcId);
			if (town_id >= 1 && town_id <= 10) {
				TownTable.getInstance().addSalesMoney(town_id, price);
			}
		}
	}

	// XXX 纳税处理责务无气、
	private void payTax(L1ShopBuyOrderList orderList) {
		payCastleTax(orderList);
		payTownTax(orderList);
		payDiadTax(orderList);
	}

	/**
	 * 贩卖取引
	 */
	//删除private void sellItems(L1PcInventory inv, L1ShopBuyOrderList orderList) {
	private void sellItems(L1PcInstance pc, L1PcInventory inv, L1ShopBuyOrderList orderList) {//增加L1PcInstance pc, 
		/*if (!inv.consumeItem(L1ItemId.ADENA, orderList
				.getTotalPriceTaxIncluded())) {
			throw new IllegalStateException("不能消费对购买必要的金币。");
		}*/
		int oederItemId = L1ItemId.ADENA;
		if (this._npcId == 8889){
			oederItemId = 10018;
		}else if (this._npcId == 8892){
			oederItemId = 10019;
		}
		String logback,lognow;
		logback = lognow = "";
		L1ItemInstance[] ijbback = pc.getInventory().findItemsId(oederItemId);
		if (ijbback.length > 0) {
			for (int i = 0; i < ijbback.length; i++) {
				L1ItemInstance l1ItemInstance = ijbback[i];
				if (l1ItemInstance!=null) {
					logback += "持有" + l1ItemInstance.getItem().getName() + "("+l1ItemInstance.getCount()+")"+"#"+i;
				}
			}
		}
		long priceTax = orderList.getTotalPriceTaxIncluded();
		if (this._npcId == 8889 || this._npcId == 8892){
			priceTax = orderList.getTotalPriceTaxIncluded();
		}
		if (priceTax <= 0) {
			return;
		}
		if (!inv.consumeItem(oederItemId, priceTax)) {
			return;// 购买物品时金币不足
		}
		L1ItemInstance[] ijbnow = pc.getInventory().findItemsId(oederItemId);
		if (ijbnow.length > 0) {
			for (int i = 0; i < ijbnow.length; i++) {
				L1ItemInstance l1ItemInstance = ijbnow[i];
				if (l1ItemInstance!=null) {
					lognow += "持有" + l1ItemInstance.getItem().getName() + "("+l1ItemInstance.getCount()+")"+"#"+i;
				}
			}
		}
		for (L1ShopBuyOrder order : orderList.getList()) {
			int itemId = order.getItem().getItemId();
			int amount = order.getCount();
			int enchantlevel = order.getItem().getEnchantLevel();
			int time = order.getItem().getTime();
			L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
			if (enchantlevel != 0){
				item.setEnchantLevel(enchantlevel);
			}
			if (time > 0){
				final Calendar cal = Calendar.getInstance();
				cal.add(Calendar.HOUR, time);
				item.set_time(new Timestamp(cal.getTimeInMillis()));
			}
			// 删除 item.setCount(amount);
			// 删除 item.setIdentified(true);
			// 删除 inv.storeItem(item);

			// 饵 & 溶解剂 数量显示小修正 
			switch (item.getItemId())
			{
				case l1j.william.New_Id.Item_AJ_4: {//Lv.10 牧羊犬 
				Random random = new Random();
				short randomhp = 0;
				short randommp = 0;
				randomhp = (short) (25 + random.nextInt(16));//Lv.10 约增加 25~40 滴
				randommp = (short) (5 + random.nextInt(6));//Lv.10 约增加 5~10 滴
				L1Pet pet = new L1Pet();
				pet.set_npcid(45034);
				pet.set_name("牧羊犬");
				pet.set_level(10);
				pet.set_hp(30 + randomhp);
				pet.set_mp(5 + randommp);
				pet.set_exp(10000);//Lv.10 exp

				L1ItemInstance petamu = inv.storeItem(40314, 1); // 
				if (petamu != null) {
					PetTable.getInstance().storeNewPet(pet, petamu.getId() + 1, petamu.getId()); // DB书迂
					// 名表示更新
					pc.sendPackets(new S_ItemName(petamu));
				}
				}
				break;
				case l1j.william.New_Id.Item_AJ_5: {//Lv.10 猫 
				Random random = new Random();
				short randomhp = 0;
				short randommp = 0;
				randomhp = (short) (15 + random.nextInt(16));//Lv.10 约增加 15~30 滴
				randommp = (short) (15 + random.nextInt(11));//Lv.10 约增加 15~25 滴
				L1Pet pet = new L1Pet();
				pet.set_npcid(45039);
				pet.set_name("猫");
				pet.set_level(10);
				pet.set_hp(20 + randomhp);
				pet.set_mp(30 + randommp);
				pet.set_exp(10000);//Lv.10 exp

				L1ItemInstance petamu = inv.storeItem(40314, 1); // 
				if (petamu != null) {
					PetTable.getInstance().storeNewPet(pet, petamu.getId() + 1, petamu.getId()); // DB书迂
					// 名表示更新
					pc.sendPackets(new S_ItemName(petamu));
				}
				}
				break;
				case l1j.william.New_Id.Item_AJ_6: {//Lv.10 熊 
				Random random = new Random();
				short randomhp = 0;
				short randommp = 0;
				randomhp = (short) (40 + random.nextInt(31));//Lv.10 约增加 40~70 滴
				randommp = (short) (5 + random.nextInt(6));//Lv.10 约增加 5~10 滴
				L1Pet pet = new L1Pet();
				pet.set_npcid(45040);
				pet.set_name("熊");
				pet.set_level(10);
				pet.set_hp(50 + randomhp);
				pet.set_mp(0 + randommp);
				pet.set_exp(10000);//Lv.10 exp

				L1ItemInstance petamu = inv.storeItem(40314, 1); // 
				if (petamu != null) {
					PetTable.getInstance().storeNewPet(pet, petamu.getId() + 1, petamu.getId()); // DB书迂
					// 名表示更新
					pc.sendPackets(new S_ItemName(petamu));
				}
				}
				break;
				case l1j.william.New_Id.Item_AJ_7: {//Lv.10 杜宾狗 
				Random random = new Random();
				short randomhp = 0;
				short randommp = 0;
				randomhp = (short) (15 + random.nextInt(16));//Lv.10 约增加 15~30 滴
				randommp = (short) (5 + random.nextInt(6));//Lv.10 约增加 5~10 滴
				L1Pet pet = new L1Pet();
				pet.set_npcid(45042);
				pet.set_name("杜宾狗");
				pet.set_level(10);
				pet.set_hp(20 + randomhp);
				pet.set_mp(5 + randommp);
				pet.set_exp(10000);//Lv.10 exp

				L1ItemInstance petamu = inv.storeItem(40314, 1); // 
				if (petamu != null) {
					PetTable.getInstance().storeNewPet(pet, petamu.getId() + 1, petamu.getId()); // DB书迂
					// 名表示更新
					pc.sendPackets(new S_ItemName(petamu));
				}
				}
				break;
				case l1j.william.New_Id.Item_AJ_8: {//Lv.10 狼 
				Random random = new Random();
				short randomhp = 0;
				short randommp = 0;
				randomhp = (short) (15 + random.nextInt(31));//Lv.10 约增加 15~45 滴
				randommp = (short) (5 + random.nextInt(6));//Lv.10 约增加 5~10 滴
				L1Pet pet = new L1Pet();
				pet.set_npcid(45043);
				pet.set_name("狼");
				pet.set_level(10);
				pet.set_hp(30 + randomhp);
				pet.set_mp(5 + randommp);
				pet.set_exp(10000);//Lv.10 exp

				L1ItemInstance petamu = inv.storeItem(40314, 1); // 
				if (petamu != null) {
					PetTable.getInstance().storeNewPet(pet, petamu.getId() + 1, petamu.getId()); // DB书迂
					// 名表示更新
					pc.sendPackets(new S_ItemName(petamu));
				}
				}
				break;
				case l1j.william.New_Id.Item_AJ_9: {//Lv.10 浣熊 
				Random random = new Random();
				short randomhp = 0;
				short randommp = 0;
				randomhp = (short) (15 + random.nextInt(31));//Lv.10 约增加 15~45 滴
				randommp = (short) (10 + random.nextInt(11));//Lv.10 约增加 10~20 滴
				L1Pet pet = new L1Pet();
				pet.set_npcid(45044);
				pet.set_name("浣熊");
				pet.set_level(10);
				pet.set_hp(30 + randomhp);
				pet.set_mp(20 + randommp);
				pet.set_exp(10000);//Lv.10 exp

				L1ItemInstance petamu = inv.storeItem(40314, 1); // 
				if (petamu != null) {
					PetTable.getInstance().storeNewPet(pet, petamu.getId() + 1, petamu.getId()); // DB书迂
					// 名表示更新
					pc.sendPackets(new S_ItemName(petamu));
				}
				}
				break;
				case l1j.william.New_Id.Item_AJ_10: {//Lv.10 小猎犬 
				Random random = new Random();
				short randomhp = 0;
				short randommp = 0;
				randomhp = (short) (20 + random.nextInt(21));//Lv.10 约增加 20~40 滴
				randommp = (short) (10 + random.nextInt(11));//Lv.10 约增加 10~20 滴
				L1Pet pet = new L1Pet();
				pet.set_npcid(45046);
				pet.set_name("小猎犬");
				pet.set_level(10);
				pet.set_hp(20 + randomhp);
				pet.set_mp(30 + randommp);
				pet.set_exp(10000);//Lv.10 exp

				L1ItemInstance petamu = inv.storeItem(40314, 1); // 
				if (petamu != null) {
					PetTable.getInstance().storeNewPet(pet, petamu.getId() + 1, petamu.getId()); // DB书迂
					// 名表示更新
					pc.sendPackets(new S_ItemName(petamu));
				}
				}
				break;
				case l1j.william.New_Id.Item_AJ_11: {//Lv.10 圣伯纳犬 
				Random random = new Random();
				short randomhp = 0;
				short randommp = 0;
				randomhp = (short) (30 + random.nextInt(21));//Lv.10 约增加 30~50 滴
				randommp = (short) (10 + random.nextInt(11));//Lv.10 约增加 10~20 滴
				L1Pet pet = new L1Pet();
				pet.set_npcid(45047);
				pet.set_name("圣伯纳犬");
				pet.set_level(10);
				pet.set_hp(30 + randomhp);
				pet.set_mp(30 + randommp);
				pet.set_exp(10000);//Lv.10 exp

				L1ItemInstance petamu = inv.storeItem(40314, 1); // 
				if (petamu != null) {
					PetTable.getInstance().storeNewPet(pet, petamu.getId() + 1, petamu.getId()); // DB书迂
					// 名表示更新
					pc.sendPackets(new S_ItemName(petamu));
				}
				}
				break;
				case l1j.william.New_Id.Item_AJ_12: {//Lv.10 狐狸 
				Random random = new Random();
				short randomhp = 0;
				short randommp = 0;
				randomhp = (short) (15 + random.nextInt(16));//Lv.10 约增加 15~30 滴
				randommp = (short) (10 + random.nextInt(6));//Lv.10 约增加 10~15 滴
				L1Pet pet = new L1Pet();
				pet.set_npcid(45048);
				pet.set_name("狐狸");
				pet.set_level(10);
				pet.set_hp(15 + randomhp);
				pet.set_mp(30 + randommp);
				pet.set_exp(10000);//Lv.10 exp

				L1ItemInstance petamu = inv.storeItem(40314, 1); // 
				if (petamu != null) {
					PetTable.getInstance().storeNewPet(pet, petamu.getId() + 1, petamu.getId()); // DB书迂
					// 名表示更新
					pc.sendPackets(new S_ItemName(petamu));
				}
				}
				break;
				case l1j.william.New_Id.Item_AJ_13: {//Lv.10 暴走兔 
				Random random = new Random();
				short randomhp = 0;
				short randommp = 0;
				randomhp = (short) (15 + random.nextInt(26));//Lv.10 约增加 15~40 滴
				randommp = (short) (15 + random.nextInt(11));//Lv.10 约增加 15~25 滴
				L1Pet pet = new L1Pet();
				pet.set_npcid(45049);
				pet.set_name("暴走兔");
				pet.set_level(10);
				pet.set_hp(20 + randomhp);
				pet.set_mp(30 + randommp);
				pet.set_exp(10000);//Lv.10 exp

				L1ItemInstance petamu = inv.storeItem(40314, 1); // 
				if (petamu != null) {
					PetTable.getInstance().storeNewPet(pet, petamu.getId() + 1, petamu.getId()); // DB书迂
					// 名表示更新
					pc.sendPackets(new S_ItemName(petamu));
				}
				}
				break;
				case l1j.william.New_Id.Item_AJ_14: {//Lv.10 哈士奇 
				Random random = new Random();
				short randomhp = 0;
				short randommp = 0;
				randomhp = (short) (40 + random.nextInt(26));//Lv.10 约增加 40~65 滴
				randommp = (short) (5 + random.nextInt(6));//Lv.10 约增加 5~10 滴
				L1Pet pet = new L1Pet();
				pet.set_npcid(45053);
				pet.set_name("哈士奇");
				pet.set_level(10);
				pet.set_hp(50 + randomhp);
				pet.set_mp(5 + randommp);
				pet.set_exp(10000);//Lv.10 exp

				L1ItemInstance petamu = inv.storeItem(40314, 1); // 
				if (petamu != null) {
					PetTable.getInstance().storeNewPet(pet, petamu.getId() + 1, petamu.getId()); // DB书迂
					// 名表示更新
					pc.sendPackets(new S_ItemName(petamu));
				}
				}
				break;
				case l1j.william.New_Id.Item_AJ_15: {//Lv.10 柯利 
				Random random = new Random();
				short randomhp = 0;
				short randommp = 0;
				randomhp = (short) (40 + random.nextInt(16));//Lv.10 约增加 40~55 滴
				randommp = (short) (15 + random.nextInt(6));//Lv.10 约增加 15~20 滴
				L1Pet pet = new L1Pet();
				pet.set_npcid(45054);
				pet.set_name("柯利");
				pet.set_level(10);
				pet.set_hp(40 + randomhp);
				pet.set_mp(5 + randommp);
				pet.set_exp(10000);//Lv.10 exp

				L1ItemInstance petamu = inv.storeItem(40314, 1); // 
				if (petamu != null) {
					PetTable.getInstance().storeNewPet(pet, petamu.getId() + 1, petamu.getId()); // DB书迂
					// 名表示更新
					pc.sendPackets(new S_ItemName(petamu));
				}
				}
				break;
				default: {
					item.setCount(amount);
					item.setIdentified(true);
					inv.storeItem(item);
				}
				break;				
			}
			WriteLogTxt.Recording("NPC商人购买记录", "玩家交易前身上"+logback+" 角色名称"+ inv.getOwner().getName()
					+ "花费" + priceTax + "金币购入物品" + item.getName() +"("+ item.getCount()+")"+"。还剩"+lognow);
		//饵 & 溶解剂 数量显示小修正 
		}
	}

	/**
	 * 、L1ShopBuyOrderList记载贩卖。
	 * 
	 * @param pc
	 *            贩卖
	 * @param orderList
	 *            贩卖记载L1ShopBuyOrderList
	 */
	public void sellItems(L1PcInstance pc, L1ShopBuyOrderList orderList) {
		if (!ensureSell(pc, orderList)) {
			return;
		}

		//删除sellItems(pc.getInventory(), orderList);
		//变更
		sellItems(pc, pc.getInventory(), orderList);
		//变更 end
		payTax(orderList);
	}

	/**
	 * L1ShopSellOrderList记载买取。
	 * 
	 * @param orderList
	 *            买取价格记载L1ShopSellOrderList
	 */
	public void buyItems(L1ShopSellOrderList orderList) {
		L1PcInventory inv = orderList.getPc().getInventory();
		String logback,lognow;
		logback = lognow = "";
		L1ItemInstance[] ijbback = inv.findItemsId(L1ItemId.ADENA);
		if (ijbback.length > 0) {
			for (int i = 0; i < ijbback.length; i++) {
				L1ItemInstance l1ItemInstance = ijbback[i];
				if (l1ItemInstance!=null) {
					logback += "持有金币#("+l1ItemInstance.getCount()+")"+"#"+i;
				}
			}
		}
		int totalPrice = 0;
		for (L1ShopSellOrder order : orderList.getList()) {
			final L1ItemInstance item = inv.getItem(order.getItem()
					.getTargetId());
			//item = pc.getInventory().getItem(objectId);
			if (item == null)
				return;
			if (item.isEquipped())
				return;
			if (item.isSeal()) {
				return;
			}
			long count = inv.removeItem(order.getItem().getTargetId(), order
					.getCount());
			final long oneitemprice = order.getItem().getAssessedPrice()
					* count;
			totalPrice += oneitemprice;
			WriteLogTxt.Recording("NPC商人卖出记录", "玩家" + inv.getOwner().getName()
					+ "卖出物品" + item.getLogViewName() +"("+ count+")"+ "获得金币" + oneitemprice + "。");
		}

		totalPrice = IntRange.ensure(totalPrice, 0, 2000000000);
		if (0 < totalPrice) {
			inv.storeItem(L1ItemId.ADENA, totalPrice);
			orderList.getPc().sendPackets(new S_SystemMessage("本次出售获得金币:" + totalPrice));
		}
		L1ItemInstance[] ijbnow = inv.findItemsId(L1ItemId.ADENA);
		if (ijbnow.length > 0) {
			for (int i = 0; i < ijbnow.length; i++) {
				L1ItemInstance l1ItemInstance = ijbnow[i];
				if (l1ItemInstance!=null) {
					lognow += "持有金币("+l1ItemInstance.getCount()+")"+"#"+i;
				}
			}
		}
		WriteLogTxt.Recording("NPC商人卖出记录", "玩家" + inv.getOwner().getName()
				+ "卖出前("+logback+")"+ "卖出后" + lognow + "。");
	}

	public L1ShopBuyOrderList newBuyOrderList() {
		return new L1ShopBuyOrderList(this);
	}

	public L1ShopSellOrderList newSellOrderList(L1PcInstance pc) {
		return new L1ShopSellOrderList(this, pc);
	}
}
