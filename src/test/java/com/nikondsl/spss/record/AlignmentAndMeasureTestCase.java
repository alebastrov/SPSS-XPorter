package com.nikondsl.spss.record;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: igor
 * Date: 04.03.12
 * Time: 22:19
 * To change this template use File | Settings | File Templates.
 */
public class AlignmentAndMeasureTestCase {
    @Test
    public void testAlignment() {
        assertEquals(Alignment.LEFT_ALIGNED, Alignment.valueOf(Alignment.LEFT_ALIGNED.ordinal()));
        assertEquals(Alignment.CENTERED, Alignment.valueOf(Alignment.CENTERED.ordinal()));
        assertEquals(Alignment.RIGHT_ALIGNED, Alignment.valueOf(Alignment.RIGHT_ALIGNED.ordinal()));
        assertEquals(Alignment.LEFT_ALIGNED, Alignment.valueOf(Integer.MAX_VALUE));
    }

    @Test
    public void testMeasure() {
        assertEquals(Measure.NOMINAL, Measure.valueOf(Measure.NOMINAL.ordinal()));
        assertEquals(Measure.ORDINAL, Measure.valueOf(Measure.ORDINAL.ordinal()));
        assertEquals(Measure.SCALE, Measure.valueOf(Measure.SCALE.ordinal()));
        assertEquals(Measure.UNKNOWN, Measure.valueOf(Measure.UNKNOWN.ordinal()));
        assertEquals(Measure.NOMINAL, Measure.valueOf(Integer.MAX_VALUE));
    }
}
