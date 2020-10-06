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

import java.sql.Timestamp;

public class L1LastOnline {
	public L1LastOnline() {
	}

	private int _Id;

	public int getId() {
		return _Id;
	}

	public void setId(int i) {
		_Id = i;
	}

	private Timestamp _time = null;

	public Timestamp getLastTime() {
		return _time;
	}

	public void setLastTime(Timestamp i) {
		_time = i;
	}
}