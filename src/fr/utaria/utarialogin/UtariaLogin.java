package fr.utaria.utarialogin;

import com.utaria.utariaapi.database.Database;
import fr.utaria.utarialogin.listeners.ChatListener;
import fr.utaria.utarialogin.listeners.JoinListener;
import fr.utaria.utarialogin.listeners.LeaveListener;
import fr.utaria.utarialogin.managers.AccountManager;
import fr.utaria.utarialogin.managers.LoginManager;
import fr.utaria.utarialogin.utils.ClearSessionsTask;
import fr.utaria.utarialogin.utils.FreezePlayerTask;
import fr.utaria.utarialogin.utils.KickAFKPlayerTask;
import org.bukkit.plugin.java.JavaPlugin;

public class UtariaLogin extends JavaPlugin {

	final public static String PLAYERS_DB_TABLE = "players";
	final public static String FALLBACK_SERVER  =  "survie";
	final public static int    SESSION_TIME     =       120;
	final public static int    AFK_KICK_TIME    =        30;


	private static UtariaLogin _instance;
	private static Database    _database;

	private static AccountManager _accountManager;
	private static LoginManager   _loginManager;


	public void onEnable() {
		// On ajoute l'instance du plugin en mémoire
		_instance = this;


		// On crée l'instance de connexion à la base de données
		_database = new Database();


		// On enregistre les écouteurs d'évènements
		getServer().getPluginManager().registerEvents(new JoinListener() , this);
		getServer().getPluginManager().registerEvents(new ChatListener() , this);
		getServer().getPluginManager().registerEvents(new LeaveListener(), this);


		// On enregistre les gestionnaires
		_accountManager = new AccountManager();
		_loginManager   = new LoginManager();


		// Lancement des tâches automatiques
		new FreezePlayerTask();
		new ClearSessionsTask();
		new KickAFKPlayerTask();
	}

	public void onDisable() {

	}


	public static UtariaLogin getInstance() { return _instance; }
	public static Database    getDB() {
		return _database;
	}

	public static AccountManager getAccountManager() { return _accountManager; }
	public static LoginManager   getLoginManager() { return _loginManager; }

}
