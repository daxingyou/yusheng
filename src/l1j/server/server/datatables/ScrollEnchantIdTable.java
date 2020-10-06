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

public class ScrollEnchantIdTable {
	private static final Log _log = LogFactory.getLog(ScrollEnchantIdTable.class);

    private static ScrollEnchantIdTable _instance;

    private static final ArrayList<Integer> _armorSetList = new ArrayList<Integer>();

    public static ScrollEnchantIdTable get() {
        if (_instance == null) {
            _instance = new ScrollEnchantIdTable();
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
            pstm = con.prepareStatement("SELECT * FROM `防爆卷轴可强化的道具`");
            rs = pstm.executeQuery();
            while (rs.next()) {
                final int itemId = rs.getInt("id");// 套装编号
                _armorSetList.add(itemId);
            }
        } catch (final SQLException e) {
            _log.error(e.getLocalizedMessage(), e);

        } finally {
            SQLUtil.close(rs);
            SQLUtil.close(pstm);
            SQLUtil.close(con);
        }
        _log.info("載入防爆卷轴可强化的道具數量: " + _armorSetList.size() + "(" + timer.get() + "ms)");
    }
    public boolean isEnchant(final int itemId){
    	return _armorSetList.contains(itemId);
    }
}
