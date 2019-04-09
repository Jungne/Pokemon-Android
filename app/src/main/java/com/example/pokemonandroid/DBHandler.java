package com.example.pokemonandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DBHandler extends SQLiteOpenHelper {

        public DBHandler(Context context) {
            super(context, "my.teams.db", null, 1);


        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS team(\"nameOfTeam\" VARCHAR(200) PRIMARY KEY, \"nameOfPokemon1\" VARCHAR(200), \"nameOfPokemon2\" VARCHAR(200), \"nameOfPokemon3\" VARCHAR(200), \"nameOfPokemon4\" VARCHAR(200), \"nameOfPokemon5\" VARCHAR(200))");


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
    }

