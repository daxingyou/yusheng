///*
// * This program is free software; you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation; either version 2, or (at your option)
// * any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
// * 02111-1307, USA.
// *
// * http://www.gnu.org/copyleft/gpl.html
// */
//
//package l1j.server.server.datatables;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Random;
//
//import l1j.gui.J_Main;
//import l1j.server.Config;
//import l1j.server.L1DatabaseFactory;
//import l1j.server.server.WriteLogTxt;
//import l1j.server.server.datatables.lock.ShouBaoReading;
//import l1j.server.server.model.L1Character;
//import l1j.server.server.model.L1Inventory;
//import l1j.server.server.model.Instance.L1ItemInstance;
//import l1j.server.server.model.Instance.L1MonsterInstance;
//import l1j.server.server.model.Instance.L1NpcInstance;
//import l1j.server.server.model.Instance.L1PcInstance;
//import l1j.server.server.model.Instance.L1PetInstance;
//import l1j.server.server.model.Instance.L1SummonInstance;
//import l1j.server.server.model.item.L1ItemId;
//import l1j.server.server.serverpackets.S_BlueMessage;
//import l1j.server.server.serverpackets.S_ServerMessage;
//import l1j.server.server.serverpackets.S_SystemMessage;
//import l1j.server.server.templates.L1Drop;
//import l1j.server.server.templates.L1Item;
//import l1j.server.server.templates.L1ShouBaoTemp;
//import l1j.server.server.utils.SQLUtil;
//import l1j.server.server.world.L1World;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//// Referenced classes of package l1j.server.server.templates:
//// L1Npc, L1Item, ItemTable
//
//public class DropTable {
//
//	private static final Log _log = LogFactory.getLog(DropTable.class);
//
//	private static DropTable _instance;
//
//	private final HashMap<Integer, ArrayList<L1Drop>> _droplists = new HashMap<Integer, ArrayList<L1Drop>>();
//
//	private final ArrayList<dropWorldTemp> _dropworldlist = new ArrayList<dropWorldTemp>();
//
//	public static DropTable getInstance() {
//		if (_instance == null) {
//			_instance = new DropTable();
//		}
//		return _instance;
//	}
//
//	private DropTable() {
//		// _droplists.clear();
//		allDropList();
//		this.loadDropWorld();
//	}
//
//	public void reload() {
//		_droplists.clear();
//		_dropworldlist.clear();
//		allDropList();
//		this.loadDropWorld();
//	}
//
//	private HashMap<Integer, ArrayList<L1Drop>> allDropList() {
//		// HashMap<Integer, ArrayList<L1Drop>> droplistMap = new
//		// HashMap<Integer, ArrayList<L1Drop>>();
//
//		Connection con = null;
//		PreparedStatement pstm = null;
//		ResultSet rs = null;
//		try {
//			con = L1DatabaseFactory.getInstance().getConnection();
//			pstm = con.prepareStatement("select * from droplist");
//			rs = pstm.executeQuery();
//			while (rs.next()) {
//				L1Drop l1drop = new L1Drop();
//				l1drop.setMobid(rs.getInt("mobId"));
//				l1drop.setItemid(rs.getInt("itemId"));
//				l1drop.setMin(rs.getInt("min"));
//				l1drop.setMax(rs.getInt("max"));
//				l1drop.setChance(rs.getInt("chance"));
//
//				ArrayList<L1Drop> dropList = _droplists.get(l1drop.getMobid());
//				if (dropList == null) {
//					dropList = new ArrayList<L1Drop>();
//					_droplists.put(new Integer(l1drop.getMobid()), dropList);
//				}
//				dropList.add(l1drop);
//			}
//		} catch (SQLException e) {
//			_log.error(e.getLocalizedMessage(), e);
//		} finally {
//			SQLUtil.close(rs);
//			SQLUtil.close(pstm);
//			SQLUtil.close(con);
//		}
//		return _droplists;
//	}
//
//	private class dropWorldTemp {
//		private int _itemId;
//		private int _itemCount;
//		private int _enchanlevel;
//	}
//
//	private void loadDropWorld() {
//		Connection con = null;
//		PreparedStatement pstm = null;
//		ResultSet rs = null;
//
//		try {
//			con = L1DatabaseFactory.getInstance().getConnection();
//			pstm = con.prepareStatement("select * from droplist_world");
//			rs = pstm.executeQuery();
//			while (rs.next()) {
//				final dropWorldTemp temp = new dropWorldTemp();
//				temp._itemId = rs.getInt("itemId");
//				temp._itemCount = rs.getInt("count");
//				temp._enchanlevel = rs.getInt("enchantlevel");
//
//				_dropworldlist.add(temp);
//			}
//			_log.info("INFO 加载怪物掉落道具在地上:" + _dropworldlist.size());
//		} catch (SQLException var9) {
//			_log.error(var9.getLocalizedMessage(), var9);
//		} finally {
//			SQLUtil.close(rs);
//			SQLUtil.close(pstm);
//			SQLUtil.close(con);
//		}
//
//	}
//
//	// 设定
//	public void setDrop(L1NpcInstance npc, L1Inventory inventory) {
//		// 取得
//		int mobId = npc.getNpcTemplate().get_npcId();
//		if (mobId == 888888) {
//			for (final dropWorldTemp temp : _dropworldlist) {
//				final L1ItemInstance item = ItemTable.getInstance().createItem(
//						temp._itemId);
//				if (item == null) {
//					return;
//				}
//				if (item.getItem().isStackable()) {
//					item.setCount(temp._itemCount);
//					((L1MonsterInstance) npc).addDropListItem(item);
//				} else {
//					for (int i = 0; i < temp._itemCount; i++) {
//						final L1ItemInstance item1 = ItemTable.getInstance()
//								.createItem(temp._itemId);
//						item1.setCount(1);
//						if (temp._enchanlevel > 0) {
//							item1.setEnchantLevel(temp._enchanlevel);
//							item1.setIdentified(true);
//							((L1MonsterInstance) npc).addDropListItem(item1);
//						}
//					}
//				}
//
//			}
//			return;
//		}
//		ArrayList<L1Drop> dropList = _droplists.get(mobId);
//		if (dropList == null) {
//			return;
//		}
//
//		// 取得
//		double droprate = Config.RATE_DROP_ITEMS;
//		if (droprate <= 0) {
//			droprate = 0;
//		}
//		double adenarate = Config.RATE_DROP_ADENA;
//		if (adenarate <= 0) {
//			adenarate = 0;
//		}
//		if (droprate <= 0 && adenarate <= 0) {
//			return;
//		}
//
//		int itemId;
//		int itemCount;
//		int addCount;
//		int randomChance;
//		L1ItemInstance item;
//		Random random = new Random();
//		for (L1Drop drop : dropList) {
//			// 取得
//			itemId = drop.getItemid();
//			if (adenarate == 0 && itemId == L1ItemId.ADENA) {
//				continue; // ０场合
//			}
//
//			// 判定
//			randomChance = random.nextInt(0xf4240) + 1;
//			double rateOfMapId = MapsTable.getInstance().getDropRate(
//					npc.getMapId());
//			double rateOfItem = DropItemTable.getInstance().getDropRate(itemId);
//			if (droprate == 0
//					|| drop.getChance() * droprate * rateOfMapId * rateOfItem < randomChance) {
//				continue;
//			}
//
//			// 个数设定
//			double amount = DropItemTable.getInstance().getDropAmount(itemId);
//			int min = (int) (drop.getMin() * amount);
//			int max = (int) (drop.getMax() * amount);
//
//			itemCount = min;
//			addCount = max - min + 1;
//			if (addCount > 1) {
//				itemCount += random.nextInt(addCount);
//			}
//			if (itemId == L1ItemId.ADENA) { // 场合挂
//				itemCount *= adenarate;
//			}
//
//			if (itemCount < 0) {
//				itemCount = 0;
//			}
//
//			if (itemCount > 2000000000) {
//				itemCount = 2000000000;
//			}
//
//			// 生成
//			item = ItemTable.getInstance().createItem(itemId);
//			if (item == null) {
//				return;
//			}
//			item.setCount(itemCount);
//			if (item.getItem().isBroad()) {
//				item.setBroad(true);
//			}
//			// 格纳
//			inventory.storeItem(item);
//		}
//	}
//
//	// 分配
//	public void dropShare(L1NpcInstance npc,
//			ArrayList<L1Character> acquisitorList, ArrayList<Integer> hateList) {
//		L1Inventory inventory = npc.getInventory();
//		if (inventory.getSize() == 0) {
//			return;
//		}
//		if (acquisitorList.size() != hateList.size()) {
//			return;
//		}
//		// 合计取得
//		int totalHate = 0;
//		L1Character acquisitor;
//		for (int i = hateList.size() - 1; i >= 0; i--) {
//			acquisitor = acquisitorList.get(i);
//			if ((Config.AUTO_LOOT == 2) // ２场合及省
//					&& (acquisitor instanceof L1SummonInstance || acquisitor instanceof L1PetInstance)) {
//				acquisitorList.remove(i);
//				hateList.remove(i);
//			} else if (acquisitor != null
//					&& !acquisitor.isDead()
//					&& acquisitor.getMapId() == npc.getMapId()
//					&& acquisitor.getLocation().getTileLineDistance(
//							npc.getLocation()) <= Config.LOOTING_RANGE) {
//				totalHate += hateList.get(i);
//			} else {// null死远排除
//				acquisitorList.remove(i);
//				hateList.remove(i);
//			}
//		}
//
//		// 分配
//		L1ItemInstance item;
//		L1Inventory targetInventory = null;
//		// L1Character character = null;
//		L1PcInstance player;
//		L1PcInstance[] partyMember;
//		Random random = new Random();
//		int randomInt;
//		int chanceHate;
//		for (int i = inventory.getSize(); i > 0; i--) {
//			item = inventory.getItems().get(0);
//			if (!item.isGmDrop()
//					&& NotDropTable.getInstance().InNotDropList(
//							item.getItem().getItemId())) {
//				continue;
//			}
//			if (((Config.AUTO_LOOT != 0) || item.getItem().getItemId() == L1ItemId.ADENA)
//					&& totalHate > 0) {
//				randomInt = random.nextInt(totalHate);
//				chanceHate = 0;
//				for (int j = hateList.size() - 1; j >= 0; j--) {
//					chanceHate += (Integer) hateList.get(j);
//					if (chanceHate > randomInt) {
//						acquisitor = (L1Character) acquisitorList.get(j);
//						if (acquisitor.getInventory().checkAddItem(item,
//								item.getCount()) == L1Inventory.OK) {
//							// character = acquisitor;
//						//	targetInventory = acquisitor.getInventory();
////							if (acquisitor instanceof L1PcInstance) {
////								player = (L1PcInstance) acquisitor;
//
////								final L1ItemInstance vipring = player
////										.getInventory().findEquipped(70030);
////								int shuliang = (int) (item.getCount() * 0.2);
////								if (vipring != null) {
////									if (item.getItemId() == 40308) {
////										player.getInventory().storeItem(40308,
////												shuliang);
////										// player.sendPackets(new
////										// S_SystemMessage("\\F3佩戴特殊物品。金币获得数量翻倍"));
////									}
////								}
//								// 掉落道具队伍随机分配
//								if (player.getParty() != null) {
//									final L1PcInstance[] partyPlayers = player
//											.getParty().getMembers();
//									final int rnd = random
//											.nextInt(partyPlayers.length);
//									if (partyPlayers[rnd].getMapId() == player
//											.getMapId()
//											&& partyPlayers[rnd]
//													.getLineDistance(player) < 9) {
//										if (partyPlayers[rnd].getInventory()
//												.checkAddItem(item,
//														item.getCount()) == L1Inventory.OK) {
//											targetInventory = partyPlayers[rnd]
//													.getInventory();
//											player = partyPlayers[rnd];
//										}
//									}
//								}
//								// 掉落道具队伍随机分配 end
//
//								L1ItemInstance l1iteminstance = player
//										.getInventory().findItemId(
//												L1ItemId.ADENA); // 所持
//								if (l1iteminstance != null
//										&& l1iteminstance.getCount() > 2000000000) {
//									targetInventory = L1World.getInstance()
//											.getInventory(acquisitor.getX(),
//													acquisitor.getY(),
//													acquisitor.getMapId()); // 持足元落
//									player.sendPackets(new S_ServerMessage(166,
//											"所持有的金币", "超过20亿")); // \f1%0%4%1%3%2
//								} else {
//									if (player.isInParty()) {// 场合
//										partyMember = player.getParty()
//												.getMembers();
//										for (int p = 0; p < partyMember.length; p++) {
//											partyMember[p]
//													.sendPackets(new S_ServerMessage(
//															813,
//															npc.getNpcTemplate()
//																	.get_name(),
//															item.getLogName(),
//															player.getName()));
//											//System.out.println("命中1");
//										}
//									} else {
//										// final L1ItemInstance vipring =
//										// player.getInventory().findEquipped(70030);
//										if (vipring != null) {
//											if (item.getItemId() == 40308) {
//												player.sendPackets(new S_SystemMessage("从" + npc.getNpcTemplate().get_name() + "获得[奖励]金币 (" + item.getCount() * 0.2 + ")")); // \f1%0%1
//												//System.out.println("命中1" + item.getLogName());
//											}
//											player.sendPackets(new S_ServerMessage(
//													143, npc.getNpcTemplate()
//															.get_name(), item
//															.getLogName())); // \f1%0%1
////											player.sendPackets(new S_ServerMessage(
////													143, npc.getNpcTemplate()
////															.get_name(), item
////															.getLogName())); // \f1%0%1
//										} else {
//											player.sendPackets(new S_ServerMessage(
//													143, npc.getNpcTemplate()
//															.get_name(), item
//															.getLogName())); // \f1%0%1
//											//System.out.println("命中2");
//										}
//									}
//								}
//								final L1ShouBaoTemp shoubaoTmp = ShouBaoReading
//										.get().getTemp(
//												item.getItem().getItemId());
//								if(item.issbxz()) {
//									return;
//								}
//								if (shoubaoTmp != null
//										&& shoubaoTmp.getObjId() == 0) {
//									if (ShouBaoReading.get().update(shoubaoTmp,
//											player.getId(), player.getName())) {
//										final L1Item gvitem = ItemTable
//												.getInstance()
//												.getTemplate(
//														shoubaoTmp
//																.getGiveItemId());
//										if (gvitem != null) {
//											player.getInventory()
//													.storeItem(
//															shoubaoTmp
//																	.getGiveItemId(),
//															shoubaoTmp
//																	.getGiveItemCount());
//											final StringBuilder msg = new StringBuilder();
//											msg.append(gvitem.getName());
//											if (shoubaoTmp.getGiveItemCount() > 1) {
//												msg.append("(");
//												msg.append(shoubaoTmp
//														.getGiveItemCount());
//												msg.append(")");
//											}
//											L1World.getInstance()
//													.broadcastPacketToAll(
//															new S_BlueMessage(
//																	4536,
//																	player.getName(),
//																	item.getItem()
//																			.getName(),
//																	msg.toString()));
//										}
//									}
//								}
//								if (item.isBroad()) {
//									final String mapName = MapsTable
//											.getInstance().getMapName(
//													player.getMapId(),
//													player.getX(),
//													player.getY());
//									L1World.getInstance().broadcastPacketToAll(
//											new S_ServerMessage(4537, player
//													.getName(), mapName, player
//													.getX()
//													+ ","
//													+ player.getY(), npc
//													.getName(), item
//													.getLogViewName()));
//									J_Main.getInstance().addConsol(
//											"玩家： " + player.getName() + " 从怪物 "
//													+ npc.getName() + " 获得物品"
//													+ item.getLogViewName());
//									WriteLogTxt.Recording("掉宝记录",
//											"玩家： " + player.getName() + " 从怪物 "
//													+ npc.getName() + " 获得物品"
//													+ item.getLogViewName()
//													+ " OBJID:" + item.getId());
//									item.setBroad(false);
//								}
//							} else if (acquisitor instanceof L1PetInstance) {
//								if (((L1PetInstance) acquisitor).getMaster() != null) {
//									if (item.isBroad()) {
//										L1World.getInstance()
//												.broadcastPacketToAll(
//														new S_SystemMessage(
//																"玩家： "
//																		+ ((L1PetInstance) acquisitor)
//																				.getMaster()
//																				.getName()
//																		+ " 的宠物"
//																		+ ((L1PetInstance) acquisitor)
//																				.getNpcTemplate()
//																				.get_name()
//																		+ "从怪物 "
//																		+ npc.getName()
//																		+ " 获得物品"
//																		+ item.getLogViewName()));
//										J_Main.getInstance()
//												.addConsol(
//														"玩家： "
//																+ ((L1PetInstance) acquisitor)
//																		.getMaster()
//																		.getName()
//																+ " 的宠物"
//																+ ((L1PetInstance) acquisitor)
//																		.getNpcTemplate()
//																		.get_name()
//																+ "从怪物 "
//																+ npc.getName()
//																+ " 获得物品"
//																+ item.getLogViewName());
//										WriteLogTxt.Recording("掉宝记录", "玩家： "
//												+ ((L1PetInstance) acquisitor)
//														.getMaster().getName()
//												+ " 的宠物"
//												+ ((L1PetInstance) acquisitor)
//														.getNpcTemplate()
//														.get_name() + "从怪物 "
//												+ npc.getName() + " 获得物品"
//												+ item.getLogViewName()
//												+ " OBJID:" + item.getId());
//										item.setBroad(false);
//									}
//								}
//							} else if (acquisitor instanceof L1SummonInstance) {
//								if (((L1SummonInstance) acquisitor).getMaster() != null) {
//									if (item.isBroad()) {
//										L1World.getInstance()
//												.broadcastPacketToAll(
//														new S_SystemMessage(
//																"玩家： "
//																		+ ((L1SummonInstance) acquisitor)
//																				.getMaster()
//																				.getName()
//																		+ " 的宠物"
//																		+ ((L1SummonInstance) acquisitor)
//																				.getNpcTemplate()
//																				.get_name()
//																		+ "从怪物 "
//																		+ npc.getName()
//																		+ " 获得物品"
//																		+ item.getLogViewName()));
//										J_Main.getInstance()
//												.addConsol(
//														"玩家： "
//																+ ((L1SummonInstance) acquisitor)
//																		.getMaster()
//																		.getName()
//																+ " 的宠物"
//																+ ((L1SummonInstance) acquisitor)
//																		.getNpcTemplate()
//																		.get_name()
//																+ "从怪物 "
//																+ npc.getName()
//																+ " 获得物品"
//																+ item.getLogViewName());
//										WriteLogTxt
//												.Recording(
//														"掉宝记录",
//														"玩家： "
//																+ ((L1SummonInstance) acquisitor)
//																		.getMaster()
//																		.getName()
//																+ " 的宠物"
//																+ ((L1SummonInstance) acquisitor)
//																		.getNpcTemplate()
//																		.get_name()
//																+ "从怪物 "
//																+ npc.getName()
//																+ " 获得物品"
//																+ item.getLogViewName()
//																+ " OBJID:"
//																+ item.getId());
//										item.setBroad(false);
//									}
//								}
//							}
//						} else {
//							targetInventory = L1World.getInstance()
//									.getInventory(acquisitor.getX(),
//											acquisitor.getY(),
//											acquisitor.getMapId()); // 持足元落
//							if (item.isBroad()) {
//								L1World.getInstance().broadcastPacketToAll(
//										new S_SystemMessage("哪个土豪那么任性？从怪物 "
//												+ npc.getName() + " 获得物品 "
//												+ item.getLogViewName()
//												+ " 掉地上还不去捡？"));
//								J_Main.getInstance().addConsol(
//										"哪个土豪那么任性？从怪物 " + npc.getName()
//												+ " 获得物品 "
//												+ item.getLogViewName()
//												+ " 掉地上还不去捡？");
//								WriteLogTxt.Recording(
//										"掉宝记录",
//										"哪个土豪那么任性？从怪物 " + npc.getName()
//												+ " 获得物品 "
//												+ item.getLogViewName()
//												+ " OBJID:" + item.getId()
//												+ " 掉地上还不去捡？");
//								item.setBroad(false);
//							}
//						}
//						break;
//					}
//				}
//			} else {// 
//				List<Integer> dirList = new ArrayList<Integer>();
//				for (int j = 0; j < 8; j++) {
//					dirList.add(j);
//				}
//				int x = 0;
//				int y = 0;
//				int dir = 0;
//				do {
//					if (dirList.size() == 0) {
//						x = 0;
//						y = 0;
//						break;
//					}
//					randomInt = random.nextInt(dirList.size());
//					dir = dirList.get(randomInt);
//					dirList.remove(randomInt);
//					switch (dir) {
//					case 0:
//						x = 0;
//						y = -1;
//						break;
//					case 1:
//						x = 1;
//						y = -1;
//						break;
//					case 2:
//						x = 1;
//						y = 0;
//						break;
//					case 3:
//						x = 1;
//						y = 1;
//						break;
//					case 4:
//						x = 0;
//						y = 1;
//						break;
//					case 5:
//						x = -1;
//						y = 1;
//						break;
//					case 6:
//						x = -1;
//						y = 0;
//						break;
//					case 7:
//						x = -1;
//						y = -1;
//						break;
//					}
//				} while (!npc.getMap().isPassable(npc.getX(), npc.getY(), dir));
//				targetInventory = L1World.getInstance().getInventory(
//						npc.getX() + x, npc.getY() + y, npc.getMapId());
//				if (item.isBroad()) {
//					L1World.getInstance().broadcastPacketToAll(
//							new S_SystemMessage("哪个土豪那么任性？从怪物 " + npc.getName()
//									+ " 获得物品 " + item.getLogViewName()
//									+ " 掉地上还不去捡？"));
//					J_Main.getInstance().addConsol(
//							"哪个土豪那么任性？从怪物 " + npc.getName() + " 获得物品 "
//									+ item.getLogViewName() + " 掉地上还不去捡？");
//					WriteLogTxt.Recording(
//							"掉宝记录",
//							"哪个土豪那么任性？从怪物 " + npc.getName() + " 获得物品 "
//									+ item.getLogViewName() + " OBJID:"
//									+ item.getId() + " 掉地上还不去捡？");
//					item.setBroad(false);
//				}
//			}
//			inventory.tradeItem(item, item.getCount(), targetInventory);
//			item.setBroad(false);
//			/*
//			 * if (character instanceof L1PcInstance) {
//			 * WriteLogTxt.Recording("怪物掉落记录", "玩家"+character.getName()
//			 * +"获得了物品：+" +item.getEnchantLevel()+" "
//			 * +item.getName()+"("+item.getCount()+"),itemid:#"+item.getItemId()
//			 * +"#objid:<"+item.getId()+">"); }
//			 */
//
//		}
//	}
//
//	public ArrayList<L1Drop> getDrops(int mobID) {
//		ArrayList<L1Drop> dropList = _droplists.get(mobID);
//		return dropList;
//	}
//
//}

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

