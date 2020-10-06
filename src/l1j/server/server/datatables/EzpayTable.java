package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.storage.EzpayStorage;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EzpayTable implements EzpayStorage {
	private static final Log _log = LogFactory.getLog(EzpayTable.class);
	
	@Override
	public Map<Integer, int[]> ezpayInfo(final String loginName,final int type) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		final Map<Integer, int[]> list = new HashMap<Integer, int[]>();
		String tableName = "shop_user";
		if (type == 1){
			tableName = "shop_user_cn";
		}
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			final String sqlstr = "SELECT * FROM `" + tableName + "` WHERE `account`=? ORDER BY `id`";
			ps = con.prepareStatement(sqlstr);
			ps.setString(1, loginName.toLowerCase());
			rs = ps.executeQuery();
			while (rs.next()) {
				int[] value = new int[3];
				final int state = rs.getInt("isget");// 狀態
				if (state == 0) {
					final int key = rs.getInt("id");// ID
					final int p_id = rs.getInt("p_id");// ITEMID
					final int count = rs.getInt("count");// 數量
					value[0] = key;
					value[1] = p_id;
					value[2] = count;
					
					list.put(key, value);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(con);
			SQLUtil.close(rs);
		}
		return list;
	}
	
	@SuppressWarnings("resource")
	private boolean is(final String loginName, final int id,final int type) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String tableName = "shop_user";
			if (type == 1){
				tableName = "shop_user_cn";
			}
			con = L1DatabaseFactory.getInstance().getConnection();
			final String sqlstr = "SELECT * FROM `" + tableName + "` WHERE `account`=? AND `id`=?";
			ps = con.prepareStatement(sqlstr);
			ps.setString(1, loginName.toLowerCase());
			ps.setInt(2, id);
			rs = ps.executeQuery();
			while (rs.next()) {
				final int state = rs.getInt("isget");// 狀態
				if (state != 0) {
					return false;
				}
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(con);
			SQLUtil.close(rs);
		}
		return true;
	}

	@Override
	public boolean update(String loginName, int id, String pcname, String ip,final int type) {
		if (!is(loginName, id,type)) {
			return false;
		}
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			final Timestamp lastactive = new Timestamp(System.currentTimeMillis());
			String tableName = "shop_user";
			if (type == 1){
				tableName = "shop_user_cn";
			}
			con = L1DatabaseFactory.getInstance().getConnection();
			final String sqlstr = "UPDATE `"+ tableName +"` SET `isget`=1,`play`=?,`time`=?,`ip`=? WHERE `id`=? AND `account`=?";
			pstm = con.prepareStatement(sqlstr);
			
			pstm.setString(1, pcname);// 領取人物
			pstm.setTimestamp(2, lastactive);// 時間
			pstm.setString(3, ip);// IP位置
			
			pstm.setInt(4, id);// ID
			pstm.setString(5, loginName);// 帳號名稱
			
			pstm.execute();
			return true;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return false;
	}

	@Override
	public Map<Integer, int[]> XuanChuanInfo(final String loginName) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		final Map<Integer, int[]> list = new HashMap<Integer, int[]>();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			final String sqlstr = "SELECT * FROM `xuanchuan` WHERE `account`=? ORDER BY `id`";
			ps = con.prepareStatement(sqlstr);
			ps.setString(1, loginName.toLowerCase());
			rs = ps.executeQuery();
			while (rs.next()) {
				int[] value = new int[3];
				final int state = rs.getInt("isget");// 狀態
				if (state == 0) {
					final int key = rs.getInt("id");// ID
					final int p_id = rs.getInt("p_id");// ITEMID
					final int count = rs.getInt("count");// 數量
					value[0] = key;
					value[1] = p_id;
					value[2] = count;
					
					list.put(key, value);
				}
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(con);
			SQLUtil.close(rs);
		}
		return list;
	}
	
	@SuppressWarnings("resource")
	private boolean isXuanChuan(final String loginName, final int id) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			final String sqlstr = "SELECT * FROM `xuanchuan` WHERE `account`=? AND `id`=?";
			ps = con.prepareStatement(sqlstr);
			ps.setString(1, loginName.toLowerCase());
			ps.setInt(2, id);
			rs = ps.executeQuery();
			while (rs.next()) {
				final int state = rs.getInt("isget");// 狀態
				if (state != 0) {
					return false;
				}
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(con);
			SQLUtil.close(rs);
		}
		return true;
	}
	@Override
	public boolean updateXuanChuan(String loginName, int id, String pcname, String ip) {
		if (!isXuanChuan(loginName, id)) {
			return false;
		}
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			final Timestamp lastactive = new Timestamp(System.currentTimeMillis());
			con = L1DatabaseFactory.getInstance().getConnection();
			final String sqlstr = "UPDATE `xuanchuan` SET `isget`=1,`play`=?,`time`=?,`ip`=? WHERE `id`=? AND `account`=?";
			pstm = con.prepareStatement(sqlstr);
			
			pstm.setString(1, pcname);// 領取人物
			pstm.setTimestamp(2, lastactive);// 時間
			pstm.setString(3, ip);// IP位置
			
			pstm.setInt(4, id);// ID
			pstm.setString(5, loginName);// 帳號名稱
			
			pstm.execute();
			return true;

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return false;
	}
}
