package l1j.server.data.npc;

import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_SystemMessage;

public class NPC_GJ extends NpcExecutor {

	private NPC_GJ() {

	}

	@Override
	public int type() {
		return 3;
	}

	public static NpcExecutor get() {
		return new NPC_GJ();
	}

	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "gjdt"));
	}

	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc,
			final String cmd, final long amount) {
		if (cmd.equalsIgnoreCase("a1")) {
			if (pc.getInventory().consumeItem(40308, 20000)) {
				final L1Location newLocation = new L1Location(
						32811, 32726,
						53).randomLocation(200, false);
				L1Teleport.teleport(pc, newLocation.getX(), newLocation.getY(),
						(short) newLocation.getMapId(), 5, true);
			} else {
				pc.sendPackets(new S_SystemMessage("金币不足(20000)"));
			}
		}
	}
}
