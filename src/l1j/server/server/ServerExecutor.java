package l1j.server.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.firewall.ConnectionThrottleFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import l1j.server.Config;
import l1j.server.server.mina.LineageCodecFactory;
import l1j.server.server.mina.LineageProtocolHandler;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.StreamUtil;
/**
 * 端口监听
 * @author dexc
 *
 */
public class ServerExecutor {
	private int _port = 0;
	private int _language = 5;
	/**
	 * 端口监听
	 */
	public ServerExecutor(final int port,final int language) {
		try {
			_port = port;
			_language = language;
			
/*			if (!"*".equals(Config.GAME_SERVER_HOST_NAME)) {
				final InetAddress inetaddress = InetAddress.getByName(Config.GAME_SERVER_HOST_NAME);
				_server = new ServerSocket(_port, 50, inetaddress);
				_server.setReceiveBufferSize(36*1024);
			} else {
				_server = new ServerSocket(_port);
				_server.setReceiveBufferSize(36*1024);
			}*/
			
			startLoginServer() ;

		} /*catch (final SocketTimeoutException e) {
			_log.log(Level.SEVERE, "连线超时:(" + _port + ")", e);

		} catch (final IOException e) {
			_log.log(Level.SEVERE, "IP位置加载错误 或 端口位置已被占用:(" + _port + ")", e);
			
		} */catch (Exception e) {
			// TODO: handle exception
		}	finally {
			System.out.println("[D] " + getClass().getSimpleName() + " 开始监听服务端口:(" + _port + ")");
//			_log.info("[D] " + getClass().getSimpleName() + " 开始监听服务端口:(" + _port + ")");
		}
	}
	
	private void startLoginServer() {
		try {
			PerformanceTimer timer = new PerformanceTimer();
			System.out.print("LoginController Loading...");
			LoginController.getInstance().setMaxAllowedOnlinePlayers(
					Config.MAX_ONLINE_USERS);
			System.out.println("OK! " + timer.get() + " ms");
			NioSocketAcceptor acceptor = new NioSocketAcceptor();
			DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
			chain.addLast("codec", new ProtocolCodecFilter(
					new LineageCodecFactory()));
//			chain.addLast("cc", new ConnectionThrottleFilter(2000));
			System.out.print("SocketAcceptor Binding...");
			// Bind
			acceptor.setReuseAddress(true);
			acceptor.getSessionConfig().setTcpNoDelay(true);		
			acceptor.setHandler(new LineageProtocolHandler(_language));
			chain.addLast("ThreadPool",new ExecutorFilter());
			acceptor.bind(new InetSocketAddress(_port));
			System.out.println("OK! " + timer.get() + " ms");
			System.out.println("LoginServer Loading Compleate!");
			System.out.println("==================================================");
			System.out.println("==监听端口 [ "+ Config.GAME_SERVER_PORT +" ] 开启=");
			System.out.println("==================================================");
			timer = null;
			int num = Thread.activeCount();
			System.out.println("[存在线程量] : [ *"+ num +"* ]");
			System.out.println(" ::::::: "+ Config.SERVERNAME +" 帅哥二次修改开发 ::::::: ");

		} catch (Exception e) { /* e.printStackTrace(); */
		}
		;
		// FIXME StrackTrace�ϸ� error
	}

	/**
	 * 启动监听
	 * @throws IOException
	 */
	public void stsrtEcho() throws IOException {
		startLoginServer();
//		GeneralThreadPool.getInstance().execute(this);
	}

/*	@Override
	public void run() {
		try {
			while (_server != null) {
				Socket socket = null;
				try {
					socket = _server.accept();
					
					if (socket != null) {
						// 性能偏好
						//connectionTime - 表达短连接时间的相对重要性的 int
						//latency - 表达低延迟的相对重要性的 int
						//bandwidth - 表达高带宽的相对重要性的 int
						//socket.setPerformancePreferences(1, 0, 0);
						//socket.setSoLinger(true, 0);
						//socket.setTrafficClass(0x04); // 測試效果
						socket.setTcpNoDelay(true);// 不做緩衝直接發送
						socket.setSoTimeout(900000);// 端口15分钟清理废弃连接
						
						final String ipaddr = socket.getInetAddress().getHostAddress();
						// log4j
						final String info = 
							_t1
							+ "\n       客户端 连线游戏伺服器 服务端口:(" + _port + ")"
							+ "\n       " + ipaddr
							+ _t2;
						
						//_log.info(info);
						System.out.println(info);

						// 计时器
						final ClientThread client = new ClientThread(socket);
						GeneralThreadPool.getInstance().execute(client);
					}
					
				} catch (final SecurityException e) {
					//_log.warn(e.getLocalizedMessage());
				}
			}
		
		} catch (IOException e) {
			//_log.error(e.getLocalizedMessage(), e);

		} finally {
			final String lanInfo = 
				"[D] " + getClass().getSimpleName() + " 服务器核心关闭监听端口(" + _port + ")";
			
			//_log.info(lanInfo);
			System.out.println(lanInfo);
		}
	}*/

	/**
	 * 停止监听
	 */
	public void stopEcho() {
/*		try {
			if (_server != null) {
				StreamUtil.close(_server);
				StreamUtil.interrupt(this);
				_server = null;
			}
			
		} catch (Exception e) {
			//_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			
		} finally {

		}*/
	}
}