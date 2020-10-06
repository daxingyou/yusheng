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

import java.util.List;

import l1j.server.Config;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.datatables.LetterTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Referenced classes of package l1j.server.server.model:
// L1DeleteItemOnGround

public class L1DeleteItemOnGround {
	private DeleteTimer _deleteTimer;
	private int _time = 0;
	private int _range = 0;

	private static final Log _log = LogFactory.getLog(L1DeleteItemOnGround.class);

	public L1DeleteItemOnGround() {
	}

	class DeleteTimer implements Runnable {
		public DeleteTimer() {
		}

		@Override
		public void run() {
			for (;;) {
				try {
					Thread.sleep(_time);
				} catch (Exception exception) {
					_log.info("L1DeleteItemOnGround error: " + exception);
					break;
				}
/*				L1World.getInstance().broadcastPacketToAll(
						new S_ServerMessage(166, "地上的物品",
								"10秒后将清除")); // \f1%0%4%1%3 %2
*/				try {
					for (int i = 10; i > 0; i--) {
						L1World.getInstance().broadcastPacketToAll(
								new S_ServerMessage(166, "地上的物品",
										i+"秒后将清除")); 
						Thread.sleep(1000);		
					}		
				} catch (Exception exception) {
					_log.info("L1DeleteItemOnGround error: " + exception);
					break;
				}
				deleteItem();
				L1World.getInstance().broadcastPacketToAll(
						new S_ServerMessage(166, "地上的物品", "被清除了")); // \f1%0%4%1%3
				// %2
			}
		}
	}

	public void onAction() {
		_range = Config.ALT_ITEM_DELETION_RANGE;
		if (_range < 0) {
			_range = 0;
		}
		if (_range > 10) {
			_range = 10;
		}
		if (Config.ALT_ITEM_DELETION_TIME > 0
				&& Config.ALT_ITEM_DELETION_TIME <= 35791) {
			_time = Config.ALT_ITEM_DELETION_TIME * 60 * 1000 - 10 * 1000;
			_deleteTimer = new DeleteTimer();
			GeneralThreadPool.getInstance().execute(_deleteTimer); // 开始
		}
	}

	private void deleteItem() {
		for (L1Object l1object : L1World.getInstance().getObject()) {
			if (l1object instanceof L1ItemInstance) {
				L1ItemInstance item = (L1ItemInstance) l1object;
				if (item.getX() == 0 && item.getY() == 0) { // 地面上、谁所有物
					continue;
				}
				if (item.getItem().getItemId() == 40515) { // 精灵石
					continue;
				}
				if (L1HouseLocation.isInHouse(item.getX(), item.getY(), item
						.getMapId())) { // 内
					continue;
				}

				int pcSize = 0;
				if (_range > 0){
					final List<L1PcInstance> players = L1World.getInstance().getVisiblePlayer(item, _range);
					pcSize = players.size();
				}
				if (pcSize == 0) {
					L1Inventory groundInventory = L1World.getInstance()
							.getInventory(item.getX(), item.getY(),
									item.getMapId());
					if (item.getItem().getItemId() == 40314 // 
							|| item.getItem().getItemId() == 40316) {
						PetTable.getInstance().deletePet(item.getId());
					} else if (item.getItem().getItemId() >= 49016 // 便笺
							&& item.getItem().getItemId() <= 49025) {
						LetterTable lettertable = new LetterTable();
						lettertable.deleteLetter(item.getId());
					}
					groundInventory.removeItem(item);
				}
			}
		}
	}
}
