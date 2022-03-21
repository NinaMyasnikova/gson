/*
package com.myasnikova.centralbank;


import android.provider.DocumentsContract;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Iterator;



public class GsonParser
{
    private static final String JSON_URL = "https://www.cbr-xml-daily.ru/daily_json.js";

    public ListItem parse()
    {
        Gson gson=new Gson();

        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        StringBuilder jSonResult = new StringBuilder();

        try
        {
            URL url = new URL(JSON_URL);
            Log.d("MyLog", "=");
            connection = (HttpURLConnection)url.openConnection();
            Log.d("MyLog", "==");
            connection.connect();
            Log.d("MyLog", "*");

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line = "";

            while((line = bufferedReader.readLine()) != null)
            {
                jSonResult.append(line);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Log.d("MyLog", "/*");
            Log.d("MyLog", "Отсутствует подключение к интернет!");
        }
        finally
        {
            if(connection != null)
            {
                connection.disconnect();
            }

            try
            {
                if(bufferedReader !=null)
                {
                    bufferedReader.close();
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }


        ListItem listItem=gson.fromJson(bufferedReader,ListItem.class);




        return  null;
    }











  */
/* // private static final String filePath = "C:\\Users\\katerina\\Desktop\\jsonTestFile.json";

    public static void main(String[] args) {

        try {
            // считывание файла JSON
            FileReader reader = new FileReader(JSON_URL);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

            // получение строки из объекта
            String firstName = (String) jsonObject.get("firstname");
            System.out.println("The first name is: " + firstName);

            // получение номера из объекта
            long id =  (long) jsonObject.get("id");
            System.out.println("The id is: " + id);

            // получение массива
            JSONArray lang= (JSONArray) jsonObject.get("languages");

            // берем элементы массива
            for(int i=0; i<lang.size(); i++){
                System.out.println("The " + i + " element of the array: "+lang.get(i));
            }
            Iterator i = lang.iterator();

            // берем каждое значение из массива json отдельно
            while (i.hasNext()) {
                JSONObject innerObj = (JSONObject) i.next();
                System.out.println("language "+ innerObj.get("lang") +
                        " with level " + innerObj.get("knowledge"));
            }
            // обрабатываем структуру в объекте
            JSONObject structure = (JSONObject) jsonObject.get("job");
            System.out.println("Into job structure, name: " + structure.get("name"));

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParseException ex) {
            ex.printStackTrace();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
*//*

}
*/
