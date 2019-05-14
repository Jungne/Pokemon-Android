package com.example.pokemonandroid;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShowPokemon extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pokemon);
        Toolbar toolbar = findViewById(R.id.showPokemonToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            JSONObject pokemonData = new JSONObject(getIntent().getStringExtra("pokemonData"));
            setPokemonData(pokemonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        imageView = findViewById(R.id.showPokemonImageView);

        setPokemonSprite(getIntent().getStringExtra("pokemonID"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_pokemon_menu, menu);
        return true;
    }

    //Retrieves the information for a pokemon using Volley.
    private void getPokemonSprite(String pokemonID) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemonID + ".png";

        ImageRequest imageRequest = new ImageRequest
                (url, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                imageView.setImageResource(R.mipmap.pokedroid);
                            }
                });

        queue.add(imageRequest);
    }

    //Sets the text in the views of the activity using data from a JSONObject.
    private void setPokemonData(JSONObject data) throws JSONException {
        String number = data.getString("id");
        String name = StringUtils.capitalize(data.getString("name"));
        String height = Double.toString(data.getDouble("height") / 10);
        String weight = Double.toString(data.getDouble("weight") / 10);
        JSONArray types = data.getJSONArray("types");
        String type1 = StringUtils.capitalize(types.getJSONObject(0).getJSONObject("type").getString("name"));
        String type2 = null;
        if (types.optJSONObject(1) != null) {
            type2 = types.optJSONObject(1).getJSONObject("type").getString("name");
        }
        JSONArray stats = data.getJSONArray("stats");
        String speed = stats.getJSONObject(0).getString("base_stat");
        String spDef = stats.getJSONObject(1).getString("base_stat");
        String spAtk = stats.getJSONObject(2).getString("base_stat");
        String defense = stats.getJSONObject(3).getString("base_stat");
        String attack = stats.getJSONObject(4).getString("base_stat");
        String hp = stats.getJSONObject(5).getString("base_stat");


        //Gets the references for the views.
        TextView heightTextView = findViewById(R.id.showPokemonHeightTextView);
        TextView weightTextView = findViewById(R.id.showPokemonWeightTextView);
        TextView typeTextView = findViewById(R.id.showPokemonTypeTextView);
        TextView speedTextView = findViewById(R.id.showPokemonSpeedTextView);
        TextView spDefTextView = findViewById(R.id.showPokemonSpDefTextView);
        TextView spAtkTextView = findViewById(R.id.showPokemonSpAtkTextView);
        TextView defenseTextView = findViewById(R.id.showPokemonDefenseTextView);
        TextView attackTextView = findViewById(R.id.showPokemonAttackTextView);
        TextView hpTextView = findViewById(R.id.showPokemonHPTextView);


        //Sets the data to the views.
        String pokemonNumberAndName = "#" + number + " " + name;
        getSupportActionBar().setTitle(pokemonNumberAndName);
        heightTextView.setText(height + "m");
        weightTextView.setText(weight + "kg");
        if (type2 == null) {
            typeTextView.setText(type1);
        } else {
            typeTextView.setText(type1 + " & " + type2);
        }
        speedTextView.setText(speed);
        spDefTextView.setText(spDef);
        spAtkTextView.setText(spAtk);
        defenseTextView.setText(defense);
        attackTextView.setText(attack);
        hpTextView.setText(hp);
    }

    private void setPokemonSprite(String pokemonID) {
        getPokemonSprite(pokemonID);
    }

    public void addToTeam(View view) {
        System.out.println("Added to team");
    }
}
