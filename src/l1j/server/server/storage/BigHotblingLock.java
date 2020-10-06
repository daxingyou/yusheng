package l1j.server.server.storage;

import java.util.concurrent.locks.ReentrantLock;

import l1j.server.server.storage.mysql.MySqlBigHotblingStorage;
import l1j.server.server.templates.L1BigHotbling;

public class BigHotblingLock {
	
	private static BigHotblingLock _instance;
	
	private final BigHotblingStorage _BigHotSt;

	private final ReentrantLock _lock;
	
	public static BigHotblingLock create() {
		if (_instance == null) {
			_instance = new BigHotblingLock();
		}
		return _instance;
	}

	public BigHotblingLock() {
		_BigHotSt = new MySqlBigHotblingStorage();
		_lock = new ReentrantLock(true);
	}
	
	/**
	 * 资料载入
	 */
	public void load() {
		_BigHotSt.load();
	}
	
	/**
	 * 四星彩建立
	 * @param id 场次编号
	 * @param number 开奖号码
	 * @param totalPrice 该场累积赌金
	 * @param count 该场中奖票数
	 */
	public void create(int id, String number, int totalPrice, int money1, int count, int money2, int count1, int money3, int count2, int count3) {
		_lock.lock();
		try {
			_BigHotSt.create(id, number, totalPrice, money1, count, money2, count1, money3, count2, count3);
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 传回全部赌场纪录资料
	 * @return
	 */
	public L1BigHotbling[] getBigHotblingList() {
		return _BigHotSt.getBigHotblingList();
	}

	/**
	 * 传回指定赌场纪录资料(id)
	 * @param id
	 * @return
	 */
	public L1BigHotbling getBigHotbling(int id) {
		return _BigHotSt.getBigHotbling(id);
	}
}
