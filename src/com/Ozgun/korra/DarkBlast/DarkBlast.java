package com.Ozgun.korra.DarkBlast;
import me.xnuminousx.spirits.Methods;
import me.xnuminousx.spirits.Methods.SpiritType;
import me.xnuminousx.spirits.ability.api.DarkAbility;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.Particle.DustOptions;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.DamageHandler;

public class DarkBlast extends DarkAbility implements AddonAbility
{
  private long cooldown;
  private double range;
	private int currPoint;
  private double damage;
  private boolean isCharged;
  private boolean launched;
  private Location origin;
  private Location location;
  private Vector direction;
  private double t;
  private Permission perm;
  
  private Color red = Color.fromRGB(150, 0, 0);
  private Color darkPurple = Color.fromRGB(90, 0, 140);
  private Color black = Color.fromRGB(0, 0, 0);
  
  public DarkBlast(Player player)
  {
    super(player);
    if (!this.bPlayer.canBend(this)) {
      return;
    }
    setFields();
    start();
  }
  
  public void setFields()
  {
    this.cooldown = ConfigManager.getConfig().getLong("Abilities.Spirits.DarkSpirit.DarkBlast.Cooldown");
    this.range = ConfigManager.getConfig().getDouble("Abilities.Spirits.DarkSpirit.DarkBlast.Range");
    this.damage = ConfigManager.getConfig().getDouble("Abilities.Spirits.DarkSpirit.DarkBlast.Damage");
  }
  
  public void progress()
  {
	  
    if ((this.player.isDead()) || (!this.player.isOnline()) || GeneralMethods.isRegionProtectedFromBuild(this, player.getLocation()) ) {
      this.remove();
    	return;
    }

    if ((this.player.isSneaking()) && (!this.launched))
    {
      chargeAnimation();
    }
    else
    {
      if (!this.isCharged)
      {
        remove();
        return;
      }
      if (!this.launched)
      {
        this.bPlayer.addCooldown(this);
        this.launched = true;
      }
      blast();
      if (GeneralMethods.isSolid(location.getBlock())) {
  		remove();
  		return;
  	}
    }
  }
  
  private void blast()
  {
    this.direction = GeneralMethods.getTargetedLocation(this.player, 1).getDirection();
    Location p = this.location.add(this.direction);
    player.getWorld().spawnParticle(Particle.REDSTONE, p, 10, Math.random(), Math.random(), Math.random(), 1, new DustOptions(red, 1));
    /*ParticleEffect.RED_DUST.display((float) Math.random(), (float) Math.random(), (float) Math.random(), 0.0005f, 10,
			p, 500D);*/
    player.getWorld().spawnParticle(Particle.REDSTONE, location, 3, 0, 0, 0, 1, new DustOptions(darkPurple, 1));
    player.getWorld().spawnParticle(Particle.REDSTONE, location, 3, 0, 0, 0, 1, new DustOptions(black, 1));
    this.location.getWorld().playSound(p, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 0.01F);
    for (Entity e : GeneralMethods.getEntitiesAroundPoint(this.location, 2.5D)) {
      if (((e instanceof LivingEntity)) && (e.getEntityId() != this.player.getEntityId()))
      {
        DamageHandler.damageEntity(e, this.damage, this);
        LivingEntity le = (LivingEntity)e;
        le.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 180, 1), true);
        le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 2), true);
        le.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 160, 2), true);
        remove();
        return;
      }
    }
    if (this.origin.distance(this.location) > this.range)
    {
      remove();
      this.bPlayer.addCooldown(this);
      return;
    }
  }
  
  
    private void chargeAnimation()
  {
    this.t += 0.09817477042468103D;
    Location location = this.player.getLocation();
    if (this.t >= 5.566370614359172D)
    {
      Location loc = this.player.getLocation();
      effect(200, 0.04F, player, loc);
      if ((this.player.isSneaking()) && (!this.launched))
      {
        this.location = GeneralMethods.getTargetedLocation(this.player, 1);
        this.origin = GeneralMethods.getTargetedLocation(this.player, 1);
        this.direction = GeneralMethods.getTargetedLocation(this.player, 1).getDirection();
        this.isCharged = true;
        this.launched = false;
      }
      if (this.isCharged) {
    	  effect(200, 0.04F, player, player.getLocation().clone());
        location.getWorld().playSound(location, Sound.ENTITY_ENDERMITE_STEP, 0.2F, 1.0F);
      }
    }
    else
    {
      for (double phi = 0.0D; phi <= 6.283185307179586D; phi += 1.0943951023931953D)
      {
        double x = 0.5D * (5.566370614359172D - this.t) * Math.cos(this.t + phi);
        double y = 0.5D * (8.866370614359172D - this.t);
        double z = 0.5D * (5.566370614359172D - this.t) * Math.sin(this.t + phi);
        location.add(x, y, z);
        player.getWorld().spawnParticle(Particle.REDSTONE, location, 3, 0, 0, 0, 1, new DustOptions(darkPurple, 1));
        player.getWorld().spawnParticle(Particle.REDSTONE, location, 3, 0, 0, 0, 1, new DustOptions(black, 1));
        location.getWorld().playSound(location, Sound.ENTITY_ENDERMITE_STEP, 0.2F, 1.0F);
        location.subtract(x, y, z);
      }
    }
  }
    public void effect(int points, float size, Entity target, Location location) {
		
		for (int i = 0; i < 6; i++) {
			currPoint += 360 / points;
			if (currPoint > 360) {
				currPoint = 0;
			}
			double angle = currPoint * Math.PI / 180 * Math.cos(Math.PI);
			double x = size * (Math.PI * 4 - angle) * Math.cos(angle + i);
            double y = 1.2 * Math.cos(angle) + 1.2;
            double z = size * (Math.PI * 4 - angle) * Math.sin(angle + i);
			location.add(x, y, z);
			player.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 1, new DustOptions(darkPurple, 1));
		    player.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 1, new DustOptions(black, 1));
			location.subtract(x, y, z);
		}
		}



