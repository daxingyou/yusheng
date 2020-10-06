package l1j.server.server.serverpackets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import l1j.server.server.Opcodes;
import l1j.server.server.datatables.DollPowerTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.Instance.L1BabyInstance;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.templates.L1Doll;
import l1j.server.server.templates.L1Drop;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Pc;
import l1j.server.server.world.L1World;


public class S_RetrieveList extends ServerBasePacket
{
    /**
     * 物品名单(个人仓库)
     * 
     * @param objid
     * @param pc
     */
    public S_RetrieveList(final int objid, final L1PcInstance pc) {
        if (pc.getInventory().getSize() < 180) {
        	final L1Pc dwarpc = L1World.getInstance().getPc(pc.getAccountName());
            final int size = dwarpc.getDwarfInventory().getSize();
            if (size > 0) {
                this.writeC(Opcodes.S_OPCODE_SHOWRETRIEVELIST);
                this.writeD(objid);
                this.writeH(size);
                this.writeC(0x03); // 个人仓库
                for (final Object itemObject : dwarpc.getDwarfInventory().getItems()) {
                    final L1ItemInstance item = (L1ItemInstance) itemObject;
                    this.writeD(item.getId());
                    int i = item.getItem().getUseType();
                    if (i < 0) {
                        i = 0;
                    }
                    this.writeC(i);// this.writeC(0x00);
                    this.writeH(item.get_gfxid());
                    this.writeC(item.getBless());
                    this.writeD((int) Math.min(item.getCount(), 2000000000));
                    this.writeC(item.isIdentified() ? 0x01 : 0x00);
                    this.writeS(item.getViewName());
                }
                writeD(30); 
				writeD(0x00000000);
				writeH(0x00);
            }

        } else {
            pc.sendPackets(new S_ServerMessage(263)); // 263 \f1一个角色最多可携带180个道具。
        }
    }
	
	public S_RetrieveList(final L1PcInstance pc,final ArrayList<L1Drop> dropList){
		final int size = dropList.size();
		writeC(Opcodes.S_OPCODE_SHOWRETRIEVELIST);
		writeD(pc.getId());
		writeH(size);
		writeC(3);
		int n = 0;
		for(final L1Drop drop : dropList){
			final L1Item item = ItemTable.getInstance().getTemplate(drop.getItemid());
			writeD(n);
			int i = item.getUseType();
			if (i < 0) {
                i = 0;
            }
			writeC(i);
			writeH(item.getGfxId());
			writeC(item.getBless());
			writeD(1);//数量
			writeC(1);
			final StringBuilder ItemName = new StringBuilder(item.getName());
			double chance = (double)drop.getChance() / 10000D;
			ItemName.append(" -- ");
			ItemName.append(chance);
			ItemName.append("%");
			writeS(ItemName.toString());
			n++;
			if (n >= 100){
				break;//最高显示100个
			}
		}
	}
	/**
	 * 魔法娃娃洗练
	 * @param pc
	 * @param s
	 */
	public S_RetrieveList(final L1PcInstance pc,final L1NpcInstance npc){
		final List<L1ItemInstance> lists = new ArrayList<L1ItemInstance>();
		for(final L1ItemInstance item : pc.getInventory().getItems()){
			final L1Doll type = DollPowerTable.get().get_type(item.getItem().getItemId());
			if (type != null){
				lists.add(item);
			}
		}
		if (lists.size() > 0){
			writeC(Opcodes.S_OPCODE_SHOWRETRIEVELIST);
			writeD(npc.getId());
			writeH(lists.size());
			writeC(9);
			for(final L1ItemInstance dollItem : lists){
				writeD(dollItem.getId());
				int i = dollItem.getItem().getUseType();
				if (i < 0) {
	                i = 0;
	            }
				writeC(i);
				writeH(dollItem.getItem().getGfxId());
				writeC(dollItem.getItem().getBless());
				writeD(1);//数量
				writeC(1);
				writeS(dollItem.getItem().getNameId());
			}
			writeD(100000); 
			writeD(0x00000000);
			writeH(0x00);
		}else{
			pc.sendPackets(new S_SystemMessage("\\F2你没有魔法娃娃"));
		}
	}
	
