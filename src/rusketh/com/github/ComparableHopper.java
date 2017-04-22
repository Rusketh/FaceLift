package rusketh.com.github;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftBrewingStand;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftChest;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftDispenser;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftDropper;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftFurnace;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftHopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.md_5.bungee.api.ChatColor;

public class ComparableHopper extends JavaPlugin implements Listener {
	
	public String getVer() {
		return "0.0.2";
	}
	
	public void onEnable() {
		addRecipe();
		new TimedTask(this);
		Logger.getAnonymousLogger().info("Installed Comparable Hopper by Rusketh");
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	/*
	 * Saving Hopper Info
	 */
	
	private ConfigurationSection getChunkData(Chunk c, boolean create) {
		FileConfiguration cgf = getConfig();

		ConfigurationSection wcs = null;
		
		String world = c.getWorld().getName();
		
		if (cgf.isConfigurationSection(world))
			wcs = cgf.getConfigurationSection(world);
		else if (create)
			wcs = cgf.createSection(world);
		else
			return null;
		
		String key = "chunk_" + c.getX() + "_" + c.getZ();
		
		if (wcs.isConfigurationSection(key))
			return wcs.getConfigurationSection(key);
		
		if (create)
			return wcs.createSection(key);
		
		return null;
	}
	
	private boolean getBlock(Block b) {
		ConfigurationSection c = getChunkData(b.getChunk(), false);
		
		if (c == null)
			return false;
		
		String key = "block_" + b.getX() + "_" + b.getY() + "_" + b.getZ();
		
		if (!c.isConfigurationSection(key))
			return false;
		
		ConfigurationSection r = c.getConfigurationSection(key);
		return r != null && r.getBoolean("comparable");
	}
	
	private void setBlock(Block b, boolean v) {
		ConfigurationSection cgf = getChunkData(b.getChunk(), v);
		
		if (cgf != null) {
			String key = "block_" + b.getX() + "_" + b.getY() + "_" + b.getZ();
			
			if (v) {
				ConfigurationSection r = cgf.isConfigurationSection(key) ? cgf.getConfigurationSection(key) : cgf.createSection(key);
				r.set("x", b.getX());
				r.set("y", b.getY());
				r.set("z", b.getZ());
				r.set("comparable", v);
			} else {
				cgf.set(key, null);
			}
			
			saveConfig();
		}
	}
	
	/*
	 * Recipe
	 */
	
	public void addRecipe() {
		CustomItemStack result = new CustomItemStack(Material.HOPPER, 1);
		
		result.setDisplayName("Comparable Hopper");
		
		result.setNBTBool("comparable", true);
		
		ShapedRecipe recipe = new ShapedRecipe(result.item);
		
		recipe.shape("rrr", "cht", "rrr");
		
		recipe.setIngredient('r', Material.REDSTONE);
		
		recipe.setIngredient('c', Material.REDSTONE_COMPARATOR);
		
		recipe.setIngredient('h', Material.HOPPER);
		
		recipe.setIngredient('t', Material.REDSTONE_TORCH_ON);
		
		getServer().addRecipe(recipe);
	}
	
	@EventHandler
	public void PrepareCrafting(PrepareItemCraftEvent event) {
		CraftingInventory inventory = event.getInventory();
		
		ItemStack origonal = inventory.getResult();
		
		if (origonal == null || origonal.getType() != Material.HOPPER)
			return;
		
		CustomItemStack item = new CustomItemStack(origonal);
		
		if (item != null && !item.getNBTBool("comparable", false)) {
			inventory.setResult(new ItemStack(Material.AIR));
			return;
		}
	}
	
	@EventHandler
	public void CraftItemEvent(CraftItemEvent event) {
		CraftingInventory inventory = event.getInventory();
		
		ItemStack origonal = inventory.getResult();
		
		if (origonal == null || origonal.getType() != Material.HOPPER)
			return;
		
		CustomItemStack item = new CustomItemStack(origonal);
		
		if (item != null && !item.getNBTBool("comparable", false)) {
			inventory.setResult(new ItemStack(Material.AIR));
			event.setCancelled(true);
			return;
		}
		
		if (!(event.getWhoClicked().hasPermission("rusketh.craft.comparable_hopper") || event.getWhoClicked().isOp())) {
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ChatColor.RED + "You do not have permission to craft this.");
			item.setDisplayLore(lore);
			event.setCancelled(true);
			return;
		}
	}
	
	/*
	 * Filtering
	 */
	
	public boolean canInsert(Block dest, ItemStack item) {
		BlockState state = dest.getState();
		InventoryHolder holder = (InventoryHolder) state;
		Inventory inv = holder.getInventory();
		
		for (int s = 0; s < inv.getSize(); s++) {
			ItemStack i = inv.getItem(s);
			
			if (i != null)
				if (i.getType() == item.getType())
					if (i.getDurability() == item.getDurability())
						return true;
		}
		
		return false;
	}
	
