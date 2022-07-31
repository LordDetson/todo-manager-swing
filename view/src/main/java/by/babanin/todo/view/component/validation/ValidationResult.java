package by.babanin.todo.view.component.validation;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import by.babanin.todo.view.component.logger.LogMessageType;
import by.babanin.todo.view.exception.ViewException;

public class ValidationResult implements Iterable<Entry<LogMessageType, List<String>>> {

    private final Map<LogMessageType, List<String>> messageMap = new EnumMap<>(LogMessageType.class);

    public void put(LogMessageType type, String message) {
        if(StringUtils.isBlank(message)) {
            throw new ViewException("Message can't be empty");
        }
        List<String> messages = messageMap.computeIfAbsent(type, t -> new ArrayList<>());
        messages.add(message);
    }

    public boolean isEmpty() {
        return messageMap.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public boolean hasMessages(LogMessageType type) {
        return messageMap.containsKey(type);
    }

    @Override
    public Iterator<Entry<LogMessageType, List<String>>> iterator() {
        return messageMap.entrySet().iterator();
    }
}
