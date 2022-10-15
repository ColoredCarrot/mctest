package info.voidev.mctest.engine.server

import java.net.URI

object ServerJarGetter {

    fun get(): URI {
        //TODO Make this configurable (e.g. in @MCTest annotation, specify platform (Bukkit/Spigot) and version range, or infer from plugin.yml in classpath)
        return URI("https://download.getbukkit.org/spigot/spigot-1.18.2.jar")
    }

}
