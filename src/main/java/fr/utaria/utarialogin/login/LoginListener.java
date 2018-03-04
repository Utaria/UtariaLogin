package fr.utaria.utarialogin.login;

import fr.utaria.utariacore.util.PlayerUtil;
import fr.utaria.utariacore.util.TaskUtil;
import fr.utaria.utarialogin.UtariaLogin;
import fr.utaria.utarialogin.world.WorldManager;
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


	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		// Aucune commande autorisée
		if (!event.getPlayer().isOp())
			event.setCancelled(true);
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
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		PlayerUtil.hide(player);

		// On prépare le joueur pour éviter les problèmes
		// UUtil.preparePlayer(player);
		TaskUtil.runTaskLater(() -> {
			if (!player.isOp()) {
				player.setGameMode(GameMode.SPECTATOR);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));

				player.setFlySpeed(0f);
				player.setWalkSpeed(0f);
			} else {
				player.setGameMode(GameMode.CREATIVE);
				player.removePotionEffect(PotionEffectType.INVISIBILITY);
				player.setFlySpeed(.1f);
				player.setWalkSpeed(.1f);
			}

			player.teleport(UtariaLogin.getInstance().getInstance(WorldManager.class).getRandomHub());

			PlayerUtil.hide(player);
		}, 5L);

		// On indique au gestionnaire de logins que le joueur s'est connecté
		this.manager.newPlayerJoin(player);

		// En asynchrone, on lance le processus de connexion
		this.manager.tryToLogPlayer(player);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		// On indique au gestionnaire de logins que le joueur s'est déconnecté
		this.manager.newPlayerQuit(player);

		// On vide le cache du joueur en question
		UtariaLogin.getInstance().getInstance(AccountManager.class).clearCacheFor(player);
		this.manager.clearLoginCacheFor(player);
	}

}
