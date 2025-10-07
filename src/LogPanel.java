import javax.swing.*;
import java.awt.*;

// Класс LogPanel расширяет JScrollPane и предназначен для отображения логов (текста) с возможностью прокрутки
public class LogPanel extends JScrollPane {
    private JTextArea logArea; // Текстовая область для вывода логов
    // Цвета по умолчанию (можно перенести в SCCVisualizer, если нужно переключать тему)
    private final Color backgroundColor = new Color(40, 40, 40); // Темно-серый
    private final Color foregroundColor = new Color(200, 200, 200); // Светло-серый

    // Конструктор класса LogPanel
    public LogPanel() {
        logArea = new JTextArea(); // Создаем новую текстовую область
        logArea.setEditable(false); // Запрещаем редактирование текста пользователем (лог только для чтения)
        logArea.setBackground(backgroundColor);
        logArea.setForeground(foregroundColor);
        logArea.setCaretColor(foregroundColor); // Цвет курсора
        setViewportView(logArea); // Устанавливаем текстовую область как видимую часть JScrollPane (панели с прокруткой)
        getViewport().setBackground(backgroundColor);
        setBorder(BorderFactory.createLineBorder(backgroundColor));
    }

    // Метод для добавления текста в лог
    public void append(String text) {
        logArea.append(text); // Добавляем текст в конец текстовой области
        // Автоматически прокручиваем вниз, чтобы показать последний добавленный текст
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    // Метод для очистки всего текста в логе
    public void clear() {
        logArea.setText(""); // Очищаем текстовую область
    }
}