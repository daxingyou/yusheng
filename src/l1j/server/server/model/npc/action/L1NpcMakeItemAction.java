///*
// * This program is free software; you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation; either version 2, or (at your option)
// * any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
// * 02111-1307, USA.
// *
// * http://www.gnu.org/copyleft/gpl.html
// */
//package l1j.server.server.model.npc.action;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
//
//import l1j.server.server.datatables.ItemTable;
//import l1j.server.server.model.L1Object;
//import l1j.server.server.model.L1ObjectAmount;
//import l1j.server.server.model.L1PcInventory;
//import l1j.server.server.model.Instance.L1ItemInstance;
//import l1j.server.server.model.Instance.L1NpcInstance;
//import l1j.server.server.model.Instance.L1PcInstance;
//import l1j.server.server.model.npc.L1NpcHtml;
//import l1j.server.server.serverpackets.S_HowManyMake;
//import l1j.server.server.serverpackets.S_ServerMessage;
//import l1j.server.server.templates.L1Item;
//import l1j.server.server.utils.IterableElementList;
//
//public class L1NpcMakeItemAction extends L1NpcXmlAction {
//	private final List<L1ObjectAmount<Integer>> _materials = new ArrayList<L1ObjectAmount<Integer>>();
//	private final List<L1ObjectAmount<Integer>> _items = new ArrayList<L1ObjectAmount<Integer>>();
//	private final boolean _isAmountInputable;
//	private final L1NpcAction _actionOnSucceed;
//	private final L1NpcAction _actionOnFail;
//
//	public L1NpcMakeItemAction(Element element) {
//		super(element);
//
//		_isAmountInputable = L1NpcXmlParser.getBoolAttribute(element,
//				"AmountInputable", true);
//		NodeList list = element.getChildNodes();
//		for (Element elem : new IterableElementList(list)) {
//			if (elem.getNodeName().equalsIgnoreCase("Material")) {
//				int id = Integer.valueOf(elem.getAttribute("ItemId"));
//				int amount = Integer.valueOf(elem.getAttribute("Amount"));
//				_materials.add(new L1ObjectAmount<Integer>(id, amount));
//				continue;
//			}
//			if (elem.getNodeName().equalsIgnoreCase("Item")) {
//				int id = Integer.valueOf(elem.getAttribute("ItemId"));
//				int amount = Integer.valueOf(elem.getAttribute("Amount"));
//				_items.add(new L1ObjectAmount<Integer>(id, amount));
//				continue;
//			}
//		}
//
//		if (_items.isEmpty() || _materials.isEmpty()) {
//			throw new IllegalArgumentException();
//		}
//
//		Element elem = L1NpcXmlParser.getFirstChildElementByTagName(element,
//				"Succeed");
//		_actionOnSucceed = elem == null ? null : new L1NpcListedAction(elem);
//		elem = L1NpcXmlParser.getFirstChildElementByTagName(element, "Fail");
//		_actionOnFail = elem == null ? null : new L1NpcListedAction(elem);
//	}
//
//	private boolean makeItems(L1PcInstance pc, String npcName, int amount) {
//		if (amount <= 0) {
//			return false;
//		}
//        
//		boolean isEnoughMaterials = true;
//		for (L1ObjectAmount<Integer> material : _materials) {
//			if (!pc.getInventory().checkItemNotEquipped(material.getObject(),
//					material.getAmount() * amount)) {
//				L1Item temp = ItemTable.getInstance().getTemplate(
//						material.getObject());
//				pc.sendPackets(new S_ServerMessage(337, temp.getName()
//						+ "("
//						+ ((material.getAmount() * amount) - pc.getInventory()
//								.countItems(temp.getItemId())) + ")")); // \f1%0不足。
//				isEnoughMaterials = false;
//			}
//		}
//		if (!isEnoughMaterials) {
//			return false;
//		}
//
//		// 容量重量计算
//		int countToCreate = 0; // 个数（缠物1个）
//		int weight = 0;
//
//		for (L1ObjectAmount<Integer> makingItem : _items) {
//
////			if (makingItem.getAmount() != amount) {
////				System.out.println("我" + makingItem.getAmount());
////				System.out.println("的" + amount);
////				System.out.println("发" + makingItem.getObject());
////	        	return false;
////	        }
////	        if (makingItem.getAmount() <= 0) {
////	        	return false;
////	        }
//	        
//			L1Item temp = ItemTable.getInstance().getTemplate(
//					makingItem.getObject());
//			if (temp.isStackable()) {
//				if (!pc.getInventory().checkItem(makingItem.getObject())) {
//					countToCreate += 1;
//				}
//			} else {
//				countToCreate += makingItem.getAmount() * amount;
//			}
//			weight += temp.getWeight() * (makingItem.getAmount() * amount)
//					/ 1000;
//		}
//		// 容量确认
//		if (pc.getInventory().getSize() + countToCreate > 180) {
//			pc.sendPackets(new S_ServerMessage(263)); // \f1一人持步最大180个。
//			return false;
//		}
//		// 重量确认
//		if (pc.getMaxWeight() < pc.getInventory().getWeight() + weight) {
//			pc.sendPackets(new S_ServerMessage(82)); // 重、以上持。
//			return false;
//		}
//
//		for (L1ObjectAmount<Integer> material : _materials) {
//			// 材料消费
//			pc.getInventory().consumeItem(material.getObject(),
//					material.getAmount() * amount);
//		}
//
//		for (L1ObjectAmount<Integer> makingItem : _items) {
//			L1ItemInstance item = pc.getInventory().storeItem(
//					makingItem.getObject(), makingItem.getAmount() * amount);
//			if (item != null) {
//				String itemName = ItemTable.getInstance()
//						.getTemplate(makingItem.getObject()).getName();
//				if (makingItem.getAmount() * amount > 1) {
//					itemName = itemName + " (" + makingItem.getAmount()
//							* amount + ")";
//				}
//
//				pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0%1。
//			}
//		}
//		return true;
//	}
//
//	/**
//	 * 指定内、素材何数
//	 */
//	private long countNumOfMaterials(L1PcInventory inv) {
//		long count = Long.MAX_VALUE;
//		for (L1ObjectAmount<Integer> material : _materials) {
//			long numOfSet = inv.countItems(material.getObject())
//					/ material.getAmount();
//			count = Math.min(count, numOfSet);
//		}
//		return count;
//	}
//
//	@Override
//	public L1NpcHtml execute(String actionName, L1PcInstance pc, L1Object obj,
//			byte[] args) {
//		long numOfMaterials = countNumOfMaterials(pc.getInventory());
//		if (1 < numOfMaterials && _isAmountInputable) {
//			pc.sendPackets(new S_HowManyMake(obj.getId(), numOfMaterials,
//					actionName));
//			return null;
//		}
//		return executeWithAmount(actionName, pc, obj, 1);
//	}
//
////	@Override
////	public L1NpcHtml executeWithAmount(String actionName, L1PcInstance pc,
////			L1Object obj, int long amount) {
////		L1NpcInstance npc = (L1NpcInstance) obj;
////		L1NpcHtml result = null;
////		if (makeItems(pc, npc.getNpcTemplate().get_name(), amount)) {
////			if (_actionOnSucceed != null) {
////				result = _actionOnSucceed.execute(actionName, pc, obj,
////						new byte[0]);
////			}
////		} else {
////			if (_actionOnFail != null) {
////				result = _actionOnFail
////						.execute(actionName, pc, obj, new byte[0]);
////			}
////		}
////		return result == null ? L1NpcHtml.HTML_CLOSE : result;
////	}
//	@Override
//	public L1NpcHtml executeWithAmount(final String actionName,
//			final L1PcInstance pc, final L1Object obj, final long amount) {
//		final L1NpcInstance npc = (L1NpcInstance) obj;
//		L1NpcHtml result = null;
//		if(pc.iscangku() || pc.getOnlineStatus() != 1 || pc.ismakeitem()){
//			return L1NpcHtml.HTML_CLOSE;
//		}
//		pc.setismakeitem(true);
//		try{
//		if (this.makeItems(pc, npc.getNameId(), amount)) {
//			if (this._actionOnSucceed != null) {
//				result = this._actionOnSucceed.execute(actionName, pc, obj,
//						new byte[0]);
//			}
//		} else {
//			if (this._actionOnFail != null) {
//				result = this._actionOnFail.execute(actionName, pc, obj,
//						new byte[0]);
//			}
//		}
//		pc.setismakeitem(false);
//		}catch (final Exception e) {
//
//		} finally {
//			pc.setismakeitem(false);
//		}
//		return result == null ? L1NpcHtml.HTML_CLOSE : result;
//	}
//
//
//}

