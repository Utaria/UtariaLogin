package fr.utaria.utarialogin.listeners;

import fr.utaria.utarialogin.UtariaLogin;
import fr.utaria.utarialogin.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffectType;

public class JoinListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		// On prépare le joueur pour éviter les problèmes
		Utils.preparePlayer(player);

		// On indique au gestionnaire de logins que le joueur s'est connecté
		UtariaLogin.getLoginManager().newPlayerJoin(player);

		// En asynchrone, on lance le processus de connexion
		UtariaLogin.getLoginManager().tryToLogPlayer(player);
	}

}