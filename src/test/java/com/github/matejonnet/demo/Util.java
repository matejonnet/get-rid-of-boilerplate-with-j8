package com.github.matejonnet.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by <a href="mailto:matejonnet@gmail.com">Matej Lazar</a> on 2015-04-06.
 */
public class Util {

    private Util() {}

    public static <T> List<T> newArrayList(T... objects) {
        return new ArrayList<>(Arrays.asList(objects));
    }

}
