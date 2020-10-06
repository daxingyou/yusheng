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
package l1j.server.server.datatables;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class LogEnchantTable {
	private static final Log _log = LogFactory.getLog(LogEnchantTable.class);

	public static void storeLogEnchant(int char_id,String char_name, int item_id, int old_enchantlvl,
			int new_enchantlvl) {
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		//System.out.println("记录2");
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("INSERT INTO log_enchant SET char_id=?,char_name=?, item_id=?, old_enchantlvl=?, new_enchantlvl=?,time=?");
			pstm.setInt(1, char_id);
			pstm.setString(2, char_name);
			pstm.setInt(3, item_id);
			pstm.setInt(4, old_enchantlvl);
			pstm.setInt(5, new_enchantlvl);
			pstm.setTimestamp(6, ts);
			pstm.execute();
			//System.out.println("记录3");
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);

		}
	}

}
