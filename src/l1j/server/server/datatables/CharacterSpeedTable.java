package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.templates.L1CharacterSpeed;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

public class CharacterSpeedTable {
	private static final Log _log = LogFactory.getLog(CharacterSpeedTable.class);

	private static CharacterSpeedTable _instance;

	public static CharacterSpeedTable get() {
		if (_instance == null) {
			_instance = new CharacterSpeedTable();
		}
		return _instance;
	}

	/** 玩家限速清单 */
	public static final Map<String, L1CharacterSpeed> SPEEDMAP = new ConcurrentHashMap<String, L1CharacterSpeed>();

	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {

			cn = L1DatabaseFactory.getInstance().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `account_speed`");

			rs = ps.executeQuery();

			while (rs.next()) {
				String key = rs.getString("account");
				int attackspeed = rs.getInt("attackspeed");
				int movespeed = rs.getInt("movespeed");
				int injustice = rs.getInt("Injustice");
				int justice = rs.getInt("justice");
				L1CharacterSpeed characterSpeed = new L1CharacterSpeed();
				characterSpeed.setAttackSpeed(attackspeed);
				characterSpeed.setMoveSpeed(movespeed);
				characterSpeed.setInjustice(injustice);
				characterSpeed.setJustice(justice);
				SPEEDMAP.put(key, characterSpeed);
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("载入帐号限速资料数量: " + SPEEDMAP.size() + "(" + timer.get()
				+ "ms)");
	}

	public L1CharacterSpeed getIpcount(String key) {
		return SPEEDMAP.get(key);
	}

	public void reload() {
		SPEEDMAP.clear();
		load();	
	}
}
