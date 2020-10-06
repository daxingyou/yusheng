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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1GuardInstance.Death;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CalcExp;
import l1j.server.server.world.L1World;

public class L1GuardianInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Log _log = LogFactory.getLog(L1GuardianInstance.class);

	private Random _random = new Random();
	private L1GuardianInstance _npc = this;

	/**
	 * @param template
	 */
	public L1GuardianInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void searchTarget() {
		// 检索
		L1PcInstance targetPlayer = null;

		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			if (pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm()
					|| pc.isGhost()) {
				continue;
			}
			if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) { // 
				if (!pc.isElf()) { // 以外
					targetPlayer = pc;
					broadcastPacket(new S_NpcChatPacket(this, "$804", 2)); // 以外者、命惜早去。神圣场所。
					break;
				}
			}
		}
		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
		}
	}

	// 设定
	@Override
	public void setLink(L1Character cha) {
		if (cha != null && _hateList.isEmpty()) { // 场合追加
			_hateList.add(cha, 0);
			checkTarget();
		}
	}

	@Override
	public void onNpcAI() {
		if (isAiRunning()) {
			return;
		}
		setActived(false);
		startAI();
	}

	@Override
	public void onAction(L1PcInstance player) {
		if (ATTACK != null) {
			ATTACK.attack(player, this);
		}
		if (player.getType() == 2 && player.getCurrentWeapon() == 0
				&& player.isElf() && player.getTempCharGfx() == player.getClassId()) {//  && player.getTempCharGfx() == player.getClassId() 补上是否变身判断 
			L1Attack attack = new L1Attack(player, this);

			if (attack.calcHit()) {
				if (getNpcTemplate().get_npcId() == 70848) { // 安特
					int chance = _random.nextInt(100) + 1;
					// 守护神给材料修正  end
					if (hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_11) && 
						hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_12) && 
						hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_13)) {

						broadcastPacket(new S_NpcChatPacket(this, "$822", 0)); // 现在、没有树皮、没有枝脉。稍后、来、再一次、

						/*for(Object item : getInventory().getItems()) { // 检查身上道具
							L1ItemInstance Item = (L1ItemInstance) item;
							if (Item.getItem().getItemId() == 40499) { // 有蘑菇汁
								int i = Item.getCount(); // 取得数量

								getInventory().removeItem(Item, i);
								player.getInventory().storeItem(40499, (i)); // 给予剩下的蘑菇汁
								player.sendPackets(new S_ServerMessage(143, "$755", "$764" + " (" + i + ")"));
							}
						}*/
					} else if (chance <= 2 && _count1 < 1) {
						player.getInventory().storeItem(40506, 1); // 安果
						player.sendPackets(new S_ServerMessage(143, "$755",
								"$794"));
						_count1 ++;
						if (_count1 > 0) {
							setSkillEffect(l1j.william.New_Id.Skill_AJ_1_12, 180 * 1000);
						}
					} else if (chance <= 30 && chance > 10 && _count2 < 15) {
						player.getInventory().storeItem(40507, 1); // 树枝
						player.sendPackets(new S_ServerMessage(143, "$755",
								"$763"));
						_count2 ++;
						if (_count2 > 14) {
							setSkillEffect(l1j.william.New_Id.Skill_AJ_1_13, 60 * 1000);
						}
					} else if (chance > 50 && _count3 < 1) {
						if (!getInventory().checkItem(40499)) {
							player.sendPackets(new S_ServerMessage(337, "$764")); // 蘑菇汁不足
						} else {
							for(Object item : getInventory().getItems()) { // 检查身上道具
								L1ItemInstance Item = (L1ItemInstance) item;
								if (Item.getItem().getItemId() == 40499) { // 有蘑菇汁
									long i = Item.getCount(); // 取得数量

									getInventory().removeItem(Item, i); // 删除身上的蘑菇汁
									player.getInventory().storeItem(40505, i); // 给予等量的树皮
									player.sendPackets(new S_ServerMessage(143, "$755", "$770" + " (" + i + ")"));

									_count3 ++;
								}
							}

							if (_count3 > 0) {
								setSkillEffect(l1j.william.New_Id.Skill_AJ_1_11, 120 * 1000);
							}
						}
					}
					// 守护神给材料修正  end
				}
				if (getNpcTemplate().get_npcId() == 70850) { // 潘
					int chance = _random.nextInt(100) + 1;
					// 守护神给材料修正 
					if (hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_14)) {
						broadcastPacket(new S_NpcChatPacket(this, "$824", 0)); // 我已经没有鬃毛了！待会再来吧！
					} else if (chance <= 30 && _count1 < 4) {
						player.getInventory().storeItem(40519, 5); // 潘的鬃毛
						player.sendPackets(new S_ServerMessage(143, "$753",
								"$760" + " (" + 5 + ")"));
						_count1 ++;
						if (_count1 > 3) {
							setSkillEffect(l1j.william.New_Id.Skill_AJ_1_14, 60 * 1000);
						}
					}
					// 守护神给材料修正  end
				}
				if (getNpcTemplate().get_npcId() == 70846) { // 芮克妮
					int chance = _random.nextInt(100) + 1;
					// 守护神给材料修正 
					if (hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_10)) {
						broadcastPacket(new S_NpcChatPacket(this, "$823", 0)); // 任何事物都有限制，给我时间去制造线。
					} else if (chance <= 50 && _count1 < 1) {
						if (!getInventory().checkItem(40507)) {
							player.sendPackets(new S_ServerMessage(337, "$763")); // 安特之树枝不足
						} else {
							for(Object item : getInventory().getItems()) { // 检查身上道具
								L1ItemInstance Item = (L1ItemInstance) item;
								if (Item.getItem().getItemId() == 40507) { // 有安特之树枝
									long i = Item.getCount(); // 取得数量
									long j = i / 2;

									getInventory().removeItem(Item, i); // 删除身上的安特之树枝
									player.getInventory().storeItem(40503, j); // 给予等量的芮克妮的网
									player.sendPackets(new S_ServerMessage(143, "$752", "$769" + " (" + j + ")"));

									_count1 ++;
								}
							}

							if (_count1 > 0) {
								setSkillEffect(l1j.william.New_Id.Skill_AJ_1_10, 60 * 1000);
							}
						}
					}
					// 守护神给材料修正  end
				}
				attack.calcDamage();
				attack.calcStaffOfMana();
				attack.addPcPoisonAttack(player, this);
			}
			attack.action();
			attack.commit();
		} else if (getCurrentHp() > 0 && !isDead()) {
			L1Attack attack = new L1Attack(player, this);
			if (attack.calcHit()) {
				attack.calcDamage();
				attack.calcStaffOfMana();
				attack.addPcPoisonAttack(player, this);
			}
			attack.action();
			attack.commit();
		}
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		L1Object object = L1World.getInstance().findObject(getId());
		L1NpcInstance target = (L1NpcInstance) object;
		String htmlid = null;
		String[] htmldata = null;

		if (talking != null) {
			int pcx = player.getX(); // PCX座标
			int pcy = player.getY(); // PCY座标
			int npcx = target.getX(); // NPCX座标
			int npcy = target.getY(); // NPCY座标

			if (pcx == npcx && pcy < npcy) {
				setHeading(0);
			} else if (pcx > npcx && pcy < npcy) {
				setHeading(1);
			} else if (pcx > npcx && pcy == npcy) {
				setHeading(2);
			} else if (pcx > npcx && pcy > npcy) {
				setHeading(3);
			} else if (pcx == npcx && pcy > npcy) {
				setHeading(4);
			} else if (pcx < npcx && pcy > npcy) {
				setHeading(5);
			} else if (pcx < npcx && pcy == npcy) {
				setHeading(6);
			} else if (pcx < npcx && pcy < npcy) {
				setHeading(7);
			}
			broadcastPacket(new S_ChangeHeading(this));

			// html表示送信
			if (htmlid != null) { // htmlid指定场合
				if (htmldata != null) { // html指定场合表示
					player.sendPackets(new S_NPCTalkReturn(objid, htmlid,
							htmldata));
				} else {
					player.sendPackets(new S_NPCTalkReturn(objid, htmlid));
				}
			} else {
				if (player.getLawful() < -1000) { // 
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 2));
				} else {
					player.sendPackets(new S_NPCTalkReturn(talking, objid, 1));
				}
			}
			// 动
			synchronized (this) {
				if (_monitor != null) {
					_monitor.cancel();
				}
				setRest(true);
				_monitor = new RestMonitor();
				_restTimer.schedule(_monitor, REST_MILLISEC);
			}
		}
	}

	@Override
	public void receiveDamage(L1Character attacker, int damage) { // 攻击ＨＰ减使用
		if (attacker instanceof L1PcInstance && damage > 0) {
			L1PcInstance pc = (L1PcInstance) attacker;
			if (pc.getType() == 2 && // 素手
					pc.getCurrentWeapon() == 0 && pc.getTempCharGfx() == pc.getClassId()) { //  && player.getTempCharGfx() == player.getClassId() 补上是否变身判断 
			} else {
				if (getCurrentHp() > 0 && !isDead()) {
					if (damage >= 0) {
						setHate(attacker, damage);
					}
					if (damage > 0) {
						removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
					}
					onNpcAI();
					// 仲间意识设定
					serchLink(pc, getNpcTemplate().get_family());
					if (damage > 0) {
						pc.setPetTarget(this);
					}

					int newHp = getCurrentHp() - damage;
					if (newHp <= 0 && !isDead()) {
						setCurrentHpDirect(0);
						//setDead(true);
						_lastattacker = attacker;
						//Death death = new Death();
						//GeneralThreadPool.getInstance().execute(death);
						death(attacker);
					}
					if (newHp > 0) {
						setCurrentHp(newHp);
					}
				} else if (!isDead()) { // 念
					//setDead(true);
					_lastattacker = attacker;
					//Death death = new Death();
					//GeneralThreadPool.getInstance().execute(death);
					death(attacker);
				}
			}
		}
	}

	public synchronized void death(L1Character lastAttacker) {
		if (!isDead()) {
			setDead(true);
			Death death = new Death();
			GeneralThreadPool.getInstance().execute(death);
		}
	}
	@Override
	public void setCurrentHp(int i) {
		int currentHp = i;
		if (currentHp >= getMaxHp()) {
			currentHp = getMaxHp();
		}
		setCurrentHpDirect(currentHp);

		if (getMaxHp() > getCurrentHp()) {
			startHpRegeneration();
		}
	}

	@Override
	public void setCurrentMp(int i) {
		int currentMp = i;
		if (currentMp >= getMaxMp()) {
			currentMp = getMaxMp();
		}
		setCurrentMpDirect(currentMp);

		if (getMaxMp() > getCurrentMp()) {
			startMpRegeneration();
		}
	}

	private L1Character _lastattacker;

	class Death implements Runnable {
		L1Character lastAttacker = _lastattacker;

		public void run() {
			setDeathProcessing(true);
			setCurrentHpDirect(0);
			setDead(true);
			int targetobjid = getId();
			getMap().setPassable(getLocation(), true);
			broadcastPacket(new S_DoActionGFX(targetobjid,
					ActionCodes.ACTION_Die));

			L1PcInstance player = null;
			if (lastAttacker instanceof L1PcInstance) {
				player = (L1PcInstance) lastAttacker;
			} else if (lastAttacker instanceof L1PetInstance) {
				player = (L1PcInstance) ((L1PetInstance) lastAttacker)
						.getMaster();
			} else if (lastAttacker instanceof L1SummonInstance) {
				player = (L1PcInstance) ((L1SummonInstance) lastAttacker)
						.getMaster();
			}
			if (player != null) {
				ArrayList<L1Character> targetList = _hateList
						.toTargetArrayList();
				ArrayList<Integer> hateList = _hateList.toHateArrayList();
				long exp = getExp();

				CalcExp.calcExp(player, L1GuardianInstance.this, targetList, hateList, exp);
				try {
					DropTable.getInstance().dropShare(_npc, targetList,
							hateList);
				} catch (Exception e) {
					_log.error(e.getLocalizedMessage(), e);
				}
				// 止刺设定。or倒场合入。
				player.addKarma((int) (getKarma() * Config.RATE_KARMA));
			}
			setDeathProcessing(false);

			setKarma(0);
			setExp(0);
			allTargetClear();

			startDeleteTimer();
		}
	}

	@Override
	public void onFinalAction(L1PcInstance player, String action) {
	}

	public void doFinalAction(L1PcInstance player) {
	}

	private static final long REST_MILLISEC = 10000;

	private static final Timer _restTimer = new Timer(true);

	private RestMonitor _monitor;

	public class RestMonitor extends TimerTask {
		@Override
		public void run() {
			setRest(false);
		}
	}
	
	//守护神给材料判断 
	public int _count1 = 0;
	
	public int getCount1() {
        return _count1;
    }

    public void setCount1(int i) {
        _count1 = i;
    }
    
    public int _count2 = 0;
	
	public int getCount2() {
        return _count2;
    }

    public void setCount2(int i) {
        _count2 = i;
    }
    
    public int _count3 = 0;
	
	public int getCount3() {
        return _count3;
    }

    public void setCount3(int i) {
        _count3 = i;
    }
    //守护神给材料判断  end
}
