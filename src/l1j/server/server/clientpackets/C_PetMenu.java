package l1j.server.server.clientpackets;

import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.serverpackets.S_PetInventory;
import l1j.server.server.world.L1World;



/**
 * 要求寵物回報選單
 * 
 * @author daien
 * 
 */
public class C_PetMenu extends ClientBasePacket {

public C_PetMenu(byte abyte0[], LineageClient client) {
		super(abyte0);
		final L1PcInstance pc = client.getActiveChar();

		if (pc == null) {
			return;
		}

		if (pc.isGhost()) { // 鬼魂模式
			return;
		}

		if (pc.isTeleport()) { // 傳送中
			return;
		}

		if (pc.isPrivateShop()) { // 商店村模式
			return;
		}

		final int petId = this.readD();

		final L1Object obj = L1World.getInstance().findObject(petId);
		if (obj == null) {
			return;
		}
		if (!(obj instanceof L1PetInstance)) {
			return;
		}
		if (pc.getPetList().get(petId) == null) {
			return;
		}
		L1PetInstance pet = (L1PetInstance)obj;

		pc.sendPackets(new S_PetInventory(pet));
	}

//	private static final Log _log = LogFactory.getLog(C_PetMenu.class);

/*	@Override
	public void start(final byte[] abyte0, final ClientExecutor client) {
		try {
			// 資料載入
			super(abyte0);

			final L1PcInstance pc = client.getActiveChar();

			if (pc == null) {
				return;
			}

			if (pc.isGhost()) { // 鬼魂模式
				return;
			}

			if (pc.isTeleport()) { // 傳送中
				return;
			}

			if (pc.isPrivateShop()) { // 商店村模式
				return;
			}

			final int petId = this.readD();

			final L1PetInstance pet = WorldPet.get().get(petId);
			if (pet == null) {
				return;
			}
			if (pc.getPetList().get(petId) == null) {
				return;
			}

			pc.sendPackets(new S_PetInventory(pet));

			
			 * if ((obj != null) && (pc != null)) { if (obj instanceof
			 * L1PetInstance) { final L1PetInstance pet = (L1PetInstance) obj;
			 * pc.sendPacketss(new S_PetInventory(pet)); } }
			 

		} catch (final Exception e) {
			_log.error(e.getLocalizedMessage(), e);

		} finally {
			this.over();
		}
	}*/

	@Override
	public String getType() {
		return this.getClass().getSimpleName();
	}
}
