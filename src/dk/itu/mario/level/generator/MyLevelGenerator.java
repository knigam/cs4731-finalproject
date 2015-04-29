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
	//chomps, gaps, cannons
	public void baselineDeaths(GamePlay playerMetrics){
		baselineDeathPercentages = new double[][] {{30,33},{30,33},{30,33}};
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

		System.out.println("Player Death Stats Percentages:" +totalDeaths);
		System.out.println("chomp: "+percentChompDeaths+" gaps: "+percentGapDeaths+" cannons: "+percentCannonDeaths);

		playerDeathPercentages = new double[] {percentChompDeaths, percentGapDeaths, percentCannonDeaths};
	}


	/**
	 * Runs simulatedAnnealing to find the best level
	 * @param currLvl
	 * @return
	 */	
	public MyLevel simulatedAnnealing(MyLevel currLvl) {
		final double KMAX = 200;
		for (int k = 0; k < KMAX; k++) {
			double T = temperature(k/KMAX);
			System.out.println("K: "+k);
			System.out.println("Temperature "+T);
			MyLevel newLvl = neighbor(currLvl, k);
			double aP = acceptanceProbability(currLvl, newLvl, T);
			System.out.println("AP: "+aP);
			if (aP> Math.random()){

				 for(int x: currLvl.getBuildPercentages())
		        	System.out.println(x);
		        System.out.println("................");

				if(acceptanceProbability(currLvl, newLvl, T) != 1)
					System.out.println("move down");
				currLvl = newLvl;
			}
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
	    	double energy = 0;
	    	int[] buildPercentages = level.getBuildPercentages();
	    	// for (int x = 0; x < buildPercentages.length-2; x++){
	    	// 	energy += zScore(level, x+2, playerDeathPercentages[x]);
	    	// }
	    	energy = zScore(level, 1, buildPercentages[1]);
	    	return energy;
	    }

	    public double zScore(MyLevel level, int statID, double stat){
	    	double difference = stat - baselineDeathPercentages[1][0];
	    	double zScore = difference/baselineDeathPercentages[1][1];
	    	return zScore;
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
		System.out.println(energy(newLvl) +", "+energy(currLvl));
		if (energy(newLvl) < energy(currLvl))
			return 1;
		else
			//return 0;
			return Math.exp(-1 * (energy(newLvl)- energy(currLvl))/temp);
	}

	/**
	 * Candidate generator function to get a neighbor of current state
	 * @param s
	 * @return
	 */
	public MyLevel neighbor(MyLevel s, double T) {
		//TODO
		Random rand = new Random();
		int[] buildPercentages = new int[5];
		for(int x = 0; x < 5; x++){
			buildPercentages[x] = s.getBuildPercentages()[x];
		}

		//int[] buildPercentages = s.getBuildPercentages();
		int first = 0;
		int second = 0;
		do {
			while (first == second || buildPercentages[second] < 3) {
				first = rand.nextInt(5);
				second = rand.nextInt(5);
			}
			buildPercentages[first] += 2;
			buildPercentages[second] -= 2;
			MyLevel newlvl = new MyLevel(320,15,new Random().nextLong(),1,LevelInterface.TYPE_OVERGROUND,playerMetrics, buildPercentages);
		} while(energy(newLvl) - energy(s) < T))
		return newLvl;
	}

}
