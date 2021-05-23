package scheduler;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class MainView extends JFrame {

    private int[][] inputData = null;
    private int[][] outputData = null;
    private int[][] ganttChartData = null;
    HashMap<Integer, int[][]> result;

    public MainView(int mode) {
        if (mode == 0)
            setTitle("First Come First Served Scheduling");
        else if (mode == 1)
            setTitle("Non-Preemptive Shortest Job First Scheduling");
        else if (mode == 2)
            setTitle("Preemptive Shortest Job First Scheduling");
        else if (mode == 3)
            setTitle("Round Robin Scheduling");

        setSize(700, 650);
        setResizable(false);

        Font font = new Font("맑은 고딕", Font.PLAIN, 15);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(new JPanel(), BorderLayout.NORTH);
        container.add(new JPanel(), BorderLayout.SOUTH);
        container.add(new JPanel(), BorderLayout.WEST);
        container.add(new JPanel(), BorderLayout.EAST);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        container.add(mainPanel, BorderLayout.CENTER);

        JLabel inputLabel = new JLabel("입력");
        inputLabel.setFont(font);
        inputLabel.setHorizontalAlignment(JLabel.LEFT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weighty = 0.2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(inputLabel, gbc);

        String[] inputColumnNames = {"프로세스", "도착시간", "실행시간"};
        DefaultTableModel inputModel = new DefaultTableModel(inputColumnNames, 0);

        JTable inputTable = new JTable(inputModel);
        inputTable.getTableHeader().setReorderingAllowed(false);
        inputTable.getTableHeader().setResizingAllowed(false);
        inputTable.setEnabled(false);
        inputTable.setFont(font);

        JScrollPane inputTableScrollPane = new JScrollPane(inputTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 1.5;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(inputTableScrollPane, gbc);

        JPanel trashPanel = new JPanel();
        gbc.gridy = 0;
        gbc.gridy = 2;
        gbc.weighty = 0.2;
        mainPanel.add(trashPanel, gbc);

        JLabel outputLabel = new JLabel("출력");
        outputLabel.setFont(font);
        outputLabel.setHorizontalAlignment(JLabel.LEFT);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 0.2;
        mainPanel.add(outputLabel, gbc);

        String[] outputColumnNames = {"프로세스", "실행시간", "대기시간"};
        DefaultTableModel outputModel = new DefaultTableModel(outputColumnNames, 0);

        JTable outputTable = new JTable(outputModel);
        outputTable.setFont(font);
        outputTable.getTableHeader().setReorderingAllowed(false);
        outputTable.getTableHeader().setResizingAllowed(false);
        outputTable.setEnabled(false);

        JScrollPane outputTableScrollPane = new JScrollPane(outputTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 1.5;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(outputTableScrollPane, gbc);

        JLabel totalBurstTimeLabel = new JLabel("전체 실행시간 : ");
        totalBurstTimeLabel.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(totalBurstTimeLabel, gbc);

        JLabel averageWaitTimeLabel = new JLabel("평균 대기시간 : ");
        averageWaitTimeLabel.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(averageWaitTimeLabel, gbc);

        JLabel throughputLabel = new JLabel("처리량 :");
        throughputLabel.setFont(font);
        gbc.gridx = 0;
        gbc.gridy = 7;
        mainPanel.add(throughputLabel, gbc);

       JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(buttonPanel, gbc);

        GanttChartPanel ganttChartPanel = new GanttChartPanel();
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(ganttChartPanel, gbc);

        JButton fileChooserButton = new JButton("파일 열기");
        fileChooserButton.setFont(font);
        fileChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = fileOpen(fileChooserButton);
                if (filePath == null)
                    return;
                inputData = loadInputData(filePath);
                for (int i = 0; i < inputData.length; i++) {
                    inputModel.addRow(new Integer[]{inputData[i][0], inputData[i][1], inputData[i][2]});
                }
                repaint();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.anchor = GridBagConstraints.WEST;
        buttonPanel.add(fileChooserButton, gbc);

        JPanel trashPanel1 = new JPanel();
        gbc.gridx = 1;
        gbc.gridy = 0;
        buttonPanel.add(trashPanel1, gbc);

        JTextField timeQuantumField = new JTextField(3);

        JButton runButton = new JButton("실행");
        runButton.setFont(font);
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalBurstTime = 0;
                int totalWaitingTime = 0;
                int averageWaitingTime = 0;
                if (inputData == null)
                    return;
                if (mode == 0)
                    result = SchedulingService.getInstance().FCFSScheduling(inputData);
                else if (mode == 1)
                    result = SchedulingService.getInstance().NSJFScheduling(inputData);
                else if (mode == 2)
                    result = SchedulingService.getInstance().PSJFScheduling(inputData);
                else if (mode == 3) {
                    if(timeQuantumField.getText().equals(""))
                        return;
                    timeQuantumField.setEnabled(false);
                    result = SchedulingService.getInstance().RRScheduling(inputData, Integer.parseInt(timeQuantumField.getText()));
                }
                outputData = result.get(0);
                ganttChartData = result.get(1);
                for (int i = 0; i < outputData.length; i++) {
                    outputModel.addRow(new Integer[]{outputData[i][0], outputData[i][1], outputData[i][2]});
                    totalBurstTime += outputData[i][1];
                    totalWaitingTime += outputData[i][2];
                }
                averageWaitingTime = totalWaitingTime / outputData.length;
                totalBurstTimeLabel.setText("전체 실행시간 : " + totalBurstTime);
                averageWaitTimeLabel.setText("평균 대기시간 : " + averageWaitingTime);
                throughputLabel.setText("처리량 : " + (float)inputData.length / totalBurstTime);
                ganttChartPanel.setResult(ganttChartData);
                ganttChartPanel.repaint();
                fileChooserButton.setEnabled(false);
                runButton.setEnabled(false);
                repaint();
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 0;
        buttonPanel.add(runButton, gbc);

        if (mode == 3) {
            JPanel trashPanel2 = new JPanel();
            gbc.gridx = 3;
            gbc.gridy = 0;
            buttonPanel.add(trashPanel2, gbc);

            JLabel timeQuantumLabel = new JLabel("타임 퀀텀 : ");
            timeQuantumLabel.setFont(font);
            gbc.gridx = 4;
            gbc.gridy = 0;
            buttonPanel.add(timeQuantumLabel, gbc);

            timeQuantumField.setFont(font);
            gbc.gridx = 5;
            gbc.gridy = 0;
            buttonPanel.add(timeQuantumField, gbc);

            JPanel trashPanel3 = new JPanel();
            gbc.gridx = 6;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            buttonPanel.add(trashPanel3, gbc);
        } else {
            JPanel trashPanel2 = new JPanel();
            gbc.gridx = 3;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            buttonPanel.add(trashPanel2, gbc);
        }
        setVisible(true);
    }

    public String fileOpen(Component parent) {
        String filePath = "";

        JFileChooser fileChooser = new JFileChooser();
        setFileChooserFont(fileChooser.getComponents(), parent.getFont());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("텍스트 문서(*.txt)", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle("파일 열기");
        fileChooser.setAcceptAllFileFilterUsed(false);

        int returnValue = fileChooser.showOpenDialog(parent);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            filePath = fileChooser.getSelectedFile().getPath();
            return filePath;
        } else {
            return null;
        }
    }

    public void setFileChooserFont(Component[] comp, Font font) {
        for (Component component : comp) {
            if (component instanceof Container) setFileChooserFont(((Container) component).getComponents(), font);
            try {
                component.setFont(font);
            } catch (Exception ignored) {

            }//do nothing
        }
    }

    public int[][] loadInputData(String filePath) {
        int row = 0;
        try {
            Scanner scanner = new Scanner(new File(filePath));
            while (scanner.hasNextLine()) {
                row++;
                scanner.nextLine();
            }
            scanner.close();
            int[][] inputData = new int[row][3];

            scanner = new Scanner(new File(filePath));
            int i = 0;
            while (true) {
                if (!scanner.hasNextLine())
                    break;
                scanner.next(); // process
                inputData[i][0] = scanner.nextInt(); // pid
                inputData[i][1] = scanner.nextInt(); // 도착 시간
                inputData[i][2] = scanner.nextInt(); // 실행 시간
                i++;
            }
            return inputData;
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
            return null;
        }
    }

}
