package cz.darklabs.darkproxymanager;

import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;

public class MessageConsumer {

    private ConnectionFactory factory;
    private Connection conn;

    public void initializeRabbitMQConnection() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        conn = factory.newConnection();
    }

    public void startConsumeServerDeclareMessage() throws IOException {
        conn.createChannel().basicConsume("server_declare", true, (consumerTag, message) -> {
            System.out.println("Received message from server_declare: " + new String(message.getBody()));
        }, consumerTag -> {
        });
    }

    public void closeRabbitMQConnection() throws IOException {
        conn.close();
    }
}
