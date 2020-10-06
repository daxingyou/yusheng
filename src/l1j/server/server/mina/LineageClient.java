package l1j.server.server.mina;

/*import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;*/
import java.lang.reflect.Constructor;
/*import java.math.BigInteger;
import java.net.Socket;
import java.util.Collection;
import java.util.Date;*/
import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import l1j.gui.J_Main;
import l1j.server.Config;
import l1j.server.encryptions.Cipher;
import l1j.server.server.Account;
import l1j.server.server.GeneralThreadPool;
/*import l1j.server.server.GeneralThreadPool;*/
import l1j.server.server.IdFactory;
import l1j.server.server.LoginController;
/*import l1j.server.server.Logins;*/
import l1j.server.server.Opcodes;
import l1j.server.server.PacketHandler;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.CharBuffTable;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.NpcTable;
/*import l1j.server.server.datatables.PetTable;*/
import l1j.server.server.model.Getback;
/*import l1j.server.server.model.L1Clan;*/
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.Instance.L1BabyInstance;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1FollowInstance;
import l1j.server.server.model.Instance.L1HierarchInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1QuestInstance;
import l1j.server.server.model.item.L1ItemId;
/*import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;*/
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_ItemName;
/*import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;*/
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.world.L1World;
import l1j.william.LastOnline;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
/*import java.util.Queue;*/
/*import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;*/

public class LineageClient {

	private static final Log _log = LogFactory.getLog(LineageClient.class);

	// ���� Ű��
	public static final String CLIENT_KEY = "CLIENT";

	// Ŭ�� ������ ����
	private IoSession _session;


	public boolean clientVCheck = false;
	public boolean clientLoginCheck = false;
	// ��ȣȭ��
	private final Cipher le;

	// �α����� ���� ���̵�
	private String ID;

	// �������� �ɸ���
	private L1PcInstance _activeChar;
	
	
	private PacketHandler packetHandler;

	private static Timer observerTimer = new Timer();

	private int loginStatus = 0;
	
	public int _xorByte = (byte) 0xf0;

	public long _authdata;

	private boolean charRestart = true;

	private int _loginfaieldcount = 0;

	private Account account;

	private String hostname;
	
	private HcPacket hPacket;
	
	private HcPacket movePacket;

	ClientThreadObserver observer = new ClientThreadObserver(Config.AUTOMATIC_KICK * 60 * 1000);

	/**
	 * LineageClient ������
	 * 
	 * @param session
	 * @param key
	 */
	public LineageClient(IoSession session, int key,final int language) {
		this._session = session;
		_language = language;
		le = new Cipher(key);
	}	
	
	public void Initialization()
	{

		if (Config.AUTOMATIC_KICK > 0) {
			observer.start();
		}
		packetHandler = new PacketHandler(this._session);
		hPacket = new HcPacket();
		GeneralThreadPool.getInstance().execute(hPacket);
		movePacket = new HcPacket();
		GeneralThreadPool.getInstance().execute(movePacket);
	}
	
	private Lock synLock = new ReentrantLock(true);
	
	/** ���� ���¸� ���´� */
	public void kick() {
		try {
			sendPacket(new S_Disconnect());
			_session.close(true);
		} catch (Exception e) {
		}
	}
	

	public void setAuthCheck(boolean authCheck) {
		this.authCheck = authCheck;
	}
	
	private boolean authCheck = false;

	public boolean isAuthCheck() {
		return authCheck;
	}
	
	private String authCode;	
	
	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	
	public void CharReStart(boolean flag) {
		charRestart = flag;
	}
	
	public boolean isCharReStart(){
		return charRestart;
	}

	/** �α��� ���°��� �����Ѵ� */
	public void setloginStatus(int i){ loginStatus = i; }

	
	public int getloginStatus() {
		return loginStatus;
	}


