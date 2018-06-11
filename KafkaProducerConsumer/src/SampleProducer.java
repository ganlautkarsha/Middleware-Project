import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Producer Example in Apache Kafka
 * @author www.tutorialkart.com
 */
public class SampleProducer extends Thread {
	private final KafkaProducer<Integer, String> producer;
	private final Boolean isAsync;
	private String message;
	public static final String KAFKA_SERVER_URL = "localhost";//"35.188.26.231";
	public static final int KAFKA_SERVER_PORT = 9092;
	
	public static final String CLIENT_ID = "SampleProducer";

	public SampleProducer(Boolean isAsync) {
		Properties properties = new Properties();
//		this.message=message;
		properties.put("bootstrap.servers", KAFKA_SERVER_URL + ":" + KAFKA_SERVER_PORT);
		properties.put("client.id", CLIENT_ID);
		properties.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producer = new KafkaProducer<>(properties);
		this.isAsync = isAsync;
	}

	public void run() {
		int messageNo = 1;

			ResultSet resultset_obj=null;
			DatabaseOperations dbop = null;
			try {
				dbop=new DatabaseOperations();
				resultset_obj=dbop.executeOperation();
					while (resultset_obj.next()) 
					{
					System.out.println("iterating..");
					String messageStr = resultset_obj.getString("item");
					long startTime = System.currentTimeMillis();
					if (isAsync) { // Send asynchronously
						producer.send(new ProducerRecord<>(resultset_obj.getString("item"),
								messageNo,
								messageStr), new DemoCallBack(startTime, messageNo, messageStr));
					} else { // Send synchronously
						try {
							producer.send(new ProducerRecord<>(resultset_obj.getString("item"),
									messageNo,
									messageStr)).get();
							System.out.println("Sent message: (" + messageNo + ", " + messageStr + ")");
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
							// handle the exception
						}
					}
					++messageNo;
					}
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			finally {
				dbop.close();
			}
	}
}

class DemoCallBack implements Callback {

	private final long startTime;
	private final int key;
	private final String message;

	public DemoCallBack(long startTime, int key, String message) {
		this.startTime = startTime;
		this.key = key;
		this.message = message;
	}

	/**
	 * onCompletion method will be called when the record sent to the Kafka Server has been acknowledged.
	 * 
	 * @param metadata  The metadata contains the partition and offset of the record. Null if an error occurred.
	 * @param exception The exception thrown during processing of this record. Null if no error occurred.
	 */
	public void onCompletion(RecordMetadata metadata, Exception exception) {
		long elapsedTime = System.currentTimeMillis() - startTime;
		if (metadata != null) {
			System.out.println(
					"message(" + key + ", " + message + ") sent to partition(" + metadata.partition() +
					"), " +
					"offset(" + metadata.offset() + ") in " + elapsedTime + " ms");
		} else {
			exception.printStackTrace();
		}
	}
}

