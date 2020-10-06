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
package l1j.server.server.model.map;

import l1j.server.server.ActionCodes;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.types.Heading;
import l1j.server.server.types.Point;

public class L1V1Map extends L1Map {
	
	private static final byte HEADING_TABLE_X[] =
		{ 0, 1, 1, 1, 0, -1, -1, -1 };

	private static final byte HEADING_TABLE_Y[] =
		{ -1, -1, 0, 1, 1, 1, 0, -1 };
		
	private int _mapId;

	private int _worldTopLeftX;

	private int _worldTopLeftY;

	private int _worldBottomRightX;

	private int _worldBottomRightY;

	private byte _map[][];

	private boolean _isUnderwater;

	private boolean _isMarkable;

	private boolean _isTeleportable;

	private boolean _isEscapable;

	private boolean _isUseResurrection;

	private boolean _isUsePainwand;

	private boolean _isEnabledDeathPenalty;

	private boolean _isTakePets;

	private boolean _isRecallPets;

	/*
	 * 情报1面保持仕方。 可读性大下良子真似。
	 */
	/**
	 * Mob通行不可能上存在示
	 */
	private static final byte BITFLAG_IS_IMPASSABLE = (byte) 128; // 1000 0000

	protected L1V1Map() {

	}

	public L1V1Map(int mapId, byte map[][], int worldTopLeftX,
			int worldTopLeftY, boolean underwater, boolean markable,
			boolean teleportable, boolean escapable, boolean useResurrection,
			boolean usePainwand, boolean enabledDeathPenalty, boolean takePets,
			boolean recallPets) {
		_mapId = mapId;
		_map = map;
		_worldTopLeftX = worldTopLeftX;
		_worldTopLeftY = worldTopLeftY;

		_worldBottomRightX = worldTopLeftX + map.length - 1;
		_worldBottomRightY = worldTopLeftY + map[0].length - 1;

		_isUnderwater = underwater;
		_isMarkable = markable;
		_isTeleportable = teleportable;
		_isEscapable = escapable;
		_isUseResurrection = useResurrection;
		_isUsePainwand = usePainwand;
		_isEnabledDeathPenalty = enabledDeathPenalty;
		_isTakePets = takePets;
		_isRecallPets = recallPets;
	}

	public L1V1Map(L1V1Map map) {
		_mapId = map._mapId;

		// _map
		_map = new byte[map._map.length][];
		for (int i = 0; i < map._map.length; i++) {
			_map[i] = map._map[i].clone();
		}

		_worldTopLeftX = map._worldTopLeftX;
		_worldTopLeftY = map._worldTopLeftY;
		_worldBottomRightX = map._worldBottomRightX;
		_worldBottomRightY = map._worldBottomRightY;

	}

	private int accessTile(int x, int y) {
		if (!isInMap(x, y)) { // XXX 。良。
			return 0;
		}

		return _map[x - _worldTopLeftX][y - _worldTopLeftY];
	}

	private int accessOriginalTile(int x, int y) {
		return accessTile(x, y) & (~BITFLAG_IS_IMPASSABLE);
	}

	private void setTile(int x, int y, int tile) {
		if (!isInMap(x, y)) { // XXX 。良。
			return;
		}
		_map[x - _worldTopLeftX][y - _worldTopLeftY] = (byte) tile;
	}

	/*
	 * 良气
	 */
	public byte[][] getRawTiles() {
		return _map;
	}

	@Override
	public int getId() {
		return _mapId;
	}

	@Override
	public int getX() {
		return _worldTopLeftX;
	}

	@Override
	public int getY() {
		return _worldTopLeftY;
	}

	@Override
	public int getWidth() {
		return _worldBottomRightX - _worldTopLeftX + 1;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
		return _worldBottomRightY - _worldTopLeftY + 1;
	}

	@Override
	public int getTile(int x, int y) {
		short tile = _map[x - _worldTopLeftX][y - _worldTopLeftY];
		if (0 != (tile & BITFLAG_IS_IMPASSABLE)) {
			return 300;
		}
		return accessOriginalTile(x, y);
	}

	@Override
	public int getOriginalTile(int x, int y) {
		return accessOriginalTile(x, y);
	}

