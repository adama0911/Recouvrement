package com.example.goptimus.myapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdapterClient extends BaseAdapter {

    LayoutInflater inflater;
    Context context;
    ArrayList<String> NOMS;
    ArrayList<String> ADRESSE;
    ArrayList<String> MONTANTS;
    ArrayList<Integer> ETATS;
    ArrayList<String> TELS;
    AlertDialog.Builder builder;

    String urlRv = "";
    String urlRc = "";

    public  AdapterClient (Context c, ArrayList<String> noms, ArrayList<String> adresses,ArrayList<String> montant, ArrayList<Integer> etats,ArrayList<String> tels){
        this.context = c;
        this.NOMS = noms;
        this.ADRESSE = adresses;
        this.MONTANTS = montant;
        this.ETATS = etats;
        this.TELS = tels;
        inflater = (LayoutInflater.from(context));
        builder = new AlertDialog.Builder(context);
    }
    @Override
    public int getCount() {
        return  MONTANTS.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LinearLayout v = (LinearLayout) inflater.inflate(R.layout.row_item, null);
        TextView    nom = v.findViewById(R.id.nom);
        TextView    ad  = v.findViewById(R.id.adresse);
        TextView    mtn = v.findViewById(R.id.montant);
        TextView    etat = v.findViewById(R.id.etat);
        TextView    numero = v.findViewById(R.id.numero);


        nom.setText("Nom :"+ NOMS.get(i));
        ad.setText("Adresse :"+ADRESSE.get(i));
        mtn.setText("Montant :" + MONTANTS.get(i));
        numero.setText("Tel : "+TELS.get(i));
        int res = 0;

        if((ETATS.get(i)).intValue()==1)
            res = R.drawable.danger;
        else if ((ETATS.get(i)).intValue()==2)
            res = R.drawable.warning;

        else if ((ETATS.get(i)).intValue() ==3)
            res = R.drawable.success;

        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            etat.setBackgroundDrawable(ContextCompat.getDrawable(context, res) );
        } else {
            etat.setBackground(ContextCompat.getDrawable(context, res));
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((ETATS.get(i)).intValue() ==1) {
                    LinearLayout vi = (LinearLayout) inflater.inflate(R.layout.cleint_sans_renvez_vous, null);
                    TextView name = vi.findViewById(R.id.name);
                    TextView amount = vi.findViewById(R.id.amount);
                    TextView adress = vi.findViewById(R.id.adress);
                    TextView tel = vi.findViewById(R.id.tel);

                    Button appeler = vi.findViewById(R.id.appeler);
                    Button retour = vi.findViewById(R.id.retour);
                    Button rv = vi.findViewById(R.id.rv);

                    name.setText("Nom : " + NOMS.get(i));
                    amount.setText("Montant : " + MONTANTS.get(i));
                    adress.setText("Adresse : " + ADRESSE.get(i));
                    tel.setText("Tel : " + TELS.get(i));

                    builder.setView(vi);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();

                    rv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.cancel();
                            LinearLayout vii = (LinearLayout) inflater.inflate(R.layout.fixer_rv, null);

                            final EditText date = vii.findViewById(R.id.date);
                            final EditText heure = vii.findViewById(R.id.heure);
                            final EditText lieux = vii.findViewById(R.id.lieux);

                            Button retourrv = (Button) vii.findViewById(R.id.retourrv);
                            Button validerrv = (Button) vii.findViewById(R.id.validerrv);

                            builder.setView(vii);
                            final AlertDialog al = builder.create();
                            al.setCanceledOnTouchOutside(false);
                            al.show();

                            retourrv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    al.cancel();
                                    alertDialog.show();
                                }
                            });

                            validerrv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    al.cancel();
                                    Map<String, String> postMap = new HashMap();
                                    postMap.put("nom", NOMS.get(i));
                                    postMap.put("adresse", ADRESSE.get(i));
                                    postMap.put("Montant", MONTANTS.get(i));
                                    postMap.put("adresse", TELS.get(i));
                                    postMap.put("date", date.getText().toString());
                                    postMap.put("heure", heure.getText().toString());
                                    postMap.put("lieux", lieux.getText().toString());

                                    handleHttp(urlRv, postMap);

                                    alertDialog.show();
                                }
                            });

                        }
                    });

                    appeler.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
                            callIntent.setData(Uri.parse("tel:" + TELS.get(i)));    //this is the phone number calling

                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                //request permission from user if the app hasn't got the required permission
                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 10);
                                return;
                            } else {     //have got permission
                                try {
                                    context.startActivity(callIntent);  //call activity and make phone call
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(context, "Votre activité est introuvable", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                    retour.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.cancel();
                        }
                    });
                }

                else if  ((ETATS.get(i)).intValue() ==2){
                    LinearLayout vi = (LinearLayout) inflater.inflate(R.layout.recouvrire, null);

                    Button oui = vi.findViewById(R.id.oui);
                    Button non = vi.findViewById(R.id.non);

                    builder.setView(vi);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();

                    oui.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.cancel();
                            Map<String, String> postMap = new HashMap();
                            postMap.put("nom", NOMS.get(i));
                            postMap.put("adresse", ADRESSE.get(i));
                            postMap.put("Montant", MONTANTS.get(i));
                            postMap.put("adresse", TELS.get(i));

                            handleHttp(urlRv, postMap);

                        }
                    });

                    non.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.cancel();
                        }
                    });
                }

                else if  ((ETATS.get(i)).intValue() ==3){
                    LinearLayout vi = (LinearLayout) inflater.inflate(R.layout.dejarecouvert, null);

                    Button ok = vi.findViewById(R.id.OK);

                    builder.setView(vi);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.cancel();

                        }
                    });

                }
            }
        });


        return v;
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

        Volley.newRequestQueue(context).add(stringRequest);
    }

}
