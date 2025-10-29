package com.datawhisperer;

/**
 * Translates natural language queries to SQL.
 * Currently implements a basic rule-based approach.
 * Future enhancement: Integrate with LLM APIs for advanced translation.
 */
public class QueryTranslator {

    /**
     * Converts natural language query to SQL.
     * @param nlQuery natural language input
     * @return SQL query string
     */
    public String convertNLtoSQL(String nlQuery) {
        // Placeholder: Basic rule-based translation
        // TODO: Integrate LLM API here for more sophisticated NL to SQL conversion
        // Example: Use OpenAI API or similar to generate SQL from natural language
        // For now, simple keyword matching

        System.out.println("Translating NL query: " + nlQuery);
        String lower = nlQuery.toLowerCase().trim();
        String sql;
        if (lower.contains("show all students") && lower.contains("marks above 80")) {
            sql = "SELECT s.id, s.name, s.age, AVG(m.marks) as marks FROM students s JOIN marks m ON s.id = m.student_id WHERE m.marks > 80 GROUP BY s.id, s.name, s.age;";
        } else if (lower.contains("show all students") || lower.contains("list all students")) {
            sql = "SELECT * FROM students;";
        } else if (lower.contains("students with high marks")) {
            sql = "SELECT * FROM students WHERE marks > 90;";
        } else if (lower.contains("average marks")) {
            sql = "SELECT AVG(marks) FROM students;";
        } else {
            // Default fallback
            sql = "SELECT * FROM students;";
        }
        System.out.println("Generated SQL: " + sql);
        return sql;
    }
}