package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.datatables.HeallingPotionTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class HealPotion_Item  extends ItemExecutor {

    /**
	 *
	 */
    private HealPotion_Item() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new HealPotion_Item();
    }

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		pc.clearHealHpPotion();
		for(final Integer healItemId : HeallingPotionTable.get().getHealPotionMap().keySet()){
			if (pc.getInventory().checkItem(healItemId, 1)){
				pc.addHealHpPotion(healItemId);
			}
		}
		pc.getAction().action("loadhealpotion",0);
	}
}

