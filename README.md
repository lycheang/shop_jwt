# shop_jwt
🚀 Key Features
1. Security & Authentication (JWT)
The system uses Stateless Authentication with JSON Web Tokens (JWT).

Login/Register: Users can sign up and log in to receive an Access Token and a Refresh Token.

Role-Based Access Control (RBAC):

ROLE_USER: Can browse products, manage cart, and place orders.

ROLE_ADMIN: Can add/update/delete products, manage users, and view all orders.

Token Rotation: Uses a Refresh Token mechanism to keep users logged in securely without forcing re-login every 15 minutes.

Forgot Password: Secure email-based flow to reset passwords via unique tokens.

2. Product Management (The Core)
CRUD Operations: Admins can Create, Read, Update, and Delete products.

Soft Delete: Products are never actually deleted from the database. They are marked as active: false to preserve order history while hiding them from customers.

Inventory Logic: The system automatically calculates stock status:

"In Stock" (> 10 items)

"Low Stock" (<= 10 items)

"Out of Stock" (0 items)

Search & Filter: Advanced filtering by Category, Brand, and Name.

3. Shopping Cart & Orders
Cart System: Users can add items to a persistent cart.

Order Placement: Converts cart items into a permanent Order record.

Order History: Users can view their past orders; Admins can view orders for specific users.

4. Image Management
Multiple Images: Products can have multiple images associated with them.

BLOB Storage: Images are currently stored in the database as Large Objects (BLOBs), exposed via download URLs.

5. Performance Optimizations

DTO Pattern: Uses Data Transfer Objects to decouple the internal database entities from the API response (e.g., hiding password hashes, calculating stock status on the fly).

💾 Database Design (ERD Concept)
Here is how your entities relate to each other:

User 1--* Order (One user has many orders)

User 1--1 Cart (One user has one active cart)

Cart 1--* CartItem *--1 Product (Cart contains items, items link to products)

Product *--1 Category (Many products belong to one category)

Product 1--* Image (One product has many images)

User *--* Role (Users can be both Admin and User)

🛠️ Tech Stack Summary

Component,Technology,Purpose


Language,Java 21+,Core logic


Framework,Spring Boot 3.3,Backend framework


Database,MySQL 8.0,Persistent storage


ORM,Hibernate / JPA,Database interaction


Security,Spring Security 6,Auth & Authorization


API Docs,Swagger UI / OpenAPI,Interactive API documentation


Ops,Docker,Deployment & Environment consistency


