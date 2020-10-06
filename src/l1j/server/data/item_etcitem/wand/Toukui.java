package l1j.server.data.item_etcitem.wand;


import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.world.L1World;

public class Toukui extends ItemExecutor{
	private Toukui() {
		// TODO Auto-generated constructor stub
	}

	public static ItemExecutor get() {
		return new Toukui();
	}

	@Override
	public void execute(int[] data, L1PcInstance pc, L1ItemInstance item) {
		final int spellsc_objid = data[0];

		final L1Object target = L1World.getInstance().findObject(spellsc_objid);

		if (target == null) {
			return;
		}
		if (target instanceof L1PcInstance) {
			L1PcInstance tgpc = (L1PcInstance)target;
			L1ItemInstance tgitem = tgpc.getInventory().findItemId(99993);
			if (tgitem == null) {
				if (tgpc.getWeapon()!=null) {
					pc.sendPackets(new S_SystemMessage("对方持有武器 +"+tgpc.getWeapon().getEnchantLevel()+" "+tgpc.getWeapon().getName()));
				}else {
					pc.sendPackets(new S_SystemMessage("对方赤手空拳!"));		
				}
				tgpc.sendPackets(new S_SystemMessage(pc.getName()+" 成功对你使用了道具 "+item.getName()+" 查看了您的武器"));
			}else {
				long tgitemcount = tgitem.getCount() - 1;			
				pc.sendPackets(new S_SystemMessage(tgpc.getName()+" 消耗"+tgitem.getName()+"1个抵挡了此次探查，还剩"+tgitemcount+"个"+tgitem.getName()+"此次探查失败!"));
				tgpc.sendPackets(new S_SystemMessage(pc.getName()+" 对你使用了道具 "+item.getName()+" ，消耗了您一个"+tgitem.getName()+" 对方查看您的武器失败!"));
				tgpc.getInventory().removeItem(tgitem, 1);
			}			
			pc.getInventory().removeItem(item, 1);
		}

	}

}
