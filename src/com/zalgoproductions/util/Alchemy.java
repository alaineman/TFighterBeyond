package com.zalgoproductions.util;

import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.Widget;

public class Alchemy {
	public static boolean doAlchemy = false;

	private static int[] alchIds = {};
	private static String[] alchNames = {};
	private static final int NATURE_RUNE = 561;
	private static final int FIRE_RUNE = 554;

	public static final Filter<Item> ALCH_FILTER = new Filter<Item>() {

		@Override
		public boolean accept(Item i) {
			if (i.getWidgetChild() == null || i.getId() == -1) {
				return false;
			}
			String itemName = i.getName().toLowerCase();
			for (String s : alchNames) {
				if (itemName.contains(s)) {
					return true;
				}
			}
			for (int n : alchIds) {
				if (n == i.getId()) {
					return true;
				}
			}
			return false;
		}

	};

	public static void setAlchIds(int[] ids) {
		alchIds = ids;
	}

	public static void setAlchNames(String[] names) {
		alchNames = names;
	}

	public static boolean canAlch() {
		return canHighAlch() || canLowAlch();
	}

	private static boolean canHighAlch() {
		return Inventory.getCount(true, NATURE_RUNE) >= 1
				&& Skills.getLevel(Skills.MAGIC) >= 55
				&& (Inventory.getCount(true, FIRE_RUNE) >= 5
					|| Attacking.playerEquipment[3].getName().toLowerCase().contains("fire")
					|| Attacking.playerEquipment[3].getName().toLowerCase().contains("steam")
					|| Attacking.playerEquipment[3].getName().toLowerCase().contains("lava"));
	}

	private static boolean canLowAlch() {
		return Inventory.getCount(true, NATURE_RUNE) >= 1
				&& Skills.getLevel(Skills.MAGIC) >= 21
				&& (Inventory.getCount(true, FIRE_RUNE) >= 3
					|| Attacking.playerEquipment[3].getName().toLowerCase().contains("fire")
					|| Attacking.playerEquipment[3].getName().toLowerCase().contains("steam")
					|| Attacking.playerEquipment[3].getName().toLowerCase().contains("lava"));
	}

	public static boolean alch(Item i) {
		if (i != null && canLowAlch()) {
			Tabs.MAGIC.open();
			if (Sleeping.waitForTabChange(Tabs.MAGIC)) {
				Widget modernBook = Widgets.get(192);
				if (!modernBook.validate()) {  return false;  }
				final int widgetChildID = canHighAlch() ? 59 : 38;
				return modernBook.getChild(widgetChildID).interact("Cast")
						&& Sleeping.waitForTabChange(Tabs.INVENTORY)
						&& i.getWidgetChild().interact("Cast");
			}
		}
		return false;
	}
}