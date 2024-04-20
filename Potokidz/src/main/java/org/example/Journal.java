package org.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Journal {
    private List<Student> students;
    private List<Professor> professors;
    private List<Subject> subjects;

    public Journal() {
        this.students = new ArrayList<>();
        this.professors = new ArrayList<>();
        this.subjects = new ArrayList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void addProfessor(Professor professor) {
        professors.add(professor);
    }

    public void addSubject(Subject subject) {
        subjects.add(subject);
    }

    // Метод для чтения данных из файла Excel (в нашем случае изначальный файл)
    public void readFromExcel(String filePath) {
        try {
            FileInputStream excelFile = new FileInputStream(new File(filePath));
            Workbook workbook = new XSSFWorkbook(excelFile);

            // О студентах читаем, т.е первый лист
            Sheet studentsSheet = workbook.getSheetAt(0);
            for (int i = 0; i < studentsSheet.getLastRowNum(); i++) {
                Row studentNameRow = studentsSheet.getRow(i);
                Row subjectRow = studentsSheet.getRow(i );
                Row markRow = studentsSheet.getRow(i);

                String studentName = studentNameRow.getCell(0).getStringCellValue();
                Map<Subject, Integer> marksMap = new HashMap<>();
                String subjectName = subjectRow.getCell(1).getStringCellValue();
                int mark = (int) markRow.getCell(2).getNumericCellValue();
                Subject subject = new Subject(subjectName);
                marksMap.put(subject, mark);

                Student student = new Student(studentName, marksMap);
                addStudent(student);


            }

            // Тут о профессорах, т.е второй лист
            Sheet professorsSheet = workbook.getSheetAt(1);
            for (int i = 0; i < professorsSheet.getLastRowNum(); i++) {
                Row professorNameRow = professorsSheet.getRow(i);
                Row subjectRow = professorsSheet.getRow(i);

                String professorName = professorNameRow.getCell(0).getStringCellValue();
                String subjectName = subjectRow.getCell(1).getStringCellValue();
                Subject subject = new Subject(subjectName);
                Professor professor = new Professor(professorName, subject);
                addProfessor(professor);

            }

            workbook.close();
            excelFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для преобразования данных в JSON
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        JSONArray studentsArray = new JSONArray();
        JSONArray professorsArray = new JSONArray();
        JSONArray subjectsArray = new JSONArray();

        for (Student student : students) {
            JSONObject studentObject = new JSONObject();
            studentObject.put("name", student.getName());

            JSONArray marksArray = new JSONArray();
            for (Map.Entry<Subject, Integer> entry : student.getMarks().entrySet()) {
                JSONObject markObject = new JSONObject();
                markObject.put("subject", entry.getKey().getName());
                markObject.put("mark", entry.getValue());
                marksArray.put(markObject);
            }
            studentObject.put("marks", marksArray);

            studentsArray.put(studentObject);
        }

        for (Professor professor : professors) {
            JSONObject professorObject = new JSONObject();
            professorObject.put("name", professor.getName());
            professorObject.put("subject", professor.getSubject().getName());
            professorsArray.put(professorObject);
        }

        for (Subject subject : subjects) {
            JSONObject subjectObject = new JSONObject();
            subjectObject.put("name", subject.getName());
            subjectsArray.put(subjectObject);
        }

        jsonObject.put("students", studentsArray);
        jsonObject.put("professors", professorsArray);
        jsonObject.put("subjects", subjectsArray);

        return jsonObject;
    }



    // Создаем новую таблицу с данными из Json
    public void writeToExcelFromJson(JSONObject json, String filePath) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet studentsSheet = workbook.createSheet("Students");
            Sheet professorsSheet = workbook.createSheet("Professors");
            //Закидываем в лист со Студентами
            JSONArray studentsArray = json.getJSONArray("students");
            int rowCount = 0;
            for (int i = 0; i < studentsArray.length(); i++) {
                JSONObject studentObject = studentsArray.getJSONObject(i);
                String name = studentObject.getString("name");

                JSONArray marksArray = studentObject.getJSONArray("marks");
                Row row = studentsSheet.createRow(rowCount++);
                int columnCount = 0;
                row.createCell(columnCount++).setCellValue(name);

                for (int j = 0; j < marksArray.length(); j++) {
                    JSONObject markObject = marksArray.getJSONObject(j);
                    String subjectName = markObject.getString("subject");
                    int mark = markObject.getInt("mark");

                    row.createCell(columnCount++).setCellValue(subjectName);
                    row.createCell(columnCount++).setCellValue(mark);
                }
            }
            //Закидываем в лист с Профессорами
            JSONArray professorsArray = json.getJSONArray("professors");
            rowCount = 0;
            for (int i = 0; i < professorsArray.length(); i++) {
                JSONObject professorObject = professorsArray.getJSONObject(i);
                String name = professorObject.getString("name");
                String subject = professorObject.getString("subject");

                Row row = professorsSheet.createRow(rowCount++);
                int columnCount = 0;
                row.createCell(columnCount++).setCellValue(name);
                row.createCell(columnCount++).setCellValue(subject);
            }

            FileOutputStream outputStream = new FileOutputStream(filePath);
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            System.out.println("Данные успешно записаны в файл Excel: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
