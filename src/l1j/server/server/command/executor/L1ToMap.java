package l1j.server.server.command.executor;

import l1j.server.server.model.L1Location;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.map.L1Map;
import l1j.server.server.model.map.L1WorldMap;
import l1j.server.server.serverpackets.S_SystemMessage;

public class L1ToMap implements L1CommandExecutor {

	private L1ToMap() {
		
	}

	public static L1CommandExecutor getInstance() {
		return new L1ToMap();
	}

	// @Override
	@Override
	public void execute(L1PcInstance pc, String cmdName, String arg) {
		try {
			final Integer mapId = Integer.parseInt(arg);

            final L1Map mapData = L1WorldMap.getInstance().getMap(mapId.shortValue());
            if (mapData == null) {
                return;
            }
            final int x = mapData.getX();
            final int y = mapData.getY();
            final int height = mapData.getHeight();
            final int width = mapData.getWidth();

            final int newx = x + (height / 2);
            final int newy = y + (width / 2);

            final L1Location loc = new L1Location(newx, newy, mapId.intValue());

            final L1Location newLocation = loc.randomLocation(200, true);
            final int newX = newLocation.getX();
            final int newY = newLocation.getY();

            L1Teleport.teleport(pc, newX, newY, mapId.shortValue(), 5, true);
		}catch(Exception e){
			pc.sendPackets(new S_SystemMessage(cmdName + " 地图编号  请输入。"));
		}
	}
}
