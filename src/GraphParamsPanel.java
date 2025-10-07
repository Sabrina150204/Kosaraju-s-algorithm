import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// Панель для ввода параметров графа
public class GraphParamsPanel extends JPanel {
    private JTextField vertexCountField; // Поле для ввода количества вершин
    private SCCVisualizer visualizer;    // Ссылка на главное окно визуализатора
    // Цвета по умолчанию (можно перенести в SCCVisualizer, если нужно переключать тему)
    private final Color foregroundColor = new Color(200, 200, 200); // Светло-серый
    private final Color backgroundColor = new Color(40, 40, 40); // Темно-серый
    private final Color buttonColor = new Color(60, 60, 60); // Темно-серый для кнопок

    // Конструктор панели параметров, visualizer ссылка на главное окно
    public GraphParamsPanel(SCCVisualizer visualizer) {
        this.visualizer = visualizer; // Сохраняем ссылку на визуализатор
        setLayout(new GridLayout(2, 2, 5, 5)); // Устанавливаем layout (2 строки, 2 столбца с отступами)
        setBorder(BorderFactory.createTitledBorder(new TitledBorder(null, "Параметры графа", TitledBorder.LEADING, TitledBorder.TOP, null, foregroundColor))); // Устанавливаем рамку с заголовком
        setBackground(backgroundColor);
        setForeground(foregroundColor);

        // Создаем элементы управления
        JLabel vertexLabel = new JLabel("Количество вершин:"); // Метка для поля ввода
        vertexLabel.setForeground(foregroundColor);
        add(vertexLabel);

        vertexCountField = new JTextField("6", 5); // Поле ввода с значением по умолчанию 6
        vertexCountField.setBackground(backgroundColor);
        vertexCountField.setForeground(foregroundColor);
        vertexCountField.setCaretColor(foregroundColor);
        add(vertexCountField);

        JButton autoButton = new JButton("Сгенерировать"); // Кнопка для автоматической генерации графа
        autoButton.setBackground(buttonColor);
        autoButton.setForeground(foregroundColor);
        // Обработчик кнопки генерации
        autoButton.addActionListener(e -> {
            try {
                // Получаем количество вершин из поля ввода
                int vertexCount = Integer.parseInt(vertexCountField.getText());
                if (vertexCount <= 0) { // Проверяем, чтобы количество вершин было положительным
                    JOptionPane.showMessageDialog(visualizer,
                            "Введите положительное число вершин",
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE);
                    return; // Выходим из обработчика
                }
                visualizer.generateGraphAutomatically(); // Вызываем метод генерации графа
            } catch (NumberFormatException ex) {
                // Если ввод некорректен - выводим сообщение об ошибке
                JOptionPane.showMessageDialog(visualizer,
                        "Введите корректное число вершин",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        add(autoButton);

        JButton manualButton = new JButton("Ввести вручную"); // Кнопка для ручного ввода графа
        manualButton.setBackground(buttonColor);
        manualButton.setForeground(foregroundColor);
        // Обработчик кнопки ручного ввода
        manualButton.addActionListener(e -> showManualInputDialog()); // Показать диалог для ручного ввода
        add(manualButton);
    }

    // Отображение диалога для ручного ввода графа
    private void showManualInputDialog() {
        try {
            // Получаем количество вершин из поля ввода
            int vertexCount = Integer.parseInt(vertexCountField.getText());
            if (vertexCount <= 0) throw new NumberFormatException(); // Проверка на положительное число

            // Создаем панель с таблицей смежности
            JPanel panel = new JPanel(new GridLayout(vertexCount + 1, vertexCount + 1, 2, 2));
            panel.setBackground(backgroundColor);
            panel.setForeground(foregroundColor);

            // Заголовки столбцов
            panel.add(new JLabel("")); // Пустая ячейка для верхнего левого угла
            for (int i = 0; i < vertexCount; i++) {
                JLabel label = new JLabel("В " + i, SwingConstants.CENTER);
                label.setForeground(foregroundColor);
                panel.add(label); // Заголовки для каждой вершины
            }

            // Матрица смежности (чекбоксы)
            JCheckBox[][] matrix = new JCheckBox[vertexCount][vertexCount]; // Создаем массив чекбоксов
            for (int from = 0; from < vertexCount; from++) {
                JLabel label = new JLabel("Из " + from);
                label.setForeground(foregroundColor);
                panel.add(label); // Заголовок строки для начальной вершины
                for (int to = 0; to < vertexCount; to++) {
                    matrix[from][to] = new JCheckBox(); // Создаем чекбокс для ребра
                    matrix[from][to].setBackground(backgroundColor);
                    matrix[from][to].setForeground(foregroundColor);
                    if (from == to) matrix[from][to].setEnabled(false); // Запрещаем петли (ребра из вершины в саму себя)
                    panel.add(matrix[from][to]); // Добавляем чекбокс на панель
                }
            }

            // Показываем диалоговое окно с матрицей смежности
            int result = JOptionPane.showConfirmDialog(
                    visualizer,
                    panel,
                    "Матрица смежности", // Заголовок диалога
                    JOptionPane.OK_CANCEL_OPTION, // Опции кнопок
                    JOptionPane.PLAIN_MESSAGE); // Стиль сообщения

            // Если пользователь нажал OK
            if (result == JOptionPane.OK_OPTION) {
                List<List<Integer>> graph = new ArrayList<>(); // Создаем список для графа
                for (int i = 0; i < vertexCount; i++) {
                    graph.add(new ArrayList<>()); // Добавляем новый список для каждой вершины
                    for (int j = 0; j < vertexCount; j++) {
                        if (matrix[i][j].isSelected()) {
                            graph.get(i).add(j); // Добавляем ребро, если чекбокс выбран
                        }
                    }
                }
                // Устанавливаем граф и обновляем отображение
                visualizer.getAlgorithm().setGraph(graph); // Устанавливаем граф в алгоритм
                visualizer.getGraphPanel().repaint(); // Перерисовываем панель графа
                visualizer.getLogPanel().append("Граф введен вручную\n"); // Записываем в лог
            }
        } catch (NumberFormatException e) {
            // Обработка исключения при неверном вводе
            JOptionPane.showMessageDialog(
                    visualizer,
                    "Введите положительное число вершин", // Сообщение об ошибке
                    "Ошибка", // Заголовок сообщения
                    JOptionPane.ERROR_MESSAGE); // Стиль сообщения
        }
    }

    // Получение количества вершин
    public String getVertexCount() {
        return vertexCountField.getText(); // Возвращаем текст из поля ввода
    }
}