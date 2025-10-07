import javax.swing.*; // Импортируем библиотеку для работы с графическим интерфейсом
import java.awt.*; // Импортируем библиотеку для работы с графикой
import java.util.List; // Импортируем библиотеку для работы со списками

// Панель для визуализации графа и выполнения алгоритма
public class GraphPanel extends JPanel {
    private final SCCAlgorithm algorithm; // Ссылка на алгоритм
    // Цвета по умолчанию (можно перенести в SCCVisualizer, если нужно переключать тему)
    private final Color foregroundColor = new Color(200, 200, 200); // Светло-серый
    private final Color backgroundColor = new Color(40, 40, 40); // Темно-серый

    // Конструктор панели, algorithm объект алгоритма для визуализации
    public GraphPanel(SCCAlgorithm algorithm) {
        this.algorithm = algorithm; // Сохраняем ссылку на алгоритм
        setPreferredSize(new Dimension(500, 500)); // Устанавливаем размер панели
        setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Устанавливаем рамку панели
        setBackground(backgroundColor); // Устанавливаем темный фон
        setForeground(foregroundColor);
    }

    // Метод отрисовки компонента, g графический контекст
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Очищаем панель перед отрисовкой
        Graphics2D g2d = (Graphics2D) g; // Приводим графический контекст к Graphics2D
        // Включаем сглаживание для более гладких линий
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawGraph(g2d); // Вызываем метод для отрисовки графа
    }

    // Отрисовка графа, g2d графический контекст
    private void drawGraph(Graphics2D g2d) {
        List<List<Integer>> graph = algorithm.getGraph(); // Получаем список смежности графа
        int vertexCount = graph.size(); // Определяем количество вершин
        if (vertexCount == 0) return; // Если граф пустой, ничего не рисуем

        int centerX = getWidth() / 2; // Вычисляем центр по X
        int centerY = getHeight() / 2; // Вычисляем центр по Y
        // Радиус расположения вершин (с отступом от краев)
        int radius = Math.min(centerX, centerY) - 50;

        // Настройки отображения (адаптируемся под количество вершин)
        int vertexSize = vertexCount > 10 ? 20 : 30; // Размер вершины
        Font vertexFont = new Font("Arial", Font.BOLD, vertexCount > 10 ? 10 : 12); // Шрифт для номеров вершин
        g2d.setFont(vertexFont); // Устанавливаем шрифт

        // 1. Сначала рисуем все ребра (чтобы они были под вершинами)
        for (int from = 0; from < vertexCount; from++) { // Проходим по всем вершинам
            // Координаты начальной вершины
            double angle1 = 2 * Math.PI * from / vertexCount; // Вычисляем угол
            int x1 = centerX + (int) (radius * Math.sin(angle1)); // Вычисляем координату X
            int y1 = centerY - (int) (radius * Math.cos(angle1)); // Вычисляем координату Y

            // Рисуем ребра к соседям
            for (int to : graph.get(from)) { // Проходим по соседям текущей вершины
                // Координаты конечной вершины
                double angle2 = 2 * Math.PI * to / vertexCount; // Вычисляем угол
                int x2 = centerX + (int) (radius * Math.sin(angle2)); // Вычисляем координату X
                int y2 = centerY - (int) (radius * Math.cos(angle2)); // Вычисляем координату Y

                setEdgeStyle(g2d, from, to); // Устанавливаем стиль ребра
                drawArrow(g2d, x1, y1, x2, y2); // Рисуем стрелку
            }
        }

        // 2. Затем рисуем вершины поверх ребер
        for (int i = 0; i < vertexCount; i++) { // Проходим по всем вершинам
            // Координаты вершины
            double angle = 2 * Math.PI * i / vertexCount; // Вычисляем угол
            int x = centerX + (int) (radius * Math.sin(angle)); // Вычисляем координату X
            int y = centerY - (int) (radius * Math.cos(angle)); // Вычисляем координату Y

            // Получаем цвет вершины в зависимости от состояния
            Color vertexColor = getVertexColor(i); // Определяем цвет вершины

            // Рисуем вершину (круг)
            g2d.setColor(vertexColor); // Устанавливаем цвет
            g2d.fillOval(x - vertexSize / 2, y - vertexSize / 2, vertexSize, vertexSize); // Рисуем круг
            g2d.setColor(foregroundColor); // Устанавливаем цвет границы
            g2d.drawOval(x - vertexSize / 2, y - vertexSize / 2, vertexSize, vertexSize); // Рисуем границу круга

            // Рисуем номер вершины (центрируем текст)
            FontMetrics fm = g2d.getFontMetrics(); // Получаем метрики шрифта
            String text = Integer.toString(i); // Преобразуем номер вершины в строку
            int textWidth = fm.stringWidth(text); // Вычисляем ширину текста
            g2d.setColor(foregroundColor);
            g2d.drawString(text, x - textWidth / 2, y + fm.getAscent() / 2 - 2); // Рисуем текст
        }
    }

    // Установка стиля ребра (цвет и толщина), g2d графический контекст, from начальная вершина, to конечная
    private void setEdgeStyle(Graphics2D g2d, int from, int to) {
        // Проверяем текущий шаг алгоритма
        int currentStep = algorithm.getCurrentStep(); // Получаем текущий шаг
        if (currentStep < algorithm.getSteps().size()) { // Если шаг в пределах списка шагов
            String step = algorithm.getSteps().get(currentStep); // Получаем текущий шаг
            // Если ребро относится к текущему шагу - выделяем его
            if (step.contains("Переход из " + from + " в " + to) ||
                    step.contains("Посещаем вершину " + from)) {
                g2d.setColor(Color.RED); // Красный для активного ребра
                g2d.setStroke(new BasicStroke(2.5f)); // Увеличиваем толщину линии
                return; // Выходим из метода
            }
        }
        // Стандартный стиль для остальных ребер
        g2d.setColor(Color.GRAY); // Устанавливаем серый цвет
        g2d.setStroke(new BasicStroke(1.5f)); // Устанавливаем стандартную толщину
    }

    // Определение цвета вершины, vertex номер вершины, vertex номер вершины
    private Color getVertexColor(int vertex) {
        // 1. Проверяем текущий шаг алгоритма
        int currentStep = algorithm.getCurrentStep(); // Получаем текущий шаг
        if (currentStep < algorithm.getSteps().size()) { // Если шаг в пределах списка шагов
            String step = algorithm.getSteps().get(currentStep); // Получаем текущий шаг
            if (step.contains("Посещаем вершину " + vertex)) {
                return Color.RED; // Красный - текущая вершина
            } else if (step.contains("Добавляем вершину " + vertex)) {
                return Color.ORANGE; // Оранжевый - вершина в стеке
            }
        }

        // 2. Проверяем принадлежность к компоненте
        for (List<Integer> scc : algorithm.getSCCs()) { // Проходим по компонентам связности
            if (scc.contains(vertex)) { // Если вершина в компоненте
                // Разные пастельные цвета для разных компонент
                int index = algorithm.getSCCs().indexOf(scc) % 6; // Индекс компоненты
                return new Color[]{ // Возвращаем цвет в зависимости от индекса
                        new Color(100, 149, 237), // Светло-голубой
                        new Color(152, 251, 152), // Светло-зеленый
                        new Color(255, 182, 193), // Светло-розовый
                        new Color(255, 255, 153), // Светло-желтый
                        new Color(218, 112, 214), // Светло-фиолетовый
                        new Color(255, 160, 122)  // Светло-коралловый
                }[index]; // Возвращаем цвет
            }
        }

        return Color.WHITE; // Белый - вершина не обработана
    }

    /**
     * Рисование стрелки от (x1,y1) к (x2,y2)
     *
     * @param g2d графический контекст
     * @param x1  координата X начальной точки
     * @param y1  координата Y начальной точки
     * @param x2  координата X конечной точки
     * @param y2  координата Y конечной точки
     */
    private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        // Вычисляем угол наклона линии
        double angle = Math.atan2(y2 - y1, x2 - x1); // Вычисляем угол между точками
        int arrowSize = 10; // Размер наконечника стрелки
        int vertexSize = algorithm.getGraph().size() > 10 ? 20 : 30; // Размер вершины

        // Корректируем конечную точку (чтобы стрелка не накладывалась на вершину)
        x2 = (int) (x2 - (double) vertexSize / 2 * Math.cos(angle)); // Корректируем X
        y2 = (int) (y2 - (double) vertexSize / 2 * Math.sin(angle)); // Корректируем Y

        g2d.drawLine(x1, y1, x2, y2); // Рисуем линию от начальной до конечной точки

        // Рисуем наконечник стрелки (две линии под углом)
        int x3 = (int) (x2 - arrowSize * Math.cos(angle - Math.PI / 6)); // Координата X первой линии
        int y3 = (int) (y2 - arrowSize * Math.sin(angle - Math.PI / 6)); // Координата Y первой линии
        int x4 = (int) (x2 - arrowSize * Math.cos(angle + Math.PI / 6)); // Координата X второй линии
        int y4 = (int) (y2 - arrowSize * Math.sin(angle + Math.PI / 6)); // Координата Y второй линии

        g2d.drawLine(x2, y2, x3, y3); // Рисуем первую линию наконечника
        g2d.drawLine(x2, y2, x4, y4); // Рисуем вторую линию наконечника
    }
}