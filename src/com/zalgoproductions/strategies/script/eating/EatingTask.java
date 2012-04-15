package com.zalgoproductions.strategies.script.eating;

import com.zalgoproductions.script.TFighterBeyond;
import com.zalgoproductions.util.Eating;
import org.powerbot.concurrent.Task;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.node.Item;

public class EatingTask implements Task {
	public void run() {
		if(Inventory.getCount(Eating.foodFilter) > 0) {
			Item[] foods = Inventory.getItems();
			Item toEat = null;

			for(Item food : foods) {
				if(Eating.foodFilter.accept(food)) {
					toEat = food;
					break;
				}
			}
			if(toEat != null) {
				toEat.getWidgetChild().interact("Eat");
			}
			Time.sleep(Random.nextInt(500, 800));
		} if(Eating.canB2B() && Inventory.getCount(Eating.bonesFilter) > 0) {
			Eating.castB2B();
		} else if (Eating.canB2P() && Inventory.getCount(Eating.bonesFilter) > 0) {
			Eating.castB2P();
		} else {
			TFighterBeyond.getInstance().stop();
		}		
	}
}
