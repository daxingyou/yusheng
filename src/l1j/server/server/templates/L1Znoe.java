package l1j.server.server.templates;

public class L1Znoe {
private final String _mapName;
	
	private final int _mapId;
	
	private final int _startX;
	
	private final int _startY;
	
	private final int _endX;
	
	private final int _endY;
	
	public L1Znoe(final int mapId,final String mapName, final int startX,final int startY,final int endX,final int endY){
		_mapId = mapId;
		_mapName = mapName;
		_startX = startX;
		_startY = startY;
		_endX = endX;
		_endY = endY;
	}
	
	public String getMapName(){
		return _mapName;
	}
	
	public int getStartX(){
		return _startX;
	}
	
	public int getStartY(){
		return _startY;
	}
	
	public int getEndX(){
		return _endX;
	}
	
	public int getEndY(){
		return _endY;
	}
	
	public int getMapId(){
		return _mapId;
	}
}
