grammar:
```
expression     → conditional ;
conditional    → equality ( "?" expression ":" expression )? ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → bitwise ( ( "&" | "|" ) bitwise )*;
bitwise        → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
term           → factor ( ( "-" | "+" ) factor )* ;
factor         → unary ( ( "/" | "*" ) unary )* ;
unary          → ( "!" | "-" ) unary
               | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil"
               | "(" expression ")" 
               /* error productions*/
               | ( "==" | "!=" ) equality
               | ( ">" | "<" | "<=" | ">=" ) comparision
               | ( "+" ) term
               | ( "*" | "/" ) factor

```
possible enhancements/changes:

- block comments
- accept escape chars within a string literal
- no support for multiline string