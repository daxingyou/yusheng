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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1GetBackRestart;
import l1j.server.server.utils.SQLUtil;

// Referenced classes of package l1j.server.server:
// IdFactory

public class GetBackRestartTable {

	private static final Log _log = LogFactory.getLog(GetBackRestartTable.class);

	private static GetBackRestartTable _instance;

	private final HashMap<Integer, L1GetBackRestart> _getbackrestart = new HashMap<Integer, L1GetBackRestart>();

	public static GetBackRestartTable getInstance() {
		if (_instance == null) {
			_instance = new GetBackRestartTable();
		}
		return _instance;
	}
	
	private GetBackRestartTable(){
		LoadGetBackRestartTable();
	}
	
	public void reload(){
		_getbackrestart.clear();
		LoadGetBackRestartTable();
	}

	public void LoadGetBackRestartTable() {
		
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM getback_restart");
			rs = pstm.executeQuery();
			while (rs.next()) {
				L1GetBackRestart gbr = new L1GetBackRestart();
				int id = rs.getInt(1);
				gbr.set_id(id);
				gbr.set_area(rs.getInt(2));
				gbr.set_areaname(rs.getString(3));
				gbr.set_locx(rs.getInt(4));
				gbr.set_locy(rs.getInt(5));
				gbr.set_mapid(rs.getShort(6));

				_getbackrestart.put(new Integer(id), gbr);
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	public L1GetBackRestart[] getGetBackRestartTableList() {
		return _getbackrestart.values().toArray(
				new L1GetBackRestart[_getbackrestart.size()]);
	}

}
