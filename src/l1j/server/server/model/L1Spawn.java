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
package l1j.server.server.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import l1j.server.Config;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.timecontroller.NpcSpawnBossTimer;
import l1j.server.server.types.Point;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class L1Spawn {
	private static final Log _log = LogFactory.getLog(L1Spawn.class);

	private final L1Npc _template;

	private int _id; // just to find this in the spawn table
	private String _location;
	private int _maximumCount;
	private int _npcid;
	private int _groupId;
	private int _locx;
	private int _locy;
	private int _randomx;
	private int _randomy;
	private int _locx1;
	private int _locy1;
	private int _locx2;
	private int _locy2;
	private int _heading;
	private int _minRespawnDelay;
	private int _maxRespawnDelay;
//	private final Constructor _constructor;
	private short _mapid;
	private boolean _respaenScreen;
	private int _movementDistance;
	private boolean _rest;
	private int _spawnType;
	private int _delayInterval;
	private Calendar _nextSpawnTime = null;
	private long _spawnInterval = 0;
	private int _existTime = 0;
	private HashMap<Integer, Point> _homePoint = null; // initspawn个

	private static Random _random = new Random();

	private String _name;

	private class SpawnTask implements Runnable {
		private int _spawnNumber;

		private SpawnTask(int spawnNumber) {
			_spawnNumber = spawnNumber;
		}

		@Override
		public void run() {
			doSpawn(_spawnNumber);
		}
	}

	public L1Spawn(final L1Npc mobTemplate) {
		this._template = mobTemplate;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		_name = name;
	}

	public short getMapId() {
		return _mapid;
	}

	public void setMapId(short _mapid) {
		this._mapid = _mapid;
	}

	public boolean isRespawnScreen() {
		return _respaenScreen;
	}

	public void setRespawnScreen(boolean flag) {
		_respaenScreen = flag;
	}

	public int getMovementDistance() {
		return _movementDistance;
	}

	public void setMovementDistance(int i) {
		_movementDistance = i;
	}

	public int getAmount() {
		return _maximumCount;
	}

	public int getId() {
		return _id;
	}

	public String getLocation() {
		return _location;
	}

	public int getLocX() {
		return _locx;
	}

	public int getLocY() {
		return _locy;
	}

	public int getNpcId() {
		return _npcid;
	}

	public int getHeading() {
		return _heading;
	}

	public int getRandomx() {
		return _randomx;
	}

	public int getRandomy() {
		return _randomy;
	}

	public int getLocX1() {
		return _locx1;
	}

	public int getLocY1() {
		return _locy1;
	}

	public int getLocX2() {
		return _locx2;
	}

	public int getLocY2() {
		return _locy2;
	}

	public int getMinRespawnDelay() {
		return _minRespawnDelay;
	}

	public int getMaxRespawnDelay() {
		return _maxRespawnDelay;
	}

	public void setAmount(int amount) {
		_maximumCount = amount;
	}

	public void setId(int id) {
		_id = id;
	}

	public void setLocation(String location) {
		_location = location;
	}

	public void setLocX(int locx) {
		_locx = locx;
	}

	public void setLocY(int locy) {
		_locy = locy;
	}

	public void setNpcid(int npcid) {
		_npcid = npcid;
	}

	public void setHeading(int heading) {
		_heading = heading;
	}

	public void setRandomx(int randomx) {
		_randomx = randomx;
	}

	public void setRandomy(int randomy) {
		_randomy = randomy;
	}

	public void setLocX1(int locx1) {
		_locx1 = locx1;
	}

	public void setLocY1(int locy1) {
		_locy1 = locy1;
	}

	public void setLocX2(int locx2) {
		_locx2 = locx2;
	}

	public void setLocY2(int locy2) {
		_locy2 = locy2;
	}

	public void setMinRespawnDelay(int i) {
		_minRespawnDelay = i;
	}

	public void setMaxRespawnDelay(int i) {
		_maxRespawnDelay = i;
	}

	private int calcRespawnDelay() {
		int respawnDelay = _minRespawnDelay * 1000;
		if (_delayInterval > 0) {
			respawnDelay += _random.nextInt(_delayInterval) * 1000;
		}
		return respawnDelay;
	}

	/**
	 * SpawnTask起动。
	 * @param spawnNumber L1Spawn管理番号。无何指定良。
	 */
	public void executeSpawnTask(int spawnNumber) {
		if (_nextSpawnTime != null) {
			this.doSpawn(spawnNumber);		
		}else {
			SpawnTask task = new SpawnTask(spawnNumber);
			GeneralThreadPool.getInstance().schedule(task, calcRespawnDelay());
		}
	}

	private boolean _initSpawn = false;

	private boolean _spawnHomePoint;

	public void init() {
		_delayInterval = _maxRespawnDelay - _minRespawnDelay;
		_initSpawn = true;
		// 持
		if (Config.SPAWN_HOME_POINT
				&& Config.SPAWN_HOME_POINT_COUNT <= getAmount()
				&& Config.SPAWN_HOME_POINT_DELAY >= getMinRespawnDelay()
				&& isAreaSpawn()) {
			_spawnHomePoint = true;
			_homePoint = new HashMap<Integer, Point>();
		}

		int spawnNum = 0;
		while (spawnNum < _maximumCount) {
			// spawnNum1～maxmumCount
			doSpawn(++spawnNum);
		}
		_initSpawn = false;
	}

	/**
	 * 场合、spawnNumber基spawn。
	 * 以外场合、spawnNumber未使用。
	 */
	protected void doSpawn(int spawnNumber) {
		L1NpcInstance mob = null;
		try {
			mob = NpcTable.getInstance().newNpcInstance(this._template);
			mob.setId(IdFactory.getInstance().nextId());
			
			int newlocx = getLocX();
			int newlocy = getLocY();
			int tryCount = 0;
			// 具备时间函数以时间为准
			
			if (0 <= getHeading() && getHeading() <= 7) {
				mob.setHeading(getHeading());
			} else {
				// heading值正
				mob.setHeading(5);
			}

			// npc召唤地图换位
			final int npcId = mob.getNpcTemplate().get_npcId();
			/*if ((npcId == 45488) && (this.getMapId() == 9)) { // 卡士伯
				mob.setMap((short) (this.getMapId() + _random.nextInt(2)));

			} else*/ if ((npcId == 45601) && (this.getMapId() == 11)) { // 死亡骑士
				mob.setMap((short) (this.getMapId() + _random.nextInt(3)));
			} else {
				mob.setMap(this.getMapId());
			}
			mob.setMovementDistance(getMovementDistance());
			mob.setRest(isRest());
			while (tryCount <= 50) {
				switch (getSpawnType()) {
				case SPAWN_TYPE_PC_AROUND: // PC周边涌
					if (!_initSpawn) { // 初期配置无条件通常spawn
						ArrayList<L1PcInstance> players = new ArrayList<L1PcInstance>();
						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
							if (getMapId() == pc.getMapId()) {
								players.add(pc);
							}
						}
						if (players.size() > 0) {
							L1PcInstance pc = players.get(_random.nextInt(players.size()));
							L1Location loc = pc.getLocation().randomLocation(PC_AROUND_DISTANCE, false);
							newlocx = loc.getX();
							newlocy = loc.getY();
							break;
						}
					}
					// PC通常出现方法
				default:
					if (isAreaSpawn()) { // 座标范围指定场合
						if (!_initSpawn && _spawnHomePoint) { // 元再出现场合
							Point pt = _homePoint.get(spawnNumber);
							if (pt != null){
								L1Location loc = new L1Location(pt, getMapId()).randomLocation(Config.SPAWN_HOME_POINT_RANGE,false);
								newlocx = loc.getX();
								newlocy = loc.getY();
							}else{
								newlocx = getLocX();
								newlocy = getLocY();
							}
						} else {
							int rangeX = getLocX2() - getLocX1();
							int rangeY = getLocY2() - getLocY1();
							newlocx = _random.nextInt(rangeX) + getLocX1();
							newlocy = _random.nextInt(rangeY) + getLocY1();
						}
						if (tryCount > 49) { // 出现位置决时locx,locy值
							newlocx = getLocX();
							newlocy = getLocY();
						}
					} else if (isRandomSpawn()) { // 座标值指定场合
						if (npcId == 71075) {
							int rnd = getRandomx();
							if (getRandomy()>getRandomx()) {
								rnd =  getRandomy();
							}
							L1Location loc = new L1Location(getLocX(),getLocY(), getMapId())
							.randomLocation(
									rnd,
									false);
							newlocx = loc.getX();
							newlocy = loc.getY();
						}else {
							newlocx = (getLocX() + ((int) (Math.random() * getRandomx()) - (int) (Math
									.random() * getRandomx())));
							newlocy = (getLocY() + ((int) (Math.random() * getRandomy()) - (int) (Math
									.random() * getRandomy())));
						}					
					} else { // 指定场合
						newlocx = getLocX();
						newlocy = getLocY();
					}
				}
				mob.setX(newlocx);
				mob.setHomeX(newlocx);
				mob.setY(newlocy);
				mob.setHomeY(newlocy);

				if (mob.getMap().isInMap(mob.getLocation())
						&& mob.getMap().isPassable(mob.getLocation())
						&& mob.getPassispeed() > 0 || mob.getPassispeed() == 0) {
					if (mob instanceof L1MonsterInstance) {
						if (isRespawnScreen()) {
							break;
						}
						L1MonsterInstance mobtemp = (L1MonsterInstance) mob;
						if (L1World.getInstance().getVisiblePlayer(mobtemp)
								.size() == 0) {
							break;
						}
						// 画面内PC居出现场合、3秒后直
						SpawnTask task = new SpawnTask(spawnNumber);
						GeneralThreadPool.getInstance().schedule(task, 3000L);
						return;
					}
				}
				tryCount++;
			}
			
			// 潜MOB一定确率地中潜状态
			// 飞MOB飞状态
			int npcid = mob.getNpcTemplate().get_npcId();
			//变更成switch 
			switch (npcid)
				{
					case 45061: case 45161: case 45181: case 45455: {
						Random random = new Random();
						int rnd = random.nextInt(3);
						if (1 > rnd) {
							mob.setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
							mob.setStatus(13);
						}
					}
					break;
					case 45045: case 45126: case 45134: case 45281: {
						Random random = new Random();
						int rnd = random.nextInt(3);
						if (1 > rnd) {
							mob.setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_SINK);
							mob.setStatus(4);
						}
					}
					break;
					case 45067: case 45264: case 45452: case 45090: case 45321: case 45445: {
						mob.setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
						mob.setStatus(4);
					}
					break;
					case 45681: {
						mob.setHiddenStatus(L1NpcInstance.HIDDEN_STATUS_FLY);
						mob.setStatus(11);
					}
					break;
				}
			//变更成switch  end
			mob.setSpawn(this);
			mob.setreSpawn(true);
			mob.setSpawnNumber(spawnNumber); // L1Spawn管理番号(使用)
			if (_initSpawn && _spawnHomePoint) { // 初期配置设定
				Point pt = new Point(mob.getX(), mob.getY());
				_homePoint.put(spawnNumber, pt); // 保存point再出现时使
			}
			if (_nextSpawnTime != null) {
				if (!isSpawnTime(mob)) {
					return;
				}
			}
/*			if (mob.getNpcId() ==  45617) {
				debug = true;
			}
			if (debug) {
				System.out.println("开始启动检测");
			}*/
			
			if (mob instanceof L1MonsterInstance) {
				if (mob.getMapId() == 666) {
					((L1MonsterInstance) mob).set_storeDroped(true);
				}
			}
			if (_existTime > 0) {
				// 存在时间(秒)
				mob.set_spawnTime(_existTime * 60);
			}
			L1World.getInstance().storeWorldObject(mob);
			L1World.getInstance().addVisibleObject(mob);

			if (mob instanceof L1MonsterInstance) {
				L1MonsterInstance mobtemp = (L1MonsterInstance) mob;
				if (!_initSpawn && mobtemp.getHiddenStatus() == 0) {
					mobtemp.onNpcAI(); // ＡＩ开始
				}
			}
			// 具备NPC队伍
			if (this.getGroupId() != 0) {
				// 召唤队伍成员
				L1MobGroupSpawn.getInstance().doSpawn(mob, this.getGroupId(),
						this.isRespawnScreen(), this._initSpawn);
			}
			this.setShuaXin(0);
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	public void setRest(boolean flag) {
		_rest = flag;
	}

	public boolean isRest() {
		return _rest;
	}

	private static final int SPAWN_TYPE_NORMAL = 0;
	private static final int SPAWN_TYPE_PC_AROUND = 1;

	private static final int PC_AROUND_DISTANCE = 30;

	private int getSpawnType() {
		return _spawnType;
	}

	public void setSpawnType(int type) {
		_spawnType = type;
	}

	private boolean isAreaSpawn() {
		return getLocX1() != 0 && getLocY1() != 0 && getLocX2() != 0
				&& getLocY2() != 0;
	}

	private boolean isRandomSpawn() {
		return getRandomx() != 0 || getRandomy() != 0;
	}
	//boolean debug = false;
	/**
	 * 抵达召唤时间
	 * @param npcTemp 
	 * @param next_spawn_time
	 * @return 
	 */
	private boolean isSpawnTime(L1NpcInstance npcTemp) {
		/*if (debug) {
			System.out.println("判断0");
		}*/
		if (_nextSpawnTime != null) {
			// 取得目前时间
			final Calendar cals = Calendar.getInstance();
			long nowTime = System.currentTimeMillis();
			cals.setTimeInMillis(nowTime);
			
			if (cals.after(_nextSpawnTime)) {
				/*if (debug) {
					System.out.println("判断1");
				}*/
				//System.out.println("抵达召唤时间");
				return true;
				
			} else {
				/*if (debug) {
					System.out.println("判断2");
				}*/
				if (NpcSpawnBossTimer.MAP.get(npcTemp) == null) {
					/*if (debug) {
						System.out.println("判断3");
					}*/
					long spawnTime = _nextSpawnTime.getTimeInMillis();
					// 加入等候清单(5秒误差补正)
					long spa = ((spawnTime - nowTime) / 1000) + 5;
					// 加入等候清单(5秒误差补正)
					NpcSpawnBossTimer.MAP.put(npcTemp, spa);
				}
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 下次召唤时间
	 * @return 
	 */
	public Calendar get_nextSpawnTime() {
		return _nextSpawnTime;
	}

	/**
	 * 下次召唤时间
	 * @param next_spawn_time
	 */
	public void set_nextSpawnTime(Calendar next_spawn_time) {
		_nextSpawnTime = next_spawn_time;
	}

	/**
	 * 差异时间(单位:分钟)
	 * @param spawn_interval
	 */
	public void set_spawnInterval(long spawn_interval) {
		_spawnInterval = spawn_interval;
	}

	/**
	 * 差异时间(单位:分钟)
	 * @param spawn_interval
	 * @return 
	 */
	public long get_spawnInterval() {
		return _spawnInterval;
	}

	/**
	 * 存在时间(单位:分钟)
	 * @param exist_time
	 */
	public void set_existTime(int exist_time) {
		_existTime = exist_time;
	}

	/**
	 * 队伍召唤编号
	 * @param i
	 */
	public void setGroupId(final int i) {
		this._groupId = i;
	}
	
	/**
	 * 队伍召唤编号
	 * @return
	 */
	public int getGroupId() {
		return this._groupId;
	}

	private long _nowtimeMillis = 1;
	public void setShuaXin(final long nowtimeMillis) {
		_nowtimeMillis = nowtimeMillis;
	}
	public long getShuaXin(){
		return _nowtimeMillis;
	}
}
