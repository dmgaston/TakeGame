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
    int roundHighScore = 0;
    boolean playerWon = false;
    private Handler handler = new Handler();
    AlertDialog alertDialog;
    boolean cancelRunnable = false;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
                        cancelRunnable = false;
                        handler.postDelayed(runnable, 750);
                        playerWon = false;
                        resetGame();
                        setCompLabels();
                        score.setText("Score: " + Integer.toString(playerScore));
                        dialog.dismiss();

                    }
                });
        view = this.getWindow().getDecorView();

        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view,MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_UP){


                if(takes < 2 && pileSize != 0){
                    playerScore += pileSize;
                    pileSize = 0;
                    takes++;
                    view.setBackgroundColor(Color.rgb(0,0,0));
                }
                }
                return true;

            }
        });
        pile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP){



                }
                return true;
            }
        });
    }
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
        view.setBackgroundColor(Color.rgb(pileSize*5,pileSize*5,pileSize*32));
        for(int i = 0; i < roundNumber; i++){

            if(comps.get(i).takeThePile(pileSize)){
                pileSize = 0;
                view.setBackgroundColor(Color.rgb(0,0,0));
                break;
            }
        }
        if(limit == 0){



            for(CompPlayer c: comps){
                Log.d("Scores", Integer.toString(c.score));
                if(roundHighScore < c.score) roundHighScore = c.score;

                //c.score = 0;
                //c.takes = 0;
            }

            Log.d("roundHighScore", Integer.toString(roundHighScore));
            Log.d("playerScore", Integer.toString(playerScore));
            if(roundHighScore <= playerScore){
                Log.d("in if statment","true");
                playerWon = true;
            }
            if(playerWon){
                alertDialog.setMessage("You won round "+Integer.toString(roundNumber)+"!");
            }else{
                alertDialog.setMessage("You lost! Back to round 1!");
                roundNumber = 0;
            }

            if(roundNumber == 4){
                roundNumber = 1;
                if(!playerWon){
                    alertDialog.setMessage("You lost! Back to round 1!");
                }else{
                    alertDialog.setMessage("You beat all the comps!");
                }
            }else{
                roundNumber++;

            }
            alertDialog.show();


            remaining.setText("Remaining: "+ Integer.toString(limit));
            cancelRunnable = true;

        }

        remaining.setText("Remaining: "+ Integer.toString(limit));
        limit--;

        if(randomBool()){
            pileSize = 0;
        }
        pile.setText(Integer.toString(pileSize));
        score.setText("Score: " + Integer.toString(playerScore));
        setCompLabels();
    }

    public boolean randomBool(){
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
        roundHighScore = 0;
        for(CompPlayer c: comps){

            c.score = 0;
            c.takes = 0;
        }
    }
    public void setCompLabels(){
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
