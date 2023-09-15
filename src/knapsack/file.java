package knapsack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;


public class file {
    int testCases = 0;
    int numberOfItems = 0;
    Vector<item<Integer, Integer>> items = new Vector<>();
    knapsackGA testcase;

    public void read() throws FileNotFoundException {
        File inputFile = new File("knapsack_input.txt");
        Scanner reader = new Scanner(inputFile);
        testCases = reader.nextInt();
        for (int i = 0; i < testCases; i++) {
            numberOfItems = reader.nextInt();
            int capacity = reader.nextInt();
            for (int j = 0; j < numberOfItems; j++) {
                items.add(new item<>(reader.nextInt(), reader.nextInt()));
            }

            testcase = new knapsackGA(numberOfItems, capacity, items);
            testcase.performGA(i + 1);
            items.clear();
        }
        reader.close();
    }
    public static void main(String[] args) throws IOException {
        file f = new file();
        f.read();
    }
}