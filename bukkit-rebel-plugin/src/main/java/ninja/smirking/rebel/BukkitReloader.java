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
import java.util.logging.Level;

import ninja.smirking.rebel.bukkit.DummyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.zeroturnaround.javarebel.ClassEventListener;
import org.zeroturnaround.javarebel.LoggerFactory;
import org.zeroturnaround.javarebel.Notifier;
import org.zeroturnaround.javarebel.NotifierFactory;

/**
 * Attempts to disable and re-enable a {@link Plugin} when one of it's classes are reloaded.
 *
 * @author Connor Spencer Harries
 */
enum BukkitReloader implements ClassEventListener {
    INSTANCE;

    private static final boolean useNotifierFactory;

    static {
        boolean notifierEnabled = Boolean.parseBoolean(System.getProperty("minecraft.notify-ide", "true"));
        if (notifierEnabled) {
            try {
                Class.forName("org.zeroturnaround.javarebel.NotifierFactory");
                LoggerFactory.getInstance().infoEcho("IDE notifications have been enabled!");
            } catch (ClassNotFoundException ex) {
                notifierEnabled = false;
            }
        }
        useNotifierFactory = notifierEnabled;
    }

    private final Set<Plugin> reloading = Sets.newSetFromMap(new MapMaker().weakKeys().makeMap());

    @Override
    public void onClassEvent(int eventType, Class<?> klass) {
        if (ClassLoader.getSystemClassLoader() == klass.getClassLoader()) {
            return;
        }

        try {
            Plugin plugin = JavaPlugin.getProvidingPlugin(klass);
            if (reloading.add(plugin)) {
                Bukkit.getScheduler().runTaskLater(DummyPlugin.INSTANCE, () -> {
                    if (reloading.remove(plugin)) {
                        DummyPlugin.INSTANCE.getLogger().log(Level.INFO, "[MinecraftRebel] Reloading \"{0}\"", plugin.getName());
                        Bukkit.getPluginManager().disablePlugin(plugin);
                        Bukkit.getPluginManager().enablePlugin(plugin);

                        if (useNotifierFactory) {
                            NotifierFactory.getInstance().notify("Minecraft Rebel", String.format("%s was reloaded!", plugin.getName()), Notifier.IDENotificationLevel.INFO, Notifier.IDENotificationType.RELOAD);
                        }
                    }
                }, 60L);
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            // ignore
        }
    }

    @Override
    public int priority() {
        return 0;
    }
}
