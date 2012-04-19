package com.zalgoproductions.strategies.script.areagenerator;

import com.zalgoproductions.util.Attacking;
import org.powerbot.concurrent.Task;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.Area;
import org.powerbot.game.api.wrappers.Tile;

import java.util.LinkedList;

import static com.zalgoproductions.util.Flags.*;

public class AreaGeneratorTask implements Task {
	public volatile static Area currentRoom = new Area();
	public volatile static Area rangedRoom = new Area();
	private LinkedList<Tile> tilesOpen = new LinkedList<Tile>();
	private LinkedList<Tile> tilesClosed = new LinkedList<Tile>();
	
	public void run() {    
		if(Thread.currentThread().getPriority() != Thread.MIN_PRIORITY) 
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		final Tile root = Players.getLocal().getLocation();    
		
		tilesOpen.clear();
		tilesClosed.clear();		
		
		tilesOpen.offer(root);
		Tile t;
		while ((t = tilesOpen.poll()) != null) {
			tilesClosed.offer(t);
			for(Tile neighbor : getReachableNeighbors(t)) {
				tilesOpen.offer(neighbor);
			}
		}
		currentRoom = new Area(tilesClosed.toArray(new Tile[tilesClosed.size()]));
		
		if(Attacking.isUsingRangedWeapon()) {
			tilesOpen.clear();
			tilesClosed.clear();              		
			
			tilesOpen.offer(root);
			while ((t = tilesOpen.poll()) != null) {
				tilesClosed.offer(t);
				for(Tile neighbor : getRangeAllowedNeighbors(t)) {
					tilesOpen.offer(neighbor);
				}
			}
	
			rangedRoom = new Area(tilesClosed.toArray(new Tile[tilesClosed.size()]));
		}
		
		AreaGeneratorCondition.nextRun = System.currentTimeMillis() + 10000;
	}
	
