package fr.utaria.utarialogin.listeners;

import fr.utaria.utarialogin.UtariaLogin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveListener implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		// On indique au gestionnaire de logins que le joueur s'est déconnecté
		UtariaLogin.getLoginManager().newPlayerQuit(player);

		// On vide le cache du joueur en question
		UtariaLogin.getAccountManager().clearCacheFor(player);
		UtariaLogin.getLoginManager().clearLoginCacheFor(player);
	}

}
