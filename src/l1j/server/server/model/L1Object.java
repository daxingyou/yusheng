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

import java.io.Serializable;

import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;

// Referenced classes of package l1j.server.server.model:
// L1PcInstance, L1Character

/**
 * 上存在全
 */
public class L1Object implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 存在ID返
	 * 
	 * @return ID
	 */
	public short getMapId() {
		return (short) _loc.getMap().getId();
	}

	/**
	 * 存在ID设定
	 * 
	 * @param mapId
	 *            ID
	 */
	public void setMap(short mapId) {
		_loc.setMap(L1WorldMap.getInstance().getMap(mapId));
		_powerloc.setMap(L1WorldMap.getInstance().getPowerMap(mapId));
	}

	/**
	 * 存在保持L1Map返
	 * 
	 */
	public L1Map getMap() {
		return _loc.getMap();
	}

	/**
	 * 存在设定
	 * 
	 * @param map
	 *            存在保持L1Map
	 */
	public void setMap(L1Map map) {
		if (map == null) {
			throw new NullPointerException();
		}
		_loc.setMap(map);
		_powerloc.setMap(L1WorldMap.getInstance().getPowerMap((short)_loc.getMapId()));
	}
	
	/**
	 * 存在ID返
	 * 
	 * @return ID
	 */
	public short getPowerMapId() {
		return (short) _powerloc.getMap().getId();
	}

	/**
	 * 存在ID设定
	 * 
	 * @param mapId
	 *            ID
	 */
/*	public void setPowerMap(short mapId) {
		_powerloc.setMap(L1WorldMap.getInstance().getPowerMap(mapId));
	}*/

	/**
	 * 存在保持L1Map返
	 * 
	 */
	public L1Map getPowerMap() {
		return _powerloc.getMap();
	}

	/**
	 * 存在设定
	 * 
	 * @param map
	 *            存在保持L1Map
	 */
/*	public void setPowerMap(L1Map map) {
		if (map == null) {
			throw new NullPointerException();
		}
		_powerloc.setMap(map);
	}*/

	/**
	 * 一意识别ID返
	 * 
	 * @return ID
	 */
	public int getId() {
		return _id;
	}

	/**
	 * 一意识别ID设定
	 * 
	 * @param id
	 *            ID
	 */
	public void setId(int id) {
		_id = id;
	}

	/**
	 * 存在座标X值返
	 * 
	 * @return 座标X值
	 */
	public int getX() {
		return _loc.getX();
	}

	/**
	 * 存在座标X值设定
	 * 
	 * @param x
	 *            座标X值
	 */
	public void setX(int x) {
		_loc.setX(x);
		_powerloc.setX(x);
	}

	/**
	 * 存在座标Y值返
	 * 
	 * @return 座标Y值
	 */
	public int getY() {
		return _loc.getY();
	}

	/**
	 * 存在座标Y值设定
	 * 
	 * @param y
	 *            座标Y值
	 */
	public void setY(int y) {
		_loc.setY(y);
		_powerloc.setY(y);
	}

	private L1Location _loc = new L1Location();

	/**
	 * 存在位置保持、L1Location参照返。
	 * 
	 * @return 座标保持、L1Location参照
	 */
	public L1Location getLocation() {
		return _loc;
	}

	public void setLocation(L1Location loc) {
		_loc.setX(loc.getX());
		_loc.setY(loc.getY());	
		_loc.setMap(loc.getMapId());
		_powerloc.setX(loc.getX());
		_powerloc.setY(loc.getY());
		_powerloc.setPowerMap(loc.getMapId());
	}

	public void setLocation(int x, int y, int mapid) {
		_loc.setX(x);
		_loc.setY(y);
		_loc.setMap(mapid);
		_powerloc.setX(x);
		_powerloc.setY(y);
		_powerloc.setPowerMap(mapid);
	}
	
	/**
	 * 存在座标X值返
	 * 
	 * @return 座标X值
	 */
	public int getPowerX() {
		return _powerloc.getX();
	}

	/**
	 * 存在座标X值设定
	 * 
	 * @param x
	 *            座标X值
	 */
/*	public void setPowerX(int x) {
		_powerloc.setX(x);
	}*/

	/**
	 * 存在座标Y值返
	 * 
	 * @return 座标Y值
	 */
	public int getPowerY() {
		return _powerloc.getY();
	}

	/**
	 * 存在座标Y值设定
	 * 
	 * @param y
	 *            座标Y值
	 */
/*	public void setPowerY(int y) {
		_powerloc.setY(y);
	}*/

	private L1Location _powerloc = new L1Location();

	/**
	 * 存在位置保持、L1Location参照返。
	 * 
	 * @return 座标保持、L1Location参照
	 */
	public L1Location getPowerLocation() {
		return _powerloc;
	}

/*	public void setPowerLocation(L1Location loc) {
		_powerloc.setX(loc.getX());
		_powerloc.setY(loc.getY());
		_powerloc.setMap(loc.getMapId());
	}

	public void setPowerLocation(int x, int y, int mapid) {
		_powerloc.setX(x);
		_powerloc.setY(y);
		_powerloc.setMap(mapid);
	}*/

	/**
	 * 指定直线距离返。
	 */
	public double getLineDistance(L1Object obj) {
		return this.getLocation().getLineDistance(obj.getLocation());
	}

	/**
	 * 指定直线数返。
	 */
	public int getTileLineDistance(L1Object obj) {
		return this.getLocation().getTileLineDistance(obj.getLocation());
	}

	/**
	 * 指定数返。
	 */
	public int getTileDistance(L1Object obj) {
		return this.getLocation().getTileDistance(obj.getLocation());
	}

	/**
	 * 画面内入(认识)际呼出。
	 * 
	 * @param perceivedFrom
	 *            认识PC
	 */
	public void onPerceive(L1PcInstance perceivedFrom) {
	}

	/**
	 * 发生际呼出
	 * 
	 * @param actionFrom
	 *            起PC
	 */
	public void onAction(L1PcInstance actionFrom) {
	}

	/**
	 * 话呼出
	 * 
	 * @param talkFrom
	 *            话PC
	 */
	public void onTalkAction(L1PcInstance talkFrom) {
	}

	private int _id = 0;
}
