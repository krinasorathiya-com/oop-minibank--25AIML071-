# oop-minibank--25AIML071-
# MiniBank - Java Banking Management System

## About the Project

MiniBank is a console-based banking management application developed using Java. This project is created as a student mini project to practice Object-Oriented Programming (OOP), exception handling, collections, file handling, and multithreading concepts.

The application provides basic banking operations through a simple menu-driven interface.

## Features

- Open a new bank account
- Deposit money
- Withdraw money
- Transfer money between accounts
- View all accounts
- View account statements
- Generate banking reports
- Maintain transaction history
- Save and load account data
- Handle banking errors using custom exceptions
- Test banking operations using JUnit
- Support concurrent deposit operations

## Technologies Used

- Java 17
- Apache Maven
- JUnit 5
- Git
- GitHub

## Project Structure

```text
oop-minibank-25AIML071/
├── model/
├── service/
├── util/
├── exception/
├── data/
├── src/
│   └── test/
│       └── java/
├── pom.xml
├── README.md
└── .gitignore
```

- `model/` — account classes, interfaces, and annotations.
- `service/` — banking operations and related services.
- `util/` — utility classes.
- `exception/` — custom exception classes.
- `data/` — application data files, if generated.
- `src/test/java/` — automated test classes.
- `pom.xml` — Maven configuration and dependencies.

## Requirements

Install these tools before running the project:

- JDK 17 or later
- Apache Maven
- Git (optional)

Verify the installation:

```bash
java -version
mvn -version
```

## How to Run the Project

### 1. Clone the Repository

Replace `YOUR_GITHUB_USERNAME` with your GitHub username.

```bash
git clone https://github.com/YOUR_GITHUB_USERNAME/oop-minibank-25AIML071.git
cd oop-minibank-25AIML071
```

If the project is already on your computer, open the terminal in the existing project folder.

### 2. Run Tests

```bash
mvn clean test
```

### 3. Build the Project

```bash
mvn package
```

### 4. Run the Application

```bash
java -jar target/oop-minibank-1.0.0.jar
```

## Application Menu

The application provides the following menu options:

1. Open Account
2. Deposit
3. Withdraw
4. Transfer
5. List All Accounts
6. View Account Statement
7. Generate Report
8. Working Hours
9. Save and Exit

Select an option from the console menu and follow the instructions displayed by the application.

## OOP Concepts Used

- **Encapsulation:** Organizes account data and operations within classes.
- **Abstraction:** Defines common behavior through abstract classes and interfaces.
- **Inheritance:** Allows account types to reuse common functionality.
- **Polymorphism:** Supports different implementations of account behavior.
- **Exception Handling:** Handles invalid amounts and other banking errors.
- **Collections:** Stores and manages account and transaction information.
- **Multithreading:** Demonstrates concurrent operations and synchronization.
- **File Handling:** Supports saving and loading application data.
- **Unit Testing:** Uses JUnit to test