package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import l1j.gui.J_Main;
import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.lock.ShouBaoReading;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_BlueMessage;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Drop;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1ShouBaoTemp;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package l1j.server.server.templates:
// L1Npc, L1Item, ItemTable

public class DropTable {

	private static final Log _log = LogFactory.getLog(DropTable.class);

	private static DropTable _instance;

	private final HashMap<Integer, ArrayList<L1Drop>> _droplists = new HashMap<Integer, ArrayList<L1Drop>>();
	
	private final ArrayList<dropWorldTemp> _dropworldlist = new ArrayList<dropWorldTemp>();
	
	public static DropTable getInstance() {
		if (_instance == null) {
			_instance = new DropTable();
		}
		return _instance;
	}

	private DropTable() {
		//_droplists.clear();
		allDropList();
		this.loadDropWorld();
	}
	
	public void reload(){
		_droplists.clear();
		_dropworldlist.clear();
		allDropList();
		this.loadDropWorld();
	}

	private HashMap<Integer, ArrayList<L1Drop>> allDropList() {
		//HashMap<Integer, ArrayList<L1Drop>> droplistMap = new HashMap<Integer, ArrayList<L1Drop>>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from droplist");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1Drop l1drop = new L1Drop();
				l1drop.setMobid(rs.getInt("mobId"));
				l1drop.setItemid(rs.getInt("itemId"));
				l1drop.setMin(rs.getInt("min"));
				l1drop.setMax(rs.getInt("max"));
				l1drop.setChance(rs.getInt("chance"));
				ArrayList<L1Drop> dropList = _droplists.get(l1drop.getMobid());
				if (dropList == null) {
					dropList = new ArrayList<L1Drop>();
					_droplists.put(new Integer(l1drop.getMobid()), dropList);
				}
				dropList.add(l1drop);
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return _droplists;
	}
	
