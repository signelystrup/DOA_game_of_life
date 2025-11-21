package com.example.demo;

/**
 * Automated benchmark runner for grid strategies
 * Runs all 3 strategies sequentially for 60 seconds each
 * Saves results to benchmark_results.csv
 */
public class BenchmarkRunner {
    public static void main(String[] args) {
        // Set headless mode (no GUI needed for benchmarks)
        System.setProperty("java.awt.headless", "true");

        // Benchmark configuration - change these values to test different scenarios
        int bunnyCount = 5000;
        int wolfCount = 1000;
        int grassCount = 10;
        int fenceCount = 2;
        int durationSeconds = 60;

        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║          AUTOMATED BENCHMARK RUNNER                            ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println("\nRunning benchmarks for all 3 grid strategies...");
        System.out.println("Duration: " + durationSeconds + " seconds per strategy");
        System.out.println("Entity counts: " + bunnyCount + " bunnies, " + wolfCount +
                           " wolves, " + grassCount + " grass, " + fenceCount + " fences\n");

        GridStrategy[] strategies = {
            GridStrategy.SAFE_MAX_VISION,
            GridStrategy.OPTIMIZED_FOR_COMMON,
            GridStrategy.SMALL_CELLS
        };

        for (int i = 0; i < strategies.length; i++) {
            GridStrategy strategy = strategies[i];
            System.out.println("─────────────────────────────────────────────────────────────────");
            System.out.println("Benchmark " + (i + 1) + "/3: " + strategy);
            System.out.println("─────────────────────────────────────────────────────────────────");

            // Update global strategy
            GameConfig.STRATEGY = strategy;

            // Run benchmark
            runBenchmark(bunnyCount, wolfCount, grassCount, fenceCount, durationSeconds);

            // Wait between benchmarks (except after last one)
            if (i < strategies.length - 1) {
                System.out.println("\nWaiting 2 seconds before next benchmark...\n");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Benchmark interrupted");
                    return;
                }
            }
        }

        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║          BENCHMARK COMPLETE                                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println("\nResults saved to: benchmark_results.csv");
        System.out.println("You can now open this file in Excel/Sheets to analyze the data.");
    }

    /**
     * Run a single benchmark with specified parameters
     */
    private static void runBenchmark(int bunnyCount, int wolfCount, int grassCount, int fenceCount, int durationSeconds) {
        // Create headless game panel (no GUI)
        GamePanel gamePanel = new GamePanel();
        gamePanel.setUpGame(bunnyCount, wolfCount, grassCount, fenceCount);
        gamePanel.startGameThread();

        // Run for specified duration
        try {
            Thread.sleep(durationSeconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Benchmark interrupted");
        }

        // Stop game and save results
        gamePanel.stopGameThread();
        gamePanel.printFinalMetrics();
        gamePanel.saveResultsToCSV();
    }
}
