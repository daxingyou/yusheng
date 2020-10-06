package l1j.server.server.serverpackets;

import java.io.IOException;

import l1j.server.server.Opcodes;
import l1j.server.server.model.Instance.L1TrapInstance;

public class S_Trap extends ServerBasePacket {
	public S_Trap(L1TrapInstance trap, String name) {
		this.writeC(Opcodes.S_OPCODE_CHARPACK);
        this.writeH(trap.getX()); // X
        this.writeH(trap.getY()); // Y
        this.writeD(trap.getId()); // OBJID
        this.writeH(0x0007); // GFXID
        this.writeC(0x00); // 物件外观属性
        this.writeC(0x00); // 方向
        this.writeC(0x00); // 亮度 0:normal, 1:fast, 2:slow
        this.writeC(0x00); // 速度
        this.writeD(0x00000000); // 数量, 经验值
        this.writeH(0x0000); // 正义质
        this.writeS(name); // 名称
        this.writeS(null); // 封号
        this.writeC(0x00); // 状态
        this.writeD(0x00000000); // 血盟OBJID
        this.writeS(null); // 血盟名称
        this.writeS(null); // 主人名称
        this.writeC(0x00); // 物件分类
        this.writeC(0xFF); // HP显示
        this.writeC(0x00); // タルクック距离(通り)
        this.writeC(0x00); // LV
        this.writeC(0x00);
        this.writeC(0xFF);
        this.writeC(0xFF);
	}

	@Override
	public byte[] getContent() throws IOException {
		return getBytes();
	}
}
