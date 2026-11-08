package model;

import Main.MainClass;
import utils.FileDBHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FoodManager {

    private List<Food> foods;

    public FoodManager() {
        try {
            foods = FileDBHelper.load("foods.dat", new ArrayList<Food>());
        } catch (IOException | ClassNotFoundException e) {
            foods = new ArrayList<>();
        }

        if (foods == null) {
            foods = new ArrayList<>();
        }
    }

    // GET FOODS (search first, then Merge Sort)
    public List<Food> getFoods(String query, int column, boolean ascending) {
        List<Food> found = searchFoods(query);
        return MainClass.mergeSort(found, column, ascending);
    }

    // ADD FOOD
    public void addFood(Food food) {
        if (findFood(food.getFoodID()) != null) {
            throw new IllegalArgumentException("Food ID already exists.");
        }

        foods.add(food);
        save();
    }

    // UPDATE FOOD
    public void updateFood(String originalID, Food updated) {
        Food old = findFood(originalID);

        if (old == null) {
            throw new IllegalArgumentException("Food not found.");
        }

        Food duplicate = findFood(updated.getFoodID());

        if (duplicate != null && duplicate != old) {
            throw new IllegalArgumentException("Food ID already exists.");
        }

        old.setFoodID(updated.getFoodID());
        old.setName(updated.getName());
        old.setCategory(updated.getCategory());
        old.setQuantity(updated.getQuantity());
        old.setPrice(updated.getPrice());
        old.setExpiryDate(updated.getExpiryDate());

        save();
    }

    // DELETE FOOD
    public void deleteFood(String foodID) {
        Food food = findFood(foodID);

        if (food == null) {
            throw new IllegalArgumentException("Food not found.");
        }

        foods.remove(food);
        save();
    }

    // FIND FOOD
    public Food findFood(String foodID) {
        for (Food food : foods) {
            if (food.getFoodID().equalsIgnoreCase(foodID.trim())) {
                return food;
            }
        }

        return null;
    }

    // SEARCH FOOD
    public List<Food> searchFoods(String query) {
        String q = query.trim().toLowerCase();
        List<Food> result = new ArrayList<>();

        for (Food food : foods) {
            if (food.getFoodID().toLowerCase().contains(q)
                    || food.getName().toLowerCase().contains(q)
                    || food.getCategory().toLowerCase().contains(q)
                    || food.getExpiryDate().contains(q)) {
                result.add(food);
            }
        }

        return result;
    }

    // SAVE DATA
    private void save() {
        try {
            FileDBHelper.save(foods, "foods.dat");
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not save food data: " + e.getMessage(), e);
        }
    }
}