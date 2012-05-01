package com.zalgoproductions.strategies.script.attacking;

import com.zalgoproductions.util.Attacking;
import com.zalgoproductions.util.Safespot;
import com.zalgoproductions.util.Sleeping;
import org.powerbot.concurrent.Task;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.interactive.NPC;

public class AttackTask implements Task {
	public void run() {
		NPC nearest = NPCs.getNearest(Attacking.NPC_FILTER);
		if(nearest.isOnScreen()) {
			nearest.interact("Attack");
			if(Safespot.useSafespot)
				Sleeping.waitForAnim();
			else {  				
				Sleeping.waitForMovement();
				Sleeping.waitWhileMoving();
			}
			if(Attacking.NPC_FILTER.accept(nearest)) {
				Time.sleep(Random.nextInt(2000, 3000));
			}
		} else {
			Walking.walk(nearest.getLocation());
			if(Safespot.useSafespot)
				Sleeping.waitForAnim();
			else {
				Sleeping.waitForMovement();
				Sleeping.waitWhileMoving();
			}
			Time.sleep((Random.nextInt(200, 500)));
		}
	}
}
