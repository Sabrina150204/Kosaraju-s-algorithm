import java.io.Serializable;
import java.util.*;

// Класс, реализующий алгоритм поиска компонент сильной связности (Косарайю)
public class SCCAlgorithm implements Serializable { // Реализуем Serializable для возможности экспорта/импорта графа
    private List<List<Integer>> graph; // Исходный граф в виде списка смежности
    private List<List<Integer>> reversedGraph; // Транспонированный граф
    private List<String> steps; // Список шагов алгоритма для визуализации
    private List<List<Integer>> sccs; // Найденные компоненты сильной связности
    private boolean[] visited; // Массив посещенных вершин
    private Stack<Integer> stack; // Стек для порядка обработки вершин
    private int currentStep; // Текущий шаг визуализации
    private int executionPosition; // Текущая позиция выполнения алгоритма

    // Конструктор класса
    public SCCAlgorithm() {
        reset(); // Инициализируем все поля
    }

    // Сброс состояния алгоритма
    public void reset() {
        graph = new ArrayList<>(); // Очищаем граф
        reversedGraph = new ArrayList<>(); // Очищаем транспонированный граф
        steps = new ArrayList<>(); // Очищаем историю шагов
        sccs = new ArrayList<>(); // Очищаем результаты
        visited = new boolean[0]; // Сбрасываем массив посещений
        stack = new Stack<>(); // Очищаем стек
        currentStep = 0; // Сбрасываем счетчик шагов
        executionPosition = 0; // Сбрасываем позицию выполнения
    }

    // Генерация случайного графа, vertexCount количество вершин в графе
    public void generateRandomGraph(int vertexCount) {
        graph = new ArrayList<>(); // Создаем новый граф
        Random random = new Random(); // Генератор случайных чисел

        // Создаем вершины графа
        for (int i = 0; i < vertexCount; i++) {
            graph.add(new ArrayList<>()); // Добавляем пустой список смежности

            // Генерируем случайное количество ребер (не более половины вершин + 1)
            int edgesCount = random.nextInt(vertexCount / 2 + 1);

            // Добавляем случайные ребра
            for (int j = 0; j < edgesCount; j++) {
                int to = random.nextInt(vertexCount); // Случайная вершина назначения
                // Проверяем, чтобы не было петли и дубликатов ребер
                if (to != i && !graph.get(i).contains(to)) {
                    graph.get(i).add(to); // Добавляем ребро
                }
            }
        }
    }

    // Основной метод поиска компонент сильной связности, Поддерживает продолжение выполнения после остановки
    public void findSCCs() {
        // Если выполнение только начинается - инициализируем данные
        if (executionPosition == 0) {
            steps.clear(); // Очищаем предыдущие шаги
            sccs.clear(); // Очищаем предыдущие результаты
            int V = graph.size(); // Количество вершин
            visited = new boolean[V]; // Массив посещений
            stack = new Stack<>(); // Инициализируем стек
            steps.add("Начало первого прохода DFS"); // Добавляем первый шаг
        }

        // Выполняем алгоритм по этапам
        switch (executionPosition) {
            case 0: // Первый проход DFS (заполнение стека)
                for (int i = 0; i < graph.size(); i++) {
                    if (!visited[i]) { // Если вершина не посещена
                        dfs(i); // Выполняем поиск в глубину
                    }
                }
                executionPosition++; // Переходим к следующему этапу
                // Намеренно отсутствует break для перехода к следующему case

            case 1: // Построение обратного графа
                steps.add("Построение обращенного графа"); // Логируем шаг
                reversedGraph = new ArrayList<>(graph.size()); // Инициализируем обратный граф
                // Заполняем пустыми списками
                for (int i = 0; i < graph.size(); i++) {
                    reversedGraph.add(new ArrayList<>()); // Добавляем пустой список для каждой вершины
                }

                // Транспонируем граф (разворачиваем ребра)
                for (int i = 0; i < graph.size(); i++) {
                    for (int j : graph.get(i)) {
                        reversedGraph.get(j).add(i); // Добавляем обратное ребро
                    }
                }
                executionPosition++; // Переходим к следующему этапу
                // Намеренно отсутствует break для перехода к следующему case

            case 2: // Второй проход DFS (поиск компонент)
                steps.add("Начало второго прохода DFS"); // Логируем шаг
                visited = new boolean[graph.size()]; // Сбрасываем массив посещений
                // Обрабатываем вершины в порядке стека
                while (!stack.isEmpty()) {
                    int v = stack.pop(); // Берем вершину из стека
                    if (!visited[v]) { // Если не посещена
                        List<Integer> scc = new ArrayList<>(); // Создаем новую компоненту
                        dfsReversed(v, scc); // Обходим в обратном графе
                        sccs.add(scc); // Добавляем найденную компоненту
                        steps.add("Найден компонент: " + scc); // Логируем
                    }
                }
                executionPosition = 0; // Алгоритм завершен
                break; // Завершаем выполнение метода
        }
    }

    // Поиск в глубину для первого прохода, v текущая вершина
    private void dfs(int v) {
        visited[v] = true; // Помечаем вершину как посещенную
        steps.add("Посещаем вершину " + v); // Логируем шаг

        // Обходим всех соседей
        for (int i : graph.get(v)) {
            if (!visited[i]) { // Если сосед не посещен
                steps.add("Переход из " + v + " в " + i); // Логируем переход
                dfs(i); // Рекурсивно обрабатываем соседа
            }
        }

        stack.push(v); // Добавляем вершину в стек после обработки всех соседей
        steps.add("Добавляем вершину " + v + " в стек"); // Логируем шаг
    }

    // Поиск в глубину для второго прохода (в обращенном графе), scc текущая компонента сильной связности
    private void dfsReversed(int v, List<Integer> scc) {
        visited[v] = true; // Помечаем вершину как посещенную
        scc.add(v); // Добавляем вершину в компоненту

        // Обходим соседей в обращенном графе
        for (int i : reversedGraph.get(v)) {
            if (!visited[i]) { // Если сосед не посещен
                dfsReversed(i, scc); // Рекурсивно обрабатываем
            }
        }
    }

    // Геттеры и сеттеры
    public List<List<Integer>> getGraph() {
        return graph;
    } // Возвращает граф

    public void setGraph(List<List<Integer>> graph) {
        this.graph = graph;
    } // Устанавливает граф

    public List<String> getSteps() {
        return steps;
    } // Возвращает шаги

    public List<List<Integer>> getSCCs() {
        return sccs;
    } // Возвращает найденные компоненты

    public int getCurrentStep() {
        return currentStep;
    } // Возвращает текущий шаг

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    } // Устанавливает текущий шаг

    public int getExecutionPosition() {
        return executionPosition;
    } // Возвращает позицию выполнения

    public void setExecutionPosition(int position) {
        this.executionPosition = position;
    } // Устанавливает позицию выполнения
}