package l1j.server.server.datatables;

import l1j.server.server.datatables.storage.BalanceStorage;

public class BalanceTable implements BalanceStorage{
	
	private static BalanceTable _instance;
	
	public static BalanceTable get() {
		if (_instance == null) {
			_instance = new BalanceTable();
		}
		return _instance;
	}

	@Override
	public void load() {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public void addBalance(String name, int bce) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	public String getBalance() {
		// TODO 自动生成的方法存根
		return null;
	}

}
