package l1j.server.server.model.Instance;

import java.lang.reflect.Constructor;

import l1j.server.server.IdFactory;
import l1j.server.server.datatables.ItemTable;//获得道具 
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.serverpackets.S_FollowPack;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.world.L1World;
//获得道具 

public class L1FollowInstance extends L1NpcInstance {
	private static final long serialVersionUID = 1L;



	// 场合处理
	@Override
	public boolean noTarget() {
		// 搜寻到目标则消失 
		L1NpcInstance targetNpc = null;
		for (L1NpcInstance npc : L1World.getInstance().getVisibleNpc(this)) {
			if (npc.getNpcTemplate().get_npcId() == 70740 && getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_10) { // 公爵的士兵 && 调查员
				Death(null);
				return false;
			}
			if (npc.getNpcTemplate().get_npcId() == 70811 && getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_11) { // 莱拉 && 安迪亚
				Death(null);
				return false;
			}
		}
		// 搜寻到目标则消失  end
		
		if (getLocation().getTileLineDistance(_master.getLocation()) > 12) {// 若距离太远
			setParalyzed(true);
			deleteMe();

			// 原地召唤调查员 
			if (getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_10) { // 调查员
				spawn(getNpcTemplate().get_npcId(), getX(), getY(), getHeading(), getMapId());
			}
			// 原地召唤调查员  end

			// 原地召唤安迪亚 
			if (getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_11) { // 安迪亚
				spawn(getNpcTemplate().get_npcId(), getX(), getY(), getHeading(), getMapId());
			}
			// 原地召唤安迪亚  end

			return false;
		} else if (_master != null && _master.getMapId() == getMapId()) {
			// ●主人追尾
			if (getLocation().getTileLineDistance(_master.getLocation()) > 1) {
				int dir = moveDirection(_master.getX(), _master.getY());
				setDirectionMove(dir);
				setSleepTime(calcSleepTime(getPassispeed()));
			}
		}
		return false;
	}

	// 召唤跟随者 
	public L1FollowInstance(L1NpcInstance target, L1Character master) {
		super(null);

		_master = master;

		setId(IdFactory.getInstance().nextId());
		setting_template(target.getNpcTemplate());

		setMaster(master);
		setX(target.getX());
		setY(target.getY());
		setMap(target.getMapId());
		setHeading(target.getHeading());
		setLightSize(target.getLightSize());
		
		target.setParalyzed(true);
		target.deleteMe();

		L1World.getInstance().storeWorldObject(this);
		L1World.getInstance().addVisibleObject(this);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}

		startAI();
		master.addPet(this);
	}
	// 召唤跟随者  end

	public synchronized void Death(L1Character lastAttacker) {
		if (!isDead()) {
			if (getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_10) { // 调查员
				// 给予道具 
				setParalyzed(true);
				L1PcInstance pc = (L1PcInstance) _master;
				if (!pc.getInventory().checkItem(40593)) {
					createNewItem(pc, 40593, 1); // 调查簿的缺页
				}
				// 给予道具  end

				// 在黄昏山脉召唤一名调查员 
				spawn(getNpcTemplate().get_npcId(), 34242, 33356, 5, (short) 4);
				//在黄昏山脉召唤一名调查员  end

				setDead(true);
				setCurrentHp(0);

				getMap().setPassable(getLocation(), true);

				deleteMe();
			}
		
			if (getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_11) { // 安迪亚
				// 给予道具 
				setParalyzed(true);
				L1PcInstance pc = (L1PcInstance) _master;
				if (!pc.getInventory().checkItem(40582) && !pc.getInventory().checkItem(40853)) {
					createNewItem(pc, 40582, 1); // 安迪亚之袋
				}
				// 给予道具  end

				// 在妖森召唤一名安迪亚 
				spawn(getNpcTemplate().get_npcId(), 32944, 32280, 5, (short) 4);
				// 在妖森召唤一名安迪亚  end

				setDead(true);
				setCurrentHp(0);

				getMap().setPassable(getLocation(), true);

				deleteMe();
			}
		}
	}

	// 消去处理
	@Override
	public synchronized void deleteMe() {
		_master.getPetList().remove(getId());
		super.deleteMe();
	}

	@Override
	public void onAction(L1PcInstance attacker) {
		L1Attack attack = new L1Attack(attacker, this);
		if (attack.calcHit()) {
			attack.calcDamage();
		}
		attack.action();
		attack.commit();
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
		if (isDead()) {
			return;
		}
		if (getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_10) { // 调查员
			if (_master.equals(player)) {//被跟随的人
				player.sendPackets(new S_NPCTalkReturn(getId(), "searcherk2"));
			} else {//非被跟随的人
				player.sendPackets(new S_NPCTalkReturn(getId(), "searcherk4"));
			}
		}
		if (getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_11) { // 安迪亚
			if (_master.equals(player)) {//被跟随的人
				player.sendPackets(new S_NPCTalkReturn(getId(), "endiaq2"));
			} else {//非被跟随的人
				player.sendPackets(new S_NPCTalkReturn(getId(), "endiaq4"));
			}
		}
		
	}
	
	@Override
	public void onFinalAction(L1PcInstance player, String action) {
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_FollowPack(this, perceivedFrom));
	}
	
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
		} else {
			return false;
		}
	}
	// 获得道具  end
	
	// 召唤 
	private void spawn(int npcId, int X, int Y, int H, short Map) {
		L1Npc spawnmonster = NpcTable.getInstance().getTemplate(npcId);
		if (spawnmonster != null) {
			L1NpcInstance mob = null;
			try {
				String implementationName = spawnmonster.getImpl();
				Constructor _constructor = Class.forName((new StringBuilder()).append("l1j.server.server.model.Instance.").append(implementationName).append("Instance").toString()).getConstructors()[0];
				mob = (L1NpcInstance) _constructor.newInstance(new Object[] { spawnmonster });
				mob.setId(IdFactory.getInstance().nextId());
				mob.setX(X);
				mob.setY(Y);
				mob.setHomeX(X);
				mob.setHomeY(Y);
				mob.setMap(Map);
				mob.setHeading(H);
				L1World.getInstance().storeWorldObject(mob);
				L1World.getInstance().addVisibleObject(mob);
				L1Object object = L1World.getInstance().findObject(mob.getId());
				L1QuestInstance newnpc = (L1QuestInstance) object;
				newnpc.onNpcAI();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	// 召唤  end
}