package fr.utaria.utarialogin.world;

import fr.utaria.utarialogin.UtariaLogin;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Calendar;

public class WorldTimeTask implements Runnable {

	private World world;


	WorldTimeTask() {
		this.world = Bukkit.getWorlds().get(0);

		this.world.setGameRuleValue("doDaylightCycle", "false");

		Bukkit.getScheduler().runTaskTimer(UtariaLogin.getInstance(), this, 0L, 40L);
	}

	@Override
	public void run() {
		Calendar c = Calendar.getInstance();

		int h = c.get(Calendar.HOUR_OF_DAY) - 6;
		if (h < 0) h += 24;
		int m = c.get(Calendar.MINUTE);
		int s = c.get(Calendar.SECOND);

		long seconds = h * 60 * 60 + m * 60 + s;
		long ticks = this.secToTicks(seconds);

		this.world.setTime(ticks);
	}


	private long secToTicks(long sec) {
		return (long) Math.floor(0.2777778F * (float) sec);
	}

}
