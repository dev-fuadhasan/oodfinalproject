# 💬 Terminal-Based Chat App (Core Java)

A text-based chat application developed using **Core Java**. This terminal app allows multiple users to sign up, log in, chat one-on-one, create and manage chat groups, and store all data persistently using file handling. It simulates a basic real-time communication system with admin-level group controls.

---

## 🚀 Features

### 👤 User Account Management
- ✅ Sign Up (Create a new user account)
- 🔐 Log In / Log Out
- ❌ Delete Account (Removes the user and all associated data)

### 💬 Private Chat
- 🔍 Search for other users by username
- 📩 Send and receive messages (terminal-based simulation)
- 🕓 Message logging with timestamps
- 🗂 Chat history stored persistently

### 👥 Group Chat
- 🏗 Create new groups (creator becomes admin)
- 📃 List all available groups
- 🔍 Search groups by name
- 🙋‍♂️ Request to join a group
- ✅ Admin can accept or decline group join requests
- 🚫 Admin can remove members
- 🗑 Admin can delete group

### 🧾 Persistent Storage
- All data is stored using **text files** for reusability and session continuity:
  - `users.txt` — Stores user information
  - `messages.txt` — Stores chat messages
  - `groups.txt` — Stores group data
  - `requests.txt` — Stores group join requests
- File-based I/O ensures the application maintains its state across sessions

---

## 🛠️ Technologies Used

- **Java (Core Concepts Only)**
  - OOP (Object-Oriented Programming)
  - Java Collections Framework
  - Exception Handling
  - File I/O (BufferedReader, BufferedWriter, FileReader, FileWriter)
- **Multithreading**
  - Simulates real-time chat between users

---

## 🧠 Learning Outcomes

- Applied OOP principles to a real-world application
- Implemented user and group management features from scratch
- Gained experience in Java File Handling for persistent data storage
- Practiced multithreading to simulate live chatting
- Strengthened problem-solving and logic-building skills in Java

---

