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
package l1j.server.server.clientpackets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.Config;
import l1j.server.server.Account;
import l1j.server.server.AccountAlreadyLoginException;
//import l1j.server.server.ClientThread;
import l1j.server.server.GameServerFullException;
import l1j.server.server.LoginController;
import l1j.server.server.datatables.IPCountTable;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.serverpackets.S_CommonNews;
import l1j.server.server.serverpackets.S_LoginResult;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_AuthLogin extends ClientBasePacket {

	private static final String C_AUTH_LOGIN = "[C] C_AuthLogin";
	private static final Log _log = LogFactory.getLog(C_AuthLogin.class);
	
	private static final String _check_accname = "abcdefghijklmnopqrstuvwxyz0123456789";

// 	private static final String _check_pwd = "abcdefghijklmnopqrstuvwxyz0123456789!_=+-?.#";

	public C_AuthLogin(byte[] decrypt, LineageClient client) {
		super(decrypt);
		String accountName = readS().toLowerCase();
		String password = readS();

		String ip = client.getIp();
		String host = client.getHostname();
		boolean iserror = false;
		// 登入名称转为小写
		final String loginName = accountName.toLowerCase();

		if (loginName.length() > 12) {
			_log.info("不合法的帐号长度:" + client.getIp().toString());
			iserror = true;
		}
		for (int i = 0 ; i < loginName.length() ; i++) {
			final String ch = loginName.substring(i, i + 1);
			if (!_check_accname.contains(ch)) {
				_log.info("不被允许的帐号字元:" + client.getIp().toString());
				iserror = true;
				break;
			}
		}
		//final String password = this.readS();
		if (password.length() > 13) {
			_log.info("不合法的密码长度:" + client.getIp().toString());
			iserror = true;
		}
		/*for (int i = 0 ; i < password.length() ; i++) {
			final String ch = password.substring(i, i + 1);
			if (!_check_pwd.contains(ch.toLowerCase())) {
				_log.info("不被允许的密码字元!");
				iserror = true;
				break;
			}
		}*/
		if (client.getAccount()!=null) {
			client.getsSession().close(true);
			return;
		}
		if (iserror) {
			client.sendPacket(new S_LoginResult(S_LoginResult.EVENT_PASS_CHECK));
			return;
		}
		final int ipcount = IPCountTable.get().getIpcount(ip);
		final int linipct = LoginController.getInstance().getIPcount(ip);
		if (linipct >= ipcount) {
			_log.info("同Ip限制达到上限" + ipcount + "无法登入: account="
					+ loginName + " host=" + ip);
			client.kick();
			return;
		}
		_log.info("Request AuthLogin from user : " + accountName);
		Account account = Account.load(accountName);
		client.addclienter();
		if (account == null) {
			if (Config.AUTO_CREATE_ACCOUNTS) {
				if (!client.isNewLogin()) {
					System.out.println("拒绝重复注册帐号！");
					client.kick();
					return;
				}
				account = Account.create(accountName, password, ip, host);
				client.setNewLogin(false);
			} else {
				_log.info("account missing for user " + accountName);
			}
		}
		if (account == null || !account.validatePassword(password)||iserror) {
			client.sendPacket(new S_LoginResult(
					S_LoginResult.EVENT_PASS_CHECK));
			return;
		}
		if (account.isBanned()) { // BAN
			_log.info("拒绝封锁中的帐号登入游戏。帐号:" + accountName + " 位址:"
					+ host);
			client.sendPacket(new S_LoginResult(
					S_LoginResult.EVENT_CANT_LOGIN));
			return;
		}


		try {
			LoginController.getInstance().login(client, account);
			//删除Account.updateLastActive(account); // 最终日更新
			//更新IP 
			Account.updateLastActive(account, ip);
			//更新IP  end
			client.setAccount(account);
			client.sendPacket(new S_LoginResult(S_LoginResult.REASON_LOGIN_OK));
			//删除client.sendPacket(new S_CommonNews());
			//帐号登入是否显示公告 
			if (Config.SERVER_BORAD == true) {
				client.sendPacket(new S_CommonNews());
			} else {
				//new C_CommonClick(_client);
			}
            //帐号登入是否显示公告  end
		} catch (GameServerFullException e) {
			client.kick();
			_log.info("因为连线人数达到上限 (" + client.getHostname()
					+ ") 无法连线。");
			return;
		} catch (AccountAlreadyLoginException e) {
			client.kick();
			_log.info("相同帐号重复连线 (" + client.getHostname()
					+ ") 连线被强制切断。");
			return;
		}
	}

	@Override
	public String getType() {
		return C_AUTH_LOGIN;
	}

}