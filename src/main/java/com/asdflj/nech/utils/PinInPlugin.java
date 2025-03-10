package com.asdflj.nech.utils;

import static com.asdflj.nech.integration.nei.NEIConfig.getConfigValue;
import static com.asdflj.nech.utils.Match.inputMiddleware;
import static com.asdflj.nech.utils.Match.textMiddleware;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.asdflj.nech.integration.nei.ButtonConstants;
import com.asdflj.nech.integration.nei.Voltage;

public class PinInPlugin implements Runnable {

    public static final String voltagePatternString = Voltage.getPatternString();

    @Override
    public void run() {
        LuaPlugin.loadLuaScript();
        textMiddleware.add(s -> {
            if (getConfigValue(ButtonConstants.COMMA)) {
                return s.replaceAll(",", "");
            }
            return s;
        });
        textMiddleware.add(s -> {
            if (getConfigValue(ButtonConstants.PARENTHESES)) {
                return s.replaceAll("[()]", "");
            }
            return s;
        });
        textMiddleware.add(new ITextFunction<>() {

            @Override
            public String apply(String s) {
                if (getConfigValue(ButtonConstants.LUA_SCRIPT)) {
                    for (LuaPlugin.LuaObject o : LuaPlugin.list) {
                        if (o.getState()) {
                            s = o.textHandler(s);
                        }
                    }
                }
                return s;
            }

            @Override
            public int index() {
                return 90;
            }
        });

        inputMiddleware.add(s -> {
            if (getConfigValue(ButtonConstants.VOLTAGE) && s.matches("^" + voltagePatternString + "\\w+")) {
                s = s.replaceFirst(voltagePatternString, "$1 ");
                Set<String> result = Arrays.stream(s.split(" "))
                    .collect(Collectors.toSet());
                if (result.size() > 1) {
                    return result;
                }
            }
            return null;
        });

        inputMiddleware.add(new IInputFunction<>() {

            @Override
            public Set<String> apply(String s) {
                if (getConfigValue(ButtonConstants.LUA_SCRIPT)) {
                    Set<String> set = new HashSet<>();
                    for (LuaPlugin.LuaObject o : LuaPlugin.list) {
                        if (o.getState()) {
                            Set<String> r = o.inputHandler(s);
                            if (r != null) {
                                set.addAll(r);
                            }
                        }
                    }
                    return set;
                }
                return null;
            }

            @Override
            public int index() {
                return 90;
            }
        });
        textMiddleware.sort(Comparator.comparing(IIndexFunction::index));
        inputMiddleware.sort(Comparator.comparing(IIndexFunction::index));
    }
}
