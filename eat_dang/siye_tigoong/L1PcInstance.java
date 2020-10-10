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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
//import java.io.BufferedWriter;
//import java.util.Collection;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.HashMap;
//import java.io.OutputStream;
import java.util.List;
import java.util.Map;
//import java.util.Map;
import java.util.Random;
//import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

import l1j.server.AcceleratorChecker;
import l1j.server.Config;
import l1j.server.server.ActionCodes;
//import l1j.server.server.ClientThread;
import l1j.server.server.GeneralThreadPool;
//import l1j.server.server.PacketOutput;
import l1j.server.server.WarTimeController;
import l1j.server.server.WriteLogTxt;
//import l1j.server.server.command.GMCommands;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MapsTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.SprTable;
import l1j.server.server.datatables.lock.CharSkillReading;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.HpRegeneration;
import l1j.server.server.model.L1Attack;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
//import l1j.server.server.model.L1DwarfInventory;
import l1j.server.server.model.L1EquipmentSlot;
import l1j.server.server.model.L1ExcludingMailList;
import l1j.server.server.model.L1GamSpList;
import l1j.server.server.model.L1HateList;
import l1j.server.server.model.L1Inventory;
//import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Karma;
import l1j.server.server.model.L1Location;
//import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PCAction;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.L1PcBlessEnchant;
import l1j.server.server.model.L1PcHealAI;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PinkName;
// 冲晕,束缚,冰冻,沉睡,地屏功能 
// 冲晕,束缚,冰冻,沉睡,地屏功能  end
import l1j.server.server.model.L1Quest;
import l1j.server.server.model.L1Spawn;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1TownLocation;
import l1j.server.server.model.L1War;
import l1j.server.server.model.MpRegeneration;
import l1j.server.server.model.MpRegenerationByDoll;
import l1j.server.server.model.classes.L1ClassFeature;
import l1j.server.server.model.gametime.L1GameTimeCarrier;
import l1j.server.server.model.guaji.L1PcAI;
import l1j.server.server.model.guaji.NpcMoveExecutor;
import l1j.server.server.model.guaji.pcMove;
//import l1j.server.server.model.monitor.L1PcExpMonitor;
import l1j.server.server.model.monitor.L1PcGhostMonitor;
import l1j.server.server.model.monitor.L1PcInvisDelay;
import l1j.server.server.model.poison.L1Poison2;
import l1j.server.server.model.poison.L1Poison3;
import l1j.server.server.model.poison.L1Poison4;
import l1j.server.server.model.poison.L1Poison6;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_CastleMaster;
import l1j.server.server.serverpackets.S_ChangeShape;
//import l1j.server.server.serverpackets.S_Disconnect; // 加入断线功能
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_DoActionShop;
import l1j.server.server.serverpackets.S_Emblem;
import l1j.server.server.serverpackets.S_Exp;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_Lawful;
import l1j.server.server.serverpackets.S_Liquor;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_OtherCharPacks;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_PacketBoxDk;
import l1j.server.server.serverpackets.S_PinkName;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_bonusstats;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.templates.L1CharacterAdenaTrade;
import l1j.server.server.templates.L1FindShopSell;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Pc;
import l1j.server.server.templates.L1PrivateShopBuyList;
import l1j.server.server.templates.L1PrivateShopSellList;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.timecontroller.pc.AutoMagic;
//import l1j.server.server.templates.Tbs;
import l1j.server.server.utils.CalcStat;
//import l1j.william.PlayerSpeed;
//import l1j.william.L1WilliamPlayerSpeed;
//import l1j.william.L1WilliamSystemMessage;
import l1j.server.server.world.L1World;
import l1j.william.Reward;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


// Referenced classes of package l1j.server.server.model:
// L1Character, L1DropTable, L1Object, L1ItemInstance,
// L1World
//

public class L1PcInstance extends L1Character {
	private static final long serialVersionUID = 1L;

	public static final int CLASSID_KNIGHT_MALE = 61;
	public static final int CLASSID_KNIGHT_FEMALE = 48;
	public static final int CLASSID_ELF_MALE = 138;
	public static final int CLASSID_ELF_FEMALE = 37;
	public static final int CLASSID_WIZARD_MALE = 734;
	public static final int CLASSID_WIZARD_FEMALE = 1186;
	public static final int CLASSID_DARK_ELF_MALE = 2786;
	public static final int CLASSID_DARK_ELF_FEMALE = 2796;
	public static final int CLASSID_DRAGON_KNIGHT_MALE = 6658;
	public static final int CLASSID_DRAGON_KNIGHT_FEMALE = 6661;
	public static final int CLASSID_ILLUSIONIST_MALE = 6671;
	public static final int CLASSID_ILLUSIONIST_FEMALE = 6650;
	public static final int CLASSID_PRINCE = 0;
	public static final int CLASSID_PRINCESS = 1;
	private final Map<Integer, Integer> _uplevelList;// 属性重置清单(模式/增加数值总合)
	private static Random _random = new Random();
	// private final static Map<Integer, L1PcHpMp> _levelhpmpup = new
	// HashMap<Integer, L1PcHpMp>();
	// private final Map<Integer, Integer> _levelmpup = new HashMap<Integer,
	// Integer>();
	private final L1Inventory _tradewindow;
	private final ArrayList<Object> _tempObjects = new ArrayList<Object>();

	public void addTempObject(final Object obj) {
		_tempObjects.add(obj);
	}

	public void clearTempObjects() {
		_tempObjects.clear();
	}

	public ArrayList<Object> getTempObjects() {
		return _tempObjects;
	}

	private short _hpr = 0;
	private short _trueHpr = 0;

	public short getHpr() {
		return _hpr;
	}

	public void addHpr(final int i) {
		_trueHpr += i;
		_hpr = (short) Math.max(0, _trueHpr);
	}

	private short _mpr = 0;
	private short _trueMpr = 0;

	public short getMpr() {
		return _mpr;
	}

	public void addMpr(final int i) {
		_trueMpr += i;
		_mpr = (short) Math.max(0, _trueMpr);
	}

	public synchronized void startHpRegeneration() {
		/*
		 * final int INTERVAL = 1000;
		 * 
		 * if (!_hpRegenActive) { _hpRegen = new HpRegeneration(this);
		 * _regenTimer.scheduleAtFixedRate(_hpRegen, INTERVAL, INTERVAL);
		 * _hpRegenActive = true; }
		 */
		if (!_hpRegenActive) {
			if (_hpRegen == null) {
				_hpRegen = new HpRegeneration(this);
			}
			/*
			 * if (_hpMonitorFuture == null) { _hpMonitorFuture =
			 * GeneralThreadPool.getInstance() .pcScheduleAtFixedRate(new
			 * L1PcHpr(this), 1000L, INTERVAL_MPHP_MONITOR); }
			 */
			_hpRegenActive = true;
		}
	}

	public void stopHpRegeneration() {
		if (_hpRegenActive) {
			/*
			 * if (_hpMonitorFuture != null) { _hpMonitorFuture.cancel(true);
			 * _hpMonitorFuture = null; }
			 */
			_hpRegen = null;
			_hpRegenActive = false;
		}
	}

	public synchronized void startMpRegeneration() {
		/*
		 * final int INTERVAL = 1000;
		 * 
		 * if (!_mpRegenActive) { _mpRegen = new MpRegeneration(this);
		 * _regenTimer.scheduleAtFixedRate(_mpRegen, INTERVAL, INTERVAL);
		 * _mpRegenActive = true; }
		 */
		if (!_mpRegenActive) {
			if (_mpRegen == null) {
				_mpRegen = new MpRegeneration(this);
			}
			/*
			 * if (_mpMonitorFuture == null) { _mpMonitorFuture =
			 * GeneralThreadPool.getInstance() .pcScheduleAtFixedRate(new
			 * L1PcMpr(this), 1000L, INTERVAL_MPHP_MONITOR); }
			 */
			_mpRegenActive = true;
		}

	}

	/*
	 * public void startMpRegenerationByDoll() { final int INTERVAL_BY_DOLL =
	 * 60000; boolean isExistMprDoll = false; Object[] dollList =
	 * getDollList().values().toArray(); for (Object dollObject : dollList) {
	 * L1DollInstance doll = (L1DollInstance) dollObject; if
	 * (doll.isMpRegeneration()) { isExistMprDoll = true; } } if
	 * (!_mpRegenActiveByDoll && isExistMprDoll) { _mpRegenByDoll = new
	 * MpRegenerationByDoll(this);
	 * _regenTimer.scheduleAtFixedRate(_mpRegenByDoll, INTERVAL_BY_DOLL,
	 * INTERVAL_BY_DOLL); _mpRegenActiveByDoll = true; } }
	 */

	public void stopMpRegeneration() {
		if (_mpRegenActive) {
			/*
			 * if (_mpMonitorFuture != null) { _mpMonitorFuture.cancel(true);
			 * _mpMonitorFuture = null; }
			 */
			_mpRegen = null;
			_mpRegenActive = false;
		}
	}

	public void stopMpRegenerationByDoll() {
		if (_mpRegenActiveByDoll) {
			_mpRegenByDoll.cancel();
			_mpRegenByDoll = null;
			_mpRegenActiveByDoll = false;
		}
	}

	public void startObjectAutoUpdate() {
		removeAllKnownObjects();
		/*
		 * _autoUpdateFuture = GeneralThreadPool.getInstance()
		 * .pcScheduleAtFixedRate(new L1PcAutoUpdate(this), 1000L,
		 * INTERVAL_AUTO_UPDATE);
		 */
	}

	/**
	 * 各种停止。
	 */
	public void stopEtcMonitor() {
		/*
		 * if (_autoUpdateFuture != null) { _autoUpdateFuture.cancel(true);
		 * _autoUpdateFuture = null; }
		 */
		/*
		 * if (_mpMonitorFuture != null) { _mpMonitorFuture.cancel(true);
		 * _mpMonitorFuture = null; } if (_hpMonitorFuture != null) {
		 * _hpMonitorFuture.cancel(true); _hpMonitorFuture = null; }
		 */
		if (_ghostFuture != null) {
			_ghostFuture.cancel(true);
			_ghostFuture = null;
		}

		if (_hellFuture != null) {
			_hellFuture.cancel(true);
			_hellFuture = null;
		}

	}

	/*
	 * private static final long INTERVAL_AUTO_UPDATE = 300; private
	 * ScheduledFuture<?> _autoUpdateFuture;
	 */

	/*
	 * private static final long INTERVAL_MPHP_MONITOR = 1000; private
	 * ScheduledFuture<?> _mpMonitorFuture; private ScheduledFuture<?>
	 * _hpMonitorFuture;
	 */

	private long _old_exp = 0;

	/**
	 * 原始Lawful
	 * 
	 * @return
	 */
	public long getExpo() {
		return _old_exp;
	}

	public void onChangeExp() {
		final int level = ExpTable.getLevelByExp(getExp());
		final int char_level = getLevel();
		final int gap = level - char_level;
		long oldexp = getExp();
		if (_old_exp != getExp()) {
			oldexp = _old_exp;
			_old_exp = getExp();
		}
		if (gap == 0) {
			// sendPackets(new S_OwnCharStatus(this));
			sendPackets(new S_Exp(this));
			return;
		}

		// 变化场合
		if (gap > 0) {
			WriteLogTxt.Recording("升级记录", "玩家" + getName() + "#" + getId()
					+ "#准备升级，升级前经验值为" + oldexp + "等级为" + getLevel() + "空身血量为"
					+ getBaseMaxHp() + "空身魔量为" + getBaseMaxMp());
			levelUp(gap);
			WriteLogTxt.Recording("升级记录", "玩家" + getName() + "#" + getId()
					+ "#升级完毕当前经验值为" + getExp() + "升级后等级为" + getLevel()
					+ "空身血量为" + getBaseMaxHp() + "空身魔量为" + getBaseMaxMp());
		} else if (gap < 0) {
			WriteLogTxt.Recording("降级记录", "玩家" + getName() + "#" + getId()
					+ "#准备降级，降级前经验值为" + oldexp + "等级为" + getLevel() + "空身血量为"
					+ getBaseMaxHp() + "空身魔量为" + getBaseMaxMp());
			levelDown(gap);
			WriteLogTxt.Recording("降级记录", "玩家" + getName() + "#" + getId()
					+ "#降级完毕当前经验值为" + getExp() + "升级后等级为" + getLevel()
					+ "空身血量为" + getBaseMaxHp() + "空身魔量为" + getBaseMaxMp());
		}
	}

	@Override
	public void onPerceive(final L1PcInstance perceivedFrom) {
		if (isGmInvis() || isGhost() || isInvisble()) {
			return;
		}

		perceivedFrom.addKnownObject(this);
		perceivedFrom.sendPackets(new S_OtherCharPacks(this)); // 自分情报送
		if (isInParty() && getParty().isMember(perceivedFrom)) { // PTHP送
			perceivedFrom.sendPackets(new S_HPMeter(this));
		}

		if (isPrivateShop()) {
			perceivedFrom.sendPackets(new S_DoActionShop(getId(),
					ActionCodes.ACTION_Shop, getShopChat()));
		} else if (isFishing()) {
			perceivedFrom.sendPackets(new S_DoActionGFX(getId(), 71));
		}
		if (getFightId() == perceivedFrom.getId()) {
			sendPackets(new S_PacketBox(S_PacketBox.MSG_DUEL, getFightId(),
					getId()));
		}

		if (getPinkSec() > 0) {
			perceivedFrom.sendPackets(new S_PinkName(getId(), getPinkSec()));
		}
		// 钓鱼动作 end
		if (isCrown()) { // 君主
			final L1Clan clan = L1World.getInstance().getClan(getClanname());
			if (clan != null) {
				if (getId() == clan.getLeaderId() // 血盟主城主
						&& clan.getCastleId() != 0) {
					perceivedFrom.sendPackets(new S_CastleMaster(clan
							.getCastleId(), getId()));
				}
			}
		}
	}

