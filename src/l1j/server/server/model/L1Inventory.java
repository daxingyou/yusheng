/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import l1j.server.Config;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.LetterTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.templates.L1Item;
import l1j.server.server.world.L1World;

public class L1Inventory extends L1Object {

	private static final long serialVersionUID = 1L;

	protected List<L1ItemInstance> _items = new CopyOnWriteArrayList<L1ItemInstance>();

	//public static final int MAX_AMOUNT = 2000000000; // 2G
	
	public static final int MAX_SIZE = 8;
	
	public static final int MAX_WEIGHT = 1500;

	public L1Inventory() {
		//
	}

	// 内总数
	public int getSize() {
		return _items.size();
	}

	// 内全
	public List<L1ItemInstance> getItems() {
		return _items;
	}

	// 内总重量
	public int getWeight() {
		int weight = 0;

		for (L1ItemInstance item : _items) {
			weight += item.getWeight();
		}

		return weight;
	}

	// 引数追加容量重量大丈夫确认及送信
	public static final int OK = 0;

	public static final int SIZE_OVER = 1;

	public static final int WEIGHT_OVER = 2;

	public static final int AMOUNT_OVER = 3;

	public synchronized int checkAddItem(L1ItemInstance item, long count) {
		if (item == null) {
			return -1;
		}
		if (getSize() > MAX_SIZE
				|| (getSize() == MAX_SIZE && (!item.isStackable() || !checkItem(item
						.getItem().getItemId())))) { // 容量确认
			return SIZE_OVER;
		}

		long weight = getWeight() + item.getItem().getWeight() * count / 1000 + 1;
		if (weight > (MAX_WEIGHT * Config.RATE_WEIGHT_LIMIT_PET)) { // 他重量确认（主）
			return WEIGHT_OVER;
		}

		L1ItemInstance itemExist = findItemId(item.getItemId());
		if (itemExist != null && (itemExist.getCount() + count) > Long.MAX_VALUE) {
			return AMOUNT_OVER;
		}
		
		return OK;
	}

	// 新格纳
	public synchronized L1ItemInstance storeItem(int id, long count) {
		if (count<=0) {
			return null;
		}
		
		L1Item temp = ItemTable.getInstance().getTemplate(id);
		if (temp == null) {
			return null;
		}
		
		

		if (temp.isStackable()) {
			L1ItemInstance item = new L1ItemInstance(temp, count);

			if (findItemId(id) == null) { // 新生成必要场合ID发行L1World登录行
				item.setId(IdFactory.getInstance().nextId());
//				System.out.println("只生成1个");
				L1World.getInstance().storeWorldObject(item);
			}

			return storeItem(item);
		}

		// 场合
		L1ItemInstance result = null;
		for (int i = 0; i < count; i++) {
			L1ItemInstance item = new L1ItemInstance(temp, 1);
			item.setId(IdFactory.getInstance().nextId());
			L1World.getInstance().storeWorldObject(item);
			storeItem(item);
			result = item;
		}
		// 最后作返。配列戾定义变更良。
		return result;
	}
	
	

