package l1j.server.server.serverpackets; 
 
import java.io.IOException;

import l1j.server.server.IdFactory;
import l1j.server.server.Opcodes;
import l1j.server.server.model.L1Character;
 
 
/** 
*  
* 感谢Kiusbt大 努力的开发新的封包 @@ 
* 
* 希望大家一起来研究270 A___A 
*/ 
public class Kiusbt_BasePacket_Skills extends ServerBasePacket { 
     
    public Kiusbt_BasePacket_Skills(L1Character _user, 
            L1Character _target, int gfxid, boolean isEffectOnUser) { 
         
        //int newSkillObject = 0x0198b41b; 
        int newheading = calcheading(_user.getX(), 
                _user.getY(),_target.getX(), _target.getY());         
         
        writeC(Opcodes.S_OPCODE_RANGESKILLS);                        //260 opcode 
        writeC(0x12); 
        writeD(0);            // 谁做施法动作    (使用者) 
         
        if(isEffectOnUser){ 
            writeH(_user.getX());        // X轴        (特效做用于...) 
            writeH(_user.getY());        // Y轴        (特效做用于...)     
        }else{ 
            writeH(_target.getX()); 
            writeH(_target.getY()); 
        } 
         
        writeC(newheading);                // 角色面向         
        //writeD(newSkillObject);        // 新增物件编号用 nextId() 不晓得会不会比较好 
        writeD(IdFactory.getInstance().nextId()); 
        writeH(gfxid);                    // 图档 
        writeD(0);                        // 伤害 
        writeH(0x0000);                    // Unknown 
    } 
     
    private static int calcheading(int myx, int myy, int tx, int ty) { 
        int newheading = 0; 
        if (tx > myx && ty > myy) { 
            newheading = 3; 
        } 
        if (tx < myx && ty < myy) { 
            newheading = 7; 
        } 
        if (tx > myx && ty == myy) { 
            newheading = 2; 
        } 
        if (tx < myx && ty == myy) { 
            newheading = 6; 
        } 
        if (tx == myx && ty < myy) { 
            newheading = 0; 
        } 
        if (tx == myx && ty > myy) { 
            newheading = 4; 
        } 
        if (tx < myx && ty > myy) { 
            newheading = 5; 
        } 
        if (tx > myx && ty < myy) { 
            newheading = 1; 
        } 
        return newheading; 
    } 
 
    @Override 
    public byte[] getContent() throws IOException { 
        return getBytes(); 
    } 
} 
