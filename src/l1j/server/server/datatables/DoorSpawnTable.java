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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.ActionCodes;
import l1j.server.server.IdFactory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.SQLUtil;
import l1j.server.server.world.L1World;

public class DoorSpawnTable {
	private static final Log _log = LogFactory.getLog(DoorSpawnTable.class);

	private static DoorSpawnTable _instance;

	private final ArrayList<L1DoorInstance> _doorList =
			new ArrayList<L1DoorInstance>();
			
	private static final Map<String, L1DoorInstance> _doorDirectionLists = new HashMap<String, L1DoorInstance>();

	public static DoorSpawnTable getInstance() {
		if (_instance == null) {
			_instance = new DoorSpawnTable();
		}
		return _instance;
	}

	private DoorSpawnTable() {
		FillDoorSpawnTable();
	}

	private void FillDoorSpawnTable() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM spawnlist_door");
			rs = pstm.executeQuery();
			do {
				if (!rs.next()) {
					break;
				}
				L1Npc l1npc = NpcTable.getInstance().getTemplate(81158);
				if (l1npc != null) {
					int id = rs.getInt("id");

					// 忽略原有的賭場門設置
					if (id >= 808 && id <= 812) {
						continue;
					}

					final L1DoorInstance door = (L1DoorInstance) NpcTable.getInstance()
							.newNpcInstance(l1npc);

					door.setId(IdFactory.getInstance().nextId());

					door.setDoorId(id);
					door.setGfxId(rs.getInt("gfxid"));
					int x = rs.getInt("locx");
					int y = rs.getInt("locy");
					short mapid = rs.getShort("mapid");
					door.setX(x);
					door.setY(y);
					door.setMap(mapid);
					door.setHomeX(x);
					door.setHomeY(y);
					door.setDirection(rs.getInt("direction"));
					door.setLeftEdgeLocation(rs.getInt("left_edge_location"));
					door.setRightEdgeLocation(rs.getInt("right_edge_location"));
					int hp = rs.getInt("hp");
					door.setMaxHp(hp);
					door.setCurrentHp(hp);
					door.setKeeperId(rs.getInt("keeper"));
					L1World.getInstance().storeWorldObject(door);
					L1World.getInstance().addVisibleObject(door);

					_doorList.add(door);
					final String key = new StringBuilder().append(mapid)
							.append(x).append(y).toString();
					_doorDirectionLists.put(key, door);
				}
			} while (true);
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} catch (SecurityException e) {
			_log.error(e.getLocalizedMessage(), e);
		} catch (IllegalArgumentException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	public L1DoorInstance[] getDoorList() {
		return _doorList.toArray(new L1DoorInstance[_doorList.size()]);
	}

	public int getDoorDirection(L1Location loc) {
		final String key = new StringBuilder().append(loc.getMapId())
				.append(loc.getX()).append(loc.getY()).toString();
		L1DoorInstance door = _doorDirectionLists.get(key);
		if (door == null || door.getStatus() == ActionCodes.ACTION_Open) {
			return -1;
		}
		return door.getDirection();
	}
}
