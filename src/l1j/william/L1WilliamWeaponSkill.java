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
package l1j.william;

import java.util.Random;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.poison.L1Poison2;
import l1j.server.server.model.poison.L1Poison3;
import l1j.server.server.model.poison.L1Poison4;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.templates.L1Item;
import l1j.server.server.world.L1World;

// Referenced classes of package l1j.server.server.model:
// L1PcInstance

public class L1WilliamWeaponSkill {


	private int _weaponId;

	private int _probability;

	private int _material;

	private int _materialCount;

	private int _fixDamage;

	private int _randomDamage;

	private int _strDouble;

	private int _dexDouble;

	private int _intDouble;

	private int _wisDouble;

	private int _areaAtk;

	private int _gfxId;

	private int _gfxIdTarget;

	private int _arrowType;

	private int _effectId;

	private int _effectTime;

	public L1WilliamWeaponSkill(int weaponId, int probability, int material, 
		int materialCount, int fixDamage, int randomDamage, 
		int strDouble, int dexDouble, int intDouble, int wisDouble, 
		int areaAtk, int gfxId, int gfxIdTarget, int arrowType, int effectId, int effectTime) {

	_weaponId = weaponId;
	_probability = probability;
	_material = material;
	_materialCount = materialCount;
	_fixDamage = fixDamage;
	_randomDamage = randomDamage;
	_strDouble = strDouble;
	_dexDouble = dexDouble;
	_intDouble = intDouble;
	_wisDouble = wisDouble;
	_areaAtk = areaAtk;
	_gfxId = gfxId;
	_gfxIdTarget = gfxIdTarget;
	_arrowType = arrowType;
	_effectId = effectId;
	_effectTime = effectTime;
	}

	public int getWeaponId() {
		return _weaponId;
	}

	public int getProbability() {
		return _probability;
	}

	public int getMaterial() {
		return _material;
	}

	public int getMaterialCount() {
		return _materialCount;
	}

	public int getFixDamage() {
		return _fixDamage;
	}

	public int getRandomDamage() {
		return _randomDamage;
	}

	public int getStrDouble() {
		return _strDouble;
	}

	public int getDexDouble() {
		return _dexDouble;
	}

	public int getIntDouble() {
		return _intDouble;
	}

	public int getWisDouble() {
		return _wisDouble;
	}

	public int getAreaAtk() {
		return _areaAtk;
	}

	public int getGfxId() {
		return _gfxId;
	}

	public int getGfxIdTarget() {
		return _gfxIdTarget;
	}

	public int getArrowType() {
		return _arrowType;
	}

	public int getEffectId() {
		return _effectId;
	}

	public int getEffectTime() {
		return _effectTime;
	}

