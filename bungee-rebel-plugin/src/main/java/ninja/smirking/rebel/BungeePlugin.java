package ninja.smirking.rebel;

import org.zeroturnaround.javarebel.ClassResourceSource;
import org.zeroturnaround.javarebel.LoggerFactory;
import org.zeroturnaround.javarebel.Plugin;
import org.zeroturnaround.javarebel.ReloaderFactory;

/**
 * @author Connor Spencer Harries
 */
public class BungeePlugin implements Plugin {
    private boolean isBungeeLoaded;
    private boolean initialCheck;

    @Override
    public void preinit() {
        ReloaderFactory.getInstance().addClassReloadListener(BungeeReloader.INSTANCE);
        LoggerFactory.getInstance().echo("Plugins will be reloaded when class changes are detected.");
    }

    @Override
    public boolean checkDependencies(ClassLoader cl, ClassResourceSource crs) {
        if (!initialCheck) {
            initialCheck = true;
            isBungeeLoaded = crs.getClassResource("net.md_5.bungee.api.ProxyServer") != null;
            if (!isBungeeLoaded) {
                LoggerFactory.getInstance().echo("Could not find ProxyServer in your classpath!");
            }
        }
        return isBungeeLoaded;
    }

    @Override
    public String getId() {
        return "bungee";
    }

    @Override
    public String getName() {
        return "BungeeCord";
    }

    @Override
    public String getDescription() {
        return "Automatically reload BungeeCord plugins when class changes are detected.";
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
