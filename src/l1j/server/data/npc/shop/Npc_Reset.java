package l1j.server.data.npc.shop;

import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_DEX;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_STR;
import static l1j.server.server.model.skill.L1SkillId.STATUS_DEX_POISON;
import static l1j.server.server.model.skill.L1SkillId.STATUS_STR_POISON;

import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.CalcStat;
import l1j.william.L1WilliamSystemMessage;

public class Npc_Reset extends NpcExecutor {

	private Npc_Reset() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Reset();
	}

	@Override
	public int type() {
		return 3;
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ajreset"));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc,
			final String cmd, final long amount) {
		if (pc.isGhost()) {
			return;
		}
		String[] htmldata = null;
		String htmlid = "";
		// 能力值重置师
		try {
			if (cmd.equalsIgnoreCase("yes")) { // 切换到重置页面
				if (pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_27)) { // 有回忆蜡烛
					getOriginal(pc); // 设定出始能力值

					// 重置清单归零
					pc.setResetStr(0);
					pc.setResetDex(0);
					pc.setResetCon(0);
					pc.setResetInt(0);
					pc.setResetWis(0);
					pc.setResetCha(0);
					pc.setOthetReset((pc.getBonusStats() + pc.getElixirStats() + pc
							.getAllPoint())); // 重置点数 = 升级点数 + 万能药 + 初始点数
					// 重置清单归零

					htmldata = new String[] { String.valueOf(pc.getOriginalStr()),
							String.valueOf(pc.getOriginalDex()),
							String.valueOf(pc.getOriginalCon()),
							String.valueOf(pc.getOriginalInt()),
							String.valueOf(pc.getOriginalWis()),
							String.valueOf(pc.getOriginalCha()),
							String.valueOf(pc.getAllPoint())};
					htmlid = "ajreset1";
				} else {
					htmlid = "ajreset2";
				}
			} else if (cmd.equalsIgnoreCase("ostr_d")) { // 力量 - 1
				if (pc.getResetStr() > 0) {
					pc.setResetStr(pc.getResetStr() - 1);
					pc.setOthetReset(pc.getOthetReset() + 1);
				}

				htmldata = new String[] { String.valueOf(pc.getResetStr()),
						String.valueOf(pc.getResetDex()),
						String.valueOf(pc.getResetCon()),
						String.valueOf(pc.getResetInt()),
						String.valueOf(pc.getResetWis()),
						String.valueOf(pc.getResetCha()),
						String.valueOf(pc.getOthetReset()) };
				htmlid = "ajreset1";
			} else if (cmd.equalsIgnoreCase("ostr_u")) { // 力量 + 1
				if (pc.getOriginalStr()>19) {
					return;
				}
				if (pc.getAllPoint()<1) {
					return;
				}
				pc.setOriginalStr(pc.getOriginalStr()+1);
				pc.setAllPoint(pc.getAllPoint()-1);
/*
				if (((pc.getOriginalStr() + pc.getResetStr()) < Config.BONUS_STATS1)
						&& pc.getOthetReset() > 0) {
					pc.setResetStr(pc.getResetStr() + 1);
					pc.setOthetReset(pc.getOthetReset() - 1);
				}*/

				htmldata = new String[] { String.valueOf(pc.getOriginalStr()),
						String.valueOf(pc.getOriginalDex()),
						String.valueOf(pc.getOriginalCon()),
						String.valueOf(pc.getOriginalInt()),
						String.valueOf(pc.getOriginalWis()),
						String.valueOf(pc.getOriginalCha()),
						String.valueOf(pc.getAllPoint())};
				htmlid = "ajreset1";
			} else if (cmd.equalsIgnoreCase("odex_d")) { // 敏捷 - 1
				if (pc.getResetDex() > 0) {
					pc.setResetDex(pc.getResetDex() - 1);
					pc.setOthetReset(pc.getOthetReset() + 1);
				}

				htmldata = new String[] { String.valueOf(pc.getResetStr()),
						String.valueOf(pc.getResetDex()),
						String.valueOf(pc.getResetCon()),
						String.valueOf(pc.getResetInt()),
						String.valueOf(pc.getResetWis()),
						String.valueOf(pc.getResetCha()),
						String.valueOf(pc.getOthetReset()) };
				htmlid = "ajreset1";
			} else if (cmd.equalsIgnoreCase("odex_u")) { // 敏捷 + 1
				if (pc.getOriginalDex()>17) {
					return;
				}
				if (pc.getAllPoint()<1) {
					return;
				}
				pc.setOriginalDex(pc.getOriginalDex()+1);
				pc.setAllPoint(pc.getAllPoint()-1);
				/*if (((pc.getOriginalDex() + pc.getResetDex()) < Config.BONUS_STATS1)
						&& pc.getOthetReset() > 0) {
					pc.setResetDex(pc.getResetDex() + 1);
					pc.setOthetReset(pc.getOthetReset() - 1);
				}*/

				htmldata = new String[] { String.valueOf(pc.getOriginalStr()),
						String.valueOf(pc.getOriginalDex()),
						String.valueOf(pc.getOriginalCon()),
						String.valueOf(pc.getOriginalInt()),
						String.valueOf(pc.getOriginalWis()),
						String.valueOf(pc.getOriginalCha()),
						String.valueOf(pc.getAllPoint())};
				htmlid = "ajreset1";
			} else if (cmd.equalsIgnoreCase("ocon_d")) { // 体质 - 1
				if (pc.getResetCon() > 0) {
					pc.setResetCon(pc.getResetCon() - 1);
					pc.setOthetReset(pc.getOthetReset() + 1);
				}

				htmldata = new String[] { String.valueOf(pc.getResetStr()),
						String.valueOf(pc.getResetDex()),
						String.valueOf(pc.getResetCon()),
						String.valueOf(pc.getResetInt()),
						String.valueOf(pc.getResetWis()),
						String.valueOf(pc.getResetCha()),
						String.valueOf(pc.getOthetReset()) };
				htmlid = "ajreset1";
			} else if (cmd.equalsIgnoreCase("ocon_u")) { // 体质 + 1

				if (pc.getOriginalCon()>17) {
					return;
				}
				if (pc.getAllPoint()<1) {
					return;
				}
				pc.setOriginalCon(pc.getOriginalCon()+1);
				pc.setAllPoint(pc.getAllPoint()-1);
/*				if (((pc.getOriginalCon() + pc.getResetCon()) < Config.BONUS_STATS1)
						&& pc.getOthetReset() > 0) {
					pc.setResetCon(pc.getResetCon() + 1);
					pc.setOthetReset(pc.getOthetReset() - 1);
				}*/

				htmldata = new String[] { String.valueOf(pc.getOriginalStr()),
						String.valueOf(pc.getOriginalDex()),
						String.valueOf(pc.getOriginalCon()),
						String.valueOf(pc.getOriginalInt()),
						String.valueOf(pc.getOriginalWis()),
						String.valueOf(pc.getOriginalCha()),
						String.valueOf(pc.getAllPoint())};
				htmlid = "ajreset1";
			} else if (cmd.equalsIgnoreCase("oint_d")) { // 智力 - 1
				if (pc.getResetInt() > 0) {
					pc.setResetInt(pc.getResetInt() - 1);
					pc.setOthetReset(pc.getOthetReset() + 1);
				}

				htmldata = new String[] { String.valueOf(pc.getResetStr()),
						String.valueOf(pc.getResetDex()),
						String.valueOf(pc.getResetCon()),
						String.valueOf(pc.getResetInt()),
						String.valueOf(pc.getResetWis()),
						String.valueOf(pc.getResetCha()),
						String.valueOf(pc.getOthetReset()) };
				htmlid = "ajreset1";
			} else if (cmd.equalsIgnoreCase("oint_u")) { // 智力 + 1
				
				if (pc.getOriginalInt()>17) {
					return;
				}
				if (pc.getAllPoint()<1) {
					return;
				}
				pc.setOriginalInt(pc.getOriginalInt()+1);
				pc.setAllPoint(pc.getAllPoint()-1);

/*				if (((pc.getOriginalInt() + pc.getResetInt()) < Config.BONUS_STATS1)
						&& pc.getOthetReset() > 0) {
					pc.setResetInt(pc.getResetInt() + 1);
					pc.setOthetReset(pc.getOthetReset() - 1);
				}*/

				htmldata = new String[] { String.valueOf(pc.getOriginalStr()),
						String.valueOf(pc.getOriginalDex()),
						String.valueOf(pc.getOriginalCon()),
						String.valueOf(pc.getOriginalInt()),
						String.valueOf(pc.getOriginalWis()),
						String.valueOf(pc.getOriginalCha()),
						String.valueOf(pc.getAllPoint())};
				htmlid = "ajreset1";
			} else if (cmd.equalsIgnoreCase("owis_d")) { // 精神 - 1
				if (pc.getResetWis() > 0) {
					pc.setResetWis(pc.getResetWis() - 1);
					pc.setOthetReset(pc.getOthetReset() + 1);
				}

				htmldata = new String[] { String.valueOf(pc.getResetStr()),
						String.valueOf(pc.getResetDex()),
						String.valueOf(pc.getResetCon()),
						String.valueOf(pc.getResetInt()),
						String.valueOf(pc.getResetWis()),
						String.valueOf(pc.getResetCha()),
						String.valueOf(pc.getOthetReset()) };
				htmlid = "ajreset1";
			} else if (cmd.equalsIgnoreCase("owis_u")) { // 精神 + 1

				if (pc.getOriginalWis()>17) {
					return;
				}
				if (pc.getAllPoint()<1) {
					return;
				}
				pc.setOriginalWis(pc.getOriginalWis()+1);
				pc.setAllPoint(pc.getAllPoint()-1);
/*				if (((pc.getOriginalWis() + pc.getResetWis()) < Config.BONUS_STATS1)
						&& pc.getOthetReset() > 0) {
					pc.setResetWis(pc.getResetWis() + 1);
					pc.setOthetReset(pc.getOthetReset() - 1);
				}*/

				htmldata = new String[] { String.valueOf(pc.getOriginalStr()),
						String.valueOf(pc.getOriginalDex()),
						String.valueOf(pc.getOriginalCon()),
						String.valueOf(pc.getOriginalInt()),
						String.valueOf(pc.getOriginalWis()),
						String.valueOf(pc.getOriginalCha()),
						String.valueOf(pc.getAllPoint())};
				htmlid = "ajreset1";
			} else if (cmd.equalsIgnoreCase("ocha_d")) { // 魅力 - 1
				if (pc.getResetCha() > 0) {
					pc.setResetCha(pc.getResetCha() - 1);
					pc.setOthetReset(pc.getOthetReset() + 1);
				}

				htmldata = new String[] { String.valueOf(pc.getResetStr()),
						String.valueOf(pc.getResetDex()),
						String.valueOf(pc.getResetCon()),
						String.valueOf(pc.getResetInt()),
						String.valueOf(pc.getResetWis()),
						String.valueOf(pc.getResetCha()),
						String.valueOf(pc.getOthetReset()) };
				htmlid = "ajreset1";
			} else if (cmd.equalsIgnoreCase("ocha_u")) { // 魅力 + 1

				if (pc.getOriginalCha()>17) {
					return;
				}
				if (pc.getAllPoint()<1) {
					return;
				}
				pc.setOriginalCha(pc.getOriginalCha()+1);
				pc.setAllPoint(pc.getAllPoint()-1);
/*				if (((pc.getOriginalCha() + pc.getResetCha()) < Config.BONUS_STATS1)
						&& pc.getOthetReset() > 0) {
					pc.setResetCha(pc.getResetCha() + 1);
					pc.setOthetReset(pc.getOthetReset() - 1);
				}*/

				htmldata = new String[] { String.valueOf(pc.getOriginalStr()),
						String.valueOf(pc.getOriginalDex()),
						String.valueOf(pc.getOriginalCon()),
						String.valueOf(pc.getOriginalInt()),
						String.valueOf(pc.getOriginalWis()),
						String.valueOf(pc.getOriginalCha()),
						String.valueOf(pc.getAllPoint())};
				htmlid = "ajreset1";
			} else if (cmd.equalsIgnoreCase("yes to reset")) { // 重置
				if (pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_27)) { // 有回忆蜡烛
					if (pc.getOthetReset() == 0) {
						// 删除魔法状态
						if (pc.hasSkillEffect(PHYSICAL_ENCHANT_STR)) { // 体魄
							pc.removeSkillEffect(PHYSICAL_ENCHANT_STR);
						}
						if (pc.hasSkillEffect(PHYSICAL_ENCHANT_DEX)) { // 通畅
							pc.removeSkillEffect(PHYSICAL_ENCHANT_DEX);
						}
						if (pc.hasSkillEffect(STATUS_STR_POISON)) { // 力量提升药水
							pc.removeSkillEffect(STATUS_STR_POISON);
						}
						if (pc.hasSkillEffect(STATUS_DEX_POISON)) { // 敏捷提升药水
							pc.removeSkillEffect(STATUS_DEX_POISON);
						}
						// 删除魔法状态
						pc.getInventory().takeoffEquip(945); // 脱全身装备
						pc.getInventory().consumeItem(
								l1j.william.New_Id.Item_AJ_27, 1); // 删除回忆蜡烛
						pc.addBaseStr((byte) ((pc.getOriginalStr() + pc
								.getResetStr()) - pc.getStr()));
						pc.addBaseCon((byte) ((pc.getOriginalCon() + pc
								.getResetCon()) - pc.getCon()));
						pc.addBaseDex((byte) ((pc.getOriginalDex() + pc
								.getResetDex()) - pc.getDex()));
						pc.resetBaseAc();
						pc.addBaseInt((byte) ((pc.getOriginalInt() + pc
								.getResetInt()) - pc.getInt()));
						pc.addBaseWis((byte) ((pc.getOriginalWis() + pc
								.getResetWis()) - pc.getWis()));
						pc.resetBaseMr();
						pc.addBaseCha((byte) ((pc.getOriginalCha() + pc
								.getResetCha()) - pc.getCha()));
						short init_hp = 0;
						short init_mp = 0;
						pc.addBaseMaxHp((short) (-1 * (int) ((double) pc
								.getBaseMaxHp())));
						pc.addBaseMaxMp((short) (-1 * (int) ((double) pc
								.getBaseMaxMp())));
						if (pc.isCrown()) { // 君主
							init_hp = 14;
							switch (pc.getWis()) {
							case 11:
								init_mp = 2;
								break;
							case 12:
							case 13:
							case 14:
							case 15:
								init_mp = 3;
								break;
							case 16:
							case 17:
							case 18:
								init_mp = 4;
								break;
							default:
								init_mp = 2;
								break;
							}
						} else if (pc.isKnight()) { // 
							init_hp = 16;
							switch (pc.getWis()) {
							case 9:
							case 10:
							case 11:
								init_mp = 1;
								break;
							case 12:
							case 13:
								init_mp = 2;
								break;
							default:
								init_mp = 1;
								break;
							}
						} else if (pc.isElf()) { // 
							init_hp = 15;
							switch (pc.getWis()) {
							case 12:
							case 13:
							case 14:
							case 15:
								init_mp = 4;
								break;
							case 16:
							case 17:
							case 18:
								init_mp = 6;
								break;
							default:
								init_mp = 4;
								break;
							}
						} else if (pc.isWizard()) { // WIZ
							init_hp = 12;
							switch (pc.getWis()) {
							case 12:
							case 13:
							case 14:
							case 15:
								init_mp = 6;
								break;
							case 16:
							case 17:
							case 18:
								init_mp = 8;
								break;
							default:
								init_mp = 6;
								break;
							}
						} else if (pc.isDarkelf()) { // DE
							init_hp = 12;
							switch (pc.getWis()) {
							case 10:
							case 11:
								init_mp = 3;
								break;
							case 12:
							case 13:
							case 14:
							case 15:
								init_mp = 4;
								break;
							case 16:
							case 17:
							case 18:
								init_mp = 6;
								break;
							default:
								init_mp = 3;
								break;
							}
						}
						pc.addBaseMaxHp(init_hp);
						pc.setCurrentHp(init_hp);
						pc.addBaseMaxMp(init_mp);
						pc.setCurrentMp(init_mp);
						for (int i = 0; i < pc.getLevel(); i++) {
							final short randomHp = CalcStat.calcStatHp(
									pc.getType(), pc.getBaseMaxHp(),
									pc.getBaseCon());
							final short randomMp = CalcStat.calcStatMp(
									pc.getType(), pc.getBaseMaxMp(),
									pc.getBaseWis());
							pc.addBaseMaxHp(randomHp);
							pc.addBaseMaxMp(randomMp);
						}
						pc.sendPackets(new S_OwnCharStatus(pc)); // 更新画面
						pc.save(); // 储存在DB
						pc.sendPackets(new S_SkillSound(pc.getId(), 6505));
						pc.broadcastPacket(new S_SkillSound(pc.getId(), 6505));
						pc.sendPackets(new S_SystemMessage(L1WilliamSystemMessage
								.ShowMessage(1014)));
						htmlid = "";
					} else {
						pc.sendPackets(new S_SystemMessage(L1WilliamSystemMessage
								.ShowMessage(1015)));
					}
				} else {
					htmlid = "ajreset2";
				}
			} else if (cmd.equalsIgnoreCase("no")) {
				htmlid = "";
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (htmlid != null) { // html指定场合表示
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), htmlid, htmldata));
		}

	}

	// 设定初始能力值
	private void getOriginal(L1PcInstance pc) {
		switch (pc.getClassId()) {
		case 0:
		case 1: {
			pc.setOriginalStr(13);
			pc.setOriginalDex(10);
			pc.setOriginalCon(10);
			pc.setOriginalInt(10);
			pc.setOriginalWis(11);
			pc.setOriginalCha(13);
			pc.setAllPoint(8);
		}
			break;

		case 61:
		case 48: {
			pc.setOriginalStr(16);
			pc.setOriginalDex(12);
			pc.setOriginalCon(14);
			pc.setOriginalInt(8);
			pc.setOriginalWis(9);
			pc.setOriginalCha(12);
			pc.setAllPoint(4);
		}
			break;

		case 138:
		case 37: {
			pc.setOriginalStr(11);
			pc.setOriginalDex(12);
			pc.setOriginalCon(12);
			pc.setOriginalInt(12);
			pc.setOriginalWis(12);
			pc.setOriginalCha(9);
			pc.setAllPoint(7);
		}
			break;

		case 734:
		case 1186: {
			pc.setOriginalStr(8);
			pc.setOriginalDex(7);
			pc.setOriginalCon(12);
			pc.setOriginalInt(12);
			pc.setOriginalWis(12);
			pc.setOriginalCha(8);
			pc.setAllPoint(16);
		}
			break;

		case 2786:
		case 2796: {
			pc.setOriginalStr(12);
			pc.setOriginalDex(15);
			pc.setOriginalCon(8);
			pc.setOriginalInt(11);
			pc.setOriginalWis(10);
			pc.setOriginalCha(9);
			pc.setAllPoint(10);
		}
			break;
		}
	}
	// 设定初始能力值 end

}
