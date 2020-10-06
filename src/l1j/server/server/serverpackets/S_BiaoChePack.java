package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1BiaoCheInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_BiaoChePack extends ServerBasePacket {

	private static final String _S__1F_NPCPACK = "[S] S_NPCPack";

	public S_BiaoChePack(L1BiaoCheInstance npc,L1PcInstance pc) {
		buildPacket(npc,pc);
	}

	private void buildPacket(L1BiaoCheInstance npc,L1PcInstance pc) {
		writeC(Opcodes.S_OPCODE_CHARPACK);
		writeH(npc.getX());
		writeH(npc.getY());
		writeD(npc.getId());
		writeH(npc.getGfxId());
		writeC(npc.getStatus());
		writeC(npc.getHeading());
		writeC(npc.getLightSize());
		writeC(npc.getMoveSpeed());
		writeD(npc.getExp());
		writeH(npc.getTempLawful());
		writeS(npc.getNameId());
		writeS(npc.getTitle());
		writeC(0);
		writeD(0); // 0以外C_27飞
		writeS(null);
		writeS(null); // 名？
		writeC(0);
		if (npc.getMaster() != null && npc.getMaster().getId() == pc.getId()) {
			writeC(100 * npc.getCurrentHp() / npc.getMaxHp());
		} else {
			writeC(0xFF);
		}
		writeC(0);
		writeC(npc.getLevel());
		writeC(0);
		writeC(0xFF);
		writeC(0xFF);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return _S__1F_NPCPACK;
	}
}
