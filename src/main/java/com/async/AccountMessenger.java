package com.async;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class AccountMessenger {

    private final ActiveMQConnectionFactory connectionFactory;

    public AccountMessenger() {
        // Create a ConnectionFactory
        connectionFactory = new ActiveMQConnectionFactory("nio://0.0.0.0:61616");
    }

    public Message createAccount(String name) throws JMSException {
        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination requests = session.createQueue("requests");

        Destination replies = session.createTemporaryQueue();

        // Create a MessageProducer from the Session to the Topic or Queue
        MessageProducer producer = session.createProducer(requests);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // Create a messages
        String text = "Create account " + name;
        TextMessage message = session.createTextMessage(text);
        message.setJMSReplyTo(replies);
        producer.send(message);


        MessageConsumer consumer = session.createConsumer(replies);
        Message response = consumer.receive(5000);

        session.close();
        connection.close();

        return response;
    }
}
