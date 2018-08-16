package game;

import java.io.*;
import java.net.*;

public class SpellCheckerService implements SpellChecker {

    private String url = "http://agile.cs.uh.edu/spell?check=";

    @Override
    public boolean isSpellingCorrect(String word) {
        String spellcheck = "";

        try {
            URL spellCheckerUrl = new URL(url + word);
            URLConnection connection = spellCheckerUrl.openConnection();

            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            spellcheck = input.readLine();
            input.close();

        } catch (Exception e) {
            throw new RuntimeException("Network error");
        }

        return spellcheck.equals("true");
    }

    protected void setServiceURL(String newURL) {
        url = newURL;
    }
}
