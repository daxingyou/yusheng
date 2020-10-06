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
package l1j.server.server.model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.Config;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_AddItem;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_DeleteInventoryItem;
import l1j.server.server.serverpackets.S_EquipmentWindow;
import l1j.server.server.serverpackets.S_ItemColor;
import l1j.server.server.serverpackets.S_ItemStatus;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_ItemAmount;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.storage.CharactersItemStorage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.world.L1World;

public class L1PcInventory extends L1Inventory {
	
	private static final Log _log = LogFactory.getLog(L1PcInventory.class);

	private static final long serialVersionUID = 1L;

	private static final int MAX_SIZE = 180;

	private final L1PcInstance _owner; // 所有者

	private int _arrowId; // 优先使用ItemID

	private int _stingId; // 优先使用ItemID

	public L1PcInventory(L1PcInstance owner) {
		_owner = owner;
		_arrowId = 0;
		_stingId = 0;
	}

	public L1PcInstance getOwner() {
		return _owner;
	}
	
	// ３０段阶返
	public int getWeight240() {
		return calcWeight240(getWeight());
	}

	// ３０段阶算出
	public int calcWeight240(long weight) {
		int weight240 = 0;
		if (Config.RATE_WEIGHT_LIMIT != 0) {
			double maxWeight = _owner.getMaxWeight();
			if (weight > maxWeight) {
				weight240 = 240;
			} else {
				double wpTemp = (weight * 100 / maxWeight) * 240.00 / 100.00;
				DecimalFormat df = new DecimalFormat("00.##");
				df.format(wpTemp);
				wpTemp = Math.round(wpTemp);
				weight240 = (int) (wpTemp);
			}
		} else { // ０重量常０
			weight240 = 0;
		}
		return weight240;
	}

	@Override
	public int checkAddItem(L1ItemInstance item, long count) {
		return checkAddItem(item, count, true);
	}

	public int checkAddItem(L1ItemInstance item, long count, boolean message) {
		if (item == null) {
			return -1;
		}
		if (getSize() > MAX_SIZE
				|| (getSize() == MAX_SIZE && (!item.isStackable() || !checkItem(item
						.getItem().getItemId())))) { // 容量确认
			if (message) {
				sendOverMessage(263); // \f1一人持步最大180个。
			}
			return SIZE_OVER;
		}

		long weight = getWeight() + item.getItem().getWeight() * count / 1000 + 1;
		if (calcWeight240(weight) >= 240) {
			if (message) {
				sendOverMessage(82); // 重、以上持。
			}
			return WEIGHT_OVER;
		}

		L1ItemInstance itemExist = findItemId(item.getItemId());
		if (itemExist != null && (itemExist.getCount() + count) > Long.MAX_VALUE) {
			if (message) {
				getOwner().sendPackets(new S_ServerMessage(166,
						"所持",
						"2,000,000,000超过。")); // \f1%0%4%1%3%2
			}
			return AMOUNT_OVER;
		}
		
		return OK;
	}

	public void sendOverMessage(int message_id) {
		_owner.sendPackets(new S_ServerMessage(message_id));
	}

