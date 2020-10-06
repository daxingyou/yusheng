package l1j.server.data.npc.power;

import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_CloseList;
import l1j.server.server.serverpackets.S_NPCTalkReturn;

public class Npc_Relief extends NpcExecutor{

	
	private Npc_Relief() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Relief();
	}
	@Override
	public int type() {
		return 3;
	}
	
	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		String[] htmldata = new String[] { npc.getName()+
				":你好！亲爱的冒险者，你想出去吗?(20元宝才能出去)",
				 "5毛拿好！放我出去！", "我不出去！", " ", " "," "," "," "," "," ",
				 " "," "," "," "," "," "," "
};
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "a2_1",htmldata));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("a2_1_1")) {
			if (pc.getInventory().consumeItem(L1ItemId.TIANBAO, 20)) {
				L1Teleport.teleport(pc, 32644, 32955, (short) 0, 5, false);
				WriteLogTxt.Recording("赎人", "玩家"+pc.getName()+"OBJID:"+pc.getId()+"花费20元宝从这里出去！");
			}
		}
		pc.sendPackets(new S_CloseList(pc.getId()));
	}
}
