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

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;

public class L1DamagePoison extends L1Poison {
	private Thread _timer;
	private final L1Character _attacker;
	private final L1Character _target;
	private final int _damageSpan;
	private final int _damage;
	private final int _time;

	private L1DamagePoison(L1Character attacker, L1Character cha,
			int damageSpan,int time, int damage) {
		_attacker = attacker;
		_target = cha;
		_damageSpan = damageSpan;
		_damage = damage;
		_time = time;

		doInfection();
	}

	private class NormalPoisonTimer extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					//System.out.println("开始中毒");
					Thread.sleep(_damageSpan);
				} catch (InterruptedException e) {
					break;
				}
				if (!_target.hasSkillEffect(L1SkillId.STATUS_POISON)) {
					break;
				}

				if (_target instanceof L1PcInstance) {
					L1PcInstance player = (L1PcInstance) _target;
					player.receiveDamage(_attacker, _damage,false);
					if (player.isDead()) { // 死亡解毒处理
						//System.out.println("中毒玩家死亡");
						break;
					}
				} else if (_target instanceof L1MonsterInstance) {
					L1MonsterInstance mob = (L1MonsterInstance) _target;
					mob.receiveDamage(_attacker, _damage);
					if (mob.isDead()) { // 死亡解毒
						//System.out.println("中毒怪物死亡");
						return;
					}
				}
			}
			//System.out.println("解毒处理开始");
			cure(); // 解毒处理
		}
	}

	boolean isDamageTarget(L1Character cha) {
		return (cha instanceof L1PcInstance)
				|| (cha instanceof L1MonsterInstance);
	}

	private void doInfection() {
		_target.setSkillEffect(L1SkillId.STATUS_POISON, _time);
		_target.setPoisonEffect(1);

		if (isDamageTarget(_target)) {
			_timer = new NormalPoisonTimer();
			GeneralThreadPool.getInstance().execute(_timer); // 通常毒开始
		}
	}

	public static boolean doInfection(L1Character attacker, L1Character cha,
			int damageSpan,int time, int damage) {
		if (!isValidTarget(cha)) {
			return false;
		}

		cha.setPoison(new L1DamagePoison(attacker, cha, damageSpan,time, damage));
		return true;
	}

	@Override
	public int getEffectId() {
		return 1;
	}

	@Override
	public void cure() {
		if (_timer != null) {
			_timer.interrupt(); // 毒解除
		}

		_target.setPoisonEffect(0);
		_target.killSkillEffectTimer(L1SkillId.STATUS_POISON);
		_target.setPoison(null);
		//System.out.println("解毒完毕！");
	}
}