	// ＤＢcharacter_items读迂
	@Override
	public void loadItems() {
		try {
			CharactersItemStorage storage = CharactersItemStorage.create();

			for (L1ItemInstance item : storage.loadItems(_owner.getId())) {
				_items.add(item);

				if (item.isEquipped()) {
					item.setEquipped(false);
					setEquipped(item, true, true, false);
				}
				L1World.getInstance().storeWorldObject(item);
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	@Override
	public void loadItemtrades() {
		try {
			CharactersItemStorage storage = CharactersItemStorage.create();

			for (L1ItemInstance item : storage.loadItems(_owner.getId())) {
				_items.add(item);
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// ＤＢcharacter_items登录
	@Override
	public synchronized void insertItem(L1ItemInstance item) {
		try {
			CharactersItemStorage storage = CharactersItemStorage.create();
			item.set_char_objid(_owner.getId());
			storage.storeItem(_owner.getId(), item);
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		_owner.sendPackets(new S_AddItem(item));
		if (item.getItem().getWeight() != 0) {
			_owner.sendPackets(
					new S_PacketBox(S_PacketBox.WEIGHT, getWeight240()));
		}
	}
	public static final int COL_PVP = 524288;
	
	public static final int COL_PVE = 262144;
	
	public static final int COL_Ondurabilit = 131072;
	
	public static final int ITEM_TRADABLE = 65536;
	
	public static final int COL_BLESS = 32768;
	
	public static final int COL_OVERSEAL = 16384;
	
	public static final int COL_SEAL = 8192;
	
	public static final int COL_GAMBLE_NPCNMAE = 4096;
	
	public static final int COL_GAMBLE_NPCID = 2048;
	
	public static final int COL_GAMBLE_NO = 1024;
	
	public static final int COL_ATTR_ENCHANT_LEVEL = 512;

	public static final int COL_ATTR_ENCHANT_KIND = 256;

	public static final int COL_CHARGE_COUNT = 128;

	public static final int COL_ITEMID = 64;

	public static final int COL_DELAY_EFFECT = 32;

	public static final int COL_COUNT = 16;

	public static final int COL_EQUIPPED = 8;

	public static final int COL_ENCHANTLVL = 4;

	public static final int COL_IS_ID = 2;

	public static final int COL_DURABILITY = 1;

	@Override
	public void updateItem(L1ItemInstance item) {
		updateItem(item, COL_COUNT);
		if (item.getItem().isToBeSavedAtOnce()) {
			saveItem(item, COL_COUNT);
		}
	}

	/**
	 * 内状态更新。
	 * 
	 * @param item -
	 *            更新对像
	 * @param column -
	 *            更新种类
	 */
	@Override
	public void updateItem(L1ItemInstance item, int column) {
		//更新PVP PVE
		if (column >= COL_PVP) {
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_PVP;
		}
		if (column >= COL_PVE) {
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_PVE;
		}
		//更新PVP PVE
		if (column >= COL_Ondurabilit) {
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_Ondurabilit;
		}
		if (column >= COL_BLESS) {
			_owner.sendPackets(new S_ItemStatus(item));
			_owner.sendPackets(new S_ItemColor(item));
			column -= COL_BLESS;
		}
		if (column >= COL_OVERSEAL) {
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_OVERSEAL;
		}
		if (column >= COL_SEAL) {
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_SEAL;
		}
		if (column >= COL_GAMBLE_NPCNMAE) {
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_GAMBLE_NPCNMAE;
		}
		if (column >= COL_GAMBLE_NPCID) {
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_GAMBLE_NPCID;
		}
		if (column >= COL_GAMBLE_NO) {
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_GAMBLE_NO;
		}
		if (column >= COL_ATTR_ENCHANT_LEVEL) { // 属性强化数
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_ATTR_ENCHANT_LEVEL;
		}
		if (column >= COL_ATTR_ENCHANT_KIND) { // 属性强化の种类
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_ATTR_ENCHANT_KIND;
		}
		if (column >= COL_CHARGE_COUNT) { // 数
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_CHARGE_COUNT;
		}
		if (column >= COL_ITEMID) { // 别场合(便笺开封)
			_owner.sendPackets(new S_ItemStatus(item));
			_owner.sendPackets(new S_ItemColor(item));
			_owner.sendPackets(new S_PacketBox(
					S_PacketBox.WEIGHT, getWeight240()));
			column -= COL_ITEMID;
		}
		if (column >= COL_DELAY_EFFECT) { // 效果
			column -= COL_DELAY_EFFECT;
		}
		if (column >= COL_COUNT) { // 
			this._owner.sendPackets(new S_ItemStatus(item));
			final int weight = item.getWeight();
			if (weight != item.getLastWeight()) {
				item.setLastWeight(weight);
				this._owner.sendPackets(new S_ItemStatus(item));

			} else {
				this._owner.sendPackets(new S_ItemName(item));
			}
			if (item.getItem().getWeight() != 0) {
				// XXX 240段阶のウェイトが变化しない场合は送らなくてよい
				this._owner.sendPackets(new S_PacketBox(S_PacketBox.WEIGHT,
						getWeight240()));
			}
			column -= COL_COUNT;
		}
		if (column >= COL_EQUIPPED) { // 装备状态
			_owner.sendPackets(new S_ItemName(item));
			column -= COL_EQUIPPED;
		}
		if (column >= COL_ENCHANTLVL) { // 
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_ENCHANTLVL;
		}
		if (column >= COL_IS_ID) { // 确认状态
			_owner.sendPackets(new S_ItemStatus(item));
			_owner.sendPackets(new S_ItemColor(item));
			column -= COL_IS_ID;
		}
		if (column >= COL_DURABILITY) { // 耐久性
			_owner.sendPackets(new S_ItemStatus(item));
			column -= COL_DURABILITY;
		}
	}

	/**
	 * 内状态DB保存。
	 * 
	 * @param item -
	 *            更新对像
	 * @param column -
	 *            更新种类
	 */
	public void saveItem(L1ItemInstance item, int column) {
		if (column == 0) {
			return;
		}

		try {
			CharactersItemStorage storage = CharactersItemStorage.create();
			//更新PVP PVE 
			if (column >= COL_PVP) {
				storage.updateItemPVP(item);
				column -= COL_PVP;
			}
			if (column >= COL_PVE) {
				storage.updateItemPVE(item);
				column -= COL_PVE;
			}
			//更新PVP PVE 
			if (column >= COL_Ondurabilit) {
				storage.updateItemOndurability(item);
				column -= COL_Ondurabilit;
			}
			if (column >= ITEM_TRADABLE) {
				storage.updateItemTradable(item);
				column -= ITEM_TRADABLE;
			}
			if (column >= COL_BLESS) {
				storage.updateItemBless(item);
				column -= COL_BLESS;
			}
			if (column >= COL_OVERSEAL) {
				_owner.sendPackets(new S_ItemName(item));
				column -= COL_OVERSEAL;
			}
			if (column >= COL_SEAL) {
				storage.updateItemSeal(item);
				column -= COL_SEAL;
			}
			if (column >= COL_GAMBLE_NPCNMAE) {
				storage.updateItemGamNpcId(item);
				column -= COL_GAMBLE_NPCNMAE;
			}
			if (column >= COL_GAMBLE_NPCID) {
				storage.updateItemGamNpcId(item);
				column -= COL_GAMBLE_NPCID;
			}
			if (column >= COL_GAMBLE_NO) {
				storage.updateItemGamNo(item);
				column -= COL_GAMBLE_NO;
			}
			if (column >= COL_ATTR_ENCHANT_LEVEL) { // 属性强化数
				storage.updateItemAttrEnchantLevel(item);
				column -= COL_ATTR_ENCHANT_LEVEL;
			}
			if (column >= COL_ATTR_ENCHANT_KIND) { // 属性强化の种类
				storage.updateItemAttrEnchantKind(item);
				column -= COL_ATTR_ENCHANT_KIND;
			}
			if (column >= COL_CHARGE_COUNT) { // 数
				storage.updateItemChargeCount(item);
				column -= COL_CHARGE_COUNT;
			}
			if (column >= COL_ITEMID) { // 别场合(便笺开封)
				storage.updateItemId(item);
				column -= COL_ITEMID;
			}
			if (column >= COL_DELAY_EFFECT) { // 效果
				storage.updateItemDelayEffect(item);
				column -= COL_DELAY_EFFECT;
			}
			if (column >= COL_COUNT) { // 
				storage.updateItemCount(item);
				column -= COL_COUNT;
			}
			if (column >= COL_EQUIPPED) { // 装备状态
				storage.updateItemEquipped(item);
				column -= COL_EQUIPPED;
			}
			if (column >= COL_ENCHANTLVL) { // 
				storage.updateItemEnchantLevel(item);
				column -= COL_ENCHANTLVL;
			}
			if (column >= COL_IS_ID) { // 确认状态
				storage.updateItemIdentified(item);
				column -= COL_IS_ID;
			}
			if (column >= COL_DURABILITY) { // 耐久性
				storage.updateItemDurability(item);
				column -= COL_DURABILITY;
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	// ＤＢcharacter_items削除
	@Override
	public void deleteItem(L1ItemInstance item) {
		try {
			CharactersItemStorage storage = CharactersItemStorage.create();

			storage.deleteItem(item);
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		if (item.isEquipped()) {
			setEquipped(item, false);
		}
		_owner.sendPackets(new S_DeleteInventoryItem(item));
		_items.remove(item);
		if (item.getItem().getWeight() != 0) {
			_owner.sendPackets(
					new S_PacketBox(S_PacketBox.WEIGHT, getWeight240()));
		}
	}

	// 装着脱着（L1ItemInstance变更、补正值设定、character_items更新、送信管理）
	public void setEquipped(L1ItemInstance item, boolean equipped) {
		setEquipped(item, equipped, false, false);
	}

	public void setEquipped(L1ItemInstance item, boolean equipped,
			boolean loaded, boolean changeWeapon) {
		if (item.isEquipped() != equipped) { // 设定值违场合处理
			L1Item temp = item.getItem();
			if (equipped) { // 装着
				item.setEquipped(true);
				_owner.getEquipSlot().set(item);

				if (!loaded) {
					_owner.getEquipSlot().setMagicHelm(item);
				}
			} else { // 脱着
				if (!loaded) {
					//  装备中状态场合状态解除
					if (temp.getItemId() == 20077 || temp.getItemId() == 20062
							|| temp.getItemId() == 120077) {
						if (_owner.isInvisble()) {
							_owner.delInvis();
							return;
						}
					}
				}

				item.setEquipped(false);
				_owner.getEquipSlot().remove(item);

				if (!loaded) {
					int objectId = _owner.getId();
					_owner.getEquipSlot().removeMagicHelm(objectId, item);
				}
			}
			if (!loaded) { // 最初读迂时ＤＢ关连处理
				// XXX:意味
				_owner.setCurrentHp(_owner.getCurrentHp());
				_owner.setCurrentMp(_owner.getCurrentMp());
				updateItem(item, COL_EQUIPPED);
				_owner.sendPackets(new S_OwnCharStatus(_owner));
				if (temp.getType2() == 1 && changeWeapon == false) { // 武器场合更新。、武器持替武器脱着时更新
					_owner.sendPackets(new S_CharVisualUpdate(_owner));
					_owner.broadcastPacket(new S_CharVisualUpdate(_owner));
				}
				// _owner.getNetConnection().saveCharToDisk(_owner); //
				// DB情报书迂
			}
		}
		
		//_owner.sendPackets(new S_EquipmentWindow(item.getId(),item.getItem().getType(),item.isEquipped()));
	}

	// 特定装备确认
	public boolean checkEquipped(int id) {
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.getItem().getItemId() == id && item.isEquipped()) {
				return true;
			}
		}
		return false;
	}

	// 特定全装备确认（确认用）
	public boolean checkEquipped(int[] ids) {
		for (int id : ids) {
			if (!checkEquipped(id)) {
				return false;
			}
		}
		return true;
	}

	// 特定装备数
	public int getTypeEquipped(int type2, int type) {
		int equipeCount = 0;
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.getItem().getType2() == type2
					&& item.getItem().getType() == type && item.isEquipped()) {
				equipeCount++;
			}
		}
		return equipeCount;
	}

	// 装备特定
	public L1ItemInstance getItemEquipped(int type2, int type) {
		L1ItemInstance equipeitem = null;
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.getItem().getType2() == type2
					&& item.getItem().getType() == type && item.isEquipped()) {
				equipeitem = item;
				break;
			}
		}
		return equipeitem;
	}

	// 变身时装备装备外
	public void takeoffEquip(int polyid) {
		takeoffWeapon(polyid);
		takeoffArmor(polyid);
	}

	// 变身时装备武器外
	private void takeoffWeapon(int polyid) {
		if (_owner.getWeapon() == null) { // 素手
			return;
		}

		boolean takeoff = false;
		int weapon_type = _owner.getWeapon().getItem().getType();
		// 装备出来武器装备？
		takeoff = !L1PolyMorph.isEquipableWeapon(polyid, weapon_type);

		if (takeoff) {
			setEquipped(_owner.getWeapon(), false, false, false);
		}
	}

	// 变身时装备防具外
	private void takeoffArmor(int polyid) {
		L1ItemInstance armor = null;

		// 
		for (int type = 0; type <= 12; type++) {
			// 装备、装备不可场合外
			if (getTypeEquipped(2, type) != 0
					&& !L1PolyMorph.isEquipableArmor(polyid, type)) {
				if (type == 9) { // 场合、两手分外
					armor = getItemEquipped(2, type);
					if (armor != null) {
						setEquipped(armor, false, false, false);
					}
					armor = getItemEquipped(2, type);
					if (armor != null) {
						setEquipped(armor, false, false, false);
					}
				} else {
					armor = getItemEquipped(2, type);
					if (armor != null) {
						setEquipped(armor, false, false, false);
					}
				}
			}
		}
	}

	// 使用取得
	public L1ItemInstance getArrow() {
		return getBullet(0);
	}

	// 使用取得
	public L1ItemInstance getSting() {
		return getBullet(15);
	}

	private L1ItemInstance getBullet(int type) {
		L1ItemInstance bullet;
		int priorityId = 0;
		if (type == 0) {
			priorityId = _arrowId; // 
		}
		if (type == 15) {
			priorityId = _stingId; // 
		}
		if (priorityId > 0) // 优先弹
		{
			bullet = findItemId(priorityId);
			if (bullet != null) {
				return bullet;
			} else // 场合优先消
			{
				if (type == 0) {
					_arrowId = 0;
				}
				if (type == 15) {
					_stingId = 0;
				}
			}
		}

		for (Object itemObject : _items) // 弹探
		{
			bullet = (L1ItemInstance) itemObject;
			if (bullet.getItem().getType() == type) {
				if (type == 0) {
					_arrowId = bullet.getItem().getItemId(); // 优先
				}
				if (type == 15) {
					_stingId = bullet.getItem().getItemId(); // 优先
				}
				return bullet;
			}
		}
		return null;
	}

	// 优先设定
	public void setArrow(int id) {
		_arrowId = id;
	}

	// 优先设定
	public void setSting(int id) {
		_stingId = id;
	}

	// 装备ＨＰ自然回复补正
	public int hpRegenPerTick() {
		int hpr = 0;
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.isEquipped()) {
				hpr += item.getItem().get_addhpr();
			}
		}
		return hpr;
	}

	// 装备ＭＰ自然回复补正
	public int mpRegenPerTick() {
		int mpr = 0;
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.isEquipped()) {
				mpr += item.getItem().get_addmpr();
			}
		}
		return mpr;
	}

	public L1ItemInstance CaoPenalty() {
		Random random = new Random();
		int rnd = random.nextInt(_items.size());
		L1ItemInstance penaltyItem = _items.get(rnd);
		if (penaltyItem.getItem().getItemId() == L1ItemId.ADENA // 、不可落
				|| penaltyItem.getItem().getItemId() == L1ItemId.TIANBAO
				|| (!penaltyItem.getItem().isTradable() && !penaltyItem.isTradable())) {
			return null;
		}
		Object[] petlist = _owner.getPetList().values().toArray();
		for (Object petObject : petlist) {
			if (petObject instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) petObject;
				if (penaltyItem.getId() == pet.getItemObjId()) {
					return null;
				}
			}
		}
		setEquipped(penaltyItem, false);
		return penaltyItem;
	}

	public L1ItemInstance[] findGam() {
		ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
		for (L1ItemInstance item : _items) {
			if (item.getItem().getItemId() == 40309) {
				itemList.add(item);
			}
		}
		return itemList.toArray(new L1ItemInstance[] {});
	}
}
