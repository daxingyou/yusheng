package l1j.server.server.timecontroller;

import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.data.npc.shop.Npc_TimeCash;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.TuiguangTable;
import l1j.server.server.datatables.lock.CnReading;


/**
 * VIP计时时间轴
 * @author dexc
 *
 */
public class CNTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(CNTimer.class);

/*
	private ScheduledFuture<?> _timer;*/

	public void start() {
		final int timeMillis = 60 * 1000;// 1分钟
/*		_timer = */GeneralThreadPool.getInstance().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}
	
	@Override
	public void run() {
		try {
			CnReading.get().load();
			TuiguangTable.getInstance().reload();
		} catch (final Exception e) {
			_log.info("商城系统时间轴异常重启", e);
/*			GeneralThreadPool.getInstance().cancel(_timer, false);
			final CNTimer timer = new CNTimer();
			timer.start();*/
		}
	}

}
