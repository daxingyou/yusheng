/* This program is free software; you can redistribute it and/or modify
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
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

import l1j.gui.J_Main;
import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.ActionCodes;
import l1j.server.server.WarTimeController;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.GetBackRestartTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.lock.CharBookReading;
import l1j.server.server.datatables.lock.CharSkillReading;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.Getback;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1War;
import l1j.server.server.model.Instance.L1BiaoCheInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_BookMarkLogin;
import l1j.server.server.serverpackets.S_CharResetInfo;
import l1j.server.server.serverpackets.S_CharacterConfig;
import l1j.server.server.serverpackets.S_Dexup;
import l1j.server.server.serverpackets.S_EnterGame;
import l1j.server.server.serverpackets.S_EquipmentWindow;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_InvList;
import l1j.server.server.serverpackets.S_Karma;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_MapID;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharPack;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_Strup;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_War;
import l1j.server.server.serverpackets.S_Weather;
import l1j.server.server.serverpackets.S_bonusstats;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.templates.L1GetBackRestart;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.templates.L1UserSkillTmp;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.world.L1World;
import l1j.william.L1LastOnline;
import l1j.william.L1WilliamSystemMessage;
import l1j.william.LastOnline;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket
//
public class C_LoginToServer extends ClientBasePacket {

	private static final String C_LOGIN_TO_SERVER = "[C] C_LoginToServer";

	private static final Log _log = LogFactory.getLog(C_LoginToServer.class);

	private static final ReentrantLock LOCK = new ReentrantLock(true);

	private Random _random = new Random();

	public C_LoginToServer(byte abyte0[], LineageClient _client)
			throws FileNotFoundException, Exception {

		super(abyte0);

		LOCK.lock();
		System.out.println("登录锁打开");
		try {
			String login = _client.getAccountName();

			String charName = readS();
			L1PcInstance pc = L1PcInstance.load(charName);
			if (pc == null || !login.equals(pc.getAccountName())) {
				_log.info("【无法登入】 角色名称: " + charName + " 帐号: " + login
						+ " 位址: " + _client.getHostname());
				_client.kick();
				return;
			}
			if (_client.getActiveChar() != null) {
				_log.info("帐号重复登入人物type1: " + charName + "强制中断连线");
				_client.kick();
				return;
			}
			if (pc.getOnlineStatus() > 0) {
				_client.kick();
				_log.info("帐号重复登入人物type2: " + charName + "强制中断连线");
				return;
			}

			if (pc.getBind()) {
				_log.info("角色绑定: " + charName + "强制中断连线");
				return;
			}

			_log.info("【登入游戏】 角色名称: " + charName + " 帐号: " + login + " 位址: "
					+ _client.getHostname());

			// pc.setSjName(changename());
			pc.setOnlineStatus(1);
			CharacterTable.updateOnlineStatus(pc);
			L1World.getInstance().storeWorldObject(pc);

			pc.setNetConnection(_client);
			// pc.setPacketOutput(client);
			_client.setActiveChar(pc);

			// 初始能力加成
			/* pc.sendPackets(new S_InitialAbilityGrowth(pc)); */

			_client.sendPacket(new S_EnterGame());
			// items(pc);

			bookmarks(pc);

			// 先getback_restart指定移动
			GetBackRestartTable gbrTable = GetBackRestartTable.getInstance();
			L1GetBackRestart[] gbrList = gbrTable.getGetBackRestartTableList();
			for (L1GetBackRestart gbr : gbrList) {
				if (pc.getMapId() == gbr.get_area()) {
					pc.setX(gbr.get_locx());
					pc.setY(gbr.get_locy());
					pc.setMap(gbr.get_mapid());
					break;
				}
			}

			// altsettings.propertiesGetBacktrue街移动
			if (Config.GET_BACK) {
				int[] loc = Getback.GetBack_Location(pc, true);
				pc.setX(loc[0]);
				pc.setY(loc[1]);
				pc.setMap((short) loc[2]);
			}

			// 战争中旗内居场合、城主血盟场合扫还。
			int castle_id = L1CastleLocation.getCastleIdByArea(pc);
			if (0 < castle_id) {
				int[] loc = new int[3];
				loc = L1CastleLocation.getGetBackLoc(castle_id);
				pc.setX(loc[0]);
				pc.setY(loc[1]);
				pc.setMap((short) loc[2]);
				/*
				 * if (WarTimeController.getInstance().isNowWar(castle_id)) {
				 * L1Clan clan =
				 * L1World.getInstance().getClan(pc.getClanname()); if (clan !=
				 * null) { if (clan.getCastleId() != castle_id) { // 城主
				 * int[] loc = new int[3]; loc =
				 * L1CastleLocation.getGetBackLoc(castle_id); pc.setX(loc[0]);
				 * pc.setY(loc[1]); pc.setMap((short) loc[2]); } } else { //
				 * 所属居场合扫还 int[] loc = new int[3]; loc =
				 * L1CastleLocation.getGetBackLoc(castle_id); pc.setX(loc[0]);
				 * pc.setY(loc[1]); pc.setMap((short) loc[2]); } }
				 */
			}

			L1World.getInstance().addVisibleObject(pc);

			pc.beginGameTimeCarrier();

			if (pc.getCurrentHp() > 0) {
				pc.setDead(false);
				pc.setStatus(0);
			} else {
				pc.setStatus(ActionCodes.ACTION_Die);
				pc.setDead(true);
			}

			final S_OwnCharStatus s_owncharstatus = new S_OwnCharStatus(pc);
			_client.sendPacket(s_owncharstatus);

			final S_MapID s_mapid = new S_MapID(pc.getMapId(), pc);
			_client.sendPacket(s_mapid);

			final S_OwnCharPack s_owncharpack = new S_OwnCharPack(pc);
			_client.sendPacket(s_owncharpack);

			pc.sendPackets(new S_SPMR(pc));

			// XXX 情报S_OwnCharPack含多分不要
			// final S_CharTitle s_charTitle = new S_CharTitle(pc.getId(),
			// pc.getTitle());
			// _client.sendPacket(s_charTitle);
			// pc.broadcastPacket(s_charTitle);

			pc.sendVisualEffectAtLogin(); // 、毒、水中等视觉效果表示

			_client.sendPacket(new S_Weather(L1World.getInstance().getWeather()));

			items(pc);
			skills(pc);
			buff(_client, pc);
			/*
			 * S_ActiveSpells s_activespells = new S_ActiveSpells(pc);
			 * client.sendPacket(s_activespells);
			 */
			
			/** 生存的呐喊  **/
			if (pc.get_food() >= 225) {// LOLI 生存呐喊
				final Calendar cal = Calendar.getInstance();
				long h_time = cal.getTimeInMillis() / 1000;// 换算为秒
				pc.set_h_time(h_time);// 纪录登入时间
			}
			/** 生存的呐喊  **/

			// 最后在线时间
			if (Config.EXP_DOUBLE) {
				int count = LastOnline.getInstance().countCharacterConfig(
						pc.getId());
				if (count != 0) {
					LastOnline lastonline = new LastOnline();
					for (L1LastOnline lasttime : lastonline.getLastOnlineList()) {
						if (lasttime.getId() == pc.getId()) {
							Timestamp lastTime = lasttime.getLastTime();
							if (lastTime != null) {
								Calendar cal = Calendar.getInstance();

								if (((cal.getTimeInMillis() - lastTime
										.getTime()) / 1000) / 3600 > 0) { // 满一小时
									int time = (int) ((cal.getTimeInMillis() - lastTime
											.getTime()) / 1000) / 3600; // 数值转换

									// 加倍时间(分钟) = 离线时间(小时) * 5
									if (!pc.hasSkillEffect(l1j.william.New_Id.Skill_AJ_0_3)) {
										pc.setSkillEffect(
												l1j.william.New_Id.Skill_AJ_0_3,
												(time * 3 * 60) * 1000);
										pc.sendPackets(new S_ServerMessage(
												403,
												L1WilliamSystemMessage
														.ShowMessage(1108)
														+ ": ( "
														+ (time * 3)
														+ " )"
														+ L1WilliamSystemMessage
																.ShowMessage(1109)));
									}
								}
							}
						}
					}
					LastOnline.getInstance().updateCharacterConfig(pc);
				} else {
					LastOnline.getInstance().storeCharacterConfig(pc);
				}
			}
			if (!pc.hasSkillEffect(L1SkillId.AITIME)
					&& !pc.hasSkillEffect(L1SkillId.WAITTIME)) {
				int time = Config.MINAITIME + _random.nextInt(Config.MAXAITIME);
				pc.setSkillEffect(L1SkillId.AITIME, time * 1000);
			}
			// 最后在线时间 end

			/*
			 * if (!pc.hasSkillEffect(l1j.william.New_Id.Skill_AJ_0_1)) {//获得道具
			 * pc.setSkillEffect(l1j.william.New_Id.Skill_AJ_0_1, 180 *
			 * 1000);//3分钟 }
			 */

			if (pc.getLevel() >= 51 && pc.getLevel() - 50 > pc.getBonusStats()) {
				if ((pc.getBaseStr() + pc.getBaseDex() + pc.getBaseCon()
						+ pc.getBaseInt() + pc.getBaseWis() + pc.getBaseCha()) < (Config.BONUS_STATS1 * 6)) { // 调整能力值上限
					_client.sendPacket(new S_bonusstats(pc.getId(), 1));
				}
			}

			if (Config.CHARACTER_CONFIG_IN_SERVER_SIDE) {
				_client.sendPacket(new S_CharacterConfig(pc.getId()));
			}

			serchSummon(pc);

			WarTimeController.getInstance().checkCastleWar(pc);

			if (pc.getClanid() != 0) { // 所属中
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				if (clan != null) {
					if (pc.getId() == clan.getLeaderId()) {
						pc.setClanRank(L1Clan.CLAN_RANK_PRINCE);
					} else if (pc.getClanRank() == 0) {
						pc.setClanRank(L1Clan.CLAN_RANK_PUBLIC);
					}
					if (pc.getClanid() == clan.getClanId() && // 解散、再度、同名创设时对策
							pc.getClanname().toLowerCase()
									.equals(clan.getClanName().toLowerCase())) {
						L1PcInstance[] clanMembers = clan.getOnlineClanMember();
						for (L1PcInstance clanMember : clanMembers) {
							if (clanMember.getId() != pc.getId()) {
								clanMember.sendPackets(new S_ServerMessage(843,
										pc.getName())); // 只今、血盟员%0%s接续。
							}
						}

						// 全战争取得
						for (L1War war : L1World.getInstance().getWarList()) {
							boolean ret = war.CheckClanInWar(pc.getClanname());
							if (ret) { // 战争参加中
								String enemy_clan_name = war
										.GetEnemyClanName(pc.getClanname());
								if (enemy_clan_name != null) {
									// 血盟现在_血盟交战中。
									_client.sendPacket(new S_War(8, pc
											.getClanname(), enemy_clan_name));
								}
								break;
							}
						}

						if (clan.isClanskill()) {
							String msg = null;
							switch (clan.getSkillLevel()) {
							case 1:// Lv1
								msg = "经验值+10% 回血+1 回蓝+1 额外伤害+1 魔攻+1";
								break;
							case 2:// Lv2
								msg = "经验值+20% 回血+2 回蓝+2 魔防+3";
								break;
							case 3:// Lv3
								msg = "经验值+30% 回血+3 回蓝+3 魔防+5";
								break;
							case 4:// Lv4
								msg = "经验值+40% 回血+4 回蓝+4 魔防+7 减伤+1 魔攻+1 额外伤害+1";
								break;
							case 5:// Lv5
								msg = "经验值+50% 回血+5 回蓝+5 魔防+10 减伤+2 魔攻+2 额外伤害+2";
								break;
							}
							if (msg != null) {
								pc.sendPackets(new S_SystemMessage(String
										.format("\\F1血盟技能Lv%d %s",
												clan.getSkillLevel(), msg)));
							}
						}
					} else {
						pc.setClanid(0);
						pc.setClanname("");
						pc.setClanRank(0);
						pc.save(); // DB情报书迂
					}
				}
			}

			if (pc.getPartnerId() != 0) { // 结婚中
				L1PcInstance partner = (L1PcInstance) L1World.getInstance()
						.findObject(pc.getPartnerId());
				if (partner != null && partner.getPartnerId() != 0) {
					if (pc.getPartnerId() == partner.getId()
							&& partner.getPartnerId() == pc.getId()) {
						_client.sendPacket(new S_ServerMessage(548)); // 今中。
						partner.sendPackets(new S_ServerMessage(549)); // 今。
					}
				}
			}

			pc.startHpRegeneration();
			pc.startMpRegeneration();
			pc.startObjectAutoUpdate();
			// pc.beginExpMonitor();
			pc.save(); // DB情报书迂

			pc.sendPackets(new S_OwnCharStatus(pc));
			pc.sendPackets(new S_CharResetInfo(pc));

			if (pc.getHellTime() > 0) {
				pc.beginHell(false);
			}
			J_Main.getInstance().addPlayerTable(login, charName,
					_client.getHostname(), _client);// GUI
			_client.CharReStart(false);
			WriteLogTxt.Recording("经验记录", "玩家:" + pc.getName() + "登录游戏时经验值为("
					+ pc.getExp() + ")");
			if (Config.AI_ONLIN) {
				if(pc.getLevel() > Config.AI_ONLIN_LEVEL){
					pc.setSkillEffect(L1SkillId.AI_ONLIN_SKILLID,
							Config.AI_ONLIN_TIME * 60 * 1000);
				}

			}
			/*
			 * if (pc.get_food() >= 225) {// LOLI 生存呐喊 final Calendar cal =
			 * Calendar.getInstance(); long h_time = cal.getTimeInMillis() /
			 * 1000;// 换算为秒 pc.set_h_time(h_time);// 纪录登入时间 }
			 */
			pc.setLoginToServer(true); // 设置为登陆游戏世界状态
			
			// **** vip 系统  **** // 
			if (!pc.getInventory().checkEquipped(500050) && !pc.getInventory().checkEquipped(500051)) {
				vipjia(pc);
			}
			
			pc.sendPackets(new S_Karma(pc)); // 友好度
	        /* 闪避率 */
