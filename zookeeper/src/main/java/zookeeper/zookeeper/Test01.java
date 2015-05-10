package zookeeper.zookeeper;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Test01 {

	static final String url = "hadoopmaster:2181";

	ZooKeeper zk = null ;

	@Before
	public void init() throws IOException{
		zk = new ZooKeeper(url, 3000, new Watcher(){
			public void process(WatchedEvent event) {
				System.out.println("事件" + event.getType() + "触发");
				try {
					zk.exists(event.getPath(), this);
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@After
	public void destory() throws InterruptedException{ 
		if(zk != null){
			zk.close();
		}
	}

	@Test
	public void createRoot() throws KeeperException, InterruptedException{
		zk.create("/zf", "is_zhoufeng".getBytes(), Ids.OPEN_ACL_UNSAFE	, CreateMode.PERSISTENT);
	}

	@Test
	public void createChild() throws KeeperException, InterruptedException{
		zk.create("/zf/zf02", "is_zhoufeng02".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	@Test
	public void getChildren() throws KeeperException, InterruptedException{
		List<String> children = zk.getChildren("/zf", false);
		for (String string : children) {
			System.out.println(string); 
		}
	}

	@Test
	public void getData() throws KeeperException, InterruptedException{
		byte[] data = zk.getData("/zf/zf01", true, null); 
		System.out.println(new String(data));  
	}

	@Test
	public void setData() throws KeeperException, InterruptedException{
		zk.setData("/zf/zf02", "is_zhoufeng002".getBytes() , 0) ; 
	}

	@Test
	public void deleteData() throws InterruptedException, KeeperException{
		zk.delete("/zf/zf02", 1);   
	}
	
	@Test
	public void createSeqRoot() throws KeeperException, InterruptedException{
		String result = zk.create("/seq", "父节点".getBytes()	, Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
		System.out.println("result:" + result);
		System.out.println(new String(zk.getData("/seq", true, null)));
 		zk.create("/seq/test1", "1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		zk.create("/seq/test2", "2".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		zk.create("/seq/test3", "3".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		Thread.sleep(100000);
		System.out.println("ok");
	}  

	@Test 
	public void watch() throws KeeperException, InterruptedException{
		List<String> stat = zk.getChildren("/zf", new Watcher(){
			public void process(WatchedEvent event) {
				System.out.println("事件：" + event.getType() + " 被触发");
				try {
					zk.getChildren(event.getPath(), this);
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		Thread.sleep(100000);
	}
	
	@Test 
	public void watch2() throws KeeperException, InterruptedException{
		Stat stat = zk.exists("/zf", new Watcher(){
			public void process(WatchedEvent event) {
				try {
					System.out.println("/zf-event:" + event.getType() + ","
							+ new String(zk.getData(event.getPath(), true, null))); 
					zk.exists(event.getPath(), this);
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		System.out.println("stat:" + stat); 
		Thread.sleep(100000);
	}
	@Test 
	public void watch3() throws KeeperException, InterruptedException{
		Stat stat = zk.exists("/zf", new Watcher(){
			public void process(WatchedEvent event) {
				try {
					System.out.println("/zf-event:" + event.getType() + ","
							+ new String(zk.getData(event.getPath(), true, null))); 
					zk.exists(event.getPath(), this);
				} catch (KeeperException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		System.out.println("stat:" + stat); 
		Thread.sleep(100000);
	}
	
	
	
	@Test
	public void createSeqR() throws KeeperException, InterruptedException{
		zk.create("/seq", "seqtest".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
	
	@Test
	public void createSeq() throws KeeperException, InterruptedException{
		zk.create("/seq/s", "seqtest".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
	}

}
