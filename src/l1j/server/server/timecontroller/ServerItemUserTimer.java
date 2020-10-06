package l1j.server.server.timecontroller;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.storage.CharactersItemStorage;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServerItemUserTimer extends TimerTask{
	private static final Log _log = LogFactory.getLog(ServerItemUserTimer.class);

    private ScheduledFuture<?> _timer;

    public void start() {
        final int timeMillis = 60 * 1000;// 1分钟
        _timer = GeneralThreadPool.getInstance().scheduleAtFixedRate(this, timeMillis,timeMillis);
    }

    @Override
    public void run() {
        try {
            final Collection<L1ItemInstance> items = L1World.getInstance().getAllTimeItems();

            // 不包含元素
            if (items.isEmpty()) {
                return;
            }
            // 目前时间
            final Timestamp ts = new Timestamp(System.currentTimeMillis());

            for (final Iterator<L1ItemInstance> iter = items.iterator(); iter.hasNext();) {
                final L1ItemInstance item = iter.next();
                // 不具备使用期限 忽略
                if (item.get_time() == null) {
                    continue;
                }
                checkItem(item, ts);
                Thread.sleep(5);
            }

        } catch (final Exception e) {
            _log.error("物品使用期限计时时间轴异常重启", e);
            GeneralThreadPool.getInstance().cancel(_timer, false);
            final ServerItemUserTimer userItemTimer = new ServerItemUserTimer();
            userItemTimer.start();
        }
    }

    private static void checkItem(final L1ItemInstance item, final Timestamp ts)
            throws Exception {
        if (item.get_time().before(ts)) {
            final L1Object object = L1World.getInstance().findObject(item.get_char_objid());
            if (object != null) {
                if (object instanceof L1PcInstance) {
                    final L1PcInstance tgpc = (L1PcInstance) object;
                    tgpc.getInventory().removeItem(item);
                    if (tgpc.getOnlineStatus() > 0){
                    	tgpc.sendPackets(new S_SystemMessage(String.format("\\F2你的%s已到期 系统自动收回.", item.getLogViewName())));
                    }
                }
            } else {
            	L1World.getInstance().removeWorldObject(item);//移除世界
            	CharactersItemStorage.create().deleteItem(item);
            }
        }
    }
}
