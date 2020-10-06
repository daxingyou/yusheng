package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.L1PolyMorph;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;

public class Poly_Reel extends ItemExecutor{
	
	/**
	 *
	 */
	private Poly_Reel() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Poly_Reel();
	}

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		if (item == null) {
			return;
		}
		if (pc.getMapId() == 5124) {
			pc.sendPackets(new S_ServerMessage(1170));
			return;
		}
		if (pc.getLevel() < _level) {
			pc.sendPackets(new S_SystemMessage("等级"+_level+"才可以变身！"));
			return;
		}
		if (_polyid > 0 && _sec > 0) {
			pc.getInventory().removeItem(item, 1);
			L1PolyMorph.doPoly(pc, _polyid, _sec);
		}	
	}
	
	private int _level = 1;
	private int _polyid = 0;
	private int _sec = 0;
	
	@Override
	public void set_set(String[] set) {
		try {
			_level = Integer.parseInt(set[1]);
		} catch (Exception e) {
			_level = 1;
		}
		
		try {
			_polyid = Integer.parseInt(set[2]);
		} catch (Exception e) {
			_polyid = 0;
		}
		
		try {
			_sec = Integer.parseInt(set[3]);
		} catch (Exception e) {
			_sec = 60;
		}
	
	}
}
