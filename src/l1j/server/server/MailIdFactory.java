package l1j.server.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

public class MailIdFactory {

	private static final int FIRST_ID = 1024;

	private static final Log _log = LogFactory.getLog(MailIdFactory.class);

	private static MailIdFactory _instance;

	// 同步层次
	private Object _monitor;

	private AtomicInteger _nextId;

	public static MailIdFactory get() {
		if (_instance == null) {
			_instance = new MailIdFactory();
			// _firstID = 0xf4240;// 1000000
		}
		return _instance;
	}

	public MailIdFactory() {
		_monitor = new Object();
	}

	/**
	 * 以原子方式将当前值加 1
	 * 
	 * @return
	 */
	public int nextId() {
		synchronized (_monitor) {
			return _nextId.getAndIncrement();
		}
	}

	/**
	 * 获取当前值
	 * 
	 * @return
	 */
	public int maxId() {
		synchronized (_monitor) {
			return _nextId.get();
		}
	}

	/**
	 * 取回资料库中已用最大编号
	 */
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			cn = L1DatabaseFactory.getInstance().getConnection();
			ps = cn.prepareStatement("select * from mail order by id desc limit 1");
			rs = ps.executeQuery();

			int id = 0;
			if (rs.next()) {
				id = rs.getInt("id");
			}
			if (id < FIRST_ID) {
				id = FIRST_ID;
			}
			_nextId = new AtomicInteger(id);
			_log.info("载入已用最大邮件id编号: " + id + "(" + timer.get() + "ms)");

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}
}
