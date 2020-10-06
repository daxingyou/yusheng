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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import l1j.server.Base64;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 为样提供.
 */
public class Account {
	/** 名. */
	private String _name;

	/** 接续先IP. */
	private String _ip;

	/** (暗号化). */
	private String _password;

	/** 最终日. */
	private Timestamp _lastActive;

	/** (GM？). */
	private int _accessLevel;

	/** 接续先名. */
	private String _host;

	/** 禁止有无(True禁止). */
	private boolean _banned;

	/** 有效否(True有效). */
	private boolean _isValid = false;
	
	
	private int _twopassword = -256;

	/** 用. */
	private static final Log _log = LogFactory.getLog(Account.class);

	/**
	 * .
	 */
	private Account() {
	}

	/**
	 * 暗号化.
	 *
	 * @param rawPassword
	 *            平文
	 * @return String
	 * @throws NoSuchAlgorithmException
	 *             暗号使用环境时
	 * @throws UnsupportedEncodingException
	 *             文字时
	 */
	public static String encodePassword(final String rawPassword)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		byte[] buf = rawPassword.getBytes("UTF-8");
		buf = MessageDigest.getInstance("SHA").digest(buf);
		
		return Base64.encodeBytes(buf);
	}

	/**
	 * 新规作成.
	 *
	 * @param name
	 *            名
	 * @param rawPassword
	 *            平文
	 * @param ip
	 *            接续先IP
	 * @param host
	 *            接续先名
	 * @return Account
	 */
	public static Account create(final String name, final String rawPassword,
			final String ip, final String host) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {

			Account account = new Account();
			account._name = name;
			account._password = encodePassword(rawPassword);
			account._ip = ip;
			account._host = host;
			account._banned = false;
			account._lastActive = new Timestamp(System.currentTimeMillis());

			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "INSERT INTO accounts SET login=?,password=?,twopassword=?,lastactive=?,access_level=?,ip=?,host=?,banned=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, account._name);
			pstm.setString(2, account._password);
			pstm.setInt(3, account._twopassword);
			pstm.setTimestamp(4, account._lastActive);
			pstm.setInt(5, 0);
			pstm.setString(6, account._ip);
			pstm.setString(7, account._host);
			pstm.setInt(8, account._banned ? 1 : 0);
			pstm.execute();
			_log.info("created new account for " + name +" ip:"+ip+" host:"+host);

			return account;
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			_log.error(e.getLocalizedMessage(), e);
		} catch (UnsupportedEncodingException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return null;
	}

	/**
	 * 情报DB抽出.
	 *
	 * @param name
	 *            名
	 * @return Account
	 */
	public static Account load(final String name) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;

		Account account = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "SELECT * FROM accounts WHERE login=? LIMIT 1";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, name);
			rs = pstm.executeQuery();
			if (!rs.next()) {
				return null;
			}
			account = new Account();
			account._name = rs.getString("login");
			account._password = rs.getString("password");
			account._twopassword = rs.getInt("twopassword");
			account._lastActive = rs.getTimestamp("lastactive");
			account._accessLevel = rs.getInt("access_level");
			account._ip = rs.getString("ip");
			account._host = rs.getString("host");
			account._banned = rs.getInt("banned") == 0 ? false : true;

			_log.info("account:"+account._name+" exists");
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}

		return account;
	}

	/**
	 * 最终日DB反映.
	 *
	 * @param account
	 *            
	 */
	//删除public static void updateLastActive(final Account account) {
	//更新IP 
	public static void updateLastActive(final Account account, final String ip) {
	//更新IP  end
		Connection con = null;
		PreparedStatement pstm = null;
		Timestamp ts = new Timestamp(System.currentTimeMillis());

		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			//删除String sqlstr = "UPDATE accounts SET lastactive=? WHERE login = ?";
			//删除pstm = con.prepareStatement(sqlstr);
			//删除pstm.setTimestamp(1, ts);
			//删除pstm.setString(2, account.getName());
			//更新IP 
			String sqlstr = "UPDATE accounts SET lastactive=?,ip=? WHERE login = ?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setTimestamp(1, ts);
			pstm.setString(2, ip);
			pstm.setString(3, account.getName());
			//更新IP  end
			pstm.execute();
			account._lastActive = ts;
			_log.info("update lastactive for " + account.getName());
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	//更新密码
	public void updatePassword(final String newPassword) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			//删除String sqlstr = "UPDATE accounts SET lastactive=? WHERE login = ?";
			//删除pstm = con.prepareStatement(sqlstr);
			//删除pstm.setTimestamp(1, ts);
			//删除pstm.setString(2, account.getName());
			//更新IP 
			String sqlstr = "UPDATE accounts SET password=? WHERE login = ?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, newPassword);
			pstm.setString(2, _name);
			//更新IP  end
			pstm.execute();
			_log.info("update access_level for " + _name);
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	/**
	 * 所有数.
	 *
	 * @return int
	 */
	public int countCharacters() {
		int result = 0;
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "SELECT count(*) as cnt FROM characters WHERE account_name=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, _name);
			rs = pstm.executeQuery();
			if (rs.next()) {
				result = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}
	/**
	 * 无效.
	 *
	 * @param login
	 *            名
	 */
	public static void ban(final String login) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "UPDATE accounts SET banned=1 WHERE login=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, login);
			pstm.execute();
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	/**
	 * 入力DB上照合.
	 *
	 * @param rawPassword
	 *            平文
	 * @return boolean
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException 
	 */
	public boolean validatePassword(final String rawPassword){
		if (_password.equals("0")){
			try {
				updatePassword(encodePassword(rawPassword));
				_password = null;
				_isValid = true;
				return true;
			} catch (Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
		// 认证成功后再度认证场合失败。
		if (_isValid) {
			return false;
		}
		try {
			_isValid = _password.equals(encodePassword(rawPassword));
			if (_isValid) {
				_password = null; // 认证成功场合、破弃。
			}
			return _isValid;
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return false;
	}
	
	/**
	 * 更新二级密码
	 * @param newTwoPassword
	 */
	public void updateTwoPassword(final int newTwoPassword) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "UPDATE accounts SET twopassword=? WHERE login = ?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setInt(1, newTwoPassword);
			pstm.setString(2, _name);

			pstm.execute();
			
			_twopassword = newTwoPassword;
			setCheckTwopassword(true);
			
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	private boolean _checktwopassword = false;
	
	public void setCheckTwopassword(final boolean checktwopassword) {
		_checktwopassword = checktwopassword;
	}
	/**
	 * 返回是否验证二级密码
	 * @return
	 */
	public boolean isCheckTwopassword(){
		return _checktwopassword;
	}
	
	public boolean ispassok(String rawPassword)
	{
		boolean isok = false;
		try {
			isok = _password.equals(encodePassword(rawPassword));
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		return isok;
		
	}

	/**
	 * 有效返(True有效).
	 *
	 * @return boolean
	 */
	public boolean isValid() {
		return _isValid;
	}

	/**
	 * 返(True).
	 *
	 * @return boolean
	 */
	public boolean isGameMaster() {
		return 200 == _accessLevel;
	}

	/**
	 * 名取得.
	 *
	 * @return String
	 */
	public String getName() {
		return _name;
	}

	/**
	 * 接续先IP取得.
	 *
	 * @return String
	 */
	public String getIp() {
		return _ip;
	}

	/**
	 * 最终日取得.
	 */
	public Timestamp getLastActive() {
		return _lastActive;
	}


	/**
	 * 取得.
	 *
	 * @return int
	 */
	public int getAccountAccessLevel() {
		return _accessLevel;
	}
	
	public void setAccountAccessLevel(int AccountAccessLevel)
	{
		_accessLevel = AccountAccessLevel;		
	}

	/**
	 * 名取得.
	 *
	 * @return String
	 */
	public String getHost() {
		return _host;
	}

	/**
	 * 禁止情报取得.
	 *
	 * @return boolean
	 */
	public boolean isBanned() {
		return _banned;
	}
	
	/**
	 * 返回二级密码
	 * @return
	 */
	public int getTwoPassword(){
		return _twopassword;
	}

	public ArrayList<String> loadCharacterItems(final int char_objId) {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		final ArrayList<String> list = new ArrayList<String>();
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "SELECT item_name,count,enchantlvl FROM character_items WHERE char_id=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setInt(1, char_objId);
			rs = pstm.executeQuery();
			while (rs.next()) {
				final String itemName = rs.getString("item_name");
				final int count = rs.getInt("count");
				final int enchantlvl = rs.getInt("enchantlvl");
				final StringBuilder msg = new StringBuilder();
				msg.append(" +");
				msg.append(enchantlvl);
				msg.append(" ");
				msg.append(itemName);
				msg.append("(");
				msg.append(count);
				msg.append(")");
				
				list.add(msg.toString());
			}
		} catch (SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return list;
	}

	public boolean updaterecharBind(final int char_objId) {
		Connection con = null;
		PreparedStatement pstm = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			String sqlstr = "UPDATE characters set CharBind = 0,account_name = ? WHERE objid=?";
			pstm = con.prepareStatement(sqlstr);
			pstm.setString(1, _name);
			pstm.setInt(2, char_objId);
			pstm.execute();
			return true;
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return false;
	}
}
