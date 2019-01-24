package com.blittle.sportstatsapp;
import android.util.Log;

import org.json.JSONObject;


public class RiotApiController {
    //Gets the Summoner ID
    public String getSummonerID(String summonerName) throws Exception{
        String url = "https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + summonerName;
        String json = new RiotApi().execute(url).get();
        Log.d("JSON OBJECT: ", json);
        //No data was pulled from the API
        if(json == null) {
            return null;
        }

        //Convert json string to JSONObject, then return the summoner ID;
        JSONObject jObj = new JSONObject(json);
        String summonerID = jObj.getString("id");
        return summonerID;
    }

    //Gets the Account ID
    public String getAccountID(String summonerName) throws Exception {
        String url = "https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/" + summonerName;
        String json = new RiotApi().execute(url).get();

        //No data was pulled from the API
        if(json == null) {
            return null;
        }

        //Convert json string to JSONObject, then return the summoner ID;
        JSONObject jObj = new JSONObject(json);
        String accountID = jObj.getString("accountId");
        return accountID;
    }
}
