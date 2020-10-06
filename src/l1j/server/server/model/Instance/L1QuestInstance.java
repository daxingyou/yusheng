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

import l1j.server.Config;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CalcExp;
import l1j.server.server.world.L1World;

public class L1QuestInstance extends L1NpcInstance {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */


	private L1QuestInstance _npc = this;

	/**
	 * @param template
	 */
	public L1QuestInstance(L1Npc template) {
		super(template);
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
	public void onAction(L1PcInstance pc) {
		if (getCurrentHp() > 0 && !isDead()) {
			L1Attack attack = new L1Attack(pc, this);
			attack.calcHit();
			attack.action();
		}
	}

	@Override
	public void onTalkAction(L1PcInstance player) {
		if (getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_10 || getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_11) {
			int pcx = player.getX(); // PCX座标
			int pcy = player.getY(); // PCY座标
			int npcx = getX(); // NPCX座标
			int npcy = getY(); // NPCY座标

			if (pcx == npcx && pcy < npcy) {
				setHeading(0);
				broadcastPacket(new S_NPCPack(this));
			} else if (pcx > npcx && pcy < npcy) {
				setHeading(1);
				broadcastPacket(new S_NPCPack(this));
			} else if (pcx > npcx && pcy == npcy) {
				setHeading(2);
				broadcastPacket(new S_NPCPack(this));
			} else if (pcx > npcx && pcy > npcy) {
				setHeading(3);
				broadcastPacket(new S_NPCPack(this));
			} else if (pcx == npcx && pcy > npcy) {
				setHeading(4);
				broadcastPacket(new S_NPCPack(this));
			} else if (pcx < npcx && pcy > npcy) {
				setHeading(5);
				broadcastPacket(new S_NPCPack(this));
			} else if (pcx < npcx && pcy == npcy) {
				setHeading(6);
				broadcastPacket(new S_NPCPack(this));
			} else if (pcx < npcx && pcy < npcy) {
				setHeading(7);
				broadcastPacket(new S_NPCPack(this));
			}

			if (getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_10) { // 调查员
				if (player.isKnight() && player.getQuest().get_step(3) == 4) {
	      			player.sendPackets(new S_NPCTalkReturn(getId(), "searcherk1"));
      			} else {
      				player.sendPackets(new S_NPCTalkReturn(getId(), "searcherk4"));
      			}
			} else if (getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_11) { // 安迪亚
				if (player.isDarkelf() && 
      				player.getQuest().get_step(4) == 1) {
	      			player.sendPackets(new S_NPCTalkReturn(getId(), "endiaq1"));
      			} else {
      				player.sendPackets(new S_NPCTalkReturn(getId(), "endiaq4"));
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
		if (getCurrentHp() > 0 && !isDead()) {
			if (damage == 32767) {
				int transformId = getNpcTemplate().getTransformId();
				// 变身
				if (transformId == -1) {
					setCurrentHpDirect(0);
					//setDead(true);
					death(attacker);
					// Death(attacker);
				} // 变身
				else {
					transform(transformId);
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
	
	@Override
	public void transform(int transformId) {
		super.transform(transformId);

		// DROP再设定
		getInventory().clearItems();
		getInventory().shuffle();
	}
}
