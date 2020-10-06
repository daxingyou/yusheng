package l1j.server.server.serverpackets;

import java.util.ArrayList;

import l1j.server.Config;
import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.storage.GamblingLock;
import l1j.server.server.templates.L1Gambling;
import l1j.server.server.world.L1World;

/**
 * <b><font color=red>封包分类项目 : </font><font color=#008000>NPC物品贩卖(回收食人妖精竞赛票)</font></b>
 * @author dexc
 *
 */
public class S_ShopBuyListGam extends ServerBasePacket {

	/**
	 * <font color=#008000>NPC物品贩卖(回收食人妖精竞赛票)</font>
	 * @param objid
	 * @param list
	 * @return
	 */
	public S_ShopBuyListGam(int objid, ArrayList<L1ItemInstance> list) {
		L1Object object = L1World.getInstance().findObject(objid);
		if (!(object instanceof L1NpcInstance)) {
			return;
		}

		writeC(Opcodes.S_OPCODE_SHOWSHOPSELLLIST);
		writeD(objid);
		writeH(list.size());

		for (L1ItemInstance item : list) {
			writeD(item.getId());
			int gamId = item.getGamNo();
			L1Gambling gamInfo = GamblingLock.create().getGambling(gamId);
			if (gamInfo != null) {
				// NPCID相同
				if (gamInfo.get_npcid() == item.getGamNpcId()) {
					writeD((int) (Config.Gam_CHIP * gamInfo.get_rate()));
				}
			}
		}
		this.writeH(0x0007);
	}
	@Override
	public byte[] getContent() {
		return getBytes();
	}
	@Override
	public String getType() {
		return "[S] S_ShopBuyList";
	}
}