	// DROP、购入、GM入手新格纳
	public synchronized L1ItemInstance storeItem(L1ItemInstance item) {
		if (item == null) {
			return null;
		}
		if (item.getCount() <= 0) {
			return null;
		}
		int itemId = item.getItem().getItemId();
		if (item.isStackable()) {
			if (item.getItem().getItemId() == 40309) {
				final L1ItemInstance[] items = this.findItemsId(item
						.getItemId());
				for (final L1ItemInstance gamItem : items) {
					if (gamItem != null && 
							gamItem.getGamNo() == item.getGamNo() && 
							gamItem.getGamNpcId() == item.getGamNpcId()) {			
							gamItem.setCount(gamItem.getCount() + item.getCount());
							updateItem(gamItem);
//							System.out.println("转移A步骤1购入赛狗票数量:"+item.getCount()+"NPCID:"+item.getGamNpcId()+"NPCNAME:"+item.getGamNpcName());
//							System.out.println("转移A步骤2购入赛狗票数量:"+gamItem.getCount()+"NPCID:"+gamItem.getGamNpcId()+"NPCNAME:"+gamItem.getGamNpcName());
							return gamItem;
						}
/*					System.out.println("转移B步骤1购入赛狗票数量:"+item.getCount()+"NPCID:"+item.getGamNpcId()+"NPCNAME:"+item.getGamNpcName());
					if (gamItem!=null) {
						System.out.println("转移B步骤2购入赛狗票数量:"+gamItem.getCount()+"NPCID:"+gamItem.getGamNpcId()+"NPCNAME:"+gamItem.getGamNpcName());
					}*/
				}
//				L1ItemInstance gamItem = findItemId(itemId);								
			} else {
				L1ItemInstance findItem = findItemId(itemId);
				findItem = findItemId(itemId);
				if (findItem != null) {
					findItem.setCount(findItem.getCount() + item.getCount());
					updateItem(findItem);
					return findItem;
				}
			}		
		}
		item.setX(getX());
		item.setY(getY());
		item.setMap(getMapId());
		int chargeCount = item.getItem().getMaxChargeCount();
		if (item.getItem().getItemId() == 40006
				|| item.getItem().getItemId() == 40007
				|| item.getItem().getItemId() == 40008
				|| item.getItem().getItemId() == 140006
				|| item.getItem().getItemId() == 140008
				|| item.getItem().getItemId() == 41401) {
			Random random = new Random();
			chargeCount -= random.nextInt(5);
		}
		if (item.getItem().getItemId() == 20383) {
			chargeCount = 50;
		}
/*		if (item.getItem().getItemId() == 20288) {//传戒
			chargeCount = 100;
		}*/
		item.setChargeCount(chargeCount);
		_items.add(item);
		insertItem(item);
		return item;
	}

	// /trade、仓库入手格纳
	public synchronized L1ItemInstance storeTradeItem(L1ItemInstance item) {
		if (item.isStackable()) {
			if (item.getItem().getItemId() == 40309) {
				final L1ItemInstance[] items = this.findItemsId(item
						.getItemId());
				for (final L1ItemInstance gamItem : items) {
					if (gamItem != null && 
							gamItem.getGamNo() == item.getGamNo() && 
							gamItem.getGamNpcId() == item.getGamNpcId()) {			
							gamItem.setCount(gamItem.getCount() + item.getCount());
							updateItem(gamItem);
							System.out.println("转移A步骤1购入赛狗票数量:"+item.getCount()+"NPCID:"+item.getGamNpcId()+"NPCNAME:"+item.getGamNpcName());
							System.out.println("转移A步骤2购入赛狗票数量:"+gamItem.getCount()+"NPCID:"+gamItem.getGamNpcId()+"NPCNAME:"+gamItem.getGamNpcName());
							return gamItem;
						}
					System.out.println("转移B步骤1购入赛狗票数量:"+item.getCount()+"NPCID:"+item.getGamNpcId()+"NPCNAME:"+item.getGamNpcName());
					if (gamItem!=null) {
						System.out.println("转移B步骤2购入赛狗票数量:"+gamItem.getCount()+"NPCID:"+gamItem.getGamNpcId()+"NPCNAME:"+gamItem.getGamNpcName());
					}
				}
//				L1ItemInstance gamItem = findItemId(itemId);								
			} else {
				L1ItemInstance findItem = findItemId(item.getItem().getItemId());
				if (findItem != null) {
					findItem.setCount(findItem.getCount() + item.getCount());
					updateItem(findItem);
					return findItem;
				}
			}		
		}
		item.setX(getX());
		item.setY(getY());
		item.setMap(getMapId());
		_items.add(item);
		insertItem(item);
		return item;
	}

