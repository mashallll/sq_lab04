package com.ontariotechu.sofe3980U;

import java.io.FileReader; 
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

/**
 * Evaluate Multiclass Classification
 */
public class App {
    public static void main(String[] args) {
        String filePath = "model.csv";
        calculateMulticlassMetrics(filePath);
    }

    public static void calculateMulticlassMetrics(String filePath) {
        FileReader filereader;
        List<String[]> allData;

        try {
            filereader = new FileReader(filePath); 
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build(); 
            allData = csvReader.readAll();
            csvReader.close();
        } catch (Exception e) {
            System.out.println("Error reading the CSV file: " + filePath);
            return;
        }

        int numClasses = 5; // Classes are 1, 2, 3, 4, 5
        int[][] confusionMatrix = new int[numClasses][numClasses]; // Actual vs Predicted counts
        double crossEntropy = 0.0;
        int n = allData.size();
        double epsilon = 1e-10; // To avoid log(0)

        // Loop through each data point
        for (String[] row : allData) { 
            int y_true = Integer.parseInt(row[0]) - 1; // zero-based index (0-4)
            double[] y_predicted = new double[numClasses];

            for (int i = 0; i < numClasses; i++) {
                y_predicted[i] = Double.parseDouble(row[i + 1]);
            }

            // Cross-entropy calculation
            crossEntropy += -Math.log(y_predicted[y_true] + epsilon);

            // Predicted class is the one with the highest probability (argmax)
            int predictedClass = argMax(y_predicted);

            // Fill the confusion matrix [actual][predicted]
            confusionMatrix[y_true][predictedClass]++;
        }

        crossEntropy /= n;

        // Output the results
        System.out.println("=====================================");
        System.out.println("Results for: " + filePath);
        System.out.printf("Cross-Entropy = %.6f%n", crossEntropy);
        System.out.println("\nConfusion Matrix:");
        printConfusionMatrix(confusionMatrix);
        System.out.println("=====================================");
    }

    // Helper function to find the index of the largest value
    public static int argMax(double[] array) {
        int bestIndex = 0;
        double maxValue = array[0];

        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    // Helper function to print the confusion matrix neatly
    public static void printConfusionMatrix(int[][] matrix) {
        int numClasses = matrix.length;

        System.out.print("      ");
        for (int i = 0; i < numClasses; i++) {
            System.out.printf("Pred%-5d", i + 1);
        }
        System.out.println();

        for (int i = 0; i < numClasses; i++) {
            System.out.printf("Actual%-2d", i + 1);
            for (int j = 0; j < numClasses; j++) {
                System.out.printf("%-10d", matrix[i][j]);
            }
            System.out.println();
        }
    }
}
