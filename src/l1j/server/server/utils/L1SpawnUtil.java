package l1j.server.server.utils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.types.Point;
import l1j.server.server.world.L1World;

public class L1SpawnUtil {
	
	private static Random _rnd = new Random();

	private static final Log _log = LogFactory.getLog(L1SpawnUtil.class);

	/**
	 * 场合、spawnNumber基spawn。
	 * 以外场合、spawnNumber未使用。
	 */
	public static void spawnmob(L1PcInstance pc, int npcId, int randomRange, int timeMillisToDelete) {
		try {
			L1Npc spawnmonster = NpcTable.getInstance().getTemplate(npcId);
			if (spawnmonster != null) {
				L1NpcInstance mob = null;
				try {
					String implementationName = spawnmonster.getImpl();
					Constructor<?> _constructor = Class.forName(
							(new StringBuilder()).append(
									"l1j.server.server.model.Instance.")
									.append(implementationName).append(
											"Instance").toString())
							.getConstructors()[0];
					mob = (L1NpcInstance) _constructor
							.newInstance(new Object[] { spawnmonster });
					mob.setId(IdFactory.getInstance().nextId());
					L1Location loc = pc.getLocation().randomLocation(8,
							false);
					int heading = _rnd.nextInt(8);
					mob.setX(loc.getX());
					mob.setY(loc.getY());
					mob.setHomeX(loc.getX());
					mob.setHomeY(loc.getY());
					short mapid = pc.getMapId();
					mob.setMap(mapid);
					mob.setHeading(heading);
					L1World.getInstance().storeWorldObject(mob);
					L1World.getInstance().addVisibleObject(mob);
					L1Object object = L1World.getInstance().findObject(
							mob.getId());
					L1MonsterInstance newnpc = (L1MonsterInstance) object;
					//newnpc.set_storeDroped(true); // 召唤无
					if (npcId == 45061 // 
							|| npcId == 45161 // 
							|| npcId == 45181 // 
							|| npcId == 45455) { // 
						newnpc.broadcastPacket(new S_DoActionGFX(
								newnpc.getId(), ActionCodes.ACTION_Hide));
						newnpc.setStatus(13);
						newnpc.broadcastPacket(new S_NPCPack(newnpc));
						newnpc.broadcastPacket(new S_DoActionGFX(
								newnpc.getId(), ActionCodes.ACTION_Appear));
						newnpc.setStatus(0);
						newnpc.broadcastPacket(new S_NPCPack(newnpc));
					}
					newnpc.onNpcAI();
				} catch (Exception e) {
					_log.error(e.getLocalizedMessage(), e);
				}
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	public static void spawn(L1PcInstance pc, int npcId, int randomRange, int timeMillisToDelete){
		try {
			final L1NpcInstance npc = NpcTable.getInstance().newNpcInstance(npcId);
			if (npc == null) {
				return;
			}
			npc.setId(IdFactory.getInstance().nextId());
			npc.setMap(pc.getMap());
			L1Location loc = pc.getLocation();
			if (randomRange == 0) {
				npc.getLocation().set(pc.getLocation());
				//npc.getLocation().forward(_pc.getHeading());
				
			} else {
				loc = pc.getLocation().randomLocation(randomRange,
						false);
				npc.getLocation().set(loc);
			}
			npc.setHomeX(loc.getX());
			npc.setHomeY(loc.getY());
			npc.setHeading(pc.getHeading());

			// 设置副本编号 TODO
			//npc.set_showId(_showid);

			/*L1QuestUser q = WorldQuest.get().get(_showid);
			if (q != null) {
				q.addNpc(npc);
			}*/
			L1World.getInstance().storeWorldObject(npc);
			L1World.getInstance().addVisibleObject(npc);
			//npc.turnOnOffLight();
			
			// 设置NPC现身
			/*npc.startChat(L1NpcInstance.CHAT_TIMING_APPEARANCE);
			if (0 < _timeMillisToDelete) {
				// 存在时间(秒)
				npc.set_spawnTime(_timeMillisToDelete);
			}*/

		} catch (final Exception e) {
			_log.info("执行NPC召唤发生异常: " + npcId, e);
		}
	}
}
