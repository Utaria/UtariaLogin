package fr.utaria.utarialogin.login;

import fr.utaria.utariaapi.util.TaskUtil;
import fr.utaria.utariaapi.util.TitleUtil;
import fr.utaria.utarialogin.UtariaLogin;
import fr.utaria.utarialogin.world.WorldManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LoginListener implements Listener {

	private LoginManager manager;


	LoginListener(LoginManager manager) {
		this.manager = manager;
	}


	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		// event.setCancelled(true);

		// On supporte les commandes très connues des serveurs MC
		String message = event.getMessage();

		if (message.startsWith("/login ") || message.startsWith("/register "))
			this.manager.checkPassword(event.getPlayer(), message.replace("/login ", "").replace("/register ", ""));
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		// Le joueur ne peut pas intéragir avec les autres ...
		event.setCancelled(true);

		// ... mais on regarde quand même ce qu'il écrit pour
		// obtenir le mot de passe.
		this.manager.checkPassword(event.getPlayer(), event.getMessage());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		// On prépare le joueur pour éviter les problèmes
		// UUtil.preparePlayer(player);
		TaskUtil.runTaskLater(() -> {
			player.setGameMode(GameMode.SPECTATOR);
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));

			player.teleport(UtariaLogin.getInstance().getInstance(WorldManager.class).getRandomHub());

			TitleUtil.displayTitleToPlayer("Bienvenue sur Utaria !", "Ouvrez le tchat pour vous connecter", ChatColor.YELLOW, ChatColor.GRAY, player);
		}, 5L);

		// On indique au gestionnaire de logins que le joueur s'est connecté
		this.manager.newPlayerJoin(player);

		// En asynchrone, on lance le processus de connexion
		this.manager.tryToLogPlayer(player);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		// On indique au gestionnaire de logins que le joueur s'est déconnecté
		this.manager.newPlayerQuit(player);

		// On vide le cache du joueur en question
		UtariaLogin.getInstance().getInstance(AccountManager.class).clearCacheFor(player);
		this.manager.clearLoginCacheFor(player);
	}

}
