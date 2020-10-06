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
public class T_Gambling extends TimerTask {

	// TODO 资料存放区

	private static T_Gambling _instance;
	
	private Timer _timeHandler = new Timer(true);
	
	private boolean _isStart = false;
	
	/**
	 * <font color=#00800>赌场状态检查轴</font><br>
	 * @author dexc
	 */
	public static T_Gambling getStart() {
		if (_instance == null) {
			_instance = new T_Gambling();
		}
		return _instance;
	}

	private T_Gambling() {
		// 开始执行此时间轴(10秒检查一次)
		_timeHandler.schedule(this, 5000, 10000);
		// 交由线程工厂执行管理
		GeneralThreadPool.getInstance().execute(this);
	}

	@Override
	public void run() {
/*		if (!_isStart) {
			GamblingTime gam = new GamblingTime();
			gam.startGambling();
			_isStart = true;
		}*/
		String mTime = TimeInfo.time().getNow_YMDHMS(3);
		String hTime = TimeInfo.time().getNow_YMDHMS(4);
		int mm = Integer.parseInt(mTime);
//		int hh = Integer.parseInt(hTime);
		switch (mm) {
// 每10分钟 重新启动
			case 01: case 11: case 21: case 31: case 41: case 51:
				//if (!_isStart && second > 10) {
				if (!_isStart) {
					GamblingTime gam = new GamblingTime();
					gam.startGambling();
					_isStart = true;
				}
				break;
// 重置
			case 03: case 13: case 23: case 33: case 43: case 53:
				if (_isStart) {
					_isStart = false;
				}
				break;
		}
	}
	public void setT_GamStart(boolean flg){
		_isStart = flg;
	}
	
	public boolean isT_GamStart(){
		return _isStart;
	}

	public void startT_Gambling(){
		T_Gambling.getStart();
	}
}
