package l1j.server.server.serverpackets;


import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1GuaJiAIInstance;
import l1j.server.server.model.Instance.L1PcInstance;

// Referenced classes of package l1j.server.server.serverpackets:
// ServerBasePacket , S_DollPack

public class S_GuaJiAINpcPack extends ServerBasePacket {


	private static final String S_DOLLPACK = "[S] S_GuaJiAINpcPack";

	public S_GuaJiAINpcPack(L1GuaJiAIInstance pet, L1PcInstance player) {
		writeC(Opcodes.S_OPCODE_CHARPACK);
		writeH(pet.getX());
		writeH(pet.getY());
		writeD(pet.getId());
		writeH(pet.getGfxId());
		writeC(pet.getStatus());
		writeC(pet.getHeading());
		writeC(pet.getLightSize());
		writeC(pet.getMoveSpeed());
		writeD(0);
		writeH(0);
		writeS(pet.getNameId());
		writeS(pet.getTitle());
		writeC(0); //  - 0:mob, item(atk pointer) , 1:poisoned() ,
		// 2:invisable() , 4:pc, 8:cursed() , 16:brave() ,
		// 32:??, 64:??(??) , 128:invisable but name
		writeD(0); // ??
		writeS(null); // ??
		writeS(pet.getMaster() != null ? pet.getMaster().getName() : "");
		writeC(0); // ??
		writeC(0xFF);
		writeC(0);
		writeC(pet.getLevel()); // PC = 0, Mon = Lv
		writeC(0);
		writeC(0xFF);
		writeC(0xFF);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return S_DOLLPACK;
	}

}
