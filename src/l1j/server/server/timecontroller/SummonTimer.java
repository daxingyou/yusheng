package l1j.server.server.timecontroller;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.world.L1World;

/**
 * 召喚獸處理時間軸
 * 
 * @author dexc
 * 
 */
public class SummonTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(SummonTimer.class);


	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 60 * 1000;// 60秒
		_timer = GeneralThreadPool.getInstance().scheduleAtFixedRate(this, timeMillis,
				timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1SummonInstance> allPet = L1World.getInstance().getAllSummons();
			// 不包含元素
			if (allPet.isEmpty()) {
				return;
			}

			for (final Iterator<L1SummonInstance> iter = allPet.iterator(); iter
					.hasNext();) {
				final L1SummonInstance summon = iter.next();
				final int time = summon.get_time() - 60;
				// time -= 60;
				if (time <= 0) {
					outSummon(summon);

				} else {
					summon.set_time(time);
				}
				Thread.sleep(50);
			}

		} catch (final Exception e) {
			_log.info("召喚獸處理時間軸異常重啟", e);
/*			GeneralThreadPool.getInstance().cancel(_timer, false);
			final SummonTimer summon_Timer = new SummonTimer();
			summon_Timer.start();*/
		}
	}

	/**
	 * 移除召喚獸
	 * 
	 * @param tgpc
	 */
	private static void outSummon(final L1SummonInstance summon) {
		try {
			if (summon != null) {
				if (summon.destroyed()) {
					return;
				}
				summon.liberate();
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
