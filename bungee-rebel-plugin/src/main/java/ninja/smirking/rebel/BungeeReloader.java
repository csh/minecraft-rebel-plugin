package ninja.smirking.rebel;

import java.util.logging.Level;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.zeroturnaround.javarebel.ClassEventListener;

/**
 * @author Connor Spencer Harries
 */
public enum BungeeReloader implements ClassEventListener {
    INSTANCE;

    @Override
    public void onClassEvent(int eventType, Class<?> klass) {
        if (klass.getClassLoader() == ClassLoader.getSystemClassLoader()) {
            return;
        }

        for (Plugin plugin : ProxyServer.getInstance().getPluginManager().getPlugins()) {
            if (plugin.getClass().getClassLoader() == klass.getClassLoader()) {
                disable(plugin);
                enable(plugin);
                break;
            }
        }
    }

    @Override
    public int priority() {
        return 0;
    }

    private void disable(Plugin plugin) {
        try {
            ProxyServer.getInstance().getLogger().log(Level.INFO, "Disabling plugin {0}", new Object[] {
                    plugin.getDescription().getName()
            });
            plugin.onDisable();
        } catch (Throwable error) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Exception disabling plugin {0}: {1}", new Object[] {
                    plugin.getDescription().getName(), error
            });
        }
        ProxyServer.getInstance().getScheduler().cancel(plugin);
    }

    private void enable(Plugin plugin) {
        try {
            plugin.onEnable();
            ProxyServer.getInstance().getLogger().log(Level.INFO, "Enabled plugin {0} version {1} by {2}", new Object[] {
                    plugin.getDescription().getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAuthor()
            });
        } catch (Throwable error) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Exception encountered when enabling {0}: {1}", new Object[] {
                    plugin.getDescription().getName(), error
            });
        }
    }
}
