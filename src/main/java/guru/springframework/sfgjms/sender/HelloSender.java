package guru.springframework.sfgjms.sender;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.sfgjms.config.JmsConfig;
import guru.springframework.sfgjms.model.HelloWorldMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.UUID;

@Component
public class HelloSender {


    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public HelloSender(JmsTemplate jmsTemplate, ObjectMapper objectMapper) {
        this.jmsTemplate = jmsTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 2000)
    public void sendMessage() {
        System.out.println("I am sending a message");

        HelloWorldMessage helloWorldMessage = HelloWorldMessage.builder()
            .id(UUID.randomUUID())
            .message("Hello world!")
            .build();

        jmsTemplate.convertAndSend(JmsConfig.MY_QUEUE, helloWorldMessage);

        System.out.println("Message sent!");

    }

    @Scheduled(fixedDelay = 2000)
    public void sendAndReceiveMessage() throws JMSException {

        HelloWorldMessage helloWorldMessage = HelloWorldMessage.builder()
            .id(UUID.randomUUID())
            .message("Hello")
            .build();

        Message receivedMasage = jmsTemplate.sendAndReceive(JmsConfig.MY_SEND_RECEIVE_QUEUE, session -> {
            Message helloMessage;
            try {
                helloMessage = session.createTextMessage(objectMapper.writeValueAsString(helloWorldMessage));
                helloMessage.setStringProperty("_type", "guru.springframework.sfgjms.model.HelloWorldMessage");
                System.out.println("Sending hello!");
                return helloMessage;
            } catch (JsonProcessingException e) {
                throw new JMSException("threw");
            }
        });

        System.out.println(receivedMasage.getBody(String.class));

    }

}
