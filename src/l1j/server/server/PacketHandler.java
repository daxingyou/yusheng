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

package l1j.server.server;

import static l1j.server.server.Opcodes.C_OPCODE_ADDBUDDY;
import static l1j.server.server.Opcodes.C_OPCODE_AMOUNT;
import static l1j.server.server.Opcodes.C_OPCODE_ARROWATTACK;
import static l1j.server.server.Opcodes.C_OPCODE_ATTACK;
import static l1j.server.server.Opcodes.C_OPCODE_ATTR;
import static l1j.server.server.Opcodes.C_OPCODE_BANCLAN;
import static l1j.server.server.Opcodes.C_OPCODE_BANPARTY;
import static l1j.server.server.Opcodes.C_OPCODE_BOARD;
import static l1j.server.server.Opcodes.C_OPCODE_BOARDBACK;
import static l1j.server.server.Opcodes.C_OPCODE_BOARDDELETE;
import static l1j.server.server.Opcodes.C_OPCODE_BOARDREAD;
import static l1j.server.server.Opcodes.C_OPCODE_BOARDWRITE;
import static l1j.server.server.Opcodes.C_OPCODE_BOOKMARK;
import static l1j.server.server.Opcodes.C_OPCODE_BOOKMARKDELETE;
import static l1j.server.server.Opcodes.C_OPCODE_BUDDYLIST;
import static l1j.server.server.Opcodes.C_OPCODE_CALL;
import static l1j.server.server.Opcodes.C_OPCODE_CHANGECHAR;
import static l1j.server.server.Opcodes.C_OPCODE_CHANGEHEADING;
import static l1j.server.server.Opcodes.C_OPCODE_CHANGEWARTIME;
import static l1j.server.server.Opcodes.C_OPCODE_CHARACTERCONFIG;
import static l1j.server.server.Opcodes.C_OPCODE_CHAT;
import static l1j.server.server.Opcodes.C_OPCODE_CHATGLOBAL;
import static l1j.server.server.Opcodes.C_OPCODE_CHATWHISPER;
import static l1j.server.server.Opcodes.C_OPCODE_CHECKPK;
import static l1j.server.server.Opcodes.C_OPCODE_CLIENTVERSION;
import static l1j.server.server.Opcodes.C_OPCODE_COMMONCLICK;
import static l1j.server.server.Opcodes.C_OPCODE_CREATECLAN;
import static l1j.server.server.Opcodes.C_OPCODE_CREATEPARTY;
import static l1j.server.server.Opcodes.C_OPCODE_DELBUDDY;
import static l1j.server.server.Opcodes.C_OPCODE_DELETECHAR;
import static l1j.server.server.Opcodes.C_OPCODE_DELETEINVENTORYITEM;
import static l1j.server.server.Opcodes.C_OPCODE_DEPOSIT;
import static l1j.server.server.Opcodes.C_OPCODE_DOOR;
import static l1j.server.server.Opcodes.C_OPCODE_DRAWAL;
import static l1j.server.server.Opcodes.C_OPCODE_DROPITEM;
import static l1j.server.server.Opcodes.C_OPCODE_EMBLEM;
import static l1j.server.server.Opcodes.C_OPCODE_ENTERPORTAL;
import static l1j.server.server.Opcodes.C_OPCODE_EXCLUDE;
import static l1j.server.server.Opcodes.C_OPCODE_EXIT_GHOST;
import static l1j.server.server.Opcodes.C_OPCODE_EXTCOMMAND;
import static l1j.server.server.Opcodes.C_OPCODE_FISHCLICK;
import static l1j.server.server.Opcodes.C_OPCODE_FIX_WEAPON_LIST;
import static l1j.server.server.Opcodes.C_OPCODE_GIVEITEM;
import static l1j.server.server.Opcodes.C_OPCODE_HIRESOLDIER;
import static l1j.server.server.Opcodes.C_OPCODE_JOINCLAN;
import static l1j.server.server.Opcodes.C_OPCODE_KEEPALIVE;
import static l1j.server.server.Opcodes.C_OPCODE_LEAVECLANE;
import static l1j.server.server.Opcodes.C_OPCODE_LEAVEPARTY;
import static l1j.server.server.Opcodes.C_OPCODE_LOGINPACKET;
import static l1j.server.server.Opcodes.C_OPCODE_LOGINTOSERVER;
import static l1j.server.server.Opcodes.C_OPCODE_LOGINTOSERVEROK;
import static l1j.server.server.Opcodes.C_OPCODE_MOVECHAR;
import static l1j.server.server.Opcodes.C_OPCODE_NEWCHAR;
import static l1j.server.server.Opcodes.C_OPCODE_NPCACTION;
import static l1j.server.server.Opcodes.C_OPCODE_NPCTALK;
import static l1j.server.server.Opcodes.C_OPCODE_PARTY;
import static l1j.server.server.Opcodes.C_OPCODE_PICKUPITEM;
import static l1j.server.server.Opcodes.C_OPCODE_PLEDGE;
import static l1j.server.server.Opcodes.C_OPCODE_PRIVATESHOPLIST;
import static l1j.server.server.Opcodes.C_OPCODE_PROPOSE;
import static l1j.server.server.Opcodes.C_OPCODE_QUITGAME;
import static l1j.server.server.Opcodes.C_OPCODE_RANK;
import static l1j.server.server.Opcodes.C_OPCODE_RESTART;
import static l1j.server.server.Opcodes.C_OPCODE_RESULT;
import static l1j.server.server.Opcodes.C_OPCODE_RETURNTOLOGIN;
import static l1j.server.server.Opcodes.C_OPCODE_SELECTLIST;
import static l1j.server.server.Opcodes.C_OPCODE_SHOP;
import static l1j.server.server.Opcodes.C_OPCODE_SKILLBUY;
import static l1j.server.server.Opcodes.C_OPCODE_SKILLBUYOK;
import static l1j.server.server.Opcodes.C_OPCODE_TAXRATE;
import static l1j.server.server.Opcodes.C_OPCODE_TELEPORT;
import static l1j.server.server.Opcodes.C_OPCODE_TITLE;
import static l1j.server.server.Opcodes.C_OPCODE_TRADE;
import static l1j.server.server.Opcodes.C_OPCODE_TRADEADDCANCEL;
import static l1j.server.server.Opcodes.C_OPCODE_TRADEADDITEM;
import static l1j.server.server.Opcodes.C_OPCODE_TRADEADDOK;
import static l1j.server.server.Opcodes.C_OPCODE_USEITEM;
import static l1j.server.server.Opcodes.C_OPCODE_USESKILL;
import static l1j.server.server.Opcodes.C_OPCODE_WAR;
import static l1j.server.server.Opcodes.C_OPCODE_WHO;
import static l1j.server.server.Opcodes.C_OPCODE_MOVELOCK;
import l1j.server.server.clientpackets.C_AddBookmark;
import l1j.server.server.clientpackets.C_AddBuddy;
import l1j.server.server.clientpackets.C_Amount;
import l1j.server.server.clientpackets.C_Attack;
import l1j.server.server.clientpackets.C_AttackBow;
import l1j.server.server.clientpackets.C_AttackTarget;
import l1j.server.server.clientpackets.C_Attr;
import l1j.server.server.clientpackets.C_AuthLogin;
import l1j.server.server.clientpackets.C_BanClan;
import l1j.server.server.clientpackets.C_BanParty;
import l1j.server.server.clientpackets.C_Board;
import l1j.server.server.clientpackets.C_BoardBack;
import l1j.server.server.clientpackets.C_BoardDelete;
import l1j.server.server.clientpackets.C_BoardRead;
import l1j.server.server.clientpackets.C_BoardWrite;
import l1j.server.server.clientpackets.C_Buddy;
import l1j.server.server.clientpackets.C_CallPlayer;
import l1j.server.server.clientpackets.C_ChangeHeading;
import l1j.server.server.clientpackets.C_ChangeWarTime;
import l1j.server.server.clientpackets.C_CharcterConfig;
import l1j.server.server.clientpackets.C_CharcterTwoPassword;
import l1j.server.server.clientpackets.C_Chat;
import l1j.server.server.clientpackets.C_ChatWhisper;
import l1j.server.server.clientpackets.C_CheckPK;
import l1j.server.server.clientpackets.C_CommonClick;
import l1j.server.server.clientpackets.C_CreateChar;
import l1j.server.server.clientpackets.C_CreateClan;
import l1j.server.server.clientpackets.C_CreateParty;
import l1j.server.server.clientpackets.C_DelBuddy;
import l1j.server.server.clientpackets.C_DeleteBookmark;
import l1j.server.server.clientpackets.C_DeleteChar;
import l1j.server.server.clientpackets.C_DeleteInventoryItem;
import l1j.server.server.clientpackets.C_Deposit;
import l1j.server.server.clientpackets.C_Door;
import l1j.server.server.clientpackets.C_Drawal;
import l1j.server.server.clientpackets.C_DropItem;
import l1j.server.server.clientpackets.C_Emblem;
import l1j.server.server.clientpackets.C_EnterPortal;
import l1j.server.server.clientpackets.C_Exclude;
import l1j.server.server.clientpackets.C_ExitGhost;
import l1j.server.server.clientpackets.C_ExtraCommand;
import l1j.server.server.clientpackets.C_FishClick;
import l1j.server.server.clientpackets.C_FixWeaponList;
import l1j.server.server.clientpackets.C_GiveItem;
import l1j.server.server.clientpackets.C_HireSoldier;
import l1j.server.server.clientpackets.C_ItemUSe;
import l1j.server.server.clientpackets.C_JoinClan;
import l1j.server.server.clientpackets.C_KeepALIVE;
import l1j.server.server.clientpackets.C_LeaveClan;
import l1j.server.server.clientpackets.C_LeaveParty;
import l1j.server.server.clientpackets.C_LoginToServer;
import l1j.server.server.clientpackets.C_LoginToServerOK;
import l1j.server.server.clientpackets.C_MoveChar;
import l1j.server.server.clientpackets.C_NPCAction;
import l1j.server.server.clientpackets.C_NPCTalk;
import l1j.server.server.clientpackets.C_NewCharSelect;
import l1j.server.server.clientpackets.C_Party;
import l1j.server.server.clientpackets.C_PickUpItem;
import l1j.server.server.clientpackets.C_Pledge;
import l1j.server.server.clientpackets.C_Propose;
import l1j.server.server.clientpackets.C_Rank;
import l1j.server.server.clientpackets.C_Restart;
import l1j.server.server.clientpackets.C_Result;
import l1j.server.server.clientpackets.C_ReturnToLogin;
import l1j.server.server.clientpackets.C_SelectList;
import l1j.server.server.clientpackets.C_ServerVersion;
import l1j.server.server.clientpackets.C_Shop;
import l1j.server.server.clientpackets.C_ShopList;
import l1j.server.server.clientpackets.C_SkillBuy;
import l1j.server.server.clientpackets.C_SkillBuyOK;
import l1j.server.server.clientpackets.C_TaxRate;
import l1j.server.server.clientpackets.C_Teleport;
import l1j.server.server.clientpackets.C_Title;
import l1j.server.server.clientpackets.C_Trade;
import l1j.server.server.clientpackets.C_TradeAddItem;
import l1j.server.server.clientpackets.C_TradeCancel;
import l1j.server.server.clientpackets.C_TradeOK;
import l1j.server.server.clientpackets.C_UnLock;
import l1j.server.server.clientpackets.C_UseSkill;
import l1j.server.server.clientpackets.C_War;
import l1j.server.server.clientpackets.C_Who;
import l1j.server.server.clientpackets.C_Windows;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.Instance.L1PcInstance;

