package l1j.william;


import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.sql.*;

import l1j.server.L1DatabaseFactory;
import l1j.server.Server;
import l1j.server.server.IdFactory;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.Instance.L1CastleGuardInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.world.L1World;

public class CastleGuard {

	private static ArrayList array = new ArrayList();
	private static boolean GET_ITEM = false;
	
	
	
	public static void main(String a[]) {
		while(true) {
			try {
			Server.main(null);
			} catch(Exception ex) {
			}
		}
	}
	
	private CastleGuard() {
	}


    public static int getkeeperId(int keeperId) {
		ArrayList data = null;
		int[] iTemp = null;
		if(!GET_ITEM) {
			GET_ITEM = true;
			getData();
		}
		int keeper_id = 0 ;
		for(int i=0; i <array.size(); i++) {
			data = (ArrayList) array.get(i);			
			
			if(((Integer) data.get(0)).intValue() == keeperId) {
				return keeper_id = ((Integer) data.get(0)).intValue();				
			}
		}
		return keeper_id;
	}
	
	public static void spawn(int keeperId) {
		ArrayList data = null;
		int[] iTemp = null;
		if(!GET_ITEM) {
			GET_ITEM = true;
			getData();
		}
		for(int i=0; i <array.size(); i++) {
			data = (ArrayList) array.get(i);
			if(((Integer) data.get(0)).intValue() == keeperId) {
				try {
					L1Npc spawnmonster = NpcTable.getInstance().getTemplate(((Integer)data.get(1)).intValue());
					if (spawnmonster != null) {
						L1NpcInstance mob = null;
						try {
							String implementationName = spawnmonster.getImpl();
							Constructor _constructor = Class.forName((new StringBuilder()).append("l1j.server.server.model.Instance.").append(implementationName).append("Instance").toString()).getConstructors()[0];
							mob = (L1NpcInstance) _constructor.newInstance(new Object[] { spawnmonster });
							mob.setId(IdFactory.getInstance().nextId());
							mob.setX(((Integer)data.get(2)).intValue());
							mob.setY(((Integer)data.get(3)).intValue());
							mob.setHomeX(((Integer)data.get(2)).intValue());
							mob.setHomeY(((Integer)data.get(3)).intValue());
							mob.setMap(((Integer)data.get(4)).shortValue());
							mob.setHeading(((Integer)data.get(5)).intValue());
							mob.setMovementDistance(30);
							if (mob.getGfxId() == 5853) // 城主双手剑状态
							{
								mob.setStatus(50);
							}
							L1World.getInstance().storeWorldObject(mob);
							L1World.getInstance().addVisibleObject(mob);
							L1Object object = L1World.getInstance().findObject(mob.getId());
							L1CastleGuardInstance newnpc = (L1CastleGuardInstance) object;
							newnpc.onNpcAI();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void getData() {
        java.sql.Connection con = null;
        try {
          con = L1DatabaseFactory.getInstance().getConnection(); 
	      Statement stat = con.createStatement();
	      ResultSet rset = stat.executeQuery("SELECT * FROM william_spawn_guard");
	      ArrayList arraylist = null;
	      String sTemp = null;
          if ( rset!=null)
            while (rset.next()) {
            	arraylist = new ArrayList();
            	arraylist.add(0, new Integer(rset.getInt("keeper")));
            	arraylist.add(1, new Integer(rset.getInt("npcid")));
            	arraylist.add(2, new Integer(rset.getInt("locx")));
            	arraylist.add(3, new Integer(rset.getInt("locy")));
            	arraylist.add(4, new Integer(rset.getInt("mapid")));
            	arraylist.add(5, new Integer(rset.getInt("heading")));
            	array.add(arraylist);
            }
          if (con!=null && !con.isClosed())
        	  con.close();
        }
        catch (Exception ex) {	
        }
	}
}



