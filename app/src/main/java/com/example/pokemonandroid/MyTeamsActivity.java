package com.example.pokemonandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;
import java.util.Map;

public class MyTeamsActivity extends AppCompatActivity {
    DBHandler dbhelper;
    Map<String, List<String>> allTeams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.dbhelper = new DBHandler(getApplicationContext());
        this.allTeams = dbhelper.getAllTeams();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_teams);
    }
}
