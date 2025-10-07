import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.*;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;

// Главное окно приложения, содержащее все компоненты визуализации
public class SCCVisualizer extends JFrame {

    // Панель для отрисовки графа
    private GraphPanel graphPanel;
    // Панель управления алгоритмом
    private ControlPanel controlPanel;
    // Панель параметров графа
    private GraphParamsPanel graphParamsPanel;
    // Панель для вывода логов
    private LogPanel logPanel;
    // Объект алгоритма поиска компонент
    private SCCAlgorithm algorithm;

    // Цвета для темной темы
    private final Color darkBackgroundColor = new Color(40, 40, 40); // Темно-серый фон
    private final Color darkForegroundColor = new Color(200, 200, 200); // Светло-серый текст
    private final Color darkButtonColor = new Color(60, 60, 60); // Более темный серый для кнопок
    private final Color darkBorderColor = new Color(80, 80, 80); // Цвет границы для темной темы

    // Цвета для светлой темы (по умолчанию)
    private final Color lightBackgroundColor = new Color(240, 240, 240);
    private final Color lightForegroundColor = new Color(0, 0, 0);
    private final Color lightButtonColor = new Color(220, 220, 220);
    private final Color lightBorderColor = new Color(180, 180, 180);

    private boolean isDarkTheme = true;  // Флаг, указывающий, какая тема сейчас активна (true - темная, false - светлая)

    //Конструктор главного окна
    public SCCVisualizer() {
        // Настройка основного окна
        setTitle("Визуализатор компонентов сильной связности"); // Устанавливаем заголовок окна
        setSize(1000, 700); // Устанавливаем размер окна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Устанавливаем действие при закрытии окна
        setLocationRelativeTo(null); // Центрируем окно на экране

        // Инициализация компонентов
        algorithm = new SCCAlgorithm(); // Создаем объект алгоритма
        graphPanel = new GraphPanel(algorithm); // Создаем панель для рисования графа
        controlPanel = new ControlPanel(this); // Создаем панель управления
        graphParamsPanel = new GraphParamsPanel(this); // Создаем панель параметров графа
        logPanel = new LogPanel(); // Создаем панель для вывода логов

        // Настройка компоновки элементов
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); // Создаем главную панель с компоновкой
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Устанавливаем отступы для главной панели

        // Добавление компонентов на главную панель
        mainPanel.add(graphParamsPanel, BorderLayout.NORTH); // Добавляем панель параметров в верхнюю часть

