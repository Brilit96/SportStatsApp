package com.blittle.sportstatsapp;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

//This class is used to retrieve the data from Riot's API and return a string of the result
public class RiotApi extends AsyncTask<String, Void, String> {
    private String ApiKey = BuildConfig.ApiKey;

    @Override
    protected String doInBackground(String... params){
        try {
            String url = params[0];
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Riot-Token", ApiKey);

            int responseCode = con.getResponseCode();

            System.out.print(responseCode);
            switch (responseCode) {
                case 200:
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine + "\n");
                    }
                    in.close();
                    return response.toString();
                default:
                    break;
            }
        } catch (MalformedURLException e) {
            //e.printStackTrace();
            return "URL is formatted incorrectly";
        } catch (ProtocolException e) {
            // TODO Auto-generated catch block
            return "Protocol Exception";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return "IOException";
        }
        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
