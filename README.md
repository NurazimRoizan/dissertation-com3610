This project delves into the fascinating intersection of pebble games and the k-WL algorithm, two powerful concepts in graph theory and computer science. These two concepts are brought to life through an interactive and visually engaging interface that allows the user to explore the dynamics of pebble games and the iterative refinement process of the k-WL algorithm in a way that is both accessible and intuitive

## Running the Application in VS Code

1.  **Open the project in VS Code:**
    * Open the root directory of the project in VS Code.
    * Ensure you have the necessary Java extensions installed (e.g., the Extension Pack for Java from Microsoft). VS Code should automatically detect the project structure and the external libraries located in the `lib` folder.

2.  **Run the application:**
    * Navigate to and open the App.java file (the one containing the `main` method).
    * Right-click anywhere within the editor window showing the main file.
    * Select "Run Java" from the context menu.

3.  **Troubleshooting: If Libraries Aren't Detected (Unlikely):**
    * In most cases, the external libraries (`.jar` files) in the `lib` folder will be automatically added to the project's classpath by VS Code's Java extensions.
    * However, **if** you encounter compilation errors or runtime `ClassNotFoundException` errors indicating that libraries are missing, you may need to add them manually:
        * In the VS Code EXPLORER view (usually on the left sidebar), find the **Java Projects** section.
        * Expand your project node.
        * Right-click on the **Referenced Libraries** node.
        * Choose the option to add JARs (it might look like a '+' icon or be in the context menu).
        * Navigate to the `lib` folder within your project directory.
        * Select all the required `.jar` files and confirm their addition.
        * Try running the application again (Step 2).