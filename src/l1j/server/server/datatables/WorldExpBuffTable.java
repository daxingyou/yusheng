package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.timecontroller.WorldCalcExp;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WorldExpBuffTable {
private static final Log _log = LogFactory.getLog(WorldExpBuffTable.class);
	
	private static WorldExpBuffTable _instance;
	
	public static WorldExpBuffTable get(){
		if (_instance == null){
			_instance = new WorldExpBuffTable();
		}
		return _instance;
	}
	public WorldExpBuffTable() {
	
	}

	public void load(){
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM World_expBuff_time");
			rs = pstm.executeQuery();
			if (rs.next()){
				final long time = rs.getLong("time");
				WorldCalcExp.get().addTime(time);
				WorldCalcExp.get().start();
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	public void update(final long time){
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE `World_expBuff_time` SET `time`=?");
			pstm.setLong(1, time);
			pstm.execute();
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
