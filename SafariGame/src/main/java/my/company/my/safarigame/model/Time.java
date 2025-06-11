/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.company.my.safarigame.model;

/**
 * Represents the time system in the safari game.
 * <p>
 * The Time class tracks the passage of time in the game world, managing hours, days,
 * and months. It provides methods for advancing time at different rates and determining
 * whether it's currently daytime or nighttime. The time system affects various game
 * mechanics such as animal behavior, visibility, and event scheduling.
 * </p>
 */
public class Time {
    /** The current day of the month (1-30). */
    private int day;
    
    /** The current month. */
    private int month;
    
    /** The current hour of the day (0-23). */
    private int hour;
    
    /**
     * Constructs a new Time instance with the specified day and month.
     * <p>
     * The hour is initialized to 8 AM by default.
     * </p>
     *
     * @param day The starting day (1-30)
     * @param month The starting month
     */
    public Time(int day, int month) {
        this.day = day;
        this.month = month;
        this.hour = 8; // Start at 8 AM
    }
    
    /**
     * Advances the game time based on the specified speed setting.
     * <p>
     * The amount of time advancement depends on the speed parameter:
     * </p>
     * <ul>
     *   <li>1 (Hour): Advances time by 1 hour</li>
     *   <li>2 (Day): Advances time by 6 hours</li>
     *   <li>3 (Week): Advances time by 7 days</li>
     * </ul>
     * <p>
     * This method automatically handles the overflow of hours to days and days to months.
     * </p>
     *
     * @param speed The speed setting (1=Hour, 2=Day, 3=Week)
     */
    public void advanceTime(int speed) {
        // 1 = Hour, 2 = Day, 3 = Week
        switch (speed) {
            case 1: // Hour
                hour += 1;
                break;
            case 2: // Day
                hour += 6; // Advance 6 hours at a time
                break;
            case 3: // Week
                day += 7;
                break;
        }
        // Handle hour overflow
        if (hour >= 24) {
            hour -= 24;
            day += 1;
        }
        // Handle day overflow
        if (day > 30) {
            day -= 30;
            month++;
        }
    }
    
    /**
     * Checks if it's currently daytime in the game world.
     * <p>
     * Daytime is defined as the hours between 6 AM and 6 PM (6-17 inclusive).
     * This can be used to determine visibility, animal behavior, or other
     * time-dependent game mechanics.
     * </p>
     *
     * @return true if it's currently daytime, false if it's nighttime
     */
    public boolean isDaytime() {
        // Day is from 6 AM to 6 PM (6-18)
        return hour >= 6 && hour < 18;
    }
    
    /**
     * Gets the current hour of the day.
     *
     * @return The current hour (0-23)
     */
    public int getHour() {
        return hour;
    }
    
    /**
     * Gets the current day of the month.
     *
     * @return The current day (1-30)
     */
    public int getDay() {
        return day;
    }
    
    /**
     * Gets the current month.
     *
     * @return The current month
     */
    public int getMonth() {
        return month;
    }
    
    /**
     * Returns a string representation of the current game time.
     *
     * @return A string in the format "Day X, Month Y"
     */
    @Override
    public String toString() {
        return "Day " + day + ", Month " + month;
    }
    
    /**
     * Advances the game time by a specific number of hours.
     * <p>
     * This method provides more precise control over time advancement compared
     * to the advanceTime method. It automatically handles the overflow of hours
     * to days and days to months.
     * </p>
     *
     * @param hours The number of hours to advance time by
     */
    public void advanceTimeByHours(int hours) {
        hour += hours;
        // Handle hour overflow
        while (hour >= 24) {
            hour -= 24;
            day += 1;
        }
        // Handle day overflow
        if (day > 30) {
            day -= 30;
            month++;
        }
    }
}