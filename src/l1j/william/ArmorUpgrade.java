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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;
import l1j.william.L1WilliamArmorUpgrade;

public class ArmorUpgrade {
	private static final Log _log = LogFactory.getLog(ArmorUpgrade.class);

	private static ArmorUpgrade _instance;

	private final HashMap<Integer, L1WilliamArmorUpgrade> _itemIdIndex
			= new HashMap<Integer, L1WilliamArmorUpgrade>();

	public static ArmorUpgrade getInstance() {
		if (_instance == null) {
			_instance = new ArmorUpgrade();
		}
		return _instance;
	}

	private ArmorUpgrade() {
		loadArmorUpgrade();
	}

	private void loadArmorUpgrade() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM william_armor_upgrade");
			rs = pstm.executeQuery();
			fillArmorUpgrade(rs);
		} catch (SQLException e) {
			_log.info("error while creating william_armor_upgrade table",
					e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void fillArmorUpgrade(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int ArmorId = rs.getInt("armor_id");
			int UpgradeRnd = rs.getInt("upgrade_rnd");
			int UpgradeArmorId = rs.getInt("upgrade_armor_id");
			String Materials = rs.getString("materials");
			String Counts = rs.getString("counts");
			
			L1WilliamArmorUpgrade armor_upgrade = new L1WilliamArmorUpgrade(ArmorId, UpgradeRnd, UpgradeArmorId, Materials, Counts);
			_itemIdIndex.put(ArmorId, armor_upgrade);
		}
	}

	public L1WilliamArmorUpgrade getTemplate(int ArmorId) {
		return _itemIdIndex.get(ArmorId);
	}
}
