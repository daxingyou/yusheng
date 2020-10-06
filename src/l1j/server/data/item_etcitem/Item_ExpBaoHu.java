package l1j.server.data.item_etcitem;

import java.sql.Timestamp;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_OwnCharStatus;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_SystemMessage;

public class Item_ExpBaoHu extends ItemExecutor {

    /**
	 *余生神力药水 已取消
	 */
    private Item_ExpBaoHu() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Item_ExpBaoHu();
    }

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item){
		if (pc == null){
			return;
		}
		if (item == null){
			return;
		}
	}
	
	private int _time;
	public void set_set(String[] set) {
		try {
			_time = Integer.parseInt(set[1]);
		} catch (Exception e) {
		}
	}
}
