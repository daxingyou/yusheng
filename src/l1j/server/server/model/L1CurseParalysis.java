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
package l1j.server.server.model;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
// 宠物、召唤怪 
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
// 宠物、召唤怪  end
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ServerMessage;

/*
 * L1ParalysisPoison被多。特。何共通化难。
 */
public class L1CurseParalysis extends L1Paralysis {
	private final L1Character _target;
	private final int _delay;
	private final int _time;
	private final int _timesec;

	private Thread _timer;

	private class ParalysisDelayTimer extends Thread {
		@Override
		public void run() {
			_target.setSkillEffect(L1SkillId.STATUS_CURSE_PARALYZING, 0);

			try {
				Thread.sleep(_delay); // 麻痹犹予时间待。
			} catch (InterruptedException e) {
				_target.killSkillEffectTimer(L1SkillId.STATUS_CURSE_PARALYZING);
				return;
			}

			if (_target instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) _target;
				if (!player.isDead()) {
					player.sendPackets(new S_Paralysis(1,_timesec, true)); // 麻痹状态
				}
			}
			_target.setParalyzed(true);
			_timer = new ParalysisTimer();
			GeneralThreadPool.getInstance().execute(_timer); // 麻痹开始
			if (isInterrupted()) {
				_timer.interrupt();
			}
		}
	}

	private class ParalysisTimer extends Thread {
		@Override
		public void run() {
			_target.killSkillEffectTimer(L1SkillId.STATUS_CURSE_PARALYZING);
			_target.setSkillEffect(L1SkillId.STATUS_CURSE_PARALYZED, 0);
			try {
				Thread.sleep(_time);
			} catch (InterruptedException e) {
			}

			_target.killSkillEffectTimer(L1SkillId.STATUS_CURSE_PARALYZED);
			if (_target instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) _target;
//				if (!player.isDead()) {
					player.sendPackets(new S_Paralysis(1, 0,false)); // 麻痹状态解除
//				}
			}
			_target.setParalyzed(false);
			cure(); // 解咒处理
		}
	}

	private L1CurseParalysis(L1Character cha, int delay, int time) {
		_target = cha;
		_delay = delay;
		_time = time;
		_timesec = time/1000;

		curse();
	}

	private void curse() {
		if (_target instanceof L1PcInstance) {
			L1PcInstance player = (L1PcInstance) _target;
			player.sendPackets(new S_ServerMessage(212));
		}

		_target.setPoisonEffect(2);

		_timer = new ParalysisDelayTimer();
		GeneralThreadPool.getInstance().execute(_timer);
	}

	public static boolean curse(L1Character cha, int delay, int time) {
		if (!(cha instanceof L1PcInstance || 
			  cha instanceof L1MonsterInstance || 
			  cha instanceof L1SummonInstance || 
			  cha instanceof L1PetInstance)) { // 补上宠物、召唤怪 
			return false;
		}
		if (cha.hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZING)
				|| cha.hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZED)) {
			return false; // 既麻痹
		}

		cha.setParalaysis(new L1CurseParalysis(cha, delay, time));
		return true;
	}

	@Override
	public int getEffectId() {
		return 2;
	}

	@Override
	public void cure() {
		if (_timer != null) {
			_timer.interrupt(); // 麻痹解除
		}

		_target.setPoisonEffect(0);
		_target.setParalaysis(null);
	}
}
