package l1j.server.server.model.game;

import java.util.Random;

import l1j.server.Config;
import l1j.server.server.model.Instance.L1NpcInstance;

/**
 * 大乐透 控制项清单
 * @author dexc
 *
 */
public class BigHotblingTimeList {
	
	//private static Random _random = new Random();

	// 大乐透是否进行中
	boolean _isStart = false;
	
	// 大乐透是否进入倒数
	boolean _isWaiting = false;

	// 大乐透开放买票
	boolean _isBuy = false;

	// 大乐透纪录场次编号
	private int _BigHotId = 0;

	// 纪录开奖号码
	private String _BigHotId1 = null;

	// 大乐透累计押注元宝
	private int _yuanbao = 0;

	// 大乐透累计头奖中奖票数
	private int _count1 = 0;

	// 大乐透累计一奖中奖票数
	private int _count2 = 0;

	// 大乐透累计二奖中奖票数
	private int _count3 = 0;

	// 大乐透累计三奖中奖票数
	private int _count4 = 0;

	// 大乐透头彩奖金
	private int _bigmoney1 = 0;

	// 大乐透一奖奖金
	private int _bigmoney2 = 0;

	// 大乐透二奖奖金
	private int _bigmoney3 = 0;

	private static BigHotblingTimeList _instance;

	public static BigHotblingTimeList BigHot() {
		if (_instance == null) {
			_instance = new BigHotblingTimeList();
		}
		return _instance;
	}

	/**
	 * 清空大乐透资讯(大乐透结束)
	 * @param pc
	 */
	public void clear() {
		// 取消大乐透状态
		_isStart = false;

		// 取消开放买票
		_isBuy = false;

		// 取消四星彩进入倒数
		_isWaiting = false;

		// 清空场次编号
		_BigHotId = 0;

		// 清空开奖号码
		_BigHotId1 = null;

		// 清空累计押注筹码
		_yuanbao = 0;

		// 清空累计中奖票数
		_count1 = 0;
		_count2 = 0;
		_count3 = 0;
		_count4 = 0;

		// 清空彩金纪录
		_bigmoney1 = 0;
		_bigmoney2 = 0;
		_bigmoney3 = 0;

	}

	// TODO 大乐透状态

	/**
	 * 传回大乐透状态
	 * @return
	 */
	public boolean get_isStart() {
		return _isStart;
	}

	/**
	 * 设置大乐透状态
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
	 * 传回大乐透进入倒数状态
	 * @return
	 */
	public boolean get_isWaiting() {
		return _isWaiting;
	}

	/**
	 * 设置大乐透进入倒数状态
	 * @param b
	 */
	public void set_isWaiting(boolean b) {
		_isWaiting = b;
	}
	
	/**
	 * 传回场次编号
	 * @return
	 */
	public int get_BigHotId() {
		return _BigHotId;
	}

	/**
	 * 设置场次编号
	 * @param b
	 */
	public void set_BigHotId(int i) {
		_BigHotId = i;
	}

	/**
	 * 传回开奖编号
	 * @return
	 */
	public String get_BigHotId1() {
		return _BigHotId1;
	}

	/**
	 * 设置开奖编号
	 * @param b
	 */
	public void set_BigHotId1(String i) {
		_BigHotId1 = i;
	}

	/**
	 * 传回大乐透彩金押注(累积)
	 */
	public int get_yuanbao() {
		return _yuanbao;
	}
	
	/**
	 * 设定大乐透彩金押注(累积)
	 */
	public void add_yuanbao(int i) {
		_yuanbao += i;
	}

	/**
	 * 传回大乐透头奖中奖票数(累积)
	 */
	public int get_count1() {
		return _count1;
	}
	
	/**
	 * 设定大乐透头奖中奖票数(累积)
	 */
	public void add_count1(int i) {
		_count1 += i;
	}

	/**
	 * 传回大乐透一奖中奖票数(累积)
	 */
	public int get_count2() {
		return _count2;
	}
	
	/**
	 * 设定大乐透一奖中奖票数(累积)
	 */
	public void add_count2(int i) {
		_count2 += i;
	}

	/**
	 * 传回大乐透二奖中奖票数(累积)
	 */
	public int get_count3() {
		return _count3;
	}
	
	/**
	 * 设定大乐透二奖中奖票数(累积)
	 */
	public void add_count3(int i) {
		_count3 += i;
	}

	/**
	 * 传回大乐透三奖中奖票数(累积)
	 */
	public int get_count4() {
		return _count4;
	}
	
	/**
	 * 设定大乐透三奖中奖票数(累积)
	 */
	public void add_count4(int i) {
		_count4 += i;
	}

	/**
	 * 传回头奖彩金
	 * @return
	 */
	public int get_bigmoney1() {
		return _bigmoney1;
	}

	/**
	 * 传回一奖彩金
	 * @return
	 */
	public int get_bigmoney2() {
		return _bigmoney2;
	}

	/**
	 * 传回二奖彩金
	 * @return
	 */
	public int get_bigmoney3() {
		return _bigmoney3;
	}

	// TODO 计算大乐透彩金分配
	
	public void computationBigHot() {
		int AllMoney = _yuanbao - (_count4 * 10); // 总彩金 - (三奖中奖数*押注价格)

		_bigmoney1 = (AllMoney * 7) / 10;
		_bigmoney2 = (AllMoney * 2) / 10;
		_bigmoney3 = AllMoney / 10;
	}

}
