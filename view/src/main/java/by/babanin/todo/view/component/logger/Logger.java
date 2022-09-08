package by.babanin.todo.view.component.logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import by.babanin.todo.view.translat.TranslateCode;
import by.babanin.todo.view.translat.Translator;
import by.babanin.todo.view.util.GUIUtils;

public class Logger {

    private static final int MAX_LOG_LINES = 50;

    private final Map<LogMessageType, List<String>> logs = new LinkedHashMap<>();
    private final Map<LogMessageType, Integer> counters = new LinkedHashMap<>();
    private final Set<Object> moreLinesIgnored = new LinkedHashSet<>();
    private final List<LogUpdateListener> logUpdateListeners = new ArrayList<>();

    public void log(LogMessageType type, String message) {
        int newCount = increaseCount(type);
        List<String> messages = logs.computeIfAbsent(type, logMessageType -> new ArrayList<>());
        if(newCount < MAX_LOG_LINES) {
            messages.add(message);
        }
        else {
            moreLinesIgnored.add(type);
        }
        fireLogChanged();
    }

    public void logBatchMessages(Iterable<Entry<LogMessageType, List<String>>> batchMessages) {
        for(Entry<LogMessageType, List<String>> entry : batchMessages) {
            LogMessageType type = entry.getKey();
            List<String> messages = entry.getValue();
            for(String message : messages) {
                log(type, message);
            }
        }
    }

    public Set<LogMessageType> getMessageTypes() {
        return logs.keySet();
    }

    public void logError(String message) {
        log(LogMessageType.ERROR, message);
    }

    public void logError(Exception e) {
        log(LogMessageType.ERROR, GUIUtils.stacktraceToString(e));
    }

    public void logWarning(String message) {
        log(LogMessageType.WARNING, message);
    }

    public void logInformation(String message) {
        log(LogMessageType.INFORMATION, message);
    }

    public List<String> getLog(LogMessageType type) {
        List<String> result = Optional.ofNullable(logs.get(type))
                .orElse(Collections.emptyList());

        if(moreLinesIgnored.contains(type)) {
            result = new ArrayList<>(result);
            result.add(Translator.toLocale(TranslateCode.LOG_LINES_IGNORED).formatted(getMessageCount(type)));
            return result;
        }
        else {
            return new ArrayList<>(result);
        }
    }

    private int increaseCount(LogMessageType key) {
        Integer count = counters.get(key);
        if(count == null) {
            count = 1;
        }
        else {
            count++;
        }
        counters.put(key, count);
        return count;
    }

    public int getMessageCount(LogMessageType type) {
        Integer count = counters.get(type);
        if(count == null) {
            return 0;
        }

        return count;
    }

    public int getMessageCount() {
        int sum = 0;
        for(Integer count : counters.values()) {
            sum += count;
        }
        return sum;
    }

    public boolean hasMessages(LogMessageType type) {
        return getMessageCount(type) > 0;
    }

    public boolean hasMessages() {
        return getMessageCount() > 0;
    }

    public List<String> getMessages(LogMessageType type) {
        return getLog(type);
    }

    public boolean isEmpty() {
        return logs.isEmpty();
    }

    public boolean isValid() {
        return !hasMessages(LogMessageType.ERROR);
    }

    public void clear() {
        logs.clear();
        counters.clear();
        moreLinesIgnored.clear();
        fireLogCleared();
    }

    public void addLogChangeListener(LogUpdateListener listener) {
        logUpdateListeners.add(listener);
    }

    public void removeLogChangeListener(LogUpdateListener listener) {
        logUpdateListeners.remove(listener);
    }

    public void fireLogChanged() {
        logUpdateListeners.forEach(LogUpdateListener::logChanged);
    }

    public void fireLogCleared() {
        logUpdateListeners.forEach(LogUpdateListener::logCleared);
    }
}
