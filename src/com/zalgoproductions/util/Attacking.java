package com.zalgoproductions.util;

import com.zalgoproductions.strategies.script.areagenerator.AreaGeneratorTask;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

public class Attacking {    	
	private static int[] npcIds = {};
	private static String[] npcNames = {};
	public static boolean utilizeMultiwayCombat = false;
	public static Item[] playerEquipment = {};

	public static boolean useSpec = true;
	private static final int[] specWeaponUsage = {10, 25, 33, 35, 45, 50, 55, 60, 80, 85, 100};
	private static final String[][] specWeapons = {
			{"Rune thrownaxe", "Rod of ivandis"},
			{"Dragon Dagger", "Dragon dagger (p)", "Dragon dagger (p+)",
					"Dragon dagger (p++)", "Dragon Mace", "Dragon Spear",
					"Dragon longsword", "Rune claws"},
			{"Dragon Halberd"},
			{"Magic Longbow"},
			{"Magic Composite Bow"},
			{"Dragon Claws", "Abyssal Whip", "Granite Maul", "Darklight",
					"Barrelchest Anchor", "Armadyl Godsword"},
			{"Magic Shortbow"},
			{"Dragon Scimitar", "Dragon 2H Sword", "Zamorak Godsword",
					"Korasi's sword"},
			{"Dorgeshuun Crossbow", "Bone Dagger", "Bone Dagger (p+)",
					"Bone Dagger (p++)"},
			{"Brine Sabre"},
			{"Bandos Godsword", "Dragon Battleaxe", "Dragon Hatchet",
					"Seercull Bow", "Excalibur", "Enhanced excalibur",
					"Ancient Mace", "Saradomin sword"}};
	
	
	private static final int maxRadius = Safespot.useSafespot ? 15 : Integer.MAX_VALUE;

	public static final Filter<NPC> NPC_FILTER =
			new Filter<NPC>() {
				public boolean accept(NPC npc) {
					if(npc.validate() && npc.getHpPercent() > 0 && Calculations.distance(Players.getLocal().getLocation(), npc.getLocation()) < maxRadius &&
							(utilizeMultiwayCombat || !npc.isInCombat() && npc.getInteracting() == null) && canAttack(npc)) {
						for(int id : npcIds) {
							if (npc.getId() == id) {
								return true;
							}
						}
						for(String name : npcNames) {
							if(npc.getName().toLowerCase().contains(name.toLowerCase())) {
								return true;
							}
						}
					}
					return false;
				}
				
				private boolean canAttack(NPC npc) {
					return isUsingRangedWeapon() ? 
							AreaGeneratorTask.rangedRoom.npoints == 0 || AreaGeneratorTask.rangedRoom.contains(npc.getLocation()) :
							AreaGeneratorTask.currentRoom.npoints == 0 || AreaGeneratorTask.currentRoom.contains(npc.getLocation());
				}
			};
	
	public static void setNPCIds(int[] ids) {
		npcIds = ids;
	}
	
	public static void setNPCNames(String[] names) {
		npcNames = names;
	}


	public static void initializeEquipment() {
		if (Tabs.getCurrent() != Tabs.EQUIPMENT) {
			Tabs.EQUIPMENT.open();
			Time.sleep(Random.nextInt(900, 1500));
		}
		final Widget equipmentWidget = Widgets.get(387);
		final WidgetChild[] equip = equipmentWidget.getChildren();
		final Item[] items = new Item[12];
		for (int i = 0; i < 11; i++) {
			items[i] = new Item(equip[i * 3 + 6]);
		}
		items[11] = new Item(equip[45]);
		playerEquipment = items;  		

		//6 => Helm
		//9 => Cape
		//12 => Neck
		//15 => Weapon
		//18 => Chest
		//21 => Shield
		//24 => Legs
		//27 => Gloves
		//30 => Shoes
		//33 => Ring
		//36 => Ammo
		//45 => Aura
	}

	public static boolean isUsingRangedWeapon() {
		return playerEquipment[3].getName().toLowerCase().contains("dart") || playerEquipment[3].getName().toLowerCase().contains("knife") ||
				playerEquipment[3].getName().toLowerCase().contains("thrownaxe") || playerEquipment[3].getName().toLowerCase().contains("bow");
	}

	public static void setSpecialAttack(boolean set) {
		if (isSpecialEnabled() != set) {
			Tabs.ATTACK.open();
			final WidgetChild specBar = Widgets.get(884, 4);
			if (specBar != null && isSpecialEnabled() != set) {
				specBar.click(true);
			}
		}
	}

	public static boolean shouldSpecial() {
		if(playerEquipment[3] != null && useSpec) {
			for (int i = 0; i < specWeapons.length; i++) {
				for (int j = 0; j < specWeapons[i].length; j++) {
					if (specWeapons[i][j].equalsIgnoreCase(playerEquipment[3].getName())) {
						return getSpecialBarEnergy() >= specWeaponUsage[i];
					}
				}
			}
		}
		return false;
	}

	private static boolean isSpecialEnabled() {
		return Settings.get(301) == 1;
	}

	private static int getSpecialBarEnergy() {
		return Settings.get(300) / 10;
	}
}
