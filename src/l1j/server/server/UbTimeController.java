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

import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.server.datatables.UBTable;
import l1j.server.server.model.L1UltimateBattle;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;
import l1j.william.L1WilliamSystemMessage;

public class UbTimeController implements Runnable {
	
	private static final Log _log = LogFactory.getLog(UbTimeController.class);


	private static UbTimeController _instance;

	public static UbTimeController getInstance() {
		if (_instance == null) {
			_instance = new UbTimeController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			while (true) {
				checkUbTime(); // UB开始时间
				Thread.sleep(30000);
			}
		} catch (Exception e) {
			_log.error(e.getLocalizedMessage(), e);
		}
	}

	private void checkUbTime() {
		for (L1UltimateBattle ub : UBTable.getInstance().getAllUb()) {
			if (ub.checkUbTime() && !ub.isActive()) {
				ub.start(); // UB开始
				// 无限大赛讯息 
				String msg = "";
				switch(ub.getUbId()) {
					case 1: {
						msg = L1WilliamSystemMessage.ShowMessage(1119); // 奇岩
					}
					break;
					case 2: {
						msg = L1WilliamSystemMessage.ShowMessage(1121); // 威顿
					}
					break;
					case 3: {
						msg = L1WilliamSystemMessage.ShowMessage(1120); // 古鲁丁
					}
					break;
					case 4: {
						msg = L1WilliamSystemMessage.ShowMessage(1117); // 说话之岛
					}
					break;
					case 5: {
						msg = L1WilliamSystemMessage.ShowMessage(1118); // 银骑士
					}
					break;
				}

				// ? 竞技场５分钟后比赛将开始，想参赛者请现在入场。
				L1World.getInstance().broadcastPacketToAll(new S_SystemMessage(msg 
					+ L1WilliamSystemMessage.ShowMessage(1122) + "５" + L1WilliamSystemMessage.ShowMessage(1123)));
				// 无限大赛讯息  end
			}
		}
	}

}
