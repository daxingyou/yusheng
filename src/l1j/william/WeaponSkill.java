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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;
import l1j.william.L1WilliamWeaponSkill;

public class WeaponSkill {
	private static final Log _log = LogFactory.getLog(WeaponSkill.class);


	private static WeaponSkill _instance;

	private final HashMap<Integer, L1WilliamWeaponSkill> _weaponIdIndex
			= new HashMap<Integer, L1WilliamWeaponSkill>();

	public static WeaponSkill getInstance() {
		if (_instance == null) {
			_instance = new WeaponSkill();
		}
		return _instance;
	}

	private WeaponSkill() {
		loadWeaponSkill();
	}

	private void loadWeaponSkill() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {

			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT * FROM william_weapon_skill");
			rs = pstm.executeQuery();
			fillWeaponSkill(rs);
		} catch (SQLException e) {
			_log.info("error while creating william_weapon_skill table",
					e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void fillWeaponSkill(ResultSet rs) throws SQLException {
		while (rs.next()) {
			int weaponId = rs.getInt("weapon_id");
			int probability = rs.getInt("probability");
			int material = rs.getInt("material");
			int materialCount = rs.getInt("material_count");
			int fixDamage = rs.getInt("fixDamage");
			int randomDamage = rs.getInt("randomDamage");
			int strDouble = rs.getInt("strDouble");
			int dexDouble = rs.getInt("dexDouble");
			int intDouble = rs.getInt("intDouble");
			int wisDouble = rs.getInt("wisDouble");
			int areaAtk = rs.getInt("areaAtk");
			int gfxId = rs.getInt("gfxId");
			int gfxIdTarget = rs.getInt("gfxIdTarget");
			int arrowType = rs.getInt("arrowType");
			int effectId = rs.getInt("effectId");
			int effectTime = rs.getInt("effectTime");
			
			L1WilliamWeaponSkill weaponSkill = new L1WilliamWeaponSkill(weaponId, probability, material, materialCount, 
					fixDamage, randomDamage, strDouble, dexDouble, intDouble, wisDouble, areaAtk, gfxId, gfxIdTarget, arrowType, effectId,
					effectTime);
			_weaponIdIndex.put(weaponId, weaponSkill);
		}
	}

	public L1WilliamWeaponSkill getTemplate(int weaponId) {
		return _weaponIdIndex.get(weaponId);
	}

}
