package l1j.server.server.command.executor;

import java.util.ArrayList;
import java.util.logging.Logger;

import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;

public class L1DelInventory implements L1CommandExecutor{
	
	private L1DelInventory() {
	}

	public static L1CommandExecutor getInstance() {
		return new L1DelInventory();
	}

	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			ArrayList<L1ItemInstance> itemlist =  new ArrayList<>();
			itemlist.addAll(pc.getInventory().getItems());
			for (L1ItemInstance item : itemlist) {
				pc.getInventory().removeItem(item);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}		
	}

}
