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
package l1j.server.server.model.skill;

import static l1j.server.server.model.skill.L1SkillId.BLOODLUST;
import static l1j.server.server.model.skill.L1SkillId.GUARD_BRAKE;
import static l1j.server.server.model.skill.L1SkillId.ABSOLUTE_BARRIER;
import static l1j.server.server.model.skill.L1SkillId.ADVANCE_SPIRIT;
import static l1j.server.server.model.skill.L1SkillId.AITIME;
import static l1j.server.server.model.skill.L1SkillId.AI_ONLIN_SKILLID;
import static l1j.server.server.model.skill.L1SkillId.BERSERKERS;
import static l1j.server.server.model.skill.L1SkillId.BLIND_HIDING;
import static l1j.server.server.model.skill.L1SkillId.BRAVE_AURA;
import static l1j.server.server.model.skill.L1SkillId.BURNING_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.CLEAR_MIND;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_0_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_0_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_1_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_1_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_2_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_2_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_3_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_3_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_4_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_4_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_5_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_5_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_6_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_6_S;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_7_N;
import static l1j.server.server.model.skill.L1SkillId.COOKING_1_7_S;
import static l1j.server.server.model.skill.L1SkillId.CURSE_BLIND;
import static l1j.server.server.model.skill.L1SkillId.DARKNESS;
import static l1j.server.server.model.skill.L1SkillId.DISEASE;
import static l1j.server.server.model.skill.L1SkillId.DRESS_DEXTERITY;
import static l1j.server.server.model.skill.L1SkillId.DRESS_MIGHTY;
import static l1j.server.server.model.skill.L1SkillId.EARTH_BIND;
import static l1j.server.server.model.skill.L1SkillId.EARTH_BLESS;
import static l1j.server.server.model.skill.L1SkillId.EARTH_SKIN;
import static l1j.server.server.model.skill.L1SkillId.ELEMENTAL_FALL_DOWN;
import static l1j.server.server.model.skill.L1SkillId.ELEMENTAL_PROTECTION;
import static l1j.server.server.model.skill.L1SkillId.ENTANGLE;
import static l1j.server.server.model.skill.L1SkillId.FIRE_BLESS;
import static l1j.server.server.model.skill.L1SkillId.FIRE_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.FOG_OF_SLEEPING;
import static l1j.server.server.model.skill.L1SkillId.GLOWING_AURA;
import static l1j.server.server.model.skill.L1SkillId.GREATER_HASTE;
import static l1j.server.server.model.skill.L1SkillId.GUAJI_AI_SKILLID;
import static l1j.server.server.model.skill.L1SkillId.HASTE;
import static l1j.server.server.model.skill.L1SkillId.HOLY_WALK;
import static l1j.server.server.model.skill.L1SkillId.ICE_LANCE;
import static l1j.server.server.model.skill.L1SkillId.IRON_SKIN;
import static l1j.server.server.model.skill.L1SkillId.LIGHT;
import static l1j.server.server.model.skill.L1SkillId.MASS_SLOW;
import static l1j.server.server.model.skill.L1SkillId.MOVING_ACCELERATION;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_DEX;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_STR;
import static l1j.server.server.model.skill.L1SkillId.RESIST_ELEMENTAL;
import static l1j.server.server.model.skill.L1SkillId.RESIST_MAGIC;
import static l1j.server.server.model.skill.L1SkillId.RETURN_TO_NATURE;
import static l1j.server.server.model.skill.L1SkillId.SHADOW_ARMOR;
import static l1j.server.server.model.skill.L1SkillId.SHAPE_CHANGE;
import static l1j.server.server.model.skill.L1SkillId.SHIELD;
import static l1j.server.server.model.skill.L1SkillId.SHINING_AURA;
import static l1j.server.server.model.skill.L1SkillId.SLOW;
import static l1j.server.server.model.skill.L1SkillId.STATUS_BLUE_POTION;
import static l1j.server.server.model.skill.L1SkillId.STATUS_BRAVE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_CHAT_PROHIBITED;
import static l1j.server.server.model.skill.L1SkillId.STATUS_DEX_POISON;
import static l1j.server.server.model.skill.L1SkillId.STATUS_ELFBRAVE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_FREEZE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_HASTE;
import static l1j.server.server.model.skill.L1SkillId.STATUS_STR_POISON;
import static l1j.server.server.model.skill.L1SkillId.STATUS_UNDERWATER_BREATH;
import static l1j.server.server.model.skill.L1SkillId.STATUS_WISDOM_POTION;
import static l1j.server.server.model.skill.L1SkillId.STORM_EYE;
import static l1j.server.server.model.skill.L1SkillId.STORM_SHOT;
import static l1j.server.server.model.skill.L1SkillId.WAITTIME;
import static l1j.server.server.model.skill.L1SkillId.WEAKNESS;
import static l1j.server.server.model.skill.L1SkillId.WIND_SHACKLE;
import static l1j.server.server.model.skill.L1SkillId.WIND_SHOT;
import static l1j.server.server.model.skill.L1SkillId.WIND_WALK;
import static l1j.william.New_Id.Skill_AJ_0_1;
import static l1j.william.New_Id.Skill_AJ_0_3;
import static l1j.william.New_Id.Skill_AJ_1_10;
import static l1j.william.New_Id.Skill_AJ_1_11;
import static l1j.william.New_Id.Skill_AJ_1_12;
import static l1j.william.New_Id.Skill_AJ_1_13;
import static l1j.william.New_Id.Skill_AJ_1_14;
import static l1j.william.New_Id.Skill_AJ_1_6;
import static l1j.william.New_Id.Skill_AJ_1_7;
import static l1j.william.New_Id.Skill_AJ_1_9;

