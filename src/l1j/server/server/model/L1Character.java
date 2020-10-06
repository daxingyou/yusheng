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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l1j.server.server.model.Instance.L1BabyInstance;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.poison.L1Poison;
import l1j.server.server.model.poison.L1Poison2;
import l1j.server.server.model.poison.L1Poison3;
import l1j.server.server.model.poison.L1Poison4;
import l1j.server.server.model.poison.L1Poison5;
import l1j.server.server.model.poison.L1Poison6;
import l1j.server.server.model.poison.L1Poison7;
import l1j.server.server.model.poison.L1Poison8;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillTimer;
import l1j.server.server.model.skill.L1SkillTimerCreator;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.types.Point;
import l1j.server.server.utils.IntRange;
import l1j.server.server.world.L1World;

// Referenced classes of package l1j.server.server.model:
// L1Object, Die, L1PcInstance, L1MonsterInstance,
// L1World, ActionFailed

public class L1Character extends L1Object {

	private static final long serialVersionUID = 1L;


	private L1Poison _poison = null;
	private boolean _paralyzed;
	private boolean _sleeped;

	private final Map<Integer, L1NpcInstance> _petlist = new HashMap<Integer, L1NpcInstance>();
	private final Map<Integer, L1DollInstance> _dolllist = new HashMap<Integer, L1DollInstance>();
	private final Map<Integer, L1SkillTimer> _skillEffect = new HashMap<Integer, L1SkillTimer>();
	private final Map<Integer, L1ItemDelay.ItemDelayTimer> _itemdelay = new HashMap<Integer, L1ItemDelay.ItemDelayTimer>();

	public L1Character() {
		_level = 1;
	}

