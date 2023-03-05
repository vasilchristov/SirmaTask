package com.company;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        ProjectPairDuration projectPairDuration = new ProjectPairDuration();
        try {
            projectPairDuration.loadCSV("src/com/company/data.csv");
            final List<Integer> pair = projectPairDuration.findLongestProjectPair();
            System.out.println(pair.get(0) + ", " + pair.get(1) + ", " + pair.get(2));
        } catch (IOException e) {
            System.out.println("Error loading or saving CSV file: " + e.getMessage());
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
    }
}
