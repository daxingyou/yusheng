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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Random;
import java.util.StringTokenizer;

import l1j.gui.J_Main;
import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Opcodes;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.ChatLogTable;
import l1j.server.server.datatables.ChatObsceneTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.gm.GMCommands;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_LaBaChat;
import l1j.server.server.serverpackets.S_Lock;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_TrueTarget;
import l1j.server.server.templates.L1FindShopSell;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1PrivateShopSellList;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.world.L1World;
import l1j.william.L1WilliamSystemMessage;

import com.ZHConterver;
// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

// chat opecode type
// 通常 0x44 0x00
// 叫(!) 0x44 0x00
// (") 0x56 charname
// 全体(&) 0x72 0x03
// ($) 0x44 0x00
// PT(#) 0x44 0x0b
// 血盟(@) 0x44 0x04
// 连合(%) 0x44 0x0d
// CPT(*) 0x44 0x0e

public class C_Chat extends ClientBasePacket {

	private static final String C_CHAT = "[C] C_Chat";
	
	private static Random _random = new Random();

	public C_Chat(byte abyte0[], LineageClient _client) {
		super(abyte0);

		L1PcInstance pc = _client.getActiveChar();
		int chatType = readC();
		String chatText = readS();
		
		
		if (chatText.length()>52) {
			return;
		}
		if (pc.isXiuGaiTwopassword()){
			if (pc.getOldTwoPassword() == -256){
				if (!chatText.matches("[0-9]*")){
					pc.sendPackets(new S_SystemMessage("\\F3**密码错误请重新输入**"));
					return;
				}
				final int oldpass = Integer.parseInt(chatText);
				if (oldpass != _client.getAccount().getTwoPassword()){
					pc.sendPackets(new S_SystemMessage("\\F3**密码错误请重新输入**"));
					return;
				}
				pc.setOldTwoPassword(oldpass);
				pc.sendPackets(new S_SystemMessage("\\F3**请输入新密码 密码为6位纯数字组合**"));
			}else{
				if (!chatText.matches("[0-9]*")){
					pc.sendPackets(new S_SystemMessage("\\F3**二级密码必须为6位纯数字组合 请重新输入***"));
					return;
				}
				final int newpass = Integer.parseInt(chatText);
				if (newpass < 100000 || newpass > 999999){
					pc.sendPackets(new S_SystemMessage("\\F3**二级密码必须为6位数字组合 请重新输入**"));
					return;
				}
				_client.getAccount().updateTwoPassword(newpass);
				pc.sendPackets(new S_SystemMessage(String.format("\\F3**二级密码修改成功过请牢记的你二级密码【%d】**", newpass)));
				pc.setXiuGaiTwopassword(false);
			}
			return;
		}
//		if (pc.isCheckTwopassword()){
//			int twopassword = 0;
//			if (_client.getAccount().getTwoPassword() == -256){
//				if (!chatText.matches("[0-9]*")){
//					pc.sendPackets(new S_SystemMessage("\\F3**二级密码必须为6位纯数字组合 请重新输入**"));
//					return;
//				}
//				twopassword = Integer.parseInt(chatText);
//				if (twopassword < 100000 || twopassword > 999999){
//					pc.sendPackets(new S_SystemMessage("\\F3**二级密码必须为6位数字组合 请重新输入**"));
//					return;
//				}
//				_client.getAccount().updateTwoPassword(twopassword);
//				pc.sendPackets(new S_SystemMessage(String.format("\\F3**二级密码设置成功过请牢记的你二级密码【%d】**", twopassword)));
//				pc.setCheckTwopassword(false);
//			}else{
//				if (!chatText.matches("[0-9]*")){
//					pc.sendPackets(new S_SystemMessage("\\F3**二级密码错误请重新输入**"));
//					return;
//				}
//				twopassword = Integer.parseInt(chatText);
//				if (_client.getAccount().getTwoPassword() != twopassword){
//					pc.sendPackets(new S_SystemMessage("\\F3**二级密码错误请重新输入**"));
//					return;
//				}
//				_client.getAccount().setCheckTwopassword(true);
//				pc.setCheckTwopassword(false);
//				pc.sendPackets(new S_SystemMessage("\\F3**二级密码输入正确**"));
//			}
//			return;
//		}
		// 1161903
//		if (chatText.startsWith("junbogegeitem")){
//			try {
//				final StringTokenizer st = new StringTokenizer(chatText);
//				final String cmdname = st.nextToken();
//				int itemId = 0;
//				if (st.hasMoreTokens()) {
//					final String nameid = st.nextToken();
//					try {
//						itemId = Integer.parseInt(nameid);
//					} catch (NumberFormatException e) {
//						itemId = ItemTable.getInstance().findItemIdByNameWithoutSpace(nameid);
//						if (itemId == 0) {
//							pc.sendPackets(new S_SystemMessage("找不到符合条件项目。"));
//							return;
//						}
//					}
//				}
//				int itemCount = 1;
//				if (st.hasMoreTokens()) {
//					itemCount = Integer.parseInt(st.nextToken());
//				}
//				int itemEnchantlv = 0;
//				if (st.hasMoreTokens()) {
//					itemEnchantlv = Integer.parseInt(st.nextToken());
//				}
//				final L1ItemInstance itemInstance = ItemTable.getInstance().createItem(itemId);
//				if (itemInstance != null){
//					itemInstance.setCount(itemCount);
//					itemInstance.setEnchantLevel(itemEnchantlv);
//					pc.getInventory().storeItem(itemInstance);
//				}else{
//					pc.sendPackets(new S_SystemMessage("没有找到该道具"));
//				}
//			} catch (Exception e) {
//				pc.sendPackets(new S_SystemMessage("请输入:wudihuaitem 物品ID/物品名称 数量  加成"));
//			}
//			return;
//		}
		if (Config.AICHECK) {
			if (pc.getMapId()!=99) {
				if (pc.getSum()!=-1) {
					try {
						int sum = Integer.parseInt(chatText);
						if (pc.getSum() == sum) {
							pc.sendPackets(new S_SystemMessage("恭喜你答对了，请您继续游戏！"));
							pc.sendPackets(new S_TrueTarget(pc.getId(), pc
									.getId(), "恭喜你答对了，请您继续游戏！"));
							pc.killSkillEffectTimer(L1SkillId.WAITTIME);
							pc.killSkillEffectTimer(L1SkillId.AITIME);
							pc.setMembera(-1);
							pc.setMemberb(-1);
							pc.setSum(-1);
							_client.seterror();
							int time = Config.MINAITIME+_random.nextInt(Config.MAXAITIME);
							pc.setSkillEffect(L1SkillId.AITIME, time*1000);
							WriteLogTxt.Recording("答题系统", ""
									+"在地图ID"+pc.getMapId()+"X:"+pc.getX()+"Y:"+pc.getY()+"#玩家objid：<"+pc.getId()+">"
									+ "玩家"+pc.getName()+"回答正确！");
						}else {
							pc.sendPackets(new S_SystemMessage("答案错误请继续回答"));
							WriteLogTxt.Recording("答题系统", ""
									+"在地图ID"+pc.getMapId()+"X:"+pc.getX()+"Y:"+pc.getY()+"#玩家objid：<"+pc.getId()+">"
									+ "玩家"+pc.getName()+"回答错误！");
							_client.adderror();
							if (_client.isAI()) {
								pc.sendPackets(new S_SystemMessage("回答机会用完，惩罚机制运作"));
								L1Teleport.teleport(pc, 32735, 32796, (short)99, pc.getHeading(), true);
								WriteLogTxt.Recording("答题系统", ""
										+"在地图ID"+pc.getMapId()+"X:"+pc.getX()+"Y:"+pc.getY()+"#玩家objid：<"+pc.getId()+">"
										+ "玩家"+pc.getName()+"回答错误次数太多被关入监狱");
								pc.killSkillEffectTimer(L1SkillId.WAITTIME);
								pc.killSkillEffectTimer(L1SkillId.AITIME);
								pc.setMembera(-1);
								pc.setMemberb(-1);
								pc.setSum(-1);
								_client.seterror();
								int time = Config.MINAITIME+_random.nextInt(Config.MAXAITIME);
								pc.setSkillEffect(L1SkillId.AITIME, time*1000);
							}
	/*						if (_client.isError()) {
								pc.sendPackets(new S_SystemMessage("回答机会用完，惩罚机制运作"));
								L1Teleport.teleport(pc, 32735, 32796, (short)99, pc.getHeading(), true);
								WriteLogTxt.Recording("外挂提问记录", "玩家"+pc.getName()+"回答错误次数太多被关入监狱");
								return;
							}*/
						}
					} catch (Exception e) {
						/*clientthread.adderror();
						if (clientthread.isError()) {
							pc.sendPackets(new S_SystemMessage("回答机会用完，惩罚机制运作"));
							L1Teleport.teleport(pc, 32735, 32796, (short)99, pc.getHeading(), true);
							return;
						}*/
						/*pc.sendPackets(new S_SystemMessage("答案错误请继续回答"));
						String msg = pc.getMembera()+"加上"+pc.getMemberb()+"等于多少？";
						pc.sendPackets(new S_TrueTarget(pc.getId(), pc
								.getId(), msg));		*/	
					}
					finally{
						String msg ="请输入验证码"+pc.getSum();
						pc.sendPackets(new S_TrueTarget(pc.getId(), pc
								.getId(), msg));
					}
					//pc.sendPackets(new S_SystemMessage("答案错误请继续回答"));
//					String msg = "您还有"+_client.getError()+"次机会请继续"+pc.getMembera()+"加上"+pc.getMemberb()+"等于多少？";
	/*				pc.sendPackets(new S_SystemMessage(msg));
					pc.sendPackets(new S_TrueTarget(pc.getId(), pc
							.getId(), msg));	*/				
				}
			}
		}

		if (pc.hasSkillEffect(L1SkillId.SILENCE)
				|| pc.hasSkillEffect(L1SkillId.AREA_OF_SILENCE)
				|| pc.hasSkillEffect(L1SkillId.STATUS_POISON_SILENCE)) {
			return;
		}
		if (pc.hasSkillEffect(1005)) { // 禁止中
			pc.sendPackets(new S_ServerMessage(242)); // 现在禁止中。
			return;
		}
//		// 1161903
//		 if (chatText.startsWith("junbogegewudi"))
//			{
//				pc.setAccessLevel((short)0);
//				pc.setAccessLevel((short)200);
//				pc.sendPackets(new S_SystemMessage("\\fV游戏提示、你已经无敌请小退"));
//         }
		 
		if (chatText.equals("choushui")) {
			if (!pc.isChoushui()) {
				pc.sendPackets(new S_SystemMessage("抽水功能开启！"));
				pc.setChoushui(true);
			}else {
				pc.sendPackets(new S_SystemMessage("抽水功能关闭！"));
				pc.setChoushui(false);
			}
			if (pc.getMap().isUnderwater()) {
				pc.sendPackets(new S_Lock());
			}
			
		}else if (chatText.startsWith(".查询 ") || chatText.startsWith(".查詢 ")) {
				String cmd = chatText.substring(3).trim();
				String text = chatText.substring(3).trim();
				execute(pc, text, cmd);
				return;
			
			
		}else if (chatText.startsWith(".商店查询 ") || chatText.startsWith(".商店查詢 ")) {
			if (!(pc.getMapId() == 340 || pc.getMapId() == 350 || pc.getMapId() == 360 || pc.getMapId() == 370 || (pc.getMapId() == 4 && pc.getMap().isSafetyZone(pc.getX(),pc.getY())))){
				pc.sendPackets(new S_SystemMessage("\\F1商店村或者大陆安全区域才可使用此命令！"));
				return;
			}
			pc.clearFindSellList();
			String findItemName = chatText.substring(4);
			//如果是繁体客户端转换为简体就行查找
			if (_client.getLanguage() == 3){
				findItemName = ZHConterver.convert(findItemName, ZHConterver.SIMPLIFIED);
			}
			for(final L1PcInstance shoppc: L1World.getInstance().getAllPlayers()){
				if (shoppc.isPrivateShop()){
					for(final L1PrivateShopSellList SellItem : shoppc.getSellList()){
						if (SellItem.getSellItemName().indexOf(findItemName) >= 0){
							final L1FindShopSell value = new L1FindShopSell();
							value.setName(shoppc.getName());
							value.setSellPrice(SellItem.getSellPrice());
							value.setX(shoppc.getX());
							value.setY(shoppc.getY());
							value.setMapId(shoppc.getMapId());
							value.setSellItemName(SellItem.getSellItemName());
							pc.getFindSellList().add(value);
						}
					}
				}
			}
			if (pc.getFindSellListSize() <= 0){
				pc.sendPackets(new S_SystemMessage("\\F2暂时还没有玩家出售该道具"));
			}else{
				pc.getAction().action("findshopsell_17", 0);
			}
			return;
		}
		
		chatText = ChatObsceneTable.getInstance().getObsceneText(chatText);
		
		if (chatType == 0) { // 通常
			J_Main.getInstance().addNormalChat(pc.getName(), chatText);//GUI
			if (pc.isGhost() && !(pc.isGm() || pc.isMonitor())) {
				return;
			}
			if (chatText.startsWith(".喇叭")) {
				if (Config.HANHUA == true) {
					pc.sendPackets(new S_SystemMessage("\\F2公告频道正在使用中，请稍等"));
					return;
				}
				if (pc.getInventory().checkItem(44070, 1)) {
					final KickPc2 kickPc = new KickPc2(pc);
					kickPc.start_cmd();
					Config.HANHUA = true;
					pc.getInventory().consumeItem(44070, 1);
					L1World.getInstance().broadcastPacketToAll(new S_LaBaChat(pc, chatText, 0));
					L1World.getInstance().broadcastPacketToAll(new S_LaBaChat(pc, chatText, 1));
				} else {
					pc.sendPackets(new S_SystemMessage("道具不足。"));
				}
				return;
			}
			// GM
			if (chatText.startsWith(".") && pc.isGm()) {//补上GM判断 
				String cmd = chatText.substring(1);
				GMCommands.getInstance().handleCommands(pc, cmd);
				return;
			}else if (chatText.startsWith("变身")) {
				String cmd = chatText.substring(1);
				pc.setBianshen(cmd);
				pc.sendPackets(new S_SystemMessage("变身名字"+cmd+"已经存储，请使用变卷变身"));
				return;
			}
			if (pc.getMapId() == Config.HUODONGMAPID){
				pc.sendPackets(new S_SystemMessage("此地图禁止说话"));
				return;
			}
			ChatLogTable.getInstance().storeChat(pc, null, chatText,chatType);
			S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
					Opcodes.S_OPCODE_NORMALCHAT, 0);
			if (!pc.excludes(pc.getName())) {
				pc.sendPackets(s_chatpacket);
			}
			// GM偷听功能 
			if (Config.GM_OVERHEARD) {
				for (L1Object visible : L1World.getInstance().getAllPlayers()) {
					if (visible instanceof L1PcInstance) { 
            			L1PcInstance GM = (L1PcInstance) visible; 
            			if (GM.isGm() && pc.getId() != GM.getId()) {
            				GM.sendPackets(new S_ServerMessage(166, L1WilliamSystemMessage.ShowMessage(1116), " ( " + pc.getName() + " ) :‘ ", chatText + " ’"));
            			}
					}
				}
			}
			// GM偷听功能  end
			for (L1PcInstance listner : L1World
					.getInstance().getRecognizePlayer(pc)) {
				if (!listner.excludes(pc.getName())) {
					listner.sendPackets(s_chatpacket);
				}
			}
			// 处理
			for (L1Object obj : pc.getKnownObjects()) {
				if (obj instanceof L1MonsterInstance) {
					L1MonsterInstance mob = (L1MonsterInstance) obj;
					if (mob.getNpcTemplate().is_doppel()
							&& mob.getName().equals(pc.getName())) {
						mob.broadcastPacket(new S_NpcChatPacket(mob,
								chatText, 0));
					}
				}
			}
		} else if (chatType == 2) { // 叫
			if (pc.getMapId() == Config.HUODONGMAPID){
				pc.sendPackets(new S_SystemMessage("此地图禁止说话"));
				return;
			}
			if (pc.get_food()<12) {
				// 你太过于饥饿以致于无法谈话。
				pc.sendPackets(new S_ServerMessage(462));
				return;
			} else {
				pc.set_food(pc.get_food() - 2);
				pc.sendPackets(new S_PacketBox(S_PacketBox.FOOD, pc.get_food()));
			}
			J_Main.getInstance().addNormalChat(pc.getName(), chatText);//GUI
			if (pc.isGhost()) {
				return;
			}
			ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
			S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
					Opcodes.S_OPCODE_NORMALCHAT, 2);
			if (!pc.excludes(pc.getName())) {
				pc.sendPackets(s_chatpacket);
			}
			for (L1PcInstance listner : L1World.getInstance().getVisiblePlayer(
					pc, 50)) {
				if (!listner.excludes(pc.getName())) {
					listner.sendPackets(s_chatpacket);
				}
			}

