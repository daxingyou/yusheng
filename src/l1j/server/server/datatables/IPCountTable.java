package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;
public class IPCountTable {

	private static final Log _log = LogFactory.getLog(IPCountTable.class);

	private static IPCountTable _instance;

	public static IPCountTable get() {
		if (_instance == null) {
			_instance = new IPCountTable();
		}
		return _instance;
	}

	/** 1个IP仅允许?个连线 */
	public static final Map<String, Integer> ONEIPMAP = new ConcurrentHashMap<String, Integer>();

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			cn = L1DatabaseFactory.getInstance().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `ip_count`");

			rs = ps.executeQuery();

			while (rs.next()) {
				String key = rs.getString("ip");
				int count = rs.getInt("count");
				ONEIPMAP.put(key, count);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("载入IP限制连接资料数量: " + ONEIPMAP.size() + "(" + timer.get()
				+ "ms)");
	}

	public int getIpcount(String ip) {
		int i = Config.ISONEIP;
		if (ONEIPMAP.containsKey(ip)) {
			i = ONEIPMAP.get(ip);
		}
		return i;
	}

	public void reload() {
		ONEIPMAP.clear();
		load();	
	}
}
