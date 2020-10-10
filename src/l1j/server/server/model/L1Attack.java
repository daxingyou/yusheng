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

import static l1j.server.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.BERSERKERS;
import static l1j.server.server.model.skill.L1SkillId.BOUNCE_ATTACK;
import static l1j.server.server.model.skill.L1SkillId.BURNING_SPIRIT;
import static l1j.server.server.model.skill.L1SkillId.BURNING_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_0_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_1_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_2_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_3_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_4_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_5_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_6_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_7_S;
import static l1j.server.server.model.skill.L1SkillId.COUNTER_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.DOUBLE_BRAKE;
import static l1j.server.server.model.skill.L1SkillId.EARTH_BIND;
import static l1j.server.server.model.skill.L1SkillId.ELEMENTAL_FIRE;
import static l1j.server.server.model.skill.L1SkillId.ENCHANT_VENOM;
import static l1j.server.server.model.skill.L1SkillId.FIRE_BLESS;
import static l1j.server.server.model.skill.L1SkillId.FIRE_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.ICE_LANCE;
import static l1j.server.server.model.skill.L1SkillId.IMMUNE_TO_HARM;
import static l1j.server.server.model.skill.L1SkillId.REDUCTION_ARMOR;
import static l1j.server.server.model.skill.L1SkillId.SOUL_OF_FLAME;
import static l1j.server.server.model.skill.L1SkillId.STORM_EYE;
import static l1j.server.server.model.skill.L1SkillId.STORM_SHOT;
import static l1j.server.server.model.skill.L1SkillId.UNCANNY_DODGE;
import static l1j.william.New_Id.Item_AJ_32;
import static l1j.william.New_Id.Item_AJ_33;
import static l1j.william.New_Id.Item_AJ_34;
import static l1j.william.New_Id.Item_AJ_35;
import static l1j.william.New_Id.Item_AJ_36;
import static l1j.william.New_Id.Item_AJ_37;
import static l1j.william.New_Id.Skill_AJ_0_4;
import static l1j.william.New_Id.Skill_AJ_1_2;
import static l1j.william.New_Id.Weapon_AJ_1_2;
import static l1j.william.New_Id.Weapon_AJ_1_3;
import static l1j.william.New_Id.Weapon_AJ_1_4;
import static l1j.william.New_Id.Weapon_AJ_1_5;
import static l1j.william.New_Id.Weapon_AJ_1_6;
import static l1j.william.New_Id.Weapon_AJ_1_7;

import java.util.Calendar;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.WeaponEnchantDmgTable;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.gametime.L1GameTimeClock;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.poison.L1ParalysisPoison;
import l1j.server.server.model.poison.L1Poison7;
import l1j.server.server.model.poison.L1SilencePoison;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_AttackPacketNpc;
import l1j.server.server.serverpackets.S_AttackPacketPc;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_EffectLocation;
import l1j.server.server.serverpackets.S_PacketBoxDk;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_UseArrowSkill;
import l1j.server.server.types.Point;
import l1j.server.server.world.L1World;
import l1j.william.L1WilliamSystemMessage;
import l1j.william.L1WilliamWeaponSkill;

public class L1Attack {

	private L1PcInstance _pc = null;

	private L1Character _target = null;

	private L1PcInstance _targetPc = null;

	private L1NpcInstance _npc = null;

	private L1NpcInstance _targetNpc = null;

	private final int _targetId;

	private int _targetX;

	private int _targetY;

	private int _statusDamage = 0;

	private static final Random _random = new Random();

	private int _hitRate = 0;

	private int _calcType;

	private static final int PC_PC = 1;

	private static final int PC_NPC = 2;

	private static final int NPC_PC = 3;

	private static final int NPC_NPC = 4;

	private boolean _isHit = false;

	private int _damage = 0;

	private int _drainMana = 0;

	private int _weaponAttrEnchantKind = 0;

	private int _weaponAttrEnchantLevel = 0;

	// private int _attckGrfxId = 0;

	private int _attckActId = 0;

	// 攻击者场合武器情报
	private L1ItemInstance weapon = null;

	private L1ItemInstance target_weapon = null;// 反击屏障判断

	private int _weaponId = 0;

	private int _weaponType = 0;

	private int _weaponAddHit = 0;

	private int _weaponAddDmg = 0;

	private int _weaponSmall = 0;

	private int _weaponLarge = 0;

	private int _weaponBless = 1;

	private int _weaponRange = 1;

	private int _weaponEnchant = 0;

	private int _safeenchant = 0;

	private int _weaponMaterial = 0;

	private int _weaponDoubleDmgChance = 0;

	private L1ItemInstance _arrow = null;

	private L1ItemInstance _sting = null;

	private int _leverage = 10; // 1/10倍表现。
	// 攻击模式 0x00:none 0x02:暴击 0x04:双击 0x08:镜反射
	private byte _attackType = 0x00;

	private boolean _weaponisMana = false;

	public void setLeverage(int i) {
		_leverage = i;
	}

	private int getLeverage() {
		return _leverage;
	}

	/**
	 * 力量修正
	 */
	private static final int[] strHit = { -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7, 8,
			8, 8, 9, 9, 9, 10, 10, 10, 10, 10 };

	/**
	 * 敏捷修正
	 */
	private static final int[] dexHit = { -1, -1, -1, -1, -1, -1, -1, -1, -1,
			0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
			15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26 };

	private static final int[] strDmg = new int[128];

	static {
		// ＳＴＲ补正
		int dmg = -6;
		for (int str = 0; str <= 22; str++) { // ０～２２２每＋１
			if (str % 2 == 1) {
				dmg++;
			}
			strDmg[str] = dmg;
		}
		for (int str = 23; str <= 28; str++) { // ２３～２８３每＋１
			if (str % 3 == 2) {
				dmg++;
			}
			strDmg[str] = dmg;
		}
		for (int str = 29; str <= 32; str++) { // ２９～３２２每＋１
			if (str % 2 == 1) {
				dmg++;
			}
			strDmg[str] = dmg;
		}
		for (int str = 33; str <= 39; str++) { // ３３～３９１每＋１
			dmg++;
			strDmg[str] = dmg;
		}
		for (int str = 40; str <= 46; str++) { // ４０～４６１每＋２
			dmg += 2;
			strDmg[str] = dmg;
		}
		for (int str = 47; str <= 127; str++) { // ４７～１２７１每＋１
			dmg++;
			strDmg[str] = dmg;
		}
	}

	private static final int[] dexDmg = new int[128];

	static {
		// ＤＥＸ补正
		for (int dex = 0; dex <= 14; dex++) {
			// ０～１４０
			dexDmg[dex] = 0;
		}
		dexDmg[15] = 1;
		dexDmg[16] = 2;
		dexDmg[17] = 3;
		dexDmg[18] = 4;
		dexDmg[19] = 4;
		dexDmg[20] = 4;
		dexDmg[21] = 5;
		dexDmg[22] = 5;
		dexDmg[23] = 5;
		int dmg = 5;
		for (int dex = 24; dex <= 127; dex++) { // ２４～１２７１每＋１
			dmg++;
			dexDmg[dex] = dmg;
		}
	}

	public void setActId(int actId) {
		_attckActId = actId;
	}

	/*
	 * public void setGfxId(int gfxId) { _attckGrfxId = gfxId; }
	 */

	public int getActId() {
		return _attckActId;
	}

	/*
	 * public int getGfxId() { return _attckGrfxId; }
	 */
	public L1Attack(L1Character attacker, L1Character target) {
		if (attacker instanceof L1PcInstance) {
			_pc = (L1PcInstance) attacker;
			if (target instanceof L1PcInstance) {
				_targetPc = (L1PcInstance) target;
				_calcType = PC_PC;
			} else if (target instanceof L1NpcInstance) {
				_targetNpc = ((L1NpcInstance) target);
				_calcType = PC_NPC;
			}
			// 武器情报取得
			weapon = _pc.getWeapon();
			if (weapon != null) {
				_weaponId = weapon.getItem().getItemId();
				_weaponType = weapon.getItem().getType1();
				_weaponAddHit = weapon.getItem().getHitModifier()
						+ weapon.getHit(); // + weapon.getHit() 魔法武器修正
				_weaponAddDmg = weapon.getItem().getDmgModifier();
				_weaponSmall = weapon.getItem().getDmgSmall();
				_weaponLarge = weapon.getItem().getDmgLarge();
				_weaponBless = weapon.getBless();
				_weaponRange = weapon.getItem().getRange();
				_safeenchant = weapon.getItem().get_safeenchant();
				_weaponEnchant = (weapon.getEnchantLevel() + weapon
						.getEnchant()) // + weapon.getEnchant() 魔法武器修正
						- weapon.get_durability(); // 损伤分
				_weaponMaterial = weapon.getItem().getMaterial();
				_weaponisMana = weapon.getItem().isManaItem();
				_weaponAttrEnchantKind = weapon.getAttrEnchantKind();
				_weaponAttrEnchantLevel = weapon.getAttrEnchantLevel();
				_weaponDoubleDmgChance = weapon.getItem()
						.get_double_dmg_chance();

				if (_weaponType == 20) { // 取得
					_arrow = _pc.getInventory().getArrow();
					if (_arrow != null) {
						_weaponBless = _arrow.getBless();
						_weaponMaterial = _arrow.getItem().getMaterial();
					}
				}
				if (_weaponType == 62) { // 取得
					_sting = _pc.getInventory().getSting();
					if (_sting != null) {
						_weaponBless = _sting.getBless();
						_weaponMaterial = _sting.getItem().getMaterial();
					}
				}
			}
			// 追加补正
			if (_weaponType == 20) { // 弓场合ＤＥＸ值参照
				_statusDamage = dexDmg[_pc.getDex()];
			} else { // 以外ＳＴＲ值参照
				_statusDamage = strDmg[_pc.getStr()];
			}
		} else if (attacker instanceof L1NpcInstance) {
			_npc = (L1NpcInstance) attacker;
			if (target instanceof L1PcInstance) {
				_targetPc = (L1PcInstance) target;
				_calcType = NPC_PC;
			} else if (target instanceof L1NpcInstance) {
				_targetNpc = (L1NpcInstance) target;
				_calcType = NPC_NPC;
			}
		}
		_target = target;
		_targetId = target.getId();
		_targetX = target.getX();
		_targetY = target.getY();
	}

