package net.supremeriding.commands;

import net.supremeriding.SupremeRiding;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
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
    private final ArrayList<Entity> entitiesSpawned =new ArrayList<>();

    public RideCommand(SupremeRiding plugin) {
        this.plugin = plugin;
        plugin.getCommand("ride").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;
        if(!player.hasPermission("customride.ride")) {
            player.sendMessage("§cYou don't have permissions to access to this command!");
            return true;
        }
        if (args.length != 1) return true;
        try {
            EntityType entityType = getEntityByName(args[0].toUpperCase());
            ridedPlayer = Bukkit.getPlayer(args[0]);

            if (ridedPlayer != null) {
                if (ridedPlayer == player) {
                    player.sendMessage("§cYou can't ride yourself!");
                    return true;
                }

                ridedPlayer.addPassenger(player);
                ridedPlayer.setGlowing(true);
                Bukkit.getScheduler().runTaskTimer(plugin, new Consumer<BukkitTask>() {
                    @Override
                    public void accept(BukkitTask task) {
                        if (ridedPlayer.getPassengers().isEmpty()) {
                            ridedPlayer.setGlowing(false);


                        }
                    }
                }, 0, 4L);
                return true;
            }

            entity = player.getWorld().spawnEntity(player.getLocation(), entityType);
            entity.setPassenger(player);
            entity.setInvulnerable(true);
            entites.add(entity);
            entity.setGlowing(true);
            entitiesSpawned.add(entity);

            if(entitiesSpawned.size() > 1) {
                entitiesSpawned.get(0).remove();
                entitiesSpawned.remove(0);
            }

            ridenEntities.add(entity);

            Bukkit.getScheduler().runTaskTimer(plugin, new Consumer<BukkitTask>() {
                @Override
                public void accept(BukkitTask task) {
                    entity.setRotation(player.getLocation().getYaw(), player.getLocation().getPitch());

                    if (entity.getPassengers().isEmpty()) {
                        ridenEntities.get(0).remove();
                        ridenEntities.remove(0);
                        task.cancel();
                    }
                }
            }, 0, 4L);

        } catch (NullPointerException exception) {
            player.sendMessage("§cPlease use §a/ride §a<entity/player>");
        }
        return true;

    }

    public EntityType getEntityByName(String name) {
        for (EntityType type : EntityType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
