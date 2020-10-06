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

package l1j.server.server.serverpackets;

import java.util.List;

import l1j.server.server.Opcodes;
import l1j.server.server.datatables.CenterTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.center.L1AssessedItem;
// Referenced classes of package net.l1j.server.serverpackets:
// ServerBasePacket, S_SystemMessage
import l1j.server.server.model.center.L1Center;
import l1j.server.server.world.L1World;

public class S_CenterBuyList extends ServerBasePacket {


	private static final String S_SHOP_BUY_LIST = "[S] S_ShopBuyList";

	public S_CenterBuyList(int objid, L1PcInstance pc) {
		L1Object object = L1World.getInstance().findObject(objid);
		if (!(object instanceof L1NpcInstance)) {
			return;
		}
		//System.out.println("步骤4");
		L1NpcInstance npc = (L1NpcInstance) object;
		int npcId = npc.getNpcTemplate().get_npcId();
		L1Center shop = CenterTable.getInstance().get(npcId);
		//新所有物品贩卖 by eric1300460
		List<L1AssessedItem> assessedItems;
		/*if(npcId==70037){
				assessedItems = shop
				.allAssessItems(pc.getInventory());
				if (assessedItems.isEmpty()) {
					pc.sendPackets(new S_NoSell(npc));
					return;
				}
		}*///else{
			if (shop == null) {
				pc.sendPackets(new S_NoSell(npc));
				return;
			}
			//System.out.println("步骤5");
			assessedItems = shop
					.assessItems(pc.getInventory());
			if (assessedItems.isEmpty()) {
				pc.sendPackets(new S_NoSell(npc));
				return;
			}
		//}
		//~新所有物品贩卖 by eric1300460
			//System.out.println("步骤6");
		writeC(Opcodes.S_OPCODE_SHOWSHOPSELLLIST);
		writeD(objid);
		writeH(assessedItems.size());
		//System.out.println("步骤7");
		for (L1AssessedItem item : assessedItems) {
			writeD(item.getTargetId());
			writeD(item.getAssessedPrice());
		}
		//System.out.println("步骤8");
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	@Override
	public String getType() {
		return S_SHOP_BUY_LIST;
	}
}