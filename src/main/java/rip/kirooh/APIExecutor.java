package rip.kirooh;

import org.bukkit.command.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.apache.commons.lang.*;
import rip.kirooh.adapter.CommandTypeAdapter;
import rip.kirooh.command.CPL;
import rip.kirooh.command.CommandMeta;
import rip.kirooh.command.CommandOption;

import java.lang.reflect.*;
import java.lang.annotation.*;
import java.util.*;

public class APIExecutor
{
    private final API api;
    private final String label;
    private final CommandMeta meta;
    private final CommandSender sender;
    private final Object command;
    private String[] args;

    public APIExecutor(final API api, final String label, final CommandSender sender, final Object command, final String[] args) {
        this.api = api;
        this.label = label;
        this.meta = command.getClass().getAnnotation(CommandMeta.class);
        this.sender = sender;
        this.command = command;
        this.args = args;
    }

    public void execute() {
        if (!this.meta.permission().equalsIgnoreCase("") && !this.sender.hasPermission(this.meta.permission())) {
            this.sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
            return;
        }
        Label_0805:
        for (final Method method : this.command.getClass().getMethods()) {
            Label_0799: {
                if (method.getDeclaringClass().equals(this.command.getClass())) {
                    if (method.getParameterCount() - 1 > this.args.length) {
                        boolean doContinue = true;
                        for (final Parameter parameter : method.getParameters()) {
                            if (parameter.getType().equals(CommandOption.class) && method.getParameterCount() - 2 <= this.args.length) {
                                doContinue = false;
                                break;
                            }
                        }
                        if (doContinue) {
                            break Label_0799;
                        }
                    }
                    for (final Method otherMethod : this.command.getClass().getMethods()) {
                        if (!otherMethod.equals(method)) {
                            if (method.getParameterCount() == otherMethod.getParameterCount() && method.getParameters()[0].getType().equals(CommandSender.class) && otherMethod.getParameters()[0].getType().equals(Player.class) && this.sender instanceof Player) {
                                break Label_0799;
                            }
                            if (this.args.length != method.getParameterCount() - 1 && this.args.length - method.getParameterCount() > this.args.length - otherMethod.getParameterCount()) {
                                break Label_0799;
                            }
                        }
                    }
                    if (method.getParameterCount() > 0 && (method.getParameters()[0].getType().equals(CommandSender.class) || method.getParameters()[0].getType().equals(Player.class))) {
                        final List<Object> arguments = new ArrayList<Object>();
                        final Parameter[] parameters = method.getParameters();
                        arguments.add(this.sender);
                        if (!method.getParameters()[0].getType().equals(Player.class) || this.sender instanceof Player) {
                            for (int i = 1; i < parameters.length; ++i) {
                                final Parameter parameter2 = parameters[i];
                                final CommandTypeAdapter adapter = this.api.getTypeAdapter(parameter2.getType());
                                if (adapter == null) {
                                    arguments.add(null);
                                }
                                else {
                                    Object object;
                                    if (i == parameters.length - 1) {
                                        object = adapter.convert(StringUtils.join((Object[])this.args, " ", i - 1, this.args.length), parameter2.getType());
                                    }
                                    else {
                                        object = adapter.convert(this.args[i - 1], parameter2.getType());
                                    }
                                    if (parameter2.getType().equals(CommandOption.class) && object == null) {
                                        final List<String> replacement = new ArrayList<String>(Arrays.asList(this.args));
                                        replacement.add(i - 1, null);
                                        this.args = replacement.toArray(new String[0]);
                                    }
                                    if (object instanceof CommandOption) {
                                        final CommandOption option = (CommandOption)object;
                                        if (!Arrays.asList(this.meta.options()).contains(option.getTag().toLowerCase())) {
                                            this.sender.sendMessage(ChatColor.RED + "Unrecognized command option \"-" + option.getTag().toLowerCase() + "\"!");
                                            break Label_0805;
                                        }
                                    }
                                    arguments.add(object);
                                }
                            }
                            if (arguments.size() == parameters.length) {
                                try {
                                    method.invoke(this.command, arguments.toArray());
                                }
                                catch (IllegalAccessException | InvocationTargetException ex2) {
                                    final ReflectiveOperationException ex = null;
                                    final ReflectiveOperationException e = ex;
                                    e.printStackTrace();
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
        this.sender.sendMessage(this.getUsage());
    }

    private String getUsage() {
        final StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.RED).append("Usage: /").append(this.label);
        if (this.meta.options().length > 0) {
            final List<String> options = new ArrayList<String>();
            for (final String option : this.meta.options()) {
                options.add("-" + option.toLowerCase());
            }
            builder.append(" [");
            builder.append(StringUtils.join((Collection)options, ","));
            builder.append("]");
        }
        final Map<Integer, List<String>> arguments = new HashMap<Integer, List<String>>();
        for (final Method method : this.command.getClass().getDeclaredMethods()) {
            final Parameter[] parameters = method.getParameters();
            for (int i = 1; i < parameters.length; ++i) {
                final List<String> argument = arguments.getOrDefault(i - 1, new ArrayList<String>());
                final Parameter parameter = parameters[i];
                if (parameter.getType().equals(CommandOption.class)) {
                    arguments.put(i - 1, null);
                }
                else {
                    if (parameter.isAnnotationPresent(CPL.class)) {
                        argument.add(parameter.getAnnotation(CPL.class).value().toLowerCase());
                    }
                    else {
                        final String name = parameter.getName();
                        if (!argument.contains(name)) {
                            argument.add(name);
                        }
                    }
                    arguments.put(i - 1, argument);
                }
            }
        }
        for (int j = 0; j < arguments.size(); ++j) {
            final List<String> argument2 = arguments.get(j);
            if (argument2 != null) {
                builder.append(" <").append(StringUtils.join((Collection)argument2, "/")).append(">");
            }
        }
        return builder.toString();
    }
}
