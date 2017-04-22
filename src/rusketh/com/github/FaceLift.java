package rusketh.com.github;

import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Golem;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class FaceLift extends JavaPlugin implements Listener {
	Random random = new Random();
	
	public String getVer() {
		return "0.0.1";
	}
	
	public void onEnable() {
		Logger.getAnonymousLogger().info("Installed FaceLift by Rusketh");
		getServer().getPluginManager().registerEvents(this, this);
		defaultConfig();
	}
	
	private void defaultConfig() {
		FileConfiguration c = getConfig();
		
		if (!c.getBoolean("none-defaults")) {
			c.set("none-defaults", true);
			c.set("message", "%1 Beheaded %2.");

			ConfigurationSection pSec = c.createSection("player");
			ConfigurationSection mSec = c.createSection("mob");
					
			for (int l = 0; l <=4 ; l++) {
				pSec.set("looting." + l, 10);
				mSec.set("looting." + l, 5);
			}
			
			saveConfig();
		}
	}
	
	@EventHandler
	public void playerKillEntity(EntityDeathEvent event) {
		
		//First check for vanilla head drop.
		for (ItemStack item : event.getDrops())
			if (item.getType() == Material.SKULL_ITEM)
				return;
		
		//Get the target and the last damaged caused.
		Entity target = event.getEntity();
		EntityDamageEvent cause = target.getLastDamageCause();
		if (!(cause instanceof EntityDamageByEntityEvent))
			return;
		
		//Determine who the attacker was and that they are a player.
		EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent) cause;
		Entity damager = damage.getDamager();
		if (!(damager instanceof Player))
			return;
		
		Player attacker = (Player) damager;
		
		//Check for the looting level on the attacking weapon.
		int looting = 0;
		ItemStack weapon = attacker.getItemInHand();
		if (weapon != null && weapon.getType() != Material.AIR)
			looting = weapon.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
		
		//Get the chance of beheading and then exit out based on random chance.
		int req = 100;
		if (target instanceof Player)
			req = getConfig().getInt("player.looting." + looting);
		else
			req = getConfig().getInt("mob.looting." + looting);
		
		if (random.nextInt(100) <= (100 - req))
			return;
		
		//Generate the skull to drop.
		ItemStack skull = new ItemStack(Material.SKULL_ITEM);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		
		if (target instanceof Player) {
			Player ply = (Player) target;
			meta.setOwner(ply.getName());
			
			String m = getConfig().getString("message");
			m = m.replaceAll("%1", attacker.getDisplayName());
			m = m.replaceAll("%2", ply.getDisplayName());
			Bukkit.broadcastMessage(ChatColor.WHITE + m);
		} else if (target instanceof Blaze) {
			meta.setOwner("MHF_Blaze");
		} else if (target instanceof CaveSpider) {
			meta.setOwner("MHF_CaveSpider");
		} else if (target instanceof Chicken) {
			meta.setOwner("MHF_Chicken");
		} else if (target instanceof Cow) {
			meta.setOwner("MHF_Cow");
		} else if (target instanceof Enderman) {
			meta.setOwner("MHF_Enderman");
		} else if (target instanceof Ghast) {
			meta.setOwner("MHF_Ghast");
		} else if (target instanceof MushroomCow) {
			meta.setOwner("MHF_MushrromCow");
		} else if (target instanceof Pig) {
			meta.setOwner("MHF_Pig");
		} else if (target instanceof PigZombie) {
			meta.setOwner("MHF_PigZombie");
		} else if (target instanceof Sheep) {
			meta.setOwner("MHF_Sheep");
		} else if (target instanceof Slime) {
			meta.setOwner("MHF_Slime");
		} else if (target instanceof Spider) {
			meta.setOwner("MHF_Spider");
		} else if (target instanceof Squid) {
			meta.setOwner("MHF_Squid");
		} else if (target instanceof Villager) {
			meta.setOwner("MHF_Villager");
		} else if (target instanceof Golem) {
			meta.setOwner("MHF_Golem");
		}
		
		skull.setItemMeta(meta);
		
		if (target instanceof Skeleton) {
			skull.setDurability((short) 0);
		} else if (target instanceof WitherSkeleton) {
			skull.setDurability((short) 1);
		} else if (target instanceof Creeper) {
			skull.setDurability((short) 4);
		} else if (target instanceof EnderDragon) {
			skull.setDurability((short) 5);
		} else {
			skull.setDurability((short) 3);
		}
		
		event.getDrops().add(skull);
	}
}
