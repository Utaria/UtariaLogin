package fr.utaria.utarialogin.login;

import fr.utaria.utariadatabase.query.IQuery;
import fr.utaria.utarialogin.Config;
import org.bukkit.entity.Player;

public class RegistrationQuery implements IQuery {

	private Player player;

	private String password;

	RegistrationQuery(Player player, String password) {
		this.player = player;
		this.password = password;
	}

	@Override
	public String getRequest() {
		return "UPDATE " + Config.PLAYERS_DB_TABLE + " SET password = SHA1(?) WHERE playername = ?";
	}

	@Override
	public Object[] getAttributes() {
		return new Object[] { this.password, this.player.getName() };
	}

}
