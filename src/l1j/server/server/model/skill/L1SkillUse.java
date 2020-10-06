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

package l1j.server.server.model.skill;

import static l1j.server.server.model.skill.L1SkillId.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import l1j.server.server.ActionCodes;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.lock.CharBookReading;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1CurseParalysis;
import l1j.server.server.model.L1EffectSpawn;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1War;
import l1j.server.server.model.Instance.L1BabyInstance;
import l1j.server.server.model.Instance.L1CastleGuardInstance;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1GuardInstance;
import l1j.server.server.model.Instance.L1HierarchInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1MerchantInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.Instance.L1TowerInstance;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.poison.L1Poison2;
import l1j.server.server.model.poison.L1Poison3;
import l1j.server.server.model.poison.L1Poison4;
import l1j.server.server.model.poison.L1Poison6;
import l1j.server.server.model.trap.L1WorldTraps;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_CharVisualUpdate;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_CurseBlind;
import l1j.server.server.serverpackets.S_Dexup;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.serverpackets.S_EffectLocation;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_Light;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_PacketBoxWindShackle;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_Poison;
import l1j.server.server.serverpackets.S_RangeSkill;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_ShowPolyList;
import l1j.server.server.serverpackets.S_ShowSummonList;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillIconAura;
import l1j.server.server.serverpackets.S_SkillIconGFX;
import l1j.server.server.serverpackets.S_SkillIconShield;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_Sound;
import l1j.server.server.serverpackets.S_Strup;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.S_TrueTarget;
import l1j.server.server.serverpackets.S_UseAttackSkill;
import l1j.server.server.templates.L1BookMark;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.world.L1World;
import l1j.william.L1WilliamSystemMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class L1SkillUse {
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_LOGIN = 1;
	public static final int TYPE_SPELLSC = 2;
	public static final int TYPE_NPCBUFF = 3;
	public static final int TYPE_GMBUFF = 4;

	private L1Skills _skill;
	private int _skillId;
	private int _getBuffDuration;
	private int _shockStunDuration;
	private int _getBuffIconDuration;
	private int _targetID;
	private int _mpConsume = 0;
	private int _hpConsume = 0;
	private int _targetX = 0;
	private int _targetY = 0;
	private String _message = null;
	private int _skillTime = 0;
	private boolean _isPK = false;
	private int _itemobjid = 0;
	private boolean _checkedUseSkill = false; // 事前济
	private int _leverage = 10; // 1/10倍101倍
	private boolean _isFreeze = false;

	private L1Character _user = null;
	private L1Character _target = null;

	private L1PcInstance _player = null;
	private L1NpcInstance _npc = null;
	private L1NpcInstance _targetNpc = null;

	private int _calcType;
	private static final int PC_PC = 1;
	private static final int PC_NPC = 2;
	private static final int NPC_PC = 3;
	private static final int NPC_NPC = 4;

	private ArrayList<TargetStatus> _targetList;

	private static final Log _log = LogFactory.getLog(L1SkillUse.class);

	/**
	 * 隐身状态下不可以使用的魔法。
	 * 
	 */
	private static final int[] CAST_WITH_INVIS = { 1, 2, 3, 5, 8, 9, 12, 13,
			14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 44, 48, 49, 52, 54, 55, 57,
			60, 61, 63, 67, 68, 69, 72, 73, 75, 78, 79, REDUCTION_ARMOR,
			BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER, 97, 98, 99, 100,
			101, 102, 104, 105, 106, 107, 109, 110, 111, 113, 114, 115, 116,
			117, 118, 129, 130, 131, 133, 134, 137, 138, 146, 147, 148, 149,
			150, 151, 155, 156, 158, 159, 163, 164, 165, 166, 168, 169, 170,
			171, SOUL_OF_FLAME, ADDITIONAL_FIRE, DRAGON_SKIN, AWAKEN_ANTHARAS,
			AWAKEN_FAFURION, AWAKEN_VALAKAS, MIRROR_IMAGE, ILLUSION_OGRE,
			ILLUSION_LICH, PATIENCE, ILLUSION_DIA_GOLEM, INSIGHT,
			ILLUSION_AVATAR };

	private static final int[] EXCEPT_COUNTER_MAGIC = { 1, 2, 3, 5, 8, 9, 12,
			13, 14, 19, 21, 26, 31, 32, 35, 37, 42, 43, 44, 48, 49, 52, 54, 55,
			57, 60, 61, 63, 67, 68, 69, 72, 73, 75, 78, 79, SHOCK_STUN,
			REDUCTION_ARMOR, BOUNCE_ATTACK, SOLID_CARRIAGE, COUNTER_BARRIER,
			97, 98, 99, 100, 101, 102, 104, 105, 106, 107, 109, 110, 111, 113,
			114, 115, 116, 117, 118, 129, 130, 131, TRIPLE_ARROW, 134, 137,
			138, 146, 147, 148, 149, 150, 151, 155, 156, 158, 159, 161, 163,
			164, 165, 166, 168, 169, 170, 171, SOUL_OF_FLAME, ADDITIONAL_FIRE,
			10026, 10027, 10028, 10029, 12120, 12123, 12128, 12131,
			// 龙骑士技能 181
			DRAGON_SKIN };// 补上12120,
							// 12123,
							// 12128,
							// 12131

	public L1SkillUse() {
	}

	private static class TargetStatus {
		private L1Character _target = null;
		// private boolean _isAction = false; // 发生？
		// private boolean _isSendStatus = false; //
		// 送信？（、状态变送）
		private boolean _isCalc = true; // 确率魔法计算必要？

		public TargetStatus(L1Character _cha) {
			_target = _cha;
		}

		public TargetStatus(L1Character _cha, boolean _flg) {
			_isCalc = _flg;
		}

		public L1Character getTarget() {
			return _target;
		}

		public boolean isCalc() {
			return _isCalc;
		}

		/*
		 * public void isAction(boolean _flg) { _isAction = _flg; }
		 * 
		 * public boolean isAction() { return _isAction; }
		 * 
		 * public void isSendStatus(boolean _flg) { _isSendStatus = _flg; }
		 * 
		 * public boolean isSendStatus() { return _isSendStatus; }
		 */
	}

	/*
	 * 1/10倍表现。
	 */
	public void setLeverage(int i) {
		_leverage = i;
	}

	public int getLeverage() {
		return _leverage;
	}

	private boolean isCheckedUseSkill() {
		return _checkedUseSkill;
	}

	private void setCheckedUseSkill(boolean flg) {
		_checkedUseSkill = flg;
	}

	public boolean checkUseSkill(L1PcInstance player, int skillid,
			int target_id, int x, int y, String message, int time, int type,
			L1Character attacker) {
		// 初期设定
		setCheckedUseSkill(true);
		_targetList = new ArrayList<TargetStatus>(); // 初期化

		_skill = SkillsTable.getInstance().getTemplate(skillid);
		_skillId = skillid;
		_targetX = x;
		_targetY = y;
		_message = message;
		_skillTime = time;
		boolean checkedResult = true;

		if (attacker == null) {
			// pc
			_player = player;
			_user = _player;
		} else {
			// npc
			_npc = (L1NpcInstance) attacker;
			_user = _npc;
		}

		if (_skill.getTarget().equals("none")) {
			_targetID = _user.getId();
			_targetX = _user.getX();
			_targetY = _user.getY();
		} else {
			_targetID = target_id;
		}

		// 幽灵之家无法使用魔法
		if (player != null && (player.getMapId() == 5140)) {
			return false;
		}
		// 幽灵之家无法使用魔法 end

		if (type == TYPE_NORMAL) { // 通常魔法使用时
			checkedResult = isNormalSkillUsable();
		} else if (type == TYPE_SPELLSC) { // 使用时
			checkedResult = isSpellScrollUsable();
		} else if (type == TYPE_NPCBUFF) {
			checkedResult = true;
		}
		if (!checkedResult) {
			return false;
		}

		// 、咏唱对像座标
		if (_skillId == FIRE_WALL || _skillId == LIFE_STREAM) {
			return true;
		}

		L1Object l1object = L1World.getInstance().findObject(_targetID);
		if (l1object instanceof L1ItemInstance) {
			_log.info("skill target item name: "
					+ ((L1ItemInstance) l1object).getViewName());
			// 精灵石。
			// Linux环境确认（Windows未确认）
			// 2008.5.4追记：地面魔法使。继续return
			return false;
		}
		if (_user instanceof L1PcInstance) {
			if (l1object instanceof L1PcInstance) {
				_calcType = PC_PC;
			} else {
				_calcType = PC_NPC;
				_targetNpc = (L1NpcInstance) l1object;
			}
		} else if (_user instanceof L1NpcInstance) {
			if (l1object instanceof L1PcInstance) {
				_calcType = NPC_PC;
			} else if (_skill.getTarget().equals("none")) {
				_calcType = NPC_PC;
			} else {
				_calcType = NPC_NPC;
				_targetNpc = (L1NpcInstance) l1object;
			}
		}
		if (_skillId == CREATE_MAGICAL_WEAPON || _skillId == BRING_STONE
				|| _skillId == 12 || _skillId == 21 || _skillId == 107) {// 拟武、铠甲、fg暗牙修正
			_itemobjid = target_id;
		}
		_target = (L1Character) l1object;

		if (!(_target instanceof L1MonsterInstance)
				&& _skill.getTarget().equals("attack")
				&& _user.getId() != target_id) {
			_isPK = true; // 以外攻击系、自分以外场合PK。
		}

		// 初期设定

		// 事前
		if (!(l1object instanceof L1Character)) { // 以外场合何。
			checkedResult = false;
		}

		if (type == TYPE_GMBUFF) {
			_targetList.add(new TargetStatus(_user));
		} else {
			makeTargetList(); // 一览作成
		}

		if (_targetList.size() == 0 && (_user instanceof L1NpcInstance)) {
			checkedResult = false;
		}
		// 事前
		return checkedResult;
	}

	/**
	 * 通常使用时使用者态使用可能判
	 * 
	 * @return false 使用不可能态场合
	 */
	private boolean isNormalSkillUsable() {
		// 使用者PC场合
		if (_user instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) _user;

			if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill()) { // 中使用不可
				return false;
			}
			if (pc.getInventory().getWeight240() >= 197) { // 重量使用
				pc.sendPackets(new S_ServerMessage(316));
				return false;
			}
			int polyId = pc.getTempCharGfx();
			L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);
			// 魔法使变身
			if (poly != null && !poly.canUseSkill()) {
				pc.sendPackets(new S_ServerMessage(285)); // \f1状态魔法使。
				return false;
			}

			if (!isAttrAgrees()) { // 精灵魔法、属性一致何。
				return false;
			}
			if (_skillId == ELEMENTAL_PROTECTION && pc.getElfAttr() == 0) {
				pc.sendPackets(new S_ServerMessage(280));// \f1魔法失败。
				return false;
			}
			// 中使用不可
			if (pc.isSkillDelay()) {
				return false;
			}

			// 状态使用不可
			if ((pc.hasSkillEffect(SILENCE)
					|| pc.hasSkillEffect(AREA_OF_SILENCE) || pc
						.hasSkillEffect(STATUS_POISON_SILENCE))
					&& _skillId != SHOCK_STUN
					&& _skillId != REDUCTION_ARMOR
					&& _skillId != BOUNCE_ATTACK
					&& _skillId != SOLID_CARRIAGE
					&& _skillId != COUNTER_BARRIER
					&& _skillId != THUNDER_GRAB) { // 修正骑士技能被魔封仍可以施放
				pc.sendPackets(new S_ServerMessage(285)); // \f1状态魔法使。
				return false;
			}

			// 觉醒状态 - 非觉醒技能无法使用
			if (((pc.getAwakeSkillId() == AWAKEN_ANTHARAS) && (this._skillId != AWAKEN_ANTHARAS))
					|| ((pc.getAwakeSkillId() == AWAKEN_FAFURION) && (this._skillId != AWAKEN_FAFURION))
					|| (((pc.getAwakeSkillId() == AWAKEN_VALAKAS) && (this._skillId != AWAKEN_VALAKAS))
							&& (this._skillId != MAGMA_BREATH)
							&& (this._skillId != SHOCK_SKIN) && (this._skillId != FREEZING_BREATH))) {
				pc.sendPackets(new S_ServerMessage(1385)); // 目前状态中无法使用觉醒魔法。
				return false;
			}

			// DIG使用可
			if (_skillId == DISINTEGRATE && pc.getLawful() < 500) {
				// 未确认
				pc.sendPackets(new S_ServerMessage(352, "$967")); // 魔法利用性向值%0。
				return false;
			}

			if (isItemConsume() == false && !_player.isGm()) { // 消费
				_player.sendPackets(new S_ServerMessage(299)); // 咏唱材料。
				return false;
			}
		}
		// 使用者NPC场合
		else if (_user instanceof L1NpcInstance) {

			// 状态使用不可
			if (_user.hasSkillEffect(SILENCE)) {
				// NPC挂场合1回使用效果。
				_user.removeSkillEffect(SILENCE);
				return false;
			}
		}

		// PC、NPC共通
		if (!isHPMPConsume()) { // 消费HP、MP
			return false;
		}
		return true;
	}

	/**
	 * 使用时使用者态使用可能判
	 * 
	 * @return false 使用不可能态场合
	 */
	private boolean isSpellScrollUsable() {
		// 使用PC
		L1PcInstance pc = (L1PcInstance) _user;

		// 中使用不可
		if ((pc.isInvisble() || pc.isInvisDelay()) && !isInvisUsableSkill()) {
			return false;
		}

		if (pc.isSkillDelay()) {
			return false;
		}

		return true;
	}

	// 中使用可能返
	private boolean isInvisUsableSkill() {
		for (int skillId : CAST_WITH_INVIS) {
			if (skillId == _skillId) {
				return true;
			}
		}
		return false;
	}

	public void handleCommands(L1PcInstance player, int skillId, int targetId,
			int x, int y, String message, int timeSecs, int type, int actid) {
		L1Character attacker = null;
		handleCommands(player, skillId, targetId, x, y, message, timeSecs,
				type, actid, attacker);
	}

	public void handleCommands(L1PcInstance player, int skillId, int targetId,
			int x, int y, String message, int timeSecs, int type) {
		L1Character attacker = null;
		handleCommands(player, skillId, targetId, x, y, message, timeSecs,
				type, 0, attacker);
	}

	public void handleCommands(L1PcInstance player, int skillId, int targetId,
			int x, int y, String message, int timeSecs, int type, int actid,
			L1Character attacker) {

		try {
			// 事前？
			if (!isCheckedUseSkill()) {
				boolean isUseSkill = checkUseSkill(player, skillId, targetId,
						x, y, message, timeSecs, type, attacker);

				if (!isUseSkill) {
					failSkill();
					return;
				}
			}

			if (type == TYPE_NORMAL) { // 魔法咏唱时
				runSkill();
				useConsume();
				sendGrfx(true, actid);
				sendFailMessageHandle();
				setDelay();
			} else if (type == TYPE_LOGIN) { // 时（HPMP材料消费、）
				runSkill();
			} else if (type == TYPE_SPELLSC) { // 使用时（HPMP材料消费）
				runSkill();
				sendGrfx(true, actid);
				setDelay();
			} else if (type == TYPE_GMBUFF) { // GMBUFF使用时（HPMP材料消费、魔法）
				runSkill();
				sendGrfx(false, actid);
			} else if (type == TYPE_NPCBUFF) { // NPCBUFF使用时（HPMP材料消费）
				runSkill();
				sendGrfx(true, actid);
			}
			setCheckedUseSkill(false);
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			_targetList.clear();
		}
	}

	/**
	 * 失败处理(PC）
	 */
	private void failSkill() {
		// HP足使用场合、MP消费未实装（必要？）
		// 他场合何消费。
		// useConsume(); // HP、MP减
		setCheckedUseSkill(false);
		// 
		if (_skillId == TELEPORT || _skillId == MASS_TELEPORT
				|| _skillId == TELEPORT_TO_MATHER) {
			// 场合、侧应答待
			// 待状态解除（第2引数意味）
			_player.sendPackets(new S_Paralysis(
					S_Paralysis.TYPE_TELEPORT_UNLOCK, 0, false));
		}
	}

	// ？
	private boolean isTarget(L1Character cha) throws Exception {
		if (cha == null) {
			return false;
		}
		boolean _flg = false;

		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			if (pc.isGhost() || pc.isGmInvis()) {
				return false;
			}
			// 修正隔空施法
			if (_user.getLocation().getTileLineDistance(pc.getLocation()) >= 13) {
				return false;
			}
			// 修正隔空施法 end
		}
		if (_calcType == NPC_PC
				&& (cha instanceof L1PcInstance || cha instanceof L1PetInstance || cha instanceof L1SummonInstance)) {
			_flg = true;
		}

		if (_user instanceof L1PcInstance || _user instanceof L1PetInstance
				|| _user instanceof L1SummonInstance) {
			if (_user.getMap().isNormalZone(_user.getLocation())
					&& cha.getMap().isSafetyZone(cha.getLocation())) {
				return false;
			}
			if (_user.getMap().isCombatZone(_user.getLocation())
					&& cha.getMap().isSafetyZone(cha.getLocation())) {
				return false;
			}
		}

		// 修正召唤怪的范围魔法
		if (((_user instanceof L1PetInstance) || (_user instanceof L1SummonInstance))
				&& (_skill.getArea() > 0 || _skillId == 17)) {
			if (_user.glanceCheck(cha.getX(), cha.getY()) == true
					|| _skill.getIsThrough() == true) {
				return (cha instanceof L1MonsterInstance);
			}
		}
		// 修正召唤怪的范围魔法 end

		// 元Pet、Summon以外NPC场合、PC、Pet、Summon对像外
		if (_calcType == PC_NPC
				&& _target instanceof L1NpcInstance
				&& !(_target instanceof L1PetInstance)
				&& !(_target instanceof L1SummonInstance)
				&& (cha instanceof L1PetInstance
						|| cha instanceof L1SummonInstance || cha instanceof L1PcInstance)) {
			return false;
		}

		if (_calcType == NPC_NPC && _skill.getType() == L1Skills.TYPE_ATTACK
				&& cha instanceof L1MonsterInstance
				&& _user instanceof L1MonsterInstance) {
			return false;
		}

		// NPC对PC场合。
		if ((_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK)
				&& _calcType == NPC_PC
				&& !(cha instanceof L1PetInstance)
				&& !(cha instanceof L1SummonInstance)
				&& !(cha instanceof L1PcInstance)) {
			return false;
		}

		// 自分H-A场合效果无
		if (cha.getId() == _user.getId() && _skillId == HEAL_ALL) {
			return false;
		}
		if (_skillId == NATURES_BLESSING) {// 生命的祝福
			if (!(cha instanceof L1PcInstance)) {
				return false;
			}
			L1PcInstance player = (L1PcInstance) _user;
			L1PcInstance member = (L1PcInstance) cha;
			if (player.isInParty()) {
				if (!player.getParty().isMember(member)) {
					return false;
				}
			} else {
				if (cha.getId() != _user.getId()) {
					return false;
				}
			}
			// 修改生命的祝福不能隔墙释放
			if (!player.glanceCheck(member.getX(), member.getY())) {
				return false;
			}
			return true;
		}

		if (((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC
				|| (_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN || (_skill
				.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY)
				&& cha.getId() == _user.getId() && _skillId != 49) {
			return true; // 员自分效果。（、除外）
		}

		// 使用者PC、PK场合、自分对像外
		if (_user instanceof L1PcInstance
				&& (_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK)
				&& _isPK == false) {
			if (cha instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) cha;
				if (_player.getId() == summon.getMaster().getId()) {
					return false;
				}
			} else if (cha instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) cha;
				if (_player.getId() == pet.getMaster().getId()) {
					return false;
				}
			} else if (cha instanceof L1PcInstance) {
				L1PcInstance player = (L1PcInstance) cha;
				if (_player.getClanid() == player.getClanid()) {
					return false;
				}
			}
		}

		if ((_skill.getTarget().equals("attack") || _skill.getType() == L1Skills.TYPE_ATTACK)
				&& !(cha instanceof L1MonsterInstance)
				&& _isPK == false
				&& _target instanceof L1PcInstance) {
			L1PcInstance enemy = (L1PcInstance) _target;
			// 
			if (_skillId == COUNTER_DETECTION
					&& enemy.getZoneType() != 1
					&& (cha.hasSkillEffect(INVISIBILITY) || cha
							.hasSkillEffect(BLIND_HIDING))) {
				return true; // 中
			}
			if (_player.getClanid() != 0 && enemy.getClanid() != 0) { // 所属中
				// 全战争取得
				for (L1War war : L1World.getInstance().getWarList()) {
					if (war.CheckClanInWar(_player.getClanname())) { // 自战争参加中
						if (war.CheckClanInSameWar( // 同战争参加中
								_player.getClanname(), enemy.getClanname())) {
							/*
							 * if (enemy.getZoneType() != 1) { //
							 * 以外 return true; }
							 */
							if (L1CastleLocation.checkInAllWarArea(cha.getX(),
									cha.getY(), cha.getMapId())) {
								return true;
							}
						}
					}
				}
			}
			return false; // 攻击PK场合
		}

		if (_skill.getType() == L1Skills.TYPE_ATTACK
				&& cha.getId() == _user.getId()) {
			return false; // 攻击系对像自分对像外
		}

		if (_user.glanceCheck(cha.getX(), cha.getY()) == false
				&& _skill.getIsThrough() == false) {
			// 、复活障害物判定
			if (!(_skill.getType() == L1Skills.TYPE_CHANGE || _skill.getType() == L1Skills.TYPE_RESTORE)) {
				return false; // 直线上障害物
			}
		}

		if (cha.hasSkillEffect(ICE_LANCE) && _skillId == ICE_LANCE) {
			return false; // 中
		}

		if (cha.hasSkillEffect(EARTH_BIND) && _skillId == EARTH_BIND) {
			return false; //  中 
		}

		if (!(cha instanceof L1MonsterInstance)
				&& (_skillId == TAMING_MONSTER || _skillId == CREATE_ZOMBIE)) {
			return false; // （）
		}
		if (cha.isDead()
				&& (_skillId != CREATE_ZOMBIE && _skillId != RESURRECTION
						&& _skillId != GREATER_RESURRECTION && _skillId != CALL_OF_NATURE)) {
			return false; // 死亡
		}

		if (cha.isDead() == false
				&& (_skillId == CREATE_ZOMBIE || _skillId == RESURRECTION
						|| _skillId == GREATER_RESURRECTION || _skillId == CALL_OF_NATURE)) {
			return false; // 死亡
		}

		if (cha instanceof L1TowerInstance
				&& (_skillId == CREATE_ZOMBIE || _skillId == RESURRECTION
						|| _skillId == GREATER_RESURRECTION || _skillId == CALL_OF_NATURE)) {
			return false; // 
		}

		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			if (pc.hasSkillEffect(ABSOLUTE_BARRIER)) { // 中
				if (_skillId == CURSE_BLIND || _skillId == WEAPON_BREAK
						|| _skillId == DARKNESS || _skillId == WEAKNESS
						|| _skillId == DISEASE || _skillId == FOG_OF_SLEEPING
						|| _skillId == MASS_SLOW || _skillId == SLOW
						|| _skillId == CANCELLATION || _skillId == SILENCE
						|| _skillId == DECAY_POTION
						|| _skillId == MASS_TELEPORT || _skillId == DETECTION || _skillId == FREEZING_BREATH
						|| _skillId == COUNTER_DETECTION
						|| _skillId == ERASE_MAGIC || _skillId == ENTANGLE
						|| _skillId == PHYSICAL_ENCHANT_DEX
						|| _skillId == PHYSICAL_ENCHANT_STR
						|| _skillId == BLESS_WEAPON || _skillId == EARTH_SKIN
						|| _skillId == IMMUNE_TO_HARM
						|| _skillId == REMOVE_CURSE) {
					return true;
				} else {
					return false;
				}
			}
		}

		if (cha instanceof L1NpcInstance) {
			int hiddenStatus = ((L1NpcInstance) cha).getHiddenStatus();
			if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
				if (_skillId == DETECTION || _skillId == COUNTER_DETECTION || _skillId == FREEZING_BREATH) { // 、C
					return true;
				} else {
					return false;
				}
			} else if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_FLY) {
				return false;
			}
		}

		if ((_skill.getTargetTo() & L1Skills.TARGET_TO_PC) == L1Skills.TARGET_TO_PC // PC
				&& cha instanceof L1PcInstance) {
			_flg = true;
		} else if ((_skill.getTargetTo() & L1Skills.TARGET_TO_NPC) == L1Skills.TARGET_TO_NPC // NPC
				&& (cha instanceof L1MonsterInstance
						|| cha instanceof L1NpcInstance
						|| cha instanceof L1SummonInstance || cha instanceof L1PetInstance)) {
			_flg = true;
		} else if ((_skill.getTargetTo() & L1Skills.TARGET_TO_PET) == L1Skills.TARGET_TO_PET
				&& (cha instanceof L1SummonInstance || cha instanceof L1PetInstance)) {
			_flg = true;
		}

		if (_calcType == PC_PC && cha instanceof L1PcInstance) {
			if ((_skill.getTargetTo() & L1Skills.TARGET_TO_CLAN) == L1Skills.TARGET_TO_CLAN
					&& ((_player.getClanid() != 0 // 员
					&& _player.getClanid() == ((L1PcInstance) cha).getClanid()) || _player
							.isGm())) {
				_flg = true;
			}
			if (cha instanceof L1PcInstance) {
				if (((L1PcInstance) cha).getParty() != null && _player != null
						&& _player.getParty() != null) {
					if ((_skill.getTargetTo() & L1Skills.TARGET_TO_PARTY) == L1Skills.TARGET_TO_PARTY
							&& (_player.getParty() // 
									.isMember((L1PcInstance) cha) || _player
									.isGm())) {
						_flg = true;
					}
				}
			}
		}
		return _flg;
	}

	// 一览作成
	private void makeTargetList() {
		try {
			if (_skill.getTargetTo() == L1Skills.TARGET_TO_ME
					&& (_skill.getType() & L1Skills.TYPE_ATTACK) != L1Skills.TYPE_ATTACK) {
				_targetList.add(new TargetStatus(_user)); // 使用者
				return;
			}

			if (_target == null) {
				return;
			}
			// 射程距离-1场合画面内对像
			// 当、距离
			if (_skill.getRanged() != -1) {
				if (_user.getLocation().getTileLineDistance(
						_target.getLocation()) > _skill.getRanged()) {
					return; // 射程范围外
				}
			}

			if (isTarget(_target) == false
					&& !(_skill.getTarget().equals("none"))) {
				// 对像违发动。
				return;
			}

			if (_skillId == LIGHTNING) {
				// 直线的范围决。
				ArrayList<L1Object> al1object = L1World.getInstance()
						.getVisibleLineObjects(_user, _target);

				for (L1Object tgobj : al1object) {
					if (tgobj == null) {
						continue;
					}
					if (!(tgobj instanceof L1Character)) { // 以外场合何。
						continue;
					}
					L1Character cha = (L1Character) tgobj;
					if (!isTarget(cha)) {
						continue;
					}
					_targetList.add(new TargetStatus(cha));
				}
				return;
			}

			if (_skill.getArea() == 0) { // 单体场合
				if (_user.glanceCheck(_target.getX(), _target.getY()) // 直线上障害物
				== false) {
					if ((_skill.getType() & L1Skills.TYPE_ATTACK) == L1Skills.TYPE_ATTACK) {
						_targetList.add(new TargetStatus(_target, false)); // 发生、发生、发动
						return;
					}
				}
				_targetList.add(new TargetStatus(_target));
			} else { // 范围场合
				if (!_skill.getTarget().equals("none")) {
					_targetList.add(new TargetStatus(_target));
				}

				if (_skillId != 49
						&& !(_skill.getTarget().equals("attack") || _skill
								.getType() == L1Skills.TYPE_ATTACK)) {
					// 攻击系以外H-A以外自身含
					_targetList.add(new TargetStatus(_user));
				}

				List<L1Object> objects;
				if (_skill.getArea() == -1) {
					objects = L1World.getInstance().getVisibleObjects(_user);
				} else {
					objects = L1World.getInstance().getVisibleObjects(_target,
							_skill.getArea());
				}
				for (L1Object tgobj : objects) {
					if (tgobj == null) {
						continue;
					}
					if (!(tgobj instanceof L1Character)) { // 以外场合何。
						continue;
					}
					L1Character cha = (L1Character) tgobj;
					if (!isTarget(cha)) {
						continue;
					}

					_targetList.add(new TargetStatus(cha));
				}
				return;
			}

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			if (_user != null) {
				_log.info("skillid:" + _skill.getSkillId());
			}
		}
	}

	// 表示（何起）
	private void sendHappenMessage(L1PcInstance pc) {
		int msgID = _skill.getSysmsgIdHappen();
		if (msgID > 0) {
			pc.sendPackets(new S_ServerMessage(msgID));
		}
	}

	// 失败表示
	private void sendFailMessageHandle() {
		// 攻击以外对像指定失败场合失败送信
		// ※攻击障害物成功时同。
		if (_skill.getType() != L1Skills.TYPE_ATTACK
				&& !_skill.getTarget().equals("none")
				&& _targetList.size() == 0) {
			sendFailMessage();
		}
	}

	// 表示（失败）
	private void sendFailMessage() {
		int msgID = _skill.getSysmsgIdFail();
		if (msgID > 0 && (_user instanceof L1PcInstance)) {
			_player.sendPackets(new S_ServerMessage(msgID));
		}
	}

	// 精灵魔法属性使用者属性一致？（对处、对应消去下)
	private boolean isAttrAgrees() {
		int magicattr = _skill.getAttr();
		if (_user instanceof L1NpcInstance) { // NPC使场合OK
			return true;
		}

		if ((_skill.getSkillLevel() >= 17 && magicattr != 0) // 精灵魔法、无属性魔法、
				&& (magicattr != _player.getElfAttr() // 使用者魔法属性一致。
				&& !_player.isGm())) { // GM例外
			return false;
		}
		return true;
	}

	/**
	 * 使用必要HP返。
	 * 
	 * @return HP十分true
	 */
	/*
	 * private boolean isEnoughHp() { return false; }
	 */

	/**
	 * 使用必要MP返。
	 * 
	 * @return MP十分true
	 */
	/*
	 * private boolean isEnoughMp() { return false; }
	 */

	// 必要ＨＰ、ＭＰ？
	private boolean isHPMPConsume() {
		_mpConsume = _skill.getMpConsume();
		_hpConsume = _skill.getHpConsume();
		int currentMp = 0;
		int currentHp = 0;

		if (_user instanceof L1NpcInstance) {
			currentMp = _npc.getCurrentMp();
			currentHp = _npc.getCurrentHp();
		} else {
			currentMp = _player.getCurrentMp();
			currentHp = _player.getCurrentHp();

			// MPINT轻减
			if (_player.getInt() > 12 && _skillId > 8 && _skillId <= 80) { // LV2以上
				_mpConsume--;
			}
			if (_player.getInt() > 13 && _skillId > 16 && _skillId <= 80) { // LV3以上
				_mpConsume--;
			}
			if (_player.getInt() > 14 && _skillId > 24 && _skillId <= 80) { // LV4以上
				_mpConsume--;
			}
			if (_player.getInt() > 15 && _skillId > 32 && _skillId <= 80) { // LV5以上
				_mpConsume--;
			}
			if (_player.getInt() > 16 && _skillId > 40 && _skillId <= 80) { // LV6以上
				_mpConsume--;
			}
			if (_player.getInt() > 17 && _skillId > 48 && _skillId <= 80) { // LV7以上
				_mpConsume--;
			}
			if (_player.getInt() > 18 && _skillId > 56 && _skillId <= 80) { // LV8以上
				_mpConsume--;
			}

			// MP备减
			if (_skillId == PHYSICAL_ENCHANT_DEX
					&& _player.getInventory().checkEquipped(20013)) { // 迅速备中PE:DEX
				_mpConsume /= 2;
			}
			if (_skillId == HASTE
					&& _player.getInventory().checkEquipped(20013)) { // 迅速备中
				_mpConsume /= 2;
			}
			if (_skillId == HEAL && _player.getInventory().checkEquipped(20014)) { // 治愈备中
				_mpConsume /= 2;
			}
			if (_skillId == EXTRA_HEAL
					&& _player.getInventory().checkEquipped(20014)) { // 治愈备中
				_mpConsume /= 2;
			}
			if (_skillId == ENCHANT_WEAPON
					&& _player.getInventory().checkEquipped(20015)) { // 力备中
				_mpConsume /= 2;
			}
			if (_skillId == DETECTION
					&& _player.getInventory().checkEquipped(20015)) { // 力备中
				_mpConsume /= 2;
			}
			if (_skillId == PHYSICAL_ENCHANT_STR
					&& _player.getInventory().checkEquipped(20015)) { // 力备中PE:STR
				_mpConsume /= 2;
			}
			if (_skillId == HASTE
					&& _player.getInventory().checkEquipped(20008)) { // 备中
				_mpConsume /= 2;
			}
			if (_skillId == GREATER_HASTE
					&& _player.getInventory().checkEquipped(20023)) { // 备中
				_mpConsume /= 2;
			}

			if (0 < _skill.getMpConsume()) { // MP消费
				_mpConsume = Math.max(_mpConsume, 1); // 最低1消费。
			}
		}

		if (currentHp < _hpConsume + 1) {
			if (_user instanceof L1PcInstance) {
				_player.sendPackets(new S_ServerMessage(279));
			}
			return false;
		} else if (currentMp < _mpConsume) {
			if (_user instanceof L1PcInstance) {
				_player.sendPackets(new S_ServerMessage(278));
			}
			return false;
		}

		return true;
	}

	// 必要材料？
	private boolean isItemConsume() {

		int itemConsume = _skill.getItemConsumeId();
		int itemConsumeCount = _skill.getItemConsumeCount();

		if (itemConsume == 0) {
			return true; // 材料必要魔法
		}

		if (!_player.getInventory().checkItem(itemConsume, itemConsumeCount)) {
			return false; // 必要材料足。
		}

		return true;
	}

	// 使用材料、HPMP、Lawful。
	private void useConsume() {
		if (_user instanceof L1NpcInstance) {
			// NPC场合、HP、MP
			int current_hp = _npc.getCurrentHp() - _hpConsume;
			_npc.setCurrentHp(current_hp);

			int current_mp = _npc.getCurrentMp() - _mpConsume;
			_npc.setCurrentMp(current_mp);
			return;
		}

		// HPMP
		if (isHPMPConsume()) {
			if (_skillId == FINAL_BURN) { //  
				_player.setCurrentHp(1);
				_player.setCurrentMp(0);
			} else {
				int current_hp = _player.getCurrentHp() - _hpConsume;
				_player.setCurrentHp(current_hp);

				int current_mp = _player.getCurrentMp() - _mpConsume;
				_player.setCurrentMp(current_mp);
			}
		}

		// Lawful
		int lawful = _player.getLawful() + _skill.getLawful();
		if (lawful > 32767) {
			lawful = 32767;
		}
		if (lawful < -32767) {
			lawful = -32767;
		}
		_player.setLawful(lawful);

		int itemConsume = _skill.getItemConsumeId();
		int itemConsumeCount = _skill.getItemConsumeCount();

		if (itemConsume == 0) {
			return; // 材料必要魔法
		}

		// 使用材料
		_player.getInventory().consumeItem(itemConsume, itemConsumeCount);
	}

	// 追加。
	private void addMagicList(L1Character cha, boolean repetition) {
		if (_skillTime == 0) {
			_getBuffDuration = _skill.getBuffDuration() * 1000; // 效果时间
			if (_skill.getBuffDuration() == 0) {
				if (_skillId == INVISIBILITY) { // 
					cha.setSkillEffect(INVISIBILITY, 0);
				}
				return;
			}
		} else {
			_getBuffDuration = _skillTime * 1000; // time0以外、效果时间设定
		}

		if (_skillId == SHOCK_STUN) {
			_getBuffDuration = _shockStunDuration;
		}
		/*
		 * if (_skillId == EARTH_BIND) { Random random = new Random(); int[]
		 * stunTimeArray = { 10000, 11000, 12000, 13000, 14000, 15000, 16000};
		 * int rnd = random.nextInt(stunTimeArray.length); _earthBindDuration =
		 * stunTimeArray[rnd]; _getBuffDuration = _earthBindDuration; }
		 */

		if (_skillId == CURSE_PARALYZE) { // 效果处理L1CurseParalysis移让。
			return;
		}
		if (_skillId == ICE_LANCE && !_isFreeze) { // 冻结失败
			return;
		}
		if (_skillId != SHAPE_CHANGE) {
			cha.setSkillEffect(_skillId, _getBuffDuration);
		}

		if (cha instanceof L1PcInstance && repetition) { // 对像PC既
			L1PcInstance pc = (L1PcInstance) cha;
			sendIcon(pc);
		}
	}

	// 送信
	private void sendIcon(L1PcInstance player) {
		if (_skillTime == 0) {
			_getBuffIconDuration = _skill.getBuffDuration(); // 效果时间
		} else {
			_getBuffIconDuration = _skillTime; // time0以外、效果时间设定
		}

		switch (_skillId)// 变更为switch 1
		{
		case SHIELD: {// 保护罩
			player.sendPackets(new S_SkillIconShield(5, _getBuffIconDuration));
		}
			break;
		case PHYSICAL_ENCHANT_DEX: {// 通畅气脉术
			player.sendPackets(new S_Dexup(player, 5, _getBuffIconDuration));
		}
			break;
		case PHYSICAL_ENCHANT_STR: {// 体魄强健术
			player.sendPackets(new S_Strup(player, 5, _getBuffIconDuration));
		}
			break;
		case HASTE:
		case GREATER_HASTE: {// 加速术,强力加速术
			player.sendPackets(new S_SkillHaste(player.getId(), 1,
					_getBuffIconDuration));
		}
			break;
		case SHADOW_ARMOR: {// 影之防护
			player.sendPackets(new S_SkillIconShield(3, _getBuffIconDuration));
		}
			break;
		case DRESS_MIGHTY: {// 力量提升
			player.sendPackets(new S_Strup(player, 2, _getBuffIconDuration));
		}
			break;
		case DRESS_DEXTERITY: {// 敏捷提升
			player.sendPackets(new S_Dexup(player, 2, _getBuffIconDuration));
		}
			break;
		case GLOWING_AURA: {// 激励士气
			player.sendPackets(new S_SkillIconAura(113, _getBuffIconDuration));
		}
			break;
		case SHINING_AURA: {// 钢铁士气
			player.sendPackets(new S_SkillIconAura(114, _getBuffIconDuration));
		}
			break;
		case BRAVE_AURA: {// 冲击士气
			player.sendPackets(new S_SkillIconAura(116, _getBuffIconDuration));
		}
			break;
		case FIRE_WEAPON: {// 火焰武器
			player.sendPackets(new S_SkillIconAura(147, _getBuffIconDuration));
		}
			break;
		case WIND_SHOT: {// 风之神射
			player.sendPackets(new S_SkillIconAura(148, _getBuffIconDuration));
		}
			break;
		case EARTH_SKIN: {// 大地防护
			player.sendPackets(new S_SkillIconShield(6, _getBuffIconDuration));
		}
			break;
		case FIRE_BLESS: {// 烈炎气息
			player.sendPackets(new S_SkillIconAura(154, _getBuffIconDuration));
		}
			break;
		case STORM_EYE: {// 暴风之眼
			player.sendPackets(new S_SkillIconAura(155, _getBuffIconDuration));
		}
			break;
		case EARTH_BLESS: {// 大地的祝福
			player.sendPackets(new S_SkillIconShield(7, _getBuffIconDuration));
		}
			break;
		case BURNING_WEAPON: {// 烈炎武器
			player.sendPackets(new S_SkillIconAura(162, _getBuffIconDuration));
		}
			break;
		case STORM_SHOT: {// 暴风神射
			player.sendPackets(new S_SkillIconAura(165, _getBuffIconDuration));
		}
			break;
		case IRON_SKIN: {// 钢铁防护
			player.sendPackets(new S_SkillIconShield(10, _getBuffIconDuration));
		}
			break;
		case HOLY_WALK:
		case MOVING_ACCELERATION:
		case WIND_WALK: { // 神圣疾走,行走加速,风之疾走
			player.setBraveSpeed(4);
			player.sendPackets(new S_SkillBrave(player.getId(), 4,
					_getBuffIconDuration));
		}
			break;
		case BLOODLUST: // 血之渴望1
			player.setBraveSpeed(1);
			player.sendPackets(new S_SkillBrave(player.getId(), 1,
					_getBuffIconDuration));
			break;

		case SLOW:
		case MASS_SLOW:
		case ENTANGLE: {// 缓速术,集体缓速术,地面障碍
			player.sendPackets(new S_SkillHaste(player.getId(), 2, 64));
		}
			break;
		case IMMUNE_TO_HARM: {// 68圣结界
			player.sendPackets(new S_PacketBox(S_PacketBox.ICON_I2H,
					_getBuffIconDuration));
		}
			break;
		}// 变更为switch 1 end
		player.sendPackets(new S_OwnCharStatus(player));
	}

	// 送信
	private void sendGrfx(boolean isSkillAction, int actid) {
		// 加入 怪物施法动作设定
		if (actid == 0) {
			actid = _skill.getActid();
		}
		// 加入 怪物施法动作设定 end
		int castgfx = _skill.getCastGfx();
		if (castgfx == 0) {
			return; // 表示无
		}
		if (_calcType == PC_PC || _calcType == PC_NPC) {
			// _user instanceof L1PcInstance 改成 _calcType == PC_PC || _calcType
			// == PC_NPC
			int targetid = _target.getId();

			if (_skillId == SHOCK_STUN) {
				if (_targetList.size() == 0) { // 失败
					return;
				} else {
					if (_target instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) _target;
						pc.sendPackets(new S_SkillSound(pc.getId(), 4434));
						pc.broadcastPacket(new S_SkillSound(pc.getId(), 4434));
					} else if (_target instanceof L1NpcInstance) {
						_target.broadcastPacket(new S_SkillSound(_target
								.getId(), 4434));
					}
					return;
				}
			}

			// 修正三重矢
			if (_skillId == TRIPLE_ARROW) {
				if (_targetList.size() == 0) { // 失败
					return;
				}
			}
			// 修正三重矢 end

			if (_skillId == LIGHT) {
				L1PcInstance pc = (L1PcInstance) _target;
				pc.sendPackets(new S_Sound(145));
			}

			if (_targetList.size() == 0 && !(_skill.getTarget().equals("none"))) {
				// 数０对像指定场合、魔法使用表示终了
				int actionId;
				if (_skill.getTarget().equals("attack") && _skillId != 18) {
					actionId = ActionCodes.ACTION_SkillAttack;
				} else {
					actionId = ActionCodes.ACTION_SkillBuff;
				}

				int tempchargfx = _player.getTempCharGfx();
				if (tempchargfx == 5727 || tempchargfx == 5730) { // 系变身对应
					actionId = ActionCodes.ACTION_SkillBuff;
				} else if (tempchargfx == 5733 || tempchargfx == 5736) {
					actionId = ActionCodes.ACTION_Attack;
				}
				// 魔法使动作使用者
				if (isSkillAction) {
					S_DoActionGFX gfx = new S_DoActionGFX(_player.getId(),
							actionId);
					_player.sendPackets(gfx);
					_player.broadcastPacket(gfx);
				}
				return;
			}

			if (_skill.getTarget().equals("attack") && _skillId != 18) {
				if (_calcType == PC_PC // 对像PC
						|| (_calcType == PC_NPC && // 对像
						_target instanceof L1PetInstance)
						|| (_calcType == PC_NPC && // 对像
						_target instanceof L1SummonInstance)) {
					if (_player.getZoneType() == 1
							|| _target.getZoneType() == 1 // 攻击侧攻击侧
							|| _player.checkNonPvP(_player, _target)) { // Non-PvP设定
						_player.sendPacketsAll(new S_UseAttackSkill(_player, 0,
								castgfx, _targetX, _targetY, actid, 0)); // 
						// new S_UseAttackSkill(_player, 0,
						// castgfx, _targetX, _targetY)
						/*
						 * _player.broadcastPacket(new S_UseAttackSkill(_player,
						 * 0, castgfx, _targetX, _targetY));
						 */
						return;
					}
				}
				if (_skill.getArea() == 0) { // 单体攻击魔法
					_player.sendPacketsAll(new S_UseAttackSkill(_player,
							targetid, castgfx, _targetX, _targetY, actid, 0));
					if (_target != null) {
						if (_target instanceof L1PcInstance) {
							if (((L1PcInstance) _target).isCheckFZ()) {
								WriteLogTxt.Recording(_target.getName()
										+ "被技能打",
										"变身ID"
												+ _target.getTempCharGfx()
												+ " 武器"
												+ ((L1PcInstance) _target)
														.getWeapon()
														.getLogViewName()
												+ "被玩家 " + _player.getName()
												+ " 单体魔法打");
							}
						}
					}
					/*
					 * _player.broadcastPacket(new S_UseAttackSkill(_player,
					 * targetid, castgfx, _targetX, _targetY, actid));
					 * _target.broadcastPacketExceptTargetSight(new
					 * S_DoActionGFX( targetid, ActionCodes.ACTION_Damage),
					 * _player);
					 */
				} else { // 有方向范围攻击魔法
					L1Character[] cha = new L1Character[_targetList.size()];
					int i = 0;
					for (TargetStatus ts : _targetList) {
						cha[i] = ts.getTarget();
						i++;
					}
					_player.sendPacketsAll(new S_RangeSkill(_player, cha,
							castgfx, actid, S_RangeSkill.TYPE_DIR));
					/*
					 * _player.broadcastPacket(new S_RangeSkill(_player, cha,
					 * castgfx, actid, S_RangeSkill.TYPE_DIR));
					 */
				}
				/*
				 * _player.sendPackets(new S_UseAttackSkill(_player, targetid,
				 * castgfx, _targetX, _targetY)); _player.broadcastPacket(new
				 * S_UseAttackSkill(_player, targetid, castgfx, _targetX,
				 * _targetY));
				 */
				/*
				 * _target.broadcastPacketExceptTargetSight(new S_DoActionGFX(
				 * targetid, ActionCodes.ACTION_Damage), _player);
				 */

				// 攻击对像全员送、实装
			} else if (_skill.getTarget().equals("none")
					&& (_skill.getType() == L1Skills.TYPE_ATTACK)) { // 無方向範囲攻撃魔法
				L1Character[] cha = new L1Character[_targetList.size()];
				int i = 0;
				for (TargetStatus ts : _targetList) {
					cha[i] = ts.getTarget();
					// cha[i].broadcastPacketExceptTargetSight(new
					// S_DoActionGFX(cha[i].getId(), ActionCodes.ACTION_Damage),
					// _player);
					i++;
				}
				_player.sendPacketsAll(new S_RangeSkill(_player, cha, castgfx,
						actid, S_RangeSkill.TYPE_NODIR));
				// _player.broadcastPacket(new S_RangeSkill(_player, cha,
				// castgfx, actid, S_RangeSkill.TYPE_NODIR));
			} else {
				if (_skillId != 5 && _skillId != 69 && _skillId != 131) { // 、、
					// 
					// 以外
					// 魔法徽章施法无动作
					if ((_skill.getType() == 2 || _skill.getType() == 16 || _skill
							.getType() == 128)
							&& (_skill.getTarget().equals("none") || _skill
									.getTarget().equals("buff"))
							&& _player.getInventory().consumeItem(
									l1j.william.New_Id.Item_AJ_21, 1)) {
						_player.sendPacketsAll(new S_SkillSound(targetid,
								_skill.getCastGfx()));
						/*
						 * _player.broadcastPacket(new S_SkillSound(targetid,
						 * _skill.getCastGfx()));
						 */
						return;
					}
					// 魔法徽章施法无动作 end
					if (isSkillAction) {
						int actionCode;
						if (_skill.getType() == L1Skills.TYPE_ATTACK) {
							actionCode = ActionCodes.ACTION_SkillAttack;
						} else {
							actionCode = ActionCodes.ACTION_SkillBuff;
						}
						S_DoActionGFX gfx = new S_DoActionGFX(_player.getId(),
								actionCode);
						_player.sendPackets(gfx);
						_player.broadcastPacket(gfx);
					}
					if (_skillId == COUNTER_MAGIC
							|| _skillId == COUNTER_BARRIER) {
						_player.sendPackets(new S_SkillSound(targetid, _skill
								.getCastGfx()));
					} else if (_skillId == TRUE_TARGET) { // 个别处理送信济
						return;
					} else {
						// System.out.println("释放魔法画面");
						_player.sendPackets(new S_SkillSound(targetid, _skill
								.getCastGfx()));
						_player.broadcastPacket(new S_SkillSound(targetid,
								_skill.getCastGfx()));
					}
				}

				// 表示全员、必要性、送信
				for (TargetStatus ts : _targetList) {
					L1Character cha = ts.getTarget();

					/*
					 * if(_skillid != 5 && _skillid != 69 && _skillid != 131) //
					 * 、、  以外 { _player.sendPackets(new
					 * S_SkillSound(targetid, _l1skills.getCastGfx()));
					 * _player.broadcastPacket(new S_SkillSound(targetid,
					 * _l1skills.getCastGfx())); }
					 */
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_OwnCharStatus(pc));
					}
				}
			}
		} else if (_user instanceof L1NpcInstance) { // NPC使场合
			int targetid = _target.getId();
			if (_user instanceof L1MerchantInstance) {
				// System.out.println("施法类型1");
				_user.broadcastPacket(new S_SkillSound(targetid, _skill
						.getCastGfx()));
				return;
			}

			if (_targetList.size() == 0 && !(_skill.getTarget().equals("none"))) {
				// 数０对像指定场合、魔法使用表示终了
				int actionId;
				if (_skill.getTarget().equals("attack") && _skillId != 18) {
					actionId = ActionCodes.ACTION_SkillAttack;
				} else {
					actionId = ActionCodes.ACTION_SkillBuff;
				}
				// System.out.println("施法类型2");
				// 魔法使动作使用者
				S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(), actionId);
				_user.broadcastPacket(gfx);
				return;
			}

			if (_skill.getTarget().equals("attack") && _skillId != 18) {
				if (_skill.getArea() == 0) { // 单体攻击魔法
					_user.broadcastPacket(new S_UseAttackSkill(_user, targetid,
							castgfx, _targetX, _targetY, actid, 0));
					if (_target != null) {
						if (_target instanceof L1PcInstance) {
							if (((L1PcInstance) _target).isCheckFZ()) {
								WriteLogTxt.Recording(
										_target.getName() + "被技能打",
										"变身ID"
												+ _target.getTempCharGfx()
												+ " 武器"
												+ ((L1PcInstance) _target)
														.getWeapon()
														.getLogViewName()
												+ "被非玩家 " + _user.getName()
												+ " 单体魔法 "
												+ _skill.getSkillId() + " 打");
							}
						}
					}
				} else { // 有方向范围攻击魔法
					L1Character[] cha = new L1Character[_targetList.size()];
					int i = 0;
					for (TargetStatus ts : _targetList) {
						cha[i] = ts.getTarget();
						i++;
					}
					_user.broadcastPacket(new S_RangeSkill(_user, cha, castgfx,
							actid, S_RangeSkill.TYPE_DIR));
				}
			} else if (_skill.getTarget().equals("none")
					&& (_skill.getType() == L1Skills.TYPE_ATTACK)) { // 無方向範囲攻撃魔法
				L1Character[] cha = new L1Character[_targetList.size()];
				int i = 0;
				for (TargetStatus ts : _targetList) {
					cha[i] = ts.getTarget();
					i++;
				}
				_user.broadcastPacket(new S_RangeSkill(_user, cha, castgfx,
						actid, S_RangeSkill.TYPE_NODIR));
			} else {
				// 、、以外
				if (_skillId != 5 && _skillId != 69 && _skillId != 131) {
					// S_DoActionGFX gfx = new S_DoActionGFX(_user.getId(),
					// actid);
					_user.broadcastPacket(new S_DoActionGFX(_user.getId(),
							actid));
					// _user.broadcastPacket(new S_AttackMissPacket(_user,
					// targetid, actid));
					_user.broadcastPacket(new S_SkillSound(targetid, _skill
							.getCastGfx()));
					// 变更施法动作 end
					// 删除_user.broadcastPacket(new S_SkillSound(targetid, _skill
					// 删除 .getCastGfx()));
					/*
					 * switch(_skillId) { case 12027: //炎魔的分身-大地崩裂术 { try {
					 * Thread.sleep(1050);//延迟显示效果 } catch (InterruptedException
					 * e) { } _user.broadcastPacket(new S_SkillSound(targetid,
					 * _skill .getCastGfx())); } break; case 12035: //迪哥-震裂术 {
					 * try { Thread.sleep(750);//延迟显示效果 } catch
					 * (InterruptedException e) { } _user.broadcastPacket(new
					 * S_SkillSound(targetid, _skill .getCastGfx())); } break;
					 * case 12065: //魂骑士-大地崩裂术 { _user.broadcastPacket(new
					 * S_SkillSound(targetid, _skill .getCastGfx())); } break;
					 * default: { try { Thread.sleep(500);//延迟500毫秒显示效果 } catch
					 * (InterruptedException e) { } _user.broadcastPacket(new
					 * S_SkillSound(targetid, _skill .getCastGfx())); } break; }
					 */
				}
			}
		}
	}

	/**
	 * 不会被相消的技能 
	 * 
	 * QQ：1043567675 by：亮修改 2020年5月1日 下午3:46:40
	 */
	private void deleteRepeatedSkills(L1Character cha) {
		final int[][] repeatedSkills = {
				//  、 、 ,  
				{ HOLY_WEAPON, ENCHANT_WEAPON, BLESS_WEAPON, SHADOW_FANG },
				//  、 、 、 、 、 
				{ FIRE_WEAPON, WIND_SHOT, FIRE_BLESS, STORM_EYE,
						BURNING_WEAPON, STORM_SHOT },
				// 、 、 、、 
				{ SHIELD, SHADOW_ARMOR, EARTH_SKIN, EARTH_BLESS, IRON_SKIN },
				//  、 、 、BP、WIZP
				{ HOLY_WALK, MOVING_ACCELERATION, WIND_WALK, STATUS_BRAVE,
						STATUS_ELFBRAVE },
				// 、 、GP
				{ HASTE, GREATER_HASTE, STATUS_HASTE },
				//  ：DEX、 
				{ PHYSICAL_ENCHANT_DEX, DRESS_DEXTERITY, 1014 },// 补上能力水
				//  ：STR、 
				{ PHYSICAL_ENCHANT_STR, DRESS_MIGHTY, 1013 },// 补上激励
				// 、
				{ GLOWING_AURA, SHINING_AURA } };

		for (int[] skills : repeatedSkills) {
			for (int id : skills) {
				if (id == _skillId) {
					stopSkillList(cha, skills);
				}
			}
		}
	}

	// 重复一旦削除
	private void stopSkillList(L1Character cha, int[] repeat_skill) {
		for (int skillId : repeat_skill) {
			if (skillId != _skillId) {
				cha.removeSkillEffect(skillId);
			}
		}
	}

	// 设定
	private void setDelay() {
		if (_skill.getReuseDelay() > 0) {
			L1SkillDelay.onSkillUse(_user, _skill.getReuseDelay());
		}
	}

	private void runSkill() {

		// 治愈能量风暴
		if (_skillId == LIFE_STREAM) {
			_player.setHeading(_user.targetDirection(_targetX, _targetY));
			_player.sendPackets(new S_DoActionGFX(_player.getId(),
					ActionCodes.ACTION_SkillBuff));
			_player.broadcastPacket(new S_DoActionGFX(_player.getId(),
					ActionCodes.ACTION_SkillBuff));

			int polyId = _player.getTempCharGfx();
			L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);

			// MP < 20
			if (_player.getCurrentMp() < 20) {
				_player.sendPackets(new S_ServerMessage(278)); // 278 :
																// \f1因魔力不足而无法使用魔法。
				return;
			}

			// 消耗魔宝
			if (isItemConsume() == false && !_player.isGm()) { // 消费
				_player.sendPackets(new S_ServerMessage(299)); // 299 :
																// \f1施放魔法所需材料不足。
				return;
			}
			// 隐身状态
			if (_player.isInvisble()) {
				return;
			}
			// 超重
			if (_player.getInventory().getWeight240() >= 197) {
				_player.sendPackets(new S_ServerMessage(316)); // 316 :
																// \f1你携带太多物品，因此无法使用法术。
				return;
			}
			// 无法施法的变身
			if (poly != null && !poly.canUseSkill()) {
				_player.sendPackets(new S_ServerMessage(285)); // 285 :
																// \f1在此状态下无法使用魔法。
				return;
			}
			// 技能延迟
			if (_player.isSkillDelay()) {
				return;
			}
			// 各种禁言状态
			if ((_player.hasSkillEffect(SILENCE)
					|| _player.hasSkillEffect(AREA_OF_SILENCE) || _player
						.hasSkillEffect(STATUS_POISON_SILENCE))
					&& _skillId != SHOCK_STUN
					&& _skillId != REDUCTION_ARMOR
					&& _skillId != BOUNCE_ATTACK
					&& _skillId != SOLID_CARRIAGE
					&& _skillId != COUNTER_BARRIER) { // 修正骑士技能被魔封仍可以施放
				_player.sendPackets(new S_ServerMessage(285));
				// 285 : \f1在此状态下无法使用魔法。
				return;
			}
			L1EffectSpawn.getInstance().spawnEffect(81169,
					_skill.getBuffDuration() * 1000, _targetX, _targetY,
					_user.getMapId());
			return;
		}
		// 治愈能量风暴 END

		// 火牢伤害
		if (_skillId == FIRE_WALL) { // 
			_player.setHeading(_user.targetDirection(_targetX, _targetY));
			_player.sendPackets(new S_DoActionGFX(_player.getId(),
					ActionCodes.ACTION_SkillBuff));
			_player.broadcastPacket(new S_DoActionGFX(_player.getId(),
					ActionCodes.ACTION_SkillBuff));

			if (_player.isInvisble() && !isInvisUsableSkill()) { // 中使用不可
				return;
			}
			if (_player.getCurrentMp() < 60) {
				_player.sendPackets(new S_ServerMessage(278)); // 278 :
																// \f1因魔力不足而无法使用魔法。
				return;
			}
			if (_player.getInventory().getWeight240() >= 197) { // 重量使用
				_player.sendPackets(new S_ServerMessage(316));
				return;
			}
			int polyId = _player.getTempCharGfx();
			L1PolyMorph poly = PolyTable.getInstance().getTemplate(polyId);
			// 魔法使变身
			if (poly != null && !poly.canUseSkill()) {
				_player.sendPackets(new S_ServerMessage(285)); // \f1状态魔法使。
				return;
			}
			// 中使用不可
			if (_player.isSkillDelay()) {
				return;
			}
			// 状态使用不可
			if ((_player.hasSkillEffect(SILENCE)
					|| _player.hasSkillEffect(AREA_OF_SILENCE) || _player
						.hasSkillEffect(STATUS_POISON_SILENCE))
					&& _skillId != SHOCK_STUN
					&& _skillId != REDUCTION_ARMOR
					&& _skillId != BOUNCE_ATTACK
					&& _skillId != SOLID_CARRIAGE
					&& _skillId != COUNTER_BARRIER) { // 修正骑士技能被魔封仍可以施放
				_player.sendPackets(new S_ServerMessage(285)); // \f1状态魔法使。
				return;
			}

			L1EffectSpawn.getInstance().doSpawnFireWall(_user, _targetX,
					_targetY);
			return;
		}
		// 火牢伤害 END

		/**
		 * 有使用次数限制的技能使用成功与否的判断
		 */
		boolean isCounterMagic = true;
		for (int skillId : EXCEPT_COUNTER_MAGIC) {
			if (_skillId == skillId) {
				isCounterMagic = false; // 无效
				break;
			}
		}

		// NPC使用onActionNullPointerException发生
		// PC使用时
		if (_skillId == SHOCK_STUN) {
			_target.onAction(_player);
		}

		// 修正三重矢 end
		if (_skillId == TRIPLE_ARROW) {
			for (int i = 3; i > 0; i--) {
				_target.onAction(_player);
			}
		}
		// 修正三重矢 end

		if (_skill.getTarget().equals("attack") && _skillId != 18) { // 攻击魔法
			if (_calcType == PC_PC || // 对像PC
					(_calcType == PC_NPC && _target instanceof L1PetInstance) || // 对像
					(_calcType == PC_NPC && _target instanceof L1SummonInstance)) { // 对像
				if (_target.getZoneType() == 1) { // 攻击侧
					return;
				}
				if (_player.checkNonPvP(_player, _target)) { // Non-PvP设定
					return;
				}
			}
		}

		try {
			TargetStatus ts = null;
			L1Character cha = null;
			int dmg = 0;
			int drainMana = 0;
			int heal = 0;
			boolean isSuccess = false;
			int undeadType = 0;

			for (Iterator<TargetStatus> iter = _targetList.iterator(); iter
					.hasNext();) {
				ts = null;
				cha = null;
				dmg = 0;
				heal = 0;
				isSuccess = false;
				undeadType = 0;

				ts = iter.next();
				cha = ts.getTarget();

				if (!ts.isCalc() || !isTargetCalc(cha)) {
					continue; // 计算必要。
				}

				L1Magic _magic = new L1Magic(_user, cha);
				_magic.setLeverage(getLeverage());

				if (cha instanceof L1MonsterInstance) { // 判定
					undeadType = ((L1MonsterInstance) cha).getNpcTemplate()
							.get_undead();
				}

				// 确率系失败确定场合
				if ((_skill.getType() == L1Skills.TYPE_CURSE || _skill
						.getType() == L1Skills.TYPE_PROBABILITY)
						&& isTargetFailure(cha)) {
					iter.remove();
					continue;
				}

				// 有效中
				if (isCounterMagic && cha.hasSkillEffect(COUNTER_MAGIC)) {
					cha.removeSkillEffect(COUNTER_MAGIC);
					int castgfx = SkillsTable.getInstance()
							.getTemplate(COUNTER_MAGIC).getCastGfx();
					cha.broadcastPacket(new S_SkillSound(cha.getId(), castgfx));
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SkillSound(pc.getId(), castgfx));
					}
					iter.remove();
					continue;
				}

				if (cha instanceof L1PcInstance) { // PC场合送信。
					if (_skillTime == 0) {
						_getBuffIconDuration = _skill.getBuffDuration(); // 效果时间
					} else {
						_getBuffIconDuration = _skillTime; // time0以外、效果时间设定
					}
				}

				deleteRepeatedSkills(cha); // 重复削除

				if (_skill.getType() == L1Skills.TYPE_ATTACK
						&& _user.getId() != cha.getId()) { // 攻击系＆使用者以外。
					dmg = _magic.calcMagicDamage(_skillId);
					cha.removeSkillEffect(ERASE_MAGIC); // 中、攻击魔法解除
				} else if (_skill.getType() == L1Skills.TYPE_CURSE
						|| _skill.getType() == L1Skills.TYPE_PROBABILITY) { // 确率系
					isSuccess = _magic.calcProbabilityMagic(_skillId);
					if (_skillId != ERASE_MAGIC) {
						cha.removeSkillEffect(ERASE_MAGIC); // 中、确率魔法解除
					}
					/*
					 * 失败场合、削除
					 */
					if (!isSuccess) {
						if (_skillId == FOG_OF_SLEEPING
								&& cha instanceof L1PcInstance) {
							// L1PcInstance pc = (L1PcInstance) cha;
							// 删除pc.sendPackets(new S_ServerMessage(297)); //
							// 轻觉。
						}
						// System.out.println("施咒失败，移除成功");
						iter.remove();
						continue;
					}
				} else if (_skill.getType() == L1Skills.TYPE_HEAL) { // 回复系
					// 回复量表现
					dmg = -1 * _magic.calcHealing(_skillId);
					if (cha.hasSkillEffect(WATER_LIFE)) { // 中回复量２倍
						dmg *= 2;
					}
					if (cha.hasSkillEffect(POLLUTE_WATER)) { // 中回复量1/2倍
						dmg /= 2;
					}
					// 城门无法补血
					if (cha instanceof L1DoorInstance) {
						dmg = 0;
					}
					// 城门无法补血 end
					// 修正警卫补血死亡
					if (cha instanceof L1GuardInstance) {
						L1GuardInstance npc = (L1GuardInstance) cha;
						if (npc.getCurrentHp() == 0) {
							dmg = 0;
						}
					}
					// 修正警卫补血死亡 end
				}

				// ■■■■ 个别处理书。 ■■■■

				// 使用济场合
				// 重出来例外
				if (cha.hasSkillEffect(_skillId) && _skillId != SHOCK_STUN) {
					addMagicList(cha, true); // 魔法效果时间上书
					if (_skillId != SHAPE_CHANGE) { //  变身上书出来例外
						continue;
					}
				}

				// ●●●● PC、NPC两方效果 ●●●●
				switch (_skillId)// 变更为switch 2
				{
				case HASTE: {// 43加速术
					if (cha.getMoveSpeed() != 2) { // 中以外
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							if (pc.getHasteItemEquipped() > 0) {
								continue;
							}
							pc.setDrink(false);
							pc.sendPackets(new S_SkillHaste(pc.getId(), 1,
									_getBuffIconDuration));
						}
						cha.broadcastPacket(new S_SkillHaste(cha.getId(), 1, 0));
						cha.setMoveSpeed(1);
					} else { // 中
						int skillNum = 0;
						if (cha.hasSkillEffect(SLOW)) {
							skillNum = SLOW;
						} else if (cha.hasSkillEffect(MASS_SLOW)) {
							skillNum = MASS_SLOW;
						} else if (cha.hasSkillEffect(ENTANGLE)) {
							skillNum = ENTANGLE;
						}
						if (skillNum != 0) {
							cha.removeSkillEffect(skillNum);
							cha.removeSkillEffect(HASTE);
							cha.setMoveSpeed(0);
							continue;
						}
					}
				}
					break;
				case 14004: {// 阿鲁巴-加速术
					if (cha.getMoveSpeed() != 2) { // 中以外
						cha.broadcastPacket(new S_SkillHaste(cha.getId(), 1, 0));
						cha.setMoveSpeed(1);
						cha.setSkillEffect(43, 600000);
					} else { // 中
						int skillNum = 0;
						if (cha.hasSkillEffect(SLOW)) {
							skillNum = SLOW;
						} else if (cha.hasSkillEffect(MASS_SLOW)) {
							skillNum = MASS_SLOW;
						} else if (cha.hasSkillEffect(ENTANGLE)) {
							skillNum = ENTANGLE;
						}
						if (skillNum != 0) {
							cha.removeSkillEffect(skillNum);
							cha.removeSkillEffect(43);
							cha.setMoveSpeed(0);
							continue;
						}
					}
				}
					break;
				case CURE_POISON: { // 9解毒术
					cha.curePoison();
				}
					break;
				case REMOVE_CURSE: { // 37圣洁之光
					cha.curePoison();
					if (cha.hasSkillEffect(STATUS_CURSE_PARALYZING)
							|| cha.hasSkillEffect(STATUS_CURSE_PARALYZED)) {
						cha.cureParalaysis();
					}
				}
					break;
				case RESURRECTION:
				case GREATER_RESURRECTION: { // 返生术,终极返生术
					// 
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						if (_player.getId() != pc.getId()) {
							if (pc.getCurrentHp() == 0 && pc.isDead()) {
								if (pc.getMap().isUseResurrection()) {
									if (_skillId == RESURRECTION) {
										pc.setGres(false);
									} else if (_skillId == GREATER_RESURRECTION) {
										pc.setGres(true);
									}
									pc.setTempID(_player.getId());
									pc.sendPackets(new S_Message_YN(322, "")); // 复活？（Y/N）
								}
							}
						}
					}
					if (cha instanceof L1NpcInstance) {
						if (!(cha instanceof L1TowerInstance)
								&& !(cha instanceof L1DoorInstance)
								&& !(cha instanceof L1CastleGuardInstance)) {// 补上门
																				// &
																				// 守城警卫的判断
							L1NpcInstance npc = (L1NpcInstance) cha;
							if (npc.getCurrentHp() == 0
									&& npc.isDead()
									&& npc.getNpcTemplate().get_IsErase() == true) { // 可以复活判断
								npc.resurrect(npc.getMaxHp() / 4);
								npc.setResurrect(true);
								// 修正复活后回血回血
								npc.stopHpRegeneration();
								if (npc.getMaxHp() > npc.getCurrentHp()) {
									npc.startHpRegeneration();
								}
								npc.stopMpRegeneration();
								if (npc.getMaxMp() > npc.getCurrentMp()) {
									npc.startMpRegeneration();
								}
								// 修正
							}
						}
					}
				}
					break;
				case CALL_OF_NATURE: { // 生命呼唤
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						if (_player.getId() != pc.getId()) {
							if (pc.getCurrentHp() == 0 && pc.isDead()) {
								pc.setTempID(_player.getId());
								pc.sendPackets(new S_Message_YN(322, "")); // 复活？（Y/N）
							}
						}
					}
					if (cha instanceof L1NpcInstance) {
						if (!(cha instanceof L1TowerInstance)
								&& !(cha instanceof L1DoorInstance)
								&& !(cha instanceof L1CastleGuardInstance)) { // 补上门
																				// &
																				// 守城警卫的判断
							L1NpcInstance npc = (L1NpcInstance) cha;
							if (npc.getCurrentHp() == 0
									&& npc.isDead()
									&& npc.getNpcTemplate().get_IsErase() == true) { // 可以复活判断
								npc.resurrect(cha.getMaxHp());// HP全回复
								npc.resurrect(cha.getMaxMp() / 100);// MP0
								npc.setResurrect(true);
							}
						}
					}
				}
					break;
				case DETECTION: {// 无所遁形术
					if (cha instanceof L1NpcInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						if (npc.getNpcTemplate().get_npcId() != 45682) { // 加入地龙不可无所判断
							int hiddenStatus = npc.getHiddenStatus();
							if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
								npc.appearOnGround(_player);
							}
						}
					}
				}
					break;
				case FREEZING_BREATH: {// 龙骑士 真龙之眼
					if (cha instanceof L1NpcInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						if (npc.getNpcTemplate().get_npcId() != 45682) { // 加入地龙不可无所判断
							int hiddenStatus = npc.getHiddenStatus();
							if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
								npc.appearOnGround(_player);
							}
						}
					}
				}
					break;
				case COUNTER_DETECTION: {// 强力无所遁形术
					if (cha instanceof L1PcInstance) {
						if (cha.isInvisble()) {
							dmg = _magic.calcMagicDamage(_skillId);
						} else {
							dmg = 0;
						}
					} else if (cha instanceof L1NpcInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						int hiddenStatus = npc.getHiddenStatus();
						if (hiddenStatus == L1NpcInstance.HIDDEN_STATUS_SINK) {
							npc.appearOnGround(_player);
							dmg = _magic.calcMagicDamage(_skillId);
						} else {
							dmg = 0;
						}
						// 加入地龙不可无所判断
						/*
						 * if (npc.getNpcTemplate().get_npcId() != 45682) {
						 * 
						 * } else { dmg = 0; }
						 */
						// 加入地龙不可无所判断 end
					} else {
						dmg = 0;
					}
				}
					break;

				// case BLOODLUST:
				// if (cha instanceof L1PcInstance) {
				// final L1PcInstance pc = (L1PcInstance) cha;
				// pc.setBraveSpeed(6);
				// pc.sendPackets(new S_SkillBrave(pc.getId(), 6,
				// _getBuffIconDuration));
				// pc.broadcastPacket(new S_SkillBrave(pc.getId(), 6, 0));
				// }
				// break;

				case TRUE_TARGET: {// 113精准目标
					if (_user instanceof L1PcInstance) {
						L1PcInstance pri = (L1PcInstance) _user;
						pri.sendPackets(new S_TrueTarget(_targetID,
								pri.getId(), _message));
						L1Clan clan = L1World.getInstance().getClan(
								pri.getClanname());
						if (clan != null) {
							L1PcInstance players[] = clan.getOnlineClanMember();
							for (L1PcInstance pc : players) {
								pc.sendPackets(new S_TrueTarget(_targetID, pc
										.getId(), _message));
							}
						}
					}
				}
					break;
				// ★★★ 回复系 ★★★
				case HEAL:
				case EXTRA_HEAL:
				case GREATER_HEAL:
				case FULL_HEAL:
				case HEAL_ALL:
				case NATURES_TOUCH:
				case NATURES_BLESSING: {
					if (_user instanceof L1PcInstance) {
						cha.removeSkillEffect(WATER_LIFE);
					}
				}
					break;
				case CHILL_TOUCH:
				case VAMPIRIC_TOUCH: { // 寒冷颤栗,吸血鬼之吻
					heal = dmg;
				}
					break;
				case 11003:
				case 11004:
				case 12004:
				case 12084: {
					// 怪物的吸血鬼之吻效果
					_user.setCurrentHp(_user.getCurrentHp() + (dmg / 2));
				}
					break;
				case 12120:
				case 12123:
				case 12128:
				case 12131: {
					// 四大龙安息
					String s = cha.getName();
					if (_calcType == NPC_PC || _calcType == NPC_NPC) {
						_user.broadcastPacket(new S_NpcChatPacket(_npc, s + " "
								+ "$3717", 2));
					} else {
						_player.broadcastPacket(new S_ChatPacket(_player,
								"$3717", 0, 0));
					}
					if (!cha.hasSkillEffect(50) && !cha.hasSkillEffect(78)
							&& !cha.hasSkillEffect(80)
							&& !cha.hasSkillEffect(157)
							&& cha.get_poisonStatus4() != 4
							&& cha.get_poisonStatus6() != 4) { // 判断是否为冰冻或绝屏状态
						dmg = cha.getCurrentHp();
					} else {
						dmg = 0;
					}
				}
					break;
				case 18001:
				case 18002:
				case 18003:
				case 18004: {
					// 四大龙安息
					if (!cha.hasSkillEffect(50) && !cha.hasSkillEffect(78)
							&& !cha.hasSkillEffect(80)
							&& !cha.hasSkillEffect(157)
							&& cha.get_poisonStatus4() != 4
							&& cha.get_poisonStatus6() != 4) { // 判断是否为冰冻或绝屏状态
						dmg = 150 + (_user.getMagicDmg() * 3);
					} else {
						dmg = 0;
					}
				}
					break;
				case 12100: { // 水精灵王(傲塔)-水柱
					_user.broadcastPacket(new S_SkillSound(cha.getId(), 2739));
				}
					break;
				case 12048:
				case 12087: {
					// 库曼-冲击之晕,混沌的司祭-冲击之晕
					if (cha.get_poisonStatus2() != 4 && !cha.hasSkillEffect(50)
							&& !cha.hasSkillEffect(78)
							&& !cha.hasSkillEffect(80)
							&& !cha.hasSkillEffect(157)
							&& cha.get_poisonStatus4() != 4
							&& cha.get_poisonStatus6() != 4) {
						int[] stunTimeArray = { 1500, 2000, 2500, 3000 };
						Random random = new Random();
						int rnd = random.nextInt(stunTimeArray.length);
						_shockStunDuration = stunTimeArray[rnd];
						L1Poison2 poison = new L1Poison2();
						boolean success = poison.handleCommands((L1Object) cha,
								4, _shockStunDuration, 0);
						if (success == true) {
							cha.add_poison2(poison);
							L1EffectSpawn.getInstance().spawnEffect(81162,
									_shockStunDuration, cha.getX(), cha.getY(),
									cha.getMapId());
						}
					}
				}
					break;
				case 12114: {
					// 死亡-闇黑波动
					Random random = new Random();
					int chance = random.nextInt(100);
					if (chance < 25 && cha.get_poisonStatus2() != 4
							&& !cha.hasSkillEffect(50)
							&& !cha.hasSkillEffect(78)
							&& !cha.hasSkillEffect(80)
							&& !cha.hasSkillEffect(157)
							&& cha.get_poisonStatus4() != 4
							&& cha.get_poisonStatus6() != 4) {
						int[] stunTimeArray = { 1500, 2000, 2500, 3000 };
						int rnd = random.nextInt(stunTimeArray.length);
						_shockStunDuration = stunTimeArray[rnd];
						L1Poison2 poison = new L1Poison2();
						boolean success = poison.handleCommands((L1Object) cha,
								4, _shockStunDuration, 0);
						if (success == true) {
							cha.add_poison2(poison);
							L1EffectSpawn.getInstance().spawnEffect(81162,
									_shockStunDuration, cha.getX(), cha.getY(),
									cha.getMapId());
						}
					}
				}
					break;
				case 12036: {
					// 地之牙-束缚术
					boolean isUSE = false;
					isUSE = _magic.calcProbabilityMagic(_skillId);
					if (isUSE && !cha.hasSkillEffect(50)
							&& !cha.hasSkillEffect(78)
							&& !cha.hasSkillEffect(80)
							&& !cha.hasSkillEffect(157)
							&& cha.get_poisonStatus3() != 4
							&& cha.get_poisonStatus4() != 4
							&& cha.get_poisonStatus6() != 4) {
						_user.broadcastPacket(new S_SkillSound(cha.getId(),
								4184));
						L1Poison3 poison = new L1Poison3();
						boolean success = poison.handleCommands((L1Object) cha,
								4, 16000, 0);
						if (success == true) {
							cha.add_poison3(poison);
							L1EffectSpawn.getInstance().spawnEffect(90001,
									16000, cha.getX(), cha.getY(),
									cha.getMapId());
						}
					}
				}
					break;
				case 13008: { // 亚力安-冰冻光线
					boolean isUSE = false;
					isUSE = _magic.calcProbabilityMagic(_skillId);
					if (isUSE && !cha.hasSkillEffect(50)
							&& !cha.hasSkillEffect(78)
							&& !cha.hasSkillEffect(80)
							&& !cha.hasSkillEffect(157)
							&& cha.get_poisonStatus4() != 4
							&& cha.get_poisonStatus6() != 4) {
						L1Poison4 poison = new L1Poison4();
						boolean success = poison.handleCommands((L1Object) cha,
								4, 16000, 0);
						if (success == true) {
							cha.add_poison4(poison);
							L1EffectSpawn.getInstance().spawnEffect(81168,
									16000, cha.getX(), cha.getY(),
									cha.getMapId());
						}
					}
				}
					break;
				case 12031: { // 邪恶蜥蜴-寒冰喷吐
					boolean isUSE = false;
					isUSE = _magic.calcProbabilityMagic(_skillId);
					if (isUSE && !cha.hasSkillEffect(50)
							&& !cha.hasSkillEffect(78)
							&& !cha.hasSkillEffect(80)
							&& !cha.hasSkillEffect(157)
							&& cha.get_poisonStatus4() != 4
							&& cha.get_poisonStatus6() != 4) {
						L1Poison4 poison = new L1Poison4();
						boolean success = poison.handleCommands((L1Object) cha,
								4, 16000, 0);
						if (success == true) {
							cha.add_poison4(poison);
							L1EffectSpawn.getInstance().spawnEffect(81168,
									16000, cha.getX(), cha.getY(),
									cha.getMapId());
						}
					}
				}
					break;
				case 12130: {
					// 法利昂-水压
					boolean isUSE = false;
					isUSE = _magic.calcProbabilityMagic(_skillId);
					if (isUSE && !cha.hasSkillEffect(50)
							&& !cha.hasSkillEffect(78)
							&& !cha.hasSkillEffect(80)
							&& !cha.hasSkillEffect(157)
							&& cha.get_poisonStatus4() != 4
							&& cha.get_poisonStatus6() != 4) {
						L1Poison4 poison = new L1Poison4();
						boolean success = poison.handleCommands((L1Object) cha,
								4, 16000, 0);
						if (success == true) {
							cha.add_poison4(poison);
							L1EffectSpawn.getInstance().spawnEffect(81168,
									16000, cha.getX(), cha.getY(),
									cha.getMapId());
						}
					}
				}
					break;
				case 12007:
				case 12096:
				case 12127: { // 污浊安特-毒雾、木乃伊王-毒雾、安塔瑞斯-毒雾
					_user.setSkillEffect(_skillId, 11 * 1000);
					L1EffectSpawn.getInstance().doSpawnDamagePoisonforNpc(
							_user, _target);
				}
					break;
				case 12132: { // 巴拉卡斯-火牢
					_user.setSkillEffect(_skillId, 11 * 1000);
					L1EffectSpawn.getInstance().doSpawnFireWallforNpc(_user,
							_target);
				}
					break;
				case 16001: { // 废弃坟场骷髅堆瞬移功能
					if (cha instanceof L1PcInstance) {
						Random random = new Random();
						int rnd = random.nextInt(19) + 1;
						L1PcInstance pc = (L1PcInstance) cha;
						try {
							Thread.sleep(300); // 延迟显示效果
						} catch (InterruptedException e) {
						}

						switch (rnd) {
						case 1: {
							L1Teleport.teleport(pc, 32761, 32792, (short) 6001,
									5, false);
						}
							break;
						case 2: {
							L1Teleport.teleport(pc, 32766, 32807, (short) 6001,
									5, false);
						}
							break;
						case 3: {
							L1Teleport.teleport(pc, 32771, 32829, (short) 6001,
									5, false);
						}
							break;
						case 4: {
							L1Teleport.teleport(pc, 32772, 32846, (short) 6001,
									5, false);
						}
							break;
						case 5: {
							L1Teleport.teleport(pc, 32767, 32867, (short) 6001,
									5, false);
						}
							break;
						case 6: {
							L1Teleport.teleport(pc, 32781, 32855, (short) 6001,
									5, false);
						}
							break;
						case 7: {
							L1Teleport.teleport(pc, 32794, 32868, (short) 6001,
									5, false);
						}
							break;
						case 8: {
							L1Teleport.teleport(pc, 32795, 32828, (short) 6001,
									5, false);
						}
							break;
						case 9: {
							L1Teleport.teleport(pc, 32784, 32819, (short) 6001,
									5, false);
						}
							break;
						case 10: {
							L1Teleport.teleport(pc, 32782, 32789, (short) 6001,
									5, false);
						}
							break;
						case 11: {
							L1Teleport.teleport(pc, 32823, 32794, (short) 6001,
									5, false);
						}
							break;
						case 12: {
							L1Teleport.teleport(pc, 32813, 32813, (short) 6001,
									5, false);
						}
							break;
						case 13: {
							L1Teleport.teleport(pc, 32821, 32838, (short) 6001,
									5, false);
						}
							break;
						case 14: {
							L1Teleport.teleport(pc, 32806, 32866, (short) 6001,
									5, false);
						}
							break;
						case 15: {
							L1Teleport.teleport(pc, 32839, 32855, (short) 6001,
									5, false);
						}
							break;
						case 16: {
							L1Teleport.teleport(pc, 32846, 32873, (short) 6001,
									5, false);
						}
							break;
						case 17: {
							L1Teleport.teleport(pc, 32841, 32832, (short) 6001,
									5, false);
						}
							break;
						case 18: {
							L1Teleport.teleport(pc, 32855, 32803, (short) 6001,
									5, false);
						}
							break;
						case 19: {
							L1Teleport.teleport(pc, 32856, 32787, (short) 6001,
									5, false);
						}
							break;
						}
					}
				}
					break;
				case 15005: { // 召唤效果
					_user.broadcastPacket(new S_SkillSound(_user.getId(), 1127));
				}
					break;
				case 16002: { // 地面的鬼脸-负面效果
					_user.broadcastPacket(new S_SkillSound(_user.getId(), 6334));
					Random random = new Random();
					int rnd = random.nextInt(5) + 1;
					switch (rnd) {
					case 1: { // 缓速术
						_user.broadcastPacket(new S_SkillSound(cha.getId(), 752));
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							if (pc.getHasteItemEquipped() > 0) {
								continue;
							}
						}
						if (cha.getMoveSpeed() == 0) {
							if (cha instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) cha;
								pc.sendPackets(new S_SkillHaste(pc.getId(), 2,
										6));
							}
							cha.broadcastPacket(new S_SkillHaste(cha.getId(),
									2, 6));
							cha.setMoveSpeed(2);
							cha.setSkillEffect(29, 6000);
						} else if (cha.getMoveSpeed() == 1) {
							int skillNum = 0;
							if (cha.hasSkillEffect(HASTE)) {
								skillNum = HASTE;
							} else if (cha.hasSkillEffect(GREATER_HASTE)) {
								skillNum = GREATER_HASTE;
							} else if (cha.hasSkillEffect(STATUS_HASTE)) {
								skillNum = STATUS_HASTE;
							}
							if (skillNum != 0) {
								cha.removeSkillEffect(skillNum);
								cha.removeSkillEffect(29);
								cha.setMoveSpeed(0);
							}
						} else if (cha.getMoveSpeed() == 2) {
							cha.removeSkillEffect(29);
							if (cha instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) cha;
								pc.sendPackets(new S_SkillHaste(pc.getId(), 2,
										6));
							}
							cha.broadcastPacket(new S_SkillHaste(cha.getId(),
									2, 6));
							cha.setMoveSpeed(2);
							cha.setSkillEffect(29, 6000);
							continue;
						}
					}
						break;
					case 2: { // 黑闇之影
						_user.broadcastPacket(new S_SkillSound(cha.getId(),
								2175));
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							if (pc.hasSkillEffect(1012)) {
								pc.sendPackets(new S_CurseBlind(2));
							} else {
								pc.sendPackets(new S_CurseBlind(1));
							}
						}
						cha.setSkillEffect(40, 10000);
					}
						break;
					case 3: { // 木乃伊
						_user.broadcastPacket(new S_SkillSound(cha.getId(), 746));
						if (cha instanceof L1PcInstance) {
							L1CurseParalysis.curse(cha, 8000, 6000);
						} else {
							L1CurseParalysis.curse(cha, 8000, 16000);
						}
					}
						break;
					case 4: { // 加速
						_user.broadcastPacket(new S_SkillSound(cha.getId(), 755));
						if (cha.getMoveSpeed() != 2) { // 中以外
							if (cha instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) cha;
								pc.setDrink(false);
								pc.sendPackets(new S_SkillHaste(pc.getId(), 1,
										600));
							}
							cha.broadcastPacket(new S_SkillHaste(cha.getId(),
									1, 0));
							cha.setMoveSpeed(1);
							cha.setSkillEffect(43, 600000);
						} else { // 中
							int skillNum = 0;
							if (cha.hasSkillEffect(SLOW)) {
								skillNum = SLOW;
							} else if (cha.hasSkillEffect(MASS_SLOW)) {
								skillNum = MASS_SLOW;
							} else if (cha.hasSkillEffect(ENTANGLE)) {
								skillNum = ENTANGLE;
							}
							if (skillNum != 0) {
								cha.removeSkillEffect(skillNum);
								cha.removeSkillEffect(HASTE);
								cha.setMoveSpeed(0);
								continue;
							}
						}
					}
						break;
					}
				}
					break;
				case 16003: { // 游走的亡魂
					_user.setHeading(_user.targetDirection(cha.getX(),
							cha.getY()));
					_user.broadcastPacket(new S_DoActionGFX(_user.getId(), 1));
					dmg = 5;
					L1Object object = L1World.getInstance().findObject(
							_user.getId());
					L1MonsterInstance npc = (L1MonsterInstance) object;
					npc.receiveDamage(_user, 32767);
				}
					break;
				case 16004: { // 游走的亡魂
					_user.setHeading(_user.targetDirection(cha.getX(),
							cha.getY()));
					_user.broadcastPacket(new S_DoActionGFX(_user.getId(), 30));
					dmg = 5;
					L1Object object = L1World.getInstance().findObject(
							_user.getId());
					L1MonsterInstance npc = (L1MonsterInstance) object;
					npc.receiveDamage(_user, 32767);
				}
					break;
				case SHOCK_STUN: { // 87冲击之晕
					if (cha.get_poisonStatus2() != 4 && !cha.hasSkillEffect(50)
							&& !cha.hasSkillEffect(78)
							&& !cha.hasSkillEffect(80)
							&& !cha.hasSkillEffect(157)
							&& cha.get_poisonStatus4() != 4
							&& cha.get_poisonStatus6() != 4) {
						int[] stunTimeArray = { 3000, 3500, 4000, 4500, 5500 };
						Random random = new Random();
						int rnd = random.nextInt(stunTimeArray.length);
						_shockStunDuration = stunTimeArray[rnd];

						L1Poison2 poison = new L1Poison2();
						boolean success = poison.handleCommands((L1Object) cha,
								4, _shockStunDuration, 0);
						if (success == true) {
							cha.add_poison2(poison);
							L1EffectSpawn.getInstance().spawnEffect(81162,
									_shockStunDuration, cha.getX(), cha.getY(),
									cha.getMapId());
						}
					}
				}
				break;
				case THUNDER_GRAB: {// 夺命之雷192
					if (cha.get_poisonStatus3() != 4 &&
							cha.get_poisonStatus4() != 4 &&
							cha.get_poisonStatus6() != 4 &&
							!cha.hasSkillEffect(50) &&
							!cha.hasSkillEffect(78) &&
							!cha.hasSkillEffect(80) &&
							!cha.hasSkillEffect(157)) {
						int[] stunTimeArray = { 2000, 2500, 3000, 3500, 4000 };
						Random random = new Random();
						int rnd = random.nextInt(stunTimeArray.length);
						_shockStunDuration = stunTimeArray[rnd];

						L1Poison3 poison = new L1Poison3();
						boolean success = poison.handleCommands((L1Object) cha,
								4, _shockStunDuration, 0);
						if (success == true) {
							cha.add_poison3(poison);
							L1EffectSpawn.getInstance().spawnEffect(82268,
									_shockStunDuration, cha.getX(), cha.getY(),
									cha.getMapId());
						}
					}
				}
				break;
				case 17001: { // 骑士(怪物)-冲击之晕
					Random random = new Random();
					if (cha.get_poisonStatus2() != 4
							&& !cha.hasSkillEffect(50)
							&& !cha.hasSkillEffect(78)
							&& !cha.hasSkillEffect(80)
							&& !cha.hasSkillEffect(157)
							&& cha.get_poisonStatus4() != 4
							&& cha.get_poisonStatus6() != 4
							&& ((50 + _user.getLevel() - cha.getLevel()) > (random
									.nextInt(100) + 1))) {
						_user.broadcastPacket(new S_SkillSound(cha.getId(),
								4434));
						int[] stunTimeArray = { 1500, 2000, 2500, 3500, 4000 };
						int rnd = random.nextInt(stunTimeArray.length);
						_shockStunDuration = stunTimeArray[rnd];

						L1Poison2 poison = new L1Poison2();
						boolean success = poison.handleCommands((L1Object) cha,
								4, _shockStunDuration, 0);
						if (success == true) {
							cha.add_poison2(poison);
							L1EffectSpawn.getInstance().spawnEffect(81162,
									_shockStunDuration, cha.getX(), cha.getY(),
									cha.getMapId());
						}
					}
				}
					break;
				case TRIPLE_ARROW: { // 修正三重矢
					/*
					 * boolean gfxcheck = false; int[] BowGFX = { 138, 37, 3860,
					 * 3126, 3420, 2284, 3105, 3145, 3148, 3151, 3871, 4125,
					 * 2323, 3892, 3895, 3898, 3901, 4917, 4918, 4919, 4950,
					 * 6140, 6145, 6150, 6155, 6160, 6269, 6272, 6275, 6278,
					 * 6087, 5184, 5186, 6089, 5976,6611,6627,6633 }; //
					 * 补上2.70C弓类变身判断 // 6087, 5184, 5186, 6089, 5976 int
					 * playerGFX = _player.getTempCharGfx(); for (int gfx :
					 * BowGFX) { if (playerGFX == gfx) { gfxcheck = true; break;
					 * } } if (!gfxcheck) { return; }
					 */
					_player.setHeading(_player.targetDirection(_targetX,
							_targetY)); // 向目标向
					_player.sendPackets(new S_SkillSound(_player.getId(), 4394)); // 魔法效果取得
					_player.broadcastPacket(new S_SkillSound(_player.getId(),
							4394)); // 魔法效果送出
				}
					break;// 修正三重矢 end
				case 10026:
				case 10027:
				case 10028:
				case 10029: {// 四大龙安息
					if (_user instanceof L1NpcInstance) {
						_user.broadcastPacket(new S_NpcChatPacket(_npc,
								"$3717", 0)); // 、安息与。
					} else {
						_player.broadcastPacket(new S_ChatPacket(_player,
								"$3717", 0, 0)); // 、安息与。
					}
					dmg = cha.getCurrentHp();
				}
					break;
				case 12104: {// 混沌-呼唤玩家(原10057)
					L1Teleport.teleportToTargetFront(cha, _user, 1);
				}
					break;
				case ELEMENTAL_FALL_DOWN: {// 弱化属性实装
					byte player_attr = (byte) _player.getElfAttr();// 取得攻击者属性
					byte i = (byte) (-50);
					if (cha instanceof L1PcInstance) {// 被攻击的对象 = 玩家
						L1PcInstance pc = (L1PcInstance) cha;
						switch (player_attr) {
						case 0:
							_player.sendPackets(new S_ServerMessage(79));
							break;
						case 1:
							pc.addEarth(i);
							// pc.set_PcAttr(1);
							break;
						case 2:
							pc.addFire(i);
							// pc.set_PcAttr(2);
							break;
						case 4:
							pc.addWater(i);
							// pc.set_PcAttr(4);
							break;
						case 8:
							pc.addWind(i);
							// pc.set_PcAttr(8);
							break;
						}
						pc.set_PcAttr(player_attr);
					} else if (cha instanceof L1MonsterInstance) {
						switch (player_attr) {
						case 0:
							_player.sendPackets(new S_ServerMessage(79));
							break;
						case 1:
							_targetNpc.addEarth(i);
							// _targetNpc.set_NpcAttr(1);
							break;
						case 2:
							_targetNpc.addFire(i);
							// _targetNpc.set_NpcAttr(2);
							break;
						case 4:
							_targetNpc.addWater(i);
							// _targetNpc.set_NpcAttr(4);
							break;
						case 8:
							_targetNpc.addWind(i);
							// _targetNpc.set_NpcAttr(8);
							break;
						}
						_targetNpc.set_NpcAttr(player_attr);
					}
				}
					break;
				case 11011:
				case 11012:
				case 12038:
				case 12057: {
					// 浣熊、高等浣熊-缓速术、风之牙-缓速术、洁尼斯女王-蜘蛛丝之网
					boolean isUSE = false;
					isUSE = _magic.calcProbabilityMagic(_skillId);
					if (isUSE) {
						_user.broadcastPacket(new S_SkillSound(cha.getId(), 752));// 缓速效果
						if (cha.getMoveSpeed() == 0 || cha.getMoveSpeed() == 2) {
							if (cha instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) cha;
								pc.sendPackets(new S_SkillHaste(pc.getId(), 2,
										64));
							}
							cha.broadcastPacket(new S_SkillHaste(cha.getId(),
									2, 64));
							cha.setMoveSpeed(2);
							cha.setSkillEffect(29, 64000);
						} else if (cha.getMoveSpeed() == 1) {
							int skillNum = 0;
							if (cha.hasSkillEffect(HASTE)) {
								skillNum = HASTE;
							} else if (cha.hasSkillEffect(GREATER_HASTE)) {
								skillNum = GREATER_HASTE;
							} else if (cha.hasSkillEffect(STATUS_HASTE)) {
								skillNum = STATUS_HASTE;
							}
							if (skillNum != 0) {
								cha.removeSkillEffect(skillNum);
								cha.removeSkillEffect(29);
								cha.setMoveSpeed(0);
								continue;
							}
						}
					}
				}
					break;
				// ★★★ 确率系 ★★★
				case SLOW:
				case MASS_SLOW:
				case ENTANGLE: {// 缓速术,集体缓速术,地面障碍
					// 、
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getHasteItemEquipped() > 0) {
							continue;
						}
					}
					if (cha.getMoveSpeed() == 0) {
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							pc.sendPackets(new S_SkillHaste(pc.getId(), 2, 64));
						}
						cha.broadcastPacket(new S_SkillHaste(cha.getId(), 2, 64));
						cha.setMoveSpeed(2);
					} else if (cha.getMoveSpeed() == 1) {
						int skillNum = 0;
						if (cha.hasSkillEffect(HASTE)) {
							skillNum = HASTE;
						} else if (cha.hasSkillEffect(GREATER_HASTE)) {
							skillNum = GREATER_HASTE;
						} else if (cha.hasSkillEffect(STATUS_HASTE)) {
							skillNum = STATUS_HASTE;
						}
						if (skillNum != 0) {
							cha.removeSkillEffect(skillNum);
							cha.removeSkillEffect(_skillId);
							cha.setMoveSpeed(0);
							continue;
						}
					}
				}
					break;
				case CURSE_BLIND:
				case DARKNESS:
				case DARK_BLIND: {// 20闇盲咒术,40黑闇之影,补上103闇盲咒术
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.hasSkillEffect(STATUS_FLOATING_EYE)) {
							pc.sendPackets(new S_CurseBlind(2));
						} else {
							pc.sendPackets(new S_CurseBlind(1));
						}
					} else if (cha instanceof L1NpcInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						npc.allTargetClear();
					}
				}
					break;
				case CURSE_POISON: {// 11毒咒
					// 效果显示修正
					if (_user instanceof L1PcInstance) {
						_player.sendPackets(new S_SkillSound(cha.getId(), 745));// 毒咒效果
						_player.broadcastPacket(new S_SkillSound(cha.getId(),
								745));// 毒咒效果
					} else {
						_user.broadcastPacket(new S_SkillSound(cha.getId(), 745));// 毒咒效果
					}
					// 效果显示修正 end
					L1DamagePoison.doInfection(_user, cha, 3000, 15000, 10); // 伤害强化
				}
					break;
				case 12025:
				case 12078: {
					// 金属蜈蚣-喷毒,堕落的司祭(喷毒兽)-毒咒
					L1DamagePoison.doInfection(_user, cha, 3000, 15000, 10);
				}
					break;
				case 12125: {
					// 安塔瑞斯-剧毒喷吐
					L1DamagePoison.doInfection(_user, cha, 1000, 15000, 10);
				}
					break;
				case 12003:
				case 13006: {// 漂浮之眼-木乃伊的诅咒、梅杜莎-木乃伊的诅咒
					boolean isUSE = false;
					isUSE = _magic.calcProbabilityMagic(_skillId);
					if (isUSE && !cha.hasSkillEffect(50)
							&& !cha.hasSkillEffect(78)
							&& !cha.hasSkillEffect(80)
							&& !cha.hasSkillEffect(157)
							&& !cha.hasSkillEffect(1010)
							&& !cha.hasSkillEffect(1011)
							&& cha.get_poisonStatus4() != 4
							&& cha.get_poisonStatus6() != 4) {// 变更
						_user.broadcastPacket(new S_SkillSound(cha.getId(), 746)); // 木乃伊效果
						if (cha instanceof L1PcInstance) {
							L1CurseParalysis.curse(cha, 8000, 16000);
						} else {
							L1CurseParalysis.curse(cha, 8000, 16000);
						}
					}

					// 设定漂浮之眼放完木乃伊后逃跑
					if (_user.getGfxId() == 29) {
						_user.setSkillEffect(17005, 16 * 1000);// 逃跑判断
					}
					// 设定漂浮之眼放完木乃伊后逃跑 end
				}
					break;
				case CURSE_PARALYZE: { // 33木乃伊的诅咒
					// 判断追加
					if (!cha.hasSkillEffect(50) && !cha.hasSkillEffect(78)
							&& !cha.hasSkillEffect(80)
							&& !cha.hasSkillEffect(157)
							&& !cha.hasSkillEffect(1010)
							&& !cha.hasSkillEffect(1011)
							&& cha.get_poisonStatus4() != 4
							&& cha.get_poisonStatus6() != 4) {
						if (cha instanceof L1PcInstance) {
							L1CurseParalysis.curse(cha, 8000, 16000);
						} else {
							L1CurseParalysis.curse(cha, 8000, 16000);
						}
					}
					// 判断追加 end
				}
					break;
				case WEAKNESS: { // 弱化术
					/*
					 * 删除if (cha instanceof L1PcInstance) { L1PcInstance pc =
					 * (L1PcInstance) cha; pc.addDmgup(-5); pc.addHitup(-1); }删除
					 */
					cha.addDmgup(-5);
					cha.addHitup(-1);
				}
					break;
				case DISEASE: {// 疾病术
					/*
					 * 删除if (cha instanceof L1PcInstance) { L1PcInstance pc =
					 * (L1PcInstance) cha; pc.addHitup(-6); pc.addAc(12); }删除
					 */
					cha.addHitup(-6);
					cha.addAc(12);
				}
					break;
				case 80: { // 冰雪飓风
					boolean isUSE = false;
					isUSE = _magic.calcProbabilityMagic(_skillId);
					if (isUSE && !cha.hasSkillEffect(50)
							&& !cha.hasSkillEffect(78)
							&& !cha.hasSkillEffect(80)
							&& !cha.hasSkillEffect(157)
							&& cha.get_poisonStatus4() != 4
							&& cha.get_poisonStatus6() != 4) {
						L1Poison4 poison = new L1Poison4();
						boolean success = poison.handleCommands((L1Object) cha,
								4, 32000, 0);
						if (success == true) {
							cha.add_poison4(poison);
							L1EffectSpawn.getInstance().spawnEffect(81168,
									32000, cha.getX(), cha.getY(),
									cha.getMapId());
						}
					}
				}
					break;
				case ICE_LANCE: { // 50冰矛围篱
					_isFreeze = _magic.calcProbabilityMagic(_skillId);
					if (_isFreeze) {
						L1EffectSpawn.getInstance().spawnEffect(81168, 16000,
								cha.getX(), cha.getY(), cha.getMapId());
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							pc.sendPackets(new S_Poison(pc.getId(), 2));
							pc.broadcastPacket(new S_Poison(pc.getId(), 2));
							pc.sendPackets(new S_Paralysis(
									S_Paralysis.TYPE_FREEZE, _skill
											.getBuffDuration(), true));
						} else if (cha instanceof L1MonsterInstance
								|| cha instanceof L1SummonInstance
								|| cha instanceof L1PetInstance) {
							L1NpcInstance npc = (L1NpcInstance) cha;
							npc.broadcastPacket(new S_Poison(npc.getId(), 2));
							npc.setParalyzed(true);
							npc.setParalysisTime(16000);
						}
					}
				}
					break;
				case EARTH_BIND: {// 157大地屏障
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Poison(pc.getId(), 2));
						pc.broadcastPacket(new S_Poison(pc.getId(), 2));
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_FREEZE,
								_skill.getBuffDuration(), true));
					} else if (cha instanceof L1MonsterInstance
							|| cha instanceof L1SummonInstance
							|| cha instanceof L1PetInstance) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						npc.broadcastPacket(new S_Poison(npc.getId(), 2));
						npc.setParalyzed(true);
						npc.setParalysisTime(16000);
					}
				}
					break;
				case WIND_SHACKLE: { // 风之枷锁
					if (cha instanceof L1PcInstance) {
						((L1PcInstance) cha)
								.sendPackets(new S_PacketBoxWindShackle(cha
										.getId(), _skill.getBuffDuration()));
					}
				}
					break;
				case CANCELLATION: { // 44魔法相消术

					// 骑士45级任务-巨人
					if (_targetNpc != null
							&& _targetNpc.getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_9) { // 调查员(巨人)
						_targetNpc.transform(l1j.william.New_Id.Npc_AJ_2_10);
						// return;
					}
					// 骑士45级任务-巨人 end
					if (_targetNpc != null) {
						if (_targetNpc.getNpcId() == l1j.william.New_Id.Npc_AJ_2_22) {
							if (_targetNpc instanceof L1MonsterInstance) {
								L1MonsterInstance mob = (L1MonsterInstance) _targetNpc;
								mob.transform(l1j.william.New_Id.Npc_AJ_2_23);
								// return;
							}
						}
					}

					if (_player != null && _player.isInvisble()) {
						_player.delInvis();
					}
					if (!(cha instanceof L1PcInstance)) {
						L1NpcInstance npc = (L1NpcInstance) cha;
						npc.setMoveSpeed(0);
						npc.setBraveSpeed(0);
						npc.broadcastPacket(new S_SkillHaste(cha.getId(), 0, 0));
						npc.broadcastPacket(new S_SkillBrave(cha.getId(), 0, 0));
						npc.broadcastPacket(new S_ChangeShape(cha.getId(), cha
								.getTempCharGfx()));
						npc.setWeaponBreaked(false);
						npc.setParalyzed(false);
						npc.setParalysisTime(0);
						// 更新怪物武器状态
						if (npc.getNpcTemplate().getAttStatus() != 0) {
							npc.setStatus(npc.getNpcTemplate().getAttStatus());
							for (L1PcInstance pc : L1World.getInstance()
									.getRecognizePlayer(npc)) {
								pc.sendPackets(new S_RemoveObject(npc));
								pc.removeKnownObject(npc);
								pc.updateObject();
							}
						}
						// 更新怪物武器状态 end
					}

					// 解除
					for (int skillNum = SKILLS_BEGIN; skillNum <= SKILLS_END; skillNum++) {
						if (isNotCancelable(skillNum)) {
							continue;
						}
						cha.removeSkillEffect(skillNum);
					}

					// 强化、异常解除
					cha.curePoison();
					cha.cureParalaysis();
					for (int skillNum = STATUS_BEGIN; skillNum <= STATUS_END; skillNum++) {
						if (skillNum == STATUS_CHAT_PROHIBITED) { // 禁止解除
							continue;
						}
						cha.removeSkillEffect(skillNum);
					}

					/*
					 * 删除// 料理解除 for (int skillNum = COOKING_BEGIN; skillNum <=
					 * COOKING_END; skillNum++) { if (isNotCancelable(skillNum))
					 * { continue; } cha.removeSkillEffect(skillNum); }删除
					 */

					// 装备时
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getHasteItemEquipped() > 0) {
							pc.setMoveSpeed(0);
							pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
							pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0,
									0));
						}
					}
					cha.removeSkillEffect(STATUS_FREEZE); // Freeze解除
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						// 死亡时取消冰冻、地屏状态
						/*
						 * if (pc.get_poisonStatus2() == 4) { L1Poison2 poison =
						 * pc.get_poison2(); if (poison != null) {
						 * poison.CurePoison(pc); pc.del_poison2(); } }
						 */
						if (pc.get_poisonStatus3() == 4) {
							L1Poison3 poison = pc.get_poison3();
							if (poison != null) {
								poison.CurePoison(pc);
								pc.del_poison3();
							}
						}
						if (pc.get_poisonStatus4() == 4) {
							L1Poison4 poison = pc.get_poison4();
							if (poison != null) {
								poison.CurePoison(pc);
								pc.del_poison4();
							}
						}
						if (pc.get_poisonStatus6() == 4) {
							L1Poison6 poison = pc.get_poison6();
							if (poison != null) {
								poison.CurePoison(pc);
								pc.del_poison6();
							}
						}
						pc.sendPackets(new S_CharVisualUpdate(pc));
						pc.broadcastPacket(new S_CharVisualUpdate(pc));
					}
				}
					break;
				case TURN_UNDEAD: { // 起死回生术
					if (undeadType == 1 || undeadType == 3) {
						dmg = cha.getCurrentHp();
					} else {
						dmg = 0;
					}
				}
					break;
				case MANA_DRAIN: { // 魔力夺取
					Random random = new Random();
					int chance = random.nextInt(10) + 5;
					drainMana = chance + (_user.getInt() / 2);
					if (cha.getCurrentMp() < drainMana) {
						drainMana = cha.getCurrentMp();
					}
				}
					break;
				case 12055: { // 火灵之主-魔力夺取
					boolean isUSE = false;
					isUSE = _magic.calcProbabilityMagic(_skillId);
					if (isUSE) {
						_user.broadcastPacket(new S_SkillSound(cha.getId(),
								2171)); // 魔夺效果
						Random random = new Random();
						int chance = random.nextInt(10) + 5;
						drainMana = chance + (_user.getInt() / 2);
						if (cha.getCurrentMp() < drainMana) {
							drainMana = cha.getCurrentMp();
						}
						cha.setCurrentMp(cha.getCurrentMp() - drainMana); // 被吸魔者
						if (_user.getMaxMp() <= drainMana) {
							_user.setCurrentMp(_user.getMaxMp()); // 吸魔者
						} else {
							_user.setCurrentMp(_user.getCurrentMp() + drainMana);// 吸魔者
						}
					}
				}
					break;
				case WEAPON_BREAK: { // 坏物术
					/*
					 * 对NPC场合、L1Magic算出1/2
					 * 、对PC场合记入。 损伤量1～(int/3)
					 */
					if (_calcType == PC_PC || _calcType == NPC_PC) {
						if (cha instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) cha;
							L1ItemInstance weapon = pc.getWeapon();
							if (weapon != null) {
								Random random = new Random();
								int weaponDamage = random.nextInt(_user
										.getInt() / 3) + 1;
								// \f1%0损伤。
								pc.sendPackets(new S_ServerMessage(268, weapon
										.getLogName()));
								pc.getInventory().receiveDamage(weapon,
										weaponDamage);
							}
						}
					} else {
						((L1NpcInstance) cha).setWeaponBreaked(true);
					}
				}
					break;
				case FOG_OF_SLEEPING: {// 66沉睡之雾
					if (cha instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_SLEEP,
								_skill.getBuffDuration(), true));
					}
					// 判断不是祭司或魔法娃娃
					if (!(cha instanceof L1BabyInstance)
							&& !(cha instanceof L1HierarchInstance))
						// 判断不是否为祭司或魔法娃娃 end
						cha.setSleeped(true);
				}
					break;

				// 龙骑士技能实装
				// 护卫毁灭
				case GUARD_BRAKE:
					if (cha instanceof L1PcInstance) {
						final L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(10);
					}
					break;

				case FOE_SLAYER:
					_player.isFoeSlayer(true);
					for (int i = 3; i > 0; i--) { // 次数
						_target.onAction(_player);
					}
					_player.isFoeSlayer(false);

					_player.sendPackets(new S_EffectLocation(_target.getX(),
							_target.getY(), 6509));
					_player.broadcastPacket(new S_EffectLocation(
							_target.getX(), _target.getY(), 6509));
					_player.sendPackets(new S_SkillSound(_player.getId(), 7020));
					_player.broadcastPacket(new S_SkillSound(_player.getId(),
							7020));

					if (_player.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV1)) {
						_player.killSkillEffectTimer(SPECIAL_EFFECT_WEAKNESS_LV1);
						_player.sendPackets(new S_SkillIconGFX(75, 0, 0));
					} else if (_player
							.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV2)) {
						_player.killSkillEffectTimer(SPECIAL_EFFECT_WEAKNESS_LV2);
						_player.sendPackets(new S_SkillIconGFX(75, 0, 0));
					} else if (_player
							.hasSkillEffect(SPECIAL_EFFECT_WEAKNESS_LV3)) {
						_player.killSkillEffectTimer(SPECIAL_EFFECT_WEAKNESS_LV3);
						_player.sendPackets(new S_SkillIconGFX(75, 0, 0));
					}
					break;

				}// 变更为switch 2 end

				// ●●●● PC效果 ●●●●
				if (_calcType == PC_PC || _calcType == NPC_PC) {
					// ★★★ 特殊系★★★
					switch (_skillId) // 变更为switch 3
					{
					case TELEPORT:
					case MASS_TELEPORT: { // 瞬移,集传
						L1PcInstance pc = (L1PcInstance) cha;
						L1BookMark book = null;
						final ArrayList<L1BookMark> bookList = CharBookReading
								.get().getBookMarks(pc);
						// 检查是否有此坐标
						if (bookList != null) {
							for (final L1BookMark bookmark : bookList) {
								if (bookmark.getMapId() == pc
										.getTempBookmarkMapID()
										&& bookmark.getLocX() == pc
												.getTempBookmarkLocX()
										&& bookmark.getLocY() == pc
												.getTempBookmarkLocY()) {
									book = bookmark;
									break;
								}
							}
						}
						if (book != null) {
							if (pc.getMap().isEscapable() || pc.isGm()) {
								if (_skillId == MASS_TELEPORT) {
									List<L1PcInstance> clanMember = L1World
											.getInstance().getVisiblePlayer(pc);
									for (L1PcInstance member : clanMember) {
										if (pc.getLocation()
												.getTileLineDistance(
														member.getLocation()) <= 3
												&& member.getClanid() > 0
												&& member.getClanid() == pc
														.getClanid()
												&& member.getId() != pc.getId()
												&& member.isMassTeleport()) {
											L1Teleport.teleport(member,
													book.getLocX(),
													book.getLocY(),
													book.getMapId(), 5, true);
										}
									}
									WriteLogTxt.Recording(
											"集体传送术使用记录",
											"玩家:" + pc.getName() + "ObjId:"
													+ pc.getId() + " 地图Id:"
													+ pc.getMapId() + " "
													+ pc.getX() + ","
													+ pc.getY());
								}
								L1Teleport.teleport(pc, book.getLocX(),
										book.getLocY(), book.getMapId(), 5,
										true);
							} else {
								L1Teleport.teleport(pc, pc.getX(), pc.getY(),
										pc.getMapId(), pc.getHeading(), false);
								pc.sendPackets(new S_ServerMessage(79));
							}
						} else {
							if (pc.getMap().isTeleportable() || pc.isGm()) {
								L1Location newLocation = pc.getLocation()
										.randomLocation(200, true);
								int newX = newLocation.getX();
								int newY = newLocation.getY();
								short mapId = (short) newLocation.getMapId();

								if (_skillId == MASS_TELEPORT) { // 
									List<L1PcInstance> clanMember = L1World
											.getInstance().getVisiblePlayer(pc);
									for (L1PcInstance member : clanMember) {
										if (pc.getLocation()
												.getTileLineDistance(
														member.getLocation()) <= 3
												&& member.getClanid() > 0
												&& member.getClanid() == pc
														.getClanid()
												&& member.getId() != pc.getId()
												&& member.isMassTeleport()) {
											L1Teleport.teleport(member, newX,
													newY, mapId, 5, true);
										}
									}
									WriteLogTxt.Recording(
											"集体传送术使用记录",
											"玩家:" + pc.getName() + "ObjId:"
													+ pc.getId() + " 地图Id:"
													+ pc.getMapId() + " "
													+ pc.getX() + ","
													+ pc.getY());
								}
								L1Teleport.teleport(pc, newX, newY, mapId, 5,
										true);
							} else {
								pc.sendPackets(new S_ServerMessage(276));
								L1Teleport.teleport(pc, pc.getX(), pc.getY(),
										pc.getMapId(), pc.getHeading(), false);
							}
						}
					}
						break;
					case TELEPORT_TO_MATHER: { // 世界树的呼唤
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getMap().isEscapable() || pc.isGm()) {
							L1Teleport.teleport(pc, 33051, 32337, (short) 4, 5,
									true);
						} else {
							pc.sendPackets(new S_ServerMessage(647));
							L1Teleport.teleport(pc, pc.getX(), pc.getY(),
									pc.getMapId(), pc.getHeading(), false);
						}
					}
						break;
					case CALL_CLAN: { // 封印禁地
						L1PcInstance pc = (L1PcInstance) cha;
						L1PcInstance clanPc = (L1PcInstance) L1World
								.getInstance().findObject(_targetID);
						if (clanPc != null) {
							clanPc.setTempID(pc.getId()); // 相手ID保存
							clanPc.sendPackets(new S_Message_YN(748, "")); // 血盟员。应？（Y/N）
						}
					}
						break;
					case RUN_CLAN: { // 援护盟友
						L1PcInstance pc = (L1PcInstance) cha;
						L1PcInstance clanPc = (L1PcInstance) L1World
								.getInstance().findObject(_targetID);
						if (clanPc != null) {
							if (pc.getMap().isEscapable() || pc.isGm()) {
								boolean castle_area = L1CastleLocation
										.checkInAllWarArea(
												// 城
												clanPc.getX(), clanPc.getY(),
												clanPc.getMapId());
								if ((clanPc.getMapId() == 0
										|| clanPc.getMapId() == 4 || clanPc
										.getMapId() == 304)
										&& castle_area == false) {
									L1Teleport.teleport(pc, clanPc.getX(),
											clanPc.getY(), clanPc.getMapId(),
											5, true);
								} else {
									// \f1今行所中。
									// 删除pc
									// 删除 .sendPackets(new S_ServerMessage(
									// 删除 547));
									// 讯息修正
									pc.sendPackets(new S_SystemMessage(
											L1WilliamSystemMessage
													.ShowMessage(1024))); // 从DB取得资讯
									// 讯息修正 end
								}
							} else {
								// 周边妨害。、使用。
								pc.sendPackets(new S_ServerMessage(647));
								L1Teleport.teleport(pc, pc.getX(), pc.getY(),
										pc.getMapId(), pc.getHeading(), false);
							}
						}
					}
						break;
					case CREATE_MAGICAL_WEAPON: { // 创造魔法武器
						L1PcInstance pc = (L1PcInstance) cha;
						L1ItemInstance item = pc.getInventory().getItem(
								_itemobjid);
						if (item != null && item.getItem().getType2() == 1) {
							int item_type = item.getItem().getType2();
							int safe_enchant = item.getItem().get_safeenchant();
							int enchant_level = item.getEnchantLevel();
							String item_name = item.getName();
							if (safe_enchant < 0) { // 强化不可
								pc.sendPackets( // \f1何起。
								new S_ServerMessage(79));
							} else if (safe_enchant == 0) { // 安全圈+0
								pc.sendPackets( // \f1何起。
								new S_ServerMessage(79));
							} else if (item_type == 1 && enchant_level == 0) {
								if (!item.isIdentified()) {// 未鉴定
									pc.sendPackets( // \f1%0%2%1光。
									new S_ServerMessage(161, item_name, "$245",
											"$247"));
								} else {
									item_name = "+0 " + item_name;
									pc.sendPackets( // \f1%0%2%1光。
									new S_ServerMessage(161, "+0 " + item_name,
											"$245", "$247"));
								}
								item.setEnchantLevel(1);
								pc.getInventory().updateItem(item,
										L1PcInventory.COL_ENCHANTLVL);
							} else {
								pc.sendPackets( // \f1何起。
								new S_ServerMessage(79));
							}
						} else {
							pc.sendPackets( // \f1何起。
							new S_ServerMessage(79));
						}
					}
						break;
					case BRING_STONE: { // 提炼魔石
						L1PcInstance pc = (L1PcInstance) cha;
						Random random = new Random();
						L1ItemInstance item = pc.getInventory().getItem(
								_itemobjid);
						if (item != null) {
							int dark = (int) (10 + (pc.getLevel() * 0.8) + (pc
									.getWis() - 6) * 1.2);
							int brave = (int) (dark / 2.1);
							int wise = (int) (brave / 2.0);
							int kayser = (int) (wise / 1.9);
							int chance = random.nextInt(100) + 1;
							if (item.getItem().getItemId() == 40320) {
								pc.getInventory().removeItem(item, 1);
								if (dark >= chance) {
									pc.getInventory().storeItem(40321, 1);
									pc.sendPackets(new S_ServerMessage(403,
											"$2475")); // %0手入。
								} else {
									pc.sendPackets(new S_ServerMessage(280)); // \f1魔法失败。
								}
							} else if (item.getItem().getItemId() == 40321) {
								pc.getInventory().removeItem(item, 1);
								if (brave >= chance) {
									pc.getInventory().storeItem(40322, 1);
									pc.sendPackets(new S_ServerMessage(403,
											"$2476")); // %0手入。
								} else {
									pc.sendPackets(new S_ServerMessage(280)); // \f1魔法失败。
								}
							} else if (item.getItem().getItemId() == 40322) {
								pc.getInventory().removeItem(item, 1);
								if (wise >= chance) {
									pc.getInventory().storeItem(40323, 1);
									pc.sendPackets(new S_ServerMessage(403,
											"$2477")); // %0手入。
								} else {
									pc.sendPackets(new S_ServerMessage(280)); // \f1魔法失败。
								}
							} else if (item.getItem().getItemId() == 40323) {
								pc.getInventory().removeItem(item, 1);
								if (kayser >= chance) {
									pc.getInventory().storeItem(40324, 1);
									pc.sendPackets(new S_ServerMessage(403,
											"$2478")); // %0手入。
								} else {
									pc.sendPackets(new S_ServerMessage(280)); // \f1魔法失败
								}
							}
						}
					}
						break;
					case SUMMON_MONSTER: { // 召唤术
						L1PcInstance pc = (L1PcInstance) cha;
						int level = pc.getLevel();
						int[] summons;
						if (pc.getMap().isRecallPets() || pc.isGm()) {
							if (pc.getInventory().checkEquipped(20284)) {
								pc.sendPackets(new S_ShowSummonList(pc.getId()));
							} else {
								summons = new int[] { 81083, 81084, 81085,
										81086, 81087, 81088, 81089 };
								int summonid = 0;
								int summoncost = 6;
								int levelRange = 32;
								for (int i = 0; i < summons.length; i++) { // 该当ＬＶ范围检索
									if (level < levelRange
											|| i == summons.length - 1) {
										summonid = summons[i];
										break;
									}
									levelRange += 4;
								}

								int petcost = 0;
								Object[] petlist = pc.getPetList().values()
										.toArray();
								for (Object pet : petlist) {
									// 现在
									petcost += ((L1NpcInstance) pet)
											.getPetcost();
								}
								int charisma = pc.getCha() + 6 - petcost;
								int summoncount = charisma / summoncost;
								L1Npc npcTemp = NpcTable.getInstance()
										.getTemplate(summonid);
								for (int i = 0; i < summoncount; i++) {
									L1SummonInstance summon = new L1SummonInstance(
											npcTemp, pc);
									summon.setPetcost(summoncost);
								}
							}
						} else {
							// \f1何起。
							pc.sendPackets(new S_ServerMessage(353)); // 讯息修正
						}
					}
						break;
					case 134: {// 镜反射效果提示
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_SystemMessage(
								L1WilliamSystemMessage.ShowMessage(1025))); // 从DB取得资讯
					}
						break;
					case LESSER_ELEMENTAL:
					case GREATER_ELEMENTAL: { // 召唤属性精灵,召唤强力属性精灵
						L1PcInstance pc = (L1PcInstance) cha;
						int attr = pc.getElfAttr();
						if (attr != 0) { // 无属性实行
							if (pc.getMap().isRecallPets() || pc.isGm()) {
								int petcost = 0;
								Object[] petlist = pc.getPetList().values()
										.toArray();
								for (Object pet : petlist) {
									// 现在
									petcost += ((L1NpcInstance) pet)
											.getPetcost();
								}

								if (petcost == 0) { // 1匹所属NPC实行
									int summonid = 0;
									int summons[];
									if (_skillId == LESSER_ELEMENTAL) { // [地,火,水,风]
										summons = new int[] { 45306, 45303,
												45304, 45305 };
									} else {
										// [地,火,水,风]
										summons = new int[] { 81053, 81050,
												81051, 81052 };
									}
									int npcattr = 1;
									for (int i = 0; i < summons.length; i++) {
										if (npcattr == attr) {
											summonid = summons[i];
											i = summons.length;
										}
										npcattr *= 2;
									}
									// 特殊设定场合出现
									if (summonid == 0) {
										Random random = new Random();
										int k3 = random.nextInt(4);
										summonid = summons[k3];
									}

									L1Npc npcTemp = NpcTable.getInstance()
											.getTemplate(summonid);
									L1SummonInstance summon = new L1SummonInstance(
											npcTemp, pc);
									summon.setPetcost(pc.getCha() + 7); // 精灵他NPC所属
								}
							} else {
								// \f1何起。
								pc.sendPackets(new S_ServerMessage(353)); // 讯息修正
							}
						}
					}
						break;
					case ABSOLUTE_BARRIER: { // 绝对屏障
						L1PcInstance pc = (L1PcInstance) cha;
						pc.stopHpRegeneration();
						pc.stopMpRegeneration();
						pc.stopMpRegenerationByDoll();
					}
						break;
					}// 变更为switch 3 end

					// ★★★ 变化系（） ★★★
					switch (_skillId)// 变更为switch 4
					{
					case LIGHT: { // 日光术
						L1PcInstance pc = (L1PcInstance) cha;
						/*
						 * 删除pc.sendPackets(new S_Light(pc.getId(), 14)); if
						 * (!cha.isInvisble()) { pc.broadcastPacket(new
						 * S_Light(pc.getId(), 14)); }删除
						 */
						// 亮度
						pc.setPcLight(14);

						int item = 0;
						for (Object Light : pc.getInventory().getItems()) {
							L1ItemInstance OwnLight = (L1ItemInstance) Light;
							if ((OwnLight.getItem().getItemId() == 40001
									|| OwnLight.getItem().getItemId() == 40002
									|| OwnLight.getItem().getItemId() == 40004 || OwnLight
									.getItem().getItemId() == 40005)
									&& OwnLight.getEnchantLevel() != 0) {
								if (pc.getPcLight() < OwnLight.getItem()
										.getLightRange()) {
									pc.setPcLight(OwnLight.getItem()
											.getLightRange());
									if (OwnLight.getItem().getItemId() == 40004) {
										item = 40004;
									}
								}
							}
						}

						pc.sendPackets(new S_Light(pc.getId(), pc.getPcLight()));
						if (!pc.isInvisble() && item != 40004) {// 非隐身中跟魔法灯笼除外
							pc.broadcastPacket(new S_Light(pc.getId(), pc
									.getPcLight()));
						}
						// 亮度 end
					}
						break;
					case 11013:
					case 12043: {// 高等浣熊-疾病术、火之牙-疾病术
						boolean isUSE = false;
						isUSE = _magic.calcProbabilityMagic(_skillId);
						if (isUSE) {
							_user.broadcastPacket(new S_SkillSound(cha.getId(),
									2230));// 疾病效果
							if (cha.hasSkillEffect(56)) {
								cha.addAc(-12);
								cha.addHitup(6);
								cha.killSkillEffectTimer(56);
							}
							cha.addAc(12);
							cha.addHitup(-6);
							cha.setSkillEffect(56, 64000);
						}
					}
						break;
					case 11014:
					case 12040: {// 高等浣熊-弱化术、水之牙-弱化术
						boolean isUSE = false;
						isUSE = _magic.calcProbabilityMagic(_skillId);
						if (isUSE) {
							_user.broadcastPacket(new S_SkillSound(cha.getId(),
									2228));// 弱化效果
							if (cha.hasSkillEffect(47)) {
								cha.addDmgup(5);
								cha.addHitup(1);
								cha.killSkillEffectTimer(47);
							}
							cha.addDmgup(-5);
							cha.addHitup(-1);
							cha.setSkillEffect(47, 64000);
						}
					}
						break;
					case GLOWING_AURA: { // 激励士气
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addHitup(5);
						pc.addMr(10);
						pc.sendPackets(new S_SPMR(pc));
						pc.sendPackets(new S_SkillIconAura(113,
								_getBuffIconDuration));
					}
						break;
					case SHINING_AURA: { // 钢铁士气
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-8);
						pc.sendPackets(new S_SkillIconAura(114,
								_getBuffIconDuration));
					}
						break;
					case BRAVE_AURA: { // 冲击士气
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(5);
						pc.sendPackets(new S_SkillIconAura(116,
								_getBuffIconDuration));
					}
						break;
					case SHIELD: { // 保护罩
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-2);
						pc.sendPackets(new S_SkillIconShield(5,
								_getBuffIconDuration));
					}
						break;
					case SHADOW_ARMOR: { // 影之防护
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-3);
						pc.sendPackets(new S_SkillIconShield(3,
								_getBuffIconDuration));
					}
						break;
					case DRESS_DEXTERITY: { // 敏捷提升
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDex((byte) 2);
						pc.sendPackets(new S_Dexup(pc, 2, _getBuffIconDuration));
					}
						break;
					case DRESS_MIGHTY: { // 力量提升
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addStr((byte) 2);
						pc.sendPackets(new S_Strup(pc, 2, _getBuffIconDuration));
					}
						break;
					case SHADOW_FANG: { // 暗影之牙
						L1PcInstance pc = (L1PcInstance) cha;

						L1ItemInstance item = pc.getInventory().getItem(
								_itemobjid);
						if (item != null && item.getItem().getType2() == 1) {
							item.setSkillEnchant(pc, _skillId, 192 * 1000);
							pc.sendPackets(new S_SkillSound(pc.getId(), 2951));
							pc.broadcastPacket(new S_SkillSound(pc.getId(),
									2951));
						} else {
							pc.sendPackets(new S_ServerMessage(79));
						}
					}
						break;
					case ENCHANT_WEAPON: { // 拟似魔法武器
						L1PcInstance pc = (L1PcInstance) cha;

						L1ItemInstance item = pc.getInventory().getItem(
								_itemobjid);
						if (item != null && item.getItem().getType2() == 1) {
							pc.sendPackets(new S_ServerMessage(161, item
									.getLogName(), "$245", "$247"));
							item.setSkillEnchant(pc, _skillId, 1800 * 1000);
						} else {
							pc.sendPackets(new S_ServerMessage(79));
						}
					}
						break;
					case BLESSED_ARMOR: { // 铠甲护持
						L1PcInstance pc = (L1PcInstance) cha;
						L1ItemInstance item = pc.getInventory().getItem(
								_itemobjid);
						if (item != null && item.getItem().getType2() == 2
								&& item.getItem().getType() == 2) {
							pc.sendPackets(new S_ServerMessage(161, item
									.getLogName(), "$245", "$247"));
							item.setSkillEnchant(pc, _skillId, 1800 * 1000);// 测试修改成60秒
						} else {
							pc.sendPackets(new S_ServerMessage(79));
						}
					}
						break;
					case EARTH_BLESS: { // 大地的祝福
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-7);
						pc.sendPackets(new S_SkillIconShield(7,
								_getBuffIconDuration));
					}
						break;
					case RESIST_MAGIC: { // 魔法防御
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addMr(10);
						pc.sendPackets(new S_SPMR(pc));
					}
						break;
					case CLEAR_MIND: { // 净化精神
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addWis((byte) 3);
						pc.resetBaseMr();
					}
						break;
					case RESIST_ELEMENTAL: { // 属性防御
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addWind(10);
						pc.addWater(10);
						pc.addFire(10);
						pc.addEarth(10);
						pc.sendPackets(new S_OwnCharAttrDef(pc));
					}
						break;
					case BODY_TO_MIND: { // 心灵转换
						L1PcInstance pc = (L1PcInstance) cha;
						pc.setCurrentMp(pc.getCurrentMp() + 2);
					}
						break;
					case BLOODY_SOUL: { // 魂体转换
						L1PcInstance pc = (L1PcInstance) cha;
						pc.setCurrentMp(pc.getCurrentMp() + 15);
					}
						break;
					case ELEMENTAL_PROTECTION: { // 147单属性防御
						L1PcInstance pc = (L1PcInstance) cha;
						int attr = pc.getElfAttr();
						if (attr == 1) {
							pc.addEarth(50);
						} else if (attr == 2) {
							pc.addFire(50);
						} else if (attr == 4) {
							pc.addWater(50);
						} else if (attr == 8) {
							pc.addWind(50);
						}
					}
						break;
					case INVISIBILITY:
					case BLIND_HIDING: { // 隐身术,暗隐术
						L1PcInstance pc = (L1PcInstance) cha;
						pc.sendPackets(new S_Invis(pc.getId(), 1));
						pc.broadcastPacket(new S_RemoveObject(pc));
					}
						break;
					case IRON_SKIN: { // 钢铁防护
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-10);
						pc.sendPackets(new S_SkillIconShield(10,
								_getBuffIconDuration));
					}
						break;
					case EARTH_SKIN: { // 大地防护
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(-4);
						pc.sendPackets(new S_SkillIconShield(6,
								_getBuffIconDuration));
					}
						break;
					case PHYSICAL_ENCHANT_STR: { // 体魄强健术
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addStr((byte) 5);
						pc.sendPackets(new S_Strup(pc, 5, _getBuffIconDuration));
					}
						break;
					case PHYSICAL_ENCHANT_DEX: { // 通畅气脉术
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDex((byte) 5);
						pc.sendPackets(new S_Dexup(pc, 5, _getBuffIconDuration));
					}
						break;
					case FIRE_WEAPON: { // 火焰武器
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(4);
						pc.sendPackets(new S_SkillIconAura(147,
								_getBuffIconDuration));
					}
						break;
					case FIRE_BLESS: { // 烈炎气息
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(4);
						pc.sendPackets(new S_SkillIconAura(154,
								_getBuffIconDuration));
					}
						break;
					case BURNING_WEAPON: { // 烈炎武器
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addDmgup(6);
						pc.addHitup(3);
						pc.sendPackets(new S_SkillIconAura(162,
								_getBuffIconDuration));
					}
						break;
					case 8: { // 神圣武器
						L1PcInstance pc = (L1PcInstance) cha;

						if (pc.getWeapon() == null) { // 手上没有持有武器
							pc.sendPackets(new S_ServerMessage(79));
							return;
						}

						// 检查身上的道具
						List<L1ItemInstance> itemlist = pc.getInventory()
								.getItems();
						for (L1ItemInstance item : itemlist) {
							if (pc.getWeapon().equals(item)) { // 手上持有的武器
								pc.sendPackets(new S_ServerMessage(161, item
										.getLogName(), "$245", "$247"));
								item.setSkillEnchant(pc, _skillId, 1200 * 1000);
								pc.sendPackets(new S_SkillSound(pc.getId(),
										2165));
								pc.broadcastPacket(new S_SkillSound(pc.getId(),
										2165));
								return;
							}
						}
						// 检查身上的道具 end
					}
						break;
					case BLESS_WEAPON: { // 祝福魔法武器
						L1PcInstance pc = (L1PcInstance) cha;

						if (pc.getWeapon() == null) { // 手上没有持有武器
							pc.sendPackets(new S_ServerMessage(79));
							return;
						}

						// 检查身上的道具
						List<L1ItemInstance> itemlist = pc.getInventory()
								.getItems();
						for (L1ItemInstance item : itemlist) {
							if (pc.getWeapon().equals(item)) { // 手上持有的武器
								pc.sendPackets(new S_ServerMessage(161, item
										.getLogName(), "$245", "$247"));
								item.setSkillEnchant(pc, _skillId, 1200 * 1000);
								pc.sendPackets(new S_SkillSound(pc.getId(),
										2176));
								pc.broadcastPacket(new S_SkillSound(pc.getId(),
										2176));
								return;
							}
						}
						// 检查身上的道具 end
					}
						break;
					case WIND_SHOT: { // 风之神射
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addBowHitup(6);
						pc.sendPackets(new S_SkillIconAura(148,
								_getBuffIconDuration));
					}
						break;
					case STORM_EYE: { // 暴风之眼
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addBowHitup(2);
						pc.addBowDmgup(3);
						pc.sendPackets(new S_SkillIconAura(155,
								_getBuffIconDuration));
					}
						break;
					case STORM_SHOT: { // 暴风神射
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addBowDmgup(5);
						pc.addBowHitup(-3);
						pc.sendPackets(new S_SkillIconAura(165,
								_getBuffIconDuration));
					}
						break;
					case BERSERKERS: { // 狂暴术
						L1PcInstance pc = (L1PcInstance) cha;
						pc.addAc(10);
						pc.addDmgup(5);
						pc.addHitup(2);
					}
						break;
					case SHAPE_CHANGE: { // 变形术
						L1PcInstance pc = (L1PcInstance) cha;
						// 判断变身套装无法使用变卷
						if (!pc.hasSkillEffect(l1j.william.New_Id.Skill_AJ_0_10)
								&& pc.getMapId() != 5124)
						// 判断变身套装无法使用变卷 end
						{
							if (_user.getId() == pc.getId()) {
								pc.setPring(true);
								pc.sendPackets(new S_ShowPolyList(pc.getId()));
							}

						}
					}
						break;
					case ADVANCE_SPIRIT: { // 灵魂升华
						L1PcInstance pc = (L1PcInstance) cha;
						pc.setAdvenHp(pc.getBaseMaxHp() / 5);
						pc.setAdvenMp(pc.getBaseMaxMp() / 5);
						pc.addMaxHp(pc.getAdvenHp());
						pc.addMaxMp(pc.getAdvenMp());
						pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc
								.getMaxHp()));
						if (pc.isInParty()) { // 中
							pc.getParty().updateMiniHP(pc);
						}
						pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc
								.getMaxMp()));
					}
						break;
					case GREATER_HASTE: { // 54强力加速术
						L1PcInstance pc = (L1PcInstance) cha;
						if (pc.getHasteItemEquipped() > 0) {
							continue;
						}
						if (pc.getMoveSpeed() != 2) { // 中以外
							pc.setDrink(false);
							pc.setMoveSpeed(1);
							pc.sendPackets(new S_SkillHaste(pc.getId(), 1,
									_getBuffIconDuration));
						} else { // 中
							int skillNum = 0;
							if (cha.hasSkillEffect(SLOW)) {
								skillNum = SLOW;
							} else if (cha.hasSkillEffect(MASS_SLOW)) {
								skillNum = MASS_SLOW;
							} else if (cha.hasSkillEffect(ENTANGLE)) {
								skillNum = ENTANGLE;
							}
							if (skillNum != 0) {
								pc.removeSkillEffect(skillNum);
								pc.removeSkillEffect(GREATER_HASTE);
								pc.setMoveSpeed(0);
								continue;
							}
						}
					}
						break;
					case HOLY_WALK:
					case MOVING_ACCELERATION:
					case WIND_WALK: { // 神圣疾走,行走加速,风之疾走
						L1PcInstance pc = (L1PcInstance) cha;
						pc.setBraveSpeed(4);
						pc.sendPackets(new S_SkillBrave(pc.getId(), 4, _skill
								.getBuffDuration()));
					}
						break;

					case BLOODLUST: {// 血之渴望1
						L1PcInstance pc = (L1PcInstance) cha;
						L1BuffUtil.braveStart(pc);

						pc.setSkillEffect(L1SkillId.STATUS_BRAVE, 300 * 1000);
						// 修改血之渴望与官方同步 hjx1000
						pc.sendPackets(new S_SkillBrave(pc.getId(), 1, 300));
						pc.broadcastPacket(new S_SkillBrave(pc.getId(), 1, 0));
						pc.setBraveSpeed(1);
						break;
					}
					}// 变更为switch 4
				}

				// ●●●● NPC效果 ●●●●
				if (_calcType == PC_NPC || _calcType == NPC_NPC) {
					// ★★★ 系 ★★★
					if (_skillId == TAMING_MONSTER
							&& ((L1MonsterInstance) cha).getNpcTemplate()
									.isTamable()) { // 
						int petcost = 0;
						Object[] petlist = _user.getPetList().values()
								.toArray();
						for (Object pet : petlist) {
							// 现在
							petcost += ((L1NpcInstance) pet).getPetcost();
						}
						int charisma = _user.getCha();
						if (_player.isElf()) { // 
							charisma += 12;
						} else if (_player.isWizard()) { // 
							charisma += 6;
						}
						charisma -= petcost;
						if (charisma >= 6) { // 确认
							L1SummonInstance summon = new L1SummonInstance(
									_targetNpc, _user, false);
							_target = summon; // 入替
						} else {
							_player.sendPackets(new S_ServerMessage(319)); // \f1以上操。
						}
					} else if (_skillId == CREATE_ZOMBIE) { // 
						int petcost = 0;
						Object[] petlist = _user.getPetList().values()
								.toArray();
						for (Object pet : petlist) {
							// 现在
							petcost += ((L1NpcInstance) pet).getPetcost();
						}
						int charisma = _user.getCha();
						if (_player.isElf()) { // 
							charisma += 12;
						} else if (_player.isWizard()) { // 
							charisma += 6;
						}
						charisma -= petcost;
						if (charisma >= 6) { // 确认
							L1SummonInstance summon = new L1SummonInstance(
									_targetNpc, _user, true);
							_target = summon; // 入替
						} else {
							_player.sendPackets(new S_ServerMessage(319)); // \f1以上操。
						}
					} else if (_skillId == WEAK_ELEMENTAL) { //  
						if (cha instanceof L1MonsterInstance) {
							L1Npc npcTemp = ((L1MonsterInstance) cha)
									.getNpcTemplate();
							if (npcTemp.get_weakearth() == 1) { // 地
								cha.broadcastPacket(new S_SkillSound(cha
										.getId(), 2169));
							} else if (npcTemp.get_weakwater() == 1) { // 水
								cha.broadcastPacket(new S_SkillSound(cha
										.getId(), 2167));
							} else if (npcTemp.get_weakfire() == 1) { // 火
								cha.broadcastPacket(new S_SkillSound(cha
										.getId(), 2166));
							} else if (npcTemp.get_weakwind() == 1) { // 风
								cha.broadcastPacket(new S_SkillSound(cha
										.getId(), 2168));
							}
						}
					}
				}

				// ■■■■ 个别处理 ■■■■

				if (_skill.getType() == L1Skills.TYPE_HEAL
						&& _calcType == PC_NPC && undeadType == 1) {
					dmg *= -1; // 、回复系。
				}

				if (_skill.getType() == L1Skills.TYPE_HEAL
						&& _calcType == PC_NPC && undeadType == 3) {
					dmg = 0; // 、系
				}

				if (cha instanceof L1TowerInstance && dmg < 0) { // 使用
					dmg = 0;
				}

				if (dmg != 0 || drainMana != 0) {
					_magic.commit(dmg, drainMana); // 系、回复系值。
				}

				// 系他、别途回复场合（V-T）
				if (heal > 0) {
					if ((heal + _user.getCurrentHp()) > _user.getMaxHp()) {
						_user.setCurrentHp(_user.getMaxHp());
					} else {
						_user.setCurrentHp(heal + _user.getCurrentHp());
					}
				}

				if (cha instanceof L1PcInstance) { // PC、AC送信
					L1PcInstance pc = (L1PcInstance) cha;
					pc.sendPackets(new S_OwnCharAttrDef(pc));
					pc.sendPackets(new S_OwnCharStatus(pc));
					sendHappenMessage(pc); // 送信
				}

				addMagicList(cha, false); // 魔法效果时间设定
			}

			if (_skillId == DETECTION || _skillId == COUNTER_DETECTION || _skillId == FREEZING_BREATH) { // 、
				detection(_player);
			}

		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 解除返。
	 */
	private boolean isNotCancelable(int skillNum) {
		return skillNum == ENCHANT_WEAPON || skillNum == BLESSED_ARMOR
				|| skillNum == ABSOLUTE_BARRIER || skillNum == ADVANCE_SPIRIT
				|| skillNum == SHOCK_STUN || skillNum == SHADOW_FANG
				|| skillNum == REDUCTION_ARMOR || skillNum == SOLID_CARRIAGE
				|| skillNum == COUNTER_BARRIER;
	}

	private void detection(L1PcInstance pc) {
		if (!pc.isGmInvis() && pc.isInvisble()) { // 自分
			pc.delInvis();
			pc.beginInvisTimer();
		}

		for (L1PcInstance tgt : L1World.getInstance().getVisiblePlayer(pc)) {
			if (!tgt.isGmInvis() && tgt.isInvisble()) {
				tgt.delInvis();
			}
		}
		L1WorldTraps.getInstance().onDetection(pc);
	}

	/**
	 * 判断目标的技能
	 * 
	 * QQ：1043567675 by：亮修改 2020年5月3日 上午10:48:12
	 */
	private boolean isTargetCalc(L1Character cha) {
		// 三重矢、屠宰者、暴击、骷髅毁坏
		if ((this._user instanceof L1PcInstance)
				&& ((this._skillId == TRIPLE_ARROW)
						|| (this._skillId == FOE_SLAYER)
						|| (this._skillId == SMASH) || (this._skillId == BONE_BREAK))) {
			return true;
		}
		// 攻击魔法Non
		if (_skill.getTarget().equals("attack") && _skillId != 18) { // 攻击魔法
			if (_calcType == PC_PC || // 对像PC
					(_calcType == PC_NPC && cha instanceof L1PetInstance) || // 对像
					(_calcType == PC_NPC && cha instanceof L1SummonInstance)) { // 对像
				if (_player.getZoneType() == 1 || cha.getZoneType() == 1 // 攻击侧攻击侧
						|| _player.checkNonPvP(_player, cha)) { // Non-PvP设定
					return false;
				}
			}
		}

		// 自分自身对像外
		if (_skillId == FOG_OF_SLEEPING && _user.getId() == cha.getId()) {
			return false;
		}

		// 自分自身自分对像外
		if (_skillId == MASS_SLOW) {
			if (_user.getId() == cha.getId()) {
				return false;
			}
			if (cha instanceof L1SummonInstance) {
				L1SummonInstance summon = (L1SummonInstance) cha;
				if (_user.getId() == summon.getMaster().getId()) {
					return false;
				}
			} else if (cha instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) cha;
				if (_user.getId() == pet.getMaster().getId()) {
					return false;
				}
			}
		}

		// 自分自身对像（同时员）
		if (_skillId == MASS_TELEPORT) {
			if (_user.getId() != cha.getId()) {
				return false;
			}
		}

		return true;
	}

	// 对必失败返
	private boolean isTargetFailure(L1Character cha) {
		boolean isTU = false;
		boolean isErase = false;
		boolean isManaDrain = false;
		int undeadType = 0;

		if (cha instanceof L1TowerInstance) { // 确率系无效
			return true;
		}

		if (cha instanceof L1PcInstance) { // 对PC场合
			if (_calcType == PC_PC && _player.checkNonPvP(_player, cha)) { // Non-PvP设定
				L1PcInstance pc = (L1PcInstance) cha;
				if (_player.getId() == pc.getId()
						|| (pc.getClanid() != 0 && _player.getClanid() == pc
								.getClanid())) {
					return false;
				}
				return true;
			}
			return false;
		}

		if (cha instanceof L1MonsterInstance) { // 可能判定
			isTU = ((L1MonsterInstance) cha).getNpcTemplate().get_IsTU();
		}

		if (cha instanceof L1MonsterInstance) { // 可能判定
			isErase = ((L1MonsterInstance) cha).getNpcTemplate().get_IsErase();
		}

		if (cha instanceof L1MonsterInstance) { // 判定
			undeadType = ((L1MonsterInstance) cha).getNpcTemplate()
					.get_undead();
		}

		// 可能？
		if (cha instanceof L1MonsterInstance) {
			isManaDrain = true;
		}
		/*
		 * 成功除外条件１：T-U成功、对像。 成功除外条件２：T-U成功、对像无效。
		 * 成功除外条件３：、、、、无效
		 * 成功除外条件４：成功、以外场合
		 */
		if ((_skillId == TURN_UNDEAD && (undeadType == 0 || undeadType == 2))
				|| (_skillId == TURN_UNDEAD && isTU == false)
				|| ((_skillId == ERASE_MAGIC || _skillId == SLOW
						|| _skillId == MANA_DRAIN || _skillId == MASS_SLOW
						|| _skillId == ENTANGLE || _skillId == CURSE_PARALYZE || _skillId == SILENCE) && isErase == false) // 新增木乃伊、魔封无效
				|| (_skillId == MANA_DRAIN && isManaDrain == false)) {
			return true;
		}
		return false;
	}
}
