package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1FailureEnchant;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FailureEnchantTable {
	private static final Log _log = LogFactory.getLog(FailureEnchantTable.class);

	private static final Map<Integer, ArrayList<L1FailureEnchant>> _allFailure = new HashMap<Integer, ArrayList<L1FailureEnchant>>();

	private static FailureEnchantTable _instance;
	
	public static FailureEnchantTable get(){
		if (_instance == null){
			_instance = new FailureEnchantTable();
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
			pstm = con.prepareStatement("SELECT * FROM `character_failureenchant`");
			rs = pstm.executeQuery();
			while (rs.next()) {
				
				final int objId = rs.getInt("objId");
				final int itemId = rs.getInt("itemId");
				final int enchantLevel = rs.getInt("enchantLevel");
				final int failureCount = rs.getInt("failureCount");
				
				ArrayList<L1FailureEnchant> lists = _allFailure.get(objId);
				if (lists == null){
					lists = new ArrayList<L1FailureEnchant>();
					_allFailure.put(objId,lists);
				}
				final L1FailureEnchant itemFailure = new L1FailureEnchant();
				itemFailure.set_objId(objId);
				itemFailure.set_itemId(itemId);
				itemFailure.set_enchantLevel(enchantLevel);
				itemFailure.set_failureCount(failureCount);
				
				lists.add(itemFailure);
			}
		} catch (Exception  e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		_log.info("載入character_failureenchant數量: " + _allFailure.size() + "(" + timer.get() + "ms)");
	}
	
	public int getFailureCount(final int objId,final int itemId,final int enchantLevel){
		final ArrayList<L1FailureEnchant> lists = _allFailure.get(objId);
		if (lists == null){
			return 0;
		}
		for(final L1FailureEnchant itemFailure:lists){
			if (itemFailure.get_itemId() == itemId && itemFailure.get_enchantLevel() == enchantLevel){
				return itemFailure.get_failureCount();
			}
		}
		return 0;
	}
	private void InsertFailureItem(final L1FailureEnchant failureItem){
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int i = 0;
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO `character_failureenchant` SET `objId`=?,`itemId`=?,`enchantLevel`=?,`failureCount`=?");
			pstm.setInt(++i, failureItem.get_objId());
			pstm.setInt(++i, failureItem.get_itemId());
			pstm.setInt(++i, failureItem.get_enchantLevel());
			pstm.setInt(++i, failureItem.get_failureCount());
			pstm.execute();
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	public void updateFailureCount(final int objId,final int itemId,final int enchantLevel,final int failureCount){
		ArrayList<L1FailureEnchant> lists = _allFailure.get(objId);
		L1FailureEnchant failureItem = null;
		if (lists == null){
			lists = new ArrayList<L1FailureEnchant>();
			_allFailure.put(objId, lists);
		}else{
			if (!lists.isEmpty() && lists.size() > 0){
				for(final L1FailureEnchant itemFailure:lists){
					if (itemFailure.get_itemId() == itemId && itemFailure.get_enchantLevel() == enchantLevel){
						failureItem = itemFailure;
						break;
					}
				}
			}
		}
		if (failureItem == null){
			failureItem = new L1FailureEnchant();
			failureItem.set_objId(objId);
			failureItem.set_itemId(itemId);
			failureItem.set_enchantLevel(enchantLevel);
			failureItem.set_failureCount(failureCount);
			
			lists.add(failureItem);
			
			InsertFailureItem(failureItem);
			
		}else{
			failureItem.set_failureCount(failureCount);
			updateFailureCount(failureItem);
		}
	}
	private void updateFailureCount(final L1FailureEnchant failureItem){
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			int i = 0;
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("UPDATE `character_failureenchant` SET `failureCount`=? WHERE `objId`=? AND `itemId`=? AND `enchantLevel`=?");
			pstm.setInt(++i, failureItem.get_failureCount());
			
			pstm.setInt(++i, failureItem.get_objId());
			pstm.setInt(++i, failureItem.get_itemId());
			pstm.setInt(++i, failureItem.get_enchantLevel());
			
			pstm.execute();
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
