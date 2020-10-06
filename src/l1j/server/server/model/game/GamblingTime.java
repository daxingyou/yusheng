package l1j.server.server.model.game;

import java.lang.reflect.Constructor;
import java.util.Random;
//import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

import l1j.server.Config;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.DoorSpawnTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Gamble;
import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CloseList;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.storage.GamblingLock;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.world.L1World;

/**
 * 奇岩赌场 控制项时间轴
 * @author dexc
 *
 */
public class GamblingTime extends TimerTask {
	// 參賽者路徑資料
	private static final int _playerLocation[][][] =
	{
	{
	{ 33476, 32861 },
	{ 33473, 32858 },
	{ 33473, 32853 },
	{ 33479, 32847 },
	{ 33517, 32847 },
	{ 33525, 32847 } },
	{
	{ 33476, 32862 },
	{ 33472, 32858 },
	{ 33472, 32853 },
	{ 33479, 32846 },
	{ 33517, 32845 },
	{ 33525, 32845 } },
	{
	{ 33476, 32863 },
	{ 33471, 32858 },
	{ 33471, 32853 },
	{ 33479, 32845 },
	{ 33517, 32843 },
	{ 33525, 32843 } },
	{
	{ 33476, 32864 },
	{ 33470, 32858 },
	{ 33470, 32853 },
	{ 33479, 32844 },
	{ 33517, 32841 },
	{ 33525, 32841 } },
	{
	{ 33477, 32865 },
	{ 33469, 32858 },
	{ 33469, 32853 },
	{ 33479, 32843 },
	{ 33517, 32839 },
	{ 33525, 32839 } } };
	
	// 現在的參賽者
	private final L1NpcInstance[] _nowPlayer = new L1NpcInstance[5];
	
	private ScheduledFuture<?> _timeHandler;
	
	// 賽程截止控制用(已完成的參賽者數量)
	protected AtomicInteger _completePlayer = new AtomicInteger(0);
	
	private boolean _isOver = false;
	
	private Random _random = new Random();
	
	// 比赛已开始时间(1/2秒)
	private int _startTime = 0;

	private GamblingTimeList _gam = GamblingTimeList.gam();
	
/*	private static GamblingTime instance;
	
	public static GamblingTime getInstance()
	{
		if(instance == null)
		{
			instance = new GamblingTime();
		}
		return instance;
	}*/
	
	// 取回全部地图4 的 OBJ资料
	Object[] objList =  L1World.getInstance().getObjects();

	public int _winner = 0;;
	/**
	 * 启动第一竞技场计时
	 * @param mode 模式
	 */
	public void startGambling() {
		// 开始执行此时间轴
//		_timeHandler.schedule(this, 1000, 1000);
		// 交由线程工厂 处理
		_timeHandler = GeneralThreadPool.getInstance().scheduleAtFixedRate(this, 1000, 1000);
		// 纪录比赛控制者(NPC) 与挑选参赛者
		nowStart();
	}

