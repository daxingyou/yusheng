package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 绝对还原设定(开新服专用)
 */
public class DBClearAllUtil {

	private static final Log _log = LogFactory.getLog(DBClearAllUtil.class);

	@SuppressWarnings("resource")
	public  void start() {
		try {
			final PerformanceTimer timer = new PerformanceTimer();
			Connection cn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				
				cn = L1DatabaseFactory.getInstance().getConnection();
				System.out.print("删除玩家帐号.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `accounts`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除封锁纪录.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `ban_ip`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除金币拍卖资料.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `character_adena_trade`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除物品祝福资料.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `character_blessenchant`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除角色记忆坐标表.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `character_bookconfig`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除玩家好友.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `character_buddys`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除玩家状态.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `character_buff`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除玩家银行.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `character_cns`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除快速键纪录.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `character_config`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除角色拍卖资料.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `character_failureenchant`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除玩家背包.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `character_items`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除玩家任务.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `character_quests`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除玩家技能...");
				ps = cn.prepareStatement("TRUNCATE TABLE `character_skills`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除记忆座标.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `character_teleport`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除武防凹槽.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `character_trade`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除个人仓库.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `character_warehouse`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除玩家资料.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `characters`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除血盟资料.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `clan_data`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("删除血盟仓库.....");
				ps = cn.prepareStatement("TRUNCATE TABLE `clan_warehouse`");
				ps.execute();
				System.out.println("ok!");

				System.out.print("更新首爆数据.....");
				ps = cn.prepareStatement("UPDATE server_shoubao SET byObjId=?,byName=?");
				ps.setInt(1, 0);
				ps.setString(2, "");
				ps.execute();
				System.out.println("ok!");

				System.out.print("更新首杀数据.....");
				ps = cn.prepareStatement("UPDATE server_shousha SET byObjId=?,byName=?");
				ps.setInt(1, 0);
				ps.setString(2, "");
				ps.execute();
				System.out.println("ok!");


			} catch (final SQLException e) {
				_log.error(e.getLocalizedMessage(), e);
			} finally {
				SQLUtil.close(ps);
				SQLUtil.close(cn);
				// 重新设置关闭
				update();
			}
		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}

	}

	/**
	 * 重新设置关闭
	 */
	public void update(){
		Connection co = null;
		PreparedStatement ps = null;
		try {
			co = L1DatabaseFactory.getInstance().getConnection();
			ps = co.prepareStatement("UPDATE `newDistrictSet` SET `state`=? WHERE `parameter`=? and `id` = 1 ");
			ps.setString(1, "false");
			ps.setString(2, "DBClearAll");
			ps.execute();
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
		}
	}

	/**
	 * 获取是否清除数据库表的状态
	 * @return true为清空用户表
	 */
	public Boolean getDBClearAllState() {
		Connection co = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Boolean newDistrictSetStatus = false;
		try {
			co = L1DatabaseFactory.getInstance().getConnection();
			ps = co.prepareStatement("select `state` from `newDistrictSet` where `id` = 1");
			rs = ps.executeQuery();
			while (rs.next()){
				newDistrictSetStatus = rs.getBoolean(1);
			}
		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);
		} finally {
			SQLUtil.close(ps);
			SQLUtil.close(co);
			SQLUtil.close(rs);
		}
		return newDistrictSetStatus;
	}
}
