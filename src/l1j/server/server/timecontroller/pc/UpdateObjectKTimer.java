package l1j.server.server.timecontroller.pc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
//import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.world.WorldKnight;


/**
 * PC 可见物更新处理 时间轴(骑士)
 * 
 * @author dexc
 * 
 */
public class UpdateObjectKTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(UpdateObjectKTimer.class);


//	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 300;// 0.30秒
		GeneralThreadPool.getInstance().scheduleAtFixedRate(this, timeMillis,
				timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1PcInstance> allPc = WorldKnight.get().all();
			// 不包含元素
			if (allPc.isEmpty()) {
				return;
			}

			for (final Iterator<L1PcInstance> iter = allPc.iterator(); iter
					.hasNext();) {
				final L1PcInstance tgpc = iter.next();
				if (PcCheck.check(tgpc)) {
					tgpc.updateObject();
				}
			}

			/*
			 * for (final L1PcInstance iter : allPc) { if
			 * (UpdateObjectCheck.check(iter)) { iter.updateObject(); } }
			 */

		} catch (final Exception e) {
/*			_log.error("Pc 可见物更新处理时间轴(骑士)异常重启", e);
			PcOtherThreadPool.get().cancel(_timer, false);
			final UpdateObjectKTimer objectKTimer = new UpdateObjectKTimer();
			objectKTimer.start();*/
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
