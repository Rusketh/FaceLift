package rusketh.com.github;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_11_R1.NBTTagCompound;

public class CustomItemStack {
	
	/*
	 * Base instance
	 */
	
	protected ItemStack item = new ItemStack(Material.AIR, 1);
	
	public CustomItemStack(Material type, int amount) {
		item = new ItemStack(type, amount);
	}
	
	public CustomItemStack(ItemStack baseItem) {
		item = baseItem;
	}
	
	/*
	 * Meta Data
	 */
	
	public void setDisplayName(String name) {
		ItemMeta meta = item.getItemMeta();
		
		if (meta == null)
			return;
		
		meta.setDisplayName(name);
		
		item.setItemMeta(meta);
	}
	
	public String getDisplayName() {
		ItemMeta meta = item.getItemMeta();
			
		if (meta == null)
			return "";
			
		return meta.getDisplayName();
	}
	
	public void clearDisplayLore() {
		ItemMeta meta = item.getItemMeta();
		
		if (meta == null)
			return;
		
		List<String> lore = meta.getLore();
		if (lore != null) lore.clear();
		
		meta.setLore(lore);
		item.setItemMeta(meta);
	}
	
	public void addDisplayLore(String line) {
		ItemMeta meta = item.getItemMeta();
		
		if (meta == null)
			return;
		
		List<String> lore = meta.getLore();
		
		if (lore == null)
			lore = new ArrayList<String>();
		
		lore.add(line);
		meta.setLore(lore);
		item.setItemMeta(meta);
	}
	
	public List<String> getDisplayLore() {
		ItemMeta meta = item.getItemMeta();
		
		if (meta == null)
			return new ArrayList<String>();
		
		return meta.getLore();
	}
	
	public void setDisplayLore(List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		
		if (meta == null)
			return;
		
		meta.setLore(lore);
		
		item.setItemMeta(meta);
	}
	
	/*
	 * NBT
	 */
	
	public NBTTagCompound getNBTCompound() {
		net.minecraft.server.v1_11_R1.ItemStack stack = CraftItemStack.asNMSCopy(item);
        
        NBTTagCompound nbt = stack.getTag();
			
		if (nbt == null) {
			nbt = new NBTTagCompound();
			
			stack.setTag(nbt);
		}
        	
        return nbt;
	}
	
	public ItemStack setNBTCompound(NBTTagCompound nbt) {
		net.minecraft.server.v1_11_R1.ItemStack stack = CraftItemStack.asNMSCopy(item);

		stack.setTag(nbt);
		
		stack.save(nbt);
		
		item = CraftItemStack.asBukkitCopy(stack);
		
		return item;
	}

	public void removeNBT(String property) {
		NBTTagCompound nbt = getNBTCompound();
		
		nbt.remove(property);
		
		setNBTCompound(nbt);
	}
	
	public Set<String> getNBTKeys() {
		NBTTagCompound nbt = getNBTCompound();
		
		return nbt.c();
	}
	
	/*
	 *  GET NBT
	 */
	
	public boolean hasNBTKey(String property) {
		return getNBTCompound().hasKey(property);
	}
	
	public boolean getNBTBool(String property, boolean def) {
		if (getNBTCompound().hasKey(property))
			return getNBTCompound().getBoolean(property);
		return def;
	}
	
	public int getNBTInt(String property, int def) {
		if (getNBTCompound().hasKey(property))
			return getNBTCompound().getInt(property);
		return def;
	}
	
	public double getNBTDouble(String property, double def) {
		if (getNBTCompound().hasKey(property))
			return getNBTCompound().getDouble(property);
		return def;
	}
	
	public String getNBTString(String property, String def) {
		if (getNBTCompound().hasKey(property))
			return getNBTCompound().getString(property);
		return def;
	}
	
	public NBTTagCompound getNBTCompound(String property, NBTTagCompound def) {
		if (getNBTCompound().hasKey(property))
			return getNBTCompound().getCompound(property);
		return def;
	}
	
	/*
	 * SET NBT
	 */
	
	public void setNBTBool(String property, boolean value) {
		NBTTagCompound nbt = getNBTCompound();
		
		nbt.setBoolean(property, value);
		
		setNBTCompound(nbt);
	}
	
	public void setNBTInt(String property, int value) {
		NBTTagCompound nbt = getNBTCompound();
		
		nbt.setInt(property, value);
		
		setNBTCompound(nbt);
	}
	
	public void setNBTDouble(String property, double value) {
		NBTTagCompound nbt = getNBTCompound();
		
		nbt.setDouble(property, value);
		
		setNBTCompound(nbt);
	}
	
	public void setNBTString(String property, String value) {
		NBTTagCompound nbt = getNBTCompound();
		
		nbt.setString(property, value);
		
		setNBTCompound(nbt);
	}
	
	public void setNBTCompound(String property, NBTTagCompound value) {
		NBTTagCompound nbt = getNBTCompound();
		
		nbt.set(property, value);
		
		setNBTCompound(nbt);
	}
}