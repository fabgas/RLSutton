package org.alma.rl.chapter4;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.LongStream;

public class Solution {
    private Logger logger = LoggerFactory.getLogger(Solution.class);
    // Policy ?
    private Integer[][] policy = new Integer[Constant.MAX_CARS_IN_LOCATION+1][Constant.MAX_CARS_IN_LOCATION+1];
    private Double[][] stateVal = new Double[Constant.MAX_CARS_IN_LOCATION+1][Constant.MAX_CARS_IN_LOCATION+1];
    private Double[][] poisson = new Double[Constant.POISSON_MAX+1][Constant.POISSON_MAX+1];
    public void doSearch() {
        init();
        policyEvalution();
        for(int i = 0; i<Constant.MAX_CARS_IN_LOCATION+1;i++) {
           for(int j = 0; j<Constant.MAX_CARS_IN_LOCATION+1;j++) {
               System.out.println(i + " / " + j + " : " + policy[i][j] + " "+ stateVal[i][j]);
           }
      }
    }
    private void init() {
        initPolicy();
        initStateVal();
    }

    private void initPolicy() {
        logger.info("initialize policy");
        for(int i = 0; i<Constant.MAX_CARS_IN_LOCATION+1;i++) {
            for(int j = 0; j<Constant.MAX_CARS_IN_LOCATION+1;j++) {
                policy[i][j] = Constant.INITIAL_POLICY_VALUE;
            }
        }

    }
    private void initStateVal() {
        logger.info("initialize state");
        for(int i = 0; i<Constant.MAX_CARS_IN_LOCATION+1;i++) {
            for(int j = 0; j<Constant.MAX_CARS_IN_LOCATION+1;j++) {
                stateVal[i][j] = 0.0d;
            }
        }

    }
    private void policyEvalution() {
        Boolean improvePolicy = Boolean.FALSE;
        Integer iteration =-1;
        while(!improvePolicy) {
            Double[][] newStateVal = new Double[Constant.MAX_CARS_IN_LOCATION + 1][Constant.MAX_CARS_IN_LOCATION + 1];
            iteration++;
            logger.info("Tentative pour améliorer la fonction valeur "+ iteration);
            // boucle sur les etats, passons l'action
            for (int i = 0; i <= Constant.MAX_CARS_IN_LOCATION; i++) {
                for (int j = 0; j <= Constant.MAX_CARS_IN_LOCATION; j++) {
                    State initState = new State();
                    initState.setCarsIn1(i);
                    initState.setCarsIn2(j);
                    Action a = new Action();
                    // Je suis dans l'état (i,j) je suis ma politique .
                    a.setExchangeCar(policy[i][j]);
                    newStateVal[i][j] = expectedReturn(initState, a, stateVal);
                    //logger.info(i +" / " + j + " :" + newStateVal[i][j] + " ->" +stateVal[i][j]);
                }
            }

            // Compare previous state value function
            Double diff = compareStates(newStateVal);
            logger.info("Valeur de la diférence " + diff);
            for (int i = 0; i <= Constant.MAX_CARS_IN_LOCATION; i++) {
                for (int j = 0; j <= Constant.MAX_CARS_IN_LOCATION; j++) {
                    stateVal[i][j] = newStateVal[i][j];
                }

            }
            if (diff < 0.0001) {
                // new state ok
                improvePolicy = true;
                for(int i = 0; i<Constant.MAX_CARS_IN_LOCATION+1;i++) {
                    for(int j = 0; j<Constant.MAX_CARS_IN_LOCATION+1;j++) {
                        System.out.println(i + " / " + j + " : " + policy[i][j] + " "+ newStateVal[i][j]);
                    }
                }
            }


            if (improvePolicy) {
                logger.info("Tentative pour améliorer la policy");
                // rechercher d'une meilleure stratégie
                Integer newPolicy[][] = new Integer[Constant.MAX_CARS_IN_LOCATION + 1][Constant.MAX_CARS_IN_LOCATION + 1];
                for (int i = 0; i <= Constant.MAX_CARS_IN_LOCATION; i++) {
                    Double[] actionsReturn = new Double[11];
                    for (int j = 0; j <= Constant.MAX_CARS_IN_LOCATION; j++) {
                        for (int a = -Constant.MAX_CARS_MOVE; a < Constant.MAX_CARS_MOVE+1; a++) {
                            if (a==0) {
                                State initialState = new State();
                                initialState.setCarsIn1(i);
                                initialState.setCarsIn2(j);
                                Action action = new Action();
                                action.setExchangeCar(a);
                                actionsReturn[Constant.MAX_CARS_MOVE + a] = expectedReturn(initialState, action, stateVal);
                            }
                            else
                            if ((a > 0 && i >= a) || (a < 0 && j >= Math.abs(a))) {
                                State initialState = new State();
                                initialState.setCarsIn1(i);
                                initialState.setCarsIn2(j);
                                Action action = new Action();
                                action.setExchangeCar(a);
                                actionsReturn[Constant.MAX_CARS_MOVE + a] = expectedReturn(initialState, action, stateVal);
                            } else {
                                actionsReturn[Constant.MAX_CARS_MOVE + a] = null;
                            }
                        }

                        // recherche de la meilleur action
                        int indexMeilleureAction = -Constant.MAX_CARS_MOVE;
                        Double currentMeilleurValue = actionsReturn[Constant.MAX_CARS_MOVE + indexMeilleureAction];
                        for (int bestIndex = -Constant.MAX_CARS_MOVE+1; bestIndex < Constant.MAX_CARS_MOVE+1; bestIndex++) {
                            if (currentMeilleurValue == null) {
                                currentMeilleurValue = actionsReturn[Constant.MAX_CARS_MOVE + bestIndex];
                                indexMeilleureAction = bestIndex;
                            } else if (actionsReturn[5 + bestIndex] != null) {
                                if (currentMeilleurValue < actionsReturn[Constant.MAX_CARS_MOVE + bestIndex]) {
                                    currentMeilleurValue = actionsReturn[Constant.MAX_CARS_MOVE + bestIndex];
                                    indexMeilleureAction = bestIndex;
                                }
                            }
                        }
                        newPolicy[i][j] = indexMeilleureAction;

                    }
                }
                improvePolicy = comparePolicy(newPolicy);
                if (!improvePolicy) {

                    for (int i = 0; i <= Constant.MAX_CARS_IN_LOCATION; i++) {
                        for (int j = 0; j <= Constant.MAX_CARS_IN_LOCATION; j++) {
                            policy[i][j] = newPolicy[i][j];
                        }
                    }

                }
            }
        }
    }
    /**
     * Calcul of the value function, given an initial state and and a Action
     * @param initialState
     * @param a
     * @param stateVal
     * @return
     */
    private Double expectedReturn(State initialState, Action a, Double[][] stateVal) {
        Double returnValue = 0.0;
        // initial cost

        // nouvel état initial
        State state_2 = stateAfterAction(initialState,a);
        // On boucle sur toutes les locations possibles
        for (int i =0 ;i < Constant.POISSON_MAX;i++) {
            for (int j = 0; j < Constant.POISSON_MAX; j++) {
                // probabilité pour que cela arrive
                Double rentalProd = poissonLaw(i,Constant.RENTAL_REQUEST_FIRST)*poissonLaw(j,Constant.RENTAL_REQUEST_SECOND);
                // true rental number
                Integer totalRent1 = Math.min(i,state_2.getCarsIn1());
                Integer totalRent2 = Math.min(j,state_2.getCarsIn2());
                Double reward = costAction(a);
                reward = reward + totalRent1 * Constant.RENT_REWARD + totalRent2 * Constant.RENT_REWARD;
                Integer finalCars11 = Math.min(state_2.getCarsIn1() - totalRent1,Constant.MAX_CARS_IN_LOCATION);
                Integer finalCars12 = Math.min(state_2.getCarsIn2() - totalRent2 ,Constant.MAX_CARS_IN_LOCATION);

                // maintenant les retours
                for (int ri =0 ;ri < Constant.POISSON_MAX;ri++) {
                    for (int rj = 0; rj < Constant.POISSON_MAX; rj++) {
                        Double returnProb = poissonLaw(ri,Constant.RETURN_FIRST) * poissonLaw(rj,Constant.RETURN_SECOND);
                        // nombre de voiture à la fin de la journée
                        Integer finalCars1 = Math.min(finalCars11+ri,Constant.MAX_CARS_IN_LOCATION);
                        Integer finalCars2 = Math.min(finalCars12 + rj,Constant.MAX_CARS_IN_LOCATION);
                        returnValue += returnProb * rentalProd * (reward + Constant.GAMMA * stateVal[finalCars1][finalCars2]);
                    }
                }
            }
        }

        return returnValue;
    }

