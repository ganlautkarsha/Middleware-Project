package democlient;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ServerProducer {
	
	 public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
	        Timer timerObject = new Timer();
	        timerObject.schedule(new OperationScheduler(timerObject), 0, 10000
	        		);
	    }
}



class OperationScheduler extends TimerTask 
{
    Timer timer = new Timer();

    public OperationScheduler(Timer timerObject) {
        this.timer = timerObject;
    }

    public void run() 
    {
    	
		boolean isAsync = false;
		DatabaseOperations dbob;
//		dbob = new DatabaseOperations();
//		
//		ArrayList<String> topics=dbob.getTopics();
//		String TOPIC = "test";
//		
//		for(String topic: topics)
//		{
			
			SampleProducer producerThread = new SampleProducer(isAsync);
	        // start the producer
	        producerThread.start();
//		}
        
        
		
    }
}