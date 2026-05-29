package com.example.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class GeocodeScheduler {

    private static final Logger logger = LoggerFactory.getLogger(GeocodeScheduler.class);

    @Value("${app.geocode.permits-per-second:3}")
    private double maxPermitsPerSecond = 3.0;

    private double availablePermits = 3.0;
    private long lastRefillNanos = System.nanoTime();

    private final Map<String, Future<?>> runningTasks = new ConcurrentHashMap<>();
    private final ThreadPoolExecutor executor;

    public GeocodeScheduler() {
        this.executor = new ThreadPoolExecutor(
                2, 4,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                r -> {
                    Thread t = new Thread(r, "geocode-scheduler");
                    t.setDaemon(true);
                    return t;
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        executor.prestartAllCoreThreads();
        logger.info("GeocodeScheduler executor initialized: coreThreads=2, maxThreads=4");
    }

    @PostConstruct
    public void init() {
        maxPermitsPerSecond = Math.max(0.1, maxPermitsPerSecond);
        synchronized (this) {
            availablePermits = Math.min(availablePermits, maxPermitsPerSecond);
        }
        logger.info("GeocodeScheduler rate limit initialized: {}/s", maxPermitsPerSecond);
    }

    public void acquire() {
        while (true) {
            long waitMs;
            synchronized (this) {
                refillTokens();
                if (availablePermits >= 1.0) {
                    availablePermits -= 1.0;
                    return;
                }
                double deficitSeconds = (1.0 - availablePermits) / maxPermitsPerSecond;
                waitMs = (long) (deficitSeconds * 1000) + 1;
            }

            try {
                Thread.sleep(waitMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void refillTokens() {
        long now = System.nanoTime();
        double elapsedSeconds = (now - lastRefillNanos) / 1_000_000_000.0;
        availablePermits = Math.min(maxPermitsPerSecond,
                availablePermits + elapsedSeconds * maxPermitsPerSecond);
        lastRefillNanos = now;
    }

    public synchronized void submit(String key, Runnable task) {
        Future<?> existing = runningTasks.get(key);
        if (existing != null && !existing.isDone()) {
            logger.info("Geocode task is already running, reuse existing task. key={}", key);
            return;
        }
        Future<?> future = executor.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                logger.error("Geocode task failed: key={}, {}", key, e.getMessage(), e);
            } finally {
                runningTasks.remove(key);
            }
        });
        runningTasks.put(key, future);
        logger.info("Submitted geocode task: key={}, activeTasks={}", key, runningTasks.size());
    }

    public void cancel(String key) {
        Future<?> old = runningTasks.remove(key);
        if (old != null && !old.isDone()) {
            logger.info("Cancel geocode task: key={}", key);
            old.cancel(true);
        }
    }

    public boolean isInterrupted() {
        return Thread.currentThread().isInterrupted();
    }

    public int getActiveTaskCount() {
        return runningTasks.size();
    }

    @PreDestroy
    public void destroy() {
        logger.info("Shutdown GeocodeScheduler and cancel running tasks");
        for (Map.Entry<String, Future<?>> entry : runningTasks.entrySet()) {
            entry.getValue().cancel(true);
        }
        runningTasks.clear();
        executor.shutdownNow();
    }
}
