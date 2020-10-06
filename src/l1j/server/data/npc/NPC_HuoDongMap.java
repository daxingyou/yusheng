package l1j.server.data.npc;

import l1j.server.Config;
import l1j.server.data.executor.NpcExecutor;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;
import l1j.server.server.timecontroller.HuoDongMapTimer;

public class NPC_HuoDongMap extends NpcExecutor{

	private NPC_HuoDongMap(){
		
	}
	
	@Override
	public int type() {
		return 3;
	}
	 
	public static NpcExecutor get(){
		return new NPC_HuoDongMap();
	}
	
	@Override
	public void talk(final L1PcInstance pc, final L1NpcInstance npc){
		pc.sendPackets(new S_NPCTalkReturn(npc.getId(), "huodong"));
	}
	
	@Override
	public void action(final L1PcInstance pc, final L1NpcInstance npc, final String cmd, final long amount){
		if (cmd.equalsIgnoreCase("go")){
			if (!HuoDongMapTimer.IsStart){
				pc.sendPackets(new S_SystemMessage("活动还未开启."));
				return;
			}
			if (pc.isInParty()){
				pc.sendPackets(new S_SystemMessage("请先退出队伍"));
				return;
			}
			if (Config.HUODONGITEMID > 0){
				if (!pc.getInventory().checkItem(Config.HUODONGITEMID, Config.HUODONGITEMCOUNT)){
					final L1Item item = ItemTable.getInstance().getTemplate(Config.HUODONGITEMID);
					if (item != null){
						pc.sendPackets(new S_SystemMessage(new StringBuilder().append(item.getName()).append("(").append(Config.HUODONGITEMCOUNT).append(")不足.").toString()));
					}
					return;
				}
				pc.getInventory().consumeItem(Config.HUODONGITEMID, Config.HUODONGITEMCOUNT);
			}
			
			L1PolyMorph.undoPoly(pc);
			final L1Location newLocation = new L1Location(Config.HUODONGLOCX, Config.HUODONGLOCY, Config.HUODONGMAPID).randomLocation(200, false);
			L1Teleport.teleport(pc, newLocation.getX(),newLocation.getY(), (short)newLocation.getMapId(), 5, true);
		}
	}
}