//	        pc.sendPackets(new S_PacketBox(88, pc.getDodge())); // 正
//	        pc.sendPackets(new S_PacketBox(101, pc.getNdodge())); // 负
	        /* 闪避率 */
			
			for (L1BiaoCheInstance biaoChe : L1World.getInstance()
					.getAllBiaoCheValues()) {
				if (biaoChe.getMaster() == null) {
					continue;
				}
				if (biaoChe.getMaster().getId() == pc.getId()) {
					pc.setBiaoChe(biaoChe);
					biaoChe.setMaster(pc);
					pc.sendPackets(new S_SystemMessage(String.format(
							"\\F1你正在拉镖 你的镖车位置%d,%d", biaoChe.getX(),
							biaoChe.getY())));
					break;
				}
			}
//			if (!_client.getAccount().isCheckTwopassword()) {
//				pc.setCheckTwopassword(true);
//				if (_client.getAccount().getTwoPassword() == -256) {
//					pc.sendPackets(new S_SystemMessage(
//							"\\F3**您好!为了您账号安全,第一次登陆游戏请在聊天框内输入6位数字二级密码,防止盗号！请您一定牢记二级密码 不然无法正常游戏. 谢谢您**"));
//					// pc.sendPackets(new S_ServerMessage(842));
//				} else {
//					pc.sendPackets(new S_SystemMessage(
//							"\\F3**您好,请在聊天框输入您设置过的二级保护密码,验证成功才可正常游戏**"));
//					// pc.sendPackets(new S_ServerMessage(834));
//				}
//			}

			pc.getBlessEnchant().loadBlessEnchant();

		} catch (final Exception e) {
			// _log.error(e.getLocalizedMessage(), e);
		} finally {
			try {
				this.over();
			} finally {
				LOCK.unlock();
				System.out.println("登录锁解开");
			}
		}
	}

	/**
	 * 武器防具装备中的显示.
	 */
	private void equipmentWindow(L1PcInstance pc, List<L1ItemInstance> items) {
		for (final L1ItemInstance item : items) {
			if (item.isEquipped()) {
				switch (item.getItem().getUseType()) {
				case 1: // 武器
					pc.sendPackets(new S_EquipmentWindow(item.getId(),
							S_EquipmentWindow.EQUIPMENT_INDEX_WEAPON, true));
					break;
				case 2: // 盔甲
					pc.sendPackets(new S_EquipmentWindow(item.getId(),
							S_EquipmentWindow.EQUIPMENT_INDEX_ARMOR, true));
					break;
				case 18: // T恤
					pc.sendPackets(new S_EquipmentWindow(item.getId(),
							S_EquipmentWindow.EQUIPMENT_INDEX_T, true));
					break;
				case 19: // 斗篷
					pc.sendPackets(new S_EquipmentWindow(item.getId(),
							S_EquipmentWindow.EQUIPMENT_INDEX_CLOAK, true));
					break;
				case 20: // 手套
					pc.sendPackets(new S_EquipmentWindow(item.getId(),
							S_EquipmentWindow.EQUIPMENT_INDEX_GLOVE, true));
					break;
				case 21: // 长靴
					pc.sendPackets(new S_EquipmentWindow(item.getId(),
							S_EquipmentWindow.EQUIPMENT_INDEX_BOOTS, true));
					break;
				case 22: // 头盔
					pc.sendPackets(new S_EquipmentWindow(item.getId(),
							S_EquipmentWindow.EQUIPMENT_INDEX_HEML, true));
					break;
				case 23: // 戒指
					// final int type = armor.getItem().getType();
					// if (pc.getInventory().getTypeEquipped(2, 9) == 1) {
					if (pc.getEquipmentRing1ID() == 0) {
						pc.setEquipmentRing1ID(item.getId());
						pc.sendPackets(new S_EquipmentWindow(pc
								.getEquipmentRing1ID(),
								S_EquipmentWindow.EQUIPMENT_INDEX_RING1, true));
					} else if (pc.getEquipmentRing2ID() == 0
							&& item.getId() != pc.getEquipmentRing1ID()) {
						pc.setEquipmentRing2ID(item.getId());
						pc.sendPackets(new S_EquipmentWindow(pc
								.getEquipmentRing2ID(),
								S_EquipmentWindow.EQUIPMENT_INDEX_RING2, true));
					}
					break;
				case 24: // 项链
					pc.sendPackets(new S_EquipmentWindow(item.getId(),
							S_EquipmentWindow.EQUIPMENT_INDEX_NECKLACE, true));
					break;
				case 25: // 盾牌
					pc.sendPackets(new S_EquipmentWindow(item.getId(),
							S_EquipmentWindow.EQUIPMENT_INDEX_SHIELD, true));
					break;
				case 37: // 腰带
					pc.sendPackets(new S_EquipmentWindow(item.getId(),
							S_EquipmentWindow.EQUIPMENT_INDEX_BELT, true));
					break;
				case 40: // 耳环
					pc.sendPackets(new S_EquipmentWindow(item.getId(),
							S_EquipmentWindow.EQUIPMENT_INDEX_EARRING, true));
					break;
				case 43: // 副助道具-右
					pc.sendPackets(new S_EquipmentWindow(item.getId(),
							S_EquipmentWindow.EQUIPMENT_INDEX_AMULET1, true));
					break;
				case 44: // VIP戒指
					pc.sendPackets(new S_EquipmentWindow(item.getId(),
							S_EquipmentWindow.EQUIPMENT_INDEX_RING3, true));
					break;
				case 45: // VIP戒指
					pc.sendPackets(new S_EquipmentWindow(item.getId(),
							S_EquipmentWindow.EQUIPMENT_INDEX_RING4, true));
					break;
				case 48: // 副助道具-右下
					pc.sendPackets(new S_EquipmentWindow(item.getId(),
							S_EquipmentWindow.EQUIPMENT_INDEX_AMULET4, true));
					break;
				case 47: // 副助道具-左下
					pc.sendPackets(new S_EquipmentWindow(item.getId(),
							S_EquipmentWindow.EQUIPMENT_INDEX_AMULET5, true));
					break;
				default:
					break;
				}
			}
		}
	}

	private void items(L1PcInstance pc) {
		pc.sendPackets(new S_PacketBox(S_PacketBox.VIP_ICON, 0, true));
		// DB仓库
		// DB仓库
		CharacterTable.getInstance().restoreInventory(pc);

		pc.sendPackets(new S_InvList(pc.getInventory().getItems()));
		L1ItemInstance[] itemInstance = pc.getInventory().findItemsId(
				L1ItemId.ADENA);
		if (itemInstance.length > 0) {
			for (int i = 0; i < itemInstance.length; i++) {
				L1ItemInstance l1ItemInstance = itemInstance[i];
				if (l1ItemInstance != null) {
					WriteLogTxt.Recording("金币记录", "玩家:" + pc.getName()
							+ "登录游戏时身上有金币(" + l1ItemInstance.getCount() + ")"
							+ "#" + i);
				}
			}
		}
		equipmentWindow(pc, pc.getInventory().getItems());
	}

	private void bookmarks(L1PcInstance pc) {
		try {
			final ArrayList<L1BookMark> bookList = CharBookReading.get()
					.getBookMarks(pc);
			if (bookList != null) {
				if (bookList.size() > 0) {
					pc.sendPackets(new S_BookMarkLogin(pc, true));
				}
			} else {
				pc.sendPackets(new S_BookMarkLogin(pc, false));
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void skills(L1PcInstance pc) {
		try {
			final ArrayList<L1UserSkillTmp> skillList = CharSkillReading.get()
					.skills(pc.getId());
			final int[] skills = new int[28];
			if (skillList != null) {
				if (skillList.size() > 0) {
					for (final L1UserSkillTmp userSkillTmp : skillList) {
						// 取得魔法资料
						final L1Skills skill = SkillsTable.getInstance()
								.getTemplate(userSkillTmp.get_skill_id());
						skills[(skill.getSkillLevel() - 1)] += skill.getId();
					}
					// 送出资料
					pc.sendPackets(new S_AddSkill(pc, skills));
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void serchSummon(L1PcInstance pc) {
		for (L1SummonInstance summon : L1World.getInstance().getAllSummons()) {
			if (summon.getMaster().getId() == pc.getId()) {
				summon.setMaster(pc);
				pc.addPet(summon);
			}
		}
	}

	private void buff(LineageClient _client, L1PcInstance pc) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("SELECT * FROM character_buff WHERE char_obj_id=?");
			pstm.setInt(1, pc.getId());
			rs = pstm.executeQuery();
			while (rs.next()) {
				int skillid = rs.getInt("skill_id");
				int remaining_time = rs.getInt("remaining_time");

				// 变更为switch
				switch (skillid) {
				default: {
					L1SkillUse l1skilluse = new L1SkillUse();
					l1skilluse.handleCommands(_client.getActiveChar(), skillid,
							pc.getId(), pc.getX(), pc.getY(), null,
							remaining_time, L1SkillUse.TYPE_LOGIN);
				}
					break;

				case L1SkillId.SHAPE_CHANGE: { // 变身
					int poly_id = rs.getInt("poly_id");
					L1PolyMorph.doPoly(pc, poly_id, remaining_time);
				}
					break;
				case L1SkillId.CHECKFZ: {
					pc.setCheckFZ(true);
					pc.setSkillEffect(skillid, remaining_time);
				}
					break;
				case L1SkillId.STATUS_BRAVE: { //  
					pc.sendPackets(new S_SkillBrave(pc.getId(), 1,
							remaining_time));
					pc.setBraveSpeed(1);
					pc.setSkillEffect(skillid, remaining_time * 1000);
				}
					break;

				case L1SkillId.STATUS_HASTE: { //  
					pc.sendPackets(new S_SkillHaste(pc.getId(), 1,
							remaining_time));
					pc.setMoveSpeed(1);
					pc.setSkillEffect(skillid, remaining_time * 1000);
				}
					break;
				case L1SkillId.STATUS_ELFBRAVE: { //  
					pc.sendPackets(new S_SkillBrave(pc.getId(), 3,
							remaining_time));
					pc.setBraveSpeed(3);
					pc.setSkillEffect(skillid, remaining_time * 1000);
				}
					break;
				case L1SkillId.STATUS_BLUE_POTION: { //
					pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_BLUEPOTION,
							remaining_time));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				}
					break;

				case L1SkillId.STATUS_CHAT_PROHIBITED: { // 禁止
					pc.sendPackets(new S_PacketBox(S_PacketBox.ICON_CHATBAN,
							remaining_time));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				}
					break;

				case L1SkillId.STATUS_STR_POISON: { // 激励药水
					pc.addStr((byte) 6);
					pc.sendPackets(new S_Strup(pc, 5, remaining_time));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				}
					break;

				case L1SkillId.STATUS_DEX_POISON: { // 能力药水
					pc.addDex((byte) 6);
					pc.sendPackets(new S_Dexup(pc, 5, remaining_time));
					pc.setSkillEffect(skillid, remaining_time * 1000);
				}
					break;

				case l1j.william.New_Id.Skill_AJ_0_3: { // 经验加倍
					pc.setSkillEffect(skillid, remaining_time * 1000);
					if (remaining_time / 60 > 1) {
						pc.sendPackets(new S_ServerMessage(166,
								L1WilliamSystemMessage.ShowMessage(1110)
										+ ": ( "
										+ (remaining_time / 60)
										+ " )"
										+ L1WilliamSystemMessage
												.ShowMessage(1109)));
					} else {
						pc.sendPackets(new S_ServerMessage(166,
								L1WilliamSystemMessage.ShowMessage(1110)
										+ ": ( "
										+ (remaining_time)
										+ " )"
										+ L1WilliamSystemMessage
												.ShowMessage(1111)));
					}
				}
					break;

				case L1SkillId.COOKING_1_0_N:
				case L1SkillId.COOKING_1_0_S: { // 料理类
					pc.addWind(10);
					pc.addWater(10);
					pc.addFire(10);
					pc.addEarth(10);
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_PacketBox(53, 0, remaining_time));
					pc.setSkillEffect(skillid, remaining_time * 1000);
					pc.setCookingId(skillid);
				}
					break;

				case L1SkillId.COOKING_1_1_N:
				case L1SkillId.COOKING_1_1_S: { // 料理类
					pc.addMaxHp(30);
					pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
							.getMaxHp()));
					if (pc.isInParty()) { // 中
						pc.getParty().updateMiniHP(pc);
					}
					pc.sendPackets(new S_PacketBox(53, 1, remaining_time));
					pc.setSkillEffect(skillid, remaining_time * 1000);
					pc.setCookingId(skillid);
				}
					break;

				case L1SkillId.COOKING_1_2_N:
				case L1SkillId.COOKING_1_2_S: { // 料理类
					pc.sendPackets(new S_PacketBox(53, 2, remaining_time));
					pc.setSkillEffect(skillid, remaining_time * 1000);
					pc.setCookingId(skillid);
				}
					break;

				case L1SkillId.COOKING_1_3_N:
				case L1SkillId.COOKING_1_3_S: { // 料理类
					pc.addAc(-1);
					pc.sendPackets(new S_OwnCharStatus(pc));
					pc.sendPackets(new S_PacketBox(53, 3, remaining_time));
					pc.setSkillEffect(skillid, remaining_time * 1000);
					pc.setCookingId(skillid);
				}
					break;

				case L1SkillId.COOKING_1_4_N:
				case L1SkillId.COOKING_1_4_S: { // 料理类
					pc.addMaxMp(20);
					pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
							.getMaxMp()));
					pc.sendPackets(new S_PacketBox(53, 4, remaining_time));
					pc.setSkillEffect(skillid, remaining_time * 1000);
					pc.setCookingId(skillid);
				}
					break;

				case L1SkillId.COOKING_1_5_N:
				case L1SkillId.COOKING_1_5_S: { // 料理类
					pc.sendPackets(new S_PacketBox(53, 5, remaining_time));
					pc.setSkillEffect(skillid, remaining_time * 1000);
					pc.setCookingId(skillid);
				}
					break;

				case L1SkillId.COOKING_1_6_N:
				case L1SkillId.COOKING_1_6_S: { // 料理类
					pc.addMr(5);
					pc.sendPackets(new S_SPMR(pc));
					pc.sendPackets(new S_PacketBox(53, 6, remaining_time));
					pc.setSkillEffect(skillid, remaining_time * 1000);
					pc.setCookingId(skillid);
				}
					break;
				case L1SkillId.AITIME:
				case L1SkillId.CHECKAITIME:
				case L1SkillId.WAITTIME:
					pc.setSkillEffect(skillid, remaining_time * 1000);
					break;
				case L1SkillId.RESIST_FEAR: // 恐惧无助
                    remaining_time = remaining_time / 4;
                    pc.addNdodge((byte) 5); // 闪避率 - 50%
                    // 更新闪避率显示
                    pc.sendPackets(new S_PacketBox(101, pc.getNdodge()));
                    pc.setSkillEffect(skillid, remaining_time * 4 * 1000);
                    break;
				}
				// 变更为switch end
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	private void vipjia(L1PcInstance pc) {
		pc.sendPackets(new S_PacketBox(S_PacketBox.VIP_ICON, 0, true));
	}

	/*
	 * private String changename() { for (int i=0;i< 100;i++) {String[] firsname
	 * = { "赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "楮", "卫", "蒋", "沈",
	 * "韩", "杨", "朱", "秦", "尤", "许", "何", "吕", "施", "张", "孔", "曹", "严", "华",
	 * "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章", "云", "苏",
	 * "潘", "葛", "奚", "范", "彭", "郎", "鲁", "韦", "昌", "马", "苗", "凤", "花", "方",
	 * "俞", "任", "袁", "柳", "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺",
	 * "倪", "汤", "滕", "殷", "罗", "毕", "郝", "邬", "安", "常", "乐", "于", "时", "傅",
	 * "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "平", "黄", "和", "穆",
	 * "萧", "尹", "姚", "邵", "湛", "汪", "祁", "毛", "禹", "狄", "米", "贝", "明", "臧",
	 * "计", "伏", "成", "戴", "谈", "宋", "茅", "庞", "熊", "纪", "舒", "屈", "项", "祝",
	 * "董", "梁", "杜", "阮", "蓝", "闽", "席", "季", "麻", "强", "贾", "路", "娄", "危",
	 * "江", "童", "颜", "郭", "梅", "盛", "林", "刁", "锺", "徐", "丘", "骆", "高", "夏",
	 * "蔡", "田", "樊", "胡", "凌", "霍", "虞", "万", "支", "柯", "昝", "管", "卢", "莫",
	 * "经", "房", "裘", "缪", "干", "解", "应", "宗", "丁", "宣", "贲", "邓", "郁", "单",
	 * "杭", "洪", "包", "诸", "左", "石", "崔", "吉", "钮", "龚", "程", "嵇", "邢", "滑",
	 * "裴", "陆", "荣", "翁", "荀", "羊", "於", "惠", "甄", "麹", "家", "封", "芮", "羿",
	 * "储", "靳", "汲", "邴", "糜", "松", "井", "段", "富", "巫", "乌", "焦", "巴", "弓",
	 * "牧", "隗", "山", "谷", "车", "侯", "宓", "蓬", "全", "郗", "班", "仰", "秋", "仲",
	 * "伊", "宫", "宁", "仇", "栾", "暴", "甘", "斜", "厉", "戎", "祖", "武", "符", "刘",
	 * "景", "詹", "束", "龙", "叶", "幸", "司", "韶", "郜", "黎", "蓟", "薄", "印", "宿",
	 * "白", "怀", "蒲", "邰", "从", "鄂", "索", "咸", "籍", "赖", "卓", "蔺", "屠", "蒙",
	 * "池", "乔", "阴", "郁", "胥", "能", "苍", "双", "闻", "莘", "党", "翟", "谭", "贡",
	 * "劳", "逄", "姬", "申", "扶", "堵", "冉", "宰", "郦", "雍", "郤", "璩", "桑", "桂",
	 * "濮", "牛", "寿", "通", "边", "扈", "燕", "冀", "郏", "浦", "尚", "农", "温", "别",
	 * "庄", "晏", "柴", "瞿", "阎", "充", "慕", "连", "茹", "习", "宦", "艾", "鱼", "容",
	 * "向", "古", "易", "慎", "戈", "廖", "庾", "终", "暨", "居", "衡", "步", "都", "耿",
	 * "满", "弘", "匡", "国", "文", "寇", "广", "禄", "阙", "东", "欧", "殳", "沃", "利",
	 * "蔚", "越", "夔", "隆", "师", "巩", "厍", "聂", "晁", "勾", "敖", "融", "冷", "訾",
	 * "辛", "阚", "那", "简", "饶", "空", "曾", "毋", "沙", "乜", "养", "鞠", "须", "丰",
	 * "巢", "关", "蒯", "相", "查", "后", "荆", "红", "游", "竺", "权", "逑", "盖", "益",
	 * "桓", "公", "万俟", "司马", "上官", "欧阳", "夏侯", "诸葛", "闻人", "东方", "赫连", "皇甫",
	 * "尉迟", "公羊", "澹台", "公冶", "宗政", "濮阳", "淳于", "单于", "太叔", "申屠", "公孙", "仲孙",
	 * "轩辕", "令狐", "锺离", "宇文", "长孙", "慕容", "鲜于", "闾丘", "司徒", "司空", "丌官", "司寇",
	 * "仉", "督", "子车", "颛孙", "端木", "巫马", "公西", "漆雕", "乐正", "壤驷", "公良", "拓拔",
	 * "夹谷", "宰父", "谷梁", "晋", "楚", "阎", "法", "汝", "鄢", "涂", "钦", "段干", "百里",
	 * "东郭", "南门", "呼延", "归", "海", "羊舌", "微生", "岳", "帅", "缑", "亢", "况", "后",
	 * "有", "琴", "梁丘", "左丘", "东门", "西门", "商", "牟", "佘", "佴", "伯", "赏", "南宫",
	 * "墨", "哈", "谯", "笪", "年", "爱", "阳", "佟" }; String[] namelist = { "伟", "伟",
	 * "芳", "伟", "秀英", "秀英", "娜", "秀英", "伟", "敏", "静", "丽", "静", "丽", "强", "静",
	 * "敏", "敏", "磊", "军", "洋", "勇", "勇", "艳", "杰", "磊", "强", "军", "杰", "娟",
	 * "艳", "涛", "涛", "明", "艳", "超", "勇", "娟", "杰", "秀兰", "霞", "敏", "军", "丽",
	 * "强", "平", "刚", "杰", "桂英", "芳", "　嘉懿", "煜城", "懿轩", "烨伟", "苑博", "伟泽", "熠彤",
	 * "鸿煊", "博涛", "烨霖", "烨华", "煜祺", "智宸", "正豪", "昊然", "明杰", "立诚", "立轩", "立辉",
	 * "峻熙", "弘文", "熠彤", "鸿煊", "烨霖", "哲瀚", "鑫鹏", "致远", "俊驰", "雨泽", "烨磊", "晟睿",
	 * "天佑", "文昊", "修洁", "黎昕", "远航", "旭尧", "鸿涛", "伟祺", "荣轩", "越泽", "浩宇", "瑾瑜",
	 * "皓轩", "擎苍", "擎宇", "志泽", "睿渊", "楷瑞", "子轩", "弘文", "哲瀚", "雨泽", "鑫磊", "修杰",
	 * "伟诚", "建辉", "晋鹏", "天磊", "绍辉", "泽洋", "明轩", "健柏", "鹏煊", "昊强", "伟宸", "博超",
	 * "君浩", "子骞", "明辉", "鹏涛", "炎彬", "鹤轩", "越彬", "风华", "靖琪", "明诚", "高格", "光华",
	 * "国源", "冠宇", "晗昱", "涵润", "翰飞", "翰海", "昊乾", "浩博", "和安", "弘博", "宏恺", "鸿朗",
	 * "华奥", "华灿", "嘉慕", "坚秉", "建明", "金鑫", "锦程", "瑾瑜", "晋鹏", "经赋", "景同", "靖琪",
	 * "君昊", "俊明", "季同", "开济", "凯安", "康成", "乐语", "力勤", "良哲", "理群", "茂彦", "敏博",
	 * "明达", "朋义", "彭泽", "鹏举", "濮存", "溥心", "璞瑜", "浦泽", "奇邃", "祺祥", "荣轩", "锐达",
	 * "睿慈", "绍祺", "圣杰", "晟睿", "思源", "斯年", "泰宁", "天佑", "同巍", "奕伟", "祺温", "文虹",
	 * "向笛", "心远", "欣德", "新翰", "兴言", "星阑", "修为", "旭尧", "炫明", "学真", "雪风", "雅昶",
	 * "阳曦", "烨熠", "英韶", "永贞", "咏德", "宇寰", "雨泽", "玉韵", "越彬", "蕴和", "哲彦", "振海",
	 * "正志", "子晋", "自怡", "德赫", "君平" }; int a = (int) Math.abs(firsname.length *
	 * Math.random()); int b = (int) Math.abs(namelist.length * Math.random());
	 * String name = firsname[a] + namelist[b]; return name; }
	 */

	@Override
	public String getType() {
		return C_LOGIN_TO_SERVER;
	}
}
