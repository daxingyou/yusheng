package l1j.server.server.templates;

public class L1CharacterSpeed {
	private int _attackspeed;
	
	private int _movespeed;
	
	private int _injustice_count_limit;
	
	private int _justice_count_limit;
	
	public void setAttackSpeed(final int speed){
		_attackspeed = speed;
	}
	
	public void setMoveSpeed(final int speed){
		_movespeed = speed;
	}
	
	public void setInjustice(final int injustice){
		_injustice_count_limit = injustice;
	}
	
	public void setJustice(final int justice){
		_justice_count_limit = justice;
	}
	
	public int getAttackSpeed(){
		return _attackspeed;
	}
	
	public int getMoveSpeed(){
		return _movespeed;
	}
	
	public int getInjustice(){
		return _injustice_count_limit;
	}
	
	public int getJustice(){
		return _justice_count_limit;
	}
	
	
}
