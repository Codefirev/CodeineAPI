package rip.kirooh.example.impl;

import rip.kirooh.adapter.CommandTypeAdapter;
import rip.kirooh.command.CommandOption;

public class CommandOptionTypeAdapter implements CommandTypeAdapter
{
    @Override
    public <T> T convert(final String string, final Class<T> type) {
        if (string.startsWith("-")) {
            return type.cast(new CommandOption(string.substring(1)));
        }
        return null;
    }
}
