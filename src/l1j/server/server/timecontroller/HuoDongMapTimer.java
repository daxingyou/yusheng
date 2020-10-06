package l1j.server.server.timecontroller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.TimerTask;

import l1j.server.Config;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HuoDongMapTimer extends TimerTask{

	private static final Log _log = LogFactory.getLog(HuoDongMapTimer.class);
	public static int _startTime = -1;
	public static int _stopTime = -1;
	public static boolean IsStart = false;
	public void start() {
		final int timeMillis = 60 * 1000;// 60秒
		GeneralThreadPool.getInstance().scheduleAtFixedRate(this, timeMillis,timeMillis);
	}
	@Override
	public void run() {
		try {
			if (checkTime(_startTime)){
				if (!IsStart){
					IsStart = true;
					_stopTime = getNextTime(Config.HUODONGNEXTTIME);
					L1World.getInstance().broadcastServerMessage("勇闯神秘岛活动已经开放.");
					Thread.sleep(500);
					L1World.getInstance().broadcastServerMessage("勇闯神秘岛活动已经开放.");
					Thread.sleep(500);
					L1World.getInstance().broadcastServerMessage("勇闯神秘岛活动已经开放.");
				}
			}else if (IsStart && checkTime(_stopTime)){
				IsStart = false;
				_stopTime = -1;
				_startTime = -1;
				L1World.getInstance().broadcastServerMessage("勇闯神秘岛活动已经结束.");
				teleteAll();
				
				this.cancel();
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
	
	private void teleteAll(){
		for (final L1PcInstance pc : L1World.getInstance().getAllPlayers()){
			if (pc.getMapId() == Config.HUODONGMAPID){
				final Random random = new Random();
				int rndx = random.nextInt(4);
				int rndy = random.nextInt(4);
				int locx = 33503 + rndx;
				int locy = 32764 + rndy;
				short mapid = 4;
				L1Teleport.teleport(pc, locx, locy, mapid, 5, true);
			}
		}
	}
	private Calendar getRealTime() {
		TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(_tz);
		return cal;
	}

	private boolean checkTime(final int time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		Calendar realTime = getRealTime();
		int nowTime = Integer.valueOf(sdf.format(realTime.getTime()));
		return time == nowTime;
	}
	
	private int getNextTime(final int m) {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		Calendar realTime = getRealTime();
		realTime.add(Calendar.MINUTE, m);
		int nowTime = Integer.valueOf(sdf.format(realTime.getTime()));
		return nowTime;
	}

}