	/**
	 * 指定ID削除。L1ItemInstance照
	 * 场合removeItem方使用。 （矢魔石特定消费使）
	 * 
	 * @param itemid -
	 *            削除itemid(objid)
	 * @param count -
	 *            削除个
	 * @return 际削除场合true返。
	 */
	public  boolean consumeItem(int itemid, long count) {
		if (ItemTable.getInstance().getTemplate(itemid).isStackable()) {
			L1ItemInstance item = findItemId(itemid);
			if (item != null && item.getCount() >= count) {
				removeItem(item, count);
				return true;
			}
		} else {
			L1ItemInstance[] itemList = findItemsId(itemid);
			if (itemList.length == count) {
				for (int i = 0; i < count; i++) {
					removeItem(itemList[i], 1);
				}
				return true;
			} else if (itemList.length > count) { // 指定个数多所持场合
				DataComparator<L1ItemInstance> dc = new DataComparator<L1ItemInstance>();
				Arrays.sort(itemList, dc); // 顺、数少消费
				for (int i = 0; i < count; i++) {
					removeItem(itemList[i], 1);
				}
				return true;
			}
		}
		return false;
	}

	public class DataComparator<T> implements Comparator<L1ItemInstance> {
		@Override
		public int compare(L1ItemInstance item1, L1ItemInstance item2) {
			return item1.getEnchantLevel()
					- item2.getEnchantLevel();
		}
	}

	// 指定指定个数削除（使箱舍）戾值：实际削除数
	public  long removeItem(int objectId, long count) {
		L1ItemInstance item = getItem(objectId);
		return removeItem(item, count);
	}

	public synchronized long removeItem(L1ItemInstance item, long count) {
		if (item == null) {
			return 0;
		}
		//System.out.println("步骤1");
		if (item.getCount() < count) {
			count = item.getCount();
		}
		if ((item.getCount() <= 0) || (count <= 0)) {
			return 0;
		}
		//System.out.println("步骤2");
		if (item.getCount() == count) {
			if (item.getItem().getItemId() == 40314 || // 
					item.getItem().getItemId() == 40316) {
				PetTable.getInstance().deletePet(item.getId());
			} else if (item.getItem().getItemId() >= 49016 // 便笺
					&& item.getItem().getItemId() <= 49025) {
				LetterTable lettertable = new LetterTable();
				lettertable.deleteLetter(item.getId());
			}
			//System.out.println("步骤3");
			deleteItem(item);
			L1World.getInstance().removeWorldObject(item);
		} else {
			//System.out.println("步骤4");
			item.setCount(item.getCount() - count);
			updateItem(item);
		}
		return count;
	}
	
	public void removeItem(L1ItemInstance item){
		if (item == null) {
			return;
		}
		//System.out.println("步骤0");
		removeItem(item,item.getCount());
	}

	// _items指定削除(L1PcInstance、L1DwarfInstance、L1GroundInstance部分)
	public void deleteItem(L1ItemInstance item) {
		_items.remove(item);
	}

	// 引移
	public  L1ItemInstance tradeItem(int objectId, long count,
			L1Inventory inventory) {
		L1ItemInstance item = getItem(objectId);
		return tradeItem(item, count, inventory);
	}

	//防洗道具 
	/*public L1ItemInstance tradeItem(L1ItemInstance item, int count,
			L1Inventory inventory) {*/
	public synchronized L1ItemInstance tradeItem(L1ItemInstance item, long count,
			L1Inventory inventory) {
		if (item == null) {
			return null;
		}
		if ((item.getCount() <= 0) || (count <= 0)) {
			return null;
		}
		if (item.isEquipped()) {
			return null;
		}
/*		if (!checkItem(item.getItem().getItemId(), count)) {
			return null;
		}*/
		if (item.getCount() < count) {
			return null;
		}
		if (!_items.contains(item)) {
			return null;
		}
		L1ItemInstance carryItem;
		if (item.getCount() <= count) {
			deleteItem(item);
			carryItem = item;
		} else {
			item.setCount(item.getCount() - count);
			updateItem(item);
			carryItem = ItemTable.getInstance().createItem(item.getItem().getItemId());
			carryItem.setCount(count);
			carryItem.setEnchantLevel(item.getEnchantLevel());
			carryItem.setIdentified(item.isIdentified());
			carryItem.set_durability(item.get_durability());
			carryItem.setChargeCount(item.getChargeCount());
			carryItem.setAttrEnchantKind(item.getAttrEnchantKind());
			carryItem.setAttrEnchantLevel(item.getAttrEnchantLevel());
			carryItem.setGamNo(item.getGamNo());
			carryItem.setGamNpcId(item.getGamNpcId());
			carryItem.setGamNpcName(item.getGamNpcName());
			//carryItem.setRemainingTime(item.getRemainingTime());
			carryItem.setLastUsed(item.getLastUsed());
			//carryItem.setBless(item.getBless());
			
		}
		return inventory.storeTradeItem(carryItem);
	}
	//防洗道具  end

