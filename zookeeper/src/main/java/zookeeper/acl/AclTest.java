package zookeeper.acl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AclTest {
	

	static final String url = "localhost:2181";

	ZooKeeper zk = null ;

	@Before
	public void init() throws IOException{
		zk = new ZooKeeper(url, 3000, null);
	}

	@After
	public void destory() throws InterruptedException{ 
		if(zk != null){
			zk.close();
		}
	}
	
	@Test
	public void createZNode() throws Exception{
		List<ACL> acls = new ArrayList<ACL>() ;
		Id id1 = new Id("digest", DigestAuthenticationProvider.generateDigest("admin:admin123")) ;
		ACL acl1 = new ACL(ZooDefs.Perms.ALL, id1); 
		
		Id id2 = new Id("digest", DigestAuthenticationProvider.generateDigest("guest:guest123")) ;
		ACL acl2 = new ACL(ZooDefs.Perms.READ, id2); 
		
		acls.add(acl1);
		acls.add(acl2);
		
		String result = zk.create("/acl_test", null, acls, CreateMode.PERSISTENT) ;
		
		System.out.println("result:" + result);
	}

	@Test
	public void readZNodeChildren() throws Exception{
		zk.addAuthInfo("digest", "guest:guest123".getBytes()); 
		List<String> children = zk.getChildren("/acl_test",null);
		System.out.println(children); 
	}
	
	@Test
	public void addZNodeChild() throws Exception{
		zk.addAuthInfo("digest", "admin:admin123".getBytes()); 
		String result = zk.create("/acl_test/child01", null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println("result:" + result);  
	}
	
	@Test
	public void addZNodeChild02() throws Exception{
		List<ACL> acls = new ArrayList<ACL>() ;
		Id id = new Id("digest", DigestAuthenticationProvider.generateDigest("user1:user1")) ;
		ACL acl = new ACL(ZooDefs.Perms.ALL, id); 
		acls.add(acl);
		
		zk.addAuthInfo("digest", "admin:admin123".getBytes()); 
		String result = zk.create("/acl_test/child02", null, acls, CreateMode.PERSISTENT);
		System.out.println("result:" + result);  
	}
	
	@Test
	public void readZNodeChild02() throws Exception{
		zk.addAuthInfo("digest", "user1:user1".getBytes()); 
		List<String> children = zk.getChildren("/acl_test/child02",null);
		System.out.println(children); 
	}
	
}


