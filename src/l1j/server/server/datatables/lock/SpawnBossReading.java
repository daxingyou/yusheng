package l1j.server.server.datatables.lock;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l1j.server.server.datatables.SpawnBossTable;
import l1j.server.server.datatables.storage.SpawnBossStorage;
import l1j.server.server.model.L1Spawn;

/**
 * BOSS召唤资料
 *
 * @author dexc
 *
 */
public class SpawnBossReading {

	private final Lock _lock;

	private final SpawnBossStorage _storage;

	private static SpawnBossReading _instance;

	private SpawnBossReading() {
		this._lock = new ReentrantLock(true);
		this._storage = new SpawnBossTable();
	}

	public static SpawnBossReading get() {
		if (_instance == null) {
			_instance = new SpawnBossReading();
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
	
	/**
	 * 更新资料库 下次召唤时间纪录
	 * @param id
	 * @param spawnTime
	 */
	public void upDateNextSpawnTime( final int id, final Calendar spawnTime) {
		this._lock.lock();
		try {
			this._storage.upDateNextSpawnTime(id, spawnTime);
			
		} finally {
			this._lock.unlock();
		}
	}
	
	/**
	 * BOSS召唤列表中物件
	 * @param key
	 * @return
	 */
	public L1Spawn getTemplate(final int key) {
		this._lock.lock();
		L1Spawn tmp = null;
		try {
			tmp = this._storage.getTemplate(key);
			
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	
	/**
	 * BOSS召唤列表中物件(NPCID)
	 * @param key NPCID
	 * @return
	 */
	public boolean isBoss(final int key) {
		switch (key) {
		case 45681:// 林德拜尔
		case 45682:// 安塔瑞斯
		case 45683:// 法利昂
		case 45684:// 巴拉卡斯
			
/*		case 85008:// 巴风特
			
		case 91161:// 纪元水龙的影像
		case 91159:// 纪元地龙的影像
		case 91160:// 纪元火龙的影像
		case 91162:// 纪元风龙的影像
			
		case 71014:// 新安塔瑞斯(1阶段)
		case 71015:// 新安塔瑞斯(2阶段)
		case 71016:// 新安塔瑞斯(3阶段)
			
		case 71026:// 新法利昂(1阶段)
		case 71027:// 新法利昂(2阶段)
		case 71028:// 新法利昂(3阶段)
			
		case 92033:// 地龙(走私)
		case 92034:// 火龙(走私)
		case 92035:// 水龙(走私)
		case 92036:// 风龙(走私)
		case 92037:// 法利昂-3阶段(走私)
		case 92038:// 新安塔瑞斯-3阶段(走私)
*/			return true;
		}
		this._lock.lock();
		boolean tmp = false;
		try {
			tmp = this._storage.isBoss(key);
			
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	
	
	/**
	 * BOSS召唤列表中物件(NPCID)
	 * @return _bossId
	 */
	public List<Integer> bossIds() {
		this._lock.lock();
		List<Integer> tmp = null;
		try {
			tmp = this._storage.bossIds();
			
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
}
