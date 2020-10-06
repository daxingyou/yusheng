
package l1j.william;

import static l1j.server.server.model.skill.L1SkillId.FIRE_WALL;
import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.poison.L1Poison5;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.world.L1World;
import l1j.server.server.model.L1Object;


public class FireDamage{
	
	private L1Character user = null;
	private L1EffectInstance fire = null;
	
	public FireDamage(L1Character cha,L1NpcInstance firewall){		
		user = (L1Character) cha;
		fire = (L1EffectInstance) firewall;
	}

	class Damage implements Runnable{
		
		private Damage(){
		}	
	
		@Override
		public void run(){		
			for (int findObjecCounts = 0; findObjecCounts < 8; findObjecCounts ++) {
				try{
					for (L1Object objects : L1World.getInstance().
								getVisibleObjects(user, 15)) {//玩家视线范围15格
						if (objects instanceof L1PcInstance) { //对玩家

							L1PcInstance pc = (L1PcInstance) objects;
							if (pc.getLocation().equals(fire.getLocation())) {

								if (pc.isDead() 
									|| pc.getZoneType() == 1 
									|| DamageRegeneration(pc) 
									|| pc.hasSkillEffect(50) 
									|| pc.hasSkillEffect(78) 
									|| pc.hasSkillEffect(80) 
									|| pc.hasSkillEffect(157) 
									|| pc.get_poisonStatus4() == 4 
									|| pc.get_poisonStatus4() == 6)
									continue;

								// 计算伤害 (可由DB设定)
								L1Magic _magic = new L1Magic(user, pc);
								int damage = _magic.calcMagicDamage(FIRE_WALL);

								if (damage == 0)
									continue;

								pc.sendPackets(new S_DoActionGFX(pc.getId(),
										ActionCodes.ACTION_Damage));
								pc.broadcastPacket(new S_DoActionGFX(pc.getId(),
										ActionCodes.ACTION_Damage));
								pc.receiveDamage(user, damage,false);
								pc.removeSkillEffect(66);
							}	

						} else if (objects instanceof L1MonsterInstance) {//对怪

							L1MonsterInstance npc = (L1MonsterInstance) objects;
							if (npc.getLocation().equals(fire.getLocation())) {

								if (npc.isDead() 
									|| npc.getHiddenStatus() != 0 
									|| DamageRegeneration(npc) 
									|| npc.hasSkillEffect(50) 
									|| npc.hasSkillEffect(78) 
									|| npc.hasSkillEffect(80) 
									|| npc.hasSkillEffect(157) 
									|| npc.get_poisonStatus4() == 4 // 冰矛
									|| npc.get_poisonStatus6() == 6) // 地屏
									continue;

								// 计算伤害
								L1Magic _magic = new L1Magic(user, npc);
								int damage = _magic.calcMagicDamage(FIRE_WALL);								
								if (damage == 0 )
									continue;

								npc.broadcastPacket(new S_DoActionGFX(npc.getId(),
										ActionCodes.ACTION_Damage));

								npc.receiveDamage(user, damage);
								npc.removeSkillEffect(66);
							}
						}
					}					
				Thread.sleep(12 * 100);//即时伤害 by a8889888				
				}catch(Exception ex){}				
			}
		}
	}

		public void onDamageAction(){
			
			Damage damage_run = new Damage();
			GeneralThreadPool.getInstance().execute(damage_run);			
	
		}
		
		private boolean DamageRegeneration(L1Character cha){
			
			if(cha.hasSkillEffect(99999))
				return true;
			
			cha.setSkillEffect(99999, 1000);
			return false;					
		}
}