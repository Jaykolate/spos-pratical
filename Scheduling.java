import java.util.*;

class process{
    int pid, arrival,  brust,  remaining ,  waitingTime,turnaroundtime,priority;
    process(int pid, int arrival,int brust,int priority){
        this.pid =pid;
        this.arrival= arrival;
        this.brust=brust;
        this.remaining=brust;
        this.priority = priority;
    }

}


public class Scheduling {
    static Scanner sc  = new Scanner(System.in);
    public static void main(String[] args){
        System.out.println("\n1. FCFS Scheduling");
        fcfs();
        System.out.println("\n2. SJF Scheduling");
        sjf();
        System.out.println("\n3. priority Scheduling");
        priority();
        System.out.println("/4. Round Robin Scheduling");
        RR();
       

    }

    static void fcfs(){
        System.out.print("Enter number of process:");
        int n =sc.nextInt();
        process[]p =new process[n];

        for(int i=0;i<n;i++){
            System.out.print("Brust time for P"+(i+1)+":");
            int bt =sc.nextInt();
            p[i] =new process(i+1,0,bt,0);
        }
        
        int currentime =0;
       System.out.println("\n Gantt chart");
       for(process pr : p){
        System.out.print("|P"+pr.pid+" ");
        pr.waitingTime =currentime;
        currentime+=pr.brust;
       }
       System.out.println("|");
       currentime =0;
       for(process pr : p){
        System.out.print(currentime+"\t");
        currentime +=pr.brust;
       }
       System.out.println(currentime+"\n");

        printResults(p,"FCFS Scheduling");
    }
    static void sjf(){
        System.out.print("Enter number of process:");
        int n =sc.nextInt();
        process[]p =new process[n];

        for(int i=0;i<n;i++){
            System.out.print("Enetr arrival time for P"+(i+1)+":");
            int at  = sc.nextInt();
            System.out.print("Brust time for P"+(i+1)+":");
            int bt =sc.nextInt();
            p[i] = new process(i+1,at,bt,0);
        }

        int currentime =0; int completed =0;
        boolean[] done =new boolean[n];
        List<Integer> gantt =new ArrayList<>();

        while(completed != n){
            int idx =-1;
            int min = Integer.MAX_VALUE;
            for(int i=0;i<n;i++){
                if(p[i].arrival <= currentime && !done[i] && p[i].remaining<min){
                    if(p[i].remaining < min){
                        min =p[i].remaining;
                        idx =i;
                    }
                }
            }
            if(idx!=-1){
                gantt.add(p[idx].pid);
                p[idx].remaining--;
                currentime++;
                if(p[idx].remaining ==0){
                    done[idx] =true;
                    completed++;
                    p[idx].waitingTime = currentime -p[idx].arrival -p[idx].brust;
                }

            }else{
                gantt.add(-1);
                currentime++;
            }
        }

        System.out.println("\n Gantt chart");
        for(int i =0; i<gantt.size();i++){
            if(gantt.get(i) ==-1){
                System.out.print("| idle");
            }else{
                System.out.print("| P"+gantt.get(i)+" ");
            }
        }
            System.out.println("|");
        
        for(int i=0;i<gantt.size();i++){
            System.out.print(i+"\t");
        }
        System.out.println();

        printResults(p,"SJF Scheduli g");
    }

    static void priority(){
        System.out.print("Enter number of process:");
        int n =sc.nextInt();
        process[] p =new process[n];
        List<Integer> gantt = new ArrayList<>();

        for(int i=0;i<n;i++){
            System.out.print("Enetr arrival time for P"+(i+1)+":");
            int at  = sc.nextInt();
            System.out.print("Brust time for P"+(i+1)+":");
            int bt =sc.nextInt();
            System.out.print("Priority for P"+(i+1)+":");
            int pr =sc.nextInt();
            p[i] =new process(i+1,at,bt,pr);
        }

        int currentime = 0; int completed =0;
        boolean[] done =new boolean[n];

        while(completed<n){
            int idx =-1; int high = Integer.MAX_VALUE;
            for(int i=0;i<n;i++){
                if(!done[i]&& p[i].arrival<=currentime){
                    if(p[i].priority <high){
                        high =p[i].priority;
                        idx =i;
                    }
                }
            }
            if(idx!=-1){
                p[idx].waitingTime = currentime-p[idx].arrival;
                for (int t = 0; t < p[idx].brust; t++) {
                     gantt.add(p[idx].pid);
                }
                currentime+= p[idx].brust;
                done[idx] =true;
                completed++;
            }else{
                gantt.add(-1);
                currentime++;
            }
        }

        System.out.println("\n Gantt chart");
        for(int i=0;i<gantt.size();i++){
            if(gantt.get(i) ==-1){
                System.out.print("| idel");

            }else{
                System.out.print("|P"+gantt.get(i)+" ");
            }
        }
        System.out.println("|");
        for(int i=0;i<gantt.size();i++){
            System.out.print(i +"\t");
        }
        System.out.println();
        printResults(p,"priprity Scheduling");
    }
   

    static void RR(){
        System.out.print("Enter number of process:");
        int n =sc.nextInt();
        process[] p =new process[n];
        Queue<process> queue =new LinkedList<>();

        for(int i=0;i<n;i++){
            System.out.print("Enetr arrival time for P"+(i+1)+":");
            int at  = sc.nextInt();
            System.out.print("Brust time for P"+(i+1)+":");
            int bt =sc.nextInt();
            p[i] =new process(i+1,at,bt,0);
        }

        System.out.print("Enter time quantum:");
        int tq =sc.nextInt();

        int currentime =0; int completed =0;
        boolean[] inqueue =new boolean[n];
        List<Integer> gantt =new ArrayList<>();

        while(completed <n){
            for(int i=0;i<n;i++){
                if(p[i].arrival <= currentime && !inqueue[i] && p[i].remaining >0){
                    queue.add(p[i]);
                    inqueue[i] =true;
                }
            }
            if(queue.isEmpty()){
                gantt.add(-1);
                currentime++;
            }else{
                process curr = queue.poll();
                int exec = Math.min(tq, curr.remaining);
                for(int t=0;t<exec;t++){
                    gantt.add(curr.pid);
                }
                currentime +=exec;
                curr.remaining -=exec;
                if(curr.remaining ==0){
                    completed++;
                    curr.waitingTime = currentime -curr.arrival -curr.brust;
                }else{
                    for(int i=0;i<n;i++){
                        if(p[i].arrival <= currentime && !inqueue[i] && p[i].remaining >0){
                            queue.add(p[i]);
                            inqueue[i] =true;
                        }
                    }
                    queue.add(curr);
                }
            }
        }

        System.out.println("\n Gantt chart");
        for(int i=0;i<gantt.size();i++){
            if(gantt.get(i) ==-1){
                System.out.print("| idle");
            }else{
                System.out.print("| P"+gantt.get(i)+" ");
            }
        }
        System.out.println("|");
        for(int i=0;i<gantt.size();i++){
            System.out.print(i +"\t");
        }
        System.out.println();

        printResults(p,"Round Robin");
    }

    

    
    static void printResults(process[]p, String title){
        int total =0;
        for(int i=0;i<p.length;i++){
            System.out.println("P"+p[i].pid+" waiting time:"+p[i].waitingTime);
            total +=p[i].waitingTime;
        }
        System.out.println("Average waiting time for  "+title+":"+(float)total/p.length);
       
    }
}
