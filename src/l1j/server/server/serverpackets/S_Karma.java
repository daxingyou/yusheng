package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1PcInstance;



/**
 * 友好度
 */
public class S_Karma extends ServerBasePacket {

    private static final String S_KARMA = "[S] S_Karma";

    public S_Karma(final L1PcInstance pc) {
        this.writeC(Opcodes.S_OPCODE_PACKETBOX);
        this.writeC(0x57);
        this.writeD(pc.getKarma());
    }

    @Override
    public byte[] getContent() {
        return this.getBytes();
    }

    @Override
    public String getType() {
        return S_KARMA;
    }
}
