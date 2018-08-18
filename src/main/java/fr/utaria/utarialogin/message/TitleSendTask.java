package fr.utaria.utarialogin.message;

import fr.utaria.utariacore.util.TitleUtil;
import fr.utaria.utarialogin.UtariaLogin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;

public class TitleSendTask implements Runnable {

	private MessageManager manager;


	TitleSendTask(MessageManager manager) {
		this.manager = manager;

		Bukkit.getScheduler().runTaskTimerAsynchronously(UtariaLogin.getInstance(), this, 0, 20L);
	}

	@Override
	public void run() {
		Iterator<Map.Entry<Player, TitleUtil.Title>> iterator = this.manager.getLastTitles().entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<Player, TitleUtil.Title> entry = iterator.next();

			if (entry.getKey() == null || !entry.getKey().isOnline()) {
				iterator.remove();
				continue;
			}

			entry.getValue().setFadeInTime(0);
			entry.getValue().setStayTime(20);
			entry.getValue().setFadeOutTime(0);
			entry.getValue().send(entry.getKey());
		}
	}

}
