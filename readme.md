# Employee JDBC API Documentation

This documentation provides details on the API endpoints for managing employees, departments, locations, and projects in the `employee-servlet-jdbc` project.

### Base URL: `http://localhost:8080`


### Employees

#### `GET /employees` Retrieve a list of all employees.

#### `POST /employees` Add a new employee.

**Request:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "salary": 60000
}
```

#### `PUT /employees?employeeId=1&projectId=1&deptId=1&locationId=2` 
Update an existing employee.


**Request:**
```json
{
  "firstName": "Johnny",
  "lastName": "Dep",
  "salary": 65000
}
```
#### `DELETE /employees?employeeId=1` Delete an employee by ID.

---

### Departments

#### `GET /departments` Retrieve a list of all departments.

#### `POST /departments` Add a new department.

**Request:**
```json
{
  "deptName": "Human Resources"
}
```

#### `PUT /departments?deptId=1` Update an existing department.

**Request:**
```json
{
  "deptName": "IT"
}
```
#### `DELETE /departments?deptId=1` Delete a department by ID.

---

### Locations

#### `GET /locations` Retrieve a list of all locations.

#### `POST /locations` Add a new location.

**Request:**
```json
{
  "address": "123 Main St",
  "city": "Cityville",
  "country": "Countryland",
  "postalCode": "12345"
}
```

#### `PUT /locations?locationId=1` Update an existing location.

**Request:**
```json
{
  "address": "UpdatedAddress",
  "city": "UpdatedCity"
}
```
#### `DELETE /locations?locationId=1` Delete a location by ID.

---

### Projects

#### `GET /projects` Retrieve a list of all projects.

#### `POST /projects` Add a new project.

**Request:**
```json
{
  "projectName": "New Project",
  "startDate": "2023-01-01",
  "endDate": "2023-12-31",
  "projectStatus": "ONGOING"
}
```

#### `PUT /projects?projectId=1` Update an existing project.

**Request:**
```json
{
  "projectName": "Updated Project",
  "endDate": "2023-11-30",
  "projectStatus": "COMPLETED"
}
```
#### `DELETE /projects?projectId=1` Delete a project by ID.








