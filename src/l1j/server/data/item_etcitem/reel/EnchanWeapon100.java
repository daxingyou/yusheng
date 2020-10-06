package l1j.server.data.item_etcitem.reel;

import l1j.server.data.cmd.EnchantExecutor;
import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

public class EnchanWeapon100 extends ItemExecutor {
	private EnchanWeapon100() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new EnchanWeapon100();
    }
    private int _enchantlv = 1;
    @Override
    public void set_set(String[] set){
    	if (set.length > 1){
    		try {
    			_enchantlv = Integer.parseInt(set[1]);
			} catch (Exception e) {
			}
    	}
    }
	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		final int objectId = data[0];
		final L1ItemInstance targetItem = pc.getInventory().getItem(objectId);
		if (targetItem == null || targetItem.getItem().getType2() != 1)
		{
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		int safe_enchant = targetItem.getItem().get_safeenchant();
		if (safe_enchant < 0)
		{
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		if (targetItem.isSeal())
		{
			pc.sendPackets(new S_SystemMessage((new StringBuilder(String.valueOf(targetItem.getLogViewName()))).append("处于封印状态！").toString()));
			return;
		}
		pc.getInventory().removeItem(item, 1);
		new EnchantExecutor().successEnchant(pc,targetItem,_enchantlv);
	}
}
