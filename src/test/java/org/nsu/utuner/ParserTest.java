package org.nsu.utuner;

import org.junit.Test;

import java.io.*;
import java.util.Map;

import static org.junit.Assert.*;

public class ParserTest {

    @Test
    public void testParseStream() throws Exception {
        String[] correctModes = {"AI", "Opt", "Prof"};

        for(int i = 0; i < 1000; i++){
            for(String mode : correctModes){
                InputStream input = createTestStream(mode, i);
                testStream(input, mode, i);
                input.close();
            }
        }
    }

    @Test(expected = ParseFileException.class)
    public void testParseModeException1() throws Exception{
        InputStream input = createTestStream("Incorrect", 1000);
        Map<String, String> map = Parser.parseStream(input);
        input.close();
    }

    @Test(expected = ParseFileException.class)
    public void testParseModeException2() throws Exception{
        InputStream input = createTestStream(null, 1000);
        Map<String, String> map = Parser.parseStream(input);
    }

    private InputStream createTestStream(String mode, int n) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(outputStream));

        String property;
        if(mode != null){
            property = "Mode=" + mode;
            output.write(property);
            output.newLine();
        }

        for(int i = 0; i < n; ++i){
            property = "Property" + i + "=Value" + i;
            output.write(property);
            output.newLine();
        }
        output.close();
        outputStream.close();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private void testStream(InputStream input, String mode, int n){
        boolean exception = false;
        try {
            Map<String, String> map = Parser.parseStream(input);

            String value = map.get("Mode");
            assertEquals("Incorrect \"Mode\" parameter", mode, value);

            for(int i = 0; i < n; ++i){
                value = map.get("Property" + i);
                assertEquals("Incorrect parameter", "Value" + i, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exception = true;
        }
        assertFalse("Incorrect exception", exception);
    }
}