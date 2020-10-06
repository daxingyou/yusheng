package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class CallClanMapTable {
	private static final Log _log = LogFactory.getLog(CallClanMapTable.class);

	private static final ArrayList<Integer> _mapList = new ArrayList<Integer>();
	
	private static CallClanMapTable _instance;

	public static CallClanMapTable get() {
		if (_instance == null) {
			_instance = new CallClanMapTable();
		}
		return _instance;
	}

	public void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		PerformanceTimer timer = new PerformanceTimer();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `call_clan_map`");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int map_id = rs.getInt("mapId");
				_mapList.add(map_id);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入不能使用穿云箭地图數量: " + _mapList.size() + "(" + timer.get() + "ms)");
	}
	
	public boolean IsNoMap(final int mapId){
		return _mapList.contains(mapId);
	}
}
