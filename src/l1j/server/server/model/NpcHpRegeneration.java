package l1j.server.server.model;

import l1j.server.server.model.Instance.L1NpcInstance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NpcHpRegeneration {
	
	private static final Log _log = LogFactory.getLog(NpcHpRegeneration.class);

	
	private final int _time;
	
	private int _sec = 0;
	
	private int _hpr = 0;
	
	private L1NpcInstance _npc = null;

	public NpcHpRegeneration(L1NpcInstance npc,int hprtime, int hpr) {
		_time = hprtime;
		_hpr = hpr;
		_npc = npc;
	}
	
	public void keephpr(){
		try {
			if (_npc.isDead()) {
				return;
			}

			_sec++;

			if (_time <= _sec) {
				_sec = 0;
				regenHp();
			}
		} catch (Throwable e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		
	}

	private void regenHp() {
		int newHp = _npc.getCurrentHp() + _hpr;
		if (newHp < 0) {
			newHp = 0;
		}
		_npc.setCurrentMp(newHp);
	}

}
