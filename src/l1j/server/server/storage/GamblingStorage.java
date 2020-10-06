package l1j.server.server.storage;

import l1j.server.server.templates.L1Gambling;

/**
 * SQL DB character_gambling(Storage)
 * @author dexc
 *
 */
public interface GamblingStorage {

	/**
	 * 新赌场赔率建立
	 * @param id 场次编号
	 * @param npcid 优胜者
	 * @param rate 赔率
	 * @param totalPrice 该场累积赌金
	 */
	public void create(int id, int npcid, double rate, int totalPrice);

	/**
	 * 载入旧赌场纪录
	 */
	public void load();

	/**
	 * 传回全部赌场纪录资料
	 * @return
	 */
	public L1Gambling[] getGamblingList();

	/**
	 * 传回指定赌场纪录资料(id)
	 * @param id
	 * @return
	 */
	public L1Gambling getGambling(int id);

}
