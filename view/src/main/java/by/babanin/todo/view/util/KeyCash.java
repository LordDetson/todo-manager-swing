package by.babanin.todo.view.util;

import java.util.HashMap;
import java.util.Map;

import by.babanin.todo.view.exception.ViewException;

public class KeyCash {

    Map<Integer, String> code2name;
    Map<String, Integer> name2code;

    public KeyCash() {
        code2name = new HashMap<>();
        name2code = new HashMap<>();
    }

    public synchronized void put(String name, Integer code) {
        if(findName(code) == null && findCode(name) == null) {
            code2name.put(code, name);
            name2code.put(name, code);
        }
    }

    public synchronized Integer findCode(String name) {
        checkNotNull(name);
        return name2code.get(name);
    }

    public synchronized String findName(Integer code) {
        checkNotNull(code);
        return code2name.get(code);
    }

    private void checkNotNull(Object o) {
        if(o == null) {
            throw new ViewException("Can't be null");
        }
    }
}
