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

import static l1j.server.server.model.skill.L1SkillId.THUNDER_GRAB;
import static l1j.server.server.model.skill.L1SkillId.FREEZING_BREATH;
import static l1j.server.server.model.skill.L1SkillId.MAGMA_BREATH;
import static l1j.server.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.AREA_OF_SILENCE;
import static l1j.server.server.model.skill.L1SkillId.CANCELLATION;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_0_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_1_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_2_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_3_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_4_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_5_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_6_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_7_S;
import static l1j.server.server.model.skill.L1SkillId.COUNTER_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.CURSE_PARALYZE;
import static l1j.server.server.model.skill.L1SkillId.DARKNESS;
import static l1j.server.server.model.skill.L1SkillId.DECAY_POTION;
import static l1j.server.server.model.skill.L1SkillId.DISEASE;
import static l1j.server.server.model.skill.L1SkillId.EARTH_BIND;
import static l1j.server.server.model.skill.L1SkillId.ELEMENTAL_FALL_DOWN;
import static l1j.server.server.model.skill.L1SkillId.ENTANGLE;
import static l1j.server.server.model.skill.L1SkillId.ERASE_MAGIC;
import static l1j.server.server.model.skill.L1SkillId.FINAL_BURN;
import static l1j.server.server.model.skill.L1SkillId.FOG_OF_SLEEPING;
import static l1j.server.server.model.skill.L1SkillId.FREEZING_BLIZZARD;
import static l1j.server.server.model.skill.L1SkillId.ICE_LANCE;
import static l1j.server.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
import static l1j.server.server.model.skill.L1SkillId.MANA_DRAIN;
import static l1j.server.server.model.skill.L1SkillId.MASS_SLOW;
import static l1j.server.server.model.skill.L1SkillId.POLLUTE_WATER;
import static l1j.server.server.model.skill.L1SkillId.REDUCTION_ARMOR;
import static l1j.server.server.model.skill.L1SkillId.RETURN_TO_NATURE;
import static l1j.server.server.model.skill.L1SkillId.SHOCK_STUN;
import static l1j.server.server.model.skill.L1SkillId.SILENCE;
import static l1j.server.server.model.skill.L1SkillId.SLOW;
import static l1j.server.server.model.skill.L1SkillId.STRIKER_GALE;
import static l1j.server.server.model.skill.L1SkillId.TAMING_MONSTER;
import static l1j.server.server.model.skill.L1SkillId.TURN_UNDEAD;
import static l1j.server.server.model.skill.L1SkillId.WEAKNESS;
import static l1j.server.server.model.skill.L1SkillId.WEAPON_BREAK;
import static l1j.server.server.model.skill.L1SkillId.WIND_SHACKLE;

import java.util.Random;

import l1j.server.Config;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.lock.SpawnBossReading;
import l1j.server.server.model.Instance.L1BabyInstance;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1HierarchInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.world.L1World;
import l1j.william.L1WilliamSystemMessage;

public class L1Magic {

	private int _calcType;

	private final int PC_PC = 1;

	private final int PC_NPC = 2;

	private final int NPC_PC = 3;

	private final int NPC_NPC = 4;

	private L1PcInstance _pc = null;

	private L1PcInstance _targetPc = null;

	private L1NpcInstance _npc = null;

	private L1NpcInstance _targetNpc = null;

	private L1Character _attacker = null;

	private L1Character _target = null;

	private static final Random _random = new Random();

	private int _leverage = 10; // 1/10倍表现。

	public void setLeverage(int i) {
		_leverage = i;
	}

	private int getLeverage() {
		return _leverage;
	}

	public L1Magic(L1Character attacker, L1Character target) {
		if (attacker instanceof L1PcInstance) {
			if (target instanceof L1PcInstance) {
				_calcType = PC_PC;
				_pc = (L1PcInstance) attacker;
				_targetPc = (L1PcInstance) target;
			} else {
				_calcType = PC_NPC;
				_pc = (L1PcInstance) attacker;
				_targetNpc = (L1NpcInstance) target;
			}
		} else {
			if (target instanceof L1PcInstance) {
				_calcType = NPC_PC;
				_npc = (L1NpcInstance) attacker;
				_targetPc = (L1PcInstance) target;
			} else {
				_calcType = NPC_NPC;
				_npc = (L1NpcInstance) attacker;
				_targetNpc = (L1NpcInstance) target;
			}
		}
		_attacker = attacker;
		_target = target;
	}

	/* ■■■■■■■■■■■■■■■ 魔法共通关数 ■■■■■■■■■■■■■■ */
	/*
	 * private int getSpellPower() { int spellPower = 0; if (_calcType == PC_PC
	 * || _calcType == PC_NPC) { spellPower = _pc.getSp(); } else if (_calcType
	 * == NPC_PC || _calcType == NPC_NPC) { spellPower = _npc.getSp(); } return
	 * spellPower; }
	 */

