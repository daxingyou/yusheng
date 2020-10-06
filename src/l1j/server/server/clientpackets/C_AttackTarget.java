package l1j.server.server.clientpackets;

//import l1j.server.server.ClientThread;
import l1j.server.server.mina.LineageClient;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.world.L1World;

public class C_AttackTarget extends ClientBasePacket
{
	private static final String stringType = "[C] C_AttackTarget";
	
	public C_AttackTarget(byte[] decrypt, LineageClient _client) throws Exception
	{
		super(decrypt);
		
		//宠物指定攻击  127.0.0.1 Request Work ID : 49
		// 0000: 72 a1 49 82 12 00 2c 63 44 12 00 00                r.I...,cD...
		
		int PetObj 		= readD();	// 宠物物件编号
		int type 		= readC();	// 攻击型态
		int TargetID	= readD();	// 目标物件编号
		
		L1PetInstance pet	= (L1PetInstance) L1World.getInstance().findObject(PetObj);
		L1Character Target	= (L1Character) L1World.getInstance().findObject(TargetID);
		
		pet.setMasterTarget(Target);
	}
	
	public String getType()
	{
		return stringType;
	}
}