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

package l1j.server.server.utils;

import java.util.ArrayList;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.MapExpTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_PetPack;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_TrueTarget;
import l1j.server.server.timecontroller.WorldCalcExp;
import l1j.server.server.world.L1World;


// Referenced classes of package l1j.server.server.utils:
// CalcStat

public class CalcExp {


	public static final int MAX_EXP = ExpTable.getExpByLevel(100) - 1;
	
	private static Random _random = new Random();

	private CalcExp() {
	}

	public static void calcExp(L1PcInstance l1pcinstance,/* int targetid,*/
			L1NpcInstance npc,
			ArrayList<L1Character> acquisitorList, ArrayList<Integer> hateList, long exp) {

		int i = 0;
		double party_level = 0;
		double dist = 0;
		int member_exp = 0;
		int member_lawful = 0;
/*		L1Object l1object = L1World.getInstance().findObject(targetid);
		L1NpcInstance npc = (L1NpcInstance) l1object;*/

		// 合计取得
		L1Character acquisitor;
		int hate = 0;
		long acquire_exp = 0;
		int acquire_lawful = 0;
		long party_exp = 0;
		int party_lawful = 0;
		int totalHateExp = 0;
		int totalHateLawful = 0;
		int partyHateExp = 0;
		int partyHateLawful = 0;
		int ownHateExp = 0;

		if (acquisitorList.size() != hateList.size()) {
			return;
		}
		for (i = hateList.size() - 1; i >= 0; i--) {
			acquisitor = (L1Character) acquisitorList.get(i);
			hate = (Integer) hateList.get(i);
			if (acquisitor != null && !acquisitor.isDead()) {
				totalHateExp += hate;
				if (acquisitor instanceof L1PcInstance) {
					totalHateLawful += hate;
				}
			} else { // null死排除
				acquisitorList.remove(i);
				hateList.remove(i);
			}
		}
		if (totalHateExp == 0) { // 取得者场合
			return;
		}

//		AIcheck(l1pcinstance);
		if (npc != null && !(npc instanceof L1PetInstance)
				&& !(npc instanceof L1SummonInstance)) {
			// int exp = npc.get_exp();
			if (!L1World.getInstance().isProcessingContributionTotal()
					&& l1pcinstance.getHomeTownId() > 0) {
				int contribution = npc.getLevel() / 10;
				l1pcinstance.addContribution(contribution);
			}
			int lawful = npc.getLawful();

			if (l1pcinstance.isInParty()) { // 中
				// 合计算出
				// 以外配分
				partyHateExp = 0;
				partyHateLawful = 0;
				for (i = hateList.size() - 1; i >= 0; i--) {
					acquisitor = (L1Character) acquisitorList.get(i);
					hate = (Integer) hateList.get(i);
					if (acquisitor instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) acquisitor;
						if (pc == l1pcinstance) {
							partyHateExp += hate;
							partyHateLawful += hate;
						} else if (l1pcinstance.getParty().isMember(pc)) {
							partyHateExp += hate;
							partyHateLawful += hate;
						} else {
							if (totalHateExp > 0) {
								acquire_exp = (exp * hate / totalHateExp);
							}
							if (totalHateLawful > 0) {
								acquire_lawful = (lawful * hate / totalHateLawful);
							}
							AddExp(pc, acquire_exp, acquire_lawful,npc);
						}
					} else if (acquisitor instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) pet.getMaster();
						if (master == l1pcinstance) {
							partyHateExp += hate;
						} else if (l1pcinstance.getParty().isMember(master)) {
							partyHateExp += hate;
						} else {
							if (totalHateExp > 0) {
								acquire_exp = (exp * hate / totalHateExp);
							}
							AddExpPet(pet, acquire_exp);
						}
					} else if (acquisitor instanceof L1SummonInstance) {
						L1SummonInstance summon = (L1SummonInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) summon.getMaster();
						if (master == l1pcinstance) {
							partyHateExp += hate;
						} else if (l1pcinstance.getParty().isMember(master)) {
							partyHateExp += hate;
						} else {
						}
					}
				}
				if (totalHateExp > 0) {
					party_exp = (exp * partyHateExp / totalHateExp);
				}
				if (totalHateLawful > 0) {
					party_lawful = (lawful * partyHateLawful / totalHateLawful);
				}

				// EXP、配分

				// 
				double pri_bonus = 0;
				L1PcInstance leader = l1pcinstance.getParty().getLeader();
				if (leader.isCrown()
						&& (l1pcinstance.knownsObject(leader)
								|| l1pcinstance.equals(leader))) {
					pri_bonus = 0.059;
				}

				// PT计算
				L1PcInstance[] ptMembers = l1pcinstance.getParty().getMembers();
				double pt_bonus = 0;
				for (L1PcInstance each : ptMembers) {
					if (l1pcinstance.knownsObject(each)
							|| l1pcinstance.equals(each)) {
						party_level += each.getLevel() * each.getLevel();
					}
					if (l1pcinstance.knownsObject(each)) {
						pt_bonus += 0.04;
					}
				}

				party_exp = (int) (party_exp * (1 + pt_bonus + pri_bonus));

				// 自合计算出
				if (party_level > 0) {
					dist = ((l1pcinstance.getLevel() * l1pcinstance.getLevel()) / party_level);
				}
				member_exp = (int) (party_exp * dist);
				member_lawful = (int) (party_lawful * dist);

				ownHateExp = 0;
				for (i = hateList.size() - 1; i >= 0; i--) {
					acquisitor = (L1Character) acquisitorList.get(i);
					hate = (Integer) hateList.get(i);
					if (acquisitor instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) acquisitor;
						if (pc == l1pcinstance) {
							ownHateExp += hate;
						}
					} else if (acquisitor instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) pet.getMaster();
						if (master == l1pcinstance) {
							ownHateExp += hate;
						}
					} else if (acquisitor instanceof L1SummonInstance) {
						L1SummonInstance summon = (L1SummonInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) summon.getMaster();
						if (master == l1pcinstance) {
							ownHateExp += hate;
						}
					}
				}
				// 自分配
				if (ownHateExp != 0) { // 攻加
					for (i = hateList.size() - 1; i >= 0; i--) {
						acquisitor = (L1Character) acquisitorList.get(i);
						hate = (Integer) hateList.get(i);
						if (acquisitor instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) acquisitor;
							if (pc == l1pcinstance) {
								if (ownHateExp > 0) {
									acquire_exp = (member_exp * hate / ownHateExp);
								}
								AddExp(pc, acquire_exp, member_lawful,npc);
							}
						} else if (acquisitor instanceof L1PetInstance) {
							L1PetInstance pet = (L1PetInstance) acquisitor;
							L1PcInstance master = (L1PcInstance) pet
									.getMaster();
							if (master == l1pcinstance) {
								if (ownHateExp > 0) {
									acquire_exp = (member_exp * hate / ownHateExp);
								}
								AddExpPet(pet, acquire_exp);
							}
						} else if (acquisitor instanceof L1SummonInstance) {
						}
					}
				} else { // 攻加
					// 自分配
					AddExp(l1pcinstance, member_exp, member_lawful,npc);
				}

				// 合计算出
				for (int cnt = 0; cnt < ptMembers.length; cnt++) {
					if (l1pcinstance.knownsObject(ptMembers[cnt])) {
						if (party_level > 0) {
							dist = ((ptMembers[cnt].getLevel() * ptMembers[cnt]
									.getLevel()) / party_level);
						}
						member_exp = (int) (party_exp * dist);
						member_lawful = (int) (party_lawful * dist);

						ownHateExp = 0;
						for (i = hateList.size() - 1; i >= 0; i--) {
							acquisitor = (L1Character) acquisitorList.get(i);
							hate = (Integer) hateList.get(i);
							if (acquisitor instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) acquisitor;
								if (pc == ptMembers[cnt]) {
									ownHateExp += hate;
								}
							} else if (acquisitor instanceof L1PetInstance) {
								L1PetInstance pet = (L1PetInstance) acquisitor;
								L1PcInstance master = (L1PcInstance) pet
										.getMaster();
								if (master == ptMembers[cnt]) {
									ownHateExp += hate;
								}
							} else if (acquisitor instanceof L1SummonInstance) {
								L1SummonInstance summon = (L1SummonInstance) acquisitor;
								L1PcInstance master = (L1PcInstance) summon
										.getMaster();
								if (master == ptMembers[cnt]) {
									ownHateExp += hate;
								}
							}
						}
						// 分配
						if (ownHateExp != 0) { // 攻加
							for (i = hateList.size() - 1; i >= 0; i--) {
								acquisitor = (L1Character) acquisitorList
										.get(i);
								hate = (Integer) hateList.get(i);
								if (acquisitor instanceof L1PcInstance) {
									L1PcInstance pc = (L1PcInstance) acquisitor;
									if (pc == ptMembers[cnt]) {
										if (ownHateExp > 0) {
											acquire_exp = (member_exp * hate / ownHateExp);
										}
										AddExp(pc, acquire_exp, member_lawful,npc);
									}
								} else if (acquisitor instanceof L1PetInstance) {
									L1PetInstance pet = (L1PetInstance) acquisitor;
									L1PcInstance master = (L1PcInstance) pet
											.getMaster();
									if (master == ptMembers[cnt]) {
										if (ownHateExp > 0) {
											acquire_exp = (member_exp * hate / ownHateExp);
										}
										AddExpPet(pet, acquire_exp);
									}
								} else if (acquisitor instanceof L1SummonInstance) {
								}
							}
						} else { // 攻加
							// 分配
							AddExp(ptMembers[cnt], member_exp, member_lawful,npc);
						}
					}
				}
			} else { // 组
				// EXP、分配
				for (i = hateList.size() - 1; i >= 0; i--) {
					acquisitor = (L1Character) acquisitorList.get(i);
					hate = (Integer) hateList.get(i);
					acquire_exp = (exp * hate / totalHateExp);
					if (acquisitor instanceof L1PcInstance) {
						if (totalHateLawful > 0) {
							acquire_lawful = (lawful * hate / totalHateLawful);
						}
					}

					if (acquisitor instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) acquisitor;
						AddExp(pc, acquire_exp, acquire_lawful,npc);
					} else if (acquisitor instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) acquisitor;
						AddExpPet(pet, acquire_exp);
					} else if (acquisitor instanceof L1SummonInstance) {
					}
				}
			}
		}
	}

	private static void AddExp(L1PcInstance pc, long exp, int lawful , L1NpcInstance npc) {
		if (pc.isGhost()) {
			return;
		}

		int add_lawful = (int) (lawful * Config.RATE_LA) * -1;
		pc.addLawful(add_lawful);

		double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
		double foodBonus = 1.0;
		if (pc.hasSkillEffect(L1SkillId.COOKING_1_7_N)
				|| pc.hasSkillEffect(L1SkillId.COOKING_1_7_S)) { // 新增魔法公主效果 
			foodBonus = 1.01;
		}
		// 经验加倍 
		if (pc.hasSkillEffect(l1j.william.New_Id.Skill_AJ_0_3)) {
			//pc.sendPackets(new S_ServerMessage(166, L1WilliamSystemMessage.ShowMessage(1112)));
			foodBonus += 1;
		}
		
		if (MapExpTable.get().get_level(pc.getMapId(), pc.getLevel())){
			foodBonus += MapExpTable.get().get_exp(pc.getMapId()) - 1.0;
		}
		
		if (WorldCalcExp.get().isRuning()){
			foodBonus += 1;
		}
		
		// 经验加倍  end
		int add_exp = (int) (exp * exppenalty * Config.RATE_XP * foodBonus);
		
/*		if (pc.getLevel() < 30) {//30级以下双倍经验
			add_exp *= 2;
		}
		if (pc.getLevel() < 70) {
			add_exp *= 10;
		}else if (pc.getLevel() >= 70) {
			add_exp *= 5;
		}*/
		
		if (pc.hasSkillEffect(L1SkillId.EXPITEM)) {
			add_exp *= 1.2;
		}
		
		if (pc.getEtcItemSkillExp() > 0){
			final double bs = (double)pc.getEtcItemSkillExp() / 100.0 + 1.0;
			add_exp *= bs;
		}
		
		if (pc.getClanid() > 0){
			add_exp *= getExpReductionByClan(pc);
		}
		
		if (pc.getExpByDoll() > 1.0){
			add_exp *= pc.getExpByDoll();
		}
		pc.addExp(add_exp);
		
		if (Config.AICHECK) {
			AIcheck(pc,npc);
		}
		
	}
	/**
     * 血盟技能经验值增加
     * 
     * @return
     */
    private static double getExpReductionByClan(final L1PcInstance pc) {
        double dmg = 1.0;
        try {
            if (pc == null) {
                return 1.0;
            }
            final L1Clan clan = pc.getClan();
            if (pc.getClanid() == 0 || clan == null) {
                return 1.0;
            }
            // 具有血盟技能
            if (clan.isClanskill()) {
                if (clan.getSkillLevel() == 1) {
                	dmg += 0.1;
                }else if (clan.getSkillLevel() == 2) {
                	dmg += 0.2;
                }else if (clan.getSkillLevel() == 3) {
                	dmg += 0.3;
                }else if (clan.getSkillLevel() == 4) {
                	dmg += 0.4;
                }else if (clan.getSkillLevel() == 5) {
                	dmg += 0.5;
                }
            }

        } catch (final Exception e) {
            return 1.0;
        }
        return dmg;
    }
	private static void AIcheck(L1PcInstance pc,L1NpcInstance npc){
		if (pc.getMembera() == -1) {
			if (pc.hasSkillEffect(L1SkillId.WAITTIME)) {
				int a = _random.nextInt(100);
				int b = _random.nextInt(100);
				int sum = a + b;
				pc.setMembera(a);
				pc.setMemberb(b);
				pc.setSum(sum);
//				pc.sendPackets(new S_PacketBoxGree(1));
				pc.setSkillEffect(L1SkillId.WAITTIME, Config.WAITTIME*1000);
				//String msg = a+"加上"+b+"等于多少？";
				String msg ="请输入验证码"+sum;
				pc.sendPackets(new S_TrueTarget(pc.getId(), pc
						.getId(), msg));
				WriteLogTxt.Recording("答题系统", "玩家打死 "+npc.getName()+" 开始提问"
						+"在地图ID"+pc.getMapId()+"X:"+pc.getX()+"Y:"+pc.getY()+"#玩家objid：<"+pc.getId()+">"
						+ "玩家"+pc.getName()+msg);
			}
		}else {
			//String msg = pc.getMembera()+"加上"+pc.getMemberb()+"等于多少？";
			String msg ="请输入验证码"+pc.getSum();
			pc.sendPackets(new S_TrueTarget(pc.getId(), pc
					.getId(), msg));
			WriteLogTxt.Recording("答题系统", "由于玩家继续打 "+npc.getName()+" 再次提问"
					+"在地图ID"+pc.getMapId()+"X:"+pc.getX()+"Y:"+pc.getY()+"#玩家objid：<"+pc.getId()+">"
					+ "玩家"+pc.getName()+msg);
		}
		
	}

	public static void AddExpPet(L1PetInstance pet, long exp) {
		L1PcInstance pc = (L1PcInstance) pet.getMaster();

		//int petNpcId = pet.getNpcTemplate().get_npcId();
		//int petItemObjId = pet.getItemObjId();

		int levelBefore = pet.getLevel();
		// 删除int totalExp = (int) (exp * Config.RATE_XP + pet.getExp());
		// 宠物经验倍率变更 
		long totalExp = (long) (exp * Config.PET_RATE_XP + pet.getExp());
		// 宠物经验倍率变更  end
		if (totalExp >= ExpTable.getExpByLevel(Config.PET_LEVEL + 1)) { // 宠物等级上限调整(Config.PET_LEVEL + 1) 
			totalExp = ExpTable.getExpByLevel(Config.PET_LEVEL + 1) - 1; // 宠物等级上限调整(Config.PET_LEVEL + 1) 
		}
		pet.setExp(totalExp);

		pet.setLevel(ExpTable.getLevelByExp(totalExp));

		int expPercentage = ExpTable.getExpPercentage(pet.getLevel(), totalExp);

		int gap = pet.getLevel() - levelBefore;
		onChance(gap,pet);
		pet.setExpPercent(expPercentage);
		pc.sendPackets(new S_PetPack(pet, pc));

		if (gap != 0) { // DB书迂
/*			L1Pet petTemplate = PetTable.getInstance()
					.getTemplate(petItemObjId);
			if (petTemplate == null) { // PetTable
				_log.warning("L1Pet == null");
				return;
			}*/
			
			pet.getL1Pet().set_exp(pet.getExp());
			pet.getL1Pet().set_level(pet.getLevel());
			pet.updatePet(); // DB书
			pc.sendPackets(new S_ServerMessage(320, pet.getName())); // \f1%0上。
		}
	}
	
	private static void onChance(int gap,L1PetInstance pet){
		if (gap > 0) {
			for (int i = 1; i <= gap; i++) {
				IntRange hpUpRange = pet.getPetType().getHpUpRange();
				IntRange mpUpRange = pet.getPetType().getMpUpRange();
				int randomhp = hpUpRange.randomValue();
				int randommp = mpUpRange.randomValue();
				pet.getL1Pet().set_hp(pet.getL1Pet().get_hp() + randomhp);
				pet.getL1Pet().set_mp(pet.getL1Pet().get_mp() + randommp);
				pet.addMaxHp(randomhp);
				pet.addMaxMp(randommp);
			}
		}else {
			for (int i = 0; i > gap; i--) {
				IntRange hpUpRange = pet.getPetType().getHpUpRange();
				IntRange mpUpRange = pet.getPetType().getMpUpRange();
				int randomhp = hpUpRange.randomValue();
				int randommp = mpUpRange.randomValue();
				pet.getL1Pet().set_hp(pet.getL1Pet().get_hp() - randomhp);
				pet.getL1Pet().set_mp(pet.getL1Pet().get_mp() - randommp);
				pet.addMaxHp(-randomhp);
				pet.addMaxMp(-randommp);
			}
		}
	}
}