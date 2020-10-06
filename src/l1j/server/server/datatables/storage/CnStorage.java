package l1j.server.server.datatables.storage;

import java.util.Map;

import l1j.server.server.templates.Tbs;

public interface CnStorage {
	
	/**
	 * 资料预先载入
	 */
	public void load();
	
	public boolean isload();
	
	
	public  void addMap(final Integer objId, final Tbs overtime);
	
	public  void addcnMap(final String objId, final Tbs tbs);
	
	public  Tbs getCn(final String objId);
	
	public Map<Integer, Tbs> cnmap();
	
	public Tbs getCnOther(final int key);
	
	public Tbs getCnOther(final String name);
	
	public void storeCnOther(final String name);
	
	public void delCnOther(final Tbs key);
	
	

}