	/**
	 * 复活。
	 * 
	 * @param hp
	 *            复活后HP
	 */
	public void resurrect(int hp) {
		if (!isDead()) {
			return;
		}
		setCurrentHp(hp);
		setDead(false);
		setStatus(0);
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.sendPackets(new S_RemoveObject(this));
			pc.removeKnownObject(this);
			pc.updateObject();
		}
	}

	private int _currentHp;

	/**
	 * 现在HP返。
	 * 
	 * @return 现在HP
	 */
	public int getCurrentHp() {
		return _currentHp;
	}

	/**
	 * HP设定。
	 * 
	 * @param i
	 *            新HP
	 */
	// 特殊理场合（送信等）
	public void setCurrentHp(int i) {
		_currentHp = i;
		if (_currentHp >= getMaxHp()) {
			_currentHp = getMaxHp();
		}
	}

	/**
	 * HP设定。
	 * 
	 * @param i
	 *            新HP
	 */
	public void setCurrentHpDirect(int i) {
		_currentHp = i;
	}

	private int _currentMp;

	/**
	 * 现在MP返。
	 * 
	 * @return 现在MP
	 */
	public int getCurrentMp() {
		return _currentMp;
	}

	/**
	 * MP设定。
	 * 
	 * @param i
	 *            新MP
	 */
	// 特殊理场合（送信等）
	public void setCurrentMp(int i) {
		_currentMp = i;
		if (_currentMp >= getMaxMp()) {
			_currentMp = getMaxMp();
		}
	}

	/**
	 * MP设定。
	 * 
	 * @param i
	 *            新MP
	 */
	public void setCurrentMpDirect(int i) {
		_currentMp = i;
	}

	/**
	 * 眠态返。
	 * 
	 * @return 眠态表。眠态true。
	 */
	public boolean isSleeped() {
		return _sleeped;
	}

	/**
	 * 眠态设定。
	 * 
	 * @param sleeped
	 *            眠态表。眠态true。
	 */
	public void setSleeped(boolean sleeped) {
		_sleeped = sleeped;
	}

	/**
	 * 麻痹态返。
	 * 
	 * @return 麻痹态表。麻痹态true。
	 */
	public boolean isParalyzed() {
		return _paralyzed;
	}

	/**
	 * 麻痹态设定。
	 * 
	 * @param i
	 *            麻痹态表。麻痹态true。
	 */
	public void setParalyzed(boolean paralyzed) {
		_paralyzed = paralyzed;
	}

	L1Paralysis _paralysis;

	public L1Paralysis getParalysis() {
		return _paralysis;
	}

	public void setParalaysis(L1Paralysis p) {
		_paralysis = p;
	}

	public void cureParalaysis() {
		if (_paralysis != null) {
			_paralysis.cure();
		}
	}

	/**
	 * 可视范居、送信。
	 * 
	 * @param packet
	 *            送信表ServerBasePacket。
	 */
	public void broadcastPacket(ServerBasePacket packet) {
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.sendPackets(packet);
		}
	}

	/**
	 * 可视范居、送信。 面送信。
	 * 
	 * @param packet
	 *            送信表ServerBasePacket。
	 */
	public void broadcastPacketExceptTargetSight(ServerBasePacket packet,
			L1Character target) {
		for (L1PcInstance pc : L1World.getInstance()
				.getVisiblePlayerExceptTargetSight(this, target)) {
			pc.sendPackets(packet);
		}
	}

	/**
	 * 正面座标返。
	 * 
	 * @return 正面座标
	 */
	public int[] getFrontLoc() {
		int[] loc = new int[2];
		int x = getX();
		int y = getY();
		int heading = getHeading();
		if (heading == 0) {
			y--;
		} else if (heading == 1) {
			x++;
			y--;
		} else if (heading == 2) {
			x++;
		} else if (heading == 3) {
			x++;
			y++;
		} else if (heading == 4) {
			y++;
		} else if (heading == 5) {
			x--;
			y++;
		} else if (heading == 6) {
			x--;
		} else if (heading == 7) {
			x--;
			y--;
		}
		loc[0] = x;
		loc[1] = y;
		return loc;
	}

	/**
	 * 指定座标方向返。
	 * 
	 * @param tx
	 *            座标X
	 * @param ty
	 *            座标Y
	 * @return 指定座标方向
	 */
	public int targetDirection(int tx, int ty) {
		final float dis_x = Math.abs(this.getX() - tx); // X點方向距離
		final float dis_y = Math.abs(this.getY() - ty); // Y點方向距離
		final float dis = Math.max(dis_x, dis_y); // 取回2者最大質
		if (dis == 0) {
			return this.getHeading(); // 距離為0表示不須改變面向
		}
		final int avg_x = (int) Math.floor((dis_x / dis) + 0.59f); // 上下左右がちょっと優先な丸め
		final int avg_y = (int) Math.floor((dis_y / dis) + 0.59f); // 上下左右がちょっと優先な丸め

		int dir_x = 0;
		int dir_y = 0;
		if (this.getX() < tx) {
			dir_x = 1;
		}
		if (this.getX() > tx) {
			dir_x = -1;
		}
		if (this.getY() < ty) {
			dir_y = 1;
		}
		if (this.getY() > ty) {
			dir_y = -1;
		}

		if (avg_x == 0) {
			dir_x = 0;
		}
		if (avg_y == 0) {
			dir_y = 0;
		}

		switch (dir_x) {
		case -1:
			switch (dir_y) {
			case -1:
				return 7; // 左
			case 0:
				return 6; // 左下
			case 1:
				return 5; // 下
			}
			break;
		case 0:
			switch (dir_y) {
			case -1:
				return 0; // 左上
			case 1:
				return 4; // 右下
			}
			break;
		case 1:
			switch (dir_y) {
			case -1:
				return 1; // 上
			case 0:
				return 2; // 右上
			case 1:
				return 3; // 右
			}
			break;
		}
		return getHeading(); // 。
	}

	/**
	 * 指定座标直线上、障害物存在**返。
	 * 
	 * @param tx
	 *            座标X
	 * @param ty
	 *            座标Y
	 * @return 障害物无true、false返。
	 */
	public boolean glanceCheck(int tx, int ty)
	  {
	    L1Map map = getMap();
	    int chx = getX();
	    int chy = getY();

	    for (int i = 0; i < 15; i++) {
	      if (((chx == tx) && (chy == ty)) || ((chx + 1 == tx) && (chy - 1 == ty)) || 
	        ((chx + 1 == tx) && (chy == ty)) || 
	        ((chx + 1 == tx) && (chy + 1 == ty)) || 
	        ((chx == tx) && (chy + 1 == ty)) || 
	        ((chx - 1 == tx) && (chy + 1 == ty)) || 
	        ((chx - 1 == tx) && (chy == ty)) || 
	        ((chx - 1 == tx) && (chy - 1 == ty)) || (
	        (chx == tx) && (chy - 1 == ty)))
	      {
	        break;
	      }
	      int th = targetDirection(tx, ty);
	      if (!map.isArrowPassable(chx, chy, th)) {
	        return false;
	      }
	      if (chx < tx) {
	        if (chy == ty) {
	          chx++;
	        }
	        else if (chy > ty) {
	          chx++;
	          chy--;
	        }
	        else if (chy < ty) {
	          chx++;
	          chy++;
	        }

	      }
	      else if (chx == tx) {
	        if (chy < ty) {
	          chy++;
	        }
	        else if (chy > ty) {
	          chy--;
	        }
	      }
	      else if (chx > tx) {
	        if (chy == ty) {
	          chx--;
	        }
	        else if (chy < ty) {
	          chx--;
	          chy++;
	        }
	        else if (chy > ty) {
	          chx--;
	          chy--;
	        }
	      }
	    }
	    return true;
	  }
	
	/**
	 * 指定座标攻可能返。
	 * 
	 * @param x
	 *            座标X。
	 * @param y
	 *            座标Y。
	 * @param range
	 *            攻可能范()
	 * @return 攻可能true,不可能false
	 */
	public boolean isAttackPosition(int x, int y, int range) {
		if (range >= 7) // 远隔武器（７以上场合斜考虑面外出)
		{
			if (getLocation().getTileDistance(new Point(x, y)) > range) {
				return false;
			}
		} else // 近接武器
		{
			if (getLocation().getTileLineDistance(new Point(x, y)) > range) {
				return false;
			}
		}
		return glanceCheck(x, y);
	}

	/**
	 * 返。
	 * 
	 * @return 表、L1Inventory。
	 */
	public L1Inventory getInventory() {
		return null;
	}

	/**
	 * 、新佅果追加。
	 * 
	 * @param skillId
	 *            追加佅果ID。
	 * @param timeMillis
	 *            追加佅果持时间。无限场合0。
	 */
	private void addSkillEffect(int skillId, int timeMillis) {
		L1SkillTimer timer = null;
		if (0 < timeMillis) {
			timer = L1SkillTimerCreator.create(this, skillId, timeMillis);
			timer.begin();
		}
		_skillEffect.put(skillId, timer);
	}

	/**
	 * 、佅果设定。<br>
	 * 重复场合、新佅果追加。<br>
	 * 重复场合、佅果时间佅果时间长方优先设定。
	 * 
	 * @param skillId
	 *            设定佅果ID。
	 * @param timeMillis
	 *            设定佅果持时间。无限场合0。
	 */
	public void setSkillEffect(int skillId, int timeMillis) {
		if (hasSkillEffect(skillId)) {
			int remainingTimeMills = getSkillEffectTimeSec(skillId) * 1000;

			// 时间有限、佅果时间方长无限场合上书。
			if (remainingTimeMills >= 0
					&& (remainingTimeMills < timeMillis || timeMillis == 0)) {
				killSkillEffectTimer(skillId);
				addSkillEffect(skillId, timeMillis);
			}
		} else {
			addSkillEffect(skillId, timeMillis);
		}
	}

	/**
	 * 、佅果削除。
	 * 
	 * @param skillId
	 *            削除佅果ID
	 */
	public void removeSkillEffect(int skillId) {
		L1SkillTimer timer = _skillEffect.remove(skillId);
		if (timer != null) {
			timer.end();
		}
	}

	/**
	 * 、佅果削除。 佅果削除。
	 * 
	 * @param skillId
	 *            削除ＩＤ
	 */
	public void killSkillEffectTimer(int skillId) {
		L1SkillTimer timer = _skillEffect.remove(skillId);
		if (timer != null) {
			timer.kill();
		}
	}

	/**
	 * 、全佅果削除。佅果削除。
	 */
	public void clearSkillEffectTimer() {
		for (L1SkillTimer timer : _skillEffect.values()) {
			if (timer != null) {
				timer.kill();
			}
		}
		_skillEffect.clear();
	}

	/**
	 * 、佅果挂返。
	 * 
	 * @param skillId
	 *            调佅果ID。
	 * @return 魔法佅果true、false。
	 */
	public boolean hasSkillEffect(int skillId) {
		return _skillEffect.containsKey(skillId);
	}

	/**
	 * 佅果持时间返。
	 * 
	 * @param skillId
	 *            调佅果ID
	 * @return 佅果时间(秒)。佅果时间无限场合、-1。
	 */
	public int getSkillEffectTimeSec(int skillId) {
		L1SkillTimer timer = _skillEffect.get(skillId);
		if (timer == null) {
			return -1;
		}
		return timer.getRemainingTime();
	}

	private boolean _isSkillDelay = false;

	/**
	 * 、追加。
	 * 
	 * @param flag
	 */
	public void setSkillDelay(boolean flag) {
		_isSkillDelay = flag;
	}

	/**
	 * 毒态返。
	 * 
	 * @return 中。
	 */
	public boolean isSkillDelay() {
		return _isSkillDelay;
	}

	/**
	 * 、追加。
	 * 
	 * @param delayId
	 *            ID。 通常0、 、  1。
	 * @param timer
	 *            时间表、L1ItemDelay.ItemDelayTimer。
	 */
	public void addItemDelay(int delayId, L1ItemDelay.ItemDelayTimer timer) {
		_itemdelay.put(delayId, timer);
	}

	/**
	 * 、削除。
	 * 
	 * @param delayId
	 *            ID。 通常0、 、  1。
	 */
	public void removeItemDelay(int delayId) {
		_itemdelay.remove(delayId);
	}

	/**
	 * 、返。
	 * 
	 * @param delayId
	 *            调ID。 通常0、 、 
	 *            1。
	 * @return true、false。
	 */
	public boolean hasItemDelay(int delayId) {
		return _itemdelay.containsKey(delayId);
	}

	/**
	 * 时间表、L1ItemDelay.ItemDelayTimer返。
	 * 
	 * @param delayId
	 *            调ID。 通常0、 、 
	 *            1。
	 * @return 时间表、L1ItemDelay.ItemDelayTimer。
	 */
	public L1ItemDelay.ItemDelayTimer getItemDelayTimer(int delayId) {
		return _itemdelay.get(delayId);
	}

	/**
	 * 、新、、、追加。
	 * 
	 * @param npc
	 *            追加Npc表、L1NpcInstance。
	 */
	public void addPet(L1NpcInstance npc) {
		_petlist.put(npc.getId(), npc);
		if (!(npc instanceof L1BabyInstance)) {
			sendPetCtrlMenu(npc, true);
		}	
	}

	/**
	 * 、、、、削除。
	 * 
	 * @param npc
	 *            削除Npc表、L1NpcInstance。
	 */
	public void removePet(L1NpcInstance npc) {
		_petlist.remove(npc.getId());
		if (!(npc instanceof L1BabyInstance)) {
			sendPetCtrlMenu(npc, false);
		}
	}

	/**
	 * 返。
	 * 
	 * @return 表、HashMap。KeyID、ValueL1NpcInstance。
	 */
	public Map<Integer, L1NpcInstance> getPetList() {
		return _petlist;
	}
	
	private final void sendPetCtrlMenu(L1NpcInstance npc, boolean type) {
		if (npc instanceof L1PetInstance) {
			L1PetInstance pet = (L1PetInstance) npc;
			L1Character cha = pet.getMaster();

			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
//				pc.sendPackets(new S_PetCtrlMenu(pc, pet, type));

				if (type) {
					pc.sendPackets(new S_HPMeter(pet));
				}
			}

		} else if (npc instanceof L1SummonInstance) {
			L1SummonInstance summon = (L1SummonInstance) npc;
			L1Character cha = summon.getMaster();

			if (cha instanceof L1PcInstance) {
				L1PcInstance pc = (L1PcInstance) cha;
//				pc.sendPackets(new S_PetCtrlMenu(pc, summon, type));

				if (type) {
					pc.sendPackets(new S_HPMeter(summon));
				}
			}
		}
	}

	/**
	 * 追加。
	 * 
	 * @param doll
	 *            追加doll表、L1DollInstance。
	 */
	public void addDoll(L1DollInstance doll) {
		_dolllist.put(doll.getId(), doll);
	}

	/**
	 * 削除。
	 * 
	 * @param doll
	 *            削除doll表、L1DollInstance。
	 */
	public void removeDoll(L1DollInstance doll) {
		_dolllist.remove(doll.getId());
	}

	/**
	 * 返。
	 * 
	 * @return 魔法人形表、HashMap。KeyID、ValueL1DollInstance。
	 */
	public Map<Integer, L1DollInstance> getDollList() {
		return _dolllist;
	}

	/**
	 * 、毒追加。
	 * 
	 * @param poison
	 *            毒表、L1Poison。
	 */
	public void setPoison(L1Poison poison) {
		_poison = poison;
	}

	/**
	 * 毒治疗。
	 */
	public void curePoison() {
		if (_poison == null) {
			return;
		}
		_poison.cure();
	}

	/**
	 * 毒态返。
	 * 
	 * @return 毒表、L1Poison。
	 */
	public L1Poison getPoison() {
		return _poison;
	}

	/**
	 * 毒付加
	 * 
	 * @param effectId
	 * @see S_Poison#S_Poison(int, int)
	 */
	public void setPoisonEffect(int effectId) {
		broadcastPacket(new S_Poison(getId(), effectId));
	}

	/**
	 * 存在座标、返。
	 * 
	 * @return 座标表。1、-1、0。
	 */
	public int getZoneType() {
		if (getMap().isSafetyZone(getLocation())) {
			return 1;
		} else if (getMap().isCombatZone(getLocation())) {
			return -1;
		} else { // 
			return 0;
		}
	}

	private long _exp; // ● 

	/**
	 * 保持返。
	 * 
	 * @return 。
	 */
	public long getExp() {
		return _exp;
	}

	/**
	 * 保持设定。
	 * 
	 * @param exp
	 *            。
	 */
	public void setExp(long exp) {
		_exp = exp;
	}

	// ■■■■■■■■■■ L1PcInstance移动 ■■■■■■■■■■
	private final ConcurrentHashMap<Integer,L1Object> _knownObjects = new ConcurrentHashMap<Integer,L1Object>();
	private final ConcurrentHashMap<Integer,L1PcInstance> _knownPlayer = new ConcurrentHashMap<Integer,L1PcInstance>();

	/**
	 * 指定、认识返。
	 * 
	 * @param obj
	 *            调。
	 * @return 认识true、false。 自分自身false返。
	 */
	public boolean knownsObject(L1Object obj) {
		return _knownObjects.containsKey(obj.getId());
	}
	
	public L1Object getknownsObject(int objid){
		return _knownObjects.get(objid);
	}

	/**
	 * 认识全返。
	 * 
	 * @return 认识表L1Object格纳ArrayList。
	 */
	public Collection<L1Object> getKnownObjects() {
		return _knownObjects.values();
	}

	/**
	 * 认识全返。
	 * 
	 * @return 认识表L1PcInstance格纳ArrayList。
	 */
	public Collection<L1PcInstance> getKnownPlayers() {
		return _knownPlayer.values();
	}

	/**
	 * 、新认识追加。
	 * 
	 * @param obj
	 *            新认识。
	 */
	public void addKnownObject(L1Object obj) {
		_knownObjects.put(obj.getId(),obj);
		if (obj instanceof L1PcInstance) {
			_knownPlayer.put(obj.getId(),(L1PcInstance) obj);
		}
	}

	/**
	 * 、认识削除。
	 * 
	 * @param obj
	 *            削除。
	 */
	public void removeKnownObject(L1Object obj) {
		_knownObjects.remove(obj.getId());
		if (obj instanceof L1PcInstance) {
			_knownPlayer.remove(obj.getId());
		}
	}

	/**
	 * 、全认识削除。
	 */
	public void removeAllKnownObjects() {
		_knownObjects.clear();
		_knownPlayer.clear();
	}

	// ■■■■■■■■■■  ■■■■■■■■■■

	private String _name; // ● 名前

	public String getName() {
		return _name;
	}

	public void setName(String s) {
		_name = s;
	}
	
