package us.eunoians.mcmmox.gui;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import us.eunoians.mcmmox.Mcmmox;
import us.eunoians.mcmmox.api.util.FileManager;
import us.eunoians.mcmmox.players.McMMOPlayer;
import us.eunoians.mcmmox.types.DefaultAbilities;
import us.eunoians.mcmmox.types.Skills;
import us.eunoians.mcmmox.util.Parser;

import java.util.ArrayList;
import java.util.List;

public class SkillGUI extends GUI {

  private static FileManager fm = Mcmmox.getInstance().getFileManager();

  private static FileManager.Files file = FileManager.Files.SKILLS_GUI;

  private static GUIPlaceHolderFunction function = (GUIBuilder guiBuilder) -> {
	McMMOPlayer player = guiBuilder.getPlayer();
	if(guiBuilder.getRawPath().equalsIgnoreCase("SkillsGUI")){
	  for(int i = 0; i < guiBuilder.getInv().getSize(); i++){
		ItemStack item = guiBuilder.getInv().getItem(i);
		if(item.hasItemMeta() && item.getItemMeta().hasLore()){
		  ItemMeta meta = item.getItemMeta();
		  List<String> lore = new ArrayList<>();
		  for(String s : meta.getLore()){
			for(Skills skill : Skills.values()){
			  if(!skill.equals(Skills.SWORDS)){
			    continue;
			  }
			  s = s.replaceAll("%" + skill.getName() + "_Level%", Integer.toString(player.getSkill(skill).getCurrentLevel()));
			  DefaultAbilities ability = DefaultAbilities.getSkillsDefaultAbility(skill.getName());
			  Parser equation = ability.getActivationEquation();
			  equation.setVariable(skill.getName().toLowerCase() + "_level", player.getSkill(skill).getCurrentLevel());
			  equation.setVariable("power_level", player.getPowerLevel());
			  s = s.replaceAll("%" + ability.getName() + "_Chance%", Double.toString(equation.getValue()));
			}
			lore.add(s.replaceAll("%Power_Level%", Integer.toString(player.getPowerLevel()))
				.replaceAll("%Ability_Points%", Integer.toString(player.getAbilityPoints())));
			meta.setLore(lore);
			item.setItemMeta(meta);
			guiBuilder.getInv().setItem(i, item);
		  }
		}
		continue;
	  }
	}
  };

  public SkillGUI(McMMOPlayer p){
	super(new GUIBuilder("SkillsGUI", fm.getFile(file), p));
	this.getGui().setReplacePlaceHoldersFunction(function);
	this.getGui().replacePlaceHolders();
	if(!GUITracker.isPlayerTracked(p)){
	  GUITracker.trackPlayer(p, this);
	}
  }

}