package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class AttackPotion extends ItemExecutor{

	/**
	 *
	 */
	private AttackPotion() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new AttackPotion();
	}
	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		// TODO 自动生成的方法存根
		
	}

}
