package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_AddItem;
import l1j.server.server.serverpackets.S_DeleteInventoryItem;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

public class Item_Tradable extends ItemExecutor {
	private Item_Tradable() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Item_Tradable();
    }

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		final int objectId = data[0];
		final L1ItemInstance targetItem = pc.getInventory().getItem(objectId);
		if (targetItem == null){
			return;
		}
		if (targetItem.getItem().getItemId() != 184 && targetItem.getItem().getItemId() != 56 && targetItem.getItem().getItemId() != 50 && targetItem.getItem().getItemId() != 20225){
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		if (targetItem.isTradable()){
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		targetItem.setTradable(true);
		pc.getInventory().removeItem(item, 1);
		pc.sendPackets(new S_DeleteInventoryItem(targetItem));
		pc.sendPackets(new S_AddItem(targetItem));
		pc.getInventory().saveItem(targetItem, 65536);
		pc.sendPackets(new S_SystemMessage("\\F2道具解封成功 可交易一次 交易完成后自动封印."));
	}
}