	// 范围外认识济除去
	private void removeOutOfRangeObjects() {
		for (final L1Object known : getKnownObjects()) {
			if (known == null) {
				continue;
			}

			if (Config.PC_RECOGNIZE_RANGE == -1) {
				if (!getLocation().isInScreen(known.getLocation())) { // 画面外
					/*
					 * if (known instanceof L1MonsterInstance) { if
					 * (((L1MonsterInstance) known).getNpcId() == 45291) {
					 * System.out.println("移除45291"); } }
					 */
					removeKnownObject(known);
					sendPackets(new S_RemoveObject(known));
				}
			} else {
				if (getLocation().getTileLineDistance(known.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
					removeKnownObject(known);
					sendPackets(new S_RemoveObject(known));
				}
			}
		}
	}

	// 认识处理
	public void updateObject() {
		removeOutOfRangeObjects();

		// 认识范围内作成
		for (final L1Object visible : L1World.getInstance().getVisibleObjects(
				this, Config.PC_RECOGNIZE_RANGE)) {
			if (!knownsObject(visible)) {
				/*
				 * if (visible instanceof L1MonsterInstance) { if
				 * (((L1MonsterInstance) visible).getNpcId() == 45291) { if
				 * (getName().equals("111")) { System.out.println("一类加入45291");
				 * } } }
				 */
				visible.onPerceive(this);
			} else {
				if (visible instanceof L1NpcInstance) {
					final L1NpcInstance npc = (L1NpcInstance) visible;
					if (getLocation().isInScreen(npc.getLocation())
							&& npc.getHiddenStatus() != 0) {
						/*
						 * if (visible instanceof L1MonsterInstance) { if
						 * (((L1MonsterInstance) visible).getNpcId() == 45291) {
						 * if (getName().equals("111")) {
						 * System.out.println("二类加入45291"); } } }
						 */
						npc.approachPlayer(this);
					}
					// 大地屏障显示效果补充
					if (npc.hasSkillEffect(157)) { // 地屏
						npc.broadcastPacket(new S_Poison(npc.getId(), 2));
					} else if (npc.hasSkillEffect(1010)) { // 木乃伊
						npc.broadcastPacket(new S_Poison(npc.getId(), 2));
					} else if (npc.hasSkillEffect(1011)) { // 木乃伊
						npc.broadcastPacket(new S_Poison(npc.getId(), 2));
					} else if (npc.hasSkillEffect(1009)
							|| npc.hasSkillEffect(1010)
							|| npc.hasSkillEffect(1011)) {
						npc.broadcastPacket(new S_Poison(npc.getId(), 2));
					} else if (npc.get_poisonStatus6() == 4) { // 地屏
						npc.broadcastPacket(new S_Poison(npc.getId(), 2));
					} else if (npc.hasSkillEffect(1006)
							|| npc.hasSkillEffect(1007)
							|| npc.hasSkillEffect(1008)) {
						npc.broadcastPacket(new S_Poison(npc.getId(), 1));
					}
					// 大地屏障显示效果补充 end
				}

				// 大地屏障显示效果补充
				if (visible instanceof L1PcInstance) {
					final L1PcInstance pc = (L1PcInstance) visible;

					if (pc.hasSkillEffect(157)) {
						pc.broadcastPacket(new S_Poison(pc.getId(), 2));
					} else if (pc.hasSkillEffect(1010)) {
						pc.broadcastPacket(new S_Poison(pc.getId(), 2));
					} else if (pc.hasSkillEffect(1011)) {
						pc.broadcastPacket(new S_Poison(pc.getId(), 2));
					} else if (pc.get_poisonStatus6() == 4) {
						pc.broadcastPacket(new S_Poison(pc.getId(), 2));
					} else if (pc.hasSkillEffect(1009)
							|| pc.hasSkillEffect(1010)
							|| pc.hasSkillEffect(1011)) {
						pc.broadcastPacket(new S_Poison(pc.getId(), 2));
					} else if (pc.hasSkillEffect(1006)
							|| pc.hasSkillEffect(1007)
							|| pc.hasSkillEffect(1008)) {
						pc.broadcastPacket(new S_Poison(pc.getId(), 1));
					}
				}
				// 大地屏障显示效果补充 end
			}
			if (visible instanceof L1Character) {
				if (((L1Character) visible).getCurrentHp() > 0) {
					if (isGm() && hasSkillEffect(L1SkillId.GMSTATUS_HPBAR)) {
						sendPackets(new S_HPMeter((L1Character) visible));
					}
				}
			}
		}
	}

	private void sendVisualEffect() {
		int poisonId = 0;
		if (getPoison() != null) { // 毒状态
			poisonId = getPoison().getEffectId();
		}
		if (getParalysis() != null) { // 麻痹状态
			// 麻痹优先送为、poisonId上书。
			poisonId = getParalysis().getEffectId();
		}
		if (poisonId != 0) { // if
			sendPackets(new S_Poison(getId(), poisonId));
			broadcastPacket(new S_Poison(getId(), poisonId));
		}
	}

	public void sendVisualEffectAtLogin() {
		for (final L1Clan clan : L1World.getInstance().getAllClans()) {
			sendPackets(new S_Emblem(clan.getClanId()));
		}

		if (getClanid() != 0) { // 所属
			final L1Clan clan = L1World.getInstance().getClan(getClanname());
			if (clan != null) {
				if (isCrown() && getId() == clan.getLeaderId() && // 、、血盟主自城主
						clan.getCastleId() != 0) {
					sendPackets(new S_CastleMaster(clan.getCastleId(), getId()));
				}
			}
		}

		sendVisualEffect();
	}

	public void sendVisualEffectAtTeleport() {
		if (isDrink()) { // liquor醉
			sendPackets(new S_Liquor(getId()));
		}

		sendVisualEffect();
	}

	public L1PcInstance() {
		_speed = new AcceleratorChecker(this);
		_accessLevel = 0;
		_currentWeapon = 0;
		_inventory = new L1PcInventory(this);
		// _dwarf = new L1DwarfInventory(this);
		// _tradewindow = new L1Inventory();
		_quest = new L1Quest(this);
		_equipSlot = new L1EquipmentSlot(this); //
		_uplevelList = new HashMap<Integer, Integer>();
		_gamSpList = new L1GamSpList(this); // 赌狗
		_action = new L1PCAction(this);
		_tradewindow = new L1Inventory();
		_blessEnchant = new L1PcBlessEnchant(this);
	}

	public L1PcBlessEnchant getBlessEnchant() {
		return _blessEnchant;
	}

	public L1Inventory getTradeWindowInventory() {
		return _tradewindow;
	}

	private final List<Integer> skillList = new ArrayList<Integer>();

	public void setSkillMastery(final int skillid) {
		if (!skillList.contains(skillid)) {
			skillList.add(skillid);
		}
	}

	public void removeSkillMastery(final int skillid) {
		if (skillList.contains(skillid)) {
			skillList.remove((Object) skillid);
		}
	}

	public boolean isSkillMastery(final int skillid) {
		return skillList.contains(skillid);
	}

	public void clearSkillMastery() {
		skillList.clear();
	}

	@Override
	public void setCurrentHp(final int i) {
		if (getCurrentHp() == i) {
			return;
		}
		int currentHp = i;
		if (currentHp >= getMaxHp()) {
			currentHp = getMaxHp();
		}
		setCurrentHpDirect(currentHp);
		sendPackets(new S_HPUpdate(currentHp, getMaxHp()));
		if (isInParty()) { // 中
			getParty().updateMiniHP(this);
		}
	}

	@Override
	public void setCurrentMp(final int i) {
		if (getCurrentMp() == i) {
			return;
		}
		int currentMp = i;
		if (currentMp >= getMaxMp() || isGm()) {
			currentMp = getMaxMp();
		}
		setCurrentMpDirect(currentMp);
		sendPackets(new S_MPUpdate(currentMp, getMaxMp()));
	}

	@Override
	public L1PcInventory getInventory() {
		return _inventory;
	}

	/*
	 * public L1DwarfInventory getDwarfInventory() { return _dwarf; }
	 */

	/*
	 * public L1Inventory getTradeWindowInventory() { return _tradewindow; }
	 */

	public int getCurrentWeapon() {
		return _currentWeapon;
	}

	public void setCurrentWeapon(final int i) {
		_currentWeapon = i;
	}

	public int getType() {
		return _type;
	}

	/**
	 *  0:王族 1:骑士 2:精灵 3:法师 4:黑妖 5:龙骑 6:幻术
	 * 
	 * QQ：1043567675
	 * by：亮修改
	 * 2020年5月6日
	 * 上午11:07:44
	 */
	public void setType(final int i) {
		_type = i;
	}

	public short getAccessLevel() {
		return _accessLevel;
	}

	public void setAccessLevel(final short i) {
		_accessLevel = i;
	}

	public void addAccessLevel(final int i) {
		_accessLevel += i;
	}

	public int getClassId() {
		return _classId;
	}

	public void setClassId(final int i) {
		_classId = i;
		_classFeature = L1ClassFeature.newClassFeature(i);
	}

	private L1ClassFeature _classFeature = null;

	public L1ClassFeature getClassFeature() {
		return _classFeature;
	}

	@Override
	public synchronized long getExp() {
		return _exp;
	}

	@Override
	public synchronized void setExp(final long i) {
		_exp = i;
	}

	private int _PKcount; // ● PK

	public int get_PKcount() {
		return _PKcount;
	}

	public void set_PKcount(final int i) {
		_PKcount = i;
	}

	public void add_PKcount(final int i) {
		_PKcount += i;
	}

	private int _clanid; // ● ＩＤ

	public int getClanid() {
		return _clanid;
	}

	public void setClanid(final int i) {
		_clanid = i;
	}

	private String clanname; // ● 名

	public String getClanname() {
		return clanname;
	}

	public void setClanname(final String s) {
		clanname = s;
	}

	// 照持
	public L1Clan getClan() {
		return L1World.getInstance().getClan(getClanname());
	}

	public L1Pc getPc() {
		return L1World.getInstance().getPc(getAccountName());
	}

	private byte _sex; // ● 性别

	public byte get_sex() {
		return _sex;
	}

	public void set_sex(final int i) {
		_sex = (byte) i;
	}

	public boolean isGm() {
		return _gm;
	}

	public void setGm(final boolean flag) {
		_gm = flag;
	}

	public boolean isMonitor() {
		return _monitor;
	}

	public void setMonitor(final boolean flag) {
		_monitor = flag;
	}

	private L1PcInstance getStat() {
		return null;
	}

	public void reduceCurrentHp(final double d, final L1Character l1character) {
		getStat().reduceCurrentHp(d, l1character);
	}

	/**
	 * 指定群通知
	 * 
	 * @param playersList
	 *            通知配列
	 */
	private void notifyPlayersLogout(final Collection<L1PcInstance> collection) {
		for (final L1PcInstance player : collection) {
			if (player.knownsObject(this)) {
				player.removeKnownObject(this);
				player.sendPackets(new S_RemoveObject(this));
			}
		}
	}

	public void logout() {
		final L1World world = L1World.getInstance();
		if (getClanid() != 0) // 所属
		{
			final L1Clan clan = world.getClan(getClanname());
			if (clan != null) {
				if (clan.getWarehouseUsingChar() == getId()) // 自仓库使用中
				{
					clan.setWarehouseUsingChar(0); // 仓库解除
				}
			}
		}
		if (this.isPrivateShop()
				&& ((this.getMapId() == 340 || this.getMapId() == 350
						|| this.getMapId() == 360 || this.getMapId() == 370) || this
						.getInventory().checkEquipped(25069))) {
			// 离线摆摊
		} else {
			notifyPlayersLogout(getKnownPlayers());
			world.removeWorldObject(this);
			world.removeVisibleObject(this);
			notifyPlayersLogout(world.getRecognizePlayer(this));
			_inventory.clearItems();
			setDead(true);
		}
		_tempObjects.clear();
		// _dwarf.clearItems();
		removeAllKnownObjects();
		stopHpRegeneration();
		stopMpRegeneration();
		setNetConnection(null);
		// 使方、ＮＰＣ消灭
		_spawnBossList.clear();
		_blessEnchant.clear();
		// setPacketOutput(null);
	}

	public void clearTuoJiShop() {
		final L1World world = L1World.getInstance();
		notifyPlayersLogout(getKnownPlayers());
		world.removeWorldObject(this);
		world.removeVisibleObject(this);
		notifyPlayersLogout(world.getRecognizePlayer(this));
		removeAllKnownObjects();
		_inventory.clearItems();
		setDead(true);
	}

	public LineageClient getNetConnection() {
		return _netConnection;
	}

	public void setNetConnection(final LineageClient _client) {
		_netConnection = _client;
	}

	public boolean isInParty() {
		return getParty() != null;
	}

	public L1Party getParty() {
		return _party;
	}

	public void setParty(final L1Party p) {
		_party = p;
	}

	public int getPartyID() {
		return _partyID;
	}

	public void setPartyID(final int partyID) {
		_partyID = partyID;
	}

	public int getTradeID() {
		return _tradeID;
	}

	public void setTradeID(final int tradeID) {
		_tradeID = tradeID;
	}

	public void setTradeOk(final boolean tradeOk) {
		_tradeOk = tradeOk;
	}

	public boolean getTradeOk() {
		return _tradeOk;
	}

	public int getTempID() {
		return _tempID;
	}

	public void setTempID(final int tempID) {
		_tempID = tempID;
	}

	public int getTempCount() {
		return _tempCount;
	}

	public void setTempCount(final int tempCount) {
		_tempCount = tempCount;
	}

	public boolean isTeleport() {
		return _isTeleport;
	}

	public void setTeleport(final boolean flag) {
		_isTeleport = flag;
	}

	public boolean isDrink() {
		return _isDrink;
	}

	public void setDrink(final boolean flag) {
		_isDrink = flag;
	}

	public boolean isGres() {
		return _isGres;
	}

	public void setGres(final boolean flag) {
		_isGres = flag;
	}

	/*
	 * public boolean isPinkName() { return _isPinkName; }
	 * 
	 * public void setPinkName(boolean flag) { _isPinkName = flag; }
	 */

	private final ArrayList<L1PrivateShopSellList> _sellList = new ArrayList<L1PrivateShopSellList>();

	public ArrayList<L1PrivateShopSellList> getSellList() {
		return _sellList;
	}

	private final ArrayList<L1PrivateShopBuyList> _buyList = new ArrayList<L1PrivateShopBuyList>();

	public ArrayList<L1PrivateShopBuyList> getBuyList() {
		return _buyList;
	}

	private String[] _shopChat;

	public void setShopChat(final String[] chat) {
		_shopChat = chat;
	}

	public String[] getShopChat() {
		return _shopChat;
	}

	private boolean _isPrivateShop = false;

	public boolean isPrivateShop() {
		return _isPrivateShop;
	}

	public void setPrivateShop(final boolean flag) {
		_isPrivateShop = flag;
	}

	private boolean _isTradingInPrivateShop = false;

	public boolean isTradingInPrivateShop() {
		return _isTradingInPrivateShop;
	}

	public void setTradingInPrivateShop(final boolean flag) {
		_isTradingInPrivateShop = flag;
	}

	private int _partnersPrivateShopItemCount = 0; // 阅览中个人商店数

	public int getPartnersPrivateShopItemCount() {
		return _partnersPrivateShopItemCount;
	}

	public void setPartnersPrivateShopItemCount(final int i) {
		_partnersPrivateShopItemCount = i;
	}

	/*
	 * private OutputStream _out;
	 * 
	 * public void setPacketOutput(OutputStream out) { _out = out; }
	 */

	public void sendPackets(final ServerBasePacket serverbasepacket) {
		if (_netConnection == null) {
			return;
		}
		try {
			_netConnection.sendPacket(serverbasepacket);
		} catch (final Exception e) {
		}
	}

	public void sendPacketsAll(final ServerBasePacket serverbasepacket) {
		if (_netConnection == null) {
			return;
		}
		try {
			_netConnection.sendPacket(serverbasepacket);
			for (final L1PcInstance pc : L1World.getInstance()
					.getRecognizePlayer(this)) {
				pc.sendPackets(serverbasepacket);
			}
		} catch (final Exception e) {
		}
	}

	@Override
	public void onAction(final L1PcInstance attacker) {
		// XXX:NullPointerException回避。onAction引数型L1Character良？
		if (attacker == null) {
			return;
		}
		// 处理中
		if (isTeleport()) {
			return;
		}
		// 攻击侧攻击侧
		if (getZoneType() == 1 || attacker.getZoneType() == 1) {
			// 攻击送信
			final L1Attack attack_mortion = new L1Attack(attacker, this);
			attack_mortion.action();
			return;
		}

		if (checkNonPvP(this, attacker) == true) {
			return;
		}

		if (getCurrentHp() > 0 && !isDead()) {
			attacker.delInvis();

			final boolean isCounterBarrier = false;
			final L1Attack attack = new L1Attack(attacker, this);
			if (attack.calcHit()) {
				/*
				 * 删除if (hasSkillEffect(L1SkillId.COUNTER_BARRIER)) { L1Magic
				 * magic = new L1Magic(this, attacker); boolean isProbability =
				 * magic .calcProbabilityMagic(L1SkillId.COUNTER_BARRIER);
				 * boolean isShortDistance = attack.isShortDistance(); if
				 * (isProbability && isShortDistance) { isCounterBarrier = true;
				 * } }删除
				 */
				if (!isCounterBarrier) {
					attacker.setPetTarget(this);

					attack.calcDamage();
					attack.calcStaffOfMana();
					attack.addPcPoisonAttack(attacker, this);
				}
			}
			if (isCounterBarrier) {
				attack.actionCounterBarrier();
				attack.commitCounterBarrier();
			} else {
				attack.action();
				attack.commit();
			}
		}
	}

	public boolean checkNonPvP(final L1PcInstance pc, final L1Character target) {
		L1PcInstance targetpc = null;
		if (target instanceof L1PcInstance) {
			targetpc = (L1PcInstance) target;
		} else if (target instanceof L1PetInstance) {
			targetpc = (L1PcInstance) ((L1PetInstance) target).getMaster();
		} else if (target instanceof L1SummonInstance) {
			targetpc = (L1PcInstance) ((L1SummonInstance) target).getMaster();
		}
		if (targetpc == null) {
			return false; // 相手PC、、以外
		}
		if (!Config.ALT_NONPVP) { // Non-PvP设定
			if (getMap().isCombatZone(getLocation())) {
				return false;
			}

			// 全战争取得
			for (final L1War war : L1World.getInstance().getWarList()) {
				if (pc.getClanid() != 0 && targetpc.getClanid() != 0) { // 共所属中
					final boolean same_war = war.CheckClanInSameWar(
							pc.getClanname(), targetpc.getClanname());
					if (same_war == true) { // 同战争参加中
						return false;
					}
				}
			}
			// Non-PvP设定战争中布告攻击可能
			if (target instanceof L1PcInstance) {
				final L1PcInstance targetPc = (L1PcInstance) target;
				if (isInWarAreaAndWarTime(pc.getX(), pc.getY(), pc.getMapId(),
						targetPc)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean isInWarAreaAndWarTime(final int x, final int y,
			final int mapId, final L1PcInstance target) {
		// pctarget战争中战争居
		final L1Location deathloc = new L1Location(x, y, mapId);
		final int castleId = L1CastleLocation.getCastleIdByArea(deathloc);
		final int targetCastleId = L1CastleLocation.getCastleIdByArea(target);
		if (castleId != 0 && targetCastleId != 0 && castleId == targetCastleId) {
			if (WarTimeController.getInstance().isNowWar(castleId)) {
				return true;
			}
		}
		return false;
	}

	public void setPetTarget(final L1Character target) {
		final Object[] petList = getPetList().values().toArray();
		for (final Object pet : petList) {
			if (pet instanceof L1PetInstance) {
				final L1PetInstance pets = (L1PetInstance) pet;
				pets.setMasterTarget(target);
			} else if (pet instanceof L1SummonInstance) {
				final L1SummonInstance summon = (L1SummonInstance) pet;
				summon.setMasterTarget(target);
			}
		}
	}

	public void delInvis() {
		// 魔法接续时间内利用
		if (hasSkillEffect(L1SkillId.INVISIBILITY)) { // 
			killSkillEffectTimer(L1SkillId.INVISIBILITY);
			sendPackets(new S_Invis(getId(), 0));
			broadcastPacket(new S_OtherCharPacks(this));
		}
		if (hasSkillEffect(L1SkillId.BLIND_HIDING)) { //  
			killSkillEffectTimer(L1SkillId.BLIND_HIDING);
			sendPackets(new S_Invis(getId(), 0));
			broadcastPacket(new S_OtherCharPacks(this));
		}
	}

	public void delBlindHiding() {
		// 魔法接续时间终了
		killSkillEffectTimer(L1SkillId.BLIND_HIDING);
		sendPackets(new S_Invis(getId(), 0));
		broadcastPacket(new S_OtherCharPacks(this));
	}

	// 魔法场合使用 (魔法轻减处理) attr:0.无属性魔法,1.地魔法,2.火魔法,3.水魔法,4.风魔法
	public void receiveDamage(final L1Character attacker, int damage,
			final int attr) {
		final Random random = new Random();
		final int player_mr = getMr();
		final int rnd = random.nextInt(100) + 1;
		if (player_mr >= rnd) {
			damage /= 2;
		}
		receiveDamage(attacker, damage ,false);
	}

	public void receiveManaDamage(final L1Character attacker, final int mpDamage) { // 攻击ＭＰ减使用
		if (mpDamage > 0 && !isDead()) {
			delInvis();
			// System.out.println("魔力攻击者:"+attacker.getName());
			L1PinkName.onAction(this, attacker);

			int newMp = getCurrentMp() - mpDamage;
			if (newMp > getMaxMp()) {
				newMp = getMaxMp();
			}

			if (newMp <= 0) {
				newMp = 0;
			}
			setCurrentMp(newMp);
		}
	}

	public void receiveDamage(final L1Character attacker, final int damage, final boolean isCounterBarrier) { // 攻击ＨＰ减使用
		if (getCurrentHp() > 0 && !isDead()) {
			if (attacker != this && !knownsObject(attacker)) {
				attacker.onPerceive(this);
			}

			if (damage > 0) {
				delInvis();
				// System.out.println("攻击者:"+attacker.getName());
				L1PinkName.onAction(this, attacker);
				removeSkillEffect(L1SkillId.FOG_OF_SLEEPING);
				if (attacker instanceof L1PcInstance) {
					/*
					 * if (attacker.isVdmg()) { L1PcInstance player =
					 * (L1PcInstance) attacker; String msg = "输出->" + damage;
					 * S_ChatPacket s_chatpacket = new S_ChatPacket(this, msg,
					 * Opcodes.S_OPCODE_NORMALCHAT);
					 * player.sendPackets(s_chatpacket); }
					 */
					attacker.setAttack(true);
					attacker.setAttacksec(10);
				}
				setAttack(false);
				setAttacksec(10);
				if (attacker.isPVP()) {
					if (attacker instanceof L1PcInstance) {
						final L1PcInstance fightPc = (L1PcInstance) attacker;
						if (fightPc.getFightId() != getId()) {
							fightPc.setFightId(getId());
							fightPc.sendPackets(new S_PacketBox(
									S_PacketBox.MSG_DUEL, fightPc.getFightId(),
									fightPc.getId()));
						}
					}
				}
				L1PcInstance attackPc = null;
				L1NpcInstance attackNpc = null;
				if (attacker instanceof L1PcInstance) {
					attackPc = (L1PcInstance) attacker;// 攻击者为PC

				} else if (attacker instanceof L1NpcInstance) {
					attackNpc = (L1NpcInstance) attacker;// 攻击者为NPC
				}
				if (!isCounterBarrier) {// false:执行反馈
					// 致命身躯(自身具有效果)
					if (this.hasSkillEffect(L1SkillId.MORTAL_BODY)) {
						//System.out.println("對目標進行攻擊");
						if (this.getId() != attacker.getId()) {
							final int rnd = _random.nextInt(100) + 1;
							if ((damage > 0) && (rnd <= 18)) {// 2011-11-26 0-15
								final int dmg = attacker.getLevel() >> 1;// 修改龙骑致命身躯反伤减低一倍.hjx1000
								// SRC DMG = 50
								if (attackPc != null) {
									attackPc.sendPacketsAll(new S_DoActionGFX(
											attackPc.getId(),
											ActionCodes.ACTION_Damage));
									attackPc.receiveDamage(this, dmg,
											true);

								} else if (attackNpc != null) {
									attackNpc
											.broadcastPacket(new S_DoActionGFX(
													attackNpc.getId(),
													ActionCodes.ACTION_Damage));
									attackNpc.receiveDamage(this, dmg);
								}
							}
						}
					}
				}
			}

			int newHp = getCurrentHp() - damage;
			if (newHp > getMaxHp()) {
				newHp = getMaxHp();
			}
			if (newHp <= 0) {
				if (isGm()) {
					setCurrentHp(getMaxHp());
				} else {
					// 死亡时取消冰冻、地屏状态
					if (get_poisonStatus2() == 4) {
						final L1Poison2 poison = get_poison2();
						if (poison != null) {
							poison.CurePoison(this);
							del_poison2();
						}
					}
					if (get_poisonStatus3() == 4) {
						final L1Poison3 poison = get_poison3();
						if (poison != null) {
							poison.CurePoison(this);
							del_poison3();
						}
					}
					if (get_poisonStatus4() == 4) {
						final L1Poison4 poison = get_poison4();
						if (poison != null) {
							poison.CurePoison(this);
							del_poison4();
						}
					}
					if (get_poisonStatus6() == 4) {
						final L1Poison6 poison = get_poison6();
						if (poison != null) {
							poison.CurePoison(this);
							del_poison6();
						}
					}
					// 死亡时取消冰冻、地屏状态 end
					death(attacker);
				}
			}
			if (newHp > 0) {
				setCurrentHp(newHp);
			}
		} else if (!isDead()) { // 念
			System.out.println("警告：ＨＰ减少处理最初ＨＰ０");
			death(attacker);
		}
	}

	public void death(final L1Character lastAttacker) {
		synchronized (this) {
			if (this.isActived()) { // 挂机状态
				if (this.getInventory().consumeItem(40308, 500)) {
					this.setCurrentHp(this.getMaxHp()); // 设置满血
					this.sendPackets(new S_SystemMessage("复活成功.扣除金币500."));
				} else {
					this.sendPackets(new S_SystemMessage(
							"金币不足500复活.本次挂机停止并且回城等待..."));
					this.setActived(false);
					final L1Location newLocation = new L1Location(33437, 32812,
							4).randomLocation(10, false);
					L1Teleport.teleport(this, newLocation.getX(),
							newLocation.getY(), (short) newLocation.getMapId(),
							5, true);
				}
				return;
			}
			if (isDead()) {
				return;
			}
			setDead(true);
			setDeathProcessing(true);
			setStatus(ActionCodes.ACTION_Die);
		}
		GeneralThreadPool.getInstance().execute(new Death(lastAttacker));

	}

	private boolean _deathProcessing;

	/**
	 * 死亡处理中
	 * 
	 * @param deathProcessing
	 */
	public void setDeathProcessing(final boolean deathProcessing) {
		this._deathProcessing = deathProcessing;
	}

	/**
	 * 死亡处理中
	 * 
	 * @return
	 */
	public boolean isDeathProcessing() {
		return this._deathProcessing;
	}

	private class Death implements Runnable {
		L1Character _lastAttacker;

		Death(final L1Character cha) {
			_lastAttacker = cha;
		}

		public void run() {
			final L1Character lastAttacker = _lastAttacker;
			final int deathMapId = L1PcInstance.this.getMapId();
			final int deathLocX = L1PcInstance.this.getX();
			final int deathLocY = L1PcInstance.this.getY();
			_lastAttacker = null;

			setCurrentHp(0);
			setGresValid(false); // EXPG-RES无效
			add_Deathcount(1);

			// 死亡加入魔法娃娃消失
			Object[] petList = getPetList().values().toArray();
			for (Object petObject : petList) {
				if (petObject instanceof L1BabyInstance) { // 
					L1BabyInstance baby = (L1BabyInstance) petObject;
					baby.Death(null);
					getPetList().remove(baby.getId());
				}
			}
			if (isTeleport()) { // 中终待
				try {
					Thread.sleep(300);
				} catch (final Exception e) {
				}
			}

			stopHpRegeneration();
			stopMpRegeneration();

			final int targetobjid = getId();
			getMap().setPassable(getLocation(), true);

			// 解除
			// 变身状态解除、变身状态戾
			int tempchargfx = 0;
			if (hasSkillEffect(L1SkillId.SHAPE_CHANGE)) {
				tempchargfx = getTempCharGfx();
				setTempCharGfxAtDead(tempchargfx);
			} else {
				setTempCharGfxAtDead(getClassId());
			}
			// 
			final L1SkillUse l1skilluse = new L1SkillUse();
			l1skilluse.handleCommands(L1PcInstance.this,
					L1SkillId.CANCELLATION, getId(), getX(), getY(), null, 0,
					L1SkillUse.TYPE_LOGIN);

			if (tempchargfx > 0) {
				sendPacketsAll(new S_ChangeShape(getId(), tempchargfx));
			}

			sendPackets(new S_DoActionGFX(targetobjid, ActionCodes.ACTION_Die));
			broadcastPacket(new S_DoActionGFX(targetobjid,
					ActionCodes.ACTION_Die));

			setDeathProcessing(false);

			// 最后杀、赤
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
				player.add_PKcount(1);
				if (player.getClanid() != 0) {
					L1World.getInstance().broadcastPacketToAll(
							new S_ServerMessage(4533, L1PcInstance.this
									.getName(), player.getClanname(), player
									.getName()));
				} else {
					L1World.getInstance().broadcastPacketToAll(
							new S_ServerMessage(4534, L1PcInstance.this
									.getName(), player.getName()));
				}
				if (L1PcInstance.this.getMapId() == Config.HUODONGMAPID) {
					final L1ItemInstance deathItem = player.getInventory()
							.storeItem(10043, 1);
					if (deathItem != null) {
						player.sendPackets(new S_SystemMessage(String.format(
								"成功击杀对手，获得%s", deathItem.getItem().getName())));
					}
				}
				if (player.isKOGifd()) {
					player.sendPackets(new S_SkillSound(player.getId(), 12111));
				}
			}

			if (lastAttacker != L1PcInstance.this) {
				// 、最后杀
				// or、
				if (L1PcInstance.this.getZoneType() != 0) {
					if (player != null) {
						// 战争中战争居场合例外
						if (!isInWarAreaAndWarTime(deathLocX, deathLocY,
								deathMapId, player)) {
							return;
						}
					}
				}

				final boolean sim_ret = simWarResult(lastAttacker); // 模拟战
				if (sim_ret == true) { // 模拟战中
					return;
				}
			}

			if (!getMap().isEnabledDeathPenalty()) {
				return;
			}

			String attackName = "";
			boolean isDeathEXP = true;
			if (lastAttacker instanceof L1MonsterInstance) {
				attackName = "怪物";
				if (getInventory().consumeItem(99998, 1)) {
					isDeathEXP = false;
					sendPackets(new S_SystemMessage("天神的祝福，死亡并没有经验损失！"));
				}
			} else if (lastAttacker instanceof L1PcInstance) {
				attackName = "玩家";
				if (getInventory().consumeItem(10024, 1)) {
					isDeathEXP = false;
					sendPackets(new S_SystemMessage("天神的祝福，死亡并没有经验损失！"));
					// final L1PcInstance tagerAttackPc =
					// (L1PcInstance)lastAttacker;
					// tagerAttackPc.getInventory().storeItem(40308, 100000);
					// tagerAttackPc.sendPackets(new S_SystemMessage("由于" +
					// L1PcInstance.this.getName() +
					// "玩家携带【经验保护卷(PK)】您获得10万金币奖励！"));
				} else {
					final L1ItemInstance vipring = L1PcInstance.this
							.getInventory().findEquipped(70030);
					if (vipring != null) {
						isDeathEXP = false;
						L1PcInstance.this.getInventory().removeItem(vipring);
						L1PcInstance.this.sendPackets(new S_SystemMessage(
								"天神的祝福，死亡并没有经验损失！"));
						if (player != null) {
							player.getInventory().storeItem(44070, 1);
							L1World.getInstance().broadcastServerMessage(
									String.format("\\F4玩家(:" + getName()
											+ ")不幸牺牲了[" + player.getName()
											+ "]成功掠夺了对方1元宝"));
							// player.sendPackets(new S_SystemMessage("由于"
							// + L1PcInstance.this.getName()
							// + "玩家穿戴了【神力戒指】您获得10万金币奖励！"));
						}
					}
				}
			}
			if (isDeathEXP) {
				deathPenalty(); // EXP
				setGresValid(true); // EXPG-RES有效
				if (getExpRes() == 0) {
					setExpRes(1);
				}
			}
			if (lastAttacker != null) {
				WriteLogTxt.Recording("玩家被打死记录", "玩家 " + getName() + " 被 "
						+ attackName + lastAttacker.getName() + " 打死了！");
			} else {
				WriteLogTxt.Recording("玩家被打死记录", "玩家 " + getName()
						+ " 异常原因（淹死之类的）死亡！");
			}
			// System.out.println("攻击者："+lastAttacker.getName());

			setLastPk(null);

			// 一定确率DROP
			// 32000以上0%、以降-1000每0.4%
			// 0未满场合-1000每0.8%
			// -32000以下最高51.2%DROP率
			if (getLawful() < 32767) {
				int lostRate = _random.nextInt(70) + 1;
				int lostRate1 = _random.nextInt(60) + 1;
				int lostRate2 = _random.nextInt(50) + 1;
				int lostRate3 = _random.nextInt(40) + 1;
				int lostRate4 = _random.nextInt(20) + 1;
				int lostRate5 = _random.nextInt(15) + 1;

				int count = 0;
				int lawful = L1PcInstance.this.getLawful();
				if (lawful <= -32768 + lostRate) {// 小于-30000掉落1~5件
					count = _random.nextInt(5) + 1;

				} else if (lawful > -32768 && lawful <= -30000 + lostRate1) {// 小于-30000掉落1~3件
					count = _random.nextInt(4) + 1;

				} else if (lawful > -30000 && lawful <= -20000 + lostRate2) {// 小于-20000掉落1~3件
					count = _random.nextInt(3) + 1;

				} else if (lawful > -20000 && lawful <= -10000 + lostRate3) {// 小于-10000掉落1~2件
					count = _random.nextInt(2) + 1;

				} else if (lawful > -10000 && lawful <= -0 + lostRate4) {// 小于0掉落1件
					count = _random.nextInt(1) + 1;

				} else if (lawful > 1 && lawful <= 30000 + lostRate5) {// 小于0掉落1件
					count = _random.nextInt(1) + 1;
				}

				if (count > 0) {
					L1PcInstance.this.caoPenaltyResult(count);
				}
			}

			final boolean castle_ret = castleWarResult(deathLocX, deathLocY,
					deathMapId); // 攻城战
			if (castle_ret == true) { // 攻城战中旗内赤
				if (player != null) {
					WriteLogTxt.Recording(
							"攻城玩家PK死亡记录",
							"被打死的玩家 " + getName() + " 正义值为 " + getLawful()
									+ " OBJID#" + getId() + "# 在X:" + getX()
									+ " Y:" + getY() + " MAPID" + getMapId()
									+ "# 被玩家" + player.getName() + "OBJID#"
									+ player.getId() + "# 在X:" + player.getX()
									+ " Y:" + player.getY() + " MAPID"
									+ player.getMapId() + "#打死了，打死之前正义值为"
									+ player.getLawful() + "打死之后正义值为"
									+ player.getLawful());
				}
				return;
			}
			if (player != null) {
				if (getLawful() >= 0 && isPinkName() == false) {
					if (player.getLawful() < 30000) {
						player.setLastPk();
					}
					int lawful;

					final int oldlawful = player.getLawful();

					if (player.getLevel() < 50) {
						lawful = -1
								* (int) ((Math.pow(player.getLevel(), 2) * 4));
					} else {
						lawful = -1
								* (int) ((Math.pow(player.getLevel(), 3) * 0.08));
					}
					if ((player.getLawful() - 1000) < lawful) {
						lawful = player.getLawful() - 1000;
					}

					if (lawful <= -32768) {
						lawful = -32768;
					}
					player.setLawful(lawful);

					final S_Lawful s_lawful = new S_Lawful(player.getId(),
							player.getLawful());
					player.sendPackets(s_lawful);
					player.broadcastPacket(s_lawful);
					WriteLogTxt.Recording(
							"玩家PK掉正义记录",
							"被打死的玩家 " + getName() + " 正义值为 " + getLawful()
									+ " OBJID#" + getId() + "# 在X:" + getX()
									+ " Y:" + getY() + " MAPID" + getMapId()
									+ "# 被玩家" + player.getName() + "OBJID#"
									+ player.getId() + "# 在X:" + player.getX()
									+ " Y:" + player.getY() + " MAPID"
									+ player.getMapId() + "#打死了，打死之前正义值为"
									+ oldlawful + "打死之后正义值为"
									+ player.getLawful());
				} else {
					setPinkSec(0);
					L1PinkName.stopPinkName(L1PcInstance.this);
				}
			}

		}
	}

	private void caoPenaltyResult(final long count) {
		for (int i = 0; i < count; i++) {
			final L1ItemInstance item = getInventory().CaoPenalty();
			if (item != null) {
				if (!item.getItem().isTradable()) {
					continue;
				}
				if (item.getItem().getType2() != 0) {
					if (L1PcInstance.this.getInventory().checkItem(10048)) {
						continue;
					}
				}
				if (item.isSeal()) {
					WriteLogTxt.Recording("玩家喷装记录", "玩家 " + getName()
							+ " 正义值为 " + getLawful() + " OBJID#" + getId()
							+ "# 在X:" + getX() + " Y:" + getY() + " MAPID"
							+ getMapId() + "# 封印物品消失" + item.getLogViewName()
							+ "ITEMOBJID#" + item.getId() + "#");
					sendPackets(new S_ServerMessage(638, item.getLogViewName())); // %0を失いました。
					getInventory().removeItem(item);
				} else {
					final String mapName = MapsTable.getInstance().getMapName(
							this.getMapId(), this.getX(), this.getY());
					L1World.getInstance().broadcastPacketToAll(
							new S_ServerMessage(4538, this.getName(), mapName,
									this.getX() + "," + this.getY(), item
											.getLogViewName()));
					item.setKillDeathName(this.getName());
					getInventory().tradeItem(
							item,
							item.isStackable() ? item.getCount() : 1,
							L1World.getInstance().getInventory(getX(), getY(),
									getMapId()));
					sendPackets(new S_ServerMessage(638, item.getLogViewName())); // %0を失いました。
					WriteLogTxt.Recording("玩家喷装记录", "玩家 " + getName()
							+ " 正义值为 " + getLawful() + " OBJID#" + getId()
							+ "# 在X:" + getX() + " Y:" + getY() + " MAPID"
							+ getMapId() + "# 掉落物品" + item.getLogViewName()
							+ "ITEMOBJID#" + item.getId() + "#");
				}

			}
		}
	}

	public boolean castleWarResult(final int x, final int y, final int mapId) {
		if (getClanid() != 0 && isCrown()) { // 所属中
			final L1Clan clan = L1World.getInstance().getClan(getClanname());
			// 全战争取得
			for (final L1War war : L1World.getInstance().getWarList()) {
				final int warType = war.GetWarType();
				final boolean isInWar = war.CheckClanInWar(getClanname());
				final boolean isAttackClan = war.CheckAttackClan(getClanname());
				if (getId() == clan.getLeaderId() && // 血盟主攻击侧攻城战中
						warType == 1 && isInWar && isAttackClan) {
					final String enemyClanName = war
							.GetEnemyClanName(getClanname());
					if (enemyClanName != null) {
						war.CeaseWar(getClanname(), enemyClanName); // 终结
					}
					break;
				}
			}
		}

		int castleId = 0;
		boolean isNowWar = false;
		castleId = L1CastleLocation.getCastleIdByArea(new L1Location(x, y,
				mapId));
		if (castleId != 0) { // 旗内居
			isNowWar = WarTimeController.getInstance().isNowWar(castleId);
		}
		return isNowWar;
	}

	public boolean simWarResult(final L1Character lastAttacker) {
		if (getClanid() == 0) { // 所属
			return false;
		}
		if (Config.SIM_WAR_PENALTY) { // 模拟战场合false
			return false;
		}
		L1PcInstance attacker = null;
		String enemyClanName = null;
		boolean sameWar = false;

		if (lastAttacker instanceof L1PcInstance) {
			attacker = (L1PcInstance) lastAttacker;
		} else if (lastAttacker instanceof L1PetInstance) {
			attacker = (L1PcInstance) ((L1PetInstance) lastAttacker)
					.getMaster();
		} else if (lastAttacker instanceof L1SummonInstance) {
			attacker = (L1PcInstance) ((L1SummonInstance) lastAttacker)
					.getMaster();
		} else {
			return false;
		}

		// 全战争取得
		for (final L1War war : L1World.getInstance().getWarList()) {
			final L1Clan clan = L1World.getInstance().getClan(getClanname());

			final int warType = war.GetWarType();
			final boolean isInWar = war.CheckClanInWar(getClanname());
			if (attacker != null && attacker.getClanid() != 0) { // lastAttackerPC、、所属中
				sameWar = war.CheckClanInSameWar(getClanname(),
						attacker.getClanname());
			}

			if (getId() == clan.getLeaderId() && // 血盟主模拟战中
					warType == 2 && isInWar == true) {
				enemyClanName = war.GetEnemyClanName(getClanname());
				if (enemyClanName != null) {
					war.CeaseWar(getClanname(), enemyClanName); // 终结
				}
			}

			if (warType == 2 && sameWar) {// 模拟战同战争参加中场合、
				return true;
			}
		}
		return false;
	}

	public void resExp() {
		final int oldLevel = getLevel();
		final int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		int exp = 0;
		if (oldLevel < 45) {
			exp = (int) (needExp * 0.05);
		} else if (oldLevel == 45) {
			exp = (int) (needExp * 0.045);
		} else if (oldLevel == 46) {
			exp = (int) (needExp * 0.04);
		} else if (oldLevel == 47) {
			exp = (int) (needExp * 0.035);
		} else if (oldLevel == 48) {
			exp = (int) (needExp * 0.03);
		} else if (oldLevel >= 49) {
			exp = (int) (needExp * 0.025);
		}

		if (exp == 0) {
			return;
		}
		addExp(exp);
	}

	public void resExp1() {
		final int oldLevel = getLevel();
		final int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		int exp = 0;
		if (oldLevel < 45) {
			exp = (int) (needExp * 0.025);
		} else if (oldLevel == 45) {
			exp = (int) (needExp * 0.0225);
		} else if (oldLevel == 46) {
			exp = (int) (needExp * 0.02);
		} else if (oldLevel == 47) {
			exp = (int) (needExp * 0.0175);
		} else if (oldLevel == 48) {
			exp = (int) (needExp * 0.015);
		} else if (oldLevel >= 49) {
			exp = (int) (needExp * 0.0125);
		}

		if (exp == 0) {
			return;
		}
		addExp(exp);
	}

	public void deathPenalty() {
		final int oldLevel = getLevel();
		final int needExp = ExpTable.getNeedExpNextLevel(oldLevel);
		int exp = 0;
		if (oldLevel >= 1 && oldLevel < 14) {
			exp = 0;
		} else if (oldLevel >= 14 && oldLevel < 45) {
			exp = (int) (needExp * 0.1);
		} else if (oldLevel == 45) {
			exp = (int) (needExp * 0.09);
		} else if (oldLevel == 46) {
			exp = (int) (needExp * 0.08);
		} else if (oldLevel == 47) {
			exp = (int) (needExp * 0.07);
		} else if (oldLevel == 48) {
			exp = (int) (needExp * 0.06);
		} else if (oldLevel >= 49) {
			exp = (int) (needExp * 0.05);
		}

		if (exp == 0) {
			return;
		}
		addExp(-exp);
	}

	private int _etcItemSkillExp = 0;

	public void addEtcItemSkillExp(final int exp) {
		_etcItemSkillExp += exp;
	}

	public int getEtcItemSkillExp() {
		return _etcItemSkillExp;
	}

	private int _etcItemSkillEr = 0;

	public void addEtcItemSkillEr(final int n) {
		_etcItemSkillEr += n;
	}

	public int getEr() {
		if (hasSkillEffect(L1SkillId.STRIKER_GALE)) {
			return 0;
		}

		int er = 0;
		if (isKnight()) {
			er = getLevel() / 4; // 
		} else if (isCrown() || isElf()) {
			er = getLevel() / 8; // 君主
		} else if (isDarkelf()) {
			er = getLevel() / 6; // 
		} else if (isWizard()) {
			er = getLevel() / 10; // 
		} else if (this.isDragonKnight()) {
			er = this.getLevel() / 7; // 龙骑士

		} else if (this.isIllusionist()) {
			er = this.getLevel() / 9; // 幻术师
		}

		er += (getDex() - 8) / 2;

		if (hasSkillEffect(L1SkillId.DRESS_EVASION)) {
			er += 12;
		}
		if (hasSkillEffect(L1SkillId.SOLID_CARRIAGE)) {
			er += 15;
		}

		er += _etcItemSkillEr;

		return er;
	}

	public L1ItemInstance getWeapon() {
		return _weapon;
	}

	public void setWeapon(final L1ItemInstance weapon) {
		_weapon = weapon;
	}

	public L1Quest getQuest() {
		return _quest;
	}

	/**
	 * 王族
	 * @return
	 */
	public boolean isCrown() {
		return (getClassId() == CLASSID_PRINCE || getClassId() == CLASSID_PRINCESS);
	}

	/**
	 * 骑士
	 * @return
	 */
	public boolean isKnight() {
		return (getClassId() == CLASSID_KNIGHT_MALE || getClassId() == CLASSID_KNIGHT_FEMALE);
	}

	/**
	 * 妖精
	 * @return
	 */
	public boolean isElf() {
		return (getClassId() == CLASSID_ELF_MALE || getClassId() == CLASSID_ELF_FEMALE);
	}

	/**
	 * 法师
	 * @return
	 */
	public boolean isWizard() {
		return (getClassId() == CLASSID_WIZARD_MALE || getClassId() == CLASSID_WIZARD_FEMALE);
	}

	/**
	 * 黑妖
	 * @return
	 */
	public boolean isDarkelf() {
		return (getClassId() == CLASSID_DARK_ELF_MALE || getClassId() == CLASSID_DARK_ELF_FEMALE);
	}

	/**
	 * 龙骑士
	 * 
	 * @return
	 */
	public boolean isDragonKnight() {
		return ((this.getClassId() == CLASSID_DRAGON_KNIGHT_MALE) || (this
				.getClassId() == CLASSID_DRAGON_KNIGHT_FEMALE));
	}

	/**
	 * 幻术师
	 * 
	 * @return
	 */
	public boolean isIllusionist() {
		return ((this.getClassId() == CLASSID_ILLUSIONIST_MALE) || (this
				.getClassId() == CLASSID_ILLUSIONIST_FEMALE));
	}

	private static final Log _log = LogFactory.getLog(L1PcInstance.class);

	private LineageClient _netConnection;
	private int _classId;
	private int _type;
	private long _exp;
	private final L1Karma _karma = new L1Karma();
	private boolean _gm;
	private boolean _monitor;

	private short _accessLevel;
	private int _currentWeapon;
	private final L1PcInventory _inventory;
	private final L1PcBlessEnchant _blessEnchant;
	// private final L1DwarfInventory _dwarf;
	// private final L1Inventory _tradewindow;
	private L1ItemInstance _weapon;
	private L1Party _party;
	private int _partyID;
	private int _tradeID;
	private boolean _tradeOk;
	private int _tempID;
	private int _tempCount;
	private boolean _isTeleport = false;
	private boolean _isDrink = false;
	private boolean _isGres = false;
	// private boolean _isPinkName = false;
	private final L1Quest _quest;
	private MpRegeneration _mpRegen;
	private MpRegenerationByDoll _mpRegenByDoll;
	private HpRegeneration _hpRegen;
	// private static Timer _regenTimer = new Timer(true);
	private boolean _mpRegenActive;
	private boolean _mpRegenActiveByDoll;
	private boolean _hpRegenActive;
	private final L1EquipmentSlot _equipSlot;
	private final L1GamSpList _gamSpList; // 赌狗特殊清单资料暂存
	private final L1PCAction _action;

	public L1PCAction getAction() {
		return _action;
	}

	private String _accountName; // ● 

	public String getAccountName() {
		return _accountName;
	}

	public void setAccountName(final String s) {
		_accountName = s;
	}

	private int _baseMaxHp = 0; // ● ＭＡＸＨＰ（1～32767）

	public int getBaseMaxHp() {
		return _baseMaxHp;
	}

	public void addBaseMaxHp(int randomHp) {
		randomHp += _baseMaxHp;
		if (randomHp >= 32767) {
			randomHp = 32767;
		} else if (randomHp < 1) {
			randomHp = 1;
		}
		addMaxHp(randomHp - _baseMaxHp);
		_baseMaxHp = randomHp;
	}

	private int _baseMaxMp = 0; // ● ＭＡＸＭＰ（0～32767）

	public int getBaseMaxMp() {
		return _baseMaxMp;
	}

	public void addBaseMaxMp(int randomMp) {
		randomMp += _baseMaxMp;
		if (randomMp >= 32767) {
			randomMp = 32767;
		} else if (randomMp < 0) {
			randomMp = 0;
		}
		addMaxMp(randomMp - _baseMaxMp);
		_baseMaxMp = randomMp;
	}

	private int _baseAc = 0; // ● ＡＣ（-128～127）

	public int getBaseAc() {
		return _baseAc;
	}

	private byte _baseStr = 0; // ● ＳＴＲ（1～127）

	public byte getBaseStr() {
		return _baseStr;
	}

	public void addBaseStr(byte i) {
		i += _baseStr;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addStr((byte) (i - _baseStr));
		_baseStr = i;
	}

	private byte _baseCon = 0; // ● ＣＯＮ（1～127）

	public byte getBaseCon() {
		return _baseCon;
	}

	public void addBaseCon(byte i) {
		i += _baseCon;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addCon((byte) (i - _baseCon));
		_baseCon = i;
	}

	private byte _baseDex = 0; // ● ＤＥＸ（1～127）

	public byte getBaseDex() {
		return _baseDex;
	}

	public void addBaseDex(byte i) {
		i += _baseDex;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addDex((byte) (i - _baseDex));
		_baseDex = i;
	}

	private byte _baseCha = 0; // ● ＣＨＡ（1～127）

	public byte getBaseCha() {
		return _baseCha;
	}

	public void addBaseCha(byte i) {
		i += _baseCha;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addCha((byte) (i - _baseCha));
		_baseCha = i;
	}

	private byte _baseInt = 0; // ● ＩＮＴ（1～127）

	public byte getBaseInt() {
		return _baseInt;
	}

	public void addBaseInt(byte i) {
		i += _baseInt;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addInt((byte) (i - _baseInt));
		_baseInt = i;
	}

	private byte _baseWis = 0; // ● ＷＩＳ（1～127）

	public byte getBaseWis() {
		return _baseWis;
	}

	public void addBaseWis(byte i) {
		i += _baseWis;
		if (i >= 127) {
			i = 127;
		} else if (i < 1) {
			i = 1;
		}
		addWis((byte) (i - _baseWis));
		_baseWis = i;
	}

	private int _baseDmgup = 0; // ● 补正（-128～127）

	public int getBaseDmgup() {
		return _baseDmgup;
	}

	private int _baseHitup = 0; // ● 命中补正（-128～127）

	public int getBaseHitup() {
		return _baseHitup;
	}

	private int _baseBowHitup = 0; // ● 弓命中补正（-128～127）

	public int getBaseBowHitup() {
		return _baseBowHitup;
	}

	private int _baseMr = 0; // ● 魔法防御（0～）

	public int getBaseMr() {
		return _baseMr;
	}

	private int _advenHp; // ● //  增加ＨＰ

	public int getAdvenHp() {
		return _advenHp;
	}

	public void setAdvenHp(final int i) {
		_advenHp = i;
	}

	private int _advenMp; // ● //  增加ＭＰ

	public int getAdvenMp() {
		return _advenMp;
	}

	public void setAdvenMp(final int i) {
		_advenMp = i;
	}

	private int _bonusStats; // ● 割振

	public int getBonusStats() {
		return _bonusStats;
	}

	public void setBonusStats(final int i) {
		_bonusStats = i;
	}

	private int _elixirStats; // ● 上

	public int getElixirStats() {
		return _elixirStats;
	}

	public void setElixirStats(final int i) {
		_elixirStats = i;
	}

	private int _elfAttr; // ● 属性

	public int getElfAttr() {
		return _elfAttr;
	}

	public void setElfAttr(final int i) {
		_elfAttr = i;
	}

	// 判断弱化属性减少的属性
	private int _PcAttr;

	public int get_PcAttr() {
		return _PcAttr;
	}

	public void set_PcAttr(final int i) {
		_PcAttr = i;
	}

	// 判断弱化属性减少的属性 end

	private int _expRes; // ● EXP复旧

	public int getExpRes() {
		return _expRes;
	}

	public void setExpRes(final int i) {
		_expRes = i;
	}

	private int _partnerId = 0; // ● 结婚相手

	public int getPartnerId() {
		return _partnerId;
	}

	public void setPartnerId(final int i) {
		_partnerId = i;
	}

	private int _onlineStatus; // ● 状态

	public int getOnlineStatus() {
		return _onlineStatus;
	}

	public void setOnlineStatus(final int i) {
		_onlineStatus = i;
	}

	private int _homeTownId; // ● 

	public int getHomeTownId() {
		return _homeTownId;
	}

	public void setHomeTownId(final int i) {
		_homeTownId = i;
	}

	private int _contribution; // ● 贡献度

	public int getContribution() {
		return _contribution;
	}

	public void setContribution(final int i) {
		_contribution = i;
	}

	// 地狱滞在时间（秒）
	private int _hellTime;

	public int getHellTime() {
		return _hellTime;
	}

	public void setHellTime(final int i) {
		_hellTime = i;
	}

	private boolean _banned; // ● 冻结

	public boolean isBanned() {
		return _banned;
	}

	public void setBanned(final boolean flag) {
		_banned = flag;
	}

	private int _food; // ● 满腹度

	public int get_food() {
		return _food;
	}

	public void set_food(final int i) {
		_food = i;
		if (_food > 225) {
			_food = 225;
		}
		_food = i;
		if (_food == 225) {// LOLI 生存呐喊
			final Calendar cal = Calendar.getInstance();
			long h_time = cal.getTimeInMillis() / 1000;// 换算为秒
			set_h_time(h_time);// 纪录吃饱时间

		} else {
			set_h_time(-1);// 纪录吃饱时间
		}
	}

	public L1EquipmentSlot getEquipSlot() {
		return _equipSlot;
	}

	public static L1PcInstance load(final String charName) {
		L1PcInstance result = null;
		try {
			result = CharacterTable.getInstance().loadCharacter(charName);
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return result;
	}

	/**
	 * 状态书迂。
	 * 
	 * @throws Exception
	 */
	public void save() throws Exception {
		if (isGhost()) {
			WriteLogTxt.Recording("人物数据保存失败记录", "玩家:" + this.getName()
					+ " 保存数据失败  原因:isGhost");
			return;
		}

		CharacterTable.getInstance().storeCharacter(this);
	}

	/**
	 * 态书。
	 */
	public void saveInventory() {
		for (final L1ItemInstance item : getInventory().getItems()) {
			getInventory().saveItem(item, item.getRecordingColumns());
		}
	}

	public static final int REGENSTATE_NONE = 4;
	public static final int REGENSTATE_MOVE = 2;
	public static final int REGENSTATE_ATTACK = 1;

	public void setRegenState(final int state) {
		if (_mpRegen != null) {
			_mpRegen.setState(state);
		}
		if (_hpRegen != null) {
			_hpRegen.setState(state);
		}
	}

	public MpRegeneration getMpRegeneration() {
		return _mpRegen;
	}

	public HpRegeneration getHpRegeneration() {
		return _hpRegen;
	}

	public double getMaxWeight() {
		final int str = getStr();
		final int con = getCon();
		double maxWeight = (150 * (Math.floor(0.6 * str + 0.4 * con + 1)))
				* get_weightUPByDoll();
		// double maxWeight = 1500 + 150 * ((str + con - 18) / 2);

		final int weightReductionByArmor = getWeightReduction(); // 防具重量减

		int weightReductionByDoll = 0; // 重量减
		final Object[] dollList = getDollList().values().toArray();
		for (final Object dollObject : dollList) {
			final L1DollInstance doll = (L1DollInstance) dollObject;
			weightReductionByDoll += doll.getWeightReductionByDoll();
		}

		int weightReductionByMagic = 0;
		if (hasSkillEffect(L1SkillId.DECREASE_WEIGHT)) { // 
			weightReductionByMagic = 10;
		}

		double originalWeightReduction = 1; // オリジナルステータスによる重量轻减
		originalWeightReduction += 0.04 * (getOriginalStrWeightReduction() + getOriginalConWeightReduction());

		final int weightReduction = weightReductionByArmor
				+ weightReductionByDoll + weightReductionByMagic;
		maxWeight += ((maxWeight / 100) * weightReduction);

		maxWeight *= Config.RATE_WEIGHT_LIMIT; // 挂

		maxWeight *= originalWeightReduction;

		return maxWeight;
	}

	public boolean isFastMovable() {
		return (hasSkillEffect(L1SkillId.HOLY_WALK)
				|| hasSkillEffect(L1SkillId.MOVING_ACCELERATION) || hasSkillEffect(L1SkillId.WIND_WALK));
	}

	public boolean isBrave() {
		return hasSkillEffect(L1SkillId.STATUS_BRAVE);
	}

	public boolean isHaste() {
		return (hasSkillEffect(L1SkillId.STATUS_HASTE)
				|| hasSkillEffect(L1SkillId.HASTE)
				|| hasSkillEffect(L1SkillId.GREATER_HASTE) || getMoveSpeed() == 1);
	}

	private int invisDelayCounter = 0;

	public boolean isInvisDelay() {
		return (invisDelayCounter > 0);
	}

	private final Object _invisTimerMonitor = new Object();

	public void addInvisDelayCounter(final int counter) {
		synchronized (_invisTimerMonitor) {
			invisDelayCounter += counter;
		}
	}

	private static final long DELAY_INVIS = 3000L;

	public void beginInvisTimer() {
		addInvisDelayCounter(1);
		GeneralThreadPool.getInstance().pcSchedule(new L1PcInvisDelay(this),
				DELAY_INVIS);
	}

	/*
	 * private long _oldMoveTimeInMillis = 0L; private int _moveInjustice = 0;
	 * 
	 * public void checkMoveInterval() { long nowMoveTimeInMillis =
	 * System.currentTimeMillis();
	 * 
	 * long moveInterval = nowMoveTimeInMillis - _oldMoveTimeInMillis; byte
	 * speed = (byte) 16; // 速度单位 L1WilliamPlayerSpeed Player_Speed =
	 * PlayerSpeed.getInstance().getTemplate(getTempCharGfx()); if (Player_Speed
	 * != null) { switch(getCurrentWeapon()) { case 0: { // 空手 if
	 * (Player_Speed.getMove_0() != 0) { speed = (byte)
	 * Player_Speed.getMove_0(); } } break; case 4: { // 单手剑 if
	 * (Player_Speed.getMove_4() != 0) { speed = (byte)
	 * Player_Speed.getMove_4(); } } break; case 11: { // 斧头 if
	 * (Player_Speed.getMove_11() != 0) { speed = (byte)
	 * Player_Speed.getMove_11(); } } break; case 20: { // 弓箭 if
	 * (Player_Speed.getMove_20() != 0) { speed = (byte)
	 * Player_Speed.getMove_20(); } } break; case 24: { // 枪矛 if
	 * (Player_Speed.getMove_24() != 0) { speed = (byte)
	 * Player_Speed.getMove_24(); } } break; case 40: { // 法杖 if
	 * (Player_Speed.getMove_40() != 0) { speed = (byte)
	 * Player_Speed.getMove_40(); } } break; case 46: { // 匕首 if
	 * (Player_Speed.getMove_46() != 0) { speed = (byte)
	 * Player_Speed.getMove_46(); } } break; case 50: { // 双手剑 if
	 * (Player_Speed.getMove_50() != 0) { speed = (byte)
	 * Player_Speed.getMove_50(); } } break; case 54: { // 双刀 if
	 * (Player_Speed.getMove_54() != 0) { speed = (byte)
	 * Player_Speed.getMove_54(); } } break; case 58: { // 钢爪 if
	 * (Player_Speed.getMove_58() != 0) { speed = (byte)
	 * Player_Speed.getMove_58(); } } break; case 62: { // 铁手甲 if
	 * (Player_Speed.getMove_62() != 0) { speed = (byte)
	 * Player_Speed.getMove_62(); } } break; }
	 * 
	 * if (Player_Speed.getMoveDouble() != 0) {
	 * switch(Player_Speed.getMoveDouble()) { case 16: { // 110.(16) speed *=
	 * 1.5; } break; case 36: { // 110.(36) speed /= 1.5; } break; case 48: { //
	 * 110.(48) speed /= 2; } break; case 54: { // 110.(54) speed /= 2.25; }
	 * break; case 60: { // 110.(60) speed /= 2.5; } break; case 72: { //
	 * 110.(72) speed /= 3; } break; case 84: { // 110.(84) speed /= 3.5; }
	 * break; case 96: { // 110.(96) speed /= 4; } break; } } }
	 * 
	 * double MoveSpeed = speed * 40; // 移动速度 = 时间单位 * 40
	 * 
	 * if (hasSkillEffect(L1SkillId.SLOW) || hasSkillEffect(L1SkillId.MASS_SLOW)
	 * || hasSkillEffect(L1SkillId.ENTANGLE) || getMoveSpeed() == 2) { //
	 * 缓速、集缓、地障 MoveSpeed = (MoveSpeed * 2); } if
	 * (hasSkillEffect(L1SkillId.STATUS_HASTE) ||
	 * hasSkillEffect(L1SkillId.HASTE) ||
	 * hasSkillEffect(L1SkillId.GREATER_HASTE) || getMoveSpeed() == 1) { //
	 * 绿水、加速 MoveSpeed = (MoveSpeed / 1.33); } if
	 * (hasSkillEffect(L1SkillId.STATUS_BRAVE) ||
	 * hasSkillEffect(L1SkillId.HOLY_WALK) ||
	 * hasSkillEffect(L1SkillId.MOVING_ACCELERATION) ||
	 * hasSkillEffect(L1SkillId.WIND_WALK)) { // 勇水、神走、风走、行走 MoveSpeed =
	 * (MoveSpeed / 1.5); }
	 * 
	 * //sendPackets(new S_ServerMessage(166, "限制速度：(" + MoveSpeed + ")"));
	 * //sendPackets(new S_ServerMessage(166, "移动速度：(" + moveInterval + ")"));
	 * 
	 * if (MoveSpeed >= moveInterval) { // 移动要求间隔短 _moveInjustice++;
	 * //sendPackets(new S_ServerMessage(166, "违规次数：(" + _moveInjustice + ")"));
	 * if (_moveInjustice >= 5) {
	 * _log.info(L1WilliamSystemMessage.ShowMessage(1089) + " (" + getName() +
	 * ") " + L1WilliamSystemMessage.ShowMessage(1090)); // 记录在文件档
	 * writeInfo(L1WilliamSystemMessage.ShowMessage(1094) + ": (" + getName() +
	 * ") " + L1WilliamSystemMessage.ShowMessage(1095) + "，" +
	 * L1WilliamSystemMessage.ShowMessage(1091) + ": (" + getLastOnline() + ")，"
	 * + " Poly: (" + getTempCharGfx() + ")。"); // 记录在文件档 end if (!isGm()) { //
	 * 告知全体玩家 BroadCastToAll(L1WilliamSystemMessage.ShowMessage(1089) + " (" +
	 * getName() + ") " + L1WilliamSystemMessage.ShowMessage(1090)); // 告知全体玩家
	 * end sendPackets(new S_Disconnect()); } _moveInjustice = 0; } } else { //
	 * 移动要求间隔正常 _moveInjustice = 0; }
	 * 
	 * _oldMoveTimeInMillis = nowMoveTimeInMillis; }
	 * 
	 * private long _oldAttackTimeInMillis = 0L; private int _attackInjustice =
	 * 0;
	 * 
	 * public int getAttackInjustice() { return _attackInjustice; }
	 * 
	 * public void checkAttackInterval() { long nowAttackTimeInMillis =
	 * System.currentTimeMillis(); long attckInterval = nowAttackTimeInMillis -
	 * _oldAttackTimeInMillis; byte speed = (byte) 24; // 速度单位
	 * L1WilliamPlayerSpeed Player_Speed =
	 * PlayerSpeed.getInstance().getTemplate(getTempCharGfx()); if (Player_Speed
	 * != null) { switch(getCurrentWeapon()) { case 0: { // 空手 if
	 * (Player_Speed.getAtk_0() != 0) { speed = (byte) Player_Speed.getAtk_0();
	 * } } break; case 4: { // 单手剑 if (Player_Speed.getAtk_4() != 0) { speed =
	 * (byte) Player_Speed.getAtk_4(); } } break; case 11: { // 斧头 if
	 * (Player_Speed.getAtk_11() != 0) { speed = (byte)
	 * Player_Speed.getAtk_11(); } } break; case 20: { // 弓箭 if
	 * (Player_Speed.getAtk_20() != 0) { speed = (byte)
	 * Player_Speed.getAtk_20(); } } break; case 24: { // 枪矛 if
	 * (Player_Speed.getAtk_24() != 0) { speed = (byte)
	 * Player_Speed.getAtk_24(); } } break; case 40: { // 法杖 if
	 * (Player_Speed.getAtk_40() != 0) { speed = (byte)
	 * Player_Speed.getAtk_40(); } } break; case 46: { // 匕首 if
	 * (Player_Speed.getAtk_46() != 0) { speed = (byte)
	 * Player_Speed.getAtk_46(); } } break; case 50: { // 双手剑 if
	 * (Player_Speed.getAtk_50() != 0) { speed = (byte)
	 * Player_Speed.getAtk_50(); } } break; case 54: { // 双刀 if
	 * (Player_Speed.getAtk_54() != 0) { speed = (byte)
	 * Player_Speed.getAtk_54(); } } break; case 58: { // 钢爪 if
	 * (Player_Speed.getAtk_58() != 0) { speed = (byte)
	 * Player_Speed.getAtk_58(); } } break; case 62: { // 铁手甲 if
	 * (Player_Speed.getAtk_62() != 0) { speed = (byte)
	 * Player_Speed.getAtk_62(); } } break; }
	 * 
	 * if (Player_Speed.getAtkDouble() != 0) {
	 * switch(Player_Speed.getAtkDouble()) { case 16: { speed *= 1.5; } break;
	 * case 36: { speed /= 1.5; } break; case 48: { speed /= 2; } break; case
	 * 54: { speed /= 2.25; } break; case 60: { speed /= 2.5; } break; case 72:
	 * { speed /= 3; } break; case 84: { speed /= 3.5; } break; case 96: { speed
	 * /= 4; } break; } } }
	 * 
	 * double AtkSpeed = speed * 40; // 攻击速度 = 速度单位 * 40
	 * 
	 * if (hasSkillEffect(L1SkillId.SLOW) || hasSkillEffect(L1SkillId.MASS_SLOW)
	 * || hasSkillEffect(L1SkillId.ENTANGLE) || getMoveSpeed() == 2) { //
	 * 缓速、集缓、地障 AtkSpeed = (AtkSpeed * 2); } if
	 * (hasSkillEffect(L1SkillId.STATUS_HASTE) ||
	 * hasSkillEffect(L1SkillId.HASTE) ||
	 * hasSkillEffect(L1SkillId.GREATER_HASTE) || getMoveSpeed() == 1) { //
	 * 绿水、加速 AtkSpeed = (AtkSpeed / 1.33); } if
	 * (hasSkillEffect(L1SkillId.STATUS_BRAVE)) { // 勇水 AtkSpeed = (AtkSpeed /
	 * 1.5); }
	 * 
	 * //sendPackets(new S_ServerMessage(166, "限制速度：(" + AtkSpeed + ")"));
	 * //sendPackets(new S_ServerMessage(166, "攻击速度：(" + attckInterval + ")"));
	 * 
	 * if (AtkSpeed >= attckInterval) { // 攻击要求间隔短 _attackInjustice++;
	 * //sendPackets(new S_ServerMessage(166, "违规次数：(" + _attackInjustice +
	 * ")")); if (_attackInjustice >= 5) {
	 * _log.info(L1WilliamSystemMessage.ShowMessage(1089) + " (" + getName() +
	 * ") " + L1WilliamSystemMessage.ShowMessage(1092)); // 记录在文件档
	 * writeInfo(L1WilliamSystemMessage.ShowMessage(1094) + ": (" + getName() +
	 * ") " + L1WilliamSystemMessage.ShowMessage(1096) + "，" +
	 * L1WilliamSystemMessage.ShowMessage(1091) + ": (" + getLastOnline() + ")，"
	 * + " Poly: (" + getTempCharGfx() + ")。"); // 记录在文件档 end if (!isGm()) { //
	 * 告知全体玩家 BroadCastToAll(L1WilliamSystemMessage.ShowMessage(1089) + " (" +
	 * getName() + ") " + L1WilliamSystemMessage.ShowMessage(1092)); // 告知全体玩家
	 * end sendPackets(new S_Disconnect()); } _attackInjustice = 0; } } else {
	 * // 攻击要求间隔正常 _attackInjustice = 0; } _oldAttackTimeInMillis =
	 * nowAttackTimeInMillis; }
	 */

	public void addExp(final int exp) {
		synchronized (this) {
			_exp += exp;
			if (_exp > ExpTable.MAX_EXP) {
				_exp = ExpTable.MAX_EXP;
			}
			onChangeExp();
		}
	}

	public synchronized void addContribution(final int contribution) {
		_contribution += contribution;
	}

	/*
	 * public void beginExpMonitor() { _expMonitorFuture =
	 * GeneralThreadPool.getInstance() .pcScheduleAtFixedRate(new
	 * L1PcExpMonitor(this), 0L, INTERVAL_EXP_MONITOR); }
	 */
	private void levelUp(final int gap) {
		// int level = getLevel();
		resetLevel();

		// 复活
		if (getLevel() == 99 && Config.ALT_REVIVAL_POTION) {
			try {
				final L1Item l1item = ItemTable.getInstance()
						.getTemplate(43000);
				if (l1item != null) {
					getInventory().storeItem(43000, 1);
					sendPackets(new S_ServerMessage(403, l1item.getName()));
				} else {
					sendPackets(new S_SystemMessage("无法取得转生药水。"));
				}
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
				sendPackets(new S_SystemMessage("无法取得转生药水。"));
			}
		}

		for (int i = 0; i < gap; i++) {
			// System.out.println("循环:"+i+" gap:"+gap);
			final int randomHp = CalcStat.calcStatHp(getType(), getBaseMaxHp(),
					getBaseCon());
			final int randomMp = CalcStat.calcStatMp(getType(), getBaseMaxMp(),
					getBaseWis());
			/*
			 * int newhp = getBaseMaxHp() + randomHp; int newmp = getBaseMaxMp()
			 * + randomMp; level += i; L1PcHpMp pcHpMp; if
			 * (_levelhpmpup.containsKey(getId())) { pcHpMp =
			 * _levelhpmpup.get(getId()); if
			 * (_levelhpmpup.get(getId()).getHp().containsKey(level)) { //
			 * System
			 * .out.println("升级原始血量存在读取原始血量："+_levelhpmpup.get(getId()).getHp
			 * ().get(level)+"  等级："+level); if (newhp >
			 * _levelhpmpup.get(getId()).getHp().get(level)) { //
			 * System.out.println("新的newhp大于"+
			 * _levelhpmpup.get(getId()).getHp().get(level)); randomHp =
			 * _levelhpmpup.get(getId()).getHp().get(level) - getBaseMaxHp(); }
			 * }else { pcHpMp.puthpmp(level, newhp, newmp);
			 * _levelhpmpup.put(getId(), pcHpMp); //
			 * System.out.println("存入玩家血魔资料："+newhp+"   "+newmp+"  等级："+level);
			 * } if (_levelhpmpup.get(getId()).getMp().containsKey(level)) { //
			 * System
			 * .out.println("升级原始魔量存在读取原始魔量："+_levelhpmpup.get(getId()).getMp
			 * ().get(level)+"  等级："+level); if (newmp >
			 * _levelhpmpup.get(getId()).getMp().get(level)) { //
			 * System.out.println("新的newmp大于"+
			 * _levelhpmpup.get(getId()).getMp().get(level)); randomMp =
			 * _levelhpmpup.get(getId()).getMp().get(level) - getBaseMaxMp(); }
			 * }else { pcHpMp.puthpmp(level, newhp, newmp);
			 * _levelhpmpup.put(getId(), pcHpMp); //
			 * System.out.println("存入玩家血魔资料："+newhp+"   "+newmp+"  等级："+level);
			 * }
			 * 
			 * }else { pcHpMp = new L1PcHpMp(newhp, newmp, level); //
			 * System.out.println("存入玩家血魔资料："+newhp+"   "+newmp+"  等级："+level);
			 * _levelhpmpup.put(getId(), pcHpMp); }
			 */
			addBaseMaxHp(randomHp);
			addBaseMaxMp(randomMp);
			// 升级血魔满
			setCurrentHp(getMaxHp());
			setCurrentMp(getMaxMp());
			// 升级血魔满 end
		}
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseAc();
		resetBaseMr();

		try {
			// DB情报书迂
			save();
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		// 
		if (getLevel() >= 51 && getLevel() - 50 > getBonusStats()) {
			if ((getBaseStr() + getBaseDex() + getBaseCon() + getBaseInt()
					+ getBaseWis() + getBaseCha()) < (Config.BONUS_STATS1 * 6)) { // 调整能力值上限
				sendPackets(new S_bonusstats(getId(), 1));
			}
		}
		sendPackets(new S_OwnCharStatus(this));
		Reward.getInstance().getItem(this);
		// l1j.william.Reward.getItem(this); // 升级奖励道具
	}

	private void levelDown(final int gap) {
		// int level = getLevel();
		resetLevel();

		for (int i = 0; i > gap; i--) {
			// 时值为、base值0设定
			final int randomHp = CalcStat
					.calcStatHp(getType(), 0, getBaseCon());
			final int randomMp = CalcStat
					.calcStatMp(getType(), 0, getBaseWis());
			// level -= i;
			/*
			 * if (_levelhpup.get(level)!=null) { randomHp =
			 * _levelhpup.get(getLevel()); } if (_levelmpup.get(level)!=null) {
			 * randomMp = _levelmpup.get(getLevel()); } _levelhpup.put(level,
			 * randomHp); _levelmpup.put(level, randomMp);
			 */
			addBaseMaxHp(-randomHp);
			addBaseMaxMp(-randomMp);
		}
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseAc();
		resetBaseMr();

		try {
			// DB情报书迂
			save();
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		sendPackets(new S_OwnCharStatus(this));
	}

	public void beginGameTimeCarrier() {
		new L1GameTimeCarrier(this).start();
	}

	/*
	 * private boolean _ghost = false; // 
	 * 
	 * public boolean isGhost() { return _ghost; }
	 * 
	 * private void setGhost(boolean flag) { _ghost = flag; }
	 */

	private boolean _ghostCanTalk = true; // NPC话

	public boolean isGhostCanTalk() {
		return _ghostCanTalk;
	}

	private void setGhostCanTalk(final boolean flag) {
		_ghostCanTalk = flag;
	}

	public void beginGhost(final int locx, final int locy, final short mapid,
			final boolean canTalk) {
		beginGhost(locx, locy, mapid, canTalk, 0);
	}

	public void beginGhost(final int locx, final int locy, final short mapid,
			final boolean canTalk, final int sec) {
		if (isGhost()) {
			return;
		}
		setGhost(true);
		_ghostSaveLocX = getX();
		_ghostSaveLocY = getY();
		_ghostSaveMapId = getMapId();
		_ghostSaveHeading = getHeading();
		setGhostCanTalk(canTalk);
		L1Teleport.teleport(this, locx, locy, mapid, 5, true);
		if (sec > 0) {
			_ghostFuture = GeneralThreadPool.getInstance().pcSchedule(
					new L1PcGhostMonitor(this), sec * 1000);
		}
	}

	public void endGhost() {
		setGhost(false);
		setGhostCanTalk(true);
		L1Teleport.teleport(this, _ghostSaveLocX, _ghostSaveLocY,
				_ghostSaveMapId, _ghostSaveHeading, true);
	}

	private ScheduledFuture<?> _ghostFuture;

	private int _ghostSaveLocX = 0;
	private int _ghostSaveLocY = 0;
	private short _ghostSaveMapId = 0;
	private int _ghostSaveHeading = 0;

	private ScheduledFuture<?> _hellFuture;

	public void beginHell(final boolean isFirst) {
		/*
		 * if (getMapId() != 666) { final int locx = 32701; final int locy =
		 * 32777; final short mapid = 666; L1Teleport.teleport(this, locx, locy,
		 * mapid, 5, false); }
		 * 
		 * if (isFirst) { setHellTime(300); sendPackets(new S_BlueMessage(552,
		 * String.valueOf(get_PKcount()), String.valueOf(getHellTime() / 60)));
		 * } else { sendPackets(new S_BlueMessage(637,
		 * String.valueOf(getHellTime()))); } if (_hellFuture == null) {
		 * _hellFuture = GeneralThreadPool .getInstance()
		 * .pcScheduleAtFixedRate(new L1PcHellMonitor(this), 0L, 1000L); }
		 */
	}

	public void endHell() {
		if (_hellFuture != null) {
			_hellFuture.cancel(false);
			_hellFuture = null;
		}
		// 地狱脱出火田村扫还。
		final int[] loc = L1TownLocation
				.getGetBackLoc(L1TownLocation.TOWNID_ORCISH_FOREST);
		L1Teleport.teleport(this, loc[0], loc[1], (short) loc[2], 5, true);
		try {
			save();
		} catch (final Exception ignore) {
			// ignore
		}
	}

	@Override
	public void setPoisonEffect(final int effectId) {
		sendPackets(new S_Poison(getId(), effectId));

		if (!isGmInvis() && !isGhost() && !isInvisble()) {
			broadcastPacket(new S_Poison(getId(), effectId));
		}
	}

	@Override
	public void healHp(final int pt) {
		super.healHp(pt);

		sendPackets(new S_HPUpdate(this));
	}

	@Override
	public int getKarma() {
		return _karma.get();
	}

	@Override
	public void setKarma(final int i) {
		_karma.set(i);
	}

	public void addKarma(final int i) {
		synchronized (_karma) {
			_karma.add(i);
		}
	}

	public int getKarmaLevel() {
		return _karma.getLevel();
	}

	public int getKarmaPercent() {
		return _karma.getPercent();
	}

	private Timestamp _lastPk;

	/**
	 * 最终PK时间返。
	 * 
	 * @return _lastPk
	 * 
	 */
	public Timestamp getLastPk() {
		return _lastPk;
	}

	/**
	 * 最终PK时间设定。
	 * 
	 * @param time
	 *            最终PK时间（Timestamp型） 解除场合null代入
	 */
	public void setLastPk(final Timestamp time) {
		_lastPk = time;
	}

	/**
	 * 最终PK时间现在时刻设定。
	 */
	public void setLastPk() {
		_lastPk = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * 
	 * 
	 * @return 手配中、true
	 */
	public boolean isWanted() {
		if (_lastPk == null) {
			return false;
		} else if (System.currentTimeMillis() - _lastPk.getTime() > 24 * 3600 * 1000) {
			setLastPk(null);
			return false;
		}
		return true;
	}

	@Override
	public int getMagicLevel() {
		return getClassFeature().getMagicLevel(getLevel());
	}

	private int _weightReduction = 0;

	public int getWeightReduction() {
		return _weightReduction;
	}

	public void addWeightReduction(final int i) {
		_weightReduction += i;
	}

	private int _hasteItemEquipped = 0;

	public int getHasteItemEquipped() {
		return _hasteItemEquipped;
	}

	public void addHasteItemEquipped(final int i) {
		_hasteItemEquipped += i;
	}

	public void removeHasteSkillEffect() {
		if (hasSkillEffect(L1SkillId.SLOW)) {
			removeSkillEffect(L1SkillId.SLOW);
		}
		if (hasSkillEffect(L1SkillId.MASS_SLOW)) {
			removeSkillEffect(L1SkillId.MASS_SLOW);
		}
		if (hasSkillEffect(L1SkillId.ENTANGLE)) {
			removeSkillEffect(L1SkillId.ENTANGLE);
		}
		if (hasSkillEffect(L1SkillId.HASTE)) {
			removeSkillEffect(L1SkillId.HASTE);
		}
		if (hasSkillEffect(L1SkillId.GREATER_HASTE)) {
			removeSkillEffect(L1SkillId.GREATER_HASTE);
		}
		if (hasSkillEffect(L1SkillId.STATUS_HASTE)) {
			removeSkillEffect(L1SkillId.STATUS_HASTE);
		}
	}

	private int _damageReductionByArmor = 0; // 防具轻减

	public int getDamageReductionByArmor() {
		return _damageReductionByArmor;
	}

	public void addDamageReductionByArmor(final int i) {
		_damageReductionByArmor += i;
	}

	private int _damageReduction = 0;

	public int getDamageReduction() {
		return _damageReduction;
	}

	public void addDamageReduction(final int i, final int r) {
		_damageReduction += i;
		_damageReductionrandom += r;
	}

	private int _damageReductionrandom = 0;

	public int getDamageReductionRandom() {
		return _damageReductionrandom;
	}

	private int _damageUpByHelm = 0;

	public int getDamageUpByHelm() {
		return _damageUpByHelm;
	}

	public void addDamageUpAndRandomByHelm(final int i, final int r) {
		_damageUpByHelm += i;
		_damageUpRandomByHelm += r;
	}

	private int _damageUpRandomByHelm = 0;

	public int getDamageUpRandomByHelm() {
		return _damageUpRandomByHelm;
	}

	private int _damageReductionByRing = 0;

	public int getDamageReductionByRing() {
		return _damageReductionByRing;
	}

	public void addDamageReductionByRing(final int i) {
		_damageReductionByRing += i;
	}

	private int _bowHitRate = 0; // 防具弓命中率

	public int getBowHitRate() {
		return _bowHitRate;
	}

	public void addBowHitRate(final int i) {
		_bowHitRate += i;
	}

	private boolean _gresValid; // G-RES有效

	private void setGresValid(final boolean valid) {
		_gresValid = valid;
	}

	public boolean isGresValid() {
		return _gresValid;
	}

	private long _fishingTime = 0;

	public long getFishingTime() {
		return _fishingTime;
	}

	public void setFishingTime(final long i) {
		_fishingTime = i;
	}

	private boolean _isFishing = false;

	public boolean isFishing() {
		return _isFishing;
	}

	public void setFishing(final boolean flag) {
		_isFishing = flag;
	}

	private boolean _isFishingReady = false;

	public boolean isFishingReady() {
		return _isFishingReady;
	}

	public void setFishingReady(final boolean flag) {
		_isFishingReady = flag;
	}

	private int _cookingId = 0;

	public int getCookingId() {
		return _cookingId;
	}

	public void setCookingId(final int i) {
		_cookingId = i;
	}

	private int _dessertId = 0;

	public int getDessertId() {
		return _dessertId;
	}

	public void setDessertId(final int i) {
		_dessertId = i;
	}

	/**
	 * LV命中设定 LV变动场合呼出再计算
	 * 
	 * @return
	 */
	public void resetBaseDmgup() {
		int newBaseDmgup = 0;
		if (isKnight()) { // 
			newBaseDmgup = getLevel() / 10;
		} else if (isElf()) { // 
			newBaseDmgup = getLevel() / 10;
		} else if (isDarkelf()) { // 
			newBaseDmgup = getLevel() / 10;
		}
		addDmgup(newBaseDmgup - _baseDmgup);
		_baseDmgup = newBaseDmgup;
	}

	/**
	 * LV命中设定 LV变动场合呼出再计算
	 * 
	 * @return
	 */
	public void resetBaseHitup() {
		int newBaseHitup = 0;
		int newBaseBowHitup = 0;
		if (isCrown()) { // 
			newBaseHitup = getLevel() / 5;
			newBaseBowHitup = getLevel() / 5;
		} else if (isKnight()) { // 
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;
		} else if (isElf()) { // 
			newBaseHitup = getLevel() / 5;
			newBaseBowHitup = getLevel() / 5;
		} else if (isDarkelf()) { // 
			newBaseHitup = getLevel() / 3;
			newBaseBowHitup = getLevel() / 3;
		} else if (this.isDragonKnight()) { // ドラゴンナイト
			newBaseHitup = this.getLevel() / 3;
			newBaseBowHitup = this.getLevel() / 3;

		} else if (this.isIllusionist()) { // イリュージョニスト
			newBaseHitup = this.getLevel() / 5;
			newBaseBowHitup = this.getLevel() / 5;
		}
		addHitup(newBaseHitup - _baseHitup);
		addBowHitup(newBaseBowHitup - _baseBowHitup);
		_baseHitup = newBaseHitup;
		_baseBowHitup = newBaseBowHitup;
	}

	/**
	 * AC再计算设定 初期设定时、LVUP,LVDown时呼出
	 */
	public void resetBaseAc() {
		final int newAc = CalcStat.calcAc(getLevel(), getBaseDex());
		addAc(newAc - _baseAc);
		_baseAc = newAc;
	}

	/**
	 * 素MR再计算设定 初期设定时、使用时LVUP,LVDown时呼出
	 */
	public void resetBaseMr() {
		int newMr = 0;
		if (isCrown()) { // 
			newMr = 10;
		} else if (isElf()) { // 
			newMr = 25;
		} else if (isWizard()) { // 
			newMr = 15;
		} else if (isDarkelf()) { // 
			newMr = 10;
		}
		newMr += CalcStat.calcStatMr(getWis()); // WIS分MR
		newMr += getLevel() / 2; // LV半分追加
		addMr(newMr - _baseMr);
		_baseMr = newMr;
	}

	/**
	 * EXP现在Lv再计算设定 初期设定时、死亡时LVUP时呼出
	 */
	private void resetLevel() {
		setLevel(ExpTable.getLevelByExp(_exp));

		if (_hpRegen != null) {
			_hpRegen.updateLevel();
		}
	}

	public void refresh() {
		resetLevel();
		resetBaseHitup();
		resetBaseDmgup();
		resetBaseMr();
		resetBaseAc();
		this.resetOriginalStrWeightReduction();
		this.resetOriginalConWeightReduction();
	}

	private int _originalStrWeightReduction = 0;

	public int getOriginalStrWeightReduction() {
		return this._originalStrWeightReduction;
	}

	private int _originalConWeightReduction = 0; // ● オリジナルCON 重量轻减

	public int getOriginalConWeightReduction() {
		return this._originalConWeightReduction;
	}

	private void resetOriginalStrWeightReduction() {
		final int originalStr = this.getOriginalStr();
		if (this.isCrown()) {
			switch (originalStr) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
				this._originalStrWeightReduction = 0;
				break;

			case 14:
			case 15:
			case 16:
				this._originalStrWeightReduction = 1;
				break;

			case 17:
			case 18:
			case 19:
				this._originalStrWeightReduction = 2;
				break;

			default:// 20 UP
				this._originalStrWeightReduction = 3;
				break;
			}

		} else if (this.isKnight()) {
			this._originalStrWeightReduction = 0;
		} else if (this.isElf()) {
			switch (originalStr) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				this._originalStrWeightReduction = 0;
				break;

			default:// 16 UP
				this._originalStrWeightReduction = 2;
				break;
			}

		} else if (this.isDarkelf()) {
			switch (originalStr) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
				this._originalStrWeightReduction = 0;
				break;

			case 13:
			case 14:
			case 15:
				this._originalStrWeightReduction = 2;
				break;

			default:// 16 UP
				this._originalStrWeightReduction = 3;
				break;
			}

		} else if (this.isWizard()) {
			switch (originalStr) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
				this._originalStrWeightReduction = 0;
				break;

			default:// 9 UP
				this._originalStrWeightReduction = 1;
				break;
			}

		} else if (this.isDragonKnight()) {
			switch (originalStr) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
				this._originalStrWeightReduction = 0;
				break;

			default:// 16 UP
				this._originalStrWeightReduction = 1;
				break;
			}

		} else if (this.isIllusionist()) {
			switch (originalStr) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
				this._originalStrWeightReduction = 0;
				break;

			default:// 18 UP
				this._originalStrWeightReduction = 1;
				break;
			}
		}
	}

	public void resetOriginalConWeightReduction() {
		final int originalCon = this.getOriginalCon();
		if (this.isCrown()) {
			switch (originalCon) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				this._originalConWeightReduction = 0;
				break;

			default:// 11 UP
				this._originalConWeightReduction = 1;
				break;
			}

		} else if (this.isKnight()) {
			switch (originalCon) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				this._originalConWeightReduction = 0;
				break;

			default:// 15 UP
				this._originalConWeightReduction = 1;
				break;
			}

		} else if (this.isElf()) {
			switch (originalCon) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				this._originalConWeightReduction = 0;
				break;

			default:// 15 UP
				this._originalConWeightReduction = 2;
				break;
			}

		} else if (this.isDarkelf()) {
			switch (originalCon) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				this._originalConWeightReduction = 0;
				break;

			default:// 9 UP
				this._originalConWeightReduction = 1;
				break;
			}

		} else if (this.isWizard()) {
			switch (originalCon) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
				this._originalConWeightReduction = 0;
				break;

			case 13:
			case 14:
				this._originalConWeightReduction = 1;
				break;

			default:// 15 UP
				this._originalConWeightReduction = 2;
				break;
			}

		} else if (this.isDragonKnight()) {
			this._originalConWeightReduction = 0;

		} else if (this.isIllusionist()) {
			switch (originalCon) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
				this._originalConWeightReduction = 0;
				break;

			case 17:
				this._originalConWeightReduction = 1;
				break;

			default:// 18 UP
				this._originalConWeightReduction = 2;
				break;
			}
		}
	}

	private final ArrayList<String> _excludeList = new ArrayList<String>();

	public void addExclude(final String name) {
		_excludeList.add(name);
	}

	/**
	 * 指定名前遮削除
	 * 
	 * @param name
	 *            象名
	 * @return 际削除、遮上名。 指定名前见场合null返。
	 */
	public String removeExclude(final String name) {
		for (final String each : _excludeList) {
			if (each.equalsIgnoreCase(name)) {
				_excludeList.remove(each);
				return each;
			}
		}
		return null;
	}

	/**
	 * 指定名前遮场合true返
	 */
	public boolean excludes(final String name) {
		for (final String each : _excludeList) {
			if (each.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 遮上限16名达返
	 */
	public boolean isExcludeListFull() {
		return (_excludeList.size() >= 16) ? true : false;
	}

	// -- 加速器知机能 --
	private final AcceleratorChecker _acceleratorChecker = new AcceleratorChecker(
			this);

	public AcceleratorChecker getAcceleratorChecker() {
		return _acceleratorChecker;
	}

	// 加速检测器
	private AcceleratorChecker _speed = null;

	/**
	 * 加速检测器
	 * 
	 * @return
	 */
	public AcceleratorChecker speed_Attack() {
		return _speed;
	}

	/** 魔法传送临时地图编号(记忆坐标). */
	private short tempBookmarkMapID;
	/** 魔法传送临时X坐标(记忆坐标). */
	private int tempBookmarkLocX;
	/** 魔法传送临时Y坐标(记忆坐标). */
	private int tempBookmarkLocY;

	/**
	 * 取得魔法传送临时地图编号(记忆坐标).
	 * 
	 * @return tempBookmarkMapID
	 */
	public short getTempBookmarkMapID() {
		return tempBookmarkMapID;
	}

	/**
	 * 设置魔法传送临时地图编号(记忆坐标).
	 * 
	 * @param tempBookmarkMapID
	 *            - tempBookmarkMapID
	 */
	public void setTempBookmarkMapID(short tempBookmarkMapID) {
		this.tempBookmarkMapID = tempBookmarkMapID;
	}

	/**
	 * 取得魔法传送临时X坐标(记忆坐标).
	 * 
	 * @return tempBookmarkLocX
	 */
	public int getTempBookmarkLocX() {
		return tempBookmarkLocX;
	}

	/**
	 * 设置魔法传送临时X坐标(记忆坐标).
	 * 
	 * @param tempBookmarkLocX
	 *            - tempBookmarkLocX
	 */
	public void setTempBookmarkLocX(int tempBookmarkLocX) {
		this.tempBookmarkLocX = tempBookmarkLocX;
	}

	/**
	 * 取得魔法传送临时Y坐标(记忆坐标).
	 * 
	 * @return tempBookmarkLocY
	 */
	public int getTempBookmarkLocY() {
		return tempBookmarkLocY;
	}

	/**
	 * 设置魔法传送临时Y坐标(记忆坐标).
	 * 
	 * @param tempBookmarkLocY
	 *            - tempBookmarkLocY
	 */
	public void setTempBookmarkLocY(int tempBookmarkLocY) {
		this.tempBookmarkLocY = tempBookmarkLocY;
	}

	private boolean _isPring;

	public void setPring(final boolean flg) {
		_isPring = flg;
	}

	public boolean isPring() {
		return _isPring;
	}

	private int _warid = 0;

	public void setWarid(final int id) {
		_warid = 0;
	}

	public int getWarid() {
		return _warid;
	}

	private int _membera = -1;

	public void setMembera(final int a) {
		_membera = a;
	}

	public int getMembera() {
		return _membera;
	}

	private int _memberb = -1;

	public void setMemberb(final int b) {
		_memberb = b;
	}

	public int getMemberb() {
		return _memberb;
	}

	private int _AItime;

	public void setAItime(final int time) {
		_AItime = time;
	}

	public int getAItime() {
		return _AItime;
	}

	private int _waittime;

	public void setWaittime(final int time) {
		_waittime = time;
	}

	public int getWaittime() {
		return _waittime;
	}

	private int _sum = -1;

	public void setSum(final int sum) {
		_sum = sum;
	}

	public int getSum() {
		return _sum;
	}

	private long _tempexp;

	public void setTempMaxExp(final long exp) {
		_tempexp = exp;
	}

	public long getTempMaxExp() {
		return _tempexp;
	}

	private int _tempLevel = 1;// 人物重置等级暂存(最低)

	/**
	 * 人物重置等级暂存(最低)
	 * 
	 * @return
	 */
	public int getTempLevel() {
		return this._tempLevel;
	}

	/**
	 * 人物重置等级暂存(最低)
	 * 
	 * @param i
	 */
	public void setTempLevel(final int i) {
		this._tempLevel = i;
	}

	private boolean _isInCharReset = false;// 执行人物重设状态

	/**
	 * 传回执行人物重设状态
	 * 
	 * @return
	 */
	public boolean isInCharReset() {
		return this._isInCharReset;
	}

	/**
	 * 设置执行人物重设状态
	 * 
	 * @param flag
	 */
	public void setInCharReset(final boolean flag) {
		this._isInCharReset = flag;
	}

	private int _tempMaxLevel = 1;// 人物重置等级暂存(最高)

	/**
	 * 人物重置等级暂存(最高)
	 * 
	 * @return
	 */
	public int getTempMaxLevel() {
		return this._tempMaxLevel;
	}

	/**
	 * 人物重置等级暂存(最高)
	 * 
	 * @param i
	 */
	public void setTempMaxLevel(final int i) {
		this._tempMaxLevel = i;
	}

	private byte _chatCount = 0;// 对话检查次数

	private long _oldChatTimeInMillis = 0L;// 对话检查毫秒差

	/**
	 * 对话检查(洗画面)
	 */
	public void checkChatInterval() {
		final long nowChatTimeInMillis = System.currentTimeMillis();
		if (this._chatCount == 0) {
			this._chatCount++;
			this._oldChatTimeInMillis = nowChatTimeInMillis;
			return;
		}

		final long chatInterval = nowChatTimeInMillis
				- this._oldChatTimeInMillis;
		// 时间差异2秒以上
		if (chatInterval > 2000) {
			this._chatCount = 0;
			this._oldChatTimeInMillis = 0;

		} else {
			if (this._chatCount >= 3) {
				this.setSkillEffect(L1SkillId.STATUS_POISON_SILENCE, 120 * 1000);
				this.sendPackets(new S_PacketBox(S_PacketBox.ICON_CHATBAN, 120));
				// \f3因洗画面的关系，2分钟之内无法聊天。
				this.sendPackets(new S_ServerMessage(153));
				this._chatCount = 0;
				this._oldChatTimeInMillis = 0;
			}
			this._chatCount++;
		}
	}

	private byte _WhisperchatCount = 0;// 对话检查次数

	private long _WhisperoldChatTimeInMillis = 0L;// 对话检查毫秒差

	/**
	 * 对话检查(洗画面)
	 */
	public void checkWhisperChatInterval() {
		final long nowChatTimeInMillis = System.currentTimeMillis();
		if (this._WhisperchatCount == 0) {
			this._WhisperchatCount++;
			this._WhisperoldChatTimeInMillis = nowChatTimeInMillis;
			return;
		}

		final long chatInterval = nowChatTimeInMillis
				- this._WhisperoldChatTimeInMillis;
		// 时间差异2秒以上
		if (chatInterval > 2000) {
			this._WhisperchatCount = 0;
			this._WhisperoldChatTimeInMillis = 0;

		} else {
			if (this._WhisperchatCount >= 3) {
				this.setSkillEffect(L1SkillId.STATUS_POISON_SILENCE, 120 * 1000);
				this.sendPackets(new S_PacketBox(S_PacketBox.ICON_CHATBAN, 120));
				// \f3因洗画面的关系，2分钟之内无法聊天。
				this.sendPackets(new S_ServerMessage(153));
				this._WhisperchatCount = 0;
				this._WhisperoldChatTimeInMillis = 0;
			}
			this._WhisperchatCount++;
		}
	}

	private boolean _ispk;

	public void setPK(final boolean flg) {
		_ispk = flg;
	}

	public boolean isPK() {
		return _ispk;
	}

	private boolean _isShowWorldChat = true;// 全体聊天(收听)

	/**
	 * 全体聊天(收听)
	 * 
	 * @return flag true:接收 false:拒绝
	 */
	public boolean isShowWorldChat() {
		return this._isShowWorldChat;
	}

	/**
	 * 全体聊天(收听)
	 * 
	 * @param flag
	 *            flag true:接收 false:拒绝
	 */
	public void setShowWorldChat(final boolean flag) {
		this._isShowWorldChat = flag;
	}

	private boolean _isCanWhisper = true;// 全秘密语(收听)

	/**
	 * 全秘密语(收听)
	 * 
	 * @return flag true:接收 false:拒绝
	 */
	public boolean isCanWhisper() {
		return this._isCanWhisper;
	}

	/**
	 * 全秘密语(收听)
	 * 
	 * @param flag
	 *            flag true:接收 false:拒绝
	 */
	public void setCanWhisper(final boolean flag) {
		this._isCanWhisper = flag;
	}

	// TODO 属性重置处理

	/**
	 * 属性重置
	 * 
	 * @param key
	 *            模式<BR>
	 *            0 升级点数/万能药点数 可分配数量<BR>
	 * 
	 *            1 力量 (原始)<BR>
	 *            2 敏捷 (原始)<BR>
	 *            3 体质 (原始)<BR>
	 *            4 精神 (原始)<BR>
	 *            5 智力 (原始)<BR>
	 *            6 魅力 (原始)<BR>
	 * 
	 *            7 力量 +-<BR>
	 *            8 敏捷 +-<BR>
	 *            9 体质 +-<BR>
	 *            10 精神 +-<BR>
	 *            11 智力 +-<BR>
	 *            12 魅力 +-<BR>
	 * 
	 *            13 目前分配点数模式 0:升级点数 1:万能药点数<BR>
	 * @param value
	 *            增加数值总合
	 */
	public void add_levelList(final int key, final int value) {
		_uplevelList.put(key, value);
	}

	/**
	 * 属性重置清单
	 * 
	 * @return
	 */
	public Map<Integer, Integer> get_uplevelList() {
		return this._uplevelList;
	}

	/**
	 * 指定数值参数
	 * 
	 * @param key
	 * @return
	 */
	public Integer get_uplevelList(final int key) {
		return this._uplevelList.get(key);
	}

	/**
	 * 清空属性重置处理清单
	 */
	public void clear_uplevelList() {
		this._uplevelList.clear();
	}

	private int[] _is;

	/**
	 * 暂存人物原始素质改变
	 * 
	 * @param is
	 */
	public void set_newPcOriginal(final int[] is) {
		this._is = is;
	}

	/**
	 * 传回暂存人物原始素质改变
	 * 
	 * @return
	 */
	public int[] get_newPcOriginal() {
		return this._is;
	}

	private int _rname = 0;// 重設名稱

	/**
	 * 重設名稱
	 * 
	 * @param b
	 */
	public void rename(final int item) {
		_rname = item;
	}

	/**
	 * 重設名稱
	 * 
	 * @return
	 */
	public int is_rname() {
		return _rname;
	}

	private boolean _CanTradChat = true;

	public void setShowTradeChat(final boolean b) {
		_CanTradChat = b;
	}

	public boolean isCanTradeChat() {
		return this._CanTradChat;
	}

	// 赌狗
	public L1GamSpList getGamSplist() {
		return _gamSpList;
	}

	private int _clanRank; // ● クラン内のランク(血盟君主、守護騎士、一般、見習)

	public int getClanRank() {
		return _clanRank;
	}

	public void setClanRank(final int i) {
		_clanRank = i;
	}

	private int _pinksec = 0;

	public void setPinkSec(final int sec) {
		_pinksec = sec;
	}

	public int getPinkSec() {
		return _pinksec;
	}

	private Timestamp _deleteTime; // キャラクター削除までの时间

	public Timestamp getDeleteTime() {
		return this._deleteTime;
	}

	public void setDeleteTime(final Timestamp time) {
		this._deleteTime = time;
	}

	private int _old_lawful;

	/**
	 * 原始Lawful
	 * 
	 * @return
	 */
	public int getLawfulo() {
		return _old_lawful;
	}

	/**
	 * 更新Lawful
	 */
	public void onChangeLawful() {
		if (_old_lawful != getLawful()) {
			_old_lawful = getLawful();
			sendPackets(new S_Lawful(getId(), getLawful()));
		}
	}

	private int _deadsec = 600;

	public void setdeadsec(final int sec) {
		_deadsec = sec;
	}

	public int getdeadsec() {
		return _deadsec;
	}

	private double _itemexp = 1.0;

	public void setItemExp(final double exp) {
		_itemexp = exp;
	}

	public double getItemExp() {
		return _itemexp;
	}

	private boolean _checkfz = false;

	public void setCheckFZ(final boolean flg) {
		_checkfz = flg;
	}

	public boolean isCheckFZ() {
		return _checkfz;
	}

	private int _moncount = 0;

	public void setKillMonCount(final int i) {
		_moncount = i;
	}

	public int getKillMonCount() {
		return _moncount;
	}

	public void addKillMonCount(final int i) {
		_moncount += i;
		this.sendPackets(new S_OwnCharStatus(this));
	}

	private final L1ExcludingMailList _excludingMailList = new L1ExcludingMailList();

	public L1ExcludingMailList getExcludingMailList() {
		return _excludingMailList;
	}

	private boolean _checkds = false;

	public void setCheck(final boolean flg) {
		_checkds = flg;
	}

	public boolean isCheck() {
		return _checkds;
	}

	private String _bianshenString = "没写";

	public void setBianshen(final String bs) {
		_bianshenString = bs;
	}

	public String getBianshen() {
		return _bianshenString;
	}

	private boolean _showemblem;

	public void setShowEmblem(final boolean b) {
		_showemblem = b;
	}

	public boolean isShowEmblem() {
		return _showemblem;
	}

	private String _toukuiname = "";

	public void setTouKuiName(final String name) {
		_toukuiname = name;
	}

	public String getTouKuiName() {
		return _toukuiname;
	}

	private int _tuokui_objId = 0;

	public void set_tuokui_objId(final int eq_objId) {
		_tuokui_objId = eq_objId;
	}

	public int get_tuokui_objId() {
		return _tuokui_objId;
	}

	/*
	 * private final L1PCAction _action; public L1PCAction getAction() { return
	 * _action; }
	 */

	private int _ezpayCount = 0;

	public int getEzpayCount() {
		return _ezpayCount;
	}

	public void setEzpayCount(final int ezpayCount) {
		_ezpayCount = ezpayCount;
	}

	public void addEzpayCount(final int ezpayCount) {
		_ezpayCount += ezpayCount;
	}

	private boolean _healHPAI = false;
	private boolean _healAIProcess = false;

	public void startHealHPAI() {
		if (_healHPAI) {
			return;
		}
		if (_healAIProcess) {
			return;
		}
		_healHPAI = true;
		_healAIProcess = true;
		new L1PcHealAI(this).startAI();
	}

	public void setHealAI(final boolean healHPAI) {
		_healHPAI = healHPAI;
	}

	public boolean getHealHPAI() {
		return _healHPAI;
	}

	public boolean isHealAIProcess() {
		return _healAIProcess;
	}

	public void setHealAIProcess(final boolean AIProcess) {
		_healAIProcess = AIProcess;
	}

	private final List<Integer> _healHpPotionList = new ArrayList<Integer>();

	public void addHealHpPotion(final int itemId) {
		_healHpPotionList.add(itemId);
	}

	public void clearHealHpPotion() {
		_healHpPotionList.clear();
	}

	public List<Integer> getHealHpPotionList() {
		return _healHpPotionList;
	}

	private final int[] _weaponObjIdList = new int[18];

	public void setWeaponItemObjId(final int itemObjId, final int index) {
		_weaponObjIdList[index] = itemObjId;
	}

	public int getWeaponItemObjId(final int index) {
		return _weaponObjIdList[index];
	}

	public int[] getWeaponItemList() {
		return _weaponObjIdList;
	}

	private final int[] _selHealHpPotion = new int[4];

	public void setSelHealHpPotion(final int itemId, final int healHp,
			final int gfxid) {
		if (_selHealHpPotion[0] != itemId) {
			final L1Item item = ItemTable.getInstance().getTemplate(itemId);
			if (item != null) {
				_selHealHpPotion[0] = itemId;
				_selHealHpPotion[1] = healHp;
				_selHealHpPotion[2] = gfxid;
				_selHealHpPotion[3] = item.get_delaytime();
				if (getHealHPAI()) {
					sendPackets(new S_SystemMessage("自动喝药已切换至" + item.getName()));
				}
			}
		}
	}

	public int[] getSelHealHpPotion() {
		return _selHealHpPotion;
	}

	private int _healpersenthp = 20;

	public void setHealpersentHp(final int healpersenthp) {
		if (healpersenthp > 90) {
			_healpersenthp = 90;
		} else {
			_healpersenthp = healpersenthp;
		}
	}

	public int getHealpersentHP() {
		return _healpersenthp;
	}

	private int _Deathcount = 0;

	public int get_Deathcount() {
		return this._Deathcount;
	}

	public void add_Deathcount(final int detahcount) {
		this._Deathcount += detahcount;
	}

	public void set_Deathcount(final int detahcount) {
		this._Deathcount = detahcount;
	}

	private int _clanteleteId = 0;

	public void setClanTeletePcId(final int id) {
		_clanteleteId = id;
	}

	public int getClanTeletePcId() {
		return _clanteleteId;
	}

	private ArrayList<L1Spawn> _spawnBossList = new ArrayList<L1Spawn>();

	public void addSpawnBossItem(L1Spawn spawn) {
		_spawnBossList.add(spawn);
	}

	public void clearSpawnBossList() {
		_spawnBossList.clear();
	}

	public ArrayList<L1Spawn> getSpawnBossList() {
		return _spawnBossList;
	}

	private int _listpage = 0;

	public int getPage() {
		return _listpage;
	}

	public void addPage(final int page) {
		_listpage += page;
	}

	public void setPage(final int page) {
		_listpage = page;
	}

	private int _hitModifierByArmor = 0;

	public int getHitModifierByArmor() {
		return _hitModifierByArmor;
	}

	public void addHitModifierByArmor(int i) {
		_hitModifierByArmor += i;
	}

	private int _dmgModifierByArmor = 0;

	public int getDmgModifierByArmor() {
		return _dmgModifierByArmor;
	}

	public void addDmgModifierByArmor(int i) {
		_dmgModifierByArmor += i;
	}

	private int _bowHitModifierByArmor = 0;

	public int getBowHitModifierByArmor() {
		return _bowHitModifierByArmor;
	}

	public void addBowHitModifierByArmor(int i) {
		_bowHitModifierByArmor += i;
	}

	private int _bowDmgModifierByArmor = 0;

	public int getBowDmgModifierByArmor() {
		return _bowDmgModifierByArmor;
	}

	public void addBowDmgModifierByArmor(int i) {
		_bowDmgModifierByArmor += i;
	}

	private long _guajiAITime = 0;

	public long getGuaJiAITime() {
		return _guajiAITime;
	}

	public void setGuaJiAITime(final long nowtime) {
		_guajiAITime = nowtime;
	}

	private int _guajiAIattackcount = 0;

	public int getGuaJiAIAttackCount() {
		return _guajiAIattackcount;
	}

	public void setGuaJiAIAttackCount(final int count) {
		_guajiAIattackcount = count;
	}

	public void addGuaJiAIAttackCount(final int count) {
		_guajiAIattackcount += count;
	}

	private boolean _guaJiAI = true;

	public boolean getGuaJiAI() {
		return _guaJiAI;
	}

	public void setGuaJiAI(final boolean b) {
		_guaJiAI = b;
	}

	private int _dicezuobi = 0;

	public void setDiceZuoBi(final int dian) {
		_dicezuobi = dian;
	}

	public int getDiceZuoBi() {
		return _dicezuobi;
	}

	private int _oleLocx = 0;

	public void setOleLocX(final int oleLocx) {
		_oleLocx = oleLocx;
	}

	public int getOleLocX() {
		return _oleLocx;
	}

	private int _oleLocy = 0;

	public void setOleLocY(final int oleLocy) {
		_oleLocy = oleLocy;
	}

	public int getOleLocY() {
		return _oleLocy;
	}

	private boolean _bind = false;

	public boolean getBind() {
		return _bind;
	}

	public void setBind(final boolean bind) {
		_bind = bind;
	}

	/**
	 * 精灵饼干效果
	 * 
	 * @return
	 */
	public boolean isElfBrave() {
		return this.hasSkillEffect(L1SkillId.STATUS_ELFBRAVE);
	}

	/** 装备中的戒指1. */
	private int equipmentRing1ID;
	/** 装备中的戒指2. */
	private int equipmentRing2ID;
	/** 装备中的戒指3. */
	private int equipmentRing3ID;
	/** 装备中的戒指4. */
	private int equipmentRing4ID;

	/**
	 * 取得装备中的戒指1.
	 * 
	 * @return 该道具的OBJID
	 */
	public int getEquipmentRing1ID() {
		return equipmentRing1ID;
	}

	/**
	 * 设置装备中的戒指1.
	 * 
	 * @param i
	 *            - 该道具的OBJID
	 */
	public void setEquipmentRing1ID(int i) {
		equipmentRing1ID = i;
	}

	/**
	 * 取得装备中的戒指2.
	 * 
	 * @return 该道具的OBJID
	 */
	public int getEquipmentRing2ID() {
		return equipmentRing2ID;
	}

	/**
	 * 设置装备中的戒指2.
	 * 
	 * @param i
	 *            - 该道具的OBJID
	 */
	public void setEquipmentRing2ID(int i) {
		equipmentRing2ID = i;
	}

	/**
	 * 取得装备中的戒指3.
	 * 
	 * @return 该道具的OBJID
	 */
	public int getEquipmentRing3ID() {
		return equipmentRing3ID;
	}

	/**
	 * 设置装备中的戒指3.
	 * 
	 * @param i
	 *            - 该道具的OBJID
	 */
	public void setEquipmentRing3ID(int i) {
		equipmentRing3ID = i;
	}

	/**
	 * 取得装备中的戒指4.
	 * 
	 * @return 该道具的OBJID
	 */
	public int getEquipmentRing4ID() {
		return equipmentRing4ID;
	}

	/**
	 * 设置装备中的戒指4.
	 * 
	 * @param i
	 *            - 该道具的OBJID
	 */
	public void setEquipmentRing4ID(int i) {
		equipmentRing4ID = i;
	}

	/** 是否为进入游戏世界状态. */
	private boolean isLoginToServer;

	/**
	 * 是否为进入游戏世界状态.
	 * 
	 * @return 返回 true or false
	 */
	public boolean isLoginToServer() {
		return isLoginToServer;
	}

	/**
	 * 设置进入游戏世界状态.
	 * 
	 * @param flag
	 *            - true or false
	 */
	public void setLoginToServer(boolean flag) {
		isLoginToServer = flag;
	}

	// // 一下是挂机选项
	protected NpcMoveExecutor _pcMove = null;// XXX

	public void startAI() {
		if (this.isDead()) {
			return;
		}
		if (this.isGhost()) {
			return;
		}
		if (this.getCurrentHp() <= 0) {
			return;
		}
		if (this.isPrivateShop()) {
			return;
		}
		if (this.isParalyzed()) {
			return;
		}
		if (this.isAiRunning()) {
			return;
		}
		if (_pcMove == null) {
			_pcMove = new pcMove(this);
		}
		this.setActived(true);
		this.setAiRunning(true);
		final L1PcAI npcai = new L1PcAI(this);
		npcai.startAI();
	}

	public void clearMove() {
		if (_pcMove != null) {
			_pcMove.clear();
		}
	}

	private boolean _aiRunning = false;

	public void setAiRunning(final boolean b) {
		_aiRunning = b;
	}

	public boolean isAiRunning() {
		return this._aiRunning;
	}

	private boolean _isActived = false;

	public boolean isActived() {
		return _isActived;
	}

	public void setActived(final boolean b) {
		_isActived = b;
	}

	public void allTargetClear() {
//		_hateList.clear();
//		_AItarget = null;
//		setFirstAttack(false);
		 if (_pcMove != null) {
	            _pcMove.clear();
	        }
		 _AItarget = null;
	}

	private boolean _firstAttack = false;

	protected void setFirstAttack(final boolean firstAttack) {
		this._firstAttack = firstAttack;
	}

	protected boolean isFirstAttack() {
		return this._firstAttack;
	}

	private L1Character _AItarget = null;

	public final L1HateList _hateList = new L1HateList();// 目標清單

	public void addHateList(final L1Character cha, final int hate) {
		_hateList.add(cha, hate);
	}

	/**
	 * 有效目標檢查
	 */
	public void checkTarget() {
		 try {
	            if (_AItarget == null) {// 目标为空
	            	//targetClear();
	                return;
	            }
	            if (_AItarget.getMapId() != getMapId()) {// 目标地图不相等
	            	targetClear();
	                return;
	            }
	            if (_AItarget.getCurrentHp() <= 0) {// 目标HP小于等于0
	            	targetClear();
	                return;
	            }
	            if (_AItarget.isDead()) {// 目标死亡
	            	targetClear();
	                return;
	            }

	            final int distance = getLocation().getTileDistance(
	            		_AItarget.getLocation());
	            if (distance > 8) {
	            	targetClear();
	                return;
	            }

	        } catch (final Exception e) {
	            return;
	        }
	    }

	/**
	 * 清除单个目标
	 */
	public void targetClear() {
		if (_AItarget == null) {
			return;
		}
		_AItarget = null;
	}

//	public void tagertClear() {
//		if (_AItarget == null) {
//			return;
//		}
//		if (_hateList.containsKey(_AItarget)) {
//			_hateList.remove(_AItarget);
//			// this.searchTarget();
//			setFirstAttack(false);
//		}
//		_AItarget = null;
//	}

	private boolean _Pathfinding = false; // 尋路中.. hjx1000

	/**
	 * 目標為空掛機尋路中
	 * 
	 * @return
	 */
	public boolean isPathfinding() {
		return this._Pathfinding;
	}

	public void setPathfinding(final boolean fla) {
		this._Pathfinding = fla;
	}

	/**
	 * 現在目標
	 */
	public L1Character is_now_target() {
		return _AItarget;
	}

	/**
	 * 設置目前攻擊對象
	 * 
	 * @param cha
	 */
	public void setNowTarget(final L1Character cha) {
		this._AItarget = cha;
	}

	/**
	 * 搜寻目标
	 */
	public void searchTarget() {
	   	int hate = 8;
        final Collection<L1Object> allObj = L1World.getInstance()
                .getVisibleObjects(this, 8);
        for (final Iterator<L1Object> iter = allObj.iterator(); iter.hasNext();) {
            final L1Object obj = iter.next();
            if (!(obj instanceof L1MonsterInstance)) {
            	continue;
            }
            final L1MonsterInstance mob = (L1MonsterInstance) obj;
        	if (mob.isDead()) {
        		continue;
        	}
            if (mob.getCurrentHp() <= 0) {
                continue;
            }
            if (mob.getHiddenStatus() > 0) {
            	continue;                	
            }
            if (mob.getAtkspeed() == 0) {
            	continue;
            }
            if (mob.hasSkillEffect(this.getId() + 100000)
            		&& !this.isAttackPosition(mob.getX(), mob.getY(), 1)) {
            	continue;
            }
            if (mob != null) {
                final int Distance = this.getTileLineDistance(mob);	
                if (hate > Distance) {
                	_AItarget = mob;
                    hate = Distance;
                }
                if (hate < 2) {
                	break;
                }
            }
        }
		//if (isActived()) { // 如果目标等于空

			// 等待处理，，瞬移。。等等设置
			//if (this.getMap().isTeleportable()) { // 地图可瞬移身上有白瞬卷
				// hjx1000
        if (_AItarget == null) { //如果目标等于空
				if (this.getInventory().consumeItem(40308, 50)) {
					L1Teleport.randomTeleport(this, true);
				this.sendPackets(new S_SystemMessage(
						"无怪瞬移.并扣除50金币!"));
			} else {
				// this.setskillAuto_gj(false);
				this.sendPackets(new S_SystemMessage(
						"金币不足50无怪瞬飞.本次挂机停止并且回城等待..."));
				this.setActived(false);
				final L1Location newLocation = new L1Location(33437, 32812, 4)
						.randomLocation(10, false);
				L1Teleport.teleport(this, newLocation.getX(),
						newLocation.getY(), (short) newLocation.getMapId(), 5,
						true);
				}
			}
		}
	

	private int _randomMoveDirection = 0;

	public int getrandomMoveDirection() {
		return _randomMoveDirection;
	}

	public void setrandomMoveDirection(int randomMoveDirection) {
		this._randomMoveDirection = randomMoveDirection;
	}

	/**
	 * 沒有目標的處理 (傳回本次AI是否執行完成)<BR>
	 * 具有主人 跟隨主人移動
	 * 
	 * @return true:本次AI執行完成 <BR>
	 *         false:本次AI執行未完成
	 */
	public void noTarget() {
    	if (!_Pathfinding) {
        	_Pathfinding = true; //设置寻路中 
    	}
    	if (_randomMoveDirection > 7) {
    		_randomMoveDirection = 0;
    	}
        //System.out.println("_randomMoveDirection=:" + _randomMoveDirection);
        if (_pcMove != null) {
            if (getrandomMoveDirection() < 8) {
                int dir = _pcMove
                        .checkObject(_randomMoveDirection);
                dir = _pcMove.openDoor(dir);

                if (dir != -1) {
                    _pcMove.setDirectionMove(dir);
                } else {
                	_randomMoveDirection = _random.nextInt(8);
                }
            }
        }
    }

	private L1Location _startGuaJiLoc = null;

	private L1Location getStartLoc() {
		return _startGuaJiLoc;
	}

	public void setStartGuaJiLoc(final L1Location loc) {
		_startGuaJiLoc = loc;
	}

	/**
	 * 具有目標的處理 (攻擊的判斷)
	 */
	public void onTarget() {
		try {
			final L1Character target = _AItarget;

			if (target == null) {
				return;
			}
			// if (target )
			attack(target);
			// System.out.println("對目標進行攻擊" + target.getName());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

//	private void attack(L1Character target) {
//		int attack_Range = 1;
//		if (this.getWeapon() != null) {
//			attack_Range = this.getWeapon().getItem().getRange();
//		}
//		if (attack_Range < 0) {
//			attack_Range = 13;
//		}
//		if (isAttackPosition(target.getX(), target.getY(), attack_Range)) {// 已經到達可以攻擊的距離
//			setHeading(targetDirection(target.getX(), target.getY()));
//			attackTarget(target);
//		} else { // 攻擊不可能位置
//			int dir = _pcMove.moveDirection(target.getX(), target.getY());
//			if (dir == -1) {
//				if (!target.hasSkillEffect(this.getId() + 100000)) {
//					target.setSkillEffect(this.getId() + 100000, 30000);
//				}
//				tagertClear();
//				if (!_hateList.isEmpty()) {
//					_AItarget = _hateList.getMaxHateCharacter();
//					checkTarget();
//				}
//			} else {
//				_pcMove.setDirectionMove(dir);
//				setSleepTime(calcSleepTime(2));
//			}
//			// System.out.println("對目標進行攻擊" + dir);
//		}
//	}
	
	 private void attack(L1Character target) {
	        // 攻击可能位置
	    	int attack_Range = 1;
	    	if (this.getWeapon() != null) {
	    		attack_Range = this.getWeapon().getItem().getRange();
	    	}
	    	if (attack_Range < 0) {
	    		attack_Range = 15;
	    	}
	        if (isAttackPosition(target.getX(), target.getY(), attack_Range)) {// 已经到达可以攻击的距离
	            setHeading(targetDirection(target.getX(), target.getY()));
	            attackTarget(target);
	            this._Attack_or_walk = true;
	            // XXX
	            if (_pcMove != null) {
	                _pcMove.clear();
	            }

	        } else { // 攻击不可能位置
//	                final int distance = getLocation().getTileDistance(
//	                        target.getLocation());
	                if (_pcMove != null) {
	                    final int dir = _pcMove.moveDirection(target.getX(),
	                            target.getY());
	                    if (dir == -1) {
	                    	_AItarget.setSkillEffect(this.getId() + 100000, 20000);//给予20秒状态
	                    	targetClear();

	                    } else {
	                        _pcMove.setDirectionMove(dir);
//	                        setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
	                        this._Attack_or_walk = false;
	                        
	                    }
	                }
	        }
	    }
	 
	    private boolean _Attack_or_walk = false;//攻击或走路 
	    /**
	     * 攻击或者走路
	     * true = 攻击
	     * false = 走路
	     */
	    public boolean Attack_or_walk() {
	    	return this._Attack_or_walk;
	    }
	  
	//
	private int _sleep_time = 1000;

	public void setSleepTime(final int sleep_time) {
		_sleep_time = sleep_time;
	}

	public int getSleepTime() {
		return _sleep_time;
	}

	//
	/**
	 * 對目標進行攻擊
	 * 
	 * @param target
	 */
	public void attackTarget(final L1Character target) {
		// System.out.println("對目標進行攻擊");

		if (this.getInventory().getWeight240() >= 197) { // 重量過重
			// 110 \f1當負重過重的時候，無法戰鬥。
			this.sendPackets(new S_ServerMessage(110));
			// _log.error("要求角色攻擊:重量過重");
			return;
		}

		if (hasSkillEffect(L1SkillId.STATUS_CURSE_PARALYZED)) {
			return;
		}
		if (hasSkillEffect(L1SkillId.STATUS_POISON_PARALYZED)) {
			return;
		}
		if (hasSkillEffect(L1SkillId.STATUS_FREEZE)) {
			return;
		}
		if (target instanceof L1PcInstance) {
			final L1PcInstance player = (L1PcInstance) target;
			if (player.isTeleport()) { // テレポート處理中
				return;
			}
			if (!player.isPinkName()) {
				this.allTargetClear();
				return;
			}

		} else if (target instanceof L1PetInstance) {
			final L1PetInstance pet = (L1PetInstance) target;
			final L1Character cha = pet.getMaster();
			if (cha instanceof L1PcInstance) {
				final L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) { // テレポート處理中
					return;
				}
			}

		} else if (target instanceof L1SummonInstance) {
			final L1SummonInstance summon = (L1SummonInstance) target;
			final L1Character cha = summon.getMaster();
			if (cha instanceof L1PcInstance) {
				final L1PcInstance player = (L1PcInstance) cha;
				if (player.isTeleport()) { // テレポート處理中
					return;
				}
			}
		}

		if (target instanceof L1NpcInstance) {
			final L1NpcInstance npc = (L1NpcInstance) target;
			if (npc.getHiddenStatus() != 0) { // 地中に潛っているか、飛んでいる
				this.allTargetClear();
				return;
			}
		}
		
		 target.onAction(this);
		 
		 
		if (isGuaJiSkill()) {
			_old_skill_time = System.currentTimeMillis();
			final L1SkillUse skilluse = new L1SkillUse();
			skilluse.handleCommands(this, _selGuaJiSkillId, target.getId(),
					target.getX(), target.getY(), null, 0,
					L1SkillUse.TYPE_NORMAL);
			setSleepTime(calcSleepTime(3));
		} else {
			target.onAction(this);
			setSleepTime(calcSleepTime(1));
		}
	}

	private long _old_skill_time = 0;

	private boolean isGuaJiSkill() {
		if (_selGuaJiSkillId <= 0) {
			return false;
		}
		int time = 10000;
		if (_selGuaJiSkillId == 132) {
			if (this.getWeapon() == null) {
				return false;
			}
			if (this.getWeapon().getItem().getType1() != 20) {
				return false;
			}
			time = 3000;
		} else if (_selGuaJiSkillId == 38 || _selGuaJiSkillId == 46) {
			time = 2000;
		}
		final long nowskilltime = System.currentTimeMillis();
		if (nowskilltime - _old_skill_time <= time) {
			return false;
		}
		if (this.getCurrentHp() <= _selGuaJiSkillHP) {
			return false;
		}
		if (this.getCurrentMp() < _selGuaJiSkillMP) {
			return false;
		}
		return true;
	}

	private int calcSleepTime(final int type) {
		int interval = 0;
		switch (type) {
		case 1:
			interval = SprTable.get().getAttackSpeed(getTempCharGfx(),
					getCurrentWeapon() + 1);
			interval *= 1.05;
			break;
		case 2:
			interval = SprTable.get().getMoveSpeed(getTempCharGfx(),
					getCurrentWeapon());
			break;
		case 3:
			interval = SprTable.get().getDirSpellSpeed(getTempCharGfx());
			interval *= 1.05;
			break;
		default:
			return 0;
		}
		final int time_steep = intervalR(type, interval);
		return time_steep < 100 ? 100 : time_steep;
	}

	private int intervalR(final int type, int interval) {
		try {
			if (isHaste()) {
				interval *= 0.755;// 0.755
			}

			if (type == 2 && isFastMovable()) {
				interval *= 0.755;// 0.665
			}

			if (isBrave()) {
				interval *= 0.755;// 0.755
			}

			if (isElfBrave()) {
				interval *= 0.855;// 0.855
			}

			if (type == 1 && isElfBrave()) {
				interval *= 0.9;// 0.9
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return interval;
	}

	//
	private int _pcAILoop = 0;

	public int getPcAILoop() {
		return _pcAILoop;
	}

	public void setPcAILoop(final int loop) {
		_pcAILoop = loop;
	}

	//
	private int _selGuaJiSkillHP = 0;
	private int _selGuaJiSkillMP = 0;

	private int _selGuaJiSkillId = 0;

	public int getSelGuaJiSkillId() {
		return _selGuaJiSkillId;
	}

	public void setSelGuaJiSkillId(final int selGuaJiSkillId) {
		if (selGuaJiSkillId == 0) {
			_selGuaJiSkillHP = 0;
			_selGuaJiSkillMP = 0;
			_selGuaJiSkillId = 0;
			return;
		}
		final L1Skills skill = SkillsTable.getInstance().getTemplate(
				selGuaJiSkillId);
		if (skill != null) {
			if (CharSkillReading.get()
					.spellCheck(this.getId(), selGuaJiSkillId)) {
				_selGuaJiSkillHP = skill.getHpConsume();
				_selGuaJiSkillMP = skill.getMpConsume();
				_selGuaJiSkillId = selGuaJiSkillId;
			} else {
				_selGuaJiSkillHP = 0;
				_selGuaJiSkillMP = 0;
				_selGuaJiSkillId = 0;
			}
		} else {
			_selGuaJiSkillHP = 0;
			_selGuaJiSkillMP = 0;
			_selGuaJiSkillId = 0;
		}
	}

	private int _selGuaJiRange = 0;

	public int getSelGuaJiRange() {
		return _selGuaJiRange;
	}

	public void setSelGuaJiRange(final int selGuaJiRange) {
		_selGuaJiRange = selGuaJiRange;
	}

	public void addSelGuaJiRange(final int selGuaJiRange) {
		_selGuaJiRange += selGuaJiRange;
		if (_selGuaJiRange < 0) {
			_selGuaJiRange = 0;
		}
	}

	@Override
	public int getSp() {
		return super.getSp() + getSpReductionByClan(this);
	}

	@Override
	public int getDmgup() {
		return super.getDmgup() + getDmgReductionByClan(this);
	}

	@Override
	public int getBowDmgup() {
		return super.getBowDmgup() + getDmgReductionByClan(this);
	}

	/**
	 * 血盟技能伤害增加
	 * 
	 * @return
	 */
	private int getDmgReductionByClan(final L1PcInstance pc) {
		int dmg = 0;
		try {
			if (pc == null) {
				return 0;
			}
			final L1Clan clan = pc.getClan();
			if (pc.getClanid() == 0 || clan == null) {
				return 0;
			}
			// 具有血盟技能
			if (clan.isClanskill()) {
				if (clan.getSkillLevel() == 1) {
					dmg += 1;
				} else if (clan.getSkillLevel() == 2) {
					dmg += 2;
				} else if (clan.getSkillLevel() == 3) {
					dmg += 3;
				} else if (clan.getSkillLevel() == 4) {
					dmg += 4;
				} else if (clan.getSkillLevel() == 5) {
					dmg += 5;
				}
			}

		} catch (final Exception e) {
			return 0;
		}
		return dmg;
	}

	/**
	 * 血盟技能sp增加
	 * 
	 * @return
	 */
	private int getSpReductionByClan(final L1PcInstance pc) {
		int sp = 0;
		try {
			if (pc == null) {
				return 0;
			}
			if (pc.getClanid() == 0) {
				return 0;
			}
			final L1Clan clan = pc.getClan();
			if (clan == null) {
				return 0;
			}
			// 具有血盟技能
			if (clan.isClanskill()) {
				if (clan.getSkillLevel() == 1) {
					sp += 1;
				} else if (clan.getSkillLevel() == 2) {
					sp += 2;
				}
			}
		} catch (final Exception e) {
			return 0;
		}
		return sp;
	}

	private long _adenaTradeCount = 0;

	/**
	 * 金币交易 记录输入的金币数量
	 * 
	 * @param adenaTradeCount
	 */
	public void setAdenaTradeCount(final long adenaTradeCount) {
		_adenaTradeCount = adenaTradeCount;
	}

	/**
	 * 金币交易 获取输入的金币数量
	 * 
	 * @return
	 */
	public long getAdenaTradeCount() {
		return _adenaTradeCount;
	}

	// 金币交易 记录输入的元宝数量
	private long _adenaTradeAmount = 0;

	/**
	 * 金币交易 记录输入的元宝数量
	 * 
	 * @param adenaTradeAmount
	 */
	public void setAdenaTradeAmount(final long adenaTradeAmount) {
		_adenaTradeAmount = adenaTradeAmount;
	}

	/**
	 * 金币交易 获取输入的元宝数量
	 * 
	 * @return
	 */
	public long getAdenaTradeAmount() {
		return _adenaTradeAmount;
	}

	private List<Integer> _adenaTradeIndexList = new CopyOnWriteArrayList<Integer>();

	public void addAdenaTradeIndex(final int id) {
		_adenaTradeIndexList.add(id);
	}

	public void clearAdenaTradeIndexList() {
		_adenaTradeIndexList.clear();
	}

	public List<Integer> getAdenaTradeIndexList() {
		return _adenaTradeIndexList;
	}

	private List<L1CharacterAdenaTrade> _adenaTradeList = new CopyOnWriteArrayList<L1CharacterAdenaTrade>();

	public void addAdenaTradeItem(final L1CharacterAdenaTrade adenaTrade) {
		_adenaTradeList.add(adenaTrade);
	}

	public void clearAdenaTradeList() {
		_adenaTradeList.clear();
	}

	public List<L1CharacterAdenaTrade> getAdenaTradeList() {
		return _adenaTradeList;
	}

	private int _adenaTradeId = 0;

	/**
	 * 金币交易 记录选择的流水号
	 * 
	 * @param adenaTradeId
	 */
	public void setAdenaTradeId(final int adenaTradeId) {
		_adenaTradeId = adenaTradeId;
	}

	/**
	 * 金币交易 获取选择的流水号
	 * 
	 * @return
	 */
	public int getAdenaTradeId() {
		return _adenaTradeId;
	}

	private final List<L1FindShopSell> _findsellList = new ArrayList<L1FindShopSell>();

	public List<L1FindShopSell> getFindSellList() {
		return _findsellList;
	}

	public int getFindSellListSize() {
		return _findsellList.size();
	}

	public void clearFindSellList() {
		_findsellList.clear();
	}

	private boolean _showHealMessage = true;

	public boolean IsShowHealMessage() {
		return _showHealMessage;
	}

	public void setShowHealMessage(final boolean showhealmessage) {
		_showHealMessage = showhealmessage;
	}

	private L1BiaoCheInstance _biaCheInstance = null;

	public void setBiaoChe(L1BiaoCheInstance biaocheInstance) {
		_biaCheInstance = biaocheInstance;
	}

	public L1BiaoCheInstance getBiaoChe() {
		return _biaCheInstance;
	}

	private int _jiequbiaochecount = 0;

	public int getJieQuBiaoCheCount() {

		return _jiequbiaochecount;
	}

	public void addJieQuBiaoCheCount(int i) {
		_jiequbiaochecount += i;
	}

	public void setJieQuBiaoCheCount(int i) {
		_jiequbiaochecount = i;
	}

	private short _tempBiaoCheMapId = 0;

	public void setTempBiaoCheMapId(short mapId) {
		_tempBiaoCheMapId = mapId;
	}

	public short getTempBiaoCheMapId() {
		return _tempBiaoCheMapId;
	}

	private int _tempBiaoCheLocX = 0;

	public void setTempBiaoCheLocX(int x) {
		_tempBiaoCheLocX = x;
	}

	public int getTempBiaoCheLocX() {
		return _tempBiaoCheLocX;
	}

	private int _tempBiaoCheLocY = 0;

	public void setTempBiaoCheLocY(int y) {
		_tempBiaoCheLocY = y;
	}

	public int getTempBiaoCheLocY() {
		return _tempBiaoCheLocY;
	}

	private int _dollfailcount = 0;

	public int getDollFailCount() {
		return _dollfailcount;
	}

	public void setDollFailCount(final int dollfailcount) {
		_dollfailcount = dollfailcount;
	}

	public void addDollFailCount(final int dollfailcount) {
		_dollfailcount += dollfailcount;
	}

	public void saveDollFailCount() {
		if (isGhost()) {
			return;
		}
		CharacterTable.getInstance().storeCharacterDollFailCount(this);
	}

	private boolean _checktwopassword = false;

	public void setCheckTwopassword(final boolean checktwopassword) {
		_checktwopassword = checktwopassword;
	}

	/**
	 * 返回是否验证二级密码
	 * 
	 * @return
	 */
	public boolean isCheckTwopassword() {
		return _checktwopassword;
	}

	private boolean _xiugaitwopassword = false;

	public void setXiuGaiTwopassword(final boolean xiugaitwopassword) {
		_xiugaitwopassword = xiugaitwopassword;
	}

	/**
	 * 返回是否修改二级密码
	 * 
	 * @return
	 */
	public boolean isXiuGaiTwopassword() {
		return _xiugaitwopassword;
	}

	private int _old_twopassword = -256;

	public void setOldTwoPassword(final int old_twopassword) {
		_old_twopassword = old_twopassword;
	}

	public int getOldTwoPassword() {
		return _old_twopassword;
	}

	private boolean _isShowEnchantMessage = true;

	public boolean isShowEnchantMessage() {
		return _isShowEnchantMessage;
	}

	public void setShowEnchantMessage(final boolean showenchantMessage) {
		_isShowEnchantMessage = showenchantMessage;
	}

	private long _h_time;// 生存呐喊时间

	/**
	 * 生存呐喊时间
	 * 
	 * @return
	 */
	public long get_h_time() {
		return _h_time;
	}

	/**
	 * 生存呐喊时间
	 * 
	 * @param h_time
	 */
	public void set_h_time(long time) {
		_h_time = time;
	}

	public void startRenameThread() {
		GeneralThreadPool.getInstance().execute((new RenameThread()));
	}

	class RenameThread extends Thread {
		@Override
		public void run() {
			for (int i = 10; i > 0; i--) {
				if (getOnlineStatus() == 0) {
					break;
				}
				sendPackets(new S_SystemMessage(String.format(
						"\\F2改名成功%d秒后将退出游戏。请重新进入.", i)));
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (getNetConnection() != null) {
				getNetConnection().kick();
			}
		}
	}

	private int _damageReductionByDoll = 0;

	public int getDamageReductionByDoll() {
		return _damageReductionByDoll;
	}

	public void addDamageReductionByDoll(final int i, final int r) {
		_damageReductionByDoll += i;
		_damageReductionrandomByDoll += r;
	}

	private int _damageReductionrandomByDoll = 0;

	public int getDamageReductionRandomByDoll() {
		return _damageReductionrandomByDoll;
	}

	private double _weightUPByDoll = 1.0D;// 负重提高%

	/**
	 * 负重提高%
	 * 
	 * @return
	 */
	public double get_weightUPByDoll() {
		return _weightUPByDoll;
	}

	/**
	 * 负重提高%
	 * 
	 * @param i
	 */
	public void add_weightUPByDoll(final int i) {
		_weightUPByDoll += (i / 100D);
	}

	private int _byDollDmgupRandom = 0;

	public void addByDollDmgUpRandom(final int int1) {
		_byDollDmgupRandom += int1;
	}

	public int getByDollDmgUpRandom() {
		return _byDollDmgupRandom;
	}

	private int _byDollDmgupR = 0;

	public void addByDollDmgUpR(final int int2) {
		_byDollDmgupR += int2;
	}

	public int getByDollDmgUpR() {
		return _byDollDmgupR;
	}

	private int _byDollBowDmgupRandom = 0;

	public void addByDollBowDmgUpRandom(final int int1) {
		_byDollBowDmgupRandom += int1;
	}

	public int getByDollBowDmgUpRandom() {
		return _byDollBowDmgupRandom;
	}

	private int _byDollBowDmgupR = 0;

	public void addByDollBowDmgUpR(final int int2) {
		_byDollBowDmgupR += int2;
	}

	public int getByDollBowDmgUpR() {
		return _byDollBowDmgupR;
	}

	private double _expByDoll = 1.0;

	public void addExpByDoll(final int int2) {
		_expByDoll += (int2 / 100D);
	}

	public double getExpByDoll() {
		return _expByDoll;
	}

	private int _evasion;// 回避机率(1/1000)

	/**
	 * 回避机率
	 * 
	 * @param int1
	 */
	public void add_evasion(int int1) {
		_evasion += int1;
	}

	/**
	 * 传回回避机率
	 * 
	 * @return
	 */
	public int get_evasion() {
		return _evasion;
	}

	private int _heallingPotion = 0;

	public void addHeallingPotion(final int heallingPotion) {
		_heallingPotion += heallingPotion;
	}

	public int getHeallingPotion() {
		return _heallingPotion;
	}

	private boolean _isMassTeleport = true;

	public boolean isMassTeleport() {
		return _isMassTeleport;
	}

	public void setMassTeleport(final boolean isMassTeleport) {
		_isMassTeleport = isMassTeleport;
	}

	// 物品制造状态
	private boolean _ismakeitem = false;

	/**
	 * 是否处于物品制造状态
	 */
	public boolean ismakeitem() {
		return _ismakeitem;
	}

	/**
	 * 设置物品制造状态
	 */
	public void setismakeitem(boolean i) {
		_ismakeitem = i;
	}

	private boolean _iscangku = false;

	/**
	 * 是否处于仓库存取状态
	 */
	public boolean iscangku() {
		return _iscangku;
	}

	/**
	 * 设置仓库存取状态
	 */
	public void setcangku(boolean i) {
		_iscangku = i;
	}

	/**
	 * 无怪自动顺飞
	 */
	private boolean _isshunfei = false;

	/**
	 * 无怪自动顺飞
	 */
	public void set_isshunfei(final boolean b) {
		_isshunfei = b;
	}

	/**
	 * 无怪自动顺飞
	 */
	public boolean isshunfei() {
		return this._isshunfei;
	}

	/**
	 * 自动状态
	 */
	private boolean _skillAuto = false;

	/**
	 * 自动状态
	 */
	public boolean isskillAuto() {
		return this._skillAuto;
	}

	/**
	 * 自动状态
	 */
	private static final int skillIds[] = new int[] { 26, 42, 43, 48, 79, 151,
			158, 148, 115, 117 };

	public void setskillAuto(final boolean setskillAuto) {
		this._skillAuto = setskillAuto;
		if (setskillAuto) {
			AutoMagic.automagic(this, skillIds);
		}
	}

	// /**
	// * 自动状态
	// */
	// private static final int skillIds_gj[] = new int[] { 26, 42, 43, 48, 79,
	// 151,
	// 158, 148, 115, 117 };
	//
	// public void setskillAuto_gj(final boolean setskillAuto_gj) {
	// this._skillAuto_gj = setskillAuto_gj;
	// if (setskillAuto_gj) {
	// AutoMagic_GJ.automagic(this, skillIds_gj);
	// }
	// }

	// /** 挂机设置 **/
	// public final L1HateList _hateList = new L1HateList();// 目标清单
	// private boolean _firstAttack = false;
	// protected NpcMoveExecutor _pcMove = null;// XXX
	// private L1Character _target = null;
	//
	//
	// /**
	// * 设置目前攻击对象
	// *
	// * @param target
	// */
	// public void setNowTarget(final L1Character target) {
	// this._target = target;
	// }
	//
	// /**
	// * 传回目前攻击对象
	// */
	// public L1Character getNowTarget() {
	// return this._target;
	// }
	//
	// /**
	// * 启用PC AI
	// */
	// public synchronized void startAI() {
	// if (this.isDead()) {
	// return;
	// }
	// if (this.isGhost()) {
	// return;
	// }
	// if (this.getCurrentHp() <= 0) {
	// return;
	// }
	// if (this.isPrivateShop()) {
	// return;
	// }
	// if (this.isParalyzed()) {
	// return;
	// }
	//
	// if (_pcMove != null) {
	// _pcMove = null;
	// }
	// _pcMove = new pcMove(this);
	// this.setAiRunning(true);
	// this.setActived(true);
	// final PcAI npcai = new PcAI(this);
	// npcai.startAI();
	//
	// }
	//
	// public boolean _aiRunning = false; // PC AI时间轴 正在运行
	//
	// /**
	// * PC AI时间轴 正在运行
	// *
	// * @param aiRunning
	// */
	// public void setAiRunning(final boolean aiRunning) {
	// this._aiRunning = aiRunning;
	// }
	//
	//
	// /**
	// * PC AI时间轴 正在运行
	// *
	// * @return
	// */
	// public boolean isAiRunning() {
	// return this._aiRunning;
	// }
	//
	// /**
	// * 清除全部目标
	// */
	// public void allTargetClear() {
	// // XXX
	// if (_pcMove != null) {
	// _pcMove.clear();
	// }
	// _hateList.clear();
	// _target = null;
	// setFirstAttack(false);
	// }
	//
	// /**
	// * 清除单个目标
	// */
	// public void targetClear() {
	// if (_target == null) {
	// return;
	// }
	// _hateList.remove(_target);
	// _target = null;
	// }
	//
	// /**
	// * 有效目标检查
	// */
	// public void checkTarget() {
	// try {
	// if (_target == null) {// 目标为空
	// // targetClear();
	// return;
	// }
	// if (_target.getMapId() != getMapId()) {// 目标地图不相等
	// targetClear();
	// return;
	// }
	// if (_target.getCurrentHp() <= 0) {// 目标HP小于等于0
	// targetClear();
	// return;
	// }
	// if (_target.isDead()) {// 目标死亡
	// targetClear();
	// return;
	// }
	//
	//
	// if (!_hateList.containsKey(_target)) {// 目标不在已有攻击清单中
	// targetClear();
	// return;
	// }
	//
	// final int distance = getLocation().getTileDistance(
	// _target.getLocation());
	// if (distance > 15) {
	// targetClear();
	// return;
	// }
	//
	// } catch (final Exception e) {
	// return;
	// }
	// }
	//
	// /**
	// * 现在目标
	// */
	// public L1Character is_now_target() {
	// return _target;
	// }
	//
	// /**
	// * 对目标进行攻击
	// *
	// * @param target
	// */
	// public void attackTarget(final L1Character target) {
	//
	// if (this.getInventory().getWeight240() >= 197) { // 重量过重
	// // 110 \f1当负重过重的时候，无法战斗。
	// this.sendPackets(new S_ServerMessage(110));
	// // _log.error("要求角色攻击:重量过重");
	// return;
	// }
	//
	// if (target instanceof L1PcInstance) {
	// final L1PcInstance player = (L1PcInstance) target;
	// if (player.isTeleport()) { // テレポート处理中
	// return;
	// }
	// if (!player.isPinkName()) {
	// this.allTargetClear();
	// return;
	// }
	//
	// } else if (target instanceof L1PetInstance) {
	// final L1PetInstance pet = (L1PetInstance) target;
	// final L1Character cha = pet.getMaster();
	// if (cha instanceof L1PcInstance) {
	// final L1PcInstance player = (L1PcInstance) cha;
	// if (player.isTeleport()) { // テレポート处理中
	// return;
	// }
	// }
	//
	// } else if (target instanceof L1SummonInstance) {
	// final L1SummonInstance summon = (L1SummonInstance) target;
	// final L1Character cha = summon.getMaster();
	// if (cha instanceof L1PcInstance) {
	// final L1PcInstance player = (L1PcInstance) cha;
	// if (player.isTeleport()) { // テレポート处理中
	// return;
	// }
	// }
	// }
	//
	// if (target instanceof L1NpcInstance) {
	// final L1NpcInstance npc = (L1NpcInstance) target;
	// if (npc.getHiddenStatus() != 0) { // 地中に潜っているか、飞んでいる
	// this.allTargetClear();
	// return;
	// }
	// }
	// target.onAction(this);
	// // long h_time1 = Calendar.getInstance().getTimeInMillis() / 1000;//
	// // 换算为秒
	// // this.sendPackets(new S_SystemMessage("挂机延迟。" + h_time1));
	// }
	//
	// public void searchTarget() {
	// // 攻击目标搜寻
	// // System.out.println("AI启动44444");
	// // L1MonsterInstance targetPlayer = searchTarget(this);
	// // System.out.println("AI启动666==" + targetPlayer);
	// // if (targetPlayer != null) {
	// // _hateList.add(targetPlayer, 0);
	// // _target = targetPlayer;
	// //
	// // }
	// final Collection<L1Object> allObj =
	// L1World.getInstance().getVisibleObjects(this,
	// 15);
	// for (final Iterator<L1Object> iter = allObj.iterator(); iter.hasNext();)
	// {
	// final L1Object obj = iter.next();
	// if (!(obj instanceof L1MonsterInstance)) {
	// continue;
	// }
	// final L1MonsterInstance mob = (L1MonsterInstance) obj;
	// if (mob.isDead()) {
	// continue;
	// }
	// if (mob.getCurrentHp() <= 0) {
	// continue;
	// }
	// if (mob.getHiddenStatus() > 0) {
	// continue;
	// }
	// if (mob.getAtkspeed() == 0) {
	// continue;
	// }
	//
	// if (mob.hasSkillEffect(this.getId() + 100000)
	// && !this.isAttackPosition(mob.getX(), mob.getY(), 1)) {
	// continue;
	// }
	//
	// if (mob != null) {
	// final int Distance = 15 - this.getTileLineDistance(mob);
	// _hateList.add(mob, Distance);
	// }
	// }
	// _target = _hateList.getMaxHateCharacter();
	// if (_target == null) { // 如果目标等于空
	//
	// // 等待处理，，瞬移。。等等设置
	// if (this.getMap().isTeleportable()
	// && this.getInventory().consumeItem(40100, 1)) { // 地图可瞬移身上有白瞬卷
	// // hjx1000
	// L1Teleport.randomTeleport(this, true);
	// }
	//
	// // if (this.getMap().isTeleportable()
	// // && this.getInventory().checkItem(40100)) {
	// // L1Teleport.randomTeleport(this, true);
	// // }
	//
	// }
	// allObj.clear();
	// }
	//
	// /**
	// * 具有目标的处理 (攻击的判断)
	// */
	// public void onTarget() {
	// try {
	//
	// final L1Character target = _target;
	//
	// if (target == null) {
	// return;
	// }
	// attack(target);
	//
	// } catch (final Exception e) {
	// _log.error(e.getLocalizedMessage(), e);
	// }
	// }
	//
	// private void attack(L1Character target) {
	// // 攻击可能位置
	// int attack_Range = 1;
	// if (this.getWeapon() != null) {
	// attack_Range = this.getWeapon().getItem().getRange();
	// }
	// if (attack_Range < 0) {
	// attack_Range = 15;
	// }
	// if (isAttackPosition(target.getX(), target.getY(), attack_Range)) {//
	// 已经到达可以攻击的距离
	// setHeading(targetDirection(target.getX(), target.getY()));
	// attackTarget(target);
	// // XXX
	// if (_pcMove != null) {
	// _pcMove.clear();
	// }
	//
	// } else { // 攻击不可能位置
	// // final int distance = getLocation().getTileDistance(
	// // target.getLocation());
	// if (_pcMove != null) {
	// final int dir = _pcMove.moveDirection(target.getX(),
	// target.getY());
	// if (dir == -1) {
	// _target.setSkillEffect(this.getId() + 100000, 20000);// 给予20秒状态
	// //System.out.println("===AI执行===");
	// targetClear();
	//
	// } else {
	// _pcMove.setDirectionMove(dir);
	// //setSleepTime(calcSleepTime(getPassispeed(), MOVE_SPEED));
	// }
	// }
	// }
	// }
	//
	// private boolean _actived = false; // 挂机激活
	// private boolean _Pathfinding = false; // 寻路中.. hjx1000

	/**
	 * // * PC已经激活 // * // * @param actived // * true:激活 false:无 //
	 */
	// public void setActived(final boolean actived) {
	// this._actived = actived;
	// }
	//
	// /**
	// * PC已经激活
	// *
	// * @return true:激活 false:无
	// */
	// public boolean isActived() {
	// return this._actived;
	// }
	//
	// protected void setFirstAttack(final boolean firstAttack) {
	// this._firstAttack = firstAttack;
	// }
	//
	// protected boolean isFirstAttack() {
	// return this._firstAttack;
	// }
	//
	// /**
	// * 攻击目标设置
	// *
	// * @param cha
	// * @param hate
	// */
	// public void setHate(final L1Character cha, int hate) {
	// try {
	// if ((cha != null) && /* (cha.getId() != getId()) */_target != null) {
	// if (!isFirstAttack() && (hate > 0)) {
	// // hate += getMaxHp() / 10; // ＦＡヘイト
	// setFirstAttack(true);
	// if (_pcMove != null) {
	// _pcMove.clear();// XXX
	// }
	// // System.out.println("isFirstAttack=" + isFirstAttack());
	// _hateList.add(cha, 5);
	// _target = _hateList.getMaxHateCharacter();
	// checkTarget();
	// }
	// }
	//
	// } catch (final Exception e) {
	// return;
	// }
	// }
	//
	// /**
	// * 目标为空挂机寻路中
	// *
	// * @return
	// */
	// public boolean isPathfinding() {
	// return this._Pathfinding;
	// }
	//
	// public void setPathfinding(final boolean fla) {
	// this._Pathfinding = fla;
	// }
	//
	// // 随机移动距离
	// // private int _randomMoveDistance = 0;
	// // 随机移动方向
	// private int _randomMoveDirection = 0;
	//
	// public int getrandomMoveDirection() {
	// return _randomMoveDirection;
	// }
	//
	// public void setrandomMoveDirection(int randomMoveDirection) {
	// this._randomMoveDirection = randomMoveDirection;
	// }
	//
	// /**
	// * 没有目标的处理 (传回本次AI是否执行完成)<BR>
	// * 具有主人 跟随主人移动
	// *
	// * @return true:本次AI执行完成 <BR>
	// * false:本次AI执行未完成
	// */
	// public void noTarget() {
	// if (!_Pathfinding) {
	// _Pathfinding = true; // 设置寻路中
	// }
	// if (_randomMoveDirection > 7) {
	// _randomMoveDirection = 0;
	// }
	// // System.out.println("_randomMoveDirection=:" + _randomMoveDirection);
	// if (_pcMove != null) {
	// if (getrandomMoveDirection() < 8) {
	// int dir = _pcMove.checkObject(_randomMoveDirection);
	// dir = _pcMove.openDoor(dir);
	//
	// if (dir != -1) {
	// _pcMove.setDirectionMove(dir);
	// } else {
	// _randomMoveDirection = _random.nextInt(8);
	// }
	// }
	// }
	// }
	//
	// /**
	// * 无法攻击/使用道具/技能/回城的状态
	// *
	// * @return true:状态中 false:无
	// */
	// public boolean isParalyzedX() {
	// // 冰矛围篱
	// if (hasSkillEffect(ICE_LANCE)) {
	// return true;
	// }
	// // 冰雪飓风
	// if (hasSkillEffect(FREEZING_BLIZZARD)) {
	// return true;
	// }
	// // 寒冰喷吐
	// if (hasSkillEffect(FREEZING_BREATH)) {
	// return true;
	// }
	// // 大地屏障
	// if (hasSkillEffect(EARTH_BIND)) {
	// return true;
	// }
	// // 冲击之晕
	// if (hasSkillEffect(SHOCK_STUN)) {
	// return true;
	// }
	// // 骷髅毁坏
	// if (hasSkillEffect(BONE_BREAK)) {
	// return true;
	// }
	// // 木乃伊的诅咒
	// if (hasSkillEffect(CURSE_PARALYZE)) {
	// return true;
	// }
	// if (hasSkillEffect(STATUS_POISON_PARALYZED)) { // 食尸鬼的麻痹状态 hjx1000
	// return true;
	// }
	//
	// return false;
	// }

	private int _homeX; // 开始挂机的x坐标

	public int getHomeX() {
		return this._homeX;
	}

	public void setHomeX(final int i) {
		this._homeX = i;
	}

	private int _homeY; // 开始挂机的y坐标

	public int getHomeY() {
		return this._homeY;
	}

	public void setHomeY(final int i) {
		this._homeY = i;
	}

	private int _hookrange; // 挂机范围

	public int gethookrange() {
		return _hookrange;
	}

	public void sethookrange(final int i) {
		this._hookrange = i;
	}

	/**
     * 觉醒技能ID
     */
    private int _awakeSkillId = 0;
    
    /**
     * 取得觉醒技能ID
     * 
     * @return
     */
    public int getAwakeSkillId() {
        return this._awakeSkillId;
    }
    
    /**
     * 设定觉醒技能ID
     * 
     * @param i
     */
    public void setAwakeSkillId(final int i) {
        this._awakeSkillId = i;
    }
    
    private boolean _isFoeSlayer = false;

	/**
	 * 是否使用屠宰者
	 * 
	 * @return
	 */
	public boolean isFoeSlayer() {
		return _isFoeSlayer;
	}

	/**
	 * 是否使用屠宰者
	 */
	public void isFoeSlayer(boolean isFoeSlayer) {
		_isFoeSlayer = isFoeSlayer;
	}

	private int _weaknss = 0;
	private long _weaknss_t = 0;// 时间

	/**
	 * 弱点曝光时间
	 * 
	 * @return
	 */
	public long get_weaknss_t() {
		return _weaknss_t;
	}

	/**
	 * 弱点曝光阶段
	 * 
	 * @return
	 */
	public int get_weaknss() {
		return _weaknss;
	}

	/**
	 * 弱点曝光阶段
	 * 
	 * @param lv
	 */
	public void set_weaknss(int lv, long t) {
		_weaknss = lv;
		_weaknss_t = t;
		switch (_weaknss) { // 设置之弱点暴光状态 hjx1000
		case 1:
			this.sendPackets(new S_PacketBoxDk(S_PacketBoxDk.LV1));
			break;
		case 2:
			this.sendPackets(new S_PacketBoxDk(S_PacketBoxDk.LV2));
			break;
		case 3:
			this.sendPackets(new S_PacketBoxDk(S_PacketBoxDk.LV3));
			break;

		}
	}
}