	private int getMagicLevel() {
		int magicLevel = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			magicLevel = _pc.getMagicLevel();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			magicLevel = _npc.getMagicLevel();
		}
		return magicLevel;
	}

	private int getMagicBonus() {
		int magicBonus = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			magicBonus = _pc.getMagicBonus();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			magicBonus = _npc.getMagicBonus();
		}
		return magicBonus;
	}

	private int getLawful() {
		int lawful = 0;
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			lawful = _pc.getLawful();
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			lawful = _npc.getLawful();
		}
		return lawful;
	}

	private int getTargetMr() {
		int mr = 0;
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			mr = _targetPc.getMr();
		} else {
			mr = _targetNpc.getMr();
		}
		return mr;
	}

	/* ■■■■■■■■■■■■■■ 魔法回避成功判定 ■■■■■■■■■■■■■ */
	// ●●●● 确率系魔法成功判定 ●●●●
	// 计算方法
	// 攻击侧：LV + ((MagicBonus * 3) * 魔法固有系数)
	// 防御侧：((LV / 2) + (MR * 3)) / 2
	// 攻击成功率：攻击侧 - 防御侧
	public boolean calcProbabilityMagic(int skillId) {
		int probability = 0;
		boolean isSuccess = false;

		// 城门
		if ((_targetNpc instanceof L1DoorInstance)
				|| (_targetNpc instanceof L1BabyInstance)
				|| (_targetNpc instanceof L1HierarchInstance)) {
			return false;
		}
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			if (_targetPc.get_evasion() > 0) {
				if (_random.nextInt(100) < _targetPc.get_evasion()) {
					return false;
				}
			}
		}
		// 城门 end

		// 攻击者GM权限场合100%成功
		/*
		 * if (_pc != null && _pc.isGm()) { return true; }
		 */

		if (!checkZone(skillId)) {
			return false;
		}
		if (skillId == CANCELLATION) {
			if (_calcType == PC_PC && _pc != null && _targetPc != null) {
				// 自分自身场合100%成功
				if (_pc.getId() == _targetPc.getId()) {
					return true;
				}
				// 同场合100%失败
				if (_pc.getClanid() > 0
						&& (_pc.getClanid() == _targetPc.getClanid())) {
					if (Config.GM_CANCELLATION_ON) {
						return false;
					} else {
						return true;
					}
				}
				// 同场合100%成功
				if (_pc.getPartyID() > 0
						&& (_pc.getPartyID() == _targetPc.getPartyID())) {
					return true;
				}
				// 以外场合、无佅
				if (_pc.getZoneType() == 1 || _targetPc.getZoneType() == 1) {
					_pc.sendPackets(new S_SystemMessage(L1WilliamSystemMessage
							.ShowMessage(1029))); // 补充讯息
					return false;
				}
			}
			// 象NPC、使用者NPC场合100%成功
			if (_calcType == PC_NPC || _calcType == NPC_PC
					|| _calcType == NPC_NPC) {
				return true;
			}
		}
		if (skillId == SHOCK_STUN) {
			if (_calcType == PC_NPC) {
				if (SpawnBossReading.get().isBoss(_targetNpc.getOldNpcID())) {
					return false;
				}
			}
		}

		// 中WB、以外无佅
		/*
		 * if (_calcType == PC_PC || _calcType == NPC_PC) { if
		 * (_targetPc.hasSkillEffect(EARTH_BIND)) { if (skillId != WEAPON_BREAK
		 * && skillId != CANCELLATION) { return false; } } } else { if
		 * (_targetNpc.hasSkillEffect(EARTH_BIND)) { if (skillId != WEAPON_BREAK
		 * && skillId != CANCELLATION) { return false; } } }
		 */

		probability = calcProbability(skillId);

		Random random = new Random();
		int rnd = random.nextInt(100) + 1;
		if (probability > 90) {
			probability = 90; // 最高成功率90%。
		}

		if (probability >= rnd) {
			isSuccess = true;
		} else {
			isSuccess = false;
		}

		// System.out.println("负面法术攻击者:"+_target.getName());
		L1PinkName.onAction(_target, _attacker);

		// 确率系魔法
		if (!Config.ALT_ATKMSG) {
			return isSuccess;
		}
		if (Config.ALT_ATKMSG) {
			if ((_calcType == PC_PC || _calcType == PC_NPC) && !_pc.isGm()) {
				return isSuccess;
			}
			if ((_calcType == PC_PC || _calcType == NPC_PC)
					&& !_targetPc.isGm()) {
				return isSuccess;
			}
		}

		// String msg0 = "";
		// String msg1 = " 受到 ";
		// String msg2 = "";
		// String msg3 = "";
		// String msg4 = "";

		if (_calcType == PC_PC || _calcType == PC_NPC) { // ＰＣ场合
			// 删msg0 = _pc.getName();
		} else if (_calcType == NPC_PC) { // ＮＰＣ场合
			// 删msg0 = _npc.getName();
		}

		// msg2 = "probability:" + probability + "%";
		// if (_calcType == NPC_PC || _calcType == PC_PC) { // ＰＣ场合
		// msg4 = _targetPc.getName();
		// } else if (_calcType == PC_NPC) { // ＮＰＣ场合
		// msg4 = _targetNpc.getName();
		// }
		// if (isSuccess == true) {
		// msg3 = " 施咒成功";
		// } else {
		// msg3 = " 施咒失败";
		// }

		// if (_calcType == PC_PC || _calcType == PC_NPC) { // ＰＣ场合
		// _pc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2, msg3,
		// msg4)); // \f1%0%4%1%3 %2
		// }
		// if (_calcType == NPC_PC || _calcType == PC_PC) { // ＰＣ场合
		// _targetPc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2,
		// msg3, msg4)); // \f1%0%4%1%3 %2
		// }

		return isSuccess;
	}

	private boolean checkZone(int skillId) {
		if (_pc != null && _targetPc != null) {
			if (_pc.getZoneType() == 1 || _targetPc.getZoneType() == 1) { // 
				if (skillId == WEAPON_BREAK || skillId == SLOW
						|| skillId == CURSE_PARALYZE || skillId == MANA_DRAIN
						|| skillId == DARKNESS || skillId == WEAKNESS
						|| skillId == DISEASE || skillId == DECAY_POTION
						|| skillId == MASS_SLOW || skillId == ENTANGLE
						|| skillId == ERASE_MAGIC || skillId == EARTH_BIND
						|| skillId == AREA_OF_SILENCE
						|| skillId == WIND_SHACKLE || skillId == STRIKER_GALE
						|| skillId == SHOCK_STUN || skillId == FOG_OF_SLEEPING
						|| skillId == ICE_LANCE || skillId == POLLUTE_WATER
						|| skillId == SILENCE) { // 追加沉默
					_pc.sendPackets(new S_SystemMessage(L1WilliamSystemMessage
							.ShowMessage(1029))); // 补充讯息
					return false;
				}
			}
		}
		return true;
	}

	private int calcProbability(int skillId) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
		int attackLevel = 0;
		int defenseLevel = 0;
		int probability = 0;

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			attackLevel = _pc.getLevel();
		} else {
			attackLevel = _npc.getLevel();
		}

		if (_calcType == PC_PC || _calcType == NPC_PC) {
			defenseLevel = _targetPc.getLevel();
		} else {
			defenseLevel = _targetNpc.getLevel();
		}
		if (_calcType == PC_NPC) {
			if (_targetNpc instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) _targetNpc;
				if (summon.getMaster() != null) {
					defenseLevel = summon.getMaster().getLevel();
				}
			}
		}

		if (skillId == ELEMENTAL_FALL_DOWN || skillId == RETURN_TO_NATURE
				|| skillId == ENTANGLE || skillId == ERASE_MAGIC
				|| skillId == AREA_OF_SILENCE || skillId == WIND_SHACKLE
				|| skillId == STRIKER_GALE || skillId == POLLUTE_WATER
				|| skillId == EARTH_BIND) {
			// 成功确率 魔法固有系 × LV差 + 基本确率
			/*
			 * probability = (int) (((l1skills.getProbabilityDice()) / 10D)
			 * (attackLevel - defenseLevel)) + l1skills .getProbabilityValue();
			 */
			final int value3 = l1skills.getProbabilityValue();
			final int sum = attackLevel - defenseLevel;
			probability = value3;
			probability += sum * 20;
			if (probability > 65) {
				probability = 65;
			}
		} else if (skillId == SHOCK_STUN ||  skillId ==  THUNDER_GRAB) {
			final int value4 = l1skills.getProbabilityValue();
			final int sum1 = attackLevel - defenseLevel;
			probability = value4;
			probability += sum1 * 20;
			// 成功确率 基本确率 + LV差1+-1%
			// probability = l1skills.getProbabilityValue() + attackLevel -
			// defenseLevel;
		} else if (skillId == COUNTER_BARRIER) {
			// 成功确率は 基本确率 + LV差1每に+-1%
			probability = l1skills.getProbabilityValue() + attackLevel
					- defenseLevel;
		// 龙骑士魔法
		} else if (skillId == L1SkillId.GUARD_BRAKE) {
			probability = 100;
		// 龙骑士魔法
		} else {
			Random random = new Random();
			int dice = l1skills.getProbabilityDice();
			int diceCount = 0;
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				if (_pc.isWizard()) {
					diceCount = getMagicBonus() + getMagicLevel() + 1;
				} else if (_pc.isElf()) {
					diceCount = getMagicBonus() + getMagicLevel() - 1;
				} else {
					diceCount = getMagicBonus() + getMagicLevel() - 1;
				}
			} else {
				diceCount = getMagicBonus() + getMagicLevel();
			}
			if (diceCount < 1) {
				diceCount = 1;
			}

			for (int i = 0; i < diceCount; i++) {
				probability += (random.nextInt(dice) + 1);
			}
			probability = probability * getLeverage() / 10;

			// 命中率最高149
			if (probability > 149) {
				probability = 149;
			}
			// 命中率最高149 end

			probability -= getTargetMr();

			// 起死回生机率限制
			if (skillId == TURN_UNDEAD && _calcType == PC_NPC) {
				if (_pc.isWizard() && probability > 70) { // 法师
					probability = 70;
				} else if (_pc.isElf()) { // 妖精
					if (probability > 70) {
						probability = 55;
					} else {
						probability /= 2;
					}
				}
			}
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				if (_pc.isElf()) {
					if (skillId <= FREEZING_BLIZZARD) {
						if (probability > 70) {
							probability = 45;
						} else {
							probability /= 2;
						}
					}
				}
			}

			// 起死回生机率限制 end

			if (skillId == TAMING_MONSTER) {
				double probabilityRevision = 1;
				if ((_targetNpc.getMaxHp() * 1 / 4) > _targetNpc.getCurrentHp()) {
					probabilityRevision = 1.3;
				} else if ((_targetNpc.getMaxHp() * 2 / 4) > _targetNpc
						.getCurrentHp()) {
					probabilityRevision = 1.2;
				} else if ((_targetNpc.getMaxHp() * 3 / 4) > _targetNpc
						.getCurrentHp()) {
					probabilityRevision = 1.1;
				}
				if (_targetNpc.isTU()) {
					probabilityRevision = 0;
				}
				probability *= probabilityRevision;
			}
			// 耐性
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				switch (skillId) {
				case EARTH_BIND:// 大地屏障 - 支撑耐性
				 case THUNDER_GRAB: //夺命之雷 
					probability -= (this._targetPc.getRegistSustain() >> 1);
					break;
				case SHOCK_STUN:// 冲击之晕 - 昏迷耐性
				case L1SkillId.BONE_BREAK:// 骷髅毁坏
					probability -= (this._targetPc.getRegistStun() >> 1);
					break;
				case CURSE_PARALYZE:// 木乃伊的诅咒 - 石化耐性
				case L1SkillId.PHANTASM:// 幻想
					probability -= (_targetPc.getRegistStone() >> 1);
					break;
				case FOG_OF_SLEEPING:// 沉睡之雾 - 睡眠耐性
					probability -= (_targetPc.getRegistSleep() >> 1);
					break;
				case ICE_LANCE:// 冰矛围篱 - 寒冰耐性
				case FREEZING_BLIZZARD:// 冰雪飓风
				case L1SkillId.FREEZING_BREATH:// 寒冰喷吐
					probability -= (_targetPc.getRegistFreeze() >> 1);
					break;
				case L1SkillId.CURSE_BLIND:// 闇盲咒术 - 暗黑耐性
				case L1SkillId.DARKNESS:// 黑闇之影
				case L1SkillId.DARK_BLIND:// 暗黑盲咒
					probability -= (_targetPc.getRegistBlind() >> 1);
					break;
				}
			}
		}
		return probability;
	}

	/* ■■■■■■■■■■■■■■ 魔法算出 ■■■■■■■■■■■■■■ */

	public int calcMagicDamage(int skillId) {
		int damage = 0;
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			damage = calcPcMagicDamage(skillId);
		} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
			damage = calcNpcMagicDamage(skillId);
		}

		damage = calcMrDefense(damage);

		// 最大值对像现在HP同。
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			if (damage > _targetPc.getCurrentHp()) {
				damage = _targetPc.getCurrentHp();
			}
		} else {
			if (damage > _targetNpc.getCurrentHp()) {
				damage = _targetNpc.getCurrentHp();
			}
		}
		return damage;
	}

	// ●●●● ＮＰＣ   魔法算出 ●●●●
	private int calcPcMagicDamage(int skillId) {
		int dmg = 0;
		if (skillId == FINAL_BURN) {
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				dmg = _pc.getCurrentMp();
			} else {
				dmg = _npc.getCurrentMp();
			}
		} else {
			dmg = calcMagicDiceDamage(skillId);
			dmg = (dmg * getLeverage()) / 10;
		}

		dmg -= _targetPc.getDamageReductionByArmor(); // 防具轻减

		Random _random = new Random();

		if (_targetPc.getDamageReduction() > 0
				&& _targetPc.getDamageReductionRandom() > 0) {
			if ((_random.nextInt(100) + 1) <= _targetPc
					.getDamageReductionRandom()) {
				_targetPc
						.sendPackets(new S_SkillSound(_targetPc.getId(), 6320));
				_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(),
						6320));
				dmg -= _targetPc.getDamageReduction();
			}
		}

		if (_targetPc.getDamageReductionByDoll() > 0
				&& _targetPc.getDamageReductionRandomByDoll() > 0) {
			if ((_random.nextInt(100) + 1) <= _targetPc
					.getDamageReductionRandomByDoll()) {
				_targetPc
						.sendPackets(new S_SkillSound(_targetPc.getId(), 6320));
				_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(),
						6320));
				dmg -= _targetPc.getDamageReductionByDoll();
			}
		}

		dmg -= _targetPc.getDamageReductionByRing();

		if (_targetPc.hasSkillEffect(COOKING_1_0_S) // 料理减
				|| _targetPc.hasSkillEffect(COOKING_1_1_S)
				|| _targetPc.hasSkillEffect(COOKING_1_2_S)
				|| _targetPc.hasSkillEffect(COOKING_1_3_S)
				|| _targetPc.hasSkillEffect(COOKING_1_4_S)
				|| _targetPc.hasSkillEffect(COOKING_1_5_S)
				|| _targetPc.hasSkillEffect(COOKING_1_6_S)) {
			dmg -= 5;
		}
		if (_targetPc.hasSkillEffect(COOKING_1_7_S)) { // 减
			dmg -= 5;
		}
		if (_targetPc.hasSkillEffect(REDUCTION_ARMOR)) {
			int targetPcLvl = _targetPc.getLevel();
			if (targetPcLvl < 50) {
				targetPcLvl = 50;
			}
			dmg -= (targetPcLvl - 50) / 5 + 1;
		}

		if (_calcType == NPC_PC // 、攻击
				&& (_npc instanceof L1PetInstance || _npc instanceof L1SummonInstance)) {
			// 删除dmg /= 8;
			// 伤害变更
			dmg = 3 + _random.nextInt(3);// 3~5
			// 伤害变更 end
		}
		if (_targetPc.hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg /= 2;
		}
		// 安区宠物对玩家伤害为 0
		if (_calcType == NPC_PC
				&& (_npc instanceof L1PetInstance || _npc instanceof L1SummonInstance)
				&& _targetPc.getZoneType() == 1) {
			dmg = 0;
		}
		// 安区宠物对玩家伤害为 0 end

		if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(ICE_LANCE)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(EARTH_BIND)) {
			dmg = 0;
		}
		// 被龙骑士技能夺命之雷
		if (_targetPc.hasSkillEffect(THUNDER_GRAB)) {
			dmg = 0;
		}
		// 冰冻、地屏状态判断
		if (_targetPc.get_poisonStatus4() == 4
				|| _targetPc.get_poisonStatus6() == 4) {
			dmg = 0;
		}
		// 冰冻、地屏状态判断 end

		// 龙骑士技能 -3 伤害
		if (_targetPc.hasSkillEffect(L1SkillId.DRAGON_SKIN)) {
			dmg -= 5;
			//System.out.println("龙骑士技能伤害-5");
		}
		
		// 龙骑士魔法 (岩浆喷吐)
        if (skillId == MAGMA_BREATH) {
            if ((this._calcType == this.PC_NPC)
                    || (this._calcType == this.NPC_NPC)) {
                dmg += 25 + (_random.nextInt(10) + 1);
            }
        }

        // 龙骑士魔法 (寒冰喷吐)
        if (skillId == FREEZING_BREATH) {
            if ((this._calcType == this.PC_NPC)
                    || (this._calcType == this.NPC_NPC)) {
                dmg += 28 + (_random.nextInt(10) + 1);
            }
        }

		return dmg;
	}

	// ●●●● ＮＰＣ  ＮＰＣ 算出 ●●●●
	private int calcNpcMagicDamage(int skillId) {
		int dmg = 0;
		if (skillId == FINAL_BURN) {
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				dmg = _pc.getCurrentMp();
			} else {
				dmg = _npc.getCurrentMp();
			}
		} else {
			dmg = calcMagicDiceDamage(skillId);
			dmg = (dmg * getLeverage()) / 10;
		}
		// 宠物魔法伤害设定
		if (_calcType == NPC_NPC
				&& ((_npc instanceof L1PetInstance) || (_npc instanceof L1SummonInstance))) {
			Random _random = new Random();
			switch (_npc.getNpcTemplate().get_npcId()) {
			case 45039:
			case 45046:
			case 45047:
			case 45048:
			case 45049:
			case 45692:
			case 45693:
			case 45694:
			case 45695:
			case 45696:
			case 45313:
			case 45710:
			case 45711:
			case 45712:
			case 210001:
			case 210002:
			case 210003:
			case 210004: {
				switch (_npc.getLevel()) {
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
				case 10: {
					dmg = ((5 + (_random.nextInt(5) + 1)) * getLeverage() / 10);// 6~10
				}
					break;
				case 11:
				case 12:
				case 13:
				case 14:
				case 15:
				case 16:
				case 17:
				case 18:
				case 19:
				case 20: {
					dmg = ((5 + (_random.nextInt(10) + 1)) * getLeverage() / 10);// 6~15
				}
					break;
				case 21:
				case 22:
				case 23:
				case 24:
				case 25:
				case 26:
				case 27:
				case 28:
				case 29:
				case 30: {
					dmg = ((5 + (_random.nextInt(15) + 1)) * getLeverage() / 10);// 6~20
				}
					break;
				case 31:
				case 32:
				case 33:
				case 34:
				case 35:
				case 36:
				case 37:
				case 38:
				case 39:
				case 40: {
					dmg = ((5 + (_random.nextInt(20) + 1)) * getLeverage() / 10);// 6~25
				}
					break;
				case 41:
				case 42:
				case 43:
				case 44:
				case 45:
				case 46:
				case 47:
				case 48:
				case 49:
				case 50: {
					dmg = ((10 + (_random.nextInt(15) + 1)) * getLeverage() / 10);// 10~25
				}
					break;
				default: {
					dmg = ((15 + (_random.nextInt(10) + 1)) * getLeverage() / 10);// 15~25
				}
					break;
				}
			}
				break;
			case 81050: { // 强力火之精灵
				dmg = ((25 + (_random.nextInt(10) + 1)) * getLeverage() / 10);// 26~35
			}
				break;
			case 81051: { // 强力水之精灵
				dmg = ((15 + (_random.nextInt(10) + 1)) * getLeverage() / 10);// 16~25
			}
				break;
			case 81052:
			case 81053: { // 强力风之精灵、强力土之精灵
				dmg = ((20 + (_random.nextInt(10) + 1)) * getLeverage() / 10);// 21~30
			}
				break;
			case 81097: { // 高仑熔岩怪
				dmg = ((20 + (_random.nextInt(10) + 1)) * getLeverage() / 10);// 21~30
			}
				break;
			case 81101:
			case 81102: { // 烈炎兽、亚力安
				dmg = ((25 + (_random.nextInt(10) + 1)) * getLeverage() / 10);// 26~35
			}
				break;
			case 81103: { // 变形怪首领
				dmg = ((31 + (_random.nextInt(10) + 1)) * getLeverage() / 10);// 31~40
			}
				break;
			}
		}
		// 宠物魔法伤害设定 end

		// 怪物圣结界
		if (_targetNpc.hasSkillEffect(17004)) {
			dmg /= 2;
		}
		// 怪物圣结界 end
		
		// 龙骑士魔法 (岩浆喷吐)
        if (skillId == MAGMA_BREATH) {
            if ((this._calcType == this.PC_NPC)
                    || (this._calcType == this.NPC_NPC)) {
                dmg += 25 + (_random.nextInt(10) + 1);
            }
        }

        // 龙骑士魔法 (寒冰喷吐)
        if (skillId == FREEZING_BREATH) {
            if ((this._calcType == this.PC_NPC)
                    || (this._calcType == this.NPC_NPC)) {
                dmg += 28 + (_random.nextInt(10) + 1);
            }
        }

		/*
		 * if (_calcType == PC_NPC // 、攻击 && (_targetNpc
		 * instanceof L1PetInstance || _targetNpc instanceof L1SummonInstance))
		 * { dmg /= 8; }
		 */

		// 安区宠物对玩家伤害为 0
		if (_calcType == NPC_NPC
				&& (_npc instanceof L1PetInstance || _npc instanceof L1SummonInstance)
				&& _targetNpc.getZoneType() == 1
				&& (_targetNpc instanceof L1PetInstance || _targetNpc instanceof L1SummonInstance)) {
			dmg = 0;
		}
		// 安区宠物对玩家伤害为 0 end

		if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
			dmg = 0;
		}
		// 龙骑士技能 夺命之雷
