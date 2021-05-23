package scheduler;

public class Process implements Comparable<Process> {
    private int pid;
    private int arrivalTime;
    private int burstTime;
    private int remainTime;
    private int completedTime;
    private boolean completed;

    public Process(int pid, int arrivalTime, int burstTime, int remainTime, int completedTime, boolean completed) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainTime = remainTime;
        this.completedTime = completedTime;
        this.completed = completed;
    }

    public int getPid() {
        return pid;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(int remainTime) {
        this.remainTime = remainTime;
    }

    public int getCompletedTime() {
        return completedTime;
    }

    public void setCompletedTime(int completedTime) {
        this.completedTime = completedTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public int compareTo(Process o) {
        if (this.remainTime > o.remainTime)
            return 1;
        else if (this.remainTime < o.remainTime)
            return -1;
        else
            return 0;
    }
}
