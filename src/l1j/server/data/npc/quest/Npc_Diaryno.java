package l1j.server.data.npc.quest;

import java.util.Random;
import java.util.logging.Logger;

import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;

public class Npc_Diaryno extends NpcExecutor {

	/**
	 * 转生尊者
	 */
	private Npc_Diaryno() {
		// TODO Auto-generated constructor stub
	}

	public static NpcExecutor get() {
		return new Npc_Diaryno();
	}

	@Override
	public int type() {
		return 2;
	}
	
	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount) {
		String htmlid = "";
		System.out.println("执行动作");
		if (cmd.equalsIgnoreCase("A")) {
			System.out.println("判断生效");
			int[] diaryno = { 49082, 49083 };
			Random random = new Random();
			int pid = random.nextInt(diaryno.length);
			int di = diaryno[pid];
			if (di == 49082) { // 奇数ページ抜け
				htmlid = "voyager6a";
				L1ItemInstance item = pc.getInventory().storeItem(di, 1);
				String npcName = npc.getNpcTemplate().get_name();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
			} else if (di == 49083) { // 偶数ページ抜け
				htmlid = "voyager6b";
				L1ItemInstance item = pc.getInventory().storeItem(di, 1);
				String npcName = npc.getNpcTemplate().get_name();
				String itemName = item.getItem().getName();
				pc.sendPackets(new S_ServerMessage(143, npcName, itemName));
			}
		}
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), htmlid));
	}
}
