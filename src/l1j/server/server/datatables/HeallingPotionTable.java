package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HeallingPotionTable {
	private static final Log _log = LogFactory.getLog(HeallingPotionTable.class);
	private static final Map<Integer, int[]>  _HealHashmap = new HashMap<Integer, int[]>();
	
	private static HeallingPotionTable _instanse;
	
	public static HeallingPotionTable get(){
		if (_instanse == null){
			_instanse = new HeallingPotionTable();
		}
		return _instanse;
	}
	
	public void load() {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		PerformanceTimer timer = new PerformanceTimer();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			final String sqlstr = "SELECT * FROM `HeallingPotion`";
			ps = con.prepareStatement(sqlstr);
			rs = ps.executeQuery();
			while (rs.next()) {
				final int[] value = new int[2];
				final int itemId = rs.getInt("HPItemId");
				final int healHp = rs.getInt("healHp");
				final int gfxid = rs.getInt("gfxid");
				
				value[0] = healHp;
				value[1] = gfxid;
				_HealHashmap.put(itemId, value);
			}

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(con);
			SQLUtil.close(rs);
		}
		_log.info("載入HP道具數量: " + _HealHashmap.size() + "(" + timer.get() + "ms)");
	}
	
	public int[] getHealPotion(final int itemId){
		return _HealHashmap.get(itemId);
	}
	
	public Map<Integer, int[]> getHealPotionMap(){
		return _HealHashmap;
	}
}
