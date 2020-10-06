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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;
import l1j.william.L1WilliamPlayerSpeed;

public class PlayerSpeed {
	private static final Log _log = LogFactory.getLog(PlayerSpeed.class);


	private static PlayerSpeed _instance;

	private final HashMap<Integer, L1WilliamPlayerSpeed> _itemIdIndex
			= new HashMap<Integer, L1WilliamPlayerSpeed>();

	public static PlayerSpeed getInstance() {
		if (_instance == null) {
			_instance = new PlayerSpeed();
		}
		return _instance;
	}

	private PlayerSpeed() {
		loadPlayerSpeed();
	}

	private void loadPlayerSpeed() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM william_player_speed");
			rs = pstm.executeQuery();
			fillPlayerSpeed(rs);
		} catch (SQLException e) {
			_log.info("error while creating william_player_speed table",
					e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void fillPlayerSpeed(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int polyid = rs.getInt("polyid");
			int moveDouble = rs.getInt("moveDouble");
			int atkDouble = rs.getInt("atkDouble");
			int move_0 = rs.getInt("move_0");
			int atk_0 = rs.getInt("atk_0");
			int move_4 = rs.getInt("move_4");
			int atk_4 = rs.getInt("atk_4");
			int move_11 = rs.getInt("move_11");
			int atk_11 = rs.getInt("atk_11");
			int move_20 = rs.getInt("move_20");
			int atk_20 = rs.getInt("atk_20");
			int move_24 = rs.getInt("move_24");
			int atk_24 = rs.getInt("atk_24");
			int move_40 = rs.getInt("move_40");
			int atk_40 = rs.getInt("atk_40");
			int move_46 = rs.getInt("move_46");
			int atk_46 = rs.getInt("atk_46");
			int move_50 = rs.getInt("move_50");
			int atk_50 = rs.getInt("atk_50");
			int move_54 = rs.getInt("move_54");
			int atk_54 = rs.getInt("atk_54");
			int move_58 = rs.getInt("move_58");
			int atk_58 = rs.getInt("atk_58");
			int move_62 = rs.getInt("move_62");
			int atk_62 = rs.getInt("atk_62");
			
			L1WilliamPlayerSpeed Player_Speed = new L1WilliamPlayerSpeed(polyid, moveDouble, atkDouble
				, move_0, atk_0, move_4, atk_4, move_11, atk_11, move_20, atk_20,  move_24, atk_24
				, move_40, atk_40, move_46, atk_46, move_50, atk_50, move_54, atk_54, move_58, atk_58
				, move_62, atk_62);
			_itemIdIndex.put(polyid, Player_Speed);
		}
	}

	public L1WilliamPlayerSpeed getTemplate(int polyid) {
		return _itemIdIndex.get(polyid);
	}
}
