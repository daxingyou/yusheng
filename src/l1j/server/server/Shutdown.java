package l1j.server.server;

import java.util.Collection;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.world.L1World;

/**
 * 关机管理
 * @author dexc
 *
 */
public class Shutdown extends Thread {

	private static final Log _log = LogFactory.getLog(Shutdown.class);
	
	private static Shutdown _instance;

	private static Shutdown _counterInstance = null;

	private int _secondsShut;

	private int _shutdownMode;

	private int _overTime = 5;

	public static final int SIGTERM = 0;// 即时关机

	public static final int GM_SHUTDOWN = 1;// 倒数关机

	public static final int GM_RESTART = 2;// 重新启动

	public static final int ABORT = 3;// 取消指令

	public static boolean SHUTDOWN = false;

	public Shutdown() {
		_secondsShut = -1;
		_shutdownMode = SIGTERM;
	}

	/**
	 * <font color=#00800>执行关机读秒</font>
	 * @param seconds 时间:单位(秒)
	 * @param mode 关机模式(true关机后重新启动 false关机后不重新启动)
	 */
	public Shutdown(int seconds, final boolean restart) {
		if (seconds < 0) {
			seconds = 0;
		}
		this._secondsShut = seconds;
		if (restart) {
			_shutdownMode = GM_RESTART;
		} else {
			_shutdownMode = GM_SHUTDOWN;
		}
	}

	public static Shutdown getInstance() {
		if (_instance == null) {
			_instance = new Shutdown();
		}
		return _instance;
	}

	@Override
	public void run() {
		if (this == _instance) {
			if (_shutdownMode != ABORT) {
				try {
					saveData();
					Thread.sleep(3000);
					while (_overTime > 0) {
						_log.info("距离核心完全关闭 : " + _overTime + "秒!");
						_overTime--;
						Thread.sleep(1000);
					}

					final int list = LoginController.getInstance().getClients().size();
					_log.info("核心关闭残余连线帐号数量: " + list);
					Thread.sleep(1000);
					if (list > 0) {
						for (final LineageClient clientThread : LoginController.getInstance().getClients()) {
							_log.info("核心关闭残留帐号: " + clientThread.getAccountName());
						}
					}
					
				} catch (InterruptedException e) {
					_log.info(e.getLocalizedMessage());
				}
			}
			//mysqldump -u wcnc -p smgp_apps_wcnc > wcnc.sql2
		} else {
			countdown();
			if (_shutdownMode != ABORT) {
				switch (_shutdownMode) {
				case GM_SHUTDOWN:
					_instance.setMode(GM_SHUTDOWN);
					System.exit(0);
					break;

				case GM_RESTART:
					_instance.setMode(GM_RESTART);
					System.exit(1);
					break;
				}
			}
		}
	}

	/**
	 * <font color=#00800>启动关机计时</font>
	 * @param pc 指令执行者
	 * @param seconds 时间:单位(秒)
	 * @param mode 关机模式(true关机后重新启动 false关机后不重新启动)
	 */
	public void startShutdown(final L1PcInstance activeChar, final int seconds, final boolean restart) {
		if (_counterInstance != null) {
			return;
		}
		if (activeChar != null) {
			_log.info(activeChar.getName() + " 启动关机计时: " + seconds + " 秒!");
		}
		if (_counterInstance != null) {
			_counterInstance._abort();
		}

		_counterInstance = new Shutdown(seconds, restart);
		GeneralThreadPool.getInstance().execute(_counterInstance);
	}

	/**
	 * <font color=#00800>取消关机计时</font>
	 * @param pc 指令执行者
	 */
	public void abort(final L1PcInstance activeChar) {
		if (_counterInstance != null) {
			_counterInstance._abort();
		}
	}

	/**
	 * <font color=#00800>设定关机模式为(取消)</font>
	 */
	private void setMode(final int mode) {
		_shutdownMode = mode;
	}

	/**
	 * set shutdown mode to ABORT
	 *
	 */
	private void _abort() {
		_shutdownMode = ABORT;
	}

	/**
	 * <font color=#00800>关机倒数 以及 倒数公告 如果关机模式变更为(取消) 停止运作</font>
	 */
	private void countdown() {
		try {
			SHUTDOWN = true;
			// 产生讯息封包 (72 \f3伺服器将会在 %0 秒后关机。请玩家先行离线。)
			L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(72, String.valueOf(_secondsShut)));
			while (_secondsShut > 0) {
				switch (_secondsShut) {
/*				case 300:
				case 240:*/
				case 180:
					// 停止端口监听
					EchoServerTimer.get().stopEcho();
					break;

				case 150:
				case 120:
				case 60:
				case 30:
				case 10:
				case 5:
				case 4:
				case 3:
				case 2:
				case 1:
					// 服务端讯息
					_log.info("关机倒数: " + _secondsShut + " 秒!");
					// 产生讯息封包 (72 \f3伺服器将会在 %0 秒后关机。请玩家先行离线。)
					L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(72, String.valueOf(_secondsShut)));
					break;
				}

				_secondsShut--;

				// 每秒计算一次
				Thread.sleep(1000);

				if (_shutdownMode == ABORT) {
					if (_secondsShut > 5) {
						SHUTDOWN = false;
						// 监听端口启动重置作业
						EchoServerTimer.get().reStart();
						// 产生讯息封包 (3063 取消关机倒数!!游戏将会正常执行!!)
						L1World.getInstance().broadcastPacketToAll(new S_ServerMessage(166, "取消关机倒数!!游戏将会正常执行!!"));
						return;
					}
				}
			}

		} catch (final Exception e) {
			_log.info(e.getLocalizedMessage());

		} finally {
			if (_shutdownMode != ABORT) {
				this.saveData();
				
			} else {
				_secondsShut = -1;
				_shutdownMode = SIGTERM;
				_counterInstance = null;
			}
		}
	}

//	private static boolean _isMsg = false;
	
	/**
	 * 线上人物资料的存档
	 *
	 */
	private synchronized void saveData() {
		try {
			L1World.getInstance().broadcastPacketToAll(new S_Disconnect());

			final Collection<LineageClient> list = LoginController.getInstance().getClients();
			_log.info("人物/物品 资料的存档 - 关闭核心前连线帐号数量: " + list.size());
			for (final LineageClient client : list) {
				client.kick();
			}
			
		} catch (final Exception e) {
			_log.info(e.getLocalizedMessage());
		} finally {
			// 全部设置离线
			//AccountReading.get().updateLan();
			//ServerReading.get().setStop();// 设置服务器为关机
		}
	}

	/*private class Save extends Thread {

		private ClientExecutor _client;

		public Save(final ClientExecutor client) {
			_client = client;
		}

		@Override
		public void run() {
			try {
				
				final L1PcInstance tgpc = _client.getActiveChar();
				if (tgpc != null) {
					QuitGame.quitGame(tgpc);
					_client.setActiveChar(null);
					_client.kick();
					_client.close();
				}
				
				final L1Account value = _client.getAccount();
				if (value != null) {
					if (value.is_isLoad()) {
						// 帐号连线状态 移出连线列表
						OnlineUser.get().remove(_client.getAccountName());
					}
					value.set_isLoad(false);
					AccountReading.get().updateLan(_client.getAccountName(), false);
				}
				
			} catch (final Exception e) {
				_log.error(e.getLocalizedMessage(), e);
			}
		}
	}*/
}