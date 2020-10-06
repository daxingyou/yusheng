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
package l1j.server.server.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ZHConterver;

import l1j.server.Config;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1GroundInventory;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1War;
import l1j.server.server.model.Instance.L1BiaoCheInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;//全NPC 
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.serverpackets.S_BlueMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.templates.L1Pc;
import l1j.server.server.templates.L1TopcTemp;
import l1j.server.server.types.Point;

public class L1World {
	private static final Log _log = LogFactory.getLog(L1World.class);


	private final ConcurrentHashMap<Integer, L1NpcInstance> _allNpcs;//全NPC 
	private final ConcurrentHashMap<String, L1PcInstance> _allPlayers;
	private final ConcurrentHashMap<Integer, L1PetInstance> _allPets;
	private final ConcurrentHashMap<Integer, L1SummonInstance> _allSummons;
	private final ConcurrentHashMap<Integer, L1Object> _allObjects;
	private final ConcurrentHashMap<Integer, L1Object>[] _visibleObjects;
	private final CopyOnWriteArrayList<L1War> _allWars;
	private final ConcurrentHashMap<String, L1Clan> _allClans;
	private final ConcurrentHashMap<String, L1Pc> _Pcs;
	
	private final ConcurrentHashMap<String, String> _worldNames;
	private final ConcurrentHashMap<Integer, L1BiaoCheInstance> _allBiaoChes;//全镖车 
	private final ConcurrentHashMap<Integer, L1ItemInstance> _allTimeItems;
	
	private int _weather = 4;

	private boolean _worldChatEnabled = true;

	private boolean _processingContributionTotal = false;
	
	private boolean _worldcotrol = false;

	private static final int MAX_MAP_ID = 8105;

	private static L1World _instance;

	private L1World() {
		_allNpcs = new ConcurrentHashMap<Integer, L1NpcInstance>(); //全NPC 
		_allPlayers = new ConcurrentHashMap<String, L1PcInstance>(); // 全
		_allPets = new ConcurrentHashMap<Integer, L1PetInstance>(); // 全
		_allSummons = new ConcurrentHashMap<Integer, L1SummonInstance>(); // 全
		_allObjects = new ConcurrentHashMap<Integer, L1Object>(); // 全(L1ItemInstance入、L1Inventory)
		_visibleObjects = new ConcurrentHashMap[MAX_MAP_ID + 1]; // 每(L1Inventory入、L1ItemInstance)
		_allWars = new CopyOnWriteArrayList<L1War>(); // 全战争
		_allClans = new ConcurrentHashMap<String, L1Clan>(); // 全(Online/Offline)
		_Pcs = new ConcurrentHashMap<String, L1Pc>(); // 全てのクラン(Online/Offlineどちらも)
		_worldNames = new ConcurrentHashMap<String, String>();
		_allBiaoChes = new ConcurrentHashMap<Integer, L1BiaoCheInstance>();
		for (int i = 0; i <= MAX_MAP_ID; i++) {
			_visibleObjects[i] = new ConcurrentHashMap<Integer, L1Object>();
		}
		_allTimeItems = new ConcurrentHashMap<Integer, L1ItemInstance>();
	}

	public static L1World getInstance() {
		if (_instance == null) {
			_instance = new L1World();
		}
		return _instance;
	}

	/**
	 * 全状态。<br>
	 * 、特殊目的以外呼出。
	 */
	public void clear() {
		_instance = new L1World();
	}

