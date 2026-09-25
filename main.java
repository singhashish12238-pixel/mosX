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

    public void showProcesses() {
        System.out.println("\n--------------- PROCESSES ---------------");

        System.out.printf(
                "%-6s %-15s %-10s %-10s %-10s%n",
                "PID", "Name", "State",
                "Priority", "Remaining"
        );

        for (Process p : processes)
            p.display();
    }

    public void pause(int pid) {
        Process p = find(pid);

        if (p != null) {
            p.pause();
            System.out.println("Process paused.");
        } else {
            System.out.println("Process not found.");
        }
    }

    public void resume(int pid) {
        Process p = find(pid);

        if (p != null) {
            p.resume();
            System.out.println("Process resumed.");
        } else {
            System.out.println("Process not found.");
        }
    }

    public void terminate(int pid) {
        Process p = find(pid);

        if (p != null) {
            p.terminate();
            System.out.println("Process terminated.");
        } else {
            System.out.println("Process not found.");
        }
    }

    public List<Process> getProcesses() {
        return processes;
    }
}

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        ProcessManager manager = new ProcessManager();

        int choice;

        do {
            System.out.println("\n================================");
            System.out.println("      OS PROCESS MANAGER");
            System.out.println("================================");
            System.out.println("1. Create Process");
            System.out.println("2. Show Processes");
            System.out.println("3. Run Scheduler");
            System.out.println("4. Pause Process");
            System.out.println("5. Resume Process");
            System.out.println("6. Terminate Process");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");

            choice = sc.nextInt();

            switch (choice) {

                case 1:
                    System.out.print("Process Name: ");
                    String name = sc.next();

                    System.out.print("Priority: ");
                    int priority = sc.nextInt();

                    System.out.print("Burst Time: ");
                    int burst = sc.nextInt();

                    manager.create(name, priority, burst);
                    break;

                case 2:
                    manager.showProcesses();
                    break;

                case 3:
                    System.out.println("\n1. FCFS");
                    System.out.println("2. Priority");
                    System.out.println("3. Round Robin");
                    System.out.print("Choose: ");

                    int type = sc.nextInt();
                    Scheduler scheduler = null;

                    if (type == 1)
                        scheduler = new FCFS();

                    else if (type == 2)
                        scheduler = new PriorityScheduler();

                    else if (type == 3) {
                        System.out.print("Time Quantum: ");
                        int q = sc.nextInt();
                        scheduler = new RoundRobin(q);
                    }

                    if (scheduler != null)
                        scheduler.schedule(manager.getProcesses());

                    break;

                case 4:
                    System.out.print("PID: ");
                    manager.pause(sc.nextInt());
                    break;

                case 5:
                    System.out.print("PID: ");
                    manager.resume(sc.nextInt());
                    break;

                case 6:
                    System.out.print("PID: ");
                    manager.terminate(sc.nextInt());
                    break;

                case 7:
                    System.out.println("Program ended.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 7);

        sc.close();
    }
}