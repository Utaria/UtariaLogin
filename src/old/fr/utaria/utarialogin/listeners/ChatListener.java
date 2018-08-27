package fr.utaria.utarialogin.listeners;

import fr.utaria.utarialogin.UtariaLogin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();

		// Le joueur ne peut pas intéragir avec les autres ...
		e.setCancelled(true);

		// ... mais on regarde quand même ce qu'il écrit pour
		// obtenir le mot de passe.
		UtariaLogin.getLoginManager().checkPassword(player, e.getMessage());
	}

}
