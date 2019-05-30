package com.example.pokemonandroid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import utilities.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    DBHandler dbhelper;
    String pokemonID;
    List<String> allPokemons = new ArrayList<>();
    ArrayAdapter<String> ListViewAdapterAllPokemons = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.dbhelper = new DBHandler(getApplicationContext());
        List<String> team1 =this.dbhelper.getTeamByName("My favorites");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listViewAllPokemons = findViewById(R.id.listViewAllPokemons);
        this.ListViewAdapterAllPokemons =  new ArrayAdapter<String>
                (this, R.layout.activity_listview ,this.allPokemons);
        listViewAllPokemons.setAdapter(this.ListViewAdapterAllPokemons);

        loadAllPokemons();
        EditText searchFilter = findViewById(R.id.editSearchFilter);
        searchFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ListViewAdapterAllPokemons.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    public void loadAllPokemons()
    {
        //https://pokeapi.co/api/v2/pokemon/?limit=1000
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://pokeapi.co/api/v2/pokemon/?limit=1000";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        loadPokemonForListView(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Context context = getApplicationContext();
                        CharSequence text = "Network error";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                });

        queue.add(jsonObjectRequest);
    }
    public void loadPokemonForListView(JSONObject jsonObject)
    {
        try {
            JSONArray array = jsonObject.getJSONArray("results");
            for(int i=0;i<array.length();i++) {
                // Get current json object
                JSONObject pokemon = array.getJSONObject(i);
                String pokemonName = pokemon.getString("name");
                pokemonName = StringUtils.capitalize(pokemonName);
                pokemonName = pokemonName.replace('-', ' ');
                allPokemons.add(pokemonName);
            }
            ListViewAdapterAllPokemons.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void showPokemonFromListView(View view)
    {
        ListView pokemonsListView = findViewById(R.id.listViewAllPokemons);
        int pos = pokemonsListView.getCheckedItemPosition();
        String selectedPokemonName  = (String) pokemonsListView.getAdapter().getItem(pos);
        pokemonID = StringUtils.lowerCase(selectedPokemonName);
//        getPokemonDataAndShow(pokemonID);
        Intent intent = new Intent(this, ShowPokemon.class);
        intent.putExtra("pokemonID", pokemonID);
        startActivity(intent);

    }
    public void gotoMyTeams(View view)
    {
        Intent myIntent = new Intent(this, MyTeamsActivity.class);
        startActivity(myIntent);
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
}
