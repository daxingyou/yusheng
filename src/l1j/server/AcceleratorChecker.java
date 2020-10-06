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

package l1j.server;

/*import java.util.EnumMap;
*/

import l1j.server.server.datatables.SprTable;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;

/**
 * 加速器の使用をチェックするクラス。
 */
public class AcceleratorChecker {

	private final L1PcInstance _pc;

	private int move_injusticeCount;

	private int move_justiceCount;

	private static final int INJUSTICE_COUNT_LIMIT = Config.INJUSTICE_COUNT;

	private static final int JUSTICE_COUNT_LIMIT = Config.JUSTICE_COUNT;

	// 实际には移动と攻击のパケット间隔はsprの理论值より5%ほど迟い。
	// それを考虑して-5としている。
	private static double CHECK_STRICTNESS = (Config.CHECK_STRICTNESS - 5) / 100D;
	
	private static double CHECK_MOVESTRICTNESS = (Config.CHECK_MOVE_STRICTNESS - 5) / 100D;

	private static final double HASTE_RATE = 0.745;

	private static final double WAFFLE_RATE = 0.874;

	private int moveresult = R_OK;

	private long movenow = 0;

	private long moveinterval = 0;

	private int moverightInterval = 0;

	private int attackresult = R_OK;

	private long attacknow = 0;

	private long attackinterval = 0;

	private int attackrightInterval = 0;
	
	private int skillresult = R_OK;

	private long skillnow = 0;

	private long skillinterval = 0;

	private int skillrightInterval = 0;

/*	private final EnumMap<ACT_TYPE, Long> _actTimers = new EnumMap<ACT_TYPE, Long>(
			ACT_TYPE.class);

	private final EnumMap<ACT_TYPE, Long> _checkTimers = new EnumMap<ACT_TYPE, Long>(
			ACT_TYPE.class);*/

/*	public static enum ACT_TYPE {
		MOVE, ATTACK, SPELL_DIR, SPELL_NODIR
	}*/
	
	private long actmove;
	
	private long actattack;
	
	private long actskill;

	public static final int R_OK = 0;

	public static final int R_DETECTED = 1;// 纪录

	public static final int R_DISPOSED = 2;// 执行

	public AcceleratorChecker(L1PcInstance pc) {
		_pc = pc;
		move_injusticeCount = 0;
		move_justiceCount = 0;
		long now = System.currentTimeMillis();
		actattack = actmove = actskill = now;
/*		for (ACT_TYPE each : ACT_TYPE.values()) {
			_actTimers.put(each, now);
			_checkTimers.put(each, now);
		}*/
	}

	/**
	 * 断开用户
	 */
	public static void Setspeed() {
		CHECK_STRICTNESS = (Config.CHECK_STRICTNESS - 5) / 100D;
		CHECK_MOVESTRICTNESS = (Config.CHECK_MOVE_STRICTNESS - 5) / 100D;
	}

	/**
	 * 检查是否行动是不合法间隔、做合适处理。
	 * 
	 * @param type
	 *            - 检查行动方式
	 * @return 没有问题0、非法场合1、不正动作达到一定回数切断玩家连线2。
	 */
/*	public int checkInterval(ACT_TYPE type) {

		switch (type) {
		case MOVE:
			movenow = System.currentTimeMillis();
			moveinterval = movenow - _actTimers.get(type);
			moverightInterval = getRightInterval(type);
			moveinterval *= CHECK_MOVESTRICTNESS;
			if (0 < moveinterval && moveinterval < moverightInterval) {
				move_injusticeCount++;
				move_justiceCount = 0;
				if (move_injusticeCount >= INJUSTICE_COUNT_LIMIT) {
					moveresult = R_DISPOSED;
				} else {
					moveresult = R_DETECTED;
				}
			} else if (moveinterval >= moverightInterval) {
				move_justiceCount++;
				if (move_justiceCount >= JUSTICE_COUNT_LIMIT) {
					move_injusticeCount = 0;
					move_justiceCount = 0;
				}
				moveresult = R_OK;
			}
			_actTimers.put(type, movenow);
			return moveresult;
		default:
			attacknow = System.currentTimeMillis();
			attackinterval = attacknow - _actTimers.get(type);

			attackrightInterval = getRightInterval(type);
			attackinterval *= CHECK_STRICTNESS;
			if (0 < attackinterval && attackinterval < attackrightInterval) {
				attackresult = R_DISPOSED;
			} else if (attackinterval >= attackrightInterval) {
				attackresult = R_OK;
			} else {
				attackresult = R_DISPOSED;
			}
			_actTimers.put(type, attacknow);
			return attackresult;
		}
	}*/
	
