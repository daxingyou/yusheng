package l1j.server.server.datatables.storage;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import l1j.server.server.templates.L1CharacterAdenaTrade;

public interface CharacterAdenaTradeStorage {

	public void load();
	
	public boolean createAdenaTrade(final L1CharacterAdenaTrade adenaTrade);
	
	public boolean updateAdenaTrade(final int id,final int over);
	
	public Map<Integer, L1CharacterAdenaTrade> getAdenaTrades();
	
	public L1CharacterAdenaTrade getCharacterAdenaTrade(final int id);
	
	public int nextId();
	
	public Collection<L1CharacterAdenaTrade> getAllCharacterAdenaTrades();

}
