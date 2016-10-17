package me.satisdev.spec.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.satisdev.spec.SpecMode;

public class Spec implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		Player target;

		if (args.length == 0 && !(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You must specify a player.");
			return true;

		} else if (args.length == 0) {
			target = (Player) sender;
		} else {
			target = Bukkit.getServer().getPlayer(args[0]);

			if (target == null) {
				sender.sendMessage(ChatColor.RED + "Cannot find player '" + args[0] + "'");
				return true;
			}
		}

		if (target.getGameMode().equals(GameMode.ADVENTURE) && !SpecMode.vanished.contains(target)) {

			target.setGameMode(GameMode.CREATIVE);
			target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 0));

			for (Player ps : Bukkit.getOnlinePlayers()) {
				ps.hidePlayer(target);
			}
			SpecMode.vanished.add(target);
			target.setMetadata("hidden", new FixedMetadataValue(SpecMode.instance, true));

			if (sender != target) {
				sender.sendMessage(ChatColor.GREEN + "You  have put " + target.getName() + " in spectator mode.");
				target.sendMessage(
						ChatColor.GREEN + "You have been put in spectator mode by " + sender.getName() + ".");
			} else {
				sender.sendMessage(ChatColor.GREEN + "You are now in spectator mode.");
			}

		} else if (target.getGameMode().equals(GameMode.CREATIVE) && SpecMode.vanished.contains(target)) {

			target.setGameMode(GameMode.ADVENTURE);
			target.removePotionEffect(PotionEffectType.NIGHT_VISION);

			for (Player ps : Bukkit.getOnlinePlayers()) {
				ps.showPlayer(target);
			}
			SpecMode.vanished.remove(target);
			target.removeMetadata("hidden", SpecMode.instance);

			target.sendMessage(ChatColor.RED + "You are no longer in spectator mode.");
			if (sender != target) {
				sender.sendMessage(ChatColor.RED + "You have taken " + target.getName() + " out of spectator mode.");
			}

		} else {

			target.setGameMode(GameMode.ADVENTURE);
			target.sendMessage(ChatColor.YELLOW + "You were put into the proper GameMode, you may now use this Item.");
		}
		return true;
	}
}
