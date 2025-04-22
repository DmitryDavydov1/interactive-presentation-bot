package com.example.bot._for_shelter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;


import com.example.bot._for_shelter.command.room.SendStatisticCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class CustomWordCloud {
    // Параметры облака слов
    private static final int WIDTH = 1600;
    private static final int HEIGHT = 1000;
    private static final int MIN_FONT_SIZE = 10;
    private static final int MAX_FONT_SIZE = 200;
    private static final double BASE_SPIRAL_STEP = 2.0;
    private static final double BASE_SPIRAL_ANGLE_STEP = Math.PI / 60;
    private static final double FONT_REDUCTION_FACTOR = 0.9;
    private static final double FONT_INCREASE_FACTOR = 2.0;
    private static final double PULL_FACTOR = 0.3;
    private static final int PADDING = 2;
    private static final Logger logger = LoggerFactory.getLogger(CustomWordCloud.class);

    private static final Color[] COLOR_PALETTE = {
            new Color(0, 128, 255),    // Яркий синий (как "VISUALIZATION")
            new Color(255, 77, 77),    // Яркий красный (как "illustrate")
            new Color(0, 179, 136),    // Зеленый (как "interesting")
            new Color(153, 102, 204),  // Фиолетовый (как "easy")
            new Color(255, 147, 0),    // Оранжевый (как "attractive")
            new Color(255, 102, 153),  // Розовый (как "audience")
            new Color(0, 204, 204),    // Голубой (как "patterns")
            new Color(51, 51, 153),    // Темно-синий (как "powerful")
            new Color(255, 204, 0),    // Желтый (дополнительный для разнообразия)
            new Color(102, 204, 102)   // Светло-зеленый (дополнительный для разнообразия)
    };


    static class Word {
        String text;
        int frequency;
        int fontSize;
        Rectangle bounds;
        boolean isVertical;

        Word(String text, int frequency) {
            this.text = text;
            this.frequency = frequency;
        }
    }

    public ByteArrayOutputStream generateAndSendWordCloud(Map<String, Integer> wordFreq) throws IOException {
        // Проверка входного словаря
        if (wordFreq == null || wordFreq.isEmpty()) {
            System.err.println("Словарь пуст или равен null!");
            return null;
        }

        // Статистика
        int totalWords = wordFreq.values().stream().mapToInt(Integer::intValue).sum();
        int uniqueWords = wordFreq.size();
        double averageFreq = totalWords / (double) uniqueWords;
        int maxFreq = wordFreq.values().stream().max(Integer::compare).orElse(1);
        int minFreq = wordFreq.values().stream().min(Integer::compare).orElse(0);

//        wordFreq.entrySet().stream()
//                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
//                .limit(5)
//                .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));

        // Преобразуем в список слов
        List<Word> words = new ArrayList<>();
        int wordCount = wordFreq.size();
        for (Map.Entry<String, Integer> entry : wordFreq.entrySet()) {
            String wordText = entry.getKey().toLowerCase();
            if (!wordText.isEmpty()) { // Игнорируем короткие слова
                Word word = new Word(wordText, entry.getValue());
                word.fontSize = MIN_FONT_SIZE + (int) ((double) entry.getValue() / maxFreq * (MAX_FONT_SIZE - MIN_FONT_SIZE));
                words.add(word);
            }
        }

        // Сортируем слова по частоте
        words.sort((w1, w2) -> Integer.compare(w2.frequency, w1.frequency));


        // Адаптация параметров
        double spiralStep = BASE_SPIRAL_STEP;
        double spiralAngleStep = BASE_SPIRAL_ANGLE_STEP;
        if (wordCount > 50) {
            spiralStep *= 0.3;
            spiralAngleStep *= 0.3;
            System.out.println("Много слов (" + wordCount + "): уменьшен шаг спирали до " + spiralStep);
        }

        // Создаём изображение
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        List<Rectangle> occupied = new ArrayList<>();
        Random random = new Random();
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;

        // Размещение первого слова в центре
        if (words.isEmpty()) {
            System.err.println("Нет слов для генерации облака после фильтрации!");
            return null;
        }

        Word firstWord = words.get(0);
        firstWord.isVertical = false;
        Font font = new Font("Arial", Font.PLAIN, firstWord.fontSize);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int wordWidth = fm.stringWidth(firstWord.text);
        int wordHeight = fm.getHeight();
        Rectangle firstRect = new Rectangle(centerX - wordWidth / 2, centerY - wordHeight / 2, wordWidth, wordHeight);
        firstWord.bounds = firstRect;
        occupied.add(firstRect);
        g2d.setColor(COLOR_PALETTE[random.nextInt(COLOR_PALETTE.length)]);
        g2d.drawString(firstWord.text, centerX - wordWidth / 2, centerY + wordHeight / 4);

        // Размещение остальных слов
        for (int i = 1; i < words.size(); i++) {
            Word word = words.get(i);
            boolean placed = false;
            int fontSize = word.fontSize;

            while (!placed) {
                try {
                    font = new Font("Arial", Font.PLAIN, fontSize);
                } catch (Exception e) {
                    font = new Font("SansSerif", Font.PLAIN, fontSize);
                }
                g2d.setFont(font);
                fm = g2d.getFontMetrics();
                wordWidth = fm.stringWidth(word.text);
                wordHeight = fm.getHeight();

                word.isVertical = random.nextInt(100) < 20;
                int w = word.isVertical ? wordHeight : wordWidth;
                int h = word.isVertical ? wordWidth : wordHeight;

                double bestDistance = Double.MAX_VALUE;
                int bestX = centerX;
                int bestY = centerY;
                boolean foundPosition = false;

                double radius = 0.0;
                double angle = random.nextDouble() * 2 * Math.PI;

                while (radius < Math.max(WIDTH, HEIGHT)) {
                    int x = centerX + (int) (radius * Math.cos(angle));
                    int y = centerY + (int) (radius * Math.sin(angle));

                    if (x - w / 2 < 0 || x + w / 2 > WIDTH || y - h / 2 < 0 || y + h / 2 > HEIGHT) {
                        radius += spiralStep;
                        angle += spiralAngleStep;
                        continue;
                    }

                    Rectangle rect = new Rectangle(x - w / 2, y - h / 2, w + PADDING, h + PADDING);
                    boolean collision = false;

                    for (Rectangle occ : occupied) {
                        if (rect.intersects(occ)) {
                            collision = true;
                            break;
                        }
                    }


                    if (!collision) {
                        double minDistToOccupied = Double.MAX_VALUE;
                        for (Rectangle occ : occupied) {
                            double dist = Math.sqrt(Math.pow(x - (occ.x + occ.width / 2), 2) +
                                    Math.pow(y - (occ.y + occ.height / 2), 2));
                            minDistToOccupied = Math.min(minDistToOccupied, dist);
                        }

                        double distToCenter = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                        double score = minDistToOccupied + PULL_FACTOR * distToCenter;

                        if (score < bestDistance) {
                            bestDistance = score;
                            bestX = x;
                            bestY = y;
                            foundPosition = true;
                        }
                    }

                    radius += spiralStep;
                    angle += spiralAngleStep;

                    if (foundPosition && radius > 50) break;
                }

                if (foundPosition) {
                    Rectangle rect = new Rectangle(bestX - w / 2, bestY - h / 2, w + PADDING, h + PADDING);
                    word.bounds = rect;
                    word.fontSize = fontSize;
                    occupied.add(rect);
                    g2d.setColor(COLOR_PALETTE[random.nextInt(COLOR_PALETTE.length)]);

                    if (word.isVertical) {
                        g2d.translate(bestX, bestY);
                        g2d.rotate(Math.PI / 2);
                        g2d.drawString(word.text, -wordWidth / 2, wordHeight / 2);
                        g2d.rotate(-Math.PI / 2);
                        g2d.translate(-bestX, -bestY);
                    } else {
                        g2d.drawString(word.text, bestX - wordWidth / 2, bestY + wordHeight / 4);
                    }
                    placed = true;
                }

                if (!placed) {
                    fontSize = (int) (fontSize * FONT_REDUCTION_FACTOR);
                    if (fontSize < MIN_FONT_SIZE) {
                        fontSize = MIN_FONT_SIZE;
                        spiralStep *= 0.7;
                        spiralAngleStep *= 0.7;
                        System.out.println("Уменьшен шаг спирали до: " + spiralStep + " для слова: " + word.text);
                    }
                    word.fontSize = fontSize;
                }
            }
        }

        g2d.dispose();

        logger.info("Облако слов генерируется");
        logger.info("Общее количество слов (с учётом частот): {}", totalWords);
        logger.info("Количество уникальных слов: {}", uniqueWords);
        logger.info("Средняя частота слова: {}", String.format("%.2f", averageFreq));
        logger.info("Максимальная частота: {}", maxFreq);
        logger.info("Минимальная частота: {}", minFreq);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        logger.info("Облако слов успешно сгенерировано");
        return baos;

    }
}
