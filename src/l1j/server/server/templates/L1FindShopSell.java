package l1j.server.server.templates;

public class L1FindShopSell {

	private int _sellPrice;

	public void setSellPrice(int i) {
		_sellPrice = i;
	}

	public int getSellPrice() {
		return _sellPrice;
	}
	
	private int _x;

	public void setX(int i) {
		_x = i;
	}

	public int getX() {
		return _x;
	}
	
	private int _y;

	public void setY(int i) {
		_y = i;
	}

	public int getY() {
		return _y;
	}
	
	private short _mapId;

	public void setMapId(short i) {
		_mapId = i;
	}

	public short getMapId() {
		return _mapId;
	}
	
	private String _name;

	public void setName(String i) {
		_name = i;
	}

	public String getName() {
		return _name;
	}
	
	private String _sellItemName = ""; // 卖累计

	public void setSellItemName(String itemName) {
		_sellItemName = itemName;
	}

	public String getSellItemName() {
		return _sellItemName;
	}
}
