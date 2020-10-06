/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.model.poison;

import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1TowerInstance;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ServerMessage;

// Referenced classes of package l1j.server.server.model:
// 沉睡效果

public class L1Poison5 {
	private L1Object _l1object = null;
	private L1PcInstance _player = null;
	private L1MonsterInstance _mob = null;
	private L1TowerInstance _tower = null;
	private L1NpcInstance _npc = null;
	private int _targetId = 0;
	private int _time;
	private int _timesec;
	private int _damage;
	private boolean _isPoisonTimerDelete = false;
	private boolean _isParalysisPoisonTimerDelete = false;

	private NormalPoisonTimer _normal_poison_timer;
	private ParalysisPoisonTimer _paralysis_poison_timer;
	private ParalysisTimer _paralysis_timer;


	private static L1Poison5 _instance;

	public L1Poison5() {
	}

	public static L1Poison5 getInstance() {
		if (_instance == null) {
			_instance = new L1Poison5();
		}
		return _instance;
	}

	class NormalPoisonTimer implements Runnable {
		public NormalPoisonTimer(L1Object l1object, int time, int damage) {
			_l1object = l1object;
			_time = time;
			_timesec = time/1000;
			_damage = damage;
		}

		@Override
		public void run() {
			for (;;) {
				try {
					Thread.sleep(_time);
				} catch (Exception exception) {
					break;
				}
				if (_isPoisonTimerDelete) {
					return;
				}
				if (_l1object instanceof L1PcInstance) {
					_player = (L1PcInstance) _l1object;
					_player.receiveDamage((L1Character) _l1object, _damage,false);
					if (_player.isDead()) {
						break;
					}
				}
				else if (_l1object instanceof L1MonsterInstance) {
					_mob = (L1MonsterInstance) _l1object;
					_mob.receiveDamage((L1Character) _l1object, _damage);
					if (_mob.isDead()) {
						return;
					}
				}
			}
			CurePoison(_l1object);
		}
	}

	class ParalysisPoisonTimer implements Runnable {
		public ParalysisPoisonTimer(L1Object l1object, int time) {
			_l1object = l1object;
			_time = time;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(10); //前置时间 0.01秒
			} catch (Exception exception) {
			}
			if (_isParalysisPoisonTimerDelete) {
				return;
			}
			if (_l1object instanceof L1PcInstance) {
				_player = (L1PcInstance) _l1object;
				if (_player.isDead() == false) {
					_targetId = _player.getId();
					_player.sendPackets(new S_Paralysis(3, _timesec,true));
					_paralysis_timer = new ParalysisTimer(_l1object, _time);
					GeneralThreadPool.getInstance().execute(_paralysis_timer);
				}
			}
			
			if (_l1object instanceof L1MonsterInstance) {
				_mob = (L1MonsterInstance) _l1object;
				if (_mob.isDead() == false) {
					_targetId = _mob.getId();
					_paralysis_timer = new ParalysisTimer(_l1object, _time);
					GeneralThreadPool.getInstance().execute(_paralysis_timer);
				}
			}
		}
	}

	class ParalysisTimer implements Runnable {
		public ParalysisTimer(L1Object l1object, int time) {
			_l1object = l1object;
			_time = time;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(_time);
			} catch (Exception exception) {
			}
			if (_l1object instanceof L1PcInstance) {
				_player = (L1PcInstance) _l1object;
//				if (_player.isDead() == false) {
					_player.sendPackets(new S_Paralysis(3,0, false));
					CurePoison(_l1object);
//				}
			}
			
			if (_l1object instanceof L1MonsterInstance) {
				_mob = (L1MonsterInstance) _l1object;
				if (_mob.isDead() == false) {
					CurePoison(_l1object);
				}
			}
		}
	}

	public boolean handleCommands(L1Object l1object, int type, int time,
			int damage) {
	
		if (l1object == null) {
			return false;
		}

		if (l1object instanceof L1PcInstance) {
			_player = (L1PcInstance) l1object;
			_targetId = _player.getId();
			if (_player.get_poisonStatus5() != 0) {
				return false;
			}
			_player.set_poisonStatus5(type);
		}
		else if (l1object instanceof L1MonsterInstance) {
			_mob = (L1MonsterInstance) l1object;
			_targetId = _mob.getId();
			if (_mob.get_poisonStatus5() != 0) {
				return false;
			}
			_mob.set_poisonStatus5(type);
		}
		else if (l1object instanceof L1TowerInstance) {
			type = 1;
			_tower = (L1TowerInstance) l1object;
			_targetId = _tower.getId();
			_tower.set_poisonStatus5(type);
			return true;
		}
		else {
			type = 1;
			_npc = (L1NpcInstance) l1object;
			_targetId = _npc.getId();
			_npc.set_poisonStatus5(type);
			return true;
		}

		if (type == 1) {
			_normal_poison_timer =
					new NormalPoisonTimer(l1object, time, damage);
			GeneralThreadPool.getInstance().execute(_normal_poison_timer);
		}
		else if (type == 4) {
			_paralysis_poison_timer =
					new ParalysisPoisonTimer(l1object, time);
			GeneralThreadPool.getInstance().execute(_paralysis_poison_timer);
		}
		return true;
	}

	public void CurePoison(L1Object l1object) {
		if (l1object instanceof L1PcInstance) {
			_player = (L1PcInstance) l1object;
			_targetId = _player.getId();

			if (_player.get_poisonStatus5() == 1) {
				_isPoisonTimerDelete = true;
			}
			else if (_player.get_poisonStatus5() == 4) {
				_isParalysisPoisonTimerDelete = true;
			}
			else {
				return;
			}
			_player.set_poisonStatus5(0);
		}
		else if (l1object instanceof L1MonsterInstance) {
			_mob = (L1MonsterInstance) l1object;
			_targetId = _mob.getId();

			if (_mob.get_poisonStatus5() == 1)
			{
				_isPoisonTimerDelete = true;
			}
			else if (_mob.get_poisonStatus5() == 4) {
				_isParalysisPoisonTimerDelete = true;
			}
			else {
				return;
			}
			_mob.set_poisonStatus5(0);
		}
		else if (l1object instanceof L1TowerInstance) {
			_tower = (L1TowerInstance) l1object;
			_targetId = _tower.getId();
			_tower.set_poisonStatus5(0);
		}
		else {
			_npc = (L1NpcInstance) l1object;
			_targetId = _npc.getId();
			_npc.set_poisonStatus5(0);
		}
	}
}
