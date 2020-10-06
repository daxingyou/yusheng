package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.datatables.CallClanMapTable;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_BlueMessage;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class ClanTelete extends ItemExecutor {

    private static final Log _log = LogFactory.getLog(ClanTelete.class);

    private ClanTelete() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new ClanTelete();
    }

    @Override
    public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
        try {
            // 例外状况:物件为空
            if (item == null) {
                return;
            }
            // 例外状况:人物为空
            if (pc == null) {
                return;
            }
            final boolean castle_area = L1CastleLocation.checkInAllWarArea(
					pc.getX(), pc.getY(), pc.getMapId());
			if (castle_area) {
				pc.sendPackets(new S_SystemMessage("你不能在这里使用"));
            	return;
			}
            if (pc.getClanid() == 0 || pc.getClan() == null){
            	pc.sendPackets(new S_SystemMessage("你还有血盟不能使用"));
            	return;
            }
            if (CallClanMapTable.get().IsNoMap(pc.getMapId())){
            	pc.sendPackets(new S_SystemMessage("此地图不能使用"));
            	return;
            }
            if (pc.getClan().getOnlineClanMemberSize() < 2){
            	pc.sendPackets(new S_SystemMessage("血盟没有其他在线成员"));
            	return;
            }
            if (!pc.getInventory().checkItem(40308, 30000)){
            	pc.sendPackets(new S_SystemMessage("\\F2金币不足30000"));
            	return;
            }
            pc.getInventory().consumeItem(40308, 30000);
            L1World.getInstance().broadcastPacketToAll(new S_BlueMessage(4535,pc.getClanname(),pc.getName()));
            pc.getInventory().removeItem(item, 1);
            for(final L1PcInstance tagpc : pc.getClan().getOnlineClanMember()){
            	if (tagpc.getId() != pc.getId()){
            		tagpc.setClanTeletePcId(pc.getId());
            		tagpc.sendPackets(new S_Message_YN(4531, pc.getName()));
            	}
            }
        } catch (final Exception e) {
            _log.error(e.getLocalizedMessage(), e);
        }
    }
}
