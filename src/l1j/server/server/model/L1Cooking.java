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

import java.util.logging.Logger;

import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SPMR;
import static l1j.server.server.model.skill.L1SkillId.*;

// Referenced classes of package l1j.server.server.model:
// L1Cooking

public class L1Cooking {
	
	private L1Cooking() {
	}

	public static void useCookingItem(L1PcInstance pc, L1ItemInstance item) {
		int itemId = item.getItem().getItemId();
		if (itemId == 41284 || itemId == 41292) { // 
			if (pc.get_food() < 29) {
				pc.sendPackets(new S_ServerMessage(74, item
						.getNumberedName(1))); // \f1%0使用。
				return;
			}
		}

		if (itemId >= 41277 && itemId <= 41283 // Lv1料理
				|| itemId >= 41285 && itemId <= 41291) { // Lv1幻想料理
			int cookingId = pc.getCookingId();
			if (cookingId != 0) {
				pc.removeSkillEffect(cookingId);
			}
		}

		if (itemId == 41284 || itemId == 41292) { // 
			int dessertId = pc.getDessertId();
			if (dessertId != 0) {
				pc.removeSkillEffect(dessertId);
			}
		}

		int cookingType;
		int cookingId;
		int time = 900;
		if (itemId == 41277 || itemId == 41285) { // 
			cookingType = 0;
			if (itemId == 41277) {
				cookingId = COOKING_1_0_N;
			} else {
				cookingId = COOKING_1_0_S;
			}
			pc.addWind(10);
			pc.addWater(10);
			pc.addFire(10);
			pc.addEarth(10);
			pc.sendPackets(new S_OwnCharAttrDef(pc));
			pc.sendPackets(new S_PacketBox(53, cookingType, time));
			pc.setSkillEffect(cookingId, time * 1000);
			pc.setCookingId(cookingId);
		} else if (itemId == 41278 || itemId == 41286) { // 
			cookingType = 1;
			if (itemId == 41278) {
				cookingId = COOKING_1_1_N;
			} else {
				cookingId = COOKING_1_1_S;
			}
			pc.addMaxHp(30);
			pc.sendPackets(new S_HPUpdate(pc.getCurrentHp(), pc.getMaxHp()));
			if (pc.isInParty()) { // 中
				pc.getParty().updateMiniHP(pc);
			}
			pc.sendPackets(new S_PacketBox(53, cookingType, time));
			pc.setSkillEffect(cookingId, time * 1000);
			pc.setCookingId(cookingId);
		} else if (itemId == 41279 || itemId == 41287) { // 饼
			cookingType = 2;
			if (itemId == 41279) {
				cookingId = COOKING_1_2_N;
			} else {
				cookingId = COOKING_1_2_S;
			}
			pc.sendPackets(new S_PacketBox(53, cookingType, time));
			pc.setSkillEffect(cookingId, time * 1000);
			pc.setCookingId(cookingId);
		} else if (itemId == 41280 || itemId == 41288) { // 蚁脚烧
			cookingType = 3;
			if (itemId == 41280) {
				cookingId = COOKING_1_3_N;
			} else {
				cookingId = COOKING_1_3_S;
			}
			pc.addAc(-1);
			pc.sendPackets(new S_OwnCharStatus(pc));
			pc.sendPackets(new S_PacketBox(53, cookingType, time));
			pc.setSkillEffect(cookingId, time * 1000);
			pc.setCookingId(cookingId);
		} else if (itemId == 41281 || itemId == 41289) { // 
			cookingType = 4;
			if (itemId == 41281) {
				cookingId = COOKING_1_4_N;
			} else {
				cookingId = COOKING_1_4_S;
			}
			pc.addMaxMp(20);
			pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
			pc.sendPackets(new S_PacketBox(53, cookingType, time));
			pc.setSkillEffect(cookingId, time * 1000);
			pc.setCookingId(cookingId);
		} else if (itemId == 41282 || itemId == 41290) { // 甘酢
			cookingType = 5;
			if (itemId == 41282) {
				cookingId = COOKING_1_5_N;
			} else {
				cookingId = COOKING_1_5_S;
			}
			pc.sendPackets(new S_PacketBox(53, cookingType, time));
			pc.setSkillEffect(cookingId, time * 1000);
			pc.setCookingId(cookingId);
		} else if (itemId == 41283 || itemId == 41291) { // 猪肉串烧
			cookingType = 6;
			if (itemId == 41283) {
				cookingId = COOKING_1_6_N;
			} else {
				cookingId = COOKING_1_6_S;
			}
			pc.addMr(5);
			pc.sendPackets(new S_SPMR(pc));
			pc.sendPackets(new S_PacketBox(53, cookingType, time));
			pc.setSkillEffect(cookingId, time * 1000);
			pc.setCookingId(cookingId);
		} else if (itemId == 41284 || itemId == 41292) { // 
			cookingType = 7;
			if (itemId == 41284) {
				cookingId = COOKING_1_7_N;
			} else {
				cookingId = COOKING_1_7_S;
			}
			pc.sendPackets(new S_PacketBox(53, cookingType, time));
			pc.setSkillEffect(cookingId, time * 1000);
			pc.setDessertId(cookingId);
		}
		pc.sendPackets(new S_ServerMessage(76, item.getNumberedName(1))); // \f1%0食。
		pc.getInventory().removeItem(item , 1);

		// 空腹17%再送信。S_PacketBox空腹更新含？
		pc.sendPackets(new S_OwnCharStatus(pc));
	}

}
