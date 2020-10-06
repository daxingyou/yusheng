package l1j.server.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l1j.server.server.datatables.ShouShaTable;
import l1j.server.server.datatables.storage.ShouShaStorage;
import l1j.server.server.templates.L1ShouShaTemp;

public class ShouShaReading {
	private final Lock _lock;

	private final ShouShaStorage _storage;

	private static ShouShaReading _instance;

	private ShouShaReading() {
		this._lock = new ReentrantLock(true);
		this._storage = new ShouShaTable();
	}
	public static ShouShaReading get() {
		if (_instance == null) {
			_instance = new ShouShaReading();
		}
		return _instance;
	}
	
	public void load(){
		this._storage.load();
	}
	
	public boolean update(final L1ShouShaTemp tmp,final int objId,final String name){
		this._lock.lock();
		boolean tmp1 = false;
		try {
			tmp1 = this._storage.update(tmp, objId, name);
		} finally {
			this._lock.unlock();
		}
		return tmp1;
	}
	
	public L1ShouShaTemp getTemp(final int npcId){
		this._lock.lock();
		L1ShouShaTemp tmp = null;
		try {
			tmp = this._storage.getTemp(npcId);
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	public Map<Integer, L1ShouShaTemp> getShouShaMaps() {
		this._lock.lock();
		Map<Integer, L1ShouShaTemp> tmp = null;
		try {
			tmp = this._storage.getShouShaMaps();
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	
	/**
	 * 返回当前怪物没有被首杀剩余数量
	 * @return
	 */
	public int getCount(){
		return this._storage.getCount();
	}
	/**
	 * 返回当前奖励总数
	 * @return
	 */
	public long getAmcount0(){
		return this._storage.getAmcount0();
	}
	/**
	 * 返回当前奖励剩余总数
	 * @return
	 */
	public long getAmcount1(){
		return this._storage.getAmcount1();
	}
}
