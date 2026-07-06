PRAGMA foreign_keys = ON;

-- Delete students first because students depends on courses.
DELETE FROM students;
DELETE FROM courses;

-- TODO 1:
-- Insert 3 courses.
-- Remember:
-- course_id is a number.
-- course_name must be present.
-- credits must be greater than 0.

INSERT INTO courses (course_id, course_name, credits) VALUES
    (1, 'Computer Design', 6),
    (2, 'Philosophy', 3),
    (3, 'Game Design', 6);

-- TODO 2:
-- Insert 5 students.
-- Remember:
-- id is a number.
-- name must be present.
-- email must be unique.
-- age must be 18 or older.
-- course_id must exist in the courses table.

INSERT INTO students (id, name, email, age, course_id) VALUES
    (1, 'Elena',   'elena@email.com',   18, 1),
    (2, 'Damon',    'damon@email.com',    23, 1),
    (3, 'Stefan',  'stefan@email.com',  20, 2),
    (4, 'Caroline',  'caroline@email.com',  19, 3),
    (5, 'Claus',   'claus@email.com',   25, 3);