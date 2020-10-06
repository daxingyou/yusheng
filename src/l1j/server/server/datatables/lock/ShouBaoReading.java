package l1j.server.server.datatables.lock;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l1j.server.server.datatables.ShouBaoTable;
import l1j.server.server.datatables.storage.ShouBaoStorage;
import l1j.server.server.templates.L1ShouBaoTemp;

public class ShouBaoReading {
	private final Lock _lock;

	private final ShouBaoStorage _storage;

	private static ShouBaoReading _instance;

	private ShouBaoReading() {
		this._lock = new ReentrantLock(true);
		this._storage = new ShouBaoTable();
	}
	public static ShouBaoReading get() {
		if (_instance == null) {
			_instance = new ShouBaoReading();
		}
		return _instance;
	}
	
	public void load(){
		this._storage.load();
	}
	
	public boolean update(final L1ShouBaoTemp tmp,final int objId,final String name){
		this._lock.lock();
		boolean tmp1 = false;
		try {
			tmp1 = this._storage.update(tmp, objId, name);
		} finally {
			this._lock.unlock();
		}
		return tmp1;
	}
	
	public L1ShouBaoTemp getTemp(final int npcId){
		//this._lock.lock();
		L1ShouBaoTemp tmp = null;
		try {
			tmp = this._storage.getTemp(npcId);
		} finally {
			//this._lock.unlock();
		}
		return tmp;
	}
	public Map<Integer,L1ShouBaoTemp> getShouBaoMaps() {
		//this._lock.lock();
		Map<Integer,L1ShouBaoTemp> tmp = null;
		try {
			tmp = this._storage.getShouBaoMaps();
		} finally {
			//this._lock.unlock();
		}
		return tmp;
		
	}
	
	/**
	 * 返回当前物品没有被首爆剩余数量
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