	/**
	 * �ش� ��Ŷ�� ���� �Ѵ�.
	 * 
	 * @param bp
	 */
	public void sendPacket(ServerBasePacket bp) {
		if (bp == null||_session.isClosing()) {
			return;
		}
		try {
			synLock.lock();
			_session.write(bp);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			synLock.unlock();
		}	
/*		synchronized (this) {
			_session.write(bp);
		}	*/
//		_session.write(bp);
	}
	private long _lastSavedTime = System.currentTimeMillis();

	private long _lastSavedTime_inventory = System.currentTimeMillis();

	private void doAutoSave() throws Exception {
		if (_activeChar == null) {
			return;
		}
		try {
			// 情报
			if (Config.AUTOSAVE_INTERVAL * 1000
					< System.currentTimeMillis() - _lastSavedTime) {
				_activeChar.save();
				_lastSavedTime = System.currentTimeMillis();
			}

			// 所持情报
			if (Config.AUTOSAVE_INTERVAL_INVENTORY * 1000
					< System.currentTimeMillis() - _lastSavedTime_inventory) {
				_activeChar.saveInventory();
				_lastSavedTime_inventory = System.currentTimeMillis();
			}
		} catch (Exception e) {
			_log.info("Client autosave failure.");
			_log.error(e.getLocalizedMessage(), e);
			throw e;
		}
	}
	/**
	 * ����� ȣ��
	 */
	public void clientclose(){
			try {
				if (_activeChar != null) {
					if (_activeChar.isAttack()) {
						if (!_activeChar.getMap().isSafetyZone(_activeChar.getX(), _activeChar.getY())) {
							Thread.sleep(12000);
						}			
					}
					quitGame(_activeChar);
					synchronized (_activeChar) {
						_activeChar.logout();
						setActiveChar(null);
					}
				}
			} catch (Exception e) {
			}finally{
				try {
					LoginController.getInstance().logout(this);
					stopObsever();
				} catch (Exception e) {
				}
			}
	}

	/**
	 * ���� Ŭ���̾�Ʈ�� ����� PC ��ü�� �����Ѵ�.
	 * 
	 * @param pc
	 */
	public void setActiveChar(L1PcInstance pc) {
		_activeChar = pc;
		if (pc != null) {
			_pcobjid = pc.getId();
		}
	}

	
		
	/**
	 * ���� Ŭ���̾�Ʈ ����ϰ� �ִ� PC ��ü�� ��ȯ�Ѵ�.
	 * 
	 * @return activeCharInstance;
	 */
	public L1PcInstance getActiveChar() {
		return _activeChar;
	}

	/**
	 * ���� ����ϴ� ������ �����Ѵ�.
	 * 
	 * @param account
	 */
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * ���� ������� ������ ��ȯ�Ѵ�.
	 * 
	 * @return account
	 */
	public Account getAccount() {
		return account;
	}

	/**
	 * ���� ������� �������� ��ȯ�Ѵ�.
	 * 
	 * @return account.getName();
	 */
	public String getAccountName() {
		if (account == null) {
			return null;
		}
		String name = account.getName();

		return name;
	}

