package Main;

import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import model.Food;
import view.MainForm;

public class MainClass {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.out.println("Using default appearance.");
            }

            new MainForm().setVisible(true);
        });
    }

    // MERGE SORT
    // Split the list in half, sort each half, then merge them.
    // column: 0 ID, 1 Name, 2 Category, 3 Quantity, 4 Price, 5 Expiry Date
    public static List<Food> mergeSort(List<Food> list,
                                       int column, boolean ascending) {
        if (list.size() <= 1) {
            return list;
        }

        int middle = list.size() / 2;

        List<Food> left = mergeSort(
                new ArrayList<>(list.subList(0, middle)),
                column, ascending);

        List<Food> right = mergeSort(
                new ArrayList<>(list.subList(middle, list.size())),
                column, ascending);

        return merge(left, right, column, ascending);
    }

    // Join two sorted lists into one sorted list.
    private static List<Food> merge(List<Food> left, List<Food> right,
                                    int column, boolean ascending) {
        List<Food> result = new ArrayList<>();
        int i = 0;
        int j = 0;

        while (i < left.size() && j < right.size()) {
            int compare = compareFoods(left.get(i), right.get(j), column);

            if (!ascending) {
                compare = -compare;
            }

            if (compare <= 0) {
                result.add(left.get(i));
                i++;
            } else {
                result.add(right.get(j));
                j++;
            }
        }

        while (i < left.size()) {
            result.add(left.get(i));
            i++;
        }

        while (j < right.size()) {
            result.add(right.get(j));
            j++;
        }

        return result;
    }

    // Compare two foods by the chosen column (ignoring letter case).
    private static int compareFoods(Food a, Food b, int column) {
        switch (column) {
            case 1:
                return a.getName().compareToIgnoreCase(b.getName());
            case 2:
                return a.getCategory().compareToIgnoreCase(b.getCategory());
            case 3:
                return Integer.compare(a.getQuantity(), b.getQuantity());
            case 4:
                return Double.compare(a.getPrice(), b.getPrice());
            case 5:
                return a.getExpiryDate().compareTo(b.getExpiryDate());
            default:
                return a.getFoodID().compareToIgnoreCase(b.getFoodID());
        }
    }
}