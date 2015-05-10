package zookeeper.conf;

public class App1 {

	public static void main(String[] args) throws InterruptedException {

		ConfigManager cfgMgr = ConfigManager.getInstance();
		while(true){
			String config = cfgMgr.getConfig();
			System.out.println(config); 
			Thread.sleep(1000); 
		} 

	}

}