	public boolean pipeItem(Block dest, ItemStack item) {
		boolean allowed = canInsert(dest, item);
		return allowed;
	}
	
	public boolean canEject(Block source, ItemStack item) {
		BlockState state = source.getState();
		InventoryHolder holder = (InventoryHolder) state;
		Inventory inv = holder.getInventory();
	
		int c = 0;
		
		for (int s = 0; s < inv.getSize(); s++) {
			ItemStack i = inv.getItem(s);
			
			if (i != null)
				if (i.getType() == item.getType())
					if (i.getDurability() == item.getDurability())
						c += i.getAmount();
		}
		
		return c > 0;
	}
	
	public boolean pushItem(Block source, ItemStack item) {
		boolean allowed = canEject(source, item);
		return allowed;
	}
	
	/*
	 * Item Movement
	 */
	
	public boolean isComparable(Block b) {
		if (b == null || b.getType() != Material.HOPPER)
			return false;
		
		return getBlock(b);
	}
	
	public Block getBlockFromInventory(Inventory inventory) {
		InventoryHolder holder = inventory.getHolder();

		if (holder instanceof CraftFurnace)
			return ((CraftFurnace) holder).getBlock();
		
		if (holder instanceof CraftBrewingStand)
			return ((CraftBrewingStand) holder).getBlock();
		
		if (holder instanceof CraftHopper)
			return ((CraftHopper) holder).getBlock();
		
		if (holder instanceof CraftChest)
			return ((CraftChest) holder).getBlock();
		
		if (holder instanceof CraftDispenser)
			return ((CraftDispenser) holder).getBlock();
		
		if (holder instanceof CraftDropper)
			return ((CraftDropper) holder).getBlock();
		
		return null;
	}
	
	@EventHandler
	public void InventoryMoveItemEvent(InventoryMoveItemEvent event) {
		Block source = getBlockFromInventory(event.getSource());
		Block dest = getBlockFromInventory(event.getDestination());
		
		if (isComparable(source) && !pushItem(source, event.getItem())) {
			event.setCancelled(true);
			return;
		}
		
		if (isComparable(dest) && !pipeItem(dest, event.getItem())) {
			event.setCancelled(true);
			return;
		}
	}
	
	/*
	 * Placement
	 */
	
	@EventHandler
	public void BlockPlaceEvent(BlockPlaceEvent event) {
		ItemStack origonal = event.getItemInHand();
		
		if (origonal == null || origonal.getType() != Material.HOPPER)
			return;

		CustomItemStack item = new CustomItemStack(origonal);
		
		if (!item.getNBTBool("comparable", false))
			return;
		
		setBlock(event.getBlock(), true);
		
		//Logger.getGlobal().info("Placed comparabale hopper: " + event.getBlock());
	}
	
	@EventHandler
	public void BlockBreakEvent(BlockBreakEvent event) {
		Block b = event.getBlock();
		
		if (isComparable(b)) {
			setBlock(b, false);
			
			b.setType(Material.AIR);
			
			CustomItemStack drop = new CustomItemStack(Material.HOPPER, 1);
			
			drop.setDisplayName("Comparable Hopper");
			
			drop.setNBTBool("comparable", true);
			
			b.getWorld().dropItemNaturally(b.getLocation(), drop.item);
			
			event.setCancelled(true);
		}
	}
	
	/*
	 * Timed Logic
	 */
	
	protected void update(Block b) {
		BlockState state = b.getState();
		
		Hopper hopper = (Hopper) state;
		
		Inventory inv = hopper.getInventory();
		
		if (inv.getViewers().size() > 0)
			return;
		
		ItemStack first = inv.getItem(0);
		
		if (first != null && first.getType() != Material.AIR && first.getAmount() == 1) {
			int o = 1;
			
			for (int s = 1; s < inv.getSize(); s++) {
				ItemStack i = inv.getItem(s);
				
				if (i != null) {
					inv.setItem(s - o, i);
				} else {
					inv.setItem(s - o, new ItemStack(Material.AIR));
					o++;
				}
			}
			
			inv.setItem(inv.getSize() - o, first);
		}
	}
	
	private class TimedTask extends BukkitRunnable {
		ComparableHopper plugin;
		
		public TimedTask(JavaPlugin p) {
			plugin = (ComparableHopper) p;
			Bukkit.getScheduler().runTaskTimer(p, this, 20L, 20L);
		}
		
		public void run() {
			for (World w : Bukkit.getWorlds()) {
				for (Chunk c : w.getLoadedChunks()) {
					ConfigurationSection yml = getChunkData(c, false);
					
					if (yml != null)
						for (String key : yml.getKeys(false)) {
							ConfigurationSection values = yml.getConfigurationSection(key);
							
							if (values.getBoolean("comparable"))
								plugin.update(w.getBlockAt(values.getInt("x"), values.getInt("y"), values.getInt("z")));
						}
				}
			}
		}
	}
}


