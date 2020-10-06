package l1j.server.data.item_etcitem;

import java.util.Random;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;


/**
 * 骰子1阶40325<BR>
 * 骰子3阶40326<BR>
 * 骰子4阶40327<BR>
 * 骰子6阶40328<BR>
 */
public class Dice extends ItemExecutor {

    /**
	 *
	 */
    private Dice() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Dice();
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
    public void execute(final int[] data, final L1PcInstance pc,
            final L1ItemInstance item) {
        final Random _random = new Random();
        int gfxid = 0;
        if (_dian == 0 && pc.getDiceZuoBi() > 0){
        	gfxid = 3204 + pc.getDiceZuoBi() - 1;
        	pc.setDiceZuoBi(0);
        }else if (_dian > 0 && _dian <= 6){
	    	gfxid = 3204 + _dian-1;
	    }else{
	    	gfxid = 3204 + _random.nextInt(6);
	    }
        if (gfxid != 0) {
            //pc.getInventory().consumeItem(40318, 1);
            pc.sendPacketsAll(new S_SkillSound(pc.getId(), gfxid));
            try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
            pc.sendPacketsAll(new S_SystemMessage("\\fH【" + pc.getName() + "】玩家使用骰子开出点数为:" + ((gfxid - 3204) + 1) + "点"));
        }
    }
    private int _dian = 0;
    public void set_set(String[] set) {
    	try {
			if (set.length > 1){
				_dian = Integer.parseInt(set[1]);
			}
		} catch (Exception e) {
			
		}
    }
}
