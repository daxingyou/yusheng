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

import java.util.ArrayList;//补充

import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;//补充
import l1j.server.server.WarTimeController;//补充
import l1j.server.server.datatables.DropTable;//补充
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.types.Point;
import l1j.server.server.utils.CalcExp;//补充
import l1j.server.server.world.L1World;
//补充
//补充

public class L1CastleGuardInstance extends L1NpcInstance {
    //怪物自动死亡 
    private boolean _KillMyselfRunning = false;
    class KillMyself implements Runnable{
    public void run() {
    _KillMyselfRunning = true;
    try {
         Thread.sleep(840000);//14分钟
        } catch (Exception e) {
        }
    _KillMyselfRunning = false;
    setDead(true);
    allTargetClear();
    DeleteMob delete_timer = new DeleteMob();
	GeneralThreadPool.getInstance().execute(delete_timer);
    }     
    } 
    //怪物自动死亡 亡 end
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	// 探
	@Override
	public void searchTarget() {
		// 搜索
		L1PcInstance targetPlayer = null;
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			if (pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm()
					|| pc.isGhost()) {
				continue;
			}
			//城堡警卫设定 
			int npc_hascastle = getNpcTemplate().getHascastle();// 
			int castle_id = 0;
			if (pc.getClanid() != 0) {
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
				if (clan != null) {
					castle_id = clan.getCastleId();
				}
			}
			if (npc_hascastle > 0 && 
				castle_id != npc_hascastle && 
				WarTimeController.getInstance().isNowWar(npc_hascastle)) {
				targetPlayer = pc;
				break;
			}
			//城堡警卫设定  end
			if (!pc.isInvisble() || getNpcTemplate().is_agrocoi()) // 
			{
				if (pc.isWanted()) { // PK手配中
					targetPlayer = pc;
					break;
				}
			}
		}
		if (targetPlayer != null) {
			_hateList.add(targetPlayer, 0);
			_target = targetPlayer;
		}
	}

	// 场合处理
	@Override
	public boolean noTarget() {
		if (getLocation()
				.getTileLineDistance(new Point(getHomeX(), getHomeY())) > 0) {
			int dir = moveDirection(getHomeX(), getHomeY());
			if (dir != -1) {
				setDirectionMove(dir);
				setSleepTime(calcSleepTime(getPassispeed()));
			} else // 远or经路见场合扫
			{
				teleport(getHomeX(), getHomeY(), 1);
			}
		} else {
			if (L1World.getInstance().getRecognizePlayer(this).size() == 0) {
				return true; // 周ＡＩ处理终了
			}
		}
		if (L1CastleLocation.getCastleId(getLocation())==0) {
			teleport(getHomeX(), getHomeY(), 1);
		}
		return false;
	}

	public L1CastleGuardInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onNpcAI() {
		//怪物自动死亡  
		if(!_KillMyselfRunning && (getNpcTemplate().get_npcId() >= 101001 && getNpcTemplate().get_npcId() <= 101028)) {
        	KillMyself aKillMyself = new KillMyself(); 
        	GeneralThreadPool.getInstance().execute(aKillMyself);
        }
        //怪物自动死亡  end
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
		if (getCurrentHp() > 0 && !isDead()) {
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

	public void onTalkAction(L1PcInstance player) {
	}

	public void onFinalAction() {

	}

	public void doFinalAction() {

	}
	//警卫可以杀死 & 帮打 
	public void ReceiveManaDamage(L1Character attacker, int mpDamage)
	{
		if (mpDamage > 0 && !isDead()) {

			setHate(attacker, mpDamage);
			onNpcAI();

			if (attacker instanceof L1PcInstance) {
				serchLink((L1PcInstance) attacker, getNpcTemplate().get_family());
			}

			int newMp = getCurrentMp() - mpDamage;
			if (newMp < 0) {
				newMp = 0;
			}
			setCurrentMp(newMp);
		}
	}
	//警卫可以杀死 & 帮打  end
	
	@Override
	public void receiveDamage(L1Character attacker, int damage) // 攻击ＨＰ减使用
	{
		//警卫可以杀死 & 帮打 
		if (getCurrentHp() > 0 && !isDead()) {
		//警卫可以杀死 & 帮打  end
		if (damage >= 0) {
			// int Hate = damage / 10 + 10; // １０分１＋１０
			// setHate(attacker, Hate);
			setHate(attacker, damage);
			removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
/*			if (attacker.isVdmg()) {
				if (attacker instanceof L1PcInstance){
					L1PcInstance player = (L1PcInstance) attacker;
					String msg = "输出->"+damage;
					S_ChatPacket s_chatpacket = new S_ChatPacket(this, msg,
							Opcodes.S_OPCODE_NORMALCHAT);
					player.sendPackets(s_chatpacket);
				}
			}*/
		}

		onNpcAI();
		//警卫可以杀死 & 帮打 
		if (attacker instanceof L1PcInstance) {
			serchLink((L1PcInstance) attacker, getNpcTemplate().get_family());
		}
		//警卫可以杀死 & 帮打  end
		if (attacker instanceof L1PcInstance && damage > 0) {
			L1PcInstance player = (L1PcInstance) attacker;
			player.setPetTarget(this);
		}
		//警卫可以杀死 & 帮打 
		int newHp = getCurrentHp() - damage;
		if (newHp <= 0 && !isDead()) {
			setCurrentHpDirect(0);
			setDead(true);
			_lastattacker = attacker;
			Death death = new Death();
			GeneralThreadPool.getInstance().execute(death);
			// Death(attacker);
		}
		if (newHp > 0)
			setCurrentHp(newHp);
		} else if (!isDead()) {
			setDead(true);
			System.out.println("警告：ＮＰＣ的ＨＰ减少处理发生异常，视为最初ＨＰ０处理。");
			_lastattacker = attacker;
			Death death = new Death();
			GeneralThreadPool.getInstance().execute(death);
			// Death(attacker);
		}
		//警卫可以杀死 & 帮打  end
	}
	
	//警卫可以杀死 & 帮打 
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
	//警卫可以杀死 & 帮打  end

	//警卫可以杀死 & 帮打 
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
	//警卫可以杀死 & 帮打  end
	
	//警卫可以杀死 & 帮打 
	private L1Character _lastattacker;

	class Death implements Runnable {
		L1Character lastAttacker = _lastattacker;
/*		L1Object object = L1World.getInstance().findObject(getId());*/
		L1CastleGuardInstance npc = L1CastleGuardInstance.this;

		public void run() {
			setDeathProcessing(true);
			setCurrentHpDirect(0);
			setDead(true);
/*			int targetobjid = getId();*/
			npc.getMap().setPassable(npc.getLocation(), true);
			setHeading(npc.targetDirection(lastAttacker.getX(), lastAttacker.getY()));
			broadcastPacket(new S_DoActionGFX(lastAttacker.getId(), 8));
			setStatus(ActionCodes.ACTION_Die);
			//broadcastPacket(new S_AttackPacket(lastAttacker,targetobjid, 8));

			L1PcInstance player = null;
			if (lastAttacker instanceof L1PcInstance)
				player = (L1PcInstance) lastAttacker;
			else if (lastAttacker instanceof L1PetInstance)
				player = (L1PcInstance) ((L1PetInstance) lastAttacker)
						.getMaster();
			else if (lastAttacker instanceof L1SummonInstance)
				player = (L1PcInstance) ((L1SummonInstance) lastAttacker)
						.getMaster();
						
			if (player != null) {
				ArrayList<L1Character> targetList = _hateList.toTargetArrayList();
				ArrayList<Integer> hateList = _hateList.toHateArrayList();
				long exp = getExp();
				CalcExp.calcExp(player, L1CastleGuardInstance.this, targetList, hateList, exp);

				try {
					DropTable.getInstance().dropShare(npc, targetList,
							hateList);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			setDeathProcessing(false);

			setExp(0);
			allTargetClear();
			DeleteNpc delete_timer = new DeleteNpc();
			GeneralThreadPool.getInstance().execute(delete_timer);
		}
	}
	
	class DeleteNpc implements Runnable {
		public void run() {
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!isDead() || _destroyed)
				return;
			deleteMe();
		}
	}
	
	//怪物自动死亡 
	class DeleteMob implements Runnable {
		@Override
		public void run() {
			if (!isDead() || _destroyed) {
				return; // 复活、既破弃济拔
			}
			deleteMe();
		}
	}
	//怪物自动死亡  end
	
	public void setLink(L1Character cha) {
		if (cha != null && _hateList.isEmpty()) {
			_hateList.add(cha, 0);
			checkTarget();
		}
	}
	//警卫可以杀死 & 帮打  end
}