package l1j.server.server.model.npc.action;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1ObjectAmount;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.serverpackets.S_HowManyMake;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.IterableElementList;

public class L1NpcMakeItemAction extends L1NpcXmlAction {
	private final List<L1ObjectAmount<Integer>> _materials = new ArrayList<L1ObjectAmount<Integer>>();
	private final List<L1ObjectAmount<Integer>> _items = new ArrayList<L1ObjectAmount<Integer>>();
	private final boolean _isAmountInputable;
	private final L1NpcAction _actionOnSucceed;
	private final L1NpcAction _actionOnFail;

	public L1NpcMakeItemAction(final Element element) {
		super(element);

		this._isAmountInputable = L1NpcXmlParser.getBoolAttribute(element,
				"AmountInputable", true);
		final NodeList list = element.getChildNodes();
		for (final Element elem : new IterableElementList(list)) {
			if (elem.getNodeName().equalsIgnoreCase("Material")) {
				final int id = Integer.valueOf(elem.getAttribute("ItemId"));
				final long amount = Long.valueOf(elem.getAttribute("Amount"));
				this._materials.add(new L1ObjectAmount<Integer>(id, amount));
				continue;
			}
			if (elem.getNodeName().equalsIgnoreCase("Item")) {
				final int id = Integer.valueOf(elem.getAttribute("ItemId"));
				final long amount = Long.valueOf(elem.getAttribute("Amount"));
				this._items.add(new L1ObjectAmount<Integer>(id, amount));
				continue;
			}
		}

		if (this._items.isEmpty() || this._materials.isEmpty()) {
			throw new IllegalArgumentException();
		}

		Element elem = L1NpcXmlParser.getFirstChildElementByTagName(element,
				"Succeed");
		this._actionOnSucceed = elem == null ? null : new L1NpcListedAction(
				elem);
		elem = L1NpcXmlParser.getFirstChildElementByTagName(element, "Fail");
		this._actionOnFail = elem == null ? null : new L1NpcListedAction(elem);
	}

