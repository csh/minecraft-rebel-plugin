package ninja.smirking.rebel;

import org.zeroturnaround.javarebel.ClassResourceSource;
import org.zeroturnaround.javarebel.LoggerFactory;
import org.zeroturnaround.javarebel.Plugin;
import org.zeroturnaround.javarebel.ReloaderFactory;

/**
 * @author Connor Spencer Harries
 */
public final class BukkitPlugin implements Plugin {
    private boolean isBukkitLoaded;
    private boolean initialCheck;

    @Override
    public void preinit() {
        ReloaderFactory.getInstance().addClassReloadListener(BukkitReloader.INSTANCE);
        LoggerFactory.getInstance().echo("Plugins will be reloaded when class changes are detected.");
    }

    @Override
    public boolean checkDependencies(ClassLoader cl, ClassResourceSource crs) {
        if (!initialCheck) {
            initialCheck = true;
            isBukkitLoaded = crs.getClassResource("org.bukkit.Bukkit") != null;
            if (!isBukkitLoaded) {
                LoggerFactory.getInstance().echo("Could not find Bukkit in your classpath!");
            }
        }
        return isBukkitLoaded;
    }

    @Override
    public String getId() {
        return "bukkit";
    }

    @Override
    public String getName() {
        return "Bukkit";
    }

    @Override
    public String getDescription() {
        return "Automatically reload Bukkit plugins when class changes are detected.";
    }

    @Override
    public String getAuthor() {
        return "Connor Spencer Harries";
    }

    @Override
    public String getWebsite() {
        return "https://fireflies.github.io/minecraft-rebel-plugin";
    }

    @Override
    public String getSupportedVersions() {
        return null;
    }

    @Override
    public String getTestedVersions() {
        return null;
    }
}
