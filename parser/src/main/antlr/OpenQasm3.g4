grammar OpenQasm3;

// Parser rules
program
    : version? (statement)* EOF
    ;

version
    : 'OPENQASM' REAL ';'
    ;

statement
    : qubitDeclaration
    | gateApplication
    | measureStatement
    | barrier
    ;

qubitDeclaration
    : 'qubit' '[' INT ']' IDENTIFIER ';'
    ;

gateApplication
    : gateCall qubitArguments ';'
    ;

gateCall
    : IDENTIFIER                           // Single qubit gates: x, y, z, h
    | IDENTIFIER '(' expression ')'        // Parametrized gates: rz(π/2)
    ;

qubitArguments
    : qubitReference (',' qubitReference)*
    ;

qubitReference
    : IDENTIFIER '[' INT ']'
    ;

measureStatement
    : 'measure' qubitReference '->' classicalReference ';'
    | 'measure' IDENTIFIER '->' IDENTIFIER ';'  // measure all qubits
    ;

classicalReference
    : IDENTIFIER '[' INT ']'
    ;

barrier
    : 'barrier' qubitArguments? ';'
    ;

expression
    : REAL
    | INT
    | PI
    | expression ('*'|'/') expression
    | expression ('+'|'-') expression
    | '(' expression ')'
    ;

// Lexer rules
IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_]*
    ;

INT
    : [0-9]+
    ;

REAL
    : [0-9]+ '.' [0-9]*
    | '.' [0-9]+
    | [0-9]+
    ;

PI
    : 'π'
    | 'pi'
    ;

// Whitespace and comments
WS
    : [ \t\r\n]+ -> skip
    ;

LINE_COMMENT
    : '//' ~[\r\n]* -> skip
    ;

BLOCK_COMMENT
    : '/*' .*? '*/' -> skip
    ;