package com.Ozgun.korra.DarkBlast;

import com.projectkorra.projectkorra.ability.CoreAbility;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class DarkBlastListener
  implements Listener
{
  @EventHandler
  public void onSwing(PlayerToggleSneakEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    if (CoreAbility.hasAbility(event.getPlayer(), DarkBlast.class)) {
      return;
    }
    new DarkBlast(event.getPlayer());
  }
}