	private LinkedList<Tile> getReachableNeighbors(Tile tile) {
		final LinkedList<Tile> tiles = new LinkedList<Tile>();
		final int x =  tile.getX() - Game.getBaseX(), y = tile.getY() - Game.getBaseY(), z = tile.getPlane();
		final int[][] flags = Walking.getCollisionFlags(z);
		if(flags == null) {
			return null;
		}
		final int f_x = x - Walking.getCollisionOffset(z).getX(), f_y = y - Walking.getCollisionOffset(z).getY();
		final int here = flags[f_x][f_y];
		final int upper = flags.length - 1;
		if (f_y > 0 && (here & WALL_SOUTH) == 0 && (flags[f_x][f_y - 1] & BLOCKED) == 0) {
			Tile bottom = getMapTileFromLocal(new Tile(x, y - 1, z));
			if (!tilesOpen.contains(bottom) && !tilesClosed.contains(bottom))
				tiles.add(bottom);
		}
		if (f_x > 0 && (here & WALL_WEST) == 0 && (flags[f_x - 1][f_y] & BLOCKED) == 0) {
			Tile left = getMapTileFromLocal(new Tile(x - 1, y, z));
			if (!tilesOpen.contains(left) && !tilesClosed.contains(left))
				tiles.add(left);
		}
		if (f_y < upper && (here & WALL_NORTH) == 0 && (flags[f_x][f_y + 1] & BLOCKED) == 0) {
			Tile top = getMapTileFromLocal(new Tile(x, y + 1, z));
				if (!tilesOpen.contains(top) && !tilesClosed.contains(top))
			tiles.add(top);
		}
		if (f_x < upper && (here & WALL_EAST) == 0 && (flags[f_x + 1][f_y] & BLOCKED) == 0) {
			Tile right = getMapTileFromLocal(new Tile(x + 1, y, z));
				if (!tilesOpen.contains(right) && !tilesClosed.contains(right))
			tiles.add(right);
		}
		if (f_x > 0 && f_y > 0 && (here & (WALL_SOUTHWEST | WALL_SOUTH | WALL_WEST)) == 0
				&& (flags[f_x - 1][f_y - 1] & BLOCKED) == 0
				&& (flags[f_x][f_y - 1] & (BLOCKED | WALL_WEST)) == 0
				&& (flags[f_x - 1][f_y] & (BLOCKED | WALL_SOUTH)) == 0) {
			Tile bottomLeft = getMapTileFromLocal(new Tile(x - 1, y - 1, z));
			if (!tilesOpen.contains(bottomLeft) && !tilesClosed.contains(bottomLeft))
				tiles.add(bottomLeft);
		}
		if (f_x > 0 && f_y < upper && (here & (WALL_NORTHWEST | WALL_NORTH | WALL_WEST)) == 0
				&& (flags[f_x - 1][f_y + 1] & BLOCKED) == 0
				&& (flags[f_x][f_y + 1] & (BLOCKED | WALL_WEST)) == 0
				&& (flags[f_x - 1][f_y] & (BLOCKED | WALL_NORTH)) == 0) {
			Tile topLeft = getMapTileFromLocal(new Tile(x - 1, y + 1, z));
			if (!tilesOpen.contains(topLeft) && !tilesClosed.contains(topLeft))
				tiles.add(topLeft);
		}
		if (f_x < upper && f_y > 0 && (here & (WALL_SOUTHEAST | WALL_SOUTH | WALL_EAST)) == 0
				&& (flags[f_x + 1][f_y - 1] & BLOCKED) == 0
				&& (flags[f_x][f_y - 1] & (BLOCKED | WALL_EAST)) == 0
				&& (flags[f_x + 1][f_y] & (BLOCKED | WALL_SOUTH)) == 0) {
			Tile bottomRight = getMapTileFromLocal(new Tile(x + 1, y - 1, z));
			if (!tilesOpen.contains(bottomRight) && !tilesClosed.contains(bottomRight))
				tiles.add(bottomRight);
		}
		if (f_x > 0 && f_y < upper && (here & (WALL_NORTHEAST | WALL_NORTH | WALL_EAST)) == 0
				&& (flags[f_x + 1][f_y + 1] & BLOCKED) == 0
				&& (flags[f_x][f_y + 1] & (BLOCKED | WALL_EAST)) == 0
				&& (flags[f_x + 1][f_y] & (BLOCKED | WALL_NORTH)) == 0) {
			Tile topRight = getMapTileFromLocal(new Tile(x + 1, y + 1, z));
			if (!tilesOpen.contains(topRight) && !tilesClosed.contains(topRight))
				tiles.add(topRight);
		}
		return tiles;
	}
	
