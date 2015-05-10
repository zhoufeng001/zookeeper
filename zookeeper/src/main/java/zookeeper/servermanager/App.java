package zookeeper.servermanager;


public class App {
	
	public static void main(String[] args) throws Exception{
		
		ServerManager sm = ServerManager.getInstance();
		
		while(true){
			System.out.println(sm.getServers());
			Thread.sleep(1000); 
		}
		
	}

}
