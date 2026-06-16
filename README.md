# 🧠 Student Mental Health & Academic Stress Tracker

A comprehensive, full-stack web application designed to help university students monitor their daily mental wellbeing, while providing counselors with actionable insights and real-time communication tools to support at-risk students.

Built with **Java Spring Boot**, **Thymeleaf**, **MySQL**, and **Tailwind CSS**, strictly following **MVC Architecture** and **SOLID Design Principles**.

---

## ✨ Core Features

### 👨‍🎓 Student Portal
- **Daily Check-ins:** Log mood and stress levels (1-10 scale) with optional notes.
- **Privacy First:** Option to mark entries as anonymous.
- **Visual Insights:** Interactive **Chart.js** line graphs for weekly mood/stress trends.
- **90-Day Stress Heatmap:** GitHub-style contribution graph visualizing long-term stress patterns.
- **Voice Journal:** Built-in Web Speech API allowing students to dictate notes hands-free.
- **PDF Export:** Generate and download professional, confidential mental health progress reports.

### 🧑‍⚕️ Counselor Portal
- **Student Directory:** View all registered students and their detailed entry histories.
- **At-Risk Alerts:** Automatic system flags for students logging stress >7 for 3 consecutive days.
- **Secure Real-Time Chat:** WhatsApp-style messaging to provide immediate support to students.
- **Alert Management:** Mark alerts as resolved after intervention.

### 🛡️ Admin Panel
- **Counselor Management:** Securely add and remove counselor accounts.

### 🎮 Gamification & Engagement (Bonus Features)
- **Streak Tracking:** Daily login and logging streaks (Current & Longest).
- **Achievement Badges:** Earn badges like "Week Warrior" and "Mindfulness Master".
- **Smart Recommendations:** AI-lite logic that provides personalized coping tips based on the latest stress score.

---

## 🛠️ Technology Stack

| Component | Technology |
| :--- | :--- |
| **Backend Framework** | Java 21, Spring Boot 3.x |
| **Frontend / UI** | Thymeleaf, HTML5, Tailwind CSS |
| **Database** | MySQL 8.0 (Relational) |
| **ORM** | Spring Data JPA (Hibernate) |
| **Security** | Spring Security (BCrypt Password Hashing, Role-Based Access) |
| **Charts & Visuals** | Chart.js, Custom CSS Heatmap Grid |
| **API / Real-time** | RESTful APIs, JavaScript Polling (Chat) |
| **PDF Generation** | OpenPDF Library |
| **Build Tool** | Maven |

---

## 🏗️ Architecture & SOLID Principles

This project strictly adheres to industry-standard design patterns:
- **MVC Architecture:** Clear separation between Controllers (HTTP), Services (Business Logic), and Repositories (Data Access).
- **Dependency Injection:** All services are injected via constructors using Spring IoC.
- **Interface Segregation:** Distinct interfaces for `AuthService`, `EntryService`, `ChatService`, etc.
- **DTO / Entity Separation:** Clean mapping between Database Entities and UI logic.

---

## 🚀 Local Setup Instructions

### Prerequisites
- Java 21 JDK
- Maven 3.9+
- MySQL 8.0

### Steps
1. **Clone the repository:**
   ```bash
   git clone <your-repo-url>
   cd Project