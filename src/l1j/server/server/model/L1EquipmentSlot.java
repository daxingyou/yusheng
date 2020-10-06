/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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
import static l1j.server.server.model.skill.L1SkillId.DETECTION;
import static l1j.server.server.model.skill.L1SkillId.ENCHANT_WEAPON;
import static l1j.server.server.model.skill.L1SkillId.EXTRA_HEAL;
import static l1j.server.server.model.skill.L1SkillId.GREATER_HASTE;
import static l1j.server.server.model.skill.L1SkillId.HASTE;
import static l1j.server.server.model.skill.L1SkillId.HEAL;
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_DEX; // 恶血状态判断 
import static l1j.server.server.model.skill.L1SkillId.PHYSICAL_ENCHANT_STR;

import java.util.ArrayList;

import l1j.server.data.ItemClass;
import l1j.server.server.datatables.EnchantDmgReductionTable;
import l1j.server.server.datatables.lock.CharSkillReading;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Ability;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_DelSkill;
import l1j.server.server.serverpackets.S_EquipmentWindow;
import l1j.server.server.serverpackets.S_Invis;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_RemoveObject;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_SkillHaste;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1EnchantDmgreduction;
import l1j.server.server.templates.L1Item;

public class L1EquipmentSlot {
	private L1PcInstance _owner;

	/**
	 * 效果中
	 */
	private ArrayList<L1ArmorSet> _currentArmorSet;

	private L1ItemInstance _weapon;
	private ArrayList<L1ItemInstance> _armors;

	public L1EquipmentSlot(L1PcInstance owner) {
		_owner = owner;

		_armors = new ArrayList<L1ItemInstance>();
		_currentArmorSet = new ArrayList<L1ArmorSet>();
	}

	private void setWeapon(L1ItemInstance weapon) {
		_owner.setWeapon(weapon);
		_owner.setCurrentWeapon(weapon.getItem().getType1());
		_weapon = weapon;
		
		// 武器变身 
		switch(weapon.getItem().getItemId())
		{
			case 195: case 100195: case 200195: {
				L1PolyMorph.doPoly(_owner, 6137, 0);
			}break;
			case 245: case 100245: case 200245: {
				L1PolyMorph.doPoly(_owner, 6002, 0);
			}break;
		}
		// 武器变身  end
		_owner.sendPackets(new S_EquipmentWindow(weapon.getId(),
				S_EquipmentWindow.EQUIPMENT_INDEX_WEAPON, true));
	}
	
	private void yuanguTouShi(final int iseq){
		switch (_owner.getClassId()) {
		case 0:
		case 1:
		case 61:
		case 48:
			_owner.addAc(-8 * iseq);
			_owner.addMaxHp(80 * iseq);
			_owner.addMr(10 * iseq);
			_owner.addDamageReductionByRing(2 * iseq);
			if (iseq > 0){
				_owner.sendPackets(new S_SystemMessage("激活隐藏属性:防御+8 HP+80 魔防+10 PVP减少伤害+2"));
			}
			break;
		case 138:
		case 37:
			_owner.addAc(-4 * iseq);
			_owner.addMaxHp(30 * iseq);
			_owner.addDex(1 * iseq);
			_owner.addMaxMp(10 * iseq);
			if (iseq > 0){
				_owner.sendPackets(new S_SystemMessage("激活隐藏属性:防御+4 HP+30 MP+10 敏捷+1"));
			}
			break;
		case 734:
		case 1186:
			_owner.addAc(-3 * iseq);
			_owner.addMaxHp(20 * iseq);
			_owner.addSp(2 * iseq);
			_owner.addMpr(2 * iseq);
			_owner.addMaxMp(50 * iseq);
			_owner.addMr(7 * iseq);
			if (iseq > 0){
				_owner.sendPackets(new S_SystemMessage("激活隐藏属性:防御+3 HP+20 MP+50 魔力恢复+2  魔法攻击+2  魔法防御+7"));
			}
			break;
		case 2786:
		case 2796:
			_owner.addAc(-4 * iseq);
			_owner.addMaxHp(30 * iseq);
			_owner.addDmgup(1 * iseq);
			_owner.addDamageUpAndRandomByHelm(15 * iseq,5 * iseq);
			if (iseq > 0){
				_owner.sendPackets(new S_SystemMessage("激活隐藏属性:防御+4 HP+30   额外伤害+1 5%机率伤害增加15"));
			}
			break;
		default:
			break;
		}
	}

