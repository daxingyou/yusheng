package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.storage.CharBookConfigStorage;
import l1j.server.server.templates.L1BookConfig;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

/** 记忆座标设置纪录 */
public class CharBookConfigTable implements CharBookConfigStorage {
	private static final Log _log = LogFactory.getLog(CharBookConfigTable.class);
	private static final Map<Integer, L1BookConfig> _Config_List = new HashMap<Integer, L1BookConfig>();

	/** 载入 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		Connection cn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			cn = L1DatabaseFactory.getInstance().getConnection();
			ps = cn.prepareStatement("SELECT * FROM `character_bookconfig`");
			rs = ps.executeQuery();

			while (rs.next()) {
				final int objId = rs.getInt("obj_Id");

				// 检查该资料所属是否遗失
				if (CharacterTable.getInstance().isChar(objId)) {
					L1BookConfig cf = new L1BookConfig();
					cf.set_objId(objId);
					cf.set_data(rs.getBytes("data"));

					_Config_List.put(objId, cf);
				} else {
					// 资料遗失删除记录
					this.delete(objId);
				}
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
		_log.info("载入快捷座标清单资料数量: " + _Config_List.size() + "(" + timer.get()
				+ "ms)");
	}

	/** 删除遗失资料 */
	private void delete(final int objId) {
		Connection cn = null;
		PreparedStatement ps = null;

		try {
			cn = L1DatabaseFactory.getInstance().getConnection();
			ps = cn.prepareStatement("DELETE FROM `character_bookconfig` WHERE `obj_Id`=?");
			ps.setInt(1, objId);
			ps.execute();
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/** 传回角色 L1BookConfig */
	@Override
	public L1BookConfig get(final int objId) {
		return _Config_List.get(objId);
	}

	/** 新建角色 L1BookConfig */
	@Override
	public void storeCharacterBookConfig(final int objId, final byte[] data) {
		final L1BookConfig cf = new L1BookConfig();
		cf.set_objId(objId);
		cf.set_data(data);
		_Config_List.put(objId, cf);
		Connection cn = null;
		PreparedStatement ps = null;

		try {
			cn = L1DatabaseFactory.getInstance().getConnection();
			ps = cn.prepareStatement("INSERT INTO `character_bookconfig` SET `obj_Id`=?,`data`=?");
			ps.setInt(1, cf.get_objId());
			ps.setBytes(2, cf.get_data());
			ps.execute();
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}

	/** 更新角色 L1BookConfig */
	@Override
	public void updateCharacterConfig(final int objId, final byte[] data) {
		final L1BookConfig config = _Config_List.get(objId);
		config.set_objId(objId);
		config.set_data(data);
		Connection cn = null;
		PreparedStatement ps = null;

		try {
			cn =L1DatabaseFactory.getInstance().getConnection();
			ps = cn.prepareStatement("UPDATE `character_bookconfig` SET `data`=? WHERE `obj_Id`=?");
			ps.setBytes(1, config.get_data());
			ps.setInt(2, config.get_objId());
			ps.execute();
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(cn);
		}
	}
}