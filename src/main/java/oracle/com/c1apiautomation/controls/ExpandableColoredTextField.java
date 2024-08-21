package oracle.com.c1apiautomation.controls;

import javafx.geometry.Insets;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpandableColoredTextField extends InlineCssTextArea {

    private static final Pattern pattern = Pattern.compile("\\{\\{(\\w+)\\}\\}");
    private String fullText = ""; // Initialize with an empty string
    private static final double SINGLE_LINE_HEIGHT = 29;
    private static final double MULTI_LINE_HEIGHT = 100;

    public ExpandableColoredTextField() {
        this(""); // Call the parameterized constructor with an empty string
    }

    public ExpandableColoredTextField(String text) {
        super();
        this.fullText = text;

        // Set to single line initially
        setPrefHeight(SINGLE_LINE_HEIGHT);
        setWrapText(false);  // No wrapping in single-line mode

        // Prevent Enter key from adding a new line in single-line mode
        addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (!isFocused() && event.getCode().toString().equals("ENTER")) {
                event.consume();
            }
        });

        // Expand on focus and update text styles on focus lost
        focusedProperty().addListener((obs, oldVal, newVal) -> {

            setPadding(new Insets(5));
            var allStyles = "-fx-font-weight: 700;";

            if (newVal) { // Gained focus
                setPrefHeight(MULTI_LINE_HEIGHT);
                setWrapText(true); // Enable wrapping in multi-line mode
                allStyles += "-fx-border-color: #3399FF ; -fx-border-width: 1;";
            } else { // Lost focus
                setPrefHeight(SINGLE_LINE_HEIGHT);
                setWrapText(false);
                allStyles += "-fx-border-color: #AFB1B3; -fx-border-width: 1;";
                // Apply highlighting if text contains {{newtext}}
                applyHighlightOnFocusLost();
            }

            setStyle(allStyles);
        });

        this.textProperty().addListener((obs, oldText, newText) -> {
            fullText = newText; // Update the fullText variable
            setStyleSpans(0, computeHighlighting(fullText)); // Apply styles to the entire text
        });

        // Initial text update
        updateText();
    }

    private void updateText() {
        replaceText(0, getLength(), fullText); // Replace existing text with new text
        // Apply styles immediately when text is updated
        StyleSpans<String> styles = computeHighlighting(fullText);
        setStyleSpans(0, styles);

        setPadding(new Insets(5));
            }

    private void applyHighlightOnFocusLost() {
        // Apply styles if text contains {{newtext}} and control is not focused
        // This ensures that new text highlighting is applied even when losing focus
        StyleSpans<String> styles = computeHighlighting(fullText);
        setStyleSpans(0, styles);

    }

    private StyleSpans<String> computeHighlighting(String text) {
        Matcher matcher = pattern.matcher(text);
        StyleSpansBuilder<String> spansBuilder = new StyleSpansBuilder<>();

        int lastEnd = 0;
        while (matcher.find()) {
            // Add a span with default style for the text before the current match
            if (matcher.start() > lastEnd) {
                spansBuilder.add("", matcher.start() - lastEnd); // Default style (normal text color)
            }

            // Add a span with highlight style for the current match
            spansBuilder.add("-fx-fill: green;", matcher.end() - matcher.start()); // Highlight (green text color)

            lastEnd = matcher.end();
        }
        // Apply default style to any remaining text after the last match

//        spansBuilder.add("-fx-fill:red", text.length() - lastEnd);
//        spansBuilder.add("", Math.max(text.length() - lastEnd, 1));
        spansBuilder.add("", text.length() - lastEnd);

        return spansBuilder.create();
    }


    public String getFullText() {
        return fullText;
    }

    public void setFullText(String text) {
        this.fullText = text;
        updateText();
    }
}
