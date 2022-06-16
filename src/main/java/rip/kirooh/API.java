package rip.kirooh;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.*;
import org.bukkit.event.server.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.command.*;
import org.apache.commons.lang.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.*;
import rip.kirooh.adapter.CommandTypeAdapter;
import rip.kirooh.command.CommandMeta;
import rip.kirooh.command.CommandOption;
import rip.kirooh.example.impl.*;

import java.lang.reflect.*;
import java.util.*;

public class API implements Listener
{
    private final JavaPlugin plugin;
    private final Map<Class, CommandTypeAdapter> adapters;
    private final Map<String, Object> commands;
    private final Map<Object, CommandMeta> metas;

    public API(final JavaPlugin plugin) {
        this.plugin = plugin;
        this.adapters = new HashMap<Class, CommandTypeAdapter>();
        this.commands = new HashMap<String, Object>();
        this.metas = new HashMap<Object, CommandMeta>();
        this.registerTypeAdapter(Player.class, new PlayerTypeAdapter());
        this.registerTypeAdapter(String.class, new StringTypeAdapter());
        this.registerTypeAdapter(Number.class, new NumberTypeAdapter());
        this.registerTypeAdapter(Integer.TYPE, new NumberTypeAdapter());
        this.registerTypeAdapter(Long.TYPE, new NumberTypeAdapter());
        this.registerTypeAdapter(Double.TYPE, new NumberTypeAdapter());
        this.registerTypeAdapter(Float.TYPE, new NumberTypeAdapter());
        this.registerTypeAdapter(Boolean.class, new BooleanTypeAdapter());
        this.registerTypeAdapter(Boolean.TYPE, new BooleanTypeAdapter());
        this.registerTypeAdapter(World.class, new WorldTypeAdapter());
        this.registerTypeAdapter(GameMode.class, new GameModeTypeAdapter());
        this.registerTypeAdapter(CommandOption.class, new CommandOptionTypeAdapter());
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }

    @EventHandler
    public void onServerCommandEvent(final ServerCommandEvent event) {
        this.handle(event.getSender(), "/" + event.getCommand(), (Cancellable)event);
    }

    @EventHandler
    public void onPlayerCommandPreprocessEvent(final PlayerCommandPreprocessEvent event) {
        this.handle((CommandSender)event.getPlayer(), event.getMessage(), (Cancellable)event);
    }

    private void handle(final CommandSender commandSender, final String message, final Cancellable cancellable) {
        final String[] messageSplit = message.substring(1).split(" ");
        Object command = null;
        String label = null;
        for (int remaining = messageSplit.length; remaining > 0; --remaining) {
            label = StringUtils.join((Object[])messageSplit, " ", 0, remaining);
            if (this.commands.get(label.toLowerCase()) != null) {
                command = this.commands.get(label.toLowerCase());
                break;
            }
        }
        if (command != null) {
            final CommandMeta meta = this.metas.get(command);
            final String[] labelSplit = label.split(" ");
            String[] args = new String[0];
            if (messageSplit.length != labelSplit.length) {
                final int numArgs = messageSplit.length - labelSplit.length;
                args = new String[numArgs];
                System.arraycopy(messageSplit, labelSplit.length, args, 0, numArgs);
            }
            cancellable.setCancelled(true);
            final APIExecutor executor = new APIExecutor(this, label.toLowerCase(), commandSender, command, args);
            if (meta.async()) {
                new BukkitRunnable() {
                    public void run() {
                        executor.execute();
                    }
                }.runTaskAsynchronously((Plugin)this.plugin);
            }
            else {
                executor.execute();
            }
        }
    }

    public void registerTypeAdapter(final Class clazz, final CommandTypeAdapter adapter) {
        this.adapters.put(clazz, adapter);
    }

    public CommandTypeAdapter getTypeAdapter(final Class clazz) {
        return this.adapters.get(clazz);
    }

    public void registerCommand(final Object object) {
        final CommandMeta meta = object.getClass().getAnnotation(CommandMeta.class);
        if (meta == null) {
            throw new RuntimeException(new ClassNotFoundException(object.getClass().getName() + " is missing CommandMeta annotation"));
        }
        for (final String label : this.getLabels(object.getClass(), new ArrayList<String>())) {
            this.commands.put(label.toLowerCase(), object);
        }
        this.metas.put(object, meta);
        if (meta.autoAddSubCommands()) {
            for (final Class<?> clazz : object.getClass().getDeclaredClasses()) {
                if (clazz.getSuperclass().equals(object.getClass())) {
                    try {
                        this.registerCommand(clazz.getDeclaredConstructor(object.getClass()).newInstance(object));
                    }
                    catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex2) {
                        final ReflectiveOperationException ex = null;
                        final ReflectiveOperationException e = ex;
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private List<String> getLabels(final Class clazz, List<String> list) {
        final List<String> toReturn = new ArrayList<String>();
        final Class superClass = clazz.getSuperclass();
        if (superClass != null) {
            final CommandMeta meta = (CommandMeta) superClass.getAnnotation(CommandMeta.class);
            if (meta != null) {
                list = this.getLabels(superClass, list);
            }
        }
        final CommandMeta meta = (CommandMeta) clazz.getAnnotation(CommandMeta.class);
        if (meta == null) {
            return list;
        }
        if (list.isEmpty()) {
            toReturn.addAll(Arrays.asList(meta.label()));
        }
        else {
            for (final String prefix : list) {
                for (final String label : meta.label()) {
                    toReturn.add(prefix + " " + label);
                }
            }
        }
        return toReturn;
    }
}
