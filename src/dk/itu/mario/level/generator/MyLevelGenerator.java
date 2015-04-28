package dk.itu.mario.level.generator;

import java.util.Random;

import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelGenerator;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.level.Level;
import dk.itu.mario.level.MyLevel;

public class MyLevelGenerator extends CustomizedLevelGenerator implements LevelGenerator{

	private double totalDeaths;
	private double [] playerDeathPercentages;
	public double [][] baselineDeathPercentages;
	private GamePlay playerMetrics;

	public LevelInterface generateLevel(GamePlay playerMetrics) {
		this.playerMetrics = playerMetrics;
		System.out.println(this.playerMetrics.timesOfDeathByJumpFlower);
		baselineDeaths(playerMetrics);
		playerStats(playerMetrics);
		MyLevel level = new MyLevel(320,15,new Random().nextLong(),1,LevelInterface.TYPE_OVERGROUND,playerMetrics);
		return simulatedAnnealing(level);
	}

	@Override
	public LevelInterface generateLevel(String detailedInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	//establishes our baseline death percentages and standard deviations to compare against
	public void baselineDeaths(GamePlay playerMetrics){
		baselineDeathPercentages = new double[][] {{10,1},{10,1},{10,1}};
	}

	//computes percentages from the relevant player stats: chomps, gaps, cannons
	public void playerStats(GamePlay playerMetrics){
		totalDeaths =
				playerMetrics.timesOfDeathByFallingIntoGap +
						playerMetrics.timesOfDeathByRedTurtle +
						playerMetrics.timesOfDeathByGoomba +
						playerMetrics.timesOfDeathByGreenTurtle +
						playerMetrics.timesOfDeathByArmoredTurtle +
						playerMetrics.timesOfDeathByJumpFlower +
						playerMetrics.timesOfDeathByCannonBall +
						playerMetrics.timesOfDeathByChompFlower;

		double percentChompDeaths = playerMetrics.timesOfDeathByChompFlower/totalDeaths;
		double percentGapDeaths = playerMetrics.timesOfDeathByFallingIntoGap/totalDeaths;
		double percentCannonDeaths = playerMetrics.timesOfDeathByCannonBall/totalDeaths;

		System.out.println("Player Death Stats Percentages:" +playerMetrics.timesOfDeathByFallingIntoGap);
		System.out.println("chomp: "+percentChompDeaths+" gaps: "+percentGapDeaths+" cannons: "+percentCannonDeaths);

		playerDeathPercentages = new double[] {percentChompDeaths, percentGapDeaths, percentCannonDeaths};
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
			MyLevel newLvl = neighbor(currLvl, k);
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
	    	int energy = 0;
	    	int[] buildPercentages = level.getBuildPercentages();
	    	for (int x = 0; x < buildPercentages.length-2; x++){
	    		energy += zScore(level, x, level.getBuildPercentages()[x]);
	    	}
	    	return energy;
	    }

	    public double zScore(MyLevel level, int statID, double stat){
	    	double[] playerBaseline = baselineDeathPercentages[statID];
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
	public MyLevel neighbor(MyLevel s, int iteration) {
		//TODO
		Random rand = new Random();
		int[] buildPercentages = s.getBuildPercentages();
		int first = 0;
		int second = 0;
		while (first == second) {
			first = rand.nextInt(3)+2;
			second = rand.nextInt(2);
		}
		if(iteration%2 == 0){
			buildPercentages[first] += 2;
			buildPercentages[second] -= 2;
		}
		else{
			buildPercentages[first] -= 2;
			buildPercentages[second] += 2;
		}
		return new MyLevel(320,15,new Random().nextLong(),1,LevelInterface.TYPE_OVERGROUND,playerMetrics, buildPercentages);
	}

}
