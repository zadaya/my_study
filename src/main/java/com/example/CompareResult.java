package com.example;

import java.util.concurrent.atomic.AtomicInteger;

public class CompareResult {
    private AtomicInteger matchedCount = new AtomicInteger(0);
    private AtomicInteger onlyInA = new AtomicInteger(0);
    private AtomicInteger onlyInB = new AtomicInteger(0);
    private AtomicInteger mismatched = new AtomicInteger(0);

    public void incrementMatchedCount() {
        matchedCount.incrementAndGet();
    }

    public void incrementOnlyInA() {
        onlyInA.incrementAndGet();
    }

    public void incrementOnlyInB(int count) {
        onlyInB.addAndGet(count);
    }

    public void incrementMismatched() {
        mismatched.incrementAndGet();
    }

    public void add(CompareResult other) {
        matchedCount.addAndGet(other.matchedCount.get());
        onlyInA.addAndGet(other.onlyInA.get());
        onlyInB.addAndGet(other.onlyInB.get());
        mismatched.addAndGet(other.mismatched.get());
    }

    public int getMatchedCount() {
        return matchedCount.get();
    }

    public int getOnlyInA() {
        return onlyInA.get();
    }

    public int getOnlyInB() {
        return onlyInB.get();
    }

    public int getMismatched() {
        return mismatched.get();
    }
}