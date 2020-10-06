package l1j.server.server.datatables;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import l1j.server.server.templates.L1Znoe;
import l1j.server.server.utils.StreamUtil;

public class LoadZnoe {
	private static LoadZnoe _instance;
	private static final HashMap<Integer,ArrayList<L1Znoe>> mapList = new HashMap<Integer,ArrayList<L1Znoe>>();
	
	private LoadZnoe(){
		
	}
	public static LoadZnoe getInstance(){
		if (_instance == null){
			_instance = new LoadZnoe();
		}
		return _instance;
	}
	
	public void load(){
		InputStreamReader is = null;
		BufferedReader br = null;
		try {
		    File file = new File("./znoe/zone.tbl");
		    if(file.isFile() && file.exists()) {
		    	is = new InputStreamReader(new FileInputStream(file), "gbk");
		    	br = new BufferedReader(is);
		    	String lineTxt = null;
		    	while ((lineTxt = br.readLine()) != null) {
		    		String regex = "(.*?)(\\s+)(\\d+)(\\s+)(\\d+)(\\s+)(\\d+)(\\s+)(\\d+)(\\s+)(\\d+)(\\s+)(\\d+)";
	    			Pattern pattern = Pattern.compile(regex);
	    			Matcher m = pattern.matcher(lineTxt);
	    		    if (m.find()){
	    		    	String mapName = m.group(1);
                        int mapId = Integer.parseInt(m.group(5));
                        int startX = Integer.parseInt(m.group(7));
                        int startY = Integer.parseInt(m.group(9));
                        int endX = Integer.parseInt(m.group(11));
                        int endY = Integer.parseInt(m.group(13));
                        ArrayList<L1Znoe> list = mapList.get(mapId);
                        if (list == null){
                        	list = new ArrayList<L1Znoe>();
                        	mapList.put(mapId, list);
                        }
                        list.add(new L1Znoe(mapId, mapName, startX, startY, endX, endY));
	    		    }
		    	}
		    } else {
		    	System.out.println("文件不存在!");
		    }
		} catch (Exception e) {
		    System.out.println("文件读取错误!");
		}finally {
			StreamUtil.close(br);
			StreamUtil.close(is);
		}
	}
	public String findMapName(final int mapId,final int x,final int y){
		ArrayList<L1Znoe> list = mapList.get(mapId);
        if (list != null){
        	if (list.size() > 1){
        		for(L1Znoe znoe : list){
            		if (x >= znoe.getStartX() && x <= znoe.getEndX() && y >= znoe.getStartY() && y <= znoe.getEndY()){
            			return znoe.getMapName();
            		}
            	}
        		return list.get(0).getMapName();//如果没找到取第一个作为名称
        	}else{
        		return list.get(0).getMapName();
        	}
        }else{
        	return "未知";
        }
	}
}
