package l1j.server.data.item_etcitem.shop;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChangeShape;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;

public class Transgender extends ItemExecutor{

	
	private static final int[] MALE_LIST = new int[] { 0, 61, 138, 734, 2786 };
	private static final int[] FEMALE_LIST = new int[] { 1, 48, 37, 1186, 2796 };
	/**
	 *
	 */
	private Transgender() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Transgender();
	}
	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		if (item == null) {
			return;
		}
		try {
			if (pc.getPartnerId() != 0) {
				pc.sendPackets(new S_SystemMessage("单身状态下才能使用！")); // \f1结婚。
				return;
			}
			pc.getInventory().removeItem(item, 1);
			int tempchargfx = pc.getClassId();
			if (pc.get_sex() == 0) {
				tempchargfx = FEMALE_LIST[pc.getType()];
				pc.set_sex(1);
			} else {
				tempchargfx = MALE_LIST[pc.getType()];
				pc.set_sex(0);
			}
			pc.setClassId(tempchargfx);
			pc.setTempCharGfx(tempchargfx);
			pc.save();
			pc.sendPackets(new S_SkillSound(pc.getId(), 10));
			pc.sendPackets(new S_ChangeShape(pc.getId(), tempchargfx));
			pc.broadcastPacket(new S_ChangeShape(pc.getId(), tempchargfx));
			pc.sendPackets(new S_SystemMessage("变性完成！"));
			WriteLogTxt.Recording("角色变性记录", "角色OBJID：<"+pc.getId()+">名字为" + pc.getName());
		} catch (Exception e) {
			// TODO: handle exception
		}		
		
	}

}
