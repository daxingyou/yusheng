package l1j.server.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

/**
 * 预先加载已使用过 游戏世界唯一编号(OBJID)
 * @author dexc
 *
 */
public class IdLoad {

	private static IdLoad _instance;
	
	private final IdFactory _loadId = IdFactory.getInstance();
	
	public static IdLoad getInstance() {
		if (_instance == null) {
			_instance = new IdLoad();
		}
		return _instance;
	}

	private IdLoad() {
		loadState();
	}

	/**
	 * 载入已耗用编号
	 */
	private void loadState() {
//		loadShatChid();		// 史莱姆
//		loadBigHotChid(); 	// 大乐透
//		loadMobChid();		// 怪物对战
//		loadStarChid(); 	// 四星彩
		loadGamChid();		// 赌狗场
	}

	/**
	 * <font color=#ff0000>赌场检查号预先载入</font><BR>
	 *
	 */
	private void loadGamChid() {
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			cn = L1DatabaseFactory.getInstance().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `race_gambling`");
			rs = ps.executeQuery();
			while (rs.next()) {
				int i = rs.getInt("id");
				_loadId.addGamId(i);
			}
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * <font color=#ff0000>四星彩检查号预先载入</font><BR>
	 *
	 */
/*	private void loadStarChid() {
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			cn = L1DatabaseFactory.getInstance().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `race_starbling`");
			rs = ps.executeQuery();
			while (rs.next()) {
				int i = rs.getInt("id");
				_loadId.addStarId(i);
			}
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}
*/
	/**
	 * <font color=#ff0000>怪物对战检查号预先载入</font><BR>
	 *
	 */
/*	private void loadMobChid() {
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			cn = L1DatabaseFactory.getInstance().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `race_mobbling`");
			rs = ps.executeQuery();
			while (rs.next()) {
				int i = rs.getInt("id");
				_loadId.addMobId(i);
			}
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}*/

	/**
	 * <font color=#ff0000>大乐透检查号预先载入</font><BR>
	 *
	 */
	private void loadBigHotChid() {
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			cn = L1DatabaseFactory.getInstance().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `race_bighotbling`");
			rs = ps.executeQuery();
			while (rs.next()) {
				int i = rs.getInt("id");
				_loadId.addBigHotId(i);
			}
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/**
	 * <font color=#ff0000>史莱姆检查号预先载入</font><BR>
	 *
	 */
/*	private void loadShatChid() {
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			cn = L1DatabaseFactory.getInstance().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `race_shatbling`");
			rs = ps.executeQuery();
			while (rs.next()) {
				int i = rs.getInt("id");
				_loadId.addShatId(i);
			}
			ps.execute();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}*/

}
