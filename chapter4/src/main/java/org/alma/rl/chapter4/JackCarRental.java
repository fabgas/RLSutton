package org.alma.rl.chapter4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Sutton book on Reinforcment learning
 * Exercice 4.3
 *  Time step are days
 *  state : number of cars at each location at the end of the day
 *  action : net number of cars that moving between both location
 *
 *  Rappel : policy iteration
 *      Starting with one policy, go for v_k to v_(k+1)
 *      checking if policy is better
 *  */
public class JackCarRental {
    static Logger logger = LoggerFactory.getLogger(JackCarRental.class);
    public static  void main (String[] args) {
        logger.info("Jack's car rental problem");
        logger.info("State : number of cars at each location at the end of the day");
        logger.info("Action : net number of cars that moving between both location");
        logger.info("Reward for rent a car: " + Constant.RENT_REWARD);
        logger.info("Cont for moving a car by night: " + Constant.COST_MOVE);
        Solution solution = new Solution();
        solution.doSearch();
        logger.info("optimal policy found");
    }
}
