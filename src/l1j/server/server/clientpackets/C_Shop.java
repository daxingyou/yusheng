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
import java.util.concurrent.ConcurrentHashMap;

import l1j.server.AcceleratorChecker;
import l1j.server.Config;
import l1j.server.server.ActionCodes;
//import l1j.server.server.ClientThread;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1BabyInstance;//魔法娃娃 
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_Light;
import l1j.server.server.serverpackets.S_Lock;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;
import l1j.server.server.world.L1World;
import l1j.william.L1WilliamSystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Shop extends ClientBasePacket {

	private static final String C_SHOP = "[C] C_Shop";
	private static final Log _log = LogFactory.getLog(C_Shop.class);


	public C_Shop(byte abyte0[], LineageClient _client) {
		super(abyte0);

		try {
			L1PcInstance pc = _client.getActiveChar();
			if (pc.isGhost()) {
				return;
			}
			if (pc.isDead()) {
				return;
			}
			if (!pc.getMap().isSafetyZone(pc.getX(),pc.getY())) {
				return;
			}
			if (pc.getMapId() == 4){
				if (!pc.getInventory().checkEquipped(25069)){
					pc.sendPackets(new S_SystemMessage("\\F2当前市场无法摆摊 请到商店村"));
					return;
				}
			}else{
				if (pc.getMapId() != 340 && pc.getMapId() != 350 && pc.getMapId() != 360 && pc.getMapId() != 370){
					pc.sendPackets(new S_SystemMessage("\\F2当前市场无法摆摊 请到商店村"));
					return;
				}
			}
//			if (pc.isCheckTwopassword()){
//				pc.sendPackets(new S_SystemMessage("\\F3**请在聊天框输入二级密码才可正常游戏**"));
//				return;
//			}
			if (Config.CHECK_MOVE_INTERVAL) {
				final int result = pc.speed_Attack().checkIntervalmove();
				if (result == AcceleratorChecker.R_DISPOSED) {
					if (!pc.isGm()) {
						//WriteLogTxt.Recording("行走加速记录","玩家"+this._activeChar.getName()+"加速");
						pc.sendPackets(new S_Lock());
						return;
						//isError = true;
					}else {
						pc.sendPackets(new S_SystemMessage("防加速机制有效!"));
					}				
				}
			}		

			final ConcurrentHashMap<Integer,L1PrivateShopSellList> sellList = new ConcurrentHashMap<Integer,L1PrivateShopSellList>();// pc.getSellList();
			final ConcurrentHashMap<Integer,L1PrivateShopBuyList> buyList = new ConcurrentHashMap<Integer,L1PrivateShopBuyList>(); // pc.getBuyList();
			L1ItemInstance checkItem;
			boolean tradable = true;

			int type = readC();
			if (type == 0) { // 开始
				for (final L1Object obj : L1World.getInstance().getVisibleObjects(pc, 2)){
					if (obj instanceof L1PcInstance){
						final L1PcInstance tagpc = (L1PcInstance)obj;
						if (tagpc.isPrivateShop()){
							pc.sendPackets(new S_SystemMessage("\\F2其他玩家商店范围2格不可以摆摊!"));
							return;
						}
					}
				}
				int sellTotalCount = readH();
				int sellObjectId;
				int sellPrice;
				int sellCount;
				for (int i = 0; i < sellTotalCount; i++) {
					sellObjectId = readD();
					sellPrice = readD();
					sellCount = readD();
					if (sellPrice*sellCount<=0) {
						continue;
					}
					// 取引可能
					checkItem = pc.getInventory().getItem(sellObjectId);
					if (checkItem == null) {
						continue;
					}
					if (pc.getMapId() == 360){
						if (checkItem.getItem().getItemId() == 44070){
							tradable = false;
							pc.sendPackets(new S_SystemMessage("\\F2元宝不能在此商店村出售！"));
						}
					}
					if (checkItem.isSeal()) {
						tradable = false;
						pc.sendPackets(new S_SystemMessage(checkItem.getLogViewName() +"处于封印状态！"));
					}
					if (!checkItem.getItem().isTradable() && !checkItem.isTradable()) {
						tradable = false;
						pc.sendPackets(new S_ServerMessage(166, // \f1%0%4%1%3%2
								checkItem.getItem().getName(), L1WilliamSystemMessage.ShowMessage(1026))); // 从DB取得资讯 
					}
					if (checkItem.get_time() != null){
						tradable = false;
						pc.sendPackets(new S_ServerMessage(210, checkItem.getItem()
								.getName()));
					}
					Object[] petlist = pc.getPetList().values().toArray();
					for (Object petObject : petlist) {
						if (petObject instanceof L1PetInstance) {
							L1PetInstance pet = (L1PetInstance) petObject;
							if (checkItem.getId() == pet.getItemObjId()) {
								tradable = false;
								pc.sendPackets(new S_ServerMessage(166, // \f1%0%4%1%3%2
										checkItem.getItem().getName(),
										L1WilliamSystemMessage.ShowMessage(1026))); // 从DB取得资讯 
								break;
							}
						}
					}
					//魔法娃娃使用判断 
					for (Object babyObject : petlist) {
						if (babyObject instanceof L1BabyInstance) {
							L1BabyInstance baby = (L1BabyInstance) babyObject;
							if (checkItem.getId() == baby.getItemObjId()) {
								tradable = false;
								pc.sendPackets(new S_ServerMessage(1181));
								break;
							}
						}
					}
					//魔法娃娃使用判断  end
					if (sellPrice <= 0) {
						break;
					}
					if (sellCount <= 0) {
						break;
					}
					//开商店卖打开中的灯类 
					if (checkItem.getItemId() == 40001 || 
						checkItem.getItemId() == 40002 || 
						checkItem.getItemId() == 40004 || 
						checkItem.getItemId() == 40005) {
						pc.setPcLight(0);		
						if (checkItem.getEnchantLevel() != 0) {
							checkItem.setEnchantLevel(0);
						}
						pc.getInventory().updateItem(checkItem, L1PcInventory.COL_ENCHANTLVL);
						pc.sendPackets(new S_ItemName(checkItem));
				
						if (pc.hasSkillEffect(2)) {//日光术
							pc.setPcLight(14);
						}
							
						for(Object Light : pc.getInventory().getItems()) {
							L1ItemInstance OwnLight = (L1ItemInstance) Light;
							if((OwnLight.getItem().getItemId() == 40001 || 
							OwnLight.getItem().getItemId() == 40002 || 
							OwnLight.getItem().getItemId() == 40004 || 
							OwnLight.getItem().getItemId() == 40005) && 
							OwnLight.getEnchantLevel() != 0) {
								if(pc.getPcLight() < OwnLight.getItem().getLightRange()) {
									pc.setPcLight(OwnLight.getItem().getLightRange());
								}
							}
						}
							
						pc.sendPackets(new S_Light(pc.getId(), pc.getPcLight()));
						if (!pc.isInvisble() && checkItem.getItemId() != 40004) {//非隐身中跟魔法灯笼除外
							pc.broadcastPacket(new S_Light(pc.getId(), pc.getPcLight()));
						}
					}
					//开商店卖打开中的灯类  end
					L1PrivateShopSellList pssl = new L1PrivateShopSellList();
					pssl.setItemObjectId(sellObjectId);
					pssl.setSellPrice(sellPrice);
					pssl.setSellTotalCount(sellCount);
					final StringBuilder msg = new StringBuilder();
					if (checkItem.getItem().getType2() == 1 || checkItem.getItem().getType2() == 2){
						msg.append("+");
						msg.append(checkItem.getEnchantLevel());
						msg.append(" ");
					}
					if (checkItem.getBless() == 0){
						msg.append("受祝福的 ");
					}
					msg.append(checkItem.getItem().getName());
					pssl.setSellItemName(msg.toString());
					sellList.put(pssl.getItemObjectId(),pssl);
				}
				int buyTotalCount = readH();
				int buyObjectId;
				int buyPrice;
				int buyCount;
				for (int i = 0; i < buyTotalCount; i++) {
					buyObjectId = readD();
					buyPrice = readD();
					buyCount = readD();
					if (buyPrice <= 0) {
						break;
					}
					if (buyCount <= 0) {
						break;
					}
					// 取引可能
					checkItem = pc.getInventory().getItem(buyObjectId);
					if (checkItem == null) {
						break;
					}
					if (checkItem.isSeal()) {
						tradable = false;
						pc.sendPackets(new S_SystemMessage(checkItem.getLogViewName() +"处于封印状态！"));
					}
					if (!checkItem.getItem().isTradable() && !checkItem.isTradable()) {
						tradable = false;
						pc.sendPackets(new S_ServerMessage(166, // \f1%0%4%1%3%2
								checkItem.getItem().getName(), L1WilliamSystemMessage.ShowMessage(1026))); // 从DB取得资讯 
					}
					if (checkItem.get_time() != null){
						tradable = false;
						pc.sendPackets(new S_ServerMessage(210, checkItem.getItem()
								.getName()));
					}
					Object[] petlist = pc.getPetList().values().toArray();
					for (Object petObject : petlist) {
						if (petObject instanceof L1PetInstance) {
							L1PetInstance pet = (L1PetInstance) petObject;
							if (checkItem.getId() == pet.getItemObjId()) {
								tradable = false;
								pc.sendPackets(new S_ServerMessage(166, // \f1%0%4%1%3%2
										checkItem.getItem().getName(),
										L1WilliamSystemMessage.ShowMessage(1026))); // 从DB取得资讯 
								break;
							}
						}
					}
					//魔法娃娃使用判断 
					for (Object babyObject : petlist) {
						if (babyObject instanceof L1BabyInstance) {
							L1BabyInstance baby = (L1BabyInstance) babyObject;
							if (checkItem.getId() == baby.getItemObjId()) {
								tradable = false;
								pc.sendPackets(new S_ServerMessage(1181));
								break;
							}
						}
					}
					//魔法娃娃使用判断  end
					L1PrivateShopBuyList psbl = new L1PrivateShopBuyList();
					psbl.setItemObjectId(buyObjectId);
					psbl.setItemId(checkItem.getItemId());
					psbl.setBuyPrice(buyPrice);
					psbl.setBuyTotalCount(buyCount);
					buyList.put(psbl.getItemObjectId(),psbl);
				}
				if (!tradable) { // 取引不可能含场合、个人商店终了
					sellList.clear();
					buyList.clear();
					pc.setPrivateShop(false);
					pc.sendPackets(new S_DoActionGFX(pc.getId(),
							ActionCodes.ACTION_Idle));
					pc.broadcastPacket(new S_DoActionGFX(pc.getId(),
							ActionCodes.ACTION_Idle));
					return;
				}
				pc.getBuyList().clear();
				pc.getSellList().clear();
				if (sellList.size()>8) {
					fail(pc);
					return;
				}
				pc.getSellList().addAll(sellList.values());
				pc.getBuyList().addAll(buyList.values());
				//byte[] chat = readByte();
				final String[] chat = readShopString();
				pc.setShopChat(chat);
				pc.setPrivateShop(true);
				pc.setStatus(ActionCodes.ACTION_Shop);
				pc.sendPackets(new S_DoActionShop(pc.getId(),
						ActionCodes.ACTION_Shop, chat));
				pc.broadcastPacket(new S_DoActionShop(pc.getId(),
						ActionCodes.ACTION_Shop, chat));
			} else if (type == 1) { // 终了
				sellList.clear();
				buyList.clear();
				pc.getBuyList().clear();
				pc.getSellList().clear();
				fail(pc);
/*				pc.setPrivateShop(false);
				pc.setStatus(0);
				pc.sendPackets(new S_DoActionGFX(pc.getId(),
						ActionCodes.ACTION_Idle));
				pc.broadcastPacket(new S_DoActionGFX(pc.getId(),
						ActionCodes.ACTION_Idle));*/
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally{
			this.over();
		}
		
	}
	
	private void fail(final L1PcInstance pc)
	{
		pc.setPrivateShop(false);
		pc.setStatus(0);
		pc.sendPackets(new S_DoActionGFX(pc.getId(),
				ActionCodes.ACTION_Idle));
		pc.broadcastPacket(new S_DoActionGFX(pc.getId(),
				ActionCodes.ACTION_Idle));	
	}

	@Override
	public String getType() {
		return C_SHOP;
	}

}
