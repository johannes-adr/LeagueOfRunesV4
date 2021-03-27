package de.jadr;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import de.jadr.ImprovedClientAPI;
import de.jadr.Proton;
import de.jadr.ddragon.Champion;
import de.jadr.ddragon.ChampionList;
import de.jadr.ddragon.ChampionList.Language;
import de.jadr.loggetter.LOGChampion;
import de.jadr.loggetter.LOGChampion.Rank;
import de.jadr.net.JadrHTTPServer.FileReader;

public class LOR {

	public static void main(String[] args) throws IOException {
		ImprovedClientAPI api = new ImprovedClientAPI();
		ChampionList list = api.IMPROVED.getLolChampionList(Language.en_GB);
		//api.IMPROVED.waitForConnection();

		Proton p = new Proton(-1, 1200, 800, "http");
		p.getWindow().setAppImage("/http/Domination.png");

		p.getHTTPServer().addRestPostAPI("/champion/list", (json, e) -> {
			JSONObject res = new JSONObject();

			ArrayList<String> arr = new ArrayList<String>(list.champions.length);
			for (Champion c : list.champions) {
				arr.add(c.id);
			}
			res.put("champions", arr);
			return res;
		});

		p.getHTTPServer().addRestPostAPI("/champion/single", (json, e) -> {
			try {
				
				JSONObject total = new JSONObject();
				JSONObject log = new JSONObject();
				String name = json.getString("name");
				boolean found = false;
				for (Champion c : list.champions) {
					if(c.id.equalsIgnoreCase(name)) {
						found = true;
						break;
					}
				}
				if(!found) {
					JSONObject err = new JSONObject();
					err.put("ERROR", "Champion not found!");
					return err;
				}
				LOGChampion c = api.IMPROVED.getLeagueOfGraphsChampionDetails(name, Rank.PLATIN);

				log.put("name", c.getName());
				log.put("winrate", c.getWinrate());
				log.put("banrate", c.getBanrate());
				log.put("games", c.getAnalyzedGames());
				log.put("playrate", c.getPlayrate());
				log.put("goodagianst", c.getChampstThatGetCountered());
				log.put("counters", c.getChampsThatCounters());
				log.put("rune", c.getMostPopularRune().primary.runecomponents.get(0).name);
				total.put("log", log);
				
				Champion ddc = list.getByDataName(name);
				
				JSONObject ddragon = new JSONObject();
				ddragon.put("img", ddc.image.full);
				
				total.put("data", ddragon);
				return total;
			} catch (Exception e1) {
			}
			return new JSONObject();
		});
		
		p.getHTTPServer().addRestPostAPI("/lol/setrune", (json, e)->{
			try {
				System.out.println(json);
				System.out.println(json.getString("name"));
				LOGChampion c = api.IMPROVED.getLeagueOfGraphsChampionDetails(json.getString("name"), Rank.PLATIN);
				api.IMPROVED.setRunePage(LORUtils.convertLogRuneToRune(c.getMostPopularRune(), "LOR: " + json.getString("name")));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return new JSONObject();
		});
		
		

	}

}
