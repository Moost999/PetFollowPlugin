package org.moostdev.petFollowPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PetFollowPlugin extends JavaPlugin {

    private Map<UUID, Entity> playersPet = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("PetFollowPlugin is Enabled");
        Objects.requireNonNull(getCommand("pet")).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(!(sender instanceof Player player)){
            sender.sendMessage("Only Players Can use This command");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("pet")) {
            if (args.length < 2) {
                player.sendMessage("§eUsage: /pet <type> <color>");
                return true;
            }

        String petType = args[0].toUpperCase();
        ChatColor color = getColor(args[1]);

        if(color == null){
            player.sendMessage("§cInvalid color! Try: RED, BLUE, GREEN, YELLOW.");
            return true;
        }

        EntityType entityType;
        try {
            entityType = EntityType.valueOf(petType);
            if(!isAllowedPet(entityType)) throw new IllegalArgumentException();
            } catch(IllegalArgumentException e){
                player.sendMessage("Invalid Pet Argument, Try Fox, Wolf, Cat, Animals.");
                return true;
            }
        spawnPet(player, entityType, color);
        player.sendMessage("§aYour " + petType.toLowerCase() + " has been summoned!");
        return true;
        }
        return false;
    }

    public boolean isAllowedPet(EntityType type){
        return switch(type){
            case WOLF, CAT, FOX, PARROT, CAMEL -> true;
            default -> false;
        };
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void spawnPet(Player player, EntityType type, ChatColor nameColor){
        if(playersPet.containsKey(player.getUniqueId())){
            playersPet.get(player.getUniqueId()).remove();
        }
        Entity pet = player.getWorld().spawnEntity(player.getLocation(), type);
        if(pet instanceof Tameable tameable){
            tameable.setOwner(player);
        }

        pet.setCustomName(nameColor + player.getName() + "'Pet");
        pet.setCustomNameVisible(true);
        playersPet.put(player.getUniqueId(), pet);
    }

    private ChatColor getColor(String color){
        return switch(color.toUpperCase()){
            case "RED" -> ChatColor.RED;
            case "BLUE" -> ChatColor.BLUE;
            case "GREEN" -> ChatColor.GREEN;
            case "YELLOW" -> ChatColor.YELLOW;
            case "AQUA" -> ChatColor.AQUA;
            default -> null;
        };
    }
}
