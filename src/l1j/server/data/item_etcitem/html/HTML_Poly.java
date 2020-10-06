package l1j.server.data.item_etcitem.html;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NPCTalkReturn;

public class HTML_Poly extends ItemExecutor{

	private HTML_Poly()
	{
	}

	public static ItemExecutor get()
	{
		return new HTML_Poly();
	}
	
	public void execute(final int[] arg0,final L1PcInstance pc,final L1ItemInstance item) {
		if (pc == null){
			return;
		}
		pc.sendPackets(new S_NPCTalkReturn(pc.getId(),"monlist_tsbs"));
	}
}
