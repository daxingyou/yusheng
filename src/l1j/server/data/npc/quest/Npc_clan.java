package l1j.server.data.npc.quest;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CloseList;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_SPMR;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.serverpackets.ServerBasePacket;
import l1j.server.server.templates.L1Item;

public class Npc_clan extends NpcExecutor {

	//这个是建立血盟技能要的材料
    private static final int[][] _items = new int[][] {
            { 40048, 100 },// 高品质钻石 x 10<br>
            { 40049, 100 },// 高品质红宝石 x 10<br>
            { 40050, 100 },// 高品质蓝宝石 x 10<br>
            { 40051, 100 },// 高品质绿宝石 x 10<br>
            { 40405, 200 },// 高品质绿宝石 x 10<br>
            { 40408, 200 },// 高品质绿宝石 x 10<br>
            { 40524, 30 },// 高品质绿宝石 x 10<br>
            { 10051, 100},
            { 40308, 2000000 },// 高品质绿宝石 x 10<br>
    };
    /*
     * 
    //个是升级血盟技能要的材料1~2
    private static final int[][] _upitems1 = new int[][] {
        {40052, 10 },// 高品质钻石 x 10<br>
        {40053, 10 },// 高品质红宝石 x 10<br>
        {40054, 10 },// 高品质蓝宝石 x 10<br>
        {40055, 10 },// 高品质绿宝石 x 10<br>

    };
  //这个是升级血盟技能要的材料2~3
    private static final int[][] _upitems2 = new int[][] {
        {40052, 20 },// 高品质钻石 x 10<br>
        {40053, 20 },// 高品质红宝石 x 10<br>
        {40054, 20 },// 高品质蓝宝石 x 10<br>
        {40055, 20 },// 高品质绿宝石 x 10<br>

    };
  //这个是升级血盟技能要的材料3~4
    private static final int[][] _upitems3 = new int[][] {
        {40052, 30 },// 高品质钻石 x 10<br>
        {40053, 30 },// 高品质红宝石 x 10<br>
        {40054, 30 },// 高品质蓝宝石 x 10<br>
        {40055, 30 },// 高品质绿宝石 x 10<br>

    };
  //这个是升级血盟技能要的材料4~5
    private static final int[][] _upitems4 = new int[][] {
        {40052, 40 },// 高品质钻石 x 10<br>
        {40053, 40 },// 高品质红宝石 x 10<br>
        {40054, 40 },// 高品质蓝宝石 x 10<br>
        {40055, 40 },// 高品质绿宝石 x 10<br>

    };
    */
    /**
	 *
	 */
    private Npc_clan() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new Npc_clan();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
        int clanid = pc.getClanid();
        // 王族角色
        if (pc.isCrown()) {
            // 不具有血盟
            if (clanid == 0) {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1"));

                // 已有血盟
            } else {
                // 不具有血盟技能
                if (!pc.getClan().isClanskill()) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1b"));
                } else {
                    String[] times = new String[] {String.valueOf(pc.getClan().getSkillLevel())};
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1aL",
                            times));
                }
            }

            // 非王族角色
        } else {
            // 已有血盟
            if (clanid != 0) {
                // 不具有血盟技能
                if (!pc.getClan().isClanskill()) {
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1g"));

                } else {
                    String[] times = new String[] {String.valueOf(pc.getClan().getSkillLevel())};
                    pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1a",
                            times));
                }

                // 不具有血盟
            } else {
                pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1c"));
            }
        }
    }

    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc,
            final String cmd, final long amount) {
        int clanid = pc.getClanid();
        if (clanid == 0) {
        	pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1e"));
            return;
        }
        /*if (cmd.equals("1")) {// 升级血盟技能
        	if (pc.isCrown()) {
        		if (pc.getClan().isClanskill()) {
        			if (pc.getClan().getSkillLevel() >= 5){
        				pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1m"));
        	            return;
        			}
        			up_clanSkill(pc, npc);
        		}
        	}
        } else */if (cmd.equals("2")) {// 创立血盟技能
            // 王族角色
            if (pc.isCrown()) {
                if (!pc.getClan().isClanskill()) {
                    get_clanSkill(pc, npc);
                }
            }
        }
    }

    private void up_clanSkill(L1PcInstance pc,L1NpcInstance npc){
        if (pc.getClan() == null){
        	return;
        }
    	// 道具不足的显示清单
        Queue<String> itemListX = new ConcurrentLinkedQueue<String>();

        int[][] upitems = null;
    	/*if (pc.getClan().getSkillLevel() == 1){
    		upitems = _upitems1;
    	}else if (pc.getClan().getSkillLevel() == 2){
    		upitems = _upitems2;
    	}else if (pc.getClan().getSkillLevel() == 3){
    		upitems = _upitems3;
    	}else if (pc.getClan().getSkillLevel() == 4){
    		upitems = _upitems4;
    	}*/
    	if (upitems == null){
    		return;
    	}
        // 检查所需物品数量
        for (int itemid[] : upitems) {
            final L1ItemInstance item = pc.getInventory().checkItemX(itemid[0],itemid[1]);
            if (item == null) {
                long countX = pc.getInventory().countItems(itemid[0]);
                final L1Item itemX2 = ItemTable.getInstance().getTemplate(itemid[0]);
                if (countX > 0) {
                    itemListX.offer(itemX2.getNameId() + " ("
                            + (itemid[1] - countX) + ")");

                } else {
                    itemListX.offer(itemX2.getNameId() + " (" + itemid[1]
                            + ")");
                }
            }
        }

        if (itemListX.size() > 0) {
            for (final Iterator<String> iter = itemListX.iterator(); iter
                    .hasNext();) {
                final String tgitem = iter.next();// 返回迭代的下一个元素。
                // 337：\f1%0不足%s。 0_o"
                pc.sendPackets(new S_ServerMessage(337, tgitem));
            }
            itemListX.clear();
            // 关闭对话窗
            pc.sendPackets(new S_CloseList(pc.getId()));
            return;
        }

        itemListX.clear();

        // 检查所需物品数量
        for (int itemid[] : upitems) {
            int ritemid = itemid[0];// 道具编号
            int rcount = itemid[1];// 耗用数量
            final L1ItemInstance item = pc.getInventory().checkItemX(
                    ritemid, rcount);
            if (item != null) {
                long rcountx = pc.getInventory().removeItem(item, rcount);
                if (rcountx != rcount) {// 删除道具
                    // 关闭对话窗
                    pc.sendPackets(new S_CloseList(pc.getId()));
                    return;
                }
            }
        }
        pc.getClan().setSkillILevel(pc.getClan().getSkillLevel() + 1);
		ClanTable.getInstance().updateClan(pc.getClan());
		
		final String[] times = new String[] {String.valueOf(pc.getClan().getSkillLevel())};
		final L1PcInstance[] clanMembers = pc.getClan().getOnlineClanMember();
        final ServerBasePacket msg = new S_SystemMessage(String.format("\\F1恭喜你血盟技能等级已提升到Lv%d", pc.getClan().getSkillLevel()));
		for (final L1PcInstance clanpc : clanMembers){
        	clanpc.sendPackets(new S_SPMR(clanpc));
        	clanpc.sendPackets(msg);
        }
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c1aL",times));
    }
    /**
     * 创立血盟技能
     * 
     * @param pc
     * @param npc
     */
    private void get_clanSkill(L1PcInstance pc, L1NpcInstance npc) {
        // 道具不足的显示清单
        Queue<String> itemListX = new ConcurrentLinkedQueue<String>();

     // 检查所需物品数量
        for (int itemid[] : _items) {
            final L1ItemInstance item = pc.getInventory().checkItemX(itemid[0],itemid[1]);
            if (item == null) {
                long countX = pc.getInventory().countItems(itemid[0]);
                final L1Item itemX2 = ItemTable.getInstance().getTemplate(itemid[0]);
                if (countX > 0) {
                    itemListX.offer(itemX2.getNameId() + " ("
                            + (itemid[1] - countX) + ")");

                } else {
                    itemListX.offer(itemX2.getNameId() + " (" + itemid[1]
                            + ")");
                }
            }
        }

        if (itemListX.size() > 0) {
            for (final Iterator<String> iter = itemListX.iterator(); iter
                    .hasNext();) {
                final String tgitem = iter.next();// 返回迭代的下一个元素。
                // 337：\f1%0不足%s。 0_o"
                pc.sendPackets(new S_ServerMessage(337, tgitem));
            }
            itemListX.clear();
            // 关闭对话窗
            pc.sendPackets(new S_CloseList(pc.getId()));
            return;
        }

        itemListX.clear();

        // 检查所需物品数量
        for (int itemid[] : _items) {
            int ritemid = itemid[0];// 道具编号
            int rcount = itemid[1];// 耗用数量
            final L1ItemInstance item = pc.getInventory().checkItemX(
                    ritemid, rcount);
            if (item != null) {
                long rcountx = pc.getInventory().removeItem(item, rcount);
                if (rcountx != rcount) {// 删除道具
                    // 关闭对话窗
                    pc.sendPackets(new S_CloseList(pc.getId()));
                    return;
                }
            }
        }
        
        pc.getClan().setSkillILevel(1);
        pc.getClan().set_clanskill(true);
        ClanTable.getInstance().updateClan(pc.getClan());
        
        final L1PcInstance[] clanMembers = pc.getClan().getOnlineClanMember();
        final ServerBasePacket msg = new S_SystemMessage("\\F1恭喜你的血盟创建了血盟技能当前等级Lv1 小退后效果生效");
        for (final L1PcInstance clanpc : clanMembers){
        	clanpc.sendPackets(new S_SPMR(clanpc));
        	clanpc.sendPackets(msg);
        }
        pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "y_c2a"));
    }
}