import java.util.concurrent.ScheduledFuture;

import l1j.server.Config;
import l1j.server.server.ActionCodes;//门 
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.EtcItemSkillTable;
import l1j.server.server.datatables.ItemTable;//获得道具 
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;//获得道具 
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1DoorInstance;//门 
import l1j.server.server.model.Instance.L1GuardianInstance;
import l1j.server.server.model.Instance.L1ItemInstance;//获得道具 
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_Dexup;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Light;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_PacketBoxWindShackle;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconAura;
import l1j.server.server.serverpackets.S_SkillIconBlessOfEva;
import l1j.server.server.serverpackets.S_SkillIconShield;
import l1j.server.server.serverpackets.S_Strup;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.world.L1World;
import l1j.william.L1WilliamSystemMessage;
//门 
//门 

public interface L1SkillTimer {
	public int getRemainingTime();

	public void begin();

	public void end();

	public void kill();
}

/**
 * 魔法结束
 * 
 * 
 * QQ：1043567675 by：亮修改 2020年5月1日 上午9:38:36
 */
class L1SkillStop {
	public static void stopSkill(L1Character cha, int skillId) {
		// 变更成switch
		switch (skillId) {
		case LIGHT: { // 
			if (cha instanceof L1PcInstance) {
				/*
				 * 删除if (!cha.isInvisble()) { L1PcInstance pc = (L1PcInstance)
				 * cha; pc.sendPackets(new S_Light(pc.getId(), 0));
				 * pc.broadcastPacket(new S_Light(pc.getId(), 0)); }删除
				 */
				// 亮度
				L1PcInstance pc = (L1PcInstance) cha;
				pc.setPcLight(0);

				int item = 0;
				for (Object Light : pc.getInventory().getItems()) {
					L1ItemInstance OwnLight = (L1ItemInstance) Light;
					if ((OwnLight.getItem().getItemId() == 40001
							|| OwnLight.getItem().getItemId() == 40002
							|| OwnLight.getItem().getItemId() == 40004 || OwnLight
							.getItem().getItemId() == 40005)
							&& OwnLight.getEnchantLevel() != 0) {
						if (pc.getPcLight() < OwnLight.getItem()
								.getLightRange()) {
							pc.setPcLight(OwnLight.getItem().getLightRange());
							if (OwnLight.getItem().getItemId() == 40004) {
								item = 40004;
							}
						}
					}
				}

				pc.sendPackets(new S_Light(pc.getId(), pc.getPcLight()));
				if (!pc.isInvisble() && item != 40004) {// 非隐身中跟魔法灯笼除外
					pc.broadcastPacket(new S_Light(pc.getId(), pc.getPcLight()));
				}
				// 亮度 end
			}
		}
			break;

		// 龙骑士技能实装
		case GUARD_BRAKE: {// 护卫毁灭
			if (cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) cha;
				pc.addAc(-10);
			}
		}
		break;
		
		case BLOODLUST: // 血之渴望1
            cha.setBraveSpeed(0);
            if (cha instanceof L1PcInstance) {
                final L1PcInstance pc = (L1PcInstance) cha;
                pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
                pc.broadcastPacket(new S_SkillBrave(pc.getId(), 0, 0));
            }
            break;
	

		case GUAJI_AI_SKILLID: {
			if (cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) cha;
				if (!pc.getGuaJiAI()) {
					if (pc.getNetConnection() != null) {
						pc.getNetConnection().kick();
					}
				}
			}

		}
			break;
		case AI_ONLIN_SKILLID: {
			if (cha instanceof L1PcInstance) {
				final L1PcInstance pc = (L1PcInstance) cha;
				final L1ItemInstance item = ItemTable.getInstance().createItem(
						Config.AI_ONLIN_ITEMID);
				if (item != null) {
					item.setCount(Config.AI_ONLIN_ITEMCOUNT);
					if (pc.getInventory().checkAddItem(item, 1) == 0) {
						pc.getInventory().storeItem(item);
						pc.sendPackets(new S_SystemMessage("获得在线奖励:"
								+ item.getItem().getName() + "("
								+ Config.AI_ONLIN_ITEMCOUNT + ")"));
					}
				}

				pc.setSkillEffect(AI_ONLIN_SKILLID,
						Config.AI_ONLIN_TIME * 60 * 1000);
			}
		}
			break;
		case GLOWING_AURA: { //  
			cha.addHitup(-5);
			cha.addMr(-10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_SkillIconAura(113, 0));
			}
		}
			break;
		case SHINING_AURA: { //  
			cha.addAc(8);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(114, 0));
			}
		}
			break;
		case BRAVE_AURA: { //  
			cha.addDmgup(-5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(116, 0));
			}
		}
			break;
		case SHIELD: { // 
			cha.addAc(2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(5, 0));
			}
		}
			break;
		case BLIND_HIDING: { // 
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.delBlindHiding();
			}
		}
			break;
		case SHADOW_ARMOR: { //  
			cha.addAc(3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(3, 0));
			}
		}
			break;
		case DRESS_DEXTERITY: { //  
			cha.addDex((byte) -2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Dexup(pc, 2, 0));
			}
		}
			break;
		case DRESS_MIGHTY: { //  
			cha.addStr((byte) -2);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Strup(pc, 2, 0));
			}
		}
			break;
		/*
		 * case SHADOW_FANG: { //   cha.addDmgup(-5); } break; case
		 * ENCHANT_WEAPON: { //   cha.addDmgup(-2); } break; case
		 * BLESSED_ARMOR: { //   cha.addAc(3); } break;
		 */
		case EARTH_BLESS: { //  
			cha.addAc(7);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(7, 0));
			}
		}
			break;
		case RESIST_MAGIC: { //  
			cha.addMr(-10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SPMR(pc));
			}
		}
			break;
		case CLEAR_MIND: { //  
			cha.addWis((byte) -3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.resetBaseMr();
			}
		}
			break;
		case RESIST_ELEMENTAL: { //  
			cha.addWind(-10);
			cha.addWater(-10);
			cha.addFire(-10);
			cha.addEarth(-10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		}
			break;
		case ELEMENTAL_PROTECTION: { // 
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				int attr = pc.getElfAttr();
				if (attr == 1) {
					cha.addEarth(-50);
				} else if (attr == 2) {
					cha.addFire(-50);
				} else if (attr == 4) {
					cha.addWater(-50);
				} else if (attr == 8) {
					cha.addWind(-50);
				}
				pc.sendPackets(new S_OwnCharAttrDef(pc));
			}
		}
			break;
		case IRON_SKIN: { //  
			cha.addAc(10);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(10, 0));
			}
		}
			break;
		case EARTH_SKIN: { //  
			cha.addAc(4);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconShield(6, 0));
			}
		}
			break;
		case PHYSICAL_ENCHANT_STR: { //  ：STR
			cha.addStr((byte) -5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Strup(pc, 5, 0));
			}
		}
			break;
		case PHYSICAL_ENCHANT_DEX: { //  ：DEX
			cha.addDex((byte) -5);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Dexup(pc, 5, 0));
			}
		}
			break;
		case FIRE_WEAPON: { //  
			cha.addDmgup(-4);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(147, 0));
			}
		}
			break;
		case FIRE_BLESS: { //  
			cha.addDmgup(-4);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(154, 0));
			}
		}
			break;
		case BURNING_WEAPON: { //  
			cha.addDmgup(-6);
			cha.addHitup(-3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(162, 0));
			}
		}
			break;
		/*
		 * 删除case 8: { // 神武 cha.addDmgup(-1); } break; case BLESS_WEAPON: { //
		 *   cha.addDmgup(-2); cha.addHitup(-2); cha.addBowHitup(-2); }
		 * break;删除
		 */
		case WIND_SHOT: { //  
			cha.addBowHitup(-6);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(148, 0));
			}
		}
			break;
		case STORM_EYE: { //  
			cha.addBowHitup(-2);
			cha.addBowDmgup(-3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(155, 0));
			}
		}
			break;
		case STORM_SHOT: { //  
			cha.addBowDmgup(-5);
			cha.addBowHitup(3);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconAura(165, 0));
			}
		}
			break;
		case BERSERKERS: { // 
			cha.addAc(-10);
			cha.addDmgup(-5);
			cha.addHitup(-2);
		}
			break;
		case SHAPE_CHANGE: { //  
			if (cha instanceof L1PcInstance) {
				L1PolyMorph.undoPoly((L1PcInstance) cha);
			}
		}
			break;
		case ADVANCE_SPIRIT: { //  
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-pc.getAdvenHp());
				pc.addMaxMp(-pc.getAdvenMp());
				pc.setAdvenHp(0);
				pc.setAdvenMp(0);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // 中
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
		}
			break;
		case HASTE:
		case GREATER_HASTE: { // 、 
			cha.setMoveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
		}
			break;
		case HOLY_WALK:
		case MOVING_ACCELERATION:
		case WIND_WALK: { // 
			// 、
			// 、
			// 
			cha.setBraveSpeed(0);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
			}
		}
			break;

		// ****** 状态变化解场合
		case CURSE_BLIND:
		case DARKNESS: {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_CurseBlind(0));
			}
		}
			break;
		/*
		 * 删除case CURSE_PARALYZE: { //   if (cha instanceof
		 * L1PcInstance) { L1PcInstance pc = (L1PcInstance) cha;
		 * pc.sendPackets(new S_Poison(pc.getId(), 0)); pc.broadcastPacket(new
		 * S_Poison(pc.getId(), 0)); pc.sendPackets(new
		 * S_Paralysis(S_Paralysis.TYPE_PARALYSIS, false)); } } break;删除
		 */
		case WEAKNESS: { // 
			/*
			 * 删除if (cha instanceof L1PcInstance) { L1PcInstance pc =
			 * (L1PcInstance) cha; pc.addDmgup(5); pc.addHitup(1); }删除
			 */
			cha.addDmgup(5);
			cha.addHitup(1);
		}
			break;
		case DISEASE: { // 
			/*
			 * 删除if (cha instanceof L1PcInstance) { L1PcInstance pc =
			 * (L1PcInstance) cha; pc.addDmgup(6); pc.addAc(-12); }删除
			 */
			cha.addHitup(6);
			cha.addAc(-12);
		}
			break;
		case ICE_LANCE: { // 
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, 0,
						false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.broadcastPacket(new S_Poison(npc.getId(), 0));
				npc.setParalyzed(false);
			}
		}
			break;
		case EARTH_BIND: { // 
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Poison(pc.getId(), 0));
				pc.broadcastPacket(new S_Poison(pc.getId(), 0));
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE, 0,
						false));
			} else if (cha instanceof L1MonsterInstance
					|| cha instanceof L1SummonInstance
					|| cha instanceof L1PetInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.broadcastPacket(new S_Poison(npc.getId(), 0));
				npc.setParalyzed(false);
			}
		}
			break;
		/*
		 * 删除case SHOCK_STUN: { //   if (cha instanceof L1PcInstance) {
		 * L1PcInstance pc = (L1PcInstance) cha; pc.sendPackets(new
		 * S_Paralysis(S_Paralysis.TYPE_STUN, false)); } else if (cha instanceof
		 * L1MonsterInstance || cha instanceof L1SummonInstance || cha
		 * instanceof L1PetInstance) { L1NpcInstance npc = (L1NpcInstance) cha;
		 * npc.setParalyzed(false); } } break;删除
		 */
		case FOG_OF_SLEEPING: { //   
			cha.setSleeped(false);
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP, 0, false));
				pc.sendPackets(new S_OwnCharStatus(pc));
			}
		}
			break;
		case ABSOLUTE_BARRIER: { //  
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.startHpRegeneration();
				pc.startMpRegeneration();
				// pc.startMpRegenerationByDoll();
			}
		}
			break;
		case WIND_SHACKLE: { //  
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBoxWindShackle(pc.getId(), 0));
			}
		}
			break;
		case RETURN_TO_NATURE: {
			if (cha instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) cha;
				summon.liberate();
			}
		}
			break;
		case ELEMENTAL_FALL_DOWN: {// 弱化属性实装
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				byte attr = (byte) (pc.get_PcAttr());
				byte i = (byte) (50);
				switch (attr) {
				case 1:
					pc.addEarth(i);
					break;
				case 2:
					pc.addFire(i);
					break;
				case 4:
					pc.addWater(i);
					break;
				case 8:
					pc.addWind(i);
					break;
				}
				pc.set_PcAttr(0);
			} else if (cha instanceof L1MonsterInstance) {
				L1MonsterInstance npc = (L1MonsterInstance) cha;
				byte attr = (byte) (npc.get_NpcAttr());
				byte i = (byte) (50);
				switch (attr) {
				case 1:
					npc.addEarth(i);
					break;
				case 2:
					npc.addFire(i);
					break;
				case 4:
					npc.addWater(i);
					break;
				case 8:
					npc.addWind(i);
					break;
				}
				npc.set_NpcAttr(0);
			}
		}
			break;
		case SLOW:
		case ENTANGLE:
		case MASS_SLOW: { // 、、
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
				pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
			}
			cha.setMoveSpeed(0);
		}
			break;
		case STATUS_FREEZE: {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_BIND, 0, false));
			}
		}
			break;

		// ****** 关系

		case STATUS_BRAVE:
		case STATUS_ELFBRAVE: { //  等
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
			}
			cha.setBraveSpeed(0);
		}
			break;
		case STATUS_HASTE: { //  
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
			}
			cha.setMoveSpeed(0);
		}
			break;
		case STATUS_BLUE_POTION: { //  
		}
			break;
		case STATUS_UNDERWATER_BREATH: { // 祝福＆鳞
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_SkillIconBlessOfEva(pc.getId(), 0));
			}
		}
			break;
		case STATUS_WISDOM_POTION: { //  
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				cha.addSp(-2);
				// SP画面更新
				pc.sendPackets(new S_SPMR(pc));
				// SP画面更新 end
				// pc.sendPackets(new S_SkillBrave(pc.getId(), 0, 0));
			}
		}
			break;
		case STATUS_CHAT_PROHIBITED: { // 禁止
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_ServerMessage(288)); // 。
			}
		}
			break;

		// ****** 料理系
		case COOKING_1_0_N:
		case COOKING_1_0_S: {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addWind(-10);
				pc.addWater(-10);
				pc.addFire(-10);
				pc.addEarth(-10);
				pc.sendPackets(new S_OwnCharAttrDef(pc));
				pc.sendPackets(new S_PacketBox(53, 0, 0));
				pc.setCookingId(0);
			}
		}
			break;
		case COOKING_1_1_N:
		case COOKING_1_1_S: {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxHp(-30);
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				if (pc.isInParty()) { // 中
					pc.getParty().updateMiniHP(pc);
				}
				pc.sendPackets(new S_PacketBox(53, 1, 0));
				pc.setCookingId(0);
			}
		}
			break;
		case COOKING_1_2_N:
		case COOKING_1_2_S: {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 2, 0));
				pc.setCookingId(0);
			}
		}
			break;
		case COOKING_1_3_N:
		case COOKING_1_3_S: {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addAc(1);
				pc.sendPackets(new S_PacketBox(53, 3, 0));
				pc.setCookingId(0);
			}
		}
			break;
		case COOKING_1_4_N:
		case COOKING_1_4_S: {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMaxMp(-20);
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				pc.sendPackets(new S_PacketBox(53, 4, 0));
				pc.setCookingId(0);
			}
		}
			break;
		case COOKING_1_5_N:
		case COOKING_1_5_S: {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 5, 0));
				pc.setCookingId(0);
			}
		}
			break;
		case COOKING_1_6_N:
		case COOKING_1_6_S: {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.addMr(-5);
				pc.sendPackets(new S_SPMR(pc));
				pc.sendPackets(new S_PacketBox(53, 6, 0));
				pc.setCookingId(0);
			}
		}
			break;
		case COOKING_1_7_N:
		case COOKING_1_7_S: {
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_PacketBox(53, 7, 0));
				pc.setDessertId(0);
			}
		}
			break;
		case STATUS_STR_POISON: {// 激励药水
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Strup(pc, 0, 0));
				pc.addStr((byte) -6);
			}
		}
			break;
		case STATUS_DEX_POISON: {// 能力药水
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_Dexup(pc, 0, 0));
				pc.addDex((byte) -6);
			}
		}
			break;
		case Skill_AJ_0_1: { // 获得道具
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				L1ItemInstance item = ItemTable.getInstance().createItem(41159);
				item.setCount(1);
				if (item != null) {
					if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
						pc.getInventory().storeItem(item);
					} else { // 持场合地面落 处理（不正防止）
						L1World.getInstance()
								.getInventory(pc.getX(), pc.getY(),
										pc.getMapId()).storeItem(item);
					}
					pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0手入
				}
			}
			cha.setSkillEffect(Skill_AJ_0_1, 180 * 1000);
		}
			break;
		case Skill_AJ_1_6: { // 宠物活力药水
			if (cha instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.stopHpRegeneration();
				if (npc.getMaxHp() > npc.getCurrentHp()) {
					npc.startHpRegeneration();
				}
			}
		}
			break;
		case Skill_AJ_1_7: { // 宠物魔力药水
			if (cha instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) cha;
				npc.stopMpRegeneration();
				if (npc.getMaxMp() > npc.getCurrentMp()) {
					npc.startMpRegeneration();
				}
			}
		}
			break;
		case Skill_AJ_1_9: { // 判断门一段时间后自行关闭
			if (cha instanceof L1DoorInstance) {
				L1DoorInstance door = (L1DoorInstance) cha;
				if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
					door.doorclose();
				}
			}
		}
			break;
		case Skill_AJ_1_10: { // 芮克妮的网 重生结束
			if (cha instanceof L1GuardianInstance) {
				L1GuardianInstance npc = (L1GuardianInstance) cha;
				npc.setCount1(0);
			}
		}
			break;
		case Skill_AJ_1_11: { // 安特之树皮 重生结束
			if (cha instanceof L1GuardianInstance) {
				L1GuardianInstance npc = (L1GuardianInstance) cha;
				npc.setCount3(0);
			}
		}
			break;
		case Skill_AJ_1_12: { // 安特的水果 重生结束
			if (cha instanceof L1GuardianInstance) {
				L1GuardianInstance npc = (L1GuardianInstance) cha;
				npc.setCount1(0);
			}
		}
			break;
		case Skill_AJ_1_13: { // 安特的树枝 重生结束
			if (cha instanceof L1GuardianInstance) {
				L1GuardianInstance npc = (L1GuardianInstance) cha;
				npc.setCount2(0);
			}
		}
			break;
		case Skill_AJ_1_14: { // 潘的鬃毛 重生结束
			if (cha instanceof L1GuardianInstance) {
				L1GuardianInstance npc = (L1GuardianInstance) cha;
				npc.setCount1(0);
			}
		}
			break;
		case Skill_AJ_0_3: { // 经验加倍
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.sendPackets(new S_ServerMessage(166, L1WilliamSystemMessage
						.ShowMessage(1087))); // 。
			}
		}
			break;
		case AITIME:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				pc.setSkillEffect(WAITTIME, Config.WAITTIME * 1000);
			}
			break;
		case WAITTIME:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				if (pc.getMapId() != 99) {
					if (pc.getMembera() != -1) {// 如果打怪结束产生数字
						L1Teleport.teleport(pc, 32735, 32796, (short) 99,
								pc.getHeading(), true);
						WriteLogTxt.Recording("答题系统", "玩家" + pc.getName()
								+ "回答时间超过限制被关监狱");
					} else {// 如果打怪没结束，数字尚未产生
						pc.setSkillEffect(WAITTIME, Config.WAITTIME * 1000);
					}
				} else {
					pc.setSkillEffect(WAITTIME, Config.WAITTIME * 1000);
				}
			}
			break;
		default:
			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
				final int[] etcItemSkillArray = EtcItemSkillTable.getInstance()
						.getEtcitemSkillArray(skillId);
				if (etcItemSkillArray == null) {
					return;
				}
				pc.addStr(-etcItemSkillArray[2]);
				pc.addDex(-etcItemSkillArray[3]);
				pc.addCon(-etcItemSkillArray[4]);
				pc.addInt(-etcItemSkillArray[5]);
				pc.addWis(-etcItemSkillArray[6]);
				pc.addCha(-etcItemSkillArray[7]);
				pc.addMaxHp(-etcItemSkillArray[8]);
				pc.addMaxMp(-etcItemSkillArray[9]);
				pc.addHpr(-etcItemSkillArray[10]);
				pc.addMpr(-etcItemSkillArray[11]);
				pc.addSp(-etcItemSkillArray[12]);
				pc.addMr(-etcItemSkillArray[13]);
				pc.addAc(-etcItemSkillArray[14]);
				pc.addHitup(-etcItemSkillArray[15]);
				pc.addBowHitup(-etcItemSkillArray[15]);
				pc.addDmgup(-etcItemSkillArray[16]);
				pc.addBowDmgup(-etcItemSkillArray[16]);
				pc.addEtcItemSkillExp(-etcItemSkillArray[17]);
				pc.addEtcItemSkillEr(-etcItemSkillArray[18]);
				pc.sendPackets(new S_OwnCharStatus(pc));
				pc.sendPackets(new S_SPMR(pc));
			}
			break;
		}
		// 变更成switch end

		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			sendStopMessage(pc, skillId);
			pc.sendPackets(new S_OwnCharStatus(pc));
		}
	}

	// 表示（终了）
	private static void sendStopMessage(L1PcInstance charaPc, int skillid) {
		L1Skills l1skills = SkillsTable.getInstance().getTemplate(skillid);
		if (l1skills == null || charaPc == null) {
			return;
		}

		int msgID = l1skills.getSysmsgIdStop();
		if (msgID > 0) {
			charaPc.sendPackets(new S_ServerMessage(msgID, ""));
		}
	}
}

