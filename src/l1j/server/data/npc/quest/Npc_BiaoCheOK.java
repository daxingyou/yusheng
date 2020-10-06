package l1j.server.data.npc.quest;

import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1BiaoCheInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;

public class Npc_BiaoCheOK extends NpcExecutor {

	private static final double[] exps = {0.05,0.06,0.08,0.10,0.15};
	private static final int[] itemcounts = {1,2,3,4,5};
	private Npc_BiaoCheOK() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new Npc_BiaoCheOK();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
    	final int count = 2 - pc.getJieQuBiaoCheCount();
    	pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "biaoche1",new String[]{String.valueOf(count)}));
    }
    
    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc,
            final String cmd, final long amount) {
    	if (cmd.startsWith("ok_")){//提交镖车
    		final L1BiaoCheInstance biaoche = pc.getBiaoChe();
    		if (biaoche == null){
    			pc.sendPackets(new S_SystemMessage("\\F2你都没接取镖车。"));
    			return;
    		}
    		if (biaoche.getMapId() != pc.getMapId()){
    			pc.sendPackets(new S_SystemMessage("\\F2你的镖车离你距离太远"));
    			return;
    		}
    		int lineDistance = biaoche.getLocation().getTileLineDistance(pc.getLocation());
			if (lineDistance >= 10){
				pc.sendPackets(new S_SystemMessage("\\F2你都镖车离你距离太远"));
    			return;
			}
			final int color = biaoche.getColor();
			pc.setBiaoChe(null);
			biaoche.deleteMe();
			int type = Integer.parseInt(cmd.substring(3));
			if (type == 0){//奖励1
				final int maxexp = ExpTable.getExpByLevel(pc.getLevel()+1) - ExpTable.getExpByLevel(pc.getLevel());
				final int addexp = (int)(maxexp * exps[color]);
				pc.addExp(addexp);
			}else if (type == 1){//奖励2
				pc.getInventory().storeItem(10051, itemcounts[color]);
				final L1Item item1 = ItemTable.getInstance().getTemplate(10051);
				pc.sendPackets(new S_SystemMessage(String.format("\\F2%s给你%s(%d)", npc.getName(),item1.getName(),itemcounts[color])));
			}else if (type == 2){//奖励3
				pc.getInventory().storeItem(10052, itemcounts[color]);
				final L1Item item2 = ItemTable.getInstance().getTemplate(10052);
				pc.sendPackets(new S_SystemMessage(String.format("\\F2%s给你%s(%d)", npc.getName(),item2.getName(),itemcounts[color])));
			}
			pc.getInventory().storeItem(40308, 1000000);//退还保证金
    	}
    }
}
