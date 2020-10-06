package l1j.server.data.item_etcitem.doll;

import l1j.server.data.executor.ItemExecutor;
import l1j.server.server.datatables.DollPowerTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.Instance.L1BabyInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Doll;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Npc;

public class Magic_Doll extends ItemExecutor {

    /**
	 *
	 */
    private Magic_Doll() {
        // TODO Auto-generated constructor stub
    }

    public static ItemExecutor get() {
        return new Magic_Doll();
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
    	if (item.isXiLianIng()){
    		pc.sendPackets(new S_SystemMessage("\\F3正在洗练中"));
    		return;
    	}
        final int itemId = item.getItemId();
        final int itemObjId = item.getId();
        this.useMagicDoll(pc, itemId,itemObjId);
    }

    private void useMagicDoll(final L1PcInstance pc, final int itemId,final int itemObjId) {
    	for (Object babyObject : pc.getPetList().values().toArray()) {
			if (babyObject instanceof L1BabyInstance) {
				L1BabyInstance baby = (L1BabyInstance) babyObject;
				baby.set_currentPetStatus(6); // 解散
				return;
			}
		}
        boolean iserror = false;
        final L1Doll type = DollPowerTable.get().get_type(itemId);
        if (type != null) {
            if (type.get_need() != null) {
                final int[] itemids = type.get_need();
                final int[] counts = type.get_counts();

                for (int i = 0; i < itemids.length; i++) {
                    if (!pc.getInventory().checkItem(itemids[i], counts[i])) {
                        final L1Item temp = ItemTable.getInstance().getTemplate(itemids[i]);
                        pc.sendPackets(new S_ServerMessage(337, temp.getNameId()));
                        iserror = true;
                    }
                }

                if (!iserror) {
                    for (int i = 0; i < itemids.length; i++) {
                        pc.getInventory().consumeItem(itemids[i], counts[i]);
                    }
                }
            }

            if (!iserror) {
                final L1Npc template = NpcTable.getInstance().getTemplate(200002);
                final L1BabyInstance baby =  new L1BabyInstance(template,pc,type,itemObjId);
                baby.set_currentPetStatus(1);
            }
        }
    }
}
