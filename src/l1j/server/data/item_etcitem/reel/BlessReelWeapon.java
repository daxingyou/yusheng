package l1j.server.data.item_etcitem.reel;

import java.util.Random;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

public class BlessReelWeapon extends ItemExecutor {
	private BlessReelWeapon() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new BlessReelWeapon();
    }
    private int _random = 0;
    @Override
    public void set_set(String[] set){
    	if (set.length > 1){
    		try {
    			_random = Integer.parseInt(set[1]);
			} catch (Exception e) {
			}
    	}
    }
	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		final int objectId = data[0];
		final L1ItemInstance targetItem = pc.getInventory().getItem(objectId);
		if (targetItem == null || targetItem.getItem().getType2() != 1){
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		if (targetItem.getBless() == 2){
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		if (targetItem.getBless() == 0){
			pc.sendPackets(new S_SystemMessage("你的武器已经祝福过了."));
			return;
		}
		if (targetItem.isSeal()){
			pc.sendPackets(new S_SystemMessage((new StringBuilder(String.valueOf(targetItem.getLogViewName()))).append("处于封印状态！").toString()));
			return;
		}
		final Random rnd = new Random();
		pc.getInventory().removeItem(item, 1);
		if (rnd.nextInt(100) < _random){
			targetItem.setBless(0);
			pc.getInventory().updateItem(targetItem,L1PcInventory.COL_BLESS);
			pc.getInventory().saveItem(targetItem, L1PcInventory.COL_BLESS);
			pc.sendPackets(new S_SystemMessage(new StringBuilder(targetItem.getLogViewName()).append("祝福成功.").toString()));
		}else{
			pc.sendPackets(new S_SystemMessage(new StringBuilder(targetItem.getLogViewName()).append("祝福失败.").toString()));
		}
	}
}
