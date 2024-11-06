package hangman;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Hangman {
    private JPanel hangmanView;
    private JTextField guessLetter;
    private JLabel secretWord;
    private JLabel guessesLeft;
    private JLabel imgHere;
    private JTextPane history;
    private JLabel messages;

    private int attempts = 0;
    private final int maxAttempts = 10;
    private String wordToGuess;
    private List<Character> guessedLetters = new ArrayList<>();
    private List<String> wordList = new ArrayList<>();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hangman");
        frame.setContentPane(new Hangman().hangmanView);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 450);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public Hangman() {
        loadWordsFromFile();
        initializeGame();

        guessLetter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = guessLetter.getText().toLowerCase();
                if (input.length() == 1 && Character.isLetter(input.charAt(0))) {
                    processGuess(input.charAt(0));
                    guessLetter.setText("");
                } else {
                    updateMessage("Bitte einen gültigen Buchstaben eingeben.");
                }
            }
        });
    }

    // Initialisiert das Spiel und setzt alles zurück
    private void initializeGame() {
        attempts = 0;
        wordToGuess = getRandomWord();
        guessedLetters.clear();

        // Erstellt eine Zeichenkette mit Unterstrichen für das geheime Wort
        StringBuilder displayedWordBuilder = new StringBuilder();
        for (int i = 0; i < wordToGuess.length(); i++) {
            displayedWordBuilder.append("_ ");
        }
        String displayedWord = displayedWordBuilder.toString().trim();

        // Setzt alles zu den Anfangszustand des Spiels (zurück)
        secretWord.setText(displayedWord);
        guessesLeft.setText("Versuche übrig: " + (maxAttempts - attempts));
        history.setText("Bisherige Buchstaben: ");
        messages.setText("");
        imgHere.setIcon(new ImageIcon("src/images/hangman0.png"));
        history.setEditable(false);
    }

    // Wählt ein zufälliges Wort aus der Wortliste aus
    private String getRandomWord() {
        if (wordList.isEmpty()) {
            return "default"; // Fallback word if list is empty
        }
        Random rand = new Random();
        return wordList.get(rand.nextInt(wordList.size()));

    }

    // Verarbeitet den eingegebenen Buchstaben
    private void processGuess(char guessedChar) {
        if (guessedLetters.contains(guessedChar)) {
            updateMessage("Buchstabe wurde schon geraten!");
            return;
        }

        guessedLetters.add(guessedChar);
        boolean correctGuess = false;
        // Aktualisiert das versteckte Wort, wenn der Buchstabe korrekt ist
        StringBuilder updatedWordBuilder = new StringBuilder(secretWord.getText().replace(" ", ""));

        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) == guessedChar) {
                updatedWordBuilder.setCharAt(i, guessedChar);
                correctGuess = true;
            }
        }

        String displayedWord = updatedWordBuilder.toString();
        secretWord.setText(String.join(" ", displayedWord.split("")));

        // Überprüft, ob das Spiel gewonnen wurde oder aktualisiert den Status bei einem Fehlversuch
        if (correctGuess) {
            if (displayedWord.equals(wordToGuess)) {
                updateMessage("Glückwunsch, du hast das Wort erraten!");
                if (confirmRestart()) {
                    initializeGame();
                } else {
                    System.exit(0);
                }
            }
        } else {
            updateStatus();
        }

        updateHistory();
    }

    // Status updaten wenn ein flascher Buchstabe eingetipt wird
    private void updateStatus() {
        attempts++;
        guessesLeft.setText("Versuche übrig: " + (maxAttempts - attempts));
        updateImage(attempts);
        if (attempts >= maxAttempts) {
            updateMessage("Game Over! Das Wort war: " + wordToGuess);
            if (confirmRestart()) {
                initializeGame();
            } else {
                System.exit(0);
            }
        }
    }

    // Hangman Bilder updaten
    private void updateImage(int attempts) {
        imgHere.setIcon(new ImageIcon("src/images/hangman" + attempts + ".png"));
    }

    // Buchstaben Verlauf anzeigen/updaten
    private void updateHistory() {
        String guessedLettersString = "Bisherige Buchstaben: " + String.join(" ", guessedLetters.stream().map(String::valueOf).toArray(String[]::new));
        history.setText(guessedLettersString);
    }

    // Nachrichten anzeigen/updaten
    private void updateMessage(String message) {
        messages.setText(message);
    }

    // Spiel neustarten wenn mans geschaft hat
    private boolean confirmRestart() {
        int response = JOptionPane.showConfirmDialog(hangmanView, "Neues Spiel starten?", "Neustart", JOptionPane.YES_NO_OPTION);
        return response == JOptionPane.YES_OPTION;
    }

    // Methode um Wörter aus einer Txt Datei zu laden
    private void loadWordsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/wordList.txt"))) { // Path specified here
            String line;
            while ((line = reader.readLine()) != null) {
                wordList.add(line.trim()); // Add each word to the list
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(hangmanView, "Fehler beim Laden der Wörter!", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }
}
