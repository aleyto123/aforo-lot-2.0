package com.iot.aforo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "businesses")
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private String location;
    private int maxCapacity;
    private int minCapacityProfit;
    private int currentCount;
    private int totalEntriesToday;
    private int totalExitsToday;

    public Business() {
    }

    public Business(String name, int maxCapacity, int minCapacityProfit) {
        this.name = name;
        this.category = "GENERAL";
        this.location = "LIMA";
        this.maxCapacity = maxCapacity;
        this.minCapacityProfit = minCapacityProfit;
        this.currentCount = 0;
        this.totalEntriesToday = 0;
        this.totalExitsToday = 0;
    }

    public Business(String name, String category, String location, int maxCapacity) {
        this.name = name;
        this.category = normalizeCategory(category);
        this.location = location;
        this.maxCapacity = maxCapacity;
        this.minCapacityProfit = 0;
        this.currentCount = 0;
        this.totalEntriesToday = 0;
        this.totalExitsToday = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = normalizeCategory(category);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getMinCapacityProfit() {
        return minCapacityProfit;
    }

    public void setMinCapacityProfit(int minCapacityProfit) {
        this.minCapacityProfit = minCapacityProfit;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public int getTotalEntriesToday() {
        return totalEntriesToday;
    }

    public void setTotalEntriesToday(int totalEntriesToday) {
        this.totalEntriesToday = totalEntriesToday;
    }

    public int getTotalExitsToday() {
        return totalExitsToday;
    }

    public void setTotalExitsToday(int totalExitsToday) {
        this.totalExitsToday = totalExitsToday;
    }

    public void incrementEntries() {
        this.currentCount++;
        this.totalEntriesToday++;
    }

    public void incrementExits() {
        if (this.currentCount > 0) {
            this.currentCount--;
        }
        this.totalExitsToday++;
    }

    private String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "GENERAL";
        }
        return category.trim().toUpperCase();
    }
}
