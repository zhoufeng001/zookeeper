package zookeeper.lock;

public class App {

	public static void main(String[] args) throws Exception {

		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {
				public void run() {
					RLock lock = new RLock("lock01");
					while(true){
						try{
							try {
								lock.lock();
								System.out.println(Thread.currentThread().getName());
								Thread.sleep(3000);
							} catch (Exception e) {
								e.printStackTrace();
							} 
						}finally{
							try {
								lock.unLock();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
				}
			}).start();
		}
		

	}

}
