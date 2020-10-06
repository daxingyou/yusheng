package l1j.server.server.datatables.storage;

import java.util.Map;

import l1j.server.server.templates.L1ShouShaTemp;

public interface ShouShaStorage {
	public void load();
	public L1ShouShaTemp getTemp(final int npcId);
	public boolean update(final L1ShouShaTemp tmp,final int objId,final String name);
	public Map<Integer, L1ShouShaTemp> getShouShaMaps();
	public int getCount();
	public long getAmcount0();
	public long getAmcount1();
}
