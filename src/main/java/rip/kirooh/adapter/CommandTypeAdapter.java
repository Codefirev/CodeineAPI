package rip.kirooh.adapter;

import java.util.*;

public interface CommandTypeAdapter
{
    <T> T convert(final String string, final Class<T> type);

    default <T> List<String> tabComplete(final String string, final Class<T> type) {
        return new ArrayList<String>();
    }
}
