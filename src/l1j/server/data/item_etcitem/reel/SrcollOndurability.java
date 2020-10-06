package l1j.server.data.item_etcitem.reel;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

public class SrcollOndurability extends ItemExecutor {

	private SrcollOndurability() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new SrcollOndurability();
    }

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		final int objectId = data[0];
		final L1ItemInstance targetItem = pc.getInventory().getItem(objectId);
		if (targetItem == null){
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		if (targetItem.getItem().getType2() != 1){
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		if (targetItem.isOndurability()){
			pc.sendPackets(new S_SystemMessage("\\F2你武器已开启过了"));
			return;
		}
		pc.getInventory().removeItem(item, 1);
		targetItem.setOndurability(true);
		pc.getInventory().saveItem(targetItem, L1PcInventory.COL_Ondurabilit);
		pc.getInventory().updateItem(targetItem, L1PcInventory.COL_Ondurabilit);
		pc.sendPackets(new S_SystemMessage("\\F2开启自动修复成功"));
	}
}
