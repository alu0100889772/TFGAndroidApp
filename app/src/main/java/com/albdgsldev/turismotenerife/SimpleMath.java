package com.albdgsldev.turismotenerife;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class SimpleMath {
    public static Set<Double> getMode(double[] data) {
        if (data.length == 0) {
            return new TreeSet<>();
        }
        TreeMap<Double, Integer> map = new TreeMap<>(); //Map Keys are array values and Map Values are how many times each key appears in the array
        for (int index = 0; index != data.length; ++index) {
            double value = data[index];
            if (!map.containsKey(value)) {
                map.put(value, 1); //first time, put one
            }
            else {
                map.put(value, map.get(value) + 1); //seen it again increment count
            }
        }
        Set<Double> modes = new TreeSet<>(); //result set of modes, min to max sorted
        int maxCount = 1;
        Iterator<Integer> modeApperance = map.values().iterator();
        while (modeApperance.hasNext()) {
            maxCount = Math.max(maxCount, modeApperance.next()); //go through all the value counts
        }
        for (double key : map.keySet()) {
            if (map.get(key) == maxCount) { //if this key's value is max
                modes.add(key); //get it
            }
        }
        return modes;
    }

    //std dev function for good measure
    public static double getStandardDeviation(double[] data) {
        final double mean = getMean(data);
        double sum = 0;
        for (int index = 0; index != data.length; ++index) {
            sum += Math.pow(Math.abs(mean - data[index]), 2);
        }
        return Math.sqrt(sum / data.length);
    }


    public static double getMean(double[] data) {
        if (data.length == 0) {
            return 0;
        }
        double sum = 0.0;
        for (int index = 0; index != data.length; ++index) {
            sum += data[index];
        }
        return sum / data.length;
    }

    //by creating a copy array and sorting it, this function can take any data.
    public static double getMedian(double[] data) {
        double[] copy = Arrays.copyOf(data, data.length);
        Arrays.sort(copy);
        return (copy.length % 2 != 0) ? copy[copy.length / 2] : (copy[copy.length / 2] + copy[(copy.length / 2) - 1]) / 2;
    }
}
