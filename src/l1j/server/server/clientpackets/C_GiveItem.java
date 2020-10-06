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

import java.util.Random;

import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.PetItemTable;
import l1j.server.server.datatables.PetTypeTable;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1BabyInstance;//魔法娃娃 
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.serverpackets.S_ItemName;
import l1j.server.server.serverpackets.S_Light;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.templates.L1PetItem;
import l1j.server.server.templates.L1PetType;
import l1j.server.server.world.L1World;

public class C_GiveItem extends ClientBasePacket {

	private static final String C_GIVE_ITEM = "[C] C_GiveItem";

	private static Random _random = new Random();

	public C_GiveItem(byte decrypt[], LineageClient _client) {
		super(decrypt);
		int targetId = readD();
		int x = readH();
		int y = readH();
		int itemId = readD();
		int count = readD();
/*		//防洗道具 
		count = Math.abs(count);
		count = Math.min(count,2000000000);
		//防洗道具  end
*/
		if (itemId<=0) {
			return;
		}
		
		if (count<=0) {
			return;
		}
		
		L1PcInstance pc = _client.getActiveChar();
		if (pc.isGhost()) {
			return;
		}
//		if (pc.isCheckTwopassword()){
//			pc.sendPackets(new S_SystemMessage("\\F3**请在聊天框输入二级密码才可正常游戏**"));
//			return;
//		}
		L1Object object = L1World.getInstance().findObject(targetId);
		if (object == null || !(object instanceof L1NpcInstance)) {
			return;
		}
		L1NpcInstance target = (L1NpcInstance) object;
		if (!isNpcItemReceivable(target.getNpcTemplate())) {
			return;
		}
		L1Inventory targetInv = target.getInventory();
		L1Inventory inv = pc.getInventory();
		L1ItemInstance item = inv.getItem(itemId);
		if (item == null) {
			return;
		}
		if (item.get_time() != null){
			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName()));
			return;
		}
		if (item.isEquipped()) {
			pc.sendPackets(new S_ServerMessage(141)); // \f1装备、人渡。
			return;
		}
		if (!pc.isGm() && !item.getItem().isTradable()) {
			pc.sendPackets(new S_ServerMessage(210, item.getItem().getName())); // \f1%0舍他人让。
			return;
		}
		for (Object petObject : pc.getPetList().values()) {
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
		//魔法娃娃使用判断 
		for (Object babyObject : pc.getPetList().values().toArray()) {
			if (babyObject instanceof L1BabyInstance) {
				L1BabyInstance baby = (L1BabyInstance) babyObject;
				if (item.getId() == baby.getItemObjId()) {
					pc.sendPackets(new S_ServerMessage(1181));
					return;
				}
			}
		}
		//魔法娃娃使用判断  end
			
		if (targetInv.checkAddItem(item, count) != L1Inventory.OK) {
			pc.sendPackets(new S_ServerMessage(942)); // 相手重、以上。
			return;
		}
		if (pc.getLocation().getTileLineDistance(target.getLocation()) > 3) {
			return;
		}

		//给予打开中的灯类 
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
		//给予打开中的灯类  end
		if (item.isSeal()) {
			pc.sendPackets(new S_SystemMessage(item.getLogViewName() +"处于封印状态！"));
			return;
		}
		
		item = inv.tradeItem(item, count, targetInv);
		if (pc.isGm()){
			item.setGmDrop(true);
			item.setBroad(item.getItem().isBroad());
		}
		target.onGetItem(item);
		item.setsbxz(true); //防止玩家丢东西到怪身上获得首爆奖励
		WriteLogTxt.Recording(
				"给予记录","玩家#"+pc.getName()+"#玩家objid：<"+pc.getId()+">"
		        +"#给予NPC#"+target.getName()+"#NPCID：<"+target.getNpcId()+">物品"+item.getLogViewName()+"物品objid：<"+item.getId()+">"
				+"。"
				);
		L1PetType petType = PetTypeTable.getInstance().get(
				target.getNpcTemplate().get_npcId());
		if (petType == null || target.isDead()) {
			return;
		}

		if (item.getItemId() == petType.getItemIdForTaming()) {
			tamePet(pc, target);
		}
		if (item.getItemId() == 40070 && petType.canEvolve()) {
			evolvePet(pc, target);
		}
		if (!petType.canEvolve()) {
			usePetWeaponArmor(target, item);
		}
	}
	
	private void usePetWeaponArmor(L1NpcInstance target, L1ItemInstance item) {
		if (!(target instanceof L1PetInstance)) {
			return;
		}
		L1PetInstance pet = (L1PetInstance) target;
		L1PetItem petItem = PetItemTable.getInstance().getTemplate(
				item.getItemId());
		if (petItem == null) {
			return;
		}
		if (petItem.getUseType() == 22) { // 牙齒
//			System.out.println("宠物使用武器");
			pet.usePetWeapon(item);
		} else if (petItem.getUseType() == 2) { // 盔甲
//			System.out.println("宠物使用盔甲");
			pet.usePetArmor(item);
		}
	}

	private final static String receivableImpls[] = new String[] { "L1Npc", // NPC
			"L1Monster", // 
			"L1Guardian", // 森守护者
			"L1Teleporter", // 
			"L1Guard" }; // 

	private boolean isNpcItemReceivable(L1Npc npc) {
		for (String impl : receivableImpls) {
			if (npc.getImpl().equals(impl)) {
				return true;
			}
		}
		return false;
	}

	private void tamePet(L1PcInstance pc, L1NpcInstance target) {
		if (target instanceof L1PetInstance
				|| target instanceof L1SummonInstance) {
			return;
		}

		int petcost = 0;
		Object[] petlist = pc.getPetList().values().toArray();
		for (Object pet : petlist) {
			petcost += ((L1NpcInstance) pet).getPetcost();
		}
		int charisma = pc.getCha();
		if (pc.isCrown()) { // 君主
			charisma += 6;
		} else if (pc.isElf()) { // 
			charisma += 12;
		} else if (pc.isWizard()) { // WIZ
			charisma += 6;
		} else if (pc.isDarkelf()) { // DE
			charisma += 6;
		}
		charisma -= petcost;

		L1PcInventory inv = pc.getInventory();
		if (charisma >= 6 && inv.getSize() < 180) {
			if (isTamePet(target)) {
				L1ItemInstance petamu = inv.storeItem(40314, 1); // 
				if (petamu != null) {
					new L1PetInstance(target, pc, petamu.getId());
					pc.sendPackets(new S_ItemName(petamu));
				}
			} else {
				pc.sendPackets(new S_ServerMessage(324)); // 失败。
			}
		}
	}

	private void evolvePet(L1PcInstance pc, L1NpcInstance target) {
		if (!(target instanceof L1PetInstance)) {
			return;
		}
		L1PcInventory inv = pc.getInventory();
		L1PetInstance pet = (L1PetInstance) target;
		L1ItemInstance petamu = inv.getItem(pet.getItemObjId());
		if (pet.getLevel() >= 30 && // Lv30以上
				pc == pet.getMaster() && // 自分
				petamu != null) {
			L1ItemInstance highpetamu = inv.storeItem(40316, 1);
			if (highpetamu != null) {
				pet.evolvePet(highpetamu.getId());
				pc.sendPackets(new S_ItemName(highpetamu));
				inv.removeItem(petamu, 1);
			}
		}
	}

	private boolean isTamePet(L1NpcInstance npc) {
		boolean isSuccess = false;
		int npcId = npc.getNpcTemplate().get_npcId();
		if (npcId == 45313) { // 
			if (npc.getMaxHp() / 3 > npc.getCurrentHp() // HP1/3未满1/16确率
					&& _random.nextInt(16) == 15) {
				isSuccess = true;
			}
		} else {
			if (npc.getMaxHp() / 3 > npc.getCurrentHp()) {
				isSuccess = true;
			}
		}

		if (npcId == 45313 || npcId == 45044 || npcId == 45711) { // 、、纪州犬子犬
			if (npc.isResurrect()) { // RES后不可
				isSuccess = false;
			}
		}

		return isSuccess;
	}

	@Override
	public String getType() {
		return C_GIVE_ITEM;
	}
}
