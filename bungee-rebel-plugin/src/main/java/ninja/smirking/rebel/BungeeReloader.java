/*
 * Copyright 2016 Connor Spencer Harries
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.smirking.rebel;

import java.util.logging.Level;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.zeroturnaround.javarebel.ClassEventListener;

/**
 * Attempts to disable and re-enable a {@link Plugin} when one of it's classes are reloaded.
 *
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