        // Центральная панель (граф + логи)
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10)); // Создаем центральную панель с сеткой
        centerPanel.add(graphPanel); // Добавляем панель графа
        centerPanel.add(logPanel); // Добавляем панель логов
        mainPanel.add(centerPanel, BorderLayout.CENTER); // Размещаем центральную панель

        mainPanel.add(controlPanel, BorderLayout.SOUTH); // Добавляем панель управления в нижнюю часть

        add(mainPanel); // Добавляем главную панель в окно

        // Применяем темную тему
        applyTheme(mainPanel);

        // Добавляем меню
        addMenu();
    }

    // Метод для переключения темы
    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        JPanel mainPanel = (JPanel) getContentPane().getComponent(0);
        applyTheme(mainPanel);
    }

    // Метод для применения темы (теперь учитывает, какая тема активна)
    private void applyTheme(JPanel panel) {
        Color backgroundColor = isDarkTheme ? darkBackgroundColor : lightBackgroundColor;
        Color foregroundColor = isDarkTheme ? darkForegroundColor : lightForegroundColor;
        Color buttonColor = isDarkTheme ? darkButtonColor : lightButtonColor;
        Color borderColor = isDarkTheme ? darkBorderColor : lightBorderColor;

        panel.setBackground(backgroundColor);
        panel.setForeground(foregroundColor);

        for (Component component : panel.getComponents()) {
            if (component instanceof JPanel) {
                applyTheme((JPanel) component); // Рекурсивно применяем к дочерним панелям
            } else if (component instanceof JLabel) {
                ((JLabel) component).setForeground(foregroundColor); // Устанавливаем цвет текста для меток
            } else if (component instanceof JTextField) {
                component.setBackground(backgroundColor);
                component.setForeground(foregroundColor);
                ((JTextField) component).setCaretColor(foregroundColor);  // Устанавливаем цвет каретки для текстовых полей
            } else if (component instanceof JButton) {
                component.setBackground(buttonColor); // Устанавливаем цвет фона для кнопок
                component.setForeground(foregroundColor); // Устанавливаем цвет текста для кнопок
            } else if (component instanceof JCheckBox) {
                component.setBackground(backgroundColor);
                component.setForeground(foregroundColor);
            }

            if (component instanceof JScrollPane) {
                component.setBackground(backgroundColor);
                component.setForeground(foregroundColor);
                JViewport viewport = ((JScrollPane) component).getViewport();
                viewport.setBackground(backgroundColor);
                if (viewport.getView() instanceof JTextArea) {
                    JTextArea textArea = (JTextArea) viewport.getView();
                    textArea.setBackground(backgroundColor);
                    textArea.setForeground(foregroundColor);
                    textArea.setCaretColor(foregroundColor);
                }

                ((JScrollPane) component).setBorder(BorderFactory.createLineBorder(borderColor));

            }
            Border border = panel.getBorder();
            if (border instanceof TitledBorder) {
                ((TitledBorder) border).setTitleColor(foregroundColor);
                ((TitledBorder) border).setBorder(BorderFactory.createLineBorder(borderColor));
            }
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    // Добавляем меню для экспорта/импорта графа и смены темы
    private void addMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem importItem = new JMenuItem("Импорт графа");
        JMenuItem exportItem = new JMenuItem("Экспорт графа");
        JMenuItem themeItem = new JMenuItem("Сменить тему");

        //Обработчик импорта
        importItem.addActionListener(e -> importGraph());

        //Обработчик экспорта
        exportItem.addActionListener(e -> exportGraph());

        // Обработчик смены темы
        themeItem.addActionListener(e -> toggleTheme());

        fileMenu.add(importItem);
        fileMenu.add(exportItem);
        fileMenu.add(themeItem);  // Добавляем пункт меню для смены темы
        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
    }

    // Метод для экспорта графа в файл
    private void exportGraph() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить граф");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Графы (*.graph)", "graph")); // Добавлено расширение фильтра

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // Добавляем расширение .graph, если его нет
            if (!fileToSave.getName().toLowerCase().endsWith(".graph")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".graph");
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileToSave))) {
                oos.writeObject(algorithm.getGraph());
                logPanel.append("Граф экспортирован в " + fileToSave.getAbsolutePath() + "\n");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Ошибка при экспорте графа: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Метод для импорта графа из файла
    private void importGraph() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Импортировать граф");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Графы (*.graph)", "graph")); // Добавлено расширение фильтра

        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();

            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileToLoad))) {
                List<List<Integer>> importedGraph = (List<List<Integer>>) ois.readObject();
                algorithm.setGraph(importedGraph);
                graphPanel.repaint();
                logPanel.append("Граф импортирован из " + fileToLoad.getAbsolutePath() + "\n");
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Ошибка при импорте графа: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Сброс всех данных
    public void resetData() {
        algorithm.reset(); // Сбрасываем состояние алгоритма
        graphPanel.repaint(); // Перерисовываем граф
        logPanel.clear(); // Очищаем логи
        controlPanel.updateStepInfo(0, 0); // Сбрасываем счетчик шагов
    }

    // Генерация случайного графа
    public void generateGraphAutomatically() {
        try {
            // Получаем количество вершин из текстового поля
            int vertexCount = Integer.parseInt(graphParamsPanel.getVertexCount()); // Парсим количество вершин
            if (vertexCount <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Введите положительное число вершин",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            algorithm.generateRandomGraph(vertexCount); // Генерируем граф
            logPanel.append("Сгенерирован граф с " + vertexCount + " вершинами\n"); // Логируем информацию о графе
            graphPanel.repaint(); // Обновляем отображение графа
        } catch (NumberFormatException e) {
            // Показываем сообщение об ошибке
            JOptionPane.showMessageDialog(this,
                    "Некорректное число вершин", // Текст ошибки
                    "Ошибка", // Заголовок окна ошибки
                    JOptionPane.ERROR_MESSAGE); // Тип сообщения
        }
    }

    // Запуск/продолжение визуализации алгоритма
    public void startVisualization() {
        // Проверяем, что граф создан
        if (algorithm.getGraph().isEmpty()) { // Если граф пуст
            JOptionPane.showMessageDialog(this,
                    "Граф не создан", // Текст ошибки
                    "Ошибка", // Заголовок окна ошибки
                    JOptionPane.ERROR_MESSAGE); // Тип сообщения
            return; // Выходим из метода
        }

        // Если алгоритм еще не начат - инициализируем
        if (algorithm.getExecutionPosition() == 0) { // Если алгоритм не запущен
            new Thread(() -> {
                algorithm.findSCCs(); // Запускаем алгоритм для поиска компонент
                startVisualizationThread(); // Запускаем поток визуализации
            }).start(); // Запускаем новый поток
        } else {
            // Продолжение выполнения
            startVisualizationThread(); // Запускаем поток визуализации
        }
    }

    // Поток визуализации выполнения алгоритма
    private void startVisualizationThread() {
        new Thread(() -> {
            controlPanel.setRunning(true); // Устанавливаем флаг работы панели управления

            // Продолжаем с текущего шага
            for (int i = algorithm.getCurrentStep(); i < algorithm.getSteps().size(); i++) { // Проходим по шагам алгоритма
                if (!controlPanel.isRunning()) { // Если панель управления не работает
                    // Сохраняем позицию для продолжения
                    algorithm.setExecutionPosition(algorithm.getCurrentStep()); // Устанавливаем текущую позицию выполнения
                    break; // Выходим из цикла
                }

                final int step = i; // Сохраняем текущий шаг
                // Обновляем GUI в основном потоке
                SwingUtilities.invokeLater(() -> {
                    algorithm.setCurrentStep(step); // Устанавливаем текущий шаг в алгоритме
                    // Обновляем информацию о шагах
                    controlPanel.updateStepInfo(step + 1, algorithm.getSteps().size()); // Обновляем информацию о текущем шаге
                    logPanel.append(algorithm.getSteps().get(step) + "\n"); // Добавляем шаг в лог
                    graphPanel.repaint(); // Перерисовываем граф
                });

                try {
                    Thread.sleep(controlPanel.getDelay()); // Задержка между шагами
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Прерывание потока
                }
            }

            // После завершения обновляем состояние
            SwingUtilities.invokeLater(() -> {
                controlPanel.setRunning(false); // Снимаем флаг работы панели управления
                // Если алгоритм завершен полностью
                if (algorithm.getCurrentStep() >= algorithm.getSteps().size() - 1) { // Если достигнут последний шаг
                    algorithm.setExecutionPosition(0); // Сбрасываем позицию выполнения
                }
            });
        }).start(); // Запускаем новый поток
    }

    // Отображение результатов работы алгоритма
    public void showResult() {
        // Формируем строку с результатами
        StringBuilder sb = new StringBuilder("\nКомпоненты сильной связности:\n"); // Создаем строку для вывода результатов
        // Добавляем каждую компоненту
        for (List<Integer> scc : algorithm.getSCCs()) { // Проходим по найденным компонентам
            sb.append(scc).append("\n"); // Добавляем компоненту в строку
        }
        logPanel.append(sb.toString()); // Выводим результаты в лог
    }

    // Геттеры для доступа к компонентам
    public GraphPanel getGraphPanel() {
        return graphPanel;
    } // Геттер для панели графа

    public LogPanel getLogPanel() {
        return logPanel;
    } // Геттер для панели логов

    public SCCAlgorithm getAlgorithm() {
        return algorithm;
    } // Геттер для алгоритма
}