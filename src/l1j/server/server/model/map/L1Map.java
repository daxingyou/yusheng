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

import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.types.Point;

/**
 * L1Map 情报保持、对样提供。
 */
public abstract class L1Map {
	private static L1NullMap _nullMap = new L1NullMap();

	protected L1Map() {
	}

	/**
	 * ID返。
	 * 
	 * @return ID
	 */
	public abstract int getId();

	// TODO JavaDoc
	public abstract int getX();

	public abstract int getY();

	public abstract int getWidth();

	public abstract int getHeight();

	/**
	 * 指定座标值返。
	 * 
	 * 推奖。、既存互换性为提供。
	 * L1Map利用者通常、值格纳知必要。
	 * 、格纳值依存书。 等特殊场合限、利用。
	 * 
	 * @param x
	 *            座标X值
	 * @param y
	 *            座标Y值
	 * @return 指定座标值
	 */
	public abstract int getTile(int x, int y);

	/**
	 * 指定座标值返。
	 * 
	 * 推奖。、既存互换性为提供。
	 * L1Map利用者通常、值格纳知必要。
	 * 、格纳值依存书。 等特殊场合限、利用。
	 * 
	 * @param x
	 *            座标X值
	 * @param y
	 *            座标Y值
	 * @return 指定座标值
	 */
	public abstract int getOriginalTile(int x, int y);

	/**
	 * 指定座标范围内返。
	 * 
	 * @param pt
	 *            座标保持Point
	 * @return 范围内true
	 */
	public abstract boolean isInMap(Point pt);

	/**
	 * 指定座标范围内返。
	 * 
	 * @param x
	 *            座标X值
	 * @param y
	 *            座标Y值
	 * @return 范围内true
	 */
	public abstract boolean isInMap(int x, int y);

	/**
	 * 指定座标通行可能返。
	 * 
	 * @param pt
	 *            座标保持Point
	 * @return 通行可能true
	 */
	public abstract boolean isPassable(Point pt);

	/**
	 * 指定座标通行可能返。
	 * 
	 * @param x
	 *            座标X值
	 * @param y
	 *            座标Y值
	 * @return 通行可能true
	 */
	public abstract boolean isPassable(int x, int y);

	/**
	 * 指定座标heading方向通行可能返。
	 * 
	 * @param pt
	 *            座标保持Point
	 * @return 通行可能true
	 */
	public abstract boolean isPassable(Point pt, int heading);

	/**
	 * 指定座标heading方向通行可能返。
	 * 
	 * @param x
	 *            座标X值
	 * @param y
	 *            座标Y值
	 * @return 通行可能true
	 */
	public abstract boolean isPassable(int x, int y, int heading);
	
	/**
	 * 指定座标通行可能返。
	 * 
	 * @param pt
	 *            座标保持Point
	 * @return 通行可能true
	 */
	public abstract boolean isPassable2(Point pt);

	/**
	 * 指定座标通行可能返。
	 * 
	 * @param x
	 *            座标X值
	 * @param y
	 *            座标Y值
	 * @return 通行可能true
	 */
	public abstract boolean isPassable2(int x, int y);

	/**
	 * 指定座标heading方向通行可能返。
	 * 
	 * @param pt
	 *            座标保持Point
	 * @return 通行可能true
	 */
	public abstract boolean isPassable2(Point pt, int heading);

	/**
	 * 指定座标heading方向通行可能返。
	 * 
	 * @param x
	 *            座标X值
	 * @param y
	 *            座标Y值
	 * @return 通行可能true
	 */
	public abstract boolean isPassable2(int x, int y, int heading);

	/**
	 * 指定座标通行可能、不能设定。
	 * 
	 * @param pt
	 *            座标保持Point
	 * @param isPassable
	 *            通行可能true
	 */
	public abstract void setPassable(Point pt, boolean isPassable);

	/**
	 * 指定座标通行可能、不能设定。
	 * 
	 * @param x
	 *            座标X值
	 * @param y
	 *            座标Y值
	 * @param isPassable
	 *            通行可能true
	 */
	public abstract void setPassable(int x, int y, boolean isPassable);

	/**
	 * 指定座标返。
	 * 
	 * @param pt
	 *            座标保持Point
	 * @return true
	 */
	public abstract boolean isSafetyZone(Point pt);

	/**
	 * 指定座标返。
	 * 
	 * @param x
	 *            座标X值
	 * @param y
	 *            座标Y值
	 * @return true
	 */
	public abstract boolean isSafetyZone(int x, int y);

	/**
	 * 指定座标返。
	 * 
	 * @param pt
	 *            座标保持Point
	 * @return true
	 */
	public abstract boolean isCombatZone(Point pt);

	/**
	 * 指定座标返。
	 * 
	 * @param x
	 *            座标X值
	 * @param y
	 *            座标Y值
	 * @return true
	 */
	public abstract boolean isCombatZone(int x, int y);

	/**
	 * 指定座标返。
	 * 
	 * @param pt
	 *            座标保持Point
	 * @return true
	 */
	public abstract boolean isNormalZone(Point pt);

	/**
	 * 指定座标返。
	 * 
	 * @param x
	 *            座标X值
	 * @param y
	 *            座标Y值
	 * @return true
	 */
	public abstract boolean isNormalZone(int x, int y);

	/**
	 * 指定座标矢魔法通返。
	 * 
	 * @param pt
	 *            座标保持Point
	 * @return 矢魔法通场合、true
	 */
	public abstract boolean isArrowPassable(Point pt);

