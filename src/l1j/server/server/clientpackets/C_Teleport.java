package l1j.server.server.clientpackets;




import l1j.server.server.mina.LineageClient;
//import l1j.server.server.ClientThread;

import l1j.server.server.model.Instance.L1PcInstance;

import l1j.server.server.model.skill.L1SkillId;

import l1j.server.server.serverpackets.S_CharVisualUpdate;

import l1j.server.server.serverpackets.S_OwnCharPack;
import l1j.server.server.serverpackets.S_PacketBoxWindShackle;


public class C_Teleport extends ClientBasePacket {

	public C_Teleport(byte abyte0[], LineageClient client) {
		super(abyte0);
		L1PcInstance player = client.getActiveChar();
		
/*		player.sendPackets(new S_MapID(player.getMapId(), player.getMap()
				.isUnderwater()));*/

/*		if (!player.isGhost() && !player.isGmInvis() && !player.isInvisble()) {
			player.broadcastPacket(new S_OtherCharPacks(player));
		}*/
		player.sendPackets(new S_OwnCharPack(player));

		player.removeAllKnownObjects();
		player.sendVisualEffectAtTeleport(); // 、毒、水中等视佅果表示
		player.updateObject();
		// spr番6310, 5641身中后移动
		// 武器移动、S_CharVisualUpdate送信
		player.sendPackets(new S_CharVisualUpdate(player));
		if (player.hasSkillEffect(L1SkillId.WIND_SHACKLE)) {
			player.sendPackets(new S_PacketBoxWindShackle(player.getId(), player.getSkillEffectTimeSec(L1SkillId.WIND_SHACKLE)));
		}
		player.killSkillEffectTimer(L1SkillId.MEDITATION);
		player.setTeleport(false);

	}

}
