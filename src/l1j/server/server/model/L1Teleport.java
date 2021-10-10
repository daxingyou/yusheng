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
package l1j.server.server.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;




// 祭司、跟随者 
import l1j.server.server.datatables.MapsNotAllowedTable;
import l1j.server.server.model.Instance.L1BabyInstance;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1FollowInstance;
import l1j.server.server.model.Instance.L1HierarchInstance;
// 祭司、跟随者  end
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.skill.L1SkillId;
// 无法带宠物的地区可以带魔法娃娃 
import l1j.server.server.serverpackets.S_BabyPack;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_DollPack;
import l1j.server.server.serverpackets.S_HierarchPack;
import l1j.server.server.serverpackets.S_MapID;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_OwnCharPack;
import l1j.server.server.serverpackets.S_PacketBoxWindShackle;
// 无法带宠物的地区可以带魔法娃娃  end
import l1j.server.server.serverpackets.S_PetPack;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SummonPack;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

public class L1Teleport {

	// 种类
	public static final int TELEPORT = 0;

	public static final int CHANGE_POSITION = 1;

	public static final int ADVANCED_MASS_TELEPORT = 2;

	public static final int CALL_CLAN = 3;

	// 顺番teleport(白), change position e(青), ad mass teleport e(赤), call clan()
	public static final int[] EFFECT_SPR = { 169, 2235, 2236, 2281 };

	public static final int[] EFFECT_TIME = { 280, 440, 440, 1120 };

	private L1Teleport() {
	}

	public static void teleport(L1PcInstance player, L1Location loc, int head,
			boolean effectable) {
		teleport(player, loc.getX(), loc.getY(), (short) loc.getMapId(), head,
				effectable, TELEPORT);
	}

	public static void teleport(L1PcInstance player, L1Location loc, int head,
			boolean effectable, int skillType) {
		teleport(player, loc.getX(), loc.getY(), (short) loc.getMapId(), head,
				effectable, skillType);
	}

