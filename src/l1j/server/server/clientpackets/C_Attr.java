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

import java.io.File;

import l1j.gui.J_Main;
import l1j.server.Config;//调整能力值上限 
//import l1j.server.server.ClientThread;
import l1j.server.server.WarTimeController;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.CallClanMapTable;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.HouseTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_Lock;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_PetPack;
import l1j.server.server.serverpackets.S_Resurrection;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;//调整能力值上限 
import l1j.server.server.serverpackets.S_Trade;
import l1j.server.server.templates.L1ClanMember;
import l1j.server.server.templates.L1House;
import l1j.server.server.templates.L1Pet;
import l1j.server.server.world.L1World;
import l1j.william.BadNamesTable;
import l1j.william.L1WilliamSystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Attr extends ClientBasePacket {

	private static final Log _log = LogFactory.getLog(C_Attr.class);
	private static final String C_ATTR = "[C] C_Attr";

	public C_Attr(byte abyte0[], LineageClient _client) throws Exception {
		super(abyte0);
		int mode = readH();
		int tgobjid = 0;

        if (mode == 0) {
            tgobjid = readD();
            mode = readH();
        }
		int c;
		String name;
		
		L1PcInstance pc = _client.getActiveChar();

		switch (mode) {
		case 97: // %0血盟加入。承诺？（Y/N）
			c = readC();
			L1PcInstance clan_member = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			if (clan_member != null) {
				if (c == 0) // No
				{
					clan_member.sendPackets(new S_ServerMessage(96, pc
							.getName())); // \f1%0要请拒绝。
				} else if (c == 1) // Yes
				{
					int clan_id = pc.getClanid();
					String clan_name = pc.getClanname();
					L1Clan clan = L1World.getInstance().getClan(clan_name);
					if (clan != null) {
						int max_member = 0;
						int charisma = pc.getCha();
						boolean lv45quest = false;
						if (pc.getQuest().isEnd(L1Quest.QUEST_LEVEL45)) {
							lv45quest = true;
						}
						if (pc.getLevel() >= 50) { // Lv50以上
							if (lv45quest == true) { // Lv45济
								max_member = charisma * 9;
							} else {
								max_member = charisma * 3;
							}
						} else { // Lv50未满
							if (lv45quest == true) { // Lv45济
								max_member = charisma * 6;
							} else {
								max_member = charisma * 2;
							}
						}
						if (Config.CLANCOUNT != 0) {
							max_member = Config.CLANCOUNT;
						}
						int old_clan_id = clan_member.getClanid();
						if (old_clan_id == 0) { // 未加入
							String clan_member_name[] = clan.getAllMemberNames();
							if (max_member <= clan_member_name.length) { // 空
								clan_member.sendPackets( // %0血盟员受入。
										new S_ServerMessage(188, pc.getName()));
								return;
							}
							L1PcInstance clanMembers[] = clan
									.getOnlineClanMember();
							for (int cnt = 0; cnt < clanMembers.length; cnt++) {
								clanMembers[cnt]
										.sendPackets(new S_ServerMessage(94,
												clan_member.getName())); // \f1%0血盟一员受入。
							}
							L1ClanMember clanMember = new L1ClanMember(clan_member.getName(),L1Clan.CLAN_RANK_PUBLIC);
							clan_member.setClanRank(L1Clan.CLAN_RANK_PUBLIC);
							clan_member.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, L1Clan.CLAN_RANK_PUBLIC, clan_member.getName()));
							clan_member.setClanid(clan_id);
							System.out.println("clan:"+clan_id);
							clan_member.setClanname(clan_name);
							clan_member.save(); // DB情报书迂
							clan.addMemberName(clanMember);
							clan_member.sendPackets(new S_ServerMessage(95,
									clan_name)); // \f1%0血盟加入。
						} else { // 加入济（连合）
							if (lv45quest) {
								ChangeClan(_client, pc, clan_member,
										max_member);
							}else {
								clan_member.sendPackets(new S_ServerMessage(89)); // \f1你已經有血盟了。
							}					
						}
					}
				}
			}
			break;

		case 217: // %0血盟%1血盟战争望。战争应？（Y/N）
		case 221: // %0血盟降伏望。受入？（Y/N）
		case 222: // %0血盟战争终结望。终结？（Y/N）
			c = readC();
			L1PcInstance enemyLeader = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			if (enemyLeader == null) {
				return;
			}
			pc.setTempID(0);
			String clanName = pc.getClanname();
			String enemyClanName = enemyLeader.getClanname();
			if (c == 0) { // No
				if (mode == 217) {
					enemyLeader.sendPackets(new S_ServerMessage(236, clanName)); // %0血盟血盟战争拒绝。
				} else if (mode == 221 || mode == 222) {
					enemyLeader.sendPackets(new S_ServerMessage(237, clanName)); // %0血盟提案拒绝。
				}
			} else if (c == 1) { // Yes
				if (mode == 217) {
					//L1War war = new L1War();
					//war.handleCommands(2, enemyClanName, clanName); // 模拟战开始
					pc.sendPackets(new S_SystemMessage("模拟战战争已关闭"));
				} else if (mode == 221 || mode == 222) {
					// 全战争取得
					for (L1War war : L1World.getInstance().getWarList()) {
						if (war.CheckClanInWar(clanName)) { // 自行战争发见
							if (mode == 221) {
								war.SurrenderWar(enemyClanName, clanName); // 降伏
							} else if (mode == 222) {
								war.CeaseWar(enemyClanName, clanName); // 终结
							}
							break;
						}
					}
				}
			}
			break;

		case 252: // %0%s取引望。取引？（Y/N）
			c = readC();
			final L1Object object = L1World.getInstance().findObject(pc.getTradeID());
			if (c == 0){
				if (object instanceof L1PcInstance) {
					final L1PcInstance trading_partner = (L1PcInstance)object;
					trading_partner.sendPackets(new S_ServerMessage(253, pc.getName())); // %0%d取引应。
					trading_partner.setTradeID(0);
				}
				pc.setTradeID(0);
			} else if (c == 1){
				if (object instanceof L1PcInstance) {
					final L1PcInstance trading_partner = (L1PcInstance)object;
					pc.sendPackets(new S_Trade(trading_partner.getName()));
					trading_partner.sendPackets(new S_Trade(pc.getName()));
				}else if (object instanceof L1NpcInstance) {
					final L1NpcInstance trading_partnernpc = (L1NpcInstance)object;
					pc.sendPackets(new S_Trade(trading_partnernpc.getName()));
				}
			}
			break;
		case 321: // 复活？（Y/N）
			c = readC();
			L1PcInstance resusepc1 = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
//			if (pc.getMap().isUseResurrection()) {
				if (resusepc1 != null) { // 复活
					if (c == 0) { // No
						;
					} else if (c == 1) { // Yes
						if (pc.getMap().isUseResurrection() || resusepc1.isGm()) {
							for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 1))  {
								if (obj instanceof L1PcInstance) {
									L1PcInstance tgpc = (L1PcInstance)obj;
									if (!tgpc.isGmInvis()) {
										if (obj.getX() == pc.getX()&&obj.getY() == pc.getY()) {
											pc.sendPackets(new S_SystemMessage("当前位置被占据，无法复活！"));
											return;
										}
									}					
								}					
							}
							pc.sendPackets(new S_SkillSound(pc.getId(), '\346'));
							pc.broadcastPacket(new S_SkillSound(pc.getId(), '\346'));
							pc.resurrect(pc.getLevel());
							//pc.setCurrentHp(pc.getLevel());
							//pc.resurrect(pc.getMaxHp() / 2);
							//pc.setCurrentHp(pc.getMaxHp() / 2);
							pc.startHpRegeneration();
							pc.startMpRegeneration();
//							pc.startMpRegenerationByDoll();
							pc.sendPackets(new S_Resurrection(pc, resusepc1, 0));
							pc.broadcastPacket(new S_Resurrection(pc, resusepc1, 0));
						}
					}				
				}
