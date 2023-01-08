import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Random;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.time.LocalDateTime;

public class Ex2_1 {


    public static String[] createTextFiles(int n, int seed, int bound) {
        String[] files = new String[n];
        Random rand = new Random(seed);
        for (int i = 0; i < n; i++) {
            try {
                FileWriter newFile = new FileWriter("file_" + (i + 1));
                int x = rand.nextInt(bound);
                while (x == 0) {
                    x = rand.nextInt(bound);
                }

                for (int j = 0; j < x; j++) {
                    if (j < x - 1) {
                        newFile.write("Welcome to Petah Tikva!\n");
                    } else {
                        newFile.write("Welcome to Petah Tikva!");
                    }
                }
                files[i] = "file_" + (i + 1);
                newFile.close();
            } catch (IOException e) {
                System.err.println("Error while trying create a file");
            }
        }
        return files;
    }


    public static int getNumOfLines(String[] fileNames) {
        int sum = 0;
        for (int i = 0; i < fileNames.length; i++) {
            try {
                FileInputStream fis = new FileInputStream(fileNames[i]);
                Scanner scan = new Scanner(fis);
                while (scan.hasNextLine()) {
                    scan.nextLine();
                    sum++;
                }
                scan.close();
                fis.close();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
        return sum;
    }

    public static class myThread extends Thread {
        int linecounter;
        String fileName;

        public myThread(String name) {
            this.fileName = name;
        }

        public void run() {
            int sum = 0;
            try {
                FileInputStream fis = new FileInputStream(fileName);
                Scanner scan = new Scanner(fis);
                while (scan.hasNextLine()) {
                    scan.nextLine();
                    sum++;
                }
                scan.close();
                fis.close();
            } catch (Exception e) {
                e.getStackTrace();
            }
            linecounter = sum;
        }

    }
    public static int getNumOfLinesThreads(String[] fileNames) {
        int sum = 0;

        myThread[] Threads = new myThread[fileNames.length];
        for(int i = 0 ; i < Threads.length ; i++){
            Threads[i] = new myThread(fileNames[i]);
            Threads[i].start();
        }
        for(myThread thread : Threads){

            try {
                thread.join();
                sum += thread.linecounter;
            }catch (Exception e){
                e.getStackTrace();
            }
        }
    return sum;
    }

    public static class myThreadCall implements Callable<Integer>{

        String fileName;

        public myThreadCall(String name){
            this.fileName = name;
        }

        public Integer call(){

            int sum = 0;
            try {
                FileInputStream fis = new FileInputStream(fileName);
                Scanner scan = new Scanner(fis);
                while (scan.hasNextLine()) {
                    scan.nextLine();
                    sum++;
                }
                scan.close();
                fis.close();
            } catch (Exception e) {
                e.getStackTrace();
            }
            return sum;
        }
    }

    public static int getNumOfLinesThreadPool(String[] fileNames){
        int lines=0;
        Future<Integer>[] threadResults = new Future[fileNames.length];
        ExecutorService threadPool = Executors.newFixedThreadPool(fileNames.length);
        for(int i = 0; i < fileNames.length ; i++){
           threadResults[i] = threadPool.submit(new myThreadCall(fileNames[i]));
        }
        for(Future<Integer> result : threadResults){
            try {
                lines += result.get();
            }
            catch (Exception e){
                e.getStackTrace();
            }
        }
        threadPool.shutdown();
        return lines;
    }




    public static void main(String[] args) {
        String[] files = createTextFiles(4000, 1, 10);
        LocalDateTime start;
        LocalDateTime end;

        start = LocalDateTime.now();
        System.out.println(getNumOfLines(files));
        end = LocalDateTime.now();
        System.out.println("getNumOfLines time: " + Duration.between(start, end).toMillis()+" milliseconds");
        start = LocalDateTime.now();
        System.out.println(getNumOfLinesThreads(files));
        end = LocalDateTime.now();
        System.out.println("getNumOfLinesThreads time: " + Duration.between(start, end).toMillis()+" milliseconds");
        start = LocalDateTime.now();
        System.out.println(getNumOfLinesThreadPool(files));
        end = LocalDateTime.now();
        System.out.println("getNumOfLinesThreadPool time: " + Duration.between(start, end).toMillis()+" milliseconds");
    }
}