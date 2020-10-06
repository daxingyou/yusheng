package l1j.server.server.timecontroller.pc;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChangeName;
import l1j.server.server.serverpackets.S_SkillSound;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 状态
 * 
 */
public class PcName {

	private static final Log _log = LogFactory.getLog(PcName.class);
	
	final static int time = 1000; // 自动状态监测时间5秒

	 static int _mode = 0;
	 
	/**
	 * 自动施法延时用
	 */
	private PcName() {
	}

	static class AutoMagicTimer implements Runnable {

		private L1PcInstance _pc;

		public AutoMagicTimer(final L1PcInstance pc) {
			_pc = pc;
		}

		@Override
		public void run() {
			 try {
	                while (_pc.isGm()) {
	                    if (_pc.getOnlineStatus() != 1) {
	                        break;
	                    }
	                    if (_pc.getNetConnection() == null) {
	                        break;
	                    }
	                    if (!_pc.isGm()) {
	                        _pc.sendPacketsAll(new S_ChangeName(_pc.getId(), _pc
	                                .getName()));
	                        break;
	                    }
	                    _mode++;
	                    if (_mode > 10) {
	                        _mode = 0;
	                    }

	                    _pc.sendPacketsAll(new S_ChangeName(_pc.getId(), _pc
	                            .getName(), _mode));

	                    // 送出封包
	                    _pc.sendPacketsAll(new S_SkillSound(_pc.getId(), 5288));
	                    Thread.sleep(5000);
	                }

	            } catch (final InterruptedException e) {
	                _log.error(e.getLocalizedMessage(), e);
	            }
	        }
		}

	/**
	 * 自动施法延时用
	 * 
	 * @param pc
	 * @param time
	 */
	public static void automagic(final L1PcInstance pc) {
		try {
			 final AutoMagicTimer colorTime = new AutoMagicTimer(pc);
	            GeneralThreadPool.getInstance().execute(colorTime);

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
