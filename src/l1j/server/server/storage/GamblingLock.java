package l1j.server.server.storage;

import java.util.concurrent.locks.ReentrantLock;

import l1j.server.server.storage.mysql.MySqlGamblingStorage;
import l1j.server.server.templates.L1Gambling;

/**
 * SQL DB character_gambling(Lock)
 * @author dexc
 *
 */
public class GamblingLock {
	
	private static GamblingLock _instance;
	
	private final GamblingStorage _gamSt;

	private final ReentrantLock _lock;
	
	public static GamblingLock create() {
		if (_instance == null) {
			_instance = new GamblingLock();
		}
		return _instance;
	}

	public GamblingLock() {
		_gamSt = new MySqlGamblingStorage();
		_lock = new ReentrantLock(true);
	}
	
	/**
	 * 资料载入
	 */
	public void load() {
		_gamSt.load();
	}
	
	/**
	 * 新赌场赔率建立
	 * @param id 场次编号
	 * @param npcid 优胜者
	 * @param rate 赔率
	 * @param totalPrice 该场累积赌金
	 */
	public void create(int id, int npcid, double rate, int totalPrice) {
		_lock.lock();
		try {
			_gamSt.create(id, npcid, rate, totalPrice);
		} finally {
			_lock.unlock();
		}
	}

	/**
	 * 传回全部赌场纪录资料
	 * @return
	 */
	public L1Gambling[] getGamblingList() {
		return _gamSt.getGamblingList();
	}

	/**
	 * 传回指定赌场纪录资料(id)
	 * @param id
	 * @return
	 */
	public L1Gambling getGambling(int id) {
		return _gamSt.getGambling(id);
	}
}
