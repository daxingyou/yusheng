package l1j.server.data.npc.quest;

import java.util.Random;

import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1BiaoCheInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NPCPack;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.timecontroller.server.GetNowTime;

public class Npc_BiaoChe extends NpcExecutor {
	private static final Random _random = new Random();
	
	private Npc_BiaoChe() {
        // TODO Auto-generated constructor stub
    }

    public static NpcExecutor get() {
        return new Npc_BiaoChe();
    }

    @Override
    public int type() {
        return 3;
    }

    @Override
    public void talk(final L1PcInstance pc, final L1NpcInstance npc) {
    	final int count = 2 - pc.getJieQuBiaoCheCount();
    	pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "biaoche",new String[]{String.valueOf(count)}));
    }
    
    @Override
    public void action(final L1PcInstance pc, final L1NpcInstance npc,
            final String cmd, final long amount) {
    	if (cmd.equalsIgnoreCase("c")){
    		if (pc.getBiaoChe() == null){
    			pc.sendPackets(new S_SystemMessage("\\F2你没有接镖。"));
    			return;
    		}
    		if (pc.getLocation().getLineDistance(pc.getBiaoChe().getLocation()) <= 15){
    			pc.sendPackets(new S_SystemMessage("\\F2你已经在镖车附近了。"));
    			return;
    		}
    		if (!pc.getInventory().checkItem(40308, 10000)){
    			pc.sendPackets(new S_SystemMessage("\\F2金币不足10000。"));
    			return;
    		}
    		pc.getInventory().consumeItem(40308, 10000);
    		L1Teleport.teleport(pc, pc.getBiaoChe().getLocation(), 5, true, 0);
    	}else if (cmd.equalsIgnoreCase("a")){//接取
    		if (pc.getClanid() == 0 || pc.getClan() == null){
    			pc.sendPackets(new S_SystemMessage("\\F2需要加入血盟才可以接镖。"));
    			return;
    		}
    		if (GetNowTime.GetNowHour() < 20 || GetNowTime.GetNowHour() >= 22){
    			pc.sendPackets(new S_SystemMessage("\\F2请在20:00~22:00再来。"));
    			return;
    		}
    		if (pc.getBiaoChe() != null){
    			pc.sendPackets(new S_SystemMessage("\\F2你已经接取了 请先送达目的地吧。"));
    			return;
    		}
    		if (pc.getJieQuBiaoCheCount() >= 2){
    			pc.sendPackets(new S_SystemMessage("\\F2你今日次数已用完。"));
    			return;
    		}
    		if (!pc.getInventory().checkItem(40308, 1000000)){
    			pc.sendPackets(new S_SystemMessage("\\F2接取镖车需要缴纳保证金金币100万。"));
    			return;
    		}
    		pc.addJieQuBiaoCheCount(1);
    		if (CharacterTable.getInstance().updateBiaoCheCount(pc)){
    			pc.getInventory().consumeItem(40308, 1000000);
        		final L1Npc BiaoCheTemp = NpcTable.getInstance().getTemplate(8899);
        		final L1BiaoCheInstance biaocheInstance = new L1BiaoCheInstance(BiaoCheTemp, pc);
        		pc.setBiaoChe(biaocheInstance);
    		}else{
    			pc.addJieQuBiaoCheCount(-1);
    		}
    	}else if (cmd.equalsIgnoreCase("b")){//刷新镖车颜色
    		final L1BiaoCheInstance biaoche = pc.getBiaoChe();
    		if (biaoche == null){
    			pc.sendPackets(new S_SystemMessage("\\F2你还没接取镖车。"));
    			return;
    		}
    		if (biaoche.getColor() >= 4){
    			pc.sendPackets(new S_SystemMessage("\\F2你的镖车已经是最高了。"));
    			return;
    		}
    		if (!pc.getInventory().checkItem(40308, 10000)){
    			pc.sendPackets(new S_SystemMessage("\\F2金币不足10000。"));
    			return;
    		}
    		pc.getInventory().consumeItem(40308, 10000);
    		final int rnd = _random.nextInt(100) + 1;
    		int c = 0;
    		if (rnd <= 5){//5%机率紫色
    			c = 4;
    		}else if (rnd <= 10){//10%机率绿色
    			c = 3;
    		}else if (rnd <= 20){//20%机率黄色
    			c = 2;
    		}else if (rnd <= 30){//30%机率黄色
    			c = 1;
    		}else{
    			c = 0;
    		}
    		biaoche.setColor(c);
    		biaoche.broadcastPacket(new S_NPCPack(biaoche));
    		pc.sendPackets(new S_SystemMessage("\\F2刷新成功"));
    	}
    }
}
