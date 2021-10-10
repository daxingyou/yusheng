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

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.MapNotAllowedData;
import l1j.server.server.utils.SQLUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class MapsNotAllowedTable {

	private static final Log _log = LogFactory.getLog(MapsNotAllowedTable.class);

	private static MapsNotAllowedTable _instance;


	private final Map<Integer, MapNotAllowedData> _maps = new HashMap<Integer, MapNotAllowedData>();

	public static MapsNotAllowedTable getInstance() {
		if (_instance == null) {
			_instance = new MapsNotAllowedTable();
		}
		return _instance;
	}

	private MapsNotAllowedTable() {
		loadMapsNotAllowedFromDatabase();
	}


	private void loadMapsNotAllowedFromDatabase() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT mapid,allow_level,map_allow,state FROM maps_not_allowed");
			for (rs = pstm.executeQuery(); rs.next();) {
				MapNotAllowedData data = new MapNotAllowedData();
				int mapId = rs.getInt("mapid");
				data.mapId = mapId;
				data.allow_level = rs.getInt("allow_level");
				data.map_allow = rs.getInt("map_allow");
				data.state = rs.getInt("state");
				_maps.put(new Integer(mapId), data);
			}
			_log.info("MapsNotAllowed " + _maps.size());
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs,pstm,con);
		}
	}


	public void reload(){
		_maps.clear();
		loadMapsNotAllowedFromDatabase();
	}

	/**
	 * @param mapId
	 *
	 * @return 返回是否存在禁止的map地址
	 */
	public int getMapId(int mapId) {
		MapNotAllowedData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).mapId;
	}

	/**判断地图是否允许进入等级,allow_level默认是1代表1级*/
	public int getMapAllowLevel(int mapId) {
		MapNotAllowedData map = _maps.get(mapId);
		if (map == null) {
			return 0;
		}
		return _maps.get(mapId).allow_level;
	}

	/**判断地图是否允许进入,map_allow默认是0代表false，0禁止进入*/
	public int getMapAllow(int mapId) {
		MapNotAllowedData map = _maps.get(mapId);
		if (map == null) {
			return 1;
		}
		return _maps.get(mapId).map_allow;
	}


	/**
	 * 传回全部禁止地图资料
	 * 
	 * @return
	 */
	public Map<Integer, MapNotAllowedData> getMaps() {
		return _maps;
	}



}
