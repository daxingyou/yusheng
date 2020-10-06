package l1j.server;

public class ces {

	public static void main(String[] args) {
		int i = 42;
		int n = 0;
		for(;i<94;i++){
			//System.out.println(String.format("<p align=left><a action=\"ClanCreateItem_%d\"><var src=\"#%d\"></a>&nbsp;&nbsp;<font fg=ffff00><var src=\"#%d\"></font></p>", i,n,n+1));
			System.out.println("<p align=left><var src=\"#" + i +"\"></p>");
			//n += 2;
		}
	}

}
