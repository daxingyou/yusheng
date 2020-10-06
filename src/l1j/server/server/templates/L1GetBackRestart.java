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

package l1j.server.server.templates;

public class L1GetBackRestart {
	public L1GetBackRestart() {
	}

	private int _id;

	public int get_id() {
		return _id;
	}

	public void set_id(int i) {
		_id = i;
	}

	private int _area;

	public int get_area() {
		return _area;
	}

	public void set_area(int i) {
		_area = i;
	}

	private String _areaname;

	public String get_areaname() {
		return _areaname;
	}

	public void set_areaname(String s) {
		_areaname = s;
	}

	private int _locx;

	public int get_locx() {
		return _locx;
	}

	public void set_locx(int i) {
		_locx = i;
	}

	private int _locy;

	public int get_locy() {
		return _locy;
	}

	public void set_locy(int i) {
		_locy = i;
	}

	private short _mapid;

	public short get_mapid() {
		return _mapid;
	}

	public void set_mapid(short i) {
		_mapid = i;
	}

}