import org.apache.mina.core.session.IoSession;

// Referenced classes of package l1j.server.server:
// Opcodes, LoginController, ClientThread, Logins

public class PacketHandler {

/*	public PacketHandler(ClientThread clientthread) {
		_client = clientthread;
	}*/

	public PacketHandler(IoSession session) {
		_session = session;
		LineageClient lineageClient = (LineageClient) session
				.getAttribute(LineageClient.CLIENT_KEY);
		_client = lineageClient;
	}

	public void handlePacket(byte decrypt[], L1PcInstance pc)
			throws Exception {
		if (decrypt == null) {
			return;
		}
		if (decrypt.length <= 0) {
			return;
		}
		
		int i = decrypt[0] & 0xff;
		
		decrypt[0] = (byte)(_client.getLanguage() & 0xff);//设置封包来至的语言
		
		//System.out.println("当前[a]OP为:"+i);
		
		if (i  == C_OPCODE_CHARACTERCONFIG) {
			new C_CharcterConfig(decrypt, _client);
			return;
		}
		
		if (pc == null) {
			WriteLogTxt.Recording("非法玩家","玩家不存在时拒绝发送封包: " + 
					"\nOP ID: " + i + " length:" + decrypt.length +
					"\nInfo:\n" + printData(decrypt, decrypt.length));
			return;
		}
		
		
		if (_client.isCharReStart()) {
			WriteLogTxt.Recording("非法玩家","拒绝非法玩家<"+pc.getName()+">使用封包: " + 
					"\nOP ID: " + i + " length:" + decrypt.length +
					"\nInfo:\n" + printData(decrypt, decrypt.length));
			_client.kick();
			return;
		}

		switch (i) {
		case Opcodes.C_OPCODE_WINDOWS:
			new C_Windows(decrypt, _client);
			break;
		case Opcodes.C_OPCODE_PWD:
			new C_CharcterTwoPassword(decrypt, _client);
			break;
		case C_OPCODE_RANK:
			new C_Rank(decrypt, _client);
			break;
		case C_OPCODE_EXCLUDE:
			new C_Exclude(decrypt, _client);
			break;

		case C_OPCODE_DOOR:
			new C_Door(decrypt, _client);
			break;

		case C_OPCODE_TITLE:
			new C_Title(decrypt, _client);
			break;

		case C_OPCODE_BOARDDELETE:
			new C_BoardDelete(decrypt, _client);
			break;

		case C_OPCODE_PLEDGE:
			new C_Pledge(decrypt, _client);
			break;

		case C_OPCODE_CHANGEHEADING:
			new C_ChangeHeading(decrypt, _client);
			break;

		case C_OPCODE_NPCACTION:
			new C_NPCAction(decrypt, _client);
			break;

		case C_OPCODE_USESKILL:
			new C_UseSkill(decrypt, _client);
			break;

		case C_OPCODE_EMBLEM:
			new C_Emblem(decrypt, _client);
			break;

		case C_OPCODE_TRADEADDCANCEL:
			new C_TradeCancel(decrypt, _client);
			break;

		case C_OPCODE_CHANGEWARTIME:
			new C_ChangeWarTime(decrypt, _client);
			break;

		case C_OPCODE_BOOKMARK:
			new C_AddBookmark(decrypt, _client);
			break;

		case C_OPCODE_CREATECLAN:
			new C_CreateClan(decrypt, _client);
			break;
/**
 * 验证版本
 */
		

		case C_OPCODE_PROPOSE:
			new C_Propose(decrypt, _client);
			break;

		case C_OPCODE_SKILLBUY:
			new C_SkillBuy(decrypt, _client);
			break;

		case C_OPCODE_BOARDBACK:
			new C_BoardBack(decrypt, _client);
			break;

		case C_OPCODE_SHOP:
			new C_Shop(decrypt, _client);
			break;

		case C_OPCODE_BOARDREAD:
			new C_BoardRead(decrypt, _client);
			break;

		case C_OPCODE_TRADE:
			new C_Trade(decrypt, _client);
			break;
/**
 * 删除角色
 */
		

		case C_OPCODE_KEEPALIVE:
			new C_KeepALIVE(decrypt, _client);
			break;

		case C_OPCODE_ATTR:
			new C_Attr(decrypt, _client);
			break;
/**
 * 登录帐号
 */
		

		case C_OPCODE_RESULT:
			new C_Result(decrypt, _client);
			break;

		case C_OPCODE_DEPOSIT:
			new C_Deposit(decrypt, _client);
			break;

		case C_OPCODE_DRAWAL:
			new C_Drawal(decrypt, _client);
			break;

		case C_OPCODE_LOGINTOSERVEROK:
			new C_LoginToServerOK(decrypt, _client);
			break;

		case C_OPCODE_SKILLBUYOK:
			new C_SkillBuyOK(decrypt, _client);
			break;

		case C_OPCODE_TRADEADDITEM:
			new C_TradeAddItem(decrypt, _client);
			break;

		case C_OPCODE_ADDBUDDY:
			new C_AddBuddy(decrypt, _client);
			break;
/**
 * 小退
 */
		

		case C_OPCODE_CHAT:
			new C_Chat(decrypt, _client);
			break;

		case C_OPCODE_TRADEADDOK:
			new C_TradeOK(decrypt, _client);
			break;

		case C_OPCODE_CHECKPK:
			new C_CheckPK(decrypt, _client);
			break;

		case C_OPCODE_TAXRATE:
			new C_TaxRate(decrypt, _client);
			break;

		case C_OPCODE_CHANGECHAR:
			new C_NewCharSelect(decrypt, _client);
			//new C_CommonClick(decrypt,_client);
			break;

		case C_OPCODE_BUDDYLIST:
			new C_Buddy(decrypt, _client);
			break;

		case C_OPCODE_DROPITEM:
			new C_DropItem(decrypt, _client);
			break;

		case C_OPCODE_LEAVEPARTY:
			new C_LeaveParty(decrypt, _client);
			break;

		case C_OPCODE_ATTACK:
			new C_Attack(decrypt, _client);
			break;
		case C_OPCODE_ARROWATTACK:
			new C_AttackBow(decrypt, _client);
			break;

		// 态中动场合
		// 态付加送信
		// 送终了时

		case C_OPCODE_BANCLAN:
			new C_BanClan(decrypt, _client);
			break;

		case C_OPCODE_BOARD:
			new C_Board(decrypt, _client);
			break;

		case C_OPCODE_DELETEINVENTORYITEM:
			new C_DeleteInventoryItem(decrypt, _client);
			break;

		case C_OPCODE_CHATWHISPER:
			new C_ChatWhisper(decrypt, _client);
			break;

		case C_OPCODE_PARTY:
			new C_Party(decrypt, _client);
			break;

		case C_OPCODE_PICKUPITEM:
			new C_PickUpItem(decrypt, _client);
			break;
   
		case C_OPCODE_WHO:
			new C_Who(decrypt, _client);
			break;

		case C_OPCODE_GIVEITEM:
			new C_GiveItem(decrypt, _client);
			break;

		case C_OPCODE_MOVECHAR:
			new C_MoveChar(decrypt, _client);
			break;

		case C_OPCODE_BOOKMARKDELETE:
			new C_DeleteBookmark(decrypt, _client);
			break;

		case C_OPCODE_RESTART:
			new C_Restart(decrypt, _client);
			break;

		case C_OPCODE_LEAVECLANE:
			new C_LeaveClan(decrypt, _client);
			break;

		case C_OPCODE_NPCTALK:
			new C_NPCTalk(decrypt, _client);
			break;

		case C_OPCODE_BANPARTY:
			new C_BanParty(decrypt, _client);
			break;

		case C_OPCODE_DELBUDDY:
			new C_DelBuddy(decrypt, _client);
			break;

		case C_OPCODE_WAR:
			new C_War(decrypt, _client);
			break;
/**
 * 登录角色
 */
		
		case C_OPCODE_PRIVATESHOPLIST:
			new C_ShopList(decrypt, _client);
			break;

		case C_OPCODE_CHATGLOBAL:
			new C_Chat(decrypt, _client);
			break;

		case C_OPCODE_JOINCLAN:
			new C_JoinClan(decrypt, _client);
			break;
/**
 * 换角色
 */
/**
 * 新建角色
 */
		

		case C_OPCODE_EXTCOMMAND:
			new C_ExtraCommand(decrypt, _client);
			break;

		case C_OPCODE_BOARDWRITE:
			new C_BoardWrite(decrypt, _client);
			break;

		case C_OPCODE_USEITEM:
			new C_ItemUSe(decrypt, _client);
			break;

		case C_OPCODE_CREATEPARTY:
			new C_CreateParty(decrypt, _client);
			break;

		case C_OPCODE_ENTERPORTAL:
			new C_EnterPortal(decrypt, _client);
			break;

		case C_OPCODE_AMOUNT:
			new C_Amount(decrypt, _client);
			break;

		case C_OPCODE_FIX_WEAPON_LIST:
			new C_FixWeaponList(decrypt, _client);
			break;

		case C_OPCODE_SELECTLIST:
			new C_SelectList(decrypt, _client);
			break;

		case C_OPCODE_EXIT_GHOST:
			new C_ExitGhost(decrypt, _client);
			break;

		case C_OPCODE_CALL:
			new C_CallPlayer(decrypt, _client);
			break;

		case C_OPCODE_HIRESOLDIER:
			new C_HireSoldier(decrypt, _client);
			break;

		case C_OPCODE_FISHCLICK:
			new C_FishClick(decrypt, _client);
			break;
		case C_OPCODE_TELEPORT:
			new C_Teleport(decrypt, _client);
			break;
		case C_OPCODE_MOVELOCK:
			new C_UnLock(decrypt, _client);
			break;
		case C_OPCODE_QUITGAME:
			_session.close(true);
			break;
		// 宠物指定攻击 
		case 114:
			new C_AttackTarget(decrypt, _client);
			break;

		default:
/*			System.out.println("玩家<"+pc.getName()+">在地图ID#"+pc.getMapId()+"#X:<"
		    +pc.getX()+">Y:"+pc.getY()+"使用未知封包: " 
					+"\nOP ID: " + i + " length:" + decrypt.length 
					+"\nInfo:\n" + printData(decrypt, decrypt.length));*/
			if (i != 41){
				WriteLogTxt.Recording("未知封包","玩家<"+pc.getName()+">使用未知封包: " + 
						"\nOP ID: " + i + " length:" + decrypt.length +
						"\nInfo:\n" + printData(decrypt, decrypt.length));
			}
			// String s = Integer.toHexString(abyte0[0] & 0xff);
			// _log.warning("用途不明:容");
			// _log.warning((new StringBuilder()).append(" ").append(s)
			// .toString());
			// _log.warning(new ByteArrayUtil(abyte0).dumpToString());
			break;
		}
/*		System.out.println("测试未知封包: " + 
				"\nOP ID: " + i + " length:" + decrypt.length +
				"\nInfo:\n" + printData(decrypt, decrypt.length));*/
/*		if (i == 28) {
			System.out.println("客户端转移封包: " + 
					"\nOP ID: " + i + " length:" + abyte0.length +
					"\nInfo:\n" + printData(abyte0, abyte0.length));
		}
		if (i == C_OPCODE_TRADEADDOK) {
			System.out.println("客户端交易成功封包: " + 
					"\nOP ID: " + i + " length:" + abyte0.length +
					"\nInfo:\n" + printData(abyte0, abyte0.length));
		}*/
		
		// _log.warning((new StringBuilder()).append("
		// ").append(i).toString());
	}
	
