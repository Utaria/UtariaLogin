package fr.utaria.utarialogin.util;

import fr.utaria.utarialogin.UtariaLogin;
import org.bukkit.Bukkit;

public class ClearSessionsTask implements Runnable {


	public ClearSessionsTask() {
		// On lance la tâche toutes les minutes
		Bukkit.getScheduler().runTaskTimerAsynchronously(UtariaLogin.getInstance(), this, 0, 20L * 60);
	}

	@Override
	public void run() {
		// On supprime les sessions de joueurs qui sont périmées
		// UtariaLogin.getLoginManager().clearExpiredSessions();
	}

}
