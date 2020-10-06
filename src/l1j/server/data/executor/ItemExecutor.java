package l1j.server.data.executor;

import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

/**
 * 道具物件执行
 */
public abstract class ItemExecutor {

	/**
	 * 道具物件执行
	 * @param data 参数
	 * @param pc 执行者
	 * @param item 物件
	 */
	public abstract void execute(int[] data, L1PcInstance pc, L1ItemInstance item);

	public String[] get_set() {
		return null;
	}

	public void set_set(String[] set) {
	}
}
