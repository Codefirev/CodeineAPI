package rip.kirooh.example.impl;

import rip.kirooh.adapter.CommandTypeAdapter;

import java.text.*;

public class NumberTypeAdapter implements CommandTypeAdapter
{
    @Override
    public <T> T convert(final String string, final Class<T> type) {
        try {
            return type.cast(NumberFormat.getInstance().parse(string));
        }
        catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
