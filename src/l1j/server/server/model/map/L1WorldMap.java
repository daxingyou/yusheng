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
package l1j.server.server.model.map;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.MapReader;
import l1j.server.server.utils.PerformanceTimer;

public class L1WorldMap {
	private static final Log _log = LogFactory.getLog(L1WorldMap.class);


	private static L1WorldMap _instance;
	private Map<Integer, L1Map> _maps;
	
	private Map<Integer, L1Map> _powermaps;

	public static L1WorldMap getInstance() {
		if (_instance == null) {
			_instance = new L1WorldMap();
		}
		return _instance;
	}

	private L1WorldMap() {
		PerformanceTimer timer = new PerformanceTimer();
		System.out.print("loading map...");

		MapReader in = MapReader.getDefaultReader();

		try {
			_maps = in.read();
			_powermaps = in.readPower();
			if (_maps == null) {
				throw new RuntimeException("地图档读取失败");
			}
			if (_powermaps == null) {
				throw new RuntimeException("特殊地图档读取失败");
			}
		} catch (Exception e) {
			// 复扫不能
			_log.error(e.getLocalizedMessage(), e);
		}

		System.out.println("OK! " + timer.get() + "ms");
	}

	/**
	 * 指定情报保持L1Map返。
	 * 
	 * @param mapId
	 *            ID
	 * @return 情报保持、L1Map。
	 */
	public L1Map getMap(short mapId) {
		L1Map map = _maps.get((int) mapId);
		if (map == null) { // 情报无
			map = L1Map.newNull(); // 何Map返。
		}
		return map;
	}
	
	/**
	 * 指定情报保持L1Map返。
	 * 
	 * @param mapId
	 *            ID
	 * @return 情报保持、L1Map。
	 */
	public L1Map getPowerMap(short mapId) {
		L1Map map = _powermaps.get((int) mapId);
		if (map == null) { // 情报无
			map = L1Map.newNull(); // 何Map返。
		}
		return map;
	}
}
