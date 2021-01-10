package net.supremeriding;

import net.supremeriding.commands.RideCommand;
import net.supremeriding.commands.RideTabComplete;
import org.bukkit.plugin.java.JavaPlugin;

public final class SupremeRiding extends JavaPlugin {

     @Override
    public void onEnable() {
        new RideCommand(this);
        new RideTabComplete(this);
    }

 }
