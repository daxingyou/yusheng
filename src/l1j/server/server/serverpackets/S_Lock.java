package l1j.server.server.serverpackets;


import l1j.server.server.Opcodes;

public class S_Lock extends ServerBasePacket{
	

	/**
	 * 座标异常重整
	 * @param type
	 * @param equipped
	 */
	public S_Lock() {
		this.buildPacket();
	}

	private void buildPacket() {
		this.writeC(Opcodes.S_OPCODE_TELEPORTLOCK);
		this.writeC(0x00);
		/*this.writeC(0xf1);
		this.writeC(0x2d);
		this.writeC(0x7d);
		this.writeC(0x02);
		this.writeC(0xf9);*/
	}
	
	@Override
	public byte[] getContent() {
		return getBytes();
	}

	public String getType() {
		return "[S] S_Lock";
	}

}
