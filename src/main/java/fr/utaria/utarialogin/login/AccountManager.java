package fr.utaria.utarialogin.login;

import fr.utaria.utariacore.AbstractManager;
import fr.utaria.utariadatabase.result.DatabaseSet;
import fr.utaria.utarialogin.Config;
import fr.utaria.utarialogin.UtariaLogin;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountManager extends AbstractManager {

	private Map<UUID, Boolean> playersRegistered = new HashMap<>();

	public AccountManager() {
		super(UtariaLogin.getInstance(), "global");
	}

	@Override
	public void initialize() {

	}

	public boolean playerRegistered(Player player) {
		// Si le statut du joueur est en cache, on retourne directement la valeur.
		if( this.playersRegistered.containsKey(player.getUniqueId()) )
			return this.playersRegistered.get(player.getUniqueId());

		DatabaseSet set = this.getDB().select("password").from(Config.PLAYERS_DB_TABLE)
				                      .where("playername = ?").attributes(player.getName())
				                      .find();


		boolean reg = set != null && set.getString("password") != null;

		this.playersRegistered.put(player.getUniqueId(), reg);
		return reg;
	}

	public boolean tryRegisterPlayer(Player player, String password) {
		try {
			return this.getDB().execUpdateStatement(new RegistrationQuery(player, password)).getRowsAffected() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public boolean tryLoginPlayer(Player player, String password) {
		return this.getDB().select().from(Config.PLAYERS_DB_TABLE).where("uuid = ?", "password = SHA1(?)")
				           .attributes(player.getUniqueId().toString(), password).find() != null;
	}

	public void clearCacheFor(Player player) {
		this.playersRegistered.remove(player.getUniqueId());
	}

}