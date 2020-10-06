
package l1j.william;

import l1j.server.server.ActionCodes;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Magic;
import l1j.server.server.model.Instance.L1EffectInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.poison.L1DamagePoison;
import l1j.server.server.model.poison.L1Poison5;
import l1j.server.server.serverpackets.S_DoActionGFX;
import l1j.server.server.world.L1World;
import l1j.server.server.model.L1Object;


public class NpcDamagePoison {
	
	private L1Character user = null;
	private L1EffectInstance fire = null;
	
	public NpcDamagePoison(L1Character cha,L1NpcInstance firewall){		
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
								getVisibleObjects(user, 15)) { // 玩家视线范围15格
						if (objects instanceof L1PcInstance) { // 对玩家

							L1PcInstance pc = (L1PcInstance) objects;
							if (pc.getLocation().equals(fire.getLocation())) {

								if (pc.isDead() 
									|| pc.hasSkillEffect(50) 
									|| pc.hasSkillEffect(78) 
									|| pc.hasSkillEffect(80) 
									|| pc.hasSkillEffect(157) 
									|| pc.get_poisonStatus4() == 4 
									|| pc.get_poisonStatus4() == 6)
									continue;

								L1DamagePoison.doInfection(user, pc, 1000,15000, 10);
							}	

						} else if (objects instanceof L1NpcInstance) { // 对 Npc

							if ((objects instanceof L1PetInstance) || (objects instanceof L1SummonInstance)) { // 对宠物或召唤怪
								L1NpcInstance npc = (L1NpcInstance) objects;
								if (npc.getLocation().equals(fire.getLocation())) {

									if (npc.isDead() 
										|| npc.getHiddenStatus() != 0 
										|| npc.hasSkillEffect(50) 
										|| npc.hasSkillEffect(78) 
										|| npc.hasSkillEffect(80) 
										|| npc.hasSkillEffect(157) 
										|| npc.get_poisonStatus4() == 4 // 冰矛
										|| npc.get_poisonStatus6() == 6) // 地屏
										continue;

								L1DamagePoison.doInfection(user, npc, 1000,15000, 10);
								}
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
}