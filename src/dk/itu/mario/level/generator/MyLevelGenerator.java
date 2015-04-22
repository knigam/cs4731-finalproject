package dk.itu.mario.level.generator;

import java.util.Random;

import dk.itu.mario.MarioInterface.GamePlay;
import dk.itu.mario.MarioInterface.LevelGenerator;
import dk.itu.mario.MarioInterface.LevelInterface;
import dk.itu.mario.level.Level;
import dk.itu.mario.level.MyLevel;

public class MyLevelGenerator extends CustomizedLevelGenerator implements LevelGenerator{

	public LevelInterface generateLevel(GamePlay playerMetrics) {
		LevelInterface level = new MyLevel(320,15,new Random().nextLong(),1,LevelInterface.TYPE_OVERGROUND,playerMetrics);
		return level;
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
	public Level simulatedAnnealing(Level currLvl) {
		final double KMAX = 25;
		for (int k = 0; k < KMAX; k++) {
			final double T = temperature(k/KMAX);
			Level newLvl = neighbor(currLvl);
			if (acceptanceProbability(currLvl, newLvl, T) > Math.random())
				currLvl = newLvl;
		}

		return currLvl;
	}

	/**
	 * This acts as E(), or the energy/goal function
	 * The lower the energy, the better the level is
	 * @param level
	 * @return
	 */
	public double energy(LevelInterface level){
		//TODO
		return 0.0;
	}

	/**
	 * annealing schedule function
	 */
	public double temperature(double r) {
		return 1/r;
	}

	/**
	 * This acts as P(), the acceptance probability function
	 * @param curr
	 * @param temp
	 * @return
	 */
	public double acceptanceProbability(Level currLvl, Level newLvl, double temp) {
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
	public Level neighbor(Level s) {
		//TODO
		return null;
	}

}
