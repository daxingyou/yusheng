package l1j.server.server.model.game;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import l1j.server.Config;
import l1j.server.TimeInfo;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.world.L1World;

/**
 * 赌场状态检查轴
 * @author dexc
 */
public class T_BigHotbling extends TimerTask {

	// TODO 资料存放区

	private static T_BigHotbling _instance;
	
	private Timer _timeHandler = new Timer(true);
	
	private boolean _isBigHotta = false;
	
	/**
	 * <font color=#00800>赌场状态检查轴</font><br>
	 * @author dexc
	 */
	public static T_BigHotbling getStart() {
		if (_instance == null) {
			_instance = new T_BigHotbling();
		}
		return _instance;
	}

	private T_BigHotbling() {
		// 开始执行此时间轴(10秒检查一次)
		_timeHandler.schedule(this, 5000, 10000);
		// 交由线程工厂执行管理
		GeneralThreadPool.getInstance().execute(this);
	}

	@Override
	public void run() {
		String mTime = TimeInfo.time().getNow_YMDHMS(3);
		String hTime = TimeInfo.time().getNow_YMDHMS(4);
		int mm = Integer.parseInt(mTime);
		switch (mm) {
// 每15分钟 重新启动
			case 01: case 21: case 41:
				//if (!_isBigHotta && second > 20) {
				if (!_isBigHotta) {
					BigHotblingTime BigHot = new BigHotblingTime();
					BigHot.startBigHotbling();
					_isBigHotta = true;
				}
				break;
// 重置
			case 06: case 26: case 46:
				if (_isBigHotta) {
					_isBigHotta = false;
				}
				break;
		}
	}

	public void startT_BigHotbling(){
		T_BigHotbling.getStart();
	}
}
