package l1j.server.server.clientpackets;

//import l1j.server.server.ClientThread;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Paralysis;

public class C_UnLock extends ClientBasePacket{

	public C_UnLock(byte abyte0[], LineageClient _client)
			throws Exception {
		super(abyte0);
		final L1PcInstance pc = _client.getActiveChar();
		if (pc != null){
			pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK,0,false));
			L1Teleport.teleport(pc, pc.getOleLocX(), pc.getOleLocY(), pc.getMapId(), pc.getHeading(), false);
		}
		/*pc.removeAllKnownObjects();
		pc.sendPackets(new S_MapID(pc.getMapId(), pc));
		pc.sendPackets(new S_OwnCharPack(pc));
		pc.sendPackets(new S_CharVisualUpdate(pc));//5641更新？
		if (pc.hasSkillEffect(L1SkillId.WIND_SHACKLE)) {
			pc.sendPackets(new S_PacketBoxWindShackle(pc.getId(), pc.getSkillEffectTimeSec(L1SkillId.WIND_SHACKLE)));
		}*/
	}

}
