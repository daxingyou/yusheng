package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;

public class S_LaBaChat extends ServerBasePacket {
	
	public S_LaBaChat(final L1PcInstance pc, final String chat,final int type) {
		if (type > 0){
			this.buildPacket(pc, chat,type);
		}else{
			this.buildPacket(pc, chat);
		}
	}
	private static final int GREEN_MESSAGE = 0x54;

	public void buildPacket(final L1PcInstance pc,final String chat) {
		String laba = chat.substring(3,chat.length());
		writeC(Opcodes.S_OPCODE_NPCSHOUT);
		writeC(GREEN_MESSAGE);// 57
		writeC(0x03);// 44
		writeS("\\f3"+pc.getName()+"：\\f2"+laba);
	}
	
	public void buildPacket(final L1PcInstance pc,final String chat,int n) {
		String laba = chat.substring(3,chat.length());
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(GREEN_MESSAGE);// 57
		writeC(0x02);// 44
		writeS("\\f3"+pc.getName()+"：\\f2"+laba);
	}

	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return "S_LaBaChat";
	}
}
