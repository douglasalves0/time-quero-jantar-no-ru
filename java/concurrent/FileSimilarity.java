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
                float similarityScore = similarity(fingerprint1, fingerprint2);
                System.out.println("Similarity between " + file1 + " and " + file2 + ": " + (similarityScore * 100) + "%");
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

    private static float similarity(List<Long> base, List<Long> target) {
        int counter = 0;
        List<Long> targetCopy = new ArrayList<>(target);

        for (Long value : base) {
            if (targetCopy.contains(value)) {
                counter++;
                targetCopy.remove(value);
            }
        }

        return (float) counter / base.size();
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

}
