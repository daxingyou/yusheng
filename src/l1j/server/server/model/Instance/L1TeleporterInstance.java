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

package l1j.server.server.model.Instance;

import java.util.Random;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.world.L1World;

// Referenced classes of package l1j.server.server.model:
// L1NpcInstance, L1Teleport, L1NpcTalkData, L1PcInstance,
// L1TeleporterPrices, L1TeleportLocations

public class L1TeleporterInstance extends L1NpcInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public L1TeleporterInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance player) {
		L1Attack attack = new L1Attack(player, this);
		attack.calcHit();
		attack.action();
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		int npcid = getNpcTemplate().get_npcId();
		L1Quest quest = player.getQuest();
		String htmlid = null;

		if (talking != null) {
			//变更成 switch 
			switch (npcid)
			{
				case 50014: { // 
				if (player.isWizard()) { // 
					if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1
							&& !player.getInventory().checkItem(40579)) { // 骨
						htmlid = "dilong1";
					} else {
						htmlid = "dilong3";
					}
				}
				}
				break;
				case 70779: { // 
				if (player.getTempCharGfx() == 1037) { // 变身
					htmlid = "ants3";
				} else if (player.getTempCharGfx() == 1039) {// 变身
					if (player.isCrown()) { // 君主
						if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1) {
							if (player.getInventory().checkItem(40547)) { // 住民遗品
								htmlid = "antsn";
							} else {
								htmlid = "ants1";
							}
						} else { // Step1以外
							htmlid = "antsn";
						}
					} else { // 君主以外
						htmlid = "antsn";
					}
				}
				}
				break;
				case 70853: { // 
				if (player.isElf()) { // 
					if (quest.get_step(L1Quest.QUEST_LEVEL30) == 1) {
						if (!player.getInventory().checkItem(40592)) { // 咒精灵书
							Random random = new Random();
							if (random.nextInt(100) < 50) { // 50%
								htmlid = "fairyp2";
							} else { // 
								htmlid = "fairyp1";
							}
						}
					}
				}
				}
				break;
				case 50031: { // 
				if (player.isElf()) { // 
					if (quest.get_step(L1Quest.QUEST_LEVEL45) == 2) {
						if (!player.getInventory().checkItem(40602)) { // 
							htmlid = "sepia1";
						}
					}
				}
					// 其他职业 
					else {
						htmlid = "sepia3";
					}
					// 其他职业  end
				}
				break;
				case 50043: { // 
				if (quest.get_step(L1Quest.QUEST_LEVEL50) == L1Quest.QUEST_END) {
					htmlid = "ramuda2";
				} else if (quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // 同意济
					if (player.isCrown()) { // 君主
						if (_isNowDely) { // 中
							htmlid = "ramuda4";
						} else {
							htmlid = "ramudap1";
						}
					} else { // 君主以外
						htmlid = "ramuda1";
					}
				} else {
					htmlid = "ramuda3";
				}	
				}
				break;
				case 50082: {if (player.getLevel() < 13) {
					htmlid = "en0221";
				} else {
					if (player.isElf()) {
						htmlid = "en0222e";
					} else if (player.isDarkelf()) {
						htmlid = "en0222d";
					} else {
						htmlid = "en0222";
					}
				}
				}
				break;
				case 50001: {
				if (player.isElf()) {
					htmlid = "barnia3";
				} else if (player.isKnight() || player.isCrown()) {
					htmlid = "barnia2";
				} else if (player.isWizard() || player.isDarkelf()) {
					htmlid = "barnia1";
				}
				}
				break;
				case 50006: {//隐藏之谷-小柯 
				if (player.isCrown() || player.isWizard()) {//王族或法师
					htmlid = "coco1";
				} else if (player.isElf()) {//妖精
					htmlid = "coco3";
				} else if (player.isDarkelf()) {//黑妖
					htmlid = "coco5";
				} else {
					htmlid = "coco2";
				}
				}
				break;
				case 50055: {//银骑村-德瑞斯特 
				if (player.getLevel() < 13) {//等级13以下
					htmlid = "drist";
				} else {
					htmlid = "drist1";
				}
				}
				break;
				case 50002: {//隐藏之谷-史盖 
				if (player.isCrown() || player.isWizard()) {//王族或法师
					htmlid = "sky1";
				} else if (player.isElf()) {//妖精
					htmlid = "sky3";
				} else if (player.isDarkelf()) {//黑妖
					htmlid = "sky5";
				} else {
					htmlid = "sky2";
				}
				}
				break;
				case 50003: {//隐藏之谷-伊尔卓斯 
				if (player.getLevel() < 10) {//等级13以下
					htmlid = "illdrath";
				} else {
					htmlid = "illdrath1";
				}
				}
				break;
				case 50005: {//隐藏之谷-卡连 
				if (player.isDarkelf()) {//黑妖
					htmlid = "karen1";
				} else {
					htmlid = "karen2";
				}
				}
				break;
				case 50069: {//沉默洞穴-艾纳 
				if (player.getLevel() >= 13) {//等级13以下
					htmlid = "enya4";
				} else if (player.isDarkelf()) {//黑妖
					htmlid = "enya1";
				} else {
					htmlid = "enya2";
				}
				}
				break;
				case 50016: {//说话之岛-杰诺 
				if (player.getLevel() >= 13) {//等级13以下
					htmlid = "zeno2";
				} else {
					htmlid = "zeno";
				}
				}
				break;
				case 50011: {//歌唱之岛-昆 
				if (player.isKnight()) {//骑士
					htmlid = "kun2";
				} else if (player.isElf()) {//妖精
					htmlid = "kun3";
				} else if (player.isDarkelf()) {//黑妖
					htmlid = "kun5";
				} else {
					htmlid = "kun1";
				}
				}
				break;
				case 50012: {//歌唱之岛-奇耀 
				if (player.isKnight()) {//骑士
					htmlid = "kiyari2";
				} else if (player.isElf()) {//妖精
					htmlid = "kiyari3";
				} else if (player.isDarkelf()) {//黑妖
					htmlid = "kiyari5";
				} else {
					htmlid = "kiyari1";
				}
				}
				break;
				case l1j.william.New_Id.Npc_AJ_2_12: { // 堕落的灵魂 
					if (player.isDarkelf()) { // 黑妖
						if (player.getLevel() >= 50 && player.getInventory().checkEquipped(20037)) { // 等级大于等于50
							if (quest.isEnd(L1Quest.QUEST_LEVEL45)) { // 已完成45级试炼任务
								int lv50_step = quest
									.get_step(L1Quest.QUEST_LEVEL50);
								if (lv50_step == 2) { // 已获得真实的面具
									htmlid = "csoulq1";
								} else {
									htmlid = "csoulq3";
								}
							} else {
								htmlid = "csoulqn";
							}
						} else {
							htmlid = "csoulq2";
						}
					}
				}
				break;
			}
			//变更成 switch  end

			// html表示
			if (htmlid != null) { // htmlid指定场合
				player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
			} else {
				if (player.getLawful() < -1000) { // 
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
				} else {
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}
		} else {
			_log.info((new StringBuilder())
					.append("No actions for npc id : ").append(objid)
					.toString());
		}
	}

	@Override
	public void onFinalAction(L1PcInstance player, String action) {
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		if (action.equalsIgnoreCase("teleportURL")) {
			L1NpcHtml html = new L1NpcHtml(talking.getTeleportURL());
			// 删除player.sendPackets(new S_NPCTalkReturn(objid, html));
			// 修正传送师显示传送金额 
			String[] price = null;
			int npcid = getNpcTemplate().get_npcId();
			switch(npcid)
			{
				case 50015: { // 说话岛-魔法师卢卡斯
					price = new String[]{"2100"};
				}
				break;
				case 50020: { // 肯特村-魔法师史坦利
					price = new String[]{ "75","75","75","180","180","180","180","270","270","300","300","900","10650" };
				}
				break;
				case 50024: { // 古鲁丁-魔法师史提夫
					price = new String[]{ "70","70","70","168","168","252","252","336","336","280","280","700","9520" };
				}
				break;
				case 50036: { // 奇岩村-魔法师尔玛
					price = new String[]{ "75","75","75","180","180","180","180","270","270","450","450","1050","11100" };
				}
				break;
				case 50039: { // 威顿村-魔法师莱思利
					price = new String[]{ "75","75","180","180","270","270","270","360","360","600","600","1200","11550" };
				}
				break;
				case 50044: { // 亚丁城-魔法师西里乌斯
					price = new String[]{ "50","120","120","180","180","180","240","240","300","500","500","900","7400" };
				}
				break;
				case 50046: { // 亚丁城-魔法师艾勒里斯
					price = new String[]{ "50","120","120","120","180","180","180","240","300","500","500","900","7400" };
				}
				break;
				case 50051: { // 欧瑞村-魔法师吉利乌斯
					price = new String[]{ "75","180","270","270","360","360","360","450","450","750","750","1350","12000" };
				}
				break;
				case 50054: { // 风木村-魔法师特莱
					price = new String[]{ "75","75","75","180","180","270","270","360","450","300","300","750","9750" };
				}
				break;
				case 50056: { // 银骑村-魔法师麦特
					price = new String[]{"75","75","75","180","180","180","270","270","360","450","450","1050","10200"};
				}
				break;
				case 50066: { // 海音村-魔法师里欧
					price = new String[]{ "55","55","55","132","132","132","198","198","264","440","440","880","7810" };
				}
				break;
				case 50068: { // 沉默洞穴-迪亚诺斯
					price = new String[]{ "1500","800","600","1800","1800","1000" };
				}
				break;
				case 50026: { // 古鲁丁-商店村传送师(内)
					price = new String[]{ "550","700","810"};
				}
				break;
				case 50033: { // 奇岩-商店村传送师(内)
					price = new String[]{ "560","720","560"};
				}
				break;
				case 50049: { // 欧瑞-商店村传送师(内)
					price = new String[]{ "1150","980","590"};
				}
				break;
				case 50059: { // 银骑村-商店村传送师(内)
					price = new String[]{ "580","680","680"};
				}
				break;
				default: {
					price = new String[]{""};
				}
			}
			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
			// 修正传送师显示传送金额  end
		} /*删除else if (action.equalsIgnoreCase("teleportURLA")) {
			L1NpcHtml html = new L1NpcHtml(talking.getTeleportURLA());
			player.sendPackets(new S_NPCTalkReturn(objid, html));
		}删除*/
		// 传送师狩猎区设定 
		else if (action.equalsIgnoreCase("teleportURLA")) {
			// 删除L1NpcHtml html = new L1NpcHtml(talking.getTeleportURL());
			// 删除player.sendPackets(new S_NPCTalkReturn(objid, html));
			// 修正传送师显示传送金额 
			String html = "";
			String[] price = null;
			int npcid = getNpcTemplate().get_npcId();
			switch(npcid)
			{
				case 50020: { // 魔法师史坦利
					html = "telekent3";
					price = new String[]{ "150","330","330","330","330","330","495","495","495","660","660" };
				}
				break;
				case 50024: { // 魔法师史提夫
					html = "telegludin3";
					price = new String[]{ "140","308","308","308","462","462","462","462","616","770","770" };
				}
				break;
				case 50036: { // 魔法师尔玛
					html = "telegiran3";
					price = new String[]{ "150","150","150","330","330","330","330","495","495","495","660" };
				}
				break;
				case 50039: { // 魔法师莱思利
					html = "televala3";
					price = new String[]{ "150","330","330","330","495","495","495","495","495","660","660" };
				}
				break;
				case 50044: { // 魔法师西里乌斯
					html = "sirius3";
					price = new String[]{ "100","220","220","220","330","330","440","440","550","550","550" };
				}
				break;
				case 50051: { // 魔法师吉利乌斯
					html = "kirius3";
					price = new String[]{ "150","330","495","495","495","660","660","825","825","825","825" };
				}
				break;
				case 50046: { // 魔法师艾勒里斯
					html = "elleris3";
					price = new String[]{ "100","220","220","220","330","330","440","440","550","550","550" };
				}
				break;
				case 50054: { // 魔法师特莱
					html = "telewoods3";
					price = new String[]{ "150","150","330","330","495","495","495","495","660","825","825" };
				}
				break;
				case 50056: { // 魔法师麦特
					html = "telesilver3";
					price = new String[]{ "150","150","330","330","330","330","495","495","495","495","495" };
				}
				break;
				case 50066: { // 魔法师里欧
					html = "teleheine3";
					price = new String[]{ "110","110","242","242","242","242","363","363","484","484","605" };
				}
				break;
				default: {
					price = new String[]{""};
				}
			}
			player.sendPackets(new S_NPCTalkReturn(objid, html, price));
			// 修正传送师显示传送金额  end
		}
		// 传送师狩猎区设定  end
		if (action.startsWith("teleport ")) {
//			_log.info((new StringBuilder()).append("Setting action to : ")
//					.append(action).toString());
			doFinalAction(player, action);
		}
	}

	private void doFinalAction(L1PcInstance player, String action) {
		int objid = getId();

		int npcid = getNpcTemplate().get_npcId();
		String htmlid = null;
		boolean isTeleport = true;

		if (npcid == 50014) { // 
			if (!player.getInventory().checkItem(40581)) { // 
				isTeleport = false;
				htmlid = "dilongn";
			}
		} else if (npcid == 50043) { // 
			if (_isNowDely) { // 中
				isTeleport = false;
			}
		} else if (npcid == 50625) { // 古代人（Lv50古代空间2F）
			if (_isNowDely) { // 中
				isTeleport = false;
			}
		}

		if (isTeleport) { // 实行
			try {
				// (君主Lv30)
				if (action.equalsIgnoreCase("teleport mutant-dungen")) {
					/*删除// 3以内Pc
					for (L1PcInstance otherPc : L1World.getInstance()
							.getVisiblePlayer(player, 3)) {
						if (otherPc.getClanid() == player.getClanid()
								&& otherPc.getId() != player.getId()) {
							L1Teleport.teleport(otherPc, 32740, 32800, (short) 217, 5,
									true);
						}
					}
					L1Teleport.teleport(player, 32740, 32800, (short) 217, 5,
							true);删除*/
					// 修正变种蚁洞有人在试炼则无法进入 
					if (player.isCrown()) {
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if (pc.getMapId() == 217) {
								htmlid = "antsn";
							} else {
								L1Teleport.teleport(player, 32686, 32791, (short) 217, 5, true);
							}
						}
					}
					// 修正变种蚁洞有人在试炼则无法进入  end
				}
				// 试练（Lv30）
				else if (action.equalsIgnoreCase("teleport mage-quest-dungen")) {
					L1Teleport.teleport(player, 32791, 32788, (short) 201, 5,
							true);
				} else if (action.equalsIgnoreCase("teleport 29")) { // 
					L1PcInstance kni = null;
					L1PcInstance elf = null;
					L1PcInstance wiz = null;
					// 3以内Pc
					for (L1PcInstance otherPc : L1World.getInstance()
							.getVisiblePlayer(player, 3)) {
						L1Quest quest = otherPc.getQuest();
						if (otherPc.isKnight() // 
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // 同意济
							if (kni == null) {
								kni = otherPc;
							}
						} else if (otherPc.isElf() // 
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // 同意济
							if (elf == null) {
								elf = otherPc;
							}
						} else if (otherPc.isWizard() // 
								&& quest.get_step(L1Quest.QUEST_LEVEL50) == 1) { // 同意济
							if (wiz == null) {
								wiz = otherPc;
							}
						}
					}
					if (kni != null && elf != null && wiz != null) { // 全揃
						L1Teleport.teleport(player, 32723, 32850, (short) 2000,
								2, true);
						L1Teleport.teleport(kni, 32750, 32851, (short) 2000, 6,
								true);
						L1Teleport.teleport(elf, 32878, 32980, (short) 2000, 6,
								true);
						L1Teleport.teleport(wiz, 32876, 33003, (short) 2000, 0,
								true);
						TeleportDelyTimer timer = new TeleportDelyTimer();
						GeneralThreadPool.getInstance().execute(timer);
					}
				} else if (action.equalsIgnoreCase("teleport barlog")) { // 古代人（Lv50古代空间2F）
					L1Teleport.teleport(player, 32755, 32844, (short) 2002, 5,
							true);
					TeleportDelyTimer timer = new TeleportDelyTimer();
					GeneralThreadPool.getInstance().execute(timer);
				}
				//说话之岛-杰诺传送歌唱之岛 
				else if (action.equalsIgnoreCase("teleport singing-island")) {
					if (player.getLevel() < 13) {
						L1Teleport.teleport(player, 32785, 32781, (short) 68, 5, true);
					} else {
						htmlid = "zeno1";
					}
				}
				//说话之岛-杰诺传送歌唱之岛  end
				// 堕落的灵魂-传送邪念地监 
				else if (action.equalsIgnoreCase("teleport evil-dungeon")) {
					if (player.isDarkelf() && player.getInventory().checkItem(20037)) {
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if (pc.getMapId() == 306) {
								htmlid = "csoulq3";
							} else {
								L1Teleport.teleport(player, 32707, 32790, (short) 306, 5, true);
							}
						}
					} else {
						htmlid = "csoulqn";
					}
				}
				// 堕落的灵魂-传送邪念地监  end
				// 赛菲亚-传送赛菲亚之罪房间 
				else if (action.equalsIgnoreCase("teleport sepia-dungen")) {
					if (player.isElf()) {
						if (player.getQuest().get_step(L1Quest.QUEST_LEVEL45) == 2) {
							if (!player.getInventory().checkItem(40602)) { // 
								for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
									if (pc.getMapId() == 302) {
										htmlid = "sepia3";
									} else {
										L1Teleport.teleport(player, 32740, 32860, (short) 302, 5, true);
									}
								}
							}
						}
					} else {
						htmlid = "sepia3";
					}
				}
				// 赛菲亚-传送赛菲亚之罪房间  end
				// 妖森鲁比恩-传送新手村-隐藏之谷 
				else if (action.equalsIgnoreCase("teleport valley-in")) {
					if (player.getLevel() < 13) {
						L1Teleport.teleport(player, 32694, 32855, (short) 69, 5, true);
					}
				}
				// 妖森鲁比恩-传送新手村-隐藏之谷  end
			} catch (Exception e) {
			}
		}
		if (htmlid != null) { // 表示html场合
			player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
		}
	}

	class TeleportDelyTimer implements Runnable {

		public TeleportDelyTimer() {
		}

		public void run() {
			try {
				_isNowDely = true;
				Thread.sleep(900000); // 15分
			} catch (Exception e) {
				_isNowDely = false;
			}
			_isNowDely = false;
		}
	}

	private boolean _isNowDely = false;
	private static final Log _log = LogFactory.getLog(L1TeleporterInstance.class);


}