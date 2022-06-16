package rip.kirooh.example.impl;

import org.bukkit.*;
import rip.kirooh.adapter.CommandTypeAdapter;

public class WorldTypeAdapter implements CommandTypeAdapter
{
    @Override
    public <T> T convert(final String string, final Class<T> type) {
        return type.cast(Bukkit.getWorld(string));
    }
}
