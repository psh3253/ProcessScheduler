package scheduler;

import java.util.*;

public class SchedulingService {

    private static SchedulingService instance = null;

    public static SchedulingService getInstance() {
        if (instance == null)
            instance = new SchedulingService();
        return instance;
    }

    public HashMap<Integer, int[][]> FCFSScheduling(int[][] inputData) {
        HashMap<Integer, int[][]> result = new HashMap<>();
        ArrayList<Process> processes = new ArrayList<>();
        ArrayList<int[]> ganttChart = new ArrayList<>();
        for (int i = 0; i < inputData.length; i++)
            processes.add(new Process(inputData[i][0], inputData[i][1], inputData[i][2], inputData[i][2], 0, false));
        Collections.sort(processes, new Comparator<Process>() {
            @Override
            public int compare(Process o1, Process o2) {
                return o1.getArrivalTime() - o2.getArrivalTime();
            }
        });
        int currentTime = 0;
        for (int i = 0; i < processes.size(); i++) {
            for (int j = 0; j < processes.size(); j++) {
                if (!processes.get(j).isCompleted()) {
                    currentTime += processes.get(j).getRemainTime();
                    ganttChart.add(new int[]{processes.get(j).getPid(), processes.get(j).getBurstTime()});
                    processes.get(j).setCompletedTime(currentTime);
                    processes.get(j).setCompleted(true);
                    processes.get(j).setRemainTime(0);
                    break;
                }
            }
        }

        int[][] outputData = new int[inputData.length][3];
        for (int i = 0; i < inputData.length; i++) {
            outputData[i] = new int[]{processes.get(i).getPid(), processes.get(i).getBurstTime(), processes.get(i).getCompletedTime() - processes.get(i).getArrivalTime() - processes.get(i).getBurstTime()};
        }
        Arrays.sort(outputData, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[2] - o2[2];
            }
        });

        int[][] ganttChartData = new int[ganttChart.size()][2];
        for (int i = 0; i < ganttChart.size(); i++) {
            ganttChartData[i] = ganttChart.get(i);
        }
        result.put(0, outputData);
        result.put(1, ganttChartData);
        return result;
    }


    public HashMap<Integer, int[][]> NSJFScheduling(int[][] inputData) {
        HashMap<Integer, int[][]> result = new HashMap<>();
        ArrayList<Process> processes = new ArrayList<>();
        ArrayList<int[]> ganttChart = new ArrayList<>();
        for (int i = 0; i < inputData.length; i++)
            processes.add(new Process(inputData[i][0], inputData[i][1], inputData[i][2], inputData[i][2], 0, false));
        Collections.sort(processes);
        int currentTime = 0;
        for (int i = 0; i < processes.size(); i++) {
            for (int j = 0; j < processes.size(); j++) {
                if (!processes.get(j).isCompleted() && processes.get(j).getArrivalTime() <= currentTime) {
                    currentTime += processes.get(j).getRemainTime();
                    ganttChart.add(new int[]{processes.get(j).getPid(), processes.get(j).getBurstTime()});
                    processes.get(j).setCompletedTime(currentTime);
                    processes.get(j).setCompleted(true);
                    processes.get(j).setRemainTime(0);
                    break;
                }
            }
        }

        int[][] outputData = new int[inputData.length][3];
        for (int i = 0; i < inputData.length; i++) {
            outputData[i] = new int[]{processes.get(i).getPid(), processes.get(i).getBurstTime(), processes.get(i).getCompletedTime() - processes.get(i).getArrivalTime() - processes.get(i).getBurstTime()};
        }
        Arrays.sort(outputData, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[2] - o2[2];
            }
        });

        int[][] ganttChartData = new int[ganttChart.size()][2];
        for (int i = 0; i < ganttChart.size(); i++) {
            ganttChartData[i] = ganttChart.get(i);
        }
        result.put(0, outputData);
        result.put(1, ganttChartData);
        return result;
    }

    public HashMap<Integer, int[][]> PSJFScheduling(int[][] inputData) {
        HashMap<Integer, int[][]> result = new HashMap<>();
        ArrayList<Process> processes = new ArrayList<>();
        ArrayList<int[]> ganttChart = new ArrayList<>();
        for (int i = 0; i < inputData.length; i++)
            processes.add(new Process(inputData[i][0], inputData[i][1], inputData[i][2], inputData[i][2], 0, false));
        int currentTime = 0;
        int runningTime = 0;
        Process selectedProcess = null;
        while (!isAllComplete(processes)) {
            if (selectedProcess == null) {
                Collections.sort(processes);
                for (int i = 0; i < processes.size(); i++) {
                    if (processes.get(i).getArrivalTime() <= currentTime && !processes.get(i).isCompleted()) {
                        selectedProcess = processes.get(i);
                        runningTime = 0;
                        break;
                    }
                }
            } else {
                Collections.sort(processes);
                for (int i = 0; i < processes.size(); i++) {
                    if (!processes.get(i).isCompleted() && !selectedProcess.equals(processes.get(i)) && processes.get(i).getArrivalTime() == currentTime && processes.get(i).getRemainTime() < selectedProcess.getRemainTime()) {
                        ganttChart.add(new int[]{selectedProcess.getPid(), runningTime});
                        selectedProcess = processes.get(i);
                        runningTime = 0;
                        break;
                    }
                }
            }
            selectedProcess.setRemainTime(selectedProcess.getRemainTime() - 1);
            runningTime++;
            currentTime++;
            if (selectedProcess.getRemainTime() == 0) {
                selectedProcess.setCompleted(true);
                selectedProcess.setCompletedTime(currentTime);
                ganttChart.add(new int[]{selectedProcess.getPid(), runningTime});
                selectedProcess = null;
            }
        }

        int[][] outputData = new int[inputData.length][3];
        for (int i = 0; i < inputData.length; i++) {
            outputData[i] = new int[]{processes.get(i).getPid(), processes.get(i).getBurstTime(), processes.get(i).getCompletedTime() - processes.get(i).getArrivalTime() - processes.get(i).getBurstTime()};
        }
        Arrays.sort(outputData, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[2] - o2[2];
            }
        });

        int[][] ganttChartData = new int[ganttChart.size()][2];
        for (int i = 0; i < ganttChart.size(); i++) {
            ganttChartData[i] = ganttChart.get(i);
        }
        result.put(0, outputData);
        result.put(1, ganttChartData);
        return result;
    }

    public boolean isAllComplete(ArrayList<Process> processes) {
        for (int i = 0; i < processes.size(); i++) {
            if (!processes.get(i).isCompleted()) {
                return false;
            }
        }
        return true;
    }

    public HashMap<Integer, int[][]> RRScheduling(int[][] inputData, int timeQuantum) {
        HashMap<Integer, int[][]> result = new HashMap<>();
        ArrayList<Process> processes = new ArrayList<>();
        Queue<Process> readyQueue = new LinkedList<>();
        ArrayList<int[]> ganttChart = new ArrayList<>();
        for (int i = 0; i < inputData.length; i++)
            processes.add(new Process(inputData[i][0], inputData[i][1], inputData[i][2], inputData[i][2], 0, false));
        int currentTime = 0;
        int runningTime = 0;
        Process selectedProcess = null;
        while (!isAllComplete(processes)) {
            for (int i = 0; i < processes.size(); i++) {
                if (processes.get(i).getArrivalTime() == currentTime) {
                    readyQueue.add(processes.get(i));
                }
            }
            if (selectedProcess == null) {
                if (!readyQueue.isEmpty()) {
                    selectedProcess = readyQueue.remove();
                    runningTime = 0;
                }
            } else {
                if (runningTime == timeQuantum) {
                    ganttChart.add(new int[]{selectedProcess.getPid(), runningTime});
                    if (!selectedProcess.isCompleted()) {
                        readyQueue.add(selectedProcess);
                    }
                    if (!readyQueue.isEmpty()) {
                        selectedProcess = readyQueue.remove();
                        runningTime = 0;
                    }
                }
            }
            selectedProcess.setRemainTime(selectedProcess.getRemainTime() - 1);
            runningTime++;
            currentTime++;
            if (selectedProcess.getRemainTime() == 0) {
                selectedProcess.setCompleted(true);
                selectedProcess.setCompletedTime(currentTime);
                ganttChart.add(new int[]{selectedProcess.getPid(), runningTime});
                selectedProcess = null;
            }
        }

        int[][] outputData = new int[inputData.length][3];
        for (int i = 0; i < inputData.length; i++) {
            outputData[i] = new int[]{processes.get(i).getPid(), processes.get(i).getBurstTime(), processes.get(i).getCompletedTime() - processes.get(i).getArrivalTime() - processes.get(i).getBurstTime()};
        }
        Arrays.sort(outputData, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                return o1[2] - o2[2];
            }
        });

        int[][] ganttChartData = new int[ganttChart.size()][2];
        for (int i = 0; i < ganttChart.size(); i++) {
            ganttChartData[i] = ganttChart.get(i);
        }
        result.put(0, outputData);
        result.put(1, ganttChartData);
        return result;
    }
}
