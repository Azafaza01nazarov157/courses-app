# Course Management System

A comprehensive Android application for educational course management with different user roles and functionalities.

## Overview

This Course Management System is designed to facilitate interaction between administrators, teachers, and students. The app provides role-specific interfaces and features to manage courses, assignments, enrollments, and grades.

### Key Features

- **User Role Management**: Supports three user roles - Admin, Teacher, and Student
- **Course Management**: Create, edit, and manage courses
- **Enrollment System**: Enroll students in courses and assign teachers
- **Assignment Management**: Create and manage assignments for courses
- **Grading System**: Teachers can grade student submissions and provide feedback
- **Grade Viewing**: Students can view their grades for all enrolled courses

## User Roles and Capabilities

### Admin
- Manage all users (create, edit, delete)
- Create and manage all courses
- Manage course enrollments (assign teachers and students)

### Teacher
- View courses they are assigned to teach
- Create, activate, and deactivate assignments
- Grade student submissions
- Provide feedback to students

### Student
- View courses they are enrolled in
- View assignments for their courses
- View their grades and teacher feedback

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox (2020.3.1) or newer
- Minimum SDK: Android 6.0 (Marshmallow, API level 23)
- Target SDK: Android 13 (API level 33)

### Installation
1. Clone the repository:
   ```
   git clone https://github.com/Azafaza01nazarov157/course-management-app.git
   ```

2. Open the project in Android Studio:
   - Start Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned repository folder and click "OK"

3. Build the project:
   - Wait for Gradle to sync and download dependencies
   - Click on "Build" > "Build Project"

4. Run the application:
   - Connect an Android device or use an emulator
   - Click on "Run" > "Run 'app'"

### Database Setup
The application uses SQLite for data storage. The database is created automatically on first launch.

#### Default Admin Account
Username: admin
Password: admin

## Project Structure

- `app/src/main/java/com/narxoz/myapplication/` - Main source code
  - `adapters/` - RecyclerView adapters
  - `data/` - Database and repository implementations
  - `dialogs/` - Dialog fragments for user interactions
  - `models/` - Data model classes
  - `ui/` - User interface components
    - `admin/` - Admin-specific fragments
    - `teacher/` - Teacher-specific fragments
    - `student/` - Student-specific fragments
    - `login/` - Login and authentication
  - `utils/` - Utility classes

## Libraries Used

- AndroidX libraries for UI components
- Material Design Components
- RecyclerView for list displays
- SQLite for database management

## Contributing

Contributions to the project are welcome. Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details. 