    private class dropWorldTemp{
    	private int _itemId;
    	private int _itemCount;
    	private int _enchanlevel;
    }
    
    private void loadDropWorld(){
    	Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            con = L1DatabaseFactory.getInstance().getConnection();
            pstm = con.prepareStatement("select * from droplist_world");
            rs = pstm.executeQuery();
            while (rs.next()) {
            	final dropWorldTemp temp = new dropWorldTemp();
                temp._itemId = rs.getInt("itemId");
                temp._itemCount = rs.getInt("count");
                temp._enchanlevel = rs.getInt("enchantlevel");
                
                _dropworldlist.add(temp);
			}
            _log.info("INFO 加载怪物掉落道具在地上:" + _dropworldlist.size());
        } catch (SQLException var9) {
            _log.error(var9.getLocalizedMessage(), var9);
        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
    	
    }
	// 设定
	public void setDrop(L1NpcInstance npc, L1Inventory inventory) {
		// 取得
		int mobId = npc.getNpcTemplate().get_npcId();
		if (mobId == 888888){
        	for(final dropWorldTemp temp : _dropworldlist){
        		final L1ItemInstance item = ItemTable.getInstance().createItem(temp._itemId);
                if (item == null) {
                    return;
                }
                if (item.getItem().isStackable()){
                	item.setCount(temp._itemCount);
                	((L1MonsterInstance)npc).addDropListItem(item);
                }else{
                	for(int i = 0; i < temp._itemCount;i++){
                		final L1ItemInstance item1 = ItemTable.getInstance().createItem(temp._itemId);
                		item1.setCount(1);
                		if (temp._enchanlevel > 0){
                        	item1.setEnchantLevel(temp._enchanlevel);
                            item1.setIdentified(true);
                            ((L1MonsterInstance)npc).addDropListItem(item1);
                        }
                	}
                }
                
        	}
        	return;
        }
		ArrayList<L1Drop> dropList = _droplists.get(mobId);
		if (dropList == null) {
			return;
		}

		// 取得
		double droprate = Config.RATE_DROP_ITEMS;
		if (droprate <= 0) {
			droprate = 0;
		}
		double adenarate = Config.RATE_DROP_ADENA;
		if (adenarate <= 0) {
			adenarate = 0;
		}
		if (droprate <= 0 && adenarate <= 0) {
			return;
		}

		int itemId;
		int itemCount;
		int addCount;
		int randomChance;
		L1ItemInstance item;
		Random random = new Random();
		for (L1Drop drop : dropList) {
			// 取得
			itemId = drop.getItemid();
			if (adenarate == 0 && itemId == L1ItemId.ADENA) {
				continue; // ０场合
			}

			// 判定
			randomChance = random.nextInt(0xf4240) + 1;
			double rateOfMapId = MapsTable.getInstance()
					.getDropRate(npc.getMapId());
			double rateOfItem = DropItemTable.getInstance()
					.getDropRate(itemId);
			if (droprate == 0
					|| drop.getChance() * droprate * rateOfMapId * rateOfItem
							< randomChance) {
				continue;
			}

			// 个数设定
			double amount = DropItemTable.getInstance()
					.getDropAmount(itemId);
			int min = (int)(drop.getMin() * amount);
			int max = (int)(drop.getMax() * amount);

			itemCount = min;
			addCount = max - min + 1;
			if (addCount > 1) {
				itemCount += random.nextInt(addCount);
			}
			if (itemId == L1ItemId.ADENA) { // 场合挂
				itemCount *= adenarate;
			}
			if (itemCount < 0) {
				itemCount = 0;
			}
			if (itemCount > 2000000000) {
				itemCount = 2000000000;
			}

			// 生成
			item = ItemTable.getInstance().createItem(itemId);
			if (item == null) {
				return;
			}
			item.setCount(itemCount);
			if (item.getItem().isBroad()) {
				item.setBroad(true);
			}
			// 格纳
			inventory.storeItem(item);
		}
	}

