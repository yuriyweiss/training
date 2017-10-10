package ru.yvays.training.thread.queue;

import java.util.HashSet;
import java.util.Set;

public class Message {
    private final String messageBody;
    private int number;

    public Message(String messageBody) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        String
        Message message = (Message) o;

        if (number != message.number) return false;
        return messageBody != null ? messageBody.equals(message.messageBody) : message.messageBody == null;
    }

    @Override
    public int hashCode() {
        int result = messageBody != null ? messageBody.hashCode() : 0;
        result = 31 * result + number;
        return result;
    }
}
