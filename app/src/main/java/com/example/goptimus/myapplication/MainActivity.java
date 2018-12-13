package com.example.goptimus.myapplication;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    LayoutInflater inflater;
 //   String NOMS[] = {"adama goudiaby", "adama goudiaby"};
 //   String[] ADRESSE = {"Grand Yoff", "Grand Yoff"};
 //   String[] MONTANTS = {"500", "5000"};

    ArrayList<String> NOMS;
    ArrayList<String> ADRESSE;
    ArrayList<String> MONTANTS;
    ArrayList<String> TELS;
    ArrayList<Integer> ETATS;

    String str = "[{'nom': 'Adama Goudiaby','adresse': 'Grand Yoff','montant': '5000','tel': '776537639', 'etat':1},{'nom': 'Adama Goudiaby','adresse': 'Grand Yoff','montant': '5000','tel': '776537639','etat':2},{'nom': 'Adama Goudiaby','adresse': 'Grand Yoff','montant': '5000','tel': '776537639','etat':3}]";

    ListView listView;
    AdapterClient adapterClient;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String TAG = "MainActivity";

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console.
     */
    String SENDER_ID = "Your-Sender-ID";

    /**
     * Tag used on log messages.
     */

    TextView mDisplay;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid="";


    String PROJECT_NUMBER = "571912877861";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NOMS = new ArrayList<String>();
        ADRESSE = new ArrayList<String>();
        MONTANTS = new ArrayList<String>();
        ETATS = new ArrayList<Integer>();
        TELS = new ArrayList<String>();

        listView = (ListView) findViewById(R.id.listView);

        changeList(0);

        context = getApplicationContext();
    }





    public void changeList(int n){
        try {
            JSONArray jsonArray = new JSONArray(str);
            for (int i = 0; i < jsonArray.length() ; i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if(n==0){
                    NOMS.add(jsonObject.getString("nom"));
                    ADRESSE.add(jsonObject.getString("adresse"));
                    MONTANTS.add(jsonObject.getString("montant"));
                    ETATS.add(jsonObject.getInt("etat"));
                    TELS.add(jsonObject.getString("tel"));
                }
                else if(jsonObject.getInt("etat")==n){
                    NOMS.add(jsonObject.getString("nom"));
                    ADRESSE.add(jsonObject.getString("adresse"));
                    MONTANTS.add(jsonObject.getString("montant"));
                    ETATS.add(jsonObject.getInt("etat"));
                    TELS.add(jsonObject.getString("tel"));
                }
            }

            adapterClient = new AdapterClient(this,NOMS,ADRESSE,MONTANTS,ETATS,TELS);
            listView.setAdapter(adapterClient);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public  void  home(View view){
        NOMS.clear();
        ADRESSE.clear();
        MONTANTS.clear();
        ETATS.clear();
        TELS.clear();
        adapterClient.notifyDataSetChanged();
        changeList(0);
    }

    public  void  rendezvousNonFixe(View view){
        NOMS.clear();
        ADRESSE.clear();
        MONTANTS.clear();
        ETATS.clear();
        TELS.clear();
        adapterClient.notifyDataSetChanged();
        changeList(1);
    }

    public  void  rendezvousFixe(View view){
        NOMS.clear();
        ADRESSE.clear();
        MONTANTS.clear();
        ETATS.clear();
        TELS.clear();
        adapterClient.notifyDataSetChanged();
        changeList(2);
    }

    public  void  recouvert(View view){
        NOMS.clear();
        ADRESSE.clear();
        MONTANTS.clear();
        ETATS.clear();
        TELS.clear();
        adapterClient.notifyDataSetChanged();
        changeList(3);
    }

    public void  handleHttp(final String requestUrl, final Map<String, String> postMap) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {

            @TargetApi(Build.VERSION_CODES.N)
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(String response) {
                Log.e("Volley Result", "=============>" + response); //the response contains the result from the server, a json string or any other object returned by your server;
                /*try {
                    JSONArray jsonArray = new JSONArray(response);
                    Log.d("indexHistory","----------------"+indexHistory+"------------------");
                    Log.d("lastID","----------------"+lastID+"------------------");
                    lastID = (jsonArray.getJSONObject(jsonArray.length() -1)).getString("id");
                    Log.d("lastID","----------------"+lastID+"------------------");

                    for (int i = indexHistory, max = indexHistory + Maxrow, j= 0 ; (j < jsonArray.length() ) && (i < max); i++, j++){
                        JSONObject obj = jsonArray.getJSONObject(j);

                        indexHistory = indexHistory + 1;
                        history.put(obj);
                        String traitement = obj.getString("traitement");
                        String infoclient = obj.getString("infoclient");
                        String montant = obj.getString("montant");
                        String dateoperation = obj.getString("dateoperation");

                        String[] splitStr = dateoperation.split("\\s+");

                        if(targetDate.compareTo(splitStr[0]) != 0) {
                            longrowAdapter(splitStr[0]);
                            targetDate = splitStr[0];
                        }
                        rowAdapter(traitement,infoclient,montant,splitStr[1]);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } */
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //..... Add as many key value pairs in the map as necessary for your request
                //Log.d("postMap",(postMap.toString()));
                return postMap;
            }
        };
        //make the request to your server as indicated in your request url

        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

}
