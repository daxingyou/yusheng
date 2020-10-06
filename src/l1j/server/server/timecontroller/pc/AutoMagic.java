package l1j.server.server.timecontroller.pc;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillUse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 状态
 * 
 */
public class AutoMagic {

	private static final Log _log = LogFactory.getLog(AutoMagic.class);
	final static int time = 5000; // 自动状态监测时间5秒
	private static final int skillIds[] = new int[] { 26, 42, 43, 48, 79, 151,
			158, 148, 115, 117 };

	/**
	 * 自动施法延时用
	 */
	private AutoMagic() {
	}

	static class AutoMagicTimer implements Runnable {

		private L1PcInstance _pc;

		public AutoMagicTimer(final L1PcInstance pc, final int[] skillIds) {
			_pc = pc;
		}

		@Override
		public void run() {
			stopDelayTimer();
		}

		public void stopDelayTimer() {
			if (_pc == null) {
				return;
			}
			if (_pc.getOnlineStatus() == 0) {
				return;
			}
			if (_pc.isDead()) {
				return;
			}

			if (_pc.isTeleport()) { // 传送中
				return;
			}
			// 技能延迟状态
			if (_pc.isSkillDelay()) {
				return;
			}

			if (_pc.isParalyzed()) {
				return;
			}

			if (!_pc.isActived()) {
				if (_pc.isskillAuto()) {
					if (_pc.getSkillEffectTimeSec(115) <= 0) {
						automagic(_pc, skillIds);
						for (final int element : skillIds) {
							int skillId = element;
							if (skillId == 148) {
								if (_pc.isElf()) {
									skillId = 149;
								}
							}
							new L1SkillUse().handleCommands(_pc, skillId,
									_pc.getId(), _pc.getX(), _pc.getY(), null,
									1800, L1SkillUse.TYPE_GMBUFF);
							_pc.getInventory().consumeItem(44070, 1);
						}
					}
				}
			}
		}
	}

	/**
	 * 自动施法延时用
	 * 
	 * @param pc
	 * @param time
	 */
	public static void automagic(final L1PcInstance pc, final int[] skillIds) {
		try {
			GeneralThreadPool.getInstance().schedule(
					new AutoMagicTimer(pc, skillIds), time);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