	/*
	 * 损伤损耗（武器防具含） 场合、损耗 武器防具损伤度表。
	 */
	public L1ItemInstance receiveDamage(int objectId) {
		L1ItemInstance item = getItem(objectId);
		return receiveDamage(item);
	}

	public L1ItemInstance receiveDamage(L1ItemInstance item) {
		return receiveDamage(item, 1);
	}

	public L1ItemInstance receiveDamage(L1ItemInstance item, int count) {
		int itemType = item.getItem().getType2();
		int currentDurability = item.get_durability();

/*		if (item == null) {
			return null;
		}*/

		if ((currentDurability == 0 && itemType == 0) || currentDurability < 0) {
			item.set_durability(0);
			return null;
		}

		// 武器防具损伤度
		if (itemType == 0) {
			int minDurability = (item.getEnchantLevel() + 5) * -1;
			int durability = currentDurability - count;
			if (durability < minDurability) {
				durability = minDurability;
			}
			if (currentDurability > durability) {
				item.set_durability(durability);
			}
		} else {
			int maxDurability = item.getEnchantLevel() + 5;
			int durability = currentDurability + count;
			if (durability > maxDurability) {
				durability = maxDurability;
			}
			if (currentDurability < durability) {
				//该武器是否开启自动修复
				if (item.isOndurability()){
					if (!consumeItem(40317, durability)){
						item.set_durability(durability);
					}else{
						item.set_durability(0);
					}
				}else{
					item.set_durability(durability);
				}
			}
		}

		updateItem(item, L1PcInventory.COL_DURABILITY);
		return item;
	}

	public L1ItemInstance recoveryDamage(L1ItemInstance item) {
		int itemType = item.getItem().getType2();
		int durability = item.get_durability();

/*		if (item == null) {
			return null;
		}*/

		if ((durability == 0 && itemType != 0) || durability < 0) {
			item.set_durability(0);
			return null;
		}

		if (itemType == 0) {
			// 耐久度。
			item.set_durability(durability + 1);
		} else {
			// 损伤度。
			item.set_durability(durability - 1);
		}

		updateItem(item, L1PcInventory.COL_DURABILITY);
		return item;
	}

	// ＩＤ检索
	public L1ItemInstance findItemId(int itemid) {
		for (L1ItemInstance item : _items) {
			if (item.getItem().getItemId() == itemid) {
				return item;
			}
		}
		return null;
	}

	public L1ItemInstance[] findItemsId(int id) {
		ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
		for (L1ItemInstance item : _items) {
			if (item.getItemId() == id) {
				itemList.add(item);
			}
		}
		return itemList.toArray(new L1ItemInstance[] {});
	}
	
	public L1ItemInstance findEquipped(int id){
		for (L1ItemInstance item : _items) {
			if (item.isEquipped()) {
				if (item.getItemId() == id) {
					return item;
				}
			}
		}
		return null;
	}

	public L1ItemInstance[] findItemsIdNotEquipped(int id) {
		ArrayList<L1ItemInstance> itemList = new ArrayList<L1ItemInstance>();
		for (L1ItemInstance item : _items) {
			if (item.getItemId() == id) {
				if (!item.isEquipped()) {
					itemList.add(item);
				}
			}
		}
		return itemList.toArray(new L1ItemInstance[] {});
	}

