package l1j.server.server.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.model.Instance.L1NpcInstance;

public class NpcMpRegeneration {
	
	private static final Log _log = LogFactory.getLog(NpcMpRegeneration.class);

	
	private final int _time;
	
	private int _sec = 0;
	
	private int _mpr = 0;
	
	private L1NpcInstance _npc = null;

	public NpcMpRegeneration(L1NpcInstance npc,int mprtime, int mpr) {
		_time = mprtime;
		_mpr = mpr;
		_npc = npc;
	}
	
	public void keepmpr(){
		try {
			if (_npc.isDead()) {
				return;
			}
			if (_mpr == 0) {
				return;
			}
			if (_time == 0) {
				return;
			}

			_sec++;

			if (_time <= _sec) {
				_sec = 0;
				regenMp();
			}
		} catch (Throwable e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		
	}

	private void regenMp() {
		int newMp = _npc.getCurrentMp() + _mpr;
		if (newMp < 0) {
			newMp = 0;
		}
		_npc.setCurrentMp(newMp);
	}

}
