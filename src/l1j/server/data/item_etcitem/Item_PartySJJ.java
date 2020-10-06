package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_SkillSound;

public class Item_PartySJJ extends ItemExecutor {

    /**
	 *
	 */
    private Item_PartySJJ() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Item_PartySJJ();
    }

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		pc.getInventory().removeItem(item,1);
		pc.sendPacketsAll(new S_SkillSound(pc.getId(), 228));
		pc.setSkillEffect(L1SkillId.IMMUNE_TO_HARM, 30 * 1000);
		if (pc.getParty() != null){
			for(final L1PcInstance tagpc : pc.getParty().getMembers()){
				if (tagpc.getLineDistance(pc) < 9 && tagpc.getId() != pc.getId()){
					tagpc.sendPacketsAll(new S_SkillSound(tagpc.getId(), 228));
					tagpc.setSkillEffect(L1SkillId.IMMUNE_TO_HARM, 30 * 1000);
				}
			}
		}
	}
}
