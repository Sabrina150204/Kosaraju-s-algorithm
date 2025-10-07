import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Запускаем GUI в потоке обработки событий Swing
        SwingUtilities.invokeLater(() -> {
            // Создаем экземпляр главного окна приложения
            SCCVisualizer visualizer = new SCCVisualizer();
            // Делаем окно видимым
            visualizer.setVisible(true);
        });
    }
}