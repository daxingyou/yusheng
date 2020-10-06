package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TownSetTable {

	private static final Log _log = LogFactory.getLog(TownSetTable.class);

	private static TownSetTable _instance;

	private static final Map<Integer, Integer> _townnpclist = new HashMap<Integer, Integer>();

	public static TownSetTable get() {
		if (_instance == null) {
			_instance = new TownSetTable();
		}
		return _instance;
	}
	
	public void reload(){
		_townnpclist.clear();
		load();
	}

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			cn = L1DatabaseFactory.getInstance().getConnection();
			ps = cn.prepareStatement("SELECT * FROM npctown");

			for (rs = ps.executeQuery(); rs.next();) {
				final int npcId = rs.getInt("npcid");
				final int townid = rs.getInt("townid");

				_townnpclist.put(new Integer(npcId), townid);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("载入NPC村庄资料数量: " + _townnpclist.size() + "(" + timer.get()
				+ "ms)");
	}

	public int getTownid(final int npcid) {
		int Townid = 14;//預設
		if (_townnpclist.containsKey(npcid)) {
			Townid = _townnpclist.get(npcid);
		}
		return Townid;
	}
}