public long getCooldown()
  {
    return this.cooldown;
  }
  
  public Location getLocation()
  {
    return null;
  }
  
  public String getName()
  {
    return "DarkBlast";
  }
  
  public String getDescription()
  {
    return Methods.setSpiritDescription(SpiritType.DARK, "Offense")+ Methods.setSpiritDescriptionColor(SpiritType.DARK) + " \u00A74"+  "Gather the dark energy around you and throw it onto your oppenent!";
  }
  
  public String getInstructions()
  {
    return Methods.setSpiritDescriptionColor(SpiritType.DARK) + "Hold (SHIFT) until it is charged around you and then release.";
  }
  
  public boolean isHarmlessAbility()
  {
    return false;
  }
  
  public boolean isSneakAbility()
  {
    return true;
  }
  
  public String getAuthor()
  {
    return "Ozgun_";
  }
  
  public String getVersion()
  {
    return "V-1";
  }
  
  public void load()
  {
    ConfigManager.getConfig().addDefault("Abilities.Spirits.DarkSpirit.DarkBlast.Cooldown", Integer.valueOf(8000));
    ConfigManager.getConfig().addDefault("Abilities.Spirits.DarkSpirit.DarkBlast.Range", Integer.valueOf(30));
    ConfigManager.getConfig().addDefault("Abilities.Spirits.DarkSpirit.DarkBlast.Damage", Integer.valueOf(3));
    
    ProjectKorra.plugin.getServer().getScheduler().runTaskLater(ProjectKorra.plugin, new Runnable()
    {
      public void run()
      {
        ConfigManager.defaultConfig.save();
      }
    }, 20L);
    ProjectKorra.plugin.getServer().getLogger().info(getName() + " " + getVersion() + " Made by " + getAuthor() + " has been enabled now!");
    this.perm = new Permission("bending.ability.darkblast");
    ProjectKorra.plugin.getServer().getPluginManager().addPermission(this.perm);
    this.perm.setDefault(PermissionDefault.TRUE);
    ProjectKorra.plugin.getServer().getPluginManager().registerEvents(new DarkBlastListener(), 
      ProjectKorra.plugin);
  }
  
  public void stop()
  {
    ProjectKorra.plugin.getServer().getLogger().info(getName() + " " + getVersion() + " Made by " + getAuthor() + " has been disabled now!");
    ProjectKorra.plugin.getServer().getPluginManager().removePermission(this.perm);
    super.remove();
  }

@Override
public boolean isExplosiveAbility() {

	return false;
}

@Override
public boolean isIgniteAbility() {

	return false;
}
}