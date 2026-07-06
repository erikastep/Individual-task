PRAGMA foreign_keys = ON;

-- TODO 1:
-- Select all courses.
SELECT * FROM courses;
-- TODO 2:
-- Select all students.
SELECT * FROM students;
-- TODO 3:
-- Select students older than 20.
SELECT * FROM students WHERE age > 20;
-- TODO 4:
-- Show student names together with course names.
SELECT students.name, courses.course_name
FROM students
JOIN courses ON students.course_id = courses.course_id;
-- TODO 5:
-- Count how many students are in each course.
SELECT course_id, COUNT(*) AS student_count
FROM students
GROUP BY course_id;
-- TODO 6:
-- Update the first students age.
UPDATE students SET age = 21 WHERE id = 1;
-- Check the update.
SELECT * FROM students WHERE id = 1;
-- TODO 7:
-- Move second student to a different course.
UPDATE students SET course_id = 2 WHERE id = 2;
-- Check the update using JOIN.
SELECT students.name, courses.course_name
FROM students
JOIN courses ON students.course_id = courses.course_id;
-- TODO 8:
-- Delete one student.
DELETE FROM students WHERE id = 5;
-- TODO 9:
-- Check the final result.
SELECT * FROM students;