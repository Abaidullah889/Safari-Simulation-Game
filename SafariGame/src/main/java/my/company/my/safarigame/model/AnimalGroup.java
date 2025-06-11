/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.company.my.safarigame.model;

import java.util.*;

/**
 * Represents a group of animals of the same species in the safari game.
 * <p>
 * This class allows for managing multiple animals as a cohesive group, providing
 * functionality to add, remove, and perform group operations such as movement and reproduction.
 * </p>
 *
 * @author Muhammad Eman Aftab
 */
public class AnimalGroup {
    /** The species identifier for this animal group. */
    private String species;
    
    /** The list of animals belonging to this group. */
    private List<Animal> animals;
    
    /**
     * Constructs a new AnimalGroup for the specified species.
     *
     * @param species The species identifier for this group
     */
    public AnimalGroup(String species) {
        this.species = species;
        this.animals = new ArrayList<>();
    }
    
    /**
     * Adds an animal to this group.
     *
     * @param a The animal to add to the group
     */
    public void addAnimal(Animal a) {
        animals.add(a);
    }
    
    /**
     * Removes an animal from this group.
     *
     * @param a The animal to remove from the group
     */
    public void removeAnimal(Animal a) {
        animals.remove(a);
    }
    
    /**
     * Moves all animals in the group to the specified destination.
     *
     * @param destination The coordinate to move all animals to
     */
    public void moveGroup(Coordinate destination) {
        for (Animal a : animals) {
            a.move(destination);
        }
    }
    
    /**
     * Causes all animals in the group to reproduce.
     * <p>
     * Each animal in the group creates one offspring, and all offspring
     * are added to the group.
     * </p>
     */
    public void reproduceGroup() {
        List<Animal> newAnimals = new ArrayList<>();
        for (Animal a : animals) {
            newAnimals.add(a.reproduce());
        }
        animals.addAll(newAnimals);
    }
    
    /**
     * Gets the list of all animals in this group.
     *
     * @return The list of animals in the group
     */
    public List<Animal> getAnimals() {
        return animals;
    }
    
    /**
     * Gets the species identifier for this animal group.
     *
     * @return The species identifier
     */
    public String getSpecies() {
        return species;
    }
}