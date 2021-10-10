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
package l1j.server.server.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.gametime.L1GameTimeClock;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package l1j.server.server.model:
// L1Teleport, L1PcInstance

public class Dungeon {

	private static final Log _log = LogFactory.getLog(Dungeon.class);


	private static Dungeon _instance = null;

	private static Map<String, NewDungeon> _dungeonMap = new HashMap<String, NewDungeon>();

	private enum DungeonType {
		NONE, SHIP_FOR_FI, SHIP_FOR_HEINE, SHIP_FOR_PI, SHIP_FOR_HIDDENDOCK
		, SHIP_FOR_ISLAND, SHIP_FOR_GLUDIN
	};

	public static Dungeon getInstance() {
		if (_instance == null) {
			_instance = new Dungeon();
		}
		return _instance;
	}
	
	private Dungeon(){
		loadDungeon();
	}
	
	public void reload(){
		_dungeonMap.clear();
		loadDungeon();
	}

	private void loadDungeon() {
		//_dungeonMap.clear();
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();

			pstm = con.prepareStatement("SELECT * FROM dungeon");
			rs = pstm.executeQuery();
			while (rs.next()) {
				int srcMapId = rs.getInt("src_mapid");
				int srcX = rs.getInt("src_x");
				int srcY = rs.getInt("src_y");
				String key = new StringBuilder().append(srcMapId).append(srcX)
						.append(srcY).toString();
				int newX = rs.getInt("new_x");
				int newY = rs.getInt("new_y");
				int newMapId = rs.getInt("new_mapid");
				int heading = rs.getInt("new_heading");
				DungeonType dungeonType = DungeonType.NONE;
				// 上下船的传送点修正 
				if (srcX == 33423 && srcY == 33502 && srcMapId == 4 // 往遗忘之岛的船
						|| srcX == 33424 && srcY == 33502 && srcMapId == 4
						|| srcX == 33425 && srcY == 33502 && srcMapId == 4
						|| srcX == 33426 && srcY == 33502 && srcMapId == 4
						|| srcX == 33427 && srcY == 33502 && srcMapId == 4
						|| srcX == 33428 && srcY == 33502 && srcMapId == 4
						|| srcX == 33429 && srcY == 33502 && srcMapId == 4
						|| srcX == 32735 && srcY == 32794 && srcMapId == 83) {
					dungeonType = DungeonType.SHIP_FOR_FI;
				} else if (srcX == 32936 && srcY == 33058 && srcMapId == 70 // FI船着场->行船
						|| srcX == 32732 && srcY == 32796 && srcMapId == 84) { // 行船->FI船着场
					dungeonType = DungeonType.SHIP_FOR_HEINE;
				} else if (srcX == 32750 && srcY == 32874 && srcMapId == 445 // 隐船着场->海贼岛行船
						|| srcX == 32732 && srcY == 32796 && srcMapId == 447) { // 海贼岛行船->隐船着场
					dungeonType = DungeonType.SHIP_FOR_PI;
				} else if (srcX == 32297 && srcY == 33087 && srcMapId == 440 // 海贼岛船着场->隐船着场行船
						|| srcX == 32735 && srcY == 32794 && srcMapId == 446) { // 隐船着场行船->海贼岛船着场
					dungeonType = DungeonType.SHIP_FOR_HIDDENDOCK;
				} else if (srcX == 32630 && srcY == 32983 && srcMapId == 0
						|| srcX == 32631 && srcY == 32983 && srcMapId == 0
						|| srcX == 32632 && srcY == 32983 && srcMapId == 0
						|| srcX == 32733 && srcY == 32796 && srcMapId == 5
						|| srcX == 32734 && srcY == 32796 && srcMapId == 5
						|| srcX == 32735 && srcY == 32796 && srcMapId == 5) { // 往古鲁丁的船
					dungeonType = DungeonType.SHIP_FOR_GLUDIN;
				} else if (srcX == 32540 && srcY == 32728 && srcMapId == 4
						|| srcX == 32541 && srcY == 32728 && srcMapId == 4
						|| srcX == 32542 && srcY == 32728 && srcMapId == 4
						|| srcX == 32543 && srcY == 32728 && srcMapId == 4
						|| srcX == 32544 && srcY == 32728 && srcMapId == 4
						|| srcX == 32734 && srcY == 32794 && srcMapId == 6
						|| srcX == 32735 && srcY == 32794 && srcMapId == 6
						|| srcX == 32736 && srcY == 32794 && srcMapId == 6
						|| srcX == 32737 && srcY == 32794 && srcMapId == 6) { // 往说话之岛的船
					dungeonType = DungeonType.SHIP_FOR_ISLAND;
				}
				// 上下船的传送点修正  end
				NewDungeon newDungeon = new NewDungeon(newX, newY,
						(short) newMapId, heading, dungeonType);
				if (_dungeonMap.containsKey(key)) {
					_log.info( "同样dungeon。key=" + key);
				}
				_dungeonMap.put(key, newDungeon);
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private static class NewDungeon {
		int _newX;
		int _newY;
		short _newMapId;
		int _heading;
		DungeonType _dungeonType;

		private NewDungeon(int newX, int newY, short newMapId, int heading,
				DungeonType dungeonType) {
			_newX = newX;
			_newY = newY;
			_newMapId = newMapId;
			_heading = heading;
			_dungeonType = dungeonType;

		}
	}

	public boolean dg(int locX, int locY, int mapId, L1PcInstance pc) {
		long servertime = L1GameTimeClock.getInstance().getGameTime()
				.getSeconds();
		long nowtime = servertime % 86400;
		String key = new StringBuilder().append(mapId).append(locX)
				.append(locY).toString();
		if (_dungeonMap.containsKey(key)) {
			NewDungeon newDungeon = _dungeonMap.get(key);
			short newMap = newDungeon._newMapId;
			int newX = newDungeon._newX;
			int newY = newDungeon._newY;
			int heading = newDungeon._heading;
			DungeonType dungeonType = newDungeon._dungeonType;
			boolean teleportable = false;

			if (dungeonType == DungeonType.NONE) {
				teleportable = true;
			} else if (dungeonType == DungeonType.SHIP_FOR_FI) { //前往遗忘岛
				if ((nowtime >= 0  && nowtime < 60 * 60 // 0~1
							|| nowtime >= 180 * 60 && nowtime < 240 * 60 // 3~4
							|| nowtime >= 360 * 60 && nowtime < 420 * 60 // 6~7
							|| nowtime >= 540 * 60 && nowtime < 600 * 60 // 9~10
							|| nowtime >= 720 * 60 && nowtime < 780 * 60 // 12~13
							|| nowtime >= 900 * 60 && nowtime < 960 * 60 // 15~16
							|| nowtime >= 1080 * 60 && nowtime < 1140 * 60 // 18~19
							|| nowtime >= 1260 * 60 && nowtime < 1320 * 60 // 21~22
					) && pc.getInventory().checkItem(40300, 1)&&pc.getLevel()>=45) { // 忘岛行
					teleportable = true;
				}
			} else if (dungeonType == DungeonType.SHIP_FOR_HEINE) { // FI船场->行船
																	// /
																	// 行船->FI船场
				if ((nowtime >= 90 * 60 && nowtime < 150 * 60 // 1.30~2.30
							|| nowtime >= 270 * 60 && nowtime < 330 * 60 // 4.30~5.30
							|| nowtime >= 450 * 60 && nowtime < 510 * 60 // 7.30~8.30
							|| nowtime >= 630 * 60 && nowtime < 690 * 60 // 10.30~11.30
							|| nowtime >= 810 * 60 && nowtime < 870 * 60 // 13.30~14.30
							|| nowtime >= 990 * 60 && nowtime < 1050 * 60 // 16.30~17.30
							|| nowtime >= 1170 * 60 && nowtime < 1230 * 60 // 19.30~20.30
							|| nowtime >= 1350 * 60 && nowtime < 1410 * 60 // 22.30~23.30
					) && pc.getInventory().checkItem(40301, 1)) { // 行
					teleportable = true;
				}
			} else if (dungeonType == DungeonType.SHIP_FOR_PI) {// 船场->海贼岛行船
																// /
																// 海贼岛行船->船场
				if ((nowtime >= 90 * 60 && nowtime < 150 * 60 // 1.30~2.30
							|| nowtime >= 270 * 60 && nowtime < 330 * 60 // 4.30~5.30
							|| nowtime >= 450 * 60 && nowtime < 510 * 60 // 7.30~8.30
							|| nowtime >= 630 * 60 && nowtime < 690 * 60 // 10.30~11.30
							|| nowtime >= 810 * 60 && nowtime < 870 * 60 // 13.30~14.30
							|| nowtime >= 990 * 60 && nowtime < 1050 * 60 // 16.30~17.30
							|| nowtime >= 1170 * 60 && nowtime < 1230 * 60 // 19.30~20.30
							|| nowtime >= 1350 * 60 && nowtime < 1410 * 60 // 22.30~23.30
					) && pc.getInventory().checkItem(40302, 1)) { // 海贼岛行
					teleportable = true;
				}
			} else if (dungeonType == DungeonType.SHIP_FOR_HIDDENDOCK) { // 海贼岛船场->船场行船
																			// /
																			// 船场行船->海贼岛船场
				if ((nowtime >= 0 && nowtime < 60 * 60 // 0~1
							|| nowtime >= 180 * 60 && nowtime < 240 * 60 // 3~4
							|| nowtime >= 360 * 60 && nowtime < 420 * 60 // 6~7
							|| nowtime >= 540 * 60 && nowtime < 600 * 60 // 9~10
							|| nowtime >= 720 * 60 && nowtime < 780 * 60 // 12~13
							|| nowtime >= 900 * 60 && nowtime < 960 * 60 // 15~16
							|| nowtime >= 1080 * 60 && nowtime < 1140 * 60 // 18~19
							|| nowtime >= 1260 * 60 && nowtime < 1320 * 60 // 21~22
					) && pc.getInventory().checkItem(40303, 1)) { // 船场行
					teleportable = true;
				}
			} else if (dungeonType == DungeonType.SHIP_FOR_GLUDIN) { // 往古鲁丁的船
				if ((nowtime >= 90 * 60 && nowtime < 150 * 60
						|| nowtime >= 270 * 60 && nowtime < 330 * 60
						|| nowtime >= 450 * 60 && nowtime < 510 * 60
						|| nowtime >= 630 * 60 && nowtime < 690 * 60
						|| nowtime >= 810 * 60 && nowtime < 870 * 60
						|| nowtime >= 990 * 60 && nowtime < 1050 * 60
						|| nowtime >= 1170 * 60 && nowtime < 1230 * 60
						|| nowtime >= 1350 * 60 && nowtime < 1410 * 60)
						&& pc.getInventory().checkItem(40299, 1)) { // 往古鲁丁的船票
					teleportable = true;
				}
			} else if (dungeonType == DungeonType.SHIP_FOR_ISLAND) { // 往说话之岛的船
				if ((nowtime >= 0 && nowtime < 1 * 3600
						|| nowtime >= 3 * 3600 && nowtime < 4 * 3600
						|| nowtime >= 6 * 3600 && nowtime < 7 * 3600
						|| nowtime >= 9 * 3600 && nowtime < 10 * 3600
						|| nowtime >= 12 * 3600 && nowtime < 13 * 3600
						|| nowtime >= 15 * 3600 && nowtime < 16 * 3600
						|| nowtime >= 18 * 3600 && nowtime < 19 * 3600
						|| nowtime >= 21 * 3600 && nowtime < 22 * 3600)
						&& pc.getInventory().checkItem(40298, 1)) { // 往说话之岛的船票
					teleportable = true;
				}
			}
			if (teleportable) {
				// 2秒间无敌（态）。
				pc.setSkillEffect(L1SkillId.ABSOLUTE_BARRIER, 2000);
				pc.stopHpRegeneration();
				pc.stopMpRegeneration();
				pc.stopMpRegenerationByDoll();
				L1Teleport.teleport(pc, newX, newY, newMap, heading, true);
				return true;
			}
		}
		return false;
	}

	/**获取飞往的新地图id*/
	public int getNewMapId(int locX, int locY, int mapId, L1PcInstance pc) {
		String key = new StringBuilder().append(mapId).append(locX)
				.append(locY).toString();
		if (_dungeonMap.containsKey(key)) {
			return (int)_dungeonMap.get(key)._newMapId;
		}
		return 0;
	}
}
