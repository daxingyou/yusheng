package l1j.server.data.npc.shop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.lock.CnReading;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CloseList;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.Tbs;

public class Npc_TimeCash extends NpcExecutor {

	private static final Log _log = LogFactory.getLog(Npc_TimeCash.class);
	/**
	 *
	 */
	private Npc_TimeCash() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_TimeCash();
	}

	@Override
	public int type() {
		return 3;
	}
	
	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		//System.out.println("对话有效1");
		final Tbs tbs  = CnReading.get().getCnOther(pc.getAccountName());
		//System.out.println("对话有效2");
		final boolean isuse = CnReading.get().isload();
		//System.out.println("对话有效3");
		if (tbs!= null&&!isuse) {
			//System.out.println("对话有效4");
			final int count  = tbs.getCnCount();
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "greencash",new String[]{count+"个"}));
			//System.out.println("对话有效5");
		}else {
			pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "greencash",new String[]{0+"个"}));
			//System.out.println("对话有效6");
		}
		
	}
	
	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("getcash")) {
			//System.out.println("按钮有效");
			final boolean isuse = CnReading.get().isload();
			//System.out.println("按钮有效1");
			if (isuse) {
				return;
			}
			//System.out.println("按钮有效2");
			final Tbs tbs  = CnReading.get().getCnOther(pc.getAccountName());
			if (tbs == null) {
			pc.sendPackets(new S_SystemMessage("您目前的账户余额为0，请您储值后再试。"));
			pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}
			final int count  = tbs.getCnCount();
			//System.out.println("按钮有效3");
			if (count < 1) {
			pc.sendPackets(new S_CloseList(pc.getId()));
				return;
			}
			//System.out.println("按钮有效4");
			final L1ItemInstance item = ItemTable.getInstance().createItem(
					tbs.getCnitemid());
			item.setEnchantLevel(0);
			item.setCount(count);
			pc.getInventory().storeItem(item);
			CnReading.get().storeCnOther(pc.getAccountName());
			pc.sendPackets(new S_SystemMessage("提取"+item.getName()+"("+item.getCount()+")"+"成功!"));
			_log.info("玩家 "+pc.getName()+" 通过NPC "+npc.getNpcId()+" 领取了 "+item.getLogName());
			//System.out.println("按钮有效6");
		}
	}
}
