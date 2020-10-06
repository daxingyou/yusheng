package l1j.server.server.model.game;

import java.util.Random;

import l1j.server.Config;
import l1j.server.server.model.Instance.L1NpcInstance;

/**
 * 奇岩赌场 控制项清单
 * @author dexc
 *
 */
public class GamblingTimeList {
	
	//private static Random _random = new Random();
	
	// 竞赛者
	private L1NpcInstance _npcGam1 = null;
	private L1NpcInstance _npcGam2 = null;
	private L1NpcInstance _npcGam3 = null;
	private L1NpcInstance _npcGam4 = null;
	private L1NpcInstance _npcGam5 = null;
	
	// 竞赛者累计押注筹码
	private int _npcChip1 = 0;
	private int _npcChip2 = 0;
	private int _npcChip3 = 0;
	private int _npcChip4 = 0;
	private int _npcChip5 = 0;
	
	// 本场次赔率
	private double _npcRate1 = 1.00;
	private double _npcRate2 = 1.00;
	private double _npcRate3 = 1.00;
	private double _npcRate4 = 1.00;
	private double _npcRate5 = 1.00;

	// 比赛是否进行中
	boolean _isStart = false;
	
	// 比赛是否进入倒数
	boolean _isWaiting = false;

	// 开放买票
	boolean _isBuy = false;

	// 纪录场次编号
	private int _gamId = 0;
	
	private Random _random = new Random();
	
	private static GamblingTimeList _instance;

	public static GamblingTimeList gam() {
		if (_instance == null) {
			_instance = new GamblingTimeList();
		}
		return _instance;
	}

	/**
	 * 清空奇岩赌场资讯(比赛结束)
	 * @param pc
	 */
	public void clear() {
		// 删除竞赛者
		_npcGam1.deleteMe();
		_npcGam2.deleteMe();
		_npcGam3.deleteMe();
		_npcGam4.deleteMe();
		_npcGam5.deleteMe();

		// 清空竞赛者
		_npcGam1 = null;
		_npcGam2 = null;
		_npcGam3 = null;
		_npcGam4 = null;
		_npcGam5 = null;

		// 清空押注
		_npcChip1 = 0;
		_npcChip2 = 0;
		_npcChip3 = 0;
		_npcChip4 = 0;
		_npcChip5 = 0;
		
		// 清空本场次赔率
		_npcRate1 = 1.00;
		_npcRate2 = 1.00;
		_npcRate3 = 1.00;
		_npcRate4 = 1.00;
		_npcRate5 = 1.00;
		
		// 取消比赛状态
		_isStart = false;

		// 取消开放买票
		_isBuy = false;

		// 取消比赛进入倒数
		_isWaiting = false;
		// 清空场次编号
		_gamId = 0;
	}

	// TODO 比赛状态
	
	/**
	 * 传回赌场比赛状态
	 * @return
	 */
	public boolean get_isStart() {
		return _isStart;
	}

	/**
	 * 设置赌场比赛状态
	 * @param b
	 */
	public void set_isStart(boolean b) {
		_isStart = b;
	}

// 传回买票状态
	public boolean get_isBuy() {
		return _isBuy;
	}
// 设置买票状态
	public void set_isBuy(boolean b) {
		_isBuy = b;
	}

	/**
	 * 传回赌场比赛进入倒数状态
	 * @return
	 */
	public boolean get_isWaiting() {
		return _isWaiting;
	}

	/**
	 * 设置赌场比赛进入倒数状态
	 * @param b
	 */
	public void set_isWaiting(boolean b) {
		_isWaiting = b;
	}
	
	/**
	 * 传回场次编号
	 * @return
	 */
	public int get_gamId() {
		return _gamId;
	}

	/**
	 * 设置场次编号
	 * @param b
	 */
	public void set_gamId(int i) {
		_gamId = i;
	}
	
	// TODO 参赛者
	
	/**
	 * 传回赌场参赛者1
	 * @return
	 */
	public L1NpcInstance get_npcGam1() {
		return _npcGam1;
	}
	
	/**
	 * 设定赌场参赛者1
	 * @param npc
	 */
	public void set_npcGam1(L1NpcInstance npc) {
		_npcGam1 = npc;
	}
	
	/**
	 * 传回赌场参赛者2
	 * @return
	 */
	public L1NpcInstance get_npcGam2() {
		return _npcGam2;
	}
	
	/**
	 * 设定赌场参赛者2
	 * @param npc
	 */
	public void set_npcGam2(L1NpcInstance npc) {
		_npcGam2 = npc;
	}
	
	/**
	 * 传回赌场参赛者3
	 * @return
	 */
	public L1NpcInstance get_npcGam3() {
		return _npcGam3;
	}
	
	/**
	 * 设定赌场参赛者3
	 * @param npc
	 */
	public void set_npcGam3(L1NpcInstance npc) {
		_npcGam3 = npc;
	}
	
	/**
	 * 传回赌场参赛者4
	 * @return
	 */
	public L1NpcInstance get_npcGam4() {
		return _npcGam4;
	}
	
	/**
	 * 设定赌场参赛者4
	 * @param npc
	 */
	public void set_npcGam4(L1NpcInstance npc) {
		_npcGam4 = npc;
	}
	
	/**
	 * 传回赌场参赛者5
	 * @return
	 */
	public L1NpcInstance get_npcGam5() {
		return _npcGam5;
	}
	
	/**
	 * 设定赌场参赛者5
	 * @param npc
	 */
	public void set_npcGam5(L1NpcInstance npc) {
		_npcGam5 = npc;
	}
	
	// TODO 押注
	
	/**
	 * 传回赌场参赛者1 押注(累积)
	 * @return
	 */
	public int get_npcChip1() {
		return _npcChip1;
	}
	
