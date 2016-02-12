minecraft-rebel-plugin
======================

A [JRebel] plugin for automatically reloading Bukkit and BungeeCord plugins as their classes are recompile.

### Usage

Setup the JRebel agent as you normally would. If you do not know how to do this please refer to
[the official guide](http://manuals.zeroturnaround.com/jrebel/). Support will not be provided for anything other than this plugin.

Setting the plugin up is simple, simply place the JAR for the plugin you are using in a location
you will remember and update your startup arguments.

#### BungeeCord

```bash
-Drebel.plugins=path/to/bungee-rebel-plugin.jar -Drebel.bungee=true
```

#### Bukkit
```bash
-Drebel.plugins=path/to/bukkit-rebel-plugin.jar -Drebel.bukkit=true
```

If done correctly you should see the following message appear in stdout, though obviously the timestamp will be different.
```
2016-02-12 13:21:02 JRebel: Plugins will be reloaded when class changes are detected.
```

Now you simply compile classes like normal and wait for JRebel to reload them, when a reload occurs
then the plugin you installed will kick in and attempt to reload any Bukkit or Bungee plugins that use
the reloaded class.

[jrebel]: https://zeroturnaround.com/software/jrebel/