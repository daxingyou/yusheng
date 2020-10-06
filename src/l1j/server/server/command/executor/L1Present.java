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
package l1j.server.server.command.executor;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1DwarfInventory;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.world.L1World;

public class L1Present implements L1CommandExecutor {
	
	private L1Present() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1Present();
	}

	// @Override
	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			StringTokenizer st = new StringTokenizer(arg);
			String name = st.nextToken();
			String nameid = st.nextToken();
			L1PcInstance target = L1World.getInstance().getPlayer(name);
			if (target == null) {
				pc.sendPackets(new S_SystemMessage(name+ "不在线或者不存在。"));
				return;
			}
			int count = 1;
			if (st.hasMoreTokens()) {
				count = Integer.parseInt(st.nextToken());
			}
			int enchant = 0;
			if (st.hasMoreTokens()) {
				enchant = Integer.parseInt(st.nextToken());
			}
			int isId = 0;
			if (st.hasMoreTokens()) {
				isId = Integer.parseInt(st.nextToken());
			}
			int itemid = 0;
			try {
				itemid = Integer.parseInt(nameid);
			} catch (NumberFormatException e) {
				itemid = ItemTable.getInstance().findItemIdByNameWithoutSpace(
						nameid);
				if (itemid == 0) {
					pc.sendPackets(new S_SystemMessage("找不到符合条件项目。"));
					return;
				}
			}
			L1Item temp = ItemTable.getInstance().getTemplate(itemid);
			if (temp != null) {
				if (temp.isStackable()) {
					L1ItemInstance item = ItemTable.getInstance().createItem(
							itemid);
					item.setEnchantLevel(0);
					item.setCount(count);
					if (isId == 1) {
						item.setIdentified(true);
					}
					if (target.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
						target.getInventory().storeItem(item);
						target.sendPackets(new S_ServerMessage(403, // %0を手に入れました。
								item.getLogName()));
						pc.sendPackets(new S_SystemMessage("发送"+item.getLogViewName()+"成功(itemid:"+itemid+")！"));
						WriteLogTxt.Recording("赠送记录", "在线赠送玩家 "+target.getName()+" 道具:"+item.getLogViewName()+"OBJID："+item.getId());
					}
				} else {
					L1ItemInstance item = null;
					int createCount;
					for (createCount = 0; createCount < count; createCount++) {
						item = ItemTable.getInstance().createItem(itemid);
						item.setEnchantLevel(enchant);
						if (isId == 1) {
							item.setIdentified(true);
						}
						if (target.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
							target.getInventory().storeItem(item);
							pc.sendPackets(new S_SystemMessage("发送"+item.getLogViewName()+"成功(itemid:"+itemid+")！"));
							WriteLogTxt.Recording("赠送记录", "在线赠送玩家 "+target.getName()+" 道具:"+item.getLogViewName()+"OBJID："+item.getId());
						} else {
							break;
						}
					}
					if (createCount > 0) {
						target.sendPackets(new S_ServerMessage(403, // %0を手に入れました。
								item.getLogName()));
					}
				}
			} else {
				pc.sendPackets(new S_SystemMessage("指定的道具编号不存在"));
			}	
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage(
					".present 角色  itemid|name [数目] [强化等级] [鉴定] 请输入。"));
		}
	}
}
