package l1j.server.server.datatables.storage;

public interface BalanceStorage {
	
	/**
	 * 资料预先载入
	 */
	public void load();
	
	public void addBalance(String name, int bce);
	
	public String getBalance();

}
