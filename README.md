# Restaurant Order Management System

A Java Swing desktop application for restaurant order management.

## Features

- User login system (Admin / Waiter roles)
- Menu management with sorting and filtering
- Order tracking with status updates (Waiting, Preparing, Served, Paid)
- Statistics panel with bar and pie charts
- File-based data persistence (menu.txt, orders.txt, users.txt)

## Technologies

- Java (JDK 8+)
- Java Swing (GUI)
- File I/O (BufferedReader / BufferedWriter)
- OOP principles (Encapsulation, Composition)
- Sorting algorithms (Bubble Sort, Selection Sort, Insertion Sort)

## Project Structure
```
src/
├── Main.java
├── model/
│   ├── MenuItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   └── User.java
├── manager/
│   ├── MenuManager.java
│   ├── OrderManager.java
│   ├── UserManager.java
│   └── FileManager.java
└── gui/
    ├── LoginFrame.java
    ├── MainFrame.java
    ├── MenuPanel.java
    ├── OrderPanel.java
    ├── StatisticsPanel.java
    └── UserPanel.java
```
## How to Run

1. Open project in NetBeans (Java with Ant)
2. Set Main class to `Main`
3. Run with F6

## Default Login

| Username | Password | Role   |
|----------|----------|--------|
| admin    | 1234     | Admin  |
| garson1  | 1234     | Waiter |
