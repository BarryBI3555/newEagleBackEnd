package com.example.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 统一地理编码调度器 — 令牌桶限速 + 可取消异步任务管理
 *
 * <p>核心设计：
 * <ul>
 *   <li>令牌桶算法控制 API 调用速率（每秒3次），锁仅持有极短时间，sleep 在锁外</li>
 *   <li>相同 key 的新任务自动取消旧任务（切换日期时止损，不浪费配额）</li>
 *   <li>共享线程池替代原来分散的 geocodeExecutor 和 executorService</li>
 * </ul>
 */
@Component
public class GeocodeScheduler {

    private static final Logger logger = LoggerFactory.getLogger(GeocodeScheduler.class);

    /** 每秒最大 API 请求数 */
    private static final double MAX_PERMITS_PER_SECOND = 3.0;

    // ==================== 令牌桶 ====================
    private double availablePermits = MAX_PERMITS_PER_SECOND;
    private long lastRefillNanos = System.nanoTime();

    // ==================== 任务追踪 ====================
    private final Map<String, Future<?>> runningTasks = new ConcurrentHashMap<>();

    // ==================== 线程池 ====================
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
        logger.info("GeocodeScheduler 初始化: 核心线程=2, 最大=4, 限速={}/s", MAX_PERMITS_PER_SECOND);
    }

    // ==================== 限速 API ====================

    /**
     * 获取一个 API 调用许可（阻塞直到令牌可用）。
     * 锁内仅计算令牌，sleep 在锁外，不阻塞其他线程进入。
     */
    public void acquire() {
        long waitMs = 0;
        synchronized (this) {
            refillTokens();
            if (availablePermits >= 1.0) {
                availablePermits -= 1.0;
                return;
            }
            double deficitSeconds = (1.0 - availablePermits) / MAX_PERMITS_PER_SECOND;
            waitMs = (long) (deficitSeconds * 1000) + 1;
        }
        if (waitMs > 0) {
            try {
                Thread.sleep(waitMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        synchronized (this) {
            refillTokens();
            availablePermits -= 1.0;
        }
    }

    private void refillTokens() {
        long now = System.nanoTime();
        double elapsedSeconds = (now - lastRefillNanos) / 1_000_000_000.0;
        availablePermits = Math.min(MAX_PERMITS_PER_SECOND,
                availablePermits + elapsedSeconds * MAX_PERMITS_PER_SECOND);
        lastRefillNanos = now;
    }

    // ==================== 任务调度 API ====================

    /**
     * 提交异步任务。自动先取消同 key 的旧任务。
     *
     * @param key  任务标识（如 "hotmap:2026-05-28"、"location:2026-05-28"）
     * @param task 待执行的 Runnable
     */
    public void submit(String key, Runnable task) {
        cancel(key);
        Future<?> future = executor.submit(() -> {
            try {
                task.run();
            } catch (Exception e) {
                logger.error("地理编码任务异常: key={}, {}", key, e.getMessage(), e);
            } finally {
                runningTasks.remove(key);
            }
        });
        runningTasks.put(key, future);
        logger.info("提交地理编码任务: key={}, 活跃任务数={}", key, runningTasks.size());
    }

    /**
     * 取消指定 key 的运行中任务（中断线程）。
     */
    public void cancel(String key) {
        Future<?> old = runningTasks.remove(key);
        if (old != null && !old.isDone()) {
            logger.info("取消旧的地理编码任务: key={}", key);
            old.cancel(true);
        }
    }

    /**
     * 检查当前线程是否被中断（供任务循环内部使用）。
     */
    public boolean isInterrupted() {
        return Thread.currentThread().isInterrupted();
    }

    /**
     * 返回当前活跃任务数（供监控使用）。
     */
    public int getActiveTaskCount() {
        return runningTasks.size();
    }

    @PreDestroy
    public void destroy() {
        logger.info("关闭 GeocodeScheduler，取消所有运行中任务");
        for (Map.Entry<String, Future<?>> entry : runningTasks.entrySet()) {
            entry.getValue().cancel(true);
        }
        runningTasks.clear();
        executor.shutdownNow();
    }
}