	public int checkIntervalmove() {
		movenow = System.currentTimeMillis();
		moveinterval = movenow - actmove;
		moverightInterval = getRightIntervalMove();
		moveinterval *= CHECK_MOVESTRICTNESS;
		if (0 < moveinterval && moveinterval < moverightInterval) {
			move_injusticeCount++;
			move_justiceCount = 0;
			if (move_injusticeCount >= INJUSTICE_COUNT_LIMIT) {
				moveresult = R_DISPOSED;
			} else {
				moveresult = R_DETECTED;
			}
		} else if (moveinterval >= moverightInterval) {
			move_justiceCount++;
			if (move_justiceCount >= JUSTICE_COUNT_LIMIT) {
				move_injusticeCount = 0;
				move_justiceCount = 0;
			}
			moveresult = R_OK;
		}
		actmove = movenow;
		return moveresult;
	}
	
	public int checkIntervalattack() {
		attacknow = System.currentTimeMillis();
		attackinterval = attacknow - actattack;

		attackrightInterval = getRightIntervalAttack();
		attackinterval *= CHECK_STRICTNESS;
		if (0 < attackinterval && attackinterval < attackrightInterval) {
			attackresult = R_DISPOSED;
		} else if (attackinterval >= attackrightInterval) {
			attackresult = R_OK;
		} else {
			attackresult = R_DISPOSED;
		}
		actattack = attacknow;
		return attackresult;
	}
	
	public int checkIntervalskilldir() {
		skillnow = System.currentTimeMillis();
		skillinterval = skillnow - actskill;

		skillrightInterval = getRightIntervalSkillDir();
		skillinterval *= CHECK_STRICTNESS;
		if (0 < skillinterval && skillinterval < skillrightInterval) {
			skillresult = R_DISPOSED;
		} else if (skillinterval >= skillrightInterval) {
			skillresult = R_OK;
		} else {
			skillresult = R_DISPOSED;
		}
		actskill = skillnow;
		return skillresult;
	}
	
	public int checkIntervalskillnodir(){
		skillnow = System.currentTimeMillis();
		skillinterval = skillnow - actskill;

		skillrightInterval = getRightIntervalSkillNoDir();
		skillinterval *= CHECK_STRICTNESS;
		if (0 < skillinterval && skillinterval < skillrightInterval) {
			skillresult = R_DISPOSED;
		} else if (skillinterval >= skillrightInterval) {
			skillresult = R_OK;
		} else {
			skillresult = R_DISPOSED;
		}
		actskill = skillnow;
		return skillresult;
	}

	/**
	 * PCの状态から指定された种类のアクションの正しいインターバル(ms)を计算し、返す。
	 * 
	 * @param type - アクションの种类
	 * @param _pc - 调べるPC
	 * @return 正しいインターバル(ms)
	 */
/*	private int getRightInterval(ACT_TYPE type) {
		int interval;

		// 动作判断
		switch (type) {
		case ATTACK:
			interval = SprTable.get().getAttackSpeed(_pc.getTempCharGfx(),
					_pc.getCurrentWeapon() + 1);
			break;
		case MOVE:
			interval = SprTable.get().getMoveSpeed(_pc.getTempCharGfx(),
					_pc.getCurrentWeapon());
			break;
		case SPELL_DIR:
			interval = SprTable.get().getDirSpellSpeed(_pc.getTempCharGfx());
			break;
		case SPELL_NODIR:
			interval = SprTable.get().getNodirSpellSpeed(_pc.getTempCharGfx());
			break;
		default:
			return 10000000;
		}
		// 一段加速
		switch (_pc.getMoveSpeed()) {
		case 1: // 加速术
			interval *= HASTE_RATE;
			break;
		case 2: // 缓速术
			interval /= HASTE_RATE;
			break;
		default:
			break;
		}

		// 二段加速
		switch (_pc.getBraveSpeed()) {
		case 1: // 勇水
			interval *= HASTE_RATE; // 攻速、移速 * 1.33倍
			break;
		case 3: // 精灵饼干 / 人物速度 x1.15(2段加速)
			interval *= WAFFLE_RATE; // 移速 * 1.15倍
			break;
		case 4: // 神圣疾走、风之疾走、行走加速 (移速 * 1.33倍)
			if (type.equals(ACT_TYPE.MOVE)) {
				interval *= HASTE_RATE;
			}
			break;
		default:
			break;
		}
		// 风之枷锁 攻速or施法速度 /2倍
		if (_pc.hasSkillEffect(L1SkillId.WIND_SHACKLE)
				&& !type.equals(ACT_TYPE.MOVE)) {
			interval *= 2;
		}

		return interval;
	}*/
	
