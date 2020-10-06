package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1ServerFailureEnchant;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServerFailureEnchantTable {
	private static final Log _log = LogFactory.getLog(ServerFailureEnchantTable.class);

	private static final Map<String, L1ServerFailureEnchant> _allFailure = new HashMap<String, L1ServerFailureEnchant>();

	private static ServerFailureEnchantTable _instance;
	
	public static ServerFailureEnchantTable get(){
		if (_instance == null){
			_instance = new ServerFailureEnchantTable();
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
			pstm = con.prepareStatement("SELECT * FROM `server_failureenchant`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final int itemId = rs.getInt("itemId");
				final int enchantLevel = rs.getInt("enchantLevel");
				final int min_FailureCount = rs.getInt("min_FailureCount");
				final int max_FailureCount = rs.getInt("max_FailureCount");
							
				final L1ServerFailureEnchant itemEnchant = new L1ServerFailureEnchant();
				itemEnchant.set_minFailureCount(min_FailureCount);
				itemEnchant.set_maxFailureCount(max_FailureCount);
				
				_allFailure.put(new StringBuffer().append(itemId).append(enchantLevel).toString(), itemEnchant);
			}
		} catch (Exception  e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入server_failureenchant數量: " + _allFailure.size() + "(" + timer.get() + "ms)");
	}
	
	public L1ServerFailureEnchant getItem(final int itemId,final int enchantLevel){
		final String enchantkey = new StringBuffer().append(itemId).append(enchantLevel).toString();
		return _allFailure.get(enchantkey);
	}
}
