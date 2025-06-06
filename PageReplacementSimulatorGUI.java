import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.List; // Explicit import to avoid ambiguity

public class PageReplacementSimulatorGUI extends JFrame {
    private JTextField frameField, pageField;
    private JTextArea resultArea;
    private JComboBox<String> algorithmBox;

    public PageReplacementSimulatorGUI() {
        setTitle("Page Replacement Simulator");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Number of Frames:"));
        frameField = new JTextField();
        inputPanel.add(frameField);
        inputPanel.add(new JLabel("Page References (comma-separated):"));
        pageField = new JTextField();
        inputPanel.add(pageField);
        inputPanel.add(new JLabel("Select Algorithm:"));
        algorithmBox = new JComboBox<>(new String[]{"FIFO", "LRU", "Optimal"});
        inputPanel.add(algorithmBox);
        add(inputPanel, BorderLayout.NORTH);

        // Result Area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Run Button
        JButton runButton = new JButton("Run Simulation");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runSimulation();
            }
        });
        add(runButton, BorderLayout.SOUTH);
    }

    private void runSimulation() {
        try {
            int frames = Integer.parseInt(frameField.getText().trim());
            String[] pageStrings = pageField.getText().trim().split(",");
            int[] pages = Arrays.stream(pageStrings).mapToInt(Integer::parseInt).toArray();
            String algorithm = (String) algorithmBox.getSelectedItem();
            
            int pageFaults = 0;
            switch (algorithm) {
                case "FIFO":
                    pageFaults = FIFO(pages, frames);
                    break;
                case "LRU":
                    pageFaults = LRU(pages, frames);
                    break;
                case "Optimal":
                    pageFaults = Optimal(pages, frames);
                    break;
            }
            resultArea.setText(algorithm + " Page Faults: " + pageFaults);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input! Please check your values.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // FIFO Implementation
    private int FIFO(int[] pages, int frames) {
        Queue<Integer> queue = new LinkedList<>();
        int pageFaults = 0;
        for (int page : pages) {
            if (!queue.contains(page)) {
                if (queue.size() == frames) queue.poll();
                queue.add(page);
                pageFaults++;
            }
        }
        return pageFaults;
    }

    // LRU Implementation
    private int LRU(int[] pages, int frames) {
        java.util.List<Integer> memory = new ArrayList<>();
        int pageFaults = 0;
        for (int page : pages) {
            if (!memory.contains(page)) {
                if (memory.size() == frames) memory.remove(0);
                memory.add(page);
                pageFaults++;
            } else {
                memory.remove((Integer) page);
                memory.add(page);
            }
        }
        return pageFaults;
    }

    // Optimal Implementation
    private int Optimal(int[] pages, int frames) {
        java.util.List<Integer> memory = new ArrayList<>();
        int pageFaults = 0;
        for (int i = 0; i < pages.length; i++) {
            int page = pages[i];
            if (!memory.contains(page)) {
                if (memory.size() == frames) {
                    int replaceIndex = findOptimalReplacement(memory, pages, i);
                    memory.set(replaceIndex, page);
                } else {
                    memory.add(page);
                }
                pageFaults++;
            }
        }
        return pageFaults;
    }

    private int findOptimalReplacement(java.util.List<Integer> memory, int[] pages, int currentIndex) {
        int farthestIndex = -1, replaceIndex = -1;
        for (int i = 0; i < memory.size(); i++) {
            int page = memory.get(i);
            int index = findFutureIndex(pages, currentIndex, page);
            if (index == -1) return i;
            if (index > farthestIndex) {
                farthestIndex = index;
                replaceIndex = i;
            }
        }
        return replaceIndex;
    }

    private int findFutureIndex(int[] pages, int currentIndex, int page) {
        for (int i = currentIndex + 1; i < pages.length; i++) {
            if (pages[i] == page) return i;
        }
        return -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PageReplacementSimulatorGUI frame = new PageReplacementSimulatorGUI();
            frame.setVisible(true);
        });
    }
}
