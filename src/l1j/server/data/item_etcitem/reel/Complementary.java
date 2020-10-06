package l1j.server.data.item_etcitem.reel;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

public class Complementary extends ItemExecutor{

	/**
	 *
	 */
	private Complementary() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Complementary();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data
	 *            參數
	 * @param pc
	 *            執行者
	 * @param item
	 *            物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc,
			final L1ItemInstance item) {
		// 對象OBJID
		final int targObjId = data[0];
//		System.out.println(targObjId);

		// 目標物品
		final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

		if (tgItem == null) {
			return;
		}
		if (item == null) {
			return;
		}
		
		if (tgItem.getItemId() != 20288) {
			pc.sendPackets(new S_ServerMessage(79)); // 没有发生任何事情。
			return;
		}
		if (tgItem.getChargeCount() + _charge > 100) {
			pc.sendPackets(new S_SystemMessage("补充总和大于100次，补充失败！"));
			return;
		}
		pc.getInventory().removeItem(item, 1);
		tgItem.setChargeCount(tgItem.getChargeCount() + _charge);
		pc.getInventory().updateItem(tgItem,
				L1PcInventory.COL_CHARGE_COUNT);
		pc.getInventory().saveItem(tgItem,
				L1PcInventory.COL_CHARGE_COUNT);		
	}
	
	private int _charge = 0;
	
	@Override
	public void set_set(String[] set) {
		try {
			_charge = Integer.parseInt(set[1]);		
		} catch (Exception e) {
			_charge =  100;
		}
	}

}
