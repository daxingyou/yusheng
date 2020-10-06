package l1j.server.server.timecontroller;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1NpcInstance;

/**
 * NPC工作时间轴
 *
 * @author dexc
 *
 */
public class NpcWorkTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(NpcWorkTimer.class);


	private ScheduledFuture<?> _timer;

	private static final Map<L1NpcInstance, Integer> _map = 
		new HashMap<L1NpcInstance, Integer>();

	public static void put(L1NpcInstance npc, Integer time) {
		_map.put(npc, time);
	}

	public void start() {
		// NPC工作设置资料
		final int timeMillis = 2000;
		_timer = GeneralThreadPool.getInstance().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			// 不包含元素
			if (_map.isEmpty()) {
				return;
			}
			for (final L1NpcInstance npc : _map.keySet()) {
				Integer time = _map.get(npc);
				//System.out.println(npc.getNpcTemplate().get_name() + "/"+time);
				time -= 2;
				if (time > 0) {
					_map.put(npc, time);
				} else {
					startWork(npc);
				}
				Thread.sleep(50);
			}

		} catch (final Exception e) {
			_log.info("NPC工作时间轴异常重启", e);
/*			GeneralThreadPool.getInstance().cancel(_timer, false);
			final NpcWorkTimer workTimer = new NpcWorkTimer();
			workTimer.start();*/
		}
	}

	private static void startWork(final L1NpcInstance npc) {
		try {
			if (npc != null) {
				int time = npc.WORK.workTime();// 重新取回工作间格时间

				if (time != 0) {
					npc.WORK.work(npc);// 执行动作
					_map.put(npc, time);// 重新加入计时器
				}
			}
			
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
