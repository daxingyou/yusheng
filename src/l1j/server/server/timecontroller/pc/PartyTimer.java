package l1j.server.server.timecontroller.pc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Party;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PacketBoxParty;
import l1j.server.server.world.L1World;

/**
 * 隊伍更新時間軸(優化完成LOLI 2012-05-30)
 * 
 * @author KZK
 * 
 */
public class PartyTimer extends TimerTask {

	private static final Log _log = LogFactory.getLog(PartyTimer.class);


	private ScheduledFuture<?> _timer;

	public void start() {
		final int timeMillis = 2000;// 5秒
		_timer =  GeneralThreadPool.getInstance().scheduleAtFixedRate(this, timeMillis,
				timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1PcInstance> all = L1World.getInstance().getAllPlayers();
			// 不包含元素
			if (all.isEmpty()) {
				return;
			}

			for (final Iterator<L1PcInstance> iter = all.iterator(); iter
					.hasNext();) {
				final L1PcInstance tgpc = iter.next();
				if (check(tgpc)) {
					tgpc.sendPackets(new S_PacketBoxParty(tgpc.getParty(),tgpc));
					Thread.sleep(50);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			GeneralThreadPool.getInstance().cancel(_timer, false);
			final PartyTimer partyTimer = new PartyTimer();
			partyTimer.start();
		}
	}

	/**
	 * PC 執行 判斷
	 * 
	 * @param tgpc
	 * @return true:執行 false:不執行
	 */
	private static boolean check(final L1PcInstance tgpc) {
		try {
			// 人物為空
			if (tgpc == null) {
				return false;
			}
			// 人物登出
			if (tgpc.getOnlineStatus() == 0) {
				return false;
			}
			// 中斷連線
			if (tgpc.getNetConnection() == null) {
				return false;
			}

			final L1Party party = tgpc.getParty();
			if (party == null) {
				return false;
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}
}
