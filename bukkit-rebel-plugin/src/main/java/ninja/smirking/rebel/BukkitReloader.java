package ninja.smirking.rebel;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.zeroturnaround.javarebel.ClassEventListener;

/**
 * Attempts to disable and re-enable a {@link Plugin} when one of it's classes are reloaded.
 *
 * @author Connor Spencer Harries
 */
public enum BukkitReloader implements ClassEventListener {
    INSTANCE;

    @Override
    public void onClassEvent(int eventType, Class<?> klass) {
        try {
            Plugin plugin = JavaPlugin.getProvidingPlugin(klass);
            plugin.getLogger().log(Level.INFO, "Plugin is being reloaded by bukkit-rebel-plugin");
            Bukkit.getPluginManager().disablePlugin(plugin);
            Bukkit.getPluginManager().enablePlugin(plugin);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            // ignore
        }
    }

    @Override
    public int priority() {
        return 0;
    }
}
