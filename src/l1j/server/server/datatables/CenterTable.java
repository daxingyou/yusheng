/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import javolution.util.FastTable;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.center.L1Center;
import l1j.server.server.templates.L1CenterItem;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CenterTable {

	private static final Log _log = LogFactory.getLog(CenterTable.class);

	private static CenterTable _instance;

	private final Map<Integer, L1Center> _allShops = new FastMap<Integer, L1Center>();

	public static CenterTable getInstance() {
		if (_instance == null) {
			_instance = new CenterTable();
		}
		return _instance;
	}

	private CenterTable() {
		//_allShops.clear();
		loadShops();
	}
	public void reload(){
		_allShops.clear();
		loadShops();
	}

	private FastTable<Integer> enumNpcIds() {
		FastTable<Integer> ids = new FastTable<Integer>();

		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT DISTINCT npc_id FROM shoptb");
			rs = pstm.executeQuery();

			while (rs.next()) {
				ids.add(rs.getInt("npc_id"));
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
		return ids;
	}

	private L1Center loadShop(int npcId, ResultSet rs) throws SQLException {
		List<L1CenterItem> sellingList = new FastTable<L1CenterItem>();
		List<L1CenterItem> purchasingList = new FastTable<L1CenterItem>();
		while (rs.next()) {
			int itemId = rs.getInt("item_id");
			int sellingPrice = rs.getInt("selling_price");
			int purchasingPrice = rs.getInt("purchasing_price");
			int packCount = rs.getInt("pack_count");
			int time = rs.getInt("time");
			int enlvl = rs.getInt("enlvl");
			//packCount = packCount == 0 ? 1 : packCount;
			if (0 <= sellingPrice) {
				L1CenterItem item = new L1CenterItem(itemId, sellingPrice, packCount,enlvl,time);
				sellingList.add(item);
			}
			if (1 <= purchasingPrice) {
                Connection conI         = null;
                PreparedStatement pstmI = null;
                ResultSet rsI             = null;
                try {
                    conI     = L1DatabaseFactory.getInstance().getConnection();
                    pstmI     = conI.prepareStatement("SELECT * FROM shoptb WHERE item_id='"+ itemId+"'");
                    rsI     = pstmI.executeQuery();
                    while (rsI.next()) {
                        if (rsI.getInt("selling_price") >= 1 && purchasingPrice/packCount > rsI.getInt("selling_price")) {
                            System.out.println("shop error NpcId="+rsI.getInt("npc_id")+", ItemID="+itemId+", PriceError!!!");
                            purchasingPrice = 0;
                        }
                    }
                    rsI.close();
                } catch (SQLException e) {
                	_log.error(e.getLocalizedMessage(), e);
                } finally {
                    SQLUtil.close(rsI, pstmI, conI);
                }
				L1CenterItem item = new L1CenterItem(itemId, purchasingPrice, packCount,enlvl,time);
				purchasingList.add(item);
			}
		}
		return new L1Center(npcId, sellingList, purchasingList);
	}

	private void loadShops() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM shoptb WHERE npc_id=? ORDER BY order_id");
			for (int npcId : enumNpcIds()) {
				pstm.setInt(1, npcId);
				rs = pstm.executeQuery();
				L1Center shop = loadShop(npcId, rs);
				_allShops.put(npcId, shop);
				rs.close();
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs, pstm, con);
		}
	}

	public L1Center get(int npcId) {
		return _allShops.get(npcId);
	}
}
