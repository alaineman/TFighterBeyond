package com.zalgoproductions.util;

import com.zalgoproductions.strategies.script.areagenerator.AreaGeneratorCondition;
import com.zalgoproductions.strategies.script.areagenerator.AreaGeneratorTask;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

import java.util.HashMap;

public class Attacking {
	
	private static int[] npcIds = {};
	private static String[] npcNames = {};
	private static final int maxRadius = Safespot.useSafespot ? 15 : Integer.MAX_VALUE;
	
	
	public static boolean utilizeMultiwayCombat = false;
	public static Item[] playerEquipment = {};
	public static boolean useSpec = true;

	public static final Filter<NPC> NPC_FILTER = new Filter<NPC>() {
		public boolean accept(NPC npc) {
			if (npc.validate() && npc.getHpPercent() > 0
					&& Calculations.distance(Players.getLocal().getLocation(),
							npc.getLocation()) < maxRadius
					&& (utilizeMultiwayCombat || !npc.isInCombat()
							&& npc.getInteracting() == null) && (!AreaGeneratorCondition.doGeneration || canAttack(npc))) {
				for (int id : npcIds) {
					if (npc.getId() == id) {
						return true;
					}
				}
				final String NPC_NAME = npc.getName().toLowerCase();
				for (String name : npcNames) {
					// npcNames should all be lowercase (see setNPCNames())
					if (NPC_NAME.contains(name)) {
						return true;
					}
				}
			}
			return false;
		}
	};

	public static final HashMap<String, Integer> SPECIAL_WEAPONS = new HashMap<String, Integer>() {
		{
			put("rune thrownaxe", 10);
			put("rod of ivandis", 10);
			
			put("dragon dagger", 25);
			put("dragon dagger (p)", 25);
			put("dragon dagger (p+)", 25);
			put("dragon dagger (p++)", 25);
			put("dragon longsword", 25);
			put("dragon mace", 25);
			put("dragon spear", 25);
			put("rune claws", 25);
			
			put("dragon halberd", 33);
			
			put("magic longbow", 35);
			
			put("magic composite bow", 45);
			
			put("abyssal whip", 50);
			put("armadyl godsword", 50);
			put("barrelchest anchor", 50);
			put("darklight", 50);
			put("dragon claws", 50);
			put("granite maul", 50);
			
			put("magic shortbow", 55);
			
			put("dragon 2h sword", 60);
			put("dragon scimitar", 60);
			put("korasi's sword", 60);
			put("zamorack godsword", 60);
			
			put("bone dagger", 80);
			put("bone dagger (p+)", 80);
			put("bone dagger (p++)", 80);
			put("dorgeshuun crossbow", 80);
			
			put("brine sabre", 85);
			
			put("ancient mace", 100);
			put("bandos godsword", 100);
			put("dragon battleaxe", 100);
			put("dragon hatchet", 100);
			put("enhanced excalibur", 100);
			put("exalibur", 100);
			put("saradomin godsword", 100);
			put("seercull bow", 100);
		}
	};

	private static boolean canAttack(NPC npc) {
		return isUsingRangedWeapon() ? 
				AreaGeneratorTask.rangedRoom.contains(npc.getLocation()) :
				AreaGeneratorTask.currentRoom.contains(npc.getLocation());
	}

	public static void setNPCIds(int[] ids) {
		npcIds = ids;
	}

	public static void setNPCNames(String[] names) {
		if (names != null) {
			npcNames = names;
			for (int i = 0; i < npcNames.length; i++) {
				npcNames[i] = npcNames[i].toLowerCase();
			}
		}
	}

	public static void initializeEquipment() {
		if (Tabs.getCurrent() != Tabs.EQUIPMENT) {
			Tabs.EQUIPMENT.open();
			Sleeping.waitForTabChange(Tabs.EQUIPMENT);
		}
		final Widget equipmentWidget = Widgets.get(387);
		final WidgetChild[] equip = equipmentWidget.getChildren();
		final Item[] items = new Item[12];
		for (int i = 0; i < 11; i++) {
			items[i] = new Item(equip[i * 3 + 6]);
		}
		items[11] = new Item(equip[45]);
		playerEquipment = items;

		// 6 => Helm
		// 9 => Cape
		// 12 => Neck
		// 15 => Weapon
		// 18 => Chest
		// 21 => Shield
		// 24 => Legs
		// 27 => Gloves
		// 30 => Shoes
		// 33 => Ring
		// 36 => Ammo
		// 45 => Aura
	}

	public static boolean isUsingRangedWeapon() {
		final String RANGE_EQUIPMENT_NAME = playerEquipment[3].getName().toLowerCase();
		return RANGE_EQUIPMENT_NAME.contains("dart")
				|| RANGE_EQUIPMENT_NAME.contains("knife")
				|| RANGE_EQUIPMENT_NAME.contains("thrownaxe")
				|| RANGE_EQUIPMENT_NAME.contains("bow");
	}

	public static void setSpecialAttack(boolean set) {
		if (isSpecialEnabled() != set) {
			Tabs.ATTACK.open();
			Sleeping.waitForTabChange(Tabs.ATTACK);
			final WidgetChild specBar = Widgets.get(884, 4);
			if (specBar != null && isSpecialEnabled() != set) {
				specBar.click(true);
			}
		}
	}

	public static boolean shouldSpecial() {
		if (playerEquipment[3] != null && useSpec) {
			final Integer SPEC_ENERGY_NEEDED = SPECIAL_WEAPONS.get(playerEquipment[3].getName().toLowerCase());
			return SPEC_ENERGY_NEEDED != null && getSpecialBarEnergy() >= SPEC_ENERGY_NEEDED;
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