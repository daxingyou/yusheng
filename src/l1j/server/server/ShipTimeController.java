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
package l1j.server.server;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.gametime.L1GameTimeClock;
import l1j.server.server.world.L1World;

public class ShipTimeController implements Runnable {

	private static ShipTimeController _instance;

	public static ShipTimeController getInstance() {
		if (_instance == null) {
			_instance = new ShipTimeController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			while (true) {
				checkShipTime(); // 船到着时间
				Thread.sleep(5000);
			}
		} catch (Exception e1) {
		}
	}

	private void checkShipTime() {
		long servertime = L1GameTimeClock.getInstance().getGameTime()
				.getSeconds();
		long nowtime = servertime % 86400;
		// 到达港口时间修正 
		if (nowtime >= 90 * 60 && nowtime < 179 * 60 // 1.30~3
				|| nowtime >= 270 * 60 && nowtime < 359 * 60 // 4.30~6
				|| nowtime >= 450 * 60 && nowtime < 539 * 60 // 7.30~9
				|| nowtime >= 630 * 60 && nowtime < 719 * 60 // 10.30~12
				|| nowtime >= 810 * 60 && nowtime < 899 * 60 // 13.30~15
				|| nowtime >= 990 * 60 && nowtime < 1079 * 60 // 16.30~18
				|| nowtime >= 1170 * 60 && nowtime < 1259 * 60 // 19.30~21
				|| nowtime >= 1350 * 60 && nowtime < 1439 * 60 // 22.30~24
			) {
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				if (pc.getMapId() == 83) {
					pc.getInventory().consumeItem(40300, 1);
					L1Teleport.teleport(pc, 32936, 33057, (short) 70, 0, false);
				}
			}
		}
		if (nowtime >= 0  && nowtime < 89 * 60 // 0~1.30
				|| nowtime >= 180 * 60 && nowtime < 269 * 60 // 3~4.30
				|| nowtime >= 360 * 60 && nowtime < 449 * 60 // 6~7.30
				|| nowtime >= 540 * 60 && nowtime < 629 * 60 // 9~10.30
				|| nowtime >= 720 * 60 && nowtime < 809 * 60 // 12~13.30
				|| nowtime >= 900 * 60 && nowtime < 989 * 60 // 15~16.30
				|| nowtime >= 1080 * 60 && nowtime < 1169 * 60 // 18~19.30
				|| nowtime >= 1260 * 60 && nowtime < 1349 * 60 // 21~22.30
			) {
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				if (pc.getMapId() == 84) {
					pc.getInventory().consumeItem(40301, 1);
					L1Teleport.teleport(pc, 33426, 33499, (short) 4, 0, false);
				}
			}
		}
/*		if (nowtime >= 0  && nowtime < 90 * 60 // 0~1.30
				|| nowtime >= 180 * 60 && nowtime < 270 * 60 // 3~4.30
				|| nowtime >= 360 * 60 && nowtime < 450 * 60 // 6~7.30
				|| nowtime >= 540 * 60 && nowtime < 630 * 60 // 9~10.30
				|| nowtime >= 720 * 60 && nowtime < 810 * 60 // 12~13.30
				|| nowtime >= 900 * 60 && nowtime < 990 * 60 // 15~16.30
				|| nowtime >= 1080 * 60 && nowtime < 1170 * 60 // 18~19.30
				|| nowtime >= 1260 * 60 && nowtime < 1350 * 60 // 21~22.30
			) {*/
		if (nowtime >= 0  && nowtime < 89 * 60 // 0~1.30
				|| nowtime >= 180 * 60 && nowtime < 269 * 60 // 3~4.30
				|| nowtime >= 360 * 60 && nowtime < 449 * 60 // 6~7.30
				|| nowtime >= 540 * 60 && nowtime < 629 * 60 // 9~10.30
				|| nowtime >= 720 * 60 && nowtime < 809 * 60 // 12~13.30
				|| nowtime >= 900 * 60 && nowtime < 989 * 60 // 15~16.30
				|| nowtime >= 1080 * 60 && nowtime < 1169 * 60 // 18~19.30
				|| nowtime >= 1260 * 60 && nowtime < 1349 * 60 // 21~22.30
			) {
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				if (pc.getMapId() == 447) {
					pc.getInventory().consumeItem(40302, 1);
					L1Teleport.teleport(pc, 32297, 33087, (short) 440, 0, false);
				}
			}
		}
