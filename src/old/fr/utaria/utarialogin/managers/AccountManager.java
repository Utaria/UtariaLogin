package fr.utaria.utarialogin.managers;

import com.utaria.utariaapi.database.Database;
import com.utaria.utariaapi.database.DatabaseSet;
import fr.utaria.utarialogin.UtariaLogin;
import fr.utaria.utarialogin.utils.Utils;
import org.bukkit.entity.Player;

import java.util.*;

public class AccountManager {

	private Map<UUID, Boolean> playersRegistered = new HashMap<>();



	public boolean playerRegistered(Player player) {
		// Si le statut du joueur est en cache, on retourne directement la valeur.
		if( this.playersRegistered.containsKey(player.getUniqueId()) )
			return this.playersRegistered.get(player.getUniqueId());

		DatabaseSet set = new Database().findFirst(UtariaLogin.PLAYERS_DB_TABLE, DatabaseSet.makeConditions(
			"uuid", player.getUniqueId().toString()
		));


		boolean reg = set != null && set.getString("password") != null;

		this.playersRegistered.put(player.getUniqueId(), reg);
		return reg;
	}

	public boolean tryRegisterPlayer(Player player, String password) {

		new Database().request(
				"UPDATE " + UtariaLogin.PLAYERS_DB_TABLE + " SET " +
				"password = SHA1(?) WHERE uuid = ?",

				Arrays.asList(password, player.getUniqueId().toString())
		);

		return true;
	}
	public boolean tryLoginPlayer(Player player, String password) {
		// TODO Modifier et faire une requête directe, pour éviter les soucis.
		List<DatabaseSet> sets = new Database().request(
				"SELECT password FROM " + UtariaLogin.PLAYERS_DB_TABLE + " " +
				"WHERE uuid = ? AND password = SHA1(?)",

				Arrays.asList(player.getUniqueId().toString(), password)
		);

		return sets != null && sets.size() > 0;
	}


	public void clearCacheFor(Player player) {
		this.playersRegistered.remove(player.getUniqueId());
	}

}