	/**
	 * 指定座标矢魔法通返。
	 * 
	 * @param x
	 *            座标X值
	 * @param y
	 *            座标Y值
	 * @return 矢魔法通场合、true
	 */
	public abstract boolean isArrowPassable(int x, int y);

	/**
	 * 指定座标heading方向矢魔法通返。
	 * 
	 * @param pt
	 *            座标保持Point
	 * @param heading
	 *            方向
	 * @return 矢魔法通场合、true
	 */
	public abstract boolean isArrowPassable(Point pt, int heading);

	/**
	 * 指定座标heading方向矢魔法通返。
	 * 
	 * @param x
	 *            座标X值
	 * @param y
	 *            座标Y值
	 * @param heading
	 *            方向
	 * @return 矢魔法通场合、true
	 */
	public abstract boolean isArrowPassable(int x, int y, int heading);

	/**
	 * 、水中返。
	 * 
	 * @return 水中、true
	 */
	public abstract boolean isUnderwater();

	/**
	 * 、可能返。
	 * 
	 * @return 可能、true
	 */
	public abstract boolean isMarkable();

	/**
	 * 、可能返。
	 * 
	 * @return 可能、true
	 */
	public abstract boolean isTeleportable();

	/**
	 * 、MAP超可能返。
	 * 
	 * @return 可能、true
	 */
	public abstract boolean isEscapable();

	/**
	 * 、复活可能返。
	 * 
	 * @return 复活可能、true
	 */
	public abstract boolean isUseResurrection();

	/**
	 * 、使用可能返。
	 * 
	 * @return 使用可能、true
	 */
	public abstract boolean isUsePainwand();

	/**
	 * 、返。
	 * 
	 * @return 、true
	 */
	public abstract boolean isEnabledDeathPenalty();

	/**
	 * 、连行返。
	 * 
	 * @return 连行true
	 */
	public abstract boolean isTakePets();

	/**
	 * 、呼出返。
	 * 
	 * @return 呼出true
	 */
	public abstract boolean isRecallPets();

	/**
	 * 指定座标钓返。
	 * 
	 * @param x
	 *            座标X值
	 * @param y
	 *            座标Y值
	 * @return 钓true
	 */
    public abstract boolean isFishingZone(int x, int y);

	public static L1Map newNull() {
		return _nullMap;
	}

	/**
	 * 指定pt文字列表现返。
	 */
	public abstract String toString(Point pt);
	
	
	public abstract L1DoorInstance isExistDoor(int x, int y);

	/**
	 * null返。
	 * 
	 * @return null、true
	 */
	public boolean isNull() {
		return false;
	}
}

/**
 * 何Map。
 */
class L1NullMap extends L1Map {
	public L1NullMap() {
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getTile(int x, int y) {
		return 0;
	}

	@Override
	public int getOriginalTile(int x, int y) {
		return 0;
	}

	@Override
	public boolean isInMap(int x, int y) {
		return false;
	}

	@Override
	public boolean isInMap(Point pt) {
		return false;
	}

	@Override
	public boolean isPassable(int x, int y) {
		return false;
	}

	@Override
	public boolean isPassable(Point pt) {
		return false;
	}

	@Override
	public boolean isPassable(int x, int y, int heading) {
		return false;
	}

	@Override
	public boolean isPassable(Point pt, int heading) {
		return false;
	}

	@Override
	public boolean isPassable2(int x, int y) {
		return false;
	}

	@Override
	public boolean isPassable2(Point pt) {
		return false;
	}

	@Override
	public boolean isPassable2(int x, int y, int heading) {
		return false;
	}

	@Override
	public boolean isPassable2(Point pt, int heading) {
		return false;
	}

	@Override
	public void setPassable(int x, int y, boolean isPassable) {
	}

	@Override
	public void setPassable(Point pt, boolean isPassable) {
	}

	@Override
	public boolean isSafetyZone(int x, int y) {
		return false;
	}

	@Override
	public boolean isSafetyZone(Point pt) {
		return false;
	}

	@Override
	public boolean isCombatZone(int x, int y) {
		return false;
	}

	@Override
	public boolean isCombatZone(Point pt) {
		return false;
	}

	@Override
	public boolean isNormalZone(int x, int y) {
		return false;
	}

	@Override
	public boolean isNormalZone(Point pt) {
		return false;
	}

	@Override
	public boolean isArrowPassable(int x, int y) {
		return false;
	}

	@Override
	public boolean isArrowPassable(Point pt) {
		return false;
	}

	@Override
	public boolean isArrowPassable(int x, int y, int heading) {
		return false;
	}

	@Override
	public boolean isArrowPassable(Point pt, int heading) {
		return false;
	}

	@Override
	public boolean isUnderwater() {
		return false;
	}

	@Override
	public boolean isMarkable() {
		return false;
	}

	@Override
	public boolean isTeleportable() {
		return false;
	}

	@Override
	public boolean isEscapable() {
		return false;
	}

	@Override
	public boolean isUseResurrection() {
		return false;
	}

	@Override
	public boolean isUsePainwand() {
		return false;
	}

	@Override
	public boolean isEnabledDeathPenalty() {
		return false;
	}

	@Override
	public boolean isTakePets() {
		return false;
	}

	@Override
	public boolean isRecallPets() {
		return false;
	}

	@Override
	public boolean isFishingZone(int x, int y) {
		return false;
	}

	@Override
	public String toString(Point pt) {
		return "null";
	}

	@Override
	public boolean isNull() {
		return true;
	}
	
	@Override
	public L1DoorInstance isExistDoor(int x, int y){
		return null;
	}
}
