# CollaboCanvas 🎨  
An interactive, collaborative drawing canvas that allows multiple users to design and draw on a shared canvas in real-time. Built using **JavaFX** and **Socket Programming**, this project combines creativity with advanced programming techniques.

---

## 🌟 Features  
1. **User-Friendly Graphical Interface**  
   - Designed with JavaFX for an intuitive and responsive user experience.
   - Separate screens for registration/login and the main canvas.

2. **Real-Time Collaboration**  
   - Supports multiple users drawing simultaneously on a shared canvas.
   - Synchronization of shapes, lines, and text across clients.

3. **Drawing Tools**  
   - **Pen Tool**: Freehand drawing.
   - **Shapes**: Circle, Rectangle, Triangle, and Square.
   - **Text Tool**: Add custom text to the canvas.
   - **Eraser**: Remove parts of the drawing.
   - **Color Picker**: Choose colors for drawing and filling.
   - **Size Adjuster**: Customize the pen or shape size.

4. **Connected Client Management**  
   - View a list of all connected users.
   - Chat box for communication between users.

5. **Canvas Persistence**  
   - Save the current state of the canvas.
   - Load and edit previously saved canvases.

---

## 🛠️ Project Modules  
### **1. Graphical User Interface (GUI)**  
- Built with JavaFX, featuring at least two pages:  
  - **Registration/Login Page**: Allows users to register or log in using a username and password.  
  - **Main Page**: The shared drawing canvas with tools and user list.

### **2. Server-Side Features**  
- Manages user authentication and synchronization of drawing activities between clients.
- Implements mechanisms for handling client messages and canvas activities.

### **3. Client-Side Features**  
- Connects to the server and allows users to:  
  - Perform drawing activities (lines, shapes, text, erasing).  
  - Synchronize canvas updates in real-time with other clients.  
  - Send messages in the chat box.

---

## 🚀 Getting Started  
### **Prerequisites**  
- Java 11 or later.
- JavaFX SDK (added to the classpath).
- A Git client for cloning the repository.

### **Installation**  
1. Clone the repository:  
   ```bash
   git clone https://github.com/jafarsaberi13/AP-Final-Project--7-.git
   cd AP-Final-Project--7-
