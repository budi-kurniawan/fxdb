package com.brainysoftware.fxdb.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Budi Kurniawan (http://brainysoftware.com)
 * 
 */
public class SQLScriptService {
    private static final String SAVED_SQL_PATH = "./data/saved-sql";
    public List<String> getSavedSQLFileNames() {
        List<String> fileNames = new ArrayList<>();
        Path path = Paths.get(SAVED_SQL_PATH);
        try {
            Files.list(path).sorted().forEach(file -> {
                String fileName = file.getFileName().toString();
                // remove extension
                if (fileName.endsWith(".sql")) {
                    fileNames.add(fileName.substring(0, fileName.length() - 4));
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(SQLScriptService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fileNames;
    }
    
    public void saveSQL(String name, String sql) {
        Path path = Paths.get(SAVED_SQL_PATH, name.trim() + ".sql");
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(sql);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getSQL(String fileName) {
        Path path = Paths.get(SAVED_SQL_PATH, fileName + ".sql");
        StringBuilder lines = new StringBuilder();
        try {
            Files.lines(path).forEach(line -> lines.append(line + "\n"));
        } catch (IOException ex) {
            Logger.getLogger(SQLScriptService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lines.toString();
    }
    
    public void deleteSQLScript(String name) {
        Path path = Paths.get(SAVED_SQL_PATH, name.trim() + ".sql");
        if (Files.exists(path)) {
            try {
                Files.delete(path);
            } catch (IOException ex) {
                Logger.getLogger(SQLScriptService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}