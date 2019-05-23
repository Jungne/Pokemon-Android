package com.example.pokemonandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class DBHandler extends SQLiteOpenHelper {

        public DBHandler(Context context) {
            super(context, "my.teams.db", null, 1);


        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS team(\"nameOfTeam\" VARCHAR(200) PRIMARY KEY, \"nameOfPokemon1\" VARCHAR(200), \"nameOfPokemon2\" VARCHAR(200), \"nameOfPokemon3\" VARCHAR(200), \"nameOfPokemon4\" VARCHAR(200), \"nameOfPokemon5\" VARCHAR(200))");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS pokemons(\"nameOfPokemon\" VARCHAR(200) PRIMARY KEY, \"numberOfPokemon\" INTEGER)");


        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS team");
            onCreate(sqLiteDatabase);
        }
        public boolean insertTeam(String nameOfTheTeam, String nameOfPokemon1, String nameOfPokemon2, String nameOfPokemon3, String nameOfPokemon4, String nameOfPokemon5)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("nameOfTeam",nameOfTheTeam );
            values.put("nameOfPokemon1",nameOfPokemon1 );
            values.put("nameOfPokemon2",nameOfPokemon2 );
            values.put("nameOfPokemon3",nameOfPokemon3 );
            values.put("nameOfPokemon4",nameOfPokemon4 );
            values.put("nameOfPokemon5",nameOfPokemon5 );
            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert("team", null, values);
            return true;
        }
        public boolean removePokemonFromTeam(String nameOfTheTeam, String nameOfPokemon){
            SQLiteDatabase db = this.getWritableDatabase();
            List<String> pokemons = this.getTeamByName(nameOfTheTeam);
            for(String pokemonsName:pokemons){
                if(pokemonsName.equals(nameOfPokemon)){
                    int indexOfPokemon =pokemons.indexOf(pokemonsName);
                    db.execSQL("UPDATE team SET nameOfPokemon" + (indexOfPokemon+1) + "=" + null + " WHERE nameOfTeam='" + nameOfTheTeam + "'");
                    return true;
                }
                return false;
            }
            return false;
        }
        public List<String> getTeamByName(String nameOfTheTeam){
            SQLiteDatabase db = this.getReadableDatabase();
            List<String> myteam = new ArrayList<String>();
            Cursor myCursor =
                    db.rawQuery("SELECT * FROM team WHERE nameOfTeam = \""+ nameOfTheTeam + "\"", null);
            while(myCursor.moveToNext()) {
                String nameOfTeam = myCursor.getString(0);
                String poke1 = myCursor.getString(1);
                String poke2 = myCursor.getString(2);
                String poke3 = myCursor.getString(3);
                String poke4 = myCursor.getString(4);
                String poke5 = myCursor.getString(5);
                myteam.add(poke1);
                myteam.add(poke2);
                myteam.add(poke3);
                myteam.add(poke4);
                myteam.add(poke5);
            }
            return myteam;
        }
        public Map<String, List<String>> getAllTeams() {
            Map<String, List<String>> mapOfteams = new TreeMap<String, List<String>>();
            SQLiteDatabase db = this.getReadableDatabase();
            List<String> teamsNames = new ArrayList<String>();
            Cursor myCursor =
                    db.rawQuery("SELECT nameOfTeam FROM team", null);
            while (myCursor.moveToNext()) {
                String nameOfTeam = myCursor.getString(0);
                teamsNames.add(nameOfTeam);
            }
            for (String name : teamsNames) {
                List<String> teamsPokemons = getTeamByName(name);
                mapOfteams.put(name, teamsPokemons);
            }
            return mapOfteams;
        }
        public boolean createNewEmptyTeam(String teamName) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("nameOfTeam", teamName);
            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert("team", null, values);
            return true;
        }
        public boolean addNewPokemonToExistingTeam(String pokemonsName, String TeamsName){
            SQLiteDatabase db = this.getWritableDatabase();
            List<String> existingPokemonsInTeam =getTeamByName(TeamsName);
            existingPokemonsInTeam.removeAll(Collections.singleton(null));
            existingPokemonsInTeam.add(pokemonsName);
            if(existingPokemonsInTeam.size()<=5) {
                for (String s : existingPokemonsInTeam) {
                    db.execSQL("UPDATE team SET nameOfPokemon" + existingPokemonsInTeam.size() + "='" + pokemonsName + "' WHERE nameOfTeam='" + TeamsName + "'");
                return true;
                }
            }
            return false;
        }
        public boolean removeTeamFromDb(String teamsName){
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM team WHERE nameOfTeam='" + teamsName + "'");
            return true;
        }
        public boolean addAllPokemonsToDb(Map<Integer,String> mapOfPokemons){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            for (Map.Entry<Integer, String> entry : mapOfPokemons.entrySet()) {
                int pokemonsNumber = entry.getKey();
                String pokemonsName =entry.getValue();
                values.put("nameOfPokemon",pokemonsName);
                values.put("numberOfPokemon",pokemonsNumber );
            }
            long newRowId = db.insert("team", null, values);
            return true;
        }
        public Map<Integer,String> getAllPokemonsFromDb(){
            SQLiteDatabase db = this.getReadableDatabase();
            Map<Integer,String> allPokemons = new HashMap<Integer,String>();
            Cursor myCursor =
                    db.rawQuery("SELECT * FROM pokemons", null);
            while(myCursor.moveToNext()) {
                int numberOfPokemon = myCursor.getInt(1);
                String nameOfPokemon = myCursor.getString(0);
                allPokemons.put(numberOfPokemon,nameOfPokemon);
            }
            return allPokemons;
        }

    }

