package l1j.server.server.command.executor;

import java.util.StringTokenizer;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.world.L1World;

public class L1ZengSongXC implements L1CommandExecutor {

	private L1ZengSongXC() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1ZengSongXC();
	}

	// @Override
	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			final StringTokenizer st = new StringTokenizer(arg);
			final String name = st.nextToken();
			int count = 1;
			if (st.hasMoreTokens()) {
				count = Integer.parseInt(st.nextToken());
			}
			final int itemId = 10019;
			final L1PcInstance tagPc = L1World.getInstance().getPlayer(name);
			if (tagPc == null){
				pc.sendPackets(new S_SystemMessage("该玩家已经不在线上了."));
				return;
			}
			final L1Item temp = ItemTable.getInstance().getTemplate(itemId);
			if (temp != null) {
				final L1ItemInstance item = ItemTable.getInstance().createItem(itemId);
				item.setCount(count);
				if (tagPc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
					tagPc.getInventory().storeItem(item);
					tagPc.sendPackets(new S_ServerMessage(403,item.getLogName()));
					pc.sendPackets(new S_SystemMessage(item.getLogViewName() + " 赠送成功."));
				}else{
					pc.sendPackets(new S_SystemMessage("该玩家背包已满."));
				}
			}
		}catch(Exception e){
			pc.sendPackets(new S_SystemMessage("请输入." + cmdName + " 玩家名称 数量"));
		}
	}

}
