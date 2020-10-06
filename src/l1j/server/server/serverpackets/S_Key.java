package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_Key extends ServerBasePacket{

	private static final String S_KSY = "[S] S_Key";
	private static final byte[] _firstPacket = {
        (byte) 0x0b, (byte) 0xdd, (byte) 0x43, (byte) 0x3b, (byte) 0x47,
        (byte) 0x71, (byte) 0xd6, (byte) 0x6c, (byte) 0x60, (byte) 0x7e,
        (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x08, (byte) 0x00 };

	public S_Key() {
        writeC(Opcodes.S_OPCODE_INITOPCODE);// 3.51C Taiwan Server
        writeC((byte) (0x3b43dd0b & 0xFF));
        writeC((byte) (0x3b43dd0b >> 8 & 0xFF));
        writeC((byte) (0x3b43dd0b >> 16 & 0xFF));
        writeC((byte) (0x3b43dd0b >> 24 & 0xFF));

        writeByte(_firstPacket);
	}

	@Override
	public byte[] getContent() {
		return getBytes(false);
	}
	
	public String getType() {
		return S_KSY;
	}

}