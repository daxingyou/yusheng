/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server;


import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.Config;
import l1j.server.server.model.monitor.L1PcMonitor;

public class GeneralThreadPool {
	private static final Log _log = LogFactory.getLog(GeneralThreadPool.class);


	private static GeneralThreadPool _instance;

	private static final int SCHEDULED_CORE_POOL_SIZE = 1000;

	private Executor _executor; // 泛用ExecutorService
	private ScheduledExecutorService _scheduler; // 泛用ScheduledExecutorService
	private ScheduledExecutorService _pcScheduler; // 用ScheduledExecutorService
//	private ScheduledExecutorService _timeScheduler;
	// 一应L1J状态、map:4何PC1秒间占有实行时间约6ms(AutoUpdate:约6ms,ExpMonitor:极小)
	private final int _pcSchedulerPoolSize = 1 + Config.MAX_ONLINE_USERS/10; // 适当(20User1割当)

	public static GeneralThreadPool getInstance() {
		if (_instance == null) {
			_instance = new GeneralThreadPool();
		}
		return _instance;
	}

	private GeneralThreadPool() {
		if (Config.THREAD_P_TYPE_GENERAL == 1) {
			_executor = Executors
					.newFixedThreadPool(Config.THREAD_P_SIZE_GENERAL);
		} else if (Config.THREAD_P_TYPE_GENERAL == 2) {
			_executor = Executors.newCachedThreadPool();
		} else {
			_executor = null;
		}
		_scheduler = Executors.newScheduledThreadPool(SCHEDULED_CORE_POOL_SIZE,new PriorityThreadFactory("GerenalSTPool",Thread.NORM_PRIORITY));
		_pcScheduler = Executors.newScheduledThreadPool(_pcSchedulerPoolSize,new PriorityThreadFactory("PcMonitorSTPool",Thread.NORM_PRIORITY));
		// AI(创建一个线程池，它可安排在给定延迟后运行命令或者定期地执行。)
//		_timeScheduler = Executors.newScheduledThreadPool(SCHEDULED_CORE_POOL_SIZE, new PriorityThreadFactory("AITPool",Thread.NORM_PRIORITY));
	}

	public void execute(Runnable r) {
		if (_executor == null) {
			Thread t = new Thread(r);
			t.start();
		} else {
			_executor.execute(r);
		}
	}

	public void execute(Thread t) {
		t.start();
	}

	public ScheduledFuture<?> schedule(Runnable r, long delay) {
		try {
			if (delay <= 0) {
				_executor.execute(r);
				return null;
			}
			return _scheduler.schedule(r, delay, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException e) {
			return null;
		}
	}

	public ScheduledFuture<?> scheduleAtFixedRate(Runnable r,
			long initialDelay, long period) {
		return _scheduler.scheduleAtFixedRate(r, initialDelay, period,
				TimeUnit.MILLISECONDS);
	}

	public ScheduledFuture<?> pcSchedule(L1PcMonitor r, long delay) {
		try {
			if (delay <= 0) {
				_executor.execute(r);
				return null;
			}
			return _pcScheduler.schedule(r, delay, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException e) {
			return null;
		}
	}

	public ScheduledFuture<?> pcScheduleAtFixedRate(L1PcMonitor r,
			long initialDelay, long period) {
		return _pcScheduler.scheduleAtFixedRate(r, initialDelay, period,
				TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 试图取消对此任务的执行。 如果任务已完成、或已取消，或者由于某些其他原因而无法取消，则此尝试将失败。 当调用 cancel
	 * 时，如果调用成功，而此任务尚未启动，则此任务将永不运行。 如果任务已经启动，则 mayInterruptIfRunning
	 * 参数确定是否应该以试图停止任务的方式来中断执行此任务的线程。 此方法返回后，对 isDone() 的后续调用将始终返回 true。 如果此方法返回
	 * true，则对 isCancelled() 的后续调用将始终返回 true。
	 * 
	 * @param future
	 *            - 一个延迟的、结果可接受的操作，可将其取消。
	 * @param mayInterruptIfRunning
	 *            - 如果应该中断执行此任务的线程，则为 true；否则允许正在运行的任务运行完成
	 */
	public void cancel(final ScheduledFuture<?> future,
			boolean mayInterruptIfRunning) {
		try {
			future.cancel(mayInterruptIfRunning);

		} catch (final RejectedExecutionException e) {
			_log.info(e.getLocalizedMessage());
		}
	}

	// ThreadPoolManager 拜借
	private class PriorityThreadFactory implements ThreadFactory {
		private final int _prio;

		private final String _name;

		private final AtomicInteger _threadNumber = new AtomicInteger(1);

		private final ThreadGroup _group;

		public PriorityThreadFactory(String name, int prio) {
			_prio = prio;
			_name = name;
			_group = new ThreadGroup(_name);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
		 */
		public Thread newThread(Runnable r) {
			Thread t = new Thread(_group, r);
			t.setName(_name + "-" + _threadNumber.getAndIncrement());
			t.setPriority(_prio);
			return t;
		}
	}
}
