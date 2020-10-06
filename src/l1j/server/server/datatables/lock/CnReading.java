package l1j.server.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l1j.server.server.datatables.CnTable;
import l1j.server.server.datatables.storage.CnStorage;
import l1j.server.server.templates.Tbs;


public class CnReading {

	private final Lock _lock;

	private final CnStorage _storage;

	private static CnReading _instance;

	private CnReading() {
		this._lock = new ReentrantLock(true);
		this._storage = new CnTable();
	}

	public static CnReading get() {
		if (_instance == null) {
			_instance = new CnReading();
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
	
	public boolean isload(){
		this._lock.lock();
		boolean flg = false;
		try {
			flg = this._storage.isload();
			
		} finally {
			this._lock.unlock();
		}
		return flg;
	}
	
	
	public void addMap(final Integer objId, final Tbs overtime) {
		this._lock.lock();
		try {
			this._storage.addMap(objId, overtime);
			
		} finally {
			this._lock.unlock();
		}
	}
	
	public  void addcnMap(final String objId, final Tbs tbs) {
		this._lock.lock();
		try {
			this._storage.addcnMap(objId, tbs);
			
		} finally {
			this._lock.unlock();
		}
	}
	
	public  Tbs getCn(final String objId) {
		this._lock.lock();
		Tbs tbs = null;
		try {
			this._storage.getCn(objId);
			
		} finally {
			this._lock.unlock();
		}
		return tbs;
	}
	public Map<Integer, Tbs> cnmap() {
		this._lock.lock();
		Map<Integer, Tbs> map = new ConcurrentHashMap<Integer, Tbs>();
		try {
			map = this._storage.cnmap();			
		} finally {
			this._lock.unlock();
		}
		return map;
	}
	public Tbs getCnOther(final int key){
		this._lock.lock();
		Tbs otherTmp =null;
		try {
			otherTmp = this._storage.getCnOther(key);
			
		} finally {
			this._lock.unlock();
		}
		return otherTmp;
	}
	
	public Tbs getCnOther(final String name){
		this._lock.lock();
		Tbs otherTmp =null;
		try {
			otherTmp = this._storage.getCnOther(name);
			
		} finally {
			this._lock.unlock();
		}
		return otherTmp;
	}
	
	public void storeCnOther(final String name) {
		this._lock.lock();
		try {
			this._storage.storeCnOther(name);
			
		} finally {
			this._lock.unlock();
		}
	}
	
	public void delCnOther(final Tbs key) {
		this._lock.lock();
		try {
			this._storage.delCnOther(key);
			
		} finally {
			this._lock.unlock();
		}
	}
}
