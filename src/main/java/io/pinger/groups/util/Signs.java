package io.pinger.groups.util;

import io.pinger.groups.sign.SignManager;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

public interface Signs {

    static List<Sign> getNearbySignsWithServerRadius(Location location) {
        return Signs.getNearbySigns(location, Bukkit.getViewDistance());
    }

    static List<Sign> getNearbySigns(Location location, int radius) {
        final List<Sign> signs = new ArrayList<>();
        final World world = location.getWorld();
        if (world == null) {
            return signs;
        }

        final Chunk chunk = location.getChunk();
        for (int x = chunk.getX() - radius; x <= chunk.getX() + radius; x++) {
            for (int z = chunk.getZ() - radius; z <= chunk.getZ() + radius; z++) {
                final Chunk currentChunk = world.getChunkAt(x, z);
                if (!currentChunk.isLoaded()) {
                    continue;
                }

                final BlockState[] states = currentChunk.getTileEntities();
                for (final BlockState state : states) {
                    if (!(state instanceof Sign sign)) {
                        continue;
                    }

                    signs.add(sign);
                }
            }
        }

        return signs;
    }

}
