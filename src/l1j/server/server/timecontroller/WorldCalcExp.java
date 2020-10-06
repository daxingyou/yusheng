package l1j.server.server.timecontroller;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.WorldExpBuffTable;

public class WorldCalcExp {
	private static WorldCalcExp _instance;
	private boolean _isRuning = false;
	private long _time = 0;
	
	public static WorldCalcExp get(){
		if (_instance == null){
			_instance = new WorldCalcExp();
		}
		return _instance;
	}
	
	public boolean isRuning(){
		return _isRuning;
	}
	
	public void addTime(final long time){
		_time += time;
	}
	
	public long getTime(){
		return _time;
	}
	
	public synchronized void start() {
		if (_isRuning){
			return;
		}
		_isRuning = true;
		ExpThread ub = new ExpThread();
		GeneralThreadPool.getInstance().execute(ub);
	}
	
	class ExpThread implements Runnable {
		@Override
		public void run() {
			try {
				while (_time > 0) {
					_time--;
					if (_time % 60 == 0){//每60秒存一次档
						WorldExpBuffTable.get().update(_time);
					}
					Thread.sleep(1000);
				}
				WorldExpBuffTable.get().update(_time);
				_isRuning = false;
			} catch (InterruptedException e) {
				_isRuning = false;
			}
		}
	}
	
}
