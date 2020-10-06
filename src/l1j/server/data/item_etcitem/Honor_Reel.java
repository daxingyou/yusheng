package l1j.server.data.item_etcitem;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.WriteLogTxt;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.model.L1PcInventory;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class Honor_Reel extends ItemExecutor {

//	private static final Log _log = LogFactory.getLog(Honor_Reel.class);

	/**
	 *
	 */
	private Honor_Reel() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Honor_Reel();
	}

	/**
	 * 道具物件執行
	 * 
	 * @param data
	 *            參數
	 * @param pc
	 *            執行者
	 * @param item
	 *            物件
	 */
	@Override
	public void execute(final int[] data, final L1PcInstance pc,
			final L1ItemInstance item) {
		// 例外狀況:物件為空
		if (item == null) {
			return;
		}
		// 例外狀況:人物為空
		if (pc == null) {
			return;
		}
		if (pc.getLevel()<10) {
			pc.sendPackets(new S_SystemMessage("你太弱了，经验无法存储！"));
			return;
		}
		if (pc.getLevel()>82) {
			pc.sendPackets(new S_SystemMessage("您的等级经验胶囊已经无法承受了！"));
			return;
		}
		int needExp = ExpTable.getNeedExpNextLevel(pc.getLevel());
		double exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
		needExp *= 0.05;	
		if (item.getChargeCount() == 0) {
			int exp  = ExpTable.getExpByLevel(pc.getLevel());
			if (pc.getExp()-needExp<exp) {
				pc.sendPackets(new S_SystemMessage("由于有掉级的风险，经验不予以存储！"));
				return;
			}			
			pc.addExp(-needExp);//pc.getLevel()
			WriteLogTxt.Recording("经验记录","玩家:"+pc.getName()+"使用经验胶囊扣除"+needExp+"剩下经验值为("+pc.getExp()+")");
			needExp /= exppenalty;
			item.setChargeCount(needExp);
			pc.getInventory().updateItem(item,
					L1PcInventory.COL_CHARGE_COUNT);
			pc.getInventory().saveItem(item,
					L1PcInventory.COL_CHARGE_COUNT);		
			pc.sendPackets(new S_SystemMessage("扣除您的"+needExp+"经验，存储到"+item.getName()+"成功"+"！"));
			pc.sendPackets(new S_SystemMessage("温馨提示，存储经验的"+item.getName()+"会显示经验值，鉴定后可以看到！"));		
		}else {
//			int pclevel = pc.getLevel()+1;
			int allexp = item.getChargeCount();
			WriteLogTxt.Recording("经验记录","玩家:"+pc.getName()+"使用经验胶囊增加经验使用前经验值为("+pc.getExp()+")");
			while (true) {
				int addexp = 0;//增加的经验
				int itemexp = 0;
//				System.out.println("步骤1");
				int dgExp = item.getChargeCount();
				long nextExp = ExpTable.getExpByLevel(pc.getLevel() + 1) - pc.getExp();
				exppenalty = ExpTable.getPenaltyRate(pc.getLevel());
				addexp = (int) (dgExp*exppenalty);
				if (addexp > nextExp) {
					addexp = (int) nextExp;
					pc.addExp(addexp);
					itemexp = (int) (dgExp - (addexp/exppenalty));
					item.setChargeCount(itemexp);
	/*				pc.getInventory().updateItem(item,
							L1PcInventory.COL_CHARGE_COUNT);
					pc.getInventory().saveItem(item,
							L1PcInventory.COL_CHARGE_COUNT);		*/
				}
				else
				{
					pc.addExp(addexp);
//					System.out.println("步骤2");
					pc.sendPackets(new S_SystemMessage("恭喜您，服用了 "+item.getName()+" 增加了"+allexp+"经验！"));
					pc.getInventory().removeItem(item, 1);
					break;				
				}
			}
			WriteLogTxt.Recording("经验记录","玩家:"+pc.getName()+"使用经验胶囊增加经验使用#"+allexp+"#经验胶囊后经验值为("+pc.getExp()+")");
			
		}
		try {
			pc.save();
		} catch (final Exception e) {
			//_log.error(e.getLocalizedMessage(), e);
		}
	}
}

