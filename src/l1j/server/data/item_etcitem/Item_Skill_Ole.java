package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.datatables.EtcItemSkillTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;

public class Item_Skill_Ole extends ItemExecutor {

	private final static String[] msgMessage = {"力量:%d","敏捷:%d","体质:%d","智力:%d","精神:%d","魅力:%d","HP:%d","MP:%d","回血:%d","回魔:%d","魔攻:%d","魔防:%d","防御:%d","命中:%d","攻击:%d","经验:百分之%d","闪避:%d"};
	/**
	 *
	 */
	private Item_Skill_Ole() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Item_Skill_Ole();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data
	 *            參數
	 * @param pc
	 *            執行者
	 * @param item
	 *            物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc,
			final L1ItemInstance item) {
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}
		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}
		final int[] etcItemSkillArray = EtcItemSkillTable.getInstance().getEtcitemSkillArray(_skillId);
		if (etcItemSkillArray == null){
			return;
		}
		if (pc.hasSkillEffect(_skillId)){
			pc.sendPackets(new S_SystemMessage("该状态不可叠加."));
			return;
		}
		pc.getInventory().removeItem(item, 1);
		pc.setSkillEffect(_skillId, etcItemSkillArray[0] * 1000);
		if (etcItemSkillArray[1] > 0){
			pc.sendPacketsAll(new S_SkillSound(pc.getId(), etcItemSkillArray[1]));
		}
		pc.addStr(etcItemSkillArray[2]);
		pc.addDex(etcItemSkillArray[3]);
		pc.addCon(etcItemSkillArray[4]);
		pc.addInt(etcItemSkillArray[5]);
		pc.addWis(etcItemSkillArray[6]);
		pc.addCha(etcItemSkillArray[7]);
		pc.addMaxHp(etcItemSkillArray[8]);
		pc.addMaxMp(etcItemSkillArray[9]);
		pc.addHpr(etcItemSkillArray[10]);
		pc.addMpr(etcItemSkillArray[11]);
		pc.addSp(etcItemSkillArray[12]);
		pc.addMr(etcItemSkillArray[13]);
		pc.addAc(etcItemSkillArray[14]);
		pc.addHitup(etcItemSkillArray[15]);
		pc.addBowHitup(etcItemSkillArray[15]);
		pc.addDmgup(etcItemSkillArray[16]);
		pc.addBowDmgup(etcItemSkillArray[16]);
		pc.addEtcItemSkillExp(etcItemSkillArray[17]);
		pc.addEtcItemSkillEr(etcItemSkillArray[18]);
		pc.sendPackets(new S_OwnCharStatus(pc));
		pc.sendPackets(new S_SPMR(pc));
		
		final StringBuilder msg = new StringBuilder();
		msg.append("\\F3效果:");
		for(int i = 2; i < 19;i++){
			if (etcItemSkillArray[i] != 0){
				if (i > 2){
					msg.append(" ");
				}
				msg.append(String.format(msgMessage[i-2], etcItemSkillArray[i]));
			}
		}
		msg.append(String.format(" 持续时间%d秒", etcItemSkillArray[0]));
		pc.sendPackets(new S_SystemMessage(msg.toString()));
	}
	private int _skillId;
	public void set_set(String[] set) {
		if (set.length > 1){
			_skillId = Integer.parseInt(set[1]);
		}
	}
}
