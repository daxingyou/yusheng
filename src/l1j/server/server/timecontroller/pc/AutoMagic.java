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
		private int _skillId;

		public AutoMagicTimer(final L1PcInstance pc,int skillId) {
			_pc = pc;
			this._skillId = skillId;
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

			if (this._skillId == 46 && this._pc.isskill46())
				AutoMagic.automagic(this._pc, 46);
			if (this._skillId == 132 && this._pc.isskill132())
				AutoMagic.automagic(this._pc, 132);
			if (this._skillId == 187 && this._pc.isskill187())
				AutoMagic.automagic(this._pc, 187);

			if (this._pc.is_now_target() == null)
				return;
			if (this._skillId == 46 &&!this._pc.isAttackPosition(this._pc.is_now_target().getX(),this._pc.is_now_target().getY(), 3))
				return;
			if (this._skillId == 132 && !this._pc.isAttackPosition(this._pc.is_now_target().getX(),this._pc.is_now_target().getY(), 10))
				return;
			if (this._skillId == 187 && !this._pc.isAttackPosition(this._pc.is_now_target().getX(), this._pc.is_now_target().getY(), 2))
				return;

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

			if (this._pc.getCurrentMp() < 100 && this._skillId != 187) {
			        return;
			}
			L1SkillUse skilluse = new L1SkillUse();
			skilluse.handleCommands(this._pc, this._skillId, this._pc.is_now_target().getId(), this._pc.is_now_target().getX(),this._pc.is_now_target().getY(),null,0,L1SkillUse.TYPE_NORMAL);

			if (!_pc.isActived()) {
				if (_pc.isskillAuto()) {
					if (_pc.getSkillEffectTimeSec(115) <= 0) {
						automagic(_pc, 0);
						int skillId;
						for (final int element : skillIds) {
							skillId = element;
							if (skillId == 148) {
								if (_pc.isElf()) {
									skillId = 149;
								}
							}
							skilluse.handleCommands(_pc, skillId,
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
	 *  skillIds = 0代表不装配技能
	 * @param pc
	 * @param skillIds
	 */
	public static void automagic(final L1PcInstance pc, Integer skillIds) {
		try {
			GeneralThreadPool.getInstance().schedule(
					new AutoMagicTimer(pc, skillIds), time);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

}
