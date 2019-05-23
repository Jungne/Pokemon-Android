package com.example.pokemonandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.widget.AbsListView.CHOICE_MODE_SINGLE;

public class MyTeamsActivity extends AppCompatActivity {
    DBHandler dbhelper;
    private ArrayAdapter myAdapter;
    Map<String, List<String>> allTeams;
    List<String> MyTeamNames = new ArrayList<>();
    List<String> SelectedTeamDetails = new ArrayList<>();


    ArrayAdapter<String> myTeamsListViewAdapter = null;
    ArrayAdapter<String> TeamDetailsViewAdapter = null;

    protected void updateMyTeamsListView(){
        this.allTeams.clear();
        this.MyTeamNames.clear();
        this.allTeams =dbhelper.getAllTeams();
        for (Map.Entry<String, List<String>> entry : this.allTeams.entrySet()) {
            this.MyTeamNames.add(entry.getKey());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.dbhelper = new DBHandler(getApplicationContext());
        this.allTeams = dbhelper.getAllTeams();



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_teams);

        ListView myTeamsListView = findViewById(R.id.myTeamsListView);
        ListView teamdetailsView = findViewById(R.id.teamdetails);

        //update listview
        updateMyTeamsListView();

        this.myTeamsListViewAdapter =  new ArrayAdapter<String>
                (this, R.layout.activity_listview ,this.MyTeamNames);
        myTeamsListView.setAdapter(this.myTeamsListViewAdapter);

        this.TeamDetailsViewAdapter =  new ArrayAdapter<String>
                (this, R.layout.activity_listview ,this.SelectedTeamDetails);
        teamdetailsView.setAdapter(this.TeamDetailsViewAdapter);

        teamdetailsView.setChoiceMode(CHOICE_MODE_SINGLE);
        // Set an item click listener for ListView
        myTeamsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Display the selected item text on TextView

                updateListViewTeamMembers(selectedItem);
                TeamDetailsViewAdapter.notifyDataSetChanged();
            }
        });

    }

    protected void updateListViewTeamMembers(String selectedTeam){
        SelectedTeamDetails.clear();
        this.allTeams.clear();
        this.allTeams =dbhelper.getAllTeams();
        for (Map.Entry<String, List<String>> entry : allTeams.entrySet()) {
            //upate listview

            if(entry.getKey().equals(selectedTeam))
            {
                for(String pokemon : entry.getValue())
                {
                    if(pokemon != null && !SelectedTeamDetails.contains(pokemon)){
                        SelectedTeamDetails.add(pokemon);
                    }
                    else{

                    }
                }
            }
        }

    }

    public void OnClickButtons(View v)
    {
        if (v.getId() == R.id.addNewTeambutton) {
            TextView inputTeamNameTextView = (TextView) findViewById(R.id.inputTeamNameTextView);
            addNewTeambuttonDialog();
        } else if (v.getId() == R.id.BackToMainbutton) {
            Intent myIntent = new Intent(this, MainActivity.class);
            startActivity(myIntent);
        } else if (v.getId() == R.id.removeFromTeamMembersbutton) {
            //Get selected team
            ListView removemember_myTeamsListView = findViewById(R.id.myTeamsListView);
            int pos = removemember_myTeamsListView.getCheckedItemPosition();
            String selectedTeamName  = (String) removemember_myTeamsListView.getAdapter().getItem(pos);
            //Get selected pokemon
            ListView removemember_teamdetails = findViewById(R.id.teamdetails);
            pos = removemember_teamdetails.getCheckedItemPosition();
            String selectedPokemon  = (String) removemember_teamdetails.getAdapter().getItem(pos);


            for (Map.Entry<String, List<String>> entry : allTeams.entrySet()) {

                if(entry.getKey().equals(selectedTeamName))
                {
                    List<String> pokelist = entry.getValue();
                    Boolean remove = false;
                    for(String pokemon : pokelist)
                    {
                        if(pokemon.equals(selectedPokemon))
                        {
                            remove = true;

                        }

                    }
                    if(remove == true)
                    {
                        pokelist.remove(selectedPokemon);
                        removePokemonFromTeam(selectedTeamName,selectedPokemon);


                    }
                    entry.setValue(pokelist);
                }
            }
            SelectedTeamDetails.remove(selectedPokemon);
            removemember_teamdetails.setSelection(-1);
            TeamDetailsViewAdapter.notifyDataSetChanged();
        } else if (v.getId() == R.id.removeTeambutton) {
            ListView remove_myTeamsListView = findViewById(R.id.myTeamsListView);
            int removeTeambutton_pos = remove_myTeamsListView.getCheckedItemPosition();
            String choosedremoveTeam  = (String) remove_myTeamsListView.getAdapter().getItem(removeTeambutton_pos);
            for(String Name : MyTeamNames)
            {
                if(Name.equals(choosedremoveTeam))
                {
                    MyTeamNames.remove(Name);
                    dbhelper.removeTeamFromDb(Name);
                }
            }

            SelectedTeamDetails.clear();
            myTeamsListViewAdapter.notifyDataSetChanged();
            TeamDetailsViewAdapter.notifyDataSetChanged();
        } else if (v.getId() == R.id.seDetailbutton){
            ListView myTeamsListView = findViewById(R.id.myTeamsListView);
            int pos1 = myTeamsListView.getCheckedItemPosition();
            String choosedTeam  = (String) myTeamsListView.getAdapter().getItem(pos1);


            //Goto pokemon info view.
            Intent ShowPokemon = new Intent(this, ShowPokemon.class);
            ShowPokemon.putExtra("selectedTeamName", choosedTeam);
            startActivity(ShowPokemon);
        }
    }

    protected void removePokemonFromTeam(String selectedTeamName,String selectedPokemon){
        dbhelper.removePokemonFromTeam(selectedTeamName,selectedPokemon);
        updateListViewTeamMembers(selectedTeamName);

    }

    public List<String> getpokemonsOfTeam(String teamsName){
        List<String> teammembers =allTeams.get(teamsName);
        return teammembers;
    }

    protected void addNewTeambuttonDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MyTeamsActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.inputnameforteam, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyTeamsActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        // setup a dialog window
        alertDialogBuilder
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        String text = editText.getText().toString();
                        dbhelper.createNewEmptyTeam(text);
                        updateMyTeamsListView();

                        myTeamsListViewAdapter.notifyDataSetChanged();

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

}