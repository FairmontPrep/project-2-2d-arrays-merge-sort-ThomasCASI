import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GameBoard extends JFrame {
    private static final int SIZE = 8;
    private final JPanel[][] squares = new JPanel[SIZE][SIZE];
    private final String[][] unsortedArray;
    private final String[][] sortedArray;
    private boolean showingSorted = false;
    private final JButton toggleButton;

    public GameBoard() {
        setTitle("Chess Board");
        setSize(600, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Each row: [ "HP:xxx", unusedOrEmpty ]
        // We only need HP for sorting + color generation.
        unsortedArray = new String[SIZE * SIZE][2];
        fillUnsortedArray(unsortedArray);

        sortedArray = copyArray(unsortedArray);
        mergeSort(sortedArray, 0, sortedArray.length - 1);

        JPanel boardPanel = new JPanel(new GridLayout(SIZE, SIZE));
        boardPanel.setBounds(0, 0, 600, 600);
        add(boardPanel);

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                squares[row][col] = new JPanel(new BorderLayout());
                boardPanel.add(squares[row][col]);
            }
        }

        toggleButton = new JButton("Show Sorted");
        toggleButton.setBounds(200, 600, 200, 40);
        toggleButton.addActionListener(e -> {
            showingSorted = !showingSorted;
            if (showingSorted) {
                toggleButton.setText("Show Unsorted");
                updateBoard(sortedArray);
            } else {
                toggleButton.setText("Show Sorted");
                updateBoard(unsortedArray);
            }
        });
        add(toggleButton);

        updateBoard(unsortedArray);
    }

    private void fillUnsortedArray(String[][] array) {
        Random rand = new Random();
        for (int i = 0; i < array.length; i++) {
            int randomHP = rand.nextInt(500) + 1;
            array[i][0] = "HP:" + randomHP;
            array[i][1] = ""; // unused
        }
    }

    // Generate a color based on HP, e.g., range from green (low HP) to red (high HP)
    private Color generateColorFromHP(int hp) {
        // hp ranges from 1 to 500. We map that to 0..255 for red, 255..0 for green, etc.
        // Example: shift from green to red
        int red = (int) (255 * (hp / 500.0));
        int green = (int) (255 * (1 - hp / 500.0));
        return new Color(red, green, 0);
    }

    private void updateBoard(String[][] data) {
        int index = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                squares[row][col].removeAll();
                if (index < data.length) {
                    int hp = parseHPValue(data[index][0]);
                    squares[row][col].setBackground(generateColorFromHP(hp));
                    JLabel textLabel = new JLabel(data[index][0], SwingConstants.CENTER);
                    squares[row][col].add(textLabel, BorderLayout.CENTER);
                } else {
                    squares[row][col].setBackground(Color.LIGHT_GRAY);
                }
                squares[row][col].revalidate();
                squares[row][col].repaint();
                index++;
            }
        }
    }

    private String[][] copyArray(String[][] original) {
        String[][] copy = new String[original.length][2];
        for (int i = 0; i < original.length; i++) {
            copy[i][0] = original[i][0];
            copy[i][1] = original[i][1];
        }
        return copy;
    }

    private void mergeSort(String[][] arr, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid);
            mergeSort(arr, mid + 1, right);
            merge(arr, left, mid, right);
        }
    }

    private void merge(String[][] arr, int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;
        String[][] leftArray = new String[n1][2];
        String[][] rightArray = new String[n2][2];
        for (int i = 0; i < n1; i++) {
            leftArray[i][0] = arr[left + i][0];
            leftArray[i][1] = arr[left + i][1];
        }
        for (int j = 0; j < n2; j++) {
            rightArray[j][0] = arr[mid + 1 + j][0];
            rightArray[j][1] = arr[mid + 1 + j][1];
        }
        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (parseHPValue(leftArray[i][0]) <= parseHPValue(rightArray[j][0])) {
                arr[k][0] = leftArray[i][0];
                arr[k][1] = leftArray[i][1];
                i++;
            } else {
                arr[k][0] = rightArray[j][0];
                arr[k][1] = rightArray[j][1];
                j++;
            }
            k++;
        }
        while (i < n1) {
            arr[k][0] = leftArray[i][0];
            arr[k][1] = leftArray[i][1];
            i++;
            k++;
        }
        while (j < n2) {
            arr[k][0] = rightArray[j][0];
            arr[k][1] = rightArray[j][1];
            j++;
            k++;
        }
    }

    private int parseHPValue(String hpString) {
        return Integer.parseInt(hpString.replace("HP:", ""));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameBoard board = new GameBoard();
            board.setVisible(true);
        });
    }
}