/*	private String _sjname;
	
	public String getSjName()
	{
		return _sjname;
	}
	
	public void setSjName(String name){
		_sjname = name;
	}*/

	private int _level; // ● 

	public synchronized int getLevel() {
		return _level;
	}

	public synchronized void setLevel(long level) {
		_level = (int) level;
	}

	private int _maxHp = 0; // ● ＭＡＸＨＰ（1～32767）
	private int _trueMaxHp = 0; // ● 本ＭＡＸＨＰ

	public int getMaxHp() {
		return _maxHp;
	}

	public void setMaxHp(int hp) {
		_trueMaxHp = hp;
		_maxHp = IntRange.ensure(_trueMaxHp, 1, Integer.MAX_VALUE);
		_currentHp = Math.min(_currentHp, _maxHp);
	}

	public void addMaxHp(int i) {
		setMaxHp(_trueMaxHp + i);
	}

	private short _maxMp = 0; // ● ＭＡＸＭＰ（0～32767）
	private int _trueMaxMp = 0; // ● 本ＭＡＸＭＰ

	public short getMaxMp() {
		return _maxMp;
	}

	public void setMaxMp(int mp) {
		_trueMaxMp = mp;
		_maxMp = (short) IntRange.ensure(_trueMaxMp, 0, 32767);
		_currentMp = Math.min(_currentMp, _maxMp);
	}

	public void addMaxMp(int i) {
		setMaxMp(_trueMaxMp + i);
	}

	private int _ac = 0; // ● ＡＣ（-255～255）
	private int _trueAc = 0; // ● 本ＡＣ
	private int _armorAc = 0;//防具的防御力
	
	public void setArmorAc(int i)
	{
		_armorAc = IntRange.ensure(i, -255, 255);	    
	}
	
	public void addArmorAc(int i) {
		setArmorAc(_armorAc + i);
	}
	
	public int getArmorAc() {
		return _armorAc;
	}

	public int getAc() {
		return _ac + _armorAc;
	}

	public void setAc(int i) {
		_trueAc = i;
		_ac = IntRange.ensure(i, -255, 255);
	}

	public void addAc(int i) {
		setAc(_trueAc + i);
	}

	private byte _str = 0; // ● ＳＴＲ（1～127）
	private short _trueStr = 0; // ● 本ＳＴＲ

	public byte getStr() {
		return _str;
	}

	public void setStr(int i) {
		_trueStr = (short) i;
		_str = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addStr(int i) {
		setStr(_trueStr + i);
	}

	private byte _con = 0; // ● ＣＯＮ（1～127）
	private short _trueCon = 0; // ● 本ＣＯＮ

	public byte getCon() {
		return _con;
	}

	public void setCon(int i) {
		_trueCon = (short) i;
		_con = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addCon(int i) {
		setCon(_trueCon + i);
	}

	private byte _dex = 0; // ● ＤＥＸ（1～127）
	private short _trueDex = 0; // ● 本ＤＥＸ

	public byte getDex() {
		return _dex;
	}

	public void setDex(int i) {
		_trueDex = (short) i;
		_dex = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addDex(int i) {
		setDex(_trueDex + i);
	}

	private byte _cha = 0; // ● ＣＨＡ（1～127）
	private short _trueCha = 0; // ● 本ＣＨＡ

	public byte getCha() {
		return _cha;
	}

	public void setCha(int i) {
		_trueCha = (short) i;
		_cha = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addCha(int i) {
		setCha(_trueCha + i);
	}

	private byte _int = 0; // ● ＩＮＴ（1～127）
	private short _trueInt = 0; // ● 本ＩＮＴ

	public byte getInt() {
		return _int;
	}

	public void setInt(int i) {
		_trueInt = (short) i;
		_int = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addInt(int i) {
		setInt(_trueInt + i);
	}

	private byte _wis = 0; // ● ＷＩＳ（1～127）
	private short _trueWis = 0; // ● 本ＷＩＳ

	public byte getWis() {
		return _wis;
	}

	public void setWis(int i) {
		_trueWis = (short) i;
		_wis = (byte) IntRange.ensure(i, 1, 127);
	}

	public void addWis(int i) {
		setWis(_trueWis + i);
	}

	private int _wind = 0; // ● 风防御（-128～127）
	private int _trueWind = 0; // ● 本风防御

	public int getWind() {
		return _wind;
	} // 使用

	public void addWind(int i) {
		_trueWind += i;
		if (_trueWind >= 127) {
			_wind = 127;
		} else if (_trueWind <= -128) {
			_wind = -128;
		} else {
			_wind = _trueWind;
		}
	}

	private int _water = 0; // ● 水防御（-128～127）
	private int _trueWater = 0; // ● 本水防御

	public int getWater() {
		return _water;
	} // 使用

	public void addWater(int i) {
		_trueWater += i;
		if (_trueWater >= 127) {
			_water = 127;
		} else if (_trueWater <= -128) {
			_water = -128;
		} else {
			_water = _trueWater;
		}
	}

	private int _fire = 0; // ● 火防御（-128～127）
	private int _trueFire = 0; // ● 本火防御

	public int getFire() {
		return _fire;
	} // 使用

	public void addFire(int i) {
		_trueFire += i;
		if (_trueFire >= 127) {
			_fire = 127;
		} else if (_trueFire <= -128) {
			_fire = -128;
		} else {
			_fire = _trueFire;
		}
	}

	private int _earth = 0; // ● 地防御（-128～127）
	private int _trueEarth = 0; // ● 本地防御

	public int getEarth() {
		return _earth;
	} // 使用

	public void addEarth(int i) {
		_trueEarth += i;
		if (_trueEarth >= 127) {
			_earth = 127;
		} else if (_trueEarth <= -128) {
			_earth = -128;
		} else {
			_earth = _trueEarth;
		}
	}

	// 耐性
	private int _registStan = 0;
	private int _trueRegistStan = 0;

	public int getRegistStan() {
		return _registStan;
	} // 使用

	public void addRegistStan(int i) {
		_trueRegistStan += i;
		if (_trueRegistStan > 127) {
			_registStan = 127;
		} else if (_trueRegistStan < -128) {
			_registStan = -128;
		} else {
			_registStan = _trueRegistStan;
		}
	}
	
	// 晕眩耐性
    private int _registStun = 0;
    private int _trueRegistStun = 0;

    /**
     * 晕眩耐性
     * 
     * @return
     */
    public int getRegistStun() {
        return this._registStun;
    } // 使用するとき

    /**
     * 晕眩耐性
     * 
     * @param i
     */
    public void addRegistStun(final int i) {
        this._trueRegistStun += i;
        if (this._trueRegistStun > 127) {
            this._registStun = 127;
        } else if (this._trueRegistStun < -128) {
            this._registStun = -128;
        } else {
            this._registStun = this._trueRegistStun;
        }
    }
    
 // 支撑耐性
    private int _registSustain = 0;
    private int _trueRegistSustain = 0;

    /**
     * 支撑耐性
     * 
     * @return
     */
    public int getRegistSustain() {
        return this._registSustain;
    }

    /**
     * 支撑耐性
     * 
     * @param i
     */
    public void addRegistSustain(final int i) {
        this._trueRegistSustain += i;
        if (this._trueRegistSustain > 127) {
            this._registSustain = 127;
        } else if (this._trueRegistSustain < -128) {
            this._registSustain = -128;
        } else {
            this._registSustain = this._trueRegistSustain;
        }
    }

	// 石化耐性
	private int _registStone = 0;
	private int _trueRegistStone = 0;

	public int getRegistStone() {
		return _registStone;
	} // 使用

	public void addRegistStone(int i) {
		_trueRegistStone += i;
		if (_trueRegistStone > 127) {
			_registStone = 127;
		} else if (_trueRegistStone < -128) {
			_registStone = -128;
		} else {
			_registStone = _trueRegistStone;
		}
	}

	// 睡眠耐性
	private int _registSleep = 0;
	private int _trueRegistSleep = 0;

	public int getRegistSleep() {
		return _registSleep;
	} // 使用

	public void addRegistSleep(int i) {
		_trueRegistSleep += i;
		if (_trueRegistSleep > 127) {
			_registSleep = 127;
		} else if (_trueRegistSleep < -128) {
			_registSleep = -128;
		} else {
			_registSleep = _trueRegistSleep;
		}
	}

	// 冻结耐性
	private int _registFreeze = 0;
	private int _trueRegistFreeze = 0;

	public int getRegistFreeze() {
		return _registFreeze;
	} // 使用

	public void addRegistFreeze(int i) {
		_trueRegistFreeze += i;
		if (_trueRegistFreeze > 127) {
			_registFreeze = 127;
		} else if (_trueRegistFreeze < -128) {
			_registFreeze = -128;
		} else {
			_registFreeze = _trueRegistFreeze;
		}
	}

	private int _dmgup = 0; // ● 补正（-128～127）
	private int _trueDmgup = 0; // ● 本补正

	public int getDmgup() {
		return _dmgup;
	} // 使用

	/**
	 * 近战伤害
	 * 
	 * QQ：1043567675
	 * by：亮修改
	 * 2020年6月23日
	 * 下午6:17:18
	 */
	public void addDmgup(int i) {
		_trueDmgup += i;
		if (_trueDmgup >= 127) {
			_dmgup = 127;
		} else if (_trueDmgup <= -128) {
			_dmgup = -128;
		} else {
			_dmgup = _trueDmgup;
		}
	}

	private int _bowDmgup = 0; // ● 弓补正（-128～127）
	private int _trueBowDmgup = 0; // ● 本弓补正

	public int getBowDmgup() {
		return _bowDmgup;
	} // 使用

	/**
	 * 远程伤害
	 * 
	 * QQ：1043567675
	 * by：亮修改
	 * 2020年6月23日
	 * 下午6:17:05
	 */
	public void addBowDmgup(int i) {
		_trueBowDmgup += i;
		if (_trueBowDmgup >= 127) {
			_bowDmgup = 127;
		} else if (_trueBowDmgup <= -128) {
			_bowDmgup = -128;
		} else {
			_bowDmgup = _trueBowDmgup;
		}
	}

	private int _hitup = 0; // ● 命中补正（-128～127）
	private int _trueHitup = 0; // ● 本命中补正

	public int getHitup() {
		return _hitup;
	} // 使用

	/**
	 * 近战命中
	 * 
	 * QQ：1043567675
	 * by：亮修改
	 * 2020年6月23日
	 * 下午6:17:12
	 */
	public void addHitup(int i) {
		_trueHitup += i;
		if (_trueHitup >= 127) {
			_hitup = 127;
		} else if (_trueHitup <= -128) {
			_hitup = -128;
		} else {
			_hitup = _trueHitup;
		}
	}

	private int _bowHitup = 0; // ● 弓命中补正（-128～127）
	private int _trueBowHitup = 0; // ● 本弓命中补正

	public int getBowHitup() {
		return _bowHitup;
	} // 使用

	/**
	 * 远程命中
	 * 
	 * QQ：1043567675
	 * by：亮修改
	 * 2020年6月23日
	 * 下午6:16:48
	 */
	public void addBowHitup(int i) {
		_trueBowHitup += i;
		if (_trueBowHitup >= 127) {
			_bowHitup = 127;
		} else if (_trueBowHitup <= -128) {
			_bowHitup = -128;
		} else {
			_bowHitup = _trueBowHitup;
		}
	}

	public int _mr = 0; // ● 魔法防御（0～）
	public int _trueMr = 0; // ● 本魔法防御

	public int getMr() {
		if (hasSkillEffect(153) == true) {
			return _mr / 4;
		} else {
			return _mr;
		}
	} // 使用

	public int getTrueMr() {
		return _trueMr;
	} // 

	public void addMr(int i) {
		_trueMr += i;
		if (_trueMr <= 0) {
			_mr = 0;
		} else {
			_mr = _trueMr;
		}
	}

	public int _sp = 0; // ● 加ＳＰ

	public int getSp() {
		return getTrueSp() + _sp;
	}

	public int getTrueSp() {
		return getMagicLevel() + getMagicBonus();
	}

	public void addSp(int i) {
		_sp += i;
	}

	private boolean _isDead; // ● 死亡态

	public boolean isDead() {
		return _isDead;
	}

	public void setDead(boolean flag) {
		_isDead = flag;
	}

	private int _status; // ● 态？

	public int getStatus() {
		return _status;
	}

	public void setStatus(int i) {
		_status = i;
	}

	private String _title; // ● 

	public String getTitle() {
		return _title;
	}

	public void setTitle(String s) {
		_title = s;
	}

	private int _lawful; // ● 

	public int getLawful() {
		return _lawful;
	}

	public void setLawful(int i) {
		_lawful = i;
	}

	public synchronized void addLawful(int i) {
		_lawful += i;
		if (_lawful > 32767) {
			_lawful = 32767;
		} else if (_lawful < -32768) {
			_lawful = -32768;
		}
	}

	private int _heading; // ● 向 0.左上 1.上 2.右上 3.右 4.右下 5.下 6.左下 7.左

	public int getHeading() {
		return _heading;
	}

	public void setHeading(int i) {
		_heading = i;
	}

	private int _moveSpeed; // ●  0.通常 1. 2.

	public int getMoveSpeed() {
		return _moveSpeed;
	}

	public void setMoveSpeed(int i) {
		_moveSpeed = i;
	}

	private int _braveSpeed; // ● 态 0.通常 1.

	public int getBraveSpeed() {
		return _braveSpeed;
	}

	public void setBraveSpeed(int i) {
		_braveSpeed = i;
	}

	private int _tempCharGfx; // ● ＩＤ

	public int getTempCharGfx() {
		return _tempCharGfx;
	}

	public void setTempCharGfx(int i) {
		_tempCharGfx = i;
	}
	
	private int _tempCharGfxAtDead;

	public int getTempCharGfxAtDead() {
		return _tempCharGfxAtDead;
	}

	public void setTempCharGfxAtDead(int i) {
		_tempCharGfxAtDead = i;
	}

	private int _gfxid; // ● ＩＤ

	public int getGfxId() {
		return _gfxid;
	}

	public void setGfxId(int i) {
		_gfxid = i;
	}

	public int getMagicLevel() {
		return getLevel() / 4;
	}

	public int getMagicBonus() {
		int i = getInt();
		if (i <= 8) {
			return -1;
		} else if (i <= 11) {
			return 0;
		} else if (i <= 14) {
			return 1;
		} else if (i <= 17) {
			return 2;
		} else {
			return i - 15;
		}
	}
	private boolean _gmInvis;
	
	public boolean isGmInvis() {
		return _gmInvis;
	}

	public void setGmInvis(boolean flag) {
		_gmInvis = flag;
	}

	public boolean isInvisble() {
		return (hasSkillEffect(L1SkillId.INVISIBILITY)
				|| hasSkillEffect(L1SkillId.BLIND_HIDING));
	}

	public boolean isLightOn() {
		return hasSkillEffect(L1SkillId.LIGHT);
	}

	public void healHp(int pt) {
		setCurrentHp(getCurrentHp() + pt);
	}

	private int _karma;

	/**
	 * 保持返。
	 * 
	 * @return 。
	 */
	public int getKarma() {
		return _karma;
	}

	/**
	 * 保持设定。
	 * 
	 * @param karma
	 *            。
	 */
	public void setKarma(int karma) {
		_karma = karma;
	}

	public void setMr(int i) {
		_trueMr = i;
		if (_trueMr <= 0) {
			_mr = 0;
		} else {
			_mr = _trueMr;
		}
	}
	public boolean isStop(){
		if ( get_poisonStatus2() == 4 || get_poisonStatus4() == 4 //判断冲晕,冰冻功能 
		 || get_poisonStatus5() == 4 || get_poisonStatus6() == 4) {//沉睡，地屏
			return true;
		}
		if (hasSkillEffect(L1SkillId.EARTH_BIND)) {
			return true;
		}
		if (hasSkillEffect(L1SkillId.ICE_LANCE)) {
			return true;
		}
		return false;
	}
	
	public boolean isOnlyStopMove(){
		if (get_poisonStatus3() == 4) {
			return true;
		}
		return false;
	}

	// 加入冲晕=2,束缚=3,冰茅=4,沉睡=5,地屏=6功能
	public int get_poisonStatus2() {
		return _poisonStatus2;
	}
	public int get_poisonStatus3() {
		return _poisonStatus3;
	}
	public int get_poisonStatus4() {
		return _poisonStatus4;
	}
	public int get_poisonStatus5() {
		return _poisonStatus5;
	}
	public int get_poisonStatus6() {
		return _poisonStatus6;
	}
	public int get_poisonStatus7() {
		return _poisonStatus7;
	}
	public int get_poisonStatus8() {
		return _poisonStatus8;
	}
	
	public void set_poisonStatus2(int i) {
		_poisonStatus2 = i;
	}
	public void set_poisonStatus3(int i) {
		_poisonStatus3 = i;
	}
	public void set_poisonStatus4(int i) {
		_poisonStatus4 = i;
	}
	public void set_poisonStatus5(int i) {
		_poisonStatus5 = i;
	}
	public void set_poisonStatus6(int i) {
		_poisonStatus6 = i;
	}
	public void set_poisonStatus7(int i) {
		_poisonStatus7 = i;
	}
	public void set_poisonStatus8(int i) {
		_poisonStatus7 = i;
	}
	
	public void add_poison2(L1Poison2 poison2) {
		_poison2 = poison2;
	}
	public void add_poison3(L1Poison3 poison3) {
		_poison3 = poison3;
	}
	public void add_poison4(L1Poison4 poison4) {
		_poison4 = poison4;
	}
	public void add_poison5(L1Poison5 poison5) {
		_poison5 = poison5;
	}
	public void add_poison6(L1Poison6 poison6) {
		_poison6 = poison6;
	}
	public void add_poison7(L1Poison7 poison7) {
		_poison7 = poison7;
	}
	public void add_poison8(L1Poison8 poison8) {
		_poison8 = poison8;
	}
	
	public void del_poison2() {
		_poison2 = null;
	}
	public void del_poison3() {
		_poison3 = null;
	}
	public void del_poison4() {
		_poison4 = null;
	}
	public void del_poison5() {
		_poison5 = null;
	}
	public void del_poison6() {
		_poison6 = null;
	}
	public void del_poison7() {
		_poison7 = null;
	}
	public void del_poison8() {
		_poison8 = null;
	}
	
	public L1Poison2 get_poison2() {
		return _poison2;
	}
	public L1Poison3 get_poison3() {
		return _poison3;
	}
	public L1Poison4 get_poison4() {
		return _poison4;
	}
	public L1Poison5 get_poison5() {
		return _poison5;
	}
	public L1Poison6 get_poison6() {
		return _poison6;
	}
	public L1Poison7 get_poison7() {
		return _poison7;
	}
	public L1Poison8 get_poison8() {
		return _poison8;
	}
	
	private L1Poison2 _poison2 = null;
	private L1Poison3 _poison3 = null;
	private L1Poison4 _poison4 = null;
	private L1Poison5 _poison5 = null;
	private L1Poison6 _poison6 = null;
	private L1Poison7 _poison7 = null;
	private L1Poison8 _poison8 = null;
	
	private int _poisonStatus2;
	private int _poisonStatus3;
	private int _poisonStatus4;
	private int _poisonStatus5;
	private int _poisonStatus6;
	private int _poisonStatus7;
	private int _poisonStatus8;
	// 加入冲晕=2,束缚=3,冰茅=4,沉睡=5,地屏=6功能 end

	// 亮度判断 
	private int _light = 0; // ● ＩＤ

	public int getPcLight() {
		return _light;
	}

	public void setPcLight(int i) {
		_light = i;
	}
	// 亮度判断  end

	// NPC说话顺序判断 
	private int _order = 0;

	public int getOrder() {
		return _order;
	}

	public void setOrder(int i) {
		_order = i;
	}
	// NPC说话顺序判断  end
	
	// 能力重置设定 
	private int _resetStr = 0;

	public int getResetStr() {
		return _resetStr;
	}

	public void setResetStr(int i) {
		_resetStr = i;
	}
	
	private int _resetDex = 0;

	public int getResetDex() {
		return _resetDex;
	}

	public void setResetDex(int i) {
		_resetDex = i;
	}
	
	private int _resetCon = 0;

	public int getResetCon() {
		return _resetCon;
	}

	public void setResetCon(int i) {
		_resetCon = i;
	}
	
	private int _resetInt = 0;

	public int getResetInt() {
		return _resetInt;
	}

	public void setResetInt(int i) {
		_resetInt = i;
	}
	
	private int _resetWis = 0;

	public int getResetWis() {
		return _resetWis;
	}

	public void setResetWis(int i) {
		_resetWis = i;
	}
	
	private int _resetCha = 0;

	public int getResetCha() {
		return _resetCha;
	}

	public void setResetCha(int i) {
		_resetCha = i;
	}
	
	private int _othetReset = 0;

	public int getOthetReset() {
		return _othetReset;
	}

	public void setOthetReset(int i) {
		_othetReset = i;
	}
	// 能力重置设定  end
	
	// 能力重置设定 
	private int _originalStr = 0;

	public int getOriginalStr() {
		return _originalStr;
	}

	public void setOriginalStr(int i) {
		_originalStr = i;
	}
	
	private int _originalDex = 0;

	public int getOriginalDex() {
		return _originalDex;
	}

	public void setOriginalDex(int i) {
		_originalDex = i;
	}
	
	private int _originalCon = 0;

	public int getOriginalCon() {
		return _originalCon;
	}

	public void setOriginalCon(int i) {
		_originalCon = i;
	}
	
	private int _originalInt = 0;

	public int getOriginalInt() {
		return _originalInt;
	}

	public void setOriginalInt(int i) {
		_originalInt = i;
	}
	
	private int _originalWis = 0;

	public int getOriginalWis() {
		return _originalWis;
	}

	public void setOriginalWis(int i) {
		_originalWis = i;
	}
	
	private int _originalCha = 0;

	public int getOriginalCha() {
		return _originalCha;
	}

	public void setOriginalCha(int i) {
		_originalCha = i;
	}

	private int _allPoint = 0;

	public int getAllPoint() {
		return _allPoint;
	}

	public void setAllPoint(int i) {
		_allPoint = i;
	}
	// 能力重置设定  end

	// 随身祭司补血设定 
	private int _hierarch = 0;

    public int getHierarch() {
        return _hierarch;
    }

    public void setHierarch(int i) {
        _hierarch = i;
    }
    // 随身祭司补血设定  end

    // 魔法攻击力设定 
	private int magic_dmg = 0;

    public int getMagicDmg() {
        return magic_dmg;
    }

    public void setMagicDmg(int i) {
        magic_dmg = i;
    }
    // 魔法攻击力设定  end
    
	private boolean _vdmg = true;

	public void setVdamg(boolean b) {
		_vdmg = b;
	}
	
	public boolean isVdmg(){
		return _vdmg;
	}
	
	private boolean _kogifd = false;

	public void setKOGifd(boolean b) {
		_kogifd = b;
	}
	
	public boolean isKOGifd(){
		return _kogifd;
	}
	
	private boolean _showBlue = true;

	public void setShowBlue(boolean b) {
		_showBlue = b;
	}
	
	public boolean isShowBlue(){
		return _showBlue;
	}
	
	private boolean _pvp= false;

	public void setPVP(boolean b) {
		_pvp = b;
	}
	
	public boolean isPVP(){
		return _pvp;
	}
	private int _fightId;// 决斗对象OBJID

	/**
	 * 决斗对象OBJID
	 * 
	 * @return
	 */
	public int getFightId() {
		return this._fightId;
	}

	/**
	 * 决斗对象OBJID
	 * 
	 * @param i
	 */
	public void setFightId(final int i) {
		this._fightId = i;
	}
	
	private boolean _isPinkName = false;
	
	public boolean isPinkName() {
		return _isPinkName;
	}

	public void setPinkName(boolean flag) {
		_isPinkName = flag;
	}
	
	private int _pinksec = 0;
	
	public void setPinkSec(final int sec){
		_pinksec = sec;
	}
	
	public int getPinkSec()
	{
		return _pinksec;
	}
	
	private boolean _ghost = false; // 

	public boolean isGhost() {
		return _ghost;
	}

	public void setGhost(boolean flag) {
		_ghost = flag;
	}
	
	private int _attacksec = 0;
	
	public void setAttacksec(final int sec)
	{
		_attacksec = sec;
	}
	
	public int getAttacksec()
	{
		return _attacksec;
	}
	
	private boolean _isattack;
	
	public void setAttack(boolean flg)
	{
		_isattack = true;
	}
	
	public boolean isAttack(){
		return _isattack;
	}
	
	private boolean _choushui= false;

	public void setChoushui(boolean b) {
		_choushui = b;
	}
	
	public boolean isChoushui(){
		return _choushui;
	}
	// 暗黑耐性
    private int _registBlind = 0;
    private int _trueRegistBlind = 0;

    /**
     * 暗黑耐性
     * 
     * @return
     */
    public int getRegistBlind() {
        return this._registBlind;
    }

    /**
     * 暗黑耐性
     * 
     * @param i
     */
    public void addRegistBlind(final int i) {
        this._trueRegistBlind += i;
        if (this._trueRegistBlind > 127) {
            this._registBlind = 127;
        } else if (this._trueRegistBlind < -128) {
            this._registBlind = -128;
        } else {
            this._registBlind = this._trueRegistBlind;
        }
    }
    
    /**
     * 闪避率 -
     */
    private byte _nDodge = 0;
    
    
    /**
     * 取得闪避率 -
     */
    public byte getNdodge() {
        return this._nDodge;
    }
    
    /**
     * 增加闪避率 -
     */
    public void addNdodge(final byte i) {
        this._nDodge += i;
        if (this._nDodge >= 10) {
            this._nDodge = 10;
        } else if (this._nDodge <= 0) {
            this._nDodge = 0;
        }
    }
    
    /**
     * 闪避率 +
     */
    private byte _dodge = 0;
    
    /**
     * 取得闪避率 +
     */
    public byte getDodge() {
        return this._dodge;
    }
    
    /**
     * 增加闪避率 +
     */
    public void addDodge(final byte i) {
        this._dodge += i;
        if (this._dodge >= 10) {
            this._dodge = 10;
        } else if (this._dodge <= 0) {
            this._dodge = 0;
        }
    }
    
}
