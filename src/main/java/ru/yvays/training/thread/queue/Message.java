package ru.yvays.training.thread.queue;

public class Message {
    private final String messageBody;

    public Message( String messageBody ) {
        this.messageBody = messageBody;
    }

    public String getMessageBody() {
        return messageBody;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageBody='" + messageBody + '\'' +
                '}';
    }
}