	@Override
	public boolean isInMap(Point pt) {
		return isInMap(pt.getX(), pt.getY());
	}

	@Override
	public boolean isInMap(int x, int y) {
		// 茶色判定
		if (_mapId == 4
				&& (x < 32520 || y < 32070 || (y < 32190 && x < 33950))) {
			return false;
		}
		return (_worldTopLeftX <= x && x <= _worldBottomRightX
				&& _worldTopLeftY <= y && y <= _worldBottomRightY);
	}

	@Override
	public boolean isPassable(Point pt) {
		return isPassable(pt.getX(), pt.getY());
	}

	@Override
	public boolean isPassable(int x, int y) {
		return isPassable(x, y - 1, 4) || isPassable(x + 1, y, 6)
				|| isPassable(x, y + 1, 0) || isPassable(x - 1, y, 2);
	}

	@Override
	public boolean isPassable(Point pt, int heading) {
		return isPassable(pt.getX(), pt.getY(), heading);
	}

	@Override
	public boolean isPassable(int x, int y, int heading) {
		// 现在のタイル
		int tile1 = accessTile(x, y);
		// 移动予定のタイル
		int tile2;

		if (heading == 0) {
			tile2 = accessTile(x, y - 1);
		} else if (heading == 1) {
			tile2 = accessTile(x + 1, y - 1);
		} else if (heading == 2) {
			tile2 = accessTile(x + 1, y);
		} else if (heading == 3) {
			tile2 = accessTile(x + 1, y + 1);
		} else if (heading == 4) {
			tile2 = accessTile(x, y + 1);
		} else if (heading == 5) {
			tile2 = accessTile(x - 1, y + 1);
		} else if (heading == 6) {
			tile2 = accessTile(x - 1, y);
		} else if (heading == 7) {
			tile2 = accessTile(x - 1, y - 1);
		} else {
			return false;
		}

		if ((tile2 & BITFLAG_IS_IMPASSABLE) == BITFLAG_IS_IMPASSABLE) {
			return false;
		}

		// waja add 据说可以修正怪物穿墙
		if (!((tile2 & 0x02) == 0x02 || (tile2 & 0x01) == 0x01)) {
			return false;
		}
		// add end
		if (heading == 0) {
			return (tile1 & 0x02) == 0x02;
		} else if (heading == 1) {
			int tile3 = accessTile(x, y - 1);
			int tile4 = accessTile(x + 1, y);
			return (tile3 & 0x01) == 0x01 || (tile4 & 0x02) == 0x02;
		} else if (heading == 2) {
			return (tile1 & 0x01) == 0x01;
		} else if (heading == 3) {
			int tile3 = accessTile(x, y + 1);
			return (tile3 & 0x01) == 0x01;
		} else if (heading == 4) {
			return (tile2 & 0x02) == 0x02;
		} else if (heading == 5) {
			return (tile2 & 0x01) == 0x01 || (tile2 & 0x02) == 0x02;
		} else if (heading == 6) {
			return (tile2 & 0x01) == 0x01;
		} else if (heading == 7) {
			int tile3 = accessTile(x - 1, y);
			return (tile3 & 0x02) == 0x02;
		} else {
			return false;
		} // 5.20 End
	}
	
	@Override
	public boolean isPassable2(Point pt) {
		return isPassable2(pt.getX(), pt.getY());
	}

	@Override
	public boolean isPassable2(int x, int y) {
		return isPassable2(x, y - 1, 4) || isPassable2(x + 1, y, 6)
				|| isPassable2(x, y + 1, 0) || isPassable2(x - 1, y, 2);
	}

	@Override
	public boolean isPassable2(Point pt, int heading) {
		return isPassable2(pt.getX(), pt.getY(), heading);
	}

