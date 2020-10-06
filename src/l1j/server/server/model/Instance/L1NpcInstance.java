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
package l1j.server.server.model.Instance;

//
import static l1j.server.server.model.item.L1ItemId.B_POTION_OF_GREATER_HASTE_SELF;
import static l1j.server.server.model.item.L1ItemId.B_POTION_OF_HASTE_SELF;
import static l1j.server.server.model.item.L1ItemId.POTION_OF_EXTRA_HEALING;
import static l1j.server.server.model.item.L1ItemId.POTION_OF_GREATER_HASTE_SELF;
import static l1j.server.server.model.item.L1ItemId.POTION_OF_GREATER_HEALING;
import static l1j.server.server.model.item.L1ItemId.POTION_OF_HASTE_SELF;
import static l1j.server.server.model.item.L1ItemId.POTION_OF_HEALING;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import l1j.server.Config;
import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Opcodes;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.SprTable;
import l1j.server.server.datatables.lock.SpawnBossReading;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1GroundInventory;
import l1j.server.server.model.L1HateList;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1ItemDelay;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1MobGroupInfo;
import l1j.server.server.model.L1MobSkillUse;
import l1j.server.server.model.L1NpcRegenerationTimer;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Spawn;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.model.npc.L1NpcDefaultAction;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_MoveCharPacket;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_NPCTalkReturn;//怪物显示对话视窗 
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_RemoveObject;
//import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.timecontroller.NpcWorkTimer;
import l1j.server.server.types.Point;
import l1j.server.server.utils.TimerPool;
import l1j.server.server.world.L1World;
import l1j.william.MobTalk;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1NpcInstance extends L1Character {
	private static final long serialVersionUID = 1L;

	public static final int HIDDEN_STATUS_NONE = 0;
	public static final int HIDDEN_STATUS_SINK = 1;
	public static final int HIDDEN_STATUS_FLY = 2;

	private static final Log _log = LogFactory.getLog(L1NpcInstance.class);

	private L1Npc _npcTemplate;

	private L1Spawn _spawn;
	private int _spawnNumber; // L1Spawn管理

	private boolean isAIerror = false;
	private int _petcost; // 
	public L1Inventory _inventory = new L1Inventory();
	private L1MobSkillUse mobSkill;
	L1Character _lastAttacker = null;

	private static Random _random = new Random();

	// 对像初发见。（用）
	private boolean firstFound = true;

	// 经路探索范围（半径） ※上注意！！
	public static int courceRange = 15;

	// 吸MP
	private int _drainedMana = 0;

	// 休憩
	private boolean _rest = false;
	
	/**
	 * NPC对话判断
	 */
	public NpcExecutor TALK = null;
	
	/**
	 * NPC对话执行
	 */
	public NpcExecutor ACTION = null;
	
	/**
	 * NPC受到攻击(NPC)
	 */
	public NpcExecutor ATTACK = null;
	
	/**
	 * NPC死亡(MOB)
	 */
	public NpcExecutor DEATH = null;
	
	/**
	 * NPC工作时间
	 */
	public NpcExecutor WORK = null;
	
	/**
	 * NPC召唤
	 */
	public NpcExecutor SPAWN = null;

	// ■■■■■■■■■■■■■ ＡＩ关连 ■■■■■■■■■■■

	interface NpcAI {
		public void start();
	}

	protected void startAI() {
		setAiRunning(true);
		if (Config.NPCAI_IMPLTYPE == 1) {
			new NpcAITimerImpl().start();
		} else if (Config.NPCAI_IMPLTYPE == 2) {
			new NpcAIThreadImpl().start();
		} else {
			new NpcAITimerImpl().start();
		}
	}
	/**
	 * ()为。 AI实装场合使用。
	 */
	private static final TimerPool _timerPool = new TimerPool(4);

	class NpcAITimerImpl extends TimerTask implements NpcAI {
		/**
		 * 死亡处理终了待
		 */
		private class DeathSyncTimer extends TimerTask {
			private void schedule(int delay) {
				_timerPool.getTimer().schedule(new DeathSyncTimer(), delay);
			}

			@Override
			public void run() {
				if (isDeathProcessing()) {
					schedule(getSleepTime());
					return;
				}
				allTargetClear();
				setAiRunning(false);
			}
		}

		@Override
		public void start() {
			//setAiRunning(true);
			_timerPool.getTimer().schedule(NpcAITimerImpl.this, 0);
		}

		private void stop() {
			mobSkill.resetAllSkillUseCount();
			_timerPool.getTimer().schedule(new DeathSyncTimer(), 0); // 死亡同期开始
		}

		// 同Timer登录为、苦肉策。
		private void schedule(int delay) {
			_timerPool.getTimer().schedule(new NpcAITimerImpl(), delay);
		}

		@Override
		public void run() {
			try {
				if (notContinued()) {
					stop();
					return;
				}

				// XXX 同期怪麻痹判定
				if (0 < _paralysisTime) {
					schedule(_paralysisTime);
					_paralysisTime = 0;
					setParalyzed(false);
					return;
				} else if (isParalyzed() || isSleeped()) {
					schedule(200);
					return;
				}

				if (!AIProcess()) { // AI续、次实行、终了
					schedule(getSleepTime());
					return;
				}
				stop();
			} catch (Exception e) {
				_log.info("#"+getNpcId()+"#NpcAI例外发生了。坐标X："+getX()+" Y："+getY()+" MAPID："+getMapId(), e);
				isAIerror = true;
				WriteLogTxt.Recording("AI报错记录", getNpcId()+"出错了当前仇恨清单长度"+_hateList.toTargetArrayList().size()+"AI执行状态为"+_aiRunning+"当前对象为"+_target);
			}
		}

		private boolean notContinued() {
			return _destroyed || isDead() || getCurrentHp() <= 0
					|| getHiddenStatus() != HIDDEN_STATUS_NONE;
		}
	}

	class NpcAIThreadImpl implements Runnable, NpcAI {
		@Override
		public void start() {
			GeneralThreadPool.getInstance().execute(NpcAIThreadImpl.this);
		}

		@Override
		public void run() {
			try {
				//setAiRunning(true);
				while (!_destroyed && !isDead() && getCurrentHp() > 0
						&& getHiddenStatus() == HIDDEN_STATUS_NONE) {
					/*
					 * if (_paralysisTime > 0) { try {
					 * Thread.sleep(_paralysisTime); } catch (Exception
					 * exception) { break; } finally { setParalyzed(false);
					 * _paralysisTime = 0; } }
					 */
					while (isParalyzed() || isSleeped()) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							setParalyzed(false);
						}
					}

					if (AIProcess()) {
						break;
					}
					if (isAIerror) {
						isAIerror = false;
						break;
					}
					try {
						// 指定时间分停止
						Thread.sleep(getSleepTime());
					} catch (Exception e) {
						break;
					}
				}
				mobSkill.resetAllSkillUseCount();
				do {
					try {
						Thread.sleep(getSleepTime());
					} catch (Exception e) {
						break;
					}
				} while (isDeathProcessing());
				allTargetClear();
				setAiRunning(false);
			} catch (Exception e) {
				_log.info("#"+getNpcId()+"#NpcAI例外发生了。坐标X："+getX()+" Y："+getY()+" MAPID："+getMapId(), e);
				isAIerror = true;
				WriteLogTxt.Recording("AI报错记录", getNpcId()+"出错了当前仇恨清单长度"+_hateList.toTargetArrayList().size()+"AI执行状态为"+_aiRunning+"当前对象为"+_target);
				allTargetClear();
				setAiRunning(false);
				setCurrentHp(getMaxHp());
			}
		}
	}

	private long _chat_delay_time = 0;
	private long _gifd_delay_time = 0;
	private int _chat_message_index = 0;
	// ＡＩ处理 (返值ＡＩ处理终了)
	private boolean AIProcess() {
		setSleepTime(300);

		checkTarget();
		if (_target == null && _master == null) {
			// 空场合探
			// （主人场合自分探）
			searchTarget();
		}

		if (this.getNpcTemplate().get_chat_message() != null && this.getNpcTemplate().get_chat_delay_time() > 0){
			if (System.currentTimeMillis() - _chat_delay_time >= this.getNpcTemplate().get_chat_delay_time() * 1000){
				_chat_delay_time = System.currentTimeMillis();
				if (_chat_message_index >= this.getNpcTemplate().get_chat_message().length){
					_chat_message_index = 0;
				}
				final String chatText = this.getNpcTemplate().get_chat_message()[_chat_message_index];
				if (this.getNpcTemplate().get_chat_type() == 0){
					this.broadcastPacket(new S_ChatPacket(this, chatText,Opcodes.S_OPCODE_NORMALCHAT, 0));
				}else if (this.getNpcTemplate().get_chat_type() == 1){
					this.broadcastPacket(new S_ChatPacket(this, chatText,Opcodes.S_OPCODE_NORMALCHAT, 2));
				}else if (this.getNpcTemplate().get_chat_type() == 2){
					L1World.getInstance().broadcastPacketToAll(new S_ChatPacket(this, chatText,Opcodes.S_OPCODE_GLOBALCHAT, 3));
				}
				_chat_message_index++;
			}
		}
		
		if (this.getNpcTemplate().get_gifd_id() > 0){
			if (System.currentTimeMillis() - _gifd_delay_time >= this.getNpcTemplate().get_gifd_delay_time() * 1000){
				_gifd_delay_time = System.currentTimeMillis();
				this.broadcastPacket(new S_SkillSound(this.getId(),this.getNpcTemplate().get_gifd_id()));
			}
		}
		
		onItemUse();

		if (_target == null) {
			// 场合
			checkTargetItem();
			if (isPickupItem() && _targetItem == null && !(this instanceof L1SummonInstance)) { // 修正部分召唤怪会捡地面的垃圾的问题 
				// 拾子场合探
				if (getNpcId()!=45166 && getNpcId()!=45167) {
					searchTargetItem();
				}else {
					searchNanguaItem();
				}		
			}
			if (_targetItem == null) {
				if (noTarget()) {
					return true;
				}
			} else {
				// onTargetItem();
				L1Inventory groundInventory = L1World.getInstance()
						.getInventory(_targetItem.getX(), _targetItem.getY(),
								_targetItem.getMapId());
				if (groundInventory.checkItem(_targetItem.getItemId())) {
					onTargetItem();
				} else {
					_targetItemList.remove(_targetItem);
					_targetItem = null;
					setSleepTime(1000);
					return false;
				}
			}
		} else { // 场合
			if (getHiddenStatus() == HIDDEN_STATUS_NONE) {
				onTarget();
			} else {
				return true;
			}
		}

		return false; // ＡＩ处理续行
	}

	// 使用处理（Ｔｙｐｅ结构违实装）
	public void onItemUse() {
	}

	// 探（Ｔｙｐｅ结构违实装）
	public void searchTarget() {
	}

	// 有效确认及次设定
	public void checkTarget() {
		if (_target == null
				|| _target.getMapId() != getMapId()
				|| _target.getCurrentHp() <= 0
				|| _target.isDead()
				|| _target.isGmInvis()
				|| _target.isGhost()
				|| (_target.isInvisble() && !getNpcTemplate().is_agrocoi() && !_hateList
						.containsKey(_target))) {
			if (_target != null) {
				tagertClear();
			}
			if (!_hateList.isEmpty()) {
				_target = _hateList.getMaxHateCharacter();
				checkTarget();
			}
		}
	}

	// 设定
	public void setHate(L1Character cha, int hate) {
		if (cha != null && cha.getId() != getId()) {
			if (!isFirstAttack() && hate != 0) {
				// hate += 20; // ＦＡ
				hate += getMaxHp() / 10; // ＦＡ
				setFirstAttack(true);
			}

			_hateList.add(cha, hate);
			_target = _hateList.getMaxHateCharacter();
			checkTarget();
		}
	}

	// 设定
	public void setLink(L1Character cha) {
	}

	// 仲间意识ＮＰＣ检索（攻击者有效）
	public void serchLink(L1PcInstance targetPlayer, int family) {
		Collection<L1Object> targetKnownObjects = targetPlayer.getKnownObjects();
		for (Object knownObject : targetKnownObjects) {
			if (knownObject instanceof L1NpcInstance) {
				L1NpcInstance npc = (L1NpcInstance) knownObject;
				if (npc.getNpcTemplate().get_agrofamily() > 0) {
					// 仲间对场合
					if (npc.getNpcTemplate().get_agrofamily() == 1) {
						// 同种族对仲间意识
						if (npc.getNpcTemplate().get_family() == family) {
							npc.setLink(targetPlayer);
						}
					} else {
						// 全ＮＰＣ对仲间意识
						npc.setLink(targetPlayer);
					}
				}
			}
		}
	}

	// 场合处理
	public void onTarget() {
		setActived(true);
		_targetItemList.clear();
		_targetItem = null;
		L1Character target = _target; // 先_target变影响出别领域参照确保
		if (getAtkspeed() == 0 || hasSkillEffect(17005) //判断逃跑功能 
		 || get_poisonStatus2() == 4 || get_poisonStatus4() == 4 //判断冲晕,冰冻功能 
		 || get_poisonStatus5() == 4 || get_poisonStatus6() == 4) //判断沉睡,地屏功能 
		{
			if (getPassispeed() > 0&& get_poisonStatus2() != 4 && get_poisonStatus3() != 4 //判断冲晕,束缚功能 
			 && get_poisonStatus4() != 4 && get_poisonStatus5() != 4 && get_poisonStatus6() != 4) //判断冰冻,沉睡,地屏功能 
			{
				// 怪物逃跑说话 
				MobTalk.forL1MonsterTalking(this, 4);
				// 怪物逃跑说话  end
				int escapeDistance = 15;
				/*if ((hasSkillEffect(20) == true) || (hasSkillEffect(40) == true) || (hasSkillEffect(103) == true)) {//补上20、103 
					escapeDistance = 1;
				}*/
				if (getLocation().getTileLineDistance(target.getLocation()) > escapeDistance) {
					// 逃终了
					tagertClear();
				} else { // 逃
					int dir = targetReverseDirection(target.getX(), target
							.getY());
					dir = checkObject(getX(), getY(), getMapId(), dir);
					setDirectionMove(dir);
					setSleepTime(calcSleepTime(getPassispeed()));
				}
			}
		} else { // 逃
			// 修正黑暗之影效果 
			/*if (checkTarget(target.getX(), target.getY(), getLocation().getLineDistance(new Point(target.getX(), target.getY()))) == -1) {
				allTargetClear();
				return;
			}*/
			// 修正黑暗之影效果  end
			boolean isSkillUse = false;
			isSkillUse = mobSkill.skillUse(target);
			if (isSkillUse == true) {
				setSleepTime(calcSleepTime(mobSkill.getSleepTime()));
				return;
			}

			if (isAttackPosition(target.getX(), target.getY(), getNpcTemplate()
					.get_ranged())) {
				// 攻击可能位置
				setHeading(targetDirection(target.getX(), target.getY()));
				attackTarget(target);
			} else {
				// 攻击不可能位置

				if (getPassispeed() > 0 && get_poisonStatus2() != 4 && get_poisonStatus3() != 4 //判断冲晕,束缚功能 
			 && get_poisonStatus4() != 4 && get_poisonStatus5() != 4 && get_poisonStatus6() != 4) {//判断冰冻,沉睡,地屏功能 
					// 移动
					int distance = getLocation().getTileDistance(
							target.getLocation());
					if (firstFound == true && getNpcTemplate().is_teleport()
							&& distance > 3 && distance < 15) {
						if (nearTeleport(target.getX(), target.getY()) == true) {
							firstFound = false;
							return;
						}
					}

					if (getNpcTemplate().is_teleport()
							&& 20 > _random.nextInt(100)
							&& getCurrentMp() >= 10 && distance > 6
							&& distance < 15) { // 移动
						if (nearTeleport(target.getX(), target.getY()) == true) {
							return;
						}
					}
					int dir = moveDirection(target.getX(), target.getY());
					if (dir == -1) {						
						int i = 0;
						for (Object obj : L1World.getInstance().getVisibleObjects(this,1)) {
							if (obj instanceof L1Character) {
								L1Character tg = (L1Character)obj;
								if (tg.isDead()) {
									continue;
								}
								if (this instanceof L1MonsterInstance && (obj instanceof L1PetInstance||obj instanceof L1SummonInstance||obj instanceof L1PcInstance)) {								
									_target = tg;
									i++;
								}else if (obj instanceof L1MonsterInstance) {
									if (SpawnBossReading.get().isBoss(getOldNpcID())) {
										L1MonsterInstance mob = (L1MonsterInstance)obj;
										if (!SpawnBossReading.get().isBoss(mob.getNpcId())) {
											this.broadcastPacket(new S_NpcChatPacket(this ,tg.getName()+"，滚开，别挡道！", 2)); 
											L1Location loc = getLocation().randomLocation(100, false);
											int nx = loc.getX();
											int ny = loc.getY();
											L1Teleport.teleport(tg, nx, ny, getMapId(), getHeading());
											i++;
										}							
									}								
								}
							}		
						}
						dir = moveDirection(target.getX(), target.getY());
						if (dir>-1) {
							setDirectionMove(dir);
							setSleepTime(calcSleepTime(getPassispeed()));
						}else {
							if (i == 0) {
								tagertClear();
							}
						}					
					} else {
						setDirectionMove(dir);
						setSleepTime(calcSleepTime(getPassispeed()));
					}
				} else {
					// 移动（排除、ＰＴ自业自得）
					tagertClear();
				}
			}
		}
	}

	// 目标指定攻击
	public void attackTarget(L1Character target) {
		if (target instanceof L1PcInstance) {
			L1PcInstance player = (L1PcInstance) target;
			if (player.isTeleport()) { // 处理中
				return;
			}
		} else if (target instanceof L1PetInstance) {
			L1PetInstance pet = (L1PetInstance) target;
			L1Character cha = pet.getMaster();
			if (cha instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) { // 处理中
					return;
				}
			}
		} else if (target instanceof L1SummonInstance) {
			L1SummonInstance summon = (L1SummonInstance) target;
			L1Character cha = summon.getMaster();
			if (cha instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) { // 处理中
					return;
				}
			}
		}
		if (this instanceof L1PetInstance) {
			L1PetInstance pet = (L1PetInstance) this;
			L1Character cha = pet.getMaster();
			if (cha instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) { // 处理中
					return;
				}
			}
		} else if (this instanceof L1SummonInstance) {
			L1SummonInstance summon = (L1SummonInstance) this;
			L1Character cha = summon.getMaster();
			if (cha instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) { // 处理中
					return;
				}
			}
		}

		if (target instanceof L1NpcInstance) {
			L1NpcInstance npc = (L1NpcInstance) target;
			if (npc.getHiddenStatus() != HIDDEN_STATUS_NONE) { // 地中潜、飞
				allTargetClear();
				return;
			}
		}

		boolean isCounterBarrier = false;
		L1Attack attack = new L1Attack(this, target);
		if (attack.calcHit()) {
			/*删除if (target.hasSkillEffect(L1SkillId.COUNTER_BARRIER)) {
				L1Magic magic = new L1Magic(target, this);
				boolean isProbability = magic
						.calcProbabilityMagic(L1SkillId.COUNTER_BARRIER);
				boolean isShortDistance = attack.isShortDistance();
				if (isProbability && isShortDistance) {
					isCounterBarrier = true;
				}
			}删除*/
			if (!isCounterBarrier) {
				// 怪物攻击说话 
				MobTalk.forL1MonsterTalking(this, 2);
				// 怪物攻击说话  end
				attack.calcDamage();
			}
		}
		if (isCounterBarrier) {
			attack.actionCounterBarrier();
			attack.commitCounterBarrier();
		} else {
			attack.action();
			attack.commit();
		}
		setSleepTime(calcSleepTime(getAtkspeed()));
	}

	// 探
	public void searchTargetItem() {
		ArrayList<L1Object> objects = L1World.getInstance().getVisibleObjects(
				this);
		ArrayList<L1GroundInventory> gInventorys = new ArrayList<L1GroundInventory>();

		for (L1Object obj : objects) {
			if (obj != null && obj instanceof L1GroundInventory) {
				gInventorys.add((L1GroundInventory) obj);
			}
		}
		if (gInventorys.size() == 0) {
			return;
		}
		// 拾()选定
		int pickupIndex = (int) (Math.random() * gInventorys.size());
		L1GroundInventory inventory = gInventorys.get(pickupIndex);
		for (L1ItemInstance item : inventory.getItems()) {
			if (item.getDropObjId() > 0){
				continue;
			}
			_targetItem = item;
			_targetItemList.add(_targetItem);
		}
	}
	
	public void searchNanguaItem() {
		ArrayList<L1Object> objects = L1World.getInstance().getVisibleObjects(
				this);
		ArrayList<L1GroundInventory> gInventorys = new ArrayList<L1GroundInventory>();

		for (L1Object obj : objects) {
			if (obj != null && obj instanceof L1GroundInventory) {
				gInventorys.add((L1GroundInventory) obj);
			}
		}
		if (gInventorys.size() == 0) {
			return;
		}
		// 拾()选定
		int pickupIndex = (int) (Math.random() * gInventorys.size());
		L1GroundInventory inventory = gInventorys.get(pickupIndex);
		for (L1ItemInstance item : inventory.getItems()) {
			if (item.getItemId() == 40725) {
				_targetItem = item;
				_targetItemList.add(_targetItem);
			}		
		}
	}

	// 飞天的怪物判断地面道具 
	public void searchItem() {
		ArrayList<L1Object> objects = L1World.getInstance().getVisibleObjects(
				this);
		ArrayList<L1GroundInventory> gInventorys = new ArrayList<L1GroundInventory>();

		for (L1Object obj : objects) {
			if (obj != null && obj instanceof L1GroundInventory) {
				gInventorys.add((L1GroundInventory) obj);
			}
		}
		if (gInventorys.size() == 0) {
			return;
		}
		// 拾()选定
		int pickupIndex = (int) (Math.random() * gInventorys.size());
		L1GroundInventory inventory = gInventorys.get(pickupIndex);
		for (L1ItemInstance item : inventory.getItems()) {
			if (item.getItem().getType() == 6) { // 药水类
				_targetItem = item;
				_targetItemList.add(_targetItem);
			}
			if (_npcTemplate.get_npcId() == 45166 // 
					|| _npcTemplate.get_npcId() == 45167) {
				if (item.getItemId() ==  40725) {
					_targetItem = item;
					_targetItemList.add(_targetItem);
				}
			}		
		}
	}
	// 飞天的怪物判断地面道具  end

	public static void shuffle(L1Object[] arr) {
		for (int i = arr.length - 1; i > 0; i--) {
			int t = (int) (Math.random() * i);

			// 选值交换
			L1Object tmp = arr[i];
			arr[i] = arr[t];
			arr[t] = tmp;
		}
	}

	// 有效确认及次设定
	public void checkTargetItem() {
		if (_targetItem == null
				|| _targetItem.getMapId() != getMapId()
				|| getLocation().getTileDistance(_targetItem.getLocation()) > 15) {
			if (!_targetItemList.isEmpty()) {
				_targetItem = _targetItemList.get(0);
				_targetItemList.remove(0);
				checkTargetItem();
			} else {
				_targetItem = null;
			}
		}
	}

	// 场合处理
	public void onTargetItem() {
		if (getLocation().getTileLineDistance(_targetItem.getLocation()) == 0) // 可能位置
		{
			pickupTargetItem(_targetItem);
		} else // 不可能位置
		{
			int dir = moveDirection(_targetItem.getX(), _targetItem.getY());
			if (dir == -1) // 拾谛
			{
				_targetItemList.remove(_targetItem);
				_targetItem = null;
			} else // 移动
			{
				setDirectionMove(dir);
				setSleepTime(calcSleepTime(getPassispeed()));
			}
		}
	}

	// 拾
	public void pickupTargetItem(L1ItemInstance targetItem) {
		L1Inventory groundInventory = L1World.getInstance().getInventory(
				targetItem.getX(), targetItem.getY(), targetItem.getMapId());
		L1ItemInstance item = groundInventory.tradeItem(targetItem, targetItem
				.getCount(), getInventory());
		if (item != null) {
			onGetItem(item);
			_targetItemList.remove(_targetItem);
			_targetItem = null;
			setSleepTime(1000);
		}	
	}

	// 场合处理 (返值ＡＩ处理终了)
	public boolean noTarget() {
		if (_master != null && _master.getMapId() == getMapId()
				&& getLocation().getTileLineDistance(_master.getLocation()) > 2) // 主人同离场合追尾
		{
			int dir = moveDirection(_master.getX(), _master.getY());
			if (dir != -1) {
				setDirectionMove(dir);
				setSleepTime(calcSleepTime(getPassispeed()));
			} else {
				return true;
			}
		} else {
			if (L1World.getInstance().getRecognizePlayer(this).size() == 0) {
				return true; // 周ＡＩ处理终了
			}
			// 移动动
			if (_master == null && getPassispeed() > 0 && !isRest() && get_poisonStatus2() != 4 && get_poisonStatus3() != 4 //判断冲晕,束缚功能 
			 && get_poisonStatus4() != 4 && get_poisonStatus5() != 4 && get_poisonStatus6() != 4) {//判断冰冻,沉睡,地屏功能 
				int dir = checkObject(getX(), getY(), getMapId(), _random
						.nextInt(20));
				if (dir != -1) {
					setDirectionMove(dir);
					setSleepTime(calcSleepTime(getPassispeed()));
				}
			}
		}
		return false;
	}

	public void onFinalAction(L1PcInstance pc, String s) {
	}

	// 现在削除
	public void tagertClear() {
		if (_target == null) {
			return;
		}
		_hateList.remove(_target);
		_target = null;
	}

	// 指定削除
	public void targetRemove(L1Character target) {
		_hateList.remove(target);
		if (_target != null && _target.equals(target)) {
			_target = null;
		}
	}

	// 全削除
	public void allTargetClear() {
		_hateList.clear();
		_target = null;
		_targetItemList.clear();
		_targetItem = null;
	}

	// 设定
	public void setMaster(L1Character cha) {
		_master = cha;
	}

	// 取得
	public L1Character getMaster() {
		return _master;
	}

	// ＡＩ
	public void onNpcAI() {
	}

	// 精制
	public void refineItem() {

		int[] materials = null;
		int[] counts = null;
		int[] createitem = null;
		int[] createcount = null;

		if (_npcTemplate.get_npcId() == 45032) { // 
			// 刀身
			if (getExp() != 0 && !_inventory.checkItem(20)) {
				materials = new int[] { 40508, 40521, 40045 };
				counts = new int[] { 150, 3, 3 };
				createitem = new int[] { 20 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
			// 刀身
			if (getExp() != 0 && !_inventory.checkItem(19)) {
				materials = new int[] { 40494, 40521 };
				counts = new int[] { 150, 3 };
				createitem = new int[] { 19 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
			// 刀身
			if (getExp() != 0 && !_inventory.checkItem(3)) {
				materials = new int[] { 40494, 40521 };
				counts = new int[] { 50, 1 };
				createitem = new int[] { 3 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
			// 
			if (getExp() != 0 && !_inventory.checkItem(100)) {
				materials = new int[] { 88, 40508, 40045 };
				counts = new int[] { 4, 80, 3 };
				createitem = new int[] { 100 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
			// 
			if (getExp() != 0 && !_inventory.checkItem(89)) {
				materials = new int[] { 88, 40494 };
				counts = new int[] { 2, 80 };
				createitem = new int[] { 89 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						L1ItemInstance item = _inventory.storeItem(
								createitem[j], createcount[j]);
						if (getNpcTemplate().get_digestitem() > 0) {
							setDigestItem(item);
						}
					}
				}
			}
		} else if (_npcTemplate.get_npcId() == 81069) { // （）
			// 体液
			if (getExp() != 0 && !_inventory.checkItem(40542)) {
				materials = new int[] { 40032 };
				counts = new int[] { 1 };
				createitem = new int[] { 40542 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
		} else if (_npcTemplate.get_npcId() == 45166 // 
				|| _npcTemplate.get_npcId() == 45167) {
			// 种
			if (getExp() != 0 && !_inventory.checkItem(40726)) {
				materials = new int[] { 40725 };
				counts = new int[] { 1 };
				createitem = new int[] { 40726 };
				createcount = new int[] { 1 };
				if (_inventory.checkItem(materials, counts)) {
					for (int i = 0; i < materials.length; i++) {
						_inventory.consumeItem(materials[i], counts[i]);
					}
					for (int j = 0; j < createitem.length; j++) {
						_inventory.storeItem(createitem[j], createcount[j]);
					}
				}
			}
		}
	}

	private boolean _aiRunning = false; // ＡＩ实行中
	// ※ＡＩ时实行确认时使用
	private boolean _actived = false; // ＮＰＣ
	// ※值false_target场合、初行动等使判定使用
	private boolean _firstAttack = false; // 
	private int _sleep_time; // ＡＩ停止时间(ms) ※行动起场合所要时间
	protected L1HateList _hateList = new L1HateList();
	// ※攻击判定ＰＴ时判定使用
	protected List<L1ItemInstance> _targetItemList = new ArrayList<L1ItemInstance>(); // 一览
	protected L1Character _target = null; // 现在
	protected L1ItemInstance _targetItem = null; // 现在
	protected L1Character _master = null; // 主人or
	private boolean _deathProcessing = false; // 死亡处理中
	// EXP、Drop分配中、

	private int _paralysisTime = 0; // Paralysis RestTime

	public void setParalysisTime(int ptime) {
		_paralysisTime = ptime;
	}

	public L1HateList getHateList() {
		return _hateList;
	}

	public int getParalysisTime() {
		return _paralysisTime;
	}

	// HP自然回复
	public final void startHpRegeneration() {
		int hprInterval = getNpcTemplate().get_hprinterval();
		int hpr = getNpcTemplate().get_hpr();
		//回血增加 
		if (hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_6)) {
			hpr += 15;
		}
		//回血增加  end
		if (!_hprRunning && hprInterval > 0 && hpr > 0) {
			_hprTimer = new HprTimer(hpr);
			L1NpcRegenerationTimer.getInstance().scheduleAtFixedRate(_hprTimer,
					hprInterval, hprInterval);
			_hprRunning = true;
		}
	}

	public final void stopHpRegeneration() {
		if (_hprRunning) {
			_hprTimer.cancel();
			_hprRunning = false;
		}
	}

	// MP自然回复
	public final void startMpRegeneration() {
		int mprInterval = getNpcTemplate().get_mprinterval();
		int mpr = getNpcTemplate().get_mpr();
		//回魔增加 
		if (hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_7)) {
			mpr += 3;
		}
		//回魔增加  end
		if (!_mprRunning && mprInterval > 0 && mpr > 0) {
			_mprTimer = new MprTimer(mpr);
			L1NpcRegenerationTimer.getInstance().scheduleAtFixedRate(_mprTimer,
					mprInterval, mprInterval);
			_mprRunning = true;
		}
	}

	public final void stopMpRegeneration() {
		if (_mprRunning) {
			_mprTimer.cancel();
			_mprRunning = false;
		}
	}

	// ■■■■■■■■■■■■ 关连 ■■■■■■■■■■

	// ＨＰ自然回复
	private boolean _hprRunning = false;

	private HprTimer _hprTimer;

	class HprTimer extends TimerTask {
		@Override
		public void run() {
			try {
				if ((!_destroyed && !isDead())
						&& (getCurrentHp() > 0 && getCurrentHp() < getMaxHp())) {
					setCurrentHp(getCurrentHp() + _point);
				} else {
					cancel();
					_hprRunning = false;
				}
			} catch (Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}

		public HprTimer(int point) {
			if (point < 1) {
				point = 1;
			}
			_point = point;
		}

		private final int _point;
	}

	// ＭＰ自然回复
	private boolean _mprRunning = false;

	private MprTimer _mprTimer;

	class MprTimer extends TimerTask {
		@Override
		public void run() {
			try {
				if ((!_destroyed && !isDead())
						&& (getCurrentHp() > 0 && getCurrentMp() < getMaxMp())) {
					setCurrentMp(getCurrentMp() + _point);
				} else {
					cancel();
					_mprRunning = false;
				}
			} catch (Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}

		public MprTimer(int point) {
			if (point < 1) {
				point = 1;
			}
			_point = point;
		}

		private final int _point;
	}

	// 消化
	private Map<Integer, Integer> _digestItems;
	public boolean _digestItemRunning = false;

	class DigestItemTimer implements Runnable {
		@Override
		public void run() {
			_digestItemRunning = true;
			while (!_destroyed && _digestItems.size() > 0) {
				try {
					Thread.sleep(1000);
				} catch (Exception exception) {
					break;
				}
				if (_digestItems.isEmpty()) {
					break;
				}
				Object[] keys = _digestItems.keySet().toArray();
				for (int i = 0; i < keys.length; i++) {
					Integer key = (Integer) keys[i];
					Integer digestCounter = _digestItems.get(key);
					digestCounter -= 1;
					if (digestCounter <= 0) {
						_digestItems.remove(key);
						L1ItemInstance digestItem = getInventory().getItem(key);
						if (digestItem != null) {
							getInventory().removeItem(digestItem,
									digestItem.getCount());
						}
					} else {
						_digestItems.put(key, digestCounter);
					}
				}
			}
			_digestItemRunning = false;
		}
	}

	// ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■

	public L1NpcInstance(L1Npc template) {
		setStatus(0);
		setMoveSpeed(0);
		setDead(false);
		setreSpawn(false);
		if (template != null) {
			setOldNpcID(template.get_npcId());
			setting_template(template);
		}
	}

	// 指定各种值初期化
	public void setting_template(L1Npc template) {
		_npcTemplate = template;
		int randomlevel = 0;
		double rate = 0;
		double diff = 0;
		setName(template.get_name());
		setNameId(template.get_nameid());
		if (template.get_randomlevel() == 0) { // Lv指定
			setLevel(template.get_level());
		} else { // Lv指定（最小值:get_level(),最大值:get_randomlevel()）
			randomlevel = _random.nextInt(
					template.get_randomlevel() - template.get_level() + 1);
			diff = template.get_randomlevel() - template.get_level();
			rate = randomlevel / diff;
			randomlevel += template.get_level();
			setLevel(randomlevel);
		}
		if (template.get_randomhp() == 0) {
			setMaxHp(template.get_hp());
			setCurrentHpDirect(template.get_hp());
		} else {
			double randomhp = rate
					* (template.get_randomhp() - template.get_hp());
			int hp = (int) (template.get_hp() + randomhp);
			setMaxHp(hp);
			setCurrentHpDirect(hp);
		}
		if (template.get_randommp() == 0) {
			setMaxMp(template.get_mp());
			setCurrentMpDirect(template.get_mp());
		} else {
			double randommp = rate
					* (template.get_randommp() - template.get_mp());
			int mp = (int) (template.get_mp() + randommp);
			setMaxMp(mp);
			setCurrentMpDirect(mp);
		}
		if (template.get_randomac() == 0) {
			setAc(template.get_ac());
		} else {
			double randomac = rate
					* (template.get_randomac() - template.get_ac());
			int ac = (int) (template.get_ac() + randomac);
			setAc(ac);
		}
		if (template.get_randomlevel() == 0) {
			setStr(template.get_str());
			setCon(template.get_con());
			setDex(template.get_dex());
			setInt(template.get_int());
			setWis(template.get_wis());
			setMr(template.get_mr());
		} else {
			setStr((byte) Math.min(template.get_str() + diff, 127));
			setCon((byte) Math.min(template.get_con() + diff, 127));
			setDex((byte) Math.min(template.get_dex() + diff, 127));
			setInt((byte) Math.min(template.get_int() + diff, 127));
			setWis((byte) Math.min(template.get_wis() + diff, 127));
			setMr((byte) Math.min(template.get_mr() + diff, 127));

			addHitup((int) diff * 2);
			addDmgup((int) diff * 2);
		}
		setPassispeed(template.get_passispeed());
		setAtkspeed(template.get_atkspeed());
		//特殊物理延迟时间 
		setAtkexspeed(template.get_atkexspeed());
		//特殊物理延迟时间  end
		setAgro(template.is_agro());
		setAgrocoi(template.is_agrocoi());
		setAgrososc(template.is_agrososc());
		setTempCharGfx(template.get_gfxid());
		setGfxId(template.get_gfxid());
		setStatus(L1NpcDefaultAction.get().getStatus(getTempCharGfx()));
		// 移動
		if (template.get_passispeed() != 0) {
			setPassispeed(SprTable.get().getSprSpeed(getTempCharGfx(),
					getStatus()));
		} else {
			setPassispeed(0);
		}
		// 攻擊
		if (template.get_atkspeed() != 0) {
			int actid = (getStatus() + 1);
			if (L1NpcDefaultAction.get().getDefaultAttack(getTempCharGfx()) != actid) {
				actid = L1NpcDefaultAction.get().getDefaultAttack(
						getTempCharGfx());
			}
			setAtkspeed(SprTable.get().getSprSpeed(getTempCharGfx(), actid));
			setAtkexspeed(SprTable.get().getSprSpeed(getTempCharGfx(), actid)/2);
		} else {
			setAtkspeed(0);
			setAtkexspeed(0);
		}
		if (template.get_randomexp() == 0) {
			setExp(template.get_exp());
		} else {
			setExp(template.get_randomexp() + randomlevel);
		}
		if (template.get_randomlawful() == 0) {
			setLawful(template.get_lawful());
			setTempLawful(template.get_lawful());
		} else {
			double randomlawful = rate
					* (template.get_randomlawful() - template.get_lawful());
			int lawful = (int) (template.get_lawful() + randomlawful);
			setLawful(lawful);
			setTempLawful(lawful);
		}
		setPickupItem(template.is_picupitem());
		if (template.is_bravespeed()) {
			setBraveSpeed(1);
		} else {
			setBraveSpeed(0);
		}
		if (template.get_digestitem() > 0) {
			_digestItems = new HashMap<Integer, Integer>();
		}
		setKarma(template.getKarma());
		setLightSize(template.getLightSize());
		int fireattr = template.get_weakwater() * 50;
		int earthattr = template.get_weakfire() * 50;
		int windattr = template.get_weakearth() * 50;
		int  waterattr = template.get_weakwind() * 50;
		addFire(fireattr);
		addWater(waterattr);
		addWind(windattr);
		addEarth(earthattr);
		
		addFire(-earthattr);
		addWater(-fireattr);
		addWind(-waterattr);
		addEarth(-windattr);
		if (template.talk()) {// NPC对话判断
			TALK = template.getNpcExecutor();
		}
		if (template.action()) {// NPC对话执行
			ACTION = template.getNpcExecutor();
		}
		if (template.attack()) {// NPC受到攻击
			ATTACK = template.getNpcExecutor();
		}
		if (template.death()) {// NPC死亡
			DEATH = template.getNpcExecutor();
		}
		if (template.work()) {// NPC工作时间
			WORK = template.getNpcExecutor();
			if (WORK.workTime() != 0) {
				// 加入NPC工作列队
				NpcWorkTimer.put(this, WORK.workTime());
				
			} else {// 工作时间设置为0
				// 执行一次
				WORK.work(this);
			}
		}
		if (template.spawn()) {// NPC召唤
			SPAWN = template.getNpcExecutor();
			SPAWN.spawn(this);
		}
		mobSkill = new L1MobSkillUse(this);
	}

	private int _passispeed;

	public int getPassispeed() {
		return _passispeed;
	}

	public void setPassispeed(int i) {
		_passispeed = i;
	}

	private int _atkspeed;

	public int getAtkspeed() {
		return _atkspeed;
	}

	public void setAtkspeed(int i) {
		_atkspeed = i;
	}
	
	//特殊物理攻击延迟时间 
	private int _atkexspeed;

	public int getAtkexspeed() {
		return _atkexspeed;
	}

	public void setAtkexspeed(int i) {
		_atkexspeed = i;
	}
	//特殊物理攻击延迟时间  end

	private boolean _pickupItem;

	public boolean isPickupItem() {
		return _pickupItem;
	}

	public void setPickupItem(boolean flag) {
		_pickupItem = flag;
	}

	@Override
	public L1Inventory getInventory() {
		return _inventory;
	}

	public void setInventory(L1Inventory inventory) {
		_inventory = inventory;
	}

	public L1Npc getNpcTemplate() {
		return _npcTemplate;
	}

	public int getNpcId() {
		return _npcTemplate.get_npcId();
	}
	
	public void setPetcost(int i) {
		_petcost = i;
	}

	public int getPetcost() {
		return _petcost;
	}

	public void setSpawn(L1Spawn spawn) {
		_spawn = spawn;
	}
	
	public void setSpawnNumber(int number) {
		_spawnNumber = number;
	}

	public void onDecay() {
		_spawn.executeSpawnTask(_spawnNumber);
	}

	@Override
	public void onPerceive(L1PcInstance perceivedFrom) {
		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_NPCPack(this));
		onNpcAI();
	}

	public void deleteMe() {
		//WriteLogTxt.Recording("调试专用", "启动了删除方法");
		_destroyed = true;
		if (getInventory() != null) {
			getInventory().clearItems();
		}
		allTargetClear();
		_master = null;
		L1World.getInstance().removeVisibleObject(this);
		L1World.getInstance().removeWorldObject(this);
		List<L1PcInstance> players = L1World.getInstance().getRecognizePlayer(
				this);
		if (players.size() > 0) {
			S_RemoveObject s_deleteNewObject = new S_RemoveObject(this);
			for (L1PcInstance pc : players) {
				if (pc != null) {
					pc.removeKnownObject(this);
					// if(!L1Character.distancepc(user, this))
					pc.sendPackets(s_deleteNewObject);
				}
			}
		}
		removeAllKnownObjects();
//		boolean isboss = false;
		try {
			if (_spawn !=null) {
				// BOSS召喚列表中物件(NPCID)
				L1Spawn spawn = SpawnBossReading.get().getTemplate(_spawn.getId());
				if (spawn != null) {
					long newTime = (_spawn.get_spawnInterval() * 60 * 1000);
					final Calendar cals = Calendar.getInstance();
					if (this.getNpcId() == 45681){
						cals.add(Calendar.DATE, 1);//+ 一天
						cals.set(Calendar.HOUR_OF_DAY, 22);
						cals.set(Calendar.MINUTE, 0);
						cals.set(Calendar.SECOND, 0);
						_spawn.setShuaXin(cals.getTimeInMillis());
					}else if (this.getNpcId() == 45683){
						cals.add(Calendar.DATE, 1);//+ 一天
						cals.set(Calendar.HOUR_OF_DAY, 23);
						cals.set(Calendar.MINUTE, 0);
						cals.set(Calendar.SECOND, 0);
						_spawn.setShuaXin(cals.getTimeInMillis());
					}else if (this.getNpcId() == 45684){
						cals.add(Calendar.DATE, 1);//+ 一天
						cals.set(Calendar.HOUR_OF_DAY, 19);
						cals.set(Calendar.MINUTE, 30);
						cals.set(Calendar.SECOND, 0);
						_spawn.setShuaXin(cals.getTimeInMillis());
					}else{
						final long nowtimeMillis = System.currentTimeMillis() + newTime;
						cals.setTimeInMillis(nowtimeMillis);
	                    _spawn.setShuaXin(nowtimeMillis);
					}
					_spawn.set_nextSpawnTime(cals);
					SpawnBossReading.get().upDateNextSpawnTime(_spawn.getId(), cals);
					// 公告狀態
					switch (_spawn.getHeading()) {
					case 0:// 面相基礎設置為0公告死亡及下次出現時間
						Date spawnTime = _spawn.get_nextSpawnTime().getTime();
						final String nextTime = new SimpleDateFormat(
								"yyyy/MM/dd HH:mm").format(spawnTime);
						// 9:%s 已經死亡，下次出現時間：%s。
						L1World.getInstance().broadcastServerMessage(getName() + "已经死亡，下次出现时间"
										+ nextTime);
						break;

					case 1:// 面相基礎設置為1公告死亡
							// 8:%s 已經死亡!
						L1World.getInstance().broadcastServerMessage(getName() + "已经死亡。");
						break;

					default:
						break;
					}
//					isboss = true;
					// }
					if (_lastAttacker != null) {
						if (_lastAttacker instanceof L1PcInstance) {
							WriteLogTxt.Recording("调试专用", "怪物"
									+getName()
									+"被玩家"+_lastAttacker.getName()+"杀死了！");
						}else {
							if (_lastAttacker instanceof L1NpcInstance) {
								L1NpcInstance attacknpc = (L1NpcInstance)_lastAttacker;
								if (attacknpc.getMaster()!=null) {
									WriteLogTxt.Recording("调试专用", "怪物"
											+getName()
											+"被玩家"+attacknpc.getMaster().getName()+"杀死了！");
								}else {
									WriteLogTxt.Recording("调试专用", "怪物"
											+getName()
											+"被怪物"+attacknpc.getName()+"杀死了！");
								}
							}
						}
					}else {
						WriteLogTxt.Recording("调试专用", "怪物"
								+getName()
								+"被杀死了！");
					}
					
				}
			}
			

		} catch (final Exception e) {
			// 手動召喚BOSS 因為不具備召喚時間數據
			// 因此忽略此錯誤訊息
			// _log.error(e.getLocalizedMessage(), e);
		}

		// 是否具有隊伍
		final L1MobGroupInfo mobGroupInfo = this.getMobGroupInfo();
		if (mobGroupInfo == null) {
			/*
			 * if (this.isReSpawn()) { this.onDecay(true); }
			 */

		} else {
			// 剩餘隊員數量為0
			if (mobGroupInfo.removeMember(this) == 0) {
				this.setMobGroupInfo(null);
				/*
				 * if (this.isReSpawn()) { this.onDecay(false); }
				 */
			}
		}
		if (isReSpawn()/*&&!isboss*/) {
			onDecay(); // 设定
		}
	}

	public void ReceiveManaDamage(L1Character attacker, int damageMp) {
	}

	public void receiveDamage(L1Character attacker, int damage) {
	}

	public void setDigestItem(L1ItemInstance item) {
		_digestItems.put(new Integer(item.getId()), new Integer(
				getNpcTemplate().get_digestitem()));
		if (!_digestItemRunning) {
			DigestItemTimer digestItemTimer = new DigestItemTimer();
			GeneralThreadPool.getInstance().execute(digestItemTimer);
		}
	}

	public void onGetItem(L1ItemInstance item) {
		refineItem();
		getInventory().shuffle();
		if (getNpcTemplate().get_digestitem() > 0) {
			setDigestItem(item);
		}
	}

	public void approachPlayer(L1PcInstance pc) {
		if (pc.hasSkillEffect(60) || pc.hasSkillEffect(97)) { // 、中
			return;
		}
		if (getHiddenStatus() == HIDDEN_STATUS_SINK) {
			if (getCurrentHp() == getMaxHp()) {
				if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 2) {
					appearOnGround(pc);
				}
			}
		} else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
			// 飞天怪物判断修正 
			if (getCurrentHp() == getMaxHp()) {
				if (pc.getLocation().getTileLineDistance(this.getLocation()) <= 1) {
					appearOnGround(pc);
				}
			} else {
				searchItem();
				if (_targetItem != null /*&& getNpcTemplate().get_npcId() != 45681*/) {
					appearOnGround(pc);
				}
			}
			// 飞天怪物判断修正  end
		}
	}

	public void appearOnGround(L1PcInstance pc) {
		if (getHiddenStatus() == HIDDEN_STATUS_SINK) {
			setHiddenStatus(HIDDEN_STATUS_NONE);
			broadcastPacket(new S_DoActionGFX(getId(),
					ActionCodes.ACTION_Appear));
			setStatus(0);
			broadcastPacket(new S_NPCPack(this));
			if (!pc.hasSkillEffect(60) && !pc.hasSkillEffect(97) // 、中以外、GM以外
					&& !pc.isGm()) {
				_hateList.add(pc, 0);
				_target = pc;
			}
			onNpcAI(); // ＡＩ开始
		} else if (getHiddenStatus() == HIDDEN_STATUS_FLY) {
			setHiddenStatus(HIDDEN_STATUS_NONE);
			broadcastPacket(new S_DoActionGFX(getId(),
					ActionCodes.ACTION_Movedown));
			setStatus(0);
			broadcastPacket(new S_NPCPack(this));
			if (!pc.hasSkillEffect(60) && !pc.hasSkillEffect(97) // 、中以外、GM以外
					&& !pc.isGm()) {
				_hateList.add(pc, 0);
				_target = pc;
			}
			onNpcAI(); // ＡＩ开始
		}
	}

	// ■■■■■■■■■■■■■ 移动关连 ■■■■■■■■■■■

	// 指定方向移动
	public void setDirectionMove(int dir) {
		if (dir >= 0) {
			int nx = 0;
			int ny = 0;

			switch (dir) {
			case 1:
				nx = 1;
				ny = -1;
				setHeading(1);
				break;

			case 2:
				nx = 1;
				ny = 0;
				setHeading(2);
				break;

			case 3:
				nx = 1;
				ny = 1;
				setHeading(3);
				break;

			case 4:
				nx = 0;
				ny = 1;
				setHeading(4);
				break;

			case 5:
				nx = -1;
				ny = 1;
				setHeading(5);
				break;

			case 6:
				nx = -1;
				ny = 0;
				setHeading(6);
				break;

			case 7:
				nx = -1;
				ny = -1;
				setHeading(7);
				break;

			case 0:
				nx = 0;
				ny = -1;
				setHeading(0);
				break;

			default:
				break;

			}

			getMap().setPassable(getLocation(), true);

			int nnx = getX() + nx;
			int nny = getY() + ny;
			setX(nnx);
			setY(nny);

			if (this instanceof L1BabyInstance||this instanceof L1HierarchInstance) {
				getMap().setPassable(getLocation(), true);
			}else {
				getMap().setPassable(getLocation(), false);
			}
			broadcastPacket(new S_MoveCharPacket(this));

			// movement_distance以上离
			if (getMovementDistance() > 0) {
				if (this instanceof L1GuardInstance || this instanceof L1CastleGuardInstance //守城警卫 
						|| this instanceof L1MerchantInstance
						|| this instanceof L1MonsterInstance
						|| this instanceof L1GuardianInstance) {
					if (getLocation().getLineDistance(
							new Point(getHomeX(), getHomeY())) > getMovementDistance()) {
						teleport(getHomeX(), getHomeY(), getHeading());
					}
				}
			}
		}
	}

	public int moveDirection(int x, int y) // 目标点Ｘ 目标点Ｙ
	{
		return moveDirection(x, y, getLocation().getLineDistance(
				new Point(x, y)));
	}
	
	public int moveGamDirection(int x, int y) // 目标点Ｘ 目标点Ｙ
	{
		return moveGamDirection(x, y, getLocation().getLineDistance(
				new Point(x, y)));
	}
	
	public int moveGamDirection(int x, int y, double d) {
		int dir = 0;
		dir = targetDirection(x, y);
		return dir;
	}

	// 目标距离应最适思进方向返
	public int moveDirection(int x, int y, double d) // 目标点Ｘ 目标点Ｙ 目标距离
	{
		int dir = 0;
		if (((hasSkillEffect(20) == true) || (hasSkillEffect(40) == true) || (hasSkillEffect(103) == true)) && d >= 2D) // 挂、距离2以上场合追迹终了
		{//补上20、103 
			return -1;
		} else if (d > 30D) // 距离激远场合追迹终了
		{
			return -1;
		} else if (d > courceRange) // 距离远场合单纯计算
		{
			dir = targetDirection(x, y);
			dir = checkObject(getX(), getY(), getMapId(), dir);
			_exsistCharacterBetweenTarget(dir);
		} else // 目标最短经路探索
		{
			dir = _serchCource(x, y);
			if (!_exsistCharacterBetweenTarget(dir)) {
				if (dir == -1) // 目标经路场合近
				{
					dir = targetDirection(x, y);
					dir = checkObject(getX(), getY(), getMapId(), dir);
				}
			}		
		}
		return dir;
	}

/*	// 修正黑暗之影效果 
	public int checkTarget(int x, int y, double d) {
		if (((hasSkillEffect(20) == true) || (hasSkillEffect(40) == true) || (hasSkillEffect(103) == true)) && d >= 2D) {
			return -1;
		}
		return 0;
	}
	// 修正黑暗之影效果  end
*/
	// 目标逆方向返
	public int targetReverseDirection(int tx, int ty) // 目标点Ｘ 目标点Ｙ
	{
		int dir = targetDirection(tx, ty);
		dir += 4;
		if (dir > 7) {
			dir -= 8;
		}
		return dir;
	}

	// 进方向障害物确认、场合前方斜左右确认后进方向返
	// ※从来处理、仕样省、目标反对（左右含）进
	public static int checkObject(int x, int y, short m, int d) // 起点Ｘ 起点Ｙ ＩＤ
	// 进行方向
	{
		L1Map map = L1WorldMap.getInstance().getMap(m);
		if (d == 1) {
			if (map.isPassable(x, y, 1)) {
				return 1;
			} else if (map.isPassable(x, y, 0)) {
				return 0;
			} else if (map.isPassable(x, y, 2)) {
				return 2;
			}
		} else if (d == 2) {
			if (map.isPassable(x, y, 2)) {
				return 2;
			} else if (map.isPassable(x, y, 1)) {
				return 1;
			} else if (map.isPassable(x, y, 3)) {
				return 3;
			}
		} else if (d == 3) {
			if (map.isPassable(x, y, 3)) {
				return 3;
			} else if (map.isPassable(x, y, 2)) {
				return 2;
			} else if (map.isPassable(x, y, 4)) {
				return 4;
			}
		} else if (d == 4) {
			if (map.isPassable(x, y, 4)) {
				return 4;
			} else if (map.isPassable(x, y, 3)) {
				return 3;
			} else if (map.isPassable(x, y, 5)) {
				return 5;
			}
		} else if (d == 5) {
			if (map.isPassable(x, y, 5)) {
				return 5;
			} else if (map.isPassable(x, y, 4)) {
				return 4;
			} else if (map.isPassable(x, y, 6)) {
				return 6;
			}
		} else if (d == 6) {
			if (map.isPassable(x, y, 6)) {
				return 6;
			} else if (map.isPassable(x, y, 5)) {
				return 5;
			} else if (map.isPassable(x, y, 7)) {
				return 7;
			}
		} else if (d == 7) {
			if (map.isPassable(x, y, 7)) {
				return 7;
			} else if (map.isPassable(x, y, 6)) {
				return 6;
			} else if (map.isPassable(x, y, 0)) {
				return 0;
			}
		} else if (d == 0) {
			if (map.isPassable(x, y, 0)) {
				return 0;
			} else if (map.isPassable(x, y, 7)) {
				return 7;
			} else if (map.isPassable(x, y, 1)) {
				return 1;
			}
		}
		return -1;
	}
	
	/**
	 * 前進方向障礙者攻擊判斷
	 * 
	 * @param dir
	 * @return
	 */
	private boolean _exsistCharacterBetweenTarget(final int dir) {
		try {
			// 執行者非MOB
			if (!(this instanceof L1MonsterInstance)) {
				return false;
			}
			// 無首要目標
			if (_target == null) {
				return false;
			}

			final int locX = getX();
			final int locY = getY();

			final int targetX = locX + HEADING_TABLE_X[dir];
			;
			final int targetY = locY + HEADING_TABLE_Y[dir];

			final ArrayList<L1Object> objects = L1World.getInstance().getVisibleObjects(
					this, 1);
			for (final Iterator<L1Object> iter = objects.iterator(); iter
					.hasNext();) {
				final L1Object object = iter.next();
				boolean isCheck = false;
				// 判斷障礙
				if (object instanceof L1PcInstance) {// 障礙者是玩家
					final L1PcInstance pc = (L1PcInstance) object;
					if (!pc.isGhost()) { // 鬼魂模式排除
						isCheck = true;
					}

				} else if (object instanceof L1PetInstance) {// 障礙者是寵物
					isCheck = true;
				} else if (object instanceof L1SummonInstance) {// 障礙者是 召換獸
					isCheck = true;
				}
				if (isCheck) {
					if ((object.getX() == targetX)
							&& (object.getY() == targetY)
							&& (object.getMapId() == getMapId())) {
						// 重新設置障礙者為攻擊目標
						final L1Character cha = (L1Character) object;
						_hateList.add(cha, 0);
						_target = cha;
						return true;
					}
				}
			}

		} catch (final Exception e) {
			//_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}
	
	public L1Character getTarget(){
		return _target;
	}
	
	private static final byte HEADING_TABLE_X[] = { 0, 1, 1, 1, 0, -1, -1, -1 };

	private static final byte HEADING_TABLE_Y[] = { -1, -1, 0, 1, 1, 1, 0, -1 };

	// 目标最短经路方向返
	// ※目标中心探索范围探索
	private int _serchCource(int x, int y) // 目标点Ｘ 目标点Ｙ
	{
		int i;
		int locCenter = courceRange + 1;
		int diff_x = x - locCenter; // Ｘ实际差
		int diff_y = y - locCenter; // Ｙ实际差
		int[] locBace = { getX() - diff_x, getY() - diff_y, 0, 0 }; // Ｘ Ｙ
		// 方向
		// 初期方向
		int[] locNext = new int[4];
		int[] locCopy;
		int[] dirFront = new int[5];
		boolean serchMap[][] = new boolean[locCenter * 2 + 1][locCenter * 2 + 1];
		LinkedList<int[]> queueSerch = new LinkedList<int[]>();

		// 探索用设定
		for (int j = courceRange * 2 + 1; j > 0; j--) {
			for (i = courceRange - Math.abs(locCenter - j); i >= 0; i--) {
				serchMap[j][locCenter + i] = true;
				serchMap[j][locCenter - i] = true;
			}
		}

		// 初期方向设置
		int[] firstCource = { 2, 4, 6, 0, 1, 3, 5, 7 };
		for (i = 0; i < 8; i++) {
			System.arraycopy(locBace, 0, locNext, 0, 4);
			_moveLocation(locNext, firstCource[i]);
			if (locNext[0] - locCenter == 0 && locNext[1] - locCenter == 0) {
				// 最短经路见场合:邻
				return firstCource[i];
			}
			if (serchMap[locNext[0]][locNext[1]]) {
				int tmpX = locNext[0] + diff_x;
				int tmpY = locNext[1] + diff_y;
				boolean found = false;
				if (i == 0) {
					found = getMap().isPassable(tmpX, tmpY + 1, i);
				} else if (i == 1) {
					found = getMap().isPassable(tmpX - 1, tmpY + 1, i);
				} else if (i == 2) {
					found = getMap().isPassable(tmpX - 1, tmpY, i);
				} else if (i == 3) {
					found = getMap().isPassable(tmpX - 1, tmpY - 1, i);
				} else if (i == 4) {
					found = getMap().isPassable(tmpX, tmpY - 1, i);
				} else if (i == 5) {
					found = getMap().isPassable(tmpX + 1, tmpY - 1, i);
				} else if (i == 6) {
					found = getMap().isPassable(tmpX + 1, tmpY, i);
				} else if (i == 7) {
					found = getMap().isPassable(tmpX + 1, tmpY + 1, i);
				}
				if (found)// 移动经路场合
				{
					locCopy = new int[4];
					System.arraycopy(locNext, 0, locCopy, 0, 4);
					locCopy[2] = firstCource[i];
					locCopy[3] = firstCource[i];
					queueSerch.add(locCopy);
				}
				serchMap[locNext[0]][locNext[1]] = false;
			}
		}
		locBace = null;

		// 最短经路探索
		while (queueSerch.size() > 0) {
			locBace = queueSerch.removeFirst();
			_getFront(dirFront, locBace[2]);
			for (i = 4; i >= 0; i--) {
				System.arraycopy(locBace, 0, locNext, 0, 4);
				_moveLocation(locNext, dirFront[i]);
				if (locNext[0] - locCenter == 0 && locNext[1] - locCenter == 0) {
					return locNext[3];
				}
				if (serchMap[locNext[0]][locNext[1]]) {
					int tmpX = locNext[0] + diff_x;
					int tmpY = locNext[1] + diff_y;
					boolean found = false;
					if (i == 0) {
						found = getMap().isPassable(tmpX, tmpY + 1, i);
					} else if (i == 1) {
						found = getMap().isPassable(tmpX - 1, tmpY + 1, i);
					} else if (i == 2) {
						found = getMap().isPassable(tmpX - 1, tmpY, i);
					} else if (i == 3) {
						found = getMap().isPassable(tmpX - 1, tmpY - 1, i);
					} else if (i == 4) {
						found = getMap().isPassable(tmpX, tmpY - 1, i);
					}
					if (found) // 移动经路场合
					{
						locCopy = new int[4];
						System.arraycopy(locNext, 0, locCopy, 0, 4);
						locCopy[2] = dirFront[i];
						queueSerch.add(locCopy);
					}
					serchMap[locNext[0]][locNext[1]] = false;
				}
			}
			locBace = null;
		}
		return -1; // 目标经路场合
	}

	private void _moveLocation(int[] ary, int d) {
		if (d == 1) {
			ary[0] = ary[0] + 1;
			ary[1] = ary[1] - 1;
		} else if (d == 2) {
			ary[0] = ary[0] + 1;
		} else if (d == 3) {
			ary[0] = ary[0] + 1;
			ary[1] = ary[1] + 1;
		} else if (d == 4) {
			ary[1] = ary[1] + 1;
		} else if (d == 5) {
			ary[0] = ary[0] - 1;
			ary[1] = ary[1] + 1;
		} else if (d == 6) {
			ary[0] = ary[0] - 1;
		} else if (d == 7) {
			ary[0] = ary[0] - 1;
			ary[1] = ary[1] - 1;
		} else if (d == 0) {
			ary[1] = ary[1] - 1;
		}
		ary[2] = d;
	}

	private void _getFront(int[] ary, int d) {
		if (d == 1) {
			ary[4] = 2;
			ary[3] = 0;
			ary[2] = 1;
			ary[1] = 3;
			ary[0] = 7;
		} else if (d == 2) {
			ary[4] = 2;
			ary[3] = 4;
			ary[2] = 0;
			ary[1] = 1;
			ary[0] = 3;
		} else if (d == 3) {
			ary[4] = 2;
			ary[3] = 4;
			ary[2] = 1;
			ary[1] = 3;
			ary[0] = 5;
		} else if (d == 4) {
			ary[4] = 2;
			ary[3] = 4;
			ary[2] = 6;
			ary[1] = 3;
			ary[0] = 5;
		} else if (d == 5) {
			ary[4] = 4;
			ary[3] = 6;
			ary[2] = 3;
			ary[1] = 5;
			ary[0] = 7;
		} else if (d == 6) {
			ary[4] = 4;
			ary[3] = 6;
			ary[2] = 0;
			ary[1] = 5;
			ary[0] = 7;
		} else if (d == 7) {
			ary[4] = 6;
			ary[3] = 0;
			ary[2] = 1;
			ary[1] = 5;
			ary[0] = 7;
		} else if (d == 0) {
			ary[4] = 2;
			ary[3] = 6;
			ary[2] = 0;
			ary[1] = 1;
			ary[0] = 7;
		}
	}

	// ■■■■■■■■■■■■ 关连 ■■■■■■■■■■

	private void useHealPotion(int healHp, int effectId) {
		broadcastPacket(new S_SkillSound(getId(), effectId));
		if (this.hasSkillEffect(L1SkillId.POLLUTE_WATER)) { // 中回复量1/2倍
			healHp /= 2;
		}
		if (this instanceof L1PetInstance) {
			((L1PetInstance) this).setCurrentHp(getCurrentHp() + healHp);
		} else if (this instanceof L1SummonInstance) {
			((L1SummonInstance) this).setCurrentHp(getCurrentHp() + healHp);
		} else {
			setCurrentHpDirect(getCurrentHp() + healHp);
		}
	}

	private void useHastePotion(int time) {
		broadcastPacket(new S_SkillHaste(getId(), 1, time));
		broadcastPacket(new S_SkillSound(getId(), 191));
		setMoveSpeed(1);
		setSkillEffect(L1SkillId.STATUS_HASTE, time * 1000);
	}

	// 使用判定及使用
	public static final int USEITEM_HEAL = 0;
	public static final int USEITEM_HASTE = 1;
	//宠物活力、魔力药水 
	public static final int USEITEM_PETHPPOTIONS = 2;
	public static final int USEITEM_PETMPPOTIONS = 3;
	//宠物活力、魔力药水  end
	public static int[] healPotions = { POTION_OF_GREATER_HEALING,
			POTION_OF_EXTRA_HEALING, POTION_OF_HEALING };
	public static int[] haestPotions = { B_POTION_OF_GREATER_HASTE_SELF,
			POTION_OF_GREATER_HASTE_SELF, B_POTION_OF_HASTE_SELF,
			POTION_OF_HASTE_SELF };
	//宠物活力、魔力药水 
	public static int[] petHpPotions = { l1j.william.New_Id.Item_AJ_19 };
	public static int[] petMpPotions = { l1j.william.New_Id.Item_AJ_20 };
	//宠物活力、魔力药水  end

	public void useItem(L1ItemInstance item, int type, int chance) { // 使用种类 使用可能性(％)
		if (hasSkillEffect(71)) {
			return; //  状态
		}
		if (item != null) {
			int delay_id = 0;
			if (item.getItem().getType2() == 0) { // 种别：他
				delay_id = ((L1EtcItem) item.getItem()).get_delayid();
			}
			if (delay_id != 0) { // 设定
				if (hasItemDelay(delay_id) == true) {
					return;
				}
			}
		}
		

		Random random = new Random();
		if (random.nextInt(100) > chance) {
			return; // 使用可能性
		}

		if (type == USEITEM_HEAL) { // 回复系
			// 回复量大顺
			if (getInventory().consumeItem(POTION_OF_GREATER_HEALING, 1)) {
				useHealPotion(75, 197);
			} else if (getInventory().consumeItem(POTION_OF_EXTRA_HEALING, 1)) {
				useHealPotion(45, 194);
			} else if (getInventory().consumeItem(POTION_OF_HEALING, 1)) {
				useHealPotion(15, 189);
			}
		} else if (type == USEITEM_HASTE) { // 系
			if (hasSkillEffect(1001) || hasSkillEffect(43)) {//补上加速术判断 
				return; // 状态
			}

			// 效果长顺
			if (getInventory().consumeItem(B_POTION_OF_GREATER_HASTE_SELF, 1)) {
				useHastePotion(2100);
			} else if (getInventory().consumeItem(POTION_OF_GREATER_HASTE_SELF,
					1)) {
				useHastePotion(1800);
			} else if (getInventory().consumeItem(B_POTION_OF_HASTE_SELF, 1)) {
				useHastePotion(350);
			} else if (getInventory().consumeItem(POTION_OF_HASTE_SELF, 1)) {
				useHastePotion(300);
			}
		} 
		//宠物活力、魔力药水 
		else if (type == USEITEM_PETHPPOTIONS) {
			if (hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_6)) {
				return; // 状态
			}
			if (getInventory().consumeItem(60301, 1)) {
				broadcastPacket(new S_SkillSound(getId(), 192));
				setSkillEffect(l1j.william.New_Id.Skill_AJ_1_6, 600 * 1000);
				stopHpRegeneration();
				if (getMaxHp() > getCurrentHp()) {
					startHpRegeneration();
				}
			}
		} else if (type == USEITEM_PETMPPOTIONS) {
			if (hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_7)) {
				return; // 状态
			}
			if (getInventory().consumeItem(60302, 1)) {
				broadcastPacket(new S_SkillSound(getId(), 196));
				setSkillEffect(l1j.william.New_Id.Skill_AJ_1_7, 600 * 1000);
				stopMpRegeneration();
				if (getMaxMp() > getCurrentMp()) {
					startMpRegeneration();
				}
			}
		}
		if (item != null) {
			L1ItemDelay.onItemUse(this, item);
		}	
		//宠物活力、魔力药水  end
	}

	// ■■■■■■■■■■■■■ 关连(npcskills实装消) ■■■■■■■■■■■

	// 目标邻
	public boolean nearTeleport(int nx, int ny) {
		int rdir = _random.nextInt(8);
		int dir;
		for (int i = 0; i < 8; i++) {
			dir = rdir + i;
			if (dir > 7) {
				dir -= 8;
			}
			if (dir == 1) {
				nx++;
				ny--;
			} else if (dir == 2) {
				nx++;
			} else if (dir == 3) {
				nx++;
				ny++;
			} else if (dir == 4) {
				ny++;
			} else if (dir == 5) {
				nx--;
				ny++;
			} else if (dir == 6) {
				nx--;
			} else if (dir == 7) {
				nx--;
				ny--;
			} else if (dir == 0) {
				ny--;
			}
			if (getMap().isPassable(nx, ny)) {
				dir += 4;
				if (dir > 7) {
					dir -= 8;
				}
				teleport(nx, ny, dir);
				setCurrentMp(getCurrentMp() - 10);
				return true;
			}
		}
		return false;
	}

	// 目标
	public void teleport(int nx, int ny, int dir) {
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			pc.sendPackets(new S_SkillSound(getId(), 169));
			pc.sendPackets(new S_RemoveObject(this));
			pc.removeKnownObject(this);
		}
		setX(nx);
		setY(ny);
		setHeading(dir);
	}

	// ----------From L1Character-------------
	private String _nameId; // ● ＩＤ

	public String getNameId() {
		return _nameId;
	}

	public void setNameId(String s) {
		_nameId = s;
	}

	private boolean _Agro; // ● 

	public boolean isAgro() {
		return _Agro;
	}

	public void setAgro(boolean flag) {
		_Agro = flag;
	}

	private boolean _Agrocoi; // ● 

	public boolean isAgrocoi() {
		return _Agrocoi;
	}

	public void setAgrocoi(boolean flag) {
		_Agrocoi = flag;
	}

	private boolean _Agrososc; // ● 变身

	public boolean isAgrososc() {
		return _Agrososc;
	}

	public void setAgrososc(boolean flag) {
		_Agrososc = flag;
	}

	private int _homeX; // ● Ｘ（戾位置警戒位置）

	public int getHomeX() {
		return _homeX;
	}

	public void setHomeX(int i) {
		_homeX = i;
	}

	private int _homeY; // ● Ｙ（戾位置警戒位置）

	public int getHomeY() {
		return _homeY;
	}

	public void setHomeY(int i) {
		_homeY = i;
	}

	private boolean _reSpawn; // ● 再

	public boolean isReSpawn() {
		return _reSpawn;
	}

	public void setreSpawn(boolean flag) {
		_reSpawn = flag;
	}

	private int _lightSize; // ●  ０． １～１４．大

	public int getLightSize() {
		return _lightSize;
	}

	public void setLightSize(int i) {
		_lightSize = i;
	}

	private boolean _weaponBreaked; // ● 中

	public boolean isWeaponBreaked() {
		return _weaponBreaked;
	}

	public void setWeaponBreaked(boolean flag) {
		_weaponBreaked = flag;
	}

	private int _hiddenStatus; // ● 地中潜、空飞状态

	public int getHiddenStatus() {
		return _hiddenStatus;
	}

	public void setHiddenStatus(int i) {
		_hiddenStatus = i;
	}

	// 行动距离
	private int _movementDistance = 0;

	public int getMovementDistance() {
		return _movementDistance;
	}

	public void setMovementDistance(int i) {
		_movementDistance = i;
	}

	// 表示用
	private int _tempLawful = 0;

	public int getTempLawful() {
		return _tempLawful;
	}

	public void setTempLawful(int i) {
		_tempLawful = i;
	}

	protected int calcSleepTime(int i) {
		int sleepTime = i;
		switch (getMoveSpeed()) {
		case 0: // 通常
			break;
		case 1: // 
			sleepTime -= (sleepTime * 0.25);
			break;
		case 2: // 
			sleepTime *= 2;
			break;
		}
		if (getBraveSpeed() == 1) {
			sleepTime -= (sleepTime * 0.25);
		}
		return sleepTime;
	}

	protected void setAiRunning(boolean aiRunning) {
		_aiRunning = aiRunning;
	}

	public boolean isAiRunning() {
		return _aiRunning;
	}

	protected void setActived(boolean actived) {
		_actived = actived;
	}

	public boolean isActived() {
		return _actived;
	}

	protected void setFirstAttack(boolean firstAttack) {
		_firstAttack = firstAttack;
	}

	protected boolean isFirstAttack() {
		return _firstAttack;
	}

	protected void setSleepTime(int sleep_time) {
		_sleep_time = sleep_time;
	}

	protected int getSleepTime() {
		return _sleep_time;
	}

	protected void setDeathProcessing(boolean deathProcessing) {
		_deathProcessing = deathProcessing;
	}

	protected boolean isDeathProcessing() {
		return _deathProcessing;
	}

	public int drainMana(int drain) {
		if (_drainedMana >= Config.MANA_DRAIN_LIMIT_PER_NPC) {
			return 0;
		}
		int result = Math.min(drain, getCurrentMp());
		if (_drainedMana + result > Config.MANA_DRAIN_LIMIT_PER_NPC) {
			result = Config.MANA_DRAIN_LIMIT_PER_NPC - _drainedMana;
		}
		_drainedMana += result;
		return result;
	}

	public boolean _destroyed = false; // 破弃

	//家畜对话 
	public void onTalkAction(L1PcInstance player) {
		int npcid = getGfxId();

  		switch(npcid)
  		{
  			case 943://母鸡的编号
    			player.sendPackets(new S_NPCTalkReturn(getId(), "hen1"));
    		break;
  			case 1014://鸭子的编号
     			player.sendPackets(new S_NPCTalkReturn(getId(), "duck1"));
      		break;
  			case 941://猪的编号
      			player.sendPackets(new S_NPCTalkReturn(getId(), "pig1"));
      		break;
  			case 945://牛的编号
      			player.sendPackets(new S_NPCTalkReturn(getId(), "milkcow1"));
      		break;
      		default:
      		break;
  		}
	}
	//家畜对话  end
	
	// ※破弃后动强制的ＡＩ等处理中止（念）

	// NPC别NPC变场合处理
	public void transform(int transformId) {
		stopHpRegeneration();
		stopMpRegeneration();
		L1Npc npcTemplate = NpcTable.getInstance().getTemplate(transformId);
		setting_template(npcTemplate);

		broadcastPacket(new S_ChangeShape(getId(), getTempCharGfx()));
		for (L1PcInstance pc : L1World.getInstance().getRecognizePlayer(this)) {
			onPerceive(pc);
		}

	}

	public void setRest(boolean _rest) {
		this._rest = _rest;
	}

	public boolean isRest() {
		return _rest;
	}
	
	private boolean _isResurrect;

	public boolean isResurrect() {
		return _isResurrect;
	}

	public void setResurrect(boolean flag) {
		_isResurrect = flag;
	}
	
	@Override
	public synchronized void resurrect(int hp) {
		if (_destroyed) {
			return;
		}
		if (_deleteTask != null) {
			if (!_future.cancel(false)) { // 
				return;
			}
			_deleteTask = null;
			_future = null;
		}
		super.resurrect(hp);
	}

	// 死消时间计测用
	private DeleteTimer _deleteTask;
	private ScheduledFuture<?> _future = null;

	private static final long DELETE_TIME = 10000L;

	protected synchronized void startDeleteTimer() {
		if (_deleteTask != null) {
			return;
		}
		_deleteTask = new DeleteTimer(getId());
		_future = GeneralThreadPool.getInstance().schedule(_deleteTask,
				DELETE_TIME);
	}

	protected static class DeleteTimer extends TimerTask {
		private int _id;

		protected DeleteTimer(int oId) {
			_id = oId;
			if (!(L1World.getInstance().findObject(_id) instanceof L1NpcInstance)) {
				throw new IllegalArgumentException("allowed only L1NpcInstance");
			}
		}

		@Override
		public void run() {
			L1NpcInstance npc = (L1NpcInstance) L1World.getInstance()
					.findObject(_id);
			if (npc == null || !npc.isDead() || npc._destroyed) {
				return; // 复活、既破弃济拔
			}
			try {
				npc.deleteMe();
			} catch (Exception e) { // 绝对例外投
				e.printStackTrace();
			}
		}
	}

	private int _spawnTime = 0;// 存在时间(秒)
	
	private boolean _isspawnTime = false;// 具有存在时间
	
	/**
	 * 设定存在时间(秒)
	 * @param spawnTime
	 */
	public void set_spawnTime(int spawnTime) {
		_spawnTime = spawnTime;
		_isspawnTime = true;
	}
	
	/**
	 * 传回存在时间(秒)
	 * @return
	 */
	public int get_spawnTime() {
		return _spawnTime;
	}
	
	/**
	 * 具有存在时间
	 * @return
	 */
	public boolean is_spawnTime() {
		return _isspawnTime;
	}

	public L1Spawn getSpawn() {
		return _spawn;
	}

	private L1MobGroupInfo _mobGroupInfo = null;

	public boolean isInMobGroup() {
		return getMobGroupInfo() != null;
	}

	public L1MobGroupInfo getMobGroupInfo() {
		return _mobGroupInfo;
	}

	public void setMobGroupInfo(L1MobGroupInfo m) {
		_mobGroupInfo = m;
	}

	private int _mobGroupId = 0;

	public int getMobGroupId() {
		return _mobGroupId;
	}

	public void setMobGroupId(int i) {
		_mobGroupId = i;
	}

	public int getSpawnNumber() {
		return _spawnNumber;
	}
	private int _oldnpcid = 0;
	
	public void setOldNpcID(final int npcid){
		_oldnpcid = npcid;
	}
	public int getOldNpcID(){
		return _oldnpcid;
	}
	
	public void setLastAttacker(final L1Character attacker){
		_lastAttacker = attacker;
	}
	
	public L1Character getLastAttacker(){
		return _lastAttacker;
	}
	
	private boolean _istu = false;
	
	public void setTu(boolean flg){
		_istu = flg;
	}
	
	public boolean isTU(){
		return _istu;		
	}
	//判断弱化属性减少的属性 
	private int _NpcAttr;

	public int get_NpcAttr() {
		return _NpcAttr;
	}

	public void set_NpcAttr(int i) {
		_NpcAttr = i;
	}
	//判断弱化属性减少的属性  end
}
