package com.company;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ProjectPairDuration {
    private static final String CSV_NULL_VALUE = "NULL";
    private static final String CSV_FILE_ERROR_MESSAGE = "Error loading or saving CSV file: ";
    private static final String DATE_FORMAT_ERROR_MESSAGE = "Error parsing date for line: ";
    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    private Map<Integer, Map<Integer, LocalDate[]>> employeeProjects;

    public ProjectPairDuration() {
        employeeProjects = new HashMap<>();
    }

    public void loadCSV(final String filePath) throws IOException, CsvValidationException {
        try (final CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                final int empID = Integer.parseInt(line[0].trim());
                final int projectID = Integer.parseInt(line[1].trim());

                LocalDate startDate = null;
                LocalDate endDate = null;

                for (final DateTimeFormatter formatter : DATE_FORMATTERS) {
                    try {
                        startDate = LocalDate.parse(line[2].trim(), formatter);
                        break;
                    } catch (final Exception e) {
                        System.out.println(DATE_FORMAT_ERROR_MESSAGE + Arrays.toString(line));
                    }
                }

                if (startDate == null) {
                    continue;
                }

                if (!line[3].equals(CSV_NULL_VALUE)) {
                    for (final DateTimeFormatter formatter : DATE_FORMATTERS) {
                        try {
                            endDate = LocalDate.parse(line[3].trim(), formatter);
                            break;
                        } catch (final Exception e) {
                            System.out.println(DATE_FORMAT_ERROR_MESSAGE + Arrays.toString(line));
                        }
                    }
                }

                if (endDate == null || endDate.isAfter(LocalDate.now())) {
                    endDate = LocalDate.now();
                }

                employeeProjects.computeIfAbsent(empID, k -> new HashMap<>()).put(projectID, new LocalDate[]{startDate, endDate});
            }
        } catch (final IOException e) {
            System.out.println(CSV_FILE_ERROR_MESSAGE + e.getMessage());
            throw e;
        } catch (final CsvValidationException e) {
            e.printStackTrace();
            throw e;
        }
    }


    public List<Integer> findLongestProjectPair() {
        List<Integer> pair = new ArrayList<>(Arrays.asList(0, 0, 0));
        int maxDuration = 0;
        for (int empID1 : employeeProjects.keySet()) {
            for (int empID2 : employeeProjects.keySet()) {
                if (empID1 < empID2) {
                    int duration = calculateProjectDuration(employeeProjects.get(empID1), employeeProjects.get(empID2));
                    if (duration > maxDuration) {
                        maxDuration = duration;
                        pair.set(0, empID1);
                        pair.set(1, empID2);
                        pair.set(2, maxDuration);
                    }
                }
            }
        }
        return pair;
    }

    private int calculateProjectDuration(Map<Integer, LocalDate[]> projects1, Map<Integer, LocalDate[]> projects2) {
        int duration = 0;
        for (int projectID1 : projects1.keySet()) {
            if (projects2.containsKey(projectID1)) {
                LocalDate[] dates1 = projects1.get(projectID1);
                LocalDate[] dates2 = projects2.get(projectID1);
                LocalDate startDate = dates1[0].isAfter(dates2[0]) ? dates1[0] : dates2[0];
                LocalDate endDate = dates1[1] == null || dates1[1].isAfter(LocalDate.now()) ? LocalDate.now() : dates1[1];
                if (dates2[1] != null && dates2[1].isBefore(endDate)) {
                    endDate = dates2[1];
                }
                duration += (int) ChronoUnit.MONTHS.between(startDate, endDate);
            }
        }
        return duration;
    }

}