class L1SkillTimerThreadImpl extends Thread implements L1SkillTimer {
	public L1SkillTimerThreadImpl(L1Character cha, int skillId, int timeMillis) {
		_cha = cha;
		_skillId = skillId;
		_timeMillis = timeMillis;
	}

	@Override
	public void run() {
		for (int timeCount = _timeMillis / 1000; timeCount > 0; timeCount--) {
			try {
				Thread.sleep(1000);
				_remainingTime = timeCount;
			} catch (InterruptedException e) {
				return;
			}
		}
		_cha.removeSkillEffect(_skillId);
		_cha = null;
	}

	@Override
	public int getRemainingTime() {
		return _remainingTime;
	}

	@Override
	public void begin() {
		GeneralThreadPool.getInstance().execute(this);
	}

	@Override
	public void end() {
		super.interrupt();
		L1SkillStop.stopSkill(_cha, _skillId);
		_cha = null;
	}

	@Override
	public void kill() {
		if (Thread.currentThread().getId() == super.getId()) {
			return; // 呼び出し元スレッドが自分であれば止めない
		}
		super.interrupt();
		_cha = null;
	}

	private L1Character _cha;

	private final int _timeMillis;

	private final int _skillId;

	private int _remainingTime;
}

class L1SkillTimerTimerImpl implements L1SkillTimer, Runnable {

	private ScheduledFuture<?> _future = null;

	public L1SkillTimerTimerImpl(L1Character cha, int skillId, int timeMillis) {
		_cha = cha;
		_skillId = skillId;
		_timeMillis = timeMillis;

		_remainingTime = _timeMillis / 1000;
	}

	@Override
	public void run() {
		_remainingTime--;
		if (_remainingTime <= 0) {
			_cha.removeSkillEffect(_skillId);
		}
	}

	@Override
	public void begin() {
		_future = GeneralThreadPool.getInstance().scheduleAtFixedRate(this,
				1000, 1000);
	}

	@Override
	public void end() {
		kill();
		try {
			L1SkillStop.stopSkill(_cha, _skillId);
		} catch (Throwable e) {
			// _log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	@Override
	public void kill() {
		if (_future != null) {
			_future.cancel(false);
		}
	}

	@Override
	public int getRemainingTime() {
		return _remainingTime;
	}

	private final L1Character _cha;

	private final int _timeMillis;

	private final int _skillId;

	private int _remainingTime;
}