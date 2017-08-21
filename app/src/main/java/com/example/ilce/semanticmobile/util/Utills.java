package com.example.ilce.semanticmobile.util;

import com.example.ilce.semanticmobile.model.User;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ilce on 8/10/2017.
 */

public class Utills  {

    public static boolean  sendPostRequest(String requestUrl, String payload) {

        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(payload);
            writer.close();

            int response = connection.getResponseCode();
            connection.disconnect();
            if (response==HttpURLConnection.HTTP_CREATED || response==HttpURLConnection.HTTP_OK) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static List<User> getUsers(String requestUrl) {

              HttpURLConnection connection = null;
              try {
                  URL url = new URL(requestUrl);
                  connection = (HttpURLConnection) url.openConnection();
                  int response = connection.getResponseCode();

                  if (response==HttpURLConnection.HTTP_OK) {
                      StringBuilder sb = new StringBuilder();
                      try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                          String line;
                          while ((line=reader.readLine())!=null) {
                              sb.append(line);
                          }
                      } catch (IOException e) {
                          e.printStackTrace();
                      }
                      JSONArray data = new JSONArray(sb.toString());
                      return convertJson(data);
                  }

              }  catch (Exception e) {
                  throw new RuntimeException(e.getMessage());
              } finally {
                  connection.disconnect();
              }

              return null;
    }


    public static List<User> convertJson(JSONArray data) throws JSONException {
        List<User> users = new ArrayList<>();
        for (int i=0;i<data.length();++i) {
            String name = data.getJSONObject(i).getString("username");
            JSONArray queries = data.getJSONObject(i).getJSONArray("queries");
            User user = new User(name,queries.length());
            users.add(user);
        }
      return users;
    }

    public static double[] getEastingNorthing(double Lat, double Lon) {
        int Zone = (int) Math.floor(Lon / 6 + 31);

        double[] result = new double[2];

        char Letter;

        if (Lat<-72) Letter='C';
        else if (Lat<-64) Letter='D';
        else if (Lat<-56) Letter='E';
        else if (Lat<-48) Letter='F';
        else if (Lat<-40) Letter='G';
        else if (Lat<-32) Letter='H';
        else if (Lat<-24) Letter='J';
        else if (Lat<-16) Letter='K';
        else if (Lat<-8) Letter='L';
        else if (Lat<0) Letter='M';
        else if (Lat<8) Letter='N';
        else if (Lat<16) Letter='P';
        else if (Lat<24) Letter='Q';
        else if (Lat<32) Letter='R';
        else if (Lat<40) Letter='S';
        else if (Lat<48) Letter='T';
        else if (Lat<56) Letter='U';
        else if (Lat<64) Letter='V';
        else if (Lat<72) Letter='W';
        else Letter='X';

       double Easting=0.5*Math.log((1+Math.cos(Lat*Math.PI/180)*Math.sin(Lon*Math.PI/180-(6*Zone-183)*Math.PI/180))/
               (1-Math.cos(Lat*Math.PI/180)*Math.sin(Lon*Math.PI/180-(6*Zone-183)*Math.PI/180)))
               *0.9996*6399593.62/Math.pow((1+Math.pow(0.0820944379, 2)
               *Math.pow(Math.cos(Lat*Math.PI/180), 2)), 0.5)*(1+ Math.pow(0.0820944379,2)/2*Math.pow((0.5*Math.log((1+Math.cos(Lat*Math.PI/180)*
               Math.sin(Lon*Math.PI/180-(6*Zone-183)*Math.PI/180))/(1-Math.cos(Lat*Math.PI/180)*Math.sin(Lon*Math.PI/180-(6*Zone-183)*Math.PI/180)))),2)
               *Math.pow(Math.cos(Lat*Math.PI/180),2)/3)+500000;

        Easting=Math.round(Easting*100)*0.01;


        double Northing = (Math.atan(Math.tan(Lat*Math.PI/180)/Math.cos((Lon*Math.PI/180-(6*Zone -183)*Math.PI/180)))-Lat*Math.PI/180)*0.9996*6399593.625/Math.sqrt(1+0.006739496742*
                Math.pow(Math.cos(Lat*Math.PI/180),2))*(1+0.006739496742/2*Math.pow(0.5*Math.log((1+Math.cos(Lat*Math.PI/180)*Math.sin((Lon*Math.PI/180-(6*Zone -183)*Math.PI/180)))/
                (1-Math.cos(Lat*Math.PI/180)*Math.sin((Lon*Math.PI/180-(6*Zone -183)*Math.PI/180)))),2)*Math.pow(Math.cos(Lat*Math.PI/180),2))
                +0.9996*6399593.625*(Lat*Math.PI/180-0.005054622556*(Lat*Math.PI/180+Math.sin(2*Lat*Math.PI/180)/2)
                +4.258201531e-05*(3*(Lat*Math.PI/180+Math.sin(2*Lat*Math.PI/180)/2)+Math.sin(2*Lat*Math.PI/180)*
                Math.pow(Math.cos(Lat*Math.PI/180),2))/4-1.674057895e-07*(5*(3*(Lat*Math.PI/180+Math.sin(2*Lat*Math.PI/180)/2)+Math.sin(2*Lat*Math.PI/180)*
                Math.pow(Math.cos(Lat*Math.PI/180),2))/4+Math.sin(2*Lat*Math.PI/180)*Math.pow(Math.cos(Lat*Math.PI/180),2)
                *Math.pow(Math.cos(Lat*Math.PI/180),2))/3);
        if (Letter<'M')
            Northing = Northing + 10000000;


        Northing=Math.round(Northing*100)*0.01;

        result[0]=Easting;
        result[1]=Northing;


        return result;

    }

}
