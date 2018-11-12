package us.eunoians.mcmmox.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.eunoians.mcmmox.api.util.Methods;
import us.eunoians.mcmmox.players.McMMOPlayer;
import us.eunoians.mcmmox.types.DisplayType;
import us.eunoians.mcmmox.util.mcmmo.MobHealthbarUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsGUI extends GUI {

  private static GUIInventoryFunction function;

  public SettingsGUI(McMMOPlayer player){
    super(new GUIBuilder(player));
    function  = (GUIBuilder guiBuilder) -> {
	  Inventory inv = Bukkit.createInventory(null, 27, Methods.color("&eChange your settings"));
	  ArrayList<GUIItem> items = new ArrayList<>();
	  ItemStack displayItem = new ItemStack(Material.BLAZE_ROD);
	  ItemMeta displayMeta = displayItem.getItemMeta();
	  if(player.getDisplayType() == DisplayType.SCOREBOARD){
	    displayItem.setType(Material.SIGN);
	    displayMeta.setDisplayName(Methods.color("&bDisplay Type: &5Score Board"));
	  }
	  else if(player.getDisplayType() == DisplayType.BOSS_BAR){
		displayItem.setType(Material.DRAGON_HEAD);
		displayMeta.setDisplayName(Methods.color("&bDisplay Type: &5Boss Bar"));
	  }
	  else if(player.getDisplayType() == DisplayType.ACTION_BAR){
		displayMeta.setDisplayName(Methods.color("&bDisplay Type: &5Action Bar"));
	  }
	  displayMeta.setLore(Methods.colorLore(Arrays.asList("&eClick this to change your display type", "&eWhen using /mcdisplay {skill}")));
	  displayItem.setItemMeta(displayMeta);
	  items.add(new GUIItem(displayItem, 1));

	  ItemStack itemPickup = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
	  ItemMeta itemPickupMeta = itemPickup.getItemMeta();
	  if(player.isKeepHandEmpty()){
	    itemPickupMeta.setDisplayName(Methods.color("&aKeep Hand Empty Enabled"));
	  }
	  else{
	    itemPickup.setType(Material.RED_STAINED_GLASS_PANE);
		itemPickupMeta.setDisplayName(Methods.color("&cKeep Hand Empty Disabled"));
	  }
	  itemPickupMeta.setLore(Methods.colorLore(Arrays.asList("&eClick this to change", "&eif items should go into your empty hand")));
	  itemPickup.setItemMeta(itemPickupMeta);
	  items.add(new GUIItem(itemPickup, 3));

	  MobHealthbarUtils.MobHealthbarType healthbarType = player.getHealthbarType();
	  ItemStack healthItem = new ItemStack(Material.BUBBLE_CORAL_BLOCK);
	  ItemMeta healthMeta = healthItem.getItemMeta();
	  if(healthbarType == MobHealthbarUtils.MobHealthbarType.BAR){
	    healthMeta.setDisplayName(Methods.color("&5Mob Health Display: &3Bar"));
	  }
	  else if(healthbarType == MobHealthbarUtils.MobHealthbarType.DISABLED){
	    healthItem.setType(Material.DEAD_FIRE_CORAL_BLOCK);
		healthMeta.setDisplayName(Methods.color("&5Mob Health Display: &3Disabled"));
	  }
	  else if(healthbarType == MobHealthbarUtils.MobHealthbarType.HEARTS){
		healthItem.setType(Material.FIRE_CORAL_BLOCK);
		healthMeta.setDisplayName(Methods.color("&5Mob Health Display: &3Hearts"));
	  }
	  healthItem.setItemMeta(healthMeta);
	  items.add(new GUIItem(healthItem, 5));

	  ItemStack later = new ItemStack(Material.RED_STAINED_GLASS_PANE);
	  ItemMeta laterMeta = later.getItemMeta();
	  laterMeta.setDisplayName(Methods.color("&cThis feature will be added later"));
	  later.setItemMeta(laterMeta);
	  items.add(new GUIItem(later, 7));

	  ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
	  ItemMeta fillerMeta = filler.getItemMeta();
	  fillerMeta.setDisplayName(" ");
	  filler.setItemMeta(fillerMeta);

	  inv = Methods.fillInventory(inv, filler, items);
	  return inv;
	};
    this.getGui().setBuildGUIFunction(function);
    this.getGui().rebuildGUI();
  }
}
