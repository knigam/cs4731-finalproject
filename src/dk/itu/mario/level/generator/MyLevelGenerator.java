package dk.itu.mario.level.generator;

import java.util.Random;

import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelGenerator;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.level.Level;
import dk.itu.mario.level.MyLevel;

public class MyLevelGenerator extends CustomizedLevelGenerator implements LevelGenerator{

	public LevelInterface generateLevel(GamePlay playerMetrics) {
		MyLevel level = new MyLevel(320,15,new Random().nextLong(),1,LevelInterface.TYPE_OVERGROUND,playerMetrics);
		return simulatedAnnealing(level);
	}

	@Override
	public LevelInterface generateLevel(String detailedInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Runs simulatedAnnealing to find the best level
	 * @param currLvl
	 * @return
	 */
	public MyLevel simulatedAnnealing(MyLevel currLvl) {
		final double KMAX = 25;
		for (int k = 0; k < KMAX; k++) {
			final double T = temperature(k/KMAX);
			MyLevel newLvl = neighbor(currLvl);
			if (acceptanceProbability(currLvl, newLvl, T) > Math.random())
				currLvl = newLvl;
		}

		return currLvl;
	}

	/**
	 * This acts as E(), or the energy/goal function
	 * The lower the energy, the better the level is
	 * This gets the energy from the level's energy function
	 * @param level
	 * @return
	 */
	//compares the gameplay metrics to the metrics of the level
	    //the higher the difference between metrics, the higher the energy returned
	    //sums the differences in z scores of each metric
	    public double energy(MyLevel level){
	    	return zScore(level, "gaps", level.gaps);
	    }

	    public double zScore(MyLevel level, String statName, double stat){
	    	double[] playerBaseline = level.playerThresholds.get(statName);
	    	double mean = playerBaseline[0];
	    	double stDev = playerBaseline[1];
	    	double difference = stat - mean;
	    	return Math.abs(difference);
	    }

	/**
	 * annealing schedule function
	 */
	public double temperature(double r) {
		return 1/r;
	}

	/**
	 * This acts as P(), the acceptance probability function
	 * @param currLvl
	 * @param newLvl
	 * @param temp
	 * @return
	 */
	public double acceptanceProbability(MyLevel currLvl, MyLevel newLvl, double temp) {
		if (energy(newLvl) < energy(currLvl))
			return 1;
		else
			return Math.exp(-1 * (energy(newLvl)- energy(currLvl))/temp);
	}

	/**
	 * Candidate generator function to get a neighbor of current state
	 * @param s
	 * @return
	 */
	public MyLevel neighbor(MyLevel s) {
		//TODO
		Random rand = new Random();
		int[] odds = s.getOdds();
		int first = 0;
		int second = 0;
		while (first == second || odds[second] < 3) {
			first = rand.nextInt(odds.length);
			second = rand.nextInt(odds.length);
		}
		odds[first] += 2;
		odds[second] -= 2;
		s.setOdds(odds);
		return s;
	}

}