//			}

			break;

		case 322: // 复活？（Y/N）
			c = readC();
			L1PcInstance resusepc2 = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			if (resusepc2 != null) { // 祝福 复活、、 
				if (c == 0) { // No
					;
				} else if (c == 1) { // Yes
					if (pc.getMap().isUseResurrection() || resusepc2.isGm()) {
						for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 1))  {
							if (obj instanceof L1PcInstance) {
								L1PcInstance tgpc = (L1PcInstance)obj;
								if (!tgpc.isGmInvis()) {
									if (obj.getX() == pc.getX()&&obj.getY() == pc.getY()) {
										pc.sendPackets(new S_SystemMessage("当前位置被占据，无法复活！"));
										return;
									}
								}					
							}					
						}
						pc.sendPackets(new S_SkillSound(pc.getId(), '\346'));
						pc.broadcastPacket(new S_SkillSound(pc.getId(), '\346'));
						pc.resurrect(pc.getLevel());
						//pc.resurrect(pc.getMaxHp());
						//pc.setCurrentHp(pc.getMaxHp());
						pc.startHpRegeneration();
						pc.startMpRegeneration();
//						pc.startMpRegenerationByDoll();
						pc.sendPackets(new S_Resurrection(pc, resusepc2, 0));
						pc.broadcastPacket(new S_Resurrection(pc, resusepc2, 0));
						// EXP、G-RES挂、EXP死亡
						// 全满场合EXP复旧
						if (pc.getExpRes() == 1 && pc.isGres() && pc.isGresValid()) {
							pc.resExp1();
							pc.setExpRes(0);
							pc.setGres(false);
						}
					}
				}
			}
			break;

		case 325: // 动物名前决：
			c = readC(); // ?
			name = readS();
			if (pc.is_rname()!=0) {
				if (name.isEmpty()) {
					pc.rename(0);
					return;
				}
				// 修正 
				if (!C_CreateChar.isInvalidName(name,_client.getLanguage())) {
					/*S_CharCreateStatus s_charcreatestatus = new S_CharCreateStatus(
							S_CharCreateStatus.REASON_INVALID_NAME);
					pc.sendPackets(s_charcreatestatus);*/
					pc.rename(0);
					return;
				}
				// 修正  end

				if (CharacterTable.doesCharNameExist(name)) {
					/*_log.fine("charname: " + pc.getName()
							+ " already exists. creation failed.");
					S_CharCreateStatus s_charcreatestatus1 = new S_CharCreateStatus(
							S_CharCreateStatus.REASON_ALREADY_EXSISTS);
					pc.sendPackets(s_charcreatestatus1);*/
					pc.rename(0);
					return;
				}

				// 无法创造的名称 
				if (BadNamesTable.doesCharNameExist(name)) {
					/*S_CharCreateStatus s_charcreatestatus = new S_CharCreateStatus(
							S_CharCreateStatus.REASON_INVALID_NAME);
					pc.sendPackets(s_charcreatestatus);*/
					pc.rename(0);
					return;
				}
				if (pc.isPrivateShop()) {// 商店村模式
					pc.sendPackets(new S_SystemMessage("请先结束商店模式!"));
					pc.rename(0);
					return;
				}

				final Object[] petList = pc.getPetList().values().toArray();
				if (petList.length > 0) {
					pc.sendPackets(new S_SystemMessage("请先回收宠物，祭祀，魔法娃娃！"));
					pc.rename(0);
					return;
				}
				if (pc.getClanid() != 0) {
					pc.sendPackets(new S_SystemMessage("请先退出血盟!"));
					pc.rename(0);
					return;
				}
				if (pc.getInventory().consumeItem(pc.is_rname(),1)) {
					WriteLogTxt.Recording("角色更名记录", "角色OBJID：<"+pc.getId()+">修改前名字为" + pc.getName()
							+ "，修改后名字为" + name);
					// 无法创造的名称 
					J_Main.getInstance().delPlayerTable(pc.getName());//GUI
					pc.setName(name);
					J_Main.getInstance().addPlayerTable(pc.getAccountName(), name, _client.getIp(), _client);
					pc.sendPackets(new S_SystemMessage("改名成功，请走2步看看效果!"));
					pc.rename(0);
					pc.save();
					pc.sendPackets(new S_Lock());
					pc.startRenameThread();
				}		
				return;
			}
			L1PetInstance pet = (L1PetInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			renamePet(pet, name);
			break;

		case 512: // 家名前？
			c = readC(); // ?
			name = readS();
			int houseId = pc.getTempID();
			pc.setTempID(0);
			if (name.length() <= 16) {
				L1House house = HouseTable.getInstance().getHouseTable(houseId);
				house.setHouseName(name);
				HouseTable.getInstance().updateHouse(house); // DB书
			} else {
				pc.sendPackets(new S_ServerMessage(513)); // 家名前长。
			}
			break;

		case 653: // 离婚消。离婚望？（Y/N）
			c = readH();
			L1PcInstance tgpt = (L1PcInstance) L1World.getInstance().findObject(pc.getPartnerId());
			if (c == 0) { // No
				return;
			} else if (c == 1) { // Yes
				if (tgpt != null) {
					tgpt.setPartnerId(0);
					tgpt.save();
					tgpt.sendPackets(new S_ServerMessage(662)); // \f1你(妳)目前未婚。
				} else {
					CharacterTable.updatePartnerId(
							pc.getPartnerId());
				}
			}
			pc.setPartnerId(0);
			pc.save(); // 將玩家資料儲存到資料庫中
			pc.sendPackets(new S_ServerMessage(662)); // \f1你(妳)目前未婚。
			break;

		case 654: // %0%s结婚。%0结婚？（Y/N）
			c = readC();
			L1PcInstance partner = (L1PcInstance) L1World.getInstance()
					.findObject(pc.getTempID());
			pc.setTempID(0);
			if (partner != null) {
				if (c == 0) { // No
					partner.sendPackets(new S_ServerMessage( // %0%s结婚拒绝。
							656, pc.getName()));
				} else if (c == 1) { // Yes
					if (pc.getPartnerId() != 0) {
						pc.sendPackets(new S_ServerMessage(657)); // \f1结婚。
						return;
					}
					if (partner.getPartnerId() != 0) {
						pc.sendPackets(new S_ServerMessage(658)); // \f1相手结婚。
						return;
					}
					if (pc.get_sex() == partner.get_sex()) {
						pc.sendPackets(new S_ServerMessage(661)); // \f1结婚相手异性。
						return;
					}
					pc.setPartnerId(partner.getId());
					pc.save();
					pc.sendPackets(new S_ServerMessage( // 皆祝福中、二人结婚行。
							790));
					pc.sendPackets(new S_ServerMessage( // ！%0结婚。
							655, partner.getName()));

					partner.setPartnerId(pc.getId());
					partner.save();
					partner.sendPackets(new S_ServerMessage( // 皆祝福中、二人结婚行。
							790));
					partner.sendPackets(new S_ServerMessage( // ！%0结婚。
							655, pc.getName()));
				}
			}
			break;

		case 738: // 经验值回复%0必要。经验值回复？
			c = readC();
			if (c == 0) { // No
				;
			} else if (c == 1) { // Yes
				if (pc.getExpRes() == 0) {
					return;
				}
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
				if (pc.getInventory().consumeItem(L1ItemId.ADENA, cost)) {
					pc.resExp();
					pc.setExpRes(0);
				} else {
					pc.sendPackets(new S_ServerMessage(189)); // \f1不足。
				}
			}
			break;

		// 
		case 748: // 血盟员。应？（Y/N）
			c = readC();
			if (c == 0) { // No
				;
			} else if (c == 1) { // Yes
				L1PcInstance callClanPc = (L1PcInstance) L1World.getInstance()
						.findObject(pc.getTempID());
				pc.setTempID(0);
				if (callClanPc != null) {
					if (pc.getMap().isEscapable() || pc.isGm()) {
						boolean isInWarArea = false;
						int castleId = L1CastleLocation
								.getCastleIdByArea(callClanPc);
						if (castleId != 0) {
//							isInWarArea = true;
							if (WarTimeController.getInstance()
									.isNowWar(castleId)) {
								isInWarArea = true;
							}
						}
						if ((callClanPc.getMapId() == 0
								|| callClanPc.getMapId() == 4 || callClanPc
								.getMapId() == 304)
								&& isInWarArea == false) {
							// 删除L1Teleport.teleport(pc, callClanPc.getLocation(),
							// 删除		5, true, L1Teleport.CALL_CLAN);
							// 加入呼唤盟友效果 
							pc.sendPackets(new S_SkillSound(pc.getId(), 2281));
							pc.broadcastPacket(new S_SkillSound(pc.getId(), 2281));
							try {
								Thread.sleep(300);//延迟显示效果
							} catch (InterruptedException e) {
							}
							L1Teleport.teleport(pc, callClanPc.getLocation(),
									5, true, L1Teleport.CALL_CLAN);
							// 加入呼唤盟友效果  end
						} else {
							// 删除pc.sendPackets(new S_ServerMessage(547)); // \f1今行所中。
							// 讯息修正 
							pc.sendPackets(new S_SystemMessage("你的血盟成员在你无法传送前往的地区。"));
							// 讯息修正  end
						}
					} else {
						pc.sendPackets(new S_ServerMessage(647)); // 周边妨害。、使用。
						L1Teleport.teleport(pc, pc.getLocation(),
								pc.getHeading(), false);
					}
				}
			}
			break;

		case 953: // 招待许可？（Y/N）
//			System.out.println("组队请求接受");
			c = readC();
			L1PcInstance target = (L1PcInstance) L1World.getInstance().findObject(pc.getPartyID());
			if (target != null) {
				if (c == 0) // No
				{
					target.sendPackets(new S_ServerMessage(423, pc.getName()));
					pc.setPartyID(0);
				} else if (c == 1) // Yes
				{
//					System.out.println("答应组队");
					if (pc.getMapId() != Config.HUODONGMAPID){
						if (target.isInParty()) {
							// 招待主中
							if (target.getParty().isVacancy() || target.isGm()) {
								// 空
								target.getParty().addMember(pc);
							} else {
								// 空
								target.sendPackets(new S_ServerMessage(417));
							}
						} else {
							// 招待主中
							L1Party party = new L1Party();
							party.addMember(target);
							party.addMember(pc);
							target.sendPackets(new S_ServerMessage(424, pc.getName()));
						}
					}
				}
			}else{
				pc.sendPackets(new S_SystemMessage("\\F2对方已经不在线了。"));
			}
			break;

		case 479: // 能力值向上？（str、dex、int、con、wis、cha）
			if (readC() == 1) {
				String s = readS();
				if (!(pc.getLevel() - 50 > pc.getBonusStats())) {
					return;
				}
				if (s.toLowerCase().equals("str".toLowerCase())) {
					// if(l1pcinstance.get_str() < 255)
					if (pc.getBaseStr() < Config.BONUS_STATS1) { // 调整能力值上限 
						pc.addBaseStr((byte) 1); // 素STR值+1
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB情报书迂
					} else {
						//删除pc.sendPackets(new S_ServerMessage(481));
						pc.sendPackets(new S_SystemMessage(L1WilliamSystemMessage.ShowMessage(1001))); // 变更 
					}
				} else if (s.toLowerCase().equals("dex".toLowerCase())) {
					// if(l1pcinstance.get_dex() < 255)
					if (pc.getBaseDex() < Config.BONUS_STATS1) { // 调整能力值上限 
						pc.addBaseDex((byte) 1); // 素DEX值+1
						pc.resetBaseAc();
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB情报书迂
					} else {
						//删除pc.sendPackets(new S_ServerMessage(481));
						pc.sendPackets(new S_SystemMessage(L1WilliamSystemMessage.ShowMessage(1001))); // 变更 
					}
				} else if (s.toLowerCase().equals("con".toLowerCase())) {
					// if(l1pcinstance.get_con() < 255)
					if (pc.getBaseCon() < Config.BONUS_STATS1) { // 调整能力值上限 
						pc.addBaseCon((byte) 1); // 素CON值+1
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB情报书迂
					} else {
						//删除pc.sendPackets(new S_ServerMessage(481));
						pc.sendPackets(new S_SystemMessage(L1WilliamSystemMessage.ShowMessage(1001))); // 变更 
					}
				} else if (s.toLowerCase().equals("int".toLowerCase())) {
					// if(l1pcinstance.get_int() < 255)
					if (pc.getBaseInt() < Config.BONUS_STATS1) { // 调整能力值上限 
						pc.addBaseInt((byte) 1); // 素INT值+1
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB情报书迂
					} else {
						//删除pc.sendPackets(new S_ServerMessage(481));
						pc.sendPackets(new S_SystemMessage(L1WilliamSystemMessage.ShowMessage(1001))); // 变更 
					}
				} else if (s.toLowerCase().equals("wis".toLowerCase())) {
					// if(l1pcinstance.get_wis() < 255)
					if (pc.getBaseWis() < Config.BONUS_STATS1) { // 调整能力值上限 
						pc.addBaseWis((byte) 1); // 素WIS值+1
						pc.resetBaseMr();
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB情报书迂
					} else {
						//删除pc.sendPackets(new S_ServerMessage(481));
						pc.sendPackets(new S_SystemMessage(L1WilliamSystemMessage.ShowMessage(1001))); // 变更 
					}
				} else if (s.toLowerCase().equals("cha".toLowerCase())) {
					// if(l1pcinstance.get_cha() < 255)
					if (pc.getBaseCha() < Config.BONUS_STATS1) {// 调整能力值上限 
						pc.addBaseCha((byte) 1); // 素CHA值+1
						pc.setBonusStats(pc.getBonusStats() + 1);
						pc.sendPackets(new S_OwnCharStatus2(pc));
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.save(); // DB情报书迂
					} else {
						//删除pc.sendPackets(new S_ServerMessage(481));
						pc.sendPackets(new S_SystemMessage(L1WilliamSystemMessage.ShowMessage(1001))); // 变更 
					}
				}
			}
			break;
        case 4531:
            c = this.readC();
            if (c == 1){
            	callClantelete(pc);
            }
            break;
        case 4532:
            c = this.readC();
            if (c == 1){
            	callClanteleteBiaoChe(pc);
            }
            break;
		default:
			break;
		}
	}
	private void callClanteleteBiaoChe(final L1PcInstance pc) {
		if (pc.isDead()){
			pc.sendPackets(new S_SystemMessage("你已经死亡"));
            return;
		}
		if (pc.getMapId() == 99){
			pc.sendPackets(new S_SystemMessage("你不能过去"));
            return;
		}
		if (pc.isParalyzed() || pc.hasSkillEffect(L1SkillId.SHOCK_STUN) || pc.hasSkillEffect(L1SkillId.EARTH_BIND)) {
			pc.sendPackets(new S_SystemMessage("你现在无法移动"));
            return;
        }
		if (!pc.getMap().isEscapable()) {
            // 647 这附近的能量影响到瞬间移动。在此地无法使用瞬间移动。
            pc.sendPackets(new S_ServerMessage(647));
            return;
        }
		L1Teleport.teleport(pc, pc.getTempBiaoCheLocX(),pc.getTempBiaoCheLocY(), pc.getTempBiaoCheMapId(), pc.getHeading(), true,L1Teleport.CALL_CLAN);
	}
	private void callClantelete(final L1PcInstance pc) {
		final L1Object objpc = L1World.getInstance().findObject(pc.getClanTeletePcId());
		pc.setClanTeletePcId(0);
		if (pc.isDead()){
			pc.sendPackets(new S_SystemMessage("你已经死亡"));
            return;
		}
		if (objpc == null){
			pc.sendPackets(new S_SystemMessage("对方已经不在线上"));
            return;
		}
		if (pc.getMapId() == 99){
			pc.sendPackets(new S_SystemMessage("你不能过去"));
            return;
		}
		if (pc.getSkillEffectTimeSec(L1SkillId.SHOCK_STUN) > 0){
			pc.sendPackets(new S_SystemMessage(String.format("\\F2你现在无法移动 冲晕倒计时%d秒", pc.getSkillEffectTimeSec(L1SkillId.SHOCK_STUN))));
			return;
		}
		if (pc.getSkillEffectTimeSec(L1SkillId.EARTH_BIND) > 0){
			pc.sendPackets(new S_SystemMessage(String.format("\\F2你现在无法移动 大地屏障倒计时%d秒", pc.getSkillEffectTimeSec(L1SkillId.EARTH_BIND))));
			return;
		}
		if (pc.getSkillEffectTimeSec(L1SkillId.ICE_LANCE) > 0){
			pc.sendPackets(new S_SystemMessage(String.format("\\F2你现在无法移动 冰矛围篱倒计时%d秒", pc.getSkillEffectTimeSec(L1SkillId.ICE_LANCE))));
			return;
		}
		if (pc.getSkillEffectTimeSec(L1SkillId.FOG_OF_SLEEPING) > 0){
			pc.sendPackets(new S_SystemMessage(String.format("\\F2你现在无法移动 沉睡倒计时%d秒", pc.getSkillEffectTimeSec(L1SkillId.FOG_OF_SLEEPING))));
			return;
		}
		if (!pc.getInventory().checkItem(40308, 10000)){
        	pc.sendPackets(new S_SystemMessage("\\F2金币不足10000"));
        	return;
        }
		if (!pc.getMap().isEscapable()) {
            // 647 这附近的能量影响到瞬间移动。在此地无法使用瞬间移动。
            pc.sendPackets(new S_ServerMessage(647));
            return;
        }
		if (objpc instanceof L1PcInstance){
			final L1PcInstance callClanPc = (L1PcInstance)objpc;
			boolean isInWarArea = false;
	        final int castleId = L1CastleLocation.getCastleIdByArea(callClanPc);
	        if (castleId != 0){
	        	if (WarTimeController.getInstance()
						.isNowWar(castleId)) {
					isInWarArea = true;
				}
	        }
	        final short mapId = callClanPc.getMapId();
	        if (CallClanMapTable.get().IsNoMap(mapId) || isInWarArea){
	        	pc.sendPackets(new S_ServerMessage(629));
	            return;
	        }
	        final L1Location newLocation = callClanPc.getLocation().randomLocation(3,
	                true);
	        if (newLocation != null){
	        	final int newX = newLocation.getX();
	            final int newY = newLocation.getY();
	            final short mapId0 = (short) newLocation.getMapId();
	            pc.getInventory().consumeItem(40308, 10000);
	            L1Teleport.teleport(pc, newX,newY, mapId0, pc.getHeading(), true,L1Teleport.CALL_CLAN);
	        }else{
	        	pc.getInventory().consumeItem(40308, 10000);
	        	L1Teleport.teleport(pc, callClanPc.getX(),callClanPc.getY(), callClanPc.getMapId(), pc.getHeading(), true,L1Teleport.CALL_CLAN);//没坐标重叠
	        	//pc.sendPackets(new S_ServerMessage(629));
	        }
		}
	}

	private void ChangeClan(LineageClient _client,
			L1PcInstance l1pcinstance, L1PcInstance clan_member, int max_member) {
		int clan_id = l1pcinstance.getClanid();
		String clan_name = l1pcinstance.getClanname();
		L1Clan clan = L1World.getInstance().getClan(clan_name);
		String clan_member_name[] = clan.getAllMemberNames();
		int clan_num = clan_member_name.length;

		int old_clan_id = clan_member.getClanid();
		String old_clan_name = clan_member.getClanname();
		L1Clan old_clan = L1World.getInstance().getClan(old_clan_name);
		String old_clan_member_name[] = old_clan.getAllMemberNames();
		int old_clan_num = old_clan_member_name.length;
		if (clan != null && old_clan != null && clan_member.isCrown() && // 自分君主
				clan_member.getId() == old_clan.getLeaderId()) {
			if (max_member < clan_num + old_clan_num) { // 空
				clan_member.sendPackets( // %0血盟员受入。
						new S_ServerMessage(188, l1pcinstance.getName()));
				return;
			}
			L1PcInstance clanMember[] = clan.getOnlineClanMember();
			for (int cnt = 0; cnt < clanMember.length; cnt++) {
				clanMember[cnt].sendPackets(new S_ServerMessage(94, clan_member
						.getName())); // \f1%0血盟一员受入。
			}
			/** 變更為聯盟王 */
			clan_member.setClanRank(L1Clan.CLAN_RANK_PUBLIC);
			clan_member.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, L1Clan.CLAN_RANK_PUBLIC, clan_member.getName()));
