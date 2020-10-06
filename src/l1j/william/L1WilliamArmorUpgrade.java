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

import java.util.logging.Logger;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.william.ArmorUpgrade;

// Referenced classes of package l1j.server.server.model:
// L1PcInstance

public class L1WilliamArmorUpgrade {


	private int _ArmorId;

	private int _UpgradeRnd;

	private int _UpgradeArmorId;

	private String _Materials;

	private String _Counts;

	public L1WilliamArmorUpgrade(int ArmorId, int UpgradeRnd, int UpgradeArmorId, String Materials, String Counts) {
		_ArmorId = ArmorId;
		_UpgradeRnd = UpgradeRnd;
		_UpgradeArmorId = UpgradeArmorId;
		_Materials = Materials;
		_Counts = Counts;
	}

	public int getArmorId() {
		return _ArmorId;
	}

	public int getUpgradeRnd() {
		return _UpgradeRnd;
	}

	public int getUpgradeArmorId() {
		return _UpgradeArmorId;
	}

	public String getMaterials() {
		return _Materials;
	}

	public String getCounts() {
		return _Counts;
	}
}