	public void handlePacket(byte abyte0[])
			throws Exception {
		int key = abyte0[0] & 0xff;
//		System.out.println("当前[b]OP为:"+key);	
		if (!_client.isCharReStart()) {
			return;
		}

		abyte0[0] = (byte)(_client.getLanguage() & 0xff);//设置封包来至的语言
		
		switch (key) {
		case C_OPCODE_CLIENTVERSION:
			new C_ServerVersion(abyte0, _client);
			break;
		case C_OPCODE_LOGINPACKET:
			new C_AuthLogin(abyte0, _client);
			break;
		case C_OPCODE_LOGINTOSERVER:
			new C_LoginToServer(abyte0, _client);
			break;
		case C_OPCODE_COMMONCLICK:
			new C_CommonClick(abyte0, _client);
			break;
		case C_OPCODE_NEWCHAR:
			new C_CreateChar(abyte0, _client);
			break;
		case C_OPCODE_RETURNTOLOGIN:
			new C_ReturnToLogin(abyte0, _client);
			break;
		case C_OPCODE_DELETECHAR:
			new C_DeleteChar(abyte0, _client);
			break;
		//case C_OPCODE_CHANGEPASSWORDS:
			//new C_ChangePassWords(abyte0, _client);
			//break;
		case C_OPCODE_QUITGAME:
			_session.close(true);
			break;
		default:
			String name = _client.getAccountName();
			if (name==null) {
				name ="无";
			}
/*			System.out.println("帐号<"+name+">未登录角色情况下的未知封包: " + 
					"\nOP ID: " + key + " length:" + abyte0.length +
					"\nInfo:\n" + printData(abyte0, abyte0.length));*/
			if (key != 41){
				WriteLogTxt.Recording("未知封包","帐号<"+name+">未登录角色情况下的未知封包: " + 
						"\nOP ID: " + key + " length:" + abyte0.length +
						"\nInfo:\n" + printData(abyte0, abyte0.length));
			}
			break;
			
		}
/*		System.out.println("测试封包: " + 
				"\nOP ID: " + key+ " length:" + abyte0.length +
				"\nInfo:\n" + printData(abyte0, abyte0.length));*/
	}
	
