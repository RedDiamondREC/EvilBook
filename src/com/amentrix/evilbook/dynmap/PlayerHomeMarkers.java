package com.amentrix.evilbook.dynmap;

import java.sql.ResultSet;
import java.sql.Statement;

import org.bukkit.Location;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;

import com.amentrix.evilbook.main.EvilBook;
import com.amentrix.evilbook.sql.SQL;

public class PlayerHomeMarkers {
	private static MarkerSet set;
	private static MarkerIcon icon;

	public static void loadPlayerHomes() {
		set = EvilBook.markerAPI.getMarkerSet("evilbook.player_homes");
		if (set == null) {
			set = EvilBook.markerAPI.createMarkerSet("evilbook.player_homes", "Homes", null, false);
		} else {
			set.setMarkerSetLabel("Homes");
		}
		set.setMinZoom(4);
		icon = EvilBook.markerAPI.getMarkerIcon("bed");	
		try (Statement statement = SQL.connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT home_location, player_name FROM " + SQL.database + ".`evilbook-playerlocations`  WHERE home_location IS NOT null;")) {
				while (rs.next()) {
					String[] location = rs.getString("home_location").split(">");
					set.createMarker("evilbook.player_homes." + rs.getString("player_name"), rs.getString("player_name"), 
							location[3], Double.parseDouble(location[0]),
							Double.parseDouble(location[1]), Double.parseDouble(location[2]), icon, false);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	public static void setPlayerHome(String playerName, Location homeLocation) {
		try {
			Marker homeMarker = set.findMarker("evilbook.player_homes." + playerName);
			if (homeLocation == null) {
				if (homeMarker != null) homeMarker.deleteMarker();
			} else {
				if (homeMarker != null) {
					homeMarker.setLocation(homeLocation.getWorld().getName(), homeLocation.getX(), homeLocation.getY(), homeLocation.getZ());
				} else {
					set.createMarker("evilbook.player_homes." + playerName, playerName, 
							homeLocation.getWorld().getName(), homeLocation.getX(),
							homeLocation.getY(), homeLocation.getZ(), icon, false);
				}
			}
		} catch (Exception exception) {
			EvilBook.logSevere("Failed to set player home marker");
		}
	}
}