    /**
     * Cacul next state after apply action
     * @param initialState
     * @param a
     * @return
     */
    private State stateAfterAction(State initialState, Action a) {
        Integer nbCars1 = Math.min(initialState.getCarsIn1() - a.getExchangeCar(),Constant.MAX_CARS_IN_LOCATION);
        Integer nbCars2 = Math.min(initialState.getCarsIn2() + a.getExchangeCar(),Constant.MAX_CARS_IN_LOCATION);
        if (nbCars1<0) nbCars1 =0;
        if (nbCars2<0) nbCars2 =0;
        State stateFinal = new State();
        stateFinal.setCarsIn1(nbCars1);
        stateFinal.setCarsIn2(nbCars2);
        return stateFinal;
    }
    /**
     * Return the cost of doing the action
     * @param a
     * @return
     */
    private Double costAction(Action a) {
        return - Math.abs(a.getExchangeCar()) * Constant.COST_MOVE * 1.0d;
    }

    /**
     * Caculate the value of the poisson law
     * @param number
     * @param average
     * @return
     */
    private Double poissonLaw(Integer number, Integer average) {
        if (poisson[number][average]!=null) return poisson[number][average];
        long facN = LongStream.rangeClosed(1, number).reduce(1, (long x, long y) -> x * y);
        Double averagePowerN = Math.pow(average,number);
        Double expAverage = Math.exp(-average);
        poisson[number][average] =  averagePowerN * expAverage / facN;
        return poisson[number][average];
    }

    /**
     * Return the absolute difference between states
     * @return
     */
    private Double compareStates(Double[][] newState){
        Double sum = 0.0;
        for(int i = 0; i<Constant.MAX_CARS_IN_LOCATION+1;i++) {
            for(int j = 0; j<Constant.MAX_CARS_IN_LOCATION+1;j++) {
                Double tempSum = newState[i][j] - stateVal[i][j];
                tempSum = tempSum * tempSum;
                sum += tempSum;
            }
        }
        return Math.sqrt(sum);
    }

    private Boolean comparePolicy(Integer[][] newPolicy) {
        for(int i = 0; i<Constant.MAX_CARS_IN_LOCATION+1;i++) {
            for(int j = 0; j<Constant.MAX_CARS_IN_LOCATION+1;j++) {
                Integer delta = newPolicy[i][j] - policy[i][j];
                if (delta != 0) return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
}
