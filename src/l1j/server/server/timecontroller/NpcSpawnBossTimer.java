package l1j.server.server.timecontroller;

import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1NpcInstance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * NPC(BOSS)召唤时间时间轴
 * @author dexc
 *
 */
public class NpcSpawnBossTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(NpcSpawnBossTimer.class);

	private ScheduledFuture<?> _timer;

	public static final Map<L1NpcInstance, Long> MAP = 
		new ConcurrentHashMap<L1NpcInstance, Long>();

	/*private static final ArrayList<L1NpcInstance> REMOVE = 
		new ArrayList<L1NpcInstance>();*/
	
	public void start() {
		final int timeMillis = 1000;// 1分钟
		_timer = GeneralThreadPool.getInstance().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			// 不包含元素
			if (MAP.isEmpty()) {
				return;
			}
			//System.out.println("需要刷新BOOS数量"+MAP.size());
			for (final L1NpcInstance npc : MAP.keySet()) {
				final Long time = MAP.get(npc);
				long t = time - 1;
				
				if (time > 0) {
					// 更新时间
					MAP.put(npc, t);
					//System.out.println(npc.getName()+"更新召唤时间");

				} else {
					// 召唤
					spawn(npc);
					//System.out.println(npc.getName()+"召唤成功");
					MAP.remove(npc);
				}
			}
			
		} catch (final Exception e) {
			_log.info("NPC(BOSS)召唤时间时间轴异常重启", e);
/*			GeneralThreadPool.getInstance().cancel(_timer, false);
			final NpcSpawnBossTimer bossTimer = new NpcSpawnBossTimer();
			bossTimer.start();*/
			
		} finally {
			//ListMapUtil.clear(REMOVE);
		}
	}

	/**
	 * 召唤BOSS
	 * @param npc
	 */
	private static void spawn(L1NpcInstance npc) {
		try {
			npc.getSpawn().executeSpawnTask(npc.getSpawnNumber());

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			System.out.println("召唤npc"+npc.getNpcId()+"出错");
		}
	}
}
