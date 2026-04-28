package io.github.rohergun.dotenv;

public class DotEnvParseException extends RuntimeException{
    private final String sourceFile;
    private final int lineNumber;
    private final String rawLine;

    public DotEnvParseException(String sourceFile, int lineNumber,
                                String rawLine, String reason){
        super(buildMessage(sourceFile, lineNumber, rawLine, reason));
        this.sourceFile = sourceFile;
        this.lineNumber = lineNumber;
        this.rawLine    = rawLine;
    }

    private static String buildMessage(String sourceFile, int lineNumber,
                                       String rawLine, String reason){
        return String.format("Parse error in '%s' at line %d: %s%n  offending line: \"%s\"",
                sourceFile, lineNumber, reason, rawLine
        );
    }

    public String getSourceFile(){
        return this.sourceFile;
    }

    public int getLineNumber(){
        return this.lineNumber;
    }

    public String getRawLine(){
        return this.rawLine;
    }
}
