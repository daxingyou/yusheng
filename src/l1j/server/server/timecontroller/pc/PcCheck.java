package l1j.server.server.timecontroller.pc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_SystemMessage;

/**
 * PC 可见物更新处理 判断
 * 
 * @author dexc
 * 
 */
public class PcCheck {

	private static final Log _log = LogFactory.getLog(PcCheck.class);

	private static final int skillIds[] = new int[] { 26, 42, 43, 48, 79, 151,
			158, 148, 115, 117 };

	/**
	 * 判断
	 * 
	 * @param tgpc
	 * @return true:执行 false:不执行
	 */
	public static boolean check(final L1PcInstance tgpc) {
		try {
			if (tgpc == null) {
				return false;
			}

			if (tgpc.getOnlineStatus() == 0) {
				return false;
			}

			if (tgpc.getNetConnection() == null) {
				return false;
			}

			if (tgpc.isTeleport()) {
				return false;
			}

			if (tgpc.isskillAuto()) {
				if (tgpc.getOnlineStatus() == 0) {
					return false;
				}
				if (tgpc.isDead()) {
					return false;
				}

				if (tgpc.isTeleport()) { // 传送中
					return false;
				}
				// 技能延迟状态
				if (tgpc.isSkillDelay()) {
					return false;
				}

				if (tgpc.isParalyzed()) {
					return false;
				}
				if (tgpc.isskillAuto()) {
					if (tgpc.getInventory().checkItem(44070, 10)) {
						if (tgpc.getSkillEffectTimeSec(115) <= 0) {
							AutoMagic.automagic(tgpc, skillIds);
							tgpc.getInventory().consumeItem(44070, 1);
							for (final int element : skillIds) {
								int skillId = element;
								if (skillId == 148) {
									if (tgpc.isElf()) {
										skillId = 149;
									}
								}
								new L1SkillUse().handleCommands(tgpc, skillId,
										tgpc.getId(), tgpc.getX(), tgpc.getY(),
										null, 1800, L1SkillUse.TYPE_GMBUFF);
							}
						}
					} else {
						tgpc.setskillAuto(false);
						tgpc.sendPackets(new S_SystemMessage("元宝不足10个.功能自动关闭"));
					}
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}
}
