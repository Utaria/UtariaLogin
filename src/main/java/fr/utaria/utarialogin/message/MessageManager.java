package fr.utaria.utarialogin.message;

import fr.utaria.utariacore.AbstractManager;
import fr.utaria.utariacore.util.TitleUtil;
import fr.utaria.utarialogin.UtariaLogin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.ConcurrentHashMap;

public class MessageManager extends AbstractManager {

	private ConcurrentHashMap<Player, String> lastMessages;

	private ConcurrentHashMap<Player, TitleUtil.Title> lastTitles;

	public MessageManager() {
		super(UtariaLogin.getInstance());

		this.lastMessages = new ConcurrentHashMap<>();
		this.lastTitles   = new ConcurrentHashMap<>();

		new TitleSendTask(this);
	}

	@Override
	public void initialize() {

	}

	ConcurrentHashMap<Player, TitleUtil.Title> getLastTitles() {
		return this.lastTitles;
	}

	ConcurrentHashMap<Player, String> getLastMessages() {
		return this.lastMessages;
	}

	public void sendTitle(Player player, String title, String subtitle, ChatColor titleColor, ChatColor subtitleColor, boolean persist) {
		/*TitleUtil.Title titleObj = TitleUtil.displayTitleToPlayer(title, subtitle, titleColor, subtitleColor, player);
		if (persist) this.lastTitles.put(player, titleObj);*/
	}

}
