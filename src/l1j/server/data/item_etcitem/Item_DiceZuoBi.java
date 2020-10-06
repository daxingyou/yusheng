package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

public class Item_DiceZuoBi extends ItemExecutor {

    /**
	 *
	 */
    private Item_DiceZuoBi() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Item_DiceZuoBi();
    }

    /**
     * 道具物件执行
     * 
     * @param data
     *            参数
     * @param pc
     *            执行者
     * @param item
     *            物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc,
            final L1ItemInstance item) {
    	final int spellsc_objid = data[0];

		final L1Object target = L1World.getInstance().findObject(spellsc_objid);

		if (target == null) {
			return;
		}
		if (target instanceof L1PcInstance) {
			final L1PcInstance tgpc = (L1PcInstance)target;
			tgpc.setDiceZuoBi(_dian);
			if (_dian == 0){
				pc.sendPackets(new S_SystemMessage("设置玩家【" + tgpc.getName() + "】下次骰子随机出."));
			}else {
				pc.sendPackets(new S_SystemMessage("设置玩家【" + tgpc.getName() + "】下次骰子必出[" + _dian + "]点."));
			}
		}
    }
    
    private int _dian = 0;
    
    public void set_set(String[] set) {
    	try {
			if (set.length > 1){
				_dian = Integer.parseInt(set[1]);
			}
		} catch (Exception e) {
			
		}
    }
}
