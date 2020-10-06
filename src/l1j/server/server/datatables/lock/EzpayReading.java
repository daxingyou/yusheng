package l1j.server.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l1j.server.server.datatables.EzpayTable;
import l1j.server.server.datatables.storage.EzpayStorage;

/**
 * 網站購物資料
 *
 * @author dexc
 *
 */
public class EzpayReading {

	private final Lock _lock;

	private final EzpayStorage _storage;

	private static EzpayReading _instance;

	private EzpayReading() {
		this._lock = new ReentrantLock(true);
		this._storage = new EzpayTable();
	}

	public static EzpayReading get() {
		if (_instance == null) {
			_instance = new EzpayReading();
		}
		return _instance;
	}
	/**
	 * 傳回指定帳戶匯款資料
	 * @param loginName 帳號名稱
	 * @param type 1:大陆  0:台湾
	 * @return
	 */
	public Map<Integer, int[]> ezpayInfo(final String loginName,final int type) {
		this._lock.lock();
		Map<Integer, int[]> tmp = null;
		try {
			tmp = this._storage.ezpayInfo(loginName,type);
			
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	/**
	 * 傳回指定帳戶宣传币資料
	 * @param loginName 帳號名稱
	 * @return
	 */
	public Map<Integer, int[]> XuanChuanInfo(final String loginName) {
		this._lock.lock();
		Map<Integer, int[]> tmp = null;
		try {
			tmp = this._storage.XuanChuanInfo(loginName);
			
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	/**
	 * 更新資料
	 * @param loginName 帳號名稱
	 * @param id ID
	 * @param pcname 領取人物
	 * @param ip IP
	 * @param type 1:大陆  0:台湾
	 */
	public boolean update(final String loginName, final int id, final String pcname, final String ip,final int type) {
		this._lock.lock();
		boolean tmp = false;
		try {
			tmp = this._storage.update(loginName, id, pcname, ip,type);
			
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	
	/**
	 * 更新資料
	 * @param loginName 帳號名稱
	 * @param id ID
	 * @param pcname 領取人物
	 * @param ip IP
	 */
	public boolean updateXuanChuan(final String loginName, final int id, final String pcname, final String ip) {
		this._lock.lock();
		boolean tmp = false;
		try {
			tmp = this._storage.updateXuanChuan(loginName, id, pcname, ip);
			
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
}