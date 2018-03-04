package fr.utaria.utarialogin.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class UUtil {

	public static boolean passwordIsValid(String password) {
		// Le mot de passe doit faire au moins 6 caractères
		if (password.length() < 6) return false;

		// Le mot de passe ne doit pas contenir d'espace
		if (password.contains(" ")) return false;


		return true;
	}
	public static String  encryptToSHA1(String str) {
		String sha1 = "";

		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(str.getBytes("UTF-8"));
			sha1 = UUtil.byteToHex(crypt.digest());
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
