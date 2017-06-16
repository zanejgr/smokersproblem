import java.util.concurrent.*;

public class SmokersProblem{
	static int count;
	static Semaphore TS = new Semaphore(1);
	static Semaphore AS = new Semaphore(0);
	static Table t;
	
	public static void main(String []args){
		t = new Table();
		Agent a = new Agent();
		a.start();
		
		Smoker [] c = new Smoker[3];
		
		for(int i = 0; i < c.length;i++){
			c[i]=new Smoker(i);
			c[i].start();
		}
	}
	
	private static class Agent extends Thread{
		public static String [] inventory = {"Matches","Paper","Tobacco"};
		public Agent(){
			super();
		}
		public void run(){
			for(;;){
				System.out.println("The agent is awake");
				try{
					sleep((int)(Math.random()*1000));
				}catch(InterruptedException e){}
				try{TS.acquire();}
					catch(InterruptedException e){}
				System.out.println("The agent is running");
				try{
					sleep((int)(Math.random()*1000));
				}catch(InterruptedException e){}
				int item = (int)(Math.random()*3);
				Table.stock(item);
				System.out.println("Agent stocked table with "+inventory[item]);
				
				TS.release();
				System.out.println("Agent is going to sleep");
				try{
					AS.acquire();
				}catch(InterruptedException e){}
			}
		}
	}
	
	private static class Smoker extends Thread{
		int id,item;
		public Smoker(int i){
			super();
			this.id=i;
			item = id%3;
		}
		public void run(){
			for(;;){
				try{
					sleep((int)(Math.random()*1000));
				}catch(InterruptedException e){}
				System.out.println("Smoker " + id + ": queued up for table");
				try{TS.acquire();}
				catch(InterruptedException e){}
				System.out.println("Smoker " + id + ": is at the table");
				try{
					sleep((int)(Math.random()*1000));
				}catch(InterruptedException e){}
				if(Table.use(item)){
					System.out.println("Smoker "+id+" found " +Agent.inventory[item]);
					TS.release();
					System.out.println("Smoker "+id+" is smoking " );
				}
				else{
					TS.release();
					System.out.println("Smoker "+id+" didn't find " +Agent.inventory[item]);
				}
				AS.release();
			}
		}
	}
	
	private static class Table{
		private static int [] resources;
		public Table(){
			resources = new int [3];
			for(int i = 0 ; i < resources.length ; i++){
				resources[i] = 0;
				System.out.println("Initializing table "+Agent.inventory[i]+" count to 0");
			}
			
		}
		public static void stock(int item){
			resources[item]++;
		}
		public static boolean use(int item){
			if(resources[item]>0){
				resources[item]--;
				return true;
			}else{
				return false;
			}
		}
	}
}
