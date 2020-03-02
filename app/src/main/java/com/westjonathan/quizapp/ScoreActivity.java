package com.westjonathan.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
// For ListView:
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScoreActivity extends AppCompatActivity {
    private long mLastClickTimeHome = 0;
    DatabaseReference mDatabase;
    ListView listview;
    String[] ListElements = new String[]{"Name - Score - Time"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        listview = findViewById(R.id.listView1);
        mDatabase = FirebaseDatabase.getInstance().getReference();

//        ArrayList<String> pastNames = readFromFile("nombres.txt");
//        ArrayList<String> pastScores = readFromFile("scores.txt");
//        ArrayList<String> pastTimes = readFromFile("times.txt");
//        Log.i("Names read from file: ", pastNames.toString());
//        for(int i = 0; i < pastNames.size(); i++){ // iterate through saved scores and add to listview
//            int time = Integer.parseInt(pastTimes.get(i));
//            ListElementsArrayList.add(pastNames.get(i)+"    -   "+pastScores.get(i)+"   -   "+(time/60)+":"+(time%60));
//            adapter.notifyDataSetChanged();
//        }
        readFromDatabase();
    }
    private void readFromDatabase(){
        //Get datasnapshot at leaderboard root node
        mDatabase.child("leaderboards").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("datasnap", dataSnapshot.getValue().toString());
                        Log.w("datasnap", dataSnapshot.getChildren().toString());
                        //Get map of scores in datasnapshot
                        if(dataSnapshot.getValue() != null) {// check if database is empty
//                            Log.i("childtest", "test");
//                            Map<String, QuizAttempt> testy = (Map<String, QuizAttempt>) Objects.requireNonNull(dataSnapshot.getValue());
//                            Log.i("childtest", testy.toString());
                            dispScores((Map<String, Object>) Objects.requireNonNull(dataSnapshot.getValue()));
//                            JSONObject obj = dataSnapshot.getValue(JSONObject);
//                            for(com.google.firebase.database.DataSnapshot key : dataSnapshot.getChildren()){
//                                Log.i("childtest", key.getKey());
//                            }
                        }
                        //if(dataSnapshot.getValue() != null) {// check if database is empty
                        ////                            dispScores((Map<String, Object>) Objects.requireNonNull(dataSnapshot.getValue()));
                        //                            for(String key : dataSnapshot.getValue()){
                        //
                        //                            }
                        //                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void dispScores(Map<String,Object> users) {
        final List<String> ListElementsArrayList = new ArrayList<>(Arrays.asList(ListElements));
        final ArrayAdapter<String> adapter = new ArrayAdapter<> (getApplicationContext(), android.R.layout.simple_list_item_1, ListElementsArrayList);
        listview.setAdapter(adapter);

        //iterate through each attempt, ignoring their id
        for (Map.Entry<String, Object> entry : users.entrySet()){
            Map singleAttempt = (Map) entry.getValue();
            //Get data and append to list
            ListElementsArrayList.add(singleAttempt.get("name")+"    -   "+singleAttempt.get("score")+"   -   "+(Integer.parseInt(singleAttempt.get("time").toString())/60)+":"+(Integer.parseInt(singleAttempt.get("time").toString())%60));
            adapter.notifyDataSetChanged();
        }
//        Collections.sort(ListElementsArrayList, new Comparator<Object>() {
//            @Override
//            public int compare(Object o1, Object o2) {
//                return o1.get("score").compareTo(o2.get("score"));
//            }
//        });
    }

//    private ArrayList<String> readFromFile(String fileName) {// reads text file and saves to arraylist
//        ArrayList<String> ret = new ArrayList<>();
//        try{
//            FileInputStream fis = getApplicationContext().openFileInput(fileName);// potential FileNotFoundException
//            InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
//            BufferedReader reader = new BufferedReader(inputStreamReader);//potential IOException
//            String line = reader.readLine();
//            while (line != null) {
//                ret.add(line);
//                line = reader.readLine();
//            }
//        } catch (java.io.FileNotFoundException e){
//            Log.e("Reading files:", "FileNotFoundException");
//        } catch (java.io.IOException e) {
//            // Error occurred when opening raw file for reading.
//            Log.e("Reading files:", "IOException");
//        }
//        return ret;
//    }

    public void returnToPrevious(View view) {// mis-clicking prevention, using threshold of 500 ms:
        if (SystemClock.elapsedRealtime() - mLastClickTimeHome < 500){
            return;
        }
        mLastClickTimeHome = SystemClock.elapsedRealtime();

        Intent returnIntent = new Intent(getApplicationContext(), MainActivity.class);
        returnIntent.putExtra("returning?", true);
        startActivity(returnIntent);
        finish();
    }
}