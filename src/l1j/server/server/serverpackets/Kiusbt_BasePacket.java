package l1j.server.server.serverpackets; 
 
import java.io.IOException;

import l1j.server.server.model.*;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.Opcodes;

/** 
*  
* 感谢Kiusbt大 努力的开发新的封包 @@ 
* 
* 希望大家一起来研究270 A___A 
*/ 
public class Kiusbt_BasePacket extends ServerBasePacket { 

	public ServerBasePacket Icon(int i, int j, L1Character cha) {
        return this;
    }

	public ServerBasePacket TeleportLock() { // 传送前锁定玩家
		writeC(Opcodes.S_OPCODE_TELEPORTLOCK);
		writeC(0);
		return this;
	}

    public ServerBasePacket Ingredients() { // 料理书1阶段
        writeC(Opcodes.S_OPCODE_PACKETBOX);
        writeC(52);
        writeD(0x7e29c0);
        writeC(1);
        writeC(0);
        return this;
    }

    public ServerBasePacket FoodIcon(int i, int j, L1PcInstance pc) { // 食用料理状态图
        writeC(Opcodes.S_OPCODE_PACKETBOX);
        writeC(53);
        writeD(0);
        writeH(0);
        writeC(0);
        writeC(0);
        writeH(i);
        writeH(j);
        writeC(pc.getInventory().getWeight240());
        return this;
    }
    
    public byte[] getContent()
		throws IOException
	{
		return getBytes();
	}
} 
