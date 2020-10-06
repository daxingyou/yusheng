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

import l1j.server.server.GeneralThreadPool;//补充
import l1j.server.server.datatables.DropTable;//补充
import l1j.server.server.datatables.NPCTalkDataTable;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1NpcTalkData;
import l1j.server.server.model.L1Object;//补充
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.types.Point;
import l1j.server.server.utils.CalcExp;//补充
import l1j.server.server.world.L1World;
//补充
//补充

public class L1GuardInstance extends L1NpcInstance {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 探
	@Override
	public void searchTarget() {
		// 搜索
		/*L1PcInstance targetPlayer = null;
		for (L1PcInstance pc : L1World.getInstance().getVisiblePlayer(this)) {
			if (pc.getCurrentHp() <= 0 || pc.isDead() || pc.isGm()
					|| pc.isGhost()) {
				continue;
			}
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
		}*/
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
		return false;
	}

	public L1GuardInstance(L1Npc template) {
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
	public void onAction(L1PcInstance player) {
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

	@Override
	public void onTalkAction(L1PcInstance player) {
		int objid = getId();
		L1NpcTalkData talking = NPCTalkDataTable.getInstance().getTemplate(
				getNpcTemplate().get_npcId());
		int npcid = getNpcTemplate().get_npcId();
		String htmlid = null;
		String[] htmldata = null;
		boolean hascastle = false;
		String clan_name = "";
		String pri_name = "";

		if (talking != null) {
			// 
			//变更成 switch 
			switch (npcid)
			{
				case 70549: // 城左外门
				case 70985: {// 城右外门
				hascastle = checkHasCastle(player,
					L1CastleLocation.KENT_CASTLE_ID);
				if (hascastle) { // 城主员
					htmlid = "gateokeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
				}
				break;
				case 70656: { // 城内门
				hascastle = checkHasCastle(player,
						L1CastleLocation.KENT_CASTLE_ID);
				if (hascastle) { // 城主员
					htmlid = "gatekeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}	
				}
				break;
				case 70600: // 森外门
				case 70986: {
				hascastle = checkHasCastle(player,
						L1CastleLocation.OT_CASTLE_ID);
				if (hascastle) { // 城主员
					htmlid = "orckeeper";
				} else {
					htmlid = "orckeeperop";
				}
					
				}
				break;
				case 70687: // 城外门
				case 70987: {
				hascastle = checkHasCastle(player,
						L1CastleLocation.WW_CASTLE_ID);
				if (hascastle) { // 城主员
					htmlid = "gateokeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
				}
				break;
				case 70778: { // 城内门
				hascastle = checkHasCastle(player,
						L1CastleLocation.WW_CASTLE_ID);
				if (hascastle) { // 城主员
					htmlid = "gatekeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}	
				}
				break;
				case 70800: case 70988: case 70989: case 70990: case 70991: {
				hascastle = checkHasCastle(player,
						L1CastleLocation.GIRAN_CASTLE_ID);
				if (hascastle) { // 城主员
					htmlid = "gateokeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
				}
				break;
				case 70817: { // 城内门
				hascastle = checkHasCastle(player,
						L1CastleLocation.GIRAN_CASTLE_ID);
				if (hascastle) { // 城主员
					htmlid = "gatekeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}	
				}
				break;
				case 70862: // 城外门
				case 70992: {
				hascastle = checkHasCastle(player,
						L1CastleLocation.HEINE_CASTLE_ID);
				if (hascastle) { // 城主员
					htmlid = "gateokeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
				}
				break;
				case 70863: { // 城内门
				hascastle = checkHasCastle(player,
						L1CastleLocation.HEINE_CASTLE_ID);
				if (hascastle) { // 城主员
					htmlid = "gatekeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}	
				}
				break;
				case 70993: case 70994: {
				hascastle = checkHasCastle(player,
						L1CastleLocation.DOWA_CASTLE_ID);
				if (hascastle) { // 城主员
					htmlid = "gateokeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
				}
				break;
				case 70995: { // 城内门
				hascastle = checkHasCastle(player,
						L1CastleLocation.DOWA_CASTLE_ID);
				if (hascastle) { // 城主员
					htmlid = "gatekeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}	
				}
				break;
				case 70996: { // 城内门
				hascastle = checkHasCastle(player,
						L1CastleLocation.ADEN_CASTLE_ID);
				if (hascastle) { // 城主员
					htmlid = "gatekeeper";
					htmldata = new String[] { player.getName() };
				} else {
					htmlid = "gatekeeperop";
				}
				}
				break;
				case 60514: { // 城近卫兵
				for (L1Clan clan : L1World.getInstance().getAllClans()) {
					if (clan.getCastleId() // 城主
					== L1CastleLocation.KENT_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "ktguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
				}
				break;
				case 60560: { // 近卫兵
				for (L1Clan clan : L1World.getInstance().getAllClans()) {
					if (clan.getCastleId() // 城主
					== L1CastleLocation.OT_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "orcguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };	
				}
				break;
				case 60552: { // 城近卫兵
				for (L1Clan clan : L1World.getInstance().getAllClans()) {
					if (clan.getCastleId() // 城主
					== L1CastleLocation.WW_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "wdguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };	
				}
				break;
				case 60524: // 街入口近卫兵(弓)
				case 60525: // 街入口近卫兵
				case 60529: { // 城近卫兵
				for (L1Clan clan : L1World.getInstance().getAllClans()) {
					if (clan.getCastleId() // 城主
					== L1CastleLocation.GIRAN_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "grguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
				}
				break;
				case 70857: { // 城 
				for (L1Clan clan : L1World.getInstance().getAllClans()) {
					if (clan.getCastleId() // 城主
					== L1CastleLocation.HEINE_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "heguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
				}
				break;
				case 60530: // 城 
				case 60531: {
				for (L1Clan clan : L1World.getInstance().getAllClans()) {
					if (clan.getCastleId() // 城主
					== L1CastleLocation.DOWA_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "dcguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
				}
				break;
				case 60533: // 城 
				case 60534: {
				for (L1Clan clan : L1World.getInstance().getAllClans()) {
					if (clan.getCastleId() // 城主
					== L1CastleLocation.ADEN_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "adguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
				}
				break;
				case 81156: { // 侦察兵（要塞）
				for (L1Clan clan : L1World.getInstance().getAllClans()) {
					if (clan.getCastleId() // 城主
					== L1CastleLocation.DIAD_CASTLE_ID) {
						clan_name = clan.getClanName();
						pri_name = clan.getLeaderName();
						break;
					}
				}
				htmlid = "ktguard6";
				htmldata = new String[] { getName(), clan_name, pri_name };
				}
				break;
			}
			//变更成 switch  end

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
		}
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
			//setDead(true);
			_lastattacker = attacker;
			//Death death = new Death();
			//GeneralThreadPool.getInstance().execute(death);
			death(attacker);
		}
		if (newHp > 0)
			setCurrentHp(newHp);
		} else if (!isDead()) {
			//setDead(true);
			System.out.println("警告：ＮＰＣ的ＨＰ减少处理发生异常，视为最初ＨＰ０处理。");
			_lastattacker = attacker;
			//Death death = new Death();
			//GeneralThreadPool.getInstance().execute(death);
			death(attacker);
		}
		//警卫可以杀死 & 帮打  end
	}
	
	public synchronized void death(L1Character lastAttacker) {
		if (!isDead()) {
			setDead(true);
			Death death = new Death();
			GeneralThreadPool.getInstance().execute(death);
		}
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
		L1Object object = L1World.getInstance().findObject(getId());
		L1GuardInstance npc = (L1GuardInstance) object;

		public void run() {
			setDeathProcessing(true);
			setCurrentHpDirect(0);
			setDead(true);
			int targetobjid = getId();
			npc.getMap().setPassable(npc.getLocation(), true);
			setHeading(npc.targetDirection(lastAttacker.getX(), lastAttacker.getY()));
			broadcastPacket(new S_DoActionGFX(lastAttacker.getId(), 8));
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
				CalcExp.calcExp(player, L1GuardInstance.this, targetList, hateList, exp);

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
	
	public void setLink(L1Character cha) {
		if (cha != null && _hateList.isEmpty()) {
			_hateList.add(cha, 0);
			checkTarget();
		}
	}
	//警卫可以杀死 & 帮打  end
	
	private boolean checkHasCastle(L1PcInstance pc, int castleId) {
		boolean isExistDefenseClan = false;
		for (L1Clan clan : L1World.getInstance().getAllClans()) {
			if (castleId == clan.getCastleId()) {
				isExistDefenseClan = true;
				break;
			}
		}
		/*删除if (!isExistDefenseClan) { // 城主居
			return true;
		}删除*/

		if (pc.getClanid() != 0) { // 所属中
			L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
			if (clan != null) {
				if (clan.getCastleId() == castleId) {
					return true;
				}
			}
		}
		return false;
	}

}
