package com.westjonathan.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {
    DatabaseReference mDatabase;
    // Declare android elements
    Spinner quizChoice;
    Button submitButton;
    Button scorePageBtn;
    EditText responseText;
    TextView dispMessage;
    TextView dispTime;
    TextView displayScore;
    String[] respuestas = new String[]{"Badinage", "Macabre", "Irrefutable", "Echelon", "Allege", "Fatuous", "Lackadaisical", "Juggernaut", "exacerbate", "conciliate",
            "arrant","countermand","saturnine","litany","substantive","recant","paucity","raze","portend","melange","saturate","slough"};
    String[] preguntas = new String[]{"(n.) Light or playful banter or teasing during conversation",
            "(adj.) Gruesome or horribly or dealing with death",
            "(adj.) Cannot be disproved or argued against",
            "(n.) A level of authority or command in an organization like the military",
            "(v.) To assert as true without proof or positiveness",
            "(adj.) Foolish or stupid or unintelligent",
            "(adj.) Without spirit or interest or effort",
            "(n.) A massive, overpowering destructive force that crushes everything in its way",
            "(v.) To increase the bitterness or pain of something",
            "(v.) To win or overcome the distrust of another",
            "(adj.) Glaringly obvious, outright, blatant;",
            "(v.) To cancel or reverse a previous command",
            "(adj.) A gloomy or sullen mood",
            "(n.) A recital of prayers in response to a leader; A long or drawn-out account",
            "(adj.) Solid or real or pertaining to practical importance",
            "(v.) To withdraw one's belief or statement formally",
            "(n.) Smallness of number or scarce",
            "(v.) To tear down to the ground or completely destroy",
            "(v.) To foreshadow or warn in advance",
            "(n.) A mixture or medley of things",
            "(v.) To soak or fill to capacity",
            "(v.) To shed or cast off"
    };
    String currName;
    int score = 0;
    int time = 0;
    Timer timer = new Timer();
    private long mLastClickTimeScores = 0;
    private long mLastClickTimeSubmit = 0;
    private Toast mainToast = null;
    //create a list of items for the spinner.
    String[] quizChoices = {"placeholder1", "placeholder2", "placeholder3"};

    private void scoreUpdate(int dif){ // update current score after each answer
        score += dif;
        String temp = "Score: " + score;
        displayScore.setText(temp);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences sharedPreferences = getSharedPreferences("dataStorage", Context.MODE_PRIVATE);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        //initialize elements
        quizChoice = findViewById(R.id.qcSpinner);//get the spinner from the xml
        fillSpinner();// Fill spinner element with quiz choices
        scorePageBtn=findViewById(R.id.scoreButton);
        submitButton=findViewById(R.id.submitButton);
        responseText=findViewById(R.id.responseEditText);
        dispMessage=findViewById(R.id.msgBox);
        dispTime = findViewById(R.id.dispTime);
        displayScore = findViewById(R.id.dispScore);
        //Setup what is shown on initial page
        dispTime.setVisibility(View.INVISIBLE);
        displayScore.setVisibility(View.INVISIBLE);
        // Randomize question order:
        shuffleArray(respuestas, preguntas);

        Gson gson = new Gson();
        DataStorage temp_dt = gson.fromJson(sharedPreferences.getString("name", "{'name':''}"),
                DataStorage.class);
        String prev_name = temp_dt.getName();
        String returnGreeting;
        if(!prev_name.equals("")){
            returnGreeting = "Welcome back"+ (" " + prev_name) + "! You can change your name if you want.";
            dispMessage.setText(returnGreeting);}
//        if(intent.hasExtra("returning?")) { dispMessage.setText(returnGreeting); }
//        readFromDatabase("unit8");// retrieve questions and answers from specified set
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mis-clicking prevention, using threshold of 300 ms:
                if (SystemClock.elapsedRealtime() - mLastClickTimeSubmit < 300) {
                    return;
                }
                mLastClickTimeSubmit = SystemClock.elapsedRealtime();

                currName = responseText.getText().toString().trim();
                Gson gson = new Gson();
                DataStorage storedName = gson.fromJson(sharedPreferences.getString("name", "{'name':''}"), DataStorage.class);
                if(currName.equals("") && storedName.getName().equals("")) { // if user did not input name
                    if (mainToast != null) // demand a name input
                        mainToast.cancel();
                    mainToast = Toast.makeText(MainActivity.this, "Please enter a name", Toast.LENGTH_SHORT);
                    mainToast.show();
                } else { // begin game
                    quizChoice.setVisibility(View.INVISIBLE); // prevent user from changing during game
                    if(currName.equals(""))
                        currName = storedName.getName();
                    // Welcome message
                    if (mainToast != null)
                        mainToast.cancel();//eliminate previous toasts, if any remain
                    mainToast = Toast.makeText(MainActivity.this, "Welcome to the game " + currName + "!", Toast.LENGTH_SHORT);
                    mainToast.show();
                    // Use GSON:
                    DataStorage plyr = new DataStorage(currName);
                    gson = new Gson();
//                    System.out.println(gson.toJson(plyr));
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", gson.toJson(plyr)); // store object with name
                    editor.apply();
                    dispMessage.setText(preguntas[0]);
                    dispTime.setVisibility(View.VISIBLE);
                    displayScore.setVisibility(View.VISIBLE);
                    scorePageBtn.setVisibility((View.INVISIBLE));
                    responseText.setHint("              ");
                    responseText.setText("");
                    // Game Timer
                    timer.scheduleAtFixedRate(new TimerTask() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    TextView timeView = dispTime;
                                    String timeText = "Timer: ";
                                    int sec = time % 60;
                                    int min = time / 60;
                                    timeText += min + ":" + sec;
                                    timeView.setText(timeText);
                                    time += 1; // increment seconds
                                }
                            });
                        }
                    }, 0, 1000);// call each second
                    // Answer submissions:
                    submitButton.setOnClickListener(new View.OnClickListener() {
                        int questNum = 0;
                        public void onClick(View view) {
                            // mis-clicking prevention, using threshold of 400 ms:
                            if (SystemClock.elapsedRealtime() - mLastClickTimeSubmit < 400){
                                return;
                            }
                            mLastClickTimeSubmit = SystemClock.elapsedRealtime();

                            // correct answer submitted
                            if (responseText.getText().toString().trim().equalsIgnoreCase(respuestas[questNum])) {
                                scoreUpdate(1);
                            } else {// incorrect answer submitted
                                scoreUpdate(-1);
                                // Give answer
                                if (mainToast != null)
                                    mainToast.cancel();//eliminate previous toasts, if any remain
                                mainToast = Toast.makeText(getApplicationContext(), "CORRECT ANSWER: " + respuestas[questNum], Toast.LENGTH_SHORT);
                                mainToast.show();
                            }
                            responseText.setText("");
                            if (this.questNum < preguntas.length - 1) {
                                questNum += 1;
                                dispMessage.setText(preguntas[questNum]);//Next question
                            } else {
                                timer.cancel();
                                timer.purge();
                                timer = new Timer();
                                // Go to end screen
                                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                                intent.putExtra("sendData", new int[]{score, time});
                                intent.putExtra("sendName", currName);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }
    private void fillSpinner() {// populate spinner options with available quizzes in database
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                quizChoices = new String[(int)dataSnapshot.getChildrenCount()-1];//exclude leaderboards
                int i = 0;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(!ds.getKey().equals("leaderboards")) {
                        quizChoices[i++] = ds.getKey();
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, quizChoices);
                quizChoice.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mDatabase.addListenerForSingleValueEvent(eventListener);
        //set select listener:
        quizChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                readFromDatabase(quizChoices[position]);// read/download chosen quiz from database
                shuffleArray(respuestas, preguntas);// shuffle question order
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }

    public void scorePage(View view) {
        // mis-clicking prevention, using threshold of 500 ms:
        if (SystemClock.elapsedRealtime() - mLastClickTimeScores < 500){
            return;
        }
        mLastClickTimeScores = SystemClock.elapsedRealtime();
        Intent intent = new Intent(getApplicationContext(), ScoreActivity.class);
        startActivity(intent);
    }

    // Implementing Fisher–Yates shuffle
    static void shuffleArray(String[] answrs, String[] questns)
    {
        Random rnd = ThreadLocalRandom.current();
        for (int i = questns.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String a = answrs[index];
            answrs[index] = answrs[i];
            answrs[i] = a;
            //randomize questions and answers in tandem
            a = questns[index];
            questns[index] = questns[i];
            questns[i] = a;
        }
    }
    private void readFromDatabase(String quizChoice){
        //Get datasnapshot at quiz root node
        mDatabase.child(quizChoice).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("datasnap", dataSnapshot.getValue().toString());
                        Log.w("datasnap", dataSnapshot.getChildren().toString());
                        //Get map of quiz in datasnapshot
                        if(dataSnapshot.getValue() != null) {// check if database is empty
                            readQuiz((Map<String, String>) Objects.requireNonNull(dataSnapshot.getValue()));
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }
    private void readQuiz(Map<String,String> questions) { // retrieve question set from database
        respuestas = new String[questions.size()];
        preguntas = new String[questions.size()];
        int i = 0;
        //iterate through each attempt, ignoring their id
        for (Map.Entry<String, String> entry : questions.entrySet()){
            preguntas[i] = entry.getValue();
//            Log.i("quizTest", entry.getKey());
            respuestas[i] = entry.getKey();
//            Log.i("quizTest", entry.getValue());
            i ++;
        }
        shuffleArray(respuestas, preguntas);
    }
}