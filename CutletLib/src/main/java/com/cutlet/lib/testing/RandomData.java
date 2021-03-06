/*
 * Copyright (C) 2017 rudolf.muehlbauer@gmail.com
 */

package com.cutlet.lib.testing;

import com.cutlet.lib.model.Dimension;
import com.cutlet.lib.model.Panel;
import com.cutlet.lib.model.PanelInstance;
import com.cutlet.lib.model.Project;
import com.cutlet.lib.model.StockSheet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author rmuehlba
 */
public class RandomData extends AbstractTestData {

    private final int seed;

    public RandomData(int seed) {
        this.seed = seed;
    }

    @Override
    public Project getData() {
        final Random rng = new Random();
        rng.setSeed(seed);
        int panelCount = rng.nextInt(20);

        StockSheet sheet = makeSheet(800, 600);

        final boolean canRotate = false;
        List<Panel> inputs = new ArrayList<>();
        for (int i = 0; i < panelCount; i++) {
            double x = rng.nextInt(300);
            double y = rng.nextInt(300);
            inputs.add(new Panel(sheet, new Dimension(x, y), "p" + i, canRotate, 1));
        }
        return makeProject(inputs, 1);
    }

    @Override
    public String getTitle() {
        return "Random_" + seed;
    }

}
