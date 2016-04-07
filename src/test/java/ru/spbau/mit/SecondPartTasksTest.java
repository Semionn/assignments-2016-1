package ru.spbau.mit;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.fail;
import static junitx.framework.Assert.assertEquals;
import static junitx.framework.Assert.assertTrue;
import static ru.spbau.mit.SecondPartTasks.*;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() {
        try {
            File file0 = File.createTempFile(FILE_NAME_0, FILE_EXTENSION);
            Files.write(file0.toPath(),
                    Arrays.asList(LINE_0, LINE_1, LINE_2));

            File file1 = File.createTempFile(FILE_NAME_1, FILE_EXTENSION);
            Files.write(file1.toPath(),
                    Arrays.asList(LINE_2, LINE_3, LINE_4, LINE_5));

            assertEquals(Arrays.asList(LINE_0, LINE_2, LINE_2, LINE_3),
                    findQuotes(
                            Arrays.asList(file0.getPath(), file1.getPath()),
                            QUOTE_0));

            assertEquals(Arrays.asList(LINE_1, LINE_2, LINE_2, LINE_4),
                    findQuotes(
                            Arrays.asList(file0.getPath(), file1.getPath()),
                            QUOTE_1));
        } catch (IOException e) {
            e.printStackTrace();
            fail("File creation failed");
        }
    }

    @Test
    public void testPiDividedBy4() {
        final double eps = 1e-3;
        System.out.println(piDividedBy4());
        assertTrue(Math.abs(piDividedBy4() - Math.PI / 4) < eps);
    }

    @Test
    public void testFindPrinter() {
        assertEquals(AUTHOR_2,
                findPrinter(ImmutableMap.of(
                        AUTHOR_0, COMPOSITIONS_0,
                        AUTHOR_2, COMPOSITIONS_2,
                        AUTHOR_1, COMPOSITIONS_1)));
        assertEquals(AUTHOR_1,
                findPrinter(ImmutableMap.of(
                        AUTHOR_0, COMPOSITIONS_0,
                        AUTHOR_1, COMPOSITIONS_1)));
    }

    @Test
    public void testCalculateGlobalOrder() {
        assertEquals(ImmutableMap.of(
                        PRODUCT_0, 1,
                        PRODUCT_1, 7,
                        PRODUCT_2, 8
                ),
                calculateGlobalOrder(
                        Arrays.asList(
                                ImmutableMap.of(
                                        PRODUCT_0, 1,
                                        PRODUCT_1, 2),
                                ImmutableMap.of(
                                        PRODUCT_1, 2,
                                        PRODUCT_2, 3
                                ),
                                ImmutableMap.of(
                                        PRODUCT_1, 3,
                                        PRODUCT_2, 5
                                ))));
    }

    // CHECKSTYLE:OFF

    // testFindQuotes
    private static final String FILE_NAME_0 = "temp0";
    private static final String FILE_NAME_1 = "temp1";
    private static final String FILE_EXTENSION = ".txt";
    private static final String LINE_0 = "Hello world!";
    private static final String LINE_1 = "Some test line";
    private static final String LINE_2 = "Test line with quote 'world'";
    private static final String LINE_3 = "world in fire!";
    private static final String LINE_4 = "Thetextlinearization";
    private static final String LINE_5 = "Plain text with no quotes";
    private static final String QUOTE_0 = "world";
    private static final String QUOTE_1 = "line";

    // testFindPrinter
    private static final String AUTHOR_0 = "AUTHOR_0";
    private static final String AUTHOR_1 = "AUTHOR_1";
    private static final String AUTHOR_2 = "AUTHOR_2";
    private static final List<String> COMPOSITIONS_0 = Collections.emptyList();
    private static final List<String> COMPOSITIONS_1 = Arrays.asList("Text1", "LongText1", "NotShortText1");
    private static final List<String> COMPOSITIONS_2 = Arrays.asList("Text2", "LongText2", "VeryVeryLongText2");

    // testCalculateGlobalOrder
    private static final String PRODUCT_0 = "PRODUCT_0";
    private static final String PRODUCT_1 = "PRODUCT_1";
    private static final String PRODUCT_2 = "PRODUCT_2";

    // CHECKSTYLE:ON
}
