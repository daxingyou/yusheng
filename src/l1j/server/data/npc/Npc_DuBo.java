package l1j.server.data.npc;

import l1j.server.Config;
import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.model.L1DuBo;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_HowManyMake;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

public class Npc_DuBo extends NpcExecutor{

	private Npc_DuBo(){
		
	}
	
	@Override
	public int type() {
		return 3;
	}
	 
	public static NpcExecutor get(){
		return new Npc_DuBo();
	}
	
	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc){
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "dugui",L1DuBo.get(npc).MsgDate(pc)));
	}
	
	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc,
			final String cmd, final long amount) {
		final L1ItemInstance itemExist = pc.getInventory().findItemId(40308);
		if (itemExist == null) {
			pc.sendPackets(new S_SystemMessage("\\F2连金币都没有..."));
			return;
		}
		long ybcount = 0;
		ybcount = itemExist.getCount();
		if (ybcount > 100000000) {
			ybcount = 100000000;
		}
		if (cmd.equalsIgnoreCase("a")){
			if (!Config.DuBo){
				pc.sendPackets(new S_SystemMessage("\\F2赌博未开启."));
				return;
			}
			if (L1DuBo.get(npc).getStartOn() == 0 || L1DuBo.get(npc).getStartOn() == 1){
				pc.sendPackets(new S_HowManyMake(npc.getId(),ybcount,"a_a"));
			}else{
				pc.sendPackets(new S_SystemMessage("\\F2抢庄时间已过."));
			}
		}else if (cmd.equalsIgnoreCase("b")){
			if (!Config.DuBo){
				pc.sendPackets(new S_SystemMessage("\\F2赌博未开启."));
				return;
			}
			if (L1DuBo.get(npc).getStartOn() == 2){
				pc.sendPackets(new S_HowManyMake(npc.getId(), ybcount,"b_b"));
			}else{
				pc.sendPackets(new S_SystemMessage("\\F2压住时间已过."));
			}
		}else if (cmd.equalsIgnoreCase("c")){
			if (!Config.DuBo){
				pc.sendPackets(new S_SystemMessage("\\F2赌博未开启."));
				return;
			}
			if (L1DuBo.get(npc).getStartOn() == 2){
				pc.sendPackets(new S_HowManyMake(npc.getId(), ybcount,"c_c"));
			}else{
				pc.sendPackets(new S_SystemMessage("\\F2压住时间已过."));
			}
		}else if (cmd.equalsIgnoreCase("a_a")){
			if (!Config.DuBo){
				return;
			}
			if (L1DuBo.get(npc).getStartOn() == 0 || L1DuBo.get(npc).getStartOn() == 1){
				L1DuBo.get(npc).setDuBoPc(pc, (int)amount, 0);
			}else{
				pc.sendPackets(new S_SystemMessage("\\F2抢庄时间已过."));
			}
		}else if (cmd.equalsIgnoreCase("b_b")){
			if (!Config.DuBo){
				return;
			}
			if (L1DuBo.get(npc).getStartOn() == 2){
				L1DuBo.get(npc).setDuBoPc(pc, (int)amount, 1);
			}else{
				pc.sendPackets(new S_SystemMessage("\\F2压住时间已过."));
			}
		}else if (cmd.equalsIgnoreCase("c_c")){
			if (!Config.DuBo){
				return;
			}
			if (L1DuBo.get(npc).getStartOn() == 2){
				L1DuBo.get(npc).setDuBoPc(pc, (int)amount, 2);
			}else{
				pc.sendPackets(new S_SystemMessage("\\F2压住时间已过."));
			}
		}else if (cmd.equalsIgnoreCase("online")){
			final int onlecount = L1DuBo.get(npc).getOnlineCount(pc);
			if (onlecount > 0){
				pc.getInventory().storeItem(40308, onlecount);
				pc.sendPackets(new S_ServerMessage(143, "赌博大师","金币(" + onlecount + ")"));
				WriteLogTxt.Recording("赌博记录","玩家:"+ pc.getName() +"领取掉线存储金币(" + onlecount + ")");
			}else{
				pc.sendPackets(new S_SystemMessage("你没有掉线存储 或者你已经领取过了."));
			}
		}
	}
}
