package l1j.server.server.timecontroller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.TimerTask;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.timecontroller.server.GetNowTime;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.world.L1World;

public class updateDBTimer extends TimerTask {

/*
	private ScheduledFuture<?> _timer;*/

	public void start() {
		final int timeMillis = 60 * 1000;// 1分钟
/*		_timer = */GeneralThreadPool.getInstance().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}
	
	@Override
	public void run() {
		try {
			if (GetNowTime.GetNowHour() == 0 && GetNowTime.GetNowMinute() == 0){
				for(L1PcInstance pc:L1World.getInstance().getAllPlayers()){
					pc.setJieQuBiaoCheCount(0);
				}
				updateBiaoCheCount();
			}
		} catch (final Exception e) {
/*			GeneralThreadPool.getInstance().cancel(_timer, false);
			final CNTimer timer = new CNTimer();
			timer.start();*/
		}
	}
	
	private void updateBiaoCheCount() {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE characters SET biaoche=0");
			pstm.execute();
		} catch (Exception e) {
			//_log.error(e.getLocalizedMessage(), e);
		} finally{
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