	public static void teleport(L1PcInstance player, int x, int y, short mapid,
			int head, boolean effectable) {
		teleport(player, x, y, mapid, head, effectable, TELEPORT);
		player.sendPackets(new S_MapID(player.getMapId(), player));
	}
	public static void teleport(L1PcInstance player, int x, int y, short mapId,
			int head, boolean effectable, int skillType) {
		if (/*player.isDead() || */player.isTeleport()) {
			return;
		}
		
		// 获取飞往的地图id
		L1Map map = L1WorldMap.getInstance().getMap(mapId);

		MapsNotAllowedTable mapInstance = MapsNotAllowedTable.getInstance();
		int allowLevel = mapInstance.getMapAllowLevel(mapId);//允许进入地图的等级
		if(mapInstance.getMapAllow(mapId) == 0){//如果是禁止的地图，不允许进入
			player.sendPackets(new S_SystemMessage("前往的地图暂未开放!"));
			return;
		}else if(player.getLevel() < allowLevel){
			player.sendPackets(new S_SystemMessage("等级需达到"+allowLevel+"级才能进入此地图!"));
			return;
		}

		if (player.getMapId() == 99) {
			if (!player.isGm()) {
				return;
			}
		}
		
		if (player.getMapId() != mapId && (mapId == 340 || mapId == 350 || mapId == 360 || mapId == 370)){
			player.sendPackets(new S_SystemMessage("\\F1已进入商店村 可打指令 .查询 道具名称 查询摊位位置 "));
			if (mapId == 360){
				player.sendPackets(new S_SystemMessage("\\F1***温馨提示 此商店村货币为【元宝】***"));
				player.sendPackets(new S_SystemMessage("\\F2***温馨提示 此商店村货币为【元宝】***"));
				player.sendPackets(new S_SystemMessage("\\F3***温馨提示 此商店村货币为【元宝】***"));
			}
		}
		
		if (!map.isInMap(x, y) && !player.isGm()) {
			x = player.getX();
			y = player.getY();
			mapId = player.getMapId();
		}

		//player.clearMove();
		player.setTeleport(true);
		player.setCheck(true);
		// 表示
		if (effectable && (skillType >= 0 && skillType <= EFFECT_SPR.length)) {
			S_SkillSound packet = new S_SkillSound(
					player.getId(), EFFECT_SPR[skillType]);
			player.sendPackets(packet);
			player.broadcastPacket(packet);
		}
		if (!player.isGmInvis()) {
			player.getMap().setPassable(player.getLocation(), true);
		}
		Collection<L1PcInstance> list = player.getKnownPlayers();
		for (L1PcInstance target : list) {
			target.sendPackets(new S_RemoveObject(player));
		}
		L1World.getInstance().moveVisibleObject(player, mapId);
		player.setLocation(x, y, mapId);
		player.setHeading(head);
		player.sendPackets(new S_MapID(player.getMapId(), player));

		if (!player.isGhost() && !player.isGmInvis() && !player.isInvisble()) {
			player.broadcastPacket(new S_OtherCharPacks(player));
		}
		player.sendPackets(new S_OwnCharPack(player));

		player.removeAllKnownObjects();
		player.sendVisualEffectAtTeleport(); // 、毒、水中等视佅果表示
		player.updateObject();
		// spr番6310, 5641身中后移动
		// 武器移动、S_CharVisualUpdate送信
		player.sendPackets(new S_CharVisualUpdate(player));

		player.killSkillEffectTimer(L1SkillId.MEDITATION);

		/*
		 * subjects 先面居。
		 * 各UpdateObject行方上、
		 * 负荷大为、一旦Set格纳最后UpdateObject。
		 */
		HashSet<L1PcInstance> subjects = new HashSet<L1PcInstance>();
		subjects.add(player);

		if (!player.isGhost() && player.getMap().isTakePets()) {
			// 一绪移动。
			for (L1NpcInstance petNpc : player.getPetList().values()) {

				// 跟随者无法瞬移 
				if (!(petNpc instanceof L1FollowInstance)) {
				// 跟随者无法瞬移  end

				// 先设定
				L1Location loc = player.getLocation().randomLocation(3, false);
				int nx = loc.getX();
				int ny = loc.getY();

				teleport(petNpc, nx, ny, mapId, head);
				if (petNpc instanceof L1SummonInstance) { // 
					L1SummonInstance summon = (L1SummonInstance) petNpc;
					player.sendPackets(new S_SummonPack(summon, player));
				} else if (petNpc instanceof L1PetInstance) { // 
					L1PetInstance pet = (L1PetInstance) petNpc;
					player.sendPackets(new S_PetPack(pet, player));
				}

				for (L1PcInstance pc : L1World.getInstance()
						.getRecognizePlayer(petNpc)) {
					// 元先同PC居场合、正更新为、一度remove。
					pc.removeKnownObject(petNpc);
					subjects.add(pc);
				}

				// 跟随者无法瞬移 
				}
				// 跟随者无法瞬移  end
			}

			// 一绪移动。
			for (L1DollInstance doll : player.getDollList().values()) {

				// 先设定
				L1Location loc = player.getLocation().randomLocation(3, false);
				int nx = loc.getX();
				int ny = loc.getY();

				teleport(doll, nx, ny, mapId, head);
				player.sendPackets(new S_DollPack(doll, player));

				for (L1PcInstance pc : L1World.getInstance()
						.getRecognizePlayer(doll)) {
					// 元先同PC居场合、正更新为、一度remove。
					pc.removeKnownObject(doll);
					subjects.add(pc);
				}

			}
		}
		// 无法带宠物的地区可以带魔法娃娃 
		else if (!player.isGhost() && !player.getMap().isTakePets()) {
			// 一绪移动。
			for (L1NpcInstance petNpc : player.getPetList().values()) {

				if (!(petNpc instanceof L1FollowInstance) && 
					!(petNpc instanceof L1SummonInstance) && 
					!(petNpc instanceof L1PetInstance)) {

				// 先设定
				L1Location loc = player.getLocation().randomLocation(3, false);
				int nx = loc.getX();
				int ny = loc.getY();

				teleport(petNpc, nx, ny, mapId, head);
				// 魔法娃娃、祭司 
				if (petNpc instanceof L1BabyInstance) { // 
					L1BabyInstance pet = (L1BabyInstance) petNpc;
					player.sendPackets(new S_BabyPack(pet, player));
				} else if (petNpc instanceof L1HierarchInstance) { // 
					L1HierarchInstance pet = (L1HierarchInstance) petNpc;
					player.sendPackets(new S_HierarchPack(pet, player));
				}
				// 魔法娃娃、祭司  end

				for (L1PcInstance pc : L1World.getInstance()
						.getRecognizePlayer(petNpc)) {
					// 元先同PC居场合、正更新为、一度remove。
					pc.removeKnownObject(petNpc);
					subjects.add(pc);
				}

				}
			}
		}
		// 无法带宠物的地区可以带魔法娃娃  end

		for (L1PcInstance pc : subjects) {
			pc.updateObject();
		}
		if (player.hasSkillEffect(L1SkillId.WIND_SHACKLE)) {
			player.sendPackets(new S_PacketBoxWindShackle(player.getId(), player.getSkillEffectTimeSec(L1SkillId.WIND_SHACKLE)));
		}
		if (!player.isGmInvis()) {
			player.getMap().setPassable(player.getLocation(), false);
		}
		if (player.getParty() != null){
			player.getParty().updateMiniHP(player);
		}
		player.setTeleport(false);
	}

