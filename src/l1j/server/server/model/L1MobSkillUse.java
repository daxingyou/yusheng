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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import l1j.server.server.ActionCodes;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.MobSkillTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1MobSkill;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1MobSkillUse {
	private static final Log _log = LogFactory.getLog(L1MobSkillUse.class);


	private L1MobSkill _mobSkillTemplate = null;

	private L1NpcInstance _attacker = null;

	private L1Character _target = null;

	private Random _rnd = new Random();

	private int _sleepTime = 0;

	private int _skillUseCount[];

	public L1MobSkillUse(L1NpcInstance npc) {
		_sleepTime = 0;

		_mobSkillTemplate = MobSkillTable.getInstance().getTemplate(
				npc.getNpcTemplate().get_npcId());
		if (_mobSkillTemplate == null) {
			return;
		}
		_attacker = npc;
		_skillUseCount = new int[getMobSkillTemplate().getSkillSize()];
	}

	private int getSkillUseCount(int idx) {
		return _skillUseCount[idx];
	}

	private void skillUseCountUp(int idx) {
		_skillUseCount[idx]++;
	}

	public void resetAllSkillUseCount() {
		if (getMobSkillTemplate() == null) {
			return;
		}

		for (int i = 0; i < getMobSkillTemplate().getSkillSize(); i++) {
			_skillUseCount[i] = 0;
		}
	}

	public int getSleepTime() {
		return _sleepTime;
	}

	public void setSleepTime(int i) {
		_sleepTime = i;
	}

	public L1MobSkill getMobSkillTemplate() {
		return _mobSkillTemplate;
	}

	/*
	 * 攻击 攻击可能true返。 攻击、false返、通常攻击行。
	 */
	public boolean skillUse(L1Character tg) {
		if (_mobSkillTemplate == null) {
			return false;
		}
		_target = tg;

		int type;
		type = getMobSkillTemplate().getType(0);

		if (type == L1MobSkill.TYPE_NONE) {
			return false;
		}

		int i = 0;
		for (i = 0; i < getMobSkillTemplate().getSkillSize()
				&& getMobSkillTemplate().getType(i) != L1MobSkill.TYPE_NONE; i++) {

			// changeTarget设定场合、入替
			int changeType = getMobSkillTemplate().getChangeTarget(i);
			if (changeType > 0) {
				_target = changeTarget(changeType, i);
			} else {
				// 设定场合本来
				_target = tg;
			}
			if (_target == null) {
				_target = tg;
			}
			if (_target == null) {
				return false;
			}

			if (isSkillUseble(i) == false) {
				continue;
			}

			type = getMobSkillTemplate().getType(i);
			//变更
			switch(type)
			{
				case 1: {//物理攻击
					if (physicalAttack(i) == true) {
						skillUseCountUp(i);
						return true;
					}
				}break;
				case 2: {//特殊物理攻击
					if (exceptionalphysicalAttack(i) == true) {
						skillUseCountUp(i);
						return true;
					}
				}break;
				case 3: {//有向魔法攻击
					if (magicAttack(i) == true) {
						skillUseCountUp(i);
						return true;
					}
				}break;
				case 4: {//无向魔法攻击
					if (magicnoneAttack(i) == true) {
						skillUseCountUp(i);
						return true;
					}
				}break;
				case 5: {//召唤
					if (summon(i) == true) {
						skillUseCountUp(i);
						return true;
					}
				}break;
				case 6: {//变身术
					if (poly(i) == true) {
						skillUseCountUp(i);
						return true;
					}
				}break;
				case 7: {//无延迟魔法
					if (magicnone(i) == true) {
						skillUseCountUp(i);
						return true;
					}
				}break;
			}
			//变更 end
		}

		return false;
	}

	private boolean summon(int idx) {
		L1SkillUse skillUse = new L1SkillUse();
		int skillid = getMobSkillTemplate().getSkillId(idx);
		int actid = getMobSkillTemplate().getActid(idx);
		boolean canUseSkill = false;

		int summonId = getMobSkillTemplate().getSummon(idx);
		int min = getMobSkillTemplate().getSummonMin(idx);
		int max = getMobSkillTemplate().getSummonMax(idx);
		int count = 0;

		if (summonId == 0) {
			return false;
		}

		// 障碍物判断 
		if (!_attacker.glanceCheck(_target.getX(), _target.getY())) {
			return false;
		}
		// 障碍物判断  end

		//施法动作 
		if (skillid > 0 && !_attacker.hasSkillEffect(64)) {//魔封状态判断 
			canUseSkill = skillUse.checkUseSkill(null, skillid,
					_target.getId(), _target.getX(), _target.getY(), null, 0,
					L1SkillUse.TYPE_NORMAL, _attacker);
		}

		if (canUseSkill == true) {
			if (getMobSkillTemplate().getLeverage(idx) > 0) {
				skillUse.setLeverage(getMobSkillTemplate().getLeverage(idx));
			}
			skillUse.handleCommands(null, skillid, _target.getId(), _target
					.getX(), _target.getX(), null, 0, L1SkillUse.TYPE_NORMAL,actid,
					_attacker);

			_sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
			count = _rnd.nextInt(max) + min;
			mobspawn(summonId, count);
			return true;
		}
		return false;
		//施法动作 
	}

	/*
	 * 15以内射线通PC指定强制变身。 对PC使。
	 */
	private boolean poly(int idx) {
		int polyId = getMobSkillTemplate().getPolyId(idx);
		boolean usePoly = false;

		if (polyId == 0) {
			return false;
		}

		for (L1PcInstance pc : L1World.getInstance()
				.getVisiblePlayer(_attacker)) {
			if (pc.isDead()) { // 死亡
				continue;
			}
			if (pc.isGhost()) {
				continue;
			}
			if (pc.isGmInvis()) {
				continue;
			}
			if (_attacker.glanceCheck(pc.getX(), pc.getY()) == false) {
				continue; // 射线通
			}

			int npcId = _attacker.getNpcTemplate().get_npcId();
			switch (npcId) {
			case 81082: // 场合
				pc.getInventory().takeoffEquip(945); // 牛polyId装备全部外。
				break;
			default:
				break;
			}
			L1PolyMorph.doPoly(pc, polyId, 1800);

			usePoly = true;
		}
		/*删除if (usePoly) {
			// 变身场合、柱表示。
			_attacker.broadcastPacket(new S_SkillSound(_attacker.getId(), 230));

			// 魔法使动作
			S_DoActionGFX gfx = new S_DoActionGFX(_attacker.getId(),
					ActionCodes.ACTION_SkillBuff);
			_attacker.broadcastPacket(gfx);

			_sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
		}删除*/

		return usePoly;
	}

	private boolean magicAttack(int idx) {
		L1SkillUse skillUse = new L1SkillUse();
		int skillid = getMobSkillTemplate().getSkillId(idx);
		int actid = getMobSkillTemplate().getActid(idx);
		boolean canUseSkill = false;

		// 魔封状态判断 
		if (_attacker.hasSkillEffect(64)) {
			return false;
		}
		// 魔封状态判断  end
		if (skillid > 0 && !_attacker.hasSkillEffect(skillid) && !_target.hasSkillEffect(skillid)) { // 魔法状态判断 
			canUseSkill = skillUse.checkUseSkill(null, skillid,
					_target.getId(), _target.getX(), _target.getY(), null, 0,
					L1SkillUse.TYPE_NORMAL, _attacker);
		}

		if (canUseSkill == true) {
			if (getMobSkillTemplate().getLeverage(idx) > 0) {
				skillUse.setLeverage(getMobSkillTemplate().getLeverage(idx));
			}
			skillUse.handleCommands(null, skillid, _target.getId(), _target
					.getX(), _target.getX(), null, 0, L1SkillUse.TYPE_NORMAL,actid,
					_attacker);
			// 使用sleepTime设定
			/*删除L1Skills skill = SkillsTable.getInstance().getTemplate(skillid);
			if (skill.getTarget().equals("attack") && skillid != 18) { // 有方向魔法
				_sleepTime = _attacker.getNpcTemplate().getAtkMagicSpeed();
			} else { // 无方向魔法
				_sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
			}删除*/
			// 加入延迟时间
				_sleepTime = _attacker.getNpcTemplate().getAtkMagicSpeed();
			// 加入延迟时间 end

			return true;
		}
		return false;
	}
	
	// 无向施法功能 
	private boolean magicnoneAttack(int idx) {
		L1SkillUse skillUse = new L1SkillUse();
		int skillid = getMobSkillTemplate().getSkillId(idx);
		int actid = getMobSkillTemplate().getActid(idx);
		boolean canUseSkill = false;

		// 魔封状态判断 
		if (_attacker.hasSkillEffect(64)) {
			return false;
		}
		// 魔封状态判断  end
		if (skillid > 0 && !_attacker.hasSkillEffect(skillid) && !_target.hasSkillEffect(skillid)) { // 魔法状态判断 
			canUseSkill = skillUse.checkUseSkill(null, skillid,
					_target.getId(), _target.getX(), _target.getY(), null, 0,
					L1SkillUse.TYPE_NORMAL, _attacker);
		}

		if (canUseSkill == true) {
			if (getMobSkillTemplate().getLeverage(idx) > 0) {
				skillUse.setLeverage(getMobSkillTemplate().getLeverage(idx));
			}
			skillUse.handleCommands(null, skillid, _target.getId(), _target
					.getX(), _target.getX(), null, 0, L1SkillUse.TYPE_NORMAL,actid,
					_attacker);
			// 使用sleepTime设定
			/*删除L1Skills skill = SkillsTable.getInstance().getTemplate(skillid);
			if (skill.getTarget().equals("attack") && skillid != 18) { // 有方向魔法
				_sleepTime = _attacker.getNpcTemplate().getAtkMagicSpeed();
			} else { // 无方向魔法
				_sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
			}删除*/
			// 加入延迟时间
				_sleepTime = _attacker.getNpcTemplate().getSubMagicSpeed();
			// 加入延迟时间 end

			return true;
		}
		return false;
	}
	// 无向施法功能  end

	// 无延迟魔法 
	private boolean magicnone(int idx) {
		L1SkillUse skillUse = new L1SkillUse();
		int skillid = getMobSkillTemplate().getSkillId(idx);
		int actid = getMobSkillTemplate().getActid(idx);
		boolean canUseSkill = false;

		if (skillid > 0 && !_attacker.hasSkillEffect(skillid)) { // 魔法状态判断 
			canUseSkill = skillUse.checkUseSkill(null, skillid,
					_target.getId(), _target.getX(), _target.getY(), null, 0,
					L1SkillUse.TYPE_NORMAL, _attacker);
		}

		if (canUseSkill == true) {
			if (getMobSkillTemplate().getLeverage(idx) > 0) {
				skillUse.setLeverage(getMobSkillTemplate().getLeverage(idx));
			}
			skillUse.handleCommands(null, skillid, _target.getId(), _target
					.getX(), _target.getX(), null, 0, L1SkillUse.TYPE_NORMAL,actid,
					_attacker);
			_sleepTime = 1;
			return true;
		}
		return false;
	}
	// 无延迟魔法  end

	/*
	 * 物理攻击
	 */
	private boolean physicalAttack(int idx) {
		Map<Integer, Integer> targetList = new ConcurrentHashMap<Integer, Integer>();
		int areaWidth = getMobSkillTemplate().getAreaWidth(idx);
		int areaHeight = getMobSkillTemplate().getAreaHeight(idx);
		int range = getMobSkillTemplate().getRange(idx);
		int actId = getMobSkillTemplate().getActid(idx);
		int gfxId = getMobSkillTemplate().getGfxid(idx);

		// 外
		if (_attacker.getLocation().getTileLineDistance(_target.getLocation()) > range) {
			return false;
		}

		// 障害物场合攻击不可能
		if (!_attacker.glanceCheck(_target.getX(), _target.getY())) {
			return false;
		}
		
		_attacker.setHeading(_attacker.targetDirection(_target.getX(), _target
				.getY())); // 向

		if (areaHeight > 0) {
			// 范围攻击
			ArrayList<L1Object> objs = L1World.getInstance()
					.getVisibleBoxObjects(_attacker, _attacker.getHeading(),
							areaWidth, areaHeight);

			for (L1Object obj : objs) {
				if (!(obj instanceof L1Character)) { // 以外场合何。
					continue;
				}

				L1Character cha = (L1Character) obj;
				if (cha.isDead()) { // 死对像外
					continue;
				}

				// 状态对像外
				if (cha instanceof L1PcInstance) {
					if (((L1PcInstance) cha).isGhost()) {
						continue;
					}
				}
				
				// 障害物场合对像外
				if (!_attacker.glanceCheck(cha.getX(), cha.getY())) {
					continue;
				}
				
				if (_target instanceof L1PcInstance
						|| _target instanceof L1SummonInstance
						|| _target instanceof L1PetInstance) {
					// 对PC
					if (obj instanceof L1PcInstance
							&& !((L1PcInstance) obj).isGhost()
							&& !((L1PcInstance) obj).isGmInvis()
							|| obj instanceof L1SummonInstance
							|| obj instanceof L1PetInstance) {
						targetList.put(obj.getId(), 0);
					}
				} else {
					// 对NPC
					if (obj instanceof L1MonsterInstance) {
						targetList.put(obj.getId(), 0);
					}
				}
			}
		} else {
			// 单体攻击
			targetList.put(_target.getId(), 0); // 追加
		}

		if (targetList.size() == 0) {
			return false;
		}

		Iterator<Integer> ite = targetList.keySet().iterator();
		while (ite.hasNext()) {
			int targetId = ite.next();
			L1Attack attack = new L1Attack(_attacker, (L1Character) L1World
					.getInstance().findObject(targetId));
			if (attack.calcHit()) {
				if (getMobSkillTemplate().getLeverage(idx) > 0) {
					attack.setLeverage(getMobSkillTemplate().getLeverage(idx));
				}
				attack.calcDamage();
			}
			if (actId > 0) {
				attack.setActId(actId);
			}
			// 攻击实际对行
			if (targetId == _target.getId()) {
				if (gfxId > 0) {
					_attacker.broadcastPacket(new S_SkillSound(_attacker
							.getId(), gfxId));
				}
				attack.action();
			}
			attack.commit();
		}

		_sleepTime = _attacker.getAtkspeed();
		return true;
	}
	
	//特殊物理攻击  end
	private boolean exceptionalphysicalAttack(int idx) {
		Map<Integer, Integer> targetList = new ConcurrentHashMap<Integer, Integer>();
		int areaWidth = getMobSkillTemplate().getAreaWidth(idx);
		int areaHeight = getMobSkillTemplate().getAreaHeight(idx);
		int range = getMobSkillTemplate().getRange(idx);
		int actId = getMobSkillTemplate().getActid(idx);
		int gfxId = getMobSkillTemplate().getGfxid(idx);

		// 障害物场合攻击不可能
		if (!_attacker.glanceCheck(_target.getX(), _target.getY())) {
			return false;
		}
		// 外
		if (_attacker.getLocation().getTileLineDistance(_target.getLocation()) > range) {
			return false;
		}

		_attacker.setHeading(_attacker.targetDirection(_target.getX(),
				_target.getY())); // 向

		if (areaHeight > 0) {
			// 范围攻击
			ArrayList<L1Object> objs = L1World.getInstance()
					.getVisibleBoxObjects(_attacker, _attacker.getHeading(),
							areaWidth, areaHeight);

			for (L1Object obj : objs) {
				if (!(obj instanceof L1Character)) { // 以外场合何。
					continue;
				}

				L1Character cha = (L1Character) obj;
				if (cha.isDead()) { // 死对像外
					continue;
				}

				// 状态对像外
				if (cha instanceof L1PcInstance) {
					if (((L1PcInstance) cha).isGhost()) {
						continue;
					}
				}
				
				if (_target instanceof L1PcInstance
						|| _target instanceof L1SummonInstance
						|| _target instanceof L1PetInstance) {
					// 对PC
					if (obj instanceof L1PcInstance
							&& !((L1PcInstance) obj).isGhost()
							&& !((L1PcInstance) obj).isGmInvis()
							|| obj instanceof L1SummonInstance
							|| obj instanceof L1PetInstance) {
						targetList.put(obj.getId(), 0);
					}
				} else {
					// 对NPC
					if (obj instanceof L1MonsterInstance) {
						targetList.put(obj.getId(), 0);
					}
				}
			}
		} else {
			// 单体攻击
			targetList.put(_target.getId(), 0); // 追加
		}

		if (targetList.size() == 0) {
			return false;
		}

		Iterator<Integer> ite = targetList.keySet().iterator();
		while (ite.hasNext()) {
			int targetId = ite.next();
			L1Attack attack = new L1Attack(_attacker, (L1Character) L1World
					.getInstance().findObject(targetId));
			if (attack.calcHit()) {
				if (getMobSkillTemplate().getLeverage(idx) > 0) {
					attack.setLeverage(getMobSkillTemplate().getLeverage(idx));
				}
				attack.calcDamage();
			}
			if (actId > 0) {
				attack.setActId(actId);
			}
			// 攻击实际对行
			if (targetId == _target.getId()) {
				if (gfxId > 0) {
					_attacker.broadcastPacket(new S_SkillSound(_attacker
							.getId(), gfxId));
				}
				attack.action();
			}
			attack.commit();
		}

		_sleepTime = _attacker.getAtkexspeed();
		return true;
	}
	//特殊物理攻击  end

	/*
	 * 条件
	 */
	private boolean isSkillUseble(int skillIdx) {
		boolean useble = false;

		if (getMobSkillTemplate().getTriggerRandom(skillIdx) > 0) {
			int chance = _rnd.nextInt(100) + 1;
			if (chance < getMobSkillTemplate().getTriggerRandom(skillIdx)) {
				useble = true;
			} else {
				return false;
			}
		}

		if (getMobSkillTemplate().getTriggerHp(skillIdx) > 0) {
			int hpRatio = (_attacker.getCurrentHp() * 100)
					/ _attacker.getMaxHp();
			if (hpRatio <= getMobSkillTemplate().getTriggerHp(skillIdx)) {
				useble = true;
			} else {
				return false;
			}
		}
		
		if (getMobSkillTemplate().getTriggerCompanionHp(skillIdx) > 0) {
			L1NpcInstance companionNpc = searchMinCompanionHp();
			if (companionNpc == null) {
				return false;
			}

			int hpRatio = (companionNpc.getCurrentHp() * 100)
					/ companionNpc.getMaxHp();
			if (hpRatio <= getMobSkillTemplate()
					.getTriggerCompanionHp(skillIdx)) {
				useble = true;
				_target = companionNpc; // 入替
			} else {
				return false;
			}
		}

		if (getMobSkillTemplate().getTriggerRange(skillIdx) != 0) {
			int distance = _attacker.getLocation().getTileLineDistance(
					_target.getLocation());

			if (getMobSkillTemplate().isTriggerDistance(skillIdx, distance)) {
				useble = true;
			} else {
				return false;
			}
		}

		if (getMobSkillTemplate().getTriggerCount(skillIdx) > 0) {
			if (getSkillUseCount(skillIdx) < getMobSkillTemplate()
					.getTriggerCount(skillIdx)) {
				useble = true;
			} else {
				return false;
			}
		}
		return useble;
	}
	
	private L1NpcInstance searchMinCompanionHp() {
		L1NpcInstance npc;
		L1NpcInstance minHpNpc = null;
		int hpRatio = 100;
		int companionHpRatio;
		int family = _attacker.getNpcTemplate().get_family();

		for (L1Object object : L1World.getInstance().getVisibleObjects(
				_attacker)) {
			if (object instanceof L1NpcInstance) {
				npc = (L1NpcInstance) object;
				if (npc.getNpcTemplate().get_family() == family && (npc.getCurrentHp() > 0)) {//加上怪物死亡判定 
					companionHpRatio = (npc.getCurrentHp() * 100)
							/ npc.getMaxHp();
					if (companionHpRatio < hpRatio) {
						hpRatio = companionHpRatio;
						minHpNpc = npc;
					}
				}
			}
		}
		return minHpNpc;
	}

	private void mobspawn(int summonId, int count) {
		int i;

		for (i = 0; i < count; i++) {
			mobspawn(summonId);
		}
	}

	private void mobspawn(int summonId) {
				try {
					final L1NpcInstance mob = NpcTable.getInstance().newNpcInstance(summonId);
					if (mob == null) {
						_log.info("NPC召唤技能 目标NPCID设置异常 异常编号: " + summonId);
						return;
					}
					mob.setId(IdFactory.getInstance().nextId());
					L1Location loc = _attacker.getLocation().randomLocation(8,
							false);
					int heading = _rnd.nextInt(8);
					mob.setX(loc.getX());
					mob.setY(loc.getY());
					mob.setHomeX(loc.getX());
					mob.setHomeY(loc.getY());
					short mapid = _attacker.getMapId();
					mob.setMap(mapid);
					mob.setHeading(heading);
					mob.setTu(true);
					L1World.getInstance().storeWorldObject(mob);
					L1World.getInstance().addVisibleObject(mob);
/*					L1Object object = L1World.getInstance().findObject(
							mob.getId());*/
					L1MonsterInstance newnpc = (L1MonsterInstance) mob;
					newnpc.set_storeDroped(true); // 召唤无
					if (summonId == 45061 // 
							|| summonId == 45161 // 
							|| summonId == 45181 // 
							|| summonId == 45455) { // 
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

	// 现在ChangeTarget有效值2,3
	private L1Character changeTarget(int type, int idx) {
		L1Character target;

		switch (type) {
		case L1MobSkill.CHANGE_TARGET_ME:
			target = _attacker;
			break;
		case L1MobSkill.CHANGE_TARGET_RANDOM:
			// 候补选定
			List<L1Character> targetList = new ArrayList<L1Character>();
			for (L1Object obj : L1World.getInstance().getVisibleObjects(
					_attacker)) {
				if (obj instanceof L1PcInstance || obj instanceof L1PetInstance
						|| obj instanceof L1SummonInstance) {
					L1Character cha = (L1Character) obj;

					int distance = _attacker.getLocation().getTileLineDistance(
							cha.getLocation());

					// 发动范围外对像外
					if (!getMobSkillTemplate().isTriggerDistance(idx, distance)) {
						continue;
					}
					// 障害物场合对像外
					if (!_attacker.glanceCheck(cha.getX(), cha.getY())) {
						continue;
					}
					if (!_attacker.getHateList().containsKey(cha)) { // 场合对像外
						continue;
					}

					if (cha.isDead()) { // 死对像外
						continue;
					}

					// 状态对像外
					if (cha instanceof L1PcInstance) {
						if (((L1PcInstance) cha).isGhost()) {
							continue;
						}
					}
					targetList.add((L1Character) obj);
				}
			}

			if (targetList.size() == 0) {
				target = _target;
			} else {
				int randomSize = targetList.size() * 100;
				int targetIndex = _rnd.nextInt(randomSize) / 100;
				target = targetList.get(targetIndex);
			}
			break;

		default:
			target = _target;
			break;
		}
		return target;
	}
}