//		if (_targetPc.hasSkillEffect(THUNDER_GRAB)) {
//			dmg = 0;
//		}
		// 冰冻、地屏状态判断
		if (_targetNpc.get_poisonStatus4() == 4
				|| _targetNpc.get_poisonStatus6() == 4) {
			dmg = 0;
		}
		// 冰冻、地屏状态判断 end
		
		// 如果目标是BOSS 则魔法伤害 * 2 
		if (SpawnBossReading.get().isBoss(_targetNpc.getOldNpcID())) {
			dmg += dmg * 1.5;
		}

		// 怪死公告
		if (_calcType == PC_NPC && _targetNpc.getCurrentHp() <= dmg
				&& _targetNpc.getNpcTemplate().getBroad() == true) {
			L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

			String attack_name = _pc.getName(); // 玩家名称
			String target_name = _targetNpc.getName(); // 怪物名称
			String skill_name = skill.getName(); // 魔法名称
			String msg0 = L1WilliamSystemMessage.ShowMessage(1105); // 恭喜
			String msg1 = L1WilliamSystemMessage.ShowMessage(1106); // 使用
			String msg2 = L1WilliamSystemMessage.ShowMessage(1107); // 杀死了
			L1World.getInstance().broadcastPacketToAll(
					new S_ServerMessage(166,
							msg0 + " ( " + attack_name + " ) ", msg1
									+ skill_name, msg2 + " (" + target_name
									+ ")"));
		}
		if (_calcType == NPC_NPC && _targetNpc.getCurrentHp() <= dmg
				&& _targetNpc.getNpcTemplate().getBroad() == true) {
			L1PcInstance master = null;
			if (_npc instanceof L1SummonInstance) { // 召唤兽
				master = (L1PcInstance) ((L1SummonInstance) _npc).getMaster();
			} else if (_npc instanceof L1PetInstance) { // 宠物
				master = (L1PcInstance) ((L1PetInstance) _npc).getMaster();
			}
			L1Skills skill = SkillsTable.getInstance().getTemplate(skillId);

			String master_name = ""; // 主人名称
			if (master != null) {
				master_name = master.getName();
			}
			String attack_name = _npc.getName(); // 怪物名称
			String target_name = _targetNpc.getName(); // 怪物名称
			String skill_name = skill.getName(); // 魔法名称
			String msg0 = L1WilliamSystemMessage.ShowMessage(1105); // 恭喜
			String msg1 = L1WilliamSystemMessage.ShowMessage(1106); // 使用
			String msg2 = L1WilliamSystemMessage.ShowMessage(1107); // 杀死了
			L1World.getInstance().broadcastPacketToAll(
					new S_ServerMessage(166,
							msg0 + " ( " + master_name + " ) ",
							L1WilliamSystemMessage.ShowMessage(1124)
									+ attack_name + msg1 + skill_name, msg2
									+ " (" + target_name + ")"));
		}
		// 怪死公告 end

		return dmg;
	}

	// ●●●● damage_dice、damage_dice_count、damage_value、SP魔法算出 ●●●●
	private int calcMagicDiceDamage(int skillId) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
		int dice = l1skills.getDamageDice();
		int diceCount = l1skills.getDamageDiceCount();
		int value = l1skills.getDamageValue();
		int magicDamage = 0;
		int charaIntelligence = 0;
		Random random = new Random();

		for (int i = 0; i < diceCount; i++) {
			magicDamage += (random.nextInt(dice) + 1);
		}
		magicDamage += value;

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			int weaponAddDmg = 0; // 武器追加
			L1ItemInstance weapon = _pc.getWeapon();
			if (weapon != null) {
				weaponAddDmg = weapon.getItem().getMagicDmgModifier();
			}
			magicDamage += weaponAddDmg;
		}

		if (_calcType == PC_PC || _calcType == PC_NPC) {
			int spByItem = _pc.getSp() - _pc.getTrueSp(); // SP变动
			charaIntelligence = _pc.getInt() + spByItem - 12;
		} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
			charaIntelligence = _npc.getInt() - 12;
		}
		if (charaIntelligence < 1) {
			charaIntelligence = 1;
		}

		double attrDeffence = calcAttrResistance(l1skills.getAttr());

		double coefficient = (1.0 - attrDeffence + charaIntelligence * 3.0 / 32.0);
		if (coefficient < 0) {
			coefficient = 0;
		}

		magicDamage *= coefficient;

		return magicDamage;
	}

	// ●●●● 回复量（对）算出 ●●●●
	public int calcHealing(int skillId) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillId);
		int dice = l1skills.getDamageDice();
		int value = l1skills.getDamageValue();
		int magicDamage = 0;

		int magicBonus = getMagicBonus();
		if (magicBonus > 10) {
			magicBonus = 10;
		}

		Random random = new Random();
		int diceCount = value + magicBonus;
		for (int i = 0; i < diceCount; i++) {
			magicDamage += (random.nextInt(dice) + 1);
		}

		double alignmentRevision = 1.0;
		if (getLawful() > 0) {
			alignmentRevision += (getLawful() / 32768.0);
		}

		magicDamage *= alignmentRevision;

		magicDamage = (magicDamage * getLeverage()) / 10;

		return magicDamage;
	}

	// ●●●● ＭＲ轻减 ●●●●
	private int calcMrDefense(int dmg) {
		int mr = getTargetMr();
		Random random = new Random();
		int rnd = random.nextInt(100) + 1;
		if (mr >= rnd) {
			dmg /= 2;
		}

		return dmg;
	}

	// ●●●● 属性轻减 ●●●●
	// attr:0.无属性魔法,1.地魔法,2.火魔法,4.水魔法,8.风魔法(,16.光魔法)
	private double calcAttrResistance(int attr) {
		int resist = 0;
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			if (attr == L1Skills.ATTR_EARTH) {
				resist = _targetPc.getEarth();
			} else if (attr == L1Skills.ATTR_FIRE) {
				resist = _targetPc.getFire();
			} else if (attr == L1Skills.ATTR_WATER) {
				resist = _targetPc.getWater();
			} else if (attr == L1Skills.ATTR_WIND) {
				resist = _targetPc.getWind();
			}
		} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
			if (attr == L1Skills.ATTR_EARTH) {
				resist = _targetNpc.getEarth();
			} else if (attr == L1Skills.ATTR_FIRE) {
				resist = _targetNpc.getFire();
			} else if (attr == L1Skills.ATTR_WATER) {
				resist = _targetNpc.getWater();
			} else if (attr == L1Skills.ATTR_WIND) {
				resist = _targetNpc.getWind();
			}
		}

		int resistFloor = (int) (0.32 * Math.abs(resist));
		if (resist >= 0) {
			resistFloor *= 1;
		} else {
			resistFloor *= -1;
		}

		double attrDeffence = resistFloor / 32.0;

		return attrDeffence;
	}

	/* ■■■■■■■■■■■■■■■ 计算结果反映 ■■■■■■■■■■■■■■■ */

	public void commit(int damage, int drainMana) {
		if (_calcType == PC_PC || _calcType == NPC_PC) {
			commitPc(damage, drainMana);
		} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
			commitNpc(damage, drainMana);
		}
		if (_calcType == PC_PC || _calcType == PC_NPC) { // ＰＣ场合
			if (_pc.isVdmg()) {
				if (damage == 0) {
					_pc.sendPackets(new S_SkillSound(_target.getId(), 17050));
				} else {
					int units = damage % 10;
					int tens = (damage / 10) % 10;
					int hundreads = (damage / 100) % 10;
					int thousands = (damage / 1000) % 10;
					int tenthousands = (damage / 10000) % 10;
					if (units > 0 || tens > 0 || hundreads > 0 || thousands > 0
							|| tenthousands > 0) {
						if (units == 0) {
							units = 1;
						}
						units += 12060;
						_pc.sendPackets(new S_SkillSound(_target.getId(), units));
					}
					if (tens > 0 || hundreads > 0 || thousands > 0
							|| tenthousands > 0) {
						tens += 12070;
						_pc.sendPackets(new S_SkillSound(_target.getId(), tens));
					}
					if (hundreads > 0 || thousands > 0 || tenthousands > 0) {
						hundreads += 12080;
						_pc.sendPackets(new S_SkillSound(_target.getId(),
								hundreads));
					}
					if (thousands > 0 || tenthousands > 0) {
						thousands += 12090;
						_pc.sendPackets(new S_SkillSound(_target.getId(),
								thousands));
					}
					if (tenthousands > 0) {
						tenthousands += 12100;
						_pc.sendPackets(new S_SkillSound(_target.getId(),
								tenthousands));
					}
				}
			}
		}
		// 值及命中率确认用
		if (!Config.ALT_ATKMSG) {
			return;
		}
		if (Config.ALT_ATKMSG) {
			if ((_calcType == PC_PC || _calcType == PC_NPC) && !_pc.isGm()) {
				return;
			}
			if ((_calcType == PC_PC || _calcType == NPC_PC)
					&& !_targetPc.isGm()) {
				return;
			}
		}

		String msg0 = "";
		String msg1 = " 受到 ";
		String msg2 = "";
		String msg3 = "";
		String msg4 = "";

		if (_calcType == PC_PC || _calcType == PC_NPC) {// ＰＣ场合
			// 删msg0 = _pc.getName();
		} else if (_calcType == NPC_PC) { // ＮＰＣ场合
			// 删msg0 = _npc.getName();
		}

		if (_calcType == NPC_PC || _calcType == PC_PC) { // ＰＣ场合
			msg4 = _targetPc.getName();
			msg2 = " 剩下 " + _targetPc.getCurrentHp();
		} else if (_calcType == PC_NPC) { // ＮＰＣ场合
			msg4 = _targetNpc.getName();
			msg2 = " 剩下 " + _targetNpc.getCurrentHp();
		}
		msg3 = damage + " 伤害";

		if (_calcType == PC_PC || _calcType == PC_NPC) { // ＰＣ场合
			_pc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2, msg3,
					msg4)); // \f1%0%4%1%3 %2
		}
		if (_calcType == NPC_PC || _calcType == PC_PC) { // ＰＣ场合
			_targetPc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2,
					msg3, msg4)); // \f1%0%4%1%3 %2
		}
	}

	// ●●●● 计算结果反映 ●●●●
	private void commitPc(int damage, int drainMana) {
		if (_calcType == PC_PC) {
			if (drainMana > 0 && _targetPc.getCurrentMp() > 0) {
				if (drainMana > _targetPc.getCurrentMp()) {
					drainMana = _targetPc.getCurrentMp();
				}
				int newMp = _pc.getCurrentMp() + drainMana;
				_pc.setCurrentMp(newMp);
			}
			_targetPc.receiveManaDamage(_pc, drainMana);
			_targetPc.receiveDamage(_pc, damage,false);
		} else if (_calcType == NPC_PC) {
			_targetPc.receiveDamage(_npc, damage,false);
		}
	}

	// ●●●● ＮＰＣ计算结果反映 ●●●●
	private void commitNpc(int damage, int drainMana) {
		if (_calcType == PC_NPC) {
			if (drainMana > 0) {
				int drainValue = _targetNpc.drainMana(drainMana);
				int newMp = _pc.getCurrentMp() + drainValue;
				_pc.setCurrentMp(newMp);
			}
			_targetNpc.ReceiveManaDamage(_pc, drainMana);
			_targetNpc.receiveDamage(_pc, damage);
		} else if (_calcType == NPC_NPC) {
			_targetNpc.receiveDamage(_npc, damage);
		}
	}
}
