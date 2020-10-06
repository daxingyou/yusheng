package l1j.server.data.npc.ezpay;

import java.util.Map;

import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.lock.EzpayReading;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CloseList;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ShopSellList;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.world.L1World;

public class NPC_XuanChuan extends NpcExecutor{

	private NPC_XuanChuan(){
		
	}
	
	@Override
	public int type() {
		return 3;
	}
	 
	public static NpcExecutor get(){
		return new NPC_XuanChuan();
	}
	
	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc){
		final  Map<Integer, int[]> xuanchuanMap = EzpayReading.get().XuanChuanInfo(pc.getAccountName());
		int count = 0;
		if (xuanchuanMap != null && !xuanchuanMap.isEmpty()){
			for(final int[] xuanchuanInts : xuanchuanMap.values()){
				if (xuanchuanInts != null){
					count += xuanchuanInts[2];
				}
			}
		}
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "xuanchuan", new String[]{String.valueOf(count)}));
	}
	
	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount){
		if (cmd.equalsIgnoreCase("buy")) {
			pc.sendPackets(new S_ShopSellList(npc.getId()));
		}
		if (cmd.equalsIgnoreCase("lq")){
			final  Map<Integer, int[]> xuanchuanMap = EzpayReading.get().XuanChuanInfo(pc.getAccountName());
			if (xuanchuanMap != null && !xuanchuanMap.isEmpty()){
				int count = 0;
				int itemId = 0;
				if (xuanchuanMap != null && !xuanchuanMap.isEmpty()){
					for(final int[] xuanchuanInts : xuanchuanMap.values()){
						if (xuanchuanInts != null){
							if (EzpayReading.get().updateXuanChuan(pc.getAccountName(), xuanchuanInts[0], pc.getName(), pc.getNetConnection().getIp())){
								count += xuanchuanInts[2];
							}
							if (itemId == 0){
								itemId = xuanchuanInts[1];
							}
						}
					}
				}
				if (count > 0){
					final L1Item item = ItemTable.getInstance().getTemplate(itemId);
					if (item != null){
						pc.getInventory().storeItem(itemId, count);
						pc.sendPackets(new S_SystemMessage(String.format("%s给你%s(%d)", npc.getName(),item.getName(),count)));
						L1World.getInstance().broadcastServerMessage(
								String.format("\\F4玩家(" + pc.getName()
										+ ")领取了宣传币:[" + count
										+ "]个!"));
						pc.sendPackets(new S_CloseList(pc.getId()));
					}
				}
			}else{
				pc.sendPackets(new S_SystemMessage("你还没宣传或者你已经领取过了."));
				pc.sendPackets(new S_CloseList(pc.getId()));
			}
		}else{
			pc.sendPackets(new S_CloseList(pc.getId()));
		}
	}
}
