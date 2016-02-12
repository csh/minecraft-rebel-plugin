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
