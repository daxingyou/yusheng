package l1j.server.server.serverpackets;

import java.io.IOException;
import java.util.ArrayList;

import l1j.server.Config;
import l1j.server.server.Opcodes;
import l1j.server.server.datatables.ItemTable;

import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.game.GamblingTimeList;
import l1j.server.server.templates.L1Gam;
import l1j.server.server.templates.L1Item;

/**
 * <b><font color=red>封包分类项目 : </font><font color=#008000>物品购买(赌场专用)</font></b>
 * @author dexc
 *
 */
public class S_ShopSellListGam extends ServerBasePacket {

	private static GamblingTimeList _gam = GamblingTimeList.gam();

	/**
	 * <font color=#008000>物品购买(赌场专用)</font>
	 * @param objId
	 * @param items 
	 * @return
	 */
	public S_ShopSellListGam(int objId, ArrayList<L1Gam> gams) {
		writeC(Opcodes.S_OPCODE_SHOWSHOPBUYLIST);
		writeD(objId);
		// 取回数量
		writeH(gams.size());

		L1ItemInstance dummy = new L1ItemInstance();

		if (_gam.get_isStart()) {
			return;
		}

		for (int i = 0; i < gams.size(); i++) {

			L1Gam info = gams.get(i);
			
			// 食人妖精竞赛票
			L1Item item = ItemTable.getInstance().getTemplate(40309);

			int price = (int) Config.Gam_CHIP;
			
//			L1Npc npc = NpcTable.getInstance().getTemplate(info[0]);
			
			writeD(i);
			writeH(item.getGfxId());
			writeD(price);
			writeS(info.getGamName() + "(" + info.getGamNumId() + "-" + info.getGamNpcId() + ")");

			dummy.setItem(item);
			byte[] status = dummy.getStatusBytes();
			writeC(status.length);
			for (byte b : status) {
				writeC(b);
			}
		}
		writeH(0x07); // 0x00:kaimo 0x01:pearl 0x07:adena
	}

	@Override
	public byte[] getContent() throws IOException {
		return _bao.toByteArray();
	}
}
