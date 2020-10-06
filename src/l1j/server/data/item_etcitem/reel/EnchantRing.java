package l1j.server.data.item_etcitem.reel;

import java.util.Random;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.datatables.EnchantRingListTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_BlueMessage;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

public class EnchantRing extends ItemExecutor {
	private static Random _random = new Random();
	private EnchantRing() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new EnchantRing();
    }
    private int _enchantmaxlv = 1;
    private int _enchantrandom = 0;
    @Override
    public void set_set(String[] set){
    	if (set.length > 1){
    		try {
    			_enchantmaxlv = Integer.parseInt(set[1]);
			} catch (Exception e) {
			}
    	}
    	if (set.length > 2){
    		try {
    			_enchantrandom = Integer.parseInt(set[2]);
			} catch (Exception e) {
			}
    	}
    }
	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		final int objectId = data[0];
		final L1ItemInstance targetItem = pc.getInventory().getItem(objectId);
		if (targetItem == null || targetItem.getItem().getType2() != 2 || targetItem.getItem().getType() != 9 || targetItem.getItem().getItemId() == 25063){
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		if (!EnchantRingListTable.get().isEnchant(targetItem.getItem().getItemId())){
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		if (targetItem.isSeal()){
			pc.sendPackets(new S_SystemMessage((new StringBuilder(String.valueOf(targetItem.getLogViewName()))).append("处于封印状态！").toString()));
			return;
		}
		final int old_enchant = targetItem.getEnchantLevel();
		if (old_enchant >= _enchantmaxlv){
			pc.sendPackets(new S_ServerMessage(79));
			return;
		}
		pc.getInventory().removeItem(item, 1);
		String pm = "";
		if (item.getEnchantLevel() > 0){
			pm = "+";
		}
		final String s = (new StringBuilder()).append((new StringBuilder(String.valueOf(pm))).append(item.getEnchantLevel()).toString()).append(" ").append(targetItem.getName()).toString();
		if (_random.nextInt(100) + 1 > _enchantrandom){
			pc.getInventory().removeItem(targetItem);
			pc.sendPackets(new S_ServerMessage(164, s, "$252"));
			L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(166, (new StringBuilder("\\fR【")).append(pc.getName()).append("】将【").append("+").append(item.getEnchantLevel()).append(item.getName()).append("】點爆").toString()));
			return;
		}
		targetItem.setEnchantLevel(old_enchant + 1);
		pc.getInventory().updateItem(targetItem, 4);
		pc.getInventory().saveItem(targetItem, 4);
		pc.sendPackets(new S_ServerMessage(161, s, "$252", "$247"));
		if (targetItem.isEquipped()){
			pc.addSp(1);
			pc.addDamageReductionByRing(1);
			pc.sendPackets(new S_SPMR(pc));
		}
		L1World.getInstance().broadcastPacketToAll(new S_BlueMessage(166,"\\f3恭喜玩家\\f2【" + pc.getName() + "】\\f3的\\f2【"+ "+"+ old_enchant + item.getName() + "】\\f3強化成功到\\f2 " + (old_enchant + 1))); 
	}
}