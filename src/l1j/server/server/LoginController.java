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

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import l1j.server.server.mina.LineageClient;
import l1j.server.server.serverpackets.S_LoginResult;
import l1j.server.server.serverpackets.S_ServerMessage;

public class LoginController {
	private static LoginController _instance;


	private Map<String, LineageClient> _accounts = new ConcurrentHashMap<String, LineageClient>();

	private int _maxAllowedOnlinePlayers;

	private LoginController() {
	}

	public static LoginController getInstance() {
		if (_instance == null) {
			_instance = new LoginController();
		}
		return _instance;
	}
	
	public ArrayList<LineageClient> getClients(){
		ArrayList<LineageClient> clients = new ArrayList<LineageClient>();
		clients.addAll(_accounts.values());
		return clients;
	}

	public int getOnlinePlayerCount() {
		return _accounts.size();
	}

	public int getMaxAllowedOnlinePlayers() {
		return _maxAllowedOnlinePlayers;
	}

	public void setMaxAllowedOnlinePlayers(int maxAllowedOnlinePlayers) {
		_maxAllowedOnlinePlayers = maxAllowedOnlinePlayers;
	}

	private void kickClient(final LineageClient _client) {
		if (_client == null) {
			return;
		}
		GeneralThreadPool.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				if (_client.getActiveChar() != null) {
					_client.getActiveChar()
							.sendPackets(new S_ServerMessage(357));
				}

				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}
				_client.kick();
			}
		});
	}

	public synchronized void login(LineageClient _client, Account account)
			throws GameServerFullException, AccountAlreadyLoginException {
		if (!account.isValid()) {
			// 认证、认证失败指定。
			// 、检出为存在。
			throw new IllegalArgumentException("认证失败！");
		}
		if ((getMaxAllowedOnlinePlayers() <= getOnlinePlayerCount())
				&& !account.isGameMaster()) {
			throw new GameServerFullException();
		}
		if (_accounts.containsKey(account.getName())) {
			kickClient(_accounts.get(account.getName()));
			_client.sendPacket(new S_LoginResult(S_LoginResult.REASON_ACCOUNT_IN_USE));
			throw new AccountAlreadyLoginException();
		}
		if (_accounts.containsValue(_client)) {
			kickClient(_client);
			_client.sendPacket(new S_LoginResult(S_LoginResult.REASON_ACCOUNT_IN_USE));
			throw new AccountAlreadyLoginException();
		}

		_accounts.put(account.getName(), _client);
	}

	public synchronized void logout(LineageClient client) {
		if (client.getAccountName()==null) {
			return;
		}
		if (_accounts.containsKey(client.getAccountName())) {
			 _accounts.remove(client.getAccountName());
		}
		client.setAccount(null);
	}
	
	public int getIPcount(String ip) {
		int i = 0;
		for (LineageClient client : _accounts.values()) {
			if (client.getIp().equals(ip)) {
				i++;
			}
		}
		return i;
	}
}
