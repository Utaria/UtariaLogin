package fr.utaria.utarialogin;

import fr.utaria.utariacore.UtariaPlugin;
import fr.utaria.utariadatabase.database.DatabaseManager;
import fr.utaria.utarialogin.login.AccountManager;
import fr.utaria.utarialogin.login.LoginManager;
import fr.utaria.utarialogin.message.MessageManager;
import fr.utaria.utarialogin.world.WorldManager;

public class UtariaLogin extends UtariaPlugin {

	private static UtariaLogin instance;

	public void onEnable() {
		// On ajoute l'instance du plugin en mémoire
		instance = this;

		DatabaseManager.registerDatabase("global");


		// On enregistre les gestionnaires
		new MessageManager();
		new AccountManager();
		new LoginManager();
		new WorldManager();
	}

	public void onDisable() {

	}

	public static UtariaLogin getInstance() { return instance; }

}
