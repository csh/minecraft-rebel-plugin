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

import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import ninja.smirking.rebel.bungee.DummyPlugin;
import org.zeroturnaround.javarebel.ClassEventListener;
import org.zeroturnaround.javarebel.Notifier;
import org.zeroturnaround.javarebel.NotifierFactory;

/**
 * Attempts to disable and re-enable a {@link Plugin} when one of it's classes are reloaded.
 *
 * @author Connor Spencer Harries
 */
enum BungeeReloader implements ClassEventListener {
    INSTANCE;

    private final Set<Plugin> reloading = Sets.newSetFromMap(new MapMaker().weakKeys().makeMap());

    @Override
    public void onClassEvent(int eventType, Class<?> klass) {
        if (klass.getClassLoader() == ClassLoader.getSystemClassLoader()) {
            return;
        }

        for (Plugin plugin : ProxyServer.getInstance().getPluginManager().getPlugins()) {
            if (plugin.getClass().getClassLoader() == klass.getClassLoader()) {
                if (reloading.add(plugin)) {
                    ProxyServer.getInstance().getScheduler().schedule(DummyPlugin.getInstance(), () -> {
                        if (reloading.remove(plugin)) {
                            disable(plugin);
                            enable(plugin);

                            NotifierFactory.getInstance().notify("Minecraft Rebel", String.format("%s was reloaded", plugin.getDescription().getName()), Notifier.IDENotificationLevel.INFO, Notifier.IDENotificationType.RELOAD);
                        }
                    }, 3L, TimeUnit.SECONDS);
                }
                break;
            }
        }
    }

    @Override
    public int priority() {
        return 0;
    }

    private void disable(Plugin plugin) {
        StatefulPlugin statefulPlugin = StatefulPlugin.class.cast(plugin);
        if (statefulPlugin._rebel_isEnabled()) {
            try {
                ProxyServer.getInstance().getPluginManager().unregisterListeners(plugin);
                ProxyServer.getInstance().getPluginManager().unregisterCommands(plugin);
                ProxyServer.getInstance().getScheduler().cancel(plugin);

                ProxyServer.getInstance().getLogger().log(Level.INFO, "Disabling plugin {0}", new Object[]{
                        plugin.getDescription().getName()
                });
                plugin.onDisable();
            } catch (Throwable error) {
                ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Exception disabling plugin {0}: {1}", new Object[]{
                        plugin.getDescription().getName(), error
                });
            }
        }
    }

    private void enable(Plugin plugin) {
        StatefulPlugin statefulPlugin = StatefulPlugin.class.cast(plugin);
        if (!statefulPlugin._rebel_isEnabled()) {
            try {
                plugin.onEnable();
                ProxyServer.getInstance().getLogger().log(Level.INFO, "Enabled plugin {0} version {1} by {2}", new Object[]{
                        plugin.getDescription().getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAuthor()
                });
            } catch (Throwable error) {
                ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Exception encountered when enabling {0}: {1}", new Object[]{
                        plugin.getDescription().getName(), error
                });
            }
        }
    }
}
