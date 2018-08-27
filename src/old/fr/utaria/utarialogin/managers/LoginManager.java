package fr.utaria.utarialogin.managers;

import com.utaria.utariaapi.managers.TaskManager;
import com.utaria.utariaapi.utils.BungeeUtils;
import com.utaria.utariaapi.utils.PlayerUtils;
import fr.utaria.utarialogin.UtariaLogin;
import fr.utaria.utarialogin.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class LoginManager {

	private Map<UUID, Long>   playersSession = new HashMap<>();
	private Map<UUID, Long>   joinTime       = new HashMap<>();
	private Map<UUID, String> firstPasswords = new HashMap<>();
	private List<UUID>        waitingPlayers = new ArrayList<>();


	public boolean playerIsLogged(Player player) {
		if( !this.playersSession.containsKey(player.getUniqueId()) ) return false;
		long now = System.currentTimeMillis();

		// Si la session n'est plus valide, on la supprime, puis on retourne false
		if(now - this.playersSession.get(player.getUniqueId()) > UtariaLogin.SESSION_TIME * 1000) {

			this.playersSession.remove(player.getUniqueId());
			return false;

		} else
		// Sinon, tout est bon, la session du joueur est encore valide.
			return true;
	}


	/*  Gestion des premiers mots de passe tapés   */
	public String  getFirstPasswordFor(Player player) {
		if( this.firstPasswords.containsKey(player.getUniqueId()) )
			return this.firstPasswords.get(player.getUniqueId());
		else
			return null;
	}
	public void    addFirstPasswordFor(Player player, String password) {
		if( !this.firstPasswords.containsKey(player.getUniqueId()) )
			this.firstPasswords.put(player.getUniqueId(), password);
	}
	public void    removeFirstPasswordFor(Player player) {
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
		TaskManager.runTaskLater(new Runnable() {
			@Override
			public void run() {

				// Le joueur n'est pas connecté, il doit donc le faire.
				if( !UtariaLogin.getLoginManager().playerIsLogged(player) ) {
					boolean registered = UtariaLogin.getAccountManager().playerRegistered(player);

					PlayerUtils.sendHorizontalLine(player, ChatColor.GOLD);
					player.sendMessage(" ");

					if( registered ) {
						PlayerUtils.sendCenteredMessage(player, "§e§lConnexion");
						player.sendMessage(" ");
						PlayerUtils.sendCenteredMessage(player, "§7Bienvenue sur §b§lUtaria§7 !");
						PlayerUtils.sendCenteredMessage(player, "§7Tapez votre mot de passe dans le tchat");
						PlayerUtils.sendCenteredMessage(player, "§7pour vous connecter à Utaria.");
						player.sendMessage(" ");

						// Petit son
						player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1f, 1f);
					} else {
						PlayerUtils.sendCenteredMessage(player, "§e§lInscription");
						player.sendMessage(" ");
						PlayerUtils.sendCenteredMessage(player, "§7Bienvenue sur §b§lUtaria§7 !");
						PlayerUtils.sendCenteredMessage(player, "§7Tapez un mot de passe dans le tchat");
						PlayerUtils.sendCenteredMessage(player, "§7pour pouvoir jouer dès maintenant sur §eUtaria§7 !");
						player.sendMessage(" ");

						// Petit son
						player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1f, 1f);
					}

					player.sendMessage(" ");
					PlayerUtils.sendHorizontalLine(player, ChatColor.GOLD);


					// On attends que le joueur tape le mot de passe dans le tchat
					UtariaLogin.getLoginManager().waitForPassword(player);

				} else {
					// Le joueur est déjà connecté, donc on le redirige automatiquement
					PlayerUtils.sendSuccessMessage(player, "Vous êtes déjà connecté.");
					UtariaLogin.getLoginManager().redirectPlayer(player);
				}

			}
		}, 1L);
	}

	/*  Traitement des mots de passe et des processus   */
	public void waitForPassword(Player player) {
		if( waitingPlayers.contains(player.getUniqueId()) ) return;
		this.waitingPlayers.add(player.getUniqueId());
	}
	public void checkPassword(Player player, String password) {
		// Si le serveur n'a pas demandé au joueur un mot de passe, on ne fait rien.
		if( !this.waitingPlayers.contains(player.getUniqueId()) ) return;


		// Le mot de passe doit rentrer dans les contraintes de validation
		// Sinon un message averti le joueur que le mot de passe est incorrect.
		if( Utils.passwordIsValid(password) ) {
			// On stoppe l'attente, car le joueur n'a plus rien à rentrer.
			this.waitingPlayers.remove(player.getUniqueId());

			// On lance la procédure de vérification du mot de passe
			this.processLogin(player, password);
		} else {

			PlayerUtils.sendHorizontalLine(player, ChatColor.DARK_RED);
			PlayerUtils.sendEmptyLine(player);
			PlayerUtils.sendCenteredMessage(player, "§4§lMot de passe envoyé invalide");
			PlayerUtils.sendEmptyLine(player);

			player.sendMessage("§7  §r§7 - Il doit comporter au moins §66 caractères§7.");
			player.sendMessage("§7  §r§7 - Ne communiquez §cJAMAIS §7votre mot de passe.");
			player.sendMessage("§7  §r§7 - Retenez ce mot de passe, car il vous sera indispensable.");

			PlayerUtils.sendEmptyLine(player);
			PlayerUtils.sendEmptyLine(player);
			PlayerUtils.sendHorizontalLine(player, ChatColor.DARK_RED);

			// Petit son
			player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 1f, 1f);

		}

	}
	public void processLogin(final Player player, final String password) {
		TaskManager.runTaskLater(new Runnable() {
			@Override
			public void run() {
				// Le joueur est déjà inscrit, on regarde si le mot de passe correspond
				// CONNEXION
				boolean playerRegistered = UtariaLogin.getAccountManager().playerRegistered(player);

				if( playerRegistered ) {

					// Mot de passe correct, redirection
					if( UtariaLogin.getAccountManager().tryLoginPlayer(player, password) ) {
						PlayerUtils.sendSuccessMessage(player, "Vous êtes maintenant connecté.");

						// Petit son
						player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);

						// On active la session du joueur et on le redirige vers le serveur hub
						UtariaLogin.getLoginManager().activateSessionFor(player);
						UtariaLogin.getLoginManager().redirectPlayer(player);
					}
					// Incorrect, on affiche un message d'erreur
					else {
						PlayerUtils.sendHorizontalLine(player, ChatColor.DARK_RED);
						PlayerUtils.sendEmptyLine(player);
						PlayerUtils.sendCenteredMessage(player, "§4§lMot de passe envoyé incorrect");
						PlayerUtils.sendEmptyLine(player);
						PlayerUtils.sendEmptyLine(player);

						PlayerUtils.sendCenteredMessage(player, "§7Le mot de passe envoyé");
						PlayerUtils.sendCenteredMessage(player, "§7est incorrect. Veuillez réessayez !");

						PlayerUtils.sendEmptyLine(player);
						PlayerUtils.sendEmptyLine(player);
						PlayerUtils.sendHorizontalLine(player, ChatColor.DARK_RED);

						// Petit son
						player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 1f, 1f);

						// On attends que le joueur retape le mot de passe dans le tchat
						UtariaLogin.getLoginManager().waitForPassword(player);
					}

				}
				// Le joueur renseigne le mot de passe por l'inscription
				// INSCRIPTION
				else {

					// Si le joueur tape son mot de passe pour la première fois
					if ( UtariaLogin.getLoginManager().getFirstPasswordFor(player) == null ) {

						PlayerUtils.sendHorizontalLine(player, ChatColor.GREEN);
						PlayerUtils.sendEmptyLines(player, 3);
						PlayerUtils.sendCenteredMessage(player, "§e§lVeuillez retaper votre mot de passe");
						PlayerUtils.sendCenteredMessage(player, "§e§lune seconde fois pour confirmer.");
						PlayerUtils.sendEmptyLines(player, 3);
						PlayerUtils.sendHorizontalLine(player, ChatColor.GREEN);

						// Petit son
						player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1f, 1f);

						UtariaLogin.getLoginManager().addFirstPasswordFor(player, password);

						// On attends que le joueur retape le mot de passe dans le tchat
						UtariaLogin.getLoginManager().waitForPassword(player);
					}
					// Sinon, on regarde si les deux mots de passe sont les mêmes
					else {
						String firstPassword = UtariaLogin.getLoginManager().getFirstPasswordFor(player);

						// Les deux mots de passe sont les mêmes, on finalise l'inscription
						if( firstPassword.equals(password) ) {

							// On inscrit le joueur
							if( UtariaLogin.getAccountManager().tryRegisterPlayer(player, password) ) {
								PlayerUtils.sendSuccessMessage(player, "Vous êtes maintenant inscrit sur §bUtaria§a, merci !");
								player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);

								UtariaLogin.getLoginManager().redirectPlayer(player);
							}
							// Si un problème a eu lieu, on kick le joueur avec un message d'erreur (pas normal)
							else {
								player.kickPlayer("§cUne erreur a eu lieu lors de l'inscription, veuillez contacter un administrateur.");
							}

						}
						// Ils sont différents : on recommence de zéro
						else {
							PlayerUtils.sendHorizontalLine(player, ChatColor.DARK_RED);
							PlayerUtils.sendEmptyLine(player);
							PlayerUtils.sendCenteredMessage(player, "§4§lMots de passe incorrects");
							PlayerUtils.sendEmptyLine(player);
							PlayerUtils.sendEmptyLine(player);

							PlayerUtils.sendCenteredMessage(player, "§7Les deux mots de passe envoyés");
							PlayerUtils.sendCenteredMessage(player, "§7ne correspondent pas. Retapez un mot de passe :");

							PlayerUtils.sendEmptyLine(player);
							PlayerUtils.sendEmptyLine(player);
							PlayerUtils.sendHorizontalLine(player, ChatColor.DARK_RED);

							// Petit son
							player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 1f, 1f);

							// On recommence la procédure de zéro
							UtariaLogin.getLoginManager().removeFirstPasswordFor(player);
							UtariaLogin.getLoginManager().waitForPassword(player);
						}
					}

				}
			}
		}, 1L);
	}
	public void redirectPlayer(Player player) {
		PlayerUtils.sendMessage(player, "§7Redirection vers le serveur §6" + UtariaLogin.FALLBACK_SERVER + "§7.");
		BungeeUtils.sendPlayerTo(player, UtariaLogin.FALLBACK_SERVER);
	}


	/*  Gestion des sessions de connexion   */
	public void activateSessionFor(Player player) {
		UUID uid = player.getUniqueId();

		if( !this.playersSession.containsKey(uid) )
			this.playersSession.put(uid, System.currentTimeMillis());
	}
	public void clearExpiredSessions() {
		long now = System.currentTimeMillis();
		Iterator<Map.Entry<UUID, Long>> plUuidIterator = this.playersSession.entrySet().iterator();

		while (plUuidIterator.hasNext()) {
			UUID uid   = plUuidIterator.next().getKey();
			long begin = this.playersSession.get(uid);

			// Si la session est périmée, on la supprime
			if( now - begin > UtariaLogin.SESSION_TIME * 1000 )
				plUuidIterator.remove();
		}
	}
	public void clearLoginCacheFor(Player player) {
		UUID uid = player.getUniqueId();

		this.waitingPlayers.remove(uid);
		this.firstPasswords.remove(uid);
	}

}
