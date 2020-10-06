package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NPCTalkReturn;

public class Item_ReGame extends ItemExecutor {

    /**
	 *
	 */
    private Item_ReGame() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Item_ReGame();
    }

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		if (pc.isGm()){
			pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "gmremysql"));
		}
	}
}