	public static void teleport(L1Character npc, int x, int y, short map,
			int head) {
		L1World.getInstance().moveVisibleObject(npc, map);

		L1WorldMap.getInstance().getMap(npc.getMapId()).setPassable(npc.getX(),
				npc.getY(), true);
		npc.setX(x);
		npc.setY(y);
		npc.setMap(map);
		npc.setHeading(head);
		L1WorldMap.getInstance().getMap(npc.getMapId()).setPassable(npc.getX(),
				npc.getY(), false);
	}

	/*
	 * targetdistance指定分前。指定场合何。
	 */
	public static void teleportToTargetFront(L1Character cha,
			L1Character target, int distance) {
		int locX = target.getX();
		int locY = target.getY();
		int heading = target.getHeading();
		L1Map map = target.getMap();
		short mapId = target.getMapId();

		// 向先座标决。
		switch (heading) {
		case 1:
			locX += distance;
			locY -= distance;
			break;

		case 2:
			locX += distance;
			break;

		case 3:
			locX += distance;
			locY += distance;
			break;

		case 4:
			locY += distance;
			break;

		case 5:
			locX -= distance;
			locY += distance;
			break;

		case 6:
			locX -= distance;
			break;

		case 7:
			locX -= distance;
			locY -= distance;
			break;

		case 0:
			locY -= distance;
			break;

		default:
			break;

		}

		if (map.isPassable(locX, locY)) {
			if (cha instanceof L1PcInstance) {
				teleport((L1PcInstance) cha, locX, locY, mapId, cha
						.getHeading(), true);
			} else if (cha instanceof L1NpcInstance) {
				((L1NpcInstance) cha).teleport(locX, locY, cha.getHeading());
			}
		}
	}

	public static void randomTeleport(L1PcInstance pc, boolean effectable) {
		// 本处理违结构
		L1Location newLocation = pc.getLocation().randomLocation(200, true);
		int newX = newLocation.getX();
		int newY = newLocation.getY();
		short mapId = (short) newLocation.getMapId();

		L1Teleport.teleport(pc, newX, newY, mapId, 5, effectable);
	}
}
