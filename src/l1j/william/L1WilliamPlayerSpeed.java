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

import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.world.L1World;
import l1j.william.PlayerSpeed;

public class L1WilliamPlayerSpeed {


	private int _polyid;
	private int _moveDouble;
	private int _atkDouble;
	private int _move_0;
	private int _atk_0;
	private int _move_4;
	private int _atk_4;
	private int _move_11;
	private int _atk_11;
	private int _move_20;
	private int _atk_20;
	private int _move_24;
	private int _atk_24;
	private int _move_40;
	private int _atk_40;
	private int _move_46;
	private int _atk_46;
	private int _move_50;
	private int _atk_50;
	private int _move_54;
	private int _atk_54;
	private int _move_58;
	private int _atk_58;
	private int _move_62;
	private int _atk_62;

	public L1WilliamPlayerSpeed(int polyid, int moveDouble, int atkDouble
		, int move_0, int atk_0, int move_4, int atk_4, int move_11, int atk_11
		, int move_20, int atk_20, int move_24, int atk_24, int move_40, int atk_40
		, int move_46, int atk_46, int move_50, int atk_50, int move_54, int atk_54
		, int move_58, int atk_58, int move_62, int atk_62) {
		_polyid = polyid;
		_moveDouble = moveDouble;
		_atkDouble = atkDouble;
		_move_0 = move_0;
		_atk_0 = atk_0;
		_move_4 = move_4;
		_atk_4 = atk_4;
		_move_11 = move_11;
		_atk_11 = atk_11;
		_move_20 = move_20;
		_atk_20 = atk_20;
		_move_24 = move_24;
		_atk_24 = atk_24;
		_move_40 = move_40;
		_atk_40 = atk_40;
		_move_46 = move_46;
		_atk_46 = atk_46;
		_move_50 = move_50;
		_atk_50 = atk_50;
		_move_54 = move_54;
		_atk_54 = atk_54;
		_move_58 = move_58;
		_atk_58 = atk_58;
		_move_62 = move_62;
		_atk_62 = atk_62;
	}

	public int getPolyId() {
		return _polyid;
	}

	public int getMoveDouble() {
		return _moveDouble;
	}

	public int getAtkDouble() {
		return _atkDouble;
	}

	public int getMove_0() {
		return _move_0;
	}

	public int getAtk_0() {
		return _atk_0;
	}

	public int getMove_4() {
		return _move_4;
	}

	public int getAtk_4() {
		return _atk_4;
	}

	public int getMove_11() {
		return _move_11;
	}

	public int getAtk_11() {
		return _atk_11;
	}

	public int getMove_20() {
		return _move_20;
	}

	public int getAtk_20() {
		return _atk_20;
	}

	public int getMove_24() {
		return _move_24;
	}

	public int getAtk_24() {
		return _atk_24;
	}

	public int getMove_40() {
		return _move_40;
	}

	public int getAtk_40() {
		return _atk_40;
	}

	public int getMove_46() {
		return _move_46;
	}

	public int getAtk_46() {
		return _atk_46;
	}

	public int getMove_50() {
		return _move_50;
	}

	public int getAtk_50() {
		return _atk_50;
	}

	public int getMove_54() {
		return _move_54;
	}

	public int getAtk_54() {
		return _atk_54;
	}

	public int getMove_58() {
		return _move_58;
	}

	public int getAtk_58() {
		return _atk_58;
	}

	public int getMove_62() {
		return _move_62;
	}

	public int getAtk_62() {
		return _atk_62;
	}
}