	private synchronized boolean makeItems(final L1PcInstance pc, final String npcName,
			final long amount) {
		if (amount <= 0) {
			return false;
		}

		boolean isEnoughMaterials = true;
		for (final L1ObjectAmount<Integer> material : this._materials) {
			if (!pc.getInventory().checkItemNotEquipped(material.getObject(),
					material.getAmount() * amount)) {
				final L1Item temp = ItemTable.getInstance().getTemplate(
						material.getObject());
				pc.sendPackets(new S_ServerMessage(337, temp.getNameId()
						+ "("
						+ ((material.getAmount() * amount) - pc.getInventory()
								.countItems(temp.getItemId())) + ")"));
				isEnoughMaterials = false;
			}
		}
		if (!isEnoughMaterials) {
			return false;
		}

		// 容量と重量の计算
		int countToCreate = 0; // アイテムの个数（缠まる物は1个）
		int weight = 0;

		for (final L1ObjectAmount<Integer> makingItem : this._items) {
			final L1Item temp = ItemTable.getInstance().getTemplate(
					makingItem.getObject());
			if (temp.isStackable()) {
				if (!pc.getInventory().checkItem(makingItem.getObject())) {
					countToCreate += 1;
				}
			} else {
				countToCreate += makingItem.getAmount() * amount;
			}
			weight += temp.getWeight() * (makingItem.getAmount() * amount)
					/ 1000;
		}
		// 容量确认
		if (pc.getInventory().getSize() + countToCreate > 180) {
			pc.sendPackets(new S_ServerMessage(263)); // 263 \f1一个角色最多可携带180个道具。
			return false;
		}
		// 重量确认
		if (pc.getMaxWeight() < pc.getInventory().getWeight() + weight) {
			pc.sendPackets(new S_ServerMessage(82)); // 82 此物品太重了，所以你无法携带。
			return false;
		}

		for (final L1ObjectAmount<Integer> material : this._materials) {
			// 材料消费
			pc.getInventory().consumeItem(material.getObject(),
					material.getAmount() * amount);
		}

		for (final L1ObjectAmount<Integer> makingItem : this._items) {
			final L1ItemInstance item = pc.getInventory().storeItem(
					makingItem.getObject(), makingItem.getAmount() * amount);
			if (item != null) {
				String itemName = ItemTable.getInstance()
						.getTemplate(makingItem.getObject()).getNameId();
				if (makingItem.getAmount() * amount > 1) {
					itemName = itemName + " (" + makingItem.getAmount()
							* amount + ")";
				}
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0が%1をくれました。
			}
		}
		return true;
	}

	/**
	 * 指定されたインベントリ内に、素材が何セットあるか数える
	 */
	private long countNumOfMaterials(final L1PcInventory inv) {
		long count = Long.MAX_VALUE;
		for (final L1ObjectAmount<Integer> material : this._materials) {
			final long numOfSet = inv.countItems(material.getObject())
					/ material.getAmount();
			count = Math.min(count, numOfSet);
		}
		return count;
	}

	@Override
	public L1NpcHtml execute(final String actionName, final L1PcInstance pc,
			final L1Object obj, final byte[] args) {
		final long numOfMaterials = this.countNumOfMaterials(pc.getInventory());
		if ((1 < numOfMaterials) && this._isAmountInputable) {
			pc.sendPackets(new S_HowManyMake(obj.getId(), (int) numOfMaterials,
					actionName));
			return null;
		}
		return this.executeWithAmount(actionName, pc, obj, 1);
	}

	@Override
	public L1NpcHtml executeWithAmount(final String actionName,
			final L1PcInstance pc, final L1Object obj, final long amount) {
		final L1NpcInstance npc = (L1NpcInstance) obj;
		L1NpcHtml result = null;
		if(pc.iscangku() || pc.getOnlineStatus() != 1 || pc.ismakeitem()){
			return L1NpcHtml.HTML_CLOSE;
		}
		pc.setismakeitem(true);
		try{
		if (this.makeItems(pc, npc.getNameId(), amount)) {
			if (this._actionOnSucceed != null) {
				result = this._actionOnSucceed.execute(actionName, pc, obj,
						new byte[0]);
			}
		} else {
			if (this._actionOnFail != null) {
				result = this._actionOnFail.execute(actionName, pc, obj,
						new byte[0]);
			}
		}
		pc.setismakeitem(false);
		}catch (final Exception e) {

		} finally {
			pc.setismakeitem(false);
		}
		return result == null ? L1NpcHtml.HTML_CLOSE : result;
	}

	@Override
	public void execute(String actionName, String npcid) {
		// TODO Auto-generated method stub

	}

}
