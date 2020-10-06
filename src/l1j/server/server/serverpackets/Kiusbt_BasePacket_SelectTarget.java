package l1j.server.server.serverpackets; 
 
import java.io.IOException; 
import l1j.server.server.Opcodes;

/** 
*  
* 感谢Kiusbt大 努力的开发新的封包 @@ 
* 
* 希望大家一起来研究270 A___A 
*/ 
public class Kiusbt_BasePacket_SelectTarget extends ServerBasePacket { 
     
    public Kiusbt_BasePacket_SelectTarget(int ObjectID) {//宠物指定攻击 
    	writeC(Opcodes.S_OPCODE_SELECTTARGET);
    	writeD(ObjectID);
    	writeC(0x00);
    	writeC(0x00);
    	writeC(0x02);
    }
    
    @Override 
    public byte[] getContent() throws IOException { 
        return getBytes(); 
    } 
} 
