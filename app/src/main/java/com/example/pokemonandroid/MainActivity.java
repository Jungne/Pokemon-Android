package com.example.pokemonandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    Button testButton;
    TextView textView;
    DBHandler dbhelper;
    String pokemonID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.dbhelper = new DBHandler(getApplicationContext());
        //this.dbhelper.insertTeam("My favorites", "pikachu", "snorlax", "bulbasaur", "psyduck", "charmander" );
        List<String> team1 =this.dbhelper.getTeamByName("My favorites");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button testButton = findViewById(R.id.randomPokemonButton);
        textView = findViewById(R.id.text);
        testButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pokemonID = String.valueOf((int)(Math.random() * 151) + 1);
                textView.setText(pokemonID);
            }
        });


    }
    public void gotoMyTeams(View view)
    {
        Intent myIntent = new Intent(this, MyTeamsActivity.class);
        startActivity(myIntent);
    }
//    public class PokeAPIQueryTask extends AsyncTask<URL, Void, String> {
//        @Override
//        protected String doInBackground(URL... urls) {
//            URL searchUrl = urls[0];
//            String pokeAPIQueryResults = null;
//
//            try {
//                pokeAPIQueryResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return pokeAPIQueryResults;
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            if (s != null && !s.equals("")) {
//                JSONObject pokemon = createJSONObjectFromString(s);
//                TextView textView = findViewById(R.id.text);
//                try {
//                    pokemonName = (String)pokemon.get("name");
//                    textView.setText(pokemonName);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private JSONObject createJSONObjectFromString(String s) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    //Retrieves the information for a pokemon using Volley.
    private void getPokemonDataAndShow(String pokemonID) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://pokeapi.co/api/v2/pokemon/" + pokemonID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                            showPokemon(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        queue.add(jsonObjectRequest);
    }

    public void showPokemonButtonHandler(View view) {
        getPokemonDataAndShow(textView.getText().toString());
    }

    private void showPokemon(JSONObject jsonObject) {
        Intent intent = new Intent(this, ShowPokemon.class);
        intent.putExtra("pokemonID", pokemonID);
        intent.putExtra("pokemonData", jsonObject.toString());
        startActivity(intent);
    }
}
