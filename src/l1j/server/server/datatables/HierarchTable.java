package l1j.server.server.datatables;

public class HierarchTable {
	
	private static HierarchTable _instance;
	
	public static HierarchTable getInstance() {
		if (_instance == null) {
			_instance = new HierarchTable();
		}
		return _instance;
	}
	
	public void load(){
		
	}

}
