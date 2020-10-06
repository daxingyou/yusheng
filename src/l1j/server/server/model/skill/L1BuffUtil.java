package l1j.server.server.model.skill;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillBrave;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SkillSound;
import static l1j.server.server.model.skill.L1SkillId.*;

public class L1BuffUtil {

	/**
	 * 無法使用藥水
	 * 
	 * @param pc
	 * @return true:可以使用 false:無法使用
	 */
	public static boolean stopPotion(final L1PcInstance pc) {
		if (pc.hasSkillEffect(L1SkillId.DECAY_POTION)) { // 藥水霜化術
			// 698 喉嚨灼熱，無法喝東西。
			pc.sendPackets(new S_ServerMessage(698));
			return false;
		}
		return true;
	}

	/**
	 * 解除魔法技能绝对屏障
	 * 
	 * @param pc
	 */
	public static void cancelAbsoluteBarrier(final L1PcInstance pc) { // 解除魔法技能绝对屏障
		if (pc.hasSkillEffect(L1SkillId.ABSOLUTE_BARRIER)) {
			pc.killSkillEffectTimer(L1SkillId.ABSOLUTE_BARRIER);
			pc.startHpRegeneration();
			pc.startMpRegeneration();
		}
	}

	public static void haste(L1PcInstance pc, int timeMillis) {
		/* 已存在加速狀態消除 */
		if (pc.hasSkillEffect(HASTE) || pc.hasSkillEffect(GREATER_HASTE)
				|| pc.hasSkillEffect(STATUS_HASTE)) {
			if (pc.hasSkillEffect(HASTE)) { // 加速術
				pc.killSkillEffectTimer(HASTE);
			} else if (pc.hasSkillEffect(GREATER_HASTE)) { // 強力加速術
				pc.killSkillEffectTimer(GREATER_HASTE);
			} else if (pc.hasSkillEffect(STATUS_HASTE)) { // 自我加速藥水
				pc.killSkillEffectTimer(STATUS_HASTE);
			}
		}
		/* 抵消緩速魔法效果 緩速術 集體緩速術 地面障礙 */
		if (pc.hasSkillEffect(SLOW) || pc.hasSkillEffect(MASS_SLOW)
				|| pc.hasSkillEffect(ENTANGLE)) {
			if (pc.hasSkillEffect(SLOW)) { // 緩速術
				pc.killSkillEffectTimer(SLOW);
			} else if (pc.hasSkillEffect(MASS_SLOW)) { // 集體緩速術
				pc.killSkillEffectTimer(MASS_SLOW);
			} else if (pc.hasSkillEffect(ENTANGLE)) { // 地面障礙
				pc.killSkillEffectTimer(ENTANGLE);
			}
			pc.sendPackets(new S_SkillHaste(pc.getId(), 0, 0));
			pc.broadcastPacket(new S_SkillHaste(pc.getId(), 0, 0));
		}

		pc.setSkillEffect(STATUS_HASTE, timeMillis);

		pc.sendPackets(new S_SkillSound(pc.getId(), 191));
		pc.broadcastPacket(new S_SkillSound(pc.getId(), 191));
		pc.sendPackets(new S_SkillHaste(pc.getId(), 1, timeMillis / 1000));
		pc.broadcastPacket(new S_SkillHaste(pc.getId(), 1, 0));
		pc.sendPackets(new S_ServerMessage(184)); // \f1你的動作突然變快。 */
		pc.setMoveSpeed(1);
	}

	public static void brave(L1PcInstance pc, int timeMillis) {
		// 消除重複狀態
		if (pc.hasSkillEffect(STATUS_BRAVE)) { // 勇敢藥水 1.33倍
			pc.killSkillEffectTimer(STATUS_BRAVE);
		}
		if (pc.hasSkillEffect(STATUS_ELFBRAVE)) { // 精靈餅乾 1.15倍
			pc.killSkillEffectTimer(STATUS_ELFBRAVE);
		}
		if (pc.hasSkillEffect(HOLY_WALK)) { // 神聖疾走 移速1.33倍
			pc.killSkillEffectTimer(HOLY_WALK);
		}
		if (pc.hasSkillEffect(MOVING_ACCELERATION)) { // 行走加速 移速1.33倍
			pc.killSkillEffectTimer(MOVING_ACCELERATION);
		}
		if (pc.hasSkillEffect(WIND_WALK)) { // 風之疾走 移速1.33倍
			pc.killSkillEffectTimer(WIND_WALK);
		}

		pc.setSkillEffect(STATUS_BRAVE, timeMillis);

		int objId = pc.getId();
		pc.sendPackets(new S_SkillSound(objId, 751));
		pc.broadcastPacket(new S_SkillSound(objId, 751));
		pc.sendPackets(new S_SkillBrave(objId, 1, timeMillis / 1000));
		pc.broadcastPacket(new S_SkillBrave(objId, 1, 0));
		pc.setBraveSpeed(1);
	}

	/**
	 * 勇敢效果 抵销对应技能
	 * 
	 * @param pc
	 */
	public static void braveStart(final L1PcInstance pc) {
		/*
		 * { HOLY_WALK, MOVING_ACCELERATION, WIND_WALK, STATUS_BRAVE,
		 * STATUS_BRAVE2, STATUS_ELFBRAVE, STATUS_RIBRAVE, BLOODLUST },
		 */

		// 解除神圣疾走
		if (pc.hasSkillEffect(HOLY_WALK)) {
			pc.killSkillEffectTimer(HOLY_WALK);
			pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
			pc.setBraveSpeed(0);
		}

		// 解除行走加速
		if (pc.hasSkillEffect(MOVING_ACCELERATION)) {
			pc.killSkillEffectTimer(MOVING_ACCELERATION);
			pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
			pc.setBraveSpeed(0);
		}

		// 解除风之疾走
		if (pc.hasSkillEffect(WIND_WALK)) {
			pc.killSkillEffectTimer(WIND_WALK);
			pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
			pc.setBraveSpeed(0);
		}

		// 解除勇敢药水效果
		if (pc.hasSkillEffect(STATUS_BRAVE)) {
			pc.killSkillEffectTimer(STATUS_BRAVE);
			pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
			pc.setBraveSpeed(0);
		}

		// 解除精灵饼干效果
		if (pc.hasSkillEffect(STATUS_ELFBRAVE)) {
			pc.killSkillEffectTimer(STATUS_ELFBRAVE);
			pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
			pc.setBraveSpeed(0);
		}

		// 解除血之渴望
		if (pc.hasSkillEffect(BLOODLUST)) {
			pc.killSkillEffectTimer(BLOODLUST);
			pc.sendPacketsAll(new S_SkillBrave(pc.getId(), 0, 0));
			pc.setBraveSpeed(0);
		}
	}
}
