package com.makotomiyamoto.redragon.listener;

import com.makotomiyamoto.redragon.REDragon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public final class DragonDeathListener implements Listener {

    private REDragon plugin;

    public DragonDeathListener(REDragon plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    @SuppressWarnings("all")
    public void onMobDeath(EntityDeathEvent event) {

        if (!(event.getEntity() instanceof EnderDragon)) {
            return;
        }

        if (!plugin.getConfig().getBoolean("default-dragon-dead")) {
            plugin.getConfig().set("default-dragon-dead", true);
            plugin.saveConfig();
            return;
        }

        Location coords = event.getEntity().getLocation();

        boolean runDropLoop = true;

        if (event.getEntity().getWorld().getBiome(0, 0).equals(Biome.THE_END)) {

            Location loc = new Location(event.getEntity().getWorld(), 0, 0, 0);

            while (loc.getBlockY() < 255) {

                if (loc.getBlock().getType().equals(Material.BEDROCK) &&
                        loc.add(0, 1, 0).getBlock().getType().equals(Material.AIR)) {
                    loc.add(0, 1, 0).getBlock().setType(Material.DRAGON_EGG);
                    coords = loc.add(0, 1, 0);
                    runDropLoop = false;
                    break;
                }

                loc.setY(loc.getBlockY() + 1);

            }

        }

        // dragon egg save added in 1.1
           // added to ensure the dragon
           // egg is safe if the ender
           // dragon is slain in the void
        if (coords.getBlockY() < 1 && runDropLoop) {

            coords.setY(0);
            coords.getBlock().setType(Material.OBSIDIAN);
            coords.add(0, 1, 0).getBlock().setType(Material.DRAGON_EGG);

        }

        while (coords.getBlockY() > 0 && runDropLoop) {

            if (coords.getBlockY() == 1) {

                coords.setY(coords.getBlockY() - 1);

                // extra block conditions added in 1.1
                   // added to ensure dragon egg does
                   // not spawn on fragile or liquid
                   // blocks to ensure it wont fall
                   // into the void <3
                if (coords.getBlock().getType().equals(Material.AIR) ||
                        coords.getBlock().getType().equals(Material.WATER) ||
                        coords.getBlock().getType().equals(Material.LAVA) ||
                        coords.getBlock().getType().equals(Material.GRAVEL) ||
                        coords.getBlock().getType().equals(Material.SAND) ||
                        coords.getBlock().getType().equals(Material.REDSTONE_WIRE) ||
                        coords.getBlock().getType().equals(Material.REDSTONE)) {

                    coords.getBlock().setType(Material.OBSIDIAN);
                    coords.add(0, 1, 0).getBlock().setType(Material.DRAGON_EGG);
                    break;

                }

            }

            if (!coords.getBlock().getType().equals(Material.AIR)) {
                coords.setY(coords.getBlockY() - 1);
                continue;
            }

            coords.setY(coords.getBlockY() - 1);
            if (coords.getBlock().getType().equals(Material.AIR)) {
                continue;
            }

            coords.add(0, 1, 0).getBlock().setType(Material.DRAGON_EGG);
            break;

        }

        if (plugin.getConfig().getString("egg-drop-message").equals("null")) {
            return;
        }

        String message = plugin.getConfig().getString("egg-drop-message");

        if (plugin.getConfig().getString("egg-drop-message").contains("%drop-location%")) {

            String locFormat = plugin.getConfig().getString("drop-location-format");
            if (locFormat == null) {
                locFormat = "&b%x%&7X, &b%y%&7Y, &b%y%&7Z";
                plugin.getConfig().set("drop-location-format", "&bX:%x%&7, &bY:%y%&7, &bZ:%y%");
                plugin.saveConfig();
            }

            locFormat = locFormat.replaceAll("%x%", String.valueOf(coords.getBlockX()));
            locFormat = locFormat.replaceAll("%y%", String.valueOf(coords.getBlockY()));
            locFormat = locFormat.replaceAll("%z%", String.valueOf(coords.getBlockZ()));

            message = message.replaceAll("%drop-location%", locFormat);

        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));

    }

}
