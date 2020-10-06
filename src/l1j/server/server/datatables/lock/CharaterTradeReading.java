package l1j.server.server.datatables.lock;

import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l1j.server.server.datatables.CharaterTradeTable;
import l1j.server.server.datatables.storage.CharaterTradeStorage;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1CharaterTrade;

public class CharaterTradeReading {
	private final Lock _lock;

	private final CharaterTradeStorage _storage;

	private static CharaterTradeReading _instance;

	private CharaterTradeReading() {
		this._lock = new ReentrantLock(true);
		this._storage = new CharaterTradeTable();
	}

	public static CharaterTradeReading get() {
		if (_instance == null) {
			_instance = new CharaterTradeReading();
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
	
	/**获取所有角色交易*/
	public Collection<L1CharaterTrade> getAllCharaterTradeValues(){
		return this._storage.getAllCharaterTradeValues();
	}
	
	/**获取编号*/
	public int get_nextId(){
		this._lock.lock();
		int tmp = -1;
		try {
			tmp = this._storage.get_nextId();
			
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	
	/**增加角色交易*/
	public boolean addCharaterTrade(final L1CharaterTrade charaterTrade){
		return this._storage.addCharaterTrade(charaterTrade);
	}
	
	/**
	 * 更新角色交易
	 * <br>
	 * state 0:普通 1:已交易未领取 2:已交易已领取 3:已撤销
	 */
	public void updateCharaterTrade(final L1CharaterTrade charaterTrade,final int state){
		this._lock.lock();
		try {
			this._storage.updateCharaterTrade(charaterTrade, state);
		} finally {
			this._lock.unlock();
		}
	}
	
	/**
	 * 加载当前账号内的所有角色不包括自己
	 */
	public void loadCharacterName(final L1PcInstance pc){
		this._storage.loadCharacterName(pc);
	}
	
	/**
	 * 更新人物的绑定状态
	 * @param objId
	 * @return
	 */
	public boolean updateBindChar(final int objId,final int state){
		return this._storage.updateBindChar(objId,state);
	}
	
	/**
	 * 获取交易的简易人物
	 * @param objId
	 * @return
	 */
	public L1PcInstance getPcInstance(final int objId){
		this._lock.lock();
		L1PcInstance tmp = null;
		try {
			tmp = this._storage.getPcInstance(objId);
			
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}
	
	public L1CharaterTrade getCharaterTrade(final int id){
		return this._storage.getCharaterTrade(id);
	}
	
	public void updateCharAccountName(final int objId,final String accountName){
		this._storage.updateCharAccountName(objId, accountName);
	}

}
