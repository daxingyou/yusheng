package l1j.server.data.item_etcitem;

import java.util.ArrayList;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.datatables.lock.CharaterTradeReading;
import l1j.server.server.model.L1PCAction;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1CharaterTrade;

public class Item_CharTrade extends ItemExecutor {
    /**
	 *
	 */
    private Item_CharTrade() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Item_CharTrade();
    }
    /**
     * 道具物件执行
     * 
     * @param data
     *            参数
     * @param pc
     *            执行者
     * @param item
     *            物件
     */
    @Override
    public void execute(final int[] data, final L1PcInstance pc,final L1ItemInstance item) {
    	if (pc == null){
    		return;
    	}
    	if (item == null){
    		return;
    	}
        L1CharaterTrade charaterTrade = item.getItemCharaterTrade();
        pc.setTempID(item.getId());
    	if (charaterTrade == null){
    		pc.clearTempObjects();
    		CharaterTradeReading.get().loadCharacterName(pc);
    		if (pc.getTempObjects().isEmpty() || pc.getTempObjects().size() <= 0){
    			pc.sendPackets(new S_SystemMessage("\\F1你当前没有多余的人物。"));
    			return;
    		}
    		final String[] datas = new String[14];
    		int m = 0;
    		for(int i = 0; i < pc.getTempObjects().size();i++){
    			final L1CharaterTrade charaterTrade2 = (L1CharaterTrade)pc.getTempObjects().get(i);
    			datas[m] = "绑定:";
    			datas[m + 1] = String.format("%s Lv[%d] 职业[%s]", charaterTrade2.getName(),charaterTrade2.getLevel(),L1PCAction.TYPE_CLASS[charaterTrade2.get_Type()]);
    			m += 2;
    		}
    		for(;m < 14;m++){
    			datas[m] = " ";
    		}
    		pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "tradechar0", datas));
    	}else{
    		final ArrayList<String> list = pc.getNetConnection().getAccount().loadCharacterItems(charaterTrade.get_char_objId());
    		pc.sendPackets(new S_NPCTalkReturn(pc.getId(), "tradechar1", charaterTrade.getName() + "[绑定]",charaterTrade.getLevel(), list));
    	}
    }
}
