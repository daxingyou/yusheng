package l1j.server.data.item_etcitem.wand;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

public class Consige extends ItemExecutor{
	private Consige() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Consige();
	}

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		final int spellsc_objid = data[0];

		final L1Object target = L1World.getInstance().findObject(spellsc_objid);

		if (target == null) {
			return;
		}
		if (item == null) {
			return;
		}
		if (target instanceof L1PcInstance) {
			if (!((L1PcInstance) target).isCheckFZ()) {
				((L1PcInstance) target).setCheckFZ(true);
				pc.sendPackets(new S_SystemMessage("玩家"+((L1PcInstance) target).getName()+"列入审查"));
				WriteLogTxt.Recording("审查","玩家"+pc.getName()+"把玩家"+((L1PcInstance) target).getName()+"列入审查");
			}else {
				((L1PcInstance) target).setCheckFZ(false);
				pc.sendPackets(new S_SystemMessage("玩家"+((L1PcInstance) target).getName()+"解除审查"));
				WriteLogTxt.Recording("审查","玩家"+pc.getName()+"把玩家"+((L1PcInstance) target).getName()+"解除审查");
			}
			pc.getInventory().removeItem(item, 1);
		}
	}

}
