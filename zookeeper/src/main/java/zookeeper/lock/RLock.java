package zookeeper.lock;

import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class RLock {

	private static final String connectString = "hadoopmaster:2181"	;
	private static final int sessionTimeout = 2000;
	private static final String lockRootNode = "/z_locks";

	private String lockName ;
	private ZooKeeper zk ;
	private String znode ;
	private String lock ;

	private String lockId ;
	
	/**
	 * 是否已经初始化
	 */
	private boolean haveInit = false ;

	/**
	 * 是否正在加锁
	 */
	private boolean locking = false;

	/**
	 * 是否获得了锁
	 */
	private boolean locked = false ;

	public RLock(String lockId){
		if(lockId == null || "".equals(lockId.trim())){
			throw new RuntimeException("lockId不能为空"); 
		}
		this.lockName = lockId ;
	}
	
	private void init(String lockId){
		try {
			zk = new ZooKeeper(connectString, sessionTimeout, null);
			Stat existRootNode = zk.exists(lockRootNode, false);
			if(existRootNode == null){
				String result = zk.create(lockRootNode, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT); 
				if(result == null){
					throw new RuntimeException("创建rootNode " + znode + " 失败");
				}
			}
			znode = lockRootNode + "/" + lockId ;
			Stat znodeExist = zk.exists(znode, false);
			if(znodeExist == null){
				znode = zk.create(znode, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				if(znode == null){
					throw new RuntimeException("创建锁失败"); 
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void lock() throws Exception{
		if(!haveInit){
			init(lockName);
			haveInit = true ;
		}
		lock(false);
	}

	private void lock(boolean retry) throws Exception{
		if(!retry && locking){
			throw new RuntimeException("正在试图加锁，不可重复加锁");
		}
		locking = true ;
		if(lock == null){
			lock = zk.create(znode + "/lock_", null, Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
		}
		if(lock == null){
			throw new RuntimeException("加锁失败");
		}
		lockId = lock.substring(lock.lastIndexOf("/") + 1) ; 
		List<String> nodeLocks = zk.getChildren(znode, false);
		if(nodeLocks == null || nodeLocks.size() == 0){
			throw new RuntimeException("加锁失败，nodeLocks为空");
		}
		Collections.sort(nodeLocks);
		String currentLock = nodeLocks.get(0);
		if(lockId.equals(currentLock)){
			locked = true ;
			return ;
		}else{
			final Object lockObj = new Object();
			zk.getChildren(znode, new Watcher() {
				public void process(WatchedEvent event) {
					if(event.getType() == EventType.NodeChildrenChanged){
						synchronized (lockObj) {
							lockObj.notify();
						}
					}
				}
			}) ;
			synchronized (lockObj) {
				lockObj.wait();
			}
			lock(true);
		}
	}

	/**
	 * 解锁
	 * @throws Exception  
	 */ 
	public void unLock() throws Exception{
		if(!haveInit || !locked){
			System.err.println("释放锁失败，当前并没有获得锁！"); 
			return ;
		}
		locking = false ;
		locked = false ;
		haveInit = false ;
		lock = null ;
		lockId = null ;
		if(zk != null){
			zk.close();
			zk = null ;
		}
	}


}