	/**
	 * 命中判断
	 * 
	 * QQ：1043567675 by：亮修改 2020年4月30日 下午6:28:18
	 */
	public boolean calcHit() {
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			if (_weaponRange != -1) {
				if (_pc.getLocation()
						.getTileLineDistance(_target.getLocation()) > _weaponRange + 1) { // BIGのモンスターに対応するため射程範囲+1
					_isHit = false; // 射程範囲外
					return _isHit;
				}
			} else {
				if (!_pc.getLocation().isInScreen(_target.getLocation())) {
					_isHit = false; // 射程範囲外
					return _isHit;
				}
			}
			if (_weaponType == 20 && _weaponId != 190 && _weaponId != 10056// 9.3活动新增
					&& _arrow == null && !_pc.isActived()) { // 挂机模式下无需箭支
				_isHit = false; // 矢场合
			} else if (_weaponType == 62 && _sting == null) {
				_isHit = false; // 场合
			} else if (!_pc.glanceCheck(_targetX, _targetY)) {
				_isHit = false; // 攻击者场合障害物判定
			} else if (_weaponId == 247 || _weaponId == 248 || _weaponId == 249) {
				_isHit = false; // 试练剑B～C 攻击无效
			} else if (_calcType == PC_PC) {
				_isHit = calcPcPcHit();
			} else if (_calcType == PC_NPC) {
				_isHit = calcPcNpcHit();
				// System.out.println("命中"+_isHit);
			}
		} else if (_calcType == NPC_PC) {
			_isHit = calcNpcPcHit();
		} else if (_calcType == NPC_NPC) {
			_isHit = calcNpcNpcHit();
		}
		return _isHit;
	}

	/**
	 * PC对PC的命中
	 * 
	 * QQ：1043567675 by：亮修改 2020年4月30日 下午6:28:30
	 */
	private boolean calcPcPcHit() {
		final int MIN_HITRATE = 5;

		if (_targetPc.get_evasion() > 0) {
			if (_random.nextInt(100) < _targetPc.get_evasion()) {
				return false;
			}
		}

		_hitRate = _pc.getLevel();

		if (_pc.getStr() > 39) {
			_hitRate += strHit[39];
		} else {
			_hitRate += strHit[_pc.getStr()];
		}

		if (_pc.getDex() > 39) {
			_hitRate += dexHit[39];
		} else {
			_hitRate += dexHit[_pc.getDex()];
		}

		if (_weaponType != 20 && _weaponType != 62) {
			_hitRate += _weaponAddHit + _pc.getHitup() + (_weaponEnchant / 2);
		} else {
			_hitRate += _weaponAddHit + _pc.getBowHitup()
					+ (_weaponEnchant / 2);
		}
		// 50 += 5 + 10 + (9 /2)

		if (_weaponType == 20 || _weaponType == 62) {
			_hitRate += _pc.getBowHitRate();
		}

		int hitAc = (int) (_hitRate * 0.68 - 10) * -1;

		if (hitAc <= _targetPc.getAc()) {
			_hitRate = 95;
		} else {
			_hitRate = 95 - (hitAc - _targetPc.getAc());
		}

		if (_targetPc.hasSkillEffect(UNCANNY_DODGE)) {
			_hitRate -= 20;
		}

		if (_hitRate < MIN_HITRATE) {
			_hitRate = MIN_HITRATE;
		}

		if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			_hitRate = 0;
		}
		// 妖精命中虚弱百分之几 2018年12月10日 16:32:28
		if (_weaponType == 20) {
			if (Config.BowHit > 0) {
				_hitRate -= (_hitRate * (Config.BowHit * 0.01));
			}
		}
		/*
		 * 删除if (_targetPc.hasSkillEffect(ICE_LANCE)) { _hitRate = 0; } if
		 * (_targetPc.hasSkillEffect(EARTH_BIND)) { _hitRate = 0; }删除
		 */
		int rnd = _random.nextInt(100) + 1;
		if (_weaponType == 20 && _hitRate > rnd) { // 弓场合、场合ER回避再度行。
			return calcErEvasion();
		}

		return _hitRate >= rnd;
	}

	/**
	 * PC对NPC的命中
	 * 
	 * QQ：1043567675 by：亮修改 2020年4月30日 下午6:28:44
	 */
	private boolean calcPcNpcHit() {
		// ＮＰＣ命中率
		// ＝（PCLv＋补正＋STR补正＋DEX补正＋武器补正＋DAI枚数/2＋魔法补正）×5－{NPCAC×（-5）}
		_hitRate = _pc.getLevel();

		if (_pc.getStr() > 39) {
			_hitRate += strHit[39];
		} else {
			_hitRate += strHit[_pc.getStr()];
		}

		if (_pc.getDex() > 39) {
			_hitRate += dexHit[39];
		} else {
			_hitRate += dexHit[_pc.getDex()];
		}

		if (_weaponType != 20 && _weaponType != 62) {
			_hitRate += _weaponAddHit + _pc.getHitup() + (_weaponEnchant / 2);
		} else {
			_hitRate += _weaponAddHit + _pc.getBowHitup()
					+ (_weaponEnchant / 2);
		}

		if (_weaponType == 20 || _weaponType == 62) {
			_hitRate += _pc.getBowHitRate();
		}

		_hitRate *= 5;
		_hitRate += _targetNpc.getAc() * 5;

		if (_hitRate > 95) {
			_hitRate = 95;
		} else if (_hitRate < 5) {
			_hitRate = 5;
		}
		int rnd = _random.nextInt(100) + 1;
		return _hitRate >= rnd;
	}

	/**
	 * NPC对PC的命中
	 * 
	 * QQ：1043567675 by：亮修改 2020年4月30日 下午6:29:03
	 */
	private boolean calcNpcPcHit() {
		if (_targetPc.get_evasion() > 0) {
			if (_random.nextInt(100) < _targetPc.get_evasion()) {
				return false;
			}
		}
		// ＰＣ命中率
		// ＝（NPCLv×2）×5－{NPCAC×（-5）}
		_hitRate = _npc.getLevel() * 2;
		_hitRate *= 5;
		_hitRate += _targetPc.getAc() * 5;

		if (_npc instanceof L1PetInstance) { // LV1每追加命中+2
			_hitRate += _npc.getLevel() * 2;
			_hitRate += ((L1PetInstance) _npc).getHitByWeapon();
		}

		_hitRate += _npc.getHitup();

		// 最低命中率NPC设定
		if (_hitRate < _npc.getLevel()) {
			_hitRate = _npc.getLevel();
		}

		if (_hitRate > 95) {
			_hitRate = 95;
		}

		if (_targetPc.hasSkillEffect(UNCANNY_DODGE)) {
			_hitRate -= 20;
		}

		if (_hitRate < 5) {
			_hitRate = 5;
		}

		// 安区宠物对玩家命中率为0
		if ((_npc instanceof L1PetInstance || _npc instanceof L1SummonInstance)
				&& _targetPc.getZoneType() == 1) {
			_hitRate = 0;
		}
		// 安区宠物对玩家命中率为0 end

		if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			_hitRate = 0;
		}
		/*
		 * 删除if (_targetPc.hasSkillEffect(ICE_LANCE)) { _hitRate = 0; } if
		 * (_targetPc.hasSkillEffect(EARTH_BIND)) { _hitRate = 0; }删除
		 */

		int rnd = _random.nextInt(100) + 1;

		// NPC攻击10以上场合、2以上离场合弓攻击
		if (_npc.getNpcTemplate().get_ranged() >= 6 // 将10改成6
				&& _hitRate > rnd
				&& _npc.getLocation().getTileLineDistance(
						new Point(_targetX, _targetY)) >= 2) {
			return calcErEvasion();
		}
		return _hitRate >= rnd;
	}

	/**
	 * NPC对NPC的命中
	 * 
	 * QQ：1043567675 by：亮修改 2020年4月30日 下午6:29:17
	 */
	private boolean calcNpcNpcHit() {
		int target_ac = 10 - _targetNpc.getNpcTemplate().get_ac();
		int attacker_lvl = _npc.getNpcTemplate().get_level();

		if (target_ac != 0) {
			_hitRate = (100 / target_ac * attacker_lvl); // 被攻击者AC = 攻击者Lv
			// 时命中率１００％
		} else {
			_hitRate = 100 / 1 * attacker_lvl;
		}

		if (_npc instanceof L1PetInstance) { // LV1每追加命中+2
			_hitRate += _npc.getLevel() * 2;
			_hitRate += ((L1PetInstance) _npc).getHitByWeapon();
		}

		if (_hitRate < attacker_lvl) {
			_hitRate = attacker_lvl; // 最低命中率＝Ｌｖ％
		}
		if (_hitRate > 95) {
			_hitRate = 95; // 最高命中率９５％
		}
		if (_hitRate < 5) {
			_hitRate = 5; // 攻击者Lv５未满时命中率５％
		}

		// 安区宠物对宠物命中率为0
		if ((_npc instanceof L1PetInstance || _npc instanceof L1SummonInstance)
				&& _targetNpc.getZoneType() == 1
				&& (_targetNpc instanceof L1PetInstance || _targetNpc instanceof L1SummonInstance)) {
			_hitRate = 0;
		}
		// 安区宠物对宠物命中率为0 end

		int rnd = _random.nextInt(100) + 1;
		return _hitRate >= rnd;
	}

	// ●●●● ＥＲ回避判定 ●●●●
	private boolean calcErEvasion() {
		int er = _targetPc.getEr();

		int rnd = _random.nextInt(100) + 1;
		return er < rnd;
	}

	/**
	 * 总伤害计算
	 * 
	 * QQ：1043567675 by：亮修改 2020年4月30日 下午6:29:29
	 */
	public int calcDamage() {
		if (_calcType == PC_PC) {
			_damage = calcPcPcDamage();
			if (_damage > 0 && _pc.getWeapon() != null) {
				final int enchantDmg = WeaponEnchantDmgTable.get().getDmg(
						_weaponId, _pc.getWeapon().getEnchantLevel());
				if (enchantDmg > 0) {
					_damage *= ((double) enchantDmg * 0.01 + 1.0);
				}
				if (_weaponBless == 0) {
					_damage += _random.nextInt(4) + 1;
				}
			}
			// 增加PVP攻击
			if (_pc.getWeapon() != null && _damage > 0
					&& _pc.getWeapon().getUpdatePVP() > 0) {
				_damage += _pc.getWeapon().getUpdatePVP();
			}
			// 增加PVP攻击
		} else if (_calcType == PC_NPC) {
			_damage = calcPcNpcDamage();
			if (_damage > 0 && _pc.getWeapon() != null) {
				final int enchantDmg = WeaponEnchantDmgTable.get().getDmg(
						_weaponId, _pc.getWeapon().getEnchantLevel());
				if (enchantDmg > 0) {
					_damage *= ((double) enchantDmg * 0.01 + 1.0);
				}
				if (_weaponBless == 0) {
					_damage += _random.nextInt(4) + 1;
				}
			}
			// 增加PVE攻击
			if (_pc.getWeapon() != null && _damage > 0
					&& _pc.getWeapon().getUpdatePVE() > 0) {
				_damage += _pc.getWeapon().getUpdatePVE();
			}
			// 增加PVE攻击
		} else if (_calcType == NPC_PC) {
			_damage = calcNpcPcDamage();
		} else if (_calcType == NPC_NPC) {
			_damage = calcNpcNpcDamage();
		}
		return _damage;
	}

	/**
	 * PC对PC伤害
	 * 
	 * QQ：1043567675 by：亮修改 2020年4月30日 下午6:26:43
	 */
	public int calcPcPcDamage() {
		int weaponMaxDamage = _weaponSmall;

		weaponMaxDamage += calcAttrEnchantDmg();

		int weaponDamage = 0;

		if (_weaponType == 58 && (_random.nextInt(100) + 1) <= 40) { // 
			weaponDamage = weaponMaxDamage;
			_attackType = 0x02;
			// _pc.sendPackets(new S_SkillSound(_pc.getId(), 3671));
			// _pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3671));
		} else if (_weaponType == 0) { // 素手、
			weaponDamage = 0;
		} else if (_weaponType == 20 || _weaponType == 62) { // 弓、
			weaponDamage += calcAttrEnchantDmg();
		} else {
			weaponDamage = _random.nextInt(weaponMaxDamage) + 1;
		}

		if (_pc.hasSkillEffect(SOUL_OF_FLAME)) {
			if (_weaponType != 20 && _weaponType != 62) {
				weaponDamage = weaponMaxDamage;
				// _attackType = 0x02;
			}
		}

		int weaponTotalDamage = weaponDamage + _weaponAddDmg + _weaponEnchant;

		double dmg;

		if (_weaponType != 20 && _weaponType != 62) {
			dmg = weaponTotalDamage + _statusDamage /* + _pc.getDmgup() */;
			if (_pc.getByDollDmgUpRandom() > 0 && _pc.getByDollDmgUpR() > 0) {
				if (_random.nextInt(100) < _pc.getByDollDmgUpRandom()) {
					_pc.sendPackets(new S_SkillSound(_pc.getId(), 6319));
					_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 6319));
					dmg += _pc.getByDollDmgUpR();
				}
			}
		} else {
			dmg = weaponTotalDamage + _statusDamage /* + _pc.getBowDmgup() */;
			if (_pc.getByDollBowDmgUpRandom() > 0
					&& _pc.getByDollBowDmgUpR() > 0) {
				if (_random.nextInt(100) < _pc.getByDollBowDmgUpRandom()) {
					_pc.sendPackets(new S_SkillSound(_pc.getId(), 6319));
					_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 6319));
					dmg += _pc.getByDollBowDmgUpR();
				}
			}
		}
		if (_pc.isKnight() || _pc.isDarkelf() || _pc.isDragonKnight()) { // 、DE
			if (_weaponType == 20 || _weaponType == 62) { // 弓、
				dmg -= _pc.getBaseDmgup();
			}
		} else if (_pc.isElf()) { // 
			if (_weaponType != 20 && _weaponType != 62) { // 弓、以外
				dmg -= _pc.getBaseDmgup();
			}
		}

		switch (_pc.get_weaknss()) {
		case 1:
			if (_pc.isFoeSlayer()) {// 使用屠宰者
				_pc.set_weaknss(0, 0);
				_pc.sendPackets(new S_PacketBoxDk(0));
				if (_random.nextInt(100) < 65) {
					dmg *= 1.2;
					// System.out.println("1段眼加成dmg:" + dmg);
				} else {
					dmg *= 0.5;
					// System.out.println("1段眼非加成dmg:" + dmg);
				}
			}
			break;
		case 2:
			if (_pc.isFoeSlayer()) {// 使用屠宰者
				_pc.set_weaknss(0, 0);
				_pc.sendPackets(new S_PacketBoxDk(0));
				if (_random.nextInt(100) < 52) {
					dmg *= 2;
					// System.out.println("2段眼加成dmg:" + dmg);
				} else {
					dmg *= 1;
					// System.out.println("2段眼非加成dmg:" + dmg);
				}
			}
			break;
		case 3:
			if (_pc.isFoeSlayer()) {// 使用屠宰者
				_pc.set_weaknss(0, 0);
				_pc.sendPackets(new S_PacketBoxDk(0));
				if (_random.nextInt(100) < 58) {
					dmg *= 2.5;
					// System.out.println("3段眼加成dmg:" + dmg);
				} else {
					dmg *= 1.5;
					// System.out.println("3段眼非加成dmg:" + dmg);
				}
			}
			break;
		}

		 int dmgup = dk_dmgUp();
		 if (_pc.isDragonKnight()) {
			 dmg += dmgup;
		 }

		if (_weaponType == 20) { // 弓
			if (_arrow != null) {
				int add_dmg = _arrow.getItem().getDmgSmall();
				if (add_dmg == 0) {
					add_dmg = 1;
				}
				dmg = dmg + _random.nextInt(add_dmg) + 1;
			} else if (_weaponId == 190 || _weaponId == 10056// 9.3活动新增
			) { // 弓
				dmg = dmg + _random.nextInt(15) + 7;// 沙哈弓无箭伤害
			}
		} else if (_weaponType == 62) { // 
			int add_dmg = _sting.getItem().getDmgSmall();
			if (add_dmg == 0) {
				add_dmg = 1;
			}
			dmg = dmg + _random.nextInt(add_dmg) + 1;
		}

		if (_pc.hasSkillEffect(DOUBLE_BRAKE)
				&& (_weaponType == 54 || _weaponType == 58)) {
			if ((_random.nextInt(100) + 1) <= 33) {
				dmg *= 2;
				// _attackType = 0x04;
			}
		}

		// 龙骑士技能 -3 伤害
		if (_targetPc.hasSkillEffect(L1SkillId.DRAGON_SKIN)) {
			dmg -= 5;
		}

		if (_weaponType == 54
				&& (_random.nextInt(100) + 1) <= _weaponDoubleDmgChance) { // 
			dmg *= 2;
			_attackType = 0x04;
			// _pc.sendPackets(new S_SkillSound(_pc.getId(), 3398));
			// _pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3398));
		}
		if ((_weaponId == 76 || _weaponId == 10076)
				&& (_random.nextInt(100) + 1) <= 25) {// 伦德双刀
			dmg *= 2;
		}
		if (_weaponType != 20 && _weaponType != 62) {
			dmg += _pc.getDmgup();
		} else {
			dmg += _pc.getBowDmgup();
		}
		dmg = calcBuffDamage(dmg);
		// 无界装备-致命攻击
		if ((_weaponId == Weapon_AJ_1_2 && (_random.nextInt(100) + 1) <= 50) // 暗杀者匕首
				|| ((_weaponId == Weapon_AJ_1_3 || _weaponId == Weapon_AJ_1_4) && (_random
						.nextInt(100) + 1) <= 20) // 光辉之剑、暗杀者十字弓
				|| (_weaponId == Weapon_AJ_1_6 && (_random.nextInt(100) + 1) <= 10) // 巫妖之爪
				|| ((_weaponId == Weapon_AJ_1_5 || _weaponId == Weapon_AJ_1_7) && (_random
						.nextInt(100) + 1) <= 25)) { // 断头双刀、恶魔之爪
			dmg *= 2;
		}
		// 无界装备-致命攻击 end

		if (_weaponId == 124) { // 
			dmg += L1WeaponSkill.getBaphometStaffDamage(_pc, _target);
		} else if (_weaponId == 2 || _weaponId == 200002) { // 
			dmg = L1WeaponSkill.getDiceDaggerDamage(_pc, _targetPc, weapon);
		} else {
			// 删除dmg += L1WeaponSkill.getWeaponSkillDamage(_pc, _target,
			// _weaponId);
			// 魔武变更
			dmg += L1WilliamWeaponSkill.getWeaponSkillDamage(_pc, _target,
					_weaponId, _weaponEnchant, _safeenchant);
			// 魔武变更 end
		}

		if (_pc.getDamageUpRandomByHelm() > 0 && _pc.getDamageUpByHelm() > 0) {
			if (_random.nextInt(100) + 1 > 95) {
				_pc.sendPackets(new S_SkillSound(_pc.getId(), 6319));
				_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 6319));
				dmg += 15;
			}
		}
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

		dmg -= _targetPc.getDamageReductionByRing();
		// 反击屏障
		if (_targetPc.hasSkillEffect(COUNTER_BARRIER)
				&& (_random.nextInt(100) + 1) < 26 && _weaponType != 20
				&& _weaponType != 62) {
			target_weapon = _targetPc.getWeapon();
			_pc.sendPackets(new S_SkillSound(_targetId, 5846));
			_pc.broadcastPacket(new S_SkillSound(_targetId, 5846));
			int pc_dmg = (target_weapon.getItem().getDmgLarge()
					+ target_weapon.getEnchantLevel() + target_weapon.getItem()
					.getDmgModifier()) * 2;
			_pc.receiveDamage(_targetPc, pc_dmg, 0);
		}
		// 反击屏障 end

		// 炎之石效果
		if (_pc.getInventory().checkItem(Item_AJ_34)) { // 炎之石(Lv3)
			dmg += 5;
			if (!_targetPc.hasSkillEffect(Skill_AJ_1_2)) { // 效果延迟
				_targetPc
						.sendPackets(new S_SkillSound(_targetPc.getId(), 3362));
				_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(),
						3362));
				_targetPc.setSkillEffect(Skill_AJ_1_2, 1800);
			}
			areaskill(_pc, _targetPc, 5, 2);
		} else if (_pc.getInventory().checkItem(Item_AJ_33)) { // 炎之石(Lv2)
			dmg += 3;
			if (!_targetPc.hasSkillEffect(Skill_AJ_1_2)) { // 效果延迟
				_targetPc
						.sendPackets(new S_SkillSound(_targetPc.getId(), 3362));
				_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(),
						3362));
				_targetPc.setSkillEffect(Skill_AJ_1_2, 1800);
			}
			areaskill(_pc, _targetPc, 3, 2);
		} else if (_pc.getInventory().checkItem(Item_AJ_32)) { // 炎之石(Lv1)
			dmg += 2;
			if (!_targetPc.hasSkillEffect(Skill_AJ_1_2)) { // 效果延迟
				_targetPc
						.sendPackets(new S_SkillSound(_targetPc.getId(), 3362));
				_targetPc.broadcastPacket(new S_SkillSound(_targetPc.getId(),
						3362));
				_targetPc.setSkillEffect(Skill_AJ_1_2, 1800);
			}
			areaskill(_pc, _targetPc, 2, 2);
		}
		// 炎之石效果 end

		// 冰之石效果
		if (_pc.getInventory().checkItem(Item_AJ_37)
				&& ((_random.nextInt(100) + 1) <= 9)) { // 冰之石(Lv3)
			dmg += 20;
			if (!_targetPc.hasSkillEffect(50) && !_targetPc.hasSkillEffect(78)
					&& !_targetPc.hasSkillEffect(80)
					&& !_targetPc.hasSkillEffect(157)
					&& _targetPc.get_poisonStatus4() != 4
					&& _targetPc.get_poisonStatus6() != 4
					&& _targetPc.get_poisonStatus7() != 4) {
				L1Poison7 poison = new L1Poison7();
				boolean success = poison.handleCommands((L1Object) _targetPc,
						4, 1500, 0);
				if (success == true) {
					_targetPc.add_poison7(poison);
					L1EffectSpawn.getInstance().spawnEffect(81168, 1500,
							_targetPc.getX(), _targetPc.getY(),
							_targetPc.getMapId());
				}
			}
		} else if (_pc.getInventory().checkItem(Item_AJ_36)
				&& ((_random.nextInt(100) + 1) <= 6)) { // 冰之石(Lv2)
			dmg += 15;
			if (!_targetPc.hasSkillEffect(50) && !_targetPc.hasSkillEffect(78)
					&& !_targetPc.hasSkillEffect(80)
					&& !_targetPc.hasSkillEffect(157)
					&& _targetPc.get_poisonStatus4() != 4
					&& _targetPc.get_poisonStatus6() != 4
					&& _targetPc.get_poisonStatus7() != 4) {
				L1Poison7 poison = new L1Poison7();
				boolean success = poison.handleCommands((L1Object) _targetPc,
						4, 1500, 0);
				if (success == true) {
					_targetPc.add_poison7(poison);
					L1EffectSpawn.getInstance().spawnEffect(81168, 1500,
							_targetPc.getX(), _targetPc.getY(),
							_targetPc.getMapId());
				}
			}
		} else if (_pc.getInventory().checkItem(Item_AJ_35)
				&& ((_random.nextInt(100) + 1) <= 4)) { // 冰之石(Lv1)
			dmg += 10;
			if (!_targetPc.hasSkillEffect(50) && !_targetPc.hasSkillEffect(78)
					&& !_targetPc.hasSkillEffect(80)
					&& !_targetPc.hasSkillEffect(157)
					&& _targetPc.get_poisonStatus4() != 4
					&& _targetPc.get_poisonStatus6() != 4
					&& _targetPc.get_poisonStatus7() != 4) {
				L1Poison7 poison = new L1Poison7();
				boolean success = poison.handleCommands((L1Object) _targetPc,
						4, 1500, 0);
				if (success == true) {
					_targetPc.add_poison7(poison);
					L1EffectSpawn.getInstance().spawnEffect(81168, 1500,
							_targetPc.getX(), _targetPc.getY(),
							_targetPc.getMapId());
				}
			}
		}
		// 冰之石效果 end

		if (_weaponType == 0) { // 素手
			dmg = (_random.nextInt(5) + 4) / 4;
		}

		Object[] dollList = _pc.getDollList().values().toArray(); // 追加
		for (Object dollObject : dollList) {
			L1DollInstance doll = (L1DollInstance) dollObject;
			dmg += doll.getDamageByDoll();
		}

		dmg -= _targetPc.getDamageReductionByArmor(); // 防具轻减

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
		if (_targetPc.hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg /= 1.5;
		}
		if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(ICE_LANCE)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(EARTH_BIND)) {
			dmg = 0;
		}
		// 冰冻、地屏状态判断
		if (_targetPc.get_poisonStatus4() == 4
				|| _targetPc.get_poisonStatus6() == 4) {
			dmg = 0;
		}
		// 冰冻、地屏状态判断 end

		if (dmg <= 0) {
			_isHit = false;
		}

		// 回逤判断
		if (_pc.hasSkillEffect(Skill_AJ_0_4)) {
			_pc.setSkillEffect(Skill_AJ_0_4, 16 * 1000); // 回逤等候16秒
		}
		// 回逤判断 end

		return (int) dmg;
	}

	/**
	 * PC对NPC伤害计算
	 * 
	 * QQ：1043567675 by：亮修改 2020年4月30日 下午6:30:09
	 */
	private int calcPcNpcDamage() {
		int weaponMaxDamage = 0;
		/*
		 * if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase("small")
		 * && _weaponSmall > 0) { weaponMaxDamage = _weaponSmall; } else
		 */
		if (_targetNpc.getNpcTemplate().get_size().equalsIgnoreCase("large")
				&& _weaponLarge > 0) {
			weaponMaxDamage = _weaponLarge;
		} else {
			weaponMaxDamage = _weaponSmall;
		}
		weaponMaxDamage += calcAttrEnchantDmg();

		int weaponDamage = 0;

		if (_weaponType == 58 && (_random.nextInt(100) + 1) <= 33) { // 
			weaponDamage = weaponMaxDamage;
			_attackType = 0x02;
			// _pc.sendPackets(new S_SkillSound(_pc.getId(), 3671));
			// _pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3671));
		} else if (_weaponType == 0) { // 素手、
			weaponDamage = 0;
		} else if (_weaponType == 20 || _weaponType == 62) { // 弓、
			weaponDamage += calcAttrEnchantDmg();
		} else {
			weaponDamage = _random.nextInt(weaponMaxDamage) + 1;
		}
		if (_pc.hasSkillEffect(SOUL_OF_FLAME)) {
			if (_weaponType != 20 && _weaponType != 62) {
				weaponDamage = weaponMaxDamage;
			}
		}

		int weaponTotalDamage = weaponDamage + _weaponAddDmg + _weaponEnchant;

		double dmg;
		if (_weaponType != 20 && _weaponType != 62) {
			dmg = weaponTotalDamage + _statusDamage/* + _pc.getDmgup() */;
			if (_pc.getByDollDmgUpRandom() > 0 && _pc.getByDollDmgUpR() > 0) {
				if (_random.nextInt(100) < _pc.getByDollDmgUpRandom()) {
					_pc.sendPackets(new S_SkillSound(_pc.getId(), 6319));
					_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 6319));
					dmg += _pc.getByDollDmgUpR();
					// System.out.println(_pc.getByDollDmgUpR());
				}
			}
		} else {
			dmg = weaponTotalDamage + _statusDamage/* + _pc.getBowDmgup() */;
			if (_pc.getByDollBowDmgUpRandom() > 0
					&& _pc.getByDollBowDmgUpR() > 0) {
				if (_random.nextInt(100) < _pc.getByDollBowDmgUpRandom()) {
					_pc.sendPackets(new S_SkillSound(_pc.getId(), 6319));
					_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 6319));
					dmg += _pc.getByDollBowDmgUpR();
				}
			}
		}

		if (_pc.isKnight() || _pc.isDarkelf() || _pc.isDragonKnight()) { // 、DE
			if (_weaponType == 20 || _weaponType == 62) { // 弓、
				dmg -= _pc.getBaseDmgup();
			}
		} else if (_pc.isElf()) { // 
			if (_weaponType != 20 && _weaponType != 62) { // 弓、以外
				dmg -= _pc.getBaseDmgup();
			}
		}

		// 龙骑士使用屠宰
		if (_pc.isDragonKnight()) {
			switch (_pc.get_weaknss()) {
			case 1:
				if (_pc.isFoeSlayer()) {// 使用屠宰者
					_pc.set_weaknss(0, 0);
					_pc.sendPackets(new S_PacketBoxDk(0));
					if (_random.nextInt(100) < 65) {
						dmg *= 1.2;
					} else {
						dmg *= 0.5;
					}
				}
				break;
			case 2:
				if (_pc.isFoeSlayer()) {// 使用屠宰者
					_pc.set_weaknss(0, 0);
					_pc.sendPackets(new S_PacketBoxDk(0));
					if (_random.nextInt(100) < 52) {
						dmg *= 2;
					} else {
						dmg *= 1;
					}
				}
				break;
			case 3:
				if (_pc.isFoeSlayer()) {// 使用屠宰者
					_pc.set_weaknss(0, 0);
					_pc.sendPackets(new S_PacketBoxDk(0));
					if (_random.nextInt(100) < 58) {
						dmg *= 2.5;
					} else {
						dmg *= 1.5;
					}
				}
				break;
			}
		}

		 int dmgup = dk_dmgUp();
		 if (_pc.isDragonKnight()) {
		 dmg += dmgup;
		 }

		if (_weaponType == 20) { // 弓
			if (_arrow != null) {
				int add_dmg = 0;
				if (_targetNpc.getNpcTemplate().get_size()
						.equalsIgnoreCase("large")) {
					add_dmg = _arrow.getItem().getDmgLarge();
				} else {
					add_dmg = _arrow.getItem().getDmgSmall();
				}
				if (add_dmg == 0) {
					add_dmg = 1;
				}
				if (_targetNpc.getNpcTemplate().is_hard()) {
					add_dmg /= 2;
				}
				dmg = dmg + _random.nextInt(add_dmg) + 1;
			} else if (_weaponId == 190 || _weaponId == 10056// 9.3活动新增
			) { // 弓
				dmg = dmg + _random.nextInt(15) + 7;// 沙哈弓无箭伤害
			}
		} else if (_weaponType == 62) { // 
			int add_dmg = 0;
			if (_targetNpc.getNpcTemplate().get_size()
					.equalsIgnoreCase("large")) {
				add_dmg = _sting.getItem().getDmgLarge();
			} else {
				add_dmg = _sting.getItem().getDmgSmall();
			}
			if (add_dmg == 0) {
				add_dmg = 1;
			}
			dmg = dmg + _random.nextInt(add_dmg) + 1;
		}

		dmg += calcMaterialBlessDmg(); // 银祝福

		if (_pc.hasSkillEffect(DOUBLE_BRAKE)
				&& (_weaponType == 54 || _weaponType == 58)) {
			if ((_random.nextInt(100) + 1) <= 33) {
				dmg *= 2;
			}
		}

		if (_weaponType == 54
				&& (_random.nextInt(100) + 1) <= _weaponDoubleDmgChance) { // 
			dmg *= 2;
			_attackType = 0x04;
			// _pc.sendPackets(new S_SkillSound(_pc.getId(), 3398));
			// _pc.broadcastPacket(new S_SkillSound(_pc.getId(), 3398));
		}

		if (_weaponType != 20 && _weaponType != 62) {
			dmg += _pc.getDmgup();
		} else {
			dmg += _pc.getBowDmgup();
		}

		dmg = calcBuffDamage(dmg);

		if ((_weaponId == 76 || _weaponId == 10076)
				&& (_random.nextInt(100) + 1) <= 25) {// 伦德双刀
			dmg *= 2;
		}

		// 无界装备-致命攻击
		if ((_weaponId == Weapon_AJ_1_2 && (_random.nextInt(100) + 1) <= 50) // 暗杀者匕首
				|| ((_weaponId == Weapon_AJ_1_3 || _weaponId == Weapon_AJ_1_4) && (_random
						.nextInt(100) + 1) <= 20) // 光辉之剑、暗杀者十字弓
				|| (_weaponId == Weapon_AJ_1_6 && (_random.nextInt(100) + 1) <= 10) // 巫妖之爪
				|| ((_weaponId == Weapon_AJ_1_5 || _weaponId == Weapon_AJ_1_7) && (_random
						.nextInt(100) + 1) <= 25)) { // 断头双刀、恶魔之爪
			dmg *= 2;
		}
		// 无界装备-致命攻击 end

		// 炎之石效果
		if (_pc.getInventory().checkItem(Item_AJ_34)) { // 炎之石(Lv3)
			dmg += 5;
			if (!_targetNpc.hasSkillEffect(Skill_AJ_1_2)) { // 效果延迟
				_targetNpc.broadcastPacket(new S_SkillSound(_targetNpc.getId(),
						3362));
				_targetNpc.setSkillEffect(Skill_AJ_1_2, 1800);
			}
			areaskill(_pc, _targetNpc, 5, 2);
		} else if (_pc.getInventory().checkItem(Item_AJ_33)) { // 炎之石(Lv2)
			dmg += 3;
			if (!_targetNpc.hasSkillEffect(Skill_AJ_1_2)) { // 效果延迟
				_targetNpc.broadcastPacket(new S_SkillSound(_targetNpc.getId(),
						3362));
				_targetNpc.setSkillEffect(Skill_AJ_1_2, 1800);
			}
			areaskill(_pc, _targetNpc, 3, 2);
		} else if (_pc.getInventory().checkItem(Item_AJ_32)) { // 炎之石(Lv1)
			dmg += 2;
			if (!_targetNpc.hasSkillEffect(Skill_AJ_1_2)) { // 效果延迟
				_targetNpc.broadcastPacket(new S_SkillSound(_targetNpc.getId(),
						3362));
				_targetNpc.setSkillEffect(Skill_AJ_1_2, 1800);
			}
			areaskill(_pc, _targetNpc, 2, 2);
		}
		// 炎之石效果 end

		// 冰之石效果
		if (_pc.getInventory().checkItem(Item_AJ_37)
				&& ((_random.nextInt(100) + 1) <= 9)) { // 冰之石(Lv3)
			dmg += 20;
			if (!_targetNpc.hasSkillEffect(50)
					&& !_targetNpc.hasSkillEffect(78)
					&& !_targetNpc.hasSkillEffect(80)
					&& !_targetNpc.hasSkillEffect(157)
					&& _targetNpc.get_poisonStatus4() != 4
					&& _targetNpc.get_poisonStatus6() != 4
					&& _targetNpc.get_poisonStatus7() != 4) {
				L1Poison7 poison = new L1Poison7();
				boolean success = poison.handleCommands((L1Object) _targetNpc,
						4, 1500, 0);
				if (success == true) {
					_targetNpc.add_poison7(poison);
					L1EffectSpawn.getInstance().spawnEffect(81168, 1500,
							_targetNpc.getX(), _targetNpc.getY(),
							_targetNpc.getMapId());
				}
			}
		} else if (_pc.getInventory().checkItem(Item_AJ_36)
				&& ((_random.nextInt(100) + 1) <= 6)) { // 冰之石(Lv2)
			dmg += 15;
			if (!_targetNpc.hasSkillEffect(50)
					&& !_targetNpc.hasSkillEffect(78)
					&& !_targetNpc.hasSkillEffect(80)
					&& !_targetNpc.hasSkillEffect(157)
					&& _targetNpc.get_poisonStatus4() != 4
					&& _targetNpc.get_poisonStatus6() != 4
					&& _targetNpc.get_poisonStatus7() != 4) {
				L1Poison7 poison = new L1Poison7();
				boolean success = poison.handleCommands((L1Object) _targetNpc,
						4, 1500, 0);
				if (success == true) {
					_targetNpc.add_poison7(poison);
					L1EffectSpawn.getInstance().spawnEffect(81168, 1500,
							_targetNpc.getX(), _targetNpc.getY(),
							_targetNpc.getMapId());
				}
			}
		} else if (_pc.getInventory().checkItem(Item_AJ_35)
				&& ((_random.nextInt(100) + 1) <= 4)) { // 冰之石(Lv1)
			dmg += 10;
			if (!_targetNpc.hasSkillEffect(50)
					&& !_targetNpc.hasSkillEffect(78)
					&& !_targetNpc.hasSkillEffect(80)
					&& !_targetNpc.hasSkillEffect(157)
					&& _targetNpc.get_poisonStatus4() != 4
					&& _targetNpc.get_poisonStatus6() != 4
					&& _targetNpc.get_poisonStatus7() != 4) {
				L1Poison7 poison = new L1Poison7();
				boolean success = poison.handleCommands((L1Object) _targetNpc,
						4, 1500, 0);
				if (success == true) {
					_targetNpc.add_poison7(poison);
					L1EffectSpawn.getInstance().spawnEffect(81168, 1500,
							_targetNpc.getX(), _targetNpc.getY(),
							_targetNpc.getMapId());
				}
			}
		}
		// 冰之石效果 end

		if (_weaponId == 124) { // 
			dmg += L1WeaponSkill.getBaphometStaffDamage(_pc, _target);
		} else {
			// 删除dmg += L1WeaponSkill.getWeaponSkillDamage(_pc, _target,
			// _weaponId);
			// 魔武变更
			dmg += L1WilliamWeaponSkill.getWeaponSkillDamage(_pc, _target,
					_weaponId, _weaponEnchant, _safeenchant);
			// 魔武变更 end
		}

		if (_weaponType == 0) { // 素手
			dmg = (_random.nextInt(5) + 4) / 4;
		}

		Object[] dollList = _pc.getDollList().values().toArray(); // 追加
		for (Object dollObject : dollList) {
			L1DollInstance doll = (L1DollInstance) dollObject;
			dmg += doll.getDamageByDoll();
		}

		dmg -= calcNpcDamageReduction();

		if (_pc.getDamageUpRandomByHelm() > 0 && _pc.getDamageUpByHelm() > 0) {
			if (_random.nextInt(100) + 1 > 95) {
				_pc.sendPackets(new S_SkillSound(_pc.getId(), 6319));
				_pc.broadcastPacket(new S_SkillSound(_pc.getId(), 6319));
				dmg += 15;
			}
		}
		// 怪物反击屏障
		if (_targetNpc.hasSkillEffect(COUNTER_BARRIER)
				&& (_random.nextInt(100) + 1) < 26 && _weaponType != 20
				&& _weaponType != 62) {
			_pc.sendPackets(new S_SkillSound(_targetId, 5846));
			_pc.receiveDamage(_targetNpc, 100,false);
		}
		// 怪物反击屏障 end

		// 怪物圣结界
		if (_targetNpc.hasSkillEffect(17004)) {
			dmg /= 2;
		}
		// 怪物圣结界 end

		/*
		 * if (_targetNpc instanceof L1PetInstance // 、攻击 ||
		 * _targetNpc instanceof L1SummonInstance) { dmg /= 8; }
		 */

		if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
			dmg = 0;
		}

		// 冰冻、地屏状态判断
		if (_targetNpc.get_poisonStatus4() == 4
				|| _targetNpc.get_poisonStatus6() == 4) {
			dmg = 0;
		}
		// 冰冻、地屏状态判断 end

		// 怪死公告
		if (_targetNpc.getCurrentHp() <= dmg
				&& _targetNpc.getNpcTemplate().getBroad() == true) {
			String attack_name = _pc.getName(); // 玩家名称
			String target_name = _targetNpc.getName(); // 怪物名称
			String weapon_name = weapon.getLogName(); // 武器名称
			String msg0 = L1WilliamSystemMessage.ShowMessage(1105); // 恭喜
			String msg1 = L1WilliamSystemMessage.ShowMessage(1106); // 使用
			String msg2 = L1WilliamSystemMessage.ShowMessage(1107); // 杀死了
			L1World.getInstance().broadcastPacketToAll(
					new S_ServerMessage(166,
							msg0 + " ( " + attack_name + " ) ", msg1 + " "
									+ weapon_name, msg2 + " (" + target_name
									+ ")"));
		}
		// 怪死公告 end

		if (dmg <= 0) {
			_isHit = false;
		}

		return (int) dmg;
	}

	/**
	 * NPC对PC伤害计算
	 * 
	 * QQ：1043567675 by：亮修改 2020年4月30日 下午6:30:27
	 */
	private int calcNpcPcDamage() {
		int lvl = _npc.getLevel();
		double dmg = 0D;
		if (lvl < 10) {
			dmg = _random.nextInt(lvl) + 10D + _npc.getStr() / 2 + 1;
		} else {
			// 删除dmg = _random.nextInt(lvl) + _npc.getStr() / 2 + 1;
			// 公式变更
			switch (lvl) {
			case 10:
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
				dmg = 5 + _random.nextInt(lvl - 9) + _npc.getStr() / 2 + 1;
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
				dmg = 10 + _random.nextInt(lvl - 15) + _npc.getStr() / 2 + 1;
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
				dmg = 15 + _random.nextInt(lvl - 20) + _npc.getStr() / 2 + 1;
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
				dmg = 25 + _random.nextInt(lvl - 30) + _npc.getStr() / 2 + 1;
			}
				break;
			case 51:
			case 52:
			case 53:
			case 54:
			case 55:
			case 56:
			case 57:
			case 58:
			case 59:
			case 60: {
				dmg = 35 + _random.nextInt(lvl - 30) + _npc.getStr() / 2 + 1;
			}
				break;
			case 61:
			case 62:
			case 63:
			case 64:
			case 65:
			case 66:
			case 67:
			case 68:
			case 69:
			case 70: {
				dmg = 45 + _random.nextInt(lvl - 30) + _npc.getStr() / 2 + 1;
			}
				break;
			case 71:
			case 72:
			case 73:
			case 74:
			case 75:
			case 76:
			case 77:
			case 78:
			case 79:
			case 80: {
				dmg = 55 + _random.nextInt(lvl - 30) + _npc.getStr() / 2 + 1;
			}
				break;
			case 81:
			case 82:
			case 83:
			case 84:
			case 85:
			case 86:
			case 87:
			case 88:
			case 89:
			case 90: {
				dmg = 65 + _random.nextInt(lvl - 35) + _npc.getStr() / 2 + 1;
			}
				break;
			case 91:
			case 92:
			case 93:
			case 94:
			case 95:
			case 96:
			case 97:
			case 98:
			case 99:
			case 100:
			case 101:
			case 102:
			case 103:
			case 104:
			case 105:
			case 106:
			case 107:
			case 108:
			case 109:
			case 110:
			case 111:
			case 112:
			case 113:
			case 114:
			case 115:
			case 116:
			case 117:
			case 118:
			case 119:
			case 120:
			case 121:
			case 122:
			case 123:
			case 124:
			case 125:
			case 126:
			case 127: {
				dmg = 80 + _random.nextInt(lvl - 50) + _npc.getStr() / 2 + 1;
			}
				break;
			}
			// 公式变更 end
		}

		if (_npc instanceof L1PetInstance) {
			dmg += (lvl / 8); // 改成每8级增加额外伤害
			dmg += ((L1PetInstance) _npc).getDamageByWeapon();
		}

		dmg += _npc.getDmgup();

		if (isUndeadDamage()) {
			dmg *= 1.1;
		}

		// 梦岛怪物伤害调整
		switch (_npc.getNpcTemplate().get_npcId()) {
		case 45549:
		case 45551:
		case 45552:
		case 45553:
		case 45554:
		case 45555:
		case 45556:
		case 45557:
		case 45558:
		case 45559:
		case 45560:
		case 45561:
		case 45562: {
			dmg = _random.nextInt(lvl) + _npc.getStr() / 2 + 1;
		}
			break;
		}
		// 梦岛怪物伤害调整 end

		dmg = dmg * getLeverage() / 10;

		// 怪物双重破坏
		if (_npc.hasSkillEffect(17003)) {
			if ((_random.nextInt(100) + 1) <= 33) {
				_attackType = 0x04;
				dmg *= 2;
			}
		}
		// 怪物双重破坏 end

		// 怪物燃烧斗志
		if (_npc.hasSkillEffect(17002)) {
			if ((_random.nextInt(100) + 1) <= 33) {
				double tempDmg = dmg;
				dmg = tempDmg * 1.5;
			}
		}
		// 怪物燃烧斗志 end

		dmg -= calcPcDefense();

		// 龙骑士技能 -3 伤害
		if (_targetPc.hasSkillEffect(L1SkillId.DRAGON_SKIN)) {
			dmg -= 5;
		}

		if (_npc.isWeaponBreaked()) { // ＮＰＣ中。
			dmg /= 2;
		}

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

		// 反击屏障
		if (_targetPc.hasSkillEffect(COUNTER_BARRIER)
				&& (_random.nextInt(100) + 1) < 26
				&& (_npc.getNpcTemplate().getBowActId() == 0 || (_npc
						.getNpcTemplate().getBowActId() > 0 && _npc
						.getLocation().getTileLineDistance(
								new Point(_targetX, _targetY)) <= 1))) {
			target_weapon = _targetPc.getWeapon();
			// if (target_weapon != null && target_weapon.getItem().getType1()
			// == 50) {
			_npc.broadcastPacket(new S_SkillSound(_targetId, 5846));
			int pc_dmg = (target_weapon.getItem().getDmgLarge()
					+ target_weapon.getEnchantLevel() + target_weapon.getItem()
					.getDmgModifier()) * 2;
			_npc.receiveDamage(_targetPc, pc_dmg);
			// dmg = 0;
			// }
		}
		// 反击屏障 end

		dmg -= _targetPc.getDamageReductionByArmor(); // 防具轻减

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
		if (_targetPc.hasSkillEffect(IMMUNE_TO_HARM)) {
			dmg /= 1.5;
		}
		if (_npc instanceof L1PetInstance || // 、攻击
				_npc instanceof L1SummonInstance) {
			// 删除dmg /= 8;
			// 伤害变更
			Random _random = new Random();
			dmg = 3 + _random.nextInt(3);// 3~5
			// 伤害变更 end
		}
		if (_targetPc.hasSkillEffect(ABSOLUTE_BARRIER)) {
			dmg = 0;
		}
		// 安区宠物
		if ((_npc instanceof L1PetInstance || _npc instanceof L1SummonInstance)
				&& _targetPc.getZoneType() == 1) {
			dmg = 0;
		}
		// 安区宠物对玩家伤害为0 end
		if (_targetPc.hasSkillEffect(ICE_LANCE)) {
			dmg = 0;
		}
		if (_targetPc.hasSkillEffect(EARTH_BIND)) {
			dmg = 0;
		}
		// 冰冻、地屏状态判断
		if (_targetPc.get_poisonStatus4() == 4
				|| _targetPc.get_poisonStatus6() == 4) {
			dmg = 0;
		}
		// 冰冻、地屏状态判断 end

		if (dmg <= 0) {
			_isHit = false;
		}

		addNpcPoisonAttack(_npc, _targetPc);

		return (int) dmg;
	}

	/**
	 * NPC对PC伤害计算
	 * 
	 * QQ：1043567675 by：亮修改 2020年4月30日 下午6:30:49
	 */
	private int calcNpcNpcDamage() {
		int lvl = _npc.getLevel();
		double dmg = 0;

		if (_npc instanceof L1PetInstance) {
			// 删除dmg = _random.nextInt(_npc.getNpcTemplate().get_level())
			// 删除 + _npc.getStr() / 2 + 1;
			// 宠物物理伤害设定
			switch (lvl) {
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
				dmg = 5 + (_random.nextInt(5) + 1);// 6~10
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
				dmg = 5 + (_random.nextInt(10) + 1);// 6~15
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
				dmg = 5 + (_random.nextInt(10) + 1);// 6~15
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
				dmg = 5 + (_random.nextInt(15) + 1);// 6~20
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
				dmg = 5 + (_random.nextInt(15) + 1);// 6~20
			}
				break;
			default: {
				dmg = 10 + (_random.nextInt(15) + 1);// 10~25
			}
				break;
			}
			// 宠物物理伤害设定 end
			dmg += (lvl / 8); // 改成每8级增加额外伤害
		} else {
			// 删除dmg = _random.nextInt(lvl) + _npc.getStr() / 2 + 1;
			// 公式变更
			switch (lvl) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9: {
				dmg = _random.nextInt(lvl) + _npc.getStr() / 2 + 1;
			}
				break;
			case 10:
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
				dmg = 5 + _random.nextInt(lvl - 9) + _npc.getStr() / 2 + 1;
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
				dmg = 10 + _random.nextInt(lvl - 15) + _npc.getStr() / 2 + 1;
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
				dmg = 15 + _random.nextInt(lvl - 20) + _npc.getStr() / 2 + 1;
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
				dmg = 25 + _random.nextInt(lvl - 30) + _npc.getStr() / 2 + 1;
			}
				break;
			case 51:
			case 52:
			case 53:
			case 54:
			case 55:
			case 56:
			case 57:
			case 58:
			case 59:
			case 60: {
				dmg = 35 + _random.nextInt(lvl - 30) + _npc.getStr() / 2 + 1;
			}
				break;
			case 61:
			case 62:
			case 63:
			case 64:
			case 65:
			case 66:
			case 67:
			case 68:
			case 69:
			case 70: {
				dmg = 45 + _random.nextInt(lvl - 30) + _npc.getStr() / 2 + 1;
			}
				break;
			case 71:
			case 72:
			case 73:
			case 74:
			case 75:
			case 76:
			case 77:
			case 78:
			case 79:
			case 80: {
				dmg = 55 + _random.nextInt(lvl - 30) + _npc.getStr() / 2 + 1;
			}
				break;
			case 81:
			case 82:
			case 83:
			case 84:
			case 85:
			case 86:
			case 87:
			case 88:
			case 89:
			case 90: {
				dmg = 65 + _random.nextInt(lvl - 35) + _npc.getStr() / 2 + 1;
			}
				break;
			case 91:
			case 92:
			case 93:
			case 94:
			case 95:
			case 96:
			case 97:
			case 98:
			case 99:
			case 100:
			case 101:
			case 102:
			case 103:
			case 104:
			case 105:
			case 106:
			case 107:
			case 108:
			case 109:
			case 110:
			case 111:
			case 112:
			case 113:
			case 114:
			case 115:
			case 116:
			case 117:
			case 118:
			case 119:
			case 120:
			case 121:
			case 122:
			case 123:
			case 124:
			case 125:
			case 126:
			case 127: {
				dmg = 150 + _random.nextInt(lvl - 50) + _npc.getStr() / 2 + 1;
			}
				break;
			default:
				dmg = _random.nextInt(lvl) + _npc.getStr() / 2 + 1;
				break;
			}
			// 公式变更 end
		}

		// 召唤兽伤害设定
		if (_npc instanceof L1SummonInstance) {
			switch (_npc.getNpcTemplate().get_npcId()) {
			case 45303: { // 火之精灵
				dmg = 20 + _random.nextInt(5) + 1;// 21~25
			}
				break;
			case 45304: { // 水之精灵
				dmg = 10 + _random.nextInt(5) + 1;// 11~15
			}
				break;
			case 45305: // 风之精灵
			case 45306: { // 土之精灵
				dmg = 15 + _random.nextInt(5) + 1;// 16~20
			}
				break;
			case 81050: { // 强力火之精灵
				dmg = 35 + _random.nextInt(15) + 1;// 36~50
			}
				break;
			case 81051: { // 强力水之精灵
				dmg = 20 + _random.nextInt(10) + 1;// 21~30
			}
				break;
			case 81052: { // 强力风之精灵
				dmg = 25 + _random.nextInt(10) + 1;// 26~35
			}
				break;
			case 81053: { // 强力土之精灵
				dmg = 30 + _random.nextInt(10) + 1;// 31~40
			}
				break;
			case 81083:
			case 81084:
			case 81090: { // 欧熊、蟑螂人、骷髅斧手
				dmg = 7 + _random.nextInt(5) + 1;// 8~12
			}
				break;
			case 81085:
			case 81091:
			case 81092: { // 巨大兵蚁、莱肯、艾尔摩士兵
				dmg = 11 + _random.nextInt(5) + 1;// 12~16
			}
				break;
			case 81086:
			case 81093: { // 食人妖精、巨斧牛人
				dmg = 15 + _random.nextInt(5) + 1;// 16~20
			}
				break;
			case 81087:
			case 81088:
			case 81094: { // 食人妖精王、骷髅警卫、骷髅斗士
				dmg = 15 + _random.nextInt(9) + 1;// 20~24
			}
				break;
			case 81095: { // 翼魔
				dmg = 23 + _random.nextInt(5) + 1;// 24~28
			}
				break;
			case 81089: { // 阿鲁巴
				dmg = 10 + _random.nextInt(22) + 1;// 28~32
			}
				break;
			case 81096: { // 魔狼
				dmg = 15 + _random.nextInt(22) + 1;// 28~36
			}
				break;
			case 81097:
			case 81098:
			case 81099: { // 高仑熔岩怪、独眼巨人、梅杜莎
				dmg = 30 + _random.nextInt(10) + 1; // 31~40
			}
				break;
			case 81100:
			case 81101:
			case 81102: { // 魔熊、烈炎兽、亚力安
				dmg = 30 + _random.nextInt(15) + 1; // 36~45
			}
				break;
			case 81103: {// 变形怪首领
				dmg = 50 + _random.nextInt(15) + 1; // 51~65
			}
				break;
			case 81104: {// 黑豹
				dmg = 80 + _random.nextInt(20) + 1; // 81~100
			}
				break;
			default:
				dmg = _random.nextInt(lvl) + _npc.getStr() / 2 + 1;
				break;
			}
		}
		// 召唤兽伤害设定 end

		if (isUndeadDamage()) {
			dmg *= 1.1;
		}

		// 梦岛怪物伤害调整
		switch (_npc.getNpcTemplate().get_npcId()) {
		case 45549:
		case 45551:
		case 45552:
		case 45553:
		case 45554:
		case 45555:
		case 45556:
		case 45557:
		case 45558:
		case 45559:
		case 45560:
		case 45561:
		case 45562: {
			dmg = _random.nextInt(lvl) + _npc.getStr() / 2 + 1;
		}
			break;
		}
		// 梦岛怪物伤害调整 end

		dmg = dmg * getLeverage() / 10;

		// 怪物双重破坏
		if (_npc.hasSkillEffect(17003)) {
			if ((_random.nextInt(100) + 1) <= 33) {
				_attackType = 0x04;
				dmg *= 2;
			}
		}
		// 怪物双重破坏 end

		// 怪物燃烧斗志
		if (_npc.hasSkillEffect(17002)) {
			if ((_random.nextInt(100) + 1) <= 33) {
				double tempDmg = dmg;
				dmg = tempDmg * 1.5;
			}
		}
		// 怪物燃烧斗志 end

		// 怪物反击屏障
		if (_targetNpc.hasSkillEffect(COUNTER_BARRIER)
				&& (_random.nextInt(100) + 1) < 26
				&& (_npc.getNpcTemplate().getBowActId() == 0 || (_npc
						.getNpcTemplate().getBowActId() > 0 && _npc
						.getLocation().getTileLineDistance(
								new Point(_targetX, _targetY)) <= 1))) {
			_npc.broadcastPacket(new S_SkillSound(_targetId, 5846));
			_npc.receiveDamage(_targetNpc, 100);
		}
		// 怪物反击屏障 end

		dmg -= calcNpcDamageReduction();

		if (_npc.isWeaponBreaked()) { // ＮＰＣ中。
			dmg /= 2;
		}

		addNpcPoisonAttack(_npc, _targetNpc);

		// 怪物圣结界
		if (_targetNpc.hasSkillEffect(17004)) {
			dmg /= 2;
		}
		// 怪物圣结界 end

		// 安区宠物对宠物伤害为0
		if ((_npc instanceof L1PetInstance || _npc instanceof L1SummonInstance)
				&& _targetNpc.getZoneType() == 1
				&& (_targetNpc instanceof L1PetInstance || _targetNpc instanceof L1SummonInstance)) {
			dmg = 0;
		}
		// 安区宠物对宠物伤害为0 end
		if (_targetNpc.hasSkillEffect(ICE_LANCE)) {
			dmg = 0;
		}
		if (_targetNpc.hasSkillEffect(EARTH_BIND)) {
			dmg = 0;
		}
		// 冰冻、地屏状态判断
		if (_targetNpc.get_poisonStatus4() == 4
				|| _targetNpc.get_poisonStatus6() == 4) {
			dmg = 0;
		}
		// 冰冻、地屏状态判断 end

		// 怪死公告
		if (_targetNpc.getCurrentHp() <= dmg
				&& _targetNpc.getNpcTemplate().getBroad() == true) {
			L1PcInstance master = null;
			if (_npc instanceof L1SummonInstance) { // 召唤兽
				master = (L1PcInstance) ((L1SummonInstance) _npc).getMaster();
			} else if (_npc instanceof L1PetInstance) { // 宠物
				master = (L1PcInstance) ((L1PetInstance) _npc).getMaster();
			}

			String master_name = ""; // 主人名称
			if (master != null) {
				master_name = master.getName();
			}
			String attack_name = _npc.getName(); // 怪物名称
			String target_name = _targetNpc.getName(); // 怪物名称
			String msg0 = L1WilliamSystemMessage.ShowMessage(1105); // 恭喜
			String msg1 = L1WilliamSystemMessage.ShowMessage(1107); // 杀死了
			L1World.getInstance().broadcastPacketToAll(
					new S_ServerMessage(166,
							msg0 + " ( " + master_name + " ) ",
							L1WilliamSystemMessage.ShowMessage(1124)
									+ attack_name + msg1 + " (" + target_name
									+ ")"));
		}
		// 怪死公告 end

		if (dmg <= 0) {
			_isHit = false;
		}

		return (int) dmg;
	}

	// ●●●● 强化魔法 ●●●●
	private double calcBuffDamage(double dmg) {
		if (_weaponType == 20 || _weaponType == 62) { // 弓场合、近接魔法分处理
			if (_pc.hasSkillEffect(FIRE_WEAPON)) {
				dmg -= 4;
			}
			if (_pc.hasSkillEffect(FIRE_BLESS)) {
				dmg -= 4;
			}
			if (_pc.hasSkillEffect(BURNING_WEAPON)) {
				dmg -= 6;
			}
		} else {
			if (_pc.hasSkillEffect(STORM_EYE)) {
				dmg -= 3;
			}
			if (_pc.hasSkillEffect(STORM_SHOT)) {
				dmg -= 5;
			}
		}

		// 火武器、1.5倍
		if (_pc.hasSkillEffect(BURNING_SPIRIT)
				|| (_pc.hasSkillEffect(ELEMENTAL_FIRE) && _weaponType != 20 && _weaponType != 62)) {
			if ((_random.nextInt(100) + 1) <= 33) {
				double tempDmg = dmg;
				if (_pc.hasSkillEffect(FIRE_WEAPON)) {
					tempDmg -= 4;
				}
				if (_pc.hasSkillEffect(FIRE_BLESS)) {
					tempDmg -= 4;
				}
				if (_pc.hasSkillEffect(BURNING_WEAPON)) {
					tempDmg -= 6;
				}
				if (_pc.hasSkillEffect(BERSERKERS)) {
					tempDmg -= 5;
				}
				double diffDmg = dmg - tempDmg;
				dmg = tempDmg * 1.5 + diffDmg;
			}
		}

		// 燃烧击砍
		if (this._pc.hasSkillEffect(L1SkillId.BURNING_SLASH)) {
			dmg += 7; // 攻击时让敌人受到额外伤害7点
			this._pc.sendPackets(new S_EffectLocation(this._targetX,
					this._targetY, 6591));
			this._pc.broadcastPacket(new S_EffectLocation(this._targetX,
					this._targetY, 6591));
			this._pc.killSkillEffectTimer(L1SkillId.BURNING_SLASH);
		}

		return dmg;
	}

	// ●●●● ＡＣ轻减 ●●●●
	private int calcPcDefense() {
		int ac = Math.max(0, 10 - _targetPc.getAc());
		int acDefMax = _targetPc.getClassFeature().getAcDefenseMax(ac);
		return _random.nextInt(acDefMax + 1);
	}

	// ●●●● ＮＰＣ轻减 ●●●●
	private int calcNpcDamageReduction() {
		return _targetNpc.getNpcTemplate().get_damagereduction();
	}

	// ●●●● 武器材质祝福追加算出 ●●●●
	private int calcMaterialBlessDmg() {
		int damage = 0;
		int undead = _targetNpc.getNpcTemplate().get_undead();
		if ((_weaponMaterial == 14 || _weaponMaterial == 17 || _weaponMaterial == 22)
				&& (undead == 1 || undead == 3)) { // 银、、系系
			damage += _random.nextInt(20) + 1;
		}
		//
		// 祝福武器改成了对所有伤害 这里不用了
		// if (_weaponBless == 0 && (undead == 1 || undead == 2 || undead == 3))
		// { // 祝福武器、、系恶魔系系
		// damage += _random.nextInt(4) + 1;
		// }
		// 神圣武器对不死系增加额外伤害1
		if (_pc.getWeapon() != null && weapon.getHolyEnchant() != 0
				&& (undead == 1 || undead == 3)) {
			damage += 1;
		}
		// 神圣武器对不死系增加额外伤害1 end
		return damage;
	}

	// ●●●● ＮＰＣ夜间攻击力变化 ●●●●
	private boolean isUndeadDamage() {
		boolean flag = false;
		int undead = _npc.getNpcTemplate().get_undead();
		boolean isNight = L1GameTimeClock.getInstance().getGameTime().isNight();
		if (isNight && (undead == 1 || undead == 3)) { // 18～6时、、系系
			flag = true;
		}
		return flag;
	}

	// ●●●● ＮＰＣ毒攻击付加 ●●●●
	private void addNpcPoisonAttack(L1Character attacker, L1Character target) {
		if (_npc.getNpcTemplate().get_poisonatk() != 0) { // 毒攻击
			if (15 >= _random.nextInt(100) + 1) { // 15%确率毒攻击
				if (_npc.getNpcTemplate().get_poisonatk() == 1) { // 通常毒
					// 3秒周期5
					L1DamagePoison
							.doInfection(attacker, target, 3000, 15000, 5);
				} else if (_npc.getNpcTemplate().get_poisonatk() == 2) { // 沉默毒
					L1SilencePoison.doInfection(target);
				} else if (_npc.getNpcTemplate().get_poisonatk() == 4) { // 麻痹毒
					// 20秒后45秒间麻痹
					L1ParalysisPoison.doInfection(target, 20000, 45000);
				}
			}
		} else if (_npc.getNpcTemplate().get_paralysisatk() != 0) { // 麻痹攻击
		}
	}

	// ■■■■ 钢铁ＭＰ吸收量算出 ■■■■
	public void calcStaffOfMana() {
		// SOM钢铁SOM
		if (_weaponId == 126 || _weaponId == 127 || _weaponisMana) { // 新增getItem().isManaItem()
			int som_lvl = _weaponEnchant + 3; // 最大MP吸收量设定
			if (som_lvl < 0) {
				som_lvl = 0;
			}
			int minlvl = 1;
			if (_weaponEnchant > 6) {
				minlvl = _weaponEnchant - 6;
			}
			// MP吸收量取得
			_drainMana = _random.nextInt(som_lvl) + minlvl;
			// 最大MP吸收量16制限
			if (_drainMana > Config.MANA_DRAIN_LIMIT_PER_SOM_ATTACK) {
				_drainMana = Config.MANA_DRAIN_LIMIT_PER_SOM_ATTACK;
			}
		}
	}

	// ■■■■ ＰＣ毒攻击付加 ■■■■
	public void addPcPoisonAttack(L1Character attacker, L1Character target) {
		int chance = _random.nextInt(100) + 1;
		if ((_weaponId == 13 || _weaponId == 44 // FOD、古代
		|| (_weaponId != 0 && _pc.hasSkillEffect(ENCHANT_VENOM))) // 
																	// 中
				&& chance <= 10) {
			// 通常毒、3秒周期、HP-5
			L1DamagePoison.doInfection(attacker, target, 3000, 15000, 5);
		}
	}

	/* ■■■■■■■■■■■■■■ 攻击送信 ■■■■■■■■■■■■■■ */

	public void action() {
		try {
			if (_calcType == PC_PC || _calcType == PC_NPC) {
				if (_pc == null) {
					return;
				}
				if (_pc.isDead()) {
					return;
				}
				if (_target == null) {
					return;
				}
				// 改變面向
				_pc.setHeading(_pc.targetDirection(_targetX, _targetY));

				if (_weaponRange == -1) {// 遠距離武器
					actionPCX1();

				} else {// 近距離武器
					actionPCX2();
				}
			} else if (_calcType == NPC_PC || _calcType == NPC_NPC) {
				if (_npc == null) {
					return;
				}
				if (_npc.isDead()) {
					return;
				}
				if (_target == null) {
					return;
				}
				_npc.setHeading(_npc.targetDirection(_targetX, _targetY)); // 设置新面向

				// 距离2格以上攻击
				final boolean isLongRange = (_npc.getLocation()
						.getTileLineDistance(new Point(_targetX, _targetY)) > 1);
				int bowActId = _npc.getNpcTemplate().getBowActId();
				// 远距离武器
				if (isLongRange && (bowActId > 0)) {
					actionNPCX1();
					// 近距离武器
				} else {
					actionNPCX2();
				}
			}
		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 远距离武器
	 */
	private void actionNPCX1() {
		try {
			int bowActId = _npc.getNpcTemplate().getBowActId();
			// 攻击资讯封包
			_npc.broadcastPacket(new S_UseArrowSkill(_npc, _targetId, bowActId,
					_targetX, _targetY, _damage));

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 近距离武器
	 */
	private void actionNPCX2() {
		try {
			int actId = 0;
			if (getActId() > 0) {
				actId = getActId();

			} else {
				actId = ActionCodes.ACTION_Attack;
			}

			if (_isHit) {// 命中
				_npc.broadcastPacket(new S_AttackPacketNpc(_npc, _target,
						actId, _damage));
			} else {// 未命中
				_npc.broadcastPacket(new S_AttackPacketNpc(_npc, _target, actId));
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 遠距離武器
	 */
	private void actionPCX1() {
		try {
			// 挂机没有箭支的处理
			if (_pc.isActived()) {
				if (_arrow != null) { // 具有箭
					_pc.getInventory().removeItem(_arrow, 1);
					_pc.sendPacketsAll(new S_UseArrowSkill(_pc, _targetId, 66,
							_targetX, _targetY, _damage));
				} else {
					_pc.sendPacketsAll(new S_UseArrowSkill(_pc, _targetId, 66,
							_targetX, _targetY, _damage));
				}
			}
			switch (_weaponType) {
			case 20:// 弓
				switch (_weaponId) {
				case 190: // 沙哈之弓 不具有箭
					if (_arrow != null) { // 具有箭
						_pc.getInventory().removeItem(_arrow, 1);
						_pc.sendPacketsAll(new S_UseArrowSkill(_pc, _targetId,
								66, _targetX, _targetY, _damage));
					} else {
						_pc.sendPacketsAll(new S_UseArrowSkill(_pc, _targetId,
								2349, _targetX, _targetY, _damage));
					}
					break;

				default:// 其他武器 沒有箭
					if (_arrow != null) { // 具有箭
						int arrowGfxid = 66;
						switch (_pc.getTempCharGfx()) {
						case 8842:
						case 8900:// 海露拜
							arrowGfxid = 8904;// 黑
							break;
						case 17535:// 真弓手变身
						case 13725:
							arrowGfxid = 13656;
							break;

						case 8913:
						case 8845:// 朱里安
							arrowGfxid = 8916;// 白
							break;

						case 7959:
						case 7967:
						case 7968:
						case 7969:
						case 7970:// 天上騎士
							arrowGfxid = 7972;// 火
							break;
						}
						_pc.sendPacketsAll(new S_UseArrowSkill(_pc, _targetId,
								arrowGfxid, _targetX, _targetY, _damage));
						_pc.getInventory().removeItem(_arrow, 1);

					} else {
						int aid = 1;
						// 外型編號改變動作
						if (_pc.getTempCharGfx() == 3860) {
							aid = 21;
						}
						_pc.sendPacketsAll(new S_ChangeHeading(_pc));
						// 送出封包(動作)
						_pc.sendPacketsAll(new S_DoActionGFX(_pc.getId(), aid));
					}
				}
				break;

			case 62: // 鐵手甲
				if (_sting != null) {// 具有飛刀
					int stingGfxid = 2989;
					switch (_pc.getTempCharGfx()) {
					case 8842:
					case 8900:// 海露拜
						stingGfxid = 8904;// 黑
						break;

					case 8913:
					case 8845:// 朱里安
						stingGfxid = 8916;// 白
						break;

					case 7959:
					case 7967:
					case 7968:
					case 7969:
					case 7970:// 天上騎士
						stingGfxid = 7972;// 火
						break;
					}
					_pc.sendPacketsAll(new S_UseArrowSkill(_pc, _targetId,
							stingGfxid, _targetX, _targetY, _damage));
					_pc.getInventory().removeItem(_sting, 1);

				} else {// 沒有飛刀
					int aid = 1;
					// 外型編號改變動作
					if (_pc.getTempCharGfx() == 3860) {
						aid = 21;
					}

					_pc.sendPacketsAll(new S_ChangeHeading(_pc));
					// 送出封包(動作)
					_pc.sendPacketsAll(new S_DoActionGFX(_pc.getId(), aid));
				}
				break;
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 近距離武器/空手
	 */
	private void actionPCX2() {
		try {
			if (_isHit) {// 命中
				// System.out.println("命中");
				_pc.sendPacketsAll(new S_AttackPacketPc(_pc, _target,
						_attackType, _damage));

			} else {// 未命中
				if (_targetId > 0) {
					_pc.sendPacketsAll(new S_AttackPacketPc(_pc, _target));

				} else {
					_pc.sendPacketsAll(new S_AttackPacketPc(_pc));
				}
			}

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);
		}
	}

	// 飞道具（矢、）轨道计算
	public void calcOrbit(int cx, int cy, int head) // 起点Ｘ 起点Ｙ 今向方向
	{
		float dis_x = Math.abs(cx - _targetX); // Ｘ方向距离
		float dis_y = Math.abs(cy - _targetY); // Ｙ方向距离
		float dis = Math.max(dis_x, dis_y); // 距离
		float avg_x = 0;
		float avg_y = 0;
		if (dis == 0) { // 目标同位置向方向真直
			if (head == 1) {
				avg_x = 1;
				avg_y = -1;
			} else if (head == 2) {
				avg_x = 1;
				avg_y = 0;
			} else if (head == 3) {
				avg_x = 1;
				avg_y = 1;
			} else if (head == 4) {
				avg_x = 0;
				avg_y = 1;
			} else if (head == 5) {
				avg_x = -1;
				avg_y = 1;
			} else if (head == 6) {
				avg_x = -1;
				avg_y = 0;
			} else if (head == 7) {
				avg_x = -1;
				avg_y = -1;
			} else if (head == 0) {
				avg_x = 0;
				avg_y = -1;
			}
		} else {
			avg_x = dis_x / dis;
			avg_y = dis_y / dis;
		}

		int add_x = (int) Math.floor((avg_x * 15) + 0.59f); // 上下左右优先丸
		int add_y = (int) Math.floor((avg_y * 15) + 0.59f); // 上下左右优先丸

		if (cx > _targetX) {
			add_x *= -1;
		}
		if (cy > _targetY) {
			add_y *= -1;
		}

		_targetX = _targetX + add_x;
		_targetY = _targetY + add_y;
	}

	/* ■■■■■■■■■■■■■■■ 计算结果反映 ■■■■■■■■■■■■■■■ */

	public void commit() {
		if (_isHit) {
			if (_calcType == PC_PC || _calcType == NPC_PC) {
				commitPc();
			} else if (_calcType == PC_NPC || _calcType == NPC_NPC) {
				commitNpc();
			}
		}
		if (_calcType == PC_PC || _calcType == PC_NPC) { // ＰＣ场合
			if (_pc.isVdmg()) {
				/*
				 * String dmgmsg = "输出->"+_damage; if (_damage == 0||!_isHit) {
				 * dmgmsg = "Miss!"; } S_ChatPacket s_chatpacket = new
				 * S_ChatPacket(_target, dmgmsg,Opcodes.S_OPCODE_NORMALCHAT);
				 * _pc.sendPackets(s_chatpacket);
				 */
				if (_damage == 0 || !_isHit) {
					_pc.sendPackets(new S_SkillSound(_target.getId(), 13418));
				} else {
					int units = _damage % 10;
					int tens = (_damage / 10) % 10;
					int hundreads = (_damage / 100) % 10;
					int thousands = (_damage / 1000) % 10;
					int tenthousands = (_damage / 10000) % 10;
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
		// String msg0 = "";
		// String msg1 = " 受到 ";
		// String msg2 = "";
		// String msg3 = "";
		// String msg4 = "";
		if (_calcType == PC_PC || _calcType == PC_NPC) { // ＰＣ场合
			/*
			 * if (_pc.isVdmg()) { String dmgmsg = "输出->"+_damage; if (_damage
			 * == 0||!_isHit) { dmgmsg = "MISS"; } S_ChatPacket s_chatpacket =
			 * new S_ChatPacket(_target, dmgmsg, Opcodes.S_OPCODE_NORMALCHAT);
			 * _pc.sendPackets(s_chatpacket); }
			 */
		} else if (_calcType == NPC_PC) { // ＮＰＣ场合
			// 删除msg0 = _npc.getName();
		}

		// if (_calcType == NPC_PC || _calcType == PC_PC) { // ＰＣ场合
		// msg4 = _targetPc.getName();
		// msg2 = "命中率 " + _hitRate + "%  剩下 " + _targetPc.getCurrentHp();
		// } else if (_calcType == PC_NPC) { // ＮＰＣ场合
		// msg4 = _targetNpc.getName();
		// msg2 = "命中率 " + _hitRate + "%  剩下 " + _targetNpc.getCurrentHp();
		// }
		// msg3 = _isHit ? _damage + " 伤害" : "攻击失败";
		//
		// if (_calcType == PC_PC || _calcType == PC_NPC) { // ＰＣ场合
		// _pc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2, msg3,
		// msg4)); // \f1%0%4%1%3 %2
		// }
		// if (_calcType == NPC_PC || _calcType == PC_PC) { // ＰＣ场合
		// _targetPc.sendPackets(new S_ServerMessage(166, msg0, msg1, msg2,
		// msg3, msg4)); // \f1%0%4%1%3 %2
		// }
	}

	// ●●●● 计算结果反映 ●●●●
	private void commitPc() {
		if (_calcType == PC_PC) {
			if (_drainMana > 0 && _targetPc.getCurrentMp() > 0) {
				if (_drainMana > _targetPc.getCurrentMp()) {
					_drainMana = _targetPc.getCurrentMp();
				}
				short newMp = (short) (_targetPc.getCurrentMp() - _drainMana);
				_targetPc.setCurrentMp(newMp);
				newMp = (short) (_pc.getCurrentMp() + _drainMana);
				_pc.setCurrentMp(newMp);
			}
			damagePcWeaponDurability(); // 武器损伤。
			_targetPc.receiveDamage(_pc, _damage,false);
		} else if (_calcType == NPC_PC) {
			_targetPc.receiveDamage(_npc, _damage,false);
		}
	}

	// ●●●● ＮＰＣ计算结果反映 ●●●●
	private void commitNpc() {
		if (_calcType == PC_NPC) {
			if (_drainMana > 0) {
				int drainValue = _targetNpc.drainMana(_drainMana);
				int newMp = _pc.getCurrentMp() + drainValue;
				_pc.setCurrentMp(newMp);

				if (drainValue > 0) {
					int newMp2 = _targetNpc.getCurrentMp() - drainValue;
					_targetNpc.setCurrentMpDirect(newMp2);
				}
			}

			damageNpcWeaponDurability(); // 武器损伤。
			_targetNpc.receiveDamage(_pc, _damage);
		} else if (_calcType == NPC_NPC) {
			_targetNpc.receiveDamage(_npc, _damage);
		}
	}

	/* ■■■■■■■■■■■■■■■  ■■■■■■■■■■■■■■■ */

	// ■■■■ 时攻击送信 ■■■■
	public void actionCounterBarrier() {
		if (_calcType == PC_PC) {
			_pc.setHeading(_pc.targetDirection(_targetX, _targetY)); // 向
			/*
			 * _pc.sendPackets(new S_AttackMissPacket(_pc, _targetId));
			 * _pc.broadcastPacket(new S_AttackMissPacket(_pc, _targetId));
			 */
			_pc.sendPackets(new S_DoActionGFX(_pc.getId(),
					ActionCodes.ACTION_Damage));
			_pc.broadcastPacket(new S_DoActionGFX(_pc.getId(),
					ActionCodes.ACTION_Damage));
		} else if (_calcType == NPC_PC) {
			// int actId = 0;
			_npc.setHeading(_npc.targetDirection(_targetX, _targetY)); // 向
			/*
			 * if (getActId() > 0) { actId = getActId(); } else { actId =
			 * ActionCodes.ACTION_Attack; } if (getGfxId() > 0) { _npc
			 * .broadcastPacket(new S_UseAttackSkill(_target, _npc .getId(),
			 * getGfxId(), _targetX, _targetY, actId, 0)); } else {
			 * _npc.broadcastPacket(new S_AttackMissPacket(_npc, _targetId,
			 * actId)); }
			 */
			_npc.broadcastPacket(new S_DoActionGFX(_npc.getId(),
					ActionCodes.ACTION_Damage));
		}
	}

	// ■■■■ 相手攻击对有效判别 ■■■■
	public boolean isShortDistance() {
		boolean isShortDistance = true;
		if (_calcType == PC_PC) {
			if (_weaponType == 20 || _weaponType == 62) { // 弓
				isShortDistance = false;
			}
		} else if (_calcType == NPC_PC) {
			boolean isLongRange = (_npc.getLocation().getTileLineDistance(
					new Point(_targetX, _targetY)) > 1);
			int bowActId = _npc.getNpcTemplate().getBowActId();
			// 距离2以上、攻击者弓ID场合远攻击
			if (isLongRange && bowActId > 0) {
				isShortDistance = false;
			}
		}
		return isShortDistance;
	}

	// ■■■■ 反映 ■■■■
	public void commitCounterBarrier() {
		int damage = calcCounterBarrierDamage();
		if (damage == 0) {
			return;
		}
		if (_calcType == PC_PC) {
			_pc.receiveDamage(_targetPc, damage,false);
		} else if (_calcType == NPC_PC) {
			_npc.receiveDamage(_targetPc, damage);
		}
	}

	// ●●●● 算出 ●●●●
	private int calcCounterBarrierDamage() {
		int damage = 0;
		L1ItemInstance weapon = null;
		weapon = _targetPc.getWeapon();
		int weaponType = weapon.getItem().getType();
		if (weapon != null && weaponType == 3) { // 两手剑
			// (BIG最大+强化数+追加)*2
			damage = (weapon.getItem().getDmgLarge() + weapon.getEnchantLevel() + weapon
					.getItem().getDmgModifier()) * 2;
		}
		return damage;
	}

	/*
	 * 武器损伤。 对NPC场合、损伤确率10%。祝福武器3%。
	 */
	private void damageNpcWeaponDurability() {
		int chance = 10;
		int bchance = 3;

		/*
		 * 损伤NPC、素手、损伤武器使用场合何。
		 */
		if (_calcType != PC_NPC
				|| _targetNpc.getNpcTemplate().is_hard() == false
				|| _weaponType == 0 || weapon.getItem().get_canbedmg() == 0) {
			return;
		}
		if (_pc.hasSkillEffect(L1SkillId.SOUL_OF_FLAME)) {
			return;
		}
		// 通常武器咒武器
		if ((_weaponBless == 1 || _weaponBless == 2)
				&& ((_random.nextInt(100) + 1) < chance)) {
			// \f1%0损伤。
			_pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
			_pc.getInventory().receiveDamage(weapon);
		}
		// 祝福武器
		if (_weaponBless == 0 && ((_random.nextInt(100) + 1) < bchance)) {
			// \f1%0损伤。
			_pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
			_pc.getInventory().receiveDamage(weapon);
		}
	}

	/*
	 * 武器损伤。 损伤确率10%
	 */
	private void damagePcWeaponDurability() {
		// PvP以外、素手、弓、、未使用场合何
		if (_calcType != PC_PC || _weaponType == 0 || _weaponType == 20
				|| _weaponType == 62
				|| _targetPc.hasSkillEffect(BOUNCE_ATTACK) == false) {
			return;
		}

		if (_random.nextInt(100) + 1 <= 10) {
			// \f1%0损伤。
			_pc.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
			_pc.getInventory().receiveDamage(weapon);
		}
	}

	// ●●●● 武器の属性強化による追加ダメージ算出 ●●●●
	private int calcAttrEnchantDmg() {
		int damage = 0;
		if (_weaponAttrEnchantLevel == 1) {
			damage = 1;
		} else if (_weaponAttrEnchantLevel == 2) {
			damage = 3;
		} else if (_weaponAttrEnchantLevel == 3) {
			damage = 5;
		}
		int resist = 0;
		if (_calcType == PC_PC) {
			if (_weaponAttrEnchantKind == 1) { // 地
				resist = _targetPc.getEarth();
			} else if (_weaponAttrEnchantKind == 2) { // 火
				resist = _targetPc.getFire();
			} else if (_weaponAttrEnchantKind == 4) { // 水
				resist = _targetPc.getWater();
			} else if (_weaponAttrEnchantKind == 8) { // 風
				resist = _targetPc.getWind();
			}
		} else if (_calcType == PC_NPC) {
			if (_weaponAttrEnchantKind == 1) { // 地
				resist = _targetNpc.getEarth();
			} else if (_weaponAttrEnchantKind == 2) { // 火
				resist = _targetNpc.getFire();
			} else if (_weaponAttrEnchantKind == 4) { // 水
				resist = _targetNpc.getWater();
			} else if (_weaponAttrEnchantKind == 8) { // 風
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
		double attrCoefficient = 1 - attrDeffence;

		damage *= attrCoefficient;

		return damage;
	}

	// 范围伤害
	private static void areaskill(L1PcInstance pc, L1Character cha, int d,
			int vis) {
		for (L1Object visibleObjects : L1World.getInstance().getVisibleObjects(
				cha, vis)) {
			if (visibleObjects == null)
				continue;

			if (visibleObjects instanceof L1MonsterInstance) {
				L1NpcInstance targetNpc = (L1NpcInstance) visibleObjects;
				if (targetNpc.getCurrentHp() > 0
						&& targetNpc.getHiddenStatus() == 0
						&& targetNpc.getGfxId() != 6249
						&& targetNpc.getGfxId() != 6335
						&& targetNpc.getGfxId() != 6339
						&& !targetNpc.hasSkillEffect(50)
						&& !targetNpc.hasSkillEffect(78)
						&& !targetNpc.hasSkillEffect(80)
						&& !targetNpc.hasSkillEffect(157)
						&& targetNpc.get_poisonStatus4() != 4
						&& targetNpc.get_poisonStatus6() != 4
						&& targetNpc.glanceCheck(targetNpc.getX(),
								targetNpc.getY()) == true) {
					if (!targetNpc.hasSkillEffect(Skill_AJ_1_2)) { // 效果延迟
						targetNpc.broadcastPacket(new S_SkillSound(targetNpc
								.getId(), 3362));
						targetNpc.broadcastPacket(new S_DoActionGFX(targetNpc
								.getId(), ActionCodes.ACTION_Damage));
						targetNpc.setSkillEffect(Skill_AJ_1_2, 1800);
					}
					targetNpc.receiveDamage(pc, d); // 怪被范围魔法打死的怪经验会给玩家
				}
			}
		}
	}

	// 范围伤害 end

	private int dk_dmgUp() {
		int dmg = 0;
		if (_weaponType == 24) {// 锁炼剑
			long h_time = Calendar.getInstance().getTimeInMillis() / 1000;// 换算为秒
			final int random = _random.nextInt(100) + 1;
			switch (_pc.get_weaknss()) {
			case 0:
				if (random <= 24) {
					_pc.set_weaknss(1, h_time);
				}
				break;
			case 1:
				if (_pc.isFoeSlayer()) {// 使用屠宰者
					return 0;
				}
				if (random <= 24) {
					_pc.set_weaknss(2, h_time);
				}
				dmg += 3;// 修改为固定3攻 hjx1000
				break;
			case 2:
				if (_pc.isFoeSlayer()) {// 使用屠宰者
					return 0;
				}
				if (random <= 24) {
					_pc.set_weaknss(3, h_time);
				}
				dmg += 6;// 修改为固定6攻 hjx1000
				break;
			case 3:
				if (_pc.isFoeSlayer()) {// 使用屠宰者
					return 0;
				}
				if (random <= 24) {
					_pc.set_weaknss(3, h_time);
				}
				dmg += 9; // 修改为固定9攻 hjx1000
				break;
			}
		}
		return dmg;
	}
}
