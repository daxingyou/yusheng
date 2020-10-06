package l1j.server.server.model.game;

//import java.lang.reflect.Constructor;
//import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import l1j.server.Config;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
//import l1j.server.server.datatables.DoorSpawnTable;
//import l1j.server.server.datatables.NpcTable;
//import l1j.server.server.model.Instance.L1DoorInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
//import l1j.server.server.model.Instance.L1PcInstance;
//import l1j.server.server.serverpackets.S_CloseList;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.storage.BigHotblingLock;
//import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1BigHotbling;
import l1j.server.server.world.L1World;

/**
 * 奇岩赌场 控制项时间轴
 * @author dexc
 *
 */
public class BigHotblingTime extends TimerTask {
	
	//private static Random _random = new Random();
	
	private Timer _timeHandler = new Timer(true);
	
	private boolean _isOver = false;
	
	// 比赛已开始时间(1/2秒)
	private int _BigHottTime = 0;

	private BigHotblingTimeList _BigHot = BigHotblingTimeList.BigHot();

	public String BigHotAN; // 这个里面存答案

	private int BigHotAN1 = 0;
	private int BigHotAN2 = 0;
	private int BigHotAN3 = 0;
	private int BigHotAN4 = 0;
	private int BigHotAN5 = 0;
	private int BigHotAN6 = 0;

	// 取回全部地图4 的 OBJ资料
	Object[] objList = L1World.getInstance().getObjects();

	public void startBigHotbling() {
		// 开始执行此时间轴
		_timeHandler.schedule(this, 500, 500);
		// 交由线程工厂 处理
		GeneralThreadPool.getInstance().execute(this);
		// 纪录比赛控制者(NPC)
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
		
		_BigHottTime ++;
		switch (_BigHottTime) {

		case 1*2:// 时间轴开始15分钟
			// 通知周边玩家剩余开始开奖时间
			toAllTimeM("15");
			L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("距离大乐透开奖时间还有15分钟。"));
			donumber1(); // 设置开奖号码
			break;
			
		case 300*2:// 时间轴开始10分钟
			// 通知周边玩家剩余开始开奖时间
			toAllTimeM("10");
			L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("距离大乐透开奖时间还有10分钟。"));
			break;

		case 600*2:// 时间轴开始5分钟
			// 通知周边玩家剩余开始开奖时间
			toAllTimeM("5");
			L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("距离大乐透开奖时间还有5分钟。"));
			break;

		case 660*2:// 时间轴开始4分钟
			// 通知周边玩家剩余开始开奖时间
			toAllTimeM("4");
//			L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("距离大乐透开奖时间还有4分钟。"));
			break;
			
		case 720*2:// 时间轴开始3分钟
			// 通知周边玩家剩余开始开奖时间
			toAllTimeM("3");
//			L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("距离大乐透开奖时间还有3分钟。"));
			break;
			
		case 780*2:// 时间轴开始2分钟
			// 通知周边玩家剩余开始开奖时间
			toAllTimeM("2");
//			L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("距离大乐透开奖时间还有2分钟。"));
			break;
		case 840*2:// 时间轴开始1分钟
			// 通知周边玩家剩余开始开奖时间
			toAllTimeM("1");
			L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("距离大乐透开奖时间还有1分钟。"));
			break;

		case 895*2:// 时间轴开始5秒
			toAllTimeS("5");
			break;
		case 896*2:// 时间轴开始4秒
			toAllTimeS("4");
			break;
		case 897*2:// 时间轴开始3秒
			toAllTimeS("3");
			break;
		case 898*2:// 时间轴开始2秒
			toAllTimeS("2");
			break;
		case 899*2:// 时间轴开始1秒
			toAllTimeS("1");
			break;
		case 900*2:// 比赛开始
			Start();
			break;
		case 903*2:
			toRate1(1);
			break;
		case 906*2:
			toRate1(2);
			break;
		case 909*2:
			toRate1(3);
			break;
		case 912*2:
			toRate(1, BigHotAN1);
			break;
		case 917*2:
			toRate(2, BigHotAN2);
			break;
		case 922*2:
			toRate(3, BigHotAN3);
			break;
		case 927*2:
			toRate(4, BigHotAN4);
			break;
		case 932*2:
			toRate(5, BigHotAN5);
			break;
		case 937*2:
			toRate(6, BigHotAN6);
			break;
		case 942*2:// 发布开奖号码
			checkVictory();
			int BigHotId = _BigHot.get_BigHotId();
			L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("大乐透 第 " + String.valueOf(BigHotId) + " 期开出的号码是 " + BigHotAN + "。"));
			break;
