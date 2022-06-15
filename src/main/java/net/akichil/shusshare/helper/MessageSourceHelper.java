package net.akichil.shusshare.helper;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageSourceHelper {

    private final MessageSource messageSource;

    public MessageSourceHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * messages.propertiesのメッセージを返す
     */
    public String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, Locale.getDefault());
    }

    /**
     * messages.propertiesのメッセージを返す
     */
    public String getMessage(String code) {
        return getMessage(code, (Object[]) null);
    }

}
