package l1j.server.server.datatables.storage;

import java.util.Calendar;
import java.util.List;

import l1j.server.server.model.L1Spawn;

/**
 * BOSS召唤资料
 *
 * @author dexc
 *
 */
public interface SpawnBossStorage {

	/**
	 * 初始化载入
	 */
	public void load();
	
	/**
	 * 更新资料库 下次召唤时间纪录
	 * @param id
	 * @param spawnTime
	 */
	public void upDateNextSpawnTime( final int id, final Calendar spawnTime);
	
	/**
	 * BOSS召唤列表中物件
	 * @param key
	 * @return
	 */
	public L1Spawn getTemplate(final int key);
	
	/**
	 * BOSS召唤列表中物件(NPCID)
	 * @param key
	 * @return
	 */
	public boolean isBoss(final int key);
	
	/**
	 * BOSS召唤列表中物件(NPCID)
	 * @return _bossId
	 */
	public List<Integer> bossIds();
}
