package com.omaarr90.parser;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.parser.qasm.OpenQasm3Lexer;
import com.omaarr90.parser.qasm.OpenQasm3Parser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

/**
 * Parser for OpenQASM 3 quantum circuits. Converts OpenQASM 3 source code into immutable Circuit
 * objects.
 */
public class OpenQasmParser {

    /**
     * Parse an OpenQASM 3 file into a Circuit.
     *
     * @param file the path to the OpenQASM 3 file
     * @return the parsed Circuit
     * @throws ParseException if parsing fails
     * @throws IOException if file reading fails
     */
    public static Circuit parse(Path file) throws ParseException, IOException {
        String source = Files.readString(file);
        return parse(source);
    }

    /**
     * Parse an OpenQASM 3 source string into a Circuit.
     *
     * @param source the OpenQASM 3 source code
     * @return the parsed Circuit
     * @throws ParseException if parsing fails
     */
    public static Circuit parse(String source) throws ParseException {
        try {
            // Create input stream from source
            CharStream input = CharStreams.fromString(source);

            // Create lexer
            OpenQasm3Lexer lexer = new OpenQasm3Lexer(input);
            lexer.removeErrorListeners();
            lexer.addErrorListener(new ThrowingErrorListener());

            // Create token stream
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Create parser
            OpenQasm3Parser parser = new OpenQasm3Parser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new ThrowingErrorListener());

            // Parse the program
            ParseTree tree = parser.program();

            // Convert AST to Circuit using visitor
            AstToCircuitVisitor visitor = new AstToCircuitVisitor();
            return visitor.visit(tree);

        } catch (Exception e) {
            if (e instanceof ParseException) {
                throw e;
            }
            if (e instanceof RuntimeException && e.getCause() instanceof ParseException) {
                throw (ParseException) e.getCause();
            }
            throw new ParseException("Failed to parse OpenQASM source: " + e.getMessage(), e);
        }
    }

    /** Custom error listener that throws ParseException on syntax errors. */
    private static class ThrowingErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(
                Recognizer<?, ?> recognizer,
                Object offendingSymbol,
                int line,
                int charPositionInLine,
                String msg,
                RecognitionException e) {
            throw new RuntimeException(
                    new ParseException(
                            "Syntax error at line " + line + ":" + charPositionInLine + " - " + msg,
                            e));
        }
    }
}
