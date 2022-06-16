package rip.kirooh.example.impl;

import rip.kirooh.adapter.CommandTypeAdapter;

import java.util.*;

public class BooleanTypeAdapter implements CommandTypeAdapter
{
    private static final Map<String, Boolean> MAP;

    public <T> T convert(final String string, final Class<T> type) {
        return type.cast(BooleanTypeAdapter.MAP.get(string.toLowerCase()));
    }

    static {
        (MAP = new HashMap<String, Boolean>()).put("true", true);
        BooleanTypeAdapter.MAP.put("yes", true);
        BooleanTypeAdapter.MAP.put("false", false);
        BooleanTypeAdapter.MAP.put("no", false);
    }
}
