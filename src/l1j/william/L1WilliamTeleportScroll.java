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
package l1j.william;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;

// Referenced classes of package l1j.server.server.model:
// L1PcInstance

public class L1WilliamTeleportScroll {


	private int _itemId;

	private int _tpLocX;

	private int _tpLocY;

	private int _tpMapId;

	private int _check_minLocX;

	private int _check_minLocY;

	private int _check_maxLocX;

	private int _check_maxLocY;

	private short _check_MapId;
	
	private int _check_ItemId;
	
	private int _check_ItemCount;

	private int _removeItem;

	public L1WilliamTeleportScroll(int itemId, int tpLocX, int tpLocY, short tpMapId, 
		int check_minLocX, int check_minLocY, int check_maxLocX, int check_maxLocY, short check_MapId, int removeItem,int check_ItemId,int check_ItemCount) {

	_itemId = itemId;
	_tpLocX = tpLocX;
	_tpLocY = tpLocY;
	_tpMapId = tpMapId;
	_check_minLocX = check_minLocX;
	_check_minLocY = check_minLocY;
	_check_maxLocX = check_maxLocX;
	_check_maxLocY = check_maxLocY;
	_check_MapId = check_MapId;
	_removeItem = removeItem;
	_check_ItemId = check_ItemId;
	_check_ItemCount = check_ItemCount;
	}

	public int getItemId() {
		return _itemId;
	}

	public int getTpLocX() {
		return _tpLocX;
	}

	public int getTpLocY() {
		return _tpLocY;
	}

	public int getTpMapId() {
		return _tpMapId;
	}

	public int getCheckMinLocX() {
		return _check_minLocX;
	}

	public int getCheckMinLocY() {
		return _check_minLocY;
	}
	
	public int getCheckMaxLocX() {
		return _check_maxLocX;
	}
	
	public int getCheckMaxLocY() {
		return _check_maxLocY;
	}
	
	public int getCheckMapId() {
		return _check_MapId;
	}
	
	public int getCheckItemId() {
		return _check_ItemId;
	}
	
	public int getCheckItemCount() {
		return _check_ItemCount;
	}
	
	public int getRemoveItem() {
		return _removeItem;
	}

	public static int checkItemId(int itemId) {
		L1WilliamTeleportScroll teleport_scroll = TeleportScroll.getInstance().getTemplate(itemId);

		if (teleport_scroll == null) {
			return 0;
		}

		int item_id = teleport_scroll.getItemId();
		return item_id;
	}
	
	public static void getTeleportScroll(L1PcInstance pc, L1ItemInstance l1iteminstance, int itemId) {
		L1WilliamTeleportScroll teleport_scroll = TeleportScroll.getInstance().getTemplate(itemId);
				
		if (teleport_scroll == null) {
			return;
		}
		
		if (teleport_scroll.getCheckItemId() > 0 && teleport_scroll.getCheckItemCount() > 0){
			if (!pc.getInventory().checkItem(teleport_scroll.getCheckItemId(),teleport_scroll.getCheckItemCount())){
				final L1Item item = ItemTable.getInstance().getTemplate(teleport_scroll.getCheckItemId());
				if (item != null){
					pc.sendPackets(new S_SystemMessage(String.format("\\F2%s不足(%d)", item.getName(),teleport_scroll.getCheckItemCount())));
				}
				return;
			}
			pc.getInventory().consumeItem(teleport_scroll.getCheckItemId(),teleport_scroll.getCheckItemCount());
		}
		
		if (teleport_scroll.getCheckMinLocX() != 0 && 
			teleport_scroll.getCheckMinLocY() != 0 && 
			teleport_scroll.getCheckMaxLocX() != 0 && 
			teleport_scroll.getCheckMaxLocY() != 0 && 
			pc.getX() >= teleport_scroll.getCheckMinLocX() && pc.getX() <= teleport_scroll.getCheckMaxLocX() && 
			pc.getY() >= teleport_scroll.getCheckMinLocY() && pc.getY() <= teleport_scroll.getCheckMaxLocY() && 
			pc.getMapId() == teleport_scroll.getCheckMapId()) {
			if (pc.getMap().isEscapable() || pc.isGm()) {
				L1Location loc = new L1Location(teleport_scroll.getTpLocX(),teleport_scroll.getTpLocY(), 
					(short)teleport_scroll.getTpMapId());
				L1Location rndloc = loc.randomLocation(5, true);
				L1Teleport.teleport(pc, rndloc.getX(), 
						rndloc.getY(), 
					(short)teleport_scroll.getTpMapId(), 5, true);
				//pc.setActived(false);
				//System.out.println("X:"+loc.getX()+"  Y:"+loc.getY());
				pc.setActived(false);
				if (teleport_scroll.getRemoveItem() != 0) {
					pc.getInventory().removeItem(l1iteminstance, 1);
				}
			} else {
				pc.sendPackets(new S_ServerMessage(647));
			}
		} else if (teleport_scroll.getCheckMinLocX() == 0 && 
			teleport_scroll.getCheckMinLocY() == 0 && 
			teleport_scroll.getCheckMaxLocX() == 0 && 
			teleport_scroll.getCheckMaxLocY() == 0 && 
			teleport_scroll.getTpLocX() != 0 && 
			teleport_scroll.getTpLocY() != 0) {
			if (pc.getMap().isEscapable() || pc.isGm()) {
				L1Location loc = new L1Location(teleport_scroll.getTpLocX(),teleport_scroll.getTpLocY(), 
						(short)teleport_scroll.getTpMapId());
				loc.randomLocation(5, true);
				L1Location rndloc = loc.randomLocation(5, true);
				L1Teleport.teleport(pc,rndloc.getX(), 
						rndloc.getY(), 
					(short)teleport_scroll.getTpMapId(), 5, true);
				pc.setActived(false);
				//pc.setActived(false);
				//System.out.println("X:"+loc.getX()+"  Y:"+loc.getY());
				if (teleport_scroll.getRemoveItem() != 0) {
					pc.getInventory().removeItem(l1iteminstance, 1);
				}
			} else {
				pc.sendPackets(new S_ServerMessage(647));
			}
		} else {
			pc.sendPackets(new S_ServerMessage(79));
		}
	}
}
