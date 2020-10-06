package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class TwoPassword extends ItemExecutor {

    private TwoPassword() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new TwoPassword();
    }

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item){
		if (pc == null){
			return;
		}
		if (item == null){
			return;
		}
		if (pc.isCheckTwopassword()){
			//pc.sendPackets(new S_SystemMessage("\\F3**你还没设置二级密码**"));
			return;
		}
		pc.setXiuGaiTwopassword(true);
		pc.setOldTwoPassword(-256);
		pc.sendPackets(new S_SystemMessage("\\F3**请输入原始二级密码**"));
	}
}