	// 分配
	public void dropShare(L1NpcInstance npc, ArrayList<L1Character> acquisitorList,
			ArrayList<Integer> hateList) {
		L1Inventory inventory = npc.getInventory();
		if (inventory.getSize() == 0) {
			return;
		}
		if (acquisitorList.size() != hateList.size()) {
			return;
		}
		// 合计取得
		int totalHate = 0;
		L1Character acquisitor;
		for (int i = hateList.size() - 1; i >= 0; i--) {
			acquisitor = acquisitorList.get(i);
			if ((Config.AUTO_LOOT == 2)  // ２场合及省
					&& (acquisitor instanceof L1SummonInstance
							|| acquisitor instanceof L1PetInstance)) {
				acquisitorList.remove(i);
				hateList.remove(i);
			} else if (acquisitor != null
					&& !acquisitor.isDead()
					&& acquisitor.getMapId() == npc.getMapId()
					&& acquisitor.getLocation().getTileLineDistance(
							npc.getLocation()) <= Config.LOOTING_RANGE) {
				totalHate += hateList.get(i);
			} else {// null死远排除
				acquisitorList.remove(i);
				hateList.remove(i);
			}
		}

		// 分配
		L1ItemInstance item;
		L1Inventory targetInventory = null;
		//L1Character character = null;
		L1PcInstance player;
		L1PcInstance[] partyMember;
		Random random = new Random();
		int randomInt;
		int chanceHate;
		for (int i = inventory.getSize(); i > 0; i--) {
			item = inventory.getItems().get(0);
			if (!item.isGmDrop() && NotDropTable.getInstance().InNotDropList(item.getItem().getItemId())){
				continue;
			}
			if (((Config.AUTO_LOOT != 0) || item.getItem().getItemId() == L1ItemId.ADENA) && totalHate > 0) {
				randomInt = random.nextInt(totalHate);
				chanceHate = 0;
				for (int j = hateList.size() - 1; j >= 0; j--) {
					chanceHate += (Integer) hateList.get(j);
					if (chanceHate > randomInt) {
						acquisitor = (L1Character) acquisitorList.get(j);
						if (acquisitor.getInventory().checkAddItem(item,
								item.getCount()) == L1Inventory.OK) {
							//character = acquisitor;
							targetInventory = acquisitor.getInventory();
							if (acquisitor instanceof L1PcInstance) {
								player = (L1PcInstance) acquisitor;
								
								//掉落道具队伍随机分配
								if (player.getParty() != null){
									final L1PcInstance[] partyPlayers = player.getParty().getMembers();
									final int rnd = random.nextInt(partyPlayers.length);
									if (partyPlayers[rnd].getMapId() == player.getMapId() && partyPlayers[rnd].getLineDistance(player) < 9){
										if (partyPlayers[rnd].getInventory().checkAddItem(item,item.getCount()) == L1Inventory.OK) {
											targetInventory = partyPlayers[rnd].getInventory();
											player = partyPlayers[rnd];
										}
									}
								}
								//掉落道具队伍随机分配 end
								
								L1ItemInstance l1iteminstance = player.getInventory().findItemId(L1ItemId.ADENA); // 所持
								if (l1iteminstance != null && l1iteminstance.getCount() > 2000000000) {
									targetInventory = L1World.getInstance().getInventory(acquisitor.getX(),acquisitor.getY(),acquisitor.getMapId()); // 持足元落
									player.sendPackets(new S_ServerMessage(166,"所持有的金币","超过20亿")); // \f1%0%4%1%3%2
								} else {
									if (player.isInParty()) {// 场合
										partyMember = player.getParty().getMembers();
										for (int p = 0; p < partyMember.length; p++) {
											partyMember[p].sendPackets(new S_ServerMessage(813,
															npc.getNpcTemplate().get_name(),
															item.getLogName(),
															player.getName()));
										}
									} else {
										// 场合
										player.sendPackets(new S_ServerMessage(
												143, npc.getNpcTemplate()
														.get_name(), item
														.getLogName())); // \f1%0%1。
									}
								}
								//final L1ShouBaoTemp shoubaoTmp = ShouBaoReading.get().getTemp(item.getItem().getItemId());
								final L1ShouBaoTemp shoubaoTmp = null;//去掉首爆功能
								if(item.issbxz()) {
									return;
								}
								if (shoubaoTmp != null && shoubaoTmp.getObjId() == 0){
									if (ShouBaoReading.get().update(shoubaoTmp, player.getId(), player.getName())){
										final L1Item gvitem = ItemTable.getInstance().getTemplate(shoubaoTmp.getGiveItemId());
										if (gvitem != null){
											player.getInventory().storeItem(shoubaoTmp.getGiveItemId(), shoubaoTmp.getGiveItemCount());
											final StringBuilder msg = new StringBuilder();
											msg.append(gvitem.getName());
											if (shoubaoTmp.getGiveItemCount() > 1){
												msg.append("(");
												msg.append(shoubaoTmp.getGiveItemCount());
												msg.append(")");
											}
											L1World.getInstance().broadcastPacketToAll(new S_BlueMessage(4536,player.getName(),item.getItem().getName(),msg.toString()));
										}
									}
								}
								if (item.isBroad()) {
									final String mapName = MapsTable.getInstance().getMapName(player.getMapId(),player.getX(),player.getY());
									L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(4537,player.getName(),mapName,player.getX() + "," + player.getY(),npc.getName(),item.getLogViewName()));
									J_Main.getInstance().addConsol("玩家： "+player.getName()+" 从怪物 "+npc.getName()+" 获得物品"+item.getLogViewName());
									WriteLogTxt.Recording("掉宝记录", "玩家： "+player.getName()+" 从怪物 "+npc.getName()+" 获得物品"+item.getLogViewName()+" OBJID:"+item.getId());
									item.setBroad(false);
								}
							}else if (acquisitor instanceof L1PetInstance) {
								if (((L1PetInstance) acquisitor).getMaster()!=null) {
									if (item.isBroad()) {
										L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("玩家： "+((L1PetInstance) acquisitor).getMaster().getName()+" 的宠物"+((L1PetInstance) acquisitor).getNpcTemplate().get_name()+"从怪物 "+npc.getName()+" 获得物品"+item.getLogViewName()));
										J_Main.getInstance().addConsol("玩家： "+((L1PetInstance) acquisitor).getMaster().getName()+" 的宠物"+((L1PetInstance) acquisitor).getNpcTemplate().get_name()+"从怪物 "+npc.getName()+" 获得物品"+item.getLogViewName());
										WriteLogTxt.Recording("掉宝记录", "玩家： "+((L1PetInstance) acquisitor).getMaster().getName()+" 的宠物"+((L1PetInstance) acquisitor).getNpcTemplate().get_name()+"从怪物 "+npc.getName()+" 获得物品"+item.getLogViewName()+" OBJID:"+item.getId());
										item.setBroad(false);
									}
								}														
							}else if (acquisitor instanceof L1SummonInstance) {
								if (((L1SummonInstance) acquisitor).getMaster()!=null) {
									if (item.isBroad()) {
										L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("玩家： "+((L1SummonInstance) acquisitor).getMaster().getName()+" 的宠物"+((L1SummonInstance) acquisitor).getNpcTemplate().get_name()+"从怪物 "+npc.getName()+" 获得物品"+item.getLogViewName()));
										J_Main.getInstance().addConsol("玩家： "+((L1SummonInstance) acquisitor).getMaster().getName()+" 的宠物"+((L1SummonInstance) acquisitor).getNpcTemplate().get_name()+"从怪物 "+npc.getName()+" 获得物品"+item.getLogViewName());
										WriteLogTxt.Recording("掉宝记录", "玩家： "+((L1SummonInstance) acquisitor).getMaster().getName()+" 的宠物"+((L1SummonInstance) acquisitor).getNpcTemplate().get_name()+"从怪物 "+npc.getName()+" 获得物品"+item.getLogViewName()+" OBJID:"+item.getId());
										item.setBroad(false);
									}
								}		
							}
						} else {
							targetInventory = L1World.getInstance()
									.getInventory(acquisitor.getX(),
											acquisitor.getY(),
											acquisitor.getMapId()); // 持足元落
							if (item.isBroad()) {
								L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("哪个土豪那么任性？从怪物 "+npc.getName()+" 获得物品 "+item.getLogViewName()+" 掉地上还不去捡？"));
								J_Main.getInstance().addConsol("哪个土豪那么任性？从怪物 "+npc.getName()+" 获得物品 "+item.getLogViewName()+" 掉地上还不去捡？");
								WriteLogTxt.Recording("掉宝记录", "哪个土豪那么任性？从怪物 "+npc.getName()+" 获得物品 "+item.getLogViewName()+" OBJID:"+item.getId()+" 掉地上还不去捡？");
								item.setBroad(false);
							}					
						}
						break;
					}
				}
			} else {// 
				List<Integer> dirList = new ArrayList<Integer>();
				for (int j = 0; j < 8; j++) {
					dirList.add(j);
				}
				int x = 0;
				int y = 0;
				int dir = 0;
				do {
					if (dirList.size() == 0) {
						x = 0;
						y = 0;
						break;
					}
					randomInt = random.nextInt(dirList.size());
					dir = dirList.get(randomInt);
					dirList.remove(randomInt);
					switch (dir) {
					case 0:
						x = 0;
						y = -1;
						break;
					case 1:
						x = 1;
						y = -1;
						break;
					case 2:
						x = 1;
						y = 0;
						break;
					case 3:
						x = 1;
						y = 1;
						break;
					case 4:
						x = 0;
						y = 1;
						break;
					case 5:
						x = -1;
						y = 1;
						break;
					case 6:
						x = -1;
						y = 0;
						break;
					case 7:
						x = -1;
						y = -1;
						break;
					}
				} while (!npc.getMap().isPassable(npc.getX(), npc.getY(), dir));
				targetInventory = L1World.getInstance().getInventory(
						npc.getX() + x, npc.getY() + y, npc.getMapId());
				if (item.isBroad()) {
					L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("哪个土豪那么任性？从怪物 "+npc.getName()+" 获得物品 "+item.getLogViewName()+" 掉地上还不去捡？"));
					J_Main.getInstance().addConsol("哪个土豪那么任性？从怪物 "+npc.getName()+" 获得物品 "+item.getLogViewName()+" 掉地上还不去捡？");
					WriteLogTxt.Recording("掉宝记录", "哪个土豪那么任性？从怪物 "+npc.getName()+" 获得物品 "+item.getLogViewName()+" OBJID:"+item.getId()+" 掉地上还不去捡？");
					item.setBroad(false);
				}			
			}
			inventory.tradeItem(item, item.getCount(), targetInventory);
			item.setBroad(false);
			/*if (character instanceof L1PcInstance) {
				WriteLogTxt.Recording("怪物掉落记录", "玩家"+character.getName()
						+"获得了物品：+"
						+item.getEnchantLevel()+" "
						+item.getName()+"("+item.getCount()+"),itemid:#"+item.getItemId()
						+"#objid:<"+item.getId()+">");
			}*/
			
		}
	}

	public ArrayList<L1Drop> getDrops(int mobID) {
		ArrayList<L1Drop> dropList = _droplists.get(mobID);
		return dropList;
	}

}