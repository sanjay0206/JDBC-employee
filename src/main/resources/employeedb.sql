DROP DATABASE IF EXISTS employeedb;
CREATE DATABASE employeedb;
USE employeedb;

SET foreign_key_checks = 0;
--SET foreign_key_checks = 1;

CREATE TABLE departments (
    dept_id INT AUTO_INCREMENT PRIMARY KEY,
    dept_name VARCHAR(255)
);

CREATE TABLE locations (
    location_id INT AUTO_INCREMENT PRIMARY KEY,
    address VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    postal_code VARCHAR(20)
);

CREATE TABLE projects (
    project_id INT AUTO_INCREMENT PRIMARY KEY,
    project_name VARCHAR(255),
    start_date DATE,
    end_date DATE,
    project_status ENUM('ONGOING', 'COMPLETED')
);

CREATE TABLE employees (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    salary DECIMAL(10, 2),
    fk_dept_id INT,
    fk_proj_id INT,
    fk_loc_id INT,
    FOREIGN KEY (fk_dept_id) REFERENCES departments(dept_id),
    FOREIGN KEY (fk_proj_id) REFERENCES projects(project_id),
    FOREIGN KEY (fk_loc_id) REFERENCES locations(location_id)
);
