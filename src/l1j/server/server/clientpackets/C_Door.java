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

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.lang.reflect.Constructor;  

import l1j.server.server.ActionCodes;

import l1j.server.server.IdFactory;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.datatables.ItemTable;//获得道具 
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1ItemInstance;//获得道具 
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_Door;
import l1j.server.server.serverpackets.S_Doors;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.world.L1World;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket, C_Door

public class C_Door extends ClientBasePacket {

	private static final String C_DOOR = "[C] C_Door";

	public C_Door(byte abyte0[], LineageClient _client)
			throws Exception {
		super(abyte0);
		int locX = readH();
		int locY = readH();
		int objectId = readD();
		//判断是否需要钥匙才能开启门 
		L1PcInstance pc = _client.getActiveChar();
		//判断是否需要钥匙才能开启门  end

		L1DoorInstance door = (L1DoorInstance)L1World.getInstance()
				.findObject(objectId);
		if (door != null && door.getKeeperId() == 0) {
			/*删除if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
				door.setOpenStatus(ActionCodes.ACTION_Close);
				door.setPassable(L1DoorInstance.NOT_PASS);
				door.broadcastPacket(new S_DoActionGFX(objectId,
						door.getOpenStatus()));
				door.broadcastPacket(new S_Door(door));
			} else if (door.getOpenStatus() == ActionCodes.ACTION_Close) {
				door.setOpenStatus(ActionCodes.ACTION_Open);
				door.setPassable(L1DoorInstance.PASS);
				door.broadcastPacket(new S_DoActionGFX(objectId,
						door.getOpenStatus()));
				door.broadcastPacket(new S_Door(door));
			}删除*/
			
			//判断是否需要钥匙才能开启门 
			if (door.getKeeperId() == 40600 && pc.getInventory().checkItem(door.getKeeperId())) {//需堕落钥匙的宝箱

				if (door.getOpenStatus() == ActionCodes.ACTION_Close) {//关闭状态
					door.setOpenStatus(ActionCodes.ACTION_Open);
					door.setDoorPassable(L1DoorInstance.PASS);
					door.broadcastPacket(new S_DoActionGFX(objectId,
							door.getOpenStatus()));
					door.broadcastPacket(new S_Door(door));
					
					Random random = new Random();
					if (random.nextInt(100) < 35) {
						spawn(l1j.william.New_Id.Npc_AJ_2_13, 32621, 32906, 5, (short) 306, 5);
						spawn(l1j.william.New_Id.Npc_AJ_2_13, 32621, 32906, 5, (short) 306, 5);
						spawn(l1j.william.New_Id.Npc_AJ_2_13, 32621, 32906, 5, (short) 306, 5);
						spawn(l1j.william.New_Id.Npc_AJ_2_13, 32621, 32906, 5, (short) 306, 5);
					} else {
						createNewItem(pc, 40010, 1);
					}

					_door = door;
            		_timer = new OpenTimer();
            		(new Timer()).schedule(_timer, 3 * 1000);
            		//door.setSkillEffect(l1j.william.New_Id.Skill_AJ_1_9, 3 * 1000);//判断?秒后关门
				}

			} else if (door.getKeeperId() == 40606 && pc.getInventory().checkItem(door.getKeeperId())) {//需混沌钥匙的壁画

				if (door.getDoorId() == 3026) { // 左图
					if (door.getOpenStatus() == ActionCodes.ACTION_Close) {//关闭状态
						door.setOpenStatus(ActionCodes.ACTION_Open);
						door.setDoorPassable(L1DoorInstance.PASS);
						door.broadcastPacket(new S_DoActionGFX(objectId,
								door.getOpenStatus()));
						door.broadcastPacket(new S_Door(door));

						Random random = new Random();
						if (random.nextInt(100) < 35) {
							spawn(l1j.william.New_Id.Npc_AJ_2_13, 32621, 32906, 5, (short) 306, 5);
							spawn(l1j.william.New_Id.Npc_AJ_2_13, 32621, 32906, 5, (short) 306, 5);
							spawn(l1j.william.New_Id.Npc_AJ_2_13, 32621, 32906, 5, (short) 306, 5);
							spawn(l1j.william.New_Id.Npc_AJ_2_13, 32621, 32906, 5, (short) 306, 5);
						} else {
							createNewItem(pc, 40010, 1);
						}

						int O = 0;
						for (L1DoorInstance door1 : DoorSpawnTable.getInstance().getDoorList()) {
							if (door1.getOrder() >= 111 && door1.getOrder() <= 1114) {
								if (door1.getOpenStatus() == ActionCodes.ACTION_Open) { // 检查3幅图画跟2号宝箱是否开启
									O++;
								}
							}
						}
					
						if (O >= 4) {
							for (L1DoorInstance door2 : DoorSpawnTable.getInstance().getDoorList()) {
								if (door2.getKeeperId() == 306) {
									if (door2.getOpenStatus() == ActionCodes.ACTION_Close) { // 开启通往魔法阵的墙壁
										door2.setOpenStatus(ActionCodes.ACTION_Open);
										door2.setDoorPassable(L1DoorInstance.PASS);
										door2.broadcastPacket(new S_DoActionGFX(door2.getId(),
										door2.getOpenStatus()));
										door2.broadcastPacket(new S_Door(door2));
										door2.setSkillEffect(l1j.william.New_Id.Skill_AJ_1_9, 185 * 10);//判断?秒后关门
									}
								}
							}
						}
						_door = door;
            			_timer = new OpenTimer();
            			(new Timer()).schedule(_timer, 2 * 1000);
            			//door.setSkillEffect(l1j.william.New_Id.Skill_AJ_1_9, 2 * 1000);//判断3秒后关门
					}
				} else {
					if (door.getOpenStatus() == ActionCodes.ACTION_Close) {//关闭状态
						door.setOpenStatus(ActionCodes.ACTION_Open);
						door.setDoorPassable(L1DoorInstance.PASS);
						door.broadcastPacket(new S_DoActionGFX(objectId,
								door.getOpenStatus()));
						door.broadcastPacket(new S_Door(door));
						
						Random random = new Random();
						if (random.nextInt(100) < 35) {
							spawn(l1j.william.New_Id.Npc_AJ_2_13, 32621, 32906, 5, (short) 306, 5);
							spawn(l1j.william.New_Id.Npc_AJ_2_13, 32621, 32906, 5, (short) 306, 5);
							spawn(l1j.william.New_Id.Npc_AJ_2_13, 32621, 32906, 5, (short) 306, 5);
							spawn(l1j.william.New_Id.Npc_AJ_2_13, 32621, 32906, 5, (short) 306, 5);
						} else {
							createNewItem(pc, 40010, 1);
						}
					}
				}

			} else if (door.getKeeperId() != 0 && pc.getInventory().checkItem(door.getKeeperId())) {//需钥匙的门
				if (door.getOpenStatus() == ActionCodes.ACTION_Close) {//关闭状态
					door.setOpenStatus(ActionCodes.ACTION_Open);
					door.setDoorPassable(L1DoorInstance.PASS);
					door.broadcastPacket(new S_DoActionGFX(objectId,
							door.getOpenStatus()));
					door.broadcastPacket(new S_Door(door));
					_door = door;
            		_timer = new OpenTimer();
            		(new Timer()).schedule(_timer, 10 * 1000);
					//door.setSkillEffect(l1j.william.New_Id.Skill_AJ_1_9, 10 * 1000);//判断10秒后关门
				}
			} else if (door.getKeeperId() == 0) {//不需钥匙的门
				if (door.getOpenStatus() == ActionCodes.ACTION_Open) {
					door.setOpenStatus(ActionCodes.ACTION_Close);
					door.setDoorPassable(L1DoorInstance.NOT_PASS);
					door.broadcastPacket(new S_DoActionGFX(objectId,
							door.getOpenStatus()));
					door.broadcastPacket(new S_Door(door));
				} else if (door.getOpenStatus() == ActionCodes.ACTION_Close) {
					door.setOpenStatus(ActionCodes.ACTION_Open);
					door.setDoorPassable(L1DoorInstance.PASS);
					door.broadcastPacket(new S_DoActionGFX(objectId,
							door.getOpenStatus()));
					door.broadcastPacket(new S_Door(door));
				}
			}
			//判断是否需要钥匙才能开启门  end
		}
	}
	
