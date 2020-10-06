package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_OwnCharAttrDef;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;

public class WeaponQieHuan extends ItemExecutor {

    /**
	 *
	 */
    private WeaponQieHuan() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new WeaponQieHuan();
    }

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		if (pc.getWeaponItemObjId(0) > 0 && pc.getWeaponItemObjId(1) > 0){
			if (pc.getWeapon() != null){
				if (pc.getWeapon().getItem().getType1() == 4 || pc.getWeapon().getItem().getType1() == 46){
					for(final L1ItemInstance armor : pc.getInventory().getItems()){
						if (armor.isEquipped() && armor.getItem().getType2() == 2 && armor.getItem().getType() == 7){
							pc.getInventory().setEquipped(armor, false);
							break;
						}
					}
					UseWeapon(pc,pc.getWeaponItemObjId(1));
				}else if (pc.getWeapon().getItem().getType1() == 50){
					UseWeapon(pc,pc.getWeaponItemObjId(0));
					if (pc.getWeaponItemObjId(2) > 0){
						UseArmor(pc,pc.getWeaponItemObjId(2));
					}
				}
			}else{//没有武器 默认单手
				UseWeapon(pc,pc.getWeaponItemObjId(0));
				if (pc.getWeaponItemObjId(2) > 0){
					UseArmor(pc,pc.getWeaponItemObjId(2));
				}
			}
			return;
		}
		int n1 = 3;
		int n2 = 8;
		int n3 = 13;
		for(final L1ItemInstance weaponitem : pc.getInventory().getItems()){
			final L1Item item2 = weaponitem.getItem();
			if (item2.getType2() == 1){
				if ((item2.getType1() == 4 || item2.getType1() == 46) && n1 < 8){
					pc.setWeaponItemObjId(weaponitem.getId(),n1);
					n1++;
				}else if (item2.getType1() == 50 && n2 < 13){
					pc.setWeaponItemObjId(weaponitem.getId(),n2);
					n2++;
				}
			}else if (item2.getType2() == 2){
				if (item2.getType() == 7 && n3 < 18){
					pc.setWeaponItemObjId(weaponitem.getId(),n3);
					n3++;
				}
			}
			
			if (n1 >= 8 && n2 >= 13 && n3 >= 18){
				break;
			}
		}
		pc.getAction().action("loadweaponqiehuan",0);
	}
	private void UseArmor(final L1PcInstance activeChar, final int armorObjId) {
		final L1PcInventory pcInventory = activeChar.getInventory();
		final L1ItemInstance armor = pcInventory.getItem(armorObjId);
		if (armor == null){
			return;
		}
		final int type = armor.getItem().getType();
		boolean equipeSpace;
		if (type == 9)
			equipeSpace = pcInventory.getTypeEquipped(2, 9) <= 1;
		else
			equipeSpace = pcInventory.getTypeEquipped(2, type) <= 0;
		if (equipeSpace && !armor.isEquipped()) {
			final int polyid = activeChar.getTempCharGfx();
			if (!L1PolyMorph.isEquipableArmor(polyid, type))
				return;
			if (type == 7 && activeChar.getWeapon() != null
					&& activeChar.getWeapon().getItem().isTwohandedWeapon()) {
				activeChar.sendPackets(new S_ServerMessage(129));
				return;
			}
			if (type == 3 && pcInventory.getTypeEquipped(2, 4) >= 1) {
				activeChar
						.sendPackets(new S_ServerMessage(126, "$224", "$225"));
				return;
			}
			if (type == 3 && pcInventory.getTypeEquipped(2, 2) >= 1) {
				activeChar
						.sendPackets(new S_ServerMessage(126, "$224", "$226"));
				return;
			}
			if (type == 2 && pcInventory.getTypeEquipped(2, 4) >= 1) {
				activeChar
						.sendPackets(new S_ServerMessage(126, "$226", "$225"));
				return;
			}
			cancelAbsoluteBarrier(activeChar);
			pcInventory.setEquipped(armor, true);
		} else if (armor.isEquipped()) {
			if (armor.getBless() == 2) {
				activeChar.sendPackets(new S_ServerMessage(150));
				return;
			}
			if (type == 3 && pcInventory.getTypeEquipped(2, 2) >= 1) {
				activeChar.sendPackets(new S_ServerMessage(127));
				return;
			}
			if ((type == 2 || type == 3)
					&& pcInventory.getTypeEquipped(2, 4) >= 1) {
				activeChar.sendPackets(new S_ServerMessage(127));
				return;
			}
			pcInventory.setEquipped(armor, false);
		} else {
			activeChar.sendPackets(new S_ServerMessage(124));
		}
		activeChar.setCurrentHp(activeChar.getCurrentHp());
		activeChar.setCurrentMp(activeChar.getCurrentMp());
		activeChar.sendPackets(new S_OwnCharAttrDef(activeChar));
		activeChar.sendPackets(new S_OwnCharStatus(activeChar));
		activeChar.sendPackets(new S_SPMR(activeChar));
	}
	private void cancelAbsoluteBarrier(final L1PcInstance pc) {
		if (pc.hasSkillEffect(78)) {
			pc.killSkillEffectTimer(78);
			pc.startHpRegeneration();
			pc.startMpRegeneration();
		}
	}
	private void UseWeapon(final L1PcInstance activeChar, final int weaponObjId) {
		final L1PcInventory pcInventory = activeChar.getInventory();
		final L1ItemInstance weapon = pcInventory.getItem(weaponObjId);
		if (weapon == null){
			return;
		}
		if (activeChar.getWeapon() == null
				|| !activeChar.getWeapon().equals(weapon)) {
			final int weapon_type = weapon.getItem().getType();
			final int polyid = activeChar.getTempCharGfx();
			if (!L1PolyMorph.isEquipableWeapon(polyid, weapon_type))
				return;
			
			if (weapon.getItem().isTwohandedWeapon()
					&& pcInventory.getTypeEquipped(2, 7) >= 1) {
				activeChar.sendPackets(new S_ServerMessage(128));
				return;
			}
		}
		cancelAbsoluteBarrier(activeChar);
		if (activeChar.getWeapon() != null) {
			if (activeChar.getWeapon().getBless() == 2) {
				activeChar.sendPackets(new S_ServerMessage(150));
				return;
			}
			if (activeChar.getWeapon().equals(weapon)) {
				pcInventory.setEquipped(activeChar.getWeapon(), false, false,
						false);
				return;
			}
			pcInventory.setEquipped(activeChar.getWeapon(), false, false, true);
		}
		if (weapon.getItemId() == 0x30d42)
			activeChar.sendPackets(new S_ServerMessage(149, weapon.getLogName()));
		pcInventory.setEquipped(weapon, true, false, false);
		if (activeChar.getWeapon() != null){
			if (activeChar.getWeapon().getItem().getType1() == 4 || activeChar.getWeapon().getItem().getType1() == 46){
				activeChar.sendPackets(new S_SystemMessage("成功切换到单手武器:" + activeChar.getWeapon().getName()));
			}else if (activeChar.getWeapon().getItem().getType1() == 50){
				activeChar.sendPackets(new S_SystemMessage("成功切换到双手武器:" + activeChar.getWeapon().getName()));
			}
		}
	}
}