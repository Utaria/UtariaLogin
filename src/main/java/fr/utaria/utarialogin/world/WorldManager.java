package fr.utaria.utarialogin.world;

import fr.utaria.utariacore.AbstractManager;
import fr.utaria.utariacore.util.Materials;
import fr.utaria.utarialogin.UtariaLogin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldManager extends AbstractManager {

	private World          world;
	private List<Location> hubs;


	public WorldManager() {
		super(UtariaLogin.getInstance());

		this.world = Bukkit.getWorlds().get(0);
		this.hubs  = new ArrayList<>();

		this.hubs.add(new Location(this.world,  150.5, 80,   0.5, -145, 0));
		this.hubs.add(new Location(this.world,    0.5, 79,   0.5, -140, 0));
		this.hubs.add(new Location(this.world, -150.5, 78,   0.5,   40, 0));
		this.hubs.add(new Location(this.world,    0.5, 79, 150.5,   20, 0));

		new WorldTimeTask();
	}

	@Override
	public void initialize() {

	}

	@EventHandler
	public void onBlockFade(BlockFadeEvent e) {
		Block block    = e.getBlock();
		Material newType = e.getNewState().getType();

		// Gestion de la fonte de la neige
		if (block.getType().equals(Material.SNOW) && newType.equals(Material.AIR))
			e.setCancelled(true);

		// Gestion de la fonte de blocs de glace en eau
		if(block.getType().equals(Material.ICE) && Materials.isWater(newType))
			e.setCancelled(true);
	}


	public Location getRandomHub() {
		return this.hubs.get(new Random().nextInt(this.hubs.size()));
	}


	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		World world = event.getWorld();

		// Il doit toujours y avoir du beau temps dans le serveur de connexion
		event.setCancelled(true);

		world.setStorm(false);
		world.setThundering(false);
		world.setWeatherDuration(999999);
	}

}
