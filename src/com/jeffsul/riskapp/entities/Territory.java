package com.jeffsul.riskapp.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.jeffsul.riskapp.players.Player;

/**
 * Territory represents one of the locations on the map, abstractly and by
 * managing the associated UI button element.
 */
public class Territory {
	public static final int DEFAULT_UNITS = 3;	
		
	public String name;
	public int x;
	public int y;
	public int units = DEFAULT_UNITS;
	public Player owner;
	
	private Set<Territory> connectors;
	//private final Button btn;
	
	private ArrayList<Listener> listeners;
	
	public interface Listener {
		public void onUnitsChanged(Player player, int units);
		public void onOwnerChanged(Player oldOwner, Player newOwner);
	}
	
	public Territory(String name, int x, int y) {
		this.name = name;
		this.x = x;
		this.y = y;
		
		listeners = new ArrayList<Listener>();
		connectors = new HashSet<Territory>();
	}
	
	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	// TODO(jeffsul): Implement custom listener.
	/*public void addMouseListener(OnClickListener listener, OnLongClickListener listener2) {
		btn.setOnClickListener(listener);
		btn.setOnLongClickListener(listener2);
	}*/
	
	public void addConnector(Territory connector) {
		connectors.add(connector);
	}
	
	public void addUnits(int num) {
		setUnits(units + num);
	}
	
	public void setUnits(int num) {
		if (num != units) {
			units = num;
			for (Listener listener : listeners) {
				listener.onUnitsChanged(owner, units);
			}
		}
	}
	
	public void setOwner(Player newOwner) {
		for (Listener listener : listeners) {
			listener.onOwnerChanged(owner, newOwner);
		}
		owner = newOwner;
	}
	
	public Territory[] getConnectors() {
		return connectors.toArray(new Territory[connectors.size()]);
	}
	
	public Territory[] getFriendlyConnectors(Player player) {
		ArrayList<Territory> friends = new ArrayList<Territory>();
		for (Territory conn : connectors) {
			if (conn.owner == player) {
				friends.add(conn);
			}
		}
		Territory[] friendTerrits = new Territory[friends.size()];
		friends.toArray(friendTerrits);
		return friendTerrits;
	}
	
	public Territory[] getEnemyConnectors(Player player) {
		ArrayList<Territory> enemies = new ArrayList<Territory>();
		for (Territory conn : connectors) {
			if (conn.owner != player) {
				enemies.add(conn);
			}
		}
		Territory[] enemyTerrits = new Territory[enemies.size()];
		enemies.toArray(enemyTerrits);
		return enemyTerrits;
	}
	
	/**
	 * To check whether a territory is connected (i.e., can be attacked from) this one.
	 * @param territ
	 * @return true if territ is connected to this one
	 */
	public boolean isConnecting(Territory territ) {
		for (Territory conn : connectors) {
			if (conn == territ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isFortifyConnecting(Territory target) {
		if (isConnecting(target)) {
			return true;
		}
		
		HashMap<Territory, Boolean> checked  = new HashMap<Territory, Boolean>();
		ArrayList<Territory> territs = new ArrayList<Territory>();
		territs.add(this);
		while (territs.size() > 0) {
			Territory territ = territs.remove(0);
			if (target == territ) {
				return true;
			}
			checked.put(territ, true);
			
			Territory[] conns = territ.getFriendlyConnectors(owner);
			for (Territory conn : conns) {
				if (!checked.containsKey(conn)) {
					territs.add(conn);
				}
			}
		}
		return false;
	}
}
