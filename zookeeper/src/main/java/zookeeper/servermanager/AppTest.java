package zookeeper.servermanager;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AppTest {

	static final String url = "hadoopmaster:2181";

	ZooKeeper zk = null ;

	@Before
	public void init() throws IOException{
		zk = new ZooKeeper(url, 3000,null);
	}

	@After
	public void destory() throws InterruptedException{ 
		if(zk != null){
			zk.close();
		}
	}

	@Test
	public void createServerRoot() throws KeeperException, InterruptedException{
		zk.create("/servers", "servers".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT) ;
	}
	
	
	@Test
	public void createServer1() throws KeeperException, InterruptedException{
		zk.create("/servers/servers01", "server01".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL) ;
		Thread.sleep(20000); 
	}
	
	@Test
	public void createServer2() throws KeeperException, InterruptedException{
		zk.create("/servers/servers02", "server02".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL) ;
		Thread.sleep(20000); 
	}
	
	@Test
	public void createServer3() throws KeeperException, InterruptedException{
		zk.create("/servers/servers03", "server03".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL) ;
		Thread.sleep(20000); 
	}
	
	
	
}
