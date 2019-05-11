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
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.dbhelper = new DBHandler(getApplicationContext());
        this.allTeams = dbhelper.getAllTeams();



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_teams);

        ListView myTeamsListView = findViewById(R.id.myTeamsListView);
        ListView teamdetailsView = findViewById(R.id.teamdetails);

        for (Map.Entry<String, List<String>> entry : this.allTeams.entrySet()) {
            this.MyTeamNames.add(entry.getKey());
        }

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
                for (Map.Entry<String, List<String>> entry : allTeams.entrySet()) {
                    SelectedTeamDetails.clear();
                    if(entry.getKey().equals(selectedItem))
                    {
                        for(String pokemon : entry.getValue())
                        {
                            SelectedTeamDetails.add(pokemon);
                        }
                    }
                }
                TeamDetailsViewAdapter.notifyDataSetChanged();

            }
        });

    }
    public void OnClickButtons(View v)
    {
        switch (v.getId()) {
            case R.id.addNewTeambutton:
                TextView inputTeamNameTextView = (TextView) findViewById(R.id.inputTeamNameTextView);

                addNewTeambuttonDialog();
                break;
            case R.id.BackToMainbutton:
                Intent myIntent = new Intent(this, MainActivity.class);
                startActivity(myIntent);
                break;
            case R.id.removeFromTeamMembersbutton:

                break;
            case R.id.removeTeambutton:

                break;
                case R.id.teamdetails:
                    ListView myTeamsListView = findViewById(R.id.myTeamsListView);
                    String choosedTeam =(String) myTeamsListView.getSelectedItem();
                    List<String> teamsdetails = getpokemonsOfTeam(choosedTeam);
                    //need to use adapter, how idk...

                    break;

            default:
                break;
        }
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

                        //resultText.setText("Hello, " + editText.getText());
                        String text = editText.getText().toString();
                        MyTeamNames.add(text);
                        //
                        //sdialog.cancel();
                        myTeamsListViewAdapter.notifyDataSetChanged();
                        //myTeamsListViewAdapter.notifyDataSetChanged();

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