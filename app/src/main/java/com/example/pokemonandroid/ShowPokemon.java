package com.example.pokemonandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShowPokemon extends AppCompatActivity {

    ImageView imageView;
    RequestQueue queue;
    DBHandler dbhelper;
    ArrayAdapter<String> TeamsListViewAdapter = null;
    ArrayAdapter<String> movesAdapter = null;
    Map<String, List<String>> allTeams = null;
    List<String> MyTeamNames = new ArrayList<>();
    List<String> movesList;
    String pokemonID;
    String pokemonName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.dbhelper = new DBHandler(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pokemon);
        Toolbar toolbar = findViewById(R.id.showPokemonToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        queue = Volley.newRequestQueue(this);
        Intent showpokemons = getIntent();
        String SelecedTeamName = showpokemons.getStringExtra("selectedTeamName");

        //Do show pokemon details stuff here.
        movesList = new ArrayList<>();
        movesAdapter = new ArrayAdapter<>(this, R.layout.activity_listview, movesList);
        ListView movesListView = findViewById(R.id.showPokemonMovesListView);
        movesListView.setAdapter(movesAdapter);

        pokemonID = getIntent().getStringExtra("pokemonID");
        getPokemonData(pokemonID);

        imageView = findViewById(R.id.showPokemonImageView);

        setEvolutionChain(pokemonID);
        updateMyTeamsListView();
    }

    protected void updateMyTeamsListView(){
        if(allTeams != null) {
            this.allTeams.clear();
        }
        this.MyTeamNames.clear();
        this.allTeams =dbhelper.getAllTeams();
        for (Map.Entry<String, List<String>> entry : this.allTeams.entrySet()) {
            this.MyTeamNames.add(entry.getKey());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_pokemon_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addToTeamMenuItem:
                addToTeam();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Retrieves the information for a pokemon using volley
    private void getPokemonData(final String pokemonID) {
        String url = "https://pokeapi.co/api/v2/pokemon/" + pokemonID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            setPokemonData(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("something went wrong");

                    }
                });

        queue.add(jsonObjectRequest);
    }

    //Retrieves the sprite for a pokemon using Volley.
    private void getPokemonSprite(String pokemonID) {
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
        JSONArray abilitiesArray = data.getJSONArray("abilities");
        String abilities = StringUtils.capitalize(data.getJSONArray("abilities").getJSONObject(0).getJSONObject("ability").getString("name"));
        for (int i = 1; i < abilitiesArray.length(); i++) {
            String ability = abilitiesArray.getJSONObject(i).getJSONObject("ability").getString("name");
            ability = StringUtils.capitalize(ability);
            if (i == abilitiesArray.length() - 1) {
                abilities += " or " + ability;
            } else {
                abilities += ", " + ability;
            }
        }
        abilities = abilities.replace("-", " ");
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
        TextView abilitiesTextView = findViewById(R.id.showPokemonAbilitiesTextView);
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
        abilitiesTextView.setText(abilities);
        speedTextView.setText(speed);
        spDefTextView.setText(spDef);
        spAtkTextView.setText(spAtk);
        defenseTextView.setText(defense);
        attackTextView.setText(attack);
        hpTextView.setText(hp);

        pokemonID = number;
        pokemonName = name;
        JSONArray movesArray = data.getJSONArray("moves");
        setMoves(movesArray);
        setPokemonSprite(pokemonID);
    }

    private void setPokemonSprite(String pokemonID) {
        getPokemonSprite(pokemonID);
    }

    private void addToTeam() {
        Context context = getApplicationContext();
        CharSequence text = "Added to team!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void setEvolutionChain(String pokemonID) {
        getEvolutionChainURL(pokemonID);
    }

    private void getEvolutionChainURL(String pokemonID) {
        String url = "https://pokeapi.co/api/v2/pokemon-species/" + pokemonID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String evolutionChainURL = response.getJSONObject("evolution_chain").getString("url");
                            getEvolutionChain(evolutionChainURL);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        queue.add(jsonObjectRequest);
    }

    private void getEvolutionChain(String url) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            ArrayList evolutions = new ArrayList<String>();
                            findEvolution(evolutions, response.getJSONObject("chain"));
                            setEvolutionImageViews(evolutions);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        queue.add(jsonObjectRequest);
    }

    private void findEvolution(ArrayList<String> evolutions, JSONObject jsonObject) throws JSONException {
        String url = jsonObject.getJSONObject("species").getString("url");
        String[] splitURL = url.split("/");
        String pokemonID = splitURL[splitURL.length-1];
        evolutions.add(pokemonID);
//        if (Integer.parseInt(pokemonID) <= 151) { //Only adds the 1st generation of pokémon.
//            evolutions.add(pokemonID);
//        }
        JSONArray evolvesTo = jsonObject.getJSONArray("evolves_to");
        if (!evolvesTo.isNull(0)) {
            findEvolution(evolutions, evolvesTo.getJSONObject(0));
        }
    }

    private void setEvolutionImageViews(ArrayList<String> evolutions) {
        TextView evolutionTextView = findViewById(R.id.showPokemonEvolutionTextView);
        if (evolutions.size() <= 1) {
            evolutionTextView.setVisibility(View.VISIBLE);
            return;
        }

        evolutionTextView.setVisibility(View.INVISIBLE);
//        ArrayList imageViewsList = new ArrayList<ImageView>();
        for (String pokemonID : evolutions) {
            ImageView evolutionImageView = new ImageView(getApplicationContext());
            evolutionImageView.setLayoutParams(new LinearLayout.LayoutParams(80, 80));
            evolutionImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            LinearLayout evolutionLayout = findViewById(R.id.evolutionLinearLayout);
            evolutionLayout.addView(evolutionImageView);
            addEvolutionImage(evolutionImageView, pokemonID);
        }

    }

    private void addEvolutionImage(final ImageView evolutionImageView, String pokemonID) {
        String url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemonID + ".png";

        ImageRequest imageRequest = new ImageRequest
                (url, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        evolutionImageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                        new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                imageView.setImageResource(R.mipmap.pokedroid);
                            }
                        });

        queue.add(imageRequest);
    }
    /*public boolean addPokemonToTeam(View view)
    {
        //Show teams dialog.
        selectTeamToAddToDialog();
        return true;
    }*/
    public boolean addPokemonToTeam(MenuItem item) {
        selectTeamToAddToDialog();
        return true;
    }

    protected void selectTeamToAddToDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(ShowPokemon.this);
        final View promptView = layoutInflater.inflate(R.layout.dialog_show_pokemon_select_team, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShowPokemon.this);
        alertDialogBuilder.setView(promptView);

        ListView myTeams = (ListView) promptView.findViewById(R.id.listviewSelectTeamShowPokemonDialog);

        TeamsListViewAdapter =  new ArrayAdapter<String>
                (this, R.layout.activity_listview ,this.MyTeamNames);
            myTeams.setAdapter(TeamsListViewAdapter);
            TeamsListViewAdapter.notifyDataSetChanged();
        // setup a dialog window
        alertDialogBuilder
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ListView myTeams = (ListView) promptView.findViewById(R.id.listviewSelectTeamShowPokemonDialog);
                        int pos = myTeams.getCheckedItemPosition();
                        String selectedTeamName  = (String) myTeams.getAdapter().getItem(pos);
                        dbhelper.addNewPokemonToExistingTeam(pokemonName, selectedTeamName);
                        dialog.cancel();
                        Context context = getApplicationContext();
                        CharSequence text = "Added to team!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void setMoves(JSONArray movesJSONArray) throws JSONException {
        movesList.clear();

        for (int i = 0; i < movesJSONArray.length(); i++) {
            String moveName = movesJSONArray.getJSONObject(i).getJSONObject("move").getString("name");
            moveName = StringUtils.capitalize(moveName);
            moveName = moveName.replace('-', ' ');

            movesList.add(moveName);
        }
        movesAdapter.notifyDataSetChanged();
    }
}
