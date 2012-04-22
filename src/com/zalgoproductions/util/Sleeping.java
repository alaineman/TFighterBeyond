package com.zalgoproductions.util;

import com.zalgoproductions.script.TFighterBeyond;

import java.util.Random;

import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.interactive.Player;

public class Sleeping {
	
	private static final Random RANDOM;
	
	static {
		RANDOM = new Random();
	}
	
	/**
	 * This will cause the running script to sleep until the provided condition is not 
	 * valid anymore or the upper bound is hit.
	 * 
	 * Note: Running script means that the script is running and the script is logged into Runescape
	 * 
	 * @param condition	The condition to verify
	 * @param bound		The upper bound for waiting time
	 * @return			True if stopped waiting because of condition switch, false if condition 
	 * 					never switched or script stopped running.
	 */
	public static boolean waitWhile(Condition condition, int bound) {
		if (bound < 0) {
			throw new IllegalArgumentException("It is impossible to wait for a negative amount of time");
		}
		Timer timer = new Timer(bound);
		while (timer.isRunning()) {
			if (!scriptRunning()) {
				return false;
			}
			if (!condition.validate()) {
				return true;
			}
			Time.sleep((int) RANDOM.nextGaussian() + 50);
		}
		return false;
	}
	
	/**
	 * 
	 * @param condition
	 * @return
	 */
	public static boolean waitWhile (Condition condition) {
		while(scriptRunning() && condition.validate()) {
			Time.sleep((int) RANDOM.nextGaussian()*5 + 50);
		}
		return scriptRunning();
	}
	
	public static boolean waitForTabChange(final Tabs tab) {
		return waitWhile(new Condition() {

			@Override
			public boolean validate() {
				return !Tabs.getCurrent().equals(tab);
			}
			
		}, 1500);
	}
	
	public static boolean waitWhileMoving() {
		final Player localPlayer = Players.getLocal();
		return waitWhile(new Condition() {

			@Override
			public boolean validate() {
				return localPlayer.isMoving();
			}
			
		});
	}
	
	public static boolean waitForMovement() {
		final Player localPlayer = Players.getLocal();
		return waitWhile(new Condition() {

			@Override
			public boolean validate() {
				return !localPlayer.isMoving();
			}
			
		}, 1500);
	}

	public static boolean waitWhileAnim(final int id) {
		final Player localPlayer = Players.getLocal();
		return waitWhile(new Condition() {

			@Override
			public boolean validate() {
				return localPlayer.getAnimation() == id;
			}
			
		}, 10000);
	}

	public static boolean waitForAnim() {
		final Player localPlayer = Players.getLocal();
		return waitWhile(new Condition() {

			@Override
			public boolean validate() {
				return localPlayer.getAnimation() == -1;
			}
			
		}, 1000) ? true : waitWhile(new Condition() {

			@Override
			public boolean validate() {
				return localPlayer.getAnimation() == -1 && localPlayer.getInteracting() != null;
			}
			
		}, 1500);
	}

	public static boolean waitForInventoryChange(final int count, int bound) {
		return waitWhile(new Condition() {

			@Override
			public boolean validate() {
				return Inventory.getCount() == count;
			}
			
		}, bound);
	}
	
	public static boolean scriptRunning() {
		return TFighterBeyond.getInstance().isRunning() && Game.isLoggedIn();
	}
}