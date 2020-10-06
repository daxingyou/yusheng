package l1j.server.server.timecontroller.pc;

import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.world.WorldDarkelf;


/**
 * PC HP回復時間軸(黑妖)
 * 
 * @author dexc
 * 
 */
public class HprMprTimerDarkElf extends TimerTask {

	private static final Log _log = LogFactory.getLog(HprMprTimerDarkElf.class);

	public void start() {
		final int timeMillis = 500;// 1秒
		GeneralThreadPool.getInstance().scheduleAtFixedRate(this, timeMillis, timeMillis);
	}

	@Override
	public void run() {
		try {
			final Collection<L1PcInstance> allPc = WorldDarkelf.get().all();
			// 不包含元素
			if (allPc.isEmpty()) {
				return;
			}
			
			for (final Iterator<L1PcInstance> iter = allPc.iterator(); iter.hasNext();) {
				final L1PcInstance tgpca = iter.next();
				if (!PcCheck.check(tgpca)) {
					continue;
				}
//				if (!AutoMagic_GJ.check(tgpca)) {
//					continue;
//				}
			}

			for (final Iterator<L1PcInstance> iter = allPc.iterator(); iter.hasNext();) {
				final L1PcInstance tgpc = iter.next();
				if (tgpc.getHpRegeneration() != null) {
					tgpc.getHpRegeneration().keephpr();
				}
				if (tgpc.getMpRegeneration() != null) {
					tgpc.getMpRegeneration().keepmpr();
				}
				tgpc.onChangeLawful();
				if (tgpc.getAttacksec()>0) {
					tgpc.setAttacksec(tgpc.getAttacksec()-1);
				}else {
					tgpc.setAttack(false);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}
}
