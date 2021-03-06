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

import org.zeroturnaround.javarebel.ClassResourceSource;
import org.zeroturnaround.javarebel.LoggerFactory;
import org.zeroturnaround.javarebel.Plugin;
import org.zeroturnaround.javarebel.ReloaderFactory;

/**
 * JRebel plugin used to reload BungeeCord plugins as they're developed.
 *
 * @author Connor Spencer Harries
 */
public final class BukkitPlugin implements Plugin {
    private boolean isBukkitLoaded;
    private boolean initialCheck;

    @Override
    public void preinit() {
        ReloaderFactory.getInstance().addClassReloadListener(BukkitReloader.INSTANCE);
        LoggerFactory.getInstance().infoEcho("Plugins will be reloaded when class changes are detected.");
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
