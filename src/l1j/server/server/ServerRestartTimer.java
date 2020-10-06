package l1j.server.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.Config;

/**
 * 自动重启
 * 
 * @author dexc
 * 
 */
public class ServerRestartTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(ServerRestartTimer.class);

	private ScheduledFuture<?> _timer;

	private static final ArrayList<Calendar> _restartList = new ArrayList<Calendar>();

	private static Calendar _restart = null;

	private static String _string = "yyyy/MM/dd HH:mm:ss";

	private static String _startTime = null;

	private static String _restartTime = null;

	/**
	 * 重新启动时间
	 * 
	 * @return
	 */
	public static String get_restartTime() {
		return _restartTime;
	}

	/**
	 * 启动时间
	 * 
	 * @return
	 */
	public static String get_startTime() {
		return _startTime;
	}

	/**
	 * 距离关机小逾10分钟
	 * 
	 * @return
	 */
	public static boolean isRtartTime() {
		if (_restart == null) {
			return false;
		}
		return (_restart.getTimeInMillis() - System.currentTimeMillis()) <= (10 * 60 * 1000);
	}

	private static Calendar timestampToCalendar() {
		final TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		final Calendar cal = Calendar.getInstance(_tz);

		return cal;
	}

	public void start() {
		if (Config.AUTORESTART == null) {
			return;
		}

		final Calendar cals = timestampToCalendar();

		if (_startTime == null) {
			final String nowDate = new SimpleDateFormat(_string).format(cals
					.getTime());
			_startTime = nowDate;
		}

		if (Config.AUTORESTART != null) {
			final String HH = new SimpleDateFormat("HH").format(cals.getTime());
			int HHi = Integer.parseInt(HH);
			final String mm = new SimpleDateFormat("mm").format(cals.getTime());
			int mmi = Integer.parseInt(mm);

			for (String hm : Config.AUTORESTART) {
				String[] hm_b = hm.split(":");
				String hh_b = hm_b[0];
				String mm_b = hm_b[1];

				int newHH = Integer.parseInt(hh_b);
				int newMM = Integer.parseInt(mm_b);

				final Calendar cal = timestampToCalendar();

				int xh = -1;
				int xhh = newHH - HHi;
				if (xhh > 0) {
					xh = xhh;

				} else {
					xh = (24 - HHi) + newHH;
				}

				int xm = newMM - mmi;

				cal.add(Calendar.HOUR, xh);
				cal.add(Calendar.MINUTE, xm);

				_restartList.add(cal);
			}

			for (Calendar tmpCal : _restartList) {
				if (_restart == null) {
					_restart = tmpCal;

				} else {
					boolean re = tmpCal.before(_restart);
					if (re) {
						_restart = tmpCal;
					}
				}
			}

		}

		final String restartTime = new SimpleDateFormat(_string)
				.format(_restart.getTime());
		_restartTime = restartTime;

		_log.info("\n\r--------------------------------------------------"
				+ "\n\r       开机完成时间为:" + _startTime + "\n\r       设置关机时间为:"
				+ _restartTime
				+ "\n\r--------------------------------------------------");

		final int timeMillis = 60 * 1000;// 1分钟
		_timer = GeneralThreadPool.getInstance().scheduleAtFixedRate(this, timeMillis,
				timeMillis);
	}

	@Override
	public void run() {
		try {
			startCommand();

		} catch (final Exception e) {
			_log.info("自动重启时间轴异常重启"+e.getLocalizedMessage());
			GeneralThreadPool.getInstance().cancel(_timer, false);
			final ServerRestartTimer restartTimer = new ServerRestartTimer();
			restartTimer.start();
		}
	}

	private void startCommand() {
		if (Config.AUTORESTART != null) {
			final Calendar cals = Calendar.getInstance();
			cals.setTimeInMillis(System.currentTimeMillis());
			if (_restart.before(cals)) {
				Shutdown.getInstance().startShutdown(null, 300, true);
			}
		}
	}
}
