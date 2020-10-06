package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1ServerBlessEnchant;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServerBlessEnchantTable {
	private static final Log _log = LogFactory.getLog(ServerBlessEnchantTable.class);

	private static final Map<Integer, L1ServerBlessEnchant> _allFailure = new HashMap<Integer, L1ServerBlessEnchant>();

	private static ServerBlessEnchantTable _instance;
	
	public static ServerBlessEnchantTable get(){
		if (_instance == null){
			_instance = new ServerBlessEnchantTable();
		}
		return _instance;
	}
	
	public void reload(){
		_allFailure.clear();
		load();
	}
	
	public void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		PerformanceTimer timer = new PerformanceTimer();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM `server_blessenchant`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int itemId = rs.getInt("itemId");
				final int min_Count = rs.getInt("minCount");
				final int max_Count = rs.getInt("maxCount");
				
				final L1ServerBlessEnchant itemEnchant = new L1ServerBlessEnchant();
				itemEnchant.set_minCount(min_Count);
				itemEnchant.set_maxCount(max_Count);
				
				_allFailure.put(itemId, itemEnchant);
			}
		} catch (Exception  e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入server_blessenchant數量: " + _allFailure.size() + "(" + timer.get() + "ms)");
	}
	
	public L1ServerBlessEnchant getItem(final int itemId){
		return _allFailure.get(itemId);
	}
}
