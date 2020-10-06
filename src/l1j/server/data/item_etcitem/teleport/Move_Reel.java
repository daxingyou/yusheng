package l1j.server.data.item_etcitem.teleport;

import java.util.ArrayList;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.datatables.lock.CharBookReading;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_Paralysis;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1BookMark;

public class Move_Reel extends ItemExecutor {

    /**
	 *
	 */
    private Move_Reel() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Move_Reel();
    }

    /**
     * 道具物件执行
     * 
     * @param data
     *            参数
     * @param pc
     *            执行者
     * @param item
     *            物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc,
            final L1ItemInstance item) {
        // 所在地图编号
        Short mapID = (short) data[0];
        int mapX = data[1];
        int mapY = data[2];
        // 所在位置 是否允许传送
        final boolean isTeleport = pc.getMap().isTeleportable();
        if (!isTeleport) {
            // 647 这附近的能量影响到瞬间移动。在此地无法使用瞬间移动。
            pc.sendPackets(new S_ServerMessage(647));
            pc.sendPackets(new S_Paralysis(S_Paralysis.TYPE_TELEPORT_UNLOCK,0,false));
        } else {
            boolean flag = false; // 传送模式
            final ArrayList<L1BookMark> bookList = CharBookReading.get().getBookMarks(pc);
            // 检查是否有此坐标
            if (bookList != null) {
                for (final L1BookMark book : bookList) {
                    if (book.getMapId() == mapID && book.getLocX() == mapX
                            && book.getLocY() == mapY) {
                        flag = true;
                    }
                }
            } else {
              //  pc.getInventory().removeItem(item, 1);
                L1Teleport.randomTeleport(pc, true);
            }
            if (flag) {
                pc.getInventory().removeItem(item, 1);
                L1Teleport.teleport(pc, mapX, mapY, mapID, 5, true);
            } else { // 随机传送
                pc.getInventory().removeItem(item, 1);
                L1Teleport.randomTeleport(pc, true);
            }
        }
    }
}
