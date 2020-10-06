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

public class L1Weapon extends L1Item {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public L1Weapon() {
	}

	private int _hitModifier = 0; // ● 命中率补正

	@Override
	public int getHitModifier() {
		return _hitModifier;
	}

	public void setHitModifier(int i) {
		_hitModifier = i;
	}

	private int _dmgModifier = 0; // ● 补正

	@Override
	public int getDmgModifier() {
		return _dmgModifier;
	}

	public void setDmgModifier(int i) {
		_dmgModifier = i;
	}

	private int _magicDmgModifier = 0; // ● 攻击魔法补正

	@Override
	public int getMagicDmgModifier() {
		return _magicDmgModifier;
	}

	public void setMagicDmgModifier(int i) {
		_magicDmgModifier = i;
	}

	private int _canbedmg = 0; // ● 损伤有无

	@Override
	public int get_canbedmg() {
		return _canbedmg;
	}

	public void set_canbedmg(int i) {
		_canbedmg = i;
	}

	@Override
	public boolean isTwohandedWeapon() {
		int weapon_type = getType();
		
		boolean bool = (weapon_type == 3 || weapon_type == 4
				|| weapon_type == 5 || weapon_type == 11
				|| weapon_type == 12 || weapon_type == 15
				|| weapon_type == 16);

		return bool;
	}

	private int _range = 0; // ● 射程範囲

	@Override
	public int getRange() {
		return _range;
	}

	public void setRange(int i) {
		_range = i;
	}
	
	private int _double_dmg_chance = 0; // ● 双刀机率

	@Override
	public int get_double_dmg_chance() {
		return _double_dmg_chance;
	}

	public void set_double_dmg_chance(int i) {
		_double_dmg_chance = i;
	}
}
