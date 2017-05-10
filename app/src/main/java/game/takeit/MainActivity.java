package game.takeit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;



public class MainActivity extends Activity {
    TextView pile;
    TextView AI1;
    TextView AI2;
    TextView AI3;
    TextView AI4;
    TextView remaining;
    TextView score;
    int roundNumber = 1;
    int playerScore = 0;
    ArrayList<CompPlayer> comps = new ArrayList<>();
    ArrayList<TextView> AIviews = new ArrayList<>();

    int pileSize = 0;
    int limit = 25;
    int takes = 0;
    int compHighScore = 0;
    boolean playerWon = false;
    private Handler handler = new Handler();
    AlertDialog alertDialog;
    boolean cancelRunnable = false;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        pile = (TextView)findViewById(R.id.textView);
        AI1 = (TextView)findViewById(R.id.textView2);
        AI2 = (TextView)findViewById(R.id.textView3);
        AI3 = (TextView)findViewById(R.id.textView4);
        AI4 = (TextView)findViewById(R.id.textView5);
        AIviews.add(AI1);
        AIviews.add(AI2);
        AIviews.add(AI3);
        AIviews.add(AI4);

        score = (TextView)findViewById(R.id.textView6);
        remaining = (TextView)findViewById(R.id.textView7);
        /*computer behavior set at initialization*/
        comps.add(new CompPlayer(2,12));
        comps.add(new CompPlayer(4,14));
        comps.add(new CompPlayer(6,25));
        comps.add(new CompPlayer(8,30));
        setCompLabels();
        pile.setText(Integer.toString(pileSize));
        score.setText("Score: " + Integer.toString(playerScore));
        remaining.setText("Remaining: "+ Integer.toString(limit - 1));
        handler.postDelayed(runnable, 750);

        alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Round Over!");
        alertDialog.setCancelable(false);



        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        /*reset values, pause the runnable while dialog is showing*/
                        cancelRunnable = false;
                        handler.postDelayed(runnable, 750);
                        playerWon = false;
                        resetGame();
                        setCompLabels();
                        score.setText("Score: " + Integer.toString(playerScore));
                        dialog.dismiss();

                    }
                });
       /* add a touch listener for the whole screen*/
        view = this.getWindow().getDecorView();

        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view,MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_UP){


                if(takes < 2 && pileSize != 0){
                    /*add score update some values*/ 
                    playerScore += pileSize;
                    pileSize = 0;
                    takes++;
                    view.setBackgroundColor(Color.rgb(0,0,0));
                }
                }
                return true;

            }
        });

    }
    /*deal cards at the set interval*/
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(cancelRunnable){
                handler.removeCallbacks(this);
            }else {
                update();
                handler.postDelayed(this, 750);
            }


        }
    };
    public void update(){
        pileSize++;
        /*every time the pile grows the background color changes*/
        view.setBackgroundColor(Color.rgb(pileSize*5,pileSize*5,pileSize*32));

        for(int i = 0; i < roundNumber; i++){
            /*every increment the computers in the round either take the pile or don't first come first serve*/
            if(comps.get(i).takeThePile(pileSize)){
                pileSize = 0;
                view.setBackgroundColor(Color.rgb(0,0,0));
                break;
            }
        }
        /*if the round is over*/
        if(limit == 0){


            /*calculate the highest computer score*/
            for(CompPlayer c: comps){
                Log.d("Scores", Integer.toString(c.score));
                if(compHighScore < c.score) compHighScore = c.score;


            }

            Log.d("roundHighScore", Integer.toString(compHighScore));
            Log.d("playerScore", Integer.toString(playerScore));
            /*determine if player won the round*/
            if(compHighScore <= playerScore){
                Log.d("in if statment","true");
                playerWon = true;
            }
            /*display appropriate message based on results*/
            if(playerWon){
                alertDialog.setMessage("You won round "+Integer.toString(roundNumber)+"!");
            }else if(playerWon && roundNumber == 4) {
                alertDialog.setMessage("You beat all the comps!");
            }else{

                alertDialog.setMessage("You lost! Back to round 1!");
                roundNumber = 0;
            }
            /*calcuate round number*/
            if(roundNumber == 4){
                roundNumber = 1;
            }else{
                roundNumber++;

            }
            alertDialog.show();


            remaining.setText("Remaining: "+ Integer.toString(limit));
            cancelRunnable = true;

        }

        remaining.setText("Remaining: "+ Integer.toString(limit));
        limit--;

        /*reset the pile if "bad card" is drawn*/
        if(randomBool()){
            pileSize = 0;
        }
        pile.setText(Integer.toString(pileSize));
        score.setText("Score: " + Integer.toString(playerScore));
        setCompLabels();
    }

    public boolean randomBool(){
        /*this is used to determine if pile is thrown away*/
        Random rand = new Random();
        int randNum = rand.nextInt(100)+1;
        if(randNum < 7){

            return true;
        }
        return false;
    }
    public void resetGame(){
        takes = 0;
        playerScore = 0;
        limit = 25;
        pileSize = 0;
        compHighScore = 0;
        for(CompPlayer c: comps){

            c.score = 0;
            c.takes = 0;
        }
    }
    public void setCompLabels(){
        /*shows one comp player for round 1, two for round 2, and so on*/
        int i = 0;
        for(TextView tv: AIviews){
            if(i > roundNumber -1) {
                tv.setVisibility(View.GONE);
            }else{
                tv.setVisibility(View.VISIBLE);
                tv.setText("Comp"+Integer.toString(i+1)+" "+Integer.toString(comps.get(i).score));

            }

            i++;
        }


    }


}
