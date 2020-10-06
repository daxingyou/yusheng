package l1j.server.data.item_etcitem.reel;

import java.util.Random;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ItemStatus;
import l1j.server.server.serverpackets.S_SystemMessage;


/**
 * <font color=#00800>pvp镶嵌宝石</font><BR>
 *
 */
public class ScrollEnchantWeaponPVP extends ItemExecutor {

	/**
	 *
	 */
	private ScrollEnchantWeaponPVP() {
		// TODO Auto-generated constructor stub
	}
	
	public static ItemExecutor get() {
		return new ScrollEnchantWeaponPVP();
	}

	/**
	 * 道具物件執行
	 * @param data 參數
	 * @param pc 執行者
	 * @param item 物件
	 */
	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		// 對象OBJID
		final int targObjId = data[0];

		final L1ItemInstance tgItem = pc.getInventory().getItem(targObjId);

		if (tgItem != null && tgItem.getItem().getType2() == 1) { // 武器类
                if (tgItem.getUpdatePVP() >= 9) { // 获取还可镶嵌次数
                        pc.sendPackets(new S_SystemMessage("已经满级PVP属性了!"));//\F2提示：【%0%s】不可再镶嵌宝石！
                        return;
                }
                Random _random = new Random();
                int chance = _random.nextInt(7) + 1;
                if (chance==3) {
                	tgItem.setUpdatePVP(tgItem.getUpdatePVP() + 1); // PVP+1
                    pc.sendPackets(new S_SystemMessage("\\aD成功附加PVP属性+1.提高PK伤害+1"));//
                    pc.getInventory().updateItem(tgItem,L1PcInventory.COL_PVP);
        			pc.getInventory().saveItem(tgItem, L1PcInventory.COL_PVP);
                    //----------------------更新数据库------------------------
            		final int oldEnchantLvl = tgItem.getUpdatePVP();//获取当前镶嵌点数
            		final int newEnchantLvl = oldEnchantLvl + 1;//得到新的镶嵌点数
            		if (oldEnchantLvl != newEnchantLvl) {
            			pc.sendPackets(new S_ItemStatus(tgItem));//刷新物品
            			pc.getInventory().updateItem(tgItem,L1PcInventory.COL_PVP);
            			pc.getInventory().saveItem(tgItem, L1PcInventory.COL_PVP);
            		}
            		//----------------------更新数据库------------------------
                } else {
                    pc.sendPackets(new S_SystemMessage("\\aFPVP属性附加失败...."));//\F2提示：【%0%s】宝石镶嵌失败！
                }
                pc.getInventory().removeItem(item, 1);//删除镶嵌道具
        } else {
                pc.sendPackets(new S_SystemMessage("没有任何事情发生。"));
        }
	}
}