import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

// Панель управления визуализацией алгоритма
public class ControlPanel extends JPanel {
    private JTextField delayField; // Поле для ввода задержки между шагами (мс)
    private JLabel stepLabel; // Метка для отображения текущего шага
    private JLabel totalStepsLabel; // Метка для отображения общего количества шагов
    private JButton startButton; // Кнопка для запуска алгоритма
    private JButton stopButton; // Кнопка для остановки алгоритма
    private JButton prevButton; // Кнопка "Назад"
    private JButton nextButton; // Кнопка "Вперед"
    private volatile boolean isRunning; // Флаг выполнения алгоритма
    private SCCVisualizer visualizer; // Ссылка на главное окно визуализатора
    // Цвета по умолчанию (можно перенести в SCCVisualizer, если нужно переключать тему)
    private final Color foregroundColor = new Color(200, 200, 200); // Светло-серый
    private final Color backgroundColor = new Color(40, 40, 40); // Темно-серый
    private final Color buttonColor = new Color(60, 60, 60); // Темно-серый для кнопок

    // Конструктор панели управления, visualizer ссылка на главное окно
    public ControlPanel(SCCVisualizer visualizer) {
        this.visualizer = visualizer; // Сохраняем ссылку на визуализатор
        setLayout(new GridLayout(2, 1, 5, 5)); // Устанавливаем сетку для размещения элементов
        setBorder(BorderFactory.createTitledBorder(new TitledBorder(null, "Управление", TitledBorder.LEADING, TitledBorder.TOP, null, foregroundColor))); // Устанавливаем заголовок панели
        setBackground(backgroundColor);
        setForeground(foregroundColor);

        // 1. Панель автоматического управления
        JPanel autoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)); // Создаем панель для автоматического управления
        autoPanel.setBorder(BorderFactory.createTitledBorder(new TitledBorder(null, "Автоматически", TitledBorder.LEADING, TitledBorder.TOP, null, foregroundColor))); // Заголовок панели
        autoPanel.setBackground(backgroundColor);
        autoPanel.setForeground(foregroundColor);

        // Создаем элементы управления
        JLabel delayLabel = new JLabel("Задержка (мс):"); // Метка для задержки
        delayLabel.setForeground(foregroundColor);
        autoPanel.add(delayLabel);

        delayField = new JTextField("1000", 5); // Поле для ввода задержки с значением по умолчанию 1000 мс
        delayField.setBackground(backgroundColor);
        delayField.setForeground(foregroundColor);
        delayField.setCaretColor(foregroundColor);
        autoPanel.add(delayField);

        startButton = new JButton("Старт"); // Кнопка "Старт"
        startButton.setBackground(buttonColor);
        startButton.setForeground(foregroundColor);
        // Обработчик кнопки "Старт"
        startButton.addActionListener(e -> {
            if (!isRunning) { // Проверяем, не выполняется ли алгоритм
                visualizer.startVisualization(); // Запускаем визуализацию
            }
        });
        autoPanel.add(startButton);

        stopButton = new JButton("Стоп"); // Кнопка "Стоп"
        stopButton.setBackground(buttonColor);
        stopButton.setForeground(foregroundColor);
        stopButton.setEnabled(false); // Изначально кнопка отключена
        // Обработчик кнопки "Стоп"
        stopButton.addActionListener(e -> {
            setRunning(false); // Останавливаем выполнение
            // Позиция сохраняется автоматически в SCCVisualizer
        });
        autoPanel.add(stopButton);

        JButton resultButton = new JButton("Результат"); // Кнопка для отображения результата
        resultButton.setBackground(buttonColor);
        resultButton.setForeground(foregroundColor);
        // Обработчик кнопки "Результат"
        resultButton.addActionListener(e -> visualizer.showResult()); // Показываем результат
        autoPanel.add(resultButton);

        JButton resetButton = new JButton("Сброс"); // Кнопка для сброса данных
        resetButton.setBackground(buttonColor);
        resetButton.setForeground(foregroundColor);
        // Обработчик кнопки "Сброс"
        resetButton.addActionListener(e -> visualizer.resetData()); // Сбрасываем данные
        autoPanel.add(resetButton);

        // 2. Панель ручного управления
        JPanel manualPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5)); // Создаем панель для ручного управления
        manualPanel.setBorder(BorderFactory.createTitledBorder(new TitledBorder(null, "Вручную", TitledBorder.LEADING, TitledBorder.TOP, null, foregroundColor))); // Заголовок панели
        manualPanel.setBackground(backgroundColor);
        manualPanel.setForeground(foregroundColor);

        // Кнопки для пошагового прохода
        prevButton = new JButton("<< Назад"); // Кнопка "Назад"
        prevButton.setBackground(buttonColor);
        prevButton.setForeground(foregroundColor);
        // Обработчик кнопки "Назад"
        prevButton.addActionListener(e -> prevStep()); // Переход на предыдущий шаг
        manualPanel.add(prevButton);

        nextButton = new JButton("Вперед >>"); // Кнопка "Вперед"
        nextButton.setBackground(buttonColor);
        nextButton.setForeground(foregroundColor);
        // Обработчик кнопки "Вперед"
        nextButton.addActionListener(e -> nextStep()); // Переход на следующий шаг
        manualPanel.add(nextButton);

        // Метки для отображения информации о шагах
        stepLabel = new JLabel("Шаг: 0"); // Метка для текущего шага
        stepLabel.setForeground(foregroundColor);
        manualPanel.add(stepLabel);

        totalStepsLabel = new JLabel("Всего: 0"); // Метка для общего количества шагов
        totalStepsLabel.setForeground(foregroundColor);
        manualPanel.add(totalStepsLabel);

        // Добавляем обе панели на основную панель
        add(autoPanel); // Добавляем панель автоматического управления
        add(manualPanel); // Добавляем панель ручного управления

    }

    //Переход на предыдущий шаг
    private void prevStep() {
        int step = visualizer.getAlgorithm().getCurrentStep(); // Получаем текущий шаг
        if (step > 0) { // Проверяем, не первый ли это шаг
            visualizer.getAlgorithm().setCurrentStep(step - 1); // Уменьшаем шаг
            updateStepInfo(step - 1, visualizer.getAlgorithm().getSteps().size()); // Обновляем информацию о шагах
            visualizer.getGraphPanel().repaint(); // Перерисовываем граф
            String stepDescription = visualizer.getAlgorithm().getSteps().get(step - 1);
            visualizer.getLogPanel().append(stepDescription + "\n"); // Выводим описание шага в лог
            System.out.println("Шаг назад: " + stepDescription); // Вывод в консоль
        }
    }

    //Переход на следующий шаг
    private void nextStep() {
        int step = visualizer.getAlgorithm().getCurrentStep(); // Получаем текущий шаг
        int totalSteps = visualizer.getAlgorithm().getSteps().size(); // Получаем общее количество шагов
        if (step < totalSteps - 1) { // Проверяем, не последний ли это шаг
            visualizer.getAlgorithm().setCurrentStep(step + 1); // Увеличиваем шаг
            updateStepInfo(step + 1, totalSteps); // Обновляем информацию о шагах
            visualizer.getGraphPanel().repaint(); // Перерисовываем граф
            String stepDescription = visualizer.getAlgorithm().getSteps().get(step + 1);
            visualizer.getLogPanel().append(stepDescription + "\n"); // Выводим описание шага в лог
            System.out.println("Шаг вперед: " + stepDescription); // Вывод в консоль
        }
    }

    //Обновление информации о шагах,current текущий шаг,total общее количество шагов
    public void updateStepInfo(int current, int total) {
        stepLabel.setText("Шаг: " + current); // Обновляем метку текущего шага
        totalStepsLabel.setText("Всего: " + total); // Обновляем метку общего количества шагов
    }

    //Установка состояния работы алгоритма,running true - выполняется, false - остановлен
    public void setRunning(boolean running) {
        isRunning = running; // Устанавливаем флаг выполнения
        startButton.setEnabled(!running); // Включаем/выключаем кнопку "Старт"
        stopButton.setEnabled(running); // Включаем/выключаем кнопку "Стоп"
    }

    //Проверка состояния работы алгоритма,true если алгоритм выполняется
    public boolean isRunning() {
        return isRunning; // Возвращаем состояние выполнения алгоритма
    }

    //Получение задержки между шагами,NumberFormatException если введено некорректное число
    public int getDelay() throws NumberFormatException {
        return Integer.parseInt(delayField.getText()); // Возвращаем значение задержки, преобразованное в число
    }
}