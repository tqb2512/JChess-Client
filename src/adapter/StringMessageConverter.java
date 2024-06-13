package adapter;

import org.springframework.messaging.converter.SimpleMessageConverter;

public class StringMessageConverter extends SimpleMessageConverter {
    @Override
    public Object fromMessage(org.springframework.messaging.Message<?> message, Class<?> targetClass) {
        if (targetClass == String.class) {
            return new String((byte[]) message.getPayload());
        }
        return super.fromMessage(message, targetClass);
    }
}