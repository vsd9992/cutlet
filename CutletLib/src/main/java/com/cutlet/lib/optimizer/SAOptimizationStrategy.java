/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cutlet.lib.optimizer;

// uses http://cs.gettysburg.edu/~tneller/resources/sls/index.html
import com.cutlet.lib.model.Panel;
import com.cutlet.lib.model.Project;
import com.cutlet.lib.data.cuttree.FreeNode;
import com.cutlet.lib.tneller.SimulatedAnnealer;
import com.cutlet.lib.tneller.State;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author rmuehlba
 */
public class SAOptimizationStrategy extends AbstractOptimizationStrategy {

    private final Logger log = Logger.getLogger(SAOptimizationStrategy.class.getName());
    public static java.util.Random random = new java.util.Random();

    @Override
    public OptimizationResult optimize(Project project, FitnessFunction fitness) {

        final int ITERATIONS = 10000;

        // Uncomment the desired problem below:
        OptState state = new OptState(project, fitness);
        //State state = new BinPackingProblem();
        
        state.perm = new int[project.getPanels().size()];
        for (int i = 0; i < project.getPanels().size(); i ++) {
            state.perm[i] = i;
        }
        state.prevPerm = state.perm;

        // Uncomment the desired stochastic local search below:
        //OptState minState = (OptState) new HillDescender(state).search(ITERATIONS);
        OptState minState = (OptState) new SimulatedAnnealer(state, 1, .99999).search(ITERATIONS); 

        return minState.result;
    }

    public OptimizationResult optimizeAux(Project project, List<Panel> panels) {

        OptimizationResult optimizationResult = new OptimizationResult();

//        optimizationResult.createNewLayout(p);
        for (Panel p : panels) {
            FreeNode candidate = findSheet(optimizationResult, p);
            if (candidate == null) {
                log.info("failed to find free space on sheet, creating a new layout");

                optimizationResult.createNewLayout(p);
                candidate = findSheet(optimizationResult, p);
            }

            candidate = cutToFit(candidate, project, p);
            candidate.setPanel(p);
        }

        return optimizationResult;
    }

    class OptState implements State {

        Project project;
        FitnessFunction fitness;
        int[] perm;
        int[] prevPerm;
        OptimizationResult result;

        public OptState(Project project, FitnessFunction fitness) {
            this.project = project;
            this.fitness = fitness;
        }

        @Override
        public void step() {
            prevPerm = perm.clone();
            final int posA = random.nextInt(perm.length);
            int posB = random.nextInt(perm.length);
            while (posA == posB) {
                posB = random.nextInt(perm.length);
            }
            final int tmp = perm[posA];
            perm[posA] = perm[posB];
            perm[posB] = tmp;
        }

        @Override
        public void undo() {
            perm = prevPerm;
        }

        @Override
        public double energy() {
            result = optimizeAux(project, lookup(project.getPanels(), perm));
            return fitness.fitness(result.getStats());
        }

        List<Panel> lookup(List<Panel> input, int[] lookup) {
            List<Panel> newArr = new ArrayList<>();
            for (int i : lookup) {
                newArr.add(input.get(i));
            }
            return newArr;
        }

        public Object clone() {
            OptState copy = new OptState(project, fitness);
            copy.perm = perm;
            copy.prevPerm = prevPerm;
            copy.result = result;
            return copy;
        }

    }

}