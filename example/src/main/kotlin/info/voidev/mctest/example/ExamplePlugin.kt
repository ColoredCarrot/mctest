package info.voidev.mctest.example

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class ExamplePlugin : JavaPlugin() {

    override fun onEnable() {
        logger.info("Enabled Example v${description.version}")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (command.name != "forward") {
            return false
        }

        val distance = if (args.isEmpty()) {
            1.0
        } else {
            val d = args[0].toDoubleOrNull()
            if (d == null) {
                sender.sendMessage("${ChatColor.RED}Not a valid number")
                return true
            }
            d
        }

        val player = when {
            args.size < 2 && sender is Player ->
                sender

            args.size < 2 -> {
                sender.sendMessage("${ChatColor.RED}Please specify a target player")
                return true
            }

            else -> {
                val p = Bukkit.getPlayer(args[1])
                if (p == null) {
                    sender.sendMessage("${ChatColor.RED}Player not found")
                    return true
                }
                p
            }
        }

        player.teleport(player.location.add(player.facing.direction.multiply(distance)))

        return true
    }

}
