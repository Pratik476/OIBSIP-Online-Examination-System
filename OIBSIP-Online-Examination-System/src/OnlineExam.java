import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class OnlineExam {
    private static Scanner sc = new Scanner(System.in);
    private static Map<String, User> users = new HashMap<>();
    private static List<Question> questions = new ArrayList<>();
    private static AtomicBoolean timeUp = new AtomicBoolean(false);

    public static void main(String[] args) {
        seedData();
        System.out.println("=== Welcome to Console Online Examination System ===");
        System.out.print("Enter User ID: ");
        String uid = sc.nextLine().trim();
        System.out.print("Enter Password: ");
        String pwd = sc.nextLine().trim();

        User user = users.get(uid);
        if (user == null || !user.checkPassword(pwd)) {
            System.out.println("Invalid credentials. Exiting.\n");
            return;
        }

        System.out.println("Login successful. Hi, " + user.getName() + "!\n");

        boolean running = true;
        while (running) {
            System.out.println("1. Start Exam");
            System.out.println("2. Update Profile (Name)");
            System.out.println("3. Change Password");
            System.out.println("4. Logout");
            System.out.print("Choose option: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1": startExam(user); break;
                case "2": updateProfile(user); break;
                case "3": changePassword(user); break;
                case "4": 
                    System.out.println("Logged out. Bye!"); running = false; break;
                default: System.out.println("Invalid option. Try again."); 
            }
        }
    }

    private static void seedData() {
        users.put("stu1", new User("stu1", "Pratik Dhumal", "pass123"));
        users.put("stu2", new User("stu2", "Sanket", "pass234"));

        questions.add(new Question("What is the capital of India?", new String[]{"Mumbai","New Delhi","Kolkata","Chennai"}, 1));
        questions.add(new Question("2 + 3 * 2 = ?", new String[]{"10","7","8","5"}, 1));
        questions.add(new Question("Which language runs on JVM?", new String[]{"Python","C++","Java","JavaScript"}, 2));
        questions.add(new Question("HTML stands for?", new String[]{"Hyper Text Markup Language","High Text Markup Language","Hyperlinks and Text Markup Language","None"}, 0));
        questions.add(new Question("What is 9 % 2 ?", new String[]{"1","0","2","3"}, 0));
    }

    private static void updateProfile(User user) {
        System.out.print("Enter new name: ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) {
            user.setName(name);
            System.out.println("Name updated successfully.\n");
        } else {
            System.out.println("Name cannot be empty.\n");
        }
    }

    private static void changePassword(User user) {
        System.out.print("Enter current password: ");
        String cur = sc.nextLine().trim();
        if (!user.checkPassword(cur)) {
            System.out.println("Incorrect current password.\n"); return;
        }
        System.out.print("Enter new password: ");
        String np = sc.nextLine().trim();
        System.out.print("Confirm new password: ");
        String cp = sc.nextLine().trim();
        if (!np.equals(cp)) {
            System.out.println("Passwords do not match.\n"); return;
        }
        user.setPassword(np);
        System.out.println("Password changed successfully.\n");
    }

    private static void startExam(User user) {
        int durationSeconds = 60; // 1 minute for demo
        timeUp.set(false);
        Thread timer = new Thread(() -> {
            try {
                Thread.sleep(durationSeconds * 1000);
                timeUp.set(true);
                System.out.println("\n\n*** Time is up! Auto-submitting the exam... ***\n");
            } catch (InterruptedException e) {
                // ignore
            }
        });
        timer.setDaemon(true);
        timer.start();

        System.out.println("Exam started. You have " + durationSeconds + " seconds. Answer quickly!\n");
        int[] answers = new int[questions.size()]; // -1 default
        Arrays.fill(answers, -1);

        for (int i = 0; i < questions.size(); i++) {
            if (timeUp.get()) break;
            Question q = questions.get(i);
            System.out.println((i+1) + ". " + q.getQuestion());
            String[] opts = q.getOptions();
            for (int j = 0; j < opts.length; j++) {
                System.out.println("   " + (j+1) + ") " + opts[j]);
            }
            System.out.print("Your answer (1-4) or 0 to skip: ");
            String line = null;
            // Read input but break if timeUp after waiting
            while (true) {
                if (timeUp.get()) break;
                try {
                    if (System.in.available() > 0) {
                        line = sc.nextLine().trim();
                        break;
                    } else {
                        Thread.sleep(100);
                    }
                } catch (Exception e) {
                    break;
                }
            }
            if (timeUp.get()) break;
            try {
                int sel = Integer.parseInt(line);
                if (sel >=1 && sel <= opts.length) answers[i] = sel-1;
            } catch (Exception e) {
                // treat as skip
            }
            System.out.println();
        }

        // calculate score
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            int a = answers[i];
            if (a == questions.get(i).getCorrectIndex()) score++;
        }

        System.out.println("Exam finished. Score: " + score + " / " + questions.size() + "\n");
    }
}
