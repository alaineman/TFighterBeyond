package com.zalgoproductions.util;

import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.Widget;

public class Alchemy {
	public static boolean doAlchemy = false;

	private static int[] alchIds = {};
	private static String[] alchNames = {};
	private static final int NATURE_RUNE = 561;
	private static final int FIRE_RUNE = 554;

	public static final Filter<Item> ALCH_FILTER = new Filter<Item>() {
		public boolean accept(Item i) {
			if (i.getWidgetChild() == null || i.getId() == -1) {
				return false;
			}
			final String ITEM_NAME = i.getName().toLowerCase();
			for (String alchName : alchNames) {
				// alchNames should consist only of lowercase strings.  See setAlchNames()
				if (ITEM_NAME.contains(alchName)) {
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
		if (names != null) {
			alchNames = names;
			for (int i = 0; i < alchNames.length; i++) {
				alchNames[i] = alchNames[i].toLowerCase();
			}
		}
	}

	public static boolean canAlch() {
		return canLowAlch();
	}

	public static boolean canHighAlch() {
		return Inventory.getCount(true, NATURE_RUNE) >= 1
				&& Skills.getLevel(Skills.MAGIC) >= 55
				&& (Inventory.getCount(true, FIRE_RUNE) >= 5 || hasFireStaff());
	}

	public static boolean canLowAlch() {
		return Inventory.getCount(true, NATURE_RUNE) >= 1
				&& Skills.getLevel(Skills.MAGIC) >= 21
				&& (Inventory.getCount(true, FIRE_RUNE) >= 3 || hasFireStaff());
	}
	
	private static boolean hasFireStaff() {
		return Attacking.playerEquipment[3].getName().toLowerCase().contains("fire")
		    	|| Attacking.playerEquipment[3].getName().toLowerCase().contains("steam")
			|| Attacking.playerEquipment[3].getName().toLowerCase().contains("lava");
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