package com.ontariotechu.sofe3980U;

import java.io.FileReader; 
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

/**
 * Evaluate Single Variable Continuous Regression
 */
public class App 
{
    public static void main(String[] args) {
        calculateErrors("model_1.csv");
        calculateErrors("model_2.csv");
        calculateErrors("model_3.csv");
    }

    public static void calculateErrors(String filePath) {
        FileReader filereader;
        List<String[]> allData;
        try {
            filereader = new FileReader(filePath); 
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
            allData = csvReader.readAll();
            csvReader.close();
        } catch(Exception e) {
            System.out.println("Error reading the CSV file: " + filePath);
            return;
        }

        double sumSquaredError = 0.0;
        double sumAbsoluteError = 0.0;
        double sumAbsoluteRelativeError = 0.0;
        double epsilon = 1e-10; // Avoid division by zero
        int n = allData.size();

        for (String[] row : allData) {
            double y_true = Double.parseDouble(row[0]);
            double y_predicted = Double.parseDouble(row[1]);

            double error = y_true - y_predicted;

            sumSquaredError += error * error;
            sumAbsoluteError += Math.abs(error);
            sumAbsoluteRelativeError += Math.abs(error) / (Math.abs(y_true) + epsilon);
        }

        double mse = sumSquaredError / n;
        double mae = sumAbsoluteError / n;
        double mare = (sumAbsoluteRelativeError / n) * 100; // Percentage

        System.out.println("=====================================");
        System.out.println("Results for: " + filePath);
        System.out.printf("MSE  = %.6f%n", mse);
        System.out.printf("MAE  = %.6f%n", mae);
        System.out.printf("MARE = %.6f%%%n", mare);
        System.out.println("=====================================");
    }
}
