package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1TuiGuang;
import l1j.server.server.utils.SQLUtil;

public class TuiguangTable {
	
	private static final Log _log = LogFactory.getLog(TuiguangTable.class);
	
	private static TuiguangTable _instance;
	
	
	private final CopyOnWriteArrayList<L1TuiGuang>_tuiguangs = new CopyOnWriteArrayList<L1TuiGuang>();
//	private final Map<Integer, L1TuiGuang> _tuiguangs = new ConcurrentHashMap<Integer, L1TuiGuang>();
	
	public static TuiguangTable getInstance() {
		if (_instance == null) {
			_instance = new TuiguangTable();
		}
		return _instance;
	}
	
	public void reload() {
		_tuiguangs.clear();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM tuiguang");
			rs = pstm.executeQuery();
			while (rs.next()) {
				final boolean yl = rs.getBoolean("yl");
				if (yl) {
					continue;
				}
				L1TuiGuang tuiguang = new L1TuiGuang();
				tuiguang.setTid(rs.getInt("id"));
				tuiguang.setTaccount(rs.getString("account"));
				tuiguang.setTitmeid(rs.getInt("itemid"));
				tuiguang.setItemcount(rs.getInt("count"));
//				System.out.println("收到奖励信息："+tuiguang.getTitemid()+","+tuiguang.getTcount());
				tuiguang.setTling(rs.getBoolean("yl"));
				tuiguang.setTtime(rs.getTimestamp("time"));
				_tuiguangs.add(tuiguang);
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	public void updatetuiguang(final L1TuiGuang tuiguang) {
		Connection con = null;
		PreparedStatement pstm = null;
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "UPDATE tuiguang SET yl=?,time=? WHERE id = ?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setInt(1, 1);
			pstm.setTimestamp(2, ts);
			pstm.setInt(3, tuiguang.getTid());
			pstm.execute();
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	public L1TuiGuang isok(final String name)
	{
		for (L1TuiGuang tuiguang : _tuiguangs) {
			if (tuiguang.getTaccount().equals(name)) {
				if (!tuiguang.isTling()) {
					tuiguang.setTling(true);
					updatetuiguang(tuiguang);
					return tuiguang;
				}			
			}
		}
		return null;
	}

}