/*			clan_member.setClanRank(L1Clan.CLAN_RANK_LEAGUE_PRINCE);
			clan_member.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, L1Clan.CLAN_RANK_LEAGUE_PRINCE, clan_member.getName())); // 你的階級變更為
*/			for (int i = 0; i < old_clan_member_name.length; i++) {
				L1PcInstance old_clan_member = L1World.getInstance().getPlayer(
						old_clan_member_name[i]);
				if (old_clan_member != null) { // 中旧
					old_clan_member.setClanid(clan_id);
					old_clan_member.setClanname(clan_name);
					old_clan_member.setClanRank(L1Clan.CLAN_RANK_PUBLIC);
					try {
						// DB情报书迂
						old_clan_member.save();
					} catch (Exception e) {
						_log.error(e.getLocalizedMessage(), e);
					}
					L1ClanMember oldclanmember = new L1ClanMember(old_clan_member.getName(), L1Clan.CLAN_RANK_PUBLIC);
					clan.addMemberName(oldclanmember);
					old_clan_member.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, L1Clan.CLAN_RANK_PUBLIC, old_clan_member.getName())); // 你的階級變更為
					old_clan_member.sendPackets(new S_ServerMessage(95,
							clan_name)); // \f1%0血盟加入。
				} else { // 中旧
					try {
						L1PcInstance off_clan_member = CharacterTable
								.getInstance().restoreCharacter(
										old_clan_member_name[i]);
						off_clan_member.setClanid(clan_id);
						off_clan_member.setClanname(clan_name);
						off_clan_member.setClanRank(L1Clan.CLAN_RANK_PUBLIC);
						L1ClanMember offClanMember = new L1ClanMember(off_clan_member.getName(), L1Clan.CLAN_RANK_PUBLIC);
						off_clan_member.save(); // DB情报书迂
						clan.addMemberName(offClanMember);
					} catch (Exception e) {
						_log.error(e.getLocalizedMessage(), e);
					}
				}
			}
			// 旧削除
			String emblem_file = String.valueOf(old_clan_id);
			File file = new File("emblem/" + emblem_file);
			file.delete();
			ClanTable.getInstance().deleteClan(old_clan_name);
		}
	}

	private static void renamePet(L1PetInstance pet, String name) {
		if (pet == null || name == null) {
			throw new NullPointerException();
		}

		int petItemObjId = pet.getItemObjId();
		L1Pet petTemplate = PetTable.getInstance().getTemplate(petItemObjId);
		if (petTemplate == null) {
			throw new NullPointerException();
		}

		L1PcInstance pc = (L1PcInstance) pet.getMaster();
		if (PetTable.isNameExists(name)) {
			pc.sendPackets(new S_ServerMessage(327)); // 同名前存在。
			return;
		}
		pet.setName(name);
		petTemplate.set_name(name);
		PetTable.getInstance().UpdatePet(petTemplate); // DB书
		pc.sendPackets(new S_PetPack(pet, pc));
	}

	@Override
	public String getType() {
		return C_ATTR;
	}
}