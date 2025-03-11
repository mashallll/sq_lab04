package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

/**
 * Evaluate Single Variable Binary Regression
 */
public class App {
    public static void main(String[] args) {
        calculateBinaryMetrics("model_1.csv");
        calculateBinaryMetrics("model_2.csv");
        calculateBinaryMetrics("model_3.csv");
    }

    public static void calculateBinaryMetrics(String filePath) {
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

        double bce = 0.0;
        int tp = 0, tn = 0, fp = 0, fn = 0;
        int n = allData.size();
        double epsilon = 1e-10; // avoid log(0)

        for (String[] row : allData) {
            int y_true = Integer.parseInt(row[0]);
            double y_pred = Double.parseDouble(row[1]);

            // Binary Cross Entropy
            bce += -(y_true * Math.log(y_pred + epsilon) + (1 - y_true) * Math.log(1 - y_pred + epsilon));

            // Threshold 0.5
            int y_pred_binary = (y_pred >= 0.5) ? 1 : 0;

            // Confusion matrix
            if (y_true == 1 && y_pred_binary == 1) tp++;
            if (y_true == 0 && y_pred_binary == 0) tn++;
            if (y_true == 0 && y_pred_binary == 1) fp++;
            if (y_true == 1 && y_pred_binary == 0) fn++;
        }

        bce /= n;

        // Metrics calculations
        double accuracy = (tp + tn) / (double) n;
        double precision = (tp + fp) > 0 ? tp / (double) (tp + fp) : 0;
        double recall = (tp + fn) > 0 ? tp / (double) (tp + fn) : 0;
        double f1 = (precision + recall) > 0 ? 2 * precision * recall / (precision + recall) : 0;

        System.out.println("=====================================");
        System.out.println("Results for: " + filePath);
        System.out.printf("BCE        = %.6f%n", bce);
        System.out.printf("Accuracy   = %.6f%n", accuracy);
        System.out.printf("Precision  = %.6f%n", precision);
        System.out.printf("Recall     = %.6f%n", recall);
        System.out.printf("F1-Score   = %.6f%n", f1);
        System.out.printf("TP=%d, TN=%d, FP=%d, FN=%d%n", tp, tn, fp, fn);

        calculateAUC(allData);
        System.out.println("=====================================");
    }

    public static void calculateAUC(List<String[]> allData) {
        int n = allData.size();
        int n_positive = 0;
        int n_negative = 0;

        for (String[] row : allData) {
            int y_true = Integer.parseInt(row[0]);
            if (y_true == 1) n_positive++;
            if (y_true == 0) n_negative++;
        }

        double[] tpr = new double[101];
        double[] fpr = new double[101];

        for (int i = 0; i <= 100; i++) {
            double threshold = i / 100.0;
            int tp = 0;
            int fp = 0;

            for (String[] row : allData) {
                int y_true = Integer.parseInt(row[0]);
                double y_pred = Double.parseDouble(row[1]);

                if (y_pred >= threshold) {
                    if (y_true == 1) tp++;
                    if (y_true == 0) fp++;
                }
            }

            tpr[i] = (n_positive > 0) ? tp / (double) n_positive : 0;
            fpr[i] = (n_negative > 0) ? fp / (double) n_negative : 0;
        }

        // Calculate AUC (trapezoidal rule)
        double auc = 0.0;
        for (int i = 1; i <= 100; i++) {
            auc += (tpr[i - 1] + tpr[i]) * Math.abs(fpr[i - 1] - fpr[i]) / 2.0;
        }

        System.out.printf("AUC-ROC    = %.6f%n", auc);
    }
}