	// 召唤 
	private void spawn(int npcId, int X, int Y, int H, short Map, int randomrange) {
		L1Npc spawnmonster = NpcTable.getInstance().getTemplate(npcId);
		if (spawnmonster != null) {
			L1NpcInstance mob = null;
			try {
				String implementationName = spawnmonster.getImpl();
				Constructor _constructor = Class.forName((new StringBuilder()).append("l1j.server.server.model.Instance.").append(implementationName).append("Instance").toString()).getConstructors()[0];
				mob = (L1NpcInstance) _constructor.newInstance(new Object[] { spawnmonster });
				mob.setId(IdFactory.getInstance().nextId());
				mob.setX(X + (int) (Math.random() * randomrange) - (int) (Math.random() * randomrange));
				mob.setY(Y + (int) (Math.random() * randomrange) - (int) (Math.random() * randomrange));
				mob.setHomeX(X);
				mob.setHomeY(Y);
				mob.setMap(Map);
				mob.setHeading(H);
				L1World.getInstance().storeWorldObject(mob);
				L1World.getInstance().addVisibleObject(mob);
				L1Object object = L1World.getInstance().findObject(mob.getId());
				L1MonsterInstance newnpc = (L1MonsterInstance) object;
				newnpc.onNpcAI();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	// 召唤  end
	
	// 获得道具 
	private boolean createNewItem(L1PcInstance pc, int item_id, int count) {
		L1ItemInstance item = ItemTable.getInstance().createItem(item_id);
		if (item != null) {
			item.setCount(count);
			if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
				pc.getInventory().storeItem(item);
			} else { // 持场合地面落 处理（不正防止）
				L1World.getInstance().getInventory(pc.getX(), pc.getY(),
						pc.getMapId()).storeItem(item);
			}
			pc.sendPackets(new S_ServerMessage(403, item.getLogName())); // %0手入
			return true;
		} 
		return false;
	}
	// 获得道具  end
	
	// 关门计时 
	private L1DoorInstance _door;
	private OpenTimer _timer;

    class OpenTimer extends TimerTask {

		public OpenTimer() {
		}

		public void run() {
			try {
				if (_door.getOpenStatus() == ActionCodes.ACTION_Open) {
					_door.doorclose();
				}
                _timer = null;
			} catch (Exception e) {
			}
		}
	}
	// 关门计时  end

	@Override
	public String getType() {
		return C_DOOR;
	}
}
