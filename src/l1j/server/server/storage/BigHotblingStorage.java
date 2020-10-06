package l1j.server.server.storage;

import l1j.server.server.templates.L1BigHotbling;

public interface BigHotblingStorage {

	/**
	 * 新四星彩赔率建立
	 * @param id 场次编号
	 * @param number 开奖号码
	 * @param totalPrice 该场累积赌金
	 * @param count 该场中奖票数
	 */
	public void create(int id, String number, int totalPrice, int money1, int count, int money2, int count1, int money3, int count2, int count3);

	/**
	 * 载入旧赌场纪录
	 */
	public void load();

	/**
	 * 传回全部赌场纪录资料
	 * @return
	 */
	public L1BigHotbling[] getBigHotblingList();

	/**
	 * 传回指定赌场纪录资料(id)
	 * @param id
	 * @return
	 */
	public L1BigHotbling getBigHotbling(int id);

}
