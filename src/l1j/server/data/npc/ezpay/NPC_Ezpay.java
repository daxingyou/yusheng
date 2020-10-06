package l1j.server.data.npc.ezpay;

import java.util.Map;

import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.lock.EzpayReading;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_CloseList;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.world.L1World;

public class NPC_Ezpay extends NpcExecutor{

	private NPC_Ezpay(){
		
	}
	
	@Override
	public int type() {
		return 3;
	}
	 
	public static NpcExecutor get(){
		return new NPC_Ezpay();
	}
	
	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc){
		final  Map<Integer, int[]> ezpayMap = EzpayReading.get().ezpayInfo(pc.getAccountName(),0);
		int count = 0;
		if (ezpayMap != null && !ezpayMap.isEmpty()){
			for(final int[] ezpayInts : ezpayMap.values()){
				if (ezpayInts != null){
					count += ezpayInts[2];
				}
			}
		}
		final  Map<Integer, int[]> ezpayMapcn = EzpayReading.get().ezpayInfo(pc.getAccountName(),1);
		if (ezpayMapcn != null && !ezpayMapcn.isEmpty()){
			for(final int[] ezpaycnInts : ezpayMapcn.values()){
				if (ezpaycnInts != null){
					count += ezpaycnInts[2];
				}
			}
		}
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "ezpay", new String[]{String.valueOf(count)}));
	}
	
	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount){
		if (cmd.equalsIgnoreCase("lq")){
			final  Map<Integer, int[]> ezpayMap = EzpayReading.get().ezpayInfo(pc.getAccountName(),0);
			final  Map<Integer, int[]> ezpayMapcn = EzpayReading.get().ezpayInfo(pc.getAccountName(),1);
			boolean IsOk = false;
			if (ezpayMap != null && !ezpayMap.isEmpty()){
				IsOk = true;
			}
			if (ezpayMapcn != null && !ezpayMapcn.isEmpty()){
				IsOk = true;
			}
			if (IsOk){
				int count = 0;
				int itemId = 0;
				if (ezpayMap != null && !ezpayMap.isEmpty()){
					for(final int[] ezpayInts : ezpayMap.values()){
						if (ezpayInts != null){
							if (EzpayReading.get().update(pc.getAccountName(), ezpayInts[0], pc.getName(), pc.getNetConnection().getIp(),0)){
								count += ezpayInts[2];
							}
							if (itemId == 0){
								itemId = ezpayInts[1];
							}
						}
					}
				}
				if (ezpayMapcn != null && !ezpayMapcn.isEmpty()){
					for(final int[] ezpayIntcns : ezpayMapcn.values()){
						if (ezpayIntcns != null){
							if (EzpayReading.get().update(pc.getAccountName(), ezpayIntcns[0], pc.getName(), pc.getNetConnection().getIp(),1)){
								count += ezpayIntcns[2];
							}
							if (itemId == 0){
								itemId = ezpayIntcns[1];
							}
						}
					}
				}
				if (count > 0){
					final L1Item item = ItemTable.getInstance().getTemplate(itemId);
					if (item != null){
						pc.addEzpayCount(count);
						pc.getInventory().storeItem(itemId, count);
						pc.sendPackets(new S_SystemMessage("领取充值" + item.getName() + "(" + count + ")"));
						L1World.getInstance().broadcastServerMessage(
								String.format("\\F4玩家(:" + pc.getName()
										+ ")充值成功.领取了:[" + count
										+ "]元宝!"));
						pc.sendPackets(new S_CloseList(pc.getId()));
						try {
							pc.save();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}else{
				pc.sendPackets(new S_SystemMessage("你还没有充值或者你已经领取过了."));
				pc.sendPackets(new S_CloseList(pc.getId()));
			}
		}
	}
}
