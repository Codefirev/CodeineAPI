package rip.kirooh.example;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import rip.kirooh.command.CommandMeta;

@CommandMeta(label = { "Kirooh" }, permission = "kirooh.example.command")
public class ExampleCommand
{
    public void execute(final CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "pussy got that wet wet got that drip drip got that super soakers");
    }

    public void execute(final Player player) {
        player.sendMessage(ChatColor.RED + player.getName() + " pussy nigga");
    }

    @CommandMeta(label = { "help" }, permission = "kirooh.example.subcommand")
    public class HelpCommand extends ExampleCommand
    {
        @Override
        public void execute(final CommandSender sender) {
            sender.sendMessage(ChatColor.RED + "kirooh is the shit fam");
        }
    }

    @CommandMeta(label = { "broadcast" }, permission = "kirooh.example.subcommand")
    public class BroadcastCommand extends ExampleCommand
    {
        public void execute(final CommandSender sender, final String message) {
            Bukkit.broadcastMessage(ChatColor.AQUA + message);
        }
    }

    @CommandMeta(label = { "gamemode", "gm" }, permission = "kirooh.example.subcommand")
    public class GameModeCommand extends ExampleCommand
    {
        public void execute(final CommandSender sender, final GameMode gameMode) {
            if (gameMode == null) {
                sender.sendMessage(ChatColor.RED + "Game mode with that name not found.");
                return;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You are not a player.");
                return;
            }
            ((Player)sender).setGameMode(gameMode);
            sender.sendMessage(ChatColor.GRAY + "You set your game mode to " + gameMode.name());
        }
    }
}
