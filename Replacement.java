import java.util.*;
public class Replacement{
     static void printframes(List<Integer> frames){
        for(int f : frames){
            System.out.print(f+" ");
        }
        System.out.println();
     }

    static void FIFO(int[] pages, int capacity){
        System.out.println("\n FIFO Page Replacement");
        Queue<Integer> frames = new LinkedList<>();
        int pageFaults =0; 

        for(int i=0;i<pages.length;i++){
            int page =pages[i];
            if(!frames.contains(page)){
                if(frames.size() == capacity){
                    frames.poll();
                }
                frames.add(page);
                pageFaults++;
                System.out.print("page fault occured");
            }
            System.out.print("After Inserting "+page+"==>");
            printframes(new ArrayList<>(frames));
            
            
        }
        
            
         System.out.println("Total page Faults"+pageFaults);
        }
        

    

    static void LRU(int[] pages, int capacity){
        System.out.println("\n LRU Page Replacement");
        List<Integer> frames = new ArrayList<>();
        Map<Integer,Integer> recent = new HashMap<>();
        int pageFaults =0; 
        for(int i=0;i<pages.length;i++){
            int page =pages[i];
            if(!frames.contains(page)){
                if(frames.size() < capacity){
                    frames.add(page);
                }else{
                    int lru =frames.get(0);
                    for(int f : frames){
                        if(recent.get(f) < recent.get(lru)){
                        lru =f;
                        }  
                    }
                    frames.set(frames.indexOf(lru),page);
                    
                }
                pageFaults++;
                System.out.print("Page Fault Occured ");
                
            }
            recent.put(page,i);
            System.out.print("Ater Inserting"+page+"==>");
            printframes(frames);
        }
        System.out.println("Total Page Faults (LRU): " + pageFaults);

    }

    static void Optimal(int[] pages, int capacity){
        List<Integer> frames = new ArrayList<>();
        int pageFaults =0;
        System.out.println("\n Optimal Page Replacement");

        for(int i=0;i<pages.length;i++){
            int page =pages[i];
            if(!frames.contains(page)){
                if(frames.size() < capacity){
                    frames.add(page);
                }else{
                    int fartest =i+1; int replaceindex=-1;
                    for(int j=0;j<frames.size();j++){
                        int framepage = frames.get(j);
                        int k;
                        for(k=i+1;k<pages.length;k++)
                            if(framepage == pages[k])
                                break;
                        if(k == pages.length){
                            replaceindex =j;
                            break;
                         }
                        if(k>fartest){
                            fartest =k;
                            replaceindex =j;
                         }
                    }
                    frames.set(replaceindex,page);
                }
                pageFaults++;
                System.out.print("Page Fault Occured ");
            }
            System.out.print("After Inserting "+page+"==>");
            printframes(frames);
        }
        System.out.println("Total Page Faults (Optimal): " + pageFaults);
        System.out.printf("Page Fault Rate = %.2f%%\n", (pageFaults * 100.0 / pages.length));

    }
    
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of pages:");
        int n = sc.nextInt();
        int[] pages = new int[n];
        System.out.print("Enter the page reference string:\n");
        for(int i=0;i<n;i++){
            pages[i] =sc.nextInt();

        }
        System.out.print("Enter the frame size");
        int capacity = sc.nextInt();

        FIFO(pages,capacity);
        LRU(pages,capacity);
        Optimal(pages,capacity);    
        sc.close();
    }
}