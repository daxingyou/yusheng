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

package l1j.william;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TeleportScroll {
	private static final Log _log = LogFactory.getLog(TeleportScroll.class);


	private static TeleportScroll _instance;

	private final HashMap<Integer, L1WilliamTeleportScroll> _itemIdIndex
			= new HashMap<Integer, L1WilliamTeleportScroll>();

	public static TeleportScroll getInstance() {
		if (_instance == null) {
			_instance = new TeleportScroll();
		}
		return _instance;
	}

	private TeleportScroll() {
		loadTeleportScroll();
	}

	private void loadTeleportScroll() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM william_teleport_scroll");
			rs = pstm.executeQuery();
			fillTeleportScroll(rs);
		} catch (SQLException e) {
			_log.info("error while creating william_teleport_scroll table",
					e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void fillTeleportScroll(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int itemId = rs.getInt("item_id");
			int tpLocX = rs.getInt("tpLocX");
			int tpLocY = rs.getInt("tpLocY");
			short tpMapId = rs.getShort("tpMapId");
			int check_minLocX = rs.getInt("check_minLocX");
			int check_minLocY = rs.getInt("check_minLocY");
			int check_maxLocX = rs.getInt("check_maxLocX");
			int check_maxLocY = rs.getInt("check_maxLocY");
			short check_MapId = rs.getShort("check_MapId");
			int check_ItemId  = rs.getInt("check_ItemId");
			int check_ItemCount  = rs.getInt("check_ItemCount");
			int removeItem = rs.getInt("removeItem");
			
			L1WilliamTeleportScroll teleport_scroll = new L1WilliamTeleportScroll(itemId, tpLocX, tpLocY, tpMapId, 
				check_minLocX, check_minLocY, check_maxLocX, check_maxLocY, check_MapId, removeItem,check_ItemId,check_ItemCount);
			_itemIdIndex.put(itemId, teleport_scroll);
		}
	}

	public L1WilliamTeleportScroll getTemplate(int itemId) {
		return _itemIdIndex.get(itemId);
	}
}
