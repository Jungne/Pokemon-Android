package com.example.pokemonandroid;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    Button testButton;
    DBHandler dbhelper;
    String pokemonName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.dbhelper = new DBHandler(getApplicationContext());
        this.dbhelper.insertTeam("My favorites", "pikachu", "snorlax", "bulbasaur", "psyduck", "charmander" );
        List<String> team1 =this.dbhelper.getTeamByName("My favorites");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button testButton = findViewById(R.id.randomPokemonButton);
        testButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String pokeID = String.valueOf((int)(Math.random() * 151) + 1);
                URL url = NetworkUtils.buildUrl(pokeID);
                new PokeAPIQueryTask().execute(url);
            }
        });


    }

    public class PokeAPIQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String pokeAPIQueryResults = null;

            try {
                pokeAPIQueryResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return pokeAPIQueryResults;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")) {
                JSONObject pokemon = createJSONObjectFromString(s);
                TextView textView = findViewById(R.id.text);
                try {
                    pokemonName = (String)pokemon.get("name");
                    textView.setText(pokemonName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private JSONObject createJSONObjectFromString(String s) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public void showPokemon(View view) {
        Intent intent = new Intent(this, ShowPokemon.class);
        intent.putExtra("pokemonName", pokemonName);
        startActivity(intent);
    }
}
