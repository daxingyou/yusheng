package l1j.server.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l1j.server.server.datatables.BalanceTable;
import l1j.server.server.datatables.storage.BalanceStorage;

public class BalanceReading {
	
	private final Lock _lock;

	private final BalanceStorage _storage;

	private static BalanceReading _instance;

	private BalanceReading() {
		this._lock = new ReentrantLock(true);
		this._storage = new BalanceTable();
	}

	public static BalanceReading get() {
		if (_instance == null) {
			_instance = new BalanceReading();
		}
		return _instance;
	}

	/**
	 * 初始化载入
	 */
	public void load() {
		this._lock.lock();
		try {
			this._storage.load();
			
		} finally {
			this._lock.unlock();
		}
	}

}