	private int getRightIntervalMove() {
		int interval = SprTable.get().getMoveSpeed(_pc.getTempCharGfx(),
				_pc.getCurrentWeapon());

		// 动作判断
/*		switch (type) {
		case ATTACK:
			interval = SprTable.get().getAttackSpeed(_pc.getTempCharGfx(),
					_pc.getCurrentWeapon() + 1);
			break;
		case MOVE:
			interval = SprTable.get().getMoveSpeed(_pc.getTempCharGfx(),
					_pc.getCurrentWeapon());
			break;
		case SPELL_DIR:
			interval = SprTable.get().getDirSpellSpeed(_pc.getTempCharGfx());
			break;
		case SPELL_NODIR:
			interval = SprTable.get().getNodirSpellSpeed(_pc.getTempCharGfx());
			break;
		default:
			return 10000000;
		}*/
		// 一段加速
		switch (_pc.getMoveSpeed()) {
		case 1: // 加速术
			interval *= HASTE_RATE;
			break;
		case 2: // 缓速术
			interval /= HASTE_RATE;
			break;
		default:
			break;
		}

		// 二段加速
		switch (_pc.getBraveSpeed()) {
		case 1: // 勇水
			interval *= HASTE_RATE; // 攻速、移速 * 1.33倍
			break;
		case 3: // 精灵饼干 / 人物速度 x1.15(2段加速)
			interval *= WAFFLE_RATE; // 移速 * 1.15倍
			break;
		case 4: // 神圣疾走、风之疾走、行走加速 (移速 * 1.33倍)
			interval *= HASTE_RATE;
			break;
		default:
			break;
		}
		// 风之枷锁 攻速or施法速度 /2倍
/*		if (_pc.hasSkillEffect(L1SkillId.WIND_SHACKLE)
				&& !type.equals(ACT_TYPE.MOVE)) {
			interval *= 2;
		}*/

		return interval;
	}
	private int getRightIntervalAttack() {
		int interval = SprTable.get().getAttackSpeed(_pc.getTempCharGfx(),
				_pc.getCurrentWeapon() + 1);

		// 动作判断
/*		switch (type) {
		case ATTACK:
			interval = SprTable.get().getAttackSpeed(_pc.getTempCharGfx(),
					_pc.getCurrentWeapon() + 1);
			break;
		case MOVE:
			interval = SprTable.get().getMoveSpeed(_pc.getTempCharGfx(),
					_pc.getCurrentWeapon());
			break;
		case SPELL_DIR:
			interval = SprTable.get().getDirSpellSpeed(_pc.getTempCharGfx());
			break;
		case SPELL_NODIR:
			interval = SprTable.get().getNodirSpellSpeed(_pc.getTempCharGfx());
			break;
		default:
			return 10000000;
		}*/
		// 一段加速
		switch (_pc.getMoveSpeed()) {
		case 1: // 加速术
			interval *= HASTE_RATE;
			break;
		case 2: // 缓速术
			interval /= HASTE_RATE;
			break;
		default:
			break;
		}

		// 二段加速
		switch (_pc.getBraveSpeed()) {
		case 1: // 勇水
			interval *= HASTE_RATE; // 攻速、移速 * 1.33倍
			break;
		case 3: // 精灵饼干 / 人物速度 x1.15(2段加速)
			interval *= WAFFLE_RATE; // 移速 * 1.15倍
			break;
/*		case 4: // 神圣疾走、风之疾走、行走加速 (移速 * 1.33倍)
			if (type.equals(ACT_TYPE.MOVE)) {
				interval *= HASTE_RATE;
			}
			break;*/
		default:
			break;
		}
		// 风之枷锁 攻速or施法速度 /2倍
		if (_pc.hasSkillEffect(L1SkillId.WIND_SHACKLE)
				) {
			interval *= 2;
		}

		return interval;
	}
	private int getRightIntervalSkillDir() {
		int interval = SprTable.get().getDirSpellSpeed(_pc.getTempCharGfx());

		// 动作判断
/*		switch (type) {
		case ATTACK:
			interval = SprTable.get().getAttackSpeed(_pc.getTempCharGfx(),
					_pc.getCurrentWeapon() + 1);
			break;
		case MOVE:
			interval = SprTable.get().getMoveSpeed(_pc.getTempCharGfx(),
					_pc.getCurrentWeapon());
			break;
		case SPELL_DIR:
			interval = SprTable.get().getDirSpellSpeed(_pc.getTempCharGfx());
			break;
		case SPELL_NODIR:
			interval = SprTable.get().getNodirSpellSpeed(_pc.getTempCharGfx());
			break;
		default:
			return 10000000;
		}*/
		// 一段加速
		switch (_pc.getMoveSpeed()) {
		case 1: // 加速术
			interval *= HASTE_RATE;
			break;
		case 2: // 缓速术
			interval /= HASTE_RATE;
			break;
		default:
			break;
		}

		// 二段加速
		switch (_pc.getBraveSpeed()) {
		case 1: // 勇水
			interval *= HASTE_RATE; // 攻速、移速 * 1.33倍
			break;
		case 3: // 精灵饼干 / 人物速度 x1.15(2段加速)
			interval *= WAFFLE_RATE; // 移速 * 1.15倍
			break;
/*		case 4: // 神圣疾走、风之疾走、行走加速 (移速 * 1.33倍)
			if (type.equals(ACT_TYPE.MOVE)) {
				interval *= HASTE_RATE;
			}
			break;*/
		default:
			break;
		}
		// 风之枷锁 攻速or施法速度 /2倍
		if (_pc.hasSkillEffect(L1SkillId.WIND_SHACKLE)
			/*	&& !type.equals(ACT_TYPE.MOVE)*/) {
			interval *= 2;
		}

		return interval;
	}
	private int getRightIntervalSkillNoDir() {
		int interval = SprTable.get().getNodirSpellSpeed(_pc.getTempCharGfx());

		// 动作判断
/*		switch (type) {
		case ATTACK:
			interval = SprTable.get().getAttackSpeed(_pc.getTempCharGfx(),
					_pc.getCurrentWeapon() + 1);
			break;
		case MOVE:
			interval = SprTable.get().getMoveSpeed(_pc.getTempCharGfx(),
					_pc.getCurrentWeapon());
			break;
		case SPELL_DIR:
			interval = SprTable.get().getDirSpellSpeed(_pc.getTempCharGfx());
			break;
		case SPELL_NODIR:
			interval = SprTable.get().getNodirSpellSpeed(_pc.getTempCharGfx());
			break;
		default:
			return 10000000;
		}*/
		// 一段加速
		switch (_pc.getMoveSpeed()) {
		case 1: // 加速术
			interval *= HASTE_RATE;
			break;
		case 2: // 缓速术
			interval /= HASTE_RATE;
			break;
		default:
			break;
		}

		// 二段加速
		switch (_pc.getBraveSpeed()) {
		case 1: // 勇水
			interval *= HASTE_RATE; // 攻速、移速 * 1.33倍
			break;
		case 3: // 精灵饼干 / 人物速度 x1.15(2段加速)
			interval *= WAFFLE_RATE; // 移速 * 1.15倍
			break;
/*		case 4: // 神圣疾走、风之疾走、行走加速 (移速 * 1.33倍)
			if (type.equals(ACT_TYPE.MOVE)) {
				interval *= HASTE_RATE;
			}
			break;*/
		default:
			break;
		}
		// 风之枷锁 攻速or施法速度 /2倍
		if (_pc.hasSkillEffect(L1SkillId.WIND_SHACKLE)
				/*&& !type.equals(ACT_TYPE.MOVE)*/) {
			interval *= 2;
		}

		return interval;
	}
}