	/**
	 * �ش� LineageClient�� �����Ҷ� ȣ��
	 * 
	 * @param pc
	 */
	public static void quitGame(L1PcInstance pc) {
		// 更新最后在线时间 
		if (Config.EXP_DOUBLE) {
			int count = LastOnline.getInstance().countCharacterConfig(pc.getId());
			if (count == 0) {
				LastOnline.getInstance().storeCharacterConfig(pc);
			} else {
				LastOnline.getInstance().updateCharacterConfig(pc);
			}
		} else {
			int count = LastOnline.getInstance().countCharacterConfig(pc.getId());
			if (count != 0) {
				LastOnline.getInstance().deleteCharacterConfig(pc.getId());
			}
		}
		// 更新最后在线时间  end

		// 死亡街戾、空腹状态
		if (pc.isDead()) {
			int[] loc = Getback.GetBack_Location(pc, true);
			pc.setX(loc[0]);
			pc.setY(loc[1]);
			pc.setMap((short) loc[2]);
			pc.setCurrentHp(pc.getLevel());
			pc.set_food(5);
		}

		// 中止
		if (pc.getTradeID() != 0) { // 中
			final L1Trade trade = new L1Trade();
			trade.tradeCancel(pc);
		}

		// 拔
		if (pc.isInParty()) { // 中
			pc.getParty().leaveMember(pc);
		}

		// 上消
		Object[] petList = pc.getPetList().values().toArray();
		for (Object petObject : petList) {
			if (petObject instanceof L1PetInstance) { // 
				L1PetInstance pet = (L1PetInstance) petObject;
				pet.dropItem(pc);
				/*pet.collect();*/
				pet.updatePet();
				pc.getPetList().remove(pet.getId());
				pet.setDead(true);
				pet.deleteMe();
			}
			//重登删除祭司 
			if (petObject instanceof L1HierarchInstance) { // 
				L1HierarchInstance hierarch = (L1HierarchInstance) petObject;
				pc.getPetList().remove(hierarch.getId());
				hierarch.Death(null);
			}
			//重登删除祭司  end
			
			//重登删除魔法娃娃 
			if (petObject instanceof L1BabyInstance) { // 
				L1BabyInstance baby = (L1BabyInstance) petObject;
				pc.getPetList().remove(baby.getId());
				baby.deleteMe();
			}
			//重登删除魔法娃娃  end
			
			//重登删除跟随 
			if (petObject instanceof L1FollowInstance) { // 
				L1FollowInstance follow = (L1FollowInstance) petObject;
				pc.getPetList().remove(follow.getId());
				follow.setParalyzed(true);
				follow.deleteMe();
				
				//原地召唤调查员 
				if (follow.getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_10) {
					L1Npc spawnmonster = NpcTable.getInstance().getTemplate(l1j.william.New_Id.Npc_AJ_2_10);
					if (spawnmonster != null) {
						L1NpcInstance mob = null;
						try {
							String implementationName = spawnmonster.getImpl();
							Constructor<?> _constructor = Class.forName((new StringBuilder()).append("l1j.server.server.model.Instance.").append(implementationName).append("Instance").toString()).getConstructors()[0];
							mob = (L1NpcInstance) _constructor.newInstance(new Object[] { spawnmonster });
							mob.setId(IdFactory.getInstance().nextId());
							mob.setX(follow.getX());
							mob.setY(follow.getY());
							mob.setHomeX(follow.getX());
							mob.setHomeY(follow.getY());
							mob.setMap(follow.getMapId());
							mob.setHeading(follow.getHeading());
							L1World.getInstance().storeWorldObject(mob);
							L1World.getInstance().addVisibleObject(mob);
							L1Object object = L1World.getInstance().findObject(mob.getId());
							L1QuestInstance newnpc = (L1QuestInstance) object;
							newnpc.onNpcAI();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				//原地召唤调查员  end
				
				//原地召唤安迪亚 
				if (follow.getNpcTemplate().get_npcId() == l1j.william.New_Id.Npc_AJ_2_11) {
					L1Npc spawnmonster = NpcTable.getInstance().getTemplate(l1j.william.New_Id.Npc_AJ_2_11);
					if (spawnmonster != null) {
						L1NpcInstance mob = null;
						try {
							String implementationName = spawnmonster.getImpl();
							Constructor<?> _constructor = Class.forName((new StringBuilder()).append("l1j.server.server.model.Instance.").append(implementationName).append("Instance").toString()).getConstructors()[0];
							mob = (L1NpcInstance) _constructor.newInstance(new Object[] { spawnmonster });
							mob.setId(IdFactory.getInstance().nextId());
							mob.setX(follow.getX());
							mob.setY(follow.getY());
							mob.setHomeX(follow.getX());
							mob.setHomeY(follow.getY());
							mob.setMap(follow.getMapId());
							mob.setHeading(follow.getHeading());
							L1World.getInstance().storeWorldObject(mob);
							L1World.getInstance().addVisibleObject(mob);
							L1Object object = L1World.getInstance().findObject(mob.getId());
							L1QuestInstance newnpc = (L1QuestInstance) object;
							newnpc.onNpcAI();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				//原地召唤安迪亚  end
			}
			//重登删除跟随  end
		}

		// 上消
		Object[] dollList = pc.getDollList().values().toArray();
		for (Object dollObject : dollList) {
			L1DollInstance doll = (L1DollInstance) dollObject;
			doll.deleteDoll();
		}

		// DBcharacter_buff保存
		CharBuffTable.DeleteBuff(pc);
		CharBuffTable.SaveBuff(pc);
		pc.clearSkillEffectTimer();

		// 重登时灯类为关闭状态 
		List<L1ItemInstance> itemlist = pc.getInventory().getItems();
		for (L1ItemInstance item : itemlist) {
			if ((item.getItem().getItemId() == 40001 || 
				item.getItem().getItemId() == 40002 || 
				item.getItem().getItemId() == 40004 || 
				item.getItem().getItemId() == 40005) && 
				item.getEnchantLevel() != 0) {
				item.setEnchantLevel(0);
				pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
				pc.sendPackets(new S_ItemName(item));
			}
		}
		// 重登时灯类为关闭状态  end

		// pcstop。
		pc.stopEtcMonitor();
		// 状态OFF、DB情报书迂
		pc.setOnlineStatus(0);
		CharacterTable.updateOnlineStatus(pc);
		try {
			L1ItemInstance[] itemInstance = pc.getInventory().findItemsId(L1ItemId.ADENA);
			if (itemInstance.length > 0) {
				for (int i = 0; i < itemInstance.length; i++) {
					L1ItemInstance l1ItemInstance = itemInstance[i];
					if (l1ItemInstance!=null) {
						WriteLogTxt.Recording("金币记录","玩家:"+pc.getName()+"退出游戏时身上有金币("+l1ItemInstance.getCount()+")"+"#"+i);
					}
				}
			}
			pc.save();
			pc.saveInventory();
			WriteLogTxt.Recording("经验记录","玩家:"+pc.getName()+"退出游戏时经验值为("+pc.getExp()+")");
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}finally{
			J_Main.getInstance().delPlayerTable(pc.getName());//GUI
			_log.info("玩家：" + pc.getName()+" 退出游戏");
		}
	}
	/**
	 * ���� ����� ȣ��Ʈ���� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	public String getHostname() {
		return _ip;
	}

	/**
	 * ���� �α��� ������ ī��Ʈ ���� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	public int getLoginFailedCount() {
		return _loginfaieldcount;
	}

	/**
	 * ���� �α��� ������ ī��Ʈ ���� �����Ѵ�.
	 * 
	 * @param i
	 */
	public void setLoginFailedCount(int i) {
		_loginfaieldcount = i;
	}
	
	public Cipher getCipher(){
		return le;
	}

	/**
	 * ��Ŷ�� ��ȣȭ�Ѵ�.
	 * 
	 * @param data
	 * @return
	 */
/*	public byte[] encryptE(byte[] data) {
		try {
			return le.encrypt(data);
		} catch (Exception e) {
			// Logger.getInstance().error(getClass().toString()+"
			// encryptE(byte...
			// data)\r\n"+e.toString(), Config.LOG.error);
		}
		return null;
	}*/

	/**
	 * ��Ŷ ����� ��ȯ�Ѵ�.
	 * 
	 * @param data
	 * @return
	 */
/*	private int PacketSize(byte[] data) {
		int length = data[0] & 0xff;
		length |= data[1] << 8 & 0xff00;
		return length;
	}*/

	/**
	 * ID�� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	public String getID() {
		return ID;
	}

	/**
	 * ID�� �����Ѵ�.
	 * 
	 * @param id
	 */
	public void setID(String id) {
		ID = id;
	}

	/**
	 * LineageClient�� ���� ���θ� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	public boolean isConnected() {
		return _session.isConnected();
	}

	/**
	 * ���� �������� LineageClient�� IP�� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	private String _ip = "未知";
	
	public void setIp(final String ip){
		_ip = ip;
	}
	
	public String getIp() {	
		return _ip;
	}

	/**
	 * ���� �������� Ŭ���̾�Ʈ ���ø� �ߴ��Ѵ�.
	 */
	public void stopObsever() {
		observer.cancel();
	}

	/**
	 * ���� ���� ������¸� ��ȯ�Ѵ�.
	 * 
	 * @return
	 */
	public boolean isClosed() {
		if (_session.isClosing()) {
			return true;
		}
		return false;
	}
/*	private int packetCount;
	private int packetSaveTime = -1;
	private int packetCurrentTime = -1;
	private int packetInjusticeCount;
	private int packetJusticeCount;*/
//	private int time =0;
//	private int length = 0;
	
	private boolean _isfirst = true;
	
	/**
	 * ��Ŷ �����Ͽ� ó��.
	 * 
	 * @param data
	 * @throws Exception
	 */
	public void PacketHandler(byte[] data) throws Exception {
		int opcode = data[0] & 0xFF;
/*		Date now = new Date();
		int leng = data.length;
		length += leng;		
		if(time == 0){
			time = now.getSeconds();
		}	*/	
/*		if(now.getSeconds()  != time){
			length = 0;
			time = 0;
		}		*/
/*		if(length > 2048){
			close();
			System.out.println("该IP块封包太大: "+getIp());
			return;
		}*/
		if (_isfirst) {
			if (opcode == Opcodes.C_OPCODE_CLIENTVERSION) {
				_isfirst = false;
			}else {
				_session.close(true);
				return;
			}				
		}

		// ########## A121 ĳ���� �ߺ� �α��� ���� ���� [By �����] ##########
		if (opcode != Opcodes.C_OPCODE_KEEPALIVE) {
			// C_OPCODE_KEEPALIVE �̿��� ������ ��Ŷ�� ������(��) Observer�� ����
			observer.packetReceived();
		}

		// null�� ���� ĳ���� �������̹Ƿ� Opcode�� ��� ������ ���� �ʰ� ��� ����
		if (_activeChar == null) {
			packetHandler.handlePacket(data);
		}else {
			if (opcode == Opcodes.C_OPCODE_MOVECHAR
					|| opcode == Opcodes.C_OPCODE_ATTACK
					|| opcode == Opcodes.C_OPCODE_USESKILL
					|| opcode == Opcodes.C_OPCODE_ARROWATTACK) {
				movePacket.requestWork(data);
//				packetHandler.handlePacket(data, _activeChar);
			}else {
				hPacket.requestWork(data);
			}
			
/*			if (opcode == Opcodes.C_OPCODE_MOVECHAR){
				movePacket.requestWork(data);
			}else {
				hPacket.requestWork(data);
			}	*/	
			//packetHandler.handlePacket(data, _activeChar);
		}

		// ����, PacketHandler�� ó�� ��Ȳ�� ClientThread�� ������ ���� �ʰ� �ϱ� ������(����)�� ó��
		// ������ Opcode�� ��� ���ð� ClientThread�� PacketHandler�� �и�
		// �ı��ؼ� �� �Ǵ� Opecode�� restart, ������ ���, ������ ����
/*		if (opcode == Opcodes.C_OPCODE_MOVECHAR
				|| opcode == Opcodes.C_OPCODE_ATTACK
				|| opcode == Opcodes.C_OPCODE_USESKILL
				|| opcode == Opcodes.C_OPCODE_ARROWATTACK) {		
			_movePacket.requestWork(data);
		} else {
			// ��Ŷ ó�� thread�� �ְ� �޾�
			_hcPacket.requestWork(data);

		}*/
		doAutoSave();

	}
	
	// 行动处理
	class HcPacket implements Runnable {
		private final Queue<byte[]> _queue;

		private PacketHandler _handler;

		public HcPacket() {
			_queue = new ConcurrentLinkedQueue<byte[]>();
			_handler = new PacketHandler(_session);
		}

		public HcPacket(int capacity) {
			_queue = new LinkedBlockingQueue<byte[]>(capacity);
			_handler = new PacketHandler(_session);
		}

		public void requestWork(byte data[]) {
			_queue.offer(data);
			//System.out.println("接收封包1个");
		}

		@Override
		public void run() {
			while (_session != null&&!_session.isClosing()) {
				byte[] data = _queue.poll();
				if (data != null) {
					try {
						_handler.handlePacket(data, _activeChar);				
						//System.out.println("发送封包1个");
					} catch (Exception e) {}
				} else {
					try {
						Thread.sleep(10);
						//System.out.println("休息");
					} catch (Exception e) {}
				}
			}
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

	/**
	 * 
	 * @author Developer
	 * 
	 */
	class ClientThreadObserver extends TimerTask {
		private int _checkct = 1;

		private final int _disconnectTimeMillis;

		public ClientThreadObserver(int disconnectTimeMillis) {
			_disconnectTimeMillis = disconnectTimeMillis;
		}

		public void start() {
			observerTimer.scheduleAtFixedRate(ClientThreadObserver.this, 0,
					_disconnectTimeMillis);
		}

		@Override
		public void run() {
			try {
				if (_session.isClosing()) {
					cancel();
					return;
				}

				if (_checkct > 0) {
					_checkct = 0;
					return;
				}

				if (_activeChar == null // ĳ���� ������
						|| _activeChar != null
						&& !_activeChar.isPrivateShop()) { // ����
					// ������
					kick();
					_log.info("太长时间没动作(" + hostname
							+ ")切断路线.");
					cancel();
					return;
				}
			} catch (Exception e) {
				_log.error(e.getLocalizedMessage(), e);
				cancel();
			}
		}

		public void packetReceived() {
			_checkct++;
		}
	}

	// �ɸ����� ��Ŷ ó�� thread
/*	class ClinetPacket implements Runnable {
		public ClinetPacket() {

		}

		public void run() {
			while (_session!=null&&!_session.isClosing()) {
				try {
					// ���ڴ�
					synchronized (PacketD) {
						int length = PacketSize(PacketD);
						if (length != 0 && length <= PacketIdx) {
							if (length>1024) {
								_session.close(true);
								break;
							}
							byte[] temp = new byte[length];
							System.arraycopy(PacketD, 0, temp, 0, length);
							System.arraycopy(PacketD, length, PacketD, 0,
									PacketIdx - length);
							PacketIdx -= length;
							encryptD(temp);
						}else {
							Thread.sleep(20);
							continue;
						}
					}	
				} catch (Exception e) {
					// Logger.getInstance().error(getClass().toString()+"
					// run()\r\n"+e.toString(), Config.LOG.error);
				}
			}
		}
	}*/
	
/*	private boolean _isover =  false;

	public void setIsover(boolean flg){
		_isover = flg;
	}*/
	
	public IoSession getsSession(){
		return _session;
	}
	
	private int _pcobjid = 0;

	public int getPcobjid(){
		return _pcobjid;
	}
	
	private boolean _isnewlogin = true;
	
	public void setNewLogin(boolean flg){
		_isnewlogin = flg;
	}
	
	public boolean isNewLogin(){
		return _isnewlogin;
	}
	private int _error = 0;

	public void adderror() {
		_error++;
/*		if (_error > 3) {
			kick();
		}*/
	}
	public boolean isAI()
	{
		if (_error > 3) {
			return true;
		}
		return false;
	}

	public void seterror() {
		_error = 0;
	}
	
	private int _clienterror = 0;
	
	public void addclienter()
	{
		_clienterror++;
		if (_clienterror >=3) {
			kick();
		}
	}
	
	public int getclient()
	{
		return _clienterror;
	}
	
	private int _language = 5;
	
	public int getLanguage(){
		return _language;
	}

}
