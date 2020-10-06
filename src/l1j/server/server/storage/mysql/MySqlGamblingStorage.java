package l1j.server.server.storage.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l1j.server.L1DatabaseFactory;
import l1j.server.TimeInfo;
import l1j.server.server.storage.GamblingStorage;
import l1j.server.server.templates.L1Gambling;
import l1j.server.server.utils.SQLUtil;

/**
 * SQL DB character_gambling
 * @author dexc
 *
 */
public class MySqlGamblingStorage implements GamblingStorage {

	/**
	 * 新赌场赔率建立
	 * @param id 场次编号
	 * @param npcid 优胜者
	 * @param rate 赔率
	 * @param totalPrice 该场累积赌金
	 */
	public void create(int id, int npcid, double rate, int totalPrice) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {

			L1Gambling gam = new L1Gambling();

			gam.set_id(id);
			gam.set_npcid(npcid);
			gam.set_rate(rate);
			// 加入清单
			_gambling.put(id, gam);
			
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = 
				"INSERT INTO `race_gambling` SET `id`=?,`npcid`=?," +
				"`rate`=?,`time`=?,`totalPrice`=?";
			pstm = con.prepareStatement(sqlstr);
			
			pstm.setInt(1, gam.get_id());
			pstm.setInt(2, gam.get_npcid());
			pstm.setDouble(3, gam.get_rate());
			// 取得场次时间
			String time = TimeInfo.time().getNow_YMDHMS();
			pstm.setString(4, time);
			// 该场累积赌金
			pstm.setInt(5, totalPrice);
			
			pstm.execute();

		} catch (Exception e) {
			e.getLocalizedMessage();
			
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private final Map<Integer, L1Gambling> _gambling = new ConcurrentHashMap<Integer, L1Gambling>();
	
	/**
	 * 载入旧赌场纪录
	 */
	public void load() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		// 产生一笔资料储存空间
		L1Gambling gam = null;
		
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "SELECT * FROM `race_gambling`";
			pstm = con.prepareStatement(sqlstr);
			rs = pstm.executeQuery();
			
			while (rs.next()) {
				gam = new L1Gambling();
				// 取得场次编号
				int id = rs.getInt("id");
				gam.set_id(id);
				gam.set_npcid(rs.getInt("npcid"));
				gam.set_rate(rs.getDouble("rate"));
				// 加入清单
				_gambling.put(id, gam);
			}

		} catch (SQLException e) {
			e.getLocalizedMessage();
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 传回全部赌场纪录资料
	 * @return
	 */
	public L1Gambling[] getGamblingList() {
		return _gambling.values().toArray(new L1Gambling[_gambling.size()]);
	}

	/**
	 * 传回指定赌场纪录资料(id)
	 * @param id
	 * @return
	 */
	public L1Gambling getGambling(int id) {
		return _gambling.get(id);
	}

}
