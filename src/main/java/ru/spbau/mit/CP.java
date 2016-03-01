package main.java.ru.spbau.mit;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class CP {

    public static void copyFile(String fromFileName, String toFileName, int bufferSize) {
        File fileFrom = new File(fromFileName);
        File fileTo = new File(toFileName);
        if (!fileFrom.exists()) {
            System.out.println("File " + fromFileName + " does not exists");
            return;
        }
        if (fileTo.exists()) {
            System.out.println("File " + toFileName + " already exists");
            return;
        }
        File parentDir = fileTo.getParentFile();
        if (parentDir != null && parentDir.mkdirs()) {
            System.out.println("Can't create dirictories to " + toFileName + "");
            return;
        }
        byte[] buffer = new byte[bufferSize];
        try {
            RandomAccessFile fileFromRead = new RandomAccessFile(fileFrom, "r");
            RandomAccessFile fileToWrite = new RandomAccessFile(fileTo, "rw");
            do {
                int readSize = fileFromRead.read(buffer);
                if (readSize > 0) {
                    fileToWrite.write(buffer, 0, readSize);
                } else {
                    break;
                }
            } while (true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static final int BUF_SIZE = 4096;

    private CP() { }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Not enough arguments");
            return;
        }
        String fromFileName = args[0];
        String toFileName = args[1];
        copyFile(fromFileName, toFileName, BUF_SIZE);
    }
}