	// ＩＤ检索
	public L1ItemInstance getItem(int objectId) {
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			if (item.getId() == objectId) {
				return item;
			}
		}
		return null;
	}

	// 特定指定个数以上所持确认（矢魔石确认）
	public boolean checkItem(int id) {
		return checkItem(id, 1);
	}

	public boolean checkItem(int id, long count) {
		if (count <= 0) {
			return true;
		}
		if (ItemTable.getInstance().getTemplate(id).isStackable()) {
			L1ItemInstance item = findItemId(id);
			if (item != null && item.getCount() >= count) {
				return true;
			}
		} else {
			Object[] itemList = findItemsId(id);
			if (itemList.length >= count) {
				return true;
			}
		}
		return false;
	}
	
	public boolean checkItem(final L1ItemInstance item, final long count) {
		if (count <= 0) {
			return true;
		}
		
		if (item.getCount() >= count) {
			return true;
		}
		return false;
	}

	/**
     * 指定物品编号以及数量<BR>
     * 该物件未在装备状态
     * 
     * @param itemid
     * @param count
     * @return 足够传回物品
     */
    public L1ItemInstance checkItemX(final int itemid, final long count) {
        if (count <= 0) {
            return null;
        }
        if (ItemTable.getInstance().getTemplate(itemid) != null) {
            final L1ItemInstance item = this.findItemIdNoEq(itemid);
            if ((item != null) && (item.getCount() >= count)) {
                return item;
            }
        }
        return null;
    }
    /**
     * 找寻指定物品(未装备)
     * 
     * @param itemId
     * @return
     */
    public L1ItemInstance findItemIdNoEq(final int itemId) {
        for (final L1ItemInstance item : this._items) {
            if (item.getItem().getItemId() == itemId && !item.isEquipped()) {
            	return item;
            }
        }
        return null;
    }
	// 特定指定个数以上所持确认
	// 装备中所持判别
	public boolean checkItemNotEquipped(int id, long count) {
		if (count == 0) {
			return true;
		}
		return count <= countItems(id);
	}

	// 特定全必要个数所持确认（复数所持确认）
	public boolean checkItem(int[] ids) {
		int len = ids.length;
		int[] counts = new int[len];
		for (int i = 0; i < len; i++) {
			counts[i] = 1;
		}
		return checkItem(ids, counts);
	}

	public boolean checkItem(int[] ids, int[] counts) {
		for (int i = 0; i < ids.length; i++) {
			if (!checkItem(ids[i], counts[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 内、指定ID数数。
	 * 
	 * @return
	 */
	public long countItems(int id) {
		if (ItemTable.getInstance().getTemplate(id).isStackable()) {
			L1ItemInstance item = findItemId(id);
			if (item != null) {
				return item.getCount();
			}
		} else {
			Object[] itemList = findItemsIdNotEquipped(id);
			return itemList.length;
		}
		return 0;
	}

	public void shuffle() {
		Collections.shuffle(_items);
	}
	
	public void sort(){
		Collections.sort(_items,new Comparator<L1ItemInstance>(){
			@Override
			public int compare(L1ItemInstance item1, L1ItemInstance itme2) {
				return new Integer(item1.getItemId()).compareTo(new Integer(itme2.getItemId()));  
			}  
	    });
	}
	
	  

	// 内全消（所有者消）
	public void clearItems() {
		for (Object itemObject : _items) {
			L1ItemInstance item = (L1ItemInstance) itemObject;
			L1World.getInstance().removeWorldObject(item);
		}
		_items.clear();
	}

	// 用
	public void loadItems() {
	}

	public void loadItemtrades(){
		
	}
	public void insertItem(L1ItemInstance item) {
	}

	public void updateItem(L1ItemInstance item) {
	}

	public void updateItem(L1ItemInstance item, int colmn) {
	}

}