	/**
	 * <font color=#0000ff>印出封包</font>
	 * 目的:<BR>
	 * 用于检查客户端传出的封包资料<BR>
	 * @param data
	 * @param len
	 * @return
	 */
	public static String printData(final byte[] data, final int len) {

		final StringBuffer result = new StringBuffer();

		int counter = 0;

		for (int i = 0; i < len; i++) {

			if (counter % 16 == 0) {
				result.append(fillHex(i, 4) + ": ");
			}

			result.append(fillHex(data[i] & 0xff, 2) + " ");
			counter++;

			if (counter == 16) {
				result.append("   ");

				int charpoint = i - 15;
				for (int a = 0; a < 16; a++) {
					final int t1 = data[charpoint++];

					if ((t1 > 0x1f) && (t1 < 0x80)) {
						result.append((char) t1);
					} else {
						result.append('.');
					}
				}

				result.append("\n");
				counter = 0;
			}
		}

		final int rest = data.length % 16;

		if (rest > 0) {

			for (int i = 0; i < 17 - rest; i++) {
				result.append("   ");
			}

			int charpoint = data.length - rest;

			for (int a = 0; a < rest; a++) {

				final int t1 = data[charpoint++];

				if ((t1 > 0x1f) && (t1 < 0x80)) {
					result.append((char) t1);
				} else {
					result.append('.');
				}
			}

			result.append("\n");
		}

		return result.toString();
	}

	/**
	 * <font color=#0000ff>将数字转成 16 进位</font>
	 * @param data
	 * @param digits
	 * @return
	 */
	private static String fillHex(final int data, final int digits) {

		String number = Integer.toHexString(data);

		for (int i = number.length(); i < digits; i++) {
			number = "0" + number;
		}

		return number;
	}

	private final LineageClient _client;
	
	private final IoSession _session;
	
//	private final LineageClient _minaclient;
}