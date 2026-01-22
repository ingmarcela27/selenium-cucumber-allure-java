package config;

import java.io.FileOutputStream;
import java.util.Properties;

public class AllureEnvironmentWriter {

    public static void write() {
        try {
            Properties props = new Properties();
            props.setProperty("Browser", "Chrome");
            props.setProperty("Java", System.getProperty("java.version"));
            props.setProperty("OS", System.getProperty("os.name"));
            props.setProperty("Environment", System.getProperty("env", "qa"));
            props.setProperty("Headless", System.getProperty("headless", "true"));

            FileOutputStream out =
                    new FileOutputStream("build/allure-results/environment.properties");

            props.store(out, "Allure Environment");
            out.close();

        } catch (Exception e) {
            throw new RuntimeException("Failed to write Allure environment", e);
        }
    }
}
