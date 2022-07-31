package by.babanin.todo.view.component.logger;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CombinedLogger {

    private final Map<String, Logger> loggerMap = new LinkedHashMap<>();

    public void logMessage(String id, LogMessageType type, String message) {
        Logger logger = loggerMap.computeIfAbsent(id, i -> new Logger());
        logger.log(type, message);
    }

    public Collection<Logger> getLoggers() {
        return loggerMap.values();
    }

    public boolean isEmpty() {
        for(Logger logger : loggerMap.values()) {
            if(!logger.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public int getMessageCount(LogMessageType type) {
        int count = 0;
        for(Logger logger : loggerMap.values()) {
            count += logger.getMessageCount(type);
        }
        return count;
    }

    public Set<LogMessageType> getMessageTypes() {
        return loggerMap.values()
                .stream()
                .flatMap(log -> log.getMessageTypes().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public int getMessageCount() {
        int count = 0;
        for(Logger logger : loggerMap.values()) {
            count += logger.getMessageCount();
        }
        return count;
    }

    public void clear() {
        loggerMap.clear();
    }
}
