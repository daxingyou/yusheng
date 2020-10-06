package l1j.server.server.clientpackets;

import l1j.server.server.PacketHandler;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;

public class C_CharcterTwoPassword extends ClientBasePacket {
	
	public C_CharcterTwoPassword(byte abyte0[], LineageClient _client) {
		super(abyte0);
		L1PcInstance pc = _client.getActiveChar();
		if (pc == null) {
			return;
		}

		System.out.println(PacketHandler.printData(abyte0, abyte0.length));
		
		final int type = readC();
		
		final int gamepassword = _client.getAccount().getTwoPassword();
		if(type == 0){			
			int old_twopassword = readCH();		
			readC();	// dummy
			int new_twopassword = readCH();
			if(gamepassword == -256 || gamepassword == old_twopassword){
				_client.getAccount().updateTwoPassword(new_twopassword);
				pc.sendPackets(new S_SystemMessage("\\F3**二级密码修改成功**"));
			}else{
				pc.sendPackets(new S_SystemMessage("\\F3**二级密码错误**"));
			}			
		}else if(type == 1){
			//int chkpass = readCH();							
			//readC();	// dummy
			//int objId = readD();					
		}
	}
}
