package l1j.server.server.mina;

import java.math.BigInteger;
import java.util.StringTokenizer;
//import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.LoginController;
import l1j.server.server.ServerRestartTimer;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.serverpackets.S_Key;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.utils.SystemUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class LineageProtocolHandler extends IoHandlerAdapter {
	
	private static final Log _log = LogFactory.getLog(LineageProtocolHandler.class);
	private int _language = 5;
	private static final String[] LanguageMsg = {"未知1","未知2","未知3","台湾","未知4","大陆"};
	

	public LineageProtocolHandler(final int language){
		super();
		_language = language;
	}
	@Override
	public void sessionCreated(IoSession session) {
		try {
			
			CheckGamePort(session);
			StringTokenizer st = new StringTokenizer(session.getRemoteAddress().toString().substring(1), ":");
			String ip = st.nextToken();	
			if (ServerRestartTimer.isRtartTime()) {
				session.close(true);
				_log.info("因为重启拒绝IP:"+ip);
			}
			if(IpTable.getInstance().isBannedIp(ip)){
				_log.info("拒绝IP:"+ip);
				session.close(true);
			}			
			_log.info("IP: ("+ip+") 开始连线 端口语言:" + LanguageMsg[_language]);
			if(st.nextToken().startsWith("0")){
				session.close(true);
			}
			session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 600); 
		} catch (Exception e) {
			session.close(true);
		}
	}

	/***************************************************************************
	 * 
	 */
	@Override
	public void sessionOpened(IoSession session) {

//		System.out.println("准备开始发送--");
		if (!session.isClosing()) {
			try {
				int xorByte = (byte) 0xf0;
				long authdata = 0;
				
				ServerBasePacket keypack = new S_Key();
				if (Config.LOGINS_TO_AUTOENTICATION) {
					//xorByte = (int)(Math.random()*253+1);
					int randomNumber = (int) (Math.random() * 900000000) + 255;
					xorByte = randomNumber % 255 + 1;
					//登陆加密修改 - by461638888
			      // authdata = new BigInteger(Integer.toString(xorByte)).modPow(new BigInteger(Config.RSA_KEY_E), new BigInteger(Config.RSA_KEY_D)).longValue();
					authdata = new BigInteger(Integer.toString(randomNumber)).modPow(new BigInteger(Config.RSA_KEY_E), new BigInteger(Config.RSA_KEY_D)).longValue();
					byte[] SFLPack = new byte[4];
					SFLPack[0] = ((byte) (int) (authdata & 0xFF));
					SFLPack[1] = ((byte) (int) (authdata >> 8 & 0xFF));
					SFLPack[2] = ((byte) (int) (authdata >> 16 & 0xFF));
					SFLPack[3] = ((byte) (int) (authdata >> 24 & 0xFF));
					IoBuffer buffer = IoBuffer.allocate(4, false);
					buffer.put(SFLPack);
					buffer.flip();
					session.write(buffer);
				}
				StringTokenizer st = new StringTokenizer(session.getRemoteAddress().toString().substring(1), ":");
				String ip = st.nextToken();
//				System.out.println("步骤4");
				// add for 2.70C start
				// long seed = 0x5cc690ecL;
				// _keys.initKeys(0x5cc690ecL);
				LineageClient lc = new LineageClient(session, 0x3b43dd0b,_language);
				lc._xorByte = xorByte;
				lc._authdata = authdata;

				lc.setIp(ip);
//				System.out.println("步骤5");
				session.setAttribute(LineageClient.CLIENT_KEY, lc);
//				System.out.println("步骤6");
				lc.Initialization();
/*				System.out.println("步骤7");
				System.out.println("初始化完毕");
				
				System.out.println("通道分配完毕");*/
/*				int rowIndex = DecoderManager.getInstance().getRowIndex();
				lc.setthreadIndex(rowIndex);
				DecoderManager.getInstance().putClient(lc, lc.getthreadIndex());*/
				session.write(keypack);			
//				System.out.println("执行客户端初始化完毕");
			} catch (Exception e) {
				session.close(true);
			}		
		}
/*		try {
			if (!Config.shutdown && !session.isClosing()) {
				GeneralThreadPool.getInstance().schedule(new sessionOpen(session), 1);
//				System.out.println("发送第一个封包");

			} else {
				session.close(true);
			}
		} catch (Exception e) {
			// Logger.getInstance().error(getClass().toString()+"
			// sessionOpened(IoSession session)\r\n"+e.toString(),
			// Config.LOG.error);
		}*/
		// 取得第一個封包
					
	}

