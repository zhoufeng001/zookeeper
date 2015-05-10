package zookeeper.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class Zk01 {
	
	static final String url = "hadoopmaster:2181";
	
	public static void main(String[] args) throws Exception {
		
		ZooKeeper zk = new ZooKeeper(url, 3000, new Watcher(){

			public void process(WatchedEvent event) {
				System.out.println(event);
			}
		});
		
		zk.create("/zf", "is_zhoufeng".getBytes(), Ids.OPEN_ACL_UNSAFE	, CreateMode.PERSISTENT);
		
		zk.create("/zf/zf01", "is_zhoufeng01".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		
		zk.close();
		
	}

}
