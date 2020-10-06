package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

public class S_GreenMessage extends ServerBasePacket {
	private static final String S_GREEN_MESSAGE = "[S] S_GreenMessage";

	private byte[] _byte = null;

	public S_GreenMessage(String msg) {
		writeC(Opcodes.S_OPCODE_PACKETBOX);
		writeC(0x54);
		writeC(0x02);
		writeS(msg);
	}

	@Override
	public byte[] getContent() {
		if (_byte == null) {
			_byte = getBytes();
		}
		return _byte;
	}

	@Override
	public String getType() {
		return S_GREEN_MESSAGE;
	}
}
