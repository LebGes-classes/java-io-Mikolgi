package org.example;

import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileReader;


public class Main {
    public static void main(String[] args) {
        // Создаем экземпляр журнала
        Journal journal = new Journal();

        // Чтение данных из файла Excel
        journal.readFromExcel("C:\\Users\\Mikolgi\\Desktop\\tests\\journal.xlsx");

        // Преобразование данных в JSON
        JSONObject json = journal.toJson();
        System.out.println("JSON: " + json.toString());

        // Запись JSON в файл
        writeJsonToFile(json, "journal.json");

        // Чтение данных из JSON файла
        JSONObject jsonFromFile = readJsonFromFile("journal.json");

        // Запись данных в файл Excel
        journal.writeToExcelFromJson(jsonFromFile, "new_journal.xlsx");
    }

    // Метод сохранения json
    private static void writeJsonToFile(JSONObject jsonObject, String filePath) {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonObject.toString());
            System.out.println("JSON успешно записан в файл: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Чтение этого файла json
    private static JSONObject readJsonFromFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            StringBuilder jsonString = new StringBuilder();
            int character;
            while ((character = reader.read()) != -1) {
                jsonString.append((char) character);
            }
            return new JSONObject(jsonString.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
