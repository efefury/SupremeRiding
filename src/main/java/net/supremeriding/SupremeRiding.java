package net.supremeriding;

import net.supremeriding.commands.RideCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class SupremeRiding extends JavaPlugin {
     @Override
    public void onEnable() {
        new RideCommand(this);
    }
 }
