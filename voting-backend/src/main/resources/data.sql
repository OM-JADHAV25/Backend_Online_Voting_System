-- ================================
-- DEMO USERS (VOTER001 – VOTER010)
-- ================================

INSERT INTO users
(name, email, date_of_birth, phone_number, password, role, constituency, registration_date, voter_id, otp, otp_expires, last_voted, status)
VALUES
('Aarav Sharma', 'aarav.sharma@gmail.com', '1992-04-12', '+91 9876500001', NULL, 'User', 'Mumbai', '2020-03-12 00:00:00', 'VOTER001', NULL, NULL, NULL, 'Verified'),

('Ananya Patel', 'ananya.patel@gmail.com', '1995-09-18', '+91 9123456789', NULL, 'User', 'Mumbai', '2021-06-18 00:00:00', 'VOTER002', NULL, NULL, NULL, 'Verified'),

('Rohan Mehta', 'rohan.mehta@gmail.com', '1990-02-25', '+91 9988776655', NULL, 'User', 'Mumbai', '2020-11-05 00:00:00', 'VOTER003', NULL, NULL, NULL, 'Verified'),

('Sanya Rao', 'sanya.rao@gmail.com', '1993-06-30', '+91 9765432101', NULL, 'User', 'Mumbai', '2022-01-12 00:00:00', 'VOTER004', NULL, NULL, NULL, 'Approved'),

('Vivaan Singh', 'vivaan.singh@gmail.com', '1989-12-15', '+91 9123987654', NULL, 'User', 'Mumbai', '2021-09-30 00:00:00', 'VOTER005', NULL, NULL, NULL, 'Verified'),

('Isha Kapoor', 'isha.kapoor@gmail.com', '1996-03-20', '+91 9876123456', NULL, 'User', 'Mumbai', '2020-05-22 00:00:00', 'VOTER006', NULL, NULL, NULL, 'Verified'),

('Aditya Kumar', 'aditya.kumar@gmail.com', '1987-08-05', '+91 9123678901', NULL, 'User', 'Mumbai', '2021-02-14 00:00:00', 'VOTER007', NULL, NULL, NULL, 'Blocked'),

('Priya Jain', 'priya.jain@gmail.com', '1994-11-11', '+91 9988123456', NULL, 'User', 'Mumbai', '2022-03-08 00:00:00', 'VOTER008', NULL, NULL, NULL, 'Verified'),

('Karan Reddy', 'karan.reddy@gmail.com', '1991-01-25', '+91 9123998877', NULL, 'User', 'Mumbai', '2020-07-21 00:00:00', 'VOTER009', NULL, NULL, NULL, 'Verified'),

('Meera Sharma', 'meera.sharma@gmail.com', '1988-05-13', '+91 9876543210', NULL, 'User', 'Mumbai', '2021-12-11 00:00:00', 'VOTER010', NULL, NULL, NULL, 'Pending');