	@Override
	public boolean isPassable2(int x, int y, int heading) {
		// 现在のタイル
		int tile1 = accessTile(x, y);
		// 移动予定のタイル
		int tile2;

		if (heading == 0) {
			tile2 = accessTile(x, y - 1);
		} else if (heading == 1) {
			tile2 = accessTile(x + 1, y - 1);
		} else if (heading == 2) {
			tile2 = accessTile(x + 1, y);
		} else if (heading == 3) {
			tile2 = accessTile(x + 1, y + 1);
		} else if (heading == 4) {
			tile2 = accessTile(x, y + 1);
		} else if (heading == 5) {
			tile2 = accessTile(x - 1, y + 1);
		} else if (heading == 6) {
			tile2 = accessTile(x - 1, y);
		} else if (heading == 7) {
			tile2 = accessTile(x - 1, y - 1);
		} else {
			return false;
		}
		/*
		 * if ((tile2 & BITFLAG_IS_IMPASSABLE) == BITFLAG_IS_IMPASSABLE) {
		 * return false; }
		 */

		// 修正怪物穿墙
		if (!((tile2 & 0x02) == 0x02 || (tile2 & 0x01) == 0x01)) {
			return false;
		}
		// ~修正怪物穿墙
		if (heading == 0) {
			return (tile1 & 0x02) == 0x02;
		} else if (heading == 1) {
			int tile3 = accessTile(x, y - 1);
			int tile4 = accessTile(x + 1, y);
			return (tile3 & 0x01) == 0x01 || (tile4 & 0x02) == 0x02;
		} else if (heading == 2) {
			return (tile1 & 0x01) == 0x01;
		} else if (heading == 3) {
			int tile3 = accessTile(x, y + 1);
			return (tile3 & 0x01) == 0x01;
		} else if (heading == 4) {
			return (tile2 & 0x02) == 0x02;
		} else if (heading == 5) {
			return (tile2 & 0x01) == 0x01 || (tile2 & 0x02) == 0x02;
		} else if (heading == 6) {
			return (tile2 & 0x01) == 0x01;
		} else if (heading == 7) {
			int tile3 = accessTile(x - 1, y);
			return (tile3 & 0x02) == 0x02;
		}

		return false;
	}
	
/*	private boolean isPassableLeftThenUp(int x, int y) {
		return isPassable(x, y, Heading.LEFT)
				&& isPassable(x - 1, y, Heading.UP);
	}

	private boolean isPassableUpThenLeft(int x, int y) {
		return isPassable(x, y, Heading.UP) && isPassable(x, y, Heading.LEFT);
	}

	private boolean isPassableLeftThenDown(int x, int y) {
		return isPassable(x, y, Heading.LEFT)
				&& isPassable(x - 1, y, Heading.DOWN);
	}

	private boolean isPassableDownThenLeft(int x, int y) {
		return isPassable(x, y, Heading.DOWN)
				&& isPassable(x, y + 1, Heading.LEFT);
	}

	private boolean isPassableDownThenRight(int x, int y) {
		return isPassable(x, y, Heading.DOWN)
				&& isPassable(x, y, Heading.RIGHT);
	}

	private boolean isPassableRightThenDown(int x, int y) {
		return (isPassable(x, y, Heading.RIGHT) && isPassable(x + 1, y,
				Heading.DOWN));
	}

	private boolean isPassableRightThenUp(int x, int y) {
		return isPassable(x, y, 2) && isPassable(x + 1, y, 0);
	}

	private boolean isPassableUpThenRight(int x, int y) {
		return (isPassable(x, y, Heading.UP) && isPassable(x, y - 1,
				Heading.RIGHT));
	}*/

/*	private boolean isPassableLeftThenUp2(int x, int y) {
		return isPassable2(x, y, Heading.LEFT)
				&& isPassable2(x - 1, y, Heading.UP);
	}

	private boolean isPassableUpThenLeft2(int x, int y) {
		return isPassable2(x, y, Heading.UP) && isPassable2(x, y, Heading.LEFT);
	}

	private boolean isPassableLeftThenDown2(int x, int y) {
		return isPassable2(x, y, Heading.LEFT)
				&& isPassable2(x - 1, y, Heading.DOWN);
	}

	private boolean isPassableDownThenLeft2(int x, int y) {
		return isPassable2(x, y, Heading.DOWN)
				&& isPassable2(x, y + 1, Heading.LEFT);
	}

	private boolean isPassableDownThenRight2(int x, int y) {
		return isPassable2(x, y, Heading.DOWN)
				&& isPassable2(x, y, Heading.RIGHT);
	}

	private boolean isPassableRightThenDown2(int x, int y) {
		return (isPassable2(x, y, Heading.RIGHT) && isPassable2(x + 1, y,
				Heading.DOWN));
	}

	private boolean isPassableRightThenUp2(int x, int y) {
		return isPassable2(x, y, 2) && isPassable2(x + 1, y, 0);
	}

	private boolean isPassableUpThenRight2(int x, int y) {
		return (isPassable2(x, y, Heading.UP) && isPassable2(x, y - 1,
				Heading.RIGHT));
	}*/

