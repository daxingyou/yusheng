package l1j.server.server.datatables.lock;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l1j.server.server.datatables.CharacterAdenaTradeTable;
import l1j.server.server.datatables.storage.CharacterAdenaTradeStorage;
import l1j.server.server.templates.L1CharacterAdenaTrade;

public class CharacterAdenaTradeReading {
	private final Lock _lock;

	private final CharacterAdenaTradeStorage _storage;

	private static CharacterAdenaTradeReading _instance;

	private CharacterAdenaTradeReading() {
		this._lock = new ReentrantLock(true);
		this._storage = new CharacterAdenaTradeTable();
	}

	public static CharacterAdenaTradeReading get() {
		if (_instance == null) {
			_instance = new CharacterAdenaTradeReading();
		}
		return _instance;
	}

	/**
	 * 预先加载
	 */
	public void load(){
		this._lock.lock();
		try {
			this._storage.load();
		} finally {
			this._lock.unlock();
		}
	}
	
	/**
	 * 创建一个新的交易
	 * @param adenaTrade
	 */
	public boolean createAdenaTrade(final L1CharacterAdenaTrade adenaTrade){
		this._lock.lock();
		boolean tmp = false;
		try {
			tmp = this._storage.createAdenaTrade(adenaTrade);
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	
	/**
	 * 更新交易状态
	 * @param id
	 * @param over
	 * @return
	 */
	public boolean updateAdenaTrade(final int id,final int over){
		this._lock.lock();
		boolean tmp = false;
		try {
			tmp = this._storage.updateAdenaTrade(id, over);
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	/**
	 * 获取最大交易流水号
	 * @return
	 */
	public int nextId(){
		this._lock.lock();
		int tmp = -1;
		try {
			tmp = this._storage.nextId();
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	/**
	 * 获取所有金币交易清单
	 * @return
	 */
	public Map<Integer, L1CharacterAdenaTrade> getAdenaTrades(){
		this._lock.lock();
		Map<Integer, L1CharacterAdenaTrade> tmp = null;
		try {
			tmp = this._storage.getAdenaTrades();
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	/**
	 * 获取所有交易清单
	 * @return
	 */
	public Collection<L1CharacterAdenaTrade> getAllCharacterAdenaTrades(){
		this._lock.lock();
		Collection<L1CharacterAdenaTrade> tmp = null;
		try {
			tmp = this._storage.getAllCharacterAdenaTrades();
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	/**
	 * 根据流水号获取出售的金币
	 * @param id
	 * @return
	 */
	public L1CharacterAdenaTrade getCharacterAdenaTrade(final int id){
		this._lock.lock();
		L1CharacterAdenaTrade tmp = null;
		try {
			tmp = this._storage.getCharacterAdenaTrade(id);
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
}
