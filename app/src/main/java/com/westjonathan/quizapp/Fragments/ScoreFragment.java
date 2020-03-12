package com.westjonathan.quizapp.Fragments;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.westjonathan.quizapp.R;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScoreFragment extends Fragment {
    DatabaseReference mDatabase;
    ListView listview;
    String[] ListElements = new String[]{"Name - Score - Time"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.score_fragment, parent, false);
        listview = view.findViewById(R.id.listView1);
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

        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // handles to view objects
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
        final ArrayAdapter<String> adapter = new ArrayAdapter<> (getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, ListElementsArrayList);
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
}
