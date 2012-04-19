package com.zalgoproductions.strategies.script.areagenerator;

import org.powerbot.concurrent.strategy.Condition;

public class AreaGeneratorCondition implements Condition {
	public static long nextRun = 0;
	public static boolean doGeneration = false;

	public boolean validate() {
		return System.currentTimeMillis() > nextRun;
	}
}
