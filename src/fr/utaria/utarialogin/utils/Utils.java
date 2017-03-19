package fr.utaria.utarialogin.utils;

import com.utaria.utariaapi.utils.TitleUtils;
import fr.utaria.utarialogin.UtariaLogin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class Utils {

	public static void    preparePlayer(final Player player) {
		Bukkit.getScheduler().runTaskLater(UtariaLogin.getInstance(), () -> {
			// On cache le joueur
			player.setGameMode(GameMode.SPECTATOR);
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));

			// On lui enlève les effets
			player.removePotionEffect(PotionEffectType.BLINDNESS);
			player.removePotionEffect(PotionEffectType.SLOW);

			// On cache tout le monde pour tout le monde
			Utils.hideAllForAll();

			// On lui envoie un message
			TitleUtils.displayTitleToPlayer("Bienvenue sur Utaria !", "Ouvrez le tchat pour vous connecter", ChatColor.YELLOW, ChatColor.GRAY, player);
		}, 15L);
	}

	public static boolean passwordIsValid(String password) {
		// Le mot de passe doit faire au moins 6 caractères
		if( password.length() < 6 ) return false;

		// ...


		return true;
	}
	public static String  encryptToSHA1(String str) {
		String sha1 = "";

		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(str.getBytes("UTF-8"));
			sha1 = Utils.byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return sha1;
	}
	public static String  getPlayerIP(Player player) {
		return player.getAddress().getHostName();
	}

	private static void hideAllForAll() {
		for (Player p1 : Bukkit.getOnlinePlayers())
			for (Player p2 : Bukkit.getOnlinePlayers())
				p1.hidePlayer(p2);
	}


	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for( byte b : hash )
			formatter.format("%02x", b);

		String result = formatter.toString();
		formatter.close();
		return result;
	}

}
