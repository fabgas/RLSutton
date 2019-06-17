package org.alma.rl.chapter4;

/**
 * Constant for the Jack's car rental  problem.
 */
public class Constant {
    // reward for rent a car (in $)
    public static final Integer RENT_REWARD = 10;
    // cost for moving car from one place to another
    public static final Integer COST_MOVE = 2;
    // rental request(lambda) for the first place
    public static final Integer RENTAL_REQUEST_FIRST = 3;
    // rental request (lambda) for the second place
    public static final Integer RENTAL_REQUEST_SECOND = 4;
    // return for the first place
    public static final Integer RETURN_FIRST= 3;
    // return for the second place
    public static final Integer RETURN_SECOND = 2;
    // maximum cars in one location
    public static final Integer MAX_CARS_IN_LOCATION = 20;
    // maximum cars than can move from one place to another by night
    public static final Integer MAX_CARS_MOVE = 5;
    // gamma
    public static final Double GAMMA = 0.9;

    // initial policy value
    public static final Integer INITIAL_POLICY_VALUE = 0;
    // initial value function value
    public static final Double INITIAL_VALUE_FUNCTION_VALUE = 0.0d;
    //maximum value for poisson law
    public static final Integer POISSON_MAX = 10;
}