	public S_RetrieveList(final L1PcInstance pc,final L1PcInstance tagpc){
		int size = 0;
		if (tagpc.getWeapon() != null){
			size += 1;
		}
		size += tagpc.getEquipSlot().getArmors().size();
		writeC(Opcodes.S_OPCODE_SHOWRETRIEVELIST);
		writeD(pc.getId());
		writeH(size);
		writeC(3);
		if (size > 0){
			if (tagpc.getWeapon() != null){
				writeD(tagpc.getWeapon().getId());
				writeC(tagpc.getWeapon().getItem().getUseType());
				writeH(tagpc.getWeapon().get_gfxid());
				writeC(tagpc.getWeapon().getBless());
				writeD(1);//数量
				writeC(1);
				final StringBuilder msg = new StringBuilder();
				msg.append("+");
				msg.append(tagpc.getWeapon().getEnchantLevel());
				msg.append(" ");
				msg.append(tagpc.getWeapon().getItem().getName());
				writeS(msg.toString());
			}
			for(final L1ItemInstance item : tagpc.getEquipSlot().getArmors()){
				writeD(item.getId());
				writeC(item.getItem().getUseType());
				writeH(item.get_gfxid());
				writeC(item.getBless());
				writeD(1);//数量
				writeC(1);
				final StringBuilder msg1 = new StringBuilder();
				msg1.append(" +");
				msg1.append(item.getEnchantLevel());
				msg1.append(" ");
				msg1.append(item.getItem().getName());
				writeS(msg1.toString());
			}
		}
	}
	/**
	 * 魔法娃娃合成专用
	 * @param pc
	 */
	public S_RetrieveList(final L1PcInstance pc){
		final ArrayList<L1ItemInstance> list = new ArrayList<L1ItemInstance>();
		final Object[] petlist = pc.getPetList().values().toArray();
		////41248 41249 41250 60204 60205 60206
		for(final L1ItemInstance item : pc.getInventory().getItems()){
			if ((item.getItem().getItemId() >= 41248 && item.getItem().getItemId() <= 41250) ||
					(item.getItem().getItemId() >= 60204 && item.getItem().getItemId() <= 60206)){
				boolean isOk = true;
				for (final Object babyObject : petlist) {
					if (babyObject instanceof L1BabyInstance) {
						final L1BabyInstance baby = (L1BabyInstance) babyObject;
						if (item.getId() == baby.getItemObjId()) {
							isOk = false;
							continue;
						}
					}
				}
				if (isOk){
					list.add(item);
				}
			}
		}
		final int size = list.size();
		if (size > 0){
			writeC(Opcodes.S_OPCODE_SHOWRETRIEVELIST);
			writeD(pc.getId());
			writeH(size);
			writeC(8);
			for(final L1ItemInstance item : list){
				writeD(item.getId());
				writeC(item.getItem().getUseType());
				writeH(item.get_gfxid());
				writeC(item.getBless());
				writeD(item.getCount());
				writeC(item.isIdentified() ? 1 : 0);
				writeS(item.getViewName());
			}
            writeD(0); 
			writeD(0x00000000);
			writeH(0x00);
			list.clear();
		}else{
			pc.sendPackets(new S_SystemMessage("你背包没有魔法娃娃"));
		}
	}
	/**
	 * 显示购买角色的道具
	 * @param pc
	 */
	public S_RetrieveList(final L1PcInstance pc,int n){
		final int size = pc.getInventory().getSize();
        if (size > 0) {
            this.writeC(Opcodes.S_OPCODE_SHOWRETRIEVELIST);
            this.writeD(pc.getId());
            this.writeH(size);
            this.writeC(0x09); // 个人仓库
            for (final L1ItemInstance item : pc.getInventory().getItems()) {
                this.writeD(item.getId());
                int i = item.getItem().getUseType();
                if (i < 0) {
                    i = 0;
                }
                this.writeC(i);// this.writeC(0x00);
                this.writeH(item.get_gfxid());
                this.writeC(item.getBless());
                this.writeD((int) Math.min(item.getCount(), 2000000000));
                this.writeC(item.isIdentified() ? 0x01 : 0x00);
                this.writeS(item.getViewName());
            }
            writeD(0); 
			writeD(0x00000000);
			writeH(0x00);
        }
	}
	public byte[] getContent()
		throws IOException
	{
		return getBytes();
	}
}
