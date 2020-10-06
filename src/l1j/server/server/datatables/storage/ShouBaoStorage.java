package l1j.server.server.datatables.storage;

import java.util.Map;

import l1j.server.server.templates.L1ShouBaoTemp;

public interface ShouBaoStorage {
	public void load();
	public L1ShouBaoTemp getTemp(final int itemId);
	public boolean update(final L1ShouBaoTemp tmp,final int objId,final String name);
	public Map<Integer,L1ShouBaoTemp> getShouBaoMaps();
	public int getCount();
	public long getAmcount0();
	public long getAmcount1();
}