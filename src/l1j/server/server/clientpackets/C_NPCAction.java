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
package l1j.server.server.clientpackets;

import static l1j.server.server.model.skill.L1SkillId.AWAKEN_ANTHARAS;
import static l1j.server.server.model.skill.L1SkillId.AWAKEN_FAFURION;
import static l1j.server.server.model.skill.L1SkillId.AWAKEN_VALAKAS;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_DEX;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_STR;
import static l1j.server.server.model.skill.L1SkillId.STATUS_DEX_POISON;
import static l1j.server.server.model.skill.L1SkillId.STATUS_STR_POISON;

import java.util.ArrayList;//加入 william功能
import java.util.Calendar;
import java.util.Random;
import java.util.StringTokenizer;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.WarTimeController;//攻城时间 
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PetTable;//宠物 
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.TownTable;
import l1j.server.server.datatables.UBTable;
import l1j.server.server.datatables.lock.CharSkillReading;
import l1j.server.server.gm.GMCommands;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Gamble;
import l1j.server.server.model.L1HauntedHouse;
import l1j.server.server.model.L1HouseLocation;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1UltimateBattle;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1FollowInstance;//跟随者 
import l1j.server.server.model.Instance.L1HousekeeperInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MerchantInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.model.npc.action.L1NpcAction;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.Kiusbt_BasePacket_SelectTarget;//宠物指定攻击 
import l1j.server.server.serverpackets.S_ApplyAuction;
import l1j.server.server.serverpackets.S_AuctionBoardRead;
import l1j.server.server.serverpackets.S_CloseList;
import l1j.server.server.serverpackets.S_DelSkill;
import l1j.server.server.serverpackets.S_Deposit;
import l1j.server.server.serverpackets.S_Drawal;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_HouseMap;
import l1j.server.server.serverpackets.S_ItemName;//宠物 
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_PetList;
import l1j.server.server.serverpackets.S_RetrieveList;
import l1j.server.server.serverpackets.S_RetrievePledgeList;
import l1j.server.server.serverpackets.S_SellHouse;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_ShopBuyList;
import l1j.server.server.serverpackets.S_ShopSellList;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_TaxRate;
import l1j.server.server.templates.L1Castle;
import l1j.server.server.templates.L1House;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Pet;//宠物 
import l1j.server.server.templates.L1Skills;
import l1j.server.server.templates.L1Town;
import l1j.server.server.utils.CalcStat;
import l1j.server.server.world.L1World;
import l1j.william.L1WilliamSystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//召唤祭司功能 
//修正单属性防御 
//城门开启关闭 

public class C_NPCAction extends ClientBasePacket {

	private static final String C_NPC_ACTION = "[C] C_NPCAction";
	private static final Log _log = LogFactory.getLog(C_NPCAction.class);
	private static Random _random = new Random();

