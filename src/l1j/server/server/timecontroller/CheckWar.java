package l1j.server.server.timecontroller;

import java.util.ArrayList;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1CastleLocation;
import l1j.server.server.model.Instance.L1HierarchInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_PinkName;
import l1j.server.server.serverpackets.S_TrueTarget;
import l1j.server.server.world.L1World;

public class CheckWar implements Runnable {
	
	public void start(){
		GeneralThreadPool.getInstance().execute(this);
	}

	@Override
	public void run() {
		
		while (true) {
			try {
				final ArrayList<L1PcInstance> pcList = new ArrayList<L1PcInstance>();
/*				if (L1World.getInstance().getAllPlayers() == null) {
					Thread.sleep(1000);
					continue;
				}*/
				pcList.addAll(L1World.getInstance().getAllPlayers());
				if (pcList.isEmpty()) {
					Thread.sleep(1000);
					continue;
				}
				for (L1PcInstance tgpc : pcList) {
					if (tgpc.getPinkSec()>0) {
						tgpc.setPinkName(true);
						tgpc.setPinkSec(tgpc.getPinkSec()-1);
					}else {
						tgpc.setPinkSec(0);
						tgpc.setPinkName(false);
					}
						if (tgpc.getdeadsec()>0&&tgpc.isDead()) {
							tgpc.setdeadsec(tgpc.getdeadsec() - 1);
						}else {
							tgpc.setdeadsec(600);
						}
						if (tgpc.getdeadsec()<=0) {
							if (tgpc.getNetConnection() != null){
								tgpc.getNetConnection().kick();
							}
						}
				}
				pcList.clear();
				Thread.sleep(1000);
			} catch (Exception e) {
				final CheckWar checkWar = new CheckWar();
				checkWar.start();
				break;
			}
		}
		
	}


}
