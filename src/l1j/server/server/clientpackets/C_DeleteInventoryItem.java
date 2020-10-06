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

package l1j.server.server.clientpackets;

import java.util.logging.Logger;

import l1j.server.server.WriteLogTxt;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_Light;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_DeleteInventoryItem extends ClientBasePacket {

	private static final String C_DELETE_INVENTORY_ITEM
			= "[C] C_DeleteInventoryItem";

	public C_DeleteInventoryItem(byte[] decrypt, LineageClient _client) {
		super(decrypt);
		int itemObjectId = readD();
		L1PcInstance pc = _client.getActiveChar();
		L1ItemInstance item = pc.getInventory().getItem(itemObjectId);
		// 削除上无场合
		if (item == null) {
			// l1pcinstance.sendPackets(new
			// S_SystemMessage("所持。")); // 适切无为
			return;
		}
		if (!pc.isGm() && item.getItem().isCantDelete()) {
			// 125 \f1你不能够放弃此样物品。
			pc.sendPackets(new S_ServerMessage(125));
			return;
		}
		if (!pc.isGm() && item.getItemCharaterTrade() != null){
			pc.sendPackets(new S_ServerMessage(125));
			return;
		}
		// 不能删除的道具 
/*		if (item.getItemId() == l1j.william.New_Id.Item_EPU_48 || // 长钓竿
			item.getItemId() == l1j.william.New_Id.Item_EPU_49 || // 短钓竿
			item.getItemId() == l1j.william.New_Id.Item_EPU_67 || // 占星术师的瓮
			item.getItemId() == l1j.william.New_Id.Item_EPU_68 || // 占星术师的灵魂球
			item.getItemId() == l1j.william.New_Id.Item_EPU_69 || // 占星术师的符咒
			item.getItemId() == l1j.william.New_Id.Item_EPU_84 || // 标本制作委托书
			item.getItemId() == l1j.william.New_Id.Item_EPU_4 || // 魔法娃娃：肥肥
			item.getItemId() == l1j.william.New_Id.Item_EPU_5 || // 魔法娃娃：小思克巴
			item.getItemId() == l1j.william.New_Id.Item_EPU_6 || // 魔法娃娃：野狼宝宝
			item.getItemId() == 65 || // 天空之剑
			item.getItemId() == 192 || // 水精灵之弓
			item.getItemId() == 133 || // 古代人的智慧
			item.getItemId() == 20037 || // 真实的面具
			item.getItemId() == 40529 || // 感谢信
			item.getItemId() == 40533 || // 古代钥匙(下半部)
			item.getItemId() == 40534 || // 古代钥匙(上半部)
			item.getItemId() == 40536 || // 古代恶魔的记载
			item.getItemId() == 40553 || // 布鲁迪卡之袋
			item.getItemId() == 40556 || // 暗杀名单之袋
			item.getItemId() == 40557 || // 暗杀名单(古鲁丁村)
			item.getItemId() == 40558 || // 暗杀名单(奇岩村)
			item.getItemId() == 40559 || // 暗杀名单(亚丁城镇)
			item.getItemId() == 40560 || // 暗杀名单(风木村)
			item.getItemId() == 40561 || // 暗杀名单(肯特村)
			item.getItemId() == 40562 || // 暗杀名单(海音村)
			item.getItemId() == 40563 || // 暗杀名单(燃柳村)
			item.getItemId() == 40572 || // 刺客之证
			item.getItemId() == 40592 || // 受诅咒的精灵书
			item.getItemId() == 40595 || // 死亡之证
			item.getItemId() == 40596 || // 死亡誓约
			item.getItemId() == 40607 || // 返生药水
			item.getItemId() == 40648 || // 生锈的刺客之剑
			item.getItemId() == l1j.william.New_Id.Item_AJ_22 || // 魔法娃娃：长者
			item.getItemId() == l1j.william.New_Id.Item_AJ_23 || // 魔法娃娃：奎斯坦修
			item.getItemId() == l1j.william.New_Id.Item_AJ_24 || // 魔法娃娃：高仑石头怪
			item.getItemId() == l1j.william.New_Id.Armor_AJ_1_3  || // 8周年永恒戒
			item.getItemId() == 60101 || // 天票
			item.getItemId() == 60102 || // 天票
			item.getItemId() == 60103 || // 天票
			item.getItemId() == 60104 // 天票
			) {
			pc.sendPackets(new S_ServerMessage(125));
			return;
		}*/
		// 不能删除的道具  end
		
		Object[] petlist = pc.getPetList().values().toArray();
		for (Object petObject : petlist) {
			if (petObject instanceof L1PetInstance) {
				L1PetInstance pet = (L1PetInstance) petObject;
				if (item.getId() == pet.getItemObjId()) {
					// \f1%0舍他人让。
					// 删除pc.sendPackets(new S_ServerMessage(210, item.getItem()
					// 删除		.getName()));
					// 修正讯息 
					pc.sendPackets(new S_ServerMessage(125));
					// 修正讯息  end
					return;
				}
			}
		}

		if (item.isEquipped()) {
			// \f1削除装备舍。
			pc.sendPackets(new S_ServerMessage(125));
			return;
		}
		// 删除pc.getInventory().removeItem(item, item.getCount());
		
		// 删除打开中的灯类 
		if (item.getItemId() == 40001 || 
			item.getItemId() == 40002 || 
			item.getItemId() == 40004 || 
			item.getItemId() == 40005) {
			pc.setPcLight(0);		

			if (item.getEnchantLevel() != 0) {
				item.setEnchantLevel(0);
			}

			pc.getInventory().updateItem(item, L1PcInventory.COL_ENCHANTLVL);
			pc.sendPackets(new S_ItemName(item));

			if (pc.hasSkillEffect(2)) {//日光术
				pc.setPcLight(14);
			}
			for(Object Light : pc.getInventory().getItems()) {
				L1ItemInstance OwnLight = (L1ItemInstance) Light;
				if((OwnLight.getItem().getItemId() == 40001 || 
				OwnLight.getItem().getItemId() == 40002 || 
				OwnLight.getItem().getItemId() == 40004 || 
				OwnLight.getItem().getItemId() == 40005) && 
				OwnLight.getEnchantLevel() != 0) {
					if(pc.getPcLight() < OwnLight.getItem().getLightRange()) {
						pc.setPcLight(OwnLight.getItem().getLightRange());
					}
				}
			}
						
			pc.sendPackets(new S_Light(pc.getId(), pc.getPcLight()));
			if (!pc.isInvisble() && item.getItemId() != 40004) {//非隐身中跟魔法灯笼除外
				pc.broadcastPacket(new S_Light(pc.getId(), pc.getPcLight()));
			}
		}
		// 删除打开中的灯类  end
		if (item.isSeal()) {
			pc.sendPackets(new S_SystemMessage(item.getLogViewName() +"处于封印状态！"));
			return;
		}
		WriteLogTxt.Recording("刪除物品记录", "人物:" + pc.getName() + "刪除物品"
				+ item.getLogViewName() + " ItmeID:" + item.getItemId()
				+ " 物品OBJID:" + item.getId());
		pc.getInventory().removeItem(item, item.getCount());
	}

	@Override
	public String getType() {
		return C_DELETE_INVENTORY_ITEM;
	}
}