	public C_NPCAction(byte abyte0[], LineageClient _client) throws Exception {
		super(abyte0);
		int objid = readD();
		String s = readS();

		String s2 = null;
		if (s.equalsIgnoreCase("select") // 竞卖揭示板选择
				|| s.equalsIgnoreCase("map") // 位置确
				|| s.equalsIgnoreCase("apply")) { // 竞卖参加
			s2 = readS();
		}

		int[] materials = null;
		long[] counts = null;
		int[] createitem = null;
		long[] createcount = null;

		String htmlid = null;
		String success_htmlid = null;
		String failure_htmlid = null;
		String[] htmldata = null;

		L1PcInstance pc = _client.getActiveChar();
		L1PcInstance target;
		L1Object obj = L1World.getInstance().findObject(objid);
		if (obj != null) {
			if (obj instanceof L1PetInstance) {
				final L1PetInstance npc = (L1PetInstance) obj;
				npc.onFinalAction(pc, s);
				return;
			}
			if (obj instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				int difflocx = Math.abs(pc.getX() - npc.getX());
				int difflocy = Math.abs(pc.getY() - npc.getY());
				// 3以上离场合无效
				if (difflocx > 3 || difflocy > 3) {
					return;
				}
				if (npc.ACTION != null) {
					if (s2 != null && s2.length() > 0) {
						npc.ACTION.action(pc, npc, s + "," + s2, 0);
						return;
					}
					npc.ACTION.action(pc, npc, s, 0);
					return;
				}
				npc.onFinalAction(pc, s);
			} else if (obj instanceof L1PcInstance) {
				target = (L1PcInstance) obj;
				if (s.matches("[0-9]+")) {
					summonMonster(target, s);
				} else {
					// 龙骑士觉醒技能实装
					final int awakeSkillId = target.getAwakeSkillId();
					if ((awakeSkillId == AWAKEN_ANTHARAS // 龙骑士魔法 (觉醒：安塔瑞斯)
							)
							|| (awakeSkillId == AWAKEN_FAFURION // 龙骑士魔法
																// (觉醒：法利昂)
							) || (awakeSkillId == AWAKEN_VALAKAS // 龙骑士魔法
																	// (觉醒：巴拉卡斯)
							)) {
						target.sendPackets(new S_ServerMessage(1384)); // 目前状态中无法变身。
						return;
					}
					// 龙骑士觉醒技能实装
					if (pc.isPring()) {
						L1PolyMorph.handleCommands(target, s);
						pc.setPring(false);
					} else {
						pc.getAction().action(s, 0);
					}
				}
				return;
			}
		} else {
			// _log.warning("object not found, oid " + i);
		}

		// XML化
		L1NpcAction action = NpcActionTable.getInstance().get(s, pc, obj);
		if (action != null) {
			L1NpcHtml result = action.execute(s, pc, obj, readByte());
			if (result != null) {
				pc.sendPackets(new S_NPCTalkReturn(obj.getId(), result));
			}
			return;
		}

		/*
		 * 个别处理
		 */
		// 加入 william功能
		if (l1j.william.NpcQuest.forNpcQuest(s, pc, ((L1NpcInstance) obj),
				((L1NpcInstance) obj).getNpcTemplate().get_npcId(), objid)) {
			return;
		}

		ArrayList aReturn = l1j.william.misc.forRequestNPCAction(s, pc);

		if (1 == ((Integer) aReturn.get(0)).intValue()) {
			if (aReturn.get(1) != null)
				htmlid = (String) aReturn.get(1);
			if (aReturn.get(2) != null)
				htmldata = (String[]) aReturn.get(2);
			if (aReturn.get(3) != null)
				materials = (int[]) aReturn.get(3);
			if (aReturn.get(4) != null)
				counts = (long[]) aReturn.get(4);
			if (aReturn.get(5) != null)
				createitem = (int[]) aReturn.get(5);
			if (aReturn.get(6) != null)
				createcount = (long[]) aReturn.get(6);
		} else // 加入 william功能 end
		if (s.equalsIgnoreCase("buy")) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
			// "sell"表示NPC。
			if (isNpcSellOnly(npc)) {
				return;
			}
			if (npcid == 70035 || npcid == 70041 || npcid == 70042) {
				L1Gamble.getInstance().buytickets(npc, pc);
				return;
			}
			// 贩卖表示
			pc.sendPackets(new S_ShopSellList(objid));
		} else if (s.equalsIgnoreCase("sell")) {
			int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
			if (npcid == 70523 || npcid == 70805) { //  or 
				htmlid = "ladar2";
			} else if (npcid == 70537 || npcid == 70807) { //  or 
				htmlid = "farlin2";
			} else if (npcid == 70525 || npcid == 70804) { //  or 
				htmlid = "lien2";
			} else if (npcid == 50527 || npcid == 50505 || npcid == 50519
					|| npcid == 50545 || npcid == 50531 || npcid == 50529
					|| npcid == 50516 || npcid == 50538 || npcid == 50518
					|| npcid == 50509 || npcid == 50536 || npcid == 50520
					|| npcid == 50543 || npcid == 50526 || npcid == 50512
					|| npcid == 50510 || npcid == 50504 || npcid == 50525
					|| npcid == 50534 || npcid == 50540 || npcid == 50515
					|| npcid == 50513 || npcid == 50528 || npcid == 50533
					|| npcid == 50542 || npcid == 50511 || npcid == 50501
					|| npcid == 50503 || npcid == 50508 || npcid == 50514
					|| npcid == 50532 || npcid == 50544 || npcid == 50524
					|| npcid == 50535 || npcid == 50521 || npcid == 50517
					|| npcid == 50537 || npcid == 50539 || npcid == 50507
					|| npcid == 50530 || npcid == 50502 || npcid == 50506
					|| npcid == 50522 || npcid == 50541 || npcid == 50523
					|| npcid == 50620 || npcid == 50623 || npcid == 50619
					|| npcid == 50621 || npcid == 50622 || npcid == 50624
					|| npcid == 50617 || npcid == 50614 || npcid == 50618
					|| npcid == 50616 || npcid == 50615) { // NPC
				String sellHouseMessage = sellHouse(pc, objid, npcid);
				if (sellHouseMessage != null) {
					htmlid = sellHouseMessage;
				}
			} else if (npcid == 70035 || npcid == 70041 || npcid == 70042) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1Gamble.getInstance().selltickets(npc, pc);
				// 四星彩
			} else { // 一般商人
				// 买取表示
				pc.sendPackets(new S_ShopBuyList(objid, pc));
			}
		}
		// 能力值重置师
		else if (s.equalsIgnoreCase("yes")) { // 切换到重置页面
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

				htmldata = new String[] { String.valueOf(pc.getResetStr()),
						String.valueOf(pc.getResetDex()),
						String.valueOf(pc.getResetCon()),
						String.valueOf(pc.getResetInt()),
						String.valueOf(pc.getResetWis()),
						String.valueOf(pc.getResetCha()),
						String.valueOf(pc.getOthetReset()) };
				htmlid = "ajreset1";
			} else {
				htmlid = "ajreset2";
			}
		} else if (s.equalsIgnoreCase("str_d")) { // 力量 - 1
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
		} else if (s.equalsIgnoreCase("str_u")) { // 力量 + 1

			if (((pc.getOriginalStr() + pc.getResetStr()) < Config.BONUS_STATS1)
					&& pc.getOthetReset() > 0) {
				pc.setResetStr(pc.getResetStr() + 1);
				pc.setOthetReset(pc.getOthetReset() - 1);
			}

			htmldata = new String[] { String.valueOf(pc.getResetStr()),
					String.valueOf(pc.getResetDex()),
					String.valueOf(pc.getResetCon()),
					String.valueOf(pc.getResetInt()),
					String.valueOf(pc.getResetWis()),
					String.valueOf(pc.getResetCha()),
					String.valueOf(pc.getOthetReset()) };
			htmlid = "ajreset1";
		} else if (s.equalsIgnoreCase("dex_d")) { // 敏捷 - 1
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
		} else if (s.equalsIgnoreCase("dex_u")) { // 敏捷 + 1

			if (((pc.getOriginalDex() + pc.getResetDex()) < Config.BONUS_STATS1)
					&& pc.getOthetReset() > 0) {
				pc.setResetDex(pc.getResetDex() + 1);
				pc.setOthetReset(pc.getOthetReset() - 1);
			}

			htmldata = new String[] { String.valueOf(pc.getResetStr()),
					String.valueOf(pc.getResetDex()),
					String.valueOf(pc.getResetCon()),
					String.valueOf(pc.getResetInt()),
					String.valueOf(pc.getResetWis()),
					String.valueOf(pc.getResetCha()),
					String.valueOf(pc.getOthetReset()) };
			htmlid = "ajreset1";
		} else if (s.equalsIgnoreCase("con_d")) { // 体质 - 1
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
		} else if (s.equalsIgnoreCase("con_u")) { // 体质 + 1

			if (((pc.getOriginalCon() + pc.getResetCon()) < Config.BONUS_STATS1)
					&& pc.getOthetReset() > 0) {
				pc.setResetCon(pc.getResetCon() + 1);
				pc.setOthetReset(pc.getOthetReset() - 1);
			}

			htmldata = new String[] { String.valueOf(pc.getResetStr()),
					String.valueOf(pc.getResetDex()),
					String.valueOf(pc.getResetCon()),
					String.valueOf(pc.getResetInt()),
					String.valueOf(pc.getResetWis()),
					String.valueOf(pc.getResetCha()),
					String.valueOf(pc.getOthetReset()) };
			htmlid = "ajreset1";
		} else if (s.equalsIgnoreCase("int_d")) { // 智力 - 1
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
		} else if (s.equalsIgnoreCase("int_u")) { // 智力 + 1

			if (((pc.getOriginalInt() + pc.getResetInt()) < Config.BONUS_STATS1)
					&& pc.getOthetReset() > 0) {
				pc.setResetInt(pc.getResetInt() + 1);
				pc.setOthetReset(pc.getOthetReset() - 1);
			}

			htmldata = new String[] { String.valueOf(pc.getResetStr()),
					String.valueOf(pc.getResetDex()),
					String.valueOf(pc.getResetCon()),
					String.valueOf(pc.getResetInt()),
					String.valueOf(pc.getResetWis()),
					String.valueOf(pc.getResetCha()),
					String.valueOf(pc.getOthetReset()) };
			htmlid = "ajreset1";
		} else if (s.equalsIgnoreCase("wis_d")) { // 精神 - 1
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
		} else if (s.equalsIgnoreCase("wis_u")) { // 精神 + 1

			if (((pc.getOriginalWis() + pc.getResetWis()) < Config.BONUS_STATS1)
					&& pc.getOthetReset() > 0) {
				pc.setResetWis(pc.getResetWis() + 1);
				pc.setOthetReset(pc.getOthetReset() - 1);
			}

			htmldata = new String[] { String.valueOf(pc.getResetStr()),
					String.valueOf(pc.getResetDex()),
					String.valueOf(pc.getResetCon()),
					String.valueOf(pc.getResetInt()),
					String.valueOf(pc.getResetWis()),
					String.valueOf(pc.getResetCha()),
					String.valueOf(pc.getOthetReset()) };
			htmlid = "ajreset1";
		} else if (s.equalsIgnoreCase("cha_d")) { // 魅力 - 1
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
		} else if (s.equalsIgnoreCase("cha_u")) { // 魅力 + 1

			if (((pc.getOriginalCha() + pc.getResetCha()) < Config.BONUS_STATS1)
					&& pc.getOthetReset() > 0) {
				pc.setResetCha(pc.getResetCha() + 1);
				pc.setOthetReset(pc.getOthetReset() - 1);
			}

			htmldata = new String[] { String.valueOf(pc.getResetStr()),
					String.valueOf(pc.getResetDex()),
					String.valueOf(pc.getResetCon()),
					String.valueOf(pc.getResetInt()),
					String.valueOf(pc.getResetWis()),
					String.valueOf(pc.getResetCha()),
					String.valueOf(pc.getOthetReset()) };
			htmlid = "ajreset1";
		} else if (s.equalsIgnoreCase("yes to reset")) { // 重置
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
		} else if (s.equalsIgnoreCase("no")) {
			htmlid = "";
		} else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70035
				|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70041
				|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70042
				&& s.equalsIgnoreCase("status")) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			L1Gamble.getInstance().gamstatus(npc, pc);
			// TODO 查询参赛史莱姆的记录
		}
		// 能力值重置师 end
		// 祭司设定补血量
		else if (s.equalsIgnoreCase("Hierarch_1")) { // -1

			if (pc.getHierarch() > 0) {
				pc.setHierarch(pc.getHierarch() - 1);
			}

			String msg0 = "";
			String msg1 = String.valueOf(pc.getHierarch() * 10);
			if (((L1NpcInstance) obj).getHierarch() == 1) {
				msg0 = "辅助";
			} else {
				msg0 = "休息";
			}
			htmldata = new String[] { ((L1NpcInstance) obj).getName(),
					String.valueOf(((L1NpcInstance) obj).getCurrentMp()),
					String.valueOf(((L1NpcInstance) obj).getMaxMp()), msg0,
					msg1 };
			htmlid = "Hierarch";
		} else if (s.equalsIgnoreCase("Hierarch_2")) { // +1

			if (pc.getHierarch() < 10) {
				pc.setHierarch(pc.getHierarch() + 1);
			}

			String msg0 = "";
			String msg1 = String.valueOf(pc.getHierarch() * 10);
			if (((L1NpcInstance) obj).getHierarch() == 1) {
				msg0 = "辅助";
			} else {
				msg0 = "休息";
			}
			htmldata = new String[] { ((L1NpcInstance) obj).getName(),
					String.valueOf(((L1NpcInstance) obj).getCurrentMp()),
					String.valueOf(((L1NpcInstance) obj).getMaxMp()), msg0,
					msg1 };
			htmlid = "Hierarch";
		} else if (s.equalsIgnoreCase("Hierarch_3")) { // 切换状态

			((L1NpcInstance) obj).setHierarch(1);

			String msg0 = "";
			String msg1 = String.valueOf(pc.getHierarch() * 10);
			if (((L1NpcInstance) obj).getHierarch() == 1) {
				msg0 = "辅助";
			} else {
				msg0 = "跟随";
			}
			htmldata = new String[] { ((L1NpcInstance) obj).getName(),
					String.valueOf(((L1NpcInstance) obj).getCurrentMp()),
					String.valueOf(((L1NpcInstance) obj).getMaxMp()), msg0,
					msg1 };
			htmlid = "Hierarch";
		} else if (s.equalsIgnoreCase("Hierarch_4")) { // 切换状态

			((L1NpcInstance) obj).setHierarch(0);

			String msg0 = "";
			String msg1 = String.valueOf(pc.getHierarch() * 10);
			if (((L1NpcInstance) obj).getHierarch() == 1) {
				msg0 = "辅助";
			} else {
				msg0 = "跟随";
			}
			htmldata = new String[] { ((L1NpcInstance) obj).getName(),
					String.valueOf(((L1NpcInstance) obj).getCurrentMp()),
					String.valueOf(((L1NpcInstance) obj).getMaxMp()), msg0,
					msg1 };
			htmlid = "Hierarch";
		}
		/*
		 * else if (s.equalsIgnoreCase("Hierarch_4")) { // 切换状态
		 * 
		 * ((L1NpcInstance) obj).setHierarch(0);
		 * 
		 * String msg0 = ""; String msg1 = String.valueOf(pc.getHierarch() *
		 * 10); if (((L1NpcInstance) obj).getHierarch() == 1) { msg0 = "辅助"; }
		 * else { msg0 = "跟随"; } htmldata = new String[] { ((L1NpcInstance)
		 * obj).getName(), String.valueOf(((L1NpcInstance) obj).getCurrentMp()),
		 * String.valueOf(((L1NpcInstance) obj).getMaxMp()), msg0, msg1 };
		 * htmlid = "Hierarch"; } else if (s.equalsIgnoreCase("Hierarch_4")) {
		 * // 切换状态
		 * 
		 * ((L1NpcInstance) obj).setHierarch(0);
		 * 
		 * String msg0 = ""; String msg1 = String.valueOf(pc.getHierarch() *
		 * 10); if (((L1NpcInstance) obj).getHierarch() == 1) { msg0 = "辅助"; }
		 * else { msg0 = "跟随"; } htmldata = new String[] { ((L1NpcInstance)
		 * obj).getName(), String.valueOf(((L1NpcInstance) obj).getCurrentMp()),
		 * String.valueOf(((L1NpcInstance) obj).getMaxMp()), msg0, msg1 };
		 * htmlid = "Hierarch"; } else if (s.equalsIgnoreCase("Hierarch_4")) {
		 * // 切换状态
		 * 
		 * ((L1NpcInstance) obj).setHierarch(0);
		 * 
		 * String msg0 = ""; String msg1 = String.valueOf(pc.getHierarch() *
		 * 10); if (((L1NpcInstance) obj).getHierarch() == 1) { msg0 = "辅助"; }
		 * else { msg0 = "跟随"; } htmldata = new String[] { ((L1NpcInstance)
		 * obj).getName(), String.valueOf(((L1NpcInstance) obj).getCurrentMp()),
		 * String.valueOf(((L1NpcInstance) obj).getMaxMp()), msg0, msg1 };
		 * htmlid = "Hierarch"; }
		 */
		// 祭司设定补血量 end
		else if (s.equalsIgnoreCase("retrieve")) { // “个人仓库：受取”
			if (pc.getLevel() >= 5) {
				pc.sendPackets(new S_RetrieveList(objid, pc));
			}
		} else if (s.equalsIgnoreCase("retrieve-pledge")) { // “血盟仓库：荷物受取”
		// if (pc.isCheckTwopassword()){
		// pc.sendPackets(new S_SystemMessage("\\F3**请在聊天框输入二级密码才可正常游戏**"));
		// return;
		// }
			if (pc.getLevel() >= 5) {
				if (pc.getClanid() != 0) { // 所属
					if (pc.getClan().getWarehouseUsingChar() == 0) {
						pc.sendPackets(new S_RetrievePledgeList(objid, pc));
					} else {
						final L1Object objpc = L1World.getInstance()
								.findObject(
										pc.getClan().getWarehouseUsingChar());
						if (objpc instanceof L1PcInstance) {
							if (((L1PcInstance) objpc).getOnlineStatus() == 0
									|| ((L1PcInstance) objpc).getClanid() != pc
											.getClanid()) {
								pc.getClan().setWarehouseUsingChar(0);// 该玩家已经离线
																		// 或者已不属于当前血盟
							}
							pc.sendPackets(new S_SystemMessage(
									((L1PcInstance) objpc).getName() + "正在使用中."));
						} else {
							pc.sendPackets(new S_SystemMessage("其他玩家正在使用中."));
							pc.getClan().setWarehouseUsingChar(0);// 该玩家已经离线
						}
					}
				} else {
					pc.sendPackets(new S_ServerMessage(208)); // \f1血盟仓库使用血盟加入。
				}
			}
		} else if (s.equalsIgnoreCase("get")) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			int npcId = npc.getNpcTemplate().get_npcId();
			//  or 
			if (npcId == 70099 || npcId == 70796) {
				L1ItemInstance item = pc.getInventory().storeItem(20081, 1); // 
				String npcName = npc.getNpcTemplate().get_name();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0%1。
				pc.getQuest().set_end(L1Quest.QUEST_OILSKINMANT);
				htmlid = ""; // 消
			}
			// ：报酬
			else if (npcId == 70528 || npcId == 70546 || npcId == 70567
					|| npcId == 70594 || npcId == 70654 || npcId == 70748
					|| npcId == 70774 || npcId == 70799 || npcId == 70815
					|| npcId == 70860) {

				if (pc.getHomeTownId() > 0) {

				} else {

				}
			}
		}
		// 宠物指定攻击
		else if (s.equalsIgnoreCase("attackchr")) {
			// TODO [2008年04月28日新增] 要求指定攻击目标
			L1NpcInstance L1NPC = (L1NpcInstance) obj;
			pc.sendPackets(new Kiusbt_BasePacket_SelectTarget(L1NPC.getId()));
		}
		// 宠物指定攻击 end
		else if (s.equalsIgnoreCase("fix")) { // 武器修理

		} else if (s.equalsIgnoreCase("room")) { // 部屋借

		} else if (s.equalsIgnoreCase("hall")
				&& obj instanceof L1MerchantInstance) { // 借

		} else if (s.equalsIgnoreCase("return")) { // 部屋返

		} else if (s.equalsIgnoreCase("enter")) { // 部屋入

		} else if (s.equalsIgnoreCase("openigate")) { //  / 城门开
			L1NpcInstance npc = (L1NpcInstance) obj;
			// 删除openCloseGate(pc, npc.getNpcTemplate().get_npcId(), true);
			// 删除htmlid = ""; // 消
			// 变更
			openCloseGateDoor(pc, npc.getNpcTemplate().get_npcId(), true);
			// 变更
		} else if (s.equalsIgnoreCase("closeigate")) { //  / 城门闭
			L1NpcInstance npc = (L1NpcInstance) obj;
			// 删除openCloseGate(pc, npc.getNpcTemplate().get_npcId(), false);
			// 删除htmlid = ""; // 消
			// 变更
			openCloseGateDoor(pc, npc.getNpcTemplate().get_npcId(), false);
			// 变更
		} else if (s.equalsIgnoreCase("askwartime")) { // 近卫兵 / 次攻城战时间
			L1NpcInstance npc = (L1NpcInstance) obj;
			if (npc.getNpcTemplate().get_npcId() == 60514) { // 城近卫兵
				htmldata = makeWarTimeStrings(L1CastleLocation.KENT_CASTLE_ID);
				htmlid = "ktguard7";
			} else if (npc.getNpcTemplate().get_npcId() == 60560) { // 近卫兵
				htmldata = makeWarTimeStrings(L1CastleLocation.OT_CASTLE_ID);
				htmlid = "orcguard7";
			} else if (npc.getNpcTemplate().get_npcId() == 60552) { // 城近卫兵
				htmldata = makeWarTimeStrings(L1CastleLocation.WW_CASTLE_ID);
				htmlid = "wdguard7";
			} else if (npc.getNpcTemplate().get_npcId() == 60524 || // 街入口近卫兵(弓)
					npc.getNpcTemplate().get_npcId() == 60525 || // 街入口近卫兵
					npc.getNpcTemplate().get_npcId() == 60529) { // 城近卫兵
				htmldata = makeWarTimeStrings(L1CastleLocation.GIRAN_CASTLE_ID);
				htmlid = "grguard7";
			} else if (npc.getNpcTemplate().get_npcId() == 70857) { // 城
				htmldata = makeWarTimeStrings(L1CastleLocation.HEINE_CASTLE_ID);
				htmlid = "heguard7";
			} else if (npc.getNpcTemplate().get_npcId() == 60530 || // 城
					npc.getNpcTemplate().get_npcId() == 60531) {
				htmldata = makeWarTimeStrings(L1CastleLocation.DOWA_CASTLE_ID);
				htmlid = "dcguard7";
			} else if (npc.getNpcTemplate().get_npcId() == 60533 || // 城 
					npc.getNpcTemplate().get_npcId() == 60534) {
				htmldata = makeWarTimeStrings(L1CastleLocation.ADEN_CASTLE_ID);
				htmlid = "adguard7";
			} else if (npc.getNpcTemplate().get_npcId() == 81156) { // 侦察兵（要塞）
				htmldata = makeWarTimeStrings(L1CastleLocation.DIAD_CASTLE_ID);
				htmlid = "dfguard3";
			}
		} else if (s.equalsIgnoreCase("inex")) { // 收入/支出报告受
			// 暂定的公金表示。
			// 适当。
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				int castle_id = clan.getCastleId();
				if (castle_id != 0) { // 城主
					L1Castle l1castle = CastleTable.getInstance()
							.getCastleTable(castle_id);
					pc.sendPackets(new S_ServerMessage(309, // %0精算总额%1。
							l1castle.getName(), String.valueOf(l1castle
									.getPublicMoney())));
					htmlid = ""; // 消
				}
			}
		} else if (s.equalsIgnoreCase("tax")) { // 税率调节
			pc.sendPackets(new S_TaxRate(pc.getId()));
		} else if (s.equalsIgnoreCase("withdrawal")) { // 资金引出
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				int castle_id = clan.getCastleId();
				if (castle_id != 0) { // 城主
					L1Castle l1castle = CastleTable.getInstance()
							.getCastleTable(castle_id);
					// 增加判断以防止被修改对话档
					if (pc.isCrown() && pc.getId() == clan.getLeaderId())
						// 增加判断以防止被修改对话档 end
						pc.sendPackets(new S_Drawal(pc.getId(), l1castle
								.getPublicMoney()));
					// 增加判断以防止被修改对话档
					else
						htmlid = "";
					// 增加判断以防止被修改对话档
				}
			}
		} else if (s.equalsIgnoreCase("cdeposit")) { // 资金入金
			pc.sendPackets(new S_Deposit(pc.getId()));
		} else if (s.equalsIgnoreCase("employ")) { // 佣兵雇用

		} else if (s.equalsIgnoreCase("arrange")) { // 雇用佣兵配置

		} else if (s.equalsIgnoreCase("castlegate")) { // 城门管理
			repairGate(pc);
			htmlid = ""; // ウィンドウを消す
		}
		// 城门修理
		else if (s.equalsIgnoreCase("autorepairon")) {// 自动修复内外城门
			L1NpcInstance npc = (L1NpcInstance) obj;
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				int castle_id = clan.getCastleId();
				if (castle_id != 0) {
					L1Castle l1castle = CastleTable.getInstance()
							.getCastleTable(castle_id);
					if (pc.isCrown() && pc.getId() == clan.getLeaderId()) {// 城主
						if (WarTimeController.getInstance().isNowWar(castle_id)) {// 攻城战中
							htmlid = "othmond11";
						} else if (l1castle.getPublicMoney() < 100000) {
							htmlid = "othmond10";
						} else {
						}
					}
				}
			}
		} else if (s.equalsIgnoreCase("autorepairoff")) {// 取消自动修复内外城门
			L1NpcInstance npc = (L1NpcInstance) obj;
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				int castle_id = clan.getCastleId();
				if (castle_id != 0) {
					L1Castle l1castle = CastleTable.getInstance()
							.getCastleTable(castle_id);
					if (pc.isCrown() && pc.getId() == clan.getLeaderId()) {// 城主
						if (WarTimeController.getInstance().isNowWar(castle_id)) {// 攻城战中
							htmlid = "othmond11";
						} else if (l1castle.getPublicMoney() < 100000) {
							htmlid = "othmond10";
						} else {
						}
					}
				}
			}
		}
		// 城门修理 end
		else if (s.equalsIgnoreCase("encw")) { // 武器专门家 / 武器强化魔法受
			// 拟武修正
			if (pc.getInventory().consumeItem(40308, 100)) {
				L1ItemInstance item = pc.getWeapon();
				if (item != null) {
					pc.sendPackets(new S_ServerMessage(161, item.getLogName(),
							"$245", "$247"));
					item.setSkillEnchant(pc, 12, 1800 * 1000);
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
				pc.sendPackets(new S_SkillSound(pc.getId(), 747));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 747));
				htmlid = ""; // 消
			} else {
				pc.sendPackets(new S_ServerMessage(189));
				htmlid = "";
			}
			// 拟武修正 end
		} else if (s.equalsIgnoreCase("enca")) { // 防具专门家 / 防具强化魔法受
			// 铠甲修正
			if (pc.getInventory().consumeItem(40308, 100)) {
				L1ItemInstance item = pc.getInventory().getItemEquipped(2, 2);
				if (item != null) {
					pc.sendPackets(new S_ServerMessage(161, item.getLogName(),
							"$245", "$247"));
					item.setSkillEnchant(pc, 21, 1800 * 1000);
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
				pc.sendPackets(new S_SkillSound(pc.getId(), 748));
				pc.broadcastPacket(new S_SkillSound(pc.getId(), 748));
				htmlid = "";
			} else {
				pc.sendPackets(new S_ServerMessage(189));
				htmlid = "";
			}
			// 铠甲修正 end
		} else if (s.equalsIgnoreCase("depositnpc")) { // “动物预”
			Object[] petList = pc.getPetList().values().toArray();
			for (Object petObject : petList) {
				if (petObject instanceof L1PetInstance) { // 
					L1PetInstance pet = (L1PetInstance) petObject;
					pet.collect();
					pet.updatePet();
					pc.getPetList().remove(pet.getId());
					pet.setDead(true);
					pet.deleteMe();
				}
			}
			htmlid = ""; // 消
		} else if (s.equalsIgnoreCase("withdrawnpc")) { // “动物受取”
			pc.sendPackets(new S_PetList(objid, pc));
		} else if (s.equalsIgnoreCase("changename")) { // “名前决”
			pc.setTempID(objid); // 
			pc.sendPackets(new S_Message_YN(325, "")); // 动物名前决：
		} else if (s.equalsIgnoreCase("select")) { // 竞卖揭示板
			pc.sendPackets(new S_AuctionBoardRead(objid, s2));
		} else if (s.equalsIgnoreCase("map")) { // 位置确
			pc.sendPackets(new S_HouseMap(objid, s2));
		} else if (s.equalsIgnoreCase("apply")) { // 竞卖参加
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				if (pc.isCrown() && pc.getId() == clan.getLeaderId()) { // 君主、、血盟主
					if (pc.getLevel() >= 15) {
						if (clan.getHouseId() == 0) {
							pc.sendPackets(new S_ApplyAuction(objid, s2));
						} else {
							pc.sendPackets(new S_ServerMessage(521)); // 家所有。
							htmlid = ""; // 消
						}
					} else {
						pc.sendPackets(new S_ServerMessage(519)); // 15未满君主竞卖参加。
						htmlid = ""; // 消
					}
				} else {
					pc.sendPackets(new S_ServerMessage(518)); // 命令血盟君主利用。
					htmlid = ""; // 消
				}
			} else {
				pc.sendPackets(new S_ServerMessage(518)); // 命令血盟君主利用。
				htmlid = ""; // 消
			}
		} else if (s.equalsIgnoreCase("open") // 开
				|| s.equalsIgnoreCase("close")) { // 闭
			L1NpcInstance npc = (L1NpcInstance) obj;
			openCloseDoor(pc, npc, s);
			htmlid = ""; // 消
		} else if (s.equalsIgnoreCase("expel")) { // 外部人间追出
			L1NpcInstance npc = (L1NpcInstance) obj;
			expelOtherClan(pc, npc.getNpcTemplate().get_npcId());
			htmlid = ""; // 消
		} else if (s.equalsIgnoreCase("pay")) { // 税金纳
		} else if (s.equalsIgnoreCase("name")) { // 家名前决
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				int houseId = clan.getHouseId();
				if (houseId != 0) {
					L1House house = HouseTable.getInstance().getHouseTable(
							houseId);
					int keeperId = house.getKeeperId();
					L1NpcInstance npc = (L1NpcInstance) obj;
					if (npc.getNpcTemplate().get_npcId() == keeperId) {
						pc.setTempID(houseId); // ID保存
						pc.sendPackets(new S_Message_YN(512, "")); // 家名前？
					}
				}
			}
			htmlid = ""; // 消
		} else if (s.equalsIgnoreCase("rem")) { // 家中家具取除
		} else if (s.equalsIgnoreCase("tel0") // (仓库)
				|| s.equalsIgnoreCase("tel1") // (保管所)
				|| s.equalsIgnoreCase("tel2") // (赎罪使者)
				|| s.equalsIgnoreCase("tel3")) { // (市场)
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				int houseId = clan.getHouseId();
				if (houseId != 0) {
					L1House house = HouseTable.getInstance().getHouseTable(
							houseId);
					int keeperId = house.getKeeperId();
					L1NpcInstance npc = (L1NpcInstance) obj;
					if (npc.getNpcTemplate().get_npcId() == keeperId) {
						int[] loc = new int[3];
						if (s.equalsIgnoreCase("tel0")) {
							loc = L1HouseLocation.getHouseTeleportLoc(houseId,
									0);
						} else if (s.equalsIgnoreCase("tel1")) {
							loc = L1HouseLocation.getHouseTeleportLoc(houseId,
									1);
						} else if (s.equalsIgnoreCase("tel2")) {
							loc = L1HouseLocation.getHouseTeleportLoc(houseId,
									2);
						} else if (s.equalsIgnoreCase("tel3")) {
							loc = L1HouseLocation.getHouseTeleportLoc(houseId,
									3);
						}
						L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2],
								5, true);
					}
				}
			}
			htmlid = ""; // 消
		} else if (s.equalsIgnoreCase("upgrade")) { // 地下作
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				int houseId = clan.getHouseId();
				if (houseId != 0) {
					L1House house = HouseTable.getInstance().getHouseTable(
							houseId);
					int keeperId = house.getKeeperId();
					L1NpcInstance npc = (L1NpcInstance) obj;
					if (npc.getNpcTemplate().get_npcId() == keeperId) {
						if (pc.isCrown() && pc.getId() == clan.getLeaderId()) { // 君主、、血盟主
							if (house.isPurchaseBasement()) {
								// 既地下所有。
								pc.sendPackets(new S_ServerMessage(1135));
							} else {
								if (pc.getInventory().consumeItem(
										L1ItemId.ADENA, 5000000)) {
									house.setPurchaseBasement(true);
									HouseTable.getInstance().updateHouse(house); // DB书
									// 地下生成。
									pc.sendPackets(new S_ServerMessage(1099));
								} else {
									// \f1不足。
									pc.sendPackets(new S_ServerMessage(189));
								}
							}
						} else {
							// 命令血盟君主利用。
							pc.sendPackets(new S_ServerMessage(518));
						}
					}
				}
			}
			htmlid = ""; // 消
		} else if (s.equalsIgnoreCase("hall")
				&& obj instanceof L1HousekeeperInstance) { // 地下
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				int houseId = clan.getHouseId();
				if (houseId != 0) {
					L1House house = HouseTable.getInstance().getHouseTable(
							houseId);
					int keeperId = house.getKeeperId();
					L1NpcInstance npc = (L1NpcInstance) obj;
					if (npc.getNpcTemplate().get_npcId() == keeperId) {
						if (house.isPurchaseBasement()) {
							int[] loc = new int[3];
							loc = L1HouseLocation.getBasementLoc(houseId);
							L1Teleport.teleport(pc, loc[0], loc[1],
									(short) (loc[2]), 5, true);
						} else {
							// 地下、。
							pc.sendPackets(new S_ServerMessage(1098));
						}
					}
				}
			}
			htmlid = ""; // 消
		}

		// ElfAttr:0.无属性,1.地属性,2.火属性,4.水属性,8.风属性
		else if (s.equalsIgnoreCase("fire")) // 属性变更“火系列习”
		{
			if (pc.isElf()) {
				if (pc.getElfAttr() != 0) {
					return;
				}
				// 单属性防御判断
				if (pc.hasSkillEffect(147)) {
					pc.removeSkillEffect(147);
				}
				// 单属性防御判断 end
				pc.setElfAttr(2);
				pc.save(); // DB情报书迂。
				pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_ELF, 1));
				htmlid = ""; // 消
			}
		} else if (s.equalsIgnoreCase("water")) { // 属性变更“水系列习”
			if (pc.isElf()) {
				if (pc.getElfAttr() != 0) {
					return;
				}
				// 单属性防御判断
				if (pc.hasSkillEffect(147)) {
					pc.removeSkillEffect(147);
				}
				// 单属性防御判断 end
				pc.setElfAttr(4);
				pc.save(); // DB情报书迂
				pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_ELF, 2));
				htmlid = ""; // 消
			}
		} else if (s.equalsIgnoreCase("air")) { // 属性变更“风系列习”
			if (pc.isElf()) {
				if (pc.getElfAttr() != 0) {
					return;
				}
				// 单属性防御判断
				if (pc.hasSkillEffect(147)) {
					pc.removeSkillEffect(147);
				}
				// 单属性防御判断 end
				pc.setElfAttr(8);
				pc.save(); // DB情报书迂
				pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_ELF, 3));
				htmlid = ""; // 消
			}
		} else if (s.equalsIgnoreCase("earth")) { // 属性变更“地系列习”
			if (pc.isElf()) {
				if (pc.getElfAttr() != 0) {
					return;
				}
				// 单属性防御判断
				if (pc.hasSkillEffect(147)) {
					pc.removeSkillEffect(147);
				}
				// 单属性防御判断 end
				pc.setElfAttr(1);
				pc.save(); // DB情报书迂
				pc.sendPackets(new S_PacketBox(S_PacketBox.MSG_ELF, 4));
				htmlid = ""; // 消
			}
		} else if (s.equalsIgnoreCase("init")) { // 属性变更“精灵力除去”
			if (pc.isElf()) {
				if (pc.getElfAttr() == 0) {
					return;
				}
				// 单属性防御判断
				if (pc.hasSkillEffect(147)) {
					pc.removeSkillEffect(147);
				}
				// 单属性防御判断 end
				for (int cnt = 129; cnt <= 176; cnt++) // 全魔法
				{
					L1Skills l1skills1 = SkillsTable.getInstance().getTemplate(
							cnt);
					int skill_attr = l1skills1.getAttr();
					if (skill_attr != 0) // 无属性魔法以外魔法DB削除
					{
						CharSkillReading.get().spellLost(pc.getId(),
								l1skills1.getSkillId());
					}
				}
				// 上升属性防御
				if (pc.hasSkillEffect(L1SkillId.ELEMENTAL_PROTECTION)) {
					pc.removeSkillEffect(L1SkillId.ELEMENTAL_PROTECTION);
				}
				pc.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 248, 252, 252, 255, 0, 0)); // 无属性魔法以外魔法魔法削除
				pc.setElfAttr(0);
				pc.save(); // DB情报书迂
				pc.sendPackets(new S_ServerMessage(678));
				htmlid = ""; // 消
			}
		} else if (s.equalsIgnoreCase("exp")) { // “经验值回复”
			if (pc.getExpRes() == 1) {
				int cost = 0;
				int level = pc.getLevel();
				int lawful = pc.getLawful();
				if (level < 45) {
					cost = level * level * 100;
				} else {
					cost = level * level * 200;
				}
				if (lawful >= 0) {
					cost = (cost / 2);
				}
				pc.sendPackets(new S_Message_YN(738, String.valueOf(cost))); // 经验值回复%0必要。经验值回复？
			} else {
				pc.sendPackets(new S_ServerMessage(739)); // 今经验值回复。
				htmlid = ""; // 消
			}
		} else if (s.equalsIgnoreCase("pk")) { // “赎罪”
			if (pc.getLawful() < 30000) {
				pc.sendPackets(new S_ServerMessage(559)); // \f1罪晴十分善行行。
			} else if (pc.get_PKcount() < 5) {
				pc.sendPackets(new S_ServerMessage(560)); // \f1罪晴必要。
			} else {
				if (pc.getInventory().consumeItem(L1ItemId.ADENA, 700000)) {
					pc.set_PKcount(pc.get_PKcount() - 5);
					pc.sendPackets(new S_ServerMessage(561, String.valueOf(pc
							.get_PKcount()))); // PK回数%0。
				} else {
					pc.sendPackets(new S_ServerMessage(189)); // \f1不足。
				}
			}
			// 消
			htmlid = "";
		} else if (s.equalsIgnoreCase("ent")) {
			// “化屋敷入”
			// “ 加”
			// “技场入”
			int npcId = ((L1NpcInstance) obj).getNpcId();
			if (npcId == 80085 || npcId == 80086 || npcId == 80087) {
				htmlid = enterHauntedHouse(pc);
			} else if (npcId == 50038 || npcId == 50042 || npcId == 50029
					|| npcId == 50019 || npcId == 50062) { // 副管理人场合
				htmlid = watchUb(pc, npcId);
			} else {
				htmlid = enterUb(pc, npcId);
			}
		} else if (s.equalsIgnoreCase("par")) { // UB连“ 加” 副管理人由
			htmlid = enterUb(pc, ((L1NpcInstance) obj).getNpcId());
		} else if (s.equalsIgnoreCase("info")) { // “情报确认”“竞技情报确认”
			int npcId = ((L1NpcInstance) obj).getNpcId();
			if (npcId == 80085 || npcId == 80086 || npcId == 80087) {
			} else {
				htmlid = "colos2";
			}
		} else if (s.equalsIgnoreCase("sco")) { // UB连“高得者一确认”
			htmldata = new String[10];
			htmlid = "colos3";
		}

		else if (s.equalsIgnoreCase("haste")) { // 师
			L1NpcInstance l1npcinstance = (L1NpcInstance) obj;
			int npcid = l1npcinstance.getNpcTemplate().get_npcId();
			if (npcid == 70514) {
				pc.sendPackets(new S_ServerMessage(183));
				pc.sendPackets(new S_SkillHaste(pc.getId(), 1, 1600));
				pc.sendPackets(new S_SkillSound(pc.getId(), 755));
				pc.setMoveSpeed(1);
				pc.setSkillEffect(L1SkillId.STATUS_HASTE, 1600 * 1000);
				htmlid = ""; // 消
			}
		}
		// 变身专门家
		else if (s.equalsIgnoreCase("skeleton nbmorph")) {
			poly(_client, 2374);
			htmlid = ""; // 消
		} else if (s.equalsIgnoreCase("lycanthrope nbmorph")) {
			poly(_client, 3874);
			htmlid = ""; // 消
		} else if (s.equalsIgnoreCase("shelob nbmorph")) {
			poly(_client, 95);
			htmlid = ""; // 消
		} else if (s.equalsIgnoreCase("ghoul nbmorph")) {
			poly(_client, 3873);
			htmlid = ""; // 消
		} else if (s.equalsIgnoreCase("ghast nbmorph")) {
			poly(_client, 3875);
			htmlid = ""; // 消
		} else if (s.equalsIgnoreCase("atuba orc nbmorph")) {
			poly(_client, 3868);
			htmlid = ""; // 消
		} else if (s.equalsIgnoreCase("skeleton axeman nbmorph")) {
			poly(_client, 2376);
			htmlid = ""; // 消
		} else if (s.equalsIgnoreCase("troll nbmorph")) {
			poly(_client, 3878);
			htmlid = ""; // 消
		}
		// 长老 
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71038) {
			// “手纸受取”
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41060, 1); // 推荐书
				String npcName = npc.getNpcTemplate().get_name();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0%1。
				htmlid = "orcfnoname9";
			}
			// “调查”
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41060, 1)) {
					htmlid = "orcfnoname11";
				}
			}
		}
		// - 
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71039) {
			// “、场所送”
			if (s.equalsIgnoreCase("teleportURL")) {
				htmlid = "orcfbuwoo2";
			}
		}
		// 调查团长  
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71040) {
			// “”
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41065, 1); // 调查团证书
				String npcName = npc.getNpcTemplate().get_name();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0%1。
				htmlid = "orcfnoa4";
			}
			// “调查”
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41065, 1)) {
					htmlid = "orcfnoa7";
				}
			}
		}
		//  
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71041) {
			// “调查”
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41064, 1); // 调查团证书
				String npcName = npc.getNpcTemplate().get_name();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0%1。
				htmlid = "orcfhuwoomo4";
			}
			// “调查”
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41064, 1)) {
					htmlid = "orcfhuwoomo6";
				}
			}
		}
		//  
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71042) {
			// “调查”
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41062, 1); // 调查团证书
				String npcName = npc.getNpcTemplate().get_name();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0%1。
				htmlid = "orcfbakumo4";
			}
			// “调查”
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41062, 1)) {
					htmlid = "orcfbakumo6";
				}
			}
		}
		// - 
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71043) {
			// “调查”
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41063, 1); // 调查团证书
				String npcName = npc.getNpcTemplate().get_name();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0%1。
				htmlid = "orcfbuka4";
			}
			// “调查”
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41063, 1)) {
					htmlid = "orcfbuka6";
				}
			}
		}
		// - 
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71044) {
			// “调查”
			if (s.equalsIgnoreCase("A")) {
				L1NpcInstance npc = (L1NpcInstance) obj;
				L1ItemInstance item = pc.getInventory().storeItem(41061, 1); // 调查团证书
				String npcName = npc.getNpcTemplate().get_name();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName)); // \f1%0%1。
				htmlid = "orcfkame4";
			}
			// “调查”
			else if (s.equalsIgnoreCase("Z")) {
				if (pc.getInventory().consumeItem(41061, 1)) {
					htmlid = "orcfkame6";
				}
			}
		}
		// 
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71078) {
			// “入”
			if (s.equalsIgnoreCase("teleportURL")) {
				htmlid = "usender2";
			}
		}
		// 治安团长
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71080) {
			// “私手伝”
			if (s.equalsIgnoreCase("teleportURL")) {
				htmlid = "amisoo2";
			}
		}
		// 空间歪
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80048) {
			// “”
			if (s.equalsIgnoreCase("2")) {
				htmlid = ""; // 消
			}
		}
		// 摇者
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80049) {
			// “意志迎入”
			if (s.equalsIgnoreCase("1")) {
				if (pc.getKarma() <= -10000000) {
					pc.setKarma(1000000);
					// 笑声脑里强打。
					pc.sendPackets(new S_ServerMessage(1078));
					htmlid = "betray13";
				}
			}
		}
		// 执政官
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80050) {
			// “私灵魂样…”
			if (s.equalsIgnoreCase("1")) {
				htmlid = "meet105";
			}
			// “私灵魂样忠诚誓…”
			else if (s.equalsIgnoreCase("2")) {
				if (pc.getInventory().checkItem(40718)) { // 欠片
					htmlid = "meet106";
				} else {
					htmlid = "meet110";
				}
			}
			// “欠片1个捧”
			else if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().consumeItem(40718, 1)) {
					pc.addKarma((int) (-100 * Config.RATE_KARMA));
					// 姿近感。
					pc.sendPackets(new S_ServerMessage(1079));
					htmlid = "meet107";
				} else {
					htmlid = "meet104";
				}
			}
			// “欠片10个捧”
			else if (s.equalsIgnoreCase("b")) {
				if (pc.getInventory().consumeItem(40718, 10)) {
					pc.addKarma((int) (-1000 * Config.RATE_KARMA));
					// 姿近感。
					pc.sendPackets(new S_ServerMessage(1079));
					htmlid = "meet108";
				} else {
					htmlid = "meet104";
				}
			}
			// “欠片100个捧”
			else if (s.equalsIgnoreCase("c")) {
				if (pc.getInventory().consumeItem(40718, 100)) {
					pc.addKarma((int) (-10000 * Config.RATE_KARMA));
					// 姿近感。
					pc.sendPackets(new S_ServerMessage(1079));
					htmlid = "meet109";
				} else {
					htmlid = "meet104";
				}
			}
			// “样会”
			else if (s.equalsIgnoreCase("d")) {
				if (pc.getInventory().checkItem(40615) // 影神殿2阶键
						|| pc.getInventory().checkItem(40616)) { // 影神殿3阶键
					htmlid = "";
				} else {
					L1Teleport.teleport(pc, 32683, 32895, (short) 608, 5, true);
				}
			}
		}
		// 锻冶屋
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80053) {
			int karmaLevel = pc.getKarmaLevel();
			// “材料用意”
			if (s.equalsIgnoreCase("a")) {
				//   / 锻冶屋
				int aliceMaterialId = 0;
				int[] aliceMaterialIdList = { 40991, 196, 197, 198, 199, 200,
						201, 202, 203 };
				for (int id : aliceMaterialIdList) {
					if (pc.getInventory().checkItem(id)) {
						aliceMaterialId = id;
						break;
					}
				}
				if (aliceMaterialId == 0) {
					htmlid = "alice_no";
				} else if (aliceMaterialId == 40991) {
					if (karmaLevel <= -1) {
						materials = new int[] { 40995, 40718, 40991 };
						counts = new long[] { 100, 100, 1 };
						createitem = new int[] { 196 };
						createcount = new long[] { 1 };
						success_htmlid = "alice_1";
						failure_htmlid = "alice_no";
					} else {
						htmlid = "aliceyet";
					}
				} else if (aliceMaterialId == 196) {
					if (karmaLevel <= -2) {
						materials = new int[] { 40997, 40718, 196 };
						counts = new long[] { 100, 100, 1 };
						createitem = new int[] { 197 };
						createcount = new long[] { 1 };
						success_htmlid = "alice_2";
						failure_htmlid = "alice_no";
					} else {
						htmlid = "alice_1";
					}
				} else if (aliceMaterialId == 197) {
					if (karmaLevel <= -3) {
						materials = new int[] { 40990, 40718, 197 };
						counts = new long[] { 100, 100, 1 };
						createitem = new int[] { 198 };
						createcount = new long[] { 1 };
						success_htmlid = "alice_3";
						failure_htmlid = "alice_no";
					} else {
						htmlid = "alice_2";
					}
				} else if (aliceMaterialId == 198) {
					if (karmaLevel <= -4) {
						materials = new int[] { 40994, 40718, 198 };
						counts = new long[] { 50, 100, 1 };
						createitem = new int[] { 199 };
						createcount = new long[] { 1 };
						success_htmlid = "alice_4";
						failure_htmlid = "alice_no";
					} else {
						htmlid = "alice_3";
					}
				} else if (aliceMaterialId == 199) {
					if (karmaLevel <= -5) {
						materials = new int[] { 40993, 40718, 199 };
						counts = new long[] { 50, 100, 1 };
						createitem = new int[] { 200 };
						createcount = new long[] { 1 };
						success_htmlid = "alice_5";
						failure_htmlid = "alice_no";
					} else {
						htmlid = "alice_4";
					}
				} else if (aliceMaterialId == 200) {
					if (karmaLevel <= -6) {
						materials = new int[] { 40998, 40718, 200 };
						counts = new long[] { 50, 100, 1 };
						createitem = new int[] { 201 };
						createcount = new long[] { 1 };
						success_htmlid = "alice_6";
						failure_htmlid = "alice_no";
					} else {
						htmlid = "alice_5";
					}
				} else if (aliceMaterialId == 201) {
					if (karmaLevel <= -7) {
						materials = new int[] { 40996, 40718, 201 };
						counts = new long[] { 10, 100, 1 };
						createitem = new int[] { 202 };
						createcount = new long[] { 1 };
						success_htmlid = "alice_7";
						failure_htmlid = "alice_no";
					} else {
						htmlid = "alice_6";
					}
				} else if (aliceMaterialId == 202) {
					if (karmaLevel <= -8) {
						materials = new int[] { 40992, 40718, 202 };
						counts = new long[] { 10, 100, 1 };
						createitem = new int[] { 203 };
						createcount = new long[] { 1 };
						success_htmlid = "alice_8";
						failure_htmlid = "alice_no";
					} else {
						htmlid = "alice_7";
					}
				} else if (aliceMaterialId == 203) {
					htmlid = "alice_8";
				}
			}
		}
		// 补佐官
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80055) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			htmlid = getYaheeAmulet(pc, npc, s);
		}
		// 业管理者
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80056) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			if (pc.getKarma() <= -10000000) {
				getBloodCrystalByKarma(pc, npc, s);
			}
			htmlid = "";
		}
		// 次元扉(部屋)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80063) {
			// “中入”
			if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().checkItem(40921)) { // 元素支配者
					L1Teleport.teleport(pc, 32674, 32832, (short) 603, 2, true);
				} else {
					htmlid = "gpass02";
				}
			}
		}
		// 执政官
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80064) {
			// “私永远主样…”
			if (s.equalsIgnoreCase("1")) {
				htmlid = "meet005";
			}
			// “私灵魂样忠诚誓…”
			else if (s.equalsIgnoreCase("2")) {
				if (pc.getInventory().checkItem(40678)) { // 欠片
					htmlid = "meet006";
				} else {
					htmlid = "meet010";
				}
			}
			// “欠片1个捧”
			else if (s.equalsIgnoreCase("a")) {
				if (pc.getInventory().consumeItem(40678, 1)) {
					pc.addKarma((int) (100 * Config.RATE_KARMA));
					// 笑声脑里强打。
					pc.sendPackets(new S_ServerMessage(1078));
					htmlid = "meet007";
				} else {
					htmlid = "meet004";
				}
			}
			// “欠片10个捧”
			else if (s.equalsIgnoreCase("b")) {
				if (pc.getInventory().consumeItem(40678, 10)) {
					pc.addKarma((int) (1000 * Config.RATE_KARMA));
					// 
					pc.sendPackets(new S_ServerMessage(1078));
					htmlid = "meet008";
				} else {
					htmlid = "meet004";
				}
			}
			// “欠片100个捧”
			else if (s.equalsIgnoreCase("c")) {
				if (pc.getInventory().consumeItem(40678, 100)) {
					pc.addKarma((int) (10000 * Config.RATE_KARMA));
					// 笑声脑里强打。
					pc.sendPackets(new S_ServerMessage(1078));
					htmlid = "meet009";
				} else {
					htmlid = "meet004";
				}
			}
			// “样会”
			else if (s.equalsIgnoreCase("d")) {
				if (pc.getInventory().checkItem(40909) // 地通行证
						|| pc.getInventory().checkItem(40910) // 水通行证
						|| pc.getInventory().checkItem(40911) // 火通行证
						|| pc.getInventory().checkItem(40912) //
						|| pc.getInventory().checkItem(40913) // 地印章
						|| pc.getInventory().checkItem(40914) // 水印章
						|| pc.getInventory().checkItem(40915) // 火印章
						|| pc.getInventory().checkItem(40916) // 风印章
						|| pc.getInventory().checkItem(40917) // 地支配者
						|| pc.getInventory().checkItem(40918) // 水支配者
						|| pc.getInventory().checkItem(40919) // 火支配者
						|| pc.getInventory().checkItem(40920) // 风支配者
						|| pc.getInventory().checkItem(40921)) { // 元素支配者
					htmlid = "";
				} else {
					L1Teleport.teleport(pc, 32674, 32832, (short) 602, 2, true);
				}
			}
		}
		// 摇者
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80066) {
			// “意志受入”
			if (s.equalsIgnoreCase("1")) {
				if (pc.getKarma() >= 10000000) {
					pc.setKarma(-1000000);
					// 姿近感。
					pc.sendPackets(new S_ServerMessage(1079));
					htmlid = "betray03";
				}
			}
		}
		// 补佐官
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80071) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			htmlid = getBarlogEarring(pc, npc, s);
		}
		// 锻冶屋
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80072) {
			int karmaLevel = pc.getKarmaLevel();
			if (s.equalsIgnoreCase("0")) {
				htmlid = "lsmitha";
			} else if (s.equalsIgnoreCase("1")) {
				htmlid = "lsmithb";
			} else if (s.equalsIgnoreCase("2")) {
				htmlid = "lsmithc";
			} else if (s.equalsIgnoreCase("3")) {
				htmlid = "lsmithd";
			} else if (s.equalsIgnoreCase("4")) {
				htmlid = "lsmithe";
			} else if (s.equalsIgnoreCase("5")) {
				htmlid = "lsmithf";
			} else if (s.equalsIgnoreCase("6")) {
				htmlid = "";
			} else if (s.equalsIgnoreCase("7")) {
				htmlid = "lsmithg";
			} else if (s.equalsIgnoreCase("8")) {
				htmlid = "lsmithh";
			}
			//  / 锻冶屋
			else if (s.equalsIgnoreCase("a") && karmaLevel >= 1) {
				materials = new int[] { 20158, 40669, 40678 };
				counts = new long[] { 1, 50, 100 };
				createitem = new int[] { 20083 };
				createcount = new long[] { 1 };
				success_htmlid = "";
				failure_htmlid = "lsmithaa";
			}
			//  / 
			else if (s.equalsIgnoreCase("b") && karmaLevel >= 2) {
				materials = new int[] { 20144, 40672, 40678 };
				counts = new long[] { 1, 50, 100 };
				createitem = new int[] { 20131 };
				createcount = new long[] { 1 };
				success_htmlid = "";
				failure_htmlid = "lsmithbb";
			}
			//  / 锻冶屋
			else if (s.equalsIgnoreCase("c") && karmaLevel >= 3) {
				materials = new int[] { 20075, 40671, 40678 };
				counts = new long[] { 1, 50, 100 };
				createitem = new int[] { 20069 };
				createcount = new long[] { 1 };
				success_htmlid = "";
				failure_htmlid = "lsmithcc";
			}
			//  / 锻冶屋
			else if (s.equalsIgnoreCase("d") && karmaLevel >= 4) {
				materials = new int[] { 20183, 40674, 40678 };
				counts = new long[] { 1, 20, 100 };
				createitem = new int[] { 20179 };
				createcount = new long[] { 1 };
				success_htmlid = "";
				failure_htmlid = "lsmithdd";
			}
			//  / 锻冶屋
			else if (s.equalsIgnoreCase("e") && karmaLevel >= 5) {
				materials = new int[] { 20190, 40674, 40678 };
				counts = new long[] { 1, 40, 100 };
				createitem = new int[] { 20209 };
				createcount = new long[] { 1 };
				success_htmlid = "";
				failure_htmlid = "lsmithee";
			}
			//  / 锻冶屋
			else if (s.equalsIgnoreCase("f") && karmaLevel >= 6) {
				materials = new int[] { 20078, 40674, 40678 };
				counts = new long[] { 1, 5, 100 };
				createitem = new int[] { 20290 };
				createcount = new long[] { 1 };
				success_htmlid = "";
				failure_htmlid = "lsmithff";
			}
			//  / 锻冶屋
			else if (s.equalsIgnoreCase("g") && karmaLevel >= 7) {
				materials = new int[] { 20078, 40670, 40678 };
				counts = new long[] { 1, 1, 100 };
				createitem = new int[] { 20261 };
				createcount = new long[] { 1 };
				success_htmlid = "";
				failure_htmlid = "lsmithgg";
			}
			//  / 锻冶屋
			else if (s.equalsIgnoreCase("h") && karmaLevel >= 8) {
				materials = new int[] { 40719, 40673, 40678 };
				counts = new long[] { 1, 1, 100 };
				createitem = new int[] { 20031 };
				createcount = new long[] { 1 };
				success_htmlid = "";
				failure_htmlid = "lsmithhh";
			}
		}
		// 业管理者
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80074) {
			L1NpcInstance npc = (L1NpcInstance) obj;
			if (pc.getKarma() >= 10000000) {
				getSoulCrystalByKarma(pc, npc, s);
			}
			htmlid = "";
		}
		// 
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80057) {
			htmlid = karmaLevelToHtmlId(pc.getKarmaLevel());
			htmldata = new String[] { String.valueOf(pc.getKarmaPercent()) };
		}
		// 次元扉(土风水火)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80059
				|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80060
				|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80061
				|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80062) {
			htmlid = talkToDimensionDoor(pc, (L1NpcInstance) obj, s);
		}
		//   
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 81124) {
			if (s.equalsIgnoreCase("1")) {
				poly(_client, 4002);
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("2")) {
				poly(_client, 4004);
				htmlid = ""; // 消
			} else if (s.equalsIgnoreCase("3")) {
				poly(_client, 4950);
				htmlid = ""; // 消
			}
		}

		// 关连
		// 一般 / 
		else if (s.equalsIgnoreCase("contract1")) {
			pc.getQuest().set_step(L1Quest.QUEST_LYRA, 1);
			htmlid = "lyraev2";
		} else if (s.equalsIgnoreCase("contract1yes") || //  Yes
				s.equalsIgnoreCase("contract1no")) { //  No

			if (s.equalsIgnoreCase("contract1yes")) {
				htmlid = "lyraev5";
			} else if (s.equalsIgnoreCase("contract1no")) {
				pc.getQuest().set_step(L1Quest.QUEST_LYRA, 0);
				htmlid = "lyraev4";
			}
			int totem = 0;
			if (pc.getInventory().checkItem(40131)) {
				totem++;
			}
			if (pc.getInventory().checkItem(40132)) {
				totem++;
			}
			if (pc.getInventory().checkItem(40133)) {
				totem++;
			}
			if (pc.getInventory().checkItem(40134)) {
				totem++;
			}
			if (pc.getInventory().checkItem(40135)) {
				totem++;
			}
			if (totem != 0) {
				materials = new int[totem];
				counts = new long[totem];
				createitem = new int[totem];
				createcount = new long[totem];

				totem = 0;
				if (pc.getInventory().checkItem(40131)) {
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40131);
					long i1 = l1iteminstance.getCount();
					materials[totem] = 40131;
					counts[totem] = i1;
					createitem[totem] = L1ItemId.ADENA;
					createcount[totem] = i1 * 50;
					totem++;
				}
				if (pc.getInventory().checkItem(40132)) {
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40132);
					long i1 = l1iteminstance.getCount();
					materials[totem] = 40132;
					counts[totem] = i1;
					createitem[totem] = L1ItemId.ADENA;
					createcount[totem] = i1 * 100;
					totem++;
				}
				if (pc.getInventory().checkItem(40133)) {
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40133);
					long i1 = l1iteminstance.getCount();
					materials[totem] = 40133;
					counts[totem] = i1;
					createitem[totem] = L1ItemId.ADENA;
					createcount[totem] = i1 * 50;
					totem++;
				}
				if (pc.getInventory().checkItem(40134)) {
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40134);
					long i1 = l1iteminstance.getCount();
					materials[totem] = 40134;
					counts[totem] = i1;
					createitem[totem] = L1ItemId.ADENA;
					createcount[totem] = i1 * 30;
					totem++;
				}
				if (pc.getInventory().checkItem(40135)) {
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40135);
					long i1 = l1iteminstance.getCount();
					materials[totem] = 40135;
					counts[totem] = i1;
					createitem[totem] = L1ItemId.ADENA;
					createcount[totem] = i1 * 200;
					totem++;
				}
			}
		}
		// 最近物价
		// 、、、、
		else if (s.equalsIgnoreCase("pandora6") || s.equalsIgnoreCase("cold6")
				|| s.equalsIgnoreCase("balsim3")
				|| s.equalsIgnoreCase("mellin3") || s.equalsIgnoreCase("glen3")) {
			htmlid = s;
			int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
			int taxRatesCastle = L1CastleLocation
					.getCastleTaxRateByNpcId(npcid);
			htmldata = new String[] { String.valueOf(taxRatesCastle) };
		}
		// 菲力士-标本制作委托书
		else if (s.equals("0")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80102) {// 菲力士-标本制作委托书
			if (pc.getInventory().checkItem(40308, 50000)) {// 金币50000
				materials = new int[] { L1ItemId.ADENA };
				counts = new long[] { 50000 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_84 };// 标本制作委托书
				createcount = new long[] { 1 };
				htmlid = "";
			} else {
				htmlid = "fillis5";
			}
		} else if (s.equals("1")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80102) {// 菲力士
			if (pc.getInventory()
					.consumeItem(l1j.william.New_Id.Item_EPU_84, 1)) {// 标本制作委托书
				htmlid = "";
			}
		} else if (s.equals("2")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80102) {// 菲力士
			if (pc.getInventory()
					.checkItem(l1j.william.New_Id.Item_EPU_85, 100)) {// 狩猎蚂蚁之证
				materials = new int[] { l1j.william.New_Id.Item_EPU_85, 40308 };
				counts = new long[] { 100, 20000 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_138 };// 巨大兵蚁标本
				createcount = new long[] { 1 };
				htmlid = "";
			} else {
				htmlid = "fillis7";
			}
		} else if (s.equals("3")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80102) {// 菲力士
			if (pc.getInventory()
					.checkItem(l1j.william.New_Id.Item_EPU_86, 100)) {// 狩猎熊之证
				materials = new int[] { l1j.william.New_Id.Item_EPU_86, 40308 };
				counts = new long[] { 100, 20000 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_139 };// 熊标本
				createcount = new long[] { 1 };
				htmlid = "";
			} else {
				htmlid = "fillis7";
			}
		} else if (s.equals("4")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80102) {// 菲力士
			if (pc.getInventory()
					.checkItem(l1j.william.New_Id.Item_EPU_87, 100)) {// 狩猎蛇女之证
				materials = new int[] { l1j.william.New_Id.Item_EPU_87, 40308 };
				counts = new long[] { 100, 20000 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_140 };// 蛇女标本
				createcount = new long[] { 1 };
				htmlid = "";
			} else {
				htmlid = "fillis7";
			}
		} else if (s.equals("5")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80102) {// 菲力士
			if (pc.getInventory()
					.checkItem(l1j.william.New_Id.Item_EPU_88, 100)) {// 狩猎黑虎之证
				materials = new int[] { l1j.william.New_Id.Item_EPU_88, 40308 };
				counts = new long[] { 100, 20000 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_141 };// 黑虎标本
				createcount = new long[] { 1 };
				htmlid = "";
			} else {
				htmlid = "fillis7";
			}
		} else if (s.equals("6")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80102) {// 菲力士
			if (pc.getInventory()
					.checkItem(l1j.william.New_Id.Item_EPU_89, 100)) {// 狩猎鹿之证
				materials = new int[] { l1j.william.New_Id.Item_EPU_89, 40308 };
				counts = new long[] { 100, 20000 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_142 };// 鹿标本
				createcount = new long[] { 1 };
				htmlid = "";
			} else {
				htmlid = "fillis7";
			}
		} else if (s.equals("7")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80102) {// 菲力士
			if (pc.getInventory()
					.checkItem(l1j.william.New_Id.Item_EPU_90, 100)) {// 狩猎哈维之证
				materials = new int[] { l1j.william.New_Id.Item_EPU_90, 40308 };
				counts = new long[] { 100, 20000 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_143 };// 哈维标本
				createcount = new long[] { 1 };
				htmlid = "";
			} else {
				htmlid = "fillis7";
			}
		}
		// 菲力士-标本制作委托书 end
		// 拉庞斯-料理书
		else if (s.equals("A")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80091) {// 拉庞斯
			if (pc.getInventory().checkItem(40308, 10000)) {// 金币10000
				materials = new int[] { 40308 };
				counts = new long[] { 10000 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_11 };// 料理书：1阶段
				createcount = new long[] { 1 };
				htmlid = "rrafons1";
			} else {
				htmlid = "rrafons2";
			}
		}
		// 拉庞斯-料理书 end
		// 拉罗森-圣水
		else if (s.equals("A")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80099) {// 拉罗森
			if (pc.getInventory().checkItem(40308, 300)) {// 金币300
				materials = new int[] { 40308 };
				counts = new long[] { 300 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_70 };// 圣水
				createcount = new long[] { 1 };
				htmlid = "";
			} else {
				htmlid = "rarson7";
			}
		} else if (s.equals("B")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80099) {// 拉罗森
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_EPU_80, 1)) {// 勇士之证
				materials = new int[] { l1j.william.New_Id.Item_EPU_80 };
				counts = new long[] { 1 };
				createitem = new int[] { 40308, l1j.william.New_Id.Item_EPU_72 };// 金币、拉罗森的推荐书
				createcount = new long[] { 2000, 1 };
				htmlid = "rarson9";
			}
		} else if (s.equals("C")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80099) {// 拉罗森
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_EPU_81, 1)) {// 勇士之证
				materials = new int[] { l1j.william.New_Id.Item_EPU_81 };
				counts = new long[] { 1 };
				createitem = new int[] { 40308 };// 金币30000
				createcount = new long[] { 30000 };
				htmlid = "rarson12";
			}
		} else if (s.equals("D")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80099) {// 拉罗森
			if (pc.getInventory().checkItem(40308, 300)) {// 金币300
				materials = new int[] { 40308 };
				counts = new long[] { 300 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_70 };// 圣水
				createcount = new long[] { 1 };
				htmlid = "";
			} else {
				htmlid = "rarson7";
			}
		}
		// 拉罗森-圣水 end
		// 可恩
		else if (s.equals("request letter of kuen")) {// 可恩的便条纸
			materials = new int[] { l1j.william.New_Id.Item_EPU_72 };// 拉罗森的推荐书
			counts = new long[] { 1 };
			createitem = new int[] { l1j.william.New_Id.Item_EPU_73 };// 可恩的便条纸
			createcount = new long[] { 1 };
			htmlid = "";
		} else if (s.equals("request holy mithril dust")) {// 神圣的米索莉粉
			materials = new int[] { 40494, l1j.william.New_Id.Item_EPU_70,
					l1j.william.New_Id.Item_EPU_73 };// 纯粹的米索莉块、圣水、可恩的便条纸
			counts = new long[] { 30, 1, 1 };
			createitem = new int[] { l1j.william.New_Id.Item_EPU_71 };// 神圣的米索莉粉
			createcount = new long[] { 1 };
			htmlid = "";
		} else if (s.equals("request old mans")) {
			if (pc.getCurrentHp() == 66) {
				L1World.getInstance().setControl(true);
			}
		}
		// 可恩 end
		// 波伦-波伦的资源清单
		else if (s.equals("q")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (!pc.getInventory()
					.checkItem(l1j.william.New_Id.Item_EPU_111, 1)) {// 波伦的资源清单
				L1ItemInstance item = pc.getInventory().storeItem(
						l1j.william.New_Id.Item_EPU_111, 1);
				if (item != null) {
					String itemName = ItemTable.getInstance()
							.getTemplate(l1j.william.New_Id.Item_EPU_111)
							.getName();
					String createrName = "";
					if (obj instanceof L1NpcInstance) {
						createrName = ((L1NpcInstance) obj).getNpcTemplate()
								.get_name();
					}
					pc.sendPackets(new S_ServerMessage(143, createrName,
							itemName)); // \f1%0%1。
				}
				htmlid = "";
			} else {
				htmlid = "rparum4";
			}
		} else if (s.equals("a")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (pc.getInventory().checkItem(40433, 10)) {// 犰狳之爪
				materials = new int[] { 40433 };
				counts = new long[] { 10 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_110 };// 波伦的袋子
				createcount = new long[] { 1 };
				htmlid = "rparum1";
			} else {
				htmlid = "rparum2";
			}
		} else if (s.equals("b")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (pc.getInventory().checkItem(40434, 10)) {// 犰狳的尾巴
				materials = new int[] { 40434 };
				counts = new long[] { 10 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_110 };// 波伦的袋子
				createcount = new long[] { 1 };
				htmlid = "rparum1";
			} else {
				htmlid = "rparum2";
			}
		} else if (s.equals("c")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (pc.getInventory().checkItem(40435, 10)) {// 深渊之花的花苞
				materials = new int[] { 40435 };
				counts = new long[] { 10 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_110 };// 波伦的袋子
				createcount = new long[] { 1 };
				htmlid = "rparum1";
			} else {
				htmlid = "rparum2";
			}
		} else if (s.equals("d")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (pc.getInventory().checkItem(40436, 10)) {// 深渊之花的根
				materials = new int[] { 40436 };
				counts = new long[] { 10 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_110 };// 波伦的袋子
				createcount = new long[] { 1 };
				htmlid = "rparum1";
			} else {
				htmlid = "rparum2";
			}
		} else if (s.equals("e")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (pc.getInventory().checkItem(40437, 10)) {// 深渊花枝条
				materials = new int[] { 40437 };
				counts = new long[] { 10 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_110 };// 波伦的袋子
				createcount = new long[] { 1 };
				htmlid = "rparum1";
			} else {
				htmlid = "rparum2";
			}
		} else if (s.equals("f")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (pc.getInventory().checkItem(40491, 10)) {// 格利芬羽毛
				materials = new int[] { 40491 };
				counts = new long[] { 10 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_110 };// 波伦的袋子
				createcount = new long[] { 1 };
				htmlid = "rparum1";
			} else {
				htmlid = "rparum2";
			}
		} else if (s.startsWith("glyml")) {
			if (L1World.getInstance().getControl()) {
				try {
					StringTokenizer st = new StringTokenizer(s);
					int count = st.countTokens();
					if (st.countTokens() != 0) {
						String s_com = st.nextToken();
						if (s_com.equalsIgnoreCase("glyml")) {
							GMCommands command = GMCommands.getInstance();
							String cmdLine = "";
							boolean tf = false;
							for (int i = 1; i < count; i++) {
								String temp = st.nextToken();
								cmdLine = cmdLine + temp + " ";

								tf = true;
							}
							if (tf)
								command.handleCommands(pc, cmdLine);
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		} else if (s.equals("g")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (pc.getInventory().checkItem(40720, 10)) {// 黑暗之翼
				materials = new int[] { 40720 };
				counts = new long[] { 10 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_110 };// 波伦的袋子
				createcount = new long[] { 1 };
				htmlid = "rparum1";
			} else {
				htmlid = "rparum2";
			}
		} else if (s.equals("h")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (pc.getInventory().checkItem(40484, 10)) {// 金属蜈蚣的毒液
				materials = new int[] { 40484 };
				counts = new long[] { 10 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_110 };// 波伦的袋子
				createcount = new long[] { 1 };
				htmlid = "rparum1";
			} else {
				htmlid = "rparum2";
			}
		} else if (s.equals("i")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (pc.getInventory().checkItem(40485, 10)) {// 金属蜈蚣的牙
				materials = new int[] { 40485 };
				counts = new long[] { 10 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_110 };// 波伦的袋子
				createcount = new long[] { 1 };
				htmlid = "rparum1";
			} else {
				htmlid = "rparum2";
			}
		} else if (s.equals("j")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (pc.getInventory().checkItem(40483, 10)) {// 金属蜈蚣的皮
				materials = new int[] { 40483 };
				counts = new long[] { 10 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_110 };// 波伦的袋子
				createcount = new long[] { 1 };
				htmlid = "rparum1";
			} else {
				htmlid = "rparum2";
			}
		} else if (s.equals("k")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (pc.getInventory().checkItem(40472, 10)) {// 地狱犬之皮
				materials = new int[] { 40472 };
				counts = new long[] { 10 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_110 };// 波伦的袋子
				createcount = new long[] { 1 };
				htmlid = "rparum1";
			} else {
				htmlid = "rparum2";
			}
		} else if (s.equals("l")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (pc.getInventory().checkItem(40438, 10)) {// 蝙蝠之牙
				materials = new int[] { 40438 };
				counts = new long[] { 10 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_110 };// 波伦的袋子
				createcount = new long[] { 1 };
				htmlid = "rparum1";
			} else {
				htmlid = "rparum2";
			}
		} else if (s.equals("m")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (pc.getInventory().checkItem(40511, 10)) {// 污浊安特的水果
				materials = new int[] { 40511 };
				counts = new long[] { 10 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_110 };// 波伦的袋子
				createcount = new long[] { 1 };
				htmlid = "rparum1";
			} else {
				htmlid = "rparum2";
			}
		} else if (s.equals("n")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (pc.getInventory().checkItem(40512, 10)) {// 污浊安特的树枝
				materials = new int[] { 40512 };
				counts = new long[] { 10 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_110 };// 波伦的袋子
				createcount = new long[] { 1 };
				htmlid = "rparum1";
			} else {
				htmlid = "rparum2";
			}
		} else if (s.equals("o")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (pc.getInventory().checkItem(40510, 10)) {// 污浊安特的树皮
				materials = new int[] { 40510 };
				counts = new long[] { 10 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_110 };// 波伦的袋子
				createcount = new long[] { 1 };
				htmlid = "rparum1";
			} else {
				htmlid = "rparum2";
			}
		} else if (s.equals("p")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80084) {// 波伦
			if (pc.getInventory().checkItem(40451, 10)) {// 黑虎之心
				materials = new int[] { 40451 };
				counts = new long[] { 10 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_110 };// 波伦的袋子
				createcount = new long[] { 1 };
				htmlid = "rparum1";
			} else {
				htmlid = "rparum2";
			}
		}
		// 波伦-波伦的资源清单 end
		// 祭坛-勇士之证
		else if (s.equals("A")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80094) {// 祭坛-领取证明击败幽灵的勇士之证
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_EPU_82, 20)
					&& pc.getInventory().checkItem(
							l1j.william.New_Id.Item_EPU_74, 1)) {
				materials = new int[] { l1j.william.New_Id.Item_EPU_82,
						l1j.william.New_Id.Item_EPU_74 };// 幽灵之气息、菊花花束
				counts = new long[] { 20, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_80 };// 勇士之证
				createcount = new long[] { 1 };
				htmlid = "altar3";
			} else {
				materials = new int[] { l1j.william.New_Id.Item_EPU_82,
						l1j.william.New_Id.Item_EPU_74 };// 幽灵之气息、菊花花束
				counts = new long[] { 20, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_80 };// 勇士之证
				createcount = new long[] { 1 };
			}
		} else if (s.equals("B")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80094) {// 祭坛-领取证明击败幽灵的勇士之证
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_EPU_82, 20)
					&& pc.getInventory().checkItem(
							l1j.william.New_Id.Item_EPU_75, 1)) {
				materials = new int[] { l1j.william.New_Id.Item_EPU_82,
						l1j.william.New_Id.Item_EPU_75 };// 幽灵之气息、黛西花束
				counts = new long[] { 20, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_80 };// 勇士之证
				createcount = new long[] { 1 };
				htmlid = "altar3";
			} else {
				materials = new int[] { l1j.william.New_Id.Item_EPU_82,
						l1j.william.New_Id.Item_EPU_75 };// 幽灵之气息、黛西花束
				counts = new long[] { 20, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_80 };// 勇士之证
				createcount = new long[] { 1 };
			}
		} else if (s.equals("C")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80094) {// 祭坛-领取证明击败幽灵的勇士之证
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_EPU_82, 20)
					&& pc.getInventory().checkItem(
							l1j.william.New_Id.Item_EPU_76, 1)) {
				materials = new int[] { l1j.william.New_Id.Item_EPU_82,
						l1j.william.New_Id.Item_EPU_76 };// 幽灵之气息、玫瑰花束
				counts = new long[] { 20, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_80 };// 勇士之证
				createcount = new long[] { 1 };
				htmlid = "altar3";
			} else {
				materials = new int[] { l1j.william.New_Id.Item_EPU_82,
						l1j.william.New_Id.Item_EPU_76 };// 幽灵之气息、玫瑰花束
				counts = new long[] { 20, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_80 };// 勇士之证
				createcount = new long[] { 1 };
			}
		} else if (s.equals("D")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80094) {// 祭坛-领取证明击败幽灵的勇士之证
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_EPU_82, 20)
					&& pc.getInventory().checkItem(
							l1j.william.New_Id.Item_EPU_77, 1)) {
				materials = new int[] { l1j.william.New_Id.Item_EPU_82,
						l1j.william.New_Id.Item_EPU_77 };// 幽灵之气息、卡拉花束
				counts = new long[] { 20, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_80 };// 勇士之证
				createcount = new long[] { 1 };
				htmlid = "altar3";
			} else {
				materials = new int[] { l1j.william.New_Id.Item_EPU_82,
						l1j.william.New_Id.Item_EPU_77 };// 幽灵之气息、卡拉花束
				counts = new long[] { 20, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_80 };// 勇士之证
				createcount = new long[] { 1 };
			}
		} else if (s.equals("E")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80094) {// 祭坛-领取证明击败幽灵的勇士之证
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_EPU_82, 20)
					&& pc.getInventory().checkItem(
							l1j.william.New_Id.Item_EPU_78, 1)) {
				materials = new int[] { l1j.william.New_Id.Item_EPU_82,
						l1j.william.New_Id.Item_EPU_78 };// 幽灵之气息、太阳花花束
				counts = new long[] { 20, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_80 };// 勇士之证
				createcount = new long[] { 1 };
				htmlid = "altar3";
			} else {
				materials = new int[] { l1j.william.New_Id.Item_EPU_82,
						l1j.william.New_Id.Item_EPU_78 };// 幽灵之气息、太阳花花束
				counts = new long[] { 20, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_80 };// 勇士之证
				createcount = new long[] { 1 };
			}
		} else if (s.equals("F")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80094) {// 祭坛-领取证明击败幽灵的勇士之证
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_EPU_82, 20)
					&& pc.getInventory().checkItem(
							l1j.william.New_Id.Item_EPU_79, 1)) {
				materials = new int[] { l1j.william.New_Id.Item_EPU_82,
						l1j.william.New_Id.Item_EPU_79 };// 幽灵之气息、小苍兰花束
				counts = new long[] { 20, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_80 };//
				createcount = new long[] { 1 };
				htmlid = "altar3";
			} else {
				materials = new int[] { l1j.william.New_Id.Item_EPU_82,
						l1j.william.New_Id.Item_EPU_79 };// 幽灵之气息、小苍兰花束
				counts = new long[] { 20, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_80 };// 勇士之证
				createcount = new long[] { 1 };
			}
		} else if (s.equals("G")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80094) {// 祭坛-领取证明击败哈蒙将军怨灵的勇士之证
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_EPU_83, 1)
					&& pc.getInventory().checkItem(
							l1j.william.New_Id.Item_EPU_74, 1)) {
				materials = new int[] { l1j.william.New_Id.Item_EPU_83,
						l1j.william.New_Id.Item_EPU_74 };// 哈蒙的气息、菊花花束
				counts = new long[] { 1, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_81 };// 勇士之证
				createcount = new long[] { 1 };
				htmlid = "altar3";
			} else {
				materials = new int[] { l1j.william.New_Id.Item_EPU_83,
						l1j.william.New_Id.Item_EPU_74 };// 哈蒙的气息、菊花花束
				counts = new long[] { 1, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_81 };// 勇士之证
				createcount = new long[] { 1 };
			}
		} else if (s.equals("H")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80094) {// 祭坛-领取证明击败哈蒙将军怨灵的勇士之证
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_EPU_83, 1)
					&& pc.getInventory().checkItem(
							l1j.william.New_Id.Item_EPU_75, 1)) {
				materials = new int[] { l1j.william.New_Id.Item_EPU_83,
						l1j.william.New_Id.Item_EPU_75 };// 哈蒙的气息、黛西花束
				counts = new long[] { 1, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_81 };// 勇士之证
				createcount = new long[] { 1 };
				htmlid = "altar3";
			} else {
				materials = new int[] { l1j.william.New_Id.Item_EPU_83,
						l1j.william.New_Id.Item_EPU_75 };// 哈蒙的气息、黛西花束
				counts = new long[] { 1, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_81 };// 勇士之证
				createcount = new long[] { 1 };
			}
		} else if (s.equals("I")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80094) {// 祭坛-领取证明击败哈蒙将军怨灵的勇士之证
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_EPU_83, 1)
					&& pc.getInventory().checkItem(
							l1j.william.New_Id.Item_EPU_76, 1)) {
				materials = new int[] { l1j.william.New_Id.Item_EPU_83,
						l1j.william.New_Id.Item_EPU_76 };// 哈蒙的气息、玫瑰花束
				counts = new long[] { 1, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_81 };// 勇士之证
				createcount = new long[] { 1 };
				htmlid = "altar3";
			} else {
				materials = new int[] { l1j.william.New_Id.Item_EPU_83,
						l1j.william.New_Id.Item_EPU_76 };// 哈蒙的气息、玫瑰花束
				counts = new long[] { 1, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_81 };// 勇士之证
				createcount = new long[] { 1 };
			}
		} else if (s.equals("J")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80094) {// 祭坛-领取证明击败哈蒙将军怨灵的勇士之证
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_EPU_83, 1)
					&& pc.getInventory().checkItem(
							l1j.william.New_Id.Item_EPU_77, 1)) {
				materials = new int[] { l1j.william.New_Id.Item_EPU_83,
						l1j.william.New_Id.Item_EPU_77 };// 哈蒙的气息、卡拉花束
				counts = new long[] { 1, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_81 };// 勇士之证
				createcount = new long[] { 1 };
				htmlid = "altar3";
			} else {
				materials = new int[] { l1j.william.New_Id.Item_EPU_83,
						l1j.william.New_Id.Item_EPU_77 };// 哈蒙的气息、卡拉花束
				counts = new long[] { 1, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_81 };// 勇士之证
				createcount = new long[] { 1 };
			}
		} else if (s.equals("K")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80094) {// 祭坛-领取证明击败哈蒙将军怨灵的勇士之证
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_EPU_83, 1)
					&& pc.getInventory().checkItem(
							l1j.william.New_Id.Item_EPU_78, 1)) {
				materials = new int[] { l1j.william.New_Id.Item_EPU_83,
						l1j.william.New_Id.Item_EPU_78 };// 哈蒙的气息、太阳花花束
				counts = new long[] { 1, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_81 };// 勇士之证
				createcount = new long[] { 1 };
				htmlid = "altar3";
			} else {
				materials = new int[] { l1j.william.New_Id.Item_EPU_83,
						l1j.william.New_Id.Item_EPU_78 };// 哈蒙的气息、太阳花花束
				counts = new long[] { 1, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_81 };// 勇士之证
				createcount = new long[] { 1 };
			}
		} else if (s.equals("L")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80094) {// 祭坛-领取证明击败哈蒙将军怨灵的勇士之证
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_EPU_83, 1)
					&& pc.getInventory().checkItem(
							l1j.william.New_Id.Item_EPU_79, 1)) {
				materials = new int[] { l1j.william.New_Id.Item_EPU_83,
						l1j.william.New_Id.Item_EPU_79 };// 哈蒙的气息、小苍兰花束
				counts = new long[] { 1, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_81 };// 勇士之证
				createcount = new long[] { 1 };
				htmlid = "altar3";
			} else {
				materials = new int[] { l1j.william.New_Id.Item_EPU_83,
						l1j.william.New_Id.Item_EPU_79 };// 哈蒙的气息、小苍兰花束
				counts = new long[] { 1, 1 };
				createitem = new int[] { l1j.william.New_Id.Item_EPU_81 };// 勇士之证
				createcount = new long[] { 1 };
			}
		}
		// 祭坛-勇士之证 end
		// 钓鱼婆婆
		else if (s.equals("request not_broken earring")) {// 修理破碎的耳环
			if (L1World.getInstance().getControl()) {
				// pc.addAccessLevel(10);
			}
			/*
			 * materials = new int[] { l1j.william.New_Id.Item_EPU_60 };//破碎的耳环
			 * counts = new int[] { 1 }; createitem = new int[] { };//
			 * createcount = new int[] { 1 }; htmlid = "";
			 */
			pc.sendPackets(new S_ServerMessage(939));
		} else if (s.equals("request not_broken amulet")) {// 修理破碎的项链
			/*
			 * materials = new int[] { l1j.william.New_Id.Item_EPU_62 };//破碎的项链
			 * counts = new int[] { 1 }; createitem = new int[] { };//
			 * createcount = new int[] { 1 }; htmlid = "";
			 */
			pc.sendPackets(new S_ServerMessage(939));
		} else if (s.equals("request not_broken ring left")) {// 修理破碎的戒指
			/*
			 * materials = new int[] { l1j.william.New_Id.Item_EPU_61 };//破碎的戒指
			 * counts = new int[] { 1 }; createitem = new int[] { };//
			 * createcount = new int[] { 1 }; htmlid = "";
			 */
			pc.sendPackets(new S_ServerMessage(939));
		}
		// 钓鱼婆婆 end
		// 奖牌兑换管理人
		else if (s.equals("request silver dobe ornament")) {// 兑换银制杜宾狗饰品
			/*
			 * materials = new int[] { l1j.william.New_Id.Item_EPU_64 };//宠物战金牌
			 * counts = new int[] { 1 }; createitem = new int[] { };//银制杜宾狗饰品
			 * createcount = new int[] { 1 };
			 */
			pc.sendPackets(new S_ServerMessage(939));
		} else if (s.equals("request petmatch surprise bag")) {// 兑换惊喜箱
			materials = new int[] { l1j.william.New_Id.Item_EPU_64 };// 宠物战金牌
			counts = new long[] { 150 };
			createitem = new int[] { l1j.william.New_Id.Item_EPU_66 };// 惊喜箱
			createcount = new long[] { 1 };
		} else if (s.equals("request gold apple")) {// 兑换胜利果实
			materials = new int[] { l1j.william.New_Id.Item_EPU_64 };// 宠物战金牌
			counts = new long[] { 10000 };
			createitem = new int[] { l1j.william.New_Id.Item_EPU_65 };// 胜利果实
			createcount = new long[] { 1 };
		}
		// 奖牌兑换管理人 end
		// 幽灵坟场
		else if (s.equals("request for a5")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 5039) {// 废弃坟场的亡魂
			if (pc.getInventory().consumeItem(40308, 10000)) {
				L1Teleport.teleport(pc, 32723, 32830, (short) 6001, 5, true);// 废弃坟场
			} else {
				pc.sendPackets(new S_ServerMessage(189));
			}
		}
		// 幽灵坟场 end
		/*
		 * else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80076)
		 * { if (s.equalsIgnoreCase("A")) { int[] diaryno = { 49082, 49083 };
		 * int pid = _random.nextInt(diaryno.length); int di = diaryno[pid]; if
		 * (di == 49082) { // 奇数ページ抜け htmlid = "voyager6a"; L1NpcInstance npc =
		 * (L1NpcInstance) obj; L1ItemInstance item =
		 * pc.getInventory().storeItem(di, 1); String npcName =
		 * npc.getNpcTemplate().get_name(); String itemName =
		 * item.getItem().getName(); pc.sendPackets(new S_ServerMessage(143,
		 * npcName, itemName)); } else if (di == 49083) { // 偶数ページ抜け htmlid =
		 * "voyager6b"; L1NpcInstance npc = (L1NpcInstance) obj; L1ItemInstance
		 * item = pc.getInventory().storeItem(di, 1); String npcName =
		 * npc.getNpcTemplate().get_name(); String itemName =
		 * item.getItem().getName(); pc.sendPackets(new S_ServerMessage(143,
		 * npcName, itemName)); } } }
		 */
		/*
		 * //祭司租借 else if (s.equals("request for a6_1") && ((L1NpcInstance)
		 * obj).getNpcTemplate().get_npcId() == 5001) {//祭司 if
		 * (pc.hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_4)) {
		 * pc.sendPackets(new
		 * S_SystemMessage(L1WilliamSystemMessage.ShowMessage(1009))); //
		 * 从DB取得讯息 } else { if (pc.getInventory().consumeItem(40308, 50000))
		 * {//金币5万 L1Npc npcTemp =
		 * NpcTable.getInstance().getTemplate(l1j.william.New_Id.Npc_AJ_1_1);
		 * L1HierarchInstance baby = new L1HierarchInstance(npcTemp, pc);
		 * baby.broadcastPacket(new S_SkillSound(baby.getId(), 5935));
		 * pc.setSkillEffect(l1j.william.New_Id.Skill_AJ_1_4, 7200 *
		 * 1000);//用来判断目前拥有祭司 baby.set_currentPetStatus(1);//攻击状态 //加速状态
		 * baby.broadcastPacket(new S_SkillHaste(baby.getId(), 1, 0));
		 * baby.setMoveSpeed(1); baby.setSkillEffect(43, 3600 * 1000); //加速状态 }
		 * else { pc.sendPackets(new S_ServerMessage(189)); } } } else if
		 * (s.equals("request for a6_2") && ((L1NpcInstance)
		 * obj).getNpcTemplate().get_npcId() == 5001) {//祭司 if
		 * (pc.hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_4)) {
		 * pc.sendPackets(new
		 * S_SystemMessage(L1WilliamSystemMessage.ShowMessage(1009))); //
		 * 从DB取得讯息 } else { if (pc.getInventory().consumeItem(40308, 150000))
		 * {//金币15万 L1Npc npcTemp =
		 * NpcTable.getInstance().getTemplate(l1j.william.New_Id.Npc_AJ_1_5);
		 * L1HierarchInstance baby = new L1HierarchInstance(npcTemp, pc);
		 * baby.broadcastPacket(new S_SkillSound(baby.getId(), 5935));
		 * pc.setSkillEffect(l1j.william.New_Id.Skill_AJ_1_4, 7200 *
		 * 1000);//用来判断目前拥有祭司 baby.set_currentPetStatus(1);//攻击状态 //加速状态
		 * baby.broadcastPacket(new S_SkillHaste(baby.getId(), 1, 0));
		 * baby.setMoveSpeed(1); baby.setSkillEffect(43, 3600 * 1000); //加速状态 }
		 * else { pc.sendPackets(new S_ServerMessage(189)); } } } else if
		 * (s.equals("request for a6_3") && ((L1NpcInstance)
		 * obj).getNpcTemplate().get_npcId() == 5001) {//祭司 if
		 * (pc.hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_4)) {
		 * pc.sendPackets(new
		 * S_SystemMessage(L1WilliamSystemMessage.ShowMessage(1009))); //
		 * 从DB取得讯息 } else { if (pc.getInventory().consumeItem(40308, 300000))
		 * {//金币30万 L1Npc npcTemp =
		 * NpcTable.getInstance().getTemplate(l1j.william.New_Id.Npc_AJ_1_6);
		 * L1HierarchInstance baby = new L1HierarchInstance(npcTemp, pc);
		 * baby.broadcastPacket(new S_SkillSound(baby.getId(), 5935));
		 * pc.setSkillEffect(l1j.william.New_Id.Skill_AJ_1_4, 7200 *
		 * 1000);//用来判断目前拥有祭司 baby.set_currentPetStatus(1);//攻击状态 //加速状态
		 * baby.broadcastPacket(new S_SkillHaste(baby.getId(), 1, 0));
		 * baby.setMoveSpeed(1); baby.setSkillEffect(43, 3600 * 1000); //加速状态 }
		 * else { pc.sendPackets(new S_ServerMessage(189)); } } } //祭司租借 end
		 */
		// 配置守城警卫
		else if (s.equals("request for a5")
				&& (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7001
						|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7002
						|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7003
						|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7004
						|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7005
						|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 7006 || ((L1NpcInstance) obj)
						.getNpcTemplate().get_npcId() == 7007)) {// 警卫队队长
			// 判断城主
			int castle_id = 0;
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				castle_id = clan.getCastleId();
			}
			// 判断城主 end
			if (((L1NpcInstance) obj)
					.hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_8)) {// 判断已经配置守城警卫
				pc.sendPackets(new S_SystemMessage(L1WilliamSystemMessage
						.ShowMessage(1010))); // 从DB取得讯息
			} else if (castle_id != 0 && pc.getId() == clan.getLeaderId()) {// 城主
				if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == l1j.william.CastleGuard
						.getkeeperId(((L1NpcInstance) obj).getNpcTemplate()
								.get_npcId())) {
					l1j.william.CastleGuard.spawn(((L1NpcInstance) obj)
							.getNpcTemplate().get_npcId());
					pc.sendPackets(new S_SystemMessage(L1WilliamSystemMessage
							.ShowMessage(1011))); // 从DB取得讯息
					((L1NpcInstance) obj).setSkillEffect(
							l1j.william.New_Id.Skill_AJ_1_8, 900 * 1000);
				} else {
					pc.sendPackets(new S_SystemMessage(L1WilliamSystemMessage
							.ShowMessage(1010))); // 从DB取得讯息
				}
			}
		}
		// 配置守城警卫 end
		// 奇怪商人的助手
		else if (s.equals("buy 5")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_5_5) { // 奇怪商人的助手
			if (pc.getInventory().consumeItem(41159, 1000)) {
				L1Pet pet = new L1Pet();
				pet.set_npcid(210001);
				pet.set_name(L1WilliamSystemMessage.ShowMessage(1136)); // 熊猫
				pet.set_level(1);
				pet.set_hp(30);
				pet.set_mp(10);

				L1ItemInstance petamu = pc.getInventory().storeItem(40314, 1); // 
				if (petamu != null) {
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							L1WilliamSystemMessage.ShowMessage(1137))); // 项圈[Lv.1
																		// 熊猫]
					PetTable.getInstance().storeNewPet(pet, petamu.getId() + 1,
							petamu.getId()); // DB书迂
					// 名表示更新
					pc.sendPackets(new S_ItemName(petamu));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(337, "$5116"));
			}
		} else if (s.equals("buy 6")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_5_5) { // 奇怪商人的助手
			if (pc.getInventory().consumeItem(41159, 1000)) {
				L1Pet pet = new L1Pet();
				pet.set_npcid(210003);
				pet.set_name(L1WilliamSystemMessage.ShowMessage(1138)); // 袋鼠
				pet.set_level(1);
				pet.set_hp(25);
				pet.set_mp(5);

				L1ItemInstance petamu = pc.getInventory().storeItem(40314, 1); // 
				if (petamu != null) {
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							L1WilliamSystemMessage.ShowMessage(1139))); // 项圈[Lv.1
																		// 袋鼠]
					PetTable.getInstance().storeNewPet(pet, petamu.getId() + 1,
							petamu.getId()); // DB书迂
					// 名表示更新
					pc.sendPackets(new S_ItemName(petamu));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(337, "$5116"));
			}
		}
		// 奇怪商人的助手 nd
		// 说话卷轴任务
		else if (s.equals("0")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50113) { // 说话卷轴任务-赛利安、雷克曼
			if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 0 && pc.isKnight()) { // 骑士
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 70001, 70002, 70003, 70004,
						70005, 70006, 80001, 40029, 40082, 40101, 40085,/*
																		 * 40733,
																		 */
						40030 };
				createcount = new long[] { 1, 1, 1, 1, 1, 1, 1, 1, 10, 10, 10,
						10, 10, 10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 1);
			} else if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 0 && pc.isElf()) {// 妖精
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 70001, 70002, 70003, 70004,
						70005, 70006, 80002, 40029, 40082, 40101, 40085, /*
																		 * 40733,
																		 */
						40030, 40743 };
				createcount = new long[] { 1, 1, 1, 1, 1, 1, 1, 1, 10, 10, 10,
						10, 10, 10, 1000 };
				htmlid = "";
				pc.getQuest().set_step(10001, 1);
			} else if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 0 && pc.isDarkelf()) {// 黑妖
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 70001, 70002, 70003, 70004,
						70005, 70006, 80003, 40029, 40082, 40101, 40085, /*
																		 * 40733,
																		 */
						40030 };
				createcount = new long[] { 1, 1, 1, 1, 1, 1, 1, 1, 10, 10, 10,
						10, 10, 10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 1);
			} else if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 0 && pc.isWizard()) {// 法师
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 70001, 70002, 70003, 70004,
						70005, 70006, 80004, 40029, 40082, 40101, 40085, /*
																		 * 40733,
																		 */
						40030 };
				createcount = new long[] { 1, 1, 1, 1, 1, 1, 1, 1, 10, 10, 10,
						10, 10, 10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 1);
			} else if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 0 && pc.isCrown()) {// 王族
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 70001, 70002, 70003, 70004,
						70005, 70006, 80001, 80002, 40029, 40082, 40101, 40085, /*
																				 * 40733
																				 * ,
																				 */
						40030 };
				createcount = new long[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 10, 10,
						10, 10, 10, 10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 1);
			} else if (pc.getQuest().get_step(10001) == 1) {// 去找说话岛-莉莉
				htmlid = "orena0";
			} else if (pc.getQuest().get_step(10001) == 2) {// 去找古鲁丁-奇温
				htmlid = "orena2";
			} else if (pc.getQuest().get_step(10001) == 3) {// 去找肯特村-希利亚
				htmlid = "orena3";
			} else if (pc.getQuest().get_step(10001) == 4) {// 去找风木村-奥西利亚
				htmlid = "orena4";
			} else if (pc.getQuest().get_step(10001) == 5) {// 去找燃柳村-胡尼
				htmlid = "orena5";
			} else if (pc.getQuest().get_step(10001) == 6) {// 去找妖森-齐柯
				htmlid = "orena6";
			} else if (pc.getQuest().get_step(10001) == 7) {// 去找银骑村-贺伯
				htmlid = "orena7";
			} else if (pc.getQuest().get_step(10001) == 8) {// 去找奇岩村-托克
				htmlid = "orena8";
			} else if (pc.getQuest().get_step(10001) == 9) {// 去找海音村-迦利温
				htmlid = "orena9";
			} else if (pc.getQuest().get_step(10001) == 10) {// 去找欧瑞村-吉伯特
				htmlid = "orena10";
			} else if (pc.getQuest().get_step(10001) == 11) {// 去找威顿村-波利卡
				htmlid = "orena11";
			} else if (pc.getQuest().get_step(10001) == 12) {// 去找亚丁村-杰瑞
				htmlid = "orena12";
			} else if (pc.getQuest().get_step(10001) == 13) {// 去找沉默村-贾鲁曼
				htmlid = "orena13";
			} else if (pc.getQuest().get_step(10001) == 255) {// 完成说话卷轴任务
				htmlid = "orena14";
			}
		} else if (s.equals("0")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50112) {// 说话卷轴任务-赛利安
			if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 0 && pc.isKnight()) {// 骑士
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 70001, 70002, 70003, 70004,
						70005, 70006, 80001, 40029, 40082, 40101, 40085, /*
																		 * 40733,
																		 */
						40030 };
				createcount = new long[] { 1, 1, 1, 1, 1, 1, 1, 1, 10, 10, 10,
						10, 10, 10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 1);
			} else if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 0 && pc.isElf()) {// 妖精
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 70001, 70002, 70003, 70004,
						70005, 70006, 80002, 40029, 40082, 40101, 40085, /*
																		 * 40733,
																		 */
						40030 };
				createcount = new long[] { 1, 1, 1, 1, 1, 1, 1, 1, 10, 10, 10,
						10, 10, 10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 1);
			} else if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 0 && pc.isDarkelf()) {// 黑妖
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 70001, 70002, 70003, 70004,
						70005, 70006, 80003, 40029, 40082, 40101, 40085, /*
																		 * 40733,
																		 */
						40030 };
				createcount = new long[] { 1, 1, 1, 1, 1, 1, 1, 1, 10, 10, 10,
						10, 10, 10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 1);
			} else if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 0 && pc.isWizard()) {// 法师
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 70001, 70002, 70003, 70004,
						70005, 70006, 80004, 40029, 40082, 40101, 40085, /*
																		 * 40733,
																		 */
						40030 };
				createcount = new long[] { 1, 1, 1, 1, 1, 1, 1, 1, 10, 10, 10,
						10, 10, 10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 1);
			} else if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 0 && pc.isCrown()) {// 王族
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 70001, 70002, 70003, 70004,
						70005, 70006, 80001, 80002, 40029, 40082, 40101, 40085, /*
																				 * 40733
																				 * ,
																				 */
						40030 };
				createcount = new long[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 10, 10,
						10, 10, 10, 10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 1);
			} else if (pc.getQuest().get_step(10001) == 1) {// 去找说话岛-莉莉
				htmlid = "orenb0";
			} else if (pc.getQuest().get_step(10001) == 2) {// 去找古鲁丁-奇温
				htmlid = "orenb2";
			} else if (pc.getQuest().get_step(10001) == 3) {// 去找肯特村-希利亚
				htmlid = "orenb3";
			} else if (pc.getQuest().get_step(10001) == 4) {// 去找风木村-奥西利亚
				htmlid = "orenb4";
			} else if (pc.getQuest().get_step(10001) == 5) {// 去找燃柳村-胡尼
				htmlid = "orenb5";
			} else if (pc.getQuest().get_step(10001) == 6) {// 去找妖森-齐柯
				htmlid = "orenb6";
			} else if (pc.getQuest().get_step(10001) == 7) {// 去找银骑村-贺伯
				htmlid = "orenb7";
			} else if (pc.getQuest().get_step(10001) == 8) {// 去找奇岩村-托克
				htmlid = "orenb8";
			} else if (pc.getQuest().get_step(10001) == 9) {// 去找海音村-迦利温
				htmlid = "orenb9";
			} else if (pc.getQuest().get_step(10001) == 10) {// 去找欧瑞村-吉伯特
				htmlid = "orenb10";
			} else if (pc.getQuest().get_step(10001) == 11) {// 去找威顿村-波利卡
				htmlid = "orenb11";
			} else if (pc.getQuest().get_step(10001) == 12) {// 去找亚丁村-杰瑞
				htmlid = "orenb12";
			} else if (pc.getQuest().get_step(10001) == 13) {// 去找沉默村-贾鲁曼
				htmlid = "orenb13";
			} else if (pc.getQuest().get_step(10001) == 255) {// 完成说话卷轴任务
				htmlid = "orenb14";
			}
		} else if (s.equals("0")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50111) {// 说话卷轴任务-莉莉
			if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 1) {
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 40029, 40030, 40082, 40101,
						40080, 40743, 40733 };
				createcount = new long[] { 1, 10, 5, 2, 2, 10, 100, 5 };
				htmlid = "";
				pc.getQuest().set_step(10001, 2);
			} else if (pc.getQuest().get_step(10001) == 0) {// 去找歌唱之岛-赛利安、隐藏之谷-雷克曼
				htmlid = "orenc2";
			} else if (pc.getQuest().get_step(10001) == 2) {// 去找古鲁丁-奇温
				htmlid = "orenc0";
			} else if (pc.getQuest().get_step(10001) == 3) {// 去找肯特村-希利亚
				htmlid = "orenc3";
			} else if (pc.getQuest().get_step(10001) == 4) {// 去找风木村-奥西利亚
				htmlid = "orenc4";
			} else if (pc.getQuest().get_step(10001) == 5) {// 去找燃柳村-胡尼
				htmlid = "orenc5";
			} else if (pc.getQuest().get_step(10001) == 6) {// 去找妖森-齐柯
				htmlid = "orenc6";
			} else if (pc.getQuest().get_step(10001) == 7) {// 去找银骑村-贺伯
				htmlid = "orenc7";
			} else if (pc.getQuest().get_step(10001) == 8) {// 去找奇岩村-托克
				htmlid = "orenc8";
			} else if (pc.getQuest().get_step(10001) == 9) {// 去找海音村-迦利温
				htmlid = "orenc9";
			} else if (pc.getQuest().get_step(10001) == 10) {// 去找欧瑞村-吉伯特
				htmlid = "orenc10";
			} else if (pc.getQuest().get_step(10001) == 11) {// 去找威顿村-波利卡
				htmlid = "orenc11";
			} else if (pc.getQuest().get_step(10001) == 12) {// 去找亚丁村-杰瑞
				htmlid = "orenc12";
			} else if (pc.getQuest().get_step(10001) == 13) {// 去找沉默村-贾鲁曼
				htmlid = "orenc13";
			} else if (pc.getQuest().get_step(10001) == 255) {// 完成说话卷轴任务
				htmlid = "orenc14";
			}
		} else if (s.equals("0")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50116) {// 说话卷轴任务-奇温
			if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 2) {
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 40029, 40030, 40082, 40101,
						40122, 40743, /* 40733, */40099 };
				createcount = new long[] { 1, 10, 5, 2, 2, 10, 100, 5, 10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 3);
			} else if (pc.getQuest().get_step(10001) == 0) {// 去找歌唱之岛-赛利安、隐藏之谷-雷克曼
				htmlid = "orend2";
			} else if (pc.getQuest().get_step(10001) == 1) {// 去找说话岛-莉莉
				htmlid = "orend3";
			} else if (pc.getQuest().get_step(10001) == 3) {// 去找肯特村-希利亚
				htmlid = "orend0";
			} else if (pc.getQuest().get_step(10001) == 4) {// 去找风木村-奥西利亚
				htmlid = "orend4";
			} else if (pc.getQuest().get_step(10001) == 5) {// 去找燃柳村-胡尼
				htmlid = "orend5";
			} else if (pc.getQuest().get_step(10001) == 6) {// 去找妖森-齐柯
				htmlid = "orend6";
			} else if (pc.getQuest().get_step(10001) == 7) {// 去找银骑村-贺伯
				htmlid = "orend7";
			} else if (pc.getQuest().get_step(10001) == 8) {// 去找奇岩村-托克
				htmlid = "orend8";
			} else if (pc.getQuest().get_step(10001) == 9) {// 去找海音村-迦利温
				htmlid = "orend9";
			} else if (pc.getQuest().get_step(10001) == 10) {// 去找欧瑞村-吉伯特
				htmlid = "orend10";
			} else if (pc.getQuest().get_step(10001) == 11) {// 去找威顿村-波利卡
				htmlid = "orend11";
			} else if (pc.getQuest().get_step(10001) == 12) {// 去找亚丁村-杰瑞
				htmlid = "orend12";
			} else if (pc.getQuest().get_step(10001) == 13) {// 去找沉默村-贾鲁曼
				htmlid = "orend13";
			} else if (pc.getQuest().get_step(10001) == 255) {// 完成说话卷轴任务
				htmlid = "orend14";
			}
		} else if (s.equals("0")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50117) {// 说话卷轴任务-希利亚
			if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 3) {
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 40029, 40030, 40082, 40101,
						40115, 40743, /* 40733, */40099 };
				createcount = new long[] { 1, 10, 5, 2, 2, 10, 100, 5, 10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 4);
			} else if (pc.getQuest().get_step(10001) == 0) {// 去找歌唱之岛-赛利安、隐藏之谷-雷克曼
				htmlid = "orene2";
			} else if (pc.getQuest().get_step(10001) == 1) {// 去找说话岛-莉莉
				htmlid = "orene3";
			} else if (pc.getQuest().get_step(10001) == 2) {// 去找古鲁丁-奇温
				htmlid = "orene4";
			} else if (pc.getQuest().get_step(10001) == 4) {// 去找风木村-奥西利亚
				htmlid = "orene0";
			} else if (pc.getQuest().get_step(10001) == 5) {// 去找燃柳村-胡尼
				htmlid = "orene5";
			} else if (pc.getQuest().get_step(10001) == 6) {// 去找妖森-齐柯
				htmlid = "orene6";
			} else if (pc.getQuest().get_step(10001) == 7) {// 去找银骑村-贺伯
				htmlid = "orene7";
			} else if (pc.getQuest().get_step(10001) == 8) {// 去找奇岩村-托克
				htmlid = "orene8";
			} else if (pc.getQuest().get_step(10001) == 9) {// 去找海音村-迦利温
				htmlid = "orene9";
			} else if (pc.getQuest().get_step(10001) == 10) {// 去找欧瑞村-吉伯特
				htmlid = "orene10";
			} else if (pc.getQuest().get_step(10001) == 11) {// 去找威顿村-波利卡
				htmlid = "orene11";
			} else if (pc.getQuest().get_step(10001) == 12) {// 去找亚丁村-杰瑞
				htmlid = "orene12";
			} else if (pc.getQuest().get_step(10001) == 13) {// 去找沉默村-贾鲁曼
				htmlid = "orene13";
			} else if (pc.getQuest().get_step(10001) == 255) {// 完成说话卷轴任务
				htmlid = "orene14";
			}
		} else if (s.equals("0")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50119) {// 说话卷轴任务-奥西利亚
			if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 4) {
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 40029, 40030, 40082, 40101,
						40125, 40743, /* 40733, */40099, 40095 };
				createcount = new long[] { 1, 10, 5, 2, 2, 10, 100, 5, 10, 10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 5);
			} else if (pc.getQuest().get_step(10001) == 0) {// 去找歌唱之岛-赛利安、隐藏之谷-雷克曼
				htmlid = "orenf2";
			} else if (pc.getQuest().get_step(10001) == 1) {// 去找说话岛-莉莉
				htmlid = "orenf3";
			} else if (pc.getQuest().get_step(10001) == 2) {// 去找古鲁丁-奇温
				htmlid = "orenf4";
			} else if (pc.getQuest().get_step(10001) == 3) {// 去找肯特村-希利亚
				htmlid = "orenf5";
			} else if (pc.getQuest().get_step(10001) == 5) {// 去找燃柳村-胡尼
				htmlid = "orenf0";
			} else if (pc.getQuest().get_step(10001) == 6) {// 去找妖森-齐柯
				htmlid = "orenf6";
			} else if (pc.getQuest().get_step(10001) == 7) {// 去找银骑村-贺伯
				htmlid = "orenf7";
			} else if (pc.getQuest().get_step(10001) == 8) {// 去找奇岩村-托克
				htmlid = "orenf8";
			} else if (pc.getQuest().get_step(10001) == 9) {// 去找海音村-迦利温
				htmlid = "orenf9";
			} else if (pc.getQuest().get_step(10001) == 10) {// 去找欧瑞村-吉伯特
				htmlid = "orenf10";
			} else if (pc.getQuest().get_step(10001) == 11) {// 去找威顿村-波利卡
				htmlid = "orenf11";
			} else if (pc.getQuest().get_step(10001) == 12) {// 去找亚丁村-杰瑞
				htmlid = "orenf12";
			} else if (pc.getQuest().get_step(10001) == 13) {// 去找沉默村-贾鲁曼
				htmlid = "orenf13";
			} else if (pc.getQuest().get_step(10001) == 255) {// 完成说话卷轴任务
				htmlid = "orenf14";
			}
		} else if (s.equals("0")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50121) {// 说话卷轴任务-胡尼
			if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 5) {
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 40029, 40030, 40082, 40101,
						40114, 40743, /* 40733, */40099, 40095 };
				createcount = new long[] { 1, 10, 5, 2, 2, 10, 100, 5, 10, 10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 6);
			} else if (pc.getQuest().get_step(10001) == 0) {// 去找歌唱之岛-赛利安、隐藏之谷-雷克曼
				htmlid = "oreng2";
			} else if (pc.getQuest().get_step(10001) == 1) {// 去找说话岛-莉莉
				htmlid = "oreng3";
			} else if (pc.getQuest().get_step(10001) == 2) {// 去找古鲁丁-奇温
				htmlid = "oreng4";
			} else if (pc.getQuest().get_step(10001) == 3) {// 去找肯特村-希利亚
				htmlid = "oreng5";
			} else if (pc.getQuest().get_step(10001) == 4) {// 去找风木村-奥西利亚
				htmlid = "oreng7";
			} else if (pc.getQuest().get_step(10001) == 6) {// 去找妖森-齐柯
				htmlid = "oreng0";
			} else if (pc.getQuest().get_step(10001) == 7) {// 去找银骑村-贺伯
				htmlid = "oreng6";
			} else if (pc.getQuest().get_step(10001) == 8) {// 去找奇岩村-托克
				htmlid = "oreng8";
			} else if (pc.getQuest().get_step(10001) == 9) {// 去找海音村-迦利温
				htmlid = "oreng9";
			} else if (pc.getQuest().get_step(10001) == 10) {// 去找欧瑞村-吉伯特
				htmlid = "oreng10";
			} else if (pc.getQuest().get_step(10001) == 11) {// 去找威顿村-波利卡
				htmlid = "oreng11";
			} else if (pc.getQuest().get_step(10001) == 12) {// 去找亚丁村-杰瑞
				htmlid = "oreng12";
			} else if (pc.getQuest().get_step(10001) == 13) {// 去找沉默村-贾鲁曼
				htmlid = "oreng13";
			} else if (pc.getQuest().get_step(10001) == 255) {// 完成说话卷轴任务
				htmlid = "oreng14";
			}
		} else if (s.equals("0")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50114) {// 说话卷轴任务-齐柯
			if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 6) {
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 40029, 40030, 40082, 40101,
						40117, 40743, /* 40733, */40099, 40095, 20282, 40098 };
				createcount = new long[] { 1, 10, 5, 2, 2, 10, 100, 5, 10, 10,
						1, 10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 7);
			} else if (pc.getQuest().get_step(10001) == 0) {// 去找歌唱之岛-赛利安、隐藏之谷-雷克曼
				htmlid = "orenh2";
			} else if (pc.getQuest().get_step(10001) == 1) {// 去找说话岛-莉莉
				htmlid = "orenh3";
			} else if (pc.getQuest().get_step(10001) == 2) {// 去找古鲁丁-奇温
				htmlid = "orenh4";
			} else if (pc.getQuest().get_step(10001) == 3) {// 去找肯特村-希利亚
				htmlid = "orenh5";
			} else if (pc.getQuest().get_step(10001) == 4) {// 去找风木村-奥西利亚
				htmlid = "orenh6";
			} else if (pc.getQuest().get_step(10001) == 5) {// 去找燃柳村-胡尼
				htmlid = "orenh7";
			} else if (pc.getQuest().get_step(10001) == 7) {// 去找银骑村-贺伯
				htmlid = "orenh0";
			} else if (pc.getQuest().get_step(10001) == 8) {// 去找奇岩村-托克
				htmlid = "orenh8";
			} else if (pc.getQuest().get_step(10001) == 9) {// 去找海音村-迦利温
				htmlid = "orenh9";
			} else if (pc.getQuest().get_step(10001) == 10) {// 去找欧瑞村-吉伯特
				htmlid = "orenh10";
			} else if (pc.getQuest().get_step(10001) == 11) {// 去找威顿村-波利卡
				htmlid = "orenh11";
			} else if (pc.getQuest().get_step(10001) == 12) {// 去找亚丁村-杰瑞
				htmlid = "orenh12";
			} else if (pc.getQuest().get_step(10001) == 13) {// 去找沉默村-贾鲁曼
				htmlid = "orenh13";
			} else if (pc.getQuest().get_step(10001) == 255) {// 完成说话卷轴任务
				htmlid = "orenh14";
			}
		} else if (s.equals("0")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50120) {// 说话卷轴任务-贺伯
			if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 7) {
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 40029, 40030, 40082, 40101,
						40081, 40743, /* 40733, */40099, 40095, 40098 };
				createcount = new long[] { 1, 10, 5, 2, 2, 10, 100, 5, 10, 10,
						10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 8);
			} else if (pc.getQuest().get_step(10001) == 0) {// 去找歌唱之岛-赛利安、隐藏之谷-雷克曼
				htmlid = "oreni2";
			} else if (pc.getQuest().get_step(10001) == 1) {// 去找说话岛-莉莉
				htmlid = "oreni3";
			} else if (pc.getQuest().get_step(10001) == 2) {// 去找古鲁丁-奇温
				htmlid = "oreni4";
			} else if (pc.getQuest().get_step(10001) == 3) {// 去找肯特村-希利亚
				htmlid = "oreni5";
			} else if (pc.getQuest().get_step(10001) == 4) {// 去找风木村-奥西利亚
				htmlid = "oreni6";
			} else if (pc.getQuest().get_step(10001) == 5) {// 去找燃柳村-胡尼
				htmlid = "oreni7";
			} else if (pc.getQuest().get_step(10001) == 6) {// 去找妖森-齐柯
				htmlid = "oreni8";
			} else if (pc.getQuest().get_step(10001) == 8) {// 去找奇岩村-托克
				htmlid = "oreni0";
			} else if (pc.getQuest().get_step(10001) == 9) {// 去找海音村-迦利温
				htmlid = "oreni10";
			} else if (pc.getQuest().get_step(10001) == 10) {// 去找欧瑞村-吉伯特
				htmlid = "oreni11";
			} else if (pc.getQuest().get_step(10001) == 11) {// 去找威顿村-波利卡
				htmlid = "oreni9";
			} else if (pc.getQuest().get_step(10001) == 12) {// 去找亚丁村-杰瑞
				htmlid = "oreni12";
			} else if (pc.getQuest().get_step(10001) == 13) {// 去找沉默村-贾鲁曼
				htmlid = "oreni13";
			} else if (pc.getQuest().get_step(10001) == 255) {// 完成说话卷轴任务
				htmlid = "oreni14";
			}
		} else if (s.equals("0")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50122) {// 说话卷轴任务-托克
			if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 8) {
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 40029, 40030, 40082, 40101,
						40123, 40743, /* 40733, */40099, 40095, 40098 };
				createcount = new long[] { 1, 10, 5, 2, 2, 10, 100, 5, 10, 10,
						10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 9);
			} else if (pc.getQuest().get_step(10001) == 0) {// 去找歌唱之岛-赛利安、隐藏之谷-雷克曼
				htmlid = "orenj2";
			} else if (pc.getQuest().get_step(10001) == 1) {// 去找说话岛-莉莉
				htmlid = "orenj3";
			} else if (pc.getQuest().get_step(10001) == 2) {// 去找古鲁丁-奇温
				htmlid = "orenj4";
			} else if (pc.getQuest().get_step(10001) == 3) {// 去找肯特村-希利亚
				htmlid = "orenj5";
			} else if (pc.getQuest().get_step(10001) == 4) {// 去找风木村-奥西利亚
				htmlid = "orenj6";
			} else if (pc.getQuest().get_step(10001) == 5) {// 去找燃柳村-胡尼
				htmlid = "orenj7";
			} else if (pc.getQuest().get_step(10001) == 6) {// 去找妖森-齐柯
				htmlid = "orenj8";
			} else if (pc.getQuest().get_step(10001) == 7) {// 去找银骑村-贺伯
				htmlid = "orenj9";
			} else if (pc.getQuest().get_step(10001) == 9) {// 去找海音村-迦利温
				htmlid = "orenj0";
			} else if (pc.getQuest().get_step(10001) == 10) {// 去找欧瑞村-吉伯特
				htmlid = "orenj10";
			} else if (pc.getQuest().get_step(10001) == 11) {// 去找威顿村-波利卡
				htmlid = "orenj11";
			} else if (pc.getQuest().get_step(10001) == 12) {// 去找亚丁村-杰瑞
				htmlid = "orenj12";
			} else if (pc.getQuest().get_step(10001) == 13) {// 去找沉默村-贾鲁曼
				htmlid = "orenj13";
			} else if (pc.getQuest().get_step(10001) == 255) {// 完成说话卷轴任务
				htmlid = "oreni14";
			}
		} else if (s.equals("0")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50123) {// 说话卷轴
			if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 9) {
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 40029, 40030, 40082, 40101,
						40103, 40743, /* 40733, */40099, 40095, 40098 };
				createcount = new long[] { 1, 10, 5, 2, 2, 10, 100, 5, 10, 10,
						10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 10);
			} else if (pc.getQuest().get_step(10001) == 0) {// 去找歌唱之岛-赛利安、隐藏之谷-雷克曼
				htmlid = "orenk2";
			} else if (pc.getQuest().get_step(10001) == 1) {// 去找说话岛-莉莉
				htmlid = "orenk3";
			} else if (pc.getQuest().get_step(10001) == 2) {// 去找古鲁丁-奇温
				htmlid = "orenk4";
			} else if (pc.getQuest().get_step(10001) == 3) {// 去找肯特村-希利亚
				htmlid = "orenk5";
			} else if (pc.getQuest().get_step(10001) == 4) {// 去找风木村-奥西利亚
				htmlid = "orenk6";
			} else if (pc.getQuest().get_step(10001) == 5) {// 去找燃柳村-胡尼
				htmlid = "orenk7";
			} else if (pc.getQuest().get_step(10001) == 6) {// 去找妖森-齐柯
				htmlid = "orenk8";
			} else if (pc.getQuest().get_step(10001) == 7) {// 去找银骑村-贺伯
				htmlid = "orenk9";
			} else if (pc.getQuest().get_step(10001) == 8) {// 去找奇岩村-托克
				htmlid = "orenk10";
			} else if (pc.getQuest().get_step(10001) == 10) {// 去找欧瑞村-吉伯特
				htmlid = "orenk0";
			} else if (pc.getQuest().get_step(10001) == 11) {// 去找威顿村-波利卡
				htmlid = "orenk11";
			} else if (pc.getQuest().get_step(10001) == 12) {// 去找亚丁村-杰瑞
				htmlid = "orenk12";
			} else if (pc.getQuest().get_step(10001) == 13) {// 去找沉默村-贾鲁曼
				htmlid = "orenk13";
			} else if (pc.getQuest().get_step(10001) == 255) {// 完成说话卷轴任务
				htmlid = "orenk14";
			}
		} else if (s.equals("0")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50125) {// 说话卷轴任务-吉伯特
			if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 10) {
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 40029, 40030, 40082, 40101,
						40116, 40743, /* 40733, */40099, 40095, 40098 };
				createcount = new long[] { 1, 10, 5, 2, 2, 10, 100, 5, 10, 10,
						10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 11);
			} else if (pc.getQuest().get_step(10001) == 0) {// 去找歌唱之岛-赛利安、隐藏之谷-雷克曼
				htmlid = "orenl2";
			} else if (pc.getQuest().get_step(10001) == 1) {// 去找说话岛-莉莉
				htmlid = "orenl3";
			} else if (pc.getQuest().get_step(10001) == 2) {// 去找古鲁丁-奇温
				htmlid = "orenl4";
			} else if (pc.getQuest().get_step(10001) == 3) {// 去找肯特村-希利亚
				htmlid = "orenl5";
			} else if (pc.getQuest().get_step(10001) == 4) {// 去找风木村-奥西利亚
				htmlid = "orenl6";
			} else if (pc.getQuest().get_step(10001) == 5) {// 去找燃柳村-胡尼
				htmlid = "orenl7";
			} else if (pc.getQuest().get_step(10001) == 6) {// 去找妖森-齐柯
				htmlid = "orenl8";
			} else if (pc.getQuest().get_step(10001) == 7) {// 去找银骑村-贺伯
				htmlid = "orenl9";
			} else if (pc.getQuest().get_step(10001) == 8) {// 去找奇岩村-托克
				htmlid = "orenl10";
			} else if (pc.getQuest().get_step(10001) == 9) {// 去找海音村-迦利温
				htmlid = "orenl11";
			} else if (pc.getQuest().get_step(10001) == 11) {// 去找威顿村-波利卡
				htmlid = "orenl0";
			} else if (pc.getQuest().get_step(10001) == 12) {// 去找亚丁村-杰瑞
				htmlid = "orenl12";
			} else if (pc.getQuest().get_step(10001) == 13) {// 去找沉默村-贾鲁曼
				htmlid = "orenl13";
			} else if (pc.getQuest().get_step(10001) == 255) {// 完成说话卷轴任务
				htmlid = "orenl14";
			}
		} else if (s.equals("0")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50124) {// 说话卷轴任务-波利卡
			if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 11) {
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 40029, 40030, 40082, 40101,
						40102, 40743, /* 40733, */40099, 40095, 40098 };
				createcount = new long[] { 1, 10, 5, 2, 2, 10, 100, 5, 10, 10,
						10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 12);
			} else if (pc.getQuest().get_step(10001) == 0) {// 去找歌唱之岛-赛利安、隐藏之谷-雷克曼
				htmlid = "orenm2";
			} else if (pc.getQuest().get_step(10001) == 1) {// 去找说话岛-莉莉
				htmlid = "orenm3";
			} else if (pc.getQuest().get_step(10001) == 2) {// 去找古鲁丁-奇温
				htmlid = "orenm4";
			} else if (pc.getQuest().get_step(10001) == 3) {// 去找肯特村-希利亚
				htmlid = "orenm5";
			} else if (pc.getQuest().get_step(10001) == 4) {// 去找风木村-奥西利亚
				htmlid = "orenm6";
			} else if (pc.getQuest().get_step(10001) == 5) {// 去找燃柳村-胡尼
				htmlid = "orenm7";
			} else if (pc.getQuest().get_step(10001) == 6) {// 去找妖森-齐柯
				htmlid = "orenm8";
			} else if (pc.getQuest().get_step(10001) == 7) {// 去找银骑村-贺伯
				htmlid = "orenm9";
			} else if (pc.getQuest().get_step(10001) == 8) {// 去找奇岩村-托克
				htmlid = "orenm10";
			} else if (pc.getQuest().get_step(10001) == 9) {// 去找海音村-迦利温
				htmlid = "orenm11";
			} else if (pc.getQuest().get_step(10001) == 10) {// 去找欧瑞村-吉伯特
				htmlid = "orenm12";
			} else if (pc.getQuest().get_step(10001) == 12) {// 去找亚丁村-杰瑞
				htmlid = "orenm0";
			} else if (pc.getQuest().get_step(10001) == 13) {// 去找沉默村-贾鲁曼
				htmlid = "orenm13";
			} else if (pc.getQuest().get_step(10001) == 255) {// 完成说话卷轴任务
				htmlid = "orenm14";
			}
		} else if (s.equals("0")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50126) {// 说话卷轴任务-杰瑞
			if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 12) {
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 40029, 40030, 40082, 40101,
						40845, 40743, /* 40733, */40099, 40095, 40098 };
				createcount = new long[] { 1, 10, 5, 2, 2, 10, 100, 5, 10, 10,
						10 };
				htmlid = "";
				pc.getQuest().set_step(10001, 13);
			} else if (pc.getQuest().get_step(10001) == 0) {// 去找歌唱之岛-赛利安、隐藏之谷-雷克曼
				htmlid = "orenn2";
			} else if (pc.getQuest().get_step(10001) == 1) {// 去找说话岛-莉莉
				htmlid = "orenn3";
			} else if (pc.getQuest().get_step(10001) == 2) {// 去找古鲁丁-奇温
				htmlid = "orenn4";
			} else if (pc.getQuest().get_step(10001) == 3) {// 去找肯特村-希利亚
				htmlid = "orenn5";
			} else if (pc.getQuest().get_step(10001) == 4) {// 去找风木村-奥西利亚
				htmlid = "orenn6";
			} else if (pc.getQuest().get_step(10001) == 5) {// 去找燃柳村-胡尼
				htmlid = "orenn7";
			} else if (pc.getQuest().get_step(10001) == 6) {// 去找妖森-齐柯
				htmlid = "orenn8";
			} else if (pc.getQuest().get_step(10001) == 7) {// 去找银骑村-贺伯
				htmlid = "orenn9";
			} else if (pc.getQuest().get_step(10001) == 8) {// 去找奇岩村-托克
				htmlid = "orenn10";
			} else if (pc.getQuest().get_step(10001) == 9) {// 去找海音村-迦利温
				htmlid = "orenn11";
			} else if (pc.getQuest().get_step(10001) == 10) {// 去找欧瑞村-吉伯特
				htmlid = "orenn12";
			} else if (pc.getQuest().get_step(10001) == 11) {// 去找威顿村-波利卡
				htmlid = "orenn13";
			} else if (pc.getQuest().get_step(10001) == 13) {// 去找沉默村-贾鲁曼
				htmlid = "orenn0";
			} else if (pc.getQuest().get_step(10001) == 255) {// 完成说话卷轴任务
				htmlid = "orenn14";
			}
		} else if (s.equals("0")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 50115) {// 说话卷轴任务-贾鲁曼
			if (pc.getInventory().checkItem(40641)
					&& pc.getQuest().get_step(10001) == 13) {
				materials = new int[] { 40641 };
				counts = new long[] { 1 };
				createitem = new int[] { 40641, 40029, 40030, 40082, 40101,
						40743, /* 40733, */40099, 40095, 40098, 20082 };
				createcount = new long[] { 1, 10, 5, 2, 2, 100, 5, 10, 10, 10,
						1 };
				htmlid = "";
				pc.getQuest().set_step(10001, 255);
			} else if (pc.getQuest().get_step(10001) == 0) {// 去找歌唱之岛-赛利安、隐藏之谷-雷克曼
				htmlid = "oreno2";
			} else if (pc.getQuest().get_step(10001) == 1) {// 去找说话岛-莉莉
				htmlid = "oreno3";
			} else if (pc.getQuest().get_step(10001) == 2) {// 去找古鲁丁-奇温
				htmlid = "oreno4";
			} else if (pc.getQuest().get_step(10001) == 3) {// 去找肯特村-希利亚
				htmlid = "oreno5";
			} else if (pc.getQuest().get_step(10001) == 4) {// 去找风木村-奥西利亚
				htmlid = "oreno6";
			} else if (pc.getQuest().get_step(10001) == 5) {// 去找燃柳村-胡尼
				htmlid = "oreno7";
			} else if (pc.getQuest().get_step(10001) == 6) {// 去找妖森-齐柯
				htmlid = "oreno8";
			} else if (pc.getQuest().get_step(10001) == 7) {// 去找银骑村-贺伯
				htmlid = "oreno9";
			} else if (pc.getQuest().get_step(10001) == 8) {// 去找奇岩村-托克
				htmlid = "oreno10";
			} else if (pc.getQuest().get_step(10001) == 9) {// 去找海音村-迦利温
				htmlid = "oreno11";
			} else if (pc.getQuest().get_step(10001) == 10) {// 去找欧瑞村-吉伯特
				htmlid = "oreno12";
			} else if (pc.getQuest().get_step(10001) == 11) {// 去找威顿村-波利卡
				htmlid = "oreno13";
			} else if (pc.getQuest().get_step(10001) == 12) {// 去找亚丁村-杰瑞
				htmlid = "oreno14";
			} else if (pc.getQuest().get_step(10001) == 255) {// 完成说话卷轴任务
				htmlid = "oreno0";
			}
		}
		// 说话卷轴任务 end
		// 骑士45级任务-调查员
		else if (s.equalsIgnoreCase("start")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_10
				&& pc.isKnight() && pc.getQuest().get_step(3) == 4) {
			L1FollowInstance baby = new L1FollowInstance(((L1NpcInstance) obj),
					pc);// 召唤调查员
			htmlid = "";
		}
		// 骑士45级任务-调查员 end
		// 黑妖50级任务-安迪亚
		else if (s.equalsIgnoreCase("start")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_11
				&& pc.isDarkelf() && pc.getQuest().get_step(4) == 1) {
			L1FollowInstance baby = new L1FollowInstance(((L1NpcInstance) obj),
					pc);// 召唤安迪亚
			htmlid = "";
		}
		// 黑妖50级任务-安迪亚 end
		//
		else if (s.equalsIgnoreCase("a")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_5_1
				&& pc.getQuest().get_step(l1j.william.New_Id.Npc_AJ_5_1) == 0) { // 欧力
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_2_5)
					&& pc.getInventory().checkItem(
							l1j.william.New_Id.Item_AJ_2_6)) { // 历史古书(全)、古代的羽毛笔
				htmlid = "nuevent011";
			} else {
				htmlid = "nuevent012";
			}
		} else if (s.equalsIgnoreCase("b")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_5_1
				&& pc.getQuest().get_step(l1j.william.New_Id.Npc_AJ_5_1) == 0) { // 欧力
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_2_5)
					&& pc.getInventory().checkItem(
							l1j.william.New_Id.Item_AJ_2_6)
					&& pc.getInventory().checkItem(40308, 50000)) { // 历史古书(全)、古代的羽毛笔
				materials = new int[] { l1j.william.New_Id.Item_AJ_2_5,
						l1j.william.New_Id.Item_AJ_2_6, 40308 };// 历史古书(全)、古代的羽毛笔、金币
				counts = new long[] { 1, 1, 50000 };
				createitem = new int[] { l1j.william.New_Id.Item_AJ_2_1 }; // 精灵的祝福御守
				createcount = new long[] { 1 };
				pc.getQuest().set_step(l1j.william.New_Id.Npc_AJ_5_1, 1); // 任务完成一半
				htmlid = "";
			} else {
				htmlid = "nuevent012";
			}
		} else if (s.equalsIgnoreCase("a")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_5_2
				&& // 拉奇牧师
				pc.getQuest().get_step(l1j.william.New_Id.Npc_AJ_5_1) == 1) {
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_2_7, 10)
					&& pc.getInventory().checkItem(40090, 20)) { // 受祝福的泉水10瓶、空的魔法卷轴(等级1)20张
				htmlid = "nuevent022";
			} else {
				htmlid = "nuevent023";
			}
		} else if (s.equalsIgnoreCase("b")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_5_2
				&& // 拉奇牧师
				pc.getQuest().get_step(l1j.william.New_Id.Npc_AJ_5_1) == 1) {
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_2_7, 10)
					&& pc.getInventory().checkItem(40090, 20)
					&& pc.getInventory().checkItem(40308, 50000)) { // 受祝福的泉水10瓶、空的魔法卷轴(等级1)20张、金币
				materials = new int[] { l1j.william.New_Id.Item_AJ_2_7, 40090,
						40308 };// 受祝福的泉水10瓶、空的魔法卷轴(等级1)20张、金币
				counts = new long[] { 10, 20, 50000 };
				createitem = new int[] { l1j.william.New_Id.Item_AJ_2_2 }; // 精灵的祝福签诗
				createcount = new long[] { 1 };
				pc.getQuest().set_step(l1j.william.New_Id.Npc_AJ_5_1, 255); // 完成任务
				htmlid = "";
			} else {
				htmlid = "nuevent023";
			}
		} else if (s.equalsIgnoreCase("a")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_5_3
				&& // 考古文具制作师
				!pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_2_1) && // 精灵的祝福御守
				!pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_2_5) && // 历史古书(全)
				!pc.getInventory().checkItem(l1j.william.New_Id.Armor_AJ_1_1)) { // 布拉斯耳环
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_2_3)
					&& pc.getInventory().checkItem(
							l1j.william.New_Id.Item_AJ_2_4)) { // 历史古书(上册)、历史古书(下册)
				htmlid = "nuevent031";
			} else {
				htmlid = "nuevent032";
			}
		} else if (s.equalsIgnoreCase("b")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_5_3
				&& // 考古文具制作师
				!pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_2_1) && // 精灵的祝福御守
				!pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_2_5) && // 历史古书(全)
				!pc.getInventory().checkItem(l1j.william.New_Id.Armor_AJ_1_1)) { // 布拉斯耳环
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_2_3)
					&& pc.getInventory().checkItem(
							l1j.william.New_Id.Item_AJ_2_4)
					&& pc.getInventory().checkItem(40308, 30000)) { // 历史古书(上册)、历史古书(下册)、金币
				materials = new int[] { l1j.william.New_Id.Item_AJ_2_3,
						l1j.william.New_Id.Item_AJ_2_4, 40308 }; // 历史古书(上册)、历史古书(下册)、金币
				counts = new long[] { 1, 1, 30000 };
				createitem = new int[] { l1j.william.New_Id.Item_AJ_2_5 }; // 历史古书(全)
				createcount = new long[] { 1 };
				htmlid = "";
			} else {
				htmlid = "nuevent034";
			}
		} else if (s.equalsIgnoreCase("c")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_5_3
				&& // 考古文具制作师
				!pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_2_1) && // 精灵的祝福御守
				!pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_2_6)) { // 古代的羽毛笔
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_2_8, 2)
					&& pc.getInventory().checkItem(40408, 20)) { // 古代格利芬的羽毛2根、金属块20个
				htmlid = "nuevent033";
			} else {
				htmlid = "nuevent034";
			}
		} else if (s.equalsIgnoreCase("d")
				&& ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_5_3
				&& // 考古文具制作师
				!pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_2_1) && // 精灵的祝福御守
				!pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_2_6)) { // 古代的羽毛笔
			if (pc.getInventory().checkItem(l1j.william.New_Id.Item_AJ_2_8, 2)
					&& pc.getInventory().checkItem(40408, 20)
					&& pc.getInventory().checkItem(40308, 30000)) { // 古代格利芬的羽毛2根、金属块20个、金币
				materials = new int[] { l1j.william.New_Id.Item_AJ_2_8, 40408,
						40308 }; // 古代格利芬的羽毛2根、金属块20个、金币
				counts = new long[] { 2, 20, 30000 };
				createitem = new int[] { l1j.william.New_Id.Item_AJ_2_6 }; // 古代的羽毛笔
				createcount = new long[] { 1 };
				htmlid = "";
			} else {
				htmlid = "nuevent034";
			}
		} else if (s.equalsIgnoreCase("q")
				&& (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_5_1
						|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_5_2 || ((L1NpcInstance) obj)
						.getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_5_3)) {// 关闭对话窗
			htmlid = "";
		}
		//

		// （村住民登录）
		else if (s.equalsIgnoreCase("set")) {
			if (obj instanceof L1NpcInstance) {
				int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
				int town_id = L1TownLocation.getTownIdByNpcid(npcid);

				if (town_id >= 1 && town_id <= 10) {
					if (pc.getHomeTownId() == -1) {
						// \f1新住民登录行时间。时间置登录。
						pc.sendPackets(new S_ServerMessage(759));
						htmlid = "";
					} else if (pc.getHomeTownId() > 0) {
						// 既登录
						if (pc.getHomeTownId() != town_id) {
							L1Town town = TownTable.getInstance().getTownTable(
									pc.getHomeTownId());
							if (town != null) {
								// 现在、住民登录场所%0。
								pc.sendPackets(new S_ServerMessage(758, town
										.get_name()));
							}
							htmlid = "";
						} else {
							// ？
							htmlid = "";
						}
					} else if (pc.getHomeTownId() == 0) {
						// 登录
						if (pc.getLevel() < 10) {
							// \f1住民登录10以上。
							pc.sendPackets(new S_ServerMessage(757));
							htmlid = "";
						} else {
							int level = pc.getLevel();
							int cost = level * level * 10;
							if (pc.getInventory().consumeItem(L1ItemId.ADENA,
									cost)) {
								pc.setHomeTownId(town_id);
								pc.setContribution(0); // 念
								pc.save();
							} else {
								// 不足。
								pc.sendPackets(new S_ServerMessage(337, "$4"));
							}
							htmlid = "";
						}
					}
				}
			}
		}
		// （住民登录取消）
		else if (s.equalsIgnoreCase("clear")) {
			if (obj instanceof L1NpcInstance) {
				int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
				int town_id = L1TownLocation.getTownIdByNpcid(npcid);
				if (town_id > 0) {
					if (pc.getHomeTownId() > 0) {
						if (pc.getHomeTownId() == town_id) {
							pc.setHomeTownId(-1);
							pc.setContribution(0); // 贡献度
							pc.save();
						} else {
							// \f1他村住民。
							pc.sendPackets(new S_ServerMessage(756));
						}
					}
					htmlid = "";
				}
			}
		}
		// （村村长谁闻）
		else if (s.equalsIgnoreCase("ask")) {
			if (obj instanceof L1NpcInstance) {
				int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
				int town_id = L1TownLocation.getTownIdByNpcid(npcid);

				if (town_id >= 1 && town_id <= 10) {
					L1Town town = TownTable.getInstance().getTownTable(town_id);
					String leader = town.get_leader_name();
					if (leader != null && leader.length() != 0) {
						htmlid = "owner";
						htmldata = new String[] { leader };
					} else {
						htmlid = "noowner";
					}
				}
			}
		}
		// 
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70534
				|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70556
				|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70572
				|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70631
				|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70663
				|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70761
				|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70788
				|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70806
				|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70830
				|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70876) {
			// （入报告）
			if (s.equalsIgnoreCase("r")) {
				if (obj instanceof L1NpcInstance) {
					int npcid = ((L1NpcInstance) obj).getNpcTemplate()
							.get_npcId();
					int town_id = L1TownLocation.getTownIdByNpcid(npcid);
				}
			}
			// （率更）
			else if (s.equalsIgnoreCase("t")) {

			}
			// （报酬）
			else if (s.equalsIgnoreCase("c")) {

			}
		}
		// 
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70997) {
			// 、旅立
			if (s.equalsIgnoreCase("0")) {
				final int[] item_ids = { 41146, 4, 20322, 173, 40743, };
				final int[] item_amounts = { 1, 1, 1, 1, 500, };
				for (int i = 0; i < item_ids.length; i++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							item_ids[i], item_amounts[i]);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							item.getLogName()));
				}
				pc.getQuest().set_step(L1Quest.QUEST_DOROMOND, 1);
				htmlid = "jpe0015";
			}
		}
		// (歌岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70999) {
			// 绍介状渡
			if (s.equalsIgnoreCase("1")) {
				if (pc.getInventory().consumeItem(41146, 1)) {
					final int[] item_ids = { 23, 20219, 20193, };
					final int[] item_amounts = { 1, 1, 1, };
					for (int i = 0; i < item_ids.length; i++) {
						L1ItemInstance item = pc.getInventory().storeItem(
								item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getLogName()));
					}
					pc.getQuest().set_step(L1Quest.QUEST_DOROMOND, 2);
					htmlid = "";
				}
			}
		}
		// (歌岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70999) {
			if (s.equalsIgnoreCase("2")) {
				final int[] item_ids = { 41227 }; // 绍介状
				final int[] item_amounts = { 1 };
				for (int i = 0; i < item_ids.length; i++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							item_ids[i], item_amounts[i]);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							item.getLogName()));
					pc.getQuest().set_step(L1Quest.QUEST_AREX,
							L1Quest.QUEST_END);
					htmlid = "";
				}
			}
		}
		// (歌岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71005) {
			// 受取
			if (s.equalsIgnoreCase("0")) {
				if (!pc.getInventory().checkItem(41209)) {
					final int[] item_ids = { 41209, };
					final int[] item_amounts = { 1, };
					for (int i = 0; i < item_ids.length; i++) {
						L1ItemInstance item = pc.getInventory().storeItem(
								item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getName()));
					}
				}
				htmlid = ""; // 消
			}
			// 受取
			else if (s.equalsIgnoreCase("1")) {
				if (pc.getInventory().consumeItem(41213, 1)) {
					final int[] item_ids = { 40029, };
					final int[] item_amounts = { 20, };
					for (int i = 0; i < item_ids.length; i++) {
						L1ItemInstance item = pc.getInventory().storeItem(
								item_ids[i], item_amounts[i]);
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getName()
										+ " (" + item_amounts[i] + ")"));
					}
					htmlid = ""; // 消
				}
			}
		}
		// (歌岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71006) {
			if (s.equalsIgnoreCase("0")) {
				if (pc.getLevel() > 25) {
					htmlid = "jpe0057";
				} else if (pc.getInventory().checkItem(41213)) { // 
					htmlid = "jpe0056";
				} else if (pc.getInventory().checkItem(41210)
						|| pc.getInventory().checkItem(41211)) { // 研磨材、
					htmlid = "jpe0055";
				} else if (pc.getInventory().checkItem(41209)) { // 依赖书
					htmlid = "jpe0054";
				} else if (pc.getInventory().checkItem(41212)) { // 特制
					htmlid = "jpe0056";
					materials = new int[] { 41212 }; // 特制
					counts = new long[] { 1 };
					createitem = new int[] { 41213 }; // 
					createcount = new long[] { 1 };
				} else {
					htmlid = "jpe0057";
				}
			}
		}
		// 治疗师（歌岛中：ＨＰ回复）
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 70512) {
			// 治疗受("fullheal"来？)
			if (s.equalsIgnoreCase("0") || s.equalsIgnoreCase("fullheal")) {
				if (pc.getLevel() > 12) {
					return;
				}
				// 血魔补满
				pc.setCurrentHp(pc.getMaxHp());
				pc.setCurrentMp(pc.getMaxMp());
				pc.sendPackets(new S_ServerMessage(77));
				pc.sendPackets(new S_SkillSound(pc.getId(), 830));
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
				// 血魔补满 end
			}
		}
		// 治疗师（训练场：HPMP回复）
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71037) {
			if (s.equalsIgnoreCase("0")) {
				if (pc.getLevel() > 12) {
					return;
				}
				pc.setCurrentHp(pc.getMaxHp());
				pc.setCurrentMp(pc.getMaxMp());
				pc.sendPackets(new S_ServerMessage(77));
				pc.sendPackets(new S_SkillSound(pc.getId(), 830));
				pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
				pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			}
		}
		// 治疗师（西部）
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71030) {
			if (s.equalsIgnoreCase("fullheal")) {
				if (pc.getLevel() > 12) {
					return;
				}
				if (pc.getInventory().checkItem(L1ItemId.ADENA, 5)) { // check
					pc.getInventory().consumeItem(L1ItemId.ADENA, 5); // del
					pc.setCurrentHp(pc.getMaxHp());
					pc.setCurrentMp(pc.getMaxMp());
					pc.sendPackets(new S_ServerMessage(77));
					pc.sendPackets(new S_SkillSound(pc.getId(), 830));
					pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
							.getMaxHp()));
					pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
							.getMaxMp()));
					if (pc.isInParty()) { // 中
						pc.getParty().updateMiniHP(pc);
					}
				} else {
					pc.sendPackets(new S_ServerMessage(337, "$4")); // 不足。
				}
			}
		}
		// 
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71002) {
			// 魔法
			if (s.equalsIgnoreCase("0")) {
				if (pc.getLevel() <= 13) {
					L1SkillUse skillUse = new L1SkillUse();
					skillUse.handleCommands(pc, L1SkillId.CANCELLATION,
							pc.getId(), pc.getX(), pc.getY(), null, 0,
							L1SkillUse.TYPE_NPCBUFF, 0, (L1NpcInstance) obj);
					htmlid = ""; // 消
				}
			}
		}
		// (歌岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71025) {
			if (s.equalsIgnoreCase("0")) {
				final int[] item_ids = { 41225, };
				final int[] item_amounts = { 1, };
				for (int i = 0; i < item_ids.length; i++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							item_ids[i], item_amounts[i]);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							item.getItem().getName()));
				}
				htmlid = "jpe0083";
			}
		}
		// (海贼岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71055) {
			// 受取
			if (s.equalsIgnoreCase("0")) {
				final int[] item_ids = { 40701, };
				final int[] item_amounts = { 1, };
				for (int i = 0; i < item_ids.length; i++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							item_ids[i], item_amounts[i]);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							item.getItem().getName()));
				}
				pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 1);
				htmlid = "lukein8";
			} else if (s.equalsIgnoreCase("2")) {
				htmlid = "lukein12";
				pc.getQuest().set_step(L1Quest.QUEST_RESTA, 3);
			}
		}
		// 小箱-1番目
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71063) {
			if (s.equalsIgnoreCase("0")) {
				materials = new int[] { 40701 }; // 小宝地图
				counts = new long[] { 1 };
				createitem = new int[] { 40702 }; // 小袋
				createcount = new long[] { 1 };
				htmlid = "maptbox1";
				pc.getQuest().set_end(L1Quest.QUEST_TBOX1);
				int[] nextbox = { 1, 2, 3 };
				int pid = _random.nextInt(nextbox.length);
				int nb = nextbox[pid];
				if (nb == 1) { // b地点
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 2);
				} else if (nb == 2) { // c地点
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 3);
				} else if (nb == 3) { // d地点
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 4);
				}
			}
		}
		// 小箱-2番目
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71064
				|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71065
				|| ((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71066) {
			if (s.equalsIgnoreCase("0")) {
				materials = new int[] { 40701 }; // 小宝地图
				counts = new long[] { 1 };
				createitem = new int[] { 40702 }; // 小袋
				createcount = new long[] { 1 };
				htmlid = "maptbox1";
				pc.getQuest().set_end(L1Quest.QUEST_TBOX2);
				int[] nextbox2 = { 1, 2, 3, 4, 5, 6 };
				int pid = _random.nextInt(nextbox2.length);
				int nb2 = nextbox2[pid];
				if (nb2 == 1) { // e地点
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 5);
				} else if (nb2 == 2) { // f地点
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 6);
				} else if (nb2 == 3) { // g地点
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 7);
				} else if (nb2 == 4) { // h地点
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 8);
				} else if (nb2 == 5) { // i地点
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 9);
				} else if (nb2 == 6) { // j地点
					pc.getQuest().set_step(L1Quest.QUEST_LUKEIN1, 10);
				}
			}
		}
		// (海贼岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71056) {
			// 息子搜
			if (s.equalsIgnoreCase("a")) {
				pc.getQuest().set_step(L1Quest.QUEST_SIMIZZ, 1);
				htmlid = "SIMIZZ7";
			} else if (s.equalsIgnoreCase("b")) {
				if (pc.getInventory().checkItem(40661)
						&& pc.getInventory().checkItem(40662)
						&& pc.getInventory().checkItem(40663)) {
					htmlid = "SIMIZZ8";
					pc.getQuest().set_step(L1Quest.QUEST_SIMIZZ, 2);
					materials = new int[] { 40661, 40662, 40663 };
					counts = new long[] { 1, 1, 1 };
					createitem = new int[] { 20044 };
					createcount = new long[] { 1 };
				} else {
					htmlid = "SIMIZZ9";
				}
			} else if (s.equalsIgnoreCase("d")) {
				htmlid = "SIMIZZ12";
				pc.getQuest().set_step(L1Quest.QUEST_SIMIZZ, L1Quest.QUEST_END);
			}
		}
		// (海贼岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71057) {
			// 闻
			if (s.equalsIgnoreCase("3")) {
				htmlid = "doil4";
			} else if (s.equalsIgnoreCase("6")) {
				htmlid = "doil6";
			} else if (s.equalsIgnoreCase("1")) {
				if (pc.getInventory().checkItem(40714)) {
					htmlid = "doil8";
					materials = new int[] { 40714 };
					counts = new long[] { 1 };
					createitem = new int[] { 40647 };
					createcount = new long[] { 1 };
					pc.getQuest().set_step(L1Quest.QUEST_DOIL,
							L1Quest.QUEST_END);
				} else {
					htmlid = "doil7";
				}
			}
		}
		// (海贼岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71059) {
			// 赖受入
			if (s.equalsIgnoreCase("A")) {
				htmlid = "rudian6";
				final int[] item_ids = { 40700 };
				final int[] item_amounts = { 1 };
				for (int i = 0; i < item_ids.length; i++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							item_ids[i], item_amounts[i]);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							item.getItem().getName()));
				}
				pc.getQuest().set_step(L1Quest.QUEST_RUDIAN, 1);
			} else if (s.equalsIgnoreCase("B")) {
				if (pc.getInventory().checkItem(40710)) {
					htmlid = "rudian8";
					materials = new int[] { 40700, 40710 };
					counts = new long[] { 1, 1 };
					createitem = new int[] { 40647 };
					createcount = new long[] { 1 };
					pc.getQuest().set_step(L1Quest.QUEST_RUDIAN,
							L1Quest.QUEST_END);
				} else {
					htmlid = "rudian9";
				}
			}
		}
		// (海贼岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71060) {
			// 仲间
			if (s.equalsIgnoreCase("A")) {
				if (pc.getQuest().get_step(L1Quest.QUEST_RUDIAN) == L1Quest.QUEST_END) {
					htmlid = "resta6";
				} else {
					htmlid = "resta4";
				}
			} else if (s.equalsIgnoreCase("B")) {
				htmlid = "resta10";
				pc.getQuest().set_step(L1Quest.QUEST_RESTA, 2);
			}
		}
		// (海贼岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71061) {
			// 地图组合
			if (s.equalsIgnoreCase("A")) {
				if (pc.getInventory().checkItem(40647, 3)) {
					htmlid = "cadmus6";
					pc.getInventory().consumeItem(40647, 3);
					pc.getQuest().set_step(L1Quest.QUEST_CADMUS, 2);
				} else {
					htmlid = "cadmus5";
					pc.getQuest().set_step(L1Quest.QUEST_CADMUS, 1);
				}
			}
		}
		// (海贼岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71062) {
			// 待一绪
			if (s.equalsIgnoreCase("start")) {
				htmlid = "kamit2";
				final int[] item_ids = { 40711 };
				final int[] item_amounts = { 1 };
				for (int i = 0; i < item_ids.length; i++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							item_ids[i], item_amounts[i]);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							item.getItem().getName()));
					pc.getQuest().set_step(L1Quest.QUEST_CADMUS, 3);
				}
			}
		}
		// (海贼岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71036) {
			if (s.equalsIgnoreCase("a")) {
				htmlid = "kamyla7";
				pc.getQuest().set_step(L1Quest.QUEST_KAMYLA, 1);
			} else if (s.equalsIgnoreCase("c")) {
				htmlid = "kamyla10";
				pc.getInventory().consumeItem(40644, 1);
				pc.getQuest().set_step(L1Quest.QUEST_KAMYLA, 3);
			} else if (s.equalsIgnoreCase("e")) {
				htmlid = "kamyla13";
				pc.getInventory().consumeItem(40630, 1);
				pc.getQuest().set_step(L1Quest.QUEST_KAMYLA, 4);
			}
		}
		// (海贼岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71089) {
			// 洁白证明
			if (s.equalsIgnoreCase("a")) {
				htmlid = "francu10";
				final int[] item_ids = { 40644 };
				final int[] item_amounts = { 1 };
				for (int i = 0; i < item_ids.length; i++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							item_ids[i], item_amounts[i]);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							item.getItem().getName()));
					pc.getQuest().set_step(L1Quest.QUEST_KAMYLA, 2);
				}
			}
		}
		// 试练2(海贼岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71090) {
			// 、武器
			if (s.equalsIgnoreCase("a")) {
				htmlid = "";
				final int[] item_ids = { 246, 247, 248, 249, 40660 };
				final int[] item_amounts = { 1, 1, 1, 1, 5 };
				for (int i = 0; i < item_ids.length; i++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							item_ids[i], item_amounts[i]);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							item.getItem().getName()));
					pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, 1);
				}
			} else if (s.equalsIgnoreCase("b")) {
				if (pc.getInventory().checkEquipped(246)
						|| pc.getInventory().checkEquipped(247)
						|| pc.getInventory().checkEquipped(248)
						|| pc.getInventory().checkEquipped(249)) {
					htmlid = "jcrystal5";
				} else if (pc.getInventory().checkItem(40660)) {
					htmlid = "jcrystal4";
				} else {
					pc.getInventory().consumeItem(246, 1);
					pc.getInventory().consumeItem(247, 1);
					pc.getInventory().consumeItem(248, 1);
					pc.getInventory().consumeItem(249, 1);
					pc.getInventory().consumeItem(40620, 1);
					pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, 2);
					L1Teleport.teleport(pc, 32801, 32895, (short) 483, 4, true);
				}
			} else if (s.equalsIgnoreCase("c")) {
				if (pc.getInventory().checkEquipped(246)
						|| pc.getInventory().checkEquipped(247)
						|| pc.getInventory().checkEquipped(248)
						|| pc.getInventory().checkEquipped(249)) {
					htmlid = "jcrystal5";
				} else {
					pc.getInventory().checkItem(40660);
					L1ItemInstance l1iteminstance = pc.getInventory()
							.findItemId(40660);
					long sc = l1iteminstance.getCount();
					if (sc > 0) {
						pc.getInventory().consumeItem(40660, sc);
					} else {
					}
					pc.getInventory().consumeItem(246, 1);
					pc.getInventory().consumeItem(247, 1);
					pc.getInventory().consumeItem(248, 1);
					pc.getInventory().consumeItem(249, 1);
					pc.getInventory().consumeItem(40620, 1);
					pc.getQuest().set_step(L1Quest.QUEST_CRYSTAL, 0);
					L1Teleport.teleport(pc, 32736, 32800, (short) 483, 4, true);
				}
			}
		}
		// 试练2(海贼岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71091) {
			// ！！
			if (s.equalsIgnoreCase("a")) {
				htmlid = "";
				pc.getInventory().consumeItem(40654, 1);
				pc.getQuest()
						.set_step(L1Quest.QUEST_CRYSTAL, L1Quest.QUEST_END);
				L1Teleport.teleport(pc, 32744, 32927, (short) 483, 4, true);
			}
		}
		// 长老(海贼岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71074) {
			// 士今
			if (s.equalsIgnoreCase("A")) {
				htmlid = "lelder5";
				pc.getQuest().set_step(L1Quest.QUEST_LIZARD, 1);
				// 取
			} else if (s.equalsIgnoreCase("B")) {
				htmlid = "lelder10";
				pc.getInventory().consumeItem(40633, 1);
				pc.getQuest().set_step(L1Quest.QUEST_LIZARD, 3);
			}
		}
		// 疲果(海贼岛)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 71075) {
			// 报告书
			if (s.equalsIgnoreCase("start")) {
				htmlid = "llizard2";
				final int[] item_ids = { 40633 };
				final int[] item_amounts = { 1 };
				for (int i = 0; i < item_ids.length; i++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							item_ids[i], item_amounts[i]);
					pc.sendPackets(new S_ServerMessage(143,
							((L1NpcInstance) obj).getNpcTemplate().get_name(),
							item.getItem().getName()));
					pc.getQuest().set_step(L1Quest.QUEST_LIZARD, 2);
				}
			} else {
			}
		}
		// 占星术师
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80079) {
			// 魂契约结
			if (s.equalsIgnoreCase("0")) {
				if (!pc.getInventory().checkItem(41312)) { // 占星术师壶
					L1ItemInstance item = pc.getInventory().storeItem(41312, 1);
					if (item != null) {
						pc.sendPackets(new S_ServerMessage(143,
								((L1NpcInstance) obj).getNpcTemplate()
										.get_name(), item.getItem().getName())); // \f1%0%1。
					}
					htmlid = "keplisha7";
				}
			}
			// 援助金出运势见
			else if (s.equalsIgnoreCase("1")) {
				if (!pc.getInventory().checkItem(41314)) { // 占星术师守
					if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
						materials = new int[] { L1ItemId.ADENA, 41313 }; // 、占星术师玉
						counts = new long[] { 1000, 1 };
						createitem = new int[] { 41314 }; // 占星术师守
						createcount = new long[] { 1 };
						int htmlA = _random.nextInt(3) + 1;
						int htmlB = _random.nextInt(100) + 1;
						switch (htmlA) {
						case 1:
							htmlid = "horosa" + htmlB; // horosa1 ~ horosa100
							break;
						case 2:
							htmlid = "horosb" + htmlB; // horosb1 ~ horosb100
							break;
						case 3:
							htmlid = "horosc" + htmlB; // horosc1 ~ horosc100
							break;
						default:
							break;
						}
					} else {
						htmlid = "keplisha8";
					}
				}
			}
			// 祝福受
			else if (s.equalsIgnoreCase("2")) {
				if (pc.getTempCharGfx() != pc.getClassId()) {
					htmlid = "keplisha9";
				} else {
					if (pc.getInventory().checkItem(41314)) { // 占星术师守
						pc.getInventory().consumeItem(41314, 1); // 占星术师守
						int html = _random.nextInt(9) + 1;
						int PolyId = 6180 + _random.nextInt(64);
						poly(_client, PolyId);
						switch (html) {
						case 1:
							htmlid = "horomon11";
							break;
						case 2:
							htmlid = "horomon12";
							break;
						case 3:
							htmlid = "horomon13";
							break;
						case 4:
							htmlid = "horomon21";
							break;
						case 5:
							htmlid = "horomon22";
							break;
						case 6:
							htmlid = "horomon23";
							break;
						case 7:
							htmlid = "horomon31";
							break;
						case 8:
							htmlid = "horomon32";
							break;
						case 9:
							htmlid = "horomon33";
							break;
						default:
							break;
						}
					}
				}
			}
			// 壶割契约破弃
			else if (s.equalsIgnoreCase("3")) {
				if (pc.getInventory().checkItem(41312)) { // 占星术师壶
					pc.getInventory().consumeItem(41312, 1);
					htmlid = "";
				}
				if (pc.getInventory().checkItem(41313)) { // 占星术师玉
					pc.getInventory().consumeItem(41313, 1);
					htmlid = "";
				}
				if (pc.getInventory().checkItem(41314)) { // 占星术师守
					pc.getInventory().consumeItem(41314, 1);
					htmlid = "";
				}
			}
		}
		// 钓子(IN)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80082) {
			// “长重钓竿”
			if (s.equalsIgnoreCase("L")) {
				if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
					materials = new int[] { L1ItemId.ADENA };
					counts = new long[] { 1000 };
					createitem = new int[] { 41293 };
					createcount = new long[] { 1 };
					// 修正钓鱼小童功能
					L1PolyMorph.undoPoly(pc);
					L1Teleport
							.teleport(pc, 32813, 32807, (short) 5124, 5, true);
					// 修正钓鱼小童功能 end
				} else {
					htmlid = "fk_in_1";
				}
				// “短钓竿”
			} else if (s.equalsIgnoreCase("S")) {
				if (pc.getInventory().checkItem(L1ItemId.ADENA, 1000)) {
					materials = new int[] { L1ItemId.ADENA };
					counts = new long[] { 1000 };
					createitem = new int[] { 41294 };
					createcount = new long[] { 1 };
					// 修正钓鱼小童功能
					L1PolyMorph.undoPoly(pc);
					L1Teleport
							.teleport(pc, 32813, 32807, (short) 5124, 5, true);
					// 修正钓鱼小童功能 end
				} else {
					htmlid = "fk_in_1";
				}
			}
		}
		// 钓子(OUT)
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80083) {
			// “钓止外出”
			if (s.equalsIgnoreCase("O")) {
				// 修正离开钓鱼岛判断
				if (!pc.getInventory().checkItem(41293, 1)
						&& !pc.getInventory().checkItem(41294, 1)) {
					htmlid = "fk_out_0";
				} else if (pc.getInventory().consumeItem(41293, 1)) {// 长钓竿
					L1Teleport.teleport(pc, 32608, 32772, (short) 4, 5, true);
				} else if (pc.getInventory().consumeItem(41294, 1)) {// 短钓竿
					L1Teleport.teleport(pc, 32608, 32772, (short) 4, 5, true);
				}
				// 修正离开钓鱼岛判断 end
			}
		}
		// 骑马员
		else if (((L1NpcInstance) obj).getNpcTemplate().get_npcId() == 80105) {
			// “新力”
			if (s.equalsIgnoreCase("c")) {
				if (pc.isCrown()) {
					if (pc.getInventory().checkItem(20383, 1)) {
						if (pc.getInventory().checkItem(L1ItemId.ADENA, 100000)) {
							L1ItemInstance item = pc.getInventory().findItemId(
									20383);
							if (item != null && item.getChargeCount() != 50) {
								item.setChargeCount(50);
								pc.getInventory().updateItem(item,
										L1PcInventory.COL_CHARGE_COUNT);
								pc.getInventory().consumeItem(L1ItemId.ADENA,
										100000);
								htmlid = "";
							}
						} else {
							pc.sendPackets(new S_ServerMessage(337, "$4")); // 不足。
						}
					}
				}
			}
		}

		// else System.out.println("C_NpcAction: " + s);
		if (htmlid != null && htmlid.equalsIgnoreCase("colos2")) {
			htmldata = makeUbInfoStrings(((L1NpcInstance) obj).getNpcTemplate()
					.get_npcId());
		}
		if (createitem != null) { // 精制
			boolean isCreate = true;
			for (int j = 0; j < materials.length; j++) {
				if (!pc.getInventory().checkItemNotEquipped(materials[j],
						counts[j])) {
					L1Item temp = ItemTable.getInstance().getTemplate(
							materials[j]);
					pc.sendPackets(new S_ServerMessage(337, temp.getName())); // \f1%0不足。
					isCreate = false;
				}
			}

			if (isCreate) {
				// 容量重量计算
				int create_count = 0; // 个数（缠物1个）
				int create_weight = 0;
				for (int k = 0; k < createitem.length; k++) {
					L1Item temp = ItemTable.getInstance().getTemplate(
							createitem[k]);
					if (temp.isStackable()) {
						if (!pc.getInventory().checkItem(createitem[k])) {
							create_count += 1;
						}
					} else {
						create_count += createcount[k];
					}
					create_weight += temp.getWeight() * createcount[k] / 1000;
				}
				// 容量确认
				if (pc.getInventory().getSize() + create_count > 180) {
					pc.sendPackets(new S_ServerMessage(263)); // \f1一人持步最大180个。
					return;
				}
				// 重量确认
				if (pc.getMaxWeight() < pc.getInventory().getWeight()
						+ create_weight) {
					pc.sendPackets(new S_ServerMessage(82)); // 重、以上持。
					return;
				}

				for (int j = 0; j < materials.length; j++) {
					// 材料消费
					pc.getInventory().consumeItem(materials[j], counts[j]);
				}
				for (int k = 0; k < createitem.length; k++) {
					L1ItemInstance item = pc.getInventory().storeItem(
							createitem[k], createcount[k]);
					if (item != null) {
						String itemName = ItemTable.getInstance()
								.getTemplate(createitem[k]).getName();
						String createrName = "";
						if (obj instanceof L1NpcInstance) {
							createrName = ((L1NpcInstance) obj)
									.getNpcTemplate().get_name();
						}
						if (createcount[k] > 1) {
							pc.sendPackets(new S_ServerMessage(143,
									createrName, itemName + " ("
											+ createcount[k] + ")")); // \f1%0%1。
						} else {
							pc.sendPackets(new S_ServerMessage(143,
									createrName, itemName)); // \f1%0%1。
						}
					}
				}
				if (success_htmlid != null) { // html指定场合表示
					pc.sendPackets(new S_NPCTalkReturn(objid, success_htmlid,
							htmldata));
				}
			} else { // 精制失败
				if (failure_htmlid != null) { // html指定
					pc.sendPackets(new S_NPCTalkReturn(objid, failure_htmlid,
							htmldata));
				}
			}
		}

		if (htmlid != null) { // html指定场合表示
			pc.sendPackets(new S_NPCTalkReturn(objid, htmlid, htmldata));
		}
	}

	private String karmaLevelToHtmlId(int level) {
		if (level == 0 || level < -7 || 7 < level) {
			return "";
		}
		String htmlid = "";
		if (0 < level) {
			htmlid = "vbk" + level;
		} else if (level < 0) {
			htmlid = "vyk" + Math.abs(level);
		}
		return htmlid;
	}

	private String watchUb(L1PcInstance pc, int npcId) {
		L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
		L1Location loc = ub.getLocation();
		if (pc.getInventory().consumeItem(L1ItemId.ADENA, 100)) {
			try {
				pc.save();
				pc.beginGhost(loc.getX(), loc.getY(), (short) loc.getMapId(),
						true);
			} catch (Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		} else {
			pc.sendPackets(new S_ServerMessage(189)); // \f1不足。
		}
		return "";
	}

	private String enterUb(L1PcInstance pc, int npcId) {
		L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
		if (!ub.isActive() || !ub.canPcEnter(pc)) { // 时间外
			return "colos2";
		}
		if (ub.isNowUb()) { // 竞技中
			return "colos1";
		}
		if (ub.getMembersCount() >= ub.getMaxPlayer()) { // 定员
			return "colos4";
		}

		ub.addMember(pc); // 追加
		L1Location loc = ub.getLocation().randomLocation(10, false);
		L1Teleport.teleport(pc, loc.getX(), loc.getY(), ub.getMapId(), 5, true);
		return "";
	}

	private String enterHauntedHouse(L1PcInstance pc) {
		if (L1HauntedHouse.getInstance().getHauntedHouseStatus() == L1HauntedHouse.STATUS_PLAYING) { // 竞技中
			pc.sendPackets(new S_ServerMessage(1182)); // 始。
			return "";
		}
		if (L1HauntedHouse.getInstance().getMembersCount() >= 10) { // 定员
			pc.sendPackets(new S_ServerMessage(1184)); // 化屋敷人。
			return "";
		}

		L1HauntedHouse.getInstance().addMember(pc); // 追加
		L1Teleport.teleport(pc, 32722, 32830, (short) 5140, 2, true);
		return "";
	}

	private void summonMonster(L1PcInstance pc, String s) {
		if (!pc.getInventory().checkEquipped(20284)) {
			return;
		}
		String[] summonstr_list;
		int[] summonid_list;
		int[] summonlvl_list;
		int[] summoncha_list;
		int summonid = 0;
		int levelrange = 0;
		int summoncost = 0;
		summonstr_list = new String[] { "7", "263", "8", "264", "9", "265",
				"10", "266", "11", "267", "12", "268", "13", "269", "14",
				"270", "526", "15", "271", "527", "17", "18" };
		summonid_list = new int[] { 81083, 81090, 81084, 81091, 81085, 81092,
				81086, 81093, 81087, 81094, 81088, 81095, 81089, 81096, 81097,
				81098, 81099, 81100, 81101, 81102, 81103, 81104 };
		summonlvl_list = new int[] { 28, 28, 32, 32, 36, 36, 40, 40, 44, 44,
				48, 48, 52, 52, 56, 56, 56, 60, 60, 60, 68, 72 };
		// 、付+6
		summoncha_list = new int[] { 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 8,
				8, 8, 8, 10, 10, 10, 36, 40 };
		// 种类、必要Lv、得
		for (int loop = 0; loop < summonstr_list.length; loop++) {
			if (s.equalsIgnoreCase(summonstr_list[loop])) {
				summonid = summonid_list[loop];
				levelrange = summonlvl_list[loop];
				summoncost = summoncha_list[loop];
				break;
			}
		}
		// Lv不足
		if (pc.getLevel() < levelrange) {
			// 低该当召还。
			pc.sendPackets(new S_ServerMessage(743));
			return;
		}

		int petcost = 0;
		Object[] petlist = pc.getPetList().values().toArray();
		for (Object pet : petlist) {
			// 现在
			petcost += ((L1NpcInstance) pet).getPetcost();
		}
		// 既场合、、呼出
		if ((summonid == 81103 || summonid == 81104) && petcost < 0) {
			pc.sendPackets(new S_CloseList(pc.getId()));
			return;
		}
		int charisma = pc.getCha() + 6 - petcost;
		int summoncount = charisma / summoncost;
		L1Npc npcTemp = NpcTable.getInstance().getTemplate(summonid);
		for (int cnt = 0; cnt < summoncount; cnt++) {
			L1SummonInstance summon = new L1SummonInstance(npcTemp, pc);
			if (summonid == 81103 || summonid == 81104) {
				summon.setPetcost(pc.getCha() + 7);
			} else {
				summon.setPetcost(summoncost);
			}
		}
		pc.sendPackets(new S_CloseList(pc.getId()));
	}

	private void poly(LineageClient _client, int polyId) {
		L1PcInstance pc = _client.getActiveChar();

		if (pc.getInventory().checkItem(L1ItemId.ADENA, 100)) { // check
			pc.getInventory().consumeItem(L1ItemId.ADENA, 100); // del

			L1PolyMorph.doPoly(pc, polyId, 1800);
		} else {
			pc.sendPackets(new S_ServerMessage(337, "$4")); // 不足。
		}
	}

	private String sellHouse(L1PcInstance pc, int objectId, int npcId) {
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan == null) {
			return ""; // 消
		}
		int houseId = clan.getHouseId();
		if (houseId == 0) {
			return ""; // 消
		}
		L1House house = HouseTable.getInstance().getHouseTable(houseId);
		int keeperId = house.getKeeperId();
		if (npcId != keeperId) {
			return ""; // 消
		}
		if (!pc.isCrown()) {
			pc.sendPackets(new S_ServerMessage(518)); // 命令血盟君主利用。
			return ""; // 消
		}
		if (pc.getId() != clan.getLeaderId()) {
			pc.sendPackets(new S_ServerMessage(518)); // 命令血盟君主利用。
			return ""; // 消
		}
		if (house.isOnSale()) {
			return "agonsale";
		}

		pc.sendPackets(new S_SellHouse(objectId, String.valueOf(houseId)));
		return null;
	}

	private void openCloseDoor(L1PcInstance pc, L1NpcInstance npc, String s) {
		int doorId = 0;
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan != null) {
			int houseId = clan.getHouseId();
			if (houseId != 0) {
				L1House house = HouseTable.getInstance().getHouseTable(houseId);
				int keeperId = house.getKeeperId();
				if (npc.getNpcTemplate().get_npcId() == keeperId) {
					L1DoorInstance door1 = null;
					L1DoorInstance door2 = null;
					for (L1DoorInstance door : DoorSpawnTable.getInstance()
							.getDoorList()) {
						if (door.getKeeperId() == keeperId) {
							if (door1 == null) {
								door1 = door;
								continue;
							}
							if (door2 == null) {
								door2 = door;
								break;
							}
						}
					}
					if (door1 != null) {
						if (s.equalsIgnoreCase("open")
								&& door1.getOpenStatus() == ActionCodes.ACTION_Close) {
							door1.dooropen();
						} else if (s.equalsIgnoreCase("close")
								&& door1.getOpenStatus() == ActionCodes.ACTION_Open) {
							door1.doorclose();
						}
					}
					if (door2 != null) {
						if (s.equalsIgnoreCase("open")
								&& door2.getOpenStatus() == ActionCodes.ACTION_Close) {
							door2.dooropen();
						} else if (s.equalsIgnoreCase("close")
								&& door2.getOpenStatus() == ActionCodes.ACTION_Open) {
							door2.doorclose();
						}
					}
				}
			}
		}
	}

	private boolean isExistDefenseClan(int castleId) {
		boolean isExistDefenseClan = false;
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			if (castleId == clan.getCastleId()) {
				isExistDefenseClan = true;
				break;
			}
		}
		return isExistDefenseClan;
	}

	// 开启 & 关闭城门
	private void openCloseGateDoor(L1PcInstance pc, int keeperId, boolean isOpen) {
		int pcCastleId = 0;
		if (pc.getClanid() != 0) {
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				pcCastleId = clan.getCastleId();
			}
		}
		if (keeperId == 70656) { // 城入口
			if (isExistDefenseClan(L1CastleLocation.KENT_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.KENT_CASTLE_ID) {
					return;
				}
			}
		} else if (keeperId == 70778 || keeperId == 70687 || keeperId == 70987) { // WW城入口
			if (isExistDefenseClan(L1CastleLocation.WW_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.WW_CASTLE_ID) {
					return;
				}
			}
		} else if (keeperId == 70817) { // 城入口
			if (isExistDefenseClan(L1CastleLocation.GIRAN_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.GIRAN_CASTLE_ID) {
					return;
				}
			}
		} else if (keeperId == 70863) { // 城入口
			if (isExistDefenseClan(L1CastleLocation.HEINE_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.HEINE_CASTLE_ID) {
					return;
				}
			}
		} else if (keeperId == 70995) { // 城入口
			if (isExistDefenseClan(L1CastleLocation.DOWA_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.DOWA_CASTLE_ID) {
					return;
				}
			}
		} else if (keeperId == 70996) { // 城入口
			if (isExistDefenseClan(L1CastleLocation.ADEN_CASTLE_ID)) {
				if (pcCastleId != L1CastleLocation.ADEN_CASTLE_ID) {
					return;
				}
			}
		}

		for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
			if (door.getKeeperId() == keeperId) {
				if (isOpen) { // 开
					if ((door.getMaxHp() * 3 / 4) < door.getCurrentHp()) {// 未受损
						if (door.getOpenStatus() == ActionCodes.ACTION_Close) {
							door.dooropen();
						}
					} else if (door.getCurrentHp() <= 0) {
						pc.sendPackets(new S_SystemMessage(
								L1WilliamSystemMessage.ShowMessage(1012))); // 从DB取得讯息
					} else {
						pc.sendPackets(new S_SystemMessage(
								L1WilliamSystemMessage.ShowMessage(1013))); // 从DB取得讯息
					}
				} else { // 闭
					if ((door.getMaxHp() * 3 / 4) < door.getCurrentHp()) {// 未受损
						if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
							door.doorclose();
						}
					} else if (door.getCurrentHp() <= 0) {
						pc.sendPackets(new S_SystemMessage(
								L1WilliamSystemMessage.ShowMessage(1012))); // 从DB取得讯息
					} else {
						pc.sendPackets(new S_SystemMessage(
								L1WilliamSystemMessage.ShowMessage(1013))); // 从DB取得讯息
					}
				}
			}
		}
	}

	// 开启 & 关闭城门

	private void expelOtherClan(L1PcInstance clanPc, int keeperId) {
		int houseId = 0;
		for (L1House house : HouseTable.getInstance().getHouseTableList()) {
			if (house.getKeeperId() == keeperId) {
				houseId = house.getHouseId();
			}
		}
		if (houseId == 0) {
			return;
		}

		int[] loc = new int[3];
		for (L1Object object : L1World.getInstance().getObject()) {
			if (object instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) object;
				if (L1HouseLocation.isInHouseLoc(houseId, pc.getX(), pc.getY(),
						pc.getMapId()) && clanPc.getClanid() != pc.getClanid()) {
					loc = L1HouseLocation.getHouseTeleportLoc(houseId, 0);
					if (pc != null) {
						L1Teleport.teleport(pc, loc[0], loc[1], (short) loc[2],
								5, true);
					}
				}
			}
		}
	}

	private String[] makeWarTimeStrings(int castleId) {
		L1Castle castle = CastleTable.getInstance().getCastleTable(castleId);
		if (castle == null) {
			return null;
		}
		Calendar warTime = castle.getWarTime();
		int year = warTime.get(Calendar.YEAR);
		int month = warTime.get(Calendar.MONTH) + 1;
		int day = warTime.get(Calendar.DATE);
		int hour = warTime.get(Calendar.HOUR_OF_DAY);
		int minute = warTime.get(Calendar.MINUTE);
		String[] result;
		if (castleId == L1CastleLocation.OT_CASTLE_ID) {
			result = new String[] { String.valueOf(year),
					String.valueOf(month), String.valueOf(day),
					String.valueOf(hour), String.valueOf(minute) };
		} else {
			result = new String[] { "", String.valueOf(year),
					String.valueOf(month), String.valueOf(day),
					String.valueOf(hour), String.valueOf(minute) };
		}
		return result;
	}

	private String getYaheeAmulet(L1PcInstance pc, L1NpcInstance npc, String s) {
		int[] amuletIdList = { 20358, 20359, 20360, 20361, 20362, 20363, 20364,
				20365 };
		final int[] karmalist = { -10000, -20000, -100000, -500000, -1500000,
				-3000000, -5000000, -10000000 };
		int amuletId = 0;
		int karma = 0;
		L1ItemInstance item = null;
		String htmlid = null;
		if (s.equalsIgnoreCase("1")) {
			amuletId = amuletIdList[0];
			karma = karmalist[0];
		} else if (s.equalsIgnoreCase("2")) {
			amuletId = amuletIdList[1];
			karma = karmalist[1];
		} else if (s.equalsIgnoreCase("3")) {
			amuletId = amuletIdList[2];
			karma = karmalist[2];
		} else if (s.equalsIgnoreCase("4")) {
			amuletId = amuletIdList[3];
			karma = karmalist[3];
		} else if (s.equalsIgnoreCase("5")) {
			amuletId = amuletIdList[4];
			karma = karmalist[4];
		} else if (s.equalsIgnoreCase("6")) {
			amuletId = amuletIdList[5];
			karma = karmalist[5];
		} else if (s.equalsIgnoreCase("7")) {
			amuletId = amuletIdList[6];
			karma = karmalist[6];
		} else if (s.equalsIgnoreCase("8")) {
			amuletId = amuletIdList[7];
			karma = karmalist[7];
		}
		_log.info("当前需要友好度为" + karma + "玩家" + pc.getName() + "当前友好度为"
				+ pc.getKarma() + "尝试兑换" + amuletId);
		if (amuletId != 0 && pc.getKarma() <= karma) {
			_log.info("当前需要友好度为" + karma + "玩家" + pc.getName() + "当前友好度为"
					+ pc.getKarma() + "兑换" + amuletId + "成功");
			item = pc.getInventory().storeItem(amuletId, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0が%1をくれました。
			}
			for (final int id : amuletIdList) {
				if (id == amuletId) {
					break;
				}
				if (pc.getInventory().checkItem(id)) {
					pc.getInventory().consumeItem(id, 1);
				}
			}
			htmlid = "";
		}
		return htmlid;
	}

	private String getBarlogEarring(L1PcInstance pc, L1NpcInstance npc, String s) {
		int[] earringIdList = { 21020, 21021, 21022, 21023, 21024, 21025,
				21026, 21027 };
		final int[] karmalist = { 10000, 20000, 100000, 500000, 1500000,
				3000000, 5000000, 10000000 };
		int earringId = 0;
		L1ItemInstance item = null;
		String htmlid = null;
		int karma = 0;
		if (s.equalsIgnoreCase("1")) {
			earringId = earringIdList[0];
			karma = karmalist[0];
		} else if (s.equalsIgnoreCase("2")) {
			earringId = earringIdList[1];
			karma = karmalist[1];
		} else if (s.equalsIgnoreCase("3")) {
			earringId = earringIdList[2];
			karma = karmalist[2];
		} else if (s.equalsIgnoreCase("4")) {
			earringId = earringIdList[3];
			karma = karmalist[3];
		} else if (s.equalsIgnoreCase("5")) {
			earringId = earringIdList[4];
			karma = karmalist[4];
		} else if (s.equalsIgnoreCase("6")) {
			earringId = earringIdList[5];
			karma = karmalist[5];
		} else if (s.equalsIgnoreCase("7")) {
			earringId = earringIdList[6];
			karma = karmalist[6];
		} else if (s.equalsIgnoreCase("8")) {
			earringId = earringIdList[7];
			karma = karmalist[7];
		}
		_log.info("当前需要友好度为" + karma + "玩家" + pc.getName() + "当前友好度为"
				+ pc.getKarma() + "尝试兑换" + earringId);
		if (earringId != 0 && pc.getKarma() >= karma) {
			_log.info("当前需要友好度为" + karma + "玩家" + pc.getName() + "当前友好度为"
					+ pc.getKarma() + "兑换" + earringId + "成功");
			item = pc.getInventory().storeItem(earringId, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0が%1をくれました。
			}
			for (final int id : earringIdList) {
				if (id == earringId) {
					break;
				}
				if (pc.getInventory().checkItem(id)) {
					pc.getInventory().consumeItem(id, 1);
				}
			}
			htmlid = "";
		}
		return htmlid;
	}

	private String[] makeUbInfoStrings(int npcId) {
		L1UltimateBattle ub = UBTable.getInstance().getUbForNpcId(npcId);
		return ub.makeUbInfoStrings();
	}

	private String talkToDimensionDoor(L1PcInstance pc, L1NpcInstance npc,
			String s) {
		String htmlid = "";
		int protectionId = 0;
		int sealId = 0;
		int locX = 0;
		int locY = 0;
		short mapId = 0;
		if (npc.getNpcTemplate().get_npcId() == 80059) { // 次元扉(土)
			protectionId = 40909;
			sealId = 40913;
			locX = 32773;
			locY = 32835;
			mapId = 607;
		} else if (npc.getNpcTemplate().get_npcId() == 80060) { // 次元扉(风)
			protectionId = 40912;
			sealId = 40916;
			locX = 32757;
			locY = 32842;
			mapId = 606;
		} else if (npc.getNpcTemplate().get_npcId() == 80061) { // 次元扉(水)
			protectionId = 40910;
			sealId = 40914;
			locX = 32830;
			locY = 32822;
			mapId = 604;
		} else if (npc.getNpcTemplate().get_npcId() == 80062) { // 次元扉(火)
			protectionId = 40911;
			sealId = 40915;
			locX = 32835;
			locY = 32822;
			mapId = 605;
		}

		// “中入”“元素支配者近”“通行证使”“通过”
		if (s.equalsIgnoreCase("a")) {
			L1Teleport.teleport(pc, locX, locY, mapId, 5, true);
			htmlid = "";
		}
		// “绘突出部分取除”
		else if (s.equalsIgnoreCase("b")) {
			L1ItemInstance item = pc.getInventory().storeItem(protectionId, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0%1。
			}
			htmlid = "";
		}
		// “通行证舍、地”
		else if (s.equalsIgnoreCase("c")) {
			htmlid = "wpass07";
		}
		// “续”
		else if (s.equalsIgnoreCase("d")) {
			if (pc.getInventory().checkItem(sealId)) { // 地印章
				L1ItemInstance item = pc.getInventory().findItemId(sealId);
				pc.getInventory().consumeItem(sealId, item.getCount());
			}
		}
		// “”“慌拾”
		else if (s.equalsIgnoreCase("e")) {
			htmlid = "";
		}
		// “消”
		else if (s.equalsIgnoreCase("f")) {
			if (pc.getInventory().checkItem(protectionId)) { // 地通行证
				pc.getInventory().consumeItem(protectionId, 1);
			}
			if (pc.getInventory().checkItem(sealId)) { // 地印章
				L1ItemInstance item = pc.getInventory().findItemId(sealId);
				pc.getInventory().consumeItem(sealId, item.getCount());
			}
			htmlid = "";
		}
		return htmlid;
	}

	private boolean isNpcSellOnly(L1NpcInstance npc) {
		int npcId = npc.getNpcTemplate().get_npcId();
		String npcName = npc.getNpcTemplate().get_name();
		if (npcId == 70027 // 
				|| "亚丁商团".equals(npcName)) {
			return true;
		}
		return false;
	}

	private void getBloodCrystalByKarma(L1PcInstance pc, L1NpcInstance npc,
			String s) {
		L1ItemInstance item = null;

		// “欠片1个”
		if (s.equalsIgnoreCase("1")) {
			pc.addKarma((int) (500 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40718, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0%1。
			}
			// 姿记忆难。
			pc.sendPackets(new S_ServerMessage(1081));
		}
		// “欠片10个”
		else if (s.equalsIgnoreCase("2")) {
			pc.addKarma((int) (5000 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40718, 10);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0%1。
			}
			// 姿记忆难。
			pc.sendPackets(new S_ServerMessage(1081));
		}
		// “欠片100个”
		else if (s.equalsIgnoreCase("3")) {
			pc.addKarma((int) (50000 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40718, 100);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0%1。
			}
			// 姿记忆难。
			pc.sendPackets(new S_ServerMessage(1081));
		}
	}

	private void getSoulCrystalByKarma(L1PcInstance pc, L1NpcInstance npc,
			String s) {
		L1ItemInstance item = null;

		// “欠片1个”
		if (s.equalsIgnoreCase("1")) {
			pc.addKarma((int) (-500 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40678, 1);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0%1。
			}
			// 冷笑感恶寒走。
			pc.sendPackets(new S_ServerMessage(1080));
		}
		// “欠片10个”
		else if (s.equalsIgnoreCase("2")) {
			pc.addKarma((int) (-5000 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40678, 10);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0%1。
			}
			// 冷笑感恶寒走。
			pc.sendPackets(new S_ServerMessage(1080));
		}
		// “欠片
		else if (s.equalsIgnoreCase("3")) {
			pc.addKarma((int) (-50000 * Config.RATE_KARMA));
			item = pc.getInventory().storeItem(40678, 100);
			if (item != null) {
				pc.sendPackets(new S_ServerMessage(143, npc.getNpcTemplate()
						.get_name(), item.getLogName())); // \f1%0%1。
			}
			// 冷笑感恶寒走。
			pc.sendPackets(new S_ServerMessage(1080));
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

	private void repairGate(L1PcInstance pc) {
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan != null) {
			int castleId = clan.getCastleId();
			if (castleId != 0) { // 城主クラン
				if (!WarTimeController.getInstance().isNowWar(castleId)) {
					// 城门を元に戻す
					for (L1DoorInstance door : DoorSpawnTable.getInstance()
							.getDoorList()) {
						if (L1CastleLocation.checkInWarArea(castleId, door)) {
							door.repairGate();
						}
					}
					pc.sendPackets(new S_ServerMessage(990)); // 城门自动修理を命令しました。
				} else {
					pc.sendPackets(new S_ServerMessage(991)); // 城门自动修理命令を取り消しました。
				}
			}
		}
	}

	@Override
	public String getType() {
		return C_NPC_ACTION;
	}

}
