package com.example.pokemonandroid;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckWeakness extends AppCompatActivity {

    RequestQueue queue;
    DBHandler db;
    List<String> team;
    List<String> types;
    boolean[] typeCoverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_weakness);

        queue = Volley.newRequestQueue(this);

        String teamName = getIntent().getStringExtra("teamName");

        db = new DBHandler(getApplicationContext());
        team = setupTeamList(db.getTeamByName(teamName));

        types = Arrays.asList("normal", "fighting", "flying", "poison", "ground", "rock", "bug", "ghost", "steel", "fire", "water", "grass", "electric", "psychic", "ice", "dragon", "dark", "fairy");
        typeCoverage = new boolean[types.size()];

        checkCoverage(team);
    }

    private List<String> setupTeamList(List<String> team) {
        List<String> returnTeam = new ArrayList<>();
        for (String pokemon : team) {
            if (pokemon == null) {
                continue;
            } else {
                returnTeam.add(StringUtils.lowerCase(pokemon));
            }
        }

        return returnTeam;
    }

    private void checkCoverage(List<String> team) {
        for (String pokemon : team) {
            getTypes(pokemon);
        }
    }

    private void getTypes(String pokemon) {
        String url = "https://pokeapi.co/api/v2/pokemon/" + pokemon;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            checkTypes(response.getJSONArray("types"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    private void checkTypes(JSONArray types) throws JSONException {
        for (int i = 0; i < types.length(); i++) {
            String type = types.getJSONObject(i).getJSONObject("type").getString("name");
            getTypeData(type);
        }
    }

    private void getTypeData(String type) {
        int typeID = types.indexOf(type) + 1;

        String url = "https://pokeapi.co/api/v2/type/" + typeID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            checkType(response.getJSONObject("damage_relations").getJSONArray("half_damage_from"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    private void checkType(JSONArray typeData) throws JSONException {

        for (int i = 0; i < typeData.length(); i++) {
            String typeName = typeData.getJSONObject(i).getString("name");
            int typeIndex = types.indexOf(typeName);
            typeCoverage[typeIndex] = true;
        }

        updateTextViews();
    }

    private void updateTextViews() {
        List<String> typesCoveredList = new ArrayList<>();
        List<String> typesNotCoveredList = new ArrayList<>();

        TextView typesCovered = findViewById(R.id.coveredTypesTextView);
        TextView typesNotCovered = findViewById(R.id.notCoveredTypesTextView);

        for (int i = 0; i < typeCoverage.length; i++) {
            if (typeCoverage[i] == true) {
                typesCoveredList.add(types.get(i));
            } else {
                typesNotCoveredList.add(types.get(i));
            }
        }

        typesCovered.setText(makeStringFromList(typesCoveredList));
        typesNotCovered.setText(makeStringFromList(typesNotCoveredList));
    }

    private String makeStringFromList(List<String> list) {
        if (list.isEmpty()) {
            return "";
        }

        String returnString = StringUtils.capitalize(list.get(0));

        for (int i = 1; i < list.size(); i++) {
            returnString += ", " + StringUtils.capitalize(list.get(i));
        }

        return returnString;

    }
}
