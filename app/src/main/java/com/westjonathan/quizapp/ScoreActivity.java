package com.westjonathan.quizapp;

import com.westjonathan.quizapp.Fragments.ScoreFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.westjonathan.quizapp.Fragments.ScoreFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScoreActivity extends AppCompatActivity {
    FragmentManager fm;
    ScoreFragment scr_fragment;
    private long mLastClickTimeHome = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        scr_fragment = new ScoreFragment();

        fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, scr_fragment).commit();


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