	@Override
	public void run() {
		// 比赛结束
		if (_isOver) {
			try {
				Thread.sleep(10000);
				clear();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		_startTime ++;
		switch (_startTime) {

		case 120:// 时间轴开始8分钟
			// 通知周边玩家剩余开始比赛时间
			toAllTimeM("8");
			break;
			
		case 180:// 时间轴开始7分钟
			// 通知周边玩家剩余开始比赛时间
			toAllTimeM("7");
			break;

		case 240:// 时间轴开始6分钟
			// 通知周边玩家剩余开始比赛时间
			toAllTimeM("6");
			break;
			
		case 300:// 时间轴开始5分钟
			// 通知周边玩家剩余开始比赛时间
			toAllTimeM("5");
			break;
		case 360:// 时间轴开始4分钟
			// 通知周边玩家剩余开始比赛时间
			toAllTimeM("4");
			break;
			
		case 420:// 时间轴开始3分钟
			// 通知周边玩家剩余开始比赛时间
			toAllTimeM("3");
			break;

		case 480:// 时间轴开始2分钟
			// 通知周边玩家剩余开始比赛时间
			toAllTimeM("2");
			break;
			
		case 540:// 时间轴开始1分钟
			// 通知周边玩家剩余开始比赛时间
			toAllTimeM("1");
			break;
			
		case 595:// 时间轴开始5秒
			toAllTimeS("5");
			break;
		case 596:// 时间轴开始4秒
			toAllTimeS("4");
			break;
		case 597:// 时间轴开始3秒
			toAllTimeS("3");
			break;
		case 598:// 时间轴开始2秒
			toAllTimeS("2");
			break;
		case 599:// 时间轴开始1秒
			toAllTimeS("1");
			break;
		case 600:// 比赛开始
//			setDoorOpen(true); // 打开栅栏
			start();
			checkGambling();
			break;
		case 601:// 比赛开始
			toRate(1);
			break;
		case 602:// 比赛开始
			toRate(2);
			break;
		case 603:// 比赛开始
			toRate(3);
			break;
		case 604:// 比赛开始
			toRate(4);
			break;
		case 605:// 比赛开始
			toRate(5);
//			setDoorOpen(false); // 关闭栅栏
			break;
		}
		if (_startTime >= 620) {
			checkVictory();
		}
	}
	


	/**
	 * 告知赔率
	 */
	private void toRate(int type) {
		for (Object obj : objList) {
			if (obj instanceof L1NpcInstance) {
				// 纪录比赛控制者(NPC)
				L1NpcInstance npc = (L1NpcInstance) obj;
				if (((L1NpcInstance)obj).getNpcTemplate().get_npcId()== 70035
						|| ((L1NpcInstance)obj).getNpcTemplate().get_npcId()== 70041
						|| ((L1NpcInstance)obj).getNpcTemplate().get_npcId()== 70042) {
					double npcRate = 0.0;
					String npcName = null;
					// 取回参赛者赔率
					switch (type) {
					case 1:
						npcRate = _gam.get_npcRate1();
						npcName = _gam.get_npcGam1().getNameId();
						break;
						
					case 2:
						npcRate = _gam.get_npcRate2();
						npcName = _gam.get_npcGam2().getNameId();
						break;
						
					case 3:
						npcRate = _gam.get_npcRate3();
						npcName = _gam.get_npcGam3().getNameId();
						break;
						
					case 4:
						npcRate = _gam.get_npcRate4();
						npcName = _gam.get_npcGam4().getNameId();
						break;
						
					case 5:
						npcRate = _gam.get_npcRate5();
						npcName = _gam.get_npcGam5().getNameId();
						break;
					}
					// $402 的赔率为
					String si = String.valueOf(npcRate);

					int re = si.indexOf(".");
					if (re != -1) {
						si = si.substring(0, re + 2);
					}
					String toUser = npcName + " $402 " + si + "$367";

					
					if (npc != null) {
						npc.broadcastPacket(new S_NpcChatPacket(npc, toUser, 2));
					}
				}
			}
		}
	}

	/**
	 * 通知周边玩家剩余开始比赛时间(分)
	 * @param info
	 */
	private void toAllTimeM(String info) {
		L1Gamble.getInstance().setGambingHtml(false);//设置对话框1和5
		for (Object obj : objList) {
			if (obj instanceof L1NpcInstance) {
				// 纪录比赛控制者(NPC)
				L1NpcInstance npc = (L1NpcInstance) obj;
				if (((L1NpcInstance)obj).getNpcTemplate().get_npcId()== 70035
						|| ((L1NpcInstance)obj).getNpcTemplate().get_npcId()== 70041
						|| ((L1NpcInstance)obj).getNpcTemplate().get_npcId()== 70042) {
					// $376剩余时间： $377分钟！
					String toUser = "$376 " + info + " $377";
					
					if (npc != null) {
						npc.broadcastPacket(new S_NpcChatPacket(npc, toUser, 2));
					}
				}
			}
		}
	}
	
	/**
	 * 通知周边玩家剩余开始比赛时间(秒)
	 * @param info
	 */
	private void toAllTimeS(String info) {
		for (Object obj : objList) {
			if (obj instanceof L1NpcInstance) {
				// 纪录比赛控制者(NPC)
				L1NpcInstance npc = (L1NpcInstance) obj;
				if (((L1NpcInstance)obj).getNpcTemplate().get_npcId()== 70035
						|| ((L1NpcInstance)obj).getNpcTemplate().get_npcId()== 70041
						|| ((L1NpcInstance)obj).getNpcTemplate().get_npcId()== 70042) {
					if (npc != null) {
						npc.broadcastPacket(new S_NpcChatPacket(npc, info, 2));
					}
				}
			}
		}
	}
	
	/**
	 * 通知周边玩家剩余开始比赛开始
	 * @param info
	 */
	private void start() {
		for (Object obj : objList) {
			if (obj instanceof L1NpcInstance) {
				// 纪录比赛控制者(NPC)
				L1NpcInstance npc = (L1NpcInstance) obj;
				if (((L1NpcInstance)obj).getNpcTemplate().get_npcId()== 70035
						|| ((L1NpcInstance)obj).getNpcTemplate().get_npcId()== 70041
						|| ((L1NpcInstance)obj).getNpcTemplate().get_npcId()== 70042) {
					// 执行赔率计算
					_gam.computationRate();
					
					// $364 GO！
					String toUser = "$364";
					
					if (npc != null) {
						npc.broadcastPacket(new S_NpcChatPacket(npc, toUser, 2));
					}

					// 设置为比赛开始
					_gam.set_isStart(true);

					// 设置为比赛开始就不能买票
					_gam.set_isBuy(false);				
/*					for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
						pc.sendPackets(new S_CloseList(npc.getId()));
					}*/
				}
			}
		}
	}

	/**
	 * 清空奇岩赌场资讯(比赛结束)
	 * @param pc
	 */
	private void clear() {
		// 移除时间轴 释放资源
		GeneralThreadPool.getInstance().cancel(_timeHandler, false);
		_startTime = 0;
		_winner = 0;
		_isOver = false;
		
		// 清空本场次资讯
		_gam.clear();
		// 运行失效资源回收
		
//		System.gc();
	}

	/**
	 * 检查是否生冠军
	 */
	private void checkVictory() {
		for (Object obj : objList) {
			if (obj instanceof L1NpcInstance) {
				// 纪录比赛控制者(NPC)
				L1NpcInstance npc = (L1NpcInstance) obj;
				if (((L1NpcInstance)obj).getNpcTemplate().get_npcId()== 70035
						|| ((L1NpcInstance)obj).getNpcTemplate().get_npcId()== 70041
						|| ((L1NpcInstance)obj).getNpcTemplate().get_npcId()== 70042) {
					if (_winner > 0) {
						// 取回场次编号
						int gamId = _gam.get_gamId();
						// $375第 () $366 场比赛的优胜者是
						String toUser = "$375 (" + String.valueOf(gamId) + ") $366 " + _nowPlayer[_winner - 1].getNameId() + "$367";
						
						if (npc != null) {
							npc.broadcastPacket(new S_NpcChatPacket(npc, toUser, 2));
						}
						isOver();
					}
				}
			}
		}	
	}

	/**
	 * 控制栅栏开关
	 */
	private void setDoorOpen(boolean isOpen) {
		for (L1DoorInstance door : DoorSpawnTable.getInstance().getDoorList()) {
			if (door.getDoorId() >= 808 && door.getDoorId() <= 812) {
				if (isOpen) {
					// 打开栅栏
					door.dooropen();
				} else {
					// 关闭栅栏
					door.doorclose();
				}
			}
		}
	}

	/**
	 * 宣告比赛结束
	 */
	private void isOver() {
		// 取回参赛者
		L1NpcInstance npcGam1 = _gam.get_npcGam1();
		L1NpcInstance npcGam2 = _gam.get_npcGam2();
		L1NpcInstance npcGam3 = _gam.get_npcGam3();
		L1NpcInstance npcGam4 = _gam.get_npcGam4();
		L1NpcInstance npcGam5 = _gam.get_npcGam5();
		
		int npcId = _nowPlayer[_winner - 1].getNpcId();
		
		// 本场赔率
		double npcRate = 1.0;
		
		if (_nowPlayer[_winner - 1] == npcGam1) {
			npcRate = _gam.get_npcRate1();

		} else if (_nowPlayer[_winner - 1] == npcGam2) {
			npcRate = _gam.get_npcRate2();

		} else if (_nowPlayer[_winner - 1] == npcGam3) {
			npcRate = _gam.get_npcRate3();

		} else if (_nowPlayer[_winner - 1] == npcGam4) {
			npcRate = _gam.get_npcRate4();

		} else if (_nowPlayer[_winner - 1] == npcGam5) {
			npcRate = _gam.get_npcRate5();

		}
		
		int a1 = _gam.get_npcChip1();
		int a2 = _gam.get_npcChip2();
		int a3 = _gam.get_npcChip3();
		int a4 = _gam.get_npcChip4();
		int a5 = _gam.get_npcChip5();
		// 取回本场场累积赌金
		int totalPrice = a1 + a2 + a3 + a4 + a5;
		
		// 增加纪录
		GamblingLock.create().create(_gam.get_gamId(), npcId, npcRate, totalPrice);
		
		// 停止时间轴
		_isOver = true;
	}

	/**
	 * 检查比赛NPC状态
	 */
	private void checkGambling() {
		
		L1NpcInstance npcGam1 = _gam.get_npcGam1();
		L1NpcInstance npcGam2 = _gam.get_npcGam2();
		L1NpcInstance npcGam3 = _gam.get_npcGam3();
		L1NpcInstance npcGam4 = _gam.get_npcGam4();
		L1NpcInstance npcGam5 = _gam.get_npcGam5();
		
		if (npcGam1 != null && npcGam2 != null 
				&& npcGam3 != null && npcGam4 != null 
				&& npcGam5 != null) {
			for (int i = 0; i < 5; i++) {
				GeneralThreadPool.getInstance().execute(new MoveControl(i + 1, _nowPlayer[i]));
			}
		}
	}
	
	// 比賽移動控制執行緒
	protected class MoveControl implements Runnable {
		private final int[][] _loc;

		private int _sequence;

		private int _basespeed;

		private int _step;
		
		private L1NpcInstance _player;

		public MoveControl(int sequence, L1NpcInstance player) {
			_loc = _playerLocation[sequence - 1];
			_player = player;
			_sequence = sequence;
			_step = 0;
			_basespeed = 188 - 10 + (int) (Math.random() * 21);
		}

		@Override
		public void run() {
			int speed = 0;
			// 進行階段性移動
			while (_step < (_loc.length)) {
				try {
					// 計算延遲時間
					speed = _basespeed + (int) (Math.random() * 250);

					// 移動延遲
					Thread.sleep(speed);

					// 移動處理
					MoveTo(_loc[_step]);
				}
				catch (InterruptedException e) {
					return;
				}
			}

			// 判斷是否為獲勝者
			if (_completePlayer.get() == 0) {
				_winner = _sequence;
			}

			// 增加已完成的參賽者數目
			_completePlayer.getAndIncrement();
		}

		// 移動到目的地
		private void MoveTo(int[] loc) {
			// 設定移動點
			_player.setDirectionMove(_player.moveDirection(loc[0], loc[1]));

			// 判斷是否進入下一階段
			if (_player.getLocation().getLineDistance(loc[0], loc[1]) == 0.0) {
				_step++;
			}
		}
	}

/*	private void npcGam5() {
		npcGam5 g5 = new npcGam5();
		// 交由线程工厂执行管理
		GeneralThreadPool.getInstance().execute(g5);
	}

	private void npcGam4() {
		npcGam4 g4 = new npcGam4();
		// 交由线程工厂执行管理
		GeneralThreadPool.getInstance().execute(g4);
	}

	private void npcGam3() {
		npcGam3 g3 = new npcGam3();
		// 交由线程工厂执行管理
		GeneralThreadPool.getInstance().execute(g3);
	}

	private void npcGam2() {
		npcGam2 g2 = new npcGam2();
		// 交由线程工厂执行管理
		GeneralThreadPool.getInstance().execute(g2);
	}

	private void npcGam1() {
		npcGam1 g1 = new npcGam1();
		// 交由线程工厂执行管理
		GeneralThreadPool.getInstance().execute(g1);
	}*/

	/**
	 * 取得纪录比赛控制者(NPC) 与挑选参赛者
	 * @param mode true NC纪念币 / false 金币
	 */
	private void nowStart() {	
//		System.out.println("步骤1");
		// 取回可用场次编号
		int gamId = IdFactory.getInstance().nextGamId();
		_gam.set_gamId(gamId);
//		System.out.println("步骤2");
		// 比赛用NPC
		String [] npcid = null;
//		System.out.println("步骤3");
		switch (Config.GamNpcIsWhat) {			
		case 1: case 2: // 明星的名字
			npcid = new String[]{"贝拉堤", "卡丕尼", "巴黑", "查森", "堤索","辜莫立", "詹丕", "蓝巴", "拓罗", "塔诺",
					"哈哈", "伏虎", "春天", "卢泰愚", "尹钟新"};			
			break;
    	case 3: case 4: // 赛狗的名字
			npcid = new String[]{"朱卡", "莫卡亚", "纳瑟", "丕库拉", "索柏","锡拓", "频频", "杰仑", "卡布", "杰菲特",
					"无敌", "大成", "希望", "子弹", "闪电"};
			break;
		}
//		System.out.println("步骤4");
// 赛狗
//		int[] npcid = new int[]{95201,95202,95203,95204,95205,95206,95207,95208,95209,95210
//					,95211,95212,95213,95214,95215,95216,95217,95218,95219,95220};
// 食人妖精
//		int[] npcid = new int[]{95231,95232,95233,95234,95235,95236,95237,95238,95239,95240
//					,95241,95242,95243,95244,95245,95246,95247,95248,95249,95250};
//		System.out.println("步骤5");
		String npcid1 = npcid[_random.nextInt(npcid.length)];// 随机数字
		// 召唤NPC
		spawnGamblingNpc(1, npcid1);
		
		String npcid2 = npcid[_random.nextInt(npcid.length)];// 随机数字
		while (npcid2.equals(npcid1)) {
			npcid2 = npcid[_random.nextInt(npcid.length)];
		}
		// 召唤NPC
		spawnGamblingNpc(2, npcid2);
		
		String npcid3 = npcid[_random.nextInt(npcid.length)];// 随机数字
		while (npcid3.equals(npcid1) || npcid3.equals(npcid2)) {
			npcid3 = npcid[_random.nextInt(npcid.length)];
		}
		// 召唤NPC
		spawnGamblingNpc(3, npcid3);
		
		String npcid4 = npcid[_random.nextInt(npcid.length)];// 随机数字
		while (npcid4.equals(npcid1)|| npcid4.equals(npcid2)|| npcid4.equals(npcid3) ) {
			npcid4 = npcid[_random.nextInt(npcid.length)];
		}
		// 召唤NPC
		spawnGamblingNpc(4, npcid4);
		
		String npcid5 = npcid[_random.nextInt(npcid.length)];// 随机数字
		while (npcid5.equals(npcid1)|| npcid5.equals(npcid2) || npcid5.equals(npcid3)|| npcid5.equals(npcid4) ) {
			npcid5 = npcid[_random.nextInt(npcid.length)];
		}
		// 召唤NPC
		spawnGamblingNpc(5, npcid5);
		
		setDoorOpen(false);
		
		// 设置为比赛进入倒数
		_gam.set_isWaiting(true);

		// 设置为比赛进入倒数可以买票
		_gam.set_isBuy(true);	
		L1Gamble.getInstance().setGambingHtml(true);

	}

	/**
	 * 召唤NPC
	 * @param type
	 * @param npcname
	 */
	private void spawnGamblingNpc(int type, String npcname) {
		// 外型模组
		int[] gfx1 = null;
		int[] gfx2 = null;
		int[] gfx3 = null;
		int[] gfx4 = null;
		int[] gfx5 = null;
		int[] npcid = null;

		switch (Config.GamNpcIsWhat) {

		case 1: case 3:
// 赛狗
			gfx1 = new int[]{1353, 1355, 1357, 1359};
			gfx2 = new int[]{1461, 1465, 1469, 1473};
			gfx3 = new int[]{1462, 1466, 1470, 1474};
			gfx4 = new int[]{1463, 1467, 1471, 1475};
			gfx5 = new int[]{1464, 1468, 1472, 1476};
			break;
		case 2: case 4:
// 食人妖精	
			gfx1 = new int[]{3478, 3479, 3480, 3481};
			gfx2 = new int[]{3497, 3501, 3505, 3509};
			gfx3 = new int[]{3498, 3502, 3506, 3510};
			gfx4 = new int[]{3499, 3503, 3507, 3511};
			gfx5 = new int[]{3500, 3504, 3508, 3512};
			break;
		}
		npcid = new int[]{95231,95232,95233,95234,95235};

		int x = 0;
		int y = 0;
		int gfx = 0;
		switch (type) {
		case 1:
			x = 33514;
			y = 32869;
			gfx = gfx1[_random.nextInt(4)];// 取得外型
			break;
			
		case 2:
			x = 33516;
			y = 32867;
			gfx = gfx2[_random.nextInt(4)];// 取得外型
			break;
			
		case 3:
			x = 33518;
			y = 32865;
			gfx = gfx3[_random.nextInt(4)];// 取得外型
			break;
			
		case 4:
			x = 33520;
			y = 32863;
			gfx = gfx4[_random.nextInt(4)];// 取得外型
			break;
			
		case 5:
			x = 33522;
			y = 32861;
			gfx = gfx5[_random.nextInt(4)];// 取得外型
			break;
		}
		L1Npc l1npc;
		try {
//			System.out.println( "召唤食人妖精宝宝");
			//
//			L1Npc l1npc = NpcTable.getInstance().getTemplate(npcid);
			l1npc = new L1Npc();
			l1npc.set_npcId(npcid[type-1]);
			l1npc.set_name(npcname);
			l1npc.set_nameid(npcname);
			l1npc.set_passispeed(480);
			l1npc.set_family(0);
			l1npc.set_agrofamily(0);
			l1npc.set_picupitem(false);
			l1npc.set_gfxid(gfx);
			if (l1npc != null) {
				try {
//					System.out.println( "生成资料");
					Constructor constructor = Class.forName(
							"l1j.server.server.model.Instance.L1NpcInstance").getConstructors()[0];
					Object aobj[] = { l1npc };
					L1NpcInstance npc = (L1NpcInstance) constructor.newInstance(aobj);
					npc.setId(IdFactory.getInstance().nextId());
					npc.setName(npcname);
					npc.setNameId(npcname);
					npc.setMap((short) 4);
//					System.out.println("NPCID:"+npc.getNpcId());

					npc.setX(x);
					npc.setY(y );

					npc.setHomeX(npc.getX());
					npc.setHomeY(npc.getY());
					npc.setHeading(6);

					npc.setTempCharGfx(gfx);
					npc.setGfxId(gfx);

					L1World.getInstance().storeWorldObject(npc);
					L1World.getInstance().addVisibleObject(npc);
					switch (type) {
					case 1:
						_gam.set_npcGam1(npc);
						break;
						
					case 2:
						_gam.set_npcGam2(npc);
						break;
						
					case 3:
						_gam.set_npcGam3(npc);
						break;
						
					case 4:
						_gam.set_npcGam4(npc);
						break;
						
					case 5:
						_gam.set_npcGam5(npc);
						break;
					}
					_nowPlayer[type-1] = npc;
					
				} catch (Exception e) {
					e.getLocalizedMessage();
				}
			}
		} catch (Exception exception) {
		}
	}
}