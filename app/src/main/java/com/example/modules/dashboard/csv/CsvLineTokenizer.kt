package com.example.modules.dashboard.csv

object CsvLineTokenizer {
    fun detectDelimiter(line: String): Char {
        val commaCount = line.count { it == ',' }
        val semiCount = line.count { it == ';' }
        val tabCount = line.count { it == '\t' }
        return when {
            semiCount > commaCount && semiCount > tabCount -> ';'
            tabCount > commaCount && tabCount > semiCount -> '\t'
            else -> ','
        }
    }

    fun tokenize(line: String, delimiter: Char): List<String> {
        val result = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        for (ch in line) {
            when {
                ch == '\"' -> inQuotes = !inQuotes
                ch == delimiter && !inQuotes -> {
                    result.add(sb.toString().trim())
                    sb.clear()
                }
                else -> sb.append(ch)
            }
        }
        result.add(sb.toString().trim())
        return result
    }
}
