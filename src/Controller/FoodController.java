package Controller;

import model.Food;
import model.FoodManager;
import java.util.List;

public class FoodController {

    private final FoodManager manager = new FoodManager();

    public List<Food> getFoods(String query, int column, boolean ascending) {
        return manager.getFoods(query, column, ascending);
    }

    public Food findFood(String foodID) {
        return manager.findFood(foodID);
    }

    public void addFood(Food food) {
        manager.addFood(food);
    }

    public void updateFood(String foodID, Food food) {
        manager.updateFood(foodID, food);
    }

    public void deleteFood(String foodID) {
        manager.deleteFood(foodID);
    }
}