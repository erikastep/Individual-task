public class StudentMarkTracker {

    private static final int EXCELLENT_THRESHOLD = 9;
    private static final int PASS_THRESHOLD = 5;

    //students and their marks
    private static final String[] STUDENT_NAMES = {"Kotryna", "Rokas", "Raminta", "Domantas"};

    private static final int[][] STUDENT_MARKS = {
        {8, 7, 9, 6},    // Kotryna
        {5, 4, 7, 3},    // Rokas
        {10, 9, 8, 10},  // Raminta
        {7, 6, 5, 8}     // Domantas
    };

    public static void main(String[] args) {
        for (int i = 0; i < STUDENT_MARKS.length; i++) {
            double average = computeAverage(STUDENT_MARKS[i]);
            String result = classifyResult(average);
            System.out.println(STUDENT_NAMES[i] + " | Average: " + average + " | Result: " + result);
        }
    }

    // counting the average of students' marks
    private static double computeAverage(int[] marks) {
        int sum = 0;
        for (int mark : marks) {
            sum += mark;
        }
        return (double) sum / marks.length;
    }

    // result of students' grades
    private static String classifyResult(double average) {
        if (average >= EXCELLENT_THRESHOLD) {
            return "Excellent";
        } else if (average >= PASS_THRESHOLD) {
            return "Pass";
        } else {
            return "Space for improvement";
        }
    }
}