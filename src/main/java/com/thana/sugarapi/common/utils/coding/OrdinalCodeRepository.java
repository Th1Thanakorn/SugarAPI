package com.thana.sugarapi.common.utils.coding;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class OrdinalCodeRepository {

    public static final String PATH = "./config/sugarapi/client/packs/";
    private final String fileName;
    private final String file;

    public OrdinalCodeRepository(String fileName) {
        this.fileName = fileName;
        this.file = PATH + fileName + ".mcfunction";
    }

    public static List<OrdinalCodeRepository> getRepositories() {
        File path = new File(PATH);
        path.mkdirs();
        ArrayList<OrdinalCodeRepository> repositories = new ArrayList<>();
        if (path.exists()) {
            for (File file : path.listFiles()) {
                repositories.add(new OrdinalCodeRepository(file.getName().replaceAll(".mcfunction", "")));
            }
        }
        return repositories;
    }

    public void createRepository() {
        try {
            File path = new File(PATH);
            File file = new File(this.file);
            path.mkdirs();
            file.createNewFile();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public String read() {
        this.createRepository();
        try {
            FileReader reader = new FileReader(this.file);
            StringBuilder builder = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                builder.append((char) c);
            }
            return builder.toString();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return "";
    }

    public String getName() {
        return this.fileName;
    }
}
