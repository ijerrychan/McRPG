package us.eunoians.mcrpg.api.events.mcrpg.mining;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class FakeChestOpenEvent extends PlayerInteractEvent {

  public FakeChestOpenEvent(Player p, Location location){
    super(p, Action.LEFT_CLICK_BLOCK, p.getItemOnCursor(), location.getBlock(), location.getBlock().getFace(location.add(1, 0, 0).getBlock()));
  }
}
