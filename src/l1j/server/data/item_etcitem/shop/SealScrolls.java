package l1j.server.data.item_etcitem.shop;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

public class SealScrolls extends ItemExecutor{
	
	private SealScrolls() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new SealScrolls();
	}

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		final int itemobj = data[0];
		final L1ItemInstance tgitem = pc.getInventory().getItem(itemobj);
		if (tgitem == null) {
			return;
		}
		if (tgitem.getItem().getType2() == 0) {
			pc.sendPackets(new S_ServerMessage(79)); // 没有发生任何事情。
			return;
		}
		if (tgitem.isSeal()) {
			pc.sendPackets(new S_ServerMessage(79)); // 没有发生任何事情。
			return;
		}
		tgitem.setSeal(true);
		pc.getInventory().updateItem(tgitem,
				L1PcInventory.COL_SEAL);
		pc.getInventory().saveItem(tgitem,
				L1PcInventory.COL_SEAL);
		pc.sendPackets(new S_SystemMessage(tgitem.getLogViewName() + "封印成功！"));
		pc.sendPackets(new S_SystemMessage("请注意，封印的物品在正义未满时依旧会掉落！"));
		pc.sendPackets(new S_SystemMessage("不过不会掉地上，而是直接消失！"));
		pc.getInventory().removeItem(item, 1);
	}
}
