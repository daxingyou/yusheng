package l1j.server.server.timecontroller;

import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_TrueTarget;
import l1j.server.server.world.L1World;

public class AIcheckTimer extends TimerTask{

	private static final Log _log = LogFactory.getLog(AIcheckTimer.class);

	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 5000;// 5秒
		_timer = GeneralThreadPool.getInstance().scheduleAtFixedRate(this, timeMillis,
				timeMillis);
	}
	@Override
	public void run() {
		try {
			final Collection<L1PcInstance> allPc = L1World.getInstance().getAllPlayers();
			// 不包含元素
			if (allPc.isEmpty()) {
				return;
			}
			for (L1PcInstance pc : allPc) {
				if (pc.getSum()!=-1) {
					String msg ="请输入验证码"+pc.getSum();
					pc.sendPackets(new S_TrueTarget(pc.getId(), pc
							.getId(), msg));
				}
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
/*			GeneralThreadPool.getInstance().cancel(_timer, false);
			final AIcheckTimer aicheck_Timer = new AIcheckTimer();
			aicheck_Timer.start();*/
		}

		
	}

}
