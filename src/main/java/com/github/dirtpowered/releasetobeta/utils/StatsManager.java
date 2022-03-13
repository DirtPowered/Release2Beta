package com.github.dirtpowered.releasetobeta.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.dirtpowered.releasetobeta.ReleaseToBeta;
import com.github.dirtpowered.releasetobeta.data.Block;
import com.github.dirtpowered.releasetobeta.data.Item;
import com.github.steveice10.mc.protocol.data.MagicValues;
import com.github.steveice10.mc.protocol.data.game.statistic.BreakBlockStatistic;
import com.github.steveice10.mc.protocol.data.game.statistic.BreakItemStatistic;
import com.github.steveice10.mc.protocol.data.game.statistic.CraftItemStatistic;
import com.github.steveice10.mc.protocol.data.game.statistic.GenericStatistic;
import com.github.steveice10.mc.protocol.data.game.statistic.Statistic;
import com.github.steveice10.mc.protocol.data.game.statistic.UseItemStatistic;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StatsManager {
	private static final HashMap<Integer, String> mapping = new HashMap<>();
	
	private final File folder;
	private final ReleaseToBeta rtb;
	private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	private HashMap<String, HashMap<Integer, Integer>> stats = new HashMap<>();
	public StatsManager(File folder, ReleaseToBeta rtb) {
		this.folder = folder;
		this.rtb = rtb;
	}
	
	public void saveStats() {
		Set<String> keySet = stats.keySet();
		for (String username : keySet) {
			try {
				saveStats(username);
			} catch(IOException e) {
				Utils.printException(rtb.getLogger(), e, "Unable to save stats!");
			}
		}
	}
	
	public void saveStats(String username) throws IOException {
		if(!folder.exists())
			folder.mkdirs();
		File file = new File(folder, username+".json");
		if(!file.exists())
			file.createNewFile();
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
		gson.toJson(stats.get(username), writer);
		writer.close();
	}
	
	public void loadStats() {
		if(!folder.exists())
			return;
		stats = new HashMap<>();
		File[] files = folder.listFiles();
		for (File file : files) {
			if(file.getName().endsWith(".json")) {
				String username = Utils.safeSubstring(file.getName(), 0, -5);
				try {
					loadStats(username);
				} catch(IOException e) {
					Utils.printException(rtb.getLogger(), e, "Unable to load stats!");
				}
			}
		}
	}
	public boolean loadStats(String username) throws IOException {
		File file = new File(folder, username+".json");
		if(!file.exists())
			return false;
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
		HashMap<String, Double> map = gson.fromJson(reader,HashMap.class);
		HashMap<Integer, Integer> stats = new HashMap<>();
		for (String str : map.keySet()) {
			try {
				stats.put(Integer.valueOf(str), map.get(str).intValue());
			} catch(NumberFormatException e) {}
		}
		this.stats.put(username, stats);
		reader.close();
		return true;
	}
	
	public void addStats(String username, int statId, int value) {
		if(!stats.containsKey(username))
			stats.put(username, new HashMap<>());
		Map<Integer, Integer> stats = this.stats.get(username);
		if(!stats.containsKey(statId)) {
			stats.put(statId, value);
			return;
		}
		stats.put(statId, stats.get(statId)+value);
	}

	private Statistic translateStatistic(int id) {
		if(mapping.containsKey(id)) {
			return MagicValues.key(Statistic.class, mapping.get(id));
		}
		
		if(id>=16973824) {
			return new BreakItemStatistic("minecraft."+Item.id2name(id - 16973824));
		}
		if(id>=16908288) {
			return new UseItemStatistic("minecraft."+Item.id2name(id - 16908288));
		}
		
		if(id>=16842752) {
			return new CraftItemStatistic("minecraft."+Item.id2name(id - 16842752));
		}
		
		if(id>=16777216) {
			return new BreakBlockStatistic("minecraft."+Item.id2name(id - 16777216));
		}
		
		return null; // Unknown.
	}
	
	public Map<Statistic, Integer> getPlayerStats(String username) {
		HashMap<Statistic, Integer> result = new HashMap<>();
		if(!stats.containsKey(username))
			return result;
		Map<Integer, Integer> stats = this.stats.get(username);
		Set<Integer> keySet = stats.keySet();
		for (int id : keySet) {
			Statistic statistic = translateStatistic(id);
			if(statistic != null) {
				result.put(statistic, statistic == GenericStatistic.DAMAGE_DEALT || 
									  statistic == GenericStatistic.DAMAGE_TAKEN
									  ? stats.get(id) * 5 : stats.get(id));
			}
		}
		return result;
	}
	
	static {
		mapping.put(1004, "stat.leaveGame");
		mapping.put(1100, "stat.playOneMinute");
		mapping.put(2000, "stat.walkOneCm");
		mapping.put(2002, "stat.fallOneCm");
		mapping.put(2003, "stat.climbOneCm");
		mapping.put(2004, "stat.flyOneCm");
		mapping.put(2005, "stat.walkUnderWaterOneCm");
		mapping.put(2006, "stat.minecartOneCm");
		mapping.put(2007, "stat.boatOneCm");
		mapping.put(2008, "stat.pigOneCm");
		mapping.put(2001, "stat.swimOneCm");
		mapping.put(2010, "stat.jump");
		mapping.put(2011, "stat.drop");
		mapping.put(2020, "stat.damageDealt");
		mapping.put(2021, "stat.damageTaken");
		mapping.put(2022, "stat.deaths");
		mapping.put(2023, "stat.mobKills");
		mapping.put(2024, "stat.playerKills");
		mapping.put(2025, "stat.fishCaught");
	}
}

