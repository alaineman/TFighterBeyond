package com.zalgoproductions.strategies.script.combat;

import org.powerbot.concurrent.strategy.Condition;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.interactive.Character;

public class InCombatCondition implements Condition {
	Character lastInteracting = null;
	public boolean validate() {
		boolean activate = false;
		if(lastInteracting != null && lastInteracting.validate() && lastInteracting.getInteracting() != null && lastInteracting.getModel() != null) {
			activate = lastInteracting.getInteracting().equals(Players.getLocal());
		}
		return activate || (lastInteracting = Players.getLocal().getInteracting()) != null;
	}
}
