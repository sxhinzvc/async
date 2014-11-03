package com.async;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import javax.jms.JMSException;
import java.util.concurrent.*;

@Controller
@RequestMapping("/accounts")
public class AccountController {

    private final AccountMessenger accountMessenger;

    public AccountController() {
        accountMessenger = new AccountMessenger();
    }

    private ExecutorService executor = Executors.newFixedThreadPool(200);

    @RequestMapping("/dummy")
    @ResponseBody
    public String dummy() {
        return "dummyX";
    }

    @RequestMapping(value = "/deferednew", produces = "application/json")
    @ResponseBody
    public DeferredResult<String> createAsyncDeferred() throws JMSException, InterruptedException {
        final DeferredResult result = new DeferredResult<String>();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    result.setResult("{ \"result\" : \"some result:" + accountMessenger.createAccount("tw").getStringProperty("accountID")+ "\"}");
                } catch (Exception e) {
                    result.setErrorResult(e);
                }
            }
        };
        executor.submit(task);

        return result;
    }

    @RequestMapping(value = "/syncnew", produces = "application/json")
    @ResponseBody
    public String createSync() throws JMSException {
        String result = accountMessenger.createAccount("thoughtworks").getStringProperty("accountID");
        return "{ \"result\":\"A result:" + result + "}";

    }

    @ExceptionHandler
    @ResponseBody
    public String handleException(Exception e) {
        return e.getClass().toString();
    }
}
