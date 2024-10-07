package io.pinger.groups.dependenies;

import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.LibraryManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DependencyManager {
    private final LibraryManager manager;

    public DependencyManager(JavaPlugin plugin) {
        this.manager = new BukkitLibraryManager(plugin);
    }

    public void loadDependencies() {
        this.manager.addMavenCentral();
        this.manager.addSonatype();

        for (final Dependency dependency : Dependencies.values()) {
            this.manager.loadLibrary(dependency.toLibrary());
        }
    }
}
