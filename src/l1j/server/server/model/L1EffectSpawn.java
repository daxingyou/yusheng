/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * 
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.model;

import java.lang.reflect.Constructor;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.world.L1World;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.william.FireDamage;
import l1j.william.NpcDamagePoison;
import l1j.william.NpcFireDamage;

// Referenced classes of package l1j.server.server.model:
// L1EffectSpawn

public class L1EffectSpawn {

	private static final Log _log = LogFactory.getLog(L1EffectSpawn.class);


	private static L1EffectSpawn _instance;

	private Constructor _constructor;

	private L1EffectSpawn() {}

	public static L1EffectSpawn getInstance() {
		if (_instance == null) {
			_instance = new L1EffectSpawn();
		}
		return _instance;
	}

	/**
	 * 生成设置
	 * 
	 * @param npcid NPCID
	 * @param time 存在时间(ms)
	 * @param locX 设置座标X
	 * @param locY 设置座标Y
	 * @param mapId 设置ID
	 * @return 生成
	 */
	public L1EffectInstance spawnEffect(int npcid, int time, int locX,
			int locY, short mapId) {
		L1Npc template = NpcTable.getInstance().getTemplate(npcid);
		L1EffectInstance effect = null;

		if (template == null) {
			return null;
		}

		String className = (new StringBuilder()).append(
				"l1j.server.server.model.Instance.").append(
				template.getImpl()).append("Instance").toString();

		try {
			_constructor = Class.forName(className).getConstructors()[0];
			Object obj[] = { template };
			effect = (L1EffectInstance) _constructor.newInstance(obj);

			effect.setId(IdFactory.getInstance().nextId());
			effect.setGfxId(template.get_gfxid());
			effect.setX(locX);
			effect.setY(locY);
			effect.setHomeX(locX);
			effect.setHomeY(locY);
			effect.setHeading(0);
			effect.setMap(mapId);
			L1World.getInstance().storeWorldObject(effect);
			L1World.getInstance().addVisibleObject(effect);
			effect.broadcastPacket(new S_NPCPack(effect));
			L1NpcDeleteTimer timer = new L1NpcDeleteTimer(effect, time);
			timer.begin();
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

		return effect;
	}

	public void doSpawnFireWall(L1Character cha, int targetX, int targetY) {
		L1Npc firewall = NpcTable.getInstance().getTemplate(81157); // 
		int duration = SkillsTable.getInstance().getTemplate(
				L1SkillId.FIRE_WALL).getBuffDuration();

		if (firewall == null) {
			throw new NullPointerException(
					"FireWall data not found:npcid=81157");
		}

		L1Character base = cha;
		for (int i = 0; i < 8; i++) {
			int a = base.targetDirection(targetX, targetY);
			int x = base.getX();
			int y = base.getY();
			// 变更 
			switch (a) {
			case 1: // '\001'
				x++;
				y--;
				break;

			case 2: // '\002'
				x++;
				break;

			case 3: // '\003'
				x++;
				y++;
				break;

			case 4: // '\004'
				y++;
				break;

			case 5: // '\005'
				x--;
				y++;
				break;

			case 6: // '\006'
				x--;
				break;

			case 7: // '\007'
				x--;
				y--;
				break;

			case 0: // '\0'
				y--;
				break;
			}
			// 变更  end
			if (!base.isAttackPosition(x, y, 1)) {
				x = base.getX();
				y = base.getY();
			}
			L1EffectInstance effect = spawnEffect(81157, duration * 1000, x, y,
					cha.getMapId());
			if (effect == null) {
				break;
			}
			effect.doFire();
			// 火牢伤害 
			L1Map map = L1WorldMap.getInstance().getMap(cha.getMapId());
			
			if(!map.isArrowPassable(x, y))
				break;

			FireDamage firedamage = new FireDamage(cha, effect);
			firedamage.onDamageAction();
			// 火牢伤害  end
			base = effect;
		}
	}
	
	public void doSpawnFireWallforNpc(L1Character _user, L1Character target) { // 火龙-火牢
		L1Character base = _user;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				L1EffectInstance effect = spawnEffect(81157, 10 * 1000, target.getX() + i, target.getY() + j, target.getMapId());
				if (effect == null) {
					break;
				}
				effect.doFire();
				// 火牢伤害 
				L1Map map = L1WorldMap.getInstance().getMap(_user.getMapId());

				NpcFireDamage firedamage = new NpcFireDamage(_user, effect);
				firedamage.onDamageAction();
				// 火牢伤害  end
				base = effect;
			}
		}
	}

	public void doSpawnDamagePoisonforNpc(L1Character _user, L1Character target) { // 地龙-毒雾
		L1Character base = _user;
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				L1EffectInstance effect = spawnEffect(90002, 10 * 1000, target.getX() + i, target.getY() + j, target.getMapId());
				if (effect == null) {
					break;
				}
				effect.doFire();
				// 毒雾效果 
				L1Map map = L1WorldMap.getInstance().getMap(_user.getMapId());

				NpcDamagePoison firedamage = new NpcDamagePoison(_user, effect);
				firedamage.onDamageAction();
				// 毒雾效果  end
				base = effect;
			}
		}
	}
}