//	private static final long seed = 0x00000000L;
/*	class sessionOpen implements Runnable{
		private IoSession session;
		public sessionOpen(IoSession s){
			session = s;
		}
		@Override
		public void run() {
			try {
				System.out.println("步骤1");
				ServerBasePacket packet = new S_Key();
				System.out.println("步骤2");
				byte[] FirstPacket = packet.getContent();
				System.out.println("步骤3");
				// 取得加解解密種子
				int seed = 0;
				seed |= FirstPacket[1] << 0 & 0xff;
				seed |= FirstPacket[2] << 8 & 0xff00;
				seed |= FirstPacket[3] << 16 & 0xff0000;
				seed |= FirstPacket[4] << 24 & 0xff000000;
				System.out.println("步骤4");
				// add for 2.70C start
				// long seed = 0x5cc690ecL;
				// _keys.initKeys(0x5cc690ecL);
				LineageClient lc = new LineageClient(session, seed);
				System.out.println("步骤5");
				session.setAttribute(LineageClient.CLIENT_KEY, lc);
				System.out.println("步骤6");
				lc.Initialization();
				System.out.println("步骤7");
				System.out.println("初始化完毕");
				
				System.out.println("通道分配完毕");
				int rowIndex = DecoderManager.getInstance().getRowIndex();
				lc.setthreadIndex(rowIndex);
				DecoderManager.getInstance().putClient(lc, lc.getthreadIndex());
				session.write(packet);			
				System.out.println("执行客户端初始化完毕");
				CheckGamePort(session);
			} catch (Exception e) {
				// TODO: handle exception
			}		
			
		}
		
	}*/

	// ���� ��û�� �޽��� ��ü�� ����Ʈ ���۷� ��ȯ�Ǿ� Ŭ���̾�Ʈ�� ���۵Ǿ���
	@Override
	public void messageSent(IoSession session, Object message) {
//		if (Config.PACKET) {
/*			ServerBasePacket bp = (ServerBasePacket) message;
			byte[] data = bp.getBytes();
			System.out.println("[server]\r\n" + printData(data, data.length));*/
		//}
		if (message instanceof ServerBasePacket) {
/*			ServerBasePacket bp = (ServerBasePacket) message;
			byte[] data = bp.getBytes();
			System.out.println("[server]\r\n" + printData(data, data.length));*/
			((ServerBasePacket) message).close();
		}
		
	}

	// Ŭ���̾�Ʈ�� �����͸� �����Ͽ� �� ������ �޽��� ��ü�� ��ȯ �Ǿ���
	@Override
	public void messageReceived(IoSession session, Object message) {
/*		LineageClient client = (LineageClient) session
				.getAttribute(LineageClient.CLIENT_KEY);
		if (client != null) {
			if (message instanceof byte[]) {
				int length = PacketSize(client.PacketD);
				if (length != 0 && length <= client.PacketIdx) {
					byte[] temp = new byte[length];
					System.arraycopy(client.PacketD, 0, temp, 0,
							length);
					System.arraycopy(client.PacketD, length,
							client.PacketD, 0, client.PacketIdx
									- length);
					client.PacketIdx -= length;
					client.encryptD(temp);
				}
			}
		}	*/
		if (message instanceof byte[]) {
			LineageClient client = (LineageClient) session
					.getAttribute(LineageClient.CLIENT_KEY);
			try {
				client.PacketHandler((byte[]) message);
//				System.out.println("[client]\r\n" + printData((byte[]) message, ((byte[]) message).length));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
/*	private int PacketSize(byte[] data) {
		int length = data[0] & 0xff;
		length |= data[1] << 8 & 0xff00;
		return length;
	}*/

	// Ŭ���̾�Ʈ�� ���� ������ ������ �����Ǿ���
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		LineageClient lc = (LineageClient) session
				.getAttribute(LineageClient.CLIENT_KEY);
		if (lc != null) {
			lc.clientclose();
			lc = null;
		}
/*		SystemUtil.printMemoryUsage(_log);*/
		_log.info("记忆体使用: " + SystemUtil.getUsedMemoryMB() + "MB");
		_log.info("当前在线玩家"+LoginController.getInstance().getOnlinePlayerCount()+"位，等待玩家连线中...");
	}

	// Ŭ���̾�Ʈ�� ������ �� �����̳� I/O�� ���� ���� ������
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		StringTokenizer st = new StringTokenizer(session.getRemoteAddress().toString().substring(1), ":");
		String ip = st.nextToken();
		session.close(true);
		_log.info("ip:"+ip+"闲置时间太长切断");
		// Logger.getInstance().error(getClass().toString()+" sessionIdle(
		// IoSession session, IdleStatus status )\r\n"+status.toString(),
		// Config.LOG.system);
	}

	// ��� ���� �Ǵ� �̺�Ʈ ó�� ���� ���ܰ� �߻�����
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		try {
			StringTokenizer st = new StringTokenizer(session.getRemoteAddress().toString().substring(1), ":");
			String ip = st.nextToken();
			session.close(true);
			_log.info("ip:"+ip+"异常掉线");
			_log.error(cause.toString());
			// Logger.getInstance().error(getClass().toString()+"
			// exceptionCaught(IoSession session, Throwable
			// cause)\r\n"+cause.toString(), Config.LOG.system);
		} catch (Exception e) {
		}
	}
	
	private void CheckGamePort(IoSession session) {
		 try {
			StringTokenizer st1 = new StringTokenizer(session.getRemoteAddress()
					.toString().substring(1), ":");
			String ip1 = st1.nextToken();
			IpTable iptable1 = IpTable.getInstance();
			
			StringTokenizer st2 = new StringTokenizer(session.getRemoteAddress()
					.toString().substring(1), ":");
			String ip2 = st2.nextToken();
			IpTable iptable2 = IpTable.getInstance();
			
			StringTokenizer st3 = new StringTokenizer(session.getRemoteAddress()
					.toString().substring(1), ":");
			String ip3 = st3.nextToken();
			IpTable iptable3 = IpTable.getInstance();
			
/*			StringTokenizer st4 = new StringTokenizer(session.getRemoteAddress()
					.toString().substring(1), ":");
			String ip4 = st4.nextToken();*/
			
			StringTokenizer st5 = new StringTokenizer(session.getRemoteAddress()
					.toString().substring(1), ":");
			String ip5 = st5.nextToken();
			IpTable iptable5 = IpTable.getInstance();
			
			// 0��Ʈ ���͸� (ddos����)
			if (st1.nextToken().startsWith("0")) {
				iptable1.banIp(ip1);
				System.out.println("端口不可以为0开头: "+ip1);
				session.close(true);
			}
			
			// null ����
			if (st2.nextToken().startsWith("null")) {
				iptable2.banIp(ip2);
				System.out.println("端口不能为null: "+ip2);
				session.close(true);
			}
			
			//��������
			if(st3.nextToken().isEmpty()){
				iptable3.banIp(ip3);
				System.out.println("端口不能为空: "+ip3);
				session.close(true);
			}
			
			if(st5.nextToken().length() <= 0){
				iptable5.banIp(ip5);
				System.out.println("端口不能太短: "+ip5);
				session.close(true);
			}
			
			//System.out.println("[���� �õ�] ������ : "+ip4+ " / ��Ʈ : "+st4.nextToken());
		 } catch (Exception e) {
			 
			}
	 }
	

	public String printData(byte[] data, int len) {
		StringBuffer result = new StringBuffer();
		int counter = 0;
		for (int i = 0; i < len; i++) {
			if (counter % 16 == 0) {
				result.append(fillHex(i, 4) + ": ");
			}
			result.append(fillHex(data[i] & 0xff, 2) + " ");
			counter++;
			if (counter == 16) {
				result.append("   ");
				int charpoint = i - 15;
				for (int a = 0; a < 16; a++) {
					int t1 = data[charpoint++];
					if (t1 > 0x1f && t1 < 0x80) {
						result.append((char) t1);
					} else {
						result.append('.');
					}
				}
				result.append("\n");
				counter = 0;
			}
		}

		int rest = data.length % 16;
		if (rest > 0) {
			for (int i = 0; i < 17 - rest; i++) {
				result.append("   ");
			}

			int charpoint = data.length - rest;
			for (int a = 0; a < rest; a++) {
				int t1 = data[charpoint++];
				if (t1 > 0x1f && t1 < 0x80) {
					result.append((char) t1);
				} else {
					result.append('.');
				}
			}

			result.append("\n");
		}
		return result.toString();
	}

	private String fillHex(int data, int digits) {
		String number = Integer.toHexString(data);

		for (int i = number.length(); i < digits; i++) {
			number = "0" + number;
		}
		return number;
	}

	class ip {
		public String ip;

		public int count;

		public long time;

		public boolean block;
	}
}