/*
		case 1*2:// 时间轴开始1分钟
			// 通知周边玩家剩余开始开奖时间
			toAllTimeM("1");
			L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("距离大乐透开奖时间还有1分钟。"));
			donumber1();// 设置开奖号码
			break;

		case 55*2:// 时间轴开始5秒
			toAllTimeS("5");
			break;
		case 56*2:// 时间轴开始4秒
			toAllTimeS("4");
			break;
		case 57*2:// 时间轴开始3秒
			toAllTimeS("3");
			break;
		case 58*2:// 时间轴开始2秒
			toAllTimeS("2");
			break;
		case 59*2:// 时间轴开始1秒
			toAllTimeS("1");
			break;
		case 60*2:// 比赛开始
			Start();
			break;
		case 62*2:
			toRate1(1);
			break;
		case 64*2:
			toRate1(2);
			break;
		case 66*2:
			toRate1(3);
			break;
		case 70*2:
			toRate(1, BigHotAN1);
			break;
		case 72*2:
			toRate(2, BigHotAN2);
			break;
		case 74*2:
			toRate(3, BigHotAN3);
			break;
		case 76*2:
			toRate(4, BigHotAN4);
			break;
		case 78*2:
			toRate(5, BigHotAN5);
			break;
		case 80*2:
			toRate(6, BigHotAN6);
			break;
		case 85*2:// 发布开奖号码
			checkVictory();
			int BigHotId = _BigHot.get_BigHotId();
			L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("大乐透 第 " + String.valueOf(BigHotId) + " 期开出的号码是 " + BigHotAN + "。"));
			break;
*/
		}
	}

	/**
	 * 告知开奖号码
	 */
	private void toRate(int type, int info) {
		for (Object obj : objList) {
			if (obj instanceof L1NpcInstance) {
				// 纪录比赛控制者(NPC)
				L1NpcInstance npc = (L1NpcInstance) obj;
				if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 95328) {
					String Name = null;
					switch (type) {
					case 1:
						Name = "一";
						break;
					case 2:
						Name = "二";
						break;
					case 3:
						Name = "三";
						break;
					case 4:
						Name = "四";
						break;
					case 5:
						Name = "五";
						break;
					case 6:
						Name = "六";
						break;
					}
					String toUser = "开出的第" + Name + "个号码是(" + info + ")";

					if (npc != null) {
						npc.broadcastPacket(new S_NpcChatPacket(npc, toUser, 2));
					}
				}
			}
		}
	}

	/**
	 * 告知中奖彩金
	 */
	private void toRate1(int type) {
		for (Object obj : objList) {
			if (obj instanceof L1NpcInstance) {
				// 纪录比赛控制者(NPC)
				L1NpcInstance npc = (L1NpcInstance) obj;
				if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 95328) {
					String Name1 = null;
					int money = 0;
					switch (type) {
					case 1:
						Name1 = "头奖";
						money = _BigHot.get_bigmoney1();
						break;
					case 2:
						Name1 = "一奖";
						money = _BigHot.get_bigmoney2();
						break;
					case 3:
						Name1 = "二奖";
						money = _BigHot.get_bigmoney3();
						break;
					}
					String toUser = "本期" + Name1 + "的奖金是(" + money + ")";

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
		for (Object obj : objList) {
			if (obj instanceof L1NpcInstance) {
				// 纪录比赛控制者(NPC)
				L1NpcInstance npc = (L1NpcInstance) obj;
				if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 95328) {
					// $376剩余时间： $377分钟！
					String toUser = "距离开奖$376 " + info + " $377";
					
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
				if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 95328) {
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
	private void Start() {
		for (Object obj : objList) {
			if (obj instanceof L1NpcInstance) {
				// 纪录比赛控制者(NPC)
				L1NpcInstance npc = (L1NpcInstance) obj;
				if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 95328) {

					String toUser = "大乐透即将开奖噜！";
					
					if (npc != null) {
						npc.broadcastPacket(new S_NpcChatPacket(npc, toUser, 2));
					}

					// 设置为比赛开始
					_BigHot.set_isStart(true);
				}
			}
		}
		// 如果都没有人押注，累积加码彩金1000
		if (_BigHot.get_yuanbao() == 0) {
			_BigHot.add_yuanbao((int) Config.BigHotAddGold);
		}

		// 取回累积彩金
		L1BigHotbling BigHotInfo = BigHotblingLock.create().getBigHotbling((_BigHot.get_BigHotId() - 1));
		if (BigHotInfo != null) {
			// 上一场要是没人中头奖，累积到这一场
			if (BigHotInfo.get_count() == 0) {
				// 要是上期累积彩金达设定值就不累积彩金
				if (BigHotInfo.get_money1() < Config.BigHotAddMoney) {
					_BigHot.add_yuanbao((int) BigHotInfo.get_money1());
				} else {
					_BigHot.add_yuanbao((int) Config.BigHotGold);
				}
			} else {
				// 如果没有上一场开奖纪录，基本彩金为1万
				_BigHot.add_yuanbao((int) Config.BigHotGold);
			}
			if (BigHotInfo.get_count1() == 0) {
				// 上一场要是没人中一奖，累积到这一场
				_BigHot.add_yuanbao((int) BigHotInfo.get_money2());
			}
			if (BigHotInfo.get_count2() == 0) {
				// 上一场要是没人中二奖，累积到这一场
				_BigHot.add_yuanbao((int) BigHotInfo.get_money3());
			}
		} else {
			// 如果没有上一场开奖纪录，基本彩金为1万
			_BigHot.add_yuanbao((int) Config.BigHotGold);
		}

		// 执行彩金计算
		_BigHot.computationBigHot();

	}

	/**
	 * 清空奇岩赌场资讯(比赛结束)
	 * @param pc
	 */
	private void clear() {

		// 移除时间轴 释放资源
		if (this.cancel()) {
			// 停止时间轴
			_timeHandler.purge();
		}

		BigHotAN = null;
		_BigHottTime = 0;
		_isOver = false;
		
		// 清空本场次资讯
		_BigHot.clear();
		// 运行失效资源回收
		System.gc();
	}

	/**
	 * 检查是否生冠军
	 */
	public void checkVictory() {
		for (Object obj : objList) {
			if (obj instanceof L1NpcInstance) {
				// 纪录比赛控制者(NPC)
				L1NpcInstance npc = (L1NpcInstance) obj;
				if (((L1NpcInstance)obj).getNpcTemplate().get_npcId() == 95328) {
					if (BigHotAN != null) {
						// 取回场次编号
						int BigHotId = _BigHot.get_BigHotId();

						String toUser = "大乐透 $375 " + String.valueOf(BigHotId) + " 期开出的号码是 " + BigHotAN + "。";
						
						if (npc != null) {
							npc.broadcastPacket(new S_NpcChatPacket(npc, toUser, 2));
						}
						isOver();	

						// 设置为比赛结束
						_BigHot.set_isStart(false);
					}
				}
			}
		}
	}

	private void donumber1() {
		BigHotAN = "";
		while(BigHotAN.split(",").length < 6) {
			int sk  = 1 + (int) (Math.random() * 46);
			if (BigHotAN.indexOf(sk + ",") < 0)
				BigHotAN += String.valueOf(sk) + ",";
			if (BigHotAN.split(",").length == 1) {
				BigHotAN1 = sk;
			}
			if (BigHotAN.split(",").length == 2) {
				BigHotAN2 = sk;
			}
			if (BigHotAN.split(",").length == 3) {
				BigHotAN3 = sk;
			}
			if (BigHotAN.split(",").length == 4) {
				BigHotAN4 = sk;
			}
			if (BigHotAN.split(",").length == 5) {
				BigHotAN5 = sk;
			}
			if (BigHotAN.split(",").length == 6) {
				BigHotAN6 = sk;
			}
		}
		// 记录开奖号码
		_BigHot.set_BigHotId1(BigHotAN);

	}


	/**
	 * 宣告比赛结束
	 */
	private void isOver() {
		// 取回本场累积赌金
		int yuanbao = _BigHot.get_yuanbao();

		// 取回本场头奖赌金
		int yuanbao1 = _BigHot.get_bigmoney1();

		// 取回本场一奖赌金
		int yuanbao2 = _BigHot.get_bigmoney2();

		// 取回本场二奖赌金
		int yuanbao3 = _BigHot.get_bigmoney3();

		// 取回本场头奖中奖票数
		int count1 = _BigHot.get_count1();

		// 取回本场一奖中奖票数
		int count2 = _BigHot.get_count2();

		// 取回本场二奖中奖票数
		int count3 = _BigHot.get_count3();

		// 取回本场三奖中奖票数
		int count4 = _BigHot.get_count4();

		// 增加纪录
		BigHotblingLock.create().create(_BigHot.get_BigHotId(), BigHotAN, yuanbao, yuanbao1, count1, yuanbao2, count2, yuanbao3, count3, count4);
		
		// 停止时间轴
		_isOver = true;
	}

	private void nowStart() {
		// 取回可用场次编号
		int BigHotId = IdFactory.getInstance().nextBigHotId();
		_BigHot.set_BigHotId(BigHotId);

		// 设置为比赛进入倒数
		_BigHot.set_isWaiting(true);

		// 设置为比赛进入倒数可以买票
		_BigHot.set_isBuy(true);	

	}


}