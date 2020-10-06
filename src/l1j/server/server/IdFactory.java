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
package l1j.server.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

public class IdFactory {
	private static final Log _log = LogFactory.getLog(IdFactory.class);

	private AtomicInteger _curId;
	
	private int _gamId;	// 赌场GAMID
	
	private AtomicInteger _BigHotId;

	private Object _monitor = new Object();

	private static final int FIRST_ID = 0x1000;

	private static IdFactory _instance = new IdFactory();
	
	private final ArrayList<Integer> _BigHotblingList = new ArrayList<Integer>();	// 大乐透
	
	private final ArrayList<Integer> _gamblingList = new ArrayList<Integer>();	// 奇岩赌场

	private IdFactory() {
		loadState();
	}

	public static IdFactory getInstance() {
		return _instance;
	}
	
	// 将赌场场次编号加入清单(id)
		public void addGamId(int i) {
			_gamblingList.add(i);
		}
	// 传回可用场场次编号
		public int nextGamId() {
			_gamId++;
			// 该编号已在视图中
			while (_gamblingList.contains(_gamId)) {
				_gamId++;
			}
			return _gamId;
		}
	
	// 将大乐透场次编号加入清单(id)
		public void addBigHotId(int i) {
			_BigHotblingList.add(i);
		}
		
		// 传回可用场场次编号
		public int nextBigHotId() {
			// 该编号已在视图中
			while (_BigHotblingList.contains(_BigHotId.get())) {
				_BigHotId.getAndIncrement();
			}
			return _BigHotId.getAndIncrement();
		}

	public int nextId() {
		synchronized (_monitor) {
			return _curId.getAndIncrement();
		}
	}

	private void loadState() {
		// DBMAXID求
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con
					.prepareStatement("select max(id)+1 as nextid from (select id from character_items union all select id from character_teleport union all select id from character_warehouse union all select objid as id from characters union all select clan_id as id from clan_data union all select id from clan_warehouse union all select objid as id from pets) t");
			rs = pstm.executeQuery();

			int id = 0;
			if (rs.next()) {
				id = rs.getInt("nextid");
			}
			if (id < FIRST_ID) {
				id = FIRST_ID;
			}
			_curId = new AtomicInteger(id);
			_log.info("载入已用最大ID: " + _curId);
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
