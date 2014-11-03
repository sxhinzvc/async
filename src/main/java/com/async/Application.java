package com.async;
import javax.jms.JMSException;

public class Application {
    public static void main(String[] args) throws JMSException {
        AccountListener listener = new AccountListener();
        listener.listen();

    }
}