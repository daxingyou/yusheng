/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be trading_partnerful,
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

package l1j.server.server.clientpackets;

import java.util.ArrayList;

import l1j.server.server.datatables.DollPowerTable;
import l1j.server.server.datatables.DollXiLianTable;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Trade;
import l1j.server.server.model.Instance.L1BabyInstance;//魔法娃娃 
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.doll.Doll_HprT;
import l1j.server.server.model.doll.Doll_MprT;
import l1j.server.server.model.doll.L1DollExecutor;
import l1j.server.server.serverpackets.S_Light;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;
//import l1j.server.server.ClientThread;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_TradeAddItem extends ClientBasePacket {
	private static final String C_TRADE_ADD_ITEM = "[C] C_TradeAddItem";

	public C_TradeAddItem(byte abyte0[], LineageClient _client)
			throws Exception {
		super(abyte0);

		final int itemid = readD();
		final int itemcount = readD();
		final L1PcInstance pc = _client.getActiveChar();
//		if (pc.isCheckTwopassword()){
//			pc.sendPackets(new S_SystemMessage("\\F3**请在聊天框输入二级密码才可正常游戏**"));
//			return;
//		}
		// L1Trade trade = new L1Trade();
		final L1ItemInstance item = pc.getInventory().getItem(itemid);
		if (item == null) {
			return;
		}
		// 防洗道具
		if (itemcount > 0 && itemcount < 2000000000 && item.getCount() > 0
				&& item.getCount() >= itemcount) {
			// 防洗道具 end
			if (!item.getItem().isTradable() && !item.isTradable()) {
				pc.sendPackets(new S_ServerMessage(210, item.getItem()
						.getName())); // \f1%0舍他人让。
				return;
			}
			Object[] petlist = pc.getPetList().values().toArray();
			for (Object petObject : petlist) {
				if (petObject instanceof L1PetInstance) {
					L1PetInstance pet = (L1PetInstance) petObject;
					if (item.getId() == pet.getItemObjId()) {
						// \f1%0舍他人让。
						pc.sendPackets(new S_ServerMessage(210, item.getItem()
								.getName()));
						return;
					}
				}
			}
			// 魔法娃娃使用判断
			for (Object babyObject : petlist) {
				if (babyObject instanceof L1BabyInstance) {
					L1BabyInstance baby = (L1BabyInstance) babyObject;
					if (item.getId() == baby.getItemObjId()) {
						// \f1%0舍他人让。
						pc.sendPackets(new S_ServerMessage(1181));
						return;
					}
				}
			}
			// 魔法娃娃使用判断 end

			// 安全交易打开中的灯类
			if (item.getItemId() == 40001 || item.getItemId() == 40002
					|| item.getItemId() == 40004 || item.getItemId() == 40005) {
				pc.setPcLight(0);
				if (item.getEnchantLevel() != 0) {
					item.setEnchantLevel(0);
				}
				if (pc.hasSkillEffect(2)) {// 日光术
					pc.setPcLight(14);
				}

				for (Object Light : pc.getInventory().getItems()) {
					L1ItemInstance OwnLight = (L1ItemInstance) Light;
					if ((OwnLight.getItem().getItemId() == 40001
							|| OwnLight.getItem().getItemId() == 40002
							|| OwnLight.getItem().getItemId() == 40004 || OwnLight
							.getItem().getItemId() == 40005)
							&& OwnLight.getEnchantLevel() != 0) {
						if (pc.getPcLight() < OwnLight.getItem()
								.getLightRange()) {
							pc.setPcLight(OwnLight.getItem().getLightRange());
						}
					}
				}

				pc.sendPackets(new S_Light(pc.getId(), pc.getPcLight()));
				if (!pc.isInvisble() && item.getItemId() != 40004) {// 非隐身中跟魔法灯笼除外
					pc.broadcastPacket(new S_Light(pc.getId(), pc.getPcLight()));
				}
			}
			if (item.isSeal()) {
				pc.sendPackets(new S_SystemMessage(item.getLogViewName()
						+ "处于封印状态！"));
				return;
			}
			final L1Trade trade = new L1Trade();
			L1PcInstance trading_partner = null;
			final L1Object object = L1World.getInstance().findObject(pc.getTradeID());
			if (object instanceof L1NpcInstance) {
				trade.tradeAddItem(pc, item.getId(), itemcount);
				return;
			} else if (object instanceof L1PcInstance) {
				trading_partner = (L1PcInstance) object;
			}
			if (trading_partner == null) {
				return;
			}
			
			if (trading_partner.getInventory().checkAddItem(item, itemcount) != L1Inventory.OK) // 容量重量确认及送信
			{
				trading_partner.sendPackets(new S_ServerMessage(270));
				trade.tradeCancel(pc);
				return;
			}
			if (DollPowerTable.get().get_type(item.getItem().getItemId()) != null){
				final ArrayList<L1DollExecutor> dollExecutors = DollXiLianTable.get().getDollTypes(item.getId());
				final StringBuilder msg = new StringBuilder();
				msg.append("\\F3【");
				msg.append(item.getItem().getName());
	        	msg.append("】洗练属性:");
				if (dollExecutors != null && !dollExecutors.isEmpty()){
					for (final L1DollExecutor pxl : dollExecutors) {
		            	if (pxl.getValue2() != 0){
		            		msg.append(String.format(pxl.getName(), pxl.getValue1(), pxl.getValue2()));
		            	}else{
		            		msg.append(String.format(pxl.getName(), pxl.getValue1()));
		            	}
		            	msg.append(" ");
		            }
				}
				trading_partner.sendPackets(new S_SystemMessage(msg.toString()));
			}
			// 安全交易打开中的灯类 end
			trade.tradeAddItem(pc, item.getId(), itemcount);
			// 防洗道具
		}
	}

	@Override
	public String getType() {
		return C_TRADE_ADD_ITEM;
	}
}
