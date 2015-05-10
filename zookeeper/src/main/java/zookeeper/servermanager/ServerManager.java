package zookeeper.servermanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;

public class ServerManager {

	private static final String connectString = "hadoopmaster:2181"	;
	private static final int sessionTimeout = 2000;
	private static final String serverPath = "/servers";

	private List<String> serverList = new ArrayList<String>(); 
	private Object serverListLock = new Object();
	
	private static ServerManager instance ;
	
	public static ServerManager getInstance(){
		if(instance == null){ 
			synchronized (ServerManager.class) {
				if(instance == null){
					instance = new ServerManager();
				}
			}
		}
		return instance ;
	}
	
	private ServerManager(){
		try {
			final ZooKeeper zk = new ZooKeeper(connectString, sessionTimeout, null);
			List<String> tmpServerList = zk.getChildren(serverPath, new Watcher() {
				public void process(WatchedEvent event) {
					if(event.getType() == EventType.NodeCreated){
						System.out.println("创建新节点了：" + event);
					}else if(event.getType() == EventType.NodeDeleted){
						System.out.println("节点被删除了：" + event);
					}
					try {
						List<String> tmpServerList = zk.getChildren(serverPath, this); 
						synchronized (serverListLock) {
							serverList = tmpServerList; 
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			synchronized (serverListLock) {
				serverList = tmpServerList; 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getServers(){
		List<String> returnServers = null ;
		synchronized (serverListLock) {
			returnServers = new ArrayList<String>(serverList.size());
			returnServers.addAll(serverList); 
 		}
		return returnServers;
	}

}
