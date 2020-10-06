package l1j.server.server.clientpackets;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.Account;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.serverpackets.S_CommonNews;
import l1j.server.server.serverpackets.S_LoginResult;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class C_ChangePassWords extends ClientBasePacket {

	private static final String C_CHANGE_PASSWORDS = "[C] C_ChangePassWords";
	
	//private static final String _check_accname = "abcdefghijklmnopqrstuvwxyz0123456789";

    //private static final String _check_pwd = "abcdefghijklmnopqrstuvwxyz0123456789!_=+-?.#";
	
	private static final Log _log = LogFactory.getLog(C_ChangePassWords.class);

	public C_ChangePassWords(byte abyte0[], LineageClient _client)
			throws Exception {
		super(abyte0);
		try {
			String loginName = readS().toLowerCase();
			String oldpassword = readS();
			String newpassword = readS();
			boolean iserror = false;
			if (loginName.length() > 12) {
				_log.info("不合法的帐号长度:" + _client.getIp().toString());
				_client.sendPacket(new S_LoginResult(S_LoginResult.EVENT_CANT_LOGIN));
				return;
			}
			if (newpassword.length() > 13) {
				_log.info("不合法的密码长度:" + _client.getIp().toString());
				_client.sendPacket(new S_LoginResult(S_LoginResult.EVENT_CANT_LOGIN));
				return;
			}
			if (iserror) {
				_client.sendPacket(new S_LoginResult(S_LoginResult.EVENT_CANT_LOGIN));
				return;
			}
			Account account = Account.load(loginName);
			if (account == null || !account.validatePassword(oldpassword)) {
				_client.sendPacket(new S_LoginResult(S_LoginResult.EVENT_CANT_LOGIN));
				return;
			}
			if (account.getAccountAccessLevel() != 1) {
				_client.sendPacket(new S_CommonNews("请去商城购买改密码卷才能使用此项功能！"));
				_client.sendPacket(new S_LoginResult(S_LoginResult.EVENT_CANT_LOGIN));
			}else {
				 String newPassword = "";
				 try {
					    newPassword = Account.encodePassword(newpassword);
					   } catch (NoSuchAlgorithmException e) {
					    e.printStackTrace();
					   } catch (UnsupportedEncodingException e) {
					    e.printStackTrace();
					   }
				ChanePassword(loginName, newPassword);
				WriteLogTxt.Recording("修改密码", "帐号#"+loginName+"#把登录密码:<"
						+oldpassword+"改成"+newpassword);
				_client.sendPacket(new S_LoginResult(S_LoginResult.EVENT_CANT_LOGIN));
			}				
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
		

	}
	
	private static void ChanePassword(final String login, final String newPassword) {
		  Connection con = null;
		  PreparedStatement pstm = null;
		  try {
		   con = L1DatabaseFactory.getInstance().getConnection();
		   String sqlstr = "UPDATE accounts SET password=? WHERE login=?";
		   pstm = con.prepareStatement(sqlstr);
		   pstm.setString(1, newPassword);
		   pstm.setString(2, login);
		   //bill00148
		   pstm.execute();
		  } catch (SQLException e) {
			  _log.error(e.getLocalizedMessage(), e);
		  } finally {
		   SQLUtil.close(pstm);
		   //僅分享於TGG
		   SQLUtil.close(con);
		  }
		}

	@Override
	public String getType() {
		return C_CHANGE_PASSWORDS;
	}
}
