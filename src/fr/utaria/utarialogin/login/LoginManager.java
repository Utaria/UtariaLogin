package fr.utaria.utarialogin.login;

import fr.utaria.utariaapi.AbstractManager;
import fr.utaria.utariaapi.util.PlayerUtil;
import fr.utaria.utariaapi.util.ServerUtil;
import fr.utaria.utariaapi.util.TaskUtil;
import fr.utaria.utarialogin.Config;
import fr.utaria.utarialogin.UtariaLogin;
import fr.utaria.utarialogin.util.UUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class LoginManager extends AbstractManager {

	private Map<UUID, LoginSession> playersSession = new HashMap<>();
	private Map<UUID, Long>         joinTime       = new HashMap<>();
	private Map<UUID, String>       firstPasswords = new HashMap<>();
	private List<UUID>              waitingPlayers = new ArrayList<>();


	public LoginManager() {
		super(UtariaLogin.getInstance());

		this.registerListener(new LoginListener(this));
	}

	@Override
	public void initialize() {

	}


	private boolean playerIsLogged(Player player) {
		if( !this.playersSession.containsKey(player.getUniqueId()) ) return false;
		long   now      = System.currentTimeMillis();
		String playerIp = UUtil.getPlayerIP(player);

		LoginSession session = this.playersSession.get(player.getUniqueId());

		// Si la session n'est plus valide (par le temps ou l'IP), on la supprime, puis on retourne faux.
		if (now - session.getTime() > Config.SESSION_TIME * 1000 || !playerIp.equals(session.getIP())) {
			this.playersSession.remove(player.getUniqueId());
			return false;
		} else
		// Sinon, tout est bon, la session du joueur est encore valide.
			return true;
	}


	/*  Gestion des premiers mots de passe tapés   */
	private String  getFirstPasswordFor(Player player) {
		return this.firstPasswords.getOrDefault(player.getUniqueId(), null);
	}
	private void    addFirstPasswordFor(Player player, String password) {
		if( !this.firstPasswords.containsKey(player.getUniqueId()) )
			this.firstPasswords.put(player.getUniqueId(), password);
	}
	private void    removeFirstPasswordFor(Player player) {
		this.firstPasswords.remove(player.getUniqueId());
	}

	/*  Gestion des joueurs   */
	public void    newPlayerJoin(Player player) {
		if( !this.joinTime.containsKey(player.getUniqueId()) )
			this.joinTime.put(player.getUniqueId(), System.currentTimeMillis());
	}
	public void    newPlayerQuit(Player player) {
		this.joinTime.remove(player.getUniqueId());
	}
	public long    getPlayerJoinTime(Player player) {
		if( !this.joinTime.containsKey(player.getUniqueId()) ) return -1;
		else                                                   return this.joinTime.get(player.getUniqueId());
	}


	public void tryToLogPlayer(final Player player) {
		TaskUtil.runAsyncTask(() -> {

			// Le joueur n'est pas connecté, il doit donc le faire.
			if (!this.playerIsLogged(player)) {
				boolean registered = UtariaLogin.getInstance().getInstance(AccountManager.class).playerRegistered(player);

				PlayerUtil.sendHorizontalLine(player, ChatColor.GOLD);
				player.sendMessage(" ");

				if (registered) {
					PlayerUtil.sendCenteredMessage(player, "§e§lConnexion");
					player.sendMessage(" ");
					PlayerUtil.sendCenteredMessage(player, "§7Bienvenue sur §b§lUtaria§7 !");
					PlayerUtil.sendCenteredMessage(player, "§7Tapez votre mot de passe dans le tchat");
					PlayerUtil.sendCenteredMessage(player, "§7pour vous connecter à Utaria.");
					player.sendMessage(" ");

					// Petit son
					player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1f, 1f);
				} else {
					PlayerUtil.sendCenteredMessage(player, "§e§lInscription");
					player.sendMessage(" ");
					PlayerUtil.sendCenteredMessage(player, "§7Bienvenue sur §b§lUtaria§7 !");
					PlayerUtil.sendCenteredMessage(player, "§7Tapez un mot de passe dans le tchat");
					PlayerUtil.sendCenteredMessage(player, "§7pour pouvoir jouer dès maintenant sur §eUtaria§7 !");
					player.sendMessage(" ");

					// Petit son
					player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1f, 1f);
				}

				player.sendMessage(" ");
				PlayerUtil.sendHorizontalLine(player, ChatColor.GOLD);

				// On attends que le joueur tape le mot de passe dans le tchat
				this.waitForPassword(player);
			} else {
				// Le joueur est déjà connecté, donc on le redirige automatiquement
				PlayerUtil.sendSuccessMessage(player, "Vous êtes déjà connecté.");
				this.redirectPlayer(player);
			}

		});
	}

	/*  Traitement des mots de passe et des processus   */
	public  void checkPassword(Player player, String password) {
		// Si le serveur n'a pas demandé au joueur un mot de passe, on ne fait rien.
		if (!this.waitingPlayers.contains(player.getUniqueId())) return;


		// Le mot de passe doit rentrer dans les contraintes de validation
		// Sinon un message averti le joueur que le mot de passe est incorrect.
		if (UUtil.passwordIsValid(password)) {
			// On stoppe l'attente, car le joueur n'a plus rien à rentrer.
			this.waitingPlayers.remove(player.getUniqueId());

			// On lance la procédure de vérification du mot de passe
			this.processLogin(player, password);
		} else {
			PlayerUtil.sendEmptyLine(player);
			PlayerUtil.sendEmptyLine(player);
			PlayerUtil.sendCenteredMessage(player, "§8(§4§l✖§8) §c§lMot de passe envoyé invalide !");
			PlayerUtil.sendEmptyLine(player);

			player.sendMessage("   §7Il doit comporter au moins §66 caractères§7.");
			player.sendMessage("   §7Il ne doit pas contenir d'§6espace§7.");
			PlayerUtil.sendEmptyLine(player);
			player.sendMessage("   §7Ne communiquez §c§lJAMAIS §7votre mot de passe.");
			player.sendMessage("   §7Retenez ce mot de passe, car il vous sera indispensable.");

			PlayerUtil.sendEmptyLine(player);

			// Petit son
			player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 1f, 1f);
		}

	}
	private void waitForPassword(Player player) {
		if (!waitingPlayers.contains(player.getUniqueId()))
			this.waitingPlayers.add(player.getUniqueId());
	}
	private void processLogin(final Player player, final String password) {
		TaskUtil.runAsyncTask(() -> {
			// Le joueur est déjà inscrit, on regarde si le mot de passe correspond
			// CONNEXION
			AccountManager accountManager   = UtariaLogin.getInstance().getInstance(AccountManager.class);
			boolean        playerRegistered = accountManager.playerRegistered(player);

			if (playerRegistered) {

				// Mot de passe correct, redirection
				if (accountManager.tryLoginPlayer(player, password)) {
					PlayerUtil.sendSuccessMessage(player, "Vous êtes maintenant connecté.");

					// Petit son
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);

					// On active la session du joueur et on le redirige vers le bon serveur !
					this.activateSessionFor(player);
					this.redirectPlayer(player);
				}
				// Incorrect, on affiche un message d'erreur
				else {
					PlayerUtil.sendEmptyLines(player, 2);
					PlayerUtil.sendCenteredMessage(player, "§8(§4§l✖§8) §c§lMot de passe envoyé incorrect");
					PlayerUtil.sendEmptyLines(player, 2);
					PlayerUtil.sendCenteredMessage(player, "§7Le mot de passe envoyé");
					PlayerUtil.sendCenteredMessage(player, "§7est §c§lINCORRECT§7. Veuillez réessayez !");
					PlayerUtil.sendEmptyLines(player, 3);

					// Petit son
					player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 1f, 1f);

					// On attends que le joueur retape le mot de passe dans le tchat
					this.waitForPassword(player);
				}

			}
			// Le joueur renseigne le mot de passe por l'inscription
			// INSCRIPTION
			else {

				// Si le joueur tape son mot de passe pour la première fois
				if (this.getFirstPasswordFor(player) == null) {

					PlayerUtil.sendEmptyLines(player, 4);
					PlayerUtil.sendCenteredMessage(player, "§e§lVeuillez retaper votre mot de passe");
					PlayerUtil.sendCenteredMessage(player, "§e§lune seconde fois pour confirmer.");
					PlayerUtil.sendEmptyLines(player, 4);

					// Petit son
					player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1f, 1f);

					this.addFirstPasswordFor(player, password);

					// On attends que le joueur retape le mot de passe dans le tchat
					this.waitForPassword(player);
				}
				// Sinon, on regarde si les deux mots de passe sont les mêmes
				else {
					String firstPassword = this.getFirstPasswordFor(player);

					// Les deux mots de passe sont les mêmes, on finalise l'inscription
					if (password.equals(firstPassword)) {

						// On inscrit le joueur
						if (accountManager.tryRegisterPlayer(player, password)) {
							PlayerUtil.sendSuccessMessage(player, "Vous êtes maintenant inscrit sur §bUtaria§a, merci !");
							player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);

							this.redirectPlayer(player);
						}
						// Si un problème a eu lieu, on kick le joueur avec un message d'erreur (pas normal)
						else {
							player.kickPlayer("§cUne erreur a eu lieu lors de l'inscription, veuillez contacter un administrateur.");
						}

					}
					// Ils sont différents : on recommence de zéro
					else {
						PlayerUtil.sendEmptyLine(player);
						PlayerUtil.sendEmptyLine(player);
						PlayerUtil.sendCenteredMessage(player, "§8(§4§l✖§8) §c§lMots de passe incorrects");
						PlayerUtil.sendEmptyLine(player);
						PlayerUtil.sendEmptyLine(player);

						PlayerUtil.sendCenteredMessage(player, "§7Les deux mots de passe envoyés");
						PlayerUtil.sendCenteredMessage(player, "§c§lNE CORRESPONDENT PAS§7. Retapez un mot de passe :");

						PlayerUtil.sendEmptyLines(player, 3);

						// Petit son
						player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 1f, 1f);

						// On recommence la procédure de zéro
						this.removeFirstPasswordFor(player);
						this.waitForPassword(player);
					}
				}
			}
		});
	}
	private void redirectPlayer(Player player) {
		player.sendMessage("§7> Redirection vers le serveur §6" + Config.FALLBACK_SERVER + "§7.");
		ServerUtil.sendPlayerToServer(player, Config.FALLBACK_SERVER);
	}


	/*  Gestion des sessions de connexion   */
	private void activateSessionFor(Player player) {
		UUID uid = player.getUniqueId();

		if (!this.playersSession.containsKey(uid))
			this.playersSession.put(uid, new LoginSession(System.currentTimeMillis(), UUtil.getPlayerIP(player)));
	}
	public void clearExpiredSessions() {
		long now = System.currentTimeMillis();

		// On supprime toutes les sessions perimées
		this.playersSession.entrySet().removeIf(set -> now - set.getValue().getTime() > Config.SESSION_TIME * 1000);
	}
	public void clearLoginCacheFor(Player player) {
		UUID uid = player.getUniqueId();

		this.waitingPlayers.remove(uid);
		this.firstPasswords.remove(uid);
	}



	private class LoginSession {
		private long   time;
		private String ip;

		private LoginSession(long time, String ip) {
			this.time = time;
			this.ip   = ip;
		}

		private long   getTime() { return this.time; }
		private String getIP  () { return this.ip;   }

	}

}