	@Override
	public void setPassable(Point pt, boolean isPassable) {
		setPassable(pt.getX(), pt.getY(), isPassable);
	}

	@Override
	public void setPassable(int x, int y, boolean isPassable) {
		if (isPassable) {
			setTile(x, y, (short) (accessTile(x, y) & (~BITFLAG_IS_IMPASSABLE)));
		} else {
			setTile(x, y, (short) (accessTile(x, y) | BITFLAG_IS_IMPASSABLE));
		}
	}

	@Override
	public boolean isSafetyZone(Point pt) {
		return isSafetyZone(pt.getX(), pt.getY());
	}

	@Override
	public boolean isSafetyZone(int x, int y) {
		int tile = accessOriginalTile(x, y);

		return (tile & 0x30) == 0x10;
	}

	@Override
	public boolean isCombatZone(Point pt) {
		return isCombatZone(pt.getX(), pt.getY());
	}

	@Override
	public boolean isCombatZone(int x, int y) {
		int tile = accessOriginalTile(x, y);

		return (tile & 0x30) == 0x20;
	}

	@Override
	public boolean isNormalZone(Point pt) {
		return isNormalZone(pt.getX(), pt.getY());
	}

	@Override
	public boolean isNormalZone(int x, int y) {
		int tile = accessOriginalTile(x, y);
		return (tile & 0x30) == 0x00;
	}

	@Override
	public boolean isArrowPassable(Point pt) {
		return isArrowPassable(pt.getX(), pt.getY());
	}

	@Override
	public boolean isArrowPassable(int x, int y) {
		return (accessOriginalTile(x, y) & 0x0e) != 0;
	}

	@Override
	public boolean isArrowPassable(Point pt, int heading) {
		return isArrowPassable(pt.getX(), pt.getY(), heading);
	}
	
