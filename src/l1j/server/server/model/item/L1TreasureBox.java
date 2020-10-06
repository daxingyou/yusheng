package l1j.server.server.model.item;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.gui.J_Main;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.world.L1World;

@XmlAccessorType(XmlAccessType.FIELD)
public class L1TreasureBox {

	private static final Log _log = LogFactory.getLog(L1TreasureBox.class);


	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlRootElement(name = "TreasureBoxList")
	private static class TreasureBoxList implements Iterable<L1TreasureBox> {
		@XmlElement(name = "TreasureBox")
		private List<L1TreasureBox> _list;

		public Iterator<L1TreasureBox> iterator() {
			return _list.iterator();
		}
	}

	@XmlAccessorType(XmlAccessType.FIELD)
	private static class Item {
		@XmlAttribute(name = "ItemId")
		private int _itemId;

		@XmlAttribute(name = "Count")
		private int _count;

		private int _chance;
		
		@XmlAttribute(name = "Chance")
		private void setChance(double chance) {
			_chance = (int) (chance * 10000);
		}

		public int getItemId() {
			return _itemId;
		}

		public int getCount() {
			return _count;
		}

		public double getChance() {
			return _chance;
		}
	}

	private static enum TYPE {
		RANDOM, SPECIFIC
	}

	private static final String PATH = "./data/xml/Item/TreasureBox.xml";

	private static final HashMap<Integer, L1TreasureBox> _dataMap =
			new HashMap<Integer, L1TreasureBox>();

	/**
	 * 指定IDTreasureBox返。
	 * 
	 * @param id - TreasureBoxID。普通ItemId。
	 * @return 指定IDTreasureBox。见场合null。
	 */
	public static L1TreasureBox get(int id) {
		return _dataMap.get(id);
	}

	@XmlAttribute(name = "ItemId")
	private int _boxId;

	@XmlAttribute(name = "Type")
	private TYPE _type;
	
	@XmlAttribute(name = "DelItemId")
	private int _delItemId = 0;
	
	@XmlAttribute(name = "DelItemCount")
	private int _delItemCount = 0;

	private int getDelItemId() {
		return _delItemId;
	}
	
	private int getDelItemCount() {
		return _delItemCount;
	}
	
	private int getBoxId() {
		return _boxId;
	}

	private TYPE getType() {
		return _type;
	}

	@XmlElement(name = "Item")
	private CopyOnWriteArrayList<Item> _items;

	private List<Item> getItems() {
		return _items;
	}

	private int _totalChance;

	private int getTotalChance() {
		return _totalChance;
	}

	private void init() {
		for (Item each : getItems()) {
			_totalChance += each.getChance();
			if (ItemTable.getInstance().getTemplate(each.getItemId()) == null) {
				getItems().remove(each);
				_log.info("ID " + each.getItemId()
						+ " 见。");
			}
		}
		if (getTotalChance() != 0 && getTotalChance() != 1000000) {
			_log.info("ID " + getBoxId() + " 确率合计不等于100%。");
		}
	}

	public static void load() {
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("loading TreasureBox...");
		try {
			JAXBContext context =
					JAXBContext
							.newInstance(L1TreasureBox.TreasureBoxList.class);

			Unmarshaller um = context.createUnmarshaller();

			File file = new File(PATH);
			TreasureBoxList list = (TreasureBoxList) um.unmarshal(file);

			for (L1TreasureBox each : list) {
				each.init();
				_dataMap.put(each.getBoxId(), each);
			}
		} catch (Exception e) {
			_log.info(PATH + " 失败。", e);
			System.exit(0);
		}
		System.out.println("OK! " + timer.get() + "ms");
	}

	/**
	 * TreasureBox开PC入手。PC持场合
	 * 地面落。
	 * 
	 * @param pc - TreasureBox开PC
	 * @return 开封结果何出场合true返。
	 *         持地面落场合true。
	 */
	public boolean open(L1PcInstance pc,L1ItemInstance box) {
		int count = 1;
		if (getType().equals(TYPE.SPECIFIC)) {
			count = getItems().size();
		}
		if (pc.getInventory().getItems().size()  + count >= 180){
			pc.sendPackets(new S_ServerMessage(263));
			return false;
		}
		if (getDelItemId() > 0 && getDelItemCount() > 0){
			if (!pc.getInventory().checkItem(getDelItemId(), getDelItemCount())){
				final L1Item delItem = ItemTable.getInstance().getTemplate(getDelItemId());
				if (delItem != null){
					final StringBuilder msg = new StringBuilder();
					msg.append("开启所需要的");
					msg.append(delItem.getName());
					msg.append("不足(");
					msg.append(getDelItemCount());
					msg.append(")");
					pc.sendPackets(new S_SystemMessage(msg.toString()));
				}
				return false;
			}
		}
		if (getDelItemId() > 0 && getDelItemCount() > 0){
			pc.getInventory().consumeItem(getDelItemId(), getDelItemCount());
		}
		L1ItemInstance item = null;
		if (getType().equals(TYPE.SPECIFIC)) {
			// 出决
			for (Item each : getItems()) {
				item = ItemTable.getInstance().createItem(each.getItemId());
				if (item != null) {
					item.setCount(each.getCount());
					storeItem(pc, item);
				}
			}

		} else if (getType().equals(TYPE.RANDOM)) {
			// 出决
			Random random = new Random();
			long box_count = 1;
			if (box.getItem().getItemId() == 40414){
				box_count = box.getCount();
			}
			for(int c = 0; c < box_count;c++){
				int chance = 0;

				int r = random.nextInt(getTotalChance());
				for (Item each : getItems()) {
					chance += each.getChance();
					if (r < chance) {
						item = ItemTable.getInstance().createItem(each.getItemId());
						if (item == null) {
							return false;
						}
						if (item != null) {
							item.setCount(each.getCount());
							storeItem(pc, item);
							if (item.getItem().isBroad()) {
								L1World.getInstance().broadcastServerMessage("恭喜玩家"+pc.getName()+"打开"+box.getName()+"获得"+item.getLogViewName());
								J_Main.getInstance().addConsol("恭喜玩家"+pc.getName()+"打开"+box.getName()+"获得"+item.getLogViewName());
								WriteLogTxt.Recording(
										"开宝箱记录","玩家#"+pc.getName()
										+"在地图ID"+pc.getMapId()+"X:"+pc.getX()+"Y:"+pc.getY()+"#玩家objid：<"+pc.getId()+">"
								        +"#打开箱子"+box.getLogName()+"获得："+item.getLogViewName()+"物品objid：<"+item.getId()+">"
										+"("+item.getCount()+")个。"
										);
							}				
						}
						break;
					}
				}
			}
		}
		if (item == null) {
			return false;
		} else {
			int itemId = getBoxId();
			// 魂结晶破片、魔族、实
			if (itemId == 40576 || itemId == 40577 || itemId == 40578
					|| itemId == 40411 || itemId == 49013) {
				pc.death(null); // 死亡
			}
			return true;
		}
	}

	private static void storeItem(L1PcInstance pc, L1ItemInstance item) {
		L1Inventory inventory;

		if (pc.getInventory().checkAddItem(item, item.getCount()) == L1Inventory.OK) {
			inventory = pc.getInventory();
		} else {
			// 持场合地面落 处理（不正防止）
			inventory = L1World.getInstance().getInventory(pc.getLocation());
		}
		inventory.storeItem(item);
		pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0手入。
	}
}
