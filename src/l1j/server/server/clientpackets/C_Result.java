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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import l1j.server.Config; // 全道具贩卖 
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.CenterTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.ShopTable;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1BabyInstance;//魔法娃娃 
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.center.L1Center;
import l1j.server.server.model.center.L1CenterBuyOrderList;
import l1j.server.server.model.center.L1CenterSellOrderList;
import l1j.server.server.model.game.GamblingTimeList;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.shop.L1Shop;
import l1j.server.server.model.shop.L1ShopBuyOrderList;
import l1j.server.server.model.shop.L1ShopSellOrderList;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_Light;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Pc;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;
import l1j.server.server.utils.IntRange; // 全道具贩卖 
import l1j.server.server.world.L1World;
import l1j.william.L1WilliamItemPrice; // 全道具贩卖 

public class C_Result extends ClientBasePacket {


	private static final String C_RESULT = "[C] C_Result";
	
	private static GamblingTimeList _gam = GamblingTimeList.gam(); // 修正

	public C_Result(byte abyte0[], LineageClient _client) throws Exception {
		super(abyte0);
		int npcObjectId = readD();
		int resultType = readC();
		int size = readC();
		int unknown = readC();

		L1PcInstance pc = _client.getActiveChar();
		if (pc.isPrivateShop()) {
			return;
		}
		int level = pc.getLevel();

		L1NpcInstance targetNpc = null;
		int npcId = 0;
		String npcImpl = "";
		boolean isPrivateShop = false;
		boolean tradable = true;
		L1Object findObject = L1World.getInstance().findObject(npcObjectId);
		if (findObject != null) {
			int diffLocX = Math.abs(pc.getX() - findObject.getX());
			int diffLocY = Math.abs(pc.getY() - findObject.getY());
			// 3以上离场合无佅
			if (diffLocX > 3 || diffLocY > 3) {
				return;
			}
			if (findObject instanceof L1NpcInstance) {
				targetNpc = (L1NpcInstance) findObject;
				npcId = targetNpc.getNpcTemplate().get_npcId();
				npcImpl = targetNpc.getNpcTemplate().getImpl();
			} else if (findObject instanceof L1PcInstance) {
				isPrivateShop = true;
			}
		}

		if (resultType == 0 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Merchant")) { // 购入
			if (npcId == 70035 || npcId == 70041 || npcId == 70042) {
				for (int i = 0; i < size; i++) {
					// 取得编序
					int order = readD();
					// 取得采购数量
					int count = readD();

					// 比赛不是等待中就无法买票
					if (!_gam.get_isWaiting()) {
						return;
					}
					// 比赛开始就无法买票
					if (_gam.get_isStart()) {
						return;
					}
					// 买票时间过了就无法买票
					if (!pc.hasSkillEffect(123456)) {
						return;
					}
					pc.getGamSplist().addGamItem(order, count);
				}
				// 执行完成购买的处理
				pc.getGamSplist().checkGamShop();
				pc.killSkillEffectTimer(123456);
				return;
			} 
			L1Center shop1 = CenterTable.getInstance().get(npcId);
			if (shop1!=null){
				L1CenterBuyOrderList orderList = shop1.newBuyOrderList();
				for (int i = 0; i < size; i++) {
					orderList.add(readD(), readD());
					
				}
				shop1.sellItems(pc, orderList);

				return;
			}
			L1Shop shop = ShopTable.getInstance().get(npcId);
			L1ShopBuyOrderList orderList = shop.newBuyOrderList();
			for (int i = 0; i < size; i++) {
				orderList.add(readD(), readD());
			}
			shop.sellItems(pc, orderList);
		} else if (resultType == 1 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Merchant")) { // 卖却
			/*
			 * 删除L1Shop shop = ShopTable.getInstance().get(npcId);
			 * L1ShopSellOrderList orderList = shop.newSellOrderList(pc); for
			 * (int i = 0; i < size; i++) { orderList.add(readD(), readD()); }
			 * shop.buyItems(orderList);删除
			 */
			// 全道具贩卖
			if (npcId == 70035 || npcId == 70041 || npcId == 70042) {
				for (int i = 0; i < size; i++) {
					// 取得编序
					int order = readD();
					// 取得采购数量
					int count = readD();
					pc.getGamSplist().addSellGamItem(order, count);
				}
				// 执行完成购买的处理
				pc.getGamSplist().checkGamSell();
				return;
			} 
			L1Center shop1 = CenterTable.getInstance().get(npcId);
			if (shop1!=null){
				L1CenterSellOrderList orderList = shop1.newSellOrderList(pc);
				for (int i = 0; i < size; i++) {
					orderList.add(readD(), readD());
				}
				shop1.buyItems(orderList);
				return;
			}
			if (Config.ALL_ITEM_SELL) {
				int objectId;
				long count;
				L1ItemInstance item;
				int totalPrice = 0;
				int tax_rate = L1CastleLocation.getCastleTaxRateByNpcId(npcId);
				for (int i = 0; i < size; i++) {
					objectId = readD();
					count = readD();

					item = pc.getInventory().getItem(objectId);
					if (item == null)
						continue;
					if (item.isEquipped())
						continue;
					if (item.isSeal()) {
						continue;
					}
					count = pc.getInventory().removeItem(item, count); // 削除

					int getPrice = L1WilliamItemPrice.getItemId(item.getItem()
							.getItemId());
					long price = 0;
					if (getPrice > 0) {
						price = getPrice;
					} else {
						price = 0;
					}
					if (tax_rate != 0) {
						double tax = (100 + tax_rate) / 100.0;
						price = (int) (price * tax);
					}
					price = price * count / 2;
					WriteLogTxt.Recording("全贩卖商人记录", "玩家" + pc.getName()
							+ "通过全贩卖商人卖出物品<" + item.getNumberedLogViewName()+"("+count+")" + ">获得金币"
							+ price + "。");
					totalPrice += price;
				}
				totalPrice = IntRange.ensure(totalPrice, 0, 2000000000);
				if (0 < totalPrice) {
					pc.getInventory().storeItem(L1ItemId.ADENA, totalPrice);
				}
			} else {
				L1Shop shop = ShopTable.getInstance().get(npcId);
				L1ShopSellOrderList orderList = shop.newSellOrderList(pc);
				for (int i = 0; i < size; i++) {
					orderList.add(readD(), readD());
				}
				shop.buyItems(orderList);
			}
			// 全道具贩卖 end
		} else if (resultType == 2 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) { // 自分仓库格纳
			int objectId, count;
			for (int i = 0; i < size; i++) {
				tradable = true;
				objectId = readD();
				count = readD();
				L1Object object = pc.getInventory().getItem(objectId);
				L1ItemInstance item = (L1ItemInstance) object;
				// 防洗道具
				//System.out.println("步骤1");
				if (count > 0 && count < 2000000000 && item.getCount() > 0
						&& item.getCount() >= count) {
					// 防洗道具
					if (!item.getItem().isTradable()) {
						tradable = false;
						pc.sendPackets(new S_ServerMessage(210, item.getItem()
								.getName())); // \f1%0舍他人让。
					}
					if (item.isSeal()) {
						pc.sendPackets(new S_SystemMessage(item.getLogViewName() +"处于封印状态！"));
						return;
					}
					if (item.get_time() != null){
						tradable = false;
						pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
					}
					if (item.getItemCharaterTrade() != null){
						tradable = false;
						pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
					}
					Object[] petlist = pc.getPetList().values().toArray();
					for (Object petObject : petlist) {
						if (petObject instanceof L1PetInstance) {
							L1PetInstance pet = (L1PetInstance) petObject;
							if (item.getId() == pet.getItemObjId()) {
								tradable = false;
								// \f1%0舍他人让。
								pc.sendPackets(new S_ServerMessage(210, item
										.getItem().getName()));
								break;
							}
						}
					}
					//System.out.println("步骤2");
					// 魔法娃娃使用判断
					for (Object babyObject : petlist) {
						if (babyObject instanceof L1BabyInstance) {
							L1BabyInstance baby = (L1BabyInstance) babyObject;
							if (item.getId() == baby.getItemObjId()) {
								tradable = false;
								// \f1%0舍他人让。
								pc.sendPackets(new S_ServerMessage(1181));
								break;
							}
						}
					}
					//System.out.println("步骤3");
					// 魔法娃娃使用判断 end
					// 存入打开中的灯类
					if (item.getItemId() == 40001 || item.getItemId() == 40002
							|| item.getItemId() == 40004
							|| item.getItemId() == 40005) {
						pc.setPcLight(0);
						if (item.getEnchantLevel() != 0) {
							item.setEnchantLevel(0);
						}
						pc.getInventory().updateItem(item,
								L1PcInventory.COL_ENCHANTLVL);
						pc.sendPackets(new S_ItemName(item));

						if (pc.hasSkillEffect(2)) {// 日光术
							pc.setPcLight(14);
						}
						//System.out.println("步骤4");
						for (Object Light : pc.getInventory().getItems()) {
							L1ItemInstance OwnLight = (L1ItemInstance) Light;
							if ((OwnLight.getItem().getItemId() == 40001
									|| OwnLight.getItem().getItemId() == 40002
									|| OwnLight.getItem().getItemId() == 40004 || OwnLight
									.getItem().getItemId() == 40005)
									&& OwnLight.getEnchantLevel() != 0) {
								if (pc.getPcLight() < OwnLight.getItem()
										.getLightRange()) {
									pc.setPcLight(OwnLight.getItem()
											.getLightRange());
								}
							}
						}

						pc.sendPackets(new S_Light(pc.getId(), pc.getPcLight()));
						if (!pc.isInvisble() && item.getItemId() != 40004) {// 非隐身中跟魔法灯笼除外
							pc.broadcastPacket(new S_Light(pc.getId(), pc
									.getPcLight()));
						}
					}
					//System.out.println("步骤5");
					// 存入打开中的灯类 end
					L1Pc Pc = L1World.getInstance().getPc(pc.getAccountName());
					if (tradable) {
						//System.out.println("步骤6");					
						pc.getInventory().tradeItem(objectId, count,
								Pc.getDwarfInventory());
						WriteLogTxt.Recording("个人仓库记录", "玩家" + pc.getName()
								+ "存入物品：" +item.getNumberedLogViewName()+"("+count+")"+"，物品itemid为"+ item.getItemId() 
								+ "，物品OBJID为"+item.getId()
								+"。");
					}
					// 防洗道具
				}
			}
		} else if (resultType == 3 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) { // 自分仓库取出
			int objectId, count;
			L1ItemInstance item;
			for (int i = 0; i < size; i++) {
				objectId = readD();
				count = readD();
				L1Pc Pc = L1World.getInstance().getPc(pc.getAccountName());
				item = Pc.getDwarfInventory().getItem(objectId);
				// 防洗道具
				if (item == null) {
					return;
				}
				if (count > 0 && count < 2000000000 && item.getCount() > 0
						&& item.getCount() >= count) {
					// 防洗道具
					if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) // 容量重量确认及送信
					{
						if (pc.getInventory().consumeItem(L1ItemId.ADENA, 30)) {
							Pc.getDwarfInventory().tradeItem(item, count,
									pc.getInventory());
							WriteLogTxt.Recording("个人仓库记录", "玩家" + pc.getName()
									+ "取出物品：" +item.getNumberedLogViewName()+"("+count+")"+"，物品itemid为"+ item.getItemId() 
									+ "，物品OBJID为"+item.getId()
									+"。");
						} else {
							pc.sendPackets(new S_ServerMessage(189)); // \f1不足。
							break;
						}
					} else {
						pc.sendPackets(new S_ServerMessage(270)); // \f1持重取引。
						break;
					}
					// 防洗道具
				}
			}
		} else if (resultType == 4 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) { // 仓库格纳
//			if (pc.isCheckTwopassword()){
//				pc.sendPackets(new S_SystemMessage("\\F3**请在聊天框输入二级密码才可正常游戏**"));
//				return;
//			}
			int objectId, count;
			if (pc.getClanid() != 0) { // 所属
				for (int i = 0; i < size; i++) {
					tradable = true;
					objectId = readD();
					count = readD();
					L1Clan clan = L1World.getInstance().getClan(
							pc.getClanname());
					L1ItemInstance item = pc.getInventory().getItem(objectId);
					if (item == null) {
						return;
					}
					// 防洗道具
					if (count > 0 && count < 2000000000 && item.getCount() > 0
							&& item.getCount() >= count) {
						// 防洗道具
						if (clan != null) {
							if (!item.getItem().isTradable()) {
								tradable = false;
								pc.sendPackets(new S_ServerMessage(210, item
										.getItem().getName())); // \f1%0舍他人让。
							}
							if (item.isSeal()) {
								pc.sendPackets(new S_SystemMessage(item.getLogViewName() +"处于封印状态！"));
								return;
							}
							if (item.get_time() != null){
								tradable = false;
								pc.sendPackets(new S_ServerMessage(210, item.getItem()
										.getName()));
							}
							if (item.getItemCharaterTrade() != null){
								tradable = false;
								pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
							}
							Object[] petlist = pc.getPetList().values()
									.toArray();
							for (Object petObject : petlist) {
								if (petObject instanceof L1PetInstance) {
									L1PetInstance pet = (L1PetInstance) petObject;
									if (item.getId() == pet.getItemObjId()) {
										tradable = false;
										// \f1%0舍他人让。
										pc.sendPackets(new S_ServerMessage(210,
												item.getItem().getName()));
										break;
									}
								}
							}
							// 魔法娃娃使用判断
							for (Object babyObject : petlist) {
								if (babyObject instanceof L1BabyInstance) {
									L1BabyInstance baby = (L1BabyInstance) babyObject;
									if (item.getId() == baby.getItemObjId()) {
										// \f1%0舍他人让。
										pc.sendPackets(new S_ServerMessage(1181));
										break;
									}
								}
							}
							// 魔法娃娃使用判断 end
							// 存入打开中的灯类
							if (item.getItemId() == 40001
									|| item.getItemId() == 40002
									|| item.getItemId() == 40004
									|| item.getItemId() == 40005) {
								pc.setPcLight(0);
								if (item.getEnchantLevel() != 0) {
									item.setEnchantLevel(0);
								}
								pc.getInventory().updateItem(item,
										L1PcInventory.COL_ENCHANTLVL);
								pc.sendPackets(new S_ItemName(item));

								if (pc.hasSkillEffect(2)) {// 日光术
									pc.setPcLight(14);
								}

								for (Object Light : pc.getInventory()
										.getItems()) {
									L1ItemInstance OwnLight = (L1ItemInstance) Light;
									if ((OwnLight.getItem().getItemId() == 40001
											|| OwnLight.getItem().getItemId() == 40002
											|| OwnLight.getItem().getItemId() == 40004 || OwnLight
											.getItem().getItemId() == 40005)
											&& OwnLight.getEnchantLevel() != 0) {
										if (pc.getPcLight() < OwnLight
												.getItem().getLightRange()) {
											pc.setPcLight(OwnLight.getItem()
													.getLightRange());
										}
									}
								}

								pc.sendPackets(new S_Light(pc.getId(), pc
										.getPcLight()));
								if (!pc.isInvisble()
										&& item.getItemId() != 40004) {// 非隐身中跟魔法灯笼除外
									pc.broadcastPacket(new S_Light(pc.getId(),
											pc.getPcLight()));
								}
							}
							// 存入打开中的灯类 end
							if (tradable) {
								for(final L1PcInstance clanpc : pc.getClan().getOnlineClanMember()){
									clanpc.sendPackets(new S_SystemMessage("血盟玩家:" + pc.getName() + "存入物品:" + item.getName() + "(" + count + ")到血盟仓库."));
								}
								pc.getInventory().tradeItem(objectId, count,
										clan.getDwarfForClanInventory());
								WriteLogTxt.Recording("血盟仓库记录", "玩家#" + pc.getName()
										+ "#存入血盟仓库#"+clan.getClanName()+"#物品：" +item.getNumberedLogViewName()+"("+count+")"+"，物品itemid为"+ item.getItemId() 
										+ "，物品OBJID为"+item.getId()
										+"。");
							}
						}
						// 防洗道具
					} else {
						continue;
					}
					// 防洗道具
				}
			} else {
				pc.sendPackets(new S_ServerMessage(208)); // \f1血盟仓库使用血盟加入。
			}
		} else if (resultType == 5 && size != 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf") && level >= 5) { // 仓库取出
//			if (pc.isCheckTwopassword()){
//				pc.sendPackets(new S_SystemMessage("\\F3**请在聊天框输入二级密码才可正常游戏**"));
//				return;
//			}
			int objectId, count;
			L1ItemInstance item;

			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				for (int i = 0; i < size; i++) {
					objectId = readD();
					count = readD();
					// 防洗道具
					count = Math.abs(count);
					count = Math.min(count, 2000000000);
					count = Math.max(count, 0);
					if (count > 0 && count < 2000000000) {
						// 防洗道具
						item = clan.getDwarfForClanInventory()
								.getItem(objectId);
						// 防洗道具
						if (count > 0 && count < 2000000000
								&& item.getCount() > 0
								&& item.getCount() >= count) {
							// 防洗道具
							if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 容量重量确认及送信
								if (pc.getInventory().consumeItem(
										L1ItemId.ADENA, 30)) {
									clan.getDwarfForClanInventory().tradeItem(
											item, count, pc.getInventory());
									for(final L1PcInstance clanpc : pc.getClan().getOnlineClanMember()){
										clanpc.sendPackets(new S_SystemMessage("血盟玩家:" + pc.getName() + "从血盟仓库取出:" + item.getName() + "(" + count + ")."));
									}
									WriteLogTxt.Recording("血盟仓库记录", "玩家#" + pc.getName()
											+ "#取出血盟#"+clan.getClanName()+"#仓库物品：" +item.getNumberedLogViewName()+"("+count+")"+"，物品itemid为"+ item.getItemId() 
											+ "，物品OBJID为"+item.getId()
											+"。");
								} else {
									pc.sendPackets(new S_ServerMessage(189)); // \f1不足。
									break;
								}
							} else {
								pc.sendPackets(new S_ServerMessage(270)); // \f1持重取引。
								break;
							}
							// 防洗道具
						} else {
							continue;
						}
					} else {
						continue;
					}
					// 防洗道具
				}
				clan.setWarehouseUsingChar(0); // 仓库解除
			}
		} else if (resultType == 5 && size == 0
				&& npcImpl.equalsIgnoreCase("L1Dwarf")) { // 仓库取出中Cancel、、ESC
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				clan.setWarehouseUsingChar(0); // 仓库解除
			}
		} else if (resultType == 0 && size != 0 && isPrivateShop) { // 个人商店购入
//			if (pc.isCheckTwopassword()){
//				pc.sendPackets(new S_SystemMessage("\\F3**请在聊天框输入二级密码才可正常游戏**"));
//				return;
//			}
			int order;
			int count;
			int price;
			ArrayList<L1PrivateShopSellList> sellList;
			L1PrivateShopSellList pssl;
			int itemObjectId;
			int sellPrice;
			int sellTotalCount;
			int sellCount;
			L1ItemInstance item;
			boolean[] isRemoveFromList = new boolean[8];

			L1PcInstance targetPc = null;
			if (findObject instanceof L1PcInstance) {
				targetPc = (L1PcInstance) findObject;
			}
			if (targetPc.isTradingInPrivateShop()) {
				return;
			}		
			synchronized (targetPc.getSellList()) {
				sellList = targetPc.getSellList();
				// 卖切发生、阅览中数数异
				if (pc.getPartnersPrivateShopItemCount() != sellList.size()) {
					return;
				}
				targetPc.setTradingInPrivateShop(true);

				int sellItemId = L1ItemId.ADENA;
				if (targetPc.getMapId() == 360 || targetPc.getMapId() == 370){
					sellItemId = 44070;
				}
				for (int i = 0; i < size; i++) { // 购入予定商品
					order = readD();
					count = readD();
					pssl = (L1PrivateShopSellList) sellList.get(order);
					itemObjectId = pssl.getItemObjectId();
					sellPrice = pssl.getSellPrice();
					sellTotalCount = pssl.getSellTotalCount(); // 卖予定个数
					sellCount = pssl.getSellCount(); // 卖累计
					item = targetPc.getInventory().getItem(itemObjectId);
					if (item == null) {
						continue;
					}
					if (count > sellTotalCount - sellCount) {
						count = sellTotalCount - sellCount;
					}
					if (count == 0) {
						continue;
					}
					if (item.isSeal()) {
						pc.sendPackets(new S_SystemMessage(item.getLogViewName() +"处于封印状态！"));
						continue;
					}

					// 防洗道具
					if (count > 0 && count < 2000000000 && item.getCount() > 0
							&& item.getCount() >= count) {
						// 防洗道具
						if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 容量重量确认及送信
							for (int j = 0; j < count; j++) { // 
								if (sellPrice * j > 2000000000) {
									pc.sendPackets(new S_ServerMessage(904, // 总贩卖价格%d超过。
											"2000000000"));
									targetPc.setTradingInPrivateShop(false);
									return;
								}
							}
							price = count * sellPrice;
							if (pc.getInventory().checkItem(sellItemId,price)) {
								L1ItemInstance adena = pc.getInventory().findItemId(sellItemId);
								if (targetPc != null && adena != null) {
									if (targetPc.getInventory().tradeItem(item,
											count, pc.getInventory()) == null) {
										targetPc.setTradingInPrivateShop(false);
										return;
									}
									if (item.isTradable()){
										item.setTradable(false);//交易成功后 更新不可交易
									}
									pc.getInventory().tradeItem(adena, price,
											targetPc.getInventory());
									String message = item.getItem().getName()
											+ " (" + String.valueOf(count)
											+ ")";
									targetPc.sendPackets(new S_ServerMessage(
											877, // %1%o
											// %0贩卖。
											pc.getName(), message));
									pssl.setSellCount(count + sellCount);
									sellList.set(order, pssl);
									if (pssl.getSellCount() == pssl
											.getSellTotalCount()) { // 卖予定个数卖
										isRemoveFromList[order] = true;
									}
									if (targetPc.getNetConnection() == null){
										targetPc.saveInventory();
									}
									WriteLogTxt.Recording("个人商店记录", "玩家#" + pc.getName()
											+ "花费"+price+"向玩家#"+targetPc.getName()+"#购入物品：" +item.getNumberedLogViewName()+"("+count+")"+"，物品itemid为"+ item.getItemId() 
											+ "，物品OBJID为"+item.getId()
											+"。");
								}
							} else {
								if (sellItemId == 44070){
									pc.sendPackets(new S_SystemMessage("\\F2元宝不足"));
								}else{
									pc.sendPackets(new S_ServerMessage(189));
								}
								 // \f1不足。
								break;
							}
						} else {
							pc.sendPackets(new S_ServerMessage(270)); // \f1持重取引。
							break;
						}
						// 防洗道具
					} else {
						//clientthread.sendPacket(new S_Disconnect());
						continue;
					}
					// 防洗道具
				}
				// 卖切末尾削除
				for (int i = 7; i >= 0; i--) {
					if (isRemoveFromList[i]) {
						sellList.remove(i);
					}
				}
				targetPc.setTradingInPrivateShop(false);
			}
		} else if (resultType == 1 && size != 0 && isPrivateShop) { // 个人商店卖却
//			if (pc.isCheckTwopassword()){
//				pc.sendPackets(new S_SystemMessage("\\F3**请在聊天框输入二级密码才可正常游戏**"));
//				return;
//			}
			int count;
			int order;
			ArrayList<L1PrivateShopBuyList> buyList;
			L1PrivateShopBuyList psbl;
			int itemObjectId;
			L1ItemInstance item;
			int buyPrice;
			int buyTotalCount;
			int buyCount;
			L1ItemInstance targetItem;
			boolean[] isRemoveFromList = new boolean[8];

			L1PcInstance targetPc = null;
			if (findObject instanceof L1PcInstance) {
				targetPc = (L1PcInstance) findObject;
			}
			if (targetPc.isTradingInPrivateShop()) {
				return;
			}	
			int sellItemId = L1ItemId.ADENA;
			if (targetPc.getMapId() == 360 || targetPc.getMapId() == 370){
				sellItemId = 44070;
			}
			synchronized (targetPc.getBuyList()) {
				targetPc.setTradingInPrivateShop(true);		
				buyList = targetPc.getBuyList();
				for (int i = 0; i < size; i++) {
					itemObjectId = readD();
					count = readCH();
					order = readC();
					item = pc.getInventory().getItem(itemObjectId);
					if (item == null) {
						continue;
					}
					// 防洗道具
					if (count > 0 && count < 2000000000 && item.getCount() > 0
							&& item.getCount() >= count) {
						psbl = (L1PrivateShopBuyList) buyList.get(order);
						buyPrice = psbl.getBuyPrice();
						buyTotalCount = psbl.getBuyTotalCount(); // 买予定个数
						buyCount = psbl.getBuyCount(); // 买累计
						if (psbl.getItemId() != item.getItemId() ) {
							pc.sendPackets(new S_SystemMessage("此物品为客户端显示错误，无法收购成功，请勿再尝试，谢谢！"));
							continue;
						}
						if (count > buyTotalCount - buyCount) {
							count = buyTotalCount - buyCount;
						}						
						if (item.isEquipped()) {
							pc.sendPackets(new S_ServerMessage(905)); // 装备贩卖。
							continue;
						}
						if (item.isSeal()) {
							pc.sendPackets(new S_SystemMessage(item.getLogViewName() +"处于封印状态！"));
							continue;
						}

						if (targetPc.getInventory().checkAddItem(item, count) == L1Inventory.OK) { // 容量重量确认及送信
							for (int j = 0; j < count; j++) { // 
								if (buyPrice * j > 2000000000) {
									targetPc.sendPackets(new S_ServerMessage(
											904, // 总贩卖价格%d超过。
											"2000000000"));
									targetPc.setTradingInPrivateShop(false);
									return;
								}
							}
							if (targetPc.getInventory().checkItem(sellItemId, count * buyPrice)) {
								L1ItemInstance adena = targetPc.getInventory().findItemId(sellItemId);
								if (adena != null) {
									targetPc.getInventory()
											.tradeItem(adena, count * buyPrice,
													pc.getInventory());
									pc.getInventory().tradeItem(item, count,
											targetPc.getInventory());
									psbl.setBuyCount(count + buyCount);
									buyList.set(order, psbl);
									if (psbl.getBuyCount() == psbl
											.getBuyTotalCount()) { // 买予定个数买
										isRemoveFromList[order] = true;
									}
									if (targetPc.getNetConnection() == null){
										targetPc.saveInventory();
									}
									WriteLogTxt.Recording(
											"个人商店记录",
											"玩家#" + pc.getName() + "以" + count
													* buyPrice + "的价格卖给玩家#"+targetPc.getName()+"#物品："
													+ item.getNumberedLogViewName()+"("+count+")" + "，物品itemid为"
													+ item.getItemId()
													+ "，物品OBJID为"
													+ item.getId() + "。");
								}
							} else {
								if (sellItemId == 44070){
									targetPc.sendPackets(new S_SystemMessage("\\F2元宝不足"));
								}else{
									targetPc.sendPackets(new S_ServerMessage(189));
								}
								break;
							}
						} else {
							pc.sendPackets(new S_ServerMessage(271)); // \f1相手物持取引。
							break;
						}
						// 防洗道具
					} else {
						continue;
					}
					// 防洗道具
				}
				// 买切末尾削除
				for (int i = 7; i >= 0; i--) {
					if (isRemoveFromList[i]) {
						buyList.remove(i);
					}
				}
				targetPc.setTradingInPrivateShop(false);
			}
		}else if (resultType == 8 && size != 0) {
			if (size != 3){
				pc.sendPackets(new S_SystemMessage("\\F2必须选择3个进行合成."));
				return;
			}
			if (!pc.getInventory().checkItem(40308, 1000000)){
				pc.sendPackets(new S_SystemMessage("你的金币不足100万."));
				return;
			}
			int objectId, count;
			final Object[] petlist = pc.getPetList().values().toArray();
			final List<L1ItemInstance> list = new ArrayList<L1ItemInstance>();
			boolean isError = false;
			for (int i = 0; i < size; i++) {
				objectId = readD();
				count = readD();
				if (count < 0 || count > 1){
					continue;
				}
				final L1ItemInstance item = pc.getInventory().getItem(objectId);
				if (item == null){
					continue;
				}
				boolean isOk = true;
				for (final Object babyObject : petlist) {
					if (babyObject instanceof L1BabyInstance) {
						final L1BabyInstance baby = (L1BabyInstance) babyObject;
						if (item.getId() == baby.getItemObjId()) {
							isError = true;
							isOk = false;
							pc.sendPackets(new S_SystemMessage("\\F2请先收回魔法娃娃."));
							continue;
						}
					}
				}
				if (isOk){
					list.add(item);
				}
			}
			if (!isError && list.size() == 3){
				final String[] data = new String[3];
				final int rnd = 1000;//5个的机率
				for(final L1ItemInstance dollItem : list){
					pc.getInventory().removeItem(dollItem);
				}
				pc.getInventory().consumeItem(40308, 1000000);
				
				list.clear();
				data[0] = "合成中";
				data[1] = "";
				data[2] = "";
				for(int i = 0;i < 10;i++){
					data[1] = Config.dollposs[i];
					Thread.sleep(100);
					pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcom1", data));
				}
				Thread.sleep(300);
				final Random rand = new Random();
				final int failcout = pc.getDollFailCount();
				//失败10次100%成功 大于5次 机率成功  小5不成功
				if (failcout >= 10 || (failcout >=5 && rand.nextInt(10000) < rnd)){
					final L1Item new_dollItem = ItemTable.getInstance().getTemplate(60203);
					data[0] = "恭喜合成成功";
					data[1] = "";
					if (new_dollItem != null){
						pc.getInventory().storeItem(60203, 1);
						data[2] = String.format("获得:%s", new_dollItem.getName());
					}else{
						data[2] = "";
					}
					pc.setDollFailCount(0);//合成成功失败次数清0
					pc.saveDollFailCount();
				}else{
					pc.addDollFailCount(1);
					pc.saveDollFailCount();
					data[0] = "合成失败";
					data[1] = "";
					data[2] = "";
				}
				pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "dollcom2", data));
			}
		}else if (resultType == 9 && size != 0 && targetNpc != null) {
			if (size != 1){
				pc.sendPackets(new S_SystemMessage("\\F2只能选择一个进行洗练."));
				return;
			}
			final int objectId = readD();
			final int count = readD();
			if (count < 0 || count > 1){
				return;
			}
			if (targetNpc.ACTION != null){
				pc.setTempID(objectId);
				targetNpc.ACTION.action(pc, targetNpc, "sel_doll_ok", 0);
			}
			
		}
	}

	@Override
	public String getType() {
		return C_RESULT;
	}

}
