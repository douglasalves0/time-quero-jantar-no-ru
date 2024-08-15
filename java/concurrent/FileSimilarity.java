import java.io.*;
import java.util.*;

public class FileSimilarity {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: java Sum filepath1 filepath2 filepathN");
            System.exit(1);
        }

        // Create a map to store the fingerprint for each file
        Map<String, List<Long>> fileFingerprints = new HashMap<>();

        // Calculate the fingerprint for each file
        List<Thread> threads = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        for (String path : args) {
            Task t = new Task(path);
            Thread t2 = new Thread(t, path);
            tasks.add(t);
            threads.add(t2);
            t2.start();
        }
        
        for(int i=0;i<tasks.size();i++){
            threads.get(i).join();
            fileFingerprints.put(tasks.get(i).filePath, tasks.get(i).getAns());
        }

        // Compare each pair of files
        threads.clear();
        threads = new ArrayList<>();
        List<Task2> tasks2 = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            for (int j = i + 1; j < args.length; j++) {
                String file1 = args[i];
                String file2 = args[j];
                List<Long> fingerprint1 = fileFingerprints.get(file1);
                List<Long> fingerprint2 = fileFingerprints.get(file2);
                Task2 t = new Task2(fingerprint1, fingerprint2);
                Thread t2 = new Thread(t, "quero-jantar-no-ru");
                threads.add(t2);
                tasks2.add(t);
                t2.start();
            }
        }

        int k = 0;
        for (int i = 0; i < args.length; i++) {
            for (int j = i + 1; j < args.length; j++) {
                threads.get(k).join();
                float similarityScore = tasks2.get(k).getAns();
                System.out.println("Similarity between " + args[i] + " and " + args[j] + ": " + (similarityScore * 100) + "%");
                k++;
            }
        }
        
    }

    private static long sum(byte[] buffer, int length) {
        long sum = 0;
        for (int i = 0; i < length; i++) {
            sum += Byte.toUnsignedInt(buffer[i]);
        }
        return sum;
    }

    public static class Task implements Runnable {
        private final String filePath;
        private List<Long> ans;
        public Task(String filePath) {
            this.filePath = filePath;
        }
        @Override
        public void run() {
            try{
                File file = new File(filePath);
                List<Long> chunks = new ArrayList<>();
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    byte[] buffer = new byte[100];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        long sum = sum(buffer, bytesRead);
                        chunks.add(sum);
                    }
                }
                this.ans = chunks;
            }catch(Exception e){}
        }
        public List<Long> getAns() {
            return ans;
        }
    }

    public static class Task2 implements Runnable {
        private final List<Long> base;
        private final List<Long> target;
        private float ans;
        public Task2(List<Long> base, List<Long> target) {
            this.base = base;
            this.target = target;
        }
        @Override
        public void run() {
            try{
                int counter = 0;
                List<Long> targetCopy = new ArrayList<>(target);
                Collections.sort(targetCopy);
                Collections.sort(base);
                
                int l = 0,l1 = 0;
                
                while(l < targetCopy.size() && l1 < base.size()) {
                    if(targetCopy.get(l) == base.get) {
                        counter++;
                        l1++,l2++;
                    }
                }

                for (Long value : base) {
                    if (targetCopy.contains(value)) {
                        counter++;
                        targetCopy.remove(value);
                    }
                }
                this.ans = (float) counter / base.size();
            }catch(Exception e){}
        }
        public float getAns() {
            return ans;
        }
    }

}
