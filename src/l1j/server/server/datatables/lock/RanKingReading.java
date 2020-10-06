package l1j.server.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l1j.server.server.datatables.RanKingTable;
import l1j.server.server.datatables.storage.RanKingStorage;

public class RanKingReading {
	private final Lock _lock;

	private final RanKingStorage _storage;

	private static RanKingReading _instance;

	private RanKingReading() {
		this._lock = new ReentrantLock(true);
		this._storage = new RanKingTable();
	}

	public static RanKingReading get() {
		if (_instance == null) {
			_instance = new RanKingReading();
		}
		return _instance;
	}
	
	/**
	 * 返回充值排行数据
	 * @return
	 */
	public String[] getEzpayRankingData() {
		this._lock.lock();
		String[] tmp = null;
		try {
			tmp = this._storage.getEzpayRankingData();
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	
	/**
	 * 0 王族 1 骑士 2妖精 3黑妖 4法师 5龙骑士
	 * @param type
	 * @return
	 */
	public String[] getLevelRankingData(final int type) {
		this._lock.lock();
		String[] tmp = null;
		try {
			tmp = this._storage.getLevelRankingData(type);
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	/**
	 * 所有职业排行
	 * @return
	 */
	public String[] getAllLevelRankingData() {
		this._lock.lock();
		String[] tmp = null;
		try {
			tmp = this._storage.getAllLevelRankingData();
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	/**
	 * 返回武器排行
	 * @return
	 */
	public String[] getWeaponRankingData() {
		this._lock.lock();
		String[] tmp = null;
		try {
			tmp = this._storage.getWeaponRankingData();
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	
	/**
	 * 返回防具排行
	 * @return
	 */
	public String[] getArmorRankingData() {
		this._lock.lock();
		String[] tmp = null;
		try {
			tmp = this._storage.getArmorRankingData();
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	/**
	 * PK排行
	 * @return
	 */
	public String[] getPKRankingData(){
		this._lock.lock();
		String[] tmp = null;
		try {
			tmp = this._storage.getPKRankingData();
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	
	/**
	 * 死亡排行
	 * @return
	 */
	public String[] getDeathRankingData(){
		this._lock.lock();
		String[] tmp = null;
		try {
			tmp = this._storage.getDeathRankingData();
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
}
