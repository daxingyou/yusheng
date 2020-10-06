package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class GMteleport extends ItemExecutor{
	
	/**
	 *
	 */
	private GMteleport() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new GMteleport();
	}

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		if (pc.getMapId() == 99) {
			L1Teleport.teleport(pc, 33075,33388, (short) 4, 5, true);
			pc.getInventory().removeItem(item, 1);
		}
	}

}