	public L1ItemInstance getWeapon() {
		return _weapon;
	}
	
	public void OnChanceAc()
	{
		_owner.setArmorAc(0);
		for (L1ItemInstance armor : _armors) {
			if (armor.getItem().getType2() == 2){
				if (armor.getItem().getType() == 9 || armor.getItem().getType() == 15){
					_owner.addArmorAc(armor.getItem().get_ac());
				}else{
					_owner.addArmorAc(armor.getItem().get_ac() - armor.getEnchantLevel() - armor.getEnchant());
				}
			}
		}
	}

	private void setArmor(L1ItemInstance armor) {
		final L1Item item = armor.getItem();
		final int itemId = armor.getItem().getItemId();
		final int use_type = armor.getItem().getUseType();
		if (item.getType2() == 2 && item.getType() == 9 && armor.getEnchantLevel() > 0){
			_owner.addSp(armor.getEnchantLevel());
			_owner.addDamageReductionByRing(armor.getEnchantLevel());
		}
//		if (item.getType2() == 2 && item.getType() == 16){
//			_owner.sendPackets(new S_PacketBox(S_PacketBox.VIP_ICON, 0, false));
//			_owner.sendPackets(new S_PacketBox(S_PacketBox.VIP_ICON, 5, true));
//		}
		if (item.getItemId() == 500050 || item.getItemId() == 500051){
			_owner.sendPackets(new S_PacketBox(S_PacketBox.VIP_ICON, 0, false));
			_owner.sendPackets(new S_PacketBox(S_PacketBox.VIP_ICON, 5, true));
		}
		_owner.addDmgup(armor.getItem().getDmgModifier());
		_owner.addBowDmgup(armor.getItem().getDmgModifier());
		_owner.addHitup(armor.getItem().getHitModifier());
		_owner.addBowHitup(armor.getItem().getHitModifier());
		
		
		if (itemId == 25068){
			yuanguTouShi(1);
		}
		
		if (itemId == 21200) {
			_owner.addMaxHp(armor.getUpdateHP());
			_owner.addMaxMp(armor.get_updateMP());
			_owner.addHpr(armor.get_updateMPR());
			_owner.addMpr(armor.get_updateHPR());
			_owner.addDmgup(armor.get_updateDMG());
			_owner.addHitup(armor.get_updateHOTDMG());
			_owner.addBowDmgup(armor.get_updateBOWDMG());
			_owner.addBowHitup(armor.get_updateHOTBOWDMG());
			_owner.addSp(armor.get_updateSP());
		}
		//以下是蓝瓜臂甲 穿戴后的属性
		if (itemId == 25063 || itemId == 25064){
			final int new_enchant = armor.getEnchantLevel() - armor.getItem().get_safeenchant();
			//这是加新强化后的属性
			if (new_enchant == 1){//+5
				_owner.addDamageReduction(2,10);
				_owner.addMaxHp(10);
				_owner.addMaxMp(10);
				_owner.addMr(2);
			}else if (new_enchant == 2){//+6
				_owner.addDamageReduction(3,12);
				_owner.addMaxHp(20);
				_owner.addMaxMp(20);
				_owner.addMr(4);
			}else if (new_enchant == 3){//+7
				_owner.addDamageReduction(4,13);
				_owner.addMaxHp(30);
				_owner.addMaxMp(30);
				_owner.addMr(6);
			}else if (new_enchant == 4){//+8
				_owner.addDamageReduction(5,14);
				_owner.addMaxHp(40);
				_owner.addMaxMp(40);
				_owner.addMr(8);
			}else if (new_enchant == 5){//+9
				_owner.addDamageReduction(8,15);
				_owner.addMaxHp(50);
				_owner.addMaxMp(50);
				_owner.addMr(10);
			}else if (new_enchant == 6){//+9
				_owner.addDamageReduction(8,15);
				_owner.addMaxHp(60);
				_owner.addMaxMp(60);
				_owner.addMr(9);
			}else if (new_enchant == 7){//+9
				_owner.addDamageReduction(8,15);
				_owner.addMaxHp(70);
				_owner.addMaxMp(70);
				_owner.addMr(11);
			}else if (new_enchant == 8){//+9
				_owner.addDamageReduction(8,15);
				_owner.addMaxHp(80);
				_owner.addMaxMp(80);
				_owner.addMr(13);
			}else if (new_enchant == 9){//+9
				_owner.addDamageReduction(8,15);
				_owner.addMaxHp(80);
				_owner.addMaxMp(80);
				_owner.addMr(17);
			}
		}
		//以上是蓝瓜臂甲 强化变化属性
//		_owner.addAc(item.get_ac() - armor.getEnchantLevel() - armor.getEnchant()); // - armor.getEnchant() 铠甲防御修正 
		_owner.addDamageReductionByArmor(item.getDamageReduction());
		_owner.addWeightReduction(item.getWeightReduction());
		_owner.addBowHitRate(item.getBowHitRate());
		_owner.addEarth(item.get_defense_earth());
		_owner.addWind(item.get_defense_wind());
		_owner.addWater(item.get_defense_water());
		_owner.addFire(item.get_defense_fire());

		if (armor.getBless() == 0){
			_owner.addMaxHp(20);
		}
		
		_armors.add(armor);

		for (L1ArmorSet armorSet : L1ArmorSet.getAllSet()) {
			if (armorSet.isPartOfSet(itemId) && armorSet.isValid(_owner)) {
				armorSet.giveEffect(_owner);
				_currentArmorSet.add(armorSet);
			}
		}

		if (itemId == 20077 || itemId == 20062 || itemId == 120077) {
			if (!_owner.hasSkillEffect(L1SkillId.INVISIBILITY)) {
				_owner.killSkillEffectTimer(L1SkillId.BLIND_HIDING);
				_owner.setSkillEffect(L1SkillId.INVISIBILITY, 0);
				_owner.sendPackets(new S_Invis(_owner.getId(), 1));
				_owner.broadcastPacket(new S_RemoveObject(_owner));
			}
		}
		if (itemId == 20288 /*&& armor.getChargeCount() > 0*/) { // ROTC
			_owner.sendPackets(new S_Ability(_owner,1, true));
		}
		if (itemId == 20383) { // 骑马用
			if (armor.getChargeCount() != 0) {
				armor.setChargeCount(armor.getChargeCount() - 1);
				_owner.getInventory().updateItem(armor, L1PcInventory
						.COL_CHARGE_COUNT);
			}
		}
/*		if (itemId == 20287) {
			if (_owner.getClanid() != 0) { // 所属中
				L1Clan clan = L1World.getInstance().getClan(_owner.getClanname());
				if (clan != null) {
					if (_owner.getId() == clan.getLeaderId()) {
						if (_owner.getQuest().isEnd(L1Quest.QUEST_LEVEL45)) {
							_owner.setClanRank(L1Clan.CLAN_RANK_LEAGUE_PRINCE);
						}else {
							_owner.setClanRank(L1Clan.CLAN_RANK_PRINCE);
						}
					}
				}
			}
		}*/
		OnChanceAc();
		if (_owner.isLoginToServer() && armor.isEquipped()) {
			switch (use_type) { // 显示装备中的武器与防具
			case 2: // 盔甲
				_owner.sendPackets(new S_EquipmentWindow(armor.getId(),S_EquipmentWindow.EQUIPMENT_INDEX_ARMOR, true));
				break;
			case 18: // T恤
				_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
						S_EquipmentWindow.EQUIPMENT_INDEX_T, true));
				break;
			case 19: // 斗篷
				_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
						S_EquipmentWindow.EQUIPMENT_INDEX_CLOAK, true));
				break;
			case 20: // 手套
				_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
						S_EquipmentWindow.EQUIPMENT_INDEX_GLOVE, true));
				break;
			case 21: // 长靴
				_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
						S_EquipmentWindow.EQUIPMENT_INDEX_BOOTS, true));
				break;
			case 22: // 头盔
				_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
						S_EquipmentWindow.EQUIPMENT_INDEX_HEML, true));
				break;
			case 23: // 戒指
				if (_owner.getEquipmentRing1ID() == 0) {
					_owner.setEquipmentRing1ID(armor.getId());
					_owner.sendPackets(new S_EquipmentWindow(_owner.getEquipmentRing1ID(),S_EquipmentWindow.EQUIPMENT_INDEX_RING1, true));
				} else if (_owner.getEquipmentRing2ID() == 0 && armor.getId() != _owner.getEquipmentRing1ID()) {
					_owner.setEquipmentRing2ID(armor.getId());
					_owner.sendPackets(new S_EquipmentWindow(_owner.getEquipmentRing2ID(),S_EquipmentWindow.EQUIPMENT_INDEX_RING2, true));
				}
				break;
			case 24: // 项链
				_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
						S_EquipmentWindow.EQUIPMENT_INDEX_NECKLACE, true));
				break;
			case 25: // 盾牌
				_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
						S_EquipmentWindow.EQUIPMENT_INDEX_SHIELD, true));
				break;
			case 37: // 腰带
				_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
						S_EquipmentWindow.EQUIPMENT_INDEX_BELT, true));
				break;
			case 40: // 耳环
				_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
						S_EquipmentWindow.EQUIPMENT_INDEX_EARRING, true));
				break;
			case 43: // 副助道具-右
				_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
						S_EquipmentWindow.EQUIPMENT_INDEX_AMULET1, true));
				break;
			case 44: // VIP戒指
				_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
						S_EquipmentWindow.EQUIPMENT_INDEX_RING3,true));
				break;
			case 45: // VIP戒指
				_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
						S_EquipmentWindow.EQUIPMENT_INDEX_RING4, true));
				break;
			case 48: // 副助道具-右下
				_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
						S_EquipmentWindow.EQUIPMENT_INDEX_AMULET4, true));
				break;
			case 47: // 副助道具-左下
				_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
						S_EquipmentWindow.EQUIPMENT_INDEX_AMULET5, true));
				break;
			default:
				break;
			}
		}
	}

	public ArrayList<L1ItemInstance> getArmors() {
		return _armors;
	}

	private void removeWeapon(L1ItemInstance weapon) {
		int itemId = weapon.getItem().getItemId();
		//int classId = _owner.getClassId(); // 武器变身 
		
		// 武器变身 
		switch(itemId)
		{
			case 195: case 100195: case 200195: {
				if (_owner.getTempCharGfx() == 6137) {
					L1PolyMorph.undoPoly(_owner);
				}
			}break;
			case 245: case 100245: case 200245: {
				if (_owner.getTempCharGfx() == 6002) {
					L1PolyMorph.undoPoly(_owner);
				}
			}break;
		}
		// 武器变身  end
		
		_owner.setWeapon(null);
		_owner.setCurrentWeapon(0);
		_weapon = null;
		if (_owner.hasSkillEffect(L1SkillId.COUNTER_BARRIER)) {
			_owner.removeSkillEffect(L1SkillId.COUNTER_BARRIER);
		}
		_owner.sendPackets(new S_EquipmentWindow(weapon.getId(),
				S_EquipmentWindow.EQUIPMENT_INDEX_WEAPON, false));
	}

	private void removeArmor(L1ItemInstance armor) {
		final L1Item item = armor.getItem();
		final int itemId = armor.getItem().getItemId();
		final int use_type = armor.getItem().getUseType();
		
		if (item.getType2() == 2 && item.getType() == 9 && armor.getEnchantLevel() > 0){
			_owner.addSp(-armor.getEnchantLevel());
			_owner.addDamageReductionByRing(-armor.getEnchantLevel());
		}
		_owner.addDmgup(-armor.getItem().getDmgModifier());
		_owner.addBowDmgup(-armor.getItem().getDmgModifier());
		_owner.addHitup(-armor.getItem().getHitModifier());
		_owner.addBowHitup(-armor.getItem().getHitModifier());
		
		if (itemId == 25068){
			yuanguTouShi(-1);
		}
//		if (item.getType2() == 2 && item.getType() == 16){
//			_owner.sendPackets(new S_PacketBox(S_PacketBox.VIP_ICON, 5, false));
//			_owner.sendPackets(new S_PacketBox(S_PacketBox.VIP_ICON, 0, true));
//		}
		if (item.getItemId() == 500050 || item.getItemId() == 500051){
			_owner.sendPackets(new S_PacketBox(S_PacketBox.VIP_ICON, 5, false));
			_owner.sendPackets(new S_PacketBox(S_PacketBox.VIP_ICON, 0, true));
		}
		if (itemId == 21200) {
			_owner.addMaxHp(-armor.getUpdateHP());
			_owner.addMaxMp(-armor.get_updateMP());
			_owner.addHpr(-armor.get_updateMPR());
			_owner.addMpr(-armor.get_updateHPR());
			_owner.addDmgup(-armor.get_updateDMG());
			_owner.addHitup(-armor.get_updateHOTDMG());
			_owner.addBowDmgup(-armor.get_updateBOWDMG());
			_owner.addBowHitup(-armor.get_updateHOTBOWDMG());
			_owner.addSp(-armor.get_updateSP());
		}
		//以下是蓝瓜臂甲 脱掉后的属性
		if (itemId == 25063 || itemId == 25064){
			final int old_enchant = armor.getEnchantLevel() - armor.getItem().get_safeenchant();
			//这是减去原来强化的属性
			if (old_enchant == 1){//+5
				_owner.addDamageReduction(-2,-10);
				_owner.addMaxHp(-10);
				_owner.addMaxMp(-10);
				_owner.addMr(-2);
			}else if (old_enchant == 2){//+6
				_owner.addDamageReduction(-3,-12);
				_owner.addMaxHp(-20);
				_owner.addMaxMp(-20);
				_owner.addMr(-4);
			}else if (old_enchant == 3){//+7
				_owner.addDamageReduction(-4,-13);
				_owner.addMaxHp(-30);
				_owner.addMaxMp(-30);
				_owner.addMr(-6);
			}else if (old_enchant == 4){//+8
				_owner.addDamageReduction(-5,-14);
				_owner.addMaxHp(-40);
				_owner.addMaxMp(-40);
				_owner.addMr(-8);
			}else if (old_enchant == 5){//+9
				_owner.addDamageReduction(-8,-15);
				_owner.addMaxHp(-50);
				_owner.addMaxMp(-50);
				_owner.addMr(-10);
			}else if (old_enchant == 6){//+9
				_owner.addDamageReduction(-8,-15);
				_owner.addMaxHp(-60);
				_owner.addMaxMp(-60);
				_owner.addMr(-9);
			}else if (old_enchant == 7){//+9
				_owner.addDamageReduction(-8,-15);
				_owner.addMaxHp(-70);
				_owner.addMaxMp(-70);
				_owner.addMr(-11);
			}else if (old_enchant == 8){//+9
				_owner.addDamageReduction(-8,-15);
				_owner.addMaxHp(-80);
				_owner.addMaxMp(-80);
				_owner.addMr(-13);
			}else if (old_enchant == 9){//+9
				_owner.addDamageReduction(-8,-15);
				_owner.addMaxHp(-80);
				_owner.addMaxMp(-80);
				_owner.addMr(-17);
			}
		}
		//以上是蓝瓜臂甲 强化变化属性
//		_owner.addAc(-(item.get_ac() - armor.getEnchantLevel() - armor.getEnchant())); // - armor.getEnchant() 铠甲防御修正 
		_owner.addDamageReductionByArmor(-item.getDamageReduction());
		_owner.addWeightReduction(-item.getWeightReduction());
		_owner.addBowHitRate(-item.getBowHitRate());
		_owner.addEarth(-item.get_defense_earth());
		_owner.addWind(-item.get_defense_wind());
		_owner.addWater(-item.get_defense_water());
		_owner.addFire(-item.get_defense_fire());

		if (armor.getBless() == 0){
			_owner.addMaxHp(-20);
		}
		
		for (L1ArmorSet armorSet : L1ArmorSet.getAllSet()) {
			if (armorSet.isPartOfSet(itemId)
					&& _currentArmorSet.contains(armorSet)
					&& !armorSet.isValid(_owner)) {
				armorSet.cancelEffect(_owner);
				_currentArmorSet.remove(armorSet);
			}
		}

		if (itemId == 20077 || itemId == 20062 || itemId == 120077) {
			_owner.delInvis(); // 状态解除
		}
		if (itemId == 20288) { // ROTC
			_owner.sendPackets(new S_Ability(_owner,1, false));
		}

		_armors.remove(armor);
		
		OnChanceAc();
		
		switch (use_type) { // 显示装备中的武器与防具
		case 2: // 盔甲
			_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
					S_EquipmentWindow.EQUIPMENT_INDEX_ARMOR, false));
			break;
		case 18: // T恤
			_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
					S_EquipmentWindow.EQUIPMENT_INDEX_T, false));
			break;
		case 19: // 斗篷
			_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
					S_EquipmentWindow.EQUIPMENT_INDEX_CLOAK, false));
			break;
		case 20: // 手套
			_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
					S_EquipmentWindow.EQUIPMENT_INDEX_GLOVE, false));
			break;
		case 21: // 长靴
			_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
					S_EquipmentWindow.EQUIPMENT_INDEX_BOOTS, false));
			break;
		case 22: // 头盔
			_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
					S_EquipmentWindow.EQUIPMENT_INDEX_HEML, false));
			break;
		case 23: // 戒指
			// final int type = armor.getItem().getType();
			// if (!armor.isEquipped()) {
			if (_owner.getEquipmentRing1ID() == armor.getId()) {
				_owner.sendPackets(new S_EquipmentWindow(_owner
						.getEquipmentRing1ID(),
						S_EquipmentWindow.EQUIPMENT_INDEX_RING1, false));
				_owner.setEquipmentRing1ID(0);
			} else if (_owner.getEquipmentRing2ID() == armor.getId()) {
				_owner.sendPackets(new S_EquipmentWindow(_owner
						.getEquipmentRing2ID(),
						S_EquipmentWindow.EQUIPMENT_INDEX_RING2, false));
				_owner.setEquipmentRing2ID(0);
			}
			// }
			break;
		case 24: // 项链
			_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
					S_EquipmentWindow.EQUIPMENT_INDEX_NECKLACE, false));
			break;
		case 25: // 盾牌
			_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
					S_EquipmentWindow.EQUIPMENT_INDEX_SHIELD, false));
			break;
		case 37: // 腰带
			_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
					S_EquipmentWindow.EQUIPMENT_INDEX_BELT, false));
			break;
		case 40: // 耳环
			_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
					S_EquipmentWindow.EQUIPMENT_INDEX_EARRING, false));
			break;
		case 43: // 副助道具-右
			_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
					S_EquipmentWindow.EQUIPMENT_INDEX_AMULET1, false));
			break;
		case 44: // VIP戒指
			_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
					S_EquipmentWindow.EQUIPMENT_INDEX_RING3, false));
			break;
		case 45: // VIP戒指
			_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
					S_EquipmentWindow.EQUIPMENT_INDEX_RING4, false));
			break;
		case 48: // 副助道具-右下
			_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
					S_EquipmentWindow.EQUIPMENT_INDEX_AMULET4, false));
			break;
		case 47: // 副助道具-左下
			_owner.sendPackets(new S_EquipmentWindow(armor.getId(),
					S_EquipmentWindow.EQUIPMENT_INDEX_AMULET5, false));
			break;
		default:
			break;
		}
	}

	public void set(L1ItemInstance equipment) {
		L1Item item = equipment.getItem();
		if (item.getType2() == 0) {
			return;
		}
		final L1EnchantDmgreduction ItemEnchant = EnchantDmgReductionTable.get().getEnchantDmgReduction(item.getItemId(), equipment.getEnchantLevel());
		if (ItemEnchant != null){
			_owner.addDamageReductionByArmor(ItemEnchant.get_dmgReduction());
			_owner.addAc(ItemEnchant.get_ac());
			_owner.addMaxHp(ItemEnchant.get_hp());
			_owner.addMaxMp(ItemEnchant.get_mp());
			_owner.addHpr(ItemEnchant.get_hpr());
			_owner.addMpr(ItemEnchant.get_mpr());
			_owner.addStr(ItemEnchant.get_str());
			_owner.addDex(ItemEnchant.get_dex());
			_owner.addInt(ItemEnchant.get_Intel());
			_owner.addWis(ItemEnchant.get_wis());
			_owner.addCha(ItemEnchant.get_cha());
			_owner.addCon(ItemEnchant.get_con());
			_owner.addSp(ItemEnchant.get_sp());
			_owner.addMr(ItemEnchant.get_mr());
		}
		equipment.setEnchantSkill(_owner);
		
		_owner.addMaxHp(item.get_addhp());
		_owner.addMaxMp(item.get_addmp());
		_owner.addStr(item.get_addstr());
		_owner.addCon(item.get_addcon());
		_owner.addDex(item.get_adddex());
		_owner.addInt(item.get_addint());
		_owner.addWis(item.get_addwis());
		if (item.get_addwis() != 0) {
			_owner.resetBaseMr();
		}
		_owner.addCha(item.get_addcha());

		int addMr = 0;
		addMr += equipment.getMr();
		if (item.getItemId() == 20236 && _owner.isElf()) {
			addMr += 5;
		}
		if (addMr != 0) {
			_owner.addMr(addMr);
			_owner.sendPackets(new S_SPMR(_owner));
		}
		if (item.get_addsp() != 0) {
			_owner.addSp(item.get_addsp());
			_owner.sendPackets(new S_SPMR(_owner));
		}
		if (item.isHasteItem()) {
			_owner.addHasteItemEquipped(1);
			_owner.removeHasteSkillEffect();
			if (_owner.getMoveSpeed() != 1) {
				_owner.setMoveSpeed(1);
				_owner.sendPackets(new S_SkillHaste(_owner.getId(), 1, -1));
				_owner.broadcastPacket(new S_SkillHaste(_owner.getId(), 1, 0));
			}
		}

/*		if (item.getType2() == 1) {
			setWeapon(equipment);
		} else if (item.getType2() == 2) {
			setArmor(equipment);
		}*/
		
		switch (item.getType2() ) {
		case 1:// 武器
			this.setWeapon(equipment);
			ItemClass.get().item_weapon(true, this._owner, equipment);
			break;
			
		case 2:// 防具
			this.setArmor(equipment);
			//this.setMagic(eq);
			ItemClass.get().item_armor(true, this._owner, equipment);
			break;
		}
	}

	public void remove(L1ItemInstance equipment) {
		L1Item item = equipment.getItem();
		if (item.getType2() == 0) {
			return;
		}
		
		final L1EnchantDmgreduction ItemEnchant = EnchantDmgReductionTable.get().getEnchantDmgReduction(item.getItemId(), equipment.getEnchantLevel());
		if (ItemEnchant != null){
			_owner.addDamageReductionByArmor(-ItemEnchant.get_dmgReduction());
			_owner.addAc(-ItemEnchant.get_ac());
			_owner.addMaxHp(-ItemEnchant.get_hp());
			_owner.addMaxMp(-ItemEnchant.get_mp());
			_owner.addHpr(-ItemEnchant.get_hpr());
			_owner.addMpr(-ItemEnchant.get_mpr());
			_owner.addStr(-ItemEnchant.get_str());
			_owner.addDex(-ItemEnchant.get_dex());
			_owner.addInt(-ItemEnchant.get_Intel());
			_owner.addWis(-ItemEnchant.get_wis());
			_owner.addCha(-ItemEnchant.get_cha());
			_owner.addCon(-ItemEnchant.get_con());
			_owner.addSp(-ItemEnchant.get_sp());
			_owner.addMr(-ItemEnchant.get_mr());
		}
		
		_owner.addMaxHp(-item.get_addhp());
		_owner.addMaxMp(-item.get_addmp());
		_owner.addStr((byte) -item.get_addstr());
		_owner.addCon((byte) -item.get_addcon());
		_owner.addDex((byte) -item.get_adddex());
		_owner.addInt((byte) -item.get_addint());
		_owner.addWis((byte) -item.get_addwis());
		if (item.get_addwis() != 0) {
			_owner.resetBaseMr();
		}
		_owner.addCha((byte) -item.get_addcha());

		int addMr = 0;
		addMr -= equipment.getMr();
		if (item.getItemId() == 20236 && _owner.isElf()) {
			addMr -= 5;
		}
		if (addMr != 0) {
			_owner.addMr(addMr);
			_owner.sendPackets(new S_SPMR(_owner));
		}
		if (item.get_addsp() != 0) {
			_owner.addSp(-item.get_addsp());
			_owner.sendPackets(new S_SPMR(_owner));
		}
		if (item.isHasteItem()) {
			_owner.addHasteItemEquipped(-1);
			if (_owner.getHasteItemEquipped() == 0) {
				_owner.setMoveSpeed(0);
				_owner.sendPackets(new S_SkillHaste(_owner.getId(), 0, 0));
			}
		}

/*		if (item.getType2() == 1) {
			removeWeapon(equipment);
		} else if (item.getType2() == 2) {
			removeArmor(equipment);
		}*/
		switch (item.getType2()) {
		case 1:// 武器
			this.removeWeapon(equipment);
			ItemClass.get().item_weapon(false, this._owner, equipment);
			break;
			
		case 2:// 防具
			//this.removeMagic(this._owner.getId(), eq);
			this.removeArmor(equipment);
			ItemClass.get().item_armor(false, this._owner, equipment);
			break;
		}
	}

	public void setMagicHelm(L1ItemInstance item) {
		if (item.getItemId() == 20013) {
			_owner.setSkillMastery(PHYSICAL_ENCHANT_DEX);
			_owner.setSkillMastery(HASTE);
			_owner.sendPackets(new S_AddSkill(null,0, 0, 0, 2, 0, 4, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0));
		}
		if (item.getItemId() == 20014) {
			if (!CharSkillReading.get().spellCheck(_owner.getId(),HEAL)) {
				_owner.setSkillMastery(HEAL);
				_owner.sendPackets(new S_AddSkill(null,1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0));
			}
			if (!CharSkillReading.get().spellCheck(_owner.getId(),EXTRA_HEAL)) {
				_owner.setSkillMastery(EXTRA_HEAL);
				_owner.sendPackets(new S_AddSkill(null,0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0));
			}
			
		}
		if (item.getItemId() == 20015) {
			if (!CharSkillReading.get().spellCheck(_owner.getId(), ENCHANT_WEAPON)) { // 
				_owner.setSkillMastery(ENCHANT_WEAPON);
				_owner.sendPackets(new S_AddSkill(null,0, 8, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0));
			}
			if (!CharSkillReading.get().spellCheck(_owner.getId(), DETECTION)) { // 
				_owner.setSkillMastery(DETECTION);
				_owner.sendPackets(new S_AddSkill(null,0, 16, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0));
			}
			if (!CharSkillReading.get().spellCheck(_owner.getId(), PHYSICAL_ENCHANT_STR)) { // ：STR
				_owner.setSkillMastery(PHYSICAL_ENCHANT_STR);
				_owner.sendPackets(new S_AddSkill(null,0, 0, 0, 0, 0, 2, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0));
			}
		}
		if (item.getItemId() == 20008) {
			_owner.setSkillMastery(HASTE);
			_owner.sendPackets(new S_AddSkill(null,0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0));
		}
		if (item.getItemId() == 20023) {
			_owner.setSkillMastery(HASTE);
			_owner.setSkillMastery(GREATER_HASTE);
			_owner.sendPackets(new S_AddSkill(null,0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,0,0,0,0));
		}
	}

	public void removeMagicHelm(int objectId, L1ItemInstance item) {
		if (item.getItemId() == 20013) { // 魔法：迅速
			if (!CharSkillReading.get().spellCheck(objectId, PHYSICAL_ENCHANT_DEX)) { // ：DEX
				_owner.removeSkillMastery(PHYSICAL_ENCHANT_DEX);
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 2, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!CharSkillReading.get().spellCheck(objectId, HASTE)) { // 
				_owner.removeSkillMastery(HASTE);
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
		}
		if (item.getItemId() == 20014) { // 魔法：治愈
			if (!CharSkillReading.get().spellCheck(objectId, HEAL)) { // 
				_owner.removeSkillMastery(HEAL);
				_owner.sendPackets(new S_DelSkill(1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!CharSkillReading.get().spellCheck(objectId, EXTRA_HEAL)) { // 
				_owner.removeSkillMastery(EXTRA_HEAL);
				_owner.sendPackets(new S_DelSkill(0, 0, 4, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
		}
		if (item.getItemId() == 20015) { // 魔法：力
			if (!CharSkillReading.get().spellCheck(objectId, ENCHANT_WEAPON)) { // 
				_owner.removeSkillMastery(ENCHANT_WEAPON);
				_owner.sendPackets(new S_DelSkill(0, 8, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!CharSkillReading.get().spellCheck(objectId, DETECTION)) { // 
				_owner.removeSkillMastery(DETECTION);
				_owner.sendPackets(new S_DelSkill(0, 16, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!CharSkillReading.get().spellCheck(objectId, PHYSICAL_ENCHANT_STR)) { // ：STR
				_owner.removeSkillMastery(PHYSICAL_ENCHANT_STR);
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 2, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
		}
		if (item.getItemId() == 20008) { // 
			if (!CharSkillReading.get().spellCheck(objectId, HASTE)) { // 
				_owner.removeSkillMastery(HASTE);
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 4, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
		}
		if (item.getItemId() == 20023) { // 
			if (!CharSkillReading.get().spellCheck(objectId, HASTE)) { // 
				_owner.removeSkillMastery(HASTE);
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 4, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
			if (!CharSkillReading.get().spellCheck(objectId, GREATER_HASTE)) { // 
				_owner.removeSkillMastery(GREATER_HASTE);
				_owner.sendPackets(new S_DelSkill(0, 0, 0, 0, 0, 0, 32, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			}
		}
	}

}
