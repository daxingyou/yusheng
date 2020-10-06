package l1j.server.server.timecontroller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.world.L1World;

public class PetSaveTimer extends TimerTask{
	
	public void start(){
		GeneralThreadPool.getInstance().execute(this);
	}

	@Override
	public void run() {
		
		while (true) {
			try {
				final ArrayList<L1Character> pcList = new ArrayList<L1Character>();
				pcList.addAll(L1World.getInstance().getAllPets());
				pcList.addAll(L1World.getInstance().getAllSummons());
				if (pcList.isEmpty()) {
					Thread.sleep(1000);
					continue;
				}
				for (L1Character tgpc : pcList) {
					if (tgpc.getPinkSec()>0) {
						tgpc.setPinkName(true);
						tgpc.setPinkSec(tgpc.getPinkSec()-1);
					}else {
						tgpc.setPinkSec(0);
						tgpc.setPinkName(false);
					}
				}
				pcList.clear();
				Thread.sleep(1000);
			} catch (Exception e) {
				final PetSaveTimer checkWar = new PetSaveTimer();
				checkWar.start();
				break;
			}
		}
		
	}

}