	@Override
	public boolean isArrowPassable(int x, int y, int heading) {
		// 現在のタイル
		int tile1 = accessTile(x, y);
		// 移動予定のタイル
		int tile2;

		if (heading == 0) {
			tile2 = accessTile(x, y - 1);
		} else if (heading == 1) {
			tile2 = accessTile(x + 1, y - 1);
		} else if (heading == 2) {
			tile2 = accessTile(x + 1, y);
		} else if (heading == 3) {
			tile2 = accessTile(x + 1, y + 1);
		} else if (heading == 4) {
			tile2 = accessTile(x, y + 1);
		} else if (heading == 5) {
			tile2 = accessTile(x - 1, y + 1);
		} else if (heading == 6) {
			tile2 = accessTile(x - 1, y);
		} else if (heading == 7) {
			tile2 = accessTile(x - 1, y - 1);
		} else {
			return false;
		}

		if (heading == 0) {
			return (tile1 & 0x08) == 0x08;
		} else if (heading == 1) {
			int tile3 = accessTile(x, y - 1);
			int tile4 = accessTile(x + 1, y);
			return (tile3 & 0x04) == 0x04 || (tile4 & 0x08) == 0x08;
		} else if (heading == 2) {
			return (tile1 & 0x04) == 0x04;
		} else if (heading == 3) {
			int tile3 = accessTile(x, y + 1);
			return (tile3 & 0x04) == 0x04;
		} else if (heading == 4) {
			return (tile2 & 0x08) == 0x08;
		} else if (heading == 5) {
			return (tile2 & 0x04) == 0x04 || (tile2 & 0x08) == 0x08;
		} else if (heading == 6) {
			return (tile2 & 0x04) == 0x04;
		} else if (heading == 7) {
			int tile3 = accessTile(x - 1, y);
			return (tile3 & 0x08) == 0x08;
		}

		return false;
	}
	
/*	@Override
	public boolean isArrowPassable(int x, int y, int heading) {
		// 移動予定の座標
		int newX = x + HEADING_TABLE_X[heading];
		int newY = y + HEADING_TABLE_Y[heading];

		// 現在のタイル
		int tile1 = accessTile(x, y);

		boolean flag = false;

		int tile2, tile3, tile4;
		switch (heading) {
			case 0:
				flag = (tile1 & 0x88) == 0x88;
				break;
			case 1:
				tile3 = accessTile(x, y - 1);
				tile4 = accessTile(x + 1, y);
				flag = (((tile1 & 0x88) == 0x88) && ((tile3 & 0x44) == 0x44)) || (((tile1 & 0x44) == 0x44) && ((tile4 & 0x88) == 0x88));
				break;
			case 2:
				flag = (tile1 & 0x44) == 0x44;
				break;
			case 3:
				tile2 = accessTile(newX, newY);
				tile3 = accessTile(x, y + 1);
				flag = (((tile1 & 0x44) == 0x44) && ((tile2 & 0x88) == 0x88)) || (((tile3 & 0x88) == 0x88) && ((tile3 & 0x44) == 0x44));
				break;
			case 4:
				tile2 = accessTile(newX, newY);
				flag = (tile2 & 0x88) == 0x88;
				break;
			case 5:
				tile2 = accessTile(newX, newY);
				tile3 = accessTile(x, y + 1);
				tile4 = accessTile(x - 1, y);
				flag = (((tile3 & 0x88) == 0x88) && ((tile2 & 0x44) == 0x44)) || (((tile4 & 0x44) == 0x44) && ((tile2 & 0x88) == 0x88));
				break;
			case 6:
				tile2 = accessTile(newX, newY);
				flag = (tile2 & 0x44) == 0x44;
				break;
			case 7:
				tile2 = accessTile(newX, newY);
				tile3 = accessTile(x - 1, y);
				flag = (((tile3 & 0x44) == 0x44) && ((tile3 & 0x88) == 0x88)) || (((tile1 & 0x88) == 0x88) && ((tile2 & 0x44) == 0x44));
				break;
		}

		return flag;
	}*/

/*	@Override
	public boolean isArrowPassable(int x, int y, int heading) {
		L1Location newLoc = new L1Location(x, y, _mapId);
		newLoc.forward(heading);
		// 現在のタイル
		int tile1 = accessTile(x, y);
		// 移動予定の座標
		int tile2 = accessTile(newLoc.getX(), newLoc.getY());
		if (isExistDoor(newLoc.getX(), newLoc.getY()) != null) {
			//System.out.println("類型1 X:"+newLoc.getX()+" Y:"+newLoc.getY());
			return false;
		}
		// 修正怪物穿墙
		if (!((tile2 & 0x02) == 0x02 || (tile2 & 0x01) == 0x01)) {
			return false;
		}
		if (heading == Heading.UP) {
			int dir = DoorSpawnTable.getInstance().getDoorDirection(newLoc);
			return (tile1 & 0x08) == 0x08 && dir != 1;
		} else if (heading == Heading.UP_RIGHT) {
			return isArrowPassableUpThenRight(x, y)
					|| isArrowPassableRightThenUp(x, y);
		} else if (heading == Heading.RIGHT) {
			int dir = DoorSpawnTable.getInstance().getDoorDirection(newLoc);
			return (tile1 & 0x04) == 0x04 && dir != 0;
		} else if (heading == Heading.DOWN_RIGHT) {
			return isArrowPassableRightThenDown(x, y)
					|| isArrowPassableDownThenRight(x, y);
		} else if (heading == Heading.DOWN) {
			int dir = DoorSpawnTable.getInstance().getDoorDirection(
					new L1Location(x, y, _mapId));
			return (tile2 & 0x08) == 0x08 && dir != 1;
		} else if (heading == Heading.DOWN_LEFT) {
			return isArrowPassableDownThenLeft(x, y)
					|| isArrowPassableLeftThenDown(x, y);
		} else if (heading == Heading.LEFT) {
			int dir = DoorSpawnTable.getInstance().getDoorDirection(
					new L1Location(x, y, _mapId));
			return (tile2 & 0x04) == 0x04 && dir != 0;
		} else if (heading == Heading.UP_LEFT) {
			return isArrowPassableUpThenLeft(x, y)
					|| isArrowPassableLeftThenUp(x, y);
		}

		return false;
	}*/
	
/*	private boolean isArrowPassableLeftThenUp(int x, int y) {
		return isArrowPassable(x, y, Heading.LEFT)
				&& isArrowPassable(x - 1, y, Heading.UP);
	}

	private boolean isArrowPassableUpThenLeft(int x, int y) {
		return isArrowPassable(x, y, Heading.UP)
				&& isArrowPassable(x, y, Heading.LEFT);
	}

	private boolean isArrowPassableLeftThenDown(int x, int y) {
		return isArrowPassable(x, y, Heading.LEFT)
				&& isArrowPassable(x - 1, y, Heading.DOWN);
	}

	private boolean isArrowPassableDownThenLeft(int x, int y) {
		return isArrowPassable(x, y, Heading.DOWN)
				&& isArrowPassable(x, y + 1, Heading.LEFT);
	}

	private boolean isArrowPassableDownThenRight(int x, int y) {
		return isArrowPassable(x, y, Heading.DOWN)
				&& isArrowPassable(x, y, Heading.RIGHT);
	}

	private boolean isArrowPassableRightThenDown(int x, int y) {
		return (isArrowPassable(x, y, Heading.RIGHT) && isArrowPassable(x + 1,
				y, Heading.DOWN));
	}

	private boolean isArrowPassableRightThenUp(int x, int y) {
		return isArrowPassable(x, y, 2) && isArrowPassable(x + 1, y, 0);
	}

	private boolean isArrowPassableUpThenRight(int x, int y) {
		return (isArrowPassable(x, y, Heading.UP) && isArrowPassable(x, y - 1,
				Heading.RIGHT));
	}
*/
	@Override
	public boolean isUnderwater() {
		return _isUnderwater;
	}