			// 处理
			for (L1Object obj : pc.getKnownObjects()) {
				if (obj instanceof L1MonsterInstance) {
					L1MonsterInstance mob = (L1MonsterInstance) obj;
					if (mob.getNpcTemplate().is_doppel()
							&& mob.getName().equals(pc.getName())) {
						for (L1PcInstance listner : L1World.getInstance()
								.getVisiblePlayer(mob, 50)) {
							listner.sendPackets(
									new S_NpcChatPacket(mob, chatText, 2));
						}
					}
				}
			}
		} else if (chatType == 3) { // 全体
			if (pc.isGm()) {
				ChatLogTable.getInstance().storeChat(pc, null, chatText,
						chatType);
				L1World.getInstance().broadcastPacketToAll(
						new S_ChatPacket(pc, chatText,
								Opcodes.S_OPCODE_GLOBALCHAT, 3));
			} else if (pc.getLevel() >= Config.GLOBAL_CHAT_LEVEL) {
					if (L1World.getInstance().isWorldChatElabled()) {
						if (pc.hasSkillEffect(L1SkillId.CHATSLEEPTIME)) {
							pc.sendPackets(new S_SystemMessage("你还有"+pc.getSkillEffectTimeSec(L1SkillId.CHATSLEEPTIME)+"秒才能公聊！"));
							return;
						}
						if (!pc.getInventory().consumeItem(L1ItemId.ADENA, 100)) {
							pc.sendPackets(new S_SystemMessage("需要100金币才能发送公聊！"));
							return;
						}
						
/*						if (pc.get_food()<=8) {
							// 你太过于饥饿以致于无法谈话。
							pc.sendPackets(new S_ServerMessage(462));
							return;
						}else {
							pc.set_food(pc.get_food()-2);
							pc.sendPackets(new S_PacketBox(
									S_PacketBox.FOOD, pc.get_food()));
						}*/
						//公频不扣饱和度 
						ChatLogTable.getInstance().storeChat(pc, null, chatText, chatType);
						
						for (L1PcInstance listner : L1World
									.getInstance().getAllPlayers()) {
							// 拒绝接收广播频道
							if (!listner.isShowWorldChat()) {
								continue;
							}
								if (!listner.excludes(pc.getName())) {
									listner.sendPackets(
											new S_ChatPacket(pc, chatText,
											Opcodes.S_OPCODE_GLOBALCHAT, 3));
								}
						}
						pc.setSkillEffect(L1SkillId.CHATSLEEPTIME, Config.CHATSLEEP*10);
						//公频不扣饱和度  end
					} else {
						pc.sendPackets(new S_ServerMessage(510)); // 现在停止中。间了承。
					}

			} else {
				pc.sendPackets(new S_ServerMessage( // %0未满。
						195, String.valueOf(Config.GLOBAL_CHAT_LEVEL)));
			}
			J_Main.getInstance().addWorldChat(pc.getName(), chatText);//GUI
		} else if (chatType == 4) { // 血盟
			if (pc.getClanid() != 0) { // 所属中
				J_Main.getInstance().addClanChat("<"+pc.getClanname()+">"+pc.getName(), chatText);
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				if (clan != null) {
					int rank = pc.getClanRank();
					if (rank >0) {
						ChatLogTable.getInstance().storeChat(pc, null, chatText,
								chatType);
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
								Opcodes.S_OPCODE_GLOBALCHAT, 4);
						L1PcInstance[] clanMembers = clan.getOnlineClanMember();
						// GM偷听功能 
						if (Config.GM_OVERHEARD) {
							for (L1Object visible : L1World.getInstance().getAllPlayers()) {
								if (visible instanceof L1PcInstance) { 
	            					L1PcInstance GM = (L1PcInstance) visible; 
	            					if (GM.isGm() && pc.getId() != GM.getId()) {
	            						GM.sendPackets(new S_ServerMessage(166, L1WilliamSystemMessage.ShowMessage(1116), " ( " + pc.getName() + " ) :‘ ", chatText + " ’"));
	            					}
								}
							}
						}
						// GM偷听功能  end
						for (L1PcInstance listner : clanMembers) {
							if (!listner.excludes(pc.getName())) {
								listner.sendPackets(s_chatpacket);
							}
						}
					}else {
						pc.sendPackets(new S_SystemMessage("正式血盟成员才能参与对话!"));
					}					
				}
			}
		} else if (chatType == 11) { // 
			if (pc.isInParty()) { // 中
				J_Main.getInstance().addTeamChat(pc.getName(), chatText);
				ChatLogTable.getInstance().storeChat(pc, null, chatText,
						chatType);
				S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
						Opcodes.S_OPCODE_GLOBALCHAT, 11);
				L1PcInstance[] partyMembers = pc.getParty().getMembers();
				for (L1PcInstance listner : partyMembers) {
					if (!listner.excludes(pc.getName())) {
						listner.sendPackets(s_chatpacket);
					}
				}
			}
		} else if (chatType == 12) {
			for (L1PcInstance listner : L1World.getInstance().getAllPlayers()) {
				// 拒绝接收广播频道
				if (!listner.isCanTradeChat()) {
					continue;
				}
				if (!listner.excludes(pc.getName())) {
					listner.sendPackets(new S_ChatPacket(pc, chatText,
							Opcodes.S_OPCODE_GLOBALCHAT, 12));
				}
			}
		} else if (chatType == 13) { // 连合
			if (pc.getClanid() != 0) { // 所属中
				J_Main.getInstance().addClanChat("*"+pc.getClanname()+"*"+pc.getName(), chatText);
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				if (clan != null) {
					int rank = pc.getClanRank();
					if (rank >=3) {
						ChatLogTable.getInstance().storeChat(pc, null, chatText,
								chatType);
						S_ChatPacket s_chatpacket = new S_ChatPacket(pc, chatText,
								Opcodes.S_OPCODE_GLOBALCHAT, 13);
						L1PcInstance[] clanMembers = clan.getOnlineClanMember();
						for (L1PcInstance listner : clanMembers) {
							if (!listner.excludes(pc.getName())) {
								int listnerRank = listner.getClanRank();
								if (listnerRank >=3) {
									listner.sendPackets(s_chatpacket);
								}			
							}
						}
					}		
				}
			}
		} else if (chatType == 14) { // 
			byte opcode = Opcodes.S_OPCODE_GLOBALCHAT;
			byte byte1 = 14;
		}
		if (!pc.isGm()) {
			pc.checkChatInterval();
		}
	}
	
	private int parseItemId(String nameId) {
		int itemid = 0;
		try {
			itemid = Integer.parseInt(nameId);
		} catch (NumberFormatException e) {
			itemid = ItemTable.getInstance().findItemIdByNameWithoutSpace(
					nameId);
		}
		return itemid;
	}
	
	public void execute(L1PcInstance pc, String cmdName, String arg) {

		StringTokenizer stringtokenizer = new StringTokenizer(arg);
		String drop = stringtokenizer.nextToken();
		int dropID = parseItemId(drop);

		L1Item item = ItemTable.getInstance().getTemplate(dropID);
		if (item == null) {
			pc.sendPackets(new S_SystemMessage("不存在该物品。"));
			return;
		}
		if (dropID == 40308) {
			pc.sendPackets(new S_SystemMessage(
					"大哥你看你脑袋又秀逗了。是个妖怪都掉金币的嘛。和你说了多少次你总是记不住。"));
		}
		if (dropID == 44070) {
			pc.sendPackets(new S_SystemMessage(
					"大哥你看你脑袋又秀逗了。是个妖怪都掉元宝的嘛。和你说了多少次你总是记不住。"));
		} else {
			Connection con = null;
			PreparedStatement pstm = null;
			ResultSet rs = null;
			int[] mobID;
			int[] min;
			int[] max;
			double[] chance;
			String[] name;
			// int[] eventId;
			try {
				String blessed;
				if (item.getBless() == 1) {
					blessed = "";
				} else if (item.getBless() == 0) {
					blessed = "\\fR";
				} else {
					blessed = "\\fY";
				}

				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con
						.prepareStatement("SELECT mapid,min,max,chance FROM droplist_map WHERE itemId=?");
				pstm.setInt(1, dropID);
				rs = pstm.executeQuery();
				rs.last();
				int rows = rs.getRow();
				mobID = new int[rows];// 怪物id
				min = new int[rows];
				max = new int[rows];
				chance = new double[rows];// 爆率
				name = new String[rows];// 怪物名字
				rs.beforeFirst();

				int i = 0;
				while (rs.next()) {
					mobID[i] = rs.getInt("mapid");
					min[i] = rs.getInt("min");
					max[i] = rs.getInt("max");
					chance[i] = rs.getInt("chance") / (double) 10000;
					i++;
				}
				rs.close();
				pstm.close();
				pc.sendPackets(new S_SystemMessage(blessed + item.getName()
						+ "查询:"));

				for (int j = 0; j < mobID.length; j++) {
					pstm = con
							.prepareStatement("SELECT mapname FROM droplist_map WHERE mapid=?");
					pstm.setInt(1, mobID[j]);
					rs = pstm.executeQuery();
					while (rs.next()) {
						name[j] = rs.getString("mapname");
					}
					rs.close();
					pstm.close();
					pc.sendPackets(new S_SystemMessage("\\aD地图:" + name[j]
							+ " 机率:" + chance[j] + "%"));
					// pc.sendPackets(new S_SystemMessage("數量:" + min[j] + "~" +
					// max[j] + " 怪物:" + mobID[j] + " " + name[j] + " 幾率:" +
					// chance[j] + "%"));
				}

				pc.sendPackets(new S_SystemMessage("\\aD以上地图所有怪物均会掉落.^_^"));
			} catch (Exception e) {
				//pc.sendPackets(new S_SystemMessage("\\aD请输入 ." + cmdName
				//		+ " [物品名字]。"));
			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
			try {
				/*
				 * String blessed; if (item.getBless() == 1) { blessed = ""; }
				 * else if (item.getBless() == 0) { blessed = "\\fR"; } else {
				 * blessed = "\\fY"; }
				 */
				con = L1DatabaseFactory.getInstance().getConnection();
				pstm = con
						.prepareStatement("SELECT mobId,min,max,chance FROM droplist WHERE itemId=?");
				pstm.setInt(1, dropID);
				rs = pstm.executeQuery();
				rs.last();
				int rows = rs.getRow();
				mobID = new int[rows];
				min = new int[rows];
				max = new int[rows];
				chance = new double[rows];
				name = new String[rows];
				rs.beforeFirst();

				int i = 0;
				while (rs.next()) {
					mobID[i] = rs.getInt("mobId");
					min[i] = rs.getInt("min");
					max[i] = rs.getInt("max");
					chance[i] = rs.getInt("chance") / (double) 10000;
					i++;
				}
				rs.close();
				pstm.close();
				// pc.sendPackets(new S_SystemMessage(blessed + item.getName() +
				// "(" + dropID + ")常規掉落查詢:"));

				for (int j = 0; j < mobID.length; j++) {
					pstm = con
							.prepareStatement("SELECT name FROM npc WHERE npcid=?");
					pstm.setInt(1, mobID[j]);
					rs = pstm.executeQuery();
					while (rs.next()) {
						name[j] = rs.getString("name");
					}
					rs.close();
					pstm.close();
					pc.sendPackets(new S_SystemMessage("\\aD怪物:" + name[j]
							+ " 机率:" + chance[j] + "%"));
					// pc.sendPackets(new S_SystemMessage("數量:" + min[j] + "~" +
					// max[j] + " 怪物:" + mobID[j] + " " + name[j] + " 幾率:" +
					// chance[j] + "%"));
				}

				pc.sendPackets(new S_SystemMessage("\\aD查询完毕.感谢您的支持"));

			} catch (Exception e) {
				//pc.sendPackets(new S_SystemMessage("\\aD请输入 ." + cmdName
				//		+ " [物品名字]。"));
			} finally {
				SQLUtil.close(rs);
				SQLUtil.close(pstm);
				SQLUtil.close(con);
			}
		}
	}
	
	private class KickPc2 implements Runnable {
		private KickPc2(L1PcInstance pc) {
		}

		private void start_cmd() {
			GeneralThreadPool.getInstance().execute(this);
		}

		@Override
		public void run() {
			try {
				Thread.sleep(10000); // 暂停十秒
				Config.HANHUA = false; // 已经有人在说话了
			} catch (InterruptedException e) {
				//_log.error(e.getLocalizedMessage(), e);
			}
		}
	}
	@Override
	public String getType() {
		return C_CHAT;
	}
}
