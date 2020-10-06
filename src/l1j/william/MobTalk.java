/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */

package l1j.william;

import java.util.ArrayList;
import java.util.Random;
import java.sql.*;

import l1j.server.Server;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.serverpackets.S_NpcChatPacket;

public class MobTalk {
	private static ArrayList array = new ArrayList();
	private static ArrayList aData6 = new ArrayList();
	private static boolean NO_MORE_GET_DATA6 = false;


	public static void main(String a[]) {
		while(true) {
			try {
			Server.main(null);
			} catch(Exception ex) {
			}
		}
	}

	private MobTalk() {
	}

	public  static void forL1MonsterTalking(L1NpcInstance monster, int type) {
		ArrayList aTempData = null;
		int[] iTemp = null;
		if(!NO_MORE_GET_DATA6){
			NO_MORE_GET_DATA6 = true;
			getData6();
		}
		
		int order = 0;
		for(int i=0; i < aData6.size(); i++) {
			aTempData = (ArrayList) aData6.get(i);

			if(((Integer) aTempData.get(0)).intValue() == monster.getNpcTemplate().get_npcId() 
				&& ((Integer) aTempData.get(1)).intValue() == type) { // Mob_id && Type

				if (monster.hasSkillEffect(l1j.william.New_Id.Skill_AJ_1_19)) {
					return;
				}

				Random random = new Random();
				int chance = random.nextInt(100) + 1;
				
				if (((Integer) aTempData.get(2)).intValue() != 0) {
					if (((Integer) aTempData.get(2)).intValue() >= chance) { // 机率判断
						if (((Integer)aTempData.get(3)).intValue() != 0) { // 大喊
							monster.broadcastPacket(new S_NpcChatPacket(monster, (String)aTempData.get(4), 2)); 
						} else {
							monster.broadcastPacket(new S_NpcChatPacket(monster, (String)aTempData.get(4), 0)); 
						}

						if (((Integer)aTempData.get(5)).intValue() != 0) { // 说话延迟
							monster.setSkillEffect(l1j.william.New_Id.Skill_AJ_1_19, ((Integer)aTempData.get(5)).intValue() * 1000);
						}
					}
				}
				return;
			}
		}
    }

	private static void getData6(){
        java.sql.Connection con = null;
        try { 
          con = L1DatabaseFactory.getInstance().getConnection(); 
	      Statement stat = con.createStatement();
	      ResultSet rset = stat.executeQuery("SELECT * FROM william_mob_talk");
	      ArrayList aReturn = null;
	      String sTemp = null;
          if( rset!= null)
            while (rset.next()){
	    	  aReturn = new ArrayList();
	    	  aReturn.add(0, new Integer(rset.getInt("mob_id")));
	    	  aReturn.add(1, new Integer(rset.getInt("type")));
	    	  aReturn.add(2, new Integer(rset.getInt("random")));
	    	  aReturn.add(3, new Integer(rset.getInt("shout")));
	    	  aReturn.add(4, rset.getString("talk"));
	    	  aReturn.add(5, new Integer(rset.getInt("delay")));
	    	  aData6.add(aReturn);
            }
          if(con!=null && !con.isClosed())
        	  con.close();
        }
        catch(Exception ex){}
	}
}