	public void storeWorldObject(L1Object object) {
		if (object == null) {
			throw new NullPointerException();
		}
		//清除上一次脱机 摆摊的数据
		if (object instanceof L1PcInstance){
			if (_allObjects.containsKey(object.getId())){
				final L1Object object2 = _allObjects.get(object.getId());
				if (object2 instanceof L1PcInstance){
					((L1PcInstance)object2).clearTuoJiShop();
				}
			}
		}
		//清除上一次脱机 摆摊的数据end
		_allObjects.put(object.getId(), object);
		//全NPC 
		if (object instanceof L1NpcInstance) {
			_allNpcs.put(object.getId(), (L1NpcInstance) object);
		}
		//全NPC  end
		if (object instanceof L1ItemInstance) {
			WorldItem.get().put(new Integer(object.getId()),
					(L1ItemInstance) object);
		}
		if (object instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) object;
			if (pc.isCrown()) {
				WorldCrown.get().put(new Integer(pc.getId()), pc);

			} else if (pc.isKnight()) {
				WorldKnight.get().put(new Integer(pc.getId()), pc);

			} else if (pc.isElf()) {
				WorldElf.get().put(new Integer(pc.getId()), pc);

			} else if (pc.isWizard()) {
				WorldWizard.get().put(new Integer(pc.getId()), pc);

			} else if (pc.isDarkelf()) {
				WorldDarkelf.get().put(new Integer(pc.getId()), pc);
				
			} else if (pc.isDragonKnight()) { // 增加龙骑士职业
				WorldDragonKnight.get().put(new Integer(pc.getId()), pc);
				
			} else if (pc.isIllusionist()) { // 增加幻术师职业
				WorldIllusionist.get().put(new Integer(pc.getId()), pc);

			} 
			_allPlayers.put(pc.getName(), pc);
			
			final String newName = ZHConterver.convert(pc.getName(), ZHConterver.TRADITIONAL);
			_worldNames.put(newName, pc.getName());
		}
		if (object instanceof L1PetInstance) {
			_allPets.put(object.getId(), (L1PetInstance) object);
		}
		if (object instanceof L1SummonInstance) {
			_allSummons.put(object.getId(), (L1SummonInstance) object);
		}
		if (object instanceof L1BiaoCheInstance){
			_allBiaoChes.put(object.getId(), (L1BiaoCheInstance)object);
		}
		if (object instanceof L1ItemInstance){
			final L1ItemInstance timeItem = (L1ItemInstance)object;
			if (timeItem.get_time() != null){
				_allTimeItems.put(object.getId(), timeItem);
			}
		}
	}

	public void removeWorldObject(L1Object object) {
		if (object == null) {
			throw new NullPointerException();
		}

		_allObjects.remove(object.getId());
		//全NPC 
		if (object instanceof L1NpcInstance) {
			_allNpcs.remove(object.getId());
		}
		//全NPC  end
		if (object instanceof L1ItemInstance) {
			WorldItem.get().remove(new Integer(object.getId()));
		}
		if (object instanceof L1PcInstance) {
			final L1PcInstance pc = (L1PcInstance) object;

			if (pc.isCrown()) {
				WorldCrown.get().remove(new Integer(pc.getId()));

			} else if (pc.isKnight()) {
				WorldKnight.get().remove(new Integer(pc.getId()));

			} else if (pc.isElf()) {
				WorldElf.get().remove(new Integer(pc.getId()));

			} else if (pc.isWizard()) {
				WorldWizard.get().remove(new Integer(pc.getId()));

			} else if (pc.isDarkelf()) {
				WorldDarkelf.get().remove(new Integer(pc.getId()));
				
			} else if (pc.isDragonKnight()) { // 增加龙骑士职业
				WorldDragonKnight.get().remove(new Integer(pc.getId()));
				
			} else if (pc.isIllusionist()) { // 增加幻术师职业
				WorldIllusionist.get().remove(new Integer(pc.getId()));

			} 
			_allPlayers.remove(pc.getName());
			
			final String newName = ZHConterver.convert(pc.getName(), ZHConterver.TRADITIONAL);
			_worldNames.remove(newName);
		}
		if (object instanceof L1PetInstance) {
			_allPets.remove(object.getId());
		}
		if (object instanceof L1SummonInstance) {
			_allSummons.remove(object.getId());
		}
		if (object instanceof L1BiaoCheInstance){
			_allBiaoChes.remove(object.getId());
		}
		if (object instanceof L1ItemInstance){
			final L1ItemInstance timeItem = (L1ItemInstance)object;
			if (timeItem.get_time() != null){
				_allTimeItems.remove(object.getId());
			}
		}
	}
	
	public String getNewName(final String text){
		if (_worldNames.containsKey(text)){
			return _worldNames.get(text);
		}
		return text;
	}

	public L1Object findObject(int oID) {
		return _allObjects.get(oID);
	}

	private Collection<L1BiaoCheInstance> _allBiaoCheValues;

	public Collection<L1BiaoCheInstance> getAllBiaoCheValues() {
		Collection<L1BiaoCheInstance> vs = _allBiaoCheValues;
		return (vs != null) ? vs : (_allBiaoCheValues = Collections
				.unmodifiableCollection(_allBiaoChes.values()));
	}
		
	// _allObjects
	private Collection<L1Object> _allValues;

	public Collection<L1Object> getObject() {
		Collection<L1Object> vs = _allValues;
		return (vs != null) ? vs : (_allValues = Collections
				.unmodifiableCollection(_allObjects.values()));
	}
	
	public L1Object[] getObjects() {
		  return _allObjects.values(). toArray(new L1Object[_allObjects.size()]);
	}

	public L1GroundInventory getInventory(int x, int y, short map) {
		int inventoryKey = ((x - 30000) * 10000 + (y - 30000)) * -1; // xy值使用

		Object object = _visibleObjects[map].get(inventoryKey);
		if (object == null) {
			return new L1GroundInventory(inventoryKey, x, y, map);
		} else {
			return (L1GroundInventory) object;
		}
	}

	public L1GroundInventory getInventory(L1Location loc) {
		return getInventory(loc.getX(), loc.getY(), (short) loc.getMap()
				.getId());
	}

	public void addVisibleObject(L1Object object) {
		if (object.getMapId() <= MAX_MAP_ID) {
			_visibleObjects[object.getMapId()].put(object.getId(), object);
		}
	}

	public void removeVisibleObject(L1Object object) {
		if (object.getMapId() <= MAX_MAP_ID) {
			_visibleObjects[object.getMapId()].remove(object.getId());
		}
	}

	public void moveVisibleObject(L1Object object, int newMap) // set_Map新Map呼
	{
		if (object == null) {
			return;
		}
		if (object.getMapId() != newMap) {
			if (object.getMapId() <= MAX_MAP_ID) {
				_visibleObjects[object.getMapId()].remove(object.getId());
			}
			if (newMap <= MAX_MAP_ID) {
				_visibleObjects[newMap].put(object.getId(), object);
			}
		}
	}

	private ConcurrentHashMap<Integer, Integer> createLineMap(Point src,
			Point target) {
		ConcurrentHashMap<Integer, Integer> lineMap = new ConcurrentHashMap<Integer, Integer>();

		/*
		 * http://www2.starcat.ne.jp/~fussy/algo/algo1-1.htm
		 */
		int E;
		int x;
		int y;
		int key;
		int i;
		int x0 = src.getX();
		int y0 = src.getY();
		int x1 = target.getX();
		int y1 = target.getY();
		int sx = (x1 > x0) ? 1 : -1;
		int dx = (x1 > x0) ? x1 - x0 : x0 - x1;
		int sy = (y1 > y0) ? 1 : -1;
		int dy = (y1 > y0) ? y1 - y0 : y0 - y1;

		x = x0;
		y = y0;
		/* 倾1以下场合 */
		if (dx >= dy) {
			E = -dx;
			for (i = 0; i <= dx; i++) {
				key = (x << 16) + y;
				lineMap.put(key, key);
				x += sx;
				E += 2 * dy;
				if (E >= 0) {
					y += sy;
					E -= 2 * dx;
				}
			}
			/* 倾1大场合 */
		} else {
			E = -dy;
			for (i = 0; i <= dy; i++) {
				key = (x << 16) + y;
				lineMap.put(key, key);
				y += sy;
				E += 2 * dx;
				if (E >= 0) {
					x += sx;
					E -= 2 * dy;
				}
			}
		}

		return lineMap;
	}

	public ArrayList<L1Object> getVisibleLineObjects(L1Object src,
			L1Object target) {
		ConcurrentHashMap<Integer, Integer> lineMap = createLineMap(src
				.getLocation(), target.getLocation());

		int map = target.getMapId();
		ArrayList<L1Object> result = new ArrayList<L1Object>();

		if (map <= MAX_MAP_ID) {
			for (L1Object element : _visibleObjects[map].values()) {
				if (element.equals(src)) {
					continue;
				}

				int key = (element.getX() << 16) + element.getY();
				if (lineMap.containsKey(key)) {
					result.add(element);
				}
			}
		}

		return result;
	}

	public ArrayList<L1Object> getVisibleBoxObjects(L1Object object,
			int heading, int width, int height) {
		int x = object.getX();
		int y = object.getY();
		int map = object.getMapId();
		L1Location location = object.getLocation();
		ArrayList<L1Object> result = new ArrayList<L1Object>();
		int headingRotate[] = { 6, 7, 0, 1, 2, 3, 4, 5 };
		double cosSita = Math.cos(headingRotate[heading] * Math.PI / 4);
		double sinSita = Math.sin(headingRotate[heading] * Math.PI / 4);

		if (map <= MAX_MAP_ID) {
			for (L1Object element : _visibleObjects[map].values()) {
				if (element.equals(object)) {
					continue;
				}
				if (map != element.getMapId()) {
					continue;
				}

				int distance = location.getTileLineDistance(element
						.getLocation());
				// 直线距离高、幅大场合、计算范围外
				if (distance > height && distance > width) {
					continue;
				}

				// object位置原点座标补正
				int x1 = element.getX() - x;
				int y1 = element.getY() - y;

				// Z轴回转角度0度。
				int rotX = (int) Math.round(x1 * cosSita + y1 * sinSita);
				int rotY = (int) Math.round(-x1 * sinSita + y1 * cosSita);

				int xmin = 0;
				int xmax = height;
				int ymin = -width;
				int ymax = width;

				// 奥行射程合直线距离判定变更。
				// if (rotX > xmin && rotX <= xmax && rotY >= ymin && rotY <= ymax) {
				if (rotX > xmin && distance <= xmax && rotY >= ymin
						&& rotY <= ymax) {
					result.add(element);
				}
			}
		}

		return result;
	}

	public ArrayList<L1Object> getVisibleObjects(L1Object object) {
		return getVisibleObjects(object, -1);
	}

	public ArrayList<L1Object> getVisibleObjects(L1Object object, int radius) {
		L1Map map = object.getMap();
		Point pt = object.getLocation();
		ArrayList<L1Object> result = new ArrayList<L1Object>();
		if (map.getId() <= MAX_MAP_ID) {
			for (L1Object element : _visibleObjects[map.getId()].values()) {
				if (element.equals(object)) {
					continue;
				}
				if (map != element.getMap()) {
					continue;
				}

				if (radius == -1) {
					if (pt.isInScreen(element.getLocation())) {
						result.add(element);
					}
				} else {
					if (pt.getTileLineDistance(element.getLocation()) <= radius) {
						result.add(element);
					}
				}
			}
		}

		return result;
	}

	public ArrayList<L1Object> getVisiblePoint(L1Location loc, int radius) {
		ArrayList<L1Object> result = new ArrayList<L1Object>();
		int mapId = loc.getMapId(); // 内呼重

		if (mapId <= MAX_MAP_ID) {
			for (L1Object element : _visibleObjects[mapId].values()) {
				if (mapId != element.getMapId()) {
					continue;
				}

				if (loc.getTileLineDistance(element.getLocation()) <= radius) {
					result.add(element);
				}
			}
		}

		return result;
	}

	//全NPC 
	public ArrayList<L1NpcInstance> getVisibleNpc(L1Object object) {
		return getVisibleNpc(object, -1);
	}
	//全NPC  end
	public ArrayList<L1PcInstance> getVisiblePlayer(L1Object object) {
		return getVisiblePlayer(object, -1);
	}

	//全NPC 
	public ArrayList<L1NpcInstance> getVisibleNpc(L1Object object, int radius) {
		int map = object.getMapId();
		Point pt = object.getLocation();
		ArrayList<L1NpcInstance> result = new ArrayList<L1NpcInstance>();

		for (L1NpcInstance element : _allNpcs.values()) {
			if (element.equals(object)) {
				continue;
			}

			if (map != element.getMapId()) {
				continue;
			}

			if (radius == -1) {
				if (pt.isInScreen(element.getLocation())) {
					result.add(element);
				}
			} else {
				if (pt.getTileLineDistance(element.getLocation()) <= radius) {
					result.add(element);
				}
			}
		}
		return result;
	}
	//全NPC  end
	
	public ArrayList<L1PcInstance> getVisiblePlayer(L1Object object, int radius) {
		int map = object.getMapId();
		Point pt = object.getLocation();
		ArrayList<L1PcInstance> result = new ArrayList<L1PcInstance>();

		for (L1PcInstance element : _allPlayers.values()) {
			if (element.equals(object)) {
				continue;
			}

			if (map != element.getMapId()) {
				continue;
			}

			if (radius == -1) {
				if (pt.isInScreen(element.getLocation())) {
					result.add(element);
				}
			} else {
				if (pt.getTileLineDistance(element.getLocation()) <= radius) {
					result.add(element);
				}
			}
		}
		return result;
	}

	public ArrayList<L1PcInstance> getVisiblePlayerExceptTargetSight(
			L1Object object, L1Object target) {
		int map = object.getMapId();
		Point objectPt = object.getLocation();
		Point targetPt = target.getLocation();
		ArrayList<L1PcInstance> result = new ArrayList<L1PcInstance>();

		for (L1PcInstance element : _allPlayers.values()) {
			if (element.equals(object)) {
				continue;
			}

			if (map != element.getMapId()) {
				continue;
			}

			if (Config.PC_RECOGNIZE_RANGE == -1) {
				if (objectPt.isInScreen(element.getLocation())) {
					if (!targetPt.isInScreen(element.getLocation())) {
						result.add(element);
					}
				}
			} else {
				if (objectPt.getTileLineDistance(element.getLocation()) <= Config.PC_RECOGNIZE_RANGE) {
					if (targetPt.getTileLineDistance(element.getLocation()) > Config.PC_RECOGNIZE_RANGE) {
						result.add(element);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * object认识范围取得
	 * @param object
	 * @return
	 */
	public ArrayList<L1PcInstance> getRecognizePlayer(L1Object object) {
		return getVisiblePlayer(object, Config.PC_RECOGNIZE_RANGE);
	}

	//全NPC 
	private Collection<L1NpcInstance> _allNpcValues;

	public Collection<L1NpcInstance> getAllNpcs() {
		Collection<L1NpcInstance> vs = _allNpcValues;
		return (vs != null) ? vs : (_allNpcValues = Collections
				.unmodifiableCollection(_allNpcs.values()));
	}
	//全NPC  end
	
	// _allPlayers
	private Collection<L1PcInstance> _allPlayerValues;

	public Collection<L1PcInstance> getAllPlayers() {
		Collection<L1PcInstance> vs = _allPlayerValues;
		return (vs != null) ? vs : (_allPlayerValues = Collections
				.unmodifiableCollection(_allPlayers.values()));
	}

	/**
	 * 指定名前取得。
	 * 
	 * @param name - 名(小文字大文字无视)
	 * @return 指定名前L1PcInstance。该存在场合null返。
	 */
	public L1PcInstance getPlayer(String name) {
		if (_allPlayers.containsKey(name)) {
			return _allPlayers.get(name);
		}
		for (L1PcInstance each : getAllPlayers()) {
			if (each.getName().equalsIgnoreCase(name)) {
				return each;
			}
		}
		return null;
	}

	// _allPets
	private Collection<L1PetInstance> _allPetValues;

	public Collection<L1PetInstance> getAllPets() {
		Collection<L1PetInstance> vs = _allPetValues;
		return (vs != null) ? vs : (_allPetValues = Collections
				.unmodifiableCollection(_allPets.values()));
	}

	// _allSummons
	private Collection<L1SummonInstance> _allSummonValues;

	public Collection<L1SummonInstance> getAllSummons() {
		Collection<L1SummonInstance> vs = _allSummonValues;
		return (vs != null) ? vs : (_allSummonValues = Collections
				.unmodifiableCollection(_allSummons.values()));
	}

	public final Map<Integer, L1Object> getAllVisibleObjects() {
		return _allObjects;
	}

	public final Map<Integer, L1Object>[] getVisibleObjects() {
		return _visibleObjects;
	}

	public final Map<Integer, L1Object> getVisibleObjects(int mapId) {
		return _visibleObjects[mapId];
	}

	public Object getRegion(Object object) {
		return null;
	}

	public void addWar(L1War war) {
		if (!_allWars.contains(war)) {
			_allWars.add(war);
		}
	}

	public void removeWar(L1War war) {
		if (_allWars.contains(war)) {
			_allWars.remove(war);
		}
	}

	// _allWars
	private List<L1War> _allWarList;

	public List<L1War> getWarList() {
		List<L1War> vs = _allWarList;
		return (vs != null) ? vs : (_allWarList = Collections
				.unmodifiableList(_allWars));
	}

	public void storeClan(L1Clan clan) {
		L1Clan temp = getClan(clan.getClanName());
		if (temp == null) {
			_allClans.put(clan.getClanName(), clan);
		}
	}

	public void removeClan(L1Clan clan) {
		L1Clan temp = getClan(clan.getClanName());
		if (temp != null) {
			_allClans.remove(clan.getClanName());
		}
	}

	public L1Clan getClan(String clan_name) {
		return _allClans.get(clan_name);
	}

	// _allClans
	private Collection<L1Clan> _allClanValues;

	public Collection<L1Clan> getAllClans() {
		Collection<L1Clan> vs = _allClanValues;
		return (vs != null) ? vs : (_allClanValues = Collections
				.unmodifiableCollection(_allClans.values()));
	}
	
	public void setControl(boolean flg) {
		_worldcotrol = flg;
	}

	public boolean getControl() {
		return _worldcotrol;
	}

	public void setWeather(int weather) {
		_weather = weather;
	}

	public int getWeather() {
		return _weather;
	}

	public void set_worldChatElabled(boolean flag) {
		_worldChatEnabled = flag;
	}

	public boolean isWorldChatElabled() {
		return _worldChatEnabled;
	}

	public void setProcessingContributionTotal(boolean flag) {
		_processingContributionTotal = flag;
	}

	public boolean isProcessingContributionTotal() {
		return _processingContributionTotal;
	}

	/**
	 * 上存在全送信。
	 * 
	 * @param packet
	 *            送信表ServerBasePacket。
	 */
	public void broadcastPacketToAll(ServerBasePacket packet) {
//		_log.info("players to notify : " + getAllPlayers().size());
		boolean isBlue = false;
		if (packet instanceof S_BlueMessage){
			isBlue = true;
		}
		for (L1PcInstance pc : getAllPlayers()) {
			if (isBlue && !pc.isShowBlue()){
				continue;
			}
			pc.sendPackets(packet);
		}
	}

	/**
	 * 上存在全送信。
	 * 
	 * @param message
	 *            送信
	 */
	public void broadcastServerMessage(String message) {
		broadcastPacketToAll(new S_SystemMessage(message));
	}

	//个人仓库
	public void storePc(L1Pc pc) {
		L1Pc temp = getPc(pc.getAccountName());
		if (temp == null) {
			_Pcs.put(pc.getAccountName(), pc);
		}
	}

	public void removePc(L1Pc pc) {
		L1Pc temp = getPc(pc.getAccountName());
		if (temp != null) {
			_Pcs.remove(pc.getAccountName());
		}
	}

	public L1Pc getPc(String account_name) {
		return _Pcs.get(account_name);
	}

	private List<L1TopcTemp> _topcList = new ArrayList<L1TopcTemp>();
	
	public void addTopcItem(final String name,final int level){
		_topcList.add(new L1TopcTemp(name,level));
	}
	public void clearTopcItem(){
		_topcList.clear();
	}
	public int getTopcItemCount(){
		return _topcList.size();
	}
	public String getTopcItem(final int index){
		if (index >= _topcList.size()){
			return "";
		}
		if (index < 0){
			return "";
		}
		return _topcList.get(index).getName() + "  [" + _topcList.get(index).getLevel() + "]";
	}
	public String getTopcItemName(final int index){
		if (index >= _topcList.size()){
			return "";
		}
		if (index < 0){
			return "";
		}
		return _topcList.get(index).getName();
	}
	private Collection<L1Pc> _PcValues;

	public Collection<L1Pc> getAllPcs() {
		Collection<L1Pc> vs = _PcValues;
		return (vs != null) ? vs : (_PcValues = Collections
				.unmodifiableCollection(_Pcs.values()));
	}
//	 _仓库
	
	public L1PetInstance getPet(final Integer key) {
		return _allPets.get(key);
	}

	private Collection<L1ItemInstance> _TimeItems;
	
	public Collection<L1ItemInstance> getAllTimeItems() {
		Collection<L1ItemInstance> vs = _TimeItems;
		return (vs != null) ? vs : (_TimeItems = Collections
				.unmodifiableCollection(_allTimeItems.values()));
	}
}