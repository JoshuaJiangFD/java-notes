package joshua.java.messaging.demo.p2p;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.jms.ConnectionMetaData;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MetaData {

    public static void main(String[] args) {
    	try {
			// Connect to the provider and get the JMS connection
			Hashtable<String,String> properties=new Hashtable<String,String>();
			properties.put("java.naming.factory.initial","org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			properties.put("java.naming.provider.url","tcp://localhost:61616");
			properties.put("connectionFactoryNames","QueueCF");
			properties.put("topic.topic1","jms.queue1");
			Context ctx = new InitialContext(properties);

			QueueConnectionFactory qFactory = (QueueConnectionFactory)ctx.lookup("QueueCF");
			QueueConnection qConnect = qFactory.createQueueConnection();
			ConnectionMetaData metadata = qConnect.getMetaData();
			System.out.println("JMS Version:  " + metadata.getJMSMajorVersion() + "." + metadata.getJMSMinorVersion());
			System.out.println("JMS Provider: " + metadata.getJMSProviderName());
			System.out.println("JMS Provider Version: " + metadata.getProviderMajorVersion());
			System.out.println("JMSX Properties Supported: ");
			Enumeration e = metadata.getJMSXPropertyNames();
			while (e.hasMoreElements()) {
				System.out.println("   " + e.nextElement());
			}
			
		} catch (JMSException jmse) {
			jmse.printStackTrace( ); 
			System.exit(1);
		} catch (NamingException jne) {
		    jne.printStackTrace( ); 
		    System.exit(1);
		}
    }
	
}
