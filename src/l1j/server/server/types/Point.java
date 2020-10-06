/* This program is free software; you can redistribute it and/or modify
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
package l1j.server.server.types;

public class Point {


	protected int _x = 0;
	protected int _y = 0;

	public Point() {
	}

	public Point(int x, int y) {
		_x = x;
		_y = y;
	}

	public Point(Point pt) {
		_x = pt._x;
		_y = pt._y;
	}

	public int getX() {
		return _x;
	}

	public void setX(int x) {
		_x = x;
	}

	public int getY() {
		return _y;
	}

	public void setY(int y) {
		_y = y;
	}

	public void set(Point pt) {
		_x = pt._x;
		_y = pt._y;
	}

	public void set(int x, int y) {
		_x = x;
		_y = y;
	}

	/**
	 * 指定座标直线距离返。
	 * 
	 * @param pt
	 *            座标保持Point
	 * @return 座标直线距离
	 */
	public double getLineDistance(Point pt) {
		long diffX = pt.getX() - this.getX();
		long diffY = pt.getY() - this.getY();
		return Math.sqrt((diffX * diffX) + (diffY * diffY));
	}
	
	/**
	 * 指定座標直線距離
	 * 
	 * @param x
	 * @param y
	 * @return 距離質
	 */
	public double getLineDistance(final int x, final int y) {
		final long diffX = x - this.getX();
		final long diffY = y - this.getY();
		return Math.sqrt((diffX * diffX) + (diffY * diffY));
	}

	/**
	 * 指定座标直线数返。
	 * 
	 * @param pt
	 *            座标保持Point
	 * @return 指定座标直线数。
	 */
	public int getTileLineDistance(Point pt) {
		return Math.max(Math.abs(pt.getX() - getX()), Math.abs(pt.getY()
				- getY()));
	}

	/**
	 * 指定座标数返。
	 * 
	 * @param pt
	 *            座标保持Point
	 * @return 指定座标数。
	 */
	public int getTileDistance(Point pt) {
		return Math.abs(pt.getX() - getX()) + Math.abs(pt.getY() - getY());
	}

	/**
	 * 旧版画面范围
	 * 
	 * @param pt
	 * @return 指定座标在18格范围内 返回true
	 */
	public boolean isInScreen(Point pt) {
		int dist = this.getTileDistance(pt);
	    if (dist > 22) {
	     return false;
	    } else if (dist <= 19) {
	     return true;
	    } else {
	     int dist2 = Math.abs(pt.getX() - (this.getX() - 20)) 
	       + Math.abs(pt.getY() - (this.getY() - 20));
	     if (23 <= dist2 && dist2 <= 56) {
	      return true;
	     }
	     return false;
	    }
	}

	@Override
	public Point clone() {
		return new Point(this);
	}

	@Override
	public int hashCode() {
		return 7 * getX() + getY();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Point)) {
			return false;
		}
		Point pt = (Point) obj;
		return (this.getX() == pt.getX()) && (this.getY() == pt.getY());
	}
}
