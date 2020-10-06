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

import l1j.server.server.WriteLogTxt;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1BabyInstance; // 魔法娃娃 
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_Light;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

public class C_DropItem extends ClientBasePacket {

	private static final String C_DROP_ITEM = "[C] C_DropItem";

	public C_DropItem(byte[] decrypt, LineageClient _client) throws Exception {
		super(decrypt);
		int x = readH();
		int y = readH();
		int objectId = readD();
		int count = readD();
		/*
		 * count = Math.abs(count); count = Math.min(count,2000000000); count =
		 * Math.max(count,0);
		 */

		L1PcInstance pc = _client.getActiveChar();
		if (pc.isGhost()) {
			return;
		}
//		if (pc.isCheckTwopassword()) {
//			pc.sendPackets(new S_SystemMessage("\\F3**请在聊天框输入二级密码才可正常游戏**"));
//			return;
//		}
		L1ItemInstance item = pc.getInventory().getItem(objectId);
		if (item == null) {
			return;
		}
		if (item.get_time() != null) {
			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
			return;
		}
		// 防洗道具
		if (count > 0 && count < 2000000000 && item.getCount() > 0
				&& item.getCount() >= count) {
			// 防洗道具 end
			if (item != null) {
				if (!pc.isGm() && !item.getItem().isTradable()
						&& !item.isTradable()) {
					// \f1%0舍他人让。
					pc.sendPackets(new S_ServerMessage(210, item.getItem()
							.getName()));
					return;
				}

				Object[] petlist = pc.getPetList().values().toArray();
				for (Object petObject : petlist) {
					if (petObject instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) petObject;
						if (item.getId() == pet.getItemObjId()) {
							// \f1%0舍他人让。
							pc.sendPackets(new S_ServerMessage(210, item
									.getItem().getName()));
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

				// 旅馆不能放置物品
				if (pc.getMapId() >= 16384 && pc.getMapId() <= 25088) {
					pc.sendPackets(new S_ServerMessage(506));
					return;
				}
				// 旅馆不能放置物品 end
				
				item.setsbxz(true); // 防止玩家丢东西到地上怪物获得杀死首爆奖励

				if (item.isEquipped()) {
					// \f1削除装备舍。
					pc.sendPackets(new S_ServerMessage(125));
					return;
				}
				L1Location location = new L1Location(x, y, pc.getMapId());
				if (pc.getLocation().getTileLineDistance(location) > 3) {
					return;
				}

				// 丢掉打开中的灯类
				if (item.getItemId() == 40001 || item.getItemId() == 40002
						|| item.getItemId() == 40004
						|| item.getItemId() == 40005) {
					pc.setPcLight(0);

					if (item.getEnchantLevel() != 0) {
						item.setEnchantLevel(0);
					}

					pc.getInventory().updateItem(item,
							L1PcInventory.COL_ENCHANTLVL);
					pc.sendPackets(new S_ItemName(item));

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
								pc.setPcLight(OwnLight.getItem()
										.getLightRange());
							}
						}
					}

					pc.sendPackets(new S_Light(pc.getId(), pc.getPcLight()));
					if (!pc.isInvisble() && item.getItemId() != 40004) {// 非隐身中跟魔法灯笼除外
						pc.broadcastPacket(new S_Light(pc.getId(), pc
								.getPcLight()));
					}
				}
				// 丢掉打开中的灯类 end
				if (item.isSeal()) {
					pc.sendPackets(new S_SystemMessage(item.getLogViewName()
							+ "处于封印状态！"));
					return;
				}
				WriteLogTxt.Recording("丢弃记录", "玩家#" + pc.getName() + "#在地图ID"
						+ pc.getMapId() + "X:" + x + "Y:" + y + "#玩家objid：<"
						+ pc.getId() + ">" + "#身上物品" + item.getLogViewName()
						+ "物品objid：<" + item.getId() + ">" + "丢弃(" + count
						+ ")个。");
				pc.getInventory()
						.tradeItem(
								item,
								count,
								L1World.getInstance().getInventory(x, y,
										pc.getMapId()));

			}
			// 防洗道具
		}
	}

	@Override
	public String getType() {
		return C_DROP_ITEM;
	}
}
