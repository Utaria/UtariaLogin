package fr.utaria.utarialogin.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener implements Listener {

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e) {
		World world = e.getWorld();

		// Il doit toujours y avoir du beau temps dans le serveur de connexion
		e.setCancelled(true);

		world.setStorm(false);
		world.setThundering(false);
		world.setWeatherDuration(999999);
	}

}
