package l1j.server.server.serverpackets;

import l1j.server.server.Opcodes;

/**
 * 显示指定物品到装备窗口.
 * 
 * @version 1.0
 * @author jrwz
 * @data 2013-5-2下午11:43:34
 */
public class S_EquipmentWindow extends ServerBasePacket {
    private byte[] _byte = null;
    /** 头盔. */
    public static final byte EQUIPMENT_INDEX_HEML = 1;
    /** 盔甲. */
    public static final byte EQUIPMENT_INDEX_ARMOR = 2;
    /** T恤. */
    public static final byte EQUIPMENT_INDEX_T = 3;
    /** 斗篷. */
    public static final byte EQUIPMENT_INDEX_CLOAK = 4;
    /** 靴子. */
    public static final byte EQUIPMENT_INDEX_BOOTS = 5;
    /** 手套. */
    public static final byte EQUIPMENT_INDEX_GLOVE = 6;
    /** 盾. */
    public static final byte EQUIPMENT_INDEX_SHIELD = 7;
    /** 武器. */
    public static final byte EQUIPMENT_INDEX_WEAPON = 8;
    /** 项链. */
    public static final byte EQUIPMENT_INDEX_NECKLACE = 10;
    /** 腰带. */
    public static final byte EQUIPMENT_INDEX_BELT = 11;
    /** 耳环. */
    public static final byte EQUIPMENT_INDEX_EARRING = 12;
    /** 戒指1. */
    public static final byte EQUIPMENT_INDEX_RING1 = 18;
    /** 戒指2. */
    public static final byte EQUIPMENT_INDEX_RING2 = 19;
    /** 戒指3. */
    public static final byte EQUIPMENT_INDEX_RING3 = 20;
    /** 戒指4. */
    public static final byte EQUIPMENT_INDEX_RING4 = 21;
    /** 符纹. */
    public static final byte EQUIPMENT_INDEX_AMULET1 = 22;
    public static final byte EQUIPMENT_INDEX_AMULET2 = 23;
    public static final byte EQUIPMENT_INDEX_AMULET3 = 24;
    public static final byte EQUIPMENT_INDEX_AMULET4 = 25;
    public static final byte EQUIPMENT_INDEX_AMULET5 = 26;

    /**
     * 显示指定物品到装备窗口.
     * 
     * @param itemObjId
     *            - 对象ID
     * @param index
     *            - 序号
     * @param isEq
     *            - 0:脱下 1:使用
     */
    public S_EquipmentWindow(int itemObjId, int index, boolean isEq) {
        writeC(Opcodes.S_OPCODE_CHARRESET);
        writeC(0x42); // 66
        writeD(itemObjId);
        writeC(index);
        writeBoolean(isEq);
    }

	@Override
    public byte[] getContent() {
        if (this._byte == null) {
            this._byte = this.getBytes();
        }
        return this._byte;
    }

    @Override
    public String getType() {
        return this.getClass().getSimpleName();
    }
}