	private LinkedList<Tile> getRangeAllowedNeighbors(Tile tile) {
		final LinkedList<Tile> tiles = new LinkedList<Tile>();
		final int x = tile.getX() - Game.getBaseX(), y = tile.getY() - Game.getBaseY(), z = tile.getPlane();
		final int[][] flags = Walking.getCollisionFlags(z);
		if(flags == null) {
			return null;
		}
		final int f_x = x - Walking.getCollisionOffset(z).getX(), f_y = y - Walking.getCollisionOffset(z).getY();
		final int here = flags[f_x][f_y];
		final int upper = flags.length - 1;
		if (f_y > 0 && (here & WALL_BLOCK_SOUTH) == 0 && (flags[f_x][f_y - 1] & BLOCKED) == 0) {
			Tile bottom = getMapTileFromLocal(new Tile(x, y - 1, z));
			if (!tilesOpen.contains(bottom) && !tilesClosed.contains(bottom))
				tiles.add(bottom);
		}
		if (f_x > 0 && (here & WALL_BLOCK_WEST) == 0 && (flags[f_x - 1][f_y] & BLOCKED) == 0) {
			Tile left = getMapTileFromLocal(new Tile(x - 1, y, z));
			if (!tilesOpen.contains(left) && !tilesClosed.contains(left))
				tiles.add(left);
		}
		if (f_y < upper && (here & WALL_BLOCK_NORTH) == 0 && (flags[f_x][f_y + 1] & BLOCKED) == 0) {
			Tile top = getMapTileFromLocal(new Tile(x, y + 1, z));
			if (!tilesOpen.contains(top) && !tilesClosed.contains(top))
				tiles.add(top);
		}
		if (f_x < upper && (here & WALL_BLOCK_EAST) == 0 && (flags[f_x + 1][f_y] & BLOCKED) == 0) {
			Tile right = getMapTileFromLocal(new Tile(x + 1, y, z));
			if (!tilesOpen.contains(right) && !tilesClosed.contains(right))
				tiles.add(right);
		}
		if (f_x > 0 && f_y > 0 && (here & (WALL_BLOCK_SOUTHWEST | WALL_BLOCK_SOUTH | WALL_BLOCK_WEST)) == 0
				&& (flags[f_x - 1][f_y - 1] & BLOCKED) == 0
				&& (flags[f_x][f_y - 1] & (BLOCKED | WALL_BLOCK_WEST)) == 0
				&& (flags[f_x - 1][f_y] & (BLOCKED | WALL_BLOCK_SOUTH)) == 0) {
			Tile bottomLeft = getMapTileFromLocal(new Tile(x - 1, y - 1, z));
			if (!tilesOpen.contains(bottomLeft) && !tilesClosed.contains(bottomLeft))
				tiles.add(bottomLeft);
		}
		if (f_x > 0 && f_y < upper && (here & (WALL_BLOCK_NORTHWEST | WALL_BLOCK_NORTH | WALL_BLOCK_WEST)) == 0
				&& (flags[f_x - 1][f_y + 1] & BLOCKED) == 0
				&& (flags[f_x][f_y + 1] & (BLOCKED | WALL_BLOCK_WEST)) == 0
				&& (flags[f_x - 1][f_y] & (BLOCKED | WALL_BLOCK_NORTH)) == 0) {
			Tile topLeft = getMapTileFromLocal(new Tile(x - 1, y + 1, z));
			if (!tilesOpen.contains(topLeft) && !tilesClosed.contains(topLeft))
				tiles.add(topLeft);
		}
		if (f_x < upper && f_y > 0 && (here & (WALL_BLOCK_SOUTHEAST | WALL_BLOCK_SOUTH | WALL_BLOCK_EAST)) == 0
				&& (flags[f_x + 1][f_y - 1] & BLOCKED) == 0
				&& (flags[f_x][f_y - 1] & (BLOCKED | WALL_BLOCK_EAST)) == 0
				&& (flags[f_x + 1][f_y] & (BLOCKED | WALL_BLOCK_SOUTH)) == 0) {
			Tile bottomRight = getMapTileFromLocal(new Tile(x + 1, y - 1, z));
			if (!tilesOpen.contains(bottomRight) && !tilesClosed.contains(bottomRight))
				tiles.add(bottomRight);
		}
		if (f_x > 0 && f_y < upper && (here & (WALL_BLOCK_NORTHEAST | WALL_BLOCK_NORTH | WALL_BLOCK_EAST)) == 0
				&& (flags[f_x + 1][f_y + 1] & BLOCKED) == 0
				&& (flags[f_x][f_y + 1] & (BLOCKED | WALL_BLOCK_EAST)) == 0
				&& (flags[f_x + 1][f_y] & (BLOCKED | WALL_BLOCK_NORTH)) == 0) {
			Tile topRight = getMapTileFromLocal(new Tile(x + 1, y + 1, z));
			if (!tilesOpen.contains(topRight) && !tilesClosed.contains(topRight))
				tiles.add(topRight);
		}
		return tiles;
	}
	
	private Tile getMapTileFromLocal(Tile t) {   		
		return new Tile(t.getX() + Game.getBaseX(), t.getY() + Game.getBaseY(), + t.getPlane());
	}
}
