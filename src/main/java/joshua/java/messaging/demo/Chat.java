package joshua.java.messaging.demo;

import java.io.*;
import java.util.Hashtable;
import javax.jms.*;
import javax.naming.*;


/**
 * demo example 使用 JMS pub/sub API.
 *
 * from Java Message Service 2nd edition chap2.
 */
public class Chat implements MessageListener{

    private TopicSession pubSession;

    private TopicPublisher publisher;

    private TopicConnection connection;

    private String username;

    /* Constructor used to Initialize Chat */
    public Chat(String topicFactory, String topicName, String username)
            throws Exception {

        Hashtable<String,String> properties=new Hashtable<String,String>();
        properties.put("java.naming.factory.initial","org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        properties.put("java.naming.provider.url","tcp://localhost:61616");
        properties.put("connectionFactoryNames","TopicCF");
        properties.put("topic.topic1","jms.topic1");

        // Obtain a JNDI connection using properteis from Hashtable
        InitialContext ctx = new InitialContext(properties);

        // Look up a JMS connection factory by JNDI naming service
        TopicConnectionFactory conFactory =
                (TopicConnectionFactory)ctx.lookup(topicFactory);

        // Create a JMS connection,表示和消息服务器的一个连接
        TopicConnection connection = conFactory.createTopicConnection();

        /**
         *Create two JMS session objects，通常从同一连接创建多个session.
         *session对象被用于创建Message, TopicPublisher, TopicSubscriber对象的工厂
         *session对象需要保证同一时刻只有一个线程在调用中
        */
        TopicSession pubSession = connection.createTopicSession(
                false, Session.AUTO_ACKNOWLEDGE);
        TopicSession subSession = connection.createTopicSession(
                false, Session.AUTO_ACKNOWLEDGE);

        // Look up a JMS topic by JNDI service
        Topic chatTopic = (Topic)ctx.lookup(topicName);

        // Create a JMS publisher and subscriber
        TopicPublisher publisher =
                pubSession.createPublisher(chatTopic);
        TopicSubscriber subscriber =
                subSession.createSubscriber(chatTopic, null, true);

        // Set a JMS message listener
        subscriber.setMessageListener(this);

        // Initialize the Chat application variables
        this.connection = connection;
        this.pubSession = pubSession;
        this.publisher = publisher;
        this.username = username;

        // Start the JMS connection; allows messages to be delivered
        connection.start( );
    }

    /* Receive Messages From Topic Subscriber */
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText( );
            System.out.println(text);
        } catch (JMSException jmse){ jmse.printStackTrace( ); }
    }

    /**
     * Create and Send Message Using Publisher
     *
     * @param text
     * @throws JMSException
     */
    protected void writeMessage(String text) throws JMSException {
        TextMessage message = pubSession.createTextMessage( );
        message.setText(username+": "+text);
        publisher.publish(message);
    }

    /* Close the JMS Connection */
    public void close( ) throws JMSException {
        connection.close( );
    }

    /* Run the Chat Client */
    public static void main(String [] args){
        try{
            if (args.length!=3)
                System.out.println("Factory, Topic, or username missing");

            // args[0]=topicFactory; args[1]=topicName; args[2]=username
            Chat chat = new Chat(args[0],args[1],args[2]);

            // Read from command line
            BufferedReader commandLine = new
                    java.io.BufferedReader(new InputStreamReader(System.in));

            // Loop until the word "exit" is typed
            while(true){
                String s = commandLine.readLine( );
                if (s.equalsIgnoreCase("exit")){
                    chat.close( ); // close down connection
                    System.exit(0);// exit program
                } else
                    chat.writeMessage(s);
            }
        } catch (Exception e){ e.printStackTrace( ); }
    }
}