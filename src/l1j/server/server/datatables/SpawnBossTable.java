package l1j.server.server.datatables;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.storage.SpawnBossStorage;
import l1j.server.server.model.L1Spawn;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.PerformanceTimer;
import l1j.server.server.utils.SQLUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * BOSS召唤资料
 *
 * @author dexc
 *
 */
public class SpawnBossTable implements SpawnBossStorage {

	private static final Log _log = LogFactory.getLog(SpawnBossTable.class);

	// 召唤表清单(召唤ID / 召唤执行项)
	private static final HashMap<Integer, L1Spawn> _bossSpawnTable = new HashMap<Integer, L1Spawn>();
	
	//private static final HashMap<Integer, L1SpawnBoss> _bossSpawnTableX = new HashMap<Integer, L1SpawnBoss>();
	
	// BOSS ID
	private ArrayList<Integer> _bossId = new ArrayList<Integer>();

	private Calendar timestampToCalendar(final Timestamp ts) {
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(ts.getTime());
		return cal;
	}

	/**
	 * 初始化载入
	 */
	@Override
	public void load() {
		final PerformanceTimer timer = new PerformanceTimer();
		java.sql.Connection co = null;
		PreparedStatement pm = null;
		ResultSet rs = null;
		try {

			co = L1DatabaseFactory.getInstance().getConnection();
			pm = co.prepareStatement("SELECT * FROM `spawnlist_boss`");
			rs = pm.executeQuery();

			while (rs.next()) {
				final int id = rs.getInt("id");
				final int npcTemplateId = rs.getInt("npc_templateid");
				final L1Npc temp1 = NpcTable.getInstance().getTemplate(npcTemplateId);

				if (temp1 == null) {
					_log.info("BOSS召唤MOB编号: " + npcTemplateId + " 不存在资料库中!");
					
				} else {
					int count = rs.getInt("count");
					
					int group_id = rs.getInt("group_id");
					int locx1 = rs.getInt("locx1");
					int locy1 = rs.getInt("locy1");
					int locx2 = rs.getInt("locx2");
					int locy2 = rs.getInt("locy2");
					int heading = rs.getInt("heading");
					int mapid = rs.getShort("mapid");

					Timestamp time = rs.getTimestamp("next_spawn_time");
					// 下次召唤时间
					Calendar next_spawn_time = null;
					if (time != null) {
						// 下次召唤时间
						next_spawn_time = this.timestampToCalendar(time);
					}
					
					int spawn_interval = rs.getInt("spawn_interval");// 差异时间(单位:分钟)
					int exist_time = rs.getInt("exist_time");// 存在时间(单位:分钟)
					int movement_distance = rs.getInt("movement_distance");//行动距离
					
					final L1Spawn spawnDat = new L1Spawn(temp1);
					spawnDat.setId(id);
					spawnDat.setAmount(count);
					spawnDat.setGroupId(group_id);
					spawnDat.setNpcid(npcTemplateId);
					// 加入BOSS ID 清单
					_bossId.add(new Integer(npcTemplateId));
					
					// BOSS 具备变身
					if (temp1.getTransformId() != -1) {
						// 加入BOSS ID 清单
						_bossId.add(new Integer(temp1.getTransformId()));
						
						L1Npc temp2 = NpcTable.getInstance().getTemplate(temp1.getTransformId());
						if (temp2.getTransformId() != -1) {
							// 加入BOSS ID 清单
							_bossId.add(new Integer(temp2.getTransformId()));
						}
					}
					
					if ((locx2 == 0) && (locy2 == 0)) {
						spawnDat.setLocX(locx1);
						spawnDat.setLocY(locy1);
						spawnDat.setLocX1(0);
						spawnDat.setLocY1(0);
						spawnDat.setLocX2(0);
						spawnDat.setLocY2(0);

					} else {
						spawnDat.setLocX(locx1);
						spawnDat.setLocY(locy1);
						spawnDat.setLocX1(locx1);
						spawnDat.setLocY1(locy1);
						spawnDat.setLocX2(locx2);
						spawnDat.setLocY2(locy2);
					}

					if ((locx2 < locx1) && locx2 != 0) {
						_log.info("spawnlist_boss : locx2 < locx1:"+id);
						continue;
					}

					if ((locy2 < locy1) && locy2 != 0) {
						_log.info("spawnlist_boss : locy2 < locy1:"+id);
						continue;
					}
					spawnDat.setHeading(heading);
					spawnDat.setMapId((short) mapid);
					
					spawnDat.setMinRespawnDelay(10);
					
					spawnDat.setMovementDistance(movement_distance);
					
					spawnDat.setName(temp1.get_name());
					
					spawnDat.set_nextSpawnTime(next_spawn_time);
					spawnDat.set_spawnInterval(spawn_interval);
					spawnDat.set_existTime(exist_time);
					spawnDat.setSpawnType(0);

					if ((count > 1) && (spawnDat.getLocX1() == 0)) {
						final int range = Math.min(count * 6, 30);
						spawnDat.setLocX1(spawnDat.getLocX() - range);
						spawnDat.setLocY1(spawnDat.getLocY() - range);
						spawnDat.setLocX2(spawnDat.getLocX() + range);
						spawnDat.setLocY2(spawnDat.getLocY() + range);
					}
					spawnDat.setRespawnScreen(true);
					spawnDat.init();
					
					_bossSpawnTable.put(new Integer(spawnDat.getId()), spawnDat);
				}
			}

		} catch (final SQLException e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pm);
			SQLUtil.close(co);
		}
		_log.info("载入BOSS召唤资料数量: " + _bossSpawnTable.size() + "(" + timer.get() + "ms)");
	}
	
	/**
	 * 更新资料库 下次召唤时间纪录
	 * @param id
	 * @param spawnTime
	 */
	@Override
	public void upDateNextSpawnTime( final int id, final Calendar spawnTime) {
		//_log.info("更新BOSS召唤时间!"+id);
		Connection co = null;
		PreparedStatement pm = null;
		try {

			co = L1DatabaseFactory.getInstance().getConnection();
			pm = co.prepareStatement("UPDATE `spawnlist_boss` SET `next_spawn_time`=? WHERE `id`=?");

			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			final String fm = sdf.format(spawnTime.getTime());
			
			int i = 0;
			pm.setString(++i, fm);
			pm.setInt(++i, id);
			pm.execute();

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			SQLUtil.close(pm);
			SQLUtil.close(co);
		}
	}
	
	public static Map<Integer, L1Spawn> get_bossSpawnTable() {
		return _bossSpawnTable;
	}
	
	/**
	 * BOSS召唤列表中物件
	 * @param key
	 * @return
	 */
	@Override
	public L1Spawn getTemplate(final int key) {
		return _bossSpawnTable.get(key);
	}
	
	/**
	 * BOSS召唤列表中物件(NPCID)
	 * @param key
	 * @return
	 */
	@Override
	public boolean isBoss(final int key) {
		return _bossId.contains(key);
	}
	
	/**
	 * BOSS召唤列表中物件(NPCID)
	 * @return _bossId
	 */
	@Override
	public List<Integer> bossIds() {
		return this._bossId;
	}
}