	@Override
	public boolean isMarkable() {
		return _isMarkable;
	}

	@Override
	public boolean isTeleportable() {
		return _isTeleportable;
	}

	@Override
	public boolean isEscapable() {
		return _isEscapable;
	}

	@Override
	public boolean isUseResurrection() {
		return _isUseResurrection;
	}

	@Override
	public boolean isUsePainwand() {
		return _isUsePainwand;
	}

	@Override
	public boolean isEnabledDeathPenalty() {
		return _isEnabledDeathPenalty;
	}

	@Override
	public boolean isTakePets() {
		return _isTakePets;
	}

	@Override
	public boolean isRecallPets() {
		return _isRecallPets;
	}

	@Override
	public boolean isFishingZone(int x, int y) {
		return accessOriginalTile(x, y) == 16;
	}

	@Override
	public String toString(Point pt) {
		return "" + getOriginalTile(pt.getX(), pt.getY());
	}
	
	@Override
	public L1DoorInstance isExistDoor(int x, int y) {
		for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
			if (_mapId != door.getMapId()) {
				continue;
			}
			if (door.getStatus() == ActionCodes.ACTION_Open) {
				continue;
			}
			if (door.isDead()) {
				continue;
			}
			int leftEdgeLocation = door.getLeftEdgeLocation();
			int rightEdgeLocation = door.getRightEdgeLocation();
			int size = rightEdgeLocation - leftEdgeLocation;
			if (size == 0) { // 1マス分の幅のドア
				if (x == door.getX() && y == door.getY()) {
					return door;
				}
			} else { // 2マス分以上の幅があるドア
				if (door.getDirection() == 0) { // ／向き
					for (int doorX = leftEdgeLocation; doorX <= rightEdgeLocation; doorX++) {
						if (x == doorX && y == door.getY()) {
							return door;
						}
					}
				} else { // ＼向き
					for (int doorY = leftEdgeLocation; doorY <= rightEdgeLocation; doorY++) {
						if (x == door.getX() && y == doorY) {
							return door;
						}
					}
				}
			}
		}
		return null;
	}
}