	/**
	 * 设定赌场参赛者1 押注(累积)
	 * @param npc
	 */
	public void add_npcChip1(int i) {
		_npcChip1 += i;
	}
	
	/**
	 * 传回赌场参赛者2 押注(累积)
	 * @return
	 */
	public int get_npcChip2() {
		return _npcChip2;
	}
	
	/**
	 * 设定赌场参赛者2 押注(累积)
	 * @param npc
	 */
	public void add_npcChip2(int i) {
		_npcChip2 += i;
	}
	
	/**
	 * 传回赌场参赛者3 押注(累积)
	 * @return
	 */
	public int get_npcChip3() {
		return _npcChip3;
	}
	
	/**
	 * 设定赌场参赛者3 押注(累积)
	 * @param npc
	 */
	public void add_npcChip3(int i) {
		_npcChip3 += i;
	}
	
	/**
	 * 传回赌场参赛者1 押注(累积)
	 * @return
	 */
	public int get_npcChip4() {
		return _npcChip4;
	}
	
	/**
	 * 设定赌场参赛者4 押注(累积)
	 * @param npc
	 */
	public void add_npcChip4(int i) {
		_npcChip4 += i;
	}
	
	/**
	 * 传回赌场参赛者5 押注(累积)
	 * @return
	 */
	public int get_npcChip5() {
		return _npcChip5;
	}
	
	/**
	 * 设定赌场参赛者5 押注(累积)
	 * @param npc
	 */
	public void add_npcChip5(int i) {
		_npcChip5 += i;
	}
	
	// TODO 传回赔率
	
	/**
	 * 传回赌场参赛者1 赔率
	 * @return
	 */
	public double get_npcRate1() {
		return _npcRate1;
	}
	
	/**
	 * 传回赌场参赛者2 赔率
	 * @return
	 */
	public double get_npcRate2() {
		return _npcRate2;
	}
	
	/**
	 * 传回赌场参赛者3 赔率
	 * @return
	 */
	public double get_npcRate3() {
		return _npcRate3;
	}
	
	/**
	 * 传回赌场参赛者4 赔率
	 * @return
	 */
	public double get_npcRate4() {
		return _npcRate4;
	}
	
	/**
	 * 传回赌场参赛者5 赔率
	 * @return
	 */
	public double get_npcRate5() {
		return _npcRate5;
	}
	
	// TODO 计算本场赔率
	
	public void computationRate() {
		// 机率暴增
		double rateUP = 1.0;
		int random = _random.nextInt(500);
//				RandomArrayList.getInt(500);
		if (random > 110 && random < 119) {
			rateUP = 1.55;
		}
		//System.out.println("机率暴增:" + rateUP);
		int tP = _npcChip1 + _npcChip2 + _npcChip3 + _npcChip4 + _npcChip5;
		if (tP <= 0) {
			tP = 1;
		}
		//System.out.println("Config.COMMISSION:"+Config.COMMISSION);
		
		// 预计赔出金额
		double oi = tP / Config.COMMISSION;
		//System.out.println("预计赔出金额:"+oi);
		
		// 参赛者1卖出彩票数量
		double c1 = _npcChip1;
		if (c1 <= 0.00) {
			c1 = 1.00;
		}
		double oty1 = c1 / Config.Gam_CHIP;
		//System.out.println("参赛者1卖出彩票数量:"+oty1);
		
		// 参赛者2卖出彩票数量
		double c2 = _npcChip2;
		if (c2 <= 0.00) {
			c2 = 1.00;
		}
		double oty2 = c2 / Config.Gam_CHIP;
		//System.out.println("参赛者2卖出彩票数量:"+oty2);
		
		// 参赛者3卖出彩票数量
		double c3 = _npcChip3;
		if (c3 <= 0.00) {
			c3 = 1.00;
		}
		double oty3 = c3 / Config.Gam_CHIP;
		//System.out.println("参赛者3卖出彩票数量:"+oty3);
		
		// 参赛者4卖出彩票数量
		double c4 = _npcChip4;
		if (c4 <= 0.00) {
			c4 = 1.00;
		}
		double oty4 = c4 / Config.Gam_CHIP;
		//System.out.println("参赛者4卖出彩票数量:"+oty4);
		
		// 参赛者5卖出彩票数量
		double c5 = _npcChip5;
		if (c5 <= 0.00) {
			c5 = 1.00;
		}
		double oty5 = c5 / Config.Gam_CHIP;
		//System.out.println("参赛者5卖出彩票数量:"+oty5);
		
		// 本场次各参赛者赔率
		_npcRate1 = ((oi / oty1) / Config.Gam_CHIP) * rateUP;
		if (_npcRate1 > 100) {
			_npcRate1 = 100;
		}
		_npcRate2 = ((oi / oty2) / Config.Gam_CHIP) * rateUP;
		if (_npcRate2 > 100) {
			_npcRate2 = 100;
		}
		_npcRate3 = ((oi / oty3) / Config.Gam_CHIP) * rateUP;
		if (_npcRate3 > 100) {
			_npcRate3 = 100;
		}
		_npcRate4 = ((oi / oty4) / Config.Gam_CHIP) * rateUP;
		if (_npcRate4 > 100) {
			_npcRate4 = 100;
		}
		_npcRate5 = ((oi / oty5) / Config.Gam_CHIP) * rateUP;
		if (_npcRate5 > 100) {
			_npcRate5 = 100;
		}
		/*System.out.println("_npcRate1:"+_npcRate1);
		System.out.println("_npcRate2:"+_npcRate2);
		System.out.println("_npcRate3:"+_npcRate3);
		System.out.println("_npcRate4:"+_npcRate4);
		System.out.println("_npcRate5:"+_npcRate5);*/
	}
}
