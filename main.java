import java.util.*;

enum State {
    READY, RUNNING, PAUSED, TERMINATED
}

class Process {
    private int pid;
    private String name;
    private int priority;
    private int burstTime;
    private int remainingTime;
    private State state;

    public Process(int pid, String name, int priority, int burstTime) {
        this.pid = pid;
        this.name = name;
        this.priority = priority;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.state = State.READY;
    }

    public int getPid() { return pid; }
    public String getPriorityName() { return name; }
    public int getPriority() { return priority; }
    public int getRemainingTime() { return remainingTime; }
    public State getState() { return state; }

    public void setState(State state) {
        this.state = state;
    }

    public void execute(int time) {
        remainingTime -= time;

        if (remainingTime <= 0) {
            remainingTime = 0;
            state = State.TERMINATED;
        } else {
            state = State.READY;
        }
    }

    public void pause() {
        if (state == State.READY)
            state = State.PAUSED;
    }

    public void resume() {
        if (state == State.PAUSED)
            state = State.READY;
    }

    public void terminate() {
        state = State.TERMINATED;
        remainingTime = 0;
    }

    public void display() {
        System.out.printf("%-6d %-15s %-10s %-10d %-10d%n",
                pid, name, state, priority, remainingTime);
    }
}

interface Scheduler {
    void schedule(List<Process> processes);
}

class FCFS implements Scheduler {
    public void schedule(List<Process> processes) {
        System.out.println("\n--- FCFS Scheduling ---");

        for (Process p : processes) {
            if (p.getState() == State.READY) {
                System.out.println("Running: " + p.getPriorityName());
                p.setState(State.RUNNING);
                p.execute(p.getRemainingTime());
            }
        }
    }
}

class PriorityScheduler implements Scheduler {
    public void schedule(List<Process> processes) {
        System.out.println("\n--- Priority Scheduling ---");

        List<Process> ready = new ArrayList<>();

        for (Process p : processes)
            if (p.getState() == State.READY)
                ready.add(p);

        ready.sort(Comparator.comparingInt(Process::getPriority));

        for (Process p : ready) {
            System.out.println("Running: " + p.getPriorityName());
            p.setState(State.RUNNING);
            p.execute(p.getRemainingTime());
        }
    }
}

class RoundRobin implements Scheduler {
    private int quantum;

    public RoundRobin(int quantum) {
        this.quantum = quantum;
    }

    public void schedule(List<Process> processes) {
        System.out.println("\n--- Round Robin Scheduling ---");

        boolean work = true;

        while (work) {
            work = false;

            for (Process p : processes) {
                if (p.getState() == State.READY) {
                    work = true;

                    int time = Math.min(
                            quantum,
                            p.getRemainingTime()
                    );

                    System.out.println(
                            "Running: " + p.getPriorityName()
                            + " (" + time + " units)"
                    );

                    p.setState(State.RUNNING);
                    p.execute(time);
                }
            }
        }
    }
}

class ProcessManager {
    private List<Process> processes = new ArrayList<>();
    private int nextPid = 101;

    public void create(String name, int priority, int burst) {
        Process p = new Process(
                nextPid++, name, priority, burst
        );

        processes.add(p);

        System.out.println(
                "Process created! PID: " + p.getPid()
        );
    }

    public Process find(int pid) {
        for (Process p : processes)
            if (p.getPid() == pid)
                return p;
                return null;
}