package l1j.server.data.item_etcitem.reel;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.storage.CharactersItemStorage;

public class ScrollEnchantPvP_PvE_Move extends ItemExecutor {

	/**
	 *
	 */
	private ScrollEnchantPvP_PvE_Move() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ScrollEnchantPvP_PvE_Move();
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
		final int use_type = tgItem.getItem().getUseType();
		if (use_type != 1){
			pc.sendPackets(new S_ServerMessage(79)); // 没有发生任何事情。
			return;
		}
		if (tgItem.isSeal()) {
			pc.sendPackets(new S_SystemMessage(tgItem.getLogViewName() +"处于封印状态！"));
			return;
		}
		
//		item.setUpdatePVP(pvp);
//		item.setUpdatePVE(pve);
//		tgItem.setUpdatePVP(0);
//		tgItem.setUpdatePVE(0);
//		pc.getInventory().updateItem(tgItem,L1PcInventory.COL_PVP);
//		pc.getInventory().saveItem(tgItem,L1PcInventory.COL_PVP);
//		
//		pc.getInventory().updateItem(item,L1PcInventory.COL_PVE);
//		pc.getInventory().saveItem(item,L1PcInventory.COL_PVE);

		final int pve = item.getUpdatePVE();
		final int pvp = item.getUpdatePVP();
		if (pvp <= 0 || pve <= 0){
			if (pvp <= 0 || pve <= 0){
				pc.sendPackets(new S_SystemMessage(tgItem.getLogViewName() +"没有PVP/PVE属性可以转移！"));
				return;
			}
			item.setUpdatePVE(pve);
			item.setUpdatePVP(pvp);
			tgItem.setUpdatePVE(0);
			tgItem.setUpdatePVP(0);
			pc.getInventory().updateItem(tgItem,L1PcInventory.COL_PVP);
			pc.getInventory().updateItem(tgItem,L1PcInventory.COL_PVE);
			pc.getInventory().saveItem(tgItem,L1PcInventory.COL_PVP);
			pc.getInventory().saveItem(tgItem,L1PcInventory.COL_PVE);
			
			pc.getInventory().updateItem(item,L1PcInventory.COL_PVP);
			pc.getInventory().updateItem(item,L1PcInventory.COL_PVE);
			pc.getInventory().saveItem(item,L1PcInventory.COL_PVP);
			pc.getInventory().saveItem(item,L1PcInventory.COL_PVE);
			pc.sendPackets(new S_SystemMessage(tgItem.getLogViewName() +"属性转移成功！"));
		}else{
			pc.getInventory().removeItem(item, 1);//删除道具
			tgItem.setUpdatePVE(pve);
			tgItem.setUpdatePVP(pvp);
			pc.getInventory().updateItem(tgItem,L1PcInventory.COL_PVP);
			pc.getInventory().updateItem(tgItem,L1PcInventory.COL_PVE);
			pc.getInventory().saveItem(tgItem,L1PcInventory.COL_PVP);
			pc.getInventory().saveItem(tgItem,L1PcInventory.COL_PVE);
		}
	}
}
