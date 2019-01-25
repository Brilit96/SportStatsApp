package com.blittle.sportstatsapp;
import android.util.Log;

import org.json.JSONObject;


class RiotApiController {
    //Gets the Summoner ID
    String getSummonerID(String summonerName) throws Exception{
        String url = "https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + summonerName;
        String json = new RiotApi().execute(url).get();
        Log.d("JSON OBJECT: ", json);
        //No data was pulled from the API
        if(json == null) {
            return null;
        }

        //Convert json string to JSONObject, then return the summoner ID;
        JSONObject jObj = new JSONObject(json);
        return jObj.getString("id");
    }

    //Gets the Account ID
    String getAccountID(String summonerName) throws Exception {
        String url = "https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + summonerName;
        String json = new RiotApi().execute(url).get();

        //No data was pulled from the API
        if(json == null) {
            return null;
        }

        //Convert json string to JSONObject, then return the summoner ID;
        JSONObject jObj = new JSONObject(json);
        return jObj.getString("accountId");
    }

    String getMatchLists(String accountID) throws Exception {
        String url = "https://na1.api.riotgames.com/lol/match/v4/matchlists/by-account/" + accountID;
        String json = new RiotApi().execute(url).get();

        //No data was pulled from the API
        if(json == null) {
            return null;
        }

        JSONObject jObj = new JSONObject(json);
        return jObj.getJSONArray("matches").getJSONObject(0).getString("gameId");
    }
}
