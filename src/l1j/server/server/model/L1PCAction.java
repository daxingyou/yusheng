package l1j.server.server.model;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import l1j.server.Config;
import l1j.server.server.WarTimeController;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.CenterTable;
import l1j.server.server.datatables.ChatObsceneTable;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.EnchantRingListTable;
import l1j.server.server.datatables.FindItemCountTable;
import l1j.server.server.datatables.HeallingPotionTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MapExpTable;
import l1j.server.server.datatables.MobSkillTable;
import l1j.server.server.datatables.NotDropTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.ServerBlessEnchantTable;
import l1j.server.server.datatables.ServerFailureEnchantTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.SpawnBossTable;
import l1j.server.server.datatables.TownSetTable;
import l1j.server.server.datatables.WeaponEnchantDmgTable;
import l1j.server.server.datatables.lock.CharSkillReading;
import l1j.server.server.datatables.lock.CharacterAdenaTradeReading;
import l1j.server.server.datatables.lock.CharaterTradeReading;
import l1j.server.server.datatables.lock.RanKingReading;
import l1j.server.server.datatables.lock.ShouBaoReading;
import l1j.server.server.datatables.lock.ShouShaReading;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_CloseList;
import l1j.server.server.serverpackets.S_GreenMessage;
import l1j.server.server.serverpackets.S_HowManyMake;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_RetrieveList;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_War;
import l1j.server.server.storage.CharactersItemStorage;
import l1j.server.server.templates.L1CharacterAdenaTrade;
import l1j.server.server.templates.L1CharaterTrade;
import l1j.server.server.templates.L1Drop;
import l1j.server.server.templates.L1FindShopSell;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1ShouBaoTemp;
import l1j.server.server.templates.L1ShouShaTemp;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.templates.L1UserSkillTmp;
import l1j.server.server.timecontroller.HuoDongMapTimer;
import l1j.server.server.timecontroller.WorldCalcExp;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1PCAction {
	private static final Log _log = LogFactory.getLog(L1PCAction.class
			.getName());
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy年MM月dd HH:mm:ss");
	public static final String[] TYPE_CLASS = new String[] { "王族", "骑士", "精灵",
			"法师", "黑妖", "龙骑士", "幻术师" };
	private static final int skillIds[] = new int[] { 26, 42, 43, 48, 79, 151,
			158, 148, 115, 117 };
	private final L1PcInstance _pc;

	public L1PCAction(final L1PcInstance pc) {
		_pc = pc;
	}

	public void action(final String cmd, final long amount) {
		try {
			if (cmd.equalsIgnoreCase("gmreCancellationOn")) {
				if (_pc.isGm()) {
					Config.GM_CANCELLATION_ON = !Config.GM_CANCELLATION_ON;
					_pc.sendPackets(new S_SystemMessage(String.format(
							"魔法相消术对自同盟玩家已更新为:百分之百%s",
							Config.GM_CANCELLATION_ON ? "失败" : "成功")));
				}
			} else if (cmd.equalsIgnoreCase("gmredollPowerrnd")) {
				if (_pc.isGm()) {
					Config.reDollPowerRandomload();
					_pc.sendPackets(new S_SystemMessage(String.format(
							"魔法娃娃洗练属性条数也刷新  4条机率%d%% 3条机率%d%% 2条机率%d%%",
							Config.dollPower4Random, Config.dollPower3Random,
							Config.dollPower2Random)));
				}
			} else if (cmd.equalsIgnoreCase("gmreObscene")) {
				if (_pc.isGm()) {
					ChatObsceneTable.getInstance().reload();
					_pc.sendPackets(new S_SystemMessage("聊天字符屏蔽刷新成功"));
				}
			} else if (cmd.equalsIgnoreCase("gmreMobRandomName")) {
				if (_pc.isGm()) {
					Config.mobRandomName = !Config.mobRandomName;
					_pc.sendPackets(new S_SystemMessage(String.format(
							"怪物名称随机显示已%s", Config.mobRandomName ? "开启" : "关闭")));
				}
			} else if (cmd.equalsIgnoreCase("gmreclanItem")) {
				if (_pc.isGm()) {
					Config.clanItemCheck = !Config.clanItemCheck;
					_pc.sendPackets(new S_SystemMessage(String.format(
							"血盟赠送金币已%s", Config.clanItemCheck ? "开启" : "关闭")));
				}
			} else if (cmd.equalsIgnoreCase("gmreenchantlevel")) {
				if (_pc.isGm()) {
					Config.reMaxWeaponEnchantLevelload();
					_pc.sendPackets(new S_SystemMessage(String.format(
							"刷新成功当前武器最高强化可到%d", Config.WEAPON_MAXENCHANTLEVEL)));
				}
			} else if (cmd.equalsIgnoreCase("gmreAttakMobAIDeath")) {
				if (_pc.isGm()) {
					Config.reAttakMobAIDeathload();
					_pc.sendPackets(new S_SystemMessage(String.format(
							"攻击验证怪已更改为%s", Config.AttakMobAIDeath ? "死亡"
									: "不死亡")));
				}
			} else if (cmd.equalsIgnoreCase("gmretownsettable")) {
				if (_pc.isGm()) {
					TownSetTable.get().reload();
					_pc.sendPackets(new S_SystemMessage("npctown表已刷新"));
				}
			} else if (cmd.equalsIgnoreCase("gmredubo")) {
				if (_pc.isGm()) {
					Config.DuBo = !Config.DuBo;
					_pc.sendPackets(new S_SystemMessage(String.format(
							"赌博系统【%s】", Config.DuBo == true ? "开启" : "关闭")));
				}
			} else if (cmd.equalsIgnoreCase("gmreBlessEnchant")) {
				if (_pc.isGm()) {
					Config.reBlessEnchantload();
					_pc.sendPackets(new S_SystemMessage(String.format(
							"重置祝福卷轴+2机率为:%d", Config.BLESSENCHANT)));
				}
			} else if (cmd.equalsIgnoreCase("gmreFailureEnchant")) {
				if (_pc.isGm()) {
					ServerFailureEnchantTable.get().reload();
					_pc.sendPackets(new S_SystemMessage("\\F2指定道具强化控制已重置."));
				}
			} else if (cmd.equalsIgnoreCase("gmreBlessEnchantCount")) {
				if (_pc.isGm()) {
					ServerBlessEnchantTable.get().reload();
					_pc.sendPackets(new S_SystemMessage(
							"\\F2指定道具祝福卷轴强化跳2控制已重置."));
				}
			} else if (cmd.equalsIgnoreCase("gmrenotdrop")) {
				if (_pc.isGm()) {
					NotDropTable.getInstance().reload();
					_pc.sendPackets(new S_SystemMessage("\\F2锁定道具掉落已重置."));
				}
			} else if (cmd.equalsIgnoreCase("gmreBowHit")) {
				if (_pc.isGm()) {
					Config.reBowHitload();
					_pc.sendPackets(new S_SystemMessage(String.format(
							"重置妖精命中虚弱百分之:%d", Config.BowHit)));
				}
			} else if (cmd.equalsIgnoreCase("gmonguajiAI")) {
				if (_pc.isGm()) {
					if (Config.AICHECK) {
						Config.AICHECK = false;
						_pc.sendPackets(new S_SystemMessage("挂机验证已关闭."));
					} else {
						Config.AICHECK = true;
						_pc.sendPackets(new S_SystemMessage("挂机验证已开启."));
					}
				}
			} else if (cmd.equalsIgnoreCase("gmreenchant")) {
				if (_pc.isGm()) {
					Config.reEnchantload();
					_pc.sendPackets(new S_SystemMessage(String.format(
							"重置武器强化机率为:%d 防具强化机率为%d",
							Config.ENCHANT_CHANCE_WEAPON,
							Config.ENCHANT_CHANCE_ARMOR)));
				}
			} else if (cmd.equalsIgnoreCase("gmremaxlevel")) {
				if (_pc.isGm()) {
					Config.reMaxLevelload();
					_pc.sendPackets(new S_SystemMessage("重置服务器最高等级为:"
							+ Config.MAXLV));
				}
			} else if (cmd.equalsIgnoreCase("gmrecastle")) {
				if (_pc.isGm()) {
					CastleTable.getInstance().reload();
					WarTimeController.getInstance().reload();
					_pc.sendPackets(new S_SystemMessage("【castle】表已重置."));
				}
			} else if (cmd.equalsIgnoreCase("gmovercastle")) {
				if (_pc.isGm()) {
					for (int i = 1; i <= 8; i++) {
						WarTimeController.getInstance().setCastleWarOver(i,
								true);
						_pc.sendPackets(new S_SystemMessage(String.format(
								"攻城Id%d 已被你结束了城战.", i)));
					}
				}
			} else if (cmd.equalsIgnoreCase("gmreskill")) {
				if (_pc.isGm()) {
					SkillsTable.getInstance().reload();
					_pc.sendPackets(new S_SystemMessage("技能表已重置."));
				}
			} else if (cmd.equalsIgnoreCase("gmreringlist")) {
				if (_pc.isGm()) {
					EnchantRingListTable.get().reload();
					_pc.sendPackets(new S_SystemMessage("可强化的戒指表已重置."));
				}
			} else if (cmd.equalsIgnoreCase("gmremobskill")) {
				if (_pc.isGm()) {
					MobSkillTable.getInstance().reload();
					_pc.sendPackets(new S_SystemMessage("mobskill表已重置."));
				}
			} else if (cmd.equalsIgnoreCase("gmredungeon")) {
				if (_pc.isGm()) {
					Dungeon.getInstance().reload();
					_pc.sendPackets(new S_SystemMessage("Dungeon表已重置."));
				}
			} else if (cmd.equalsIgnoreCase("gmreshop0")) {
				if (_pc.isGm()) {
					CenterTable.getInstance().reload();
					_pc.sendPackets(new S_SystemMessage("元宝商店已重置."));
				}
			} else if (cmd.equalsIgnoreCase("gmrepolytable")) {
				if (_pc.isGm()) {
					PolyTable.getInstance().reload();
					_pc.sendPackets(new S_SystemMessage("变身数据已重置."));
				}
			} else if (cmd.equalsIgnoreCase("gmreshop1")) {
				if (_pc.isGm()) {
					ShopTable.getInstance().reload();
					_pc.sendPackets(new S_SystemMessage("金币商店已重置."));
				}
			} else if (cmd.equalsIgnoreCase("gmredrop")) {
				if (_pc.isGm()) {
					DropTable.getInstance().reload();
					_pc.sendPackets(new S_SystemMessage("掉落道具已重置."));
				}
			} else if (cmd.equalsIgnoreCase("gmremapexp")) {
				if (_pc.isGm()) {
					MapExpTable.get().reload();
					_pc.sendPackets(new S_SystemMessage("指定地图经验加倍已重置."));
				}
			} else if (cmd.equalsIgnoreCase("gmreweapondmg")) {
				if (_pc.isGm()) {
					WeaponEnchantDmgTable.get().reload();
					_pc.sendPackets(new S_SystemMessage("武器加成伤害倍数已重置."));
				}
			} else if (cmd.equalsIgnoreCase("gmrehuodong0")) {
				if (_pc.isGm()) {
					if (HuoDongMapTimer.IsStart) {
						_pc.sendPackets(new S_SystemMessage("活动地图已经开放了."));
						return;
					}
					HuoDongMapTimer._startTime = getNextTime(1);
					final HuoDongMapTimer huoDongTimer = new HuoDongMapTimer();
					huoDongTimer.start();
					_pc.sendPackets(new S_SystemMessage("活动地图开放成功 1分钟后正式开始."));
				}
			} else if (cmd.equalsIgnoreCase("gmrehuodong1")) {
				if (_pc.isGm()) {
					if (!HuoDongMapTimer.IsStart) {
						_pc.sendPackets(new S_SystemMessage("活动未开放."));
						return;
					}
					HuoDongMapTimer._stopTime = getNextTime(1);
					_pc.sendPackets(new S_SystemMessage("活动地图停止成功 1分钟后正式停止."));
				}
			} else if (cmd.equalsIgnoreCase("bosschaxun")) {
				_pc.setPage(0);
				loadBossChaXun();
			} else if (cmd.equalsIgnoreCase("bosschaxun_up")) {
				if (_pc.getPage() <= 0) {
					_pc.sendPackets(new S_SystemMessage("已经是第一页了"));
					return;
				}
				_pc.addPage(-1);
				ShowBossSpawnItem(_pc.getPage());
			} else if (cmd.equalsIgnoreCase("bosschaxun_down")) {
				int bosslistPage = _pc.getSpawnBossList().size() / 10;
				if (_pc.getSpawnBossList().size() % 10 != 0) {
					bosslistPage += 1;
				}
				if (_pc.getPage() + 1 >= bosslistPage) {
					_pc.sendPackets(new S_SystemMessage("已经是最后一页了"));
					return;
				}
				_pc.addPage(1);
				ShowBossSpawnItem(_pc.getPage());
			} else if (cmd.equalsIgnoreCase("loadweaponqiehuan")) {
				loadWeaponQieHuan();
			} else if (cmd.startsWith("weapon_index")) {
				selWeaponQieHuan(cmd);
			} else if (cmd.equalsIgnoreCase("loadhealpotion")) {
				loadHealPotion();
			} else if (cmd.equalsIgnoreCase("autoHealing")) {
				setHealIng();
			} else if (cmd.startsWith("heal_persent")) {
				setHealPersent(cmd);
			} else if (cmd.startsWith("heal_index")) {
				setHealIndex(cmd);
			} else if (cmd.equalsIgnoreCase("gamehelp")) {
				showhelpHtml();
			} else if (cmd.equalsIgnoreCase("showemblem")) {
				int war_type = 0;
				if (_pc.isShowEmblem()) {
					war_type = 3;
					_pc.setShowEmblem(false);
				} else {
					war_type = 1;
					_pc.setShowEmblem(true);
				}
				if (_pc.getClanid() != 0) {
					final String emblem_file1 = String.valueOf(_pc.getClanid());
					final File file1 = new File("emblem/" + emblem_file1);
					if (file1.exists()) {
						_pc.sendPackets(new S_War(war_type, _pc.getClanname(),
								""));
					}
				}
				final Collection<L1Clan> clans = L1World.getInstance()
						.getAllClans();
				if (clans != null && !clans.isEmpty()) {
					for (final L1Clan clan : clans) {
						if (clan.getClanId() != _pc.getClanid()) {
							final String emblem_file2 = String.valueOf(clan
									.getClanId());
							final File file2 = new File("emblem/"
									+ emblem_file2);
							if (file2.exists()) {
								_pc.sendPackets(new S_War(war_type, _pc
										.getClanname(), clan.getClanName()));
							}
						}
					}
				}
				showhelpHtml();
			} else if (cmd.equalsIgnoreCase("showhealmessage")) {
				_pc.setShowHealMessage(!_pc.IsShowHealMessage());
				showhelpHtml();
			} else if (cmd.equalsIgnoreCase("showdmg")) {
				_pc.setVdamg(!_pc.isVdmg());
				showhelpHtml();
			} else if (cmd.equalsIgnoreCase("showbulemessage")) {
				_pc.setShowBlue(!_pc.isShowBlue());
				showhelpHtml();
			} else if (cmd.equalsIgnoreCase("showkogifd")) {
				_pc.setKOGifd(!_pc.isKOGifd());
				showhelpHtml();
			} else if (cmd.equalsIgnoreCase("showEnchanrMessage")) {
				_pc.setShowEnchantMessage(!_pc.isShowEnchantMessage());
				showhelpHtml();
			} else if (cmd.equalsIgnoreCase("showMassTeleport")) {
				_pc.setMassTeleport(!_pc.isMassTeleport());
				showhelpHtml();
			} else if (cmd.equalsIgnoreCase("clanparty")) {
				if (_pc.getMapId() == Config.HUODONGMAPID) {
					_pc.sendPackets(new S_SystemMessage("此处禁止组队"));
					return;
				}
				if (_pc.getClanid() == 0) {
					_pc.sendPackets(new S_SystemMessage("你还没有血盟"));
					return;
				}
				if (_pc.isInParty()) {
					if (!_pc.getParty().isLeader(_pc)) {
						_pc.sendPackets(new S_SystemMessage("你不是队长"));
						return;
					}
					if (!_pc.getParty().isVacancy()) {
						_pc.sendPackets(new S_SystemMessage("你的队伍已经满员"));
						return;
					}
				}
				for (final L1PcInstance tagpc : L1World.getInstance()
						.getVisiblePlayer(_pc)) {
					if (tagpc.getClanid() == _pc.getClanid()) {
						if (!tagpc.isInParty()) {
							tagpc.setPartyID(_pc.getId());
							tagpc.sendPackets(new S_Message_YN(953, _pc
									.getName()));
						}
					}
				}
				_pc.sendPackets(new S_SystemMessage("已成功向周围血盟成员发送组队邀请!!"));
			} else if (cmd.equalsIgnoreCase("drop_chakan")) {
				if (_pc.get_tuokui_objId() != 0) {
					final ArrayList<L1Drop> droplist = DropTable.getInstance()
							.getDrops(_pc.get_tuokui_objId());
					_pc.set_tuokui_objId(0);
					if (droplist != null && !droplist.isEmpty()) {
						_pc.sendPackets(new S_RetrieveList(_pc, droplist));
					} else {
						_pc.sendPackets(new S_SystemMessage("该怪物没有掉落任何物品"));
					}
				}
			} else if (cmd.equalsIgnoreCase("eq_chakan")) {
				if (_pc.get_tuokui_objId() != 0) {
					final L1Object target = L1World.getInstance().findObject(
							_pc.get_tuokui_objId());
					_pc.set_tuokui_objId(0);
					if (target == null) {
						_pc.sendPackets(new S_SystemMessage("玩家已经不在线上."));
						return;
					}
					if (target instanceof L1PcInstance) {
						final L1PcInstance tagpc = (L1PcInstance) target;
						_pc.sendPackets(new S_RetrieveList(_pc, tagpc));
					}
				}
			} else if (cmd.equalsIgnoreCase("doll_compose")) {
				_pc.sendPackets(new S_RetrieveList(_pc));
			} else if (cmd.equalsIgnoreCase("ezpayranking")) {
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "ranking1",
						RanKingReading.get().getEzpayRankingData()));
			} else if (cmd.equalsIgnoreCase("levelranking")) {
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "ranking2",
						RanKingReading.get().getAllLevelRankingData()));
			} else if (cmd.equalsIgnoreCase("levelranking0")) {
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "ranking20",
						RanKingReading.get().getLevelRankingData(0)));
			} else if (cmd.equalsIgnoreCase("levelranking1")) {
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "ranking20",
						RanKingReading.get().getLevelRankingData(1)));
			} else if (cmd.equalsIgnoreCase("levelranking2")) {
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "ranking20",
						RanKingReading.get().getLevelRankingData(2)));
			} else if (cmd.equalsIgnoreCase("levelranking3")) {
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "ranking20",
						RanKingReading.get().getLevelRankingData(3)));
			} else if (cmd.equalsIgnoreCase("levelranking4")) {
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "ranking20",
						RanKingReading.get().getLevelRankingData(4)));
			} else if (cmd.equalsIgnoreCase("levelranking5")) {
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "ranking20",
						RanKingReading.get().getLevelRankingData(5)));
			} else if (cmd.equalsIgnoreCase("weaponranking")) {
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "ranking3",
						RanKingReading.get().getWeaponRankingData()));
			} else if (cmd.equalsIgnoreCase("armorranking")) {
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "ranking4",
						RanKingReading.get().getArmorRankingData()));
			} else if (cmd.equalsIgnoreCase("pkranking")) {
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "ranking5",
						RanKingReading.get().getPKRankingData()));
			} else if (cmd.equalsIgnoreCase("deathranking")) {
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "ranking6",
						RanKingReading.get().getDeathRankingData()));
			} else if (cmd.equalsIgnoreCase("index04")) {
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "finditem",
						FindItemCountTable.get().getItemData()));
			} else if (cmd.equalsIgnoreCase("index05")) {
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "enchantlv",
						FindItemCountTable.get().getWeaponData()));
			} else if (cmd.equalsIgnoreCase("index06")) {
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "enchantlv",
						FindItemCountTable.get().getArmorData()));
			} else if (cmd.equalsIgnoreCase("topcpshow")) {
				if (_pc.isGm()) {
					_pc.setPage(0);
					loadTopcHtml(0);
				}
			} else if (cmd.equalsIgnoreCase("topcpshow_up")) {
				if (_pc.isGm()) {
					if (_pc.getPage() <= 0) {
						_pc.sendPackets(new S_SystemMessage("已经是第一页了"));
						return;
					}
					_pc.addPage(-1);
					loadTopcHtml(_pc.getPage());
				}
			} else if (cmd.equalsIgnoreCase("topcpshow_down")) {
				if (_pc.isGm()) {
					int topcPage = L1World.getInstance().getTopcItemCount() / 150;
					if (L1World.getInstance().getTopcItemCount() % 150 != 0) {
						topcPage += 1;
					}
					if (_pc.getPage() + 1 >= topcPage) {
						_pc.sendPackets(new S_SystemMessage("已经是最后一页了"));
						return;
					}
					_pc.addPage(1);
					loadTopcHtml(_pc.getPage());
				}
			} else if (cmd.startsWith("topcp_index")) {
				if (_pc.isGm()) {
					final int topcIndex = Integer.parseInt(cmd.substring(11))
							+ _pc.getPage() * 20;
					if (topcIndex < 0
							|| topcIndex >= L1World.getInstance()
									.getTopcItemCount()) {
						return;
					}
					final String topcName = L1World.getInstance()
							.getTopcItemName(topcIndex);
					final L1PcInstance topc = L1World.getInstance().getPlayer(
							topcName);
					if (topc == null) {
						_pc.sendPackets(new S_SystemMessage("该玩家已经不在线上了。"));
						return;
					}
					L1Teleport.teleport(_pc, topc.getLocation(),
							topc.getHeading(), false);
					loadTopcHtml(_pc.getPage());
				}
			} else if (cmd.startsWith("start")) {
				if (!_pc.isAiRunning()) {
					_pc.startAI();
					//_pc.setskillAuto_gj(true); // 开启挂机状态设置
				}
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			} else if (cmd.equalsIgnoreCase("adena_trade_up")) {
				if (_pc.getPage() <= 0) {
					_pc.sendPackets(new S_SystemMessage("已经是第一页了。"));
					return;
				}
				_pc.setPage(_pc.getPage() - 1);
				ShowAdenaTrade(_pc.getPage());
			} else if (cmd.equalsIgnoreCase("adena_trade_down")) {
				final int listsize = _pc.getAdenaTradeList().size();
				int pageamount = listsize / 10;
				if (listsize % 10 != 0) {
					pageamount += 1;
				}
				if (_pc.getPage() + 1 >= pageamount) {
					_pc.sendPackets(new S_SystemMessage("已经是最后一页了。"));
					return;
				}
				_pc.setPage(_pc.getPage() + 1);
				ShowAdenaTrade(_pc.getPage());
			} else if (cmd.equalsIgnoreCase("adena_trade_up1")) {
				if (_pc.getPage() <= 0) {
					_pc.sendPackets(new S_SystemMessage("已经是第一页了。"));
					return;
				}
				_pc.setPage(_pc.getPage() - 1);
				ShowAdenaTradePC(_pc.getPage());
			} else if (cmd.equalsIgnoreCase("adena_trade_down1")) {
				final int listsize = _pc.getAdenaTradeList().size();
				int pageamount = listsize / 10;
				if (listsize % 10 != 0) {
					pageamount += 1;
				}
				if (_pc.getPage() + 1 >= pageamount) {
					_pc.sendPackets(new S_SystemMessage("已经是最后一页了。"));
					return;
				}
				_pc.setPage(_pc.getPage() + 1);
				ShowAdenaTradePC(_pc.getPage());
			} else if (cmd.startsWith("adena_trade_")) {
				adenaTrade(cmd, amount);
			} else if (cmd.equalsIgnoreCase("shou_bao_cx")) {
				ShouBaoChaXun();
			} else if (cmd.equalsIgnoreCase("shou_sha_cx")) {
				ShouShaChaXun();
			} else if (cmd.startsWith("characterTrade_")) {
				CharaterTrade(cmd, amount);
			} else if (cmd.startsWith("bindcharIndex")) {
				bindTradeChar(cmd);
			} else if (cmd.startsWith("rebindchar")) {
				rebindTradeChar();
			} else if (cmd.startsWith("findshopsell_")) {
				showFindShopSell(cmd);

				/********************** 特殊变身系统 **********************/
			} else if (cmd.equalsIgnoreCase("re ssfs")) { // 神圣法师变身
				if (_pc.getInventory().consumeItem(44070, 1)) {
					L1PolyMorph.doPoly(_pc, 12286, 1800);
					_pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 6130));// 变卷动画效果
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("\\F2元宝不足1."));
				}

			} else if (cmd.equalsIgnoreCase("re jlgz")) { // 神圣法师变身
				if (_pc.getInventory().consumeItem(44070, 1)) {
					L1PolyMorph.doPoly(_pc, 13717, 1800);
					_pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 6130));// 变卷动画效果
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("\\F2元宝不足1."));
				}

			} else if (cmd.equalsIgnoreCase("re jlyj")) { // 神圣法师变身
				if (_pc.getInventory().consumeItem(44070, 1)) {
					L1PolyMorph.doPoly(_pc, 13725, 1800);
					_pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 6130));// 变卷动画效果
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("\\F2元宝不足1."));
				}

			} else if (cmd.equalsIgnoreCase("re ydcz")) { // 神圣法师变身
				if (_pc.getInventory().consumeItem(44070, 1)) {
					L1PolyMorph.doPoly(_pc, 12283, 1800);
					_pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 6130));// 变卷动画效果
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("\\F2元宝不足1."));
				}

			} else if (cmd.equalsIgnoreCase("re hdsq")) { // 神圣法师变身
				if (_pc.getInventory().consumeItem(44070, 1)) {
					L1PolyMorph.doPoly(_pc, 13152, 1800);
					_pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 6130));// 变卷动画效果
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("\\F2元宝不足1."));
				}

			} else if (cmd.equalsIgnoreCase("re ldsq")) { // 神圣法师变身
				if (_pc.getInventory().consumeItem(44070, 1)) {
					L1PolyMorph.doPoly(_pc, 13153, 1800);
					_pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 6130));// 变卷动画效果
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("\\F2元宝不足1."));
				}

			} else if (cmd.equalsIgnoreCase("re zswqs")) { // 神圣法师变身
				if (_pc.getInventory().consumeItem(44070, 1)) {
					L1PolyMorph.doPoly(_pc, 17541, 1800);
					_pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 6130));// 变卷动画效果
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("\\F2元宝不足1."));
				}

			} else if (cmd.equalsIgnoreCase("re zbft")) { // 神圣法师变身
				if (_pc.getInventory().consumeItem(44070, 1)) {
					L1PolyMorph.doPoly(_pc, 17515, 1800);
					_pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 6130));// 变卷动画效果
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("\\F2元宝不足1."));
				}

			} else if (cmd.equalsIgnoreCase("re zhajl")) { // 神圣法师变身
				if (_pc.getInventory().consumeItem(44070, 1)) {
					L1PolyMorph.doPoly(_pc, 17531, 1800);
					_pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 6130));// 变卷动画效果
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("\\F2元宝不足1."));
				}

			} else if (cmd.equalsIgnoreCase("re zhayj")) { // 神圣法师变身
				if (_pc.getInventory().consumeItem(44070, 1)) {
					L1PolyMorph.doPoly(_pc, 17535, 1800);
					_pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 6130));// 变卷动画效果
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("\\F2元宝不足1."));
				}

				/********************** 在线添加状态系统 *******************/
			} else if (cmd.equalsIgnoreCase("zaixianzt")) {
				final String zhuangtai = !_pc.isskillAuto() ? "关闭" : String
						.valueOf("开启");
				final String[] htmldata = new String[] {
						String.valueOf(WorldCalcExp.get().getTime()), zhuangtai };
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "zaixianzt",
						htmldata));

			} else if (cmd.equalsIgnoreCase("addexp")) {
				if (_pc.getInventory().checkItem(44070, 50)) {
					_pc.getInventory().consumeItem(44070, 50);
					WorldCalcExp.get().addTime(3600);// 1个小时
					WorldCalcExp.get().start();
					final StringBuilder msg = new StringBuilder();
					msg.append("\\f=玩家【\\f2");
					msg.append(_pc.getName());
					msg.append("\\f=】为全服双倍经验时长增加了\\f4[1小时]");
					L1World.getInstance().broadcastPacketToAll(
							new S_GreenMessage(msg.toString()));
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("\\F2元宝不足50."));
				}
			} else if (cmd.equalsIgnoreCase("addskill")) {
				if (_pc.getInventory().checkItem(44070, 10)) {
					_pc.getInventory().consumeItem(44070, 10);
					for (final L1PcInstance targetpc : L1World.getInstance()
							.getAllPlayers()) {
						if (targetpc.isPrivateShop()
								|| targetpc.getNetConnection() == null) {
							continue;
						}
						for (final int element : skillIds) {
							int skillId = element;
							if (skillId == 148) {
								if (targetpc.isElf()) {
									skillId = 149;
								}
							}
							new L1SkillUse().handleCommands(targetpc, skillId,
									targetpc.getId(), targetpc.getX(),
									targetpc.getY(), null, 1800,
									L1SkillUse.TYPE_GMBUFF);
						}
					}
					final StringBuilder msg = new StringBuilder();
					msg.append("\\f=玩家【\\f2");
					msg.append(_pc.getName());
					msg.append("\\f=】为全服在线玩家加buff");
					L1World.getInstance().broadcastPacketToAll(
							new S_GreenMessage(msg.toString()));
				} else {
					_pc.sendPackets(new S_SystemMessage("元宝不足10."));
				}
			} else if (cmd.equalsIgnoreCase("addchanskill")) {
				if (_pc.getClanid() == 0 || _pc.getClan() == null) {
					_pc.sendPackets(new S_SystemMessage("\\F2你还没有加入血盟"));
					return;
				}
				if (_pc.getInventory().checkItem(44070, 5)) {
					_pc.getInventory().consumeItem(44070, 5);
					for (final L1PcInstance targetchanpc : _pc.getClan()
							.getOnlineClanMember()) {
						if (targetchanpc.isPrivateShop()
								|| targetchanpc.getNetConnection() == null) {
							continue;
						}
						for (final int element : skillIds) {
							int skillId = element;
							if (skillId == 148) {
								if (targetchanpc.isElf()) {
									skillId = 149;
								}
							}
							new L1SkillUse().handleCommands(targetchanpc,
									skillId, targetchanpc.getId(),
									targetchanpc.getX(), targetchanpc.getY(),
									null, 1800, L1SkillUse.TYPE_GMBUFF);
						}
					}
					final StringBuilder msg = new StringBuilder();
					msg.append("\\f=【\\f2");
					msg.append(_pc.getClanname());
					msg.append("\\f=】血盟的土豪<\\f2");
					msg.append(_pc.getName());
					msg.append("\\f=>使用元宝为其在线成员加buff");
					L1World.getInstance().broadcastPacketToAll(
							new S_GreenMessage(msg.toString()));
				} else {
					_pc.sendPackets(new S_SystemMessage("元宝不足5."));
				}
			} else if (cmd.equalsIgnoreCase("addchanskilljb")) {
				if (_pc.getClanid() == 0 || _pc.getClan() == null) {
					_pc.sendPackets(new S_SystemMessage("\\F2你还没有加入血盟"));
					return;
				}
				if (_pc.getInventory().checkItem(40308, 50000)) {
					_pc.getInventory().consumeItem(40308, 50000);
					for (final L1PcInstance targetchanpc : _pc.getClan()
							.getOnlineClanMember()) {
						if (targetchanpc.isPrivateShop()
								|| targetchanpc.getNetConnection() == null) {
							continue;
						}
						for (final int element : skillIds) {
							int skillId = element;
							if (skillId == 148) {
								if (targetchanpc.isElf()) {
									skillId = 149;
								}
							}
							new L1SkillUse().handleCommands(targetchanpc,
									skillId, targetchanpc.getId(),
									targetchanpc.getX(), targetchanpc.getY(),
									null, 1800, L1SkillUse.TYPE_GMBUFF);
						}
					}
					final StringBuilder msg = new StringBuilder();
					msg.append("\\f=【\\f2");
					msg.append(_pc.getClanname());
					msg.append("\\f=】血盟的土豪<\\f2");
					msg.append(_pc.getName());
					msg.append("\\f=>使用金币为其在线成员加buff");
					L1World.getInstance().broadcastPacketToAll(
							new S_GreenMessage(msg.toString()));
				} else {
					_pc.sendPackets(new S_SystemMessage("金币不够5万"));
				}
			} else if (cmd.equalsIgnoreCase("pcskill")) {
				if (_pc.getInventory().checkItem(44070, 1)) {
					_pc.getInventory().consumeItem(44070, 1);
					if (_pc.isPrivateShop() || _pc.getNetConnection() == null) {
						return;
					}
					for (final int element : skillIds) {
						int skillId = element;
						if (skillId == 148) {
							if (_pc.isElf()) {
								skillId = 149;
							}
						}
						new L1SkillUse().handleCommands(_pc, skillId,
								_pc.getId(), _pc.getX(), _pc.getY(), null,
								1800, L1SkillUse.TYPE_GMBUFF);
					}
				} else {
					_pc.sendPackets(new S_SystemMessage("元宝不足1."));
				}
				
			} else if (cmd.equalsIgnoreCase("pcskilljb")) {
				if (_pc.getInventory().checkItem(40308, 20000)) {
					_pc.getInventory().consumeItem(40308, 20000);
					if (_pc.isPrivateShop() || _pc.getNetConnection() == null) {
						return;
					}
					for (final int element : skillIds) {
						int skillId = element;
						if (skillId == 148) {
							if (_pc.isElf()) {
								skillId = 149;
							}
						}
						new L1SkillUse().handleCommands(_pc, skillId,
								_pc.getId(), _pc.getX(), _pc.getY(), null,
								1800, L1SkillUse.TYPE_GMBUFF);
					}
				} else {
					_pc.sendPackets(new S_SystemMessage("金币不够2万"));
				}
				
			} else if (cmd.equalsIgnoreCase("zidongpcskill")) {
				if (!_pc.getInventory().checkItem(44070, 10)) {
					_pc.sendPackets(new S_SystemMessage("元宝必须大于10个才能开启此功能"));
					return;
				}
				if (!_pc.isskillAuto()) { // 没有开启烈炎
					_pc.setskillAuto(true);
					_pc.sendPackets(new S_SystemMessage("\\aD开启自动加状态(谨慎使用)"));
					final String zhuangtai = !_pc.isskillAuto() ? "关闭" : String
							.valueOf("开启");
					final String[] htmldata = new String[] {
							String.valueOf(WorldCalcExp.get().getTime()),
							zhuangtai };
					_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(),
							"zaixianzt", htmldata));
				} else {
					_pc.setskillAuto(false);
					_pc.sendPackets(new S_SystemMessage("\\aG关闭自动状态"));
					final String zhuangtai = !_pc.isskillAuto() ? "关闭" : String
							.valueOf("开启");
					final String[] htmldata = new String[] {
							String.valueOf(WorldCalcExp.get().getTime()),
							zhuangtai };
					_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(),
							"zaixianzt", htmldata));
				}

				/********************** 在线添加状态系统 *******************/

				/********************** 特殊传送书 ************************/
				/** 宫殿记忆书 */
			} else if (cmd.equalsIgnoreCase("teleport1")) {// 道具商城
				L1Teleport.teleport(_pc, 33977, 32927, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport2")) {// 道具商城
				L1Teleport.teleport(_pc, 34268, 33113, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport3")) {// 道具商城
				L1Teleport.teleport(_pc, 33709, 33307, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport4")) {// 道具商城
				L1Teleport.teleport(_pc, 32865, 33251, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport5")) {// 道具商城
				L1Teleport.teleport(_pc, 32855, 32900, (short) 304,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport6")) {// 道具商城
				L1Teleport.teleport(_pc, 34048, 32404, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport7")) {// 道具商城
				L1Teleport.teleport(_pc, 32800, 32856, (short) 1000,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport8")) {// 道具商城
				L1Teleport.teleport(_pc, 32800, 32868, (short) 1001,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport9")) {// 道具商城
				L1Teleport.teleport(_pc, 33136, 32241, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport10")) {// 道具商城
				L1Teleport.teleport(_pc, 32881, 32652, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport11")) {// 道具商城
				L1Teleport.teleport(_pc, 32767, 32803, (short) 77,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport12")) {// 道具商城
				L1Teleport.teleport(_pc, 32764, 32842, (short) 77,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport13")) {// 道具商城
				L1Teleport.teleport(_pc, 32667, 32808, (short) 1,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport14")) {// 道具商城
				L1Teleport.teleport(_pc, 32811, 32723, (short) 23,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport15")) {// 道具商城
				L1Teleport.teleport(_pc, 32784, 32750, (short) 43,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport16")) {// 道具商城
				L1Teleport.teleport(_pc, 32772, 32829, (short) 75,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport17")) {// 道具商城
				L1Teleport.teleport(_pc, 33660, 32465, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport18")) {// 道具商城
				L1Teleport.teleport(_pc, 33392, 32333, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport19")) {// 道具商城
				L1Teleport.teleport(_pc, 32707, 32859, (short) 440,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport20")) {// 道具商城
				L1Teleport.teleport(_pc, 32676, 32960, (short) 521,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport21")) {// 道具商城
				L1Teleport.teleport(_pc, 32757, 32794, (short) 600,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport22")) {// 道具商城
				L1Teleport.teleport(_pc, 32729, 32847, (short) 451,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport23")) {// 道具商城
				L1Teleport.teleport(_pc, 32422, 33074, (short) 440,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport24")) {// 道具商城
				L1Teleport.teleport(_pc, 33959, 33364, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport25")) {// 道具商城
				L1Teleport.teleport(_pc, 33051, 32340, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport26")) {// 道具商城
				L1Teleport.teleport(_pc, 32728, 32792, (short) 551,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport27")) {// 道具商城
				L1Teleport.teleport(_pc, 32682, 32831, (short) 603,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport28")) {// 道具商城
				L1Teleport.teleport(_pc, 32745, 32860, (short) 441,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport29")) {// 道具商城
				L1Teleport.teleport(_pc, 32648, 32921, (short) 535,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport30")) {// 道具商城
				L1Teleport.teleport(_pc, 32929, 32994, (short) 410,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport31")) {// 道具商城
				L1Teleport.teleport(_pc, 32921, 32800, (short) 430,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport32")) {// 道具商城
				L1Teleport.teleport(_pc, 32630, 32934, (short) 111,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport33")) {// 道具商城
				L1Teleport.teleport(_pc, 34247, 33453, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport34")) {// 道具商城
				L1Teleport.teleport(_pc, 32630, 32935, (short) 121,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport35")) {// 道具商城
				L1Teleport.teleport(_pc, 32630, 32935, (short) 131,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport36")) {// 道具商城
				L1Teleport.teleport(_pc, 32630, 32935, (short) 141,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport37")) {// 道具商城
				L1Teleport.teleport(_pc, 32666, 32862, (short) 151,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport38")) {// 道具商城
				L1Teleport.teleport(_pc, 32666, 32862, (short) 161,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport39")) {// 道具商城
				L1Teleport.teleport(_pc, 32666, 32862, (short) 171,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport40")) {// 道具商城
				L1Teleport.teleport(_pc, 32666, 32862, (short) 181,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport41")) {// 道具商城
				L1Teleport.teleport(_pc, 32666, 32862, (short) 191,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport42")) {// 道具商城
				L1Teleport.teleport(_pc, 32826, 32849, (short) 70,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport43")) {// 道具商城
				L1Teleport.teleport(_pc, 32982, 32808, (short) 244,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport44")) {// 道具商城
				L1Teleport.teleport(_pc, 32532, 32954, (short) 777,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport45")) {// 道具商城
				L1Teleport.teleport(_pc, 32726, 33132, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport46")) {// 道具商城
				L1Teleport.teleport(_pc, 32905, 33155, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport47")) {// 道具商城
				L1Teleport.teleport(_pc, 32779, 32751, (short) 67,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport48")) {// 道具商城
				L1Teleport.teleport(_pc, 34049, 32980, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport49")) {// 道具商城
				L1Teleport.teleport(_pc, 32667, 32868, (short) 37,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport50")) {// 道具商城
				L1Teleport.teleport(_pc, 32756, 32865, (short) 65,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport76")) {// 道具商城
				L1Teleport.teleport(_pc, 32743, 32833, (short) 72,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport77")) {// 道具商城
				L1Teleport.teleport(_pc, 32782, 32736, (short) 73,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport78")) {// 道具商城
				L1Teleport.teleport(_pc, 32728, 32815, (short) 74,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport65")) {// 道具商城
				L1Teleport.teleport(_pc, 32805, 32725, (short) 12,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport66")) {// 道具商城
				L1Teleport.teleport(_pc, 32730, 32726, (short) 13,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport58")) {// 道具商城
				L1Teleport.teleport(_pc, 32891, 32856, (short) 307,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport59")) {// 道具商城
				L1Teleport.teleport(_pc, 32882, 32856, (short) 308,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport60")) {// 道具商城
				L1Teleport.teleport(_pc, 32757, 32836, (short) 309,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport57")) {// 道具商城
				L1Teleport.teleport(_pc, 32771, 32870, (short) 400,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport54")) {// 道具商城
				L1Teleport.teleport(_pc, 32665, 32895, (short) 522,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport55")) {// 道具商城
				L1Teleport.teleport(_pc, 32665, 32895, (short) 523,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport56")) {// 道具商城
				L1Teleport.teleport(_pc, 32665, 32895, (short) 524,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport61")) {// 道具商城
				L1Teleport.teleport(_pc, 32830, 32822, (short) 604,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport62")) {// 道具商城
				L1Teleport.teleport(_pc, 32835, 32822, (short) 605,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport63")) {// 道具商城
				L1Teleport.teleport(_pc, 32757, 32842, (short) 606,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport64")) {// 道具商城
				L1Teleport.teleport(_pc, 32773, 32835, (short) 607,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport51")) {// 道具商城
				L1Teleport.teleport(_pc, 32745, 32862, (short) 442,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport52")) {// 道具商城
				L1Teleport.teleport(_pc, 32759, 32863, (short) 443,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport53")) {// 道具商城
				L1Teleport.teleport(_pc, 32732, 32896, (short) 420,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport67")) {// 道具商城
				L1Teleport.teleport(_pc, 32786, 32867, (short) 116,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport68")) {// 道具商城
				L1Teleport.teleport(_pc, 32786, 32867, (short) 126,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport69")) {// 道具商城
				L1Teleport.teleport(_pc, 32786, 32867, (short) 136,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport70")) {// 道具商城
				L1Teleport.teleport(_pc, 32786, 32867, (short) 146,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport71")) {// 道具商城
				L1Teleport.teleport(_pc, 32789, 32804, (short) 156,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport72")) {// 道具商城
				L1Teleport.teleport(_pc, 32789, 32804, (short) 166,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport73")) {// 道具商城
				L1Teleport.teleport(_pc, 32789, 32804, (short) 176,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport74")) {// 道具商城
				L1Teleport.teleport(_pc, 32789, 32804, (short) 186,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport75")) {// 道具商城
				L1Teleport.teleport(_pc, 32733, 32857, (short) 200,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport84")) {// 道具商城
				L1Teleport.teleport(_pc, 32745, 32857, (short) 60,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport85")) {// 道具商城
				L1Teleport.teleport(_pc, 32730, 32817, (short) 61,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport86")) {// 道具商城
				L1Teleport.teleport(_pc, 32795, 32871, (short) 63,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport79")) {// 道具商城
				L1Teleport.teleport(_pc, 32740, 32777, (short) 30,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport80")) {// 道具商城
				L1Teleport.teleport(_pc, 32760, 32781, (short) 31,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport81")) {// 道具商城
				L1Teleport.teleport(_pc, 32709, 32818, (short) 32,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport82")) {// 道具商城
				L1Teleport.teleport(_pc, 32669, 32869, (short) 33,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport83")) {// 道具商城
				L1Teleport.teleport(_pc, 32798, 32754, (short) 9,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport87")) {// 道具商城
				L1Teleport.teleport(_pc, 32892, 32769, (short) 78,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport88")) {// 道具商城
				L1Teleport.teleport(_pc, 32726, 32847, (short) 451,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport89")) {// 道具商城
				L1Teleport.teleport(_pc, 32726, 32824, (short) 452,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport90")) {// 道具商城
				L1Teleport.teleport(_pc, 32725, 32851, (short) 453,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport91")) {// 道具商城
				L1Teleport.teleport(_pc, 32726, 32872, (short) 454,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport92")) {// 道具商城
				L1Teleport.teleport(_pc, 32726, 32852, (short) 455,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport93")) {// 道具商城
				L1Teleport.teleport(_pc, 32756, 32869, (short) 456,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport94")) {// 道具商城
				L1Teleport.teleport(_pc, 32664, 32853, (short) 457,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("teleport95")) {// 道具商城
				L1Teleport.teleport(_pc, 32811, 32819, (short) 460,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));

				// 新傲慢塔1楼
			} else if (cmd.equalsIgnoreCase("teleport96")) {// 道具商城
				L1Teleport.teleport(_pc, 32811, 32819, (short) 3301,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));

			} else if (cmd.equalsIgnoreCase("aqqgj")) {// 道具商城
				L1Teleport.teleport(_pc, 32828, 32900, (short) 320,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));

			} else if (cmd.equalsIgnoreCase("xywdt")) {// 道具商城
				L1Teleport.teleport(_pc, 32811, 32819, (short) 1700,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));

			} else if (cmd.equalsIgnoreCase("lasitabade1")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 34230, 33369, (short) 4,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade2")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 33385, 32349, (short) 4,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade3")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 33718, 32240, (short) 4,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade4")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32838, 32758, (short) 453,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade5")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32805, 32839, (short) 462,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade6")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32853, 32863, (short) 492,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade7")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32862, 32840, (short) 530,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade8")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32757, 32744, (short) 531,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade9")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32791, 32786, (short) 531,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade10")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32845, 32857, (short) 531,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade11")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32789, 32812, (short) 532,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade12")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32859, 32897, (short) 533,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade13")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32789, 32891, (short) 533,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade14")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32753, 32811, (short) 533,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade15")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32758, 32758, (short) 24,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade16")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32704, 32842, (short) 2,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade17")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32738, 32739, (short) 10,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade18")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32753, 32776, (short) 73,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade19")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32842, 32921, (short) 74,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade20")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32681, 32851, (short) 0,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade21")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32741, 32742, (short) 10,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade22")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32675, 32860, (short) 254,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade23")) {// 道具商城
				// if (_pc.getInventory().consumeItem(40308, 50000)) {
				L1Teleport.teleport(_pc, 32801, 32801, (short) 110,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				// } else {
				// _pc.sendPackets(new S_SystemMessage("50000金币不足."));
				// }
			} else if (cmd.equalsIgnoreCase("lasitabade24")) {// 道具商城
				// if (_pc.getInventory().consumeItem(40308, 50000)) {
				L1Teleport.teleport(_pc, 32801, 32801, (short) 120,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				// } else {
				// _pc.sendPackets(new S_SystemMessage("50000金币不足."));
				// }
			} else if (cmd.equalsIgnoreCase("lasitabade25")) {// 道具商城
				// if (_pc.getInventory().consumeItem(40308, 50000)) {
				L1Teleport.teleport(_pc, 32801, 32801, (short) 130,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				// } else {
				// _pc.sendPackets(new S_SystemMessage("50000金币不足."));
				// }
			} else if (cmd.equalsIgnoreCase("lasitabade26")) {// 道具商城
				// if (_pc.getInventory().consumeItem(40308, 50000)) {
				L1Teleport.teleport(_pc, 32801, 32801, (short) 140,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				// } else {
				// _pc.sendPackets(new S_SystemMessage("50000金币不足."));
				// }
			} else if (cmd.equalsIgnoreCase("lasitabade27")) {// 道具商城
				// if (_pc.getInventory().consumeItem(40308, 50000)) {
				L1Teleport.teleport(_pc, 32797, 32795, (short) 150,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				// } else {
				// _pc.sendPackets(new S_SystemMessage("50000金币不足."));
				// }
			} else if (cmd.equalsIgnoreCase("lasitabade28")) {// 道具商城
				// if (_pc.getInventory().consumeItem(40308, 50000)) {
				L1Teleport.teleport(_pc, 32721, 32823, (short) 160,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				// } else {
				// _pc.sendPackets(new S_SystemMessage("50000金币不足."));
				// }
			} else if (cmd.equalsIgnoreCase("lasitabade29")) {// 道具商城
				// if (_pc.getInventory().consumeItem(40308, 50000)) {
				L1Teleport.teleport(_pc, 32721, 32823, (short) 170,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				// } else {
				// _pc.sendPackets(new S_SystemMessage("50000金币不足."));
				// }
			} else if (cmd.equalsIgnoreCase("lasitabade30")) {// 道具商城
				// if (_pc.getInventory().consumeItem(40308, 50000)) {
				L1Teleport.teleport(_pc, 32721, 32823, (short) 180,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				// } else {
				// _pc.sendPackets(new S_SystemMessage("50000金币不足."));
				// }
			} else if (cmd.equalsIgnoreCase("lasitabade31")) {// 道具商城
				// if (_pc.getInventory().consumeItem(40308, 50000)) {
				L1Teleport.teleport(_pc, 32721, 32823, (short) 190,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				// } else {
				// _pc.sendPackets(new S_SystemMessage("50000金币不足."));
				// }
			} else if (cmd.equalsIgnoreCase("lasitabade32")) {// 道具商城
				// if (_pc.getInventory().consumeItem(40308, 50000)) {
				L1Teleport.teleport(_pc, 32692, 32903, (short) 200,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11010, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				// } else {
				// _pc.sendPackets(new S_SystemMessage("50000金币不足."));
				// }
			} else if (cmd.equalsIgnoreCase("lasitabade33")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 33271, 32394, (short) 4,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade34")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32763, 33160, (short) 4,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade35")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32726, 32832, (short) 603,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade36")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32782, 32827, (short) 782,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade37")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 34041, 33007, (short) 4,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade38")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32771, 32831, (short) 65,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade39")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32697, 32823, (short) 37,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade40")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32725, 32800, (short) 67,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade41")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 32848, 32862, (short) 535,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade42")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 33973, 32480, (short) 4,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}
			} else if (cmd.equalsIgnoreCase("lasitabade43")) {// 道具商城
				if (_pc.getInventory().consumeItem(40308, 50000)) {
					L1Teleport.teleport(_pc, 34069, 32484, (short) 4,
							_pc.getHeading(), true);
					_pc.getInventory().consumeItem(11010, 1);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else {
					_pc.sendPackets(new S_SystemMessage("50000金币不足."));
				}

				// / 城镇传送
			} else if (cmd.equalsIgnoreCase("chuansong_cz_1")) {// 道具商城
				L1Teleport.teleport(_pc, 32608, 32734, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11009, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("chuansong_cz_2")) {// 道具商城
				L1Teleport.teleport(_pc, 33438, 32798, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11009, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("chuansong_cz_3")) {// 道具商城
				L1Teleport.teleport(_pc, 32583, 32942, (short) 0,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11009, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("chuansong_cz_4")) {// 道具商城
				L1Teleport.teleport(_pc, 33050, 32782, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11009, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("chuansong_cz_5")) {// 道具商城
				L1Teleport.teleport(_pc, 33612, 33257, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11009, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("chuansong_cz_6")) {// 道具商城
				L1Teleport.teleport(_pc, 33709, 32500, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11009, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("chuansong_cz_7")) {// 道具商城
				L1Teleport.teleport(_pc, 33080, 33386, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11009, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("chuansong_cz_8")) {// 道具商城
				L1Teleport.teleport(_pc, 33965, 33253, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11009, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("chuansong_cz_9")) {// 道具商城
				L1Teleport.teleport(_pc, 32714, 32447, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11009, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("chuansong_cz_10")) {// 道具商城
				L1Teleport.teleport(_pc, 32640, 33203, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11009, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("chuansong_cz_11")) {// 道具商城
				L1Teleport.teleport(_pc, 33051, 32337, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11009, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("chuansong_cz_12")) {// 道具商城
				L1Teleport.teleport(_pc, 33675, 32413, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11009, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("chuansong_cz_13")) {// 道具商城
				L1Teleport.teleport(_pc, 33628, 32369, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11009, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("chuansong_cz_14")) {// 道具商城
				L1Teleport.teleport(_pc, 33557, 32682, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11009, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			} else if (cmd.equalsIgnoreCase("chuansong_cz_15")) {// 道具商城
				L1Teleport.teleport(_pc, 33628, 32765, (short) 4,
						_pc.getHeading(), true);
				_pc.getInventory().consumeItem(11009, 1);
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			}

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void showFindShopSell(final String cmd) {
		try {
			final int type = Integer.parseInt(cmd.substring(13));
			switch (type) {
			case 17:
				_pc.setPage(0);
				break;
			case 15:
				if (_pc.getPage() <= 0) {
					_pc.sendPackets(new S_SystemMessage("\\F2已经是第一页了"));
					return;
				}
				_pc.addPage(-1);
				break;
			case 16:
				int pagecount = _pc.getFindSellListSize() / 15;
				if (_pc.getFindSellListSize() > 0
						&& _pc.getFindSellListSize() % 15 != 0) {
					pagecount += 1;
				}
				if (_pc.getPage() + 1 >= pagecount) {
					_pc.sendPackets(new S_SystemMessage("\\F2已经是最后一页了"));
					return;
				}
				_pc.addPage(1);
				break;
			default:
				if (type >= 0 && type <= 14) {
					int findShopIndex = _pc.getPage() * 15 + type;
					if (_pc.isPrivateShop()) {
						return;
					}
					if (findShopIndex >= 0
							&& findShopIndex < _pc.getFindSellListSize()) {
						final L1FindShopSell findShopSell = _pc
								.getFindSellList().get(findShopIndex);
						L1Teleport.teleport(_pc, findShopSell.getX(),
								findShopSell.getY(), findShopSell.getMapId(),
								_pc.getHeading(), false);
					}
				}
				break;
			}
			final String[] data = new String[30];
			int n = 0;
			for (int i = _pc.getPage() * 15; i < _pc.getFindSellListSize(); i++) {
				if (n >= 30) {
					break;
				}
				data[n] = _pc.getFindSellList().get(i).getSellItemName();
				data[n + 1] = String.format("%s-->单价:%d", _pc.getFindSellList()
						.get(i).getName(), _pc.getFindSellList().get(i)
						.getSellPrice());
				n += 2;
			}
			for (; n < 30; n++) {
				data[n] = " ";
			}
			_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "findshop", data));
		} catch (Exception e) {

		}
	}

	private void bindTradeChar(final String cmd) {
		try {
			final int index = Integer.parseInt(cmd.substring(13));
			if (_pc.getTempID() == 0) {
				_pc.clearTempObjects();
				return;
			}
			if (index < 0 || index >= _pc.getTempObjects().size()) {
				_pc.setTempID(0);
				_pc.clearTempObjects();
				return;
			}
			final L1ItemInstance tradeItem = _pc.getInventory().getItem(
					_pc.getTempID());
			if (tradeItem == null) {
				_pc.setTempID(0);
				_pc.clearTempObjects();
				return;
			}
			if (tradeItem.getItemCharaterTrade() != null) {
				_pc.setTempID(0);
				_pc.clearTempObjects();
				return;
			}
			final L1CharaterTrade tradechar = (L1CharaterTrade) _pc
					.getTempObjects().get(index);
			if (tradechar != null) {
				if (CharaterTradeReading.get().updateBindChar(
						tradechar.get_char_objId(), 1)) {
					tradeItem.setItemCharaterTrade(tradechar);
					try {
						CharactersItemStorage.create().updateItemCharTrade(
								tradeItem);
						_pc.sendPackets(new S_ItemName(tradeItem));
					} catch (Exception e) {
						tradeItem.setItemCharaterTrade(null);
					}
					final ArrayList<String> list = _pc.getNetConnection()
							.getAccount()
							.loadCharacterItems(tradechar.get_char_objId());
					_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(),
							"tradechar1", tradechar.getName() + "[绑定]",
							tradechar.getLevel(), list));
				}
			}
			_pc.clearTempObjects();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void rebindTradeChar() {
		if (_pc.getTempID() <= 0) {
			return;
		}
		final L1ItemInstance tradeItem = _pc.getInventory().getItem(
				_pc.getTempID());
		if (tradeItem == null) {
			_pc.setTempID(0);
			return;
		}
		final L1CharaterTrade charaterTrade = tradeItem.getItemCharaterTrade();
		if (charaterTrade == null) {
			_pc.setTempID(0);
			return;
		}
		if (_pc.getNetConnection().getAccount().countCharacters() >= 8) {
			_pc.setTempID(0);
			_pc.sendPackets(new S_SystemMessage("\\F1你当前账号内人物太多 ."));
			_pc.sendPackets(new S_CloseList(_pc.getId()));
			return;
		}
		if (_pc.getNetConnection().getAccount()
				.updaterecharBind(charaterTrade.get_char_objId())) {
			_pc.getInventory().removeItem(tradeItem);
			_pc.sendPackets(new S_SystemMessage("\\F1该人物已成功绑定至你账号内 请小退查看"));
			_pc.sendPackets(new S_CloseList(_pc.getId()));
		}
	}

	private boolean isCheckTempObjects(final int tradeindex) {
		if (tradeindex < 0) {
			return false;
		}
		if (_pc.getTempObjects().isEmpty() || _pc.getTempObjects().size() <= 0) {
			return false;
		}
		if (tradeindex >= _pc.getTempObjects().size()) {
			return false;
		}
		return true;
	}

	private void showCharacterTradeHtml(final int index) {
		try {
			final String[] charData = new String[30];
			int n = 0;
			_pc.setPage(index);
			if (_pc.getTempObjects().size() > 0) {
				if (_pc.getTempObjects().get(0) instanceof L1CharaterTrade) {
					for (int i = index * 5; i < _pc.getTempObjects().size(); i++) {
						if (n >= 30) {
							break;
						}
						// final int charTradeId =
						// ((L1CharaterTrade)_pc.getTempObjects().get(i)).intValue();
						final L1CharaterTrade charaterTrade = (L1CharaterTrade) _pc
								.getTempObjects().get(i);
						charData[n] = charaterTrade.getName();
						charData[n + 1] = String.valueOf(charaterTrade
								.getLevel());
						charData[n + 2] = TYPE_CLASS[charaterTrade.get_Type()];
						charData[n + 3] = (charaterTrade.get_Sex() == 0) ? "男"
								: "女";
						charData[n + 4] = String.valueOf(charaterTrade
								.get_money_count());
						charData[n + 5] = "查看信息";
						n += 6;
					}
				}
			}
			for (; n < 30; n++) {
				charData[n] = " ";
			}
			_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "character4",
					charData));// 显示主页
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

	}

	private void CharaterTrade(String cmd, long amount) {
		try {
			final int tradetype = Integer.parseInt(cmd.substring(15));
			switch (tradetype) {
			case 0:// 查看所有的角色交易列表
				_pc.clearTempObjects();
				for (final L1CharaterTrade charaterTrade : CharaterTradeReading
						.get().getAllCharaterTradeValues()) {
					if (charaterTrade.get_state() == 0) {
						_pc.addTempObject(charaterTrade);
					}
				}
				if (_pc.getTempObjects().size() > 0) {
					_pc.getTempObjects().sort(
							new DataComparatorCharacterTrade<Object>());
					;
				}
				showCharacterTradeHtml(0);
				break;
			case 1:// 挂卖角色
				_pc.clearTempObjects();
				CharaterTradeReading.get().loadCharacterName(_pc);
				if (_pc.getTempObjects().isEmpty()
						|| _pc.getTempObjects().size() <= 0) {
					_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(),
							"character0"));// 没有额外的角色
					return;
				}
				final String[] data = new String[7];
				int i = 0;
				for (final Object obj : _pc.getTempObjects()) {
					final L1CharaterTrade tmp = (L1CharaterTrade) obj;
					if (i >= 7) {
						break;
					}
					data[i] = tmp.getName();
					i++;
				}
				for (; i < 7; i++) {
					data[i] = " ";
				}
				_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "character1",
						data));// 显示要挂卖的角色列表
				break;
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:// 选择要挂卖的角色
				final int tradeindex = tradetype - 2;
				if (!isCheckTempObjects(tradeindex)) {
					return;
				}
				if (_pc.getTempObjects().get(tradeindex) instanceof L1CharaterTrade) {
					final L1CharaterTrade selCharTrade = (L1CharaterTrade) _pc
							.getTempObjects().get(tradeindex);
					if (selCharTrade.getLevel() < 50) {
						_pc.sendPackets(new S_SystemMessage("\\F2挂卖的角色不能低于50级"));
						_pc.sendPackets(new S_CloseList(_pc.getId()));
						return;
					}
					for (final L1CharaterTrade charaterTrade : CharaterTradeReading
							.get().getAllCharaterTradeValues()) {
						if (charaterTrade.get_by_objId() == selCharTrade
								.get_char_objId()
								&& (charaterTrade.get_state() == 0 || charaterTrade
										.get_state() == 1)) {
							_pc.sendPackets(new S_SystemMessage(
									"\\F2挂卖的角色已挂卖了其他角色"));
							_pc.sendPackets(new S_CloseList(_pc.getId()));
							return;
						}
					}
					_pc.setTempID(tradeindex);
					_pc.sendPackets(new S_HowManyMake(_pc.getId(), 0,
							2000000000, "characterTrade_9", "character2",
							new String[] { selCharTrade.getName(),
									String.valueOf(selCharTrade.getLevel()) }));
				}
				break;
			case 9:// 确认价格
				final int selIndex = _pc.getTempID();
				if (!isCheckTempObjects(selIndex)) {
					return;
				}
				if (amount <= 0 || amount >= 2000000000) {
					return;
				}
				if (_pc.getTempObjects().get(selIndex) instanceof L1CharaterTrade) {
					final L1CharaterTrade selTmp = (L1CharaterTrade) _pc
							.getTempObjects().get(selIndex);
					if (selTmp.getLevel() < 50) {
						_pc.sendPackets(new S_SystemMessage("\\F2挂卖的角色不能低于50级"));
						_pc.sendPackets(new S_CloseList(_pc.getId()));
						return;
					}
					for (final L1CharaterTrade charaterTrade : CharaterTradeReading
							.get().getAllCharaterTradeValues()) {
						if (charaterTrade.get_by_objId() == selTmp
								.get_char_objId()
								&& (charaterTrade.get_state() == 0 || charaterTrade
										.get_state() == 1)) {
							_pc.sendPackets(new S_SystemMessage(
									"\\F2挂卖的角色已挂卖了其他角色"));
							_pc.sendPackets(new S_CloseList(_pc.getId()));
							return;
						}
					}
					if (CharaterTradeReading.get().updateBindChar(
							selTmp.get_char_objId(), 1)) {
						final L1CharaterTrade tmp = new L1CharaterTrade();
						tmp.setName(selTmp.getName());
						tmp.setLevel(selTmp.getLevel());
						tmp.set_Type(selTmp.get_Type());
						tmp.set_Sex(selTmp.get_Sex());
						tmp.set_char_objId(selTmp.get_char_objId());
						tmp.set_by_objId(_pc.getId());
						tmp.set_id(CharaterTradeReading.get().get_nextId());
						tmp.set_money_count((int) amount);
						tmp.set_state(0);
						if (CharaterTradeReading.get().addCharaterTrade(tmp)) {
							_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(),
									"character3"));// 挂卖成功
						} else {
							_pc.sendPackets(new S_SystemMessage("\\F2挂卖失败"));
							_pc.sendPackets(new S_CloseList(_pc.getId()));
						}
					}
				}
				break;
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:// 确认选择要购买的角色
				final int Seltradeindex = (_pc.getPage() * 5)
						+ (tradetype - 10);
				if (!isCheckTempObjects(Seltradeindex)) {
					return;
				}
				if (_pc.getTempObjects().get(Seltradeindex) instanceof L1CharaterTrade) {
					// final int charTradeId =
					// ((L1CharaterTrade)_pc.getTempObjects().get(Seltradeindex)).get_id();
					final L1CharaterTrade selBuyCharTrade = (L1CharaterTrade) _pc
							.getTempObjects().get(Seltradeindex);
					final String[] charData = new String[5];
					charData[0] = selBuyCharTrade.getName();
					charData[1] = String.valueOf(selBuyCharTrade.getLevel());
					charData[2] = TYPE_CLASS[selBuyCharTrade.get_Type()];
					charData[3] = selBuyCharTrade.get_Sex() == 0 ? "男" : "女";
					charData[4] = String.valueOf(selBuyCharTrade
							.get_money_count());
					_pc.setTempID(selBuyCharTrade.get_id());
					_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(),
							"character5", charData));// 显示选择购买的角色
				}
				break;
			case 15:
				// 上一页
				if (_pc.getPage() <= 0) {
					_pc.sendPackets(new S_SystemMessage("\\F2已经是第一页了"));
					return;
				}
				showCharacterTradeHtml(_pc.getPage() - 1);
				break;
			case 16:
				// 下一页
				int pages = _pc.getTempObjects().size() / 5;
				if (_pc.getTempObjects().size() % 5 != 0) {
					pages++;
				}
				if (_pc.getPage() + 1 >= pages) {
					_pc.sendPackets(new S_SystemMessage("\\F2已经是最后一页了"));
					return;
				}
				showCharacterTradeHtml(_pc.getPage() + 1);
				break;
			case 17:// 显示属性
			case 18:// 显示背包道具
			case 19:// 确认购买
				final L1CharaterTrade charaterTradeItems = CharaterTradeReading
						.get().getCharaterTrade(_pc.getTempID());
				if (charaterTradeItems == null) {
					return;
				}
				final L1PcInstance target_pc = CharaterTradeReading.get()
						.getPcInstance(charaterTradeItems.get_char_objId());
				if (target_pc == null) {
					return;
				}
				if (tradetype == 17) {
					final String[] msg = new String[95];
					msg[0] = target_pc.getName();
					msg[1] = String.valueOf(target_pc.getLevel());
					msg[2] = String.valueOf(target_pc.getMaxHp());
					msg[3] = String.valueOf(target_pc.getMaxMp());
					msg[4] = String.valueOf(target_pc.getStr());
					msg[5] = String.valueOf(target_pc.getCon());
					msg[6] = String.valueOf(target_pc.getDex());
					msg[7] = String.valueOf(target_pc.getWis());
					msg[8] = String.valueOf(target_pc.getInt());
					msg[9] = String.valueOf(target_pc.getCha());
					msg[10] = String.valueOf(target_pc.getAc());
					msg[11] = String.valueOf(target_pc.getElixirStats());
					int n = 12;
					final ArrayList<L1UserSkillTmp> skillList = CharSkillReading
							.get().skills(target_pc.getId());
					if (skillList != null && !skillList.isEmpty()) {
						for (final L1UserSkillTmp skillTmp : skillList) {
							if (n >= 95) {
								break;
							}
							msg[n] = skillTmp.get_skill_name();
							n++;
						}
					}
					for (; n < 95; n++) {
						msg[n] = "";
					}
					_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(),
							"character6", msg));// 人物属性页面
				} else if (tradetype == 18) {
					_pc.sendPackets(new S_RetrieveList(target_pc, 0));
				} else if (tradetype == 19) {
					if (_pc.getNetConnection().getAccount().countCharacters() >= 8) {
						_pc.sendPackets(new S_SystemMessage("\\F2你账号无法新建跟多角色"));
						_pc.sendPackets(new S_CloseList(_pc.getId()));
						return;
					}
					synchronized (charaterTradeItems) {
						if (charaterTradeItems.get_state() == 0) {
							final L1ItemInstance ybItem = _pc.getInventory()
									.findItemId(44070);
							if (ybItem == null
									|| ybItem.getCount() < charaterTradeItems
											.get_money_count()) {
								_pc.sendPackets(new S_SystemMessage("\\F2元宝不足"));
								_pc.sendPackets(new S_CloseList(_pc.getId()));
								return;
							}
							_pc.getInventory().removeItem(ybItem,
									charaterTradeItems.get_money_count());
							CharaterTradeReading.get().updateCharaterTrade(
									charaterTradeItems, 1);
							CharaterTradeReading.get().updateCharAccountName(
									charaterTradeItems.get_char_objId(),
									_pc.getAccountName());
							_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(),
									"character7"));// 购买成功页面
						} else {
							_pc.sendPackets(new S_SystemMessage("\\F2该角色已被出售"));
							_pc.sendPackets(new S_CloseList(_pc.getId()));
						}
					}
				}
				break;
			case 20:// 查看自己的挂卖信息
				characterTrade0();
				break;
			case 21:// 操作自己挂卖的角色
			case 22:
			case 23:
			case 24:
			case 25:
			case 26:
			case 27:
				characterTrade1(tradetype - 21);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			// _log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 显示自己挂卖的角色列表
	 */
	private void characterTrade0() {
		_pc.clearTempObjects();
		;
		for (final L1CharaterTrade charaterTrade : CharaterTradeReading.get()
				.getAllCharaterTradeValues()) {
			if (charaterTrade.get_by_objId() == _pc.getId()
					&& (charaterTrade.get_state() == 0 || charaterTrade
							.get_state() == 1)) {
				_pc.addTempObject(charaterTrade.get_id());
			}
		}
		if (_pc.getTempObjects().isEmpty() && _pc.getTempObjects().size() <= 0) {
			_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "character9"));// 没有挂卖任何角色
			return;
		}
		if (_pc.getTempObjects().get(0) instanceof Integer) {
			final String[] data1 = new String[28];
			int m = 0;
			for (int n = 0; n < _pc.getTempObjects().size(); n++) {
				if (m >= 28) {
					break;
				}
				final int charTradeId = ((Integer) _pc.getTempObjects().get(n))
						.intValue();
				final L1CharaterTrade charaterTrade = CharaterTradeReading
						.get().getCharaterTrade(charTradeId);
				data1[m] = charaterTrade.getName();
				data1[m + 1] = String.valueOf(charaterTrade.get_money_count());
				if (charaterTrade.get_state() == 0) {
					data1[m + 2] = "未出售";
					data1[m + 3] = "撤销";
				} else if (charaterTrade.get_state() == 1) {
					data1[m + 2] = "已出售未领取";
					data1[m + 3] = "领取元宝";
				} else {
					data1[m + 2] = " ";
					data1[m + 3] = " ";
				}
				m += 4;
			}
			for (; m < 28; m++) {
				data1[m] = " ";
			}
			_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "character8",
					data1));
		}
	}

	/**
	 * 操作自己挂卖的角色
	 */
	private void characterTrade1(final int index) {
		if (!isCheckTempObjects(index)) {
			return;
		}
		if (_pc.getTempObjects().get(index) instanceof Integer) {
			final int charTradeId = ((Integer) _pc.getTempObjects().get(index))
					.intValue();
			final L1CharaterTrade charaterTrade = CharaterTradeReading.get()
					.getCharaterTrade(charTradeId);
			synchronized (charaterTrade) {
				if (charaterTrade.get_state() == 0) {
					// 撤销
					CharaterTradeReading.get().updateCharaterTrade(
							charaterTrade, 3);
					CharaterTradeReading.get().updateBindChar(
							charaterTrade.get_char_objId(), 0);
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				} else if (charaterTrade.get_state() == 1) {
					// 领取元宝
					CharaterTradeReading.get().updateCharaterTrade(
							charaterTrade, 2);
					int piceCount = charaterTrade.get_money_count();
					if (piceCount >= 10) {
						int preaxCount = (int) (piceCount * 0.1);
						if (preaxCount > 100) {
							preaxCount = 100;
						}
						piceCount = piceCount - preaxCount;
					}
					_pc.getInventory().storeItem(44070, piceCount);
					_pc.sendPackets(new S_SystemMessage(String.format(
							"\\F2获得元宝(%d)", piceCount)));
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				}
			}
		}
	}

	private void showhelpHtml() {
		String[] data = new String[] { "关", "关", "关", "0", "关", "关", "关", "关" };
		if (_pc.isShowEmblem()) {
			data[0] = "开";
		}
		if (_pc.IsShowHealMessage()) {
			data[1] = "开";
		}
		if (_pc.isVdmg()) {
			data[2] = "开";
		}
		final int count = L1World.getInstance().getAllPlayers().size();
		final String amount = String
				.valueOf((int) ( 50 + (count) * 3.7));
		data[3] = amount;
		if (_pc.isKOGifd()) {
			data[4] = "开";
		}
		if (_pc.isShowBlue()) {
			data[5] = "开";
		}
		if (_pc.isShowEnchantMessage()) {
			data[6] = "开";
		}
		if (_pc.isMassTeleport()) {
			data[7] = "开";
		}
		_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "gamehelp", data));
	}

	private void ShouShaChaXun() {
		final Map<Integer, L1ShouShaTemp> shoushaMaps = ShouShaReading.get()
				.getShouShaMaps();
		if (shoushaMaps != null) {
			final String[] data = new String[153];
			data[0] = String.valueOf(ShouShaReading.get().getCount());
			data[1] = String.valueOf(ShouShaReading.get().getAmcount0());
			data[2] = String.valueOf(ShouShaReading.get().getAmcount1());
			int i = 3;
			for (final L1ShouShaTemp tmp : shoushaMaps.values()) {
				if (i >= 153) {
					break;
				}
				data[i] = String.format("怪物名称:%s", tmp.getNpcName());
				data[i + 1] = String.format("首杀奖励:%s(%d)",
						tmp.getGiveItemName(), tmp.getCount());
				if (tmp.getObjId() > 0) {
					data[i + 2] = String.format("获得玩家:%s", tmp.getName());
				} else {
					data[i + 2] = "获得玩家:暂无";
				}
				i += 3;
			}
			_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "shousha", data));
		}
	}

	private void ShouBaoChaXun() {
		final Map<Integer, L1ShouBaoTemp> shoubaoMaps = ShouBaoReading.get()
				.getShouBaoMaps();
		if (shoubaoMaps != null) {
			final String[] data = new String[153];
			data[0] = String.valueOf(ShouBaoReading.get().getCount());
			data[1] = String.valueOf(ShouBaoReading.get().getAmcount0());
			data[2] = String.valueOf(ShouBaoReading.get().getAmcount1());
			int i = 3;
			for (final L1ShouBaoTemp tmp : shoubaoMaps.values()) {
				if (i >= 153) {
					break;
				}
				data[i] = String.format("物品名称:%s", tmp.getItemName());
				data[i + 1] = String.format("首爆奖励:%s(%d)",
						tmp.getGiveItemName(), tmp.getGiveItemCount());
				if (tmp.getObjId() > 0) {
					data[i + 2] = String.format("获得玩家:%s", tmp.getName());
				} else {
					data[i + 2] = "获得玩家:暂无";
				}
				i += 3;
			}
			_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "shoubao", data));
		}
	}

	/**
	 * 
	 * @param type
	 */
	private void adenaTrade(final String cmd, final long amount) {
		final int type = Integer.parseInt(cmd.substring(12));
		if (type == 1) {// 输入金币数量
			final L1ItemInstance adenaItem = _pc.getInventory().findItemId(
					40308);
			if (adenaItem == null || adenaItem.getCount() < 100000) {
				_pc.sendPackets(new S_SystemMessage("\\aD金币不足10W"));
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			_pc.sendPackets(new S_HowManyMake(_pc.getId(), 100000, adenaItem
					.getCount(), "adena_trade_2", "adenatrade1"));
		} else if (type == 2) {// 确认金币数量 输入元宝数量
			if (amount <= 0) {
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			if (amount > 2000000000) {
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			final L1ItemInstance adenaItem = _pc.getInventory().findItemId(
					40308);
			if (adenaItem == null || adenaItem.getCount() < amount) {
				_pc.sendPackets(new S_SystemMessage("\\aD金币不足。"));
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			_pc.setAdenaTradeCount(amount);
			_pc.sendPackets(new S_HowManyMake(_pc.getId(), 1, 2000000000,
					"adena_trade_3", "adenatrade2"));
		} else if (type == 3) {// 确认元宝数量
			final int adena_count = (int) _pc.getAdenaTradeCount();
			if (amount <= 0) {
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			if (amount > 2000000000) {
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			if (adena_count < 100000) {
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			final L1ItemInstance adenaItem = _pc.getInventory().findItemId(
					40308);
			if (adenaItem == null || adenaItem.getCount() < adena_count) {
				_pc.sendPackets(new S_SystemMessage("\\aD金币不足。"));
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			_pc.setAdenaTradeAmount(amount);
			_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "adenatrade3",
					new String[] { String.valueOf(adena_count),
							String.valueOf(amount) }));
		} else if (type == 4) {// 卖金币 最终确认
			final int adena_count = (int) _pc.getAdenaTradeCount();
			final int adena_amount = (int) _pc.getAdenaTradeAmount();
			if (adena_count < 100000 || adena_amount <= 0) {
				return;
			}
			_pc.setAdenaTradeCount(0);
			_pc.setAdenaTradeAmount(0);
			final L1ItemInstance adenaItem = _pc.getInventory().findItemId(
					40308);
			if (adenaItem == null || adenaItem.getCount() < adena_count) {
				_pc.sendPackets(new S_SystemMessage("\\aD金币不足。"));
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			final L1CharacterAdenaTrade adenaTrade = new L1CharacterAdenaTrade();
			adenaTrade.set_Id(CharacterAdenaTradeReading.get().nextId());
			adenaTrade.set_adena_count(adena_count);
			adenaTrade.set_over(0);
			adenaTrade.set_count(adena_amount);
			adenaTrade.set_objid(_pc.getId());
			adenaTrade.set_name(_pc.getName());
			if (_pc.getInventory().removeItem(adenaItem, adena_count) >= adena_count) {
				if (CharacterAdenaTradeReading.get().createAdenaTrade(
						adenaTrade)) {
					_pc.sendPackets(new S_SystemMessage("\\aD挂卖成功。"));
				} else {
					_pc.sendPackets(new S_SystemMessage("\\aD挂卖失败。"));
				}
			}
			_pc.sendPackets(new S_CloseList(_pc.getId()));
		} else if (type == 5) {// 确认购买
			final int adenaTrade_id = _pc.getAdenaTradeId();
			final Map<Integer, L1CharacterAdenaTrade> adenaTeadeMap = CharacterAdenaTradeReading
					.get().getAdenaTrades();
			synchronized (adenaTeadeMap) {
				final L1CharacterAdenaTrade adenaTrade = adenaTeadeMap
						.get(adenaTrade_id);
				if (adenaTrade == null) {
					_pc.sendPackets(new S_CloseList(_pc.getId()));
					return;
				}
				if (adenaTrade.get_over() != 0) {
					if (adenaTrade.get_over() == 1) {
						_pc.sendPackets(new S_SystemMessage("\\aD该单已出售。"));
					} else if (adenaTrade.get_over() == 2) {
						_pc.sendPackets(new S_SystemMessage("\\aD该单已撤销。"));
					} else if (adenaTrade.get_over() == 3) {
						_pc.sendPackets(new S_SystemMessage("\\aD该单已出售。"));
					}
					_pc.sendPackets(new S_CloseList(_pc.getId()));
					return;
				}
				if (!_pc.getInventory()
						.checkItem(44070, adenaTrade.get_count())) {
					_pc.sendPackets(new S_SystemMessage("\\F2元宝不足。"));
					_pc.sendPackets(new S_CloseList(_pc.getId()));
					return;
				}
				if (_pc.getInventory().consumeItem(44070,
						adenaTrade.get_count())) {
					if (CharacterAdenaTradeReading.get().updateAdenaTrade(
							adenaTrade_id, 1)) {
						_pc.getInventory().storeItem(40308,
								adenaTrade.get_adena_count());
						_pc.sendPackets(new S_SystemMessage(String.format(
								"\\F2获得金币(%d)", adenaTrade.get_adena_count())));
						_pc.sendPackets(new S_CloseList(_pc.getId()));
					}
				}
			}
		} else if (type == 6) {// 查看自己的挂卖清单
			_pc.getAdenaTradeList().clear();
			for (final L1CharacterAdenaTrade adenaTrade : CharacterAdenaTradeReading
					.get().getAllCharacterAdenaTrades()) {
				if (adenaTrade.get_objid() == _pc.getId()) {
					if (adenaTrade.get_over() == 0
							|| adenaTrade.get_over() == 1) {
						_pc.addAdenaTradeItem(adenaTrade);
					}
				}
			}
			if (_pc.getAdenaTradeList().size() <= 0) {
				_pc.sendPackets(new S_SystemMessage("\\F2你没有挂卖。"));
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			_pc.setPage(0);
			ShowAdenaTradePC(0);
		} else if (type == 7) {// 撤销
			final int adenaTrade_id = _pc.getAdenaTradeId();
			final Map<Integer, L1CharacterAdenaTrade> adenaTeadeMap = CharacterAdenaTradeReading
					.get().getAdenaTrades();
			synchronized (adenaTeadeMap) {
				final L1CharacterAdenaTrade adenaTrade = adenaTeadeMap
						.get(adenaTrade_id);
				if (adenaTrade == null) {
					_pc.sendPackets(new S_CloseList(_pc.getId()));
					return;
				}
				if (adenaTrade.get_objid() != _pc.getId()) {
					_pc.sendPackets(new S_CloseList(_pc.getId()));
					return;
				}
				if (adenaTrade.get_over() != 0) {
					if (adenaTrade.get_over() == 1) {
						_pc.sendPackets(new S_SystemMessage("\\aD该单已出售。"));
					} else if (adenaTrade.get_over() == 2) {
						_pc.sendPackets(new S_SystemMessage("\\aD该单已撤销。"));
					} else if (adenaTrade.get_over() == 3) {
						_pc.sendPackets(new S_SystemMessage("\\aD该单已提取。"));
					}
					_pc.sendPackets(new S_CloseList(_pc.getId()));
					return;
				}
				if (CharacterAdenaTradeReading.get().updateAdenaTrade(
						adenaTrade_id, 2)) {
					_pc.getInventory().storeItem(40308,
							adenaTrade.get_adena_count());
					_pc.sendPackets(new S_SystemMessage(String.format(
							"\\aD撤销成功获得金币(%d)", adenaTrade.get_adena_count())));
					_pc.sendPackets(new S_CloseList(_pc.getId()));
				}
			}
		} else if (type == 8) {// 提取
			final int adenaTrade_id = _pc.getAdenaTradeId();
			final L1CharacterAdenaTrade adenaTrade = CharacterAdenaTradeReading
					.get().getCharacterAdenaTrade(adenaTrade_id);
			if (adenaTrade == null) {
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			if (adenaTrade.get_objid() != _pc.getId()) {
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			if (adenaTrade.get_over() != 1) {
				if (adenaTrade.get_over() == 0) {
					_pc.sendPackets(new S_SystemMessage("\\aD该单还未出售。"));
				} else if (adenaTrade.get_over() == 2) {
					_pc.sendPackets(new S_SystemMessage("\\aD该单已撤销。"));
				} else if (adenaTrade.get_over() == 3) {
					_pc.sendPackets(new S_SystemMessage("\\aD该单已提取。"));
				}
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			if (CharacterAdenaTradeReading.get().updateAdenaTrade(
					adenaTrade_id, 3)) {
				_pc.getInventory().storeItem(44070, adenaTrade.get_count());
				_pc.sendPackets(new S_SystemMessage(String.format(
						"\\aD提取成功获得元宝(%d)", adenaTrade.get_count())));
				_pc.sendPackets(new S_CloseList(_pc.getId()));
			}
		} else if (type == 0) {
			_pc.getAdenaTradeList().clear();
			for (final L1CharacterAdenaTrade adenaTrade : CharacterAdenaTradeReading
					.get().getAllCharacterAdenaTrades()) {
				if (adenaTrade.get_over() == 0) {
					_pc.addAdenaTradeItem(adenaTrade);
				}
			}
			if (_pc.getAdenaTradeList().size() > 0) {
				_pc.getAdenaTradeList().sort(
						new DataComparatorAdenaTrade<L1CharacterAdenaTrade>());
			}
			_pc.setPage(0);
			ShowAdenaTrade(0);
		} else if (type >= 10 && type <= 19) {
			final int select_index = type - 10;
			if (select_index >= _pc.getAdenaTradeIndexList().size()) {
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			final int adenaTrade_id = _pc.getAdenaTradeIndexList().get(
					select_index);
			final L1CharacterAdenaTrade adenaTrade = CharacterAdenaTradeReading
					.get().getCharacterAdenaTrade(adenaTrade_id);
			if (adenaTrade == null) {
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			_pc.setAdenaTradeId(adenaTrade_id);
			final String[] data = new String[2];
			String msg = "";
			if (adenaTrade.get_over() == 0) {
				msg = "待售";
			} else if (adenaTrade.get_over() == 1) {
				msg = "已出售";
			} else if (adenaTrade.get_over() == 2) {
				msg = "已撤销";
			} else if (adenaTrade.get_over() == 3) {
				msg = "已出售";
			}
			data[0] = String.format("[%d]金币数量:%d(%s)", adenaTrade.get_Id(),
					adenaTrade.get_adena_count(), msg);
			data[1] = String.format("价格(%d)", adenaTrade.get_count());
			_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "adenatrade4",
					data));
		} else if (type >= 20 && type <= 29) {
			final int select_index = type - 20;
			if (select_index >= _pc.getAdenaTradeIndexList().size()) {
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			final int adenaTrade_id = _pc.getAdenaTradeIndexList().get(
					select_index);
			final L1CharacterAdenaTrade adenaTrade = CharacterAdenaTradeReading
					.get().getCharacterAdenaTrade(adenaTrade_id);
			if (adenaTrade == null) {
				_pc.sendPackets(new S_CloseList(_pc.getId()));
				return;
			}
			_pc.setAdenaTradeId(adenaTrade_id);
			final String[] data = new String[2];
			String msg = "";
			if (adenaTrade.get_over() == 0) {
				msg = "待售";
			} else if (adenaTrade.get_over() == 1) {
				msg = "领取元宝";
			} else if (adenaTrade.get_over() == 2) {
				msg = "已撤销";
			} else if (adenaTrade.get_over() == 3) {
				msg = "交易成功";
			}
			data[0] = String.format("[%d]金币数量:%d(%s)", adenaTrade.get_Id(),
					adenaTrade.get_adena_count(), msg);
			data[1] = String.format("价格(%d)", adenaTrade.get_count());
			_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "adenatrade6",
					data));
		}
	}

	private void ShowAdenaTradePC(final int index) {
		final List<L1CharacterAdenaTrade> adenaTrades = _pc.getAdenaTradeList();
		final String[] data = new String[30];
		int i = 0;
		_pc.clearAdenaTradeIndexList();
		for (int n = index * 10; n < adenaTrades.size(); n++) {
			if (i >= 30) {
				break;
			}
			String msg = "";
			if (adenaTrades.get(n).get_over() == 0) {
				msg = "待售";
			} else if (adenaTrades.get(n).get_over() == 1) {
				msg = "领取元宝";
			} else if (adenaTrades.get(n).get_over() == 2) {
				msg = "已撤销";
			} else if (adenaTrades.get(n).get_over() == 3) {
				msg = "交易成功";
			}
			data[i] = String.format("[%d]金币数量:%d(%s)", adenaTrades.get(n)
					.get_Id(), adenaTrades.get(n).get_adena_count(), msg);
			data[i + 1] = String.format("价格(%d)", adenaTrades.get(n)
					.get_count());
			data[i + 2] = " ";
			i += 3;
			_pc.addAdenaTradeIndex(adenaTrades.get(n).get_Id());
		}
		for (; i < 30; i++) {
			data[i] = " ";
		}
		_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "adenatrade5", data));
	}

	private void ShowAdenaTrade(final int index) {
		final List<L1CharacterAdenaTrade> adenaTrades = _pc.getAdenaTradeList();
		final String[] data = new String[30];
		int i = 0;
		_pc.clearAdenaTradeIndexList();
		for (int n = index * 10; n < adenaTrades.size(); n++) {
			if (i >= 30) {
				break;
			}
			data[i] = String.format("[%d]金币数量:%d(待售)", adenaTrades.get(n)
					.get_Id(), adenaTrades.get(n).get_adena_count());
			final double r = adenaTrades.get(n).get_adena_count()
					/ adenaTrades.get(n).get_count();
			data[i + 1] = String.format("-->价格(%d) 1:%s", adenaTrades.get(n)
					.get_count(), String.format("%.2f", r));
			data[i + 2] = " ";
			i += 3;
			_pc.addAdenaTradeIndex(adenaTrades.get(n).get_Id());
		}
		for (; i < 30; i++) {
			data[i] = " ";
		}
		_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "adenatrade0", data));
	}

	private void actionGuaJi(final String cmd) {
		final int type = Integer.parseInt(cmd.substring(12));
		if (type == 1) {// 选择三连射
			if (!_pc.isElf()) {
				_pc.sendPackets(new S_SystemMessage("\\aD只有妖精才可以选择"));
				return;
			}

			if (!CharSkillReading.get().spellCheck(_pc.getId(), 132)) {
				_pc.sendPackets(new S_SystemMessage("\\aD你还没学习该技能."));
				return;
			}
			_pc.setSelGuaJiSkillId(132);
		} else if (type == 2) {
			if (!_pc.isWizard()) {
				_pc.sendPackets(new S_SystemMessage("\\aD只有法师才可以选择"));
				return;
			}
			if (!CharSkillReading.get().spellCheck(_pc.getId(), 38)) {
				_pc.sendPackets(new S_SystemMessage("\\aD你还没学习该技能."));
				return;
			}
			_pc.setSelGuaJiSkillId(38);
		} else if (type == 3) {
			if (!_pc.isWizard()) {
				_pc.sendPackets(new S_SystemMessage("\\aD只有法师才可以选择"));
				return;
			}
			if (!CharSkillReading.get().spellCheck(_pc.getId(), 46)) {
				_pc.sendPackets(new S_SystemMessage("\\aD你还没学习该技能."));
				return;
			}
			_pc.setSelGuaJiSkillId(46);
		} else if (type == 4) {
			_pc.addSelGuaJiRange(1);
		} else if (type == 5) {
			_pc.addSelGuaJiRange(5);
		} else if (type == 6) {
			_pc.addSelGuaJiRange(10);
		} else if (type == 7) {
			_pc.addSelGuaJiRange(-1);
		} else if (type == 8) {
			_pc.addSelGuaJiRange(-5);
		} else if (type == 9) {
			_pc.addSelGuaJiRange(-10);
		} else if (type == 11) {
			_pc.setSelGuaJiSkillId(0);
		} else if (type == 10) {
			if (_pc.getMapId() != 53 && _pc.getMapId() != 54 && _pc.getMapId() != 55
					&& _pc.getMapId() != 56) {
				_pc.sendPackets(new S_SystemMessage("\\aD非挂机地图无法开启！"));
				return;
			}
			if (!_pc.isAiRunning()) {
				_pc.startAI();
				//_pc.setskillAuto_gj(true); // 开启挂机状态设置
			}
			_pc.sendPackets(new S_CloseList(_pc.getId()));
			return;

			// } else if (type == 12) {
			// if (!_pc.isshunfei()) {
			// _pc.set_isshunfei(true);
			//
			// _pc.sendPackets(new S_SystemMessage("\\aD无怪顺飞开启.注:(背包必须有白顺)"));
			// } else {
			// _pc.set_isshunfei(false);
			// _pc.sendPackets(new S_SystemMessage("\\aG关闭无怪顺飞"));
			// }
		}

		final int skillId = _pc.getSelGuaJiSkillId();
		String skillName = "";
		String actionText = "";
		if (skillId > 0) {
			final L1Skills skill = SkillsTable.getInstance().getTemplate(
					skillId);
			if (skill != null) {
				skillName = skill.getName();
				actionText = "清除技能";
			} else {
				_pc.setSelGuaJiSkillId(0);
			}
		}
		final String RangeStr = _pc.getSelGuaJiRange() == 0 ? "全图" : String
				.valueOf(_pc.getSelGuaJiRange());
		final String[] htmldata = new String[] { skillName, RangeStr,
				actionText };
		_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "guaji", htmldata));
	}

	private void loadTopcHtml(final int index) {
		final String[] datas = new String[153];
		int n = 0;
		for (int i = index * 150; i < L1World.getInstance().getTopcItemCount(); i++) {
			if (n >= 150) {
				break;
			}
			datas[n] = L1World.getInstance().getTopcItem(i);
			n++;
		}
		datas[150] = String.valueOf(L1World.getInstance().getTopcItemCount());
		int topcPage = L1World.getInstance().getTopcItemCount() / 150;
		if (L1World.getInstance().getTopcItemCount() % 150 != 0) {
			topcPage += 1;
		}
		for (; n < 150; n++) {
			datas[n] = " ";
		}
		datas[151] = String.valueOf(index + 1);
		datas[152] = String.valueOf(topcPage);
		_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "topcshow", datas));
	}

	private void loadBossChaXun() {
		final Map<Integer, L1Spawn> spawnmap = SpawnBossTable
				.get_bossSpawnTable();
		_pc.clearSpawnBossList();
		for (final L1Spawn spawn : spawnmap.values()) {
			if (spawn.getAmount() > 0) {
				_pc.addSpawnBossItem(spawn);
			}
		}
		_pc.getSpawnBossList().sort(new DataComparator<L1Spawn>());// 排序
		ShowBossSpawnItem(0);
	}

	private void ShowBossSpawnItem(final int index) {
		final String[] datas = new String[31];
		int n = 0;
		for (int i = index * 10; i < _pc.getSpawnBossList().size(); i++) {
			if (n >= 30) {
				break;
			}
			final L1Spawn spawn = _pc.getSpawnBossList().get(i);
			final L1Npc npc = NpcTable.getInstance().getTemplate(
					spawn.getNpcId());
			if (npc != null) {
				datas[n] = npc.get_name();
				if (spawn.getShuaXin() <= 0) {
					datas[n + 1] = "已刷新";
					datas[n + 2] = "";
				} else {
					final Date spawnTime = spawn.get_nextSpawnTime().getTime();
					final String nextTime = sdf.format(spawnTime);
					datas[n + 1] = "";
					datas[n + 2] = nextTime;
				}
			}
			n += 3;
		}
		datas[30] = " ";
		_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "serverboss", datas));
	}

	private class DataComparator<T> implements Comparator<L1Spawn> {
		@Override
		public int compare(L1Spawn spawn1, L1Spawn spawn2) {

			return new Long(spawn1.get_nextSpawnTime().getTime().getTime())
					.compareTo(new Long(spawn2.get_nextSpawnTime().getTime()
							.getTime()));
		}
	}

	private class DataComparatorAdenaTrade<T> implements
			Comparator<L1CharacterAdenaTrade> {
		@Override
		public int compare(L1CharacterAdenaTrade s1, L1CharacterAdenaTrade s2) {

			final int n1 = s1.get_adena_count() / s1.get_count();
			final int n2 = s2.get_adena_count() / s2.get_count();
			return n2 - n1;
		}
	}

	private class DataComparatorCharacterTrade<T> implements Comparator<Object> {
		@Override
		public int compare(Object s1, Object s2) {
			final L1CharaterTrade temp1 = (L1CharaterTrade) s1;
			final L1CharaterTrade temp2 = (L1CharaterTrade) s2;
			final int num1 = temp2.getLevel() - temp1.getLevel();
			int num = num1 == 0 ? temp1.get_money_count()
					- temp2.get_money_count() : num1;
			return num;
		}
	}

	private void selWeaponQieHuan(final String cmd) {
		// weapon_index
		try {
			final int index = Integer.parseInt(cmd.substring(12));
			if (index < 3) {
				return;
			}
			if (index > 17) {
				return;
			}
			final int weaponObjId = _pc.getWeaponItemObjId(index);
			if (weaponObjId > 0) {
				final L1ItemInstance weaponItem = _pc.getInventory().getItem(
						weaponObjId);
				if (weaponItem != null) {
					if (index >= 3 && index <= 7) {
						_pc.setWeaponItemObjId(weaponObjId, 0);
					} else if (index >= 8 && index <= 12) {
						_pc.setWeaponItemObjId(weaponObjId, 1);
					} else if (index >= 13 && index <= 17) {
						_pc.setWeaponItemObjId(weaponObjId, 2);
					}
					loadWeaponQieHuan();
				}
			}
		} catch (Exception e) {

		}
	}

	private void loadWeaponQieHuan() {
		final String[] datas = new String[18];
		for (int i = 0; i < 18; i++) {
			final int weaponObjId = _pc.getWeaponItemObjId(i);
			if (weaponObjId > 0) {
				final L1ItemInstance weaponItem = _pc.getInventory().getItem(
						weaponObjId);
				if (weaponItem != null) {
					datas[i] = weaponItem.getLogName();
				} else {
					datas[i] = "空";
				}
			} else {
				datas[i] = "空";
			}
		}
		_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "serverweapon", datas));
	}

	private void setHealIndex(String cmd) {
		try {
			// action="heal_index03"
			final int index = Integer.parseInt(cmd.substring(10)) - 3;
			if (index >= 0 && index < _pc.getHealHpPotionList().size()) {
				final int hpitemId = _pc.getHealHpPotionList().get(index);
				final int[] healPotion = HeallingPotionTable.get()
						.getHealPotion(hpitemId);
				if (healPotion != null) {
					_pc.setSelHealHpPotion(hpitemId, healPotion[0],
							healPotion[1]);
				} else {
					_pc.sendPackets(new S_SystemMessage("没有找到该药水类型"));
				}
				loadHealPotion();
			}
		} catch (Exception e) {

		}
	}

	private void setHealPersent(final String cmd) {
		try {
			// action="heal_persent90"
			final int persentHp = Integer.parseInt(cmd.substring(12));
			_pc.setHealpersentHp(persentHp);
			loadHealPotion();
		} catch (Exception e) {

		}
	}

	private void loadHealPotion() {
		final List<Integer> potionlist = _pc.getHealHpPotionList();
		final String[] datas = new String[41];
		if (_pc.getHealHPAI()) {
			datas[0] = "开";
		} else {
			datas[0] = "关";
		}
		datas[1] = _pc.getHealpersentHP() + "%";
		if (_pc.getSelHealHpPotion()[0] > 0) {
			final L1Item HpItem = ItemTable.getInstance().getTemplate(
					_pc.getSelHealHpPotion()[0]);
			if (HpItem != null) {
				datas[2] = HpItem.getName();
			}
		}
		int n = 3;
		if (potionlist.size() > 0) {
			for (int i = 0; i < potionlist.size(); i++) {
				final L1Item HpItem = ItemTable.getInstance().getTemplate(
						potionlist.get(i));
				if (HpItem != null) {
					datas[n] = HpItem.getName();
				}
				n++;
			}
		}
		for (int j = n; j < 41; j++) {
			datas[j] = "&nbsp;";
		}
		_pc.sendPackets(new S_NPCTalkReturn(_pc.getId(), "serverheal", datas));

	}

	private void setHealIng() {
		if (_pc.isHealAIProcess()) {
			_pc.setHealAI(false);
		} else {
			_pc.startHealHPAI();
		}
		loadHealPotion();
	}

	private Calendar getRealTime() {
		TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(_tz);
		return cal;
	}

	private int getNextTime(final int m) {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		Calendar realTime = getRealTime();
		realTime.add(Calendar.MINUTE, m);
		int nowTime = Integer.valueOf(sdf.format(realTime.getTime()));
		return nowTime;
	}
}