	public static double getWeaponSkillDamage(L1PcInstance pc, L1Character cha,
			int weaponId,int enchant,int safeenchant) {
		if (weaponId == 124){
			return 0;//巴风特魔杖
		}
		L1WilliamWeaponSkill weaponSkill = WeaponSkill.getInstance().getTemplate(
				weaponId);
		if (pc == null || cha == null || weaponSkill == null) {
			return 0;
		}

		Random random = new Random();
		int chance = random.nextInt(100) + 1;
		int rnd = 0;
		if (enchant > safeenchant){
			rnd = enchant * 2;//每过安定1 机率+2
		}
		if (weaponSkill.getProbability() + rnd < chance) {
			return 0;
		}
		
		if (weaponSkill.getMaterial() != 0 && weaponSkill.getMaterialCount() != 0) { // 发动媒介判断
			if (pc.getInventory().checkItem(weaponSkill.getMaterial(), weaponSkill.getMaterialCount())) {
				pc.getInventory().consumeItem(weaponSkill.getMaterial(), weaponSkill.getMaterialCount());
			} else {
				L1Item weapon = ItemTable.getInstance().getTemplate(weaponId);
				L1Item item = ItemTable.getInstance().getTemplate(weaponSkill.getMaterial());
				pc.sendPackets(new S_ServerMessage(337, weapon.getName() + "：魔法媒介 " + item.getName() + " (" + weaponSkill.getMaterialCount() + ") "));
				return 0;
			}
		}

		int gfxId = weaponSkill.getGfxId();
		if (gfxId != 0) { //魔法图像
			if (weaponSkill.getGfxIdTarget() == 1 && weaponSkill.getArrowType() != 0) { //显示对象是攻击目标 && 飞行效果
				S_UseAttackSkill packet = new S_UseAttackSkill(pc, cha.getId(), gfxId,cha.getX(),cha.getY(),0,false);
				pc.sendPackets(packet);
				pc.broadcastPacket(packet);
			} else if (weaponSkill.getGfxIdTarget() == 1) { // 显示对象是攻击目标
				pc.sendPackets(new S_SkillSound(cha.getId(), gfxId));
				pc.broadcastPacket(new S_SkillSound(cha.getId(), gfxId));
			} else { // 显示对象是自己
				pc.sendPackets(new S_SkillSound(pc.getId(), gfxId));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), gfxId));
			}
		}

		int damage = 0;

		int randomDamage = weaponSkill.getRandomDamage();
		if (randomDamage != 0) { // 随机伤害
			damage = random.nextInt(randomDamage);
		}
		if (weaponSkill.getStrDouble() != 0) { // 力量加成
			byte Str = (byte) pc.getStr();
			damage += Str * weaponSkill.getStrDouble();
		}
		if (weaponSkill.getDexDouble() != 0) { // 敏捷加成
			byte Dex = (byte) pc.getDex();
			damage += Dex * weaponSkill.getDexDouble();
		}
		if (weaponSkill.getIntDouble() != 0) { // 智力加成
			byte Int = (byte) pc.getInt();
			damage += Int * weaponSkill.getIntDouble();
		}
		if (weaponSkill.getWisDouble() != 0) { // 精神加成
			byte Wis = (byte) pc.getWis();
			damage += Wis * weaponSkill.getWisDouble();
		}
		if (weaponSkill.getAreaAtk() > 0) { // 范围伤害
			areaskill(pc, damage, weaponSkill.getAreaAtk()) ;
		}
		
		//负面效果判断
		int effectTime = weaponSkill.getEffectTime();
		if (effectTime > 0) {
			effectTime = effectTime * 1000;
		}

		switch(weaponSkill.getEffectId()) 
		{
			case 1: {//冲晕效果
				if (cha.get_poisonStatus2() != 4 && 
					cha.get_poisonStatus4() != 4 && 
					cha.get_poisonStatus6() != 4 && 
					!cha.hasSkillEffect(50) &&
					!cha.hasSkillEffect(78) &&
					!cha.hasSkillEffect(80) &&
					!cha.hasSkillEffect(157)) {
					L1Poison2 poison = new L1Poison2();
					boolean success = poison.handleCommands((L1Object) cha, 4, effectTime, 0);
					if (success == true) {
						cha.add_poison2(poison);
						L1EffectSpawn.getInstance()
							.spawnEffect(81162, effectTime,
							cha.getX(), cha.getY(), cha.getMapId());
					}
				}
			}
			break;
			case 2: {//束缚效果
				if (cha.get_poisonStatus3() != 4 &&
					cha.get_poisonStatus4() != 4 &&
					cha.get_poisonStatus6() != 4 &&
					!cha.hasSkillEffect(50) &&
					!cha.hasSkillEffect(78) &&
					!cha.hasSkillEffect(80) &&
					!cha.hasSkillEffect(157)) {
					L1Poison3 poison = new L1Poison3();
					boolean success = poison.handleCommands((L1Object) cha, 4, effectTime, 0);
					if (success == true) {
						cha.add_poison3(poison);
						L1EffectSpawn.getInstance()
							.spawnEffect(90001, effectTime,
							cha.getX(), cha.getY(), cha.getMapId());
					}
				}
			}
			break;
			case 3: {//冰冻效果
				if (cha.get_poisonStatus4() != 4 &&
					cha.get_poisonStatus6() != 4 &&
					!cha.hasSkillEffect(50) &&
					!cha.hasSkillEffect(78) &&
					!cha.hasSkillEffect(80) &&
					!cha.hasSkillEffect(157)) {
					L1Poison4 poison = new L1Poison4();
					boolean success = poison.handleCommands((L1Object) cha, 4, effectTime, 0);
					if (success == true) {
						cha.add_poison4(poison);
						L1EffectSpawn.getInstance()
							.spawnEffect(81168, effectTime,
							cha.getX(), cha.getY(), cha.getMapId());
					}
				}
			}
			break;
			case 4: {//沉默效果
				cha.setSkillEffect(64, effectTime);
				pc.sendPackets(new S_SkillSound(cha.getId(), 2177));
				pc.broadcastPacket(new S_SkillSound(cha.getId(), 2177));
			}
			break;
			case 5: {//药霜效果
				cha.setSkillEffect(71, effectTime);
				pc.sendPackets(new S_SkillSound(cha.getId(), 2232));
				pc.broadcastPacket(new S_SkillSound(cha.getId(), 2232));
			}
			break;
			case 6: {//缓速效果
				if (cha instanceof L1PcInstance) {
					L1PcInstance player = (L1PcInstance) cha;
					if (player.getHasteItemEquipped() > 0) {
						return weaponSkill.getFixDamage() + damage;
					}
				}

				if (cha.getMoveSpeed() == 0) {
					if (cha instanceof L1PcInstance) {
						L1PcInstance player = (L1PcInstance) cha;
						player.sendPackets(new S_SkillHaste(player.getId(), 2, effectTime));
					}
					cha.broadcastPacket(new S_SkillHaste(cha.getId(), 2, effectTime));
					cha.setMoveSpeed(2);
					cha.setSkillEffect(29, effectTime);
				} else if (cha.getMoveSpeed() == 1) {
					int skillNum = 0;
					if (cha.hasSkillEffect(43)) {
						skillNum = 43;
					} else if (cha.hasSkillEffect(54)) {
						skillNum = 54;
					} else if (cha.hasSkillEffect(1001)) {
						skillNum = 1001;
					}
					if (skillNum != 0) {
						cha.removeSkillEffect(skillNum);
						cha.removeSkillEffect(29);
						cha.setMoveSpeed(0);
						return weaponSkill.getFixDamage() + damage;
					}
				} else if (cha.getMoveSpeed() == 2) {
					cha.setSkillEffect(29, effectTime);
				}
				//pc.sendPackets(new S_SkillSound(cha.getId(), 752));
				//pc.broadcastPacket(new S_SkillSound(cha.getId(), 752));
			}
			break;
			case 7: {//毒咒效果
				L1DamagePoison.doInfection(pc, cha, 2000,15000, 30); // 2秒30点伤害
				//pc.sendPackets(new S_SkillSound(cha.getId(), 745));
				//pc.broadcastPacket(new S_SkillSound(cha.getId(), 745));
			}
			break;
			case 8: {//黑暗之影
				if (cha instanceof L1PcInstance) {
					L1PcInstance player = (L1PcInstance) cha;
					if (player.hasSkillEffect(1012)) {
						player.sendPackets(new S_CurseBlind(2));
					} else {
						player.sendPackets(new S_CurseBlind(1));
					}
				}
				cha.setSkillEffect(40, effectTime);
				//pc.sendPackets(new S_SkillSound(cha.getId(), 2175));
				//pc.broadcastPacket(new S_SkillSound(cha.getId(), 2175));
			}
			break;
			case 9: {//坏物术
				if (cha instanceof L1PcInstance) {
					L1PcInstance player = (L1PcInstance) cha;
					L1ItemInstance weapon = player.getWeapon();
					if (weapon != null) {
						int weaponDamage = random.nextInt(pc
								.getInt() / 3) + 1;
						// \f1%0损伤。
						player.sendPackets(new S_ServerMessage(268, weapon.getLogName()));
						player.getInventory().receiveDamage(weapon,
								weaponDamage);
					}
				} else {
					((L1NpcInstance) cha).setWeaponBreaked(true);
				}
				pc.sendPackets(new S_SkillSound(cha.getId(), 172));
				pc.broadcastPacket(new S_SkillSound(cha.getId(), 172));
			}
			break;
			case 10: {//吸血鬼之吻
				int drainHp = (weaponSkill.getFixDamage() + damage) / 2;
				if (cha.getCurrentHp() < drainHp) {
					drainHp = cha.getCurrentHp();
				}
			
				if ((pc.getCurrentHp() + drainHp) > pc.getMaxHp()) {
					pc.setCurrentHp(pc.getMaxHp());
				} else {
					pc.setCurrentHp(pc.getCurrentHp() + drainHp);
				}
				//S_UseAttackSkill packet = new S_UseAttackSkill(pc, cha, 236, false);
				//pc.sendPackets(packet);
				//pc.broadcastPacket(packet);
			}
			break;
			case 11: {//魔力夺取
				//pc.sendPackets(new S_SkillSound(cha.getId(), 2171));
				//pc.broadcastPacket(new S_SkillSound(cha.getId(), 2171));
				int radMp = random.nextInt(8) + 3;
				int drainMana = radMp + (pc.getInt() / 2);
				if (cha.getCurrentMp() < drainMana) {
					drainMana = cha.getCurrentMp();
					cha.setCurrentMp(0);
				} else {
					cha.setCurrentMp(cha.getCurrentMp() - drainMana);
				}
			
				if ((pc.getCurrentMp() + drainMana) > pc.getMaxMp()) {
					pc.setCurrentMp(pc.getMaxMp());
				} else {
					pc.setCurrentMp(pc.getCurrentMp() + drainMana);
				}
			}
			break;
			case 12: {//弱化术
				if (cha.hasSkillEffect(47)) {
					cha.setSkillEffect(47, effectTime);
				} else {
					cha.addDmgup(-5);
					cha.addHitup(-1);
					cha.setSkillEffect(47, effectTime);
				}
				pc.sendPackets(new S_SkillSound(cha.getId(), 2228));
                  	pc.broadcastPacket(new S_SkillSound(cha.getId(), 2228));
			}
			break;
			case 13: {//疾病术
				if (cha.hasSkillEffect(56)) {
					cha.setSkillEffect(56, effectTime);
				} else {
					cha.addHitup(-6);
					cha.addAc(12);
					cha.setSkillEffect(56, effectTime);
				}
				pc.sendPackets(new S_SkillSound(cha.getId(), 2230));
				pc.broadcastPacket(new S_SkillSound(cha.getId(), 2230));
			}
			break;
			/*需修改case 14: {//变形术
				if (cha != null) {
					int[] polyArray = { 29, 945, 947, 979, 1037, 1039, 3860, 3861, 3862,
						3863, 3864, 3865, 3904, 3906, 95, 146, 2374, 2376, 2377, 2378,
						3866, 3867, 3868, 3869, 3870, 3871, 3872, 3873, 3874, 3875,
						3876 };
					int pid = random.nextInt(polyArray.length);
					int polyId = polyArray[pid];

					if (cha instanceof L1PcInstance) {
						L1ItemInstance l1item = pc.getInventory().findItemId(20281);
						if (l1item != null && l1item.isEquipped()) {
							pc.sendPackets(new S_ShowPolyList(pc.getId()));
							pc.sendPackets(new S_ServerMessage(966)); // string-j.tbl:968行目
							// 魔法力保护。
							// 变身际、他人自分变身时出、足时出以外。
						} else {
							L1Skills skillTemp = SkillsTable.getInstance().getTemplate(67);
							L1PolyMorph.doPoly(pc, polyId, skillTemp.getBuffDuration());
							}
					} else if (cha instanceof L1MonsterInstance) {
						L1MonsterInstance mob = (L1MonsterInstance) cha;
						if (mob.getLevel() < 50) {
							mob.broadcastPacket(new S_ChangeShape(mob.getId(), polyId));
						}
					}
					pc.sendPackets(new S_SkillSound(cha.getId(), 231));
                    pc.broadcastPacket(new S_SkillSound(cha.getId(), 231));
				}
			}
			break;需修改*/
		}
		//负面效果判断 end

		return weaponSkill.getFixDamage() + damage;
	}

	public static double getBaphometStaffDamage(L1PcInstance pc, L1Character cha) {
		double dmg = 0;
		Random random = new Random();
		int chance = random.nextInt(100) + 1;
		if (15 >= chance) {
			int magicBounus = pc.getMagicBonus();
			int level = pc.getLevel() / 4;
			if (level > 10) {
				level = 10;
			}
			int sp = magicBounus + level;
			int intel = pc.getInt();
			int isBsk = 0;
			if (pc.hasSkillEffect(L1SkillId.BERSERKERS)) {
				isBsk = 2000;
			}
			double cor = 100 + (isBsk + intel * 200) / 300;
			dmg = intel / 4 + sp * 3 * cor / 100 + random
					.nextInt(sp * intel / 8 + 20);
			S_UseAttackSkill packet = new S_UseAttackSkill(pc, cha.getId(),cha.getX(),cha.getY(),129,0, false);
			pc.sendPackets(packet);
			pc.broadcastPacket(packet);
		}
		return dmg;
	}

	public static double getDiceDaggerDamage(L1PcInstance pc,
			L1PcInstance targetPc, L1ItemInstance weapon) {
		double dmg = 0;
		Random random = new Random();
		int chance = random.nextInt(100) + 1;
		if (3 >= chance) {
			dmg = targetPc.getCurrentHp() * 2 / 3;
			if (targetPc.getCurrentHp() - dmg < 0) {
				dmg = 0;
			}
			String msg = weapon.getLogName();
			pc.sendPackets(new S_ServerMessage(158, msg));
			// \f1%0蒸。
			pc.getInventory().removeItem(weapon, 1);
		}
		return dmg;
	}

	private static void areaskill( L1PcInstance npc, int d, int vis) {
		//(以自身)计算攻击范围-使用方式areaskill(pc,(int)d,几格的范围)
		for(L1Object visibleObjects : L1World.getInstance().getVisibleObjects(npc, vis))
		{
			if(visibleObjects == null)
				continue;

			if(visibleObjects instanceof L1MonsterInstance) {
				L1NpcInstance targetNpc = (L1NpcInstance) visibleObjects;
				targetNpc.receiveDamage(npc, d); // 怪被范围魔法打死的怪经验会给玩家
			}
		}
	}
}
