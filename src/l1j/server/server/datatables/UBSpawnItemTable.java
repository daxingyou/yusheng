package l1j.server.server.datatables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1UbSpawnItem;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UBSpawnItemTable {
	private static final Log _log = LogFactory.getLog(UBSpawnItemTable.class);
	
	private static UBSpawnItemTable _instance;

	private HashMap<Integer, ArrayList<L1UbSpawnItem>> _spawnTable = new HashMap<Integer, ArrayList<L1UbSpawnItem>>();;

	public static UBSpawnItemTable getInstance() {
		if (_instance == null) {
			_instance = new UBSpawnItemTable();
		}
		return _instance;
	}
	
	public void load(){
		loadSpawnTable();
	}

	private UBSpawnItemTable() {
		
	}

	private void loadSpawnTable() {

		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawnlist_ub_item");
			rs = pstm.executeQuery();

			while (rs.next()) {
				final int curRound = rs.getInt("curRound");
				final int item_id = rs.getInt("item_id");
				final int stackcount = rs.getInt("stackcount");
				final int count = rs.getInt("count");
				
				final L1Item itemTemp = ItemTable.getInstance().getTemplate(item_id);
				if (itemTemp == null) {
					continue;
				}

				final L1UbSpawnItem ubSpawnItem = new L1UbSpawnItem();
				ubSpawnItem.setGroup(curRound);
				ubSpawnItem.setItemId(item_id);
				ubSpawnItem.setStackCount(stackcount);
				ubSpawnItem.setCount(count);
				
				ArrayList<L1UbSpawnItem> spawnItems = _spawnTable.get(ubSpawnItem.getGroup());
				if (spawnItems == null){
					spawnItems = new ArrayList<L1UbSpawnItem>();
					_spawnTable.put(ubSpawnItem.getGroup(), spawnItems);
				}
				spawnItems.add(ubSpawnItem);
				
			}
		} catch (SQLException e) {
			_log.info("spawn couldnt be initialized:" + e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	public ArrayList<L1UbSpawnItem> getSpawnItems(final int round){
		return _spawnTable.get(round);
	}
}
