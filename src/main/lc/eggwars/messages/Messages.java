package lc.eggwars.messages;

import java.util.Map;

public final class Messages {
    private final Map<String, String> parsedMessages;

    public Messages(Map<String, String> parsedMessages) {
        this.parsedMessages = parsedMessages;
    }

    public String get(String path) {
        return parsedMessages.get(path);
    }
}