# Online Book Store

## Overview
The **Online Book Store** is a desktop application that facilitates the buying, selling, and management of books. It serves two types of users—Customers and Admins—each with distinct roles. The system is designed to provide an intuitive interface for customers to browse and purchase books while offering admins tools to manage inventory, orders, and monitor sales performance.

This project implements key software design patterns for maintainability and scalability, and it includes a user-friendly graphical user interface (GUI).

---

## Features

### Customer Features
1. **Account Management**:
   - Sign up, log in, and manage account details (username, password, address, phone).

2. **Book Browsing and Searching**:
   - View available books with details like title, author, price, and popularity.
   - Search by title or author, filter by categories (e.g., IT, history, classics), and sort by popularity or price.

3. **Cart Management and Ordering**:
   - Add, edit, or remove books from the cart.
   - Place an order, make payments, and receive order confirmations.
   - Cancel orders before admin confirmation.

4. **Order History and Reviews**:
   - Track order status (pending, confirmed, shipped).
   - View past orders and leave reviews for purchased books.

---

### Admin Features
1. **Book and Category Management**:
   - Add, edit, or delete books and categories, including details like price, stock, edition, and cover image.

2. **Order Management**:
   - View, confirm, or cancel customer orders.
   - Notify customers of order status updates.

3. **Inventory and Statistics Monitoring**:
   - Track stock levels, update book availability, and generate sales reports.
   - View statistics like top-selling books and popular categories.

---

## Design Patterns
This project implements the following design patterns:
1. **Singleton**: Ensures only one instance of critical classes like database connection or system configuration is created.
2. **Observer**: Keeps customers updated on their order status through notifications.
3. **Factory**: Simplifies the creation of book categories and GUI components.


## System Requirements
1. **Programming Language**: Java
2. **GUI Framework**: JavaFX
3. **Database**: SQLite or MySQL
4. **Development Tools**: IntelliJ IDEA, Eclipse, or NetBeans
5. **Java Version**: Java 8 or later

