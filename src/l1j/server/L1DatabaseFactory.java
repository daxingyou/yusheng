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
package l1j.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * DB各种提供.
 */
public class L1DatabaseFactory {
	/** . */
	private static L1DatabaseFactory _instance;

	/** DB接续情报？. */
	private ComboPooledDataSource _source;

	/** 用. */
	private static final Log _log = LogFactory.getLog(L1DatabaseFactory.class);


	/* DB必要各情报 */
	/** DB接续. */
	private static String _driver;

	/** DBURL. */
	private static String _url;

	/** DB接续名. */
	private static String _user;

	/** DB接续. */
	private static String _password;

	/**
	 * DB必要各情报保存.
	 *
	 * @param driver
	 *            DB接续
	 * @param url
	 *            DBURL
	 * @param user
	 *            DB接续名
	 * @param password
	 *            DB接续
	 */
	public static void setDatabaseSettings(final String driver,
			final String url, final String user, final String password) {
		_driver = driver;
		_url = url;
		_user = user;
		_password = password;
	}

	/**
	 * DB接续情报设定接续.
	 *
	 * @throws SQLException
	 */
	public L1DatabaseFactory() throws SQLException {
		try {
			// DatabaseFactoryL2J一部除拜借
			_source = new ComboPooledDataSource();
			_source.setDriverClass(_driver);
			_source.setJdbcUrl(_url);
			_source.setUser(_user);
			_source.setPassword(_password);

			/* Test the connection */
			_source.getConnection().close();
		} catch (SQLException x) {
			_log.info("Database Connection FAILED");
			// rethrow the exception
			throw x;
		} catch (Exception e) {
			_log.info("Database Connection FAILED");
			throw new SQLException("could not init DB connection:" + e);
		}
	}

	/**
	 * 时DB切断.
	 */
	public void shutdown() {
		try {
			_source.close();
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		try {
			_source = null;
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 返（null作成).
	 *
	 * @return L1DatabaseFactory
	 * @throws SQLException
	 */
	public static L1DatabaseFactory getInstance() throws SQLException {
		if (_instance == null) {
			_instance = new L1DatabaseFactory();
		}
		return _instance;
	}

	/**
	 * DB接续、返.
	 *
	 * @return Connection 
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		Connection con = null;

		while (con == null) {
			try {
				con = _source.getConnection();
			} catch (SQLException e) {
				_log.info("L1DatabaseFactory: getConnection() failed, trying again "
								+ e);
			}
		}
		return con;
	}
}
