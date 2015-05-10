package zookeeper.conf;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;

public class ConfigManager {

	private String config ;

	private static final String connectString = "hadoopmaster:2181"	;
	private static final int sessionTimeout = 2000;
	private static final String configPath = "/config";

	private static ConfigManager configManager ;

	public static ConfigManager getInstance(){
		try {
			if(configManager == null){
				synchronized (ConfigManager.class) {
					if(configManager == null){
						configManager = new ConfigManager();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configManager ;
	}

	private ConfigManager(){
		try {
			final ZooKeeper zk = new ZooKeeper(connectString, sessionTimeout, null) ;
			byte[] data = zk.getData(configPath, new Watcher() {
				public void process(WatchedEvent event) {
					if(event.getType() == EventType.NodeDataChanged){
						try {
							byte[] newData = zk.getData(event.getPath(), this, null);
							config = new String(newData);  
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}, null);
			config = new String(data); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setConfig(String config){
		this.config = config ;
	}

	public String getConfig(){
		return this.config ;
	}

}
