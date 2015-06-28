package co.marcin.novaguilds.command;

import java.util.HashMap;
import java.util.Iterator;

import co.marcin.novaguilds.basic.NovaPlayer;
import co.marcin.novaguilds.event.GuildRemoveEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import co.marcin.novaguilds.basic.NovaGuild;
import co.marcin.novaguilds.NovaGuilds;

public class CommandAdminGuildAbandonAll implements CommandExecutor {
	private static NovaGuilds plugin;

	public CommandAdminGuildAbandonAll(NovaGuilds novaGuilds) {
		plugin = novaGuilds;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission("novaguilds.admin.guild.abandon")) {
			plugin.getMessageManager().sendMessagesMsg(sender,"chat.nopermissions");
			return true;
		}

		if(plugin.getGuildManager().getGuilds().size() == 0) {
			plugin.getMessageManager().sendMessagesMsg(sender, "chat.guild.noguilds");
			return true;
		}

		Iterator<NovaGuild> iterator = plugin.getGuildManager().getGuilds().iterator();
		while(iterator.hasNext()) {
			NovaGuild guild = iterator.next();
			//fire event
			GuildRemoveEvent guildRemoveEvent = new GuildRemoveEvent(guild);
			guildRemoveEvent.setCause(GuildRemoveEvent.AbandonCause.ADMIN_ALL);
			plugin.getServer().getPluginManager().callEvent(guildRemoveEvent);

			//if event is not cancelled
			if(!guildRemoveEvent.isCancelled()) {
				//delete guild
				plugin.getGuildManager().deleteGuild(guild);

				HashMap<String, String> vars = new HashMap<>();
				vars.put("PLAYERNAME", sender.getName());
				vars.put("GUILDNAME", guild.getName());
				plugin.getMessageManager().broadcastMessage("broadcast.admin.guild.abandon", vars);
			}
		}
		return true;
	}

}