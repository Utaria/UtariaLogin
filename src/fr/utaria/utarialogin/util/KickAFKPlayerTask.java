package fr.utaria.utarialogin.util;

import fr.utaria.utarialogin.Config;
import fr.utaria.utarialogin.UtariaLogin;
import fr.utaria.utarialogin.login.LoginManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KickAFKPlayerTask implements Runnable {


	public KickAFKPlayerTask() {
		// On lance cette tâche qui va permettre d'exclure les joueurs inactifs toutes les 10 secondes.
		Bukkit.getScheduler().runTaskTimer(UtariaLogin.getInstance(), this, 0, 20L * 10);
	}


	@Override
	public void run() {

		for(Player player : Bukkit.getOnlinePlayers()) {
			long joinTime, now, diffTime;

			joinTime = UtariaLogin.getInstance().getInstance(LoginManager.class).getPlayerJoinTime(player);

			// Si le joueur est en train de rejoindre le serveur, on ne fait rien.
			if( joinTime == -1 ) continue;

			// Calcul du temps de connexion
			now      = System.currentTimeMillis();
			diffTime = now - joinTime;

			// Si le temps est supérieur au temps maximale (AFK Mode), on exclu le joueur.
			if( diffTime > Config.AFK_KICK_TIME * 1000 )
				player.kickPlayer("§cVous avez mis trop de temps pour vous connecter !");
		}

	}

}
