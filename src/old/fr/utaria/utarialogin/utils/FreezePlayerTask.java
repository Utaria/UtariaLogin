package fr.utaria.utarialogin.utils;

import com.utaria.utariaapi.managers.TaskManager;
import fr.utaria.utarialogin.UtariaLogin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FreezePlayerTask implements Runnable {

	public FreezePlayerTask() {
		Bukkit.getScheduler().runTaskTimer(UtariaLogin.getInstance(), this, 0, 20L);
	}

	@Override
	public void run() {
		TaskManager.runTaskLater(new Runnable() {
			@Override
			public void run() {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if( player == null || !player.isOnline() ) continue;

					player.setFlySpeed(0f);
					player.setWalkSpeed(0f);

					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));

					player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
				}
			}
		}, 1L);

	}
}
