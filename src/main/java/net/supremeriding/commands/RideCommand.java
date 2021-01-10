package net.supremeriding.commands;

import com.google.common.base.Enums;
import net.supremeriding.SupremeRiding;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class RideCommand implements CommandExecutor {

    private final SupremeRiding plugin;
    private Entity entity;
    private final List<Entity> entites = new ArrayList<>();
    private Player ridedPlayer;
    private final ArrayList<Entity> ridenEntities = new ArrayList<>();
    private final ArrayList<Entity> entitiesSpawned = new ArrayList<>();

    public RideCommand(SupremeRiding plugin) {
        this.plugin = plugin;
        plugin.getCommand("ride").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        if (!player.hasPermission("customride.ride")) {
            player.sendMessage("§cYou don't have permissions to access to this command!");
            return true;
        }
        if (args.length != 1) {
            player.sendMessage("§cPlease use §a/ride <player/entity>");
            return true;
        }
        ridedPlayer = Bukkit.getPlayer(args[0]);
        if (ridedPlayer != null) {
            if (ridedPlayer == player) {
                player.sendMessage("§cYou can't ride yourself!");
                return true;
            }
            ridedPlayer.addPassenger(player);
            ridedPlayer.setGlowing(true);
            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                if (ridedPlayer.getPassengers().isEmpty()) {
                    ridedPlayer.setGlowing(false);
                }
            }, 0, 4L);
            return true;
        }
        EntityType entityType = Enums.getIfPresent(EntityType.class, args[0].toUpperCase()).orNull();
        if (entityType == null) {
            player.sendMessage("§cPlease use §a/ride <player/entity>");
            return true;
        }
        if (entityType == EntityType.ENDER_DRAGON || entityType == EntityType.WITHER) {
            player.sendMessage("§cYou can't spawn an enderdragon or wither!");
            return true;
        }
        entity = player.getWorld().spawnEntity(player.getLocation(), entityType);
        entity.addPassenger(player);
        entity.setInvulnerable(true);
        entites.add(entity);
        entity.setGlowing(true);
        entitiesSpawned.add(entity);

        if (entitiesSpawned.size() > 1) {
            entitiesSpawned.get(0).remove();
            entitiesSpawned.remove(0);
        } else {
            player.sendMessage("§cPlease use §a/ride <player/entity>");
            return true;
        }

        ridenEntities.add(entity);
        Bukkit.getScheduler().runTaskTimer(plugin, new Consumer<BukkitTask>() {
            @Override
            public void accept(BukkitTask task) {
                entity.setRotation(player.getLocation().getYaw(), player.getLocation().getPitch());

                if (!entity.getPassengers().isEmpty()) return;
                ridenEntities.get(0).remove();
                ridenEntities.remove(0);
                task.cancel();
            }
        }, 0, 3L);
        return true;
    }
}