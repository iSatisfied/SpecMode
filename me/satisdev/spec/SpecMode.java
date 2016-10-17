package me.satisdev.spec;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.satisdev.spec.commands.Spec;

public class SpecMode extends JavaPlugin implements Listener {

	public static SpecMode instance;
	public static ArrayList<Player> vanished = new ArrayList<Player>();

	@Override
	public void onEnable() {
		System.out.println("Enabling SpecPL...");
		getServer().getPluginManager().registerEvents(this, this);
		getCommand("spec").setExecutor(new Spec());
		instance = this;
	}

	@Override
	public void onDisable() {
		System.out.println("Disabling SpecPL...");
	}

	private void specMode(Player player) {
		Player p = player.getPlayer();

		if (p.getGameMode().equals(GameMode.ADVENTURE) && !vanished.contains(p)) {

			p.setGameMode(GameMode.CREATIVE);
			p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 999999, 0));

			for (Player ps : Bukkit.getOnlinePlayers()) {
				ps.hidePlayer(p);
			}
			vanished.add(p);
			p.setMetadata("hidden", new FixedMetadataValue(this, true));

			p.sendMessage(ChatColor.GREEN + "You are now in spectator mode.");
		} else if (p.getGameMode().equals(GameMode.CREATIVE) && vanished.contains(p)) {

			p.setGameMode(GameMode.ADVENTURE);
			p.removePotionEffect(PotionEffectType.NIGHT_VISION);

			for (Player ps : Bukkit.getOnlinePlayers()) {
				ps.showPlayer(p);
			}
			vanished.remove(p);
			p.removeMetadata("hidden", this);

			p.sendMessage(ChatColor.RED + "You are no longer in spectator mode.");
		} else {
			p.setGameMode(GameMode.ADVENTURE);
			p.sendMessage(ChatColor.YELLOW + "You were put into the proper GameMode, you may now use this Item.");
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		for (Player p : vanished) {
			e.getPlayer().hidePlayer(p);
		}
		
		Player player = e.getPlayer();

		PlayerInventory inv = player.getInventory();
		inv.clear();

		ItemStack SpecMode = new ItemStack(Material.REDSTONE_TORCH_ON);
		ItemMeta im = SpecMode.getItemMeta();
		im.setDisplayName(ChatColor.DARK_RED + "Toggle SpecMode");
		SpecMode.setItemMeta(im);

		inv.setHeldItemSlot(8);
		inv.setItemInHand(SpecMode);
		inv.setHeldItemSlot(0);
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		vanished.remove(e.getPlayer());
		e.getPlayer().setGameMode(GameMode.ADVENTURE);
		
	}
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Action a = e.getAction();
		ItemStack is = e.getItem();

		if (a == Action.PHYSICAL || is == null || is.getType() == Material.AIR)
			return;

		if (is.getType() == Material.REDSTONE_TORCH_ON) {

			if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
				specMode(e.getPlayer());
			}
		}
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		Player p = e.getPlayer();
		
		if (vanished.contains(p)) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		EntityType p = e.getEntityType();
		 if (vanished.contains(p)) {
			 e.setCancelled(true);
		 }
	}
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		
		if (vanished.contains(p)) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		
		if (vanished.contains(p)) {
			e.setCancelled(true);
		}
	}
}
