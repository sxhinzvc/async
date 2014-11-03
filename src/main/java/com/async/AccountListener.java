package com.async;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AccountListener {

    private final ActiveMQConnectionFactory connectionFactory;
    private final Connection connection;
    private Session session;

    private final ExecutorService executorService = Executors.newFixedThreadPool(500);

    public AccountListener() throws JMSException {
        connectionFactory = new ActiveMQConnectionFactory("nio://0.0.0.0:61616");
        connection = connectionFactory.createConnection();
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public void listen() throws JMSException {
        final Random rand = new Random();
        // Create the destination (Topic or Queue)
        Destination requests = session.createQueue("requests");
        MessageConsumer consumer = session.createConsumer(requests);

        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(final Message message) {
                executorService.submit(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            handleAccountCreationRequest(message);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void handleAccountCreationRequest(Message message) throws InterruptedException, JMSException {
        //Thread.sleep(rand.nextInt(500));
        Thread.sleep(1000);
        Destination responses = message.getJMSReplyTo();
        MessageProducer responseProducer = session.createProducer(responses);
        TextMessage response = session.createTextMessage();
        response.setStringProperty("accountID", UUID.randomUUID().toString());
        responseProducer.send(response);
    }

    public void shutdown() throws JMSException {
        session.close();
        connection.close();
    }
}
