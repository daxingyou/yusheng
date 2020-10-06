package l1j.server.server.datatables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

public class NotDropTable {
	private static final Log _log = LogFactory.getLog(NotDropTable.class);
	private static NotDropTable _instance;
	private static final ArrayList<Integer> _droplist = new ArrayList<Integer>();
	
	public static NotDropTable getInstance(){
		if (_instance == null){
			_instance = new NotDropTable();
		}
		return _instance;
	}
	private NotDropTable(){
		
	}
	public void reload(){
		_droplist.clear();
		load();
	}
	public void load(){
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		PerformanceTimer timer = new PerformanceTimer();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM not_droplist_item");
			rs = pstm.executeQuery();
			while (rs.next()) {
				_droplist.add(rs.getInt("ItemId"));
			}
		} catch (SQLException e) {
			
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入not_droplist_item數量: " + _droplist.size() + "(" + timer.get() + "ms)");
	}
	
	public boolean InNotDropList(final int itemId){
		return _droplist.contains(itemId);
	}
}