/*		if (nowtime >= 90 * 60 && nowtime < 180 * 60 // 1.30~3
				|| nowtime >= 270 * 60 && nowtime < 360 * 60 // 4.30~6
				|| nowtime >= 450 * 60 && nowtime < 540 * 60 // 7.30~9
				|| nowtime >= 630 * 60 && nowtime < 720 * 60 // 10.30~12
				|| nowtime >= 810 * 60 && nowtime < 900 * 60 // 13.30~15
				|| nowtime >= 990 * 60 && nowtime < 1080 * 60 // 16.30~18
				|| nowtime >= 1170 * 60 && nowtime < 1260 * 60 // 19.30~21
				|| nowtime >= 1350 * 60 && nowtime < 1440 * 60 // 22.30~24
			) {*/
		if (nowtime >= 90 * 60 && nowtime < 179 * 60 // 1.30~3
				|| nowtime >= 270 * 60 && nowtime < 359 * 60 // 4.30~6
				|| nowtime >= 450 * 60 && nowtime < 539 * 60 // 7.30~9
				|| nowtime >= 630 * 60 && nowtime < 719 * 60 // 10.30~12
				|| nowtime >= 810 * 60 && nowtime < 899 * 60 // 13.30~15
				|| nowtime >= 990 * 60 && nowtime < 1079 * 60 // 16.30~18
				|| nowtime >= 1170 * 60 && nowtime < 1259 * 60 // 19.30~21
				|| nowtime >= 1350 * 60 && nowtime < 1439 * 60 // 22.30~24
			) {
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				if (pc.getMapId() == 446) {
					pc.getInventory().consumeItem(40303, 1);
					L1Teleport.teleport(pc, 32750, 32874, (short) 445, 0, false);
				}
			}
		}
/*		if (nowtime >= 0 && nowtime < 90 * 60
				|| nowtime >= 180 * 60 && nowtime < 270 * 60
				|| nowtime >= 360 * 60 && nowtime < 450 * 60
				|| nowtime >= 540 * 60 && nowtime < 630 * 60
				|| nowtime >= 720 * 60 && nowtime < 810 * 60
				|| nowtime >= 900 * 60 && nowtime < 990 * 60
				|| nowtime >= 1080 * 60 && nowtime < 1170 * 60
				|| nowtime >= 1260 * 60 && nowtime < 1350 * 60) {*/
		if (nowtime >= 0  && nowtime < 89 * 60 // 0~1.30
				|| nowtime >= 180 * 60 && nowtime < 269 * 60 // 3~4.30
				|| nowtime >= 360 * 60 && nowtime < 449 * 60 // 6~7.30
				|| nowtime >= 540 * 60 && nowtime < 629 * 60 // 9~10.30
				|| nowtime >= 720 * 60 && nowtime < 809 * 60 // 12~13.30
				|| nowtime >= 900 * 60 && nowtime < 989 * 60 // 15~16.30
				|| nowtime >= 1080 * 60 && nowtime < 1169 * 60 // 18~19.30
				|| nowtime >= 1260 * 60 && nowtime < 1349 * 60 // 21~22.30
			) {
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) { // 往古鲁丁的船
				if (pc.getMapId() == 5) { // 船到达港口人在船上
					pc.getInventory().consumeItem(40299, 1);
					L1Teleport.teleport(pc, 32542, 32726, (short) 4, 0, false); // 古鲁丁港口
				}
			}
		}
/*		if (nowtime >= 90 * 3600 && nowtime < 180 * 3600
				|| nowtime >= 270 * 3600 && nowtime < 360 * 60
				|| nowtime >= 450 * 3600 && nowtime < 540 * 60
				|| nowtime >= 630 * 3600 && nowtime < 720 * 60
				|| nowtime >= 810 * 3600 && nowtime < 900 * 60
				|| nowtime >= 990 * 3600 && nowtime < 1080 * 60
				|| nowtime >= 1170 * 3600 && nowtime < 1260 * 60
				|| nowtime >= 1350 * 3600 && nowtime < 1440 * 60) {*/
		if (nowtime >= 90 * 60 && nowtime < 179 * 60 // 1.30~3
				|| nowtime >= 270 * 60 && nowtime < 359 * 60 // 4.30~6
				|| nowtime >= 450 * 60 && nowtime < 539 * 60 // 7.30~9
				|| nowtime >= 630 * 60 && nowtime < 719 * 60 // 10.30~12
				|| nowtime >= 810 * 60 && nowtime < 899 * 60 // 13.30~15
				|| nowtime >= 990 * 60 && nowtime < 1079 * 60 // 16.30~18
				|| nowtime >= 1170 * 60 && nowtime < 1259 * 60 // 19.30~21
				|| nowtime >= 1350 * 60 && nowtime < 1439 * 60 // 22.30~24
			) {
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) { // 往说话之岛的船
				if (pc.getMapId() == 6) { // 船到达港口人在船上
					pc.getInventory().consumeItem(40298, 1);
					L1Teleport.teleport(pc, 32632, 32981, (short) 0, 0, false); // 说话之岛港口
				}
			}
		}
		// 到达港口时间修正  end
	}
}
