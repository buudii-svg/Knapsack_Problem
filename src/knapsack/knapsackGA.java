package knapsack;

import java.util.Random;
import java.util.Vector;

public class knapsackGA {
    int NumberOfItems;
    int knapsack_size;
    final double cross_prob = 0.6;
    final double mutation_prob = 0.01;
    Random rand;
    Vector<item<Integer, Integer>> items;
    Vector<String> population;
    Vector<item<String, Integer>> fitness;
    Vector<String> selected;
    Vector<item<String, Integer>> offSprings;

    public knapsackGA(int NumberOfItems, int knapsack_size, Vector<item<Integer, Integer>> items) {
        this.NumberOfItems = NumberOfItems;
        this.knapsack_size = knapsack_size;
        this.items = items;
        rand = new Random();
        population = new Vector<>();
        fitness = new Vector<>();
        selected = new Vector<>();
        offSprings = new Vector<>();
    }

    public int binarySearch(Vector<Integer> cumulative, int l, int r, int x) {
        if (r >= l) {
            int mid = l + (r - l) / 2;

            if (cumulative.get(mid + 1) > x && cumulative.get(mid) <= x)
                return mid;

            if (cumulative.get(mid) > x)
                return binarySearch(cumulative, l, mid - 1, x);

            return binarySearch(cumulative, mid + 1, r, x);
        }

        return -1;
    }

    public int factorialOf(int n) {
        if (n <= 2) {
            return n;
        }
        return n * factorialOf(n - 1);
    }

    public String generateChromosome() {
        StringBuilder chromosome = new StringBuilder();
        for (int i = 0; i < NumberOfItems; i++) {
            chromosome.append(Math.round(rand.nextDouble()));
        }
        return chromosome.toString();
    }


    public int getPopulation() {
        int N = 50;
        int result = (int) (factorialOf(NumberOfItems) * 0.000002);
        if (result > N) N = (result % 2 == 0 ? result : result + 1);

        return N;
    }

    public int getWeight(String chromosome) {
        int totalWeight = 0;
        for (int i = 0; i < chromosome.length(); i++) {
            if (chromosome.charAt(i) == '1') {
                totalWeight += items.get(i).weight;
            }
        }
        return totalWeight;
    }

    public int getFitness(String chromosome) {
        int totalValue = 0;
        for (int i = 0; i < chromosome.length(); i++) {
            if (chromosome.charAt(i) == '1') {
                totalValue += items.get(i).value;
            }
        }
        return totalValue;
    }

    public void select() {
        Vector<Integer> cumulative = new Vector<>();
        int sum = 0;
        for (item<String, Integer> fitnessValue : fitness) {
            cumulative.add(sum);
            sum += fitnessValue.value;
        }
        cumulative.add(sum);
        for (int i = 0; i < fitness.size(); i++) {
            int r = (int) Math.floor(Math.random() * sum);
            selected.add(fitness.get(binarySearch(cumulative, 0, cumulative.size() - 1, r)).weight);
        }
    }

    public void crossover(String chromosome1, String chromosome2) {
        int chromosomeLength = chromosome1.length();
        String offspring1, offspring2;
        int turn = 0;
        while (true) {
            offspring1 = chromosome1;
            offspring2 = chromosome2;
            if (turn == 3) {
                break;
            }
            double r2 = rand.nextDouble();
            if (r2 <= cross_prob) {
                int r1 = (int) Math.floor(Math.random() * ((chromosomeLength - 1)) + 1);
                offspring1 = chromosome1.substring(0, r1);
                try {
                    offspring1 += chromosome2.substring(r1, chromosomeLength);
                } catch (Exception e) {
                    System.out.println(chromosomeLength + " " + r1 + " " + chromosome2);
                }
                offspring2 = chromosome2.substring(0, r1);
                offspring2 += chromosome1.substring(r1, chromosomeLength);
            }
            if (getWeight(offspring1) <= knapsack_size && getWeight(offspring2) <= knapsack_size) {
                break;
            }
            turn++;
        }
        offSprings.add(new item<>(offspring1, getFitness(offspring1)));
        offSprings.add(new item<>(offspring2, getFitness(offspring2)));
    }

    public void mutate() {
        for (item<String, Integer> offSpring : offSprings) {
            String temp;
            for (int j = 0; j < offSpring.weight.length(); j++) {
                double r = rand.nextDouble();
                if (r <= mutation_prob) {
                    temp = offSpring.weight.substring(0, j)
                            + (offSpring.weight.charAt(j) == '0' ? '1' : '0')
                            + offSpring.weight.substring(j + 1);
                    if (getWeight(temp) <= knapsack_size) {
                        offSpring.weight = temp;
                    }
                }
            }
        }
    }

    public void replace() {
        population.clear(); 
        fitness.clear();
        for (item<String, Integer> offSpring : offSprings) {
            population.add(offSpring.weight);
            fitness.add(new item<>(offSpring.weight, getFitness(offSpring.weight)));
        }
        offSprings.clear();
        selected.clear();
    }

    public void performGA(int caseNumber) {
        int popSize = getPopulation();
        while (population.size() < popSize) {
            String chromosome = generateChromosome();
            while (getWeight(chromosome) > knapsack_size) {
                chromosome = generateChromosome();
            }
            population.add(chromosome);
            int fitness = getFitness(chromosome);
            this.fitness.add(new item<>(chromosome, fitness));
        }
        int turn = 0;
        String bestChromosome = "";
        while (turn < 100) {
            select();
            for (int i = 0; i < selected.size(); i += 2) {
                crossover(selected.get(i), selected.get(i + 1));
            }
            mutate();
            replace();
            bestChromosome = (getFitness(bestChromosome) > getFitness(getMax()) ? bestChromosome : getMax());
            turn++;
        }
        print(bestChromosome, caseNumber);
    }

    public void print(String bestChromosome, int caseNumber) {
        StringBuilder output = new StringBuilder();
        int numberOfItems = 0;
        for (int i = 0; i < bestChromosome.length(); i++) {
            if (bestChromosome.charAt(i) == '1') {
                numberOfItems++;
                output.append(items.get(i).weight).append(" ").append(items.get(i).value).append('\n');
            }
        }
        //System.out.println("Weight = " + getWeight(bestChromosome));
        System.out.println("Case " + caseNumber + ": " + getFitness(bestChromosome));
        System.out.println(numberOfItems);
        System.out.println(output);
    }

    public String getMax() {
        String bestChromosome = "";
        int max = -1;
        for (String s : population) {
            if (max < getFitness(s)) {
                max = getFitness(s);
                bestChromosome = s;
            }
        }
        return bestChromosome;
    }

}