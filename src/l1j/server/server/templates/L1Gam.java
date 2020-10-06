package l1j.server.server.templates;

public class L1Gam {
	private int _gamId;
	
	private int _gamNpcId;
	
	private String _gamNpcName;
	
	public void setGamID(final int id){
		_gamId = id;
	}
	
	public void setGamNpcId(final int npcid){
		_gamNpcId = npcid;
	}
	
	public void setGamNpcName(final String name){
		_gamNpcName = name;
	}
	
	public int getGamNumId(){
		return _gamId;
	}
	
	public int getGamNpcId(){
		return _gamNpcId;
	}
	
	public String getGamName(){
		return _gamNpcName;
	}

}
