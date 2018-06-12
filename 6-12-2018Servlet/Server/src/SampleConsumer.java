import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.JSONObject;

import kafka.utils.ShutdownableThread;
/**
* Kafka Consumer with Example Java Application
*/
public class SampleConsumer extends ShutdownableThread{
    private final KafkaConsumer<String, JSONObject> consumer;
    private static HashMap<String,JSONObject> updatedItems;
	public static final String KAFKA_SERVER_URL = "35.188.26.231";
	public static final int KAFKA_SERVER_PORT = 9092;
	public static  String CLIENT_ID = "consum";
	private static ArrayList<String> subscribedItems=null;
    public SampleConsumer() {
        super("KafkaConsumer", false);
        
        BufferedWriter writer = null;
		try {
			///home/ganla_utkarsha/looog/
			writer = new BufferedWriter(new FileWriter("D:\\log\\log.txt"));
			writer.append("in constructor");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER_URL + ":" + KAFKA_SERVER_PORT);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CLIENT_ID);
//        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,"1000");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        
        consumer = new KafkaConsumer<>(props);
        //this.topicList.addAll(topicList);
        
    }

//    public JsonArray returnResponse() {
////    	consumer.subscribe(Collections.singletonList("apple"));
////        ConsumerRecords<String, JSONObject> records = consumer.poll(1000);
////        JsonArray responseArray=new JsonArray();
////        for (ConsumerRecord<String, JSONObject> record : records) {
////        	JSONObject obj=record.value();
////        	responseArray.add(obj.toString());
////            System.out.println("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset());
////        }
////        System.out.println("RESPONSE===>"+responseArray.toString());
////        return responseArray;
//    }

	@Override
	public void doWork() {
		// TODO Auto-generated method stub
		
			synchronized (this) 
			{
				
			
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter("D:\\log\\log.txt"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
		     
		    
			ArrayList<String> temp = ConsumerServlet.getList();
			if(temp!=null)
			{	
				if(subscribedItems==null) {
					subscribedItems = new ArrayList<String>();
					for(int i=0;i<temp.size();i++)
					{
						subscribedItems.add(temp.get(i));
					}	
					//subscribe to items
					consumer.subscribe(subscribedItems);
				}
				if(subscribedItems.size()<temp.size())
				{
					subscribedItems.clear();
					for(int i=0;i<temp.size();i++)
					{
						subscribedItems.add(temp.get(i));
					}
					//subscribe to items
					consumer.subscribe(subscribedItems);
				}
				System.out.println("subscribed to"+subscribedItems.toString());
				//poll here
				ConsumerRecords<String, JSONObject> records=consumer.poll(100);
				System.out.println("in do work!"+records.count());
				for (ConsumerRecord<String, JSONObject> record : records) {
//					System.out.println("-->"+record.topic() + record.value().toString());
//						writer.append("-->"+record.topic() + record.value().toString());
					
					ConsumerServlet.updateItems(record.topic(), record.value());		
				}
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}}

}