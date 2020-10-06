package l1j.server.server.datatables.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l1j.server.server.datatables.CharBookConfigTable;
import l1j.server.server.datatables.storage.CharBookConfigStorage;
import l1j.server.server.templates.L1BookConfig;

/** 角色记忆座标操作纪录 */
public class CharBookConfigReading {
	private final Lock _lock;
	private final CharBookConfigStorage _stg;
	private static CharBookConfigReading _ins;

	private CharBookConfigReading() {
		this._lock = new ReentrantLock(true);
		this._stg = new CharBookConfigTable();
	}

	public static CharBookConfigReading get() {
		if (_ins == null) {
			_ins = new CharBookConfigReading();
		}
		return _ins;
	}

	/** 初始化载入 */
	public void load() {
		this._lock.lock();
		try {
			this._stg.load();
		} finally {
			this._lock.unlock();
		}
	}

	/** 传回角色 L1BookConfig */
	public L1BookConfig get(final int objId) {
		this._lock.lock();
		L1BookConfig tmp;
		try {
			tmp = this._stg.get(objId);
		} finally {
			this._lock.unlock();
		}
		return tmp;
	}

	/** 新建角色 L1BookConfig */
	public void storeCharacterBookConfig(final int objId, final byte[] data) {
		this._lock.lock();
		try {
			this._stg.storeCharacterBookConfig(objId, data);
		} finally {
			this._lock.unlock();
		}
	}

	/** 更新角色 L1BookConfig */
	public void updateCharacterConfig(final int objId, final byte[] data) {
		this._lock.lock();
		try {
			this._stg.updateCharacterConfig(objId, data);
		} finally {
			this._lock.unlock();
		}
	}
}
