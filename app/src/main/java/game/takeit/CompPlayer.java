package game.takeit;


import java.util.Random;

public class CompPlayer {
    public int score = 0;
    int lowestTake;
    int highestTake;
    public int takes = 0;

    public CompPlayer(int lowestTake, int highestTake){
        this.highestTake = highestTake;
        this.lowestTake = lowestTake;

    }

    public boolean takeThePile(int pileSize){
        Random rand = new Random();
        int randNum = rand.nextInt(highestTake -lowestTake )+ 1 +lowestTake;

        if(randNum == pileSize && takes < 2){
            takes++;
            score += pileSize;
            return true;
        }
        return false;
    }
}
