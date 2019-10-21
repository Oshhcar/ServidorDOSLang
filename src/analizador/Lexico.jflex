package analizador;

import java_cup.runtime.Symbol;
import java.util.ArrayList;

%%

%cupsym Sym
%class Lexico
%cup
%public
%unicode
%line
%char
%column
%ignorecase

%{
	
	StringBuffer string = new StringBuffer();
	boolean blancos = false;
        
        private String valorError = "";
        private int columnaError = 0;
        private boolean isError = false;

        private ArrayList<ErrorC> errores = new ArrayList<>();

        public ArrayList<ErrorC> getErrores(){
            return this.errores;
        }

        public void addError(){
            if(this.isError){
                ErrorC error = new ErrorC(1, yyline+1, this.columnaError, "Carácter no reconocido.");
                this.errores.add(error);
                this.isError = false;
                this.columnaError = 0;
                this.valorError = "";
            }
            
        }
        
	private Symbol symbol(int type) {
                this.addError();
		return new Symbol(type, yyline+1, yycolumn+1);
	}	
  
	private Symbol symbol(int type, Object value) {
                this.addError();
		return new Symbol(type, yyline+1, yycolumn+1, value);
	}
%}

digito = [0-9]
entero = {digito}+
decimal = {digito}+"."{digito}+
letra = [a-zA-Z\u00F1\u00D1]
identificador = ({letra}|"_")({letra}|{digito}|"_")*

finLinea = \r|\n|\r\n
espacioBlanco = {finLinea} | [ \t\f]

COMENT_SIMPLE ="(*" [^\r\n]* "*)" {finLinea}?
COMENT_MULTI ="{""{"*([^}])*"}"*"}"

%state STRING
%state CHAR

%%

{COMENT_SIMPLE} 	{/* se ignora */} 
{COMENT_MULTI} 		{/* se ignora */} 

/* Palabras Reservadas*/
<YYINITIAL> "true"			{ return symbol(Sym.true_);}
<YYINITIAL> "false"			{ return symbol(Sym.false_);}
<YYINITIAL> "nil"			{ return symbol(Sym.nil_);}

<YYINITIAL> "char"			{ return symbol(Sym.char_);}
<YYINITIAL> "integer"			{ return symbol(Sym.integer_);}
<YYINITIAL> "real"			{ return symbol(Sym.real_);}
<YYINITIAL> "string"			{ return symbol(Sym.string_);}
<YYINITIAL> "word"			{ return symbol(Sym.word_);}
<YYINITIAL> "boolean"			{ return symbol(Sym.boolean_);}

<YYINITIAL> "program"			{ return symbol(Sym.program_);}
<YYINITIAL> "uses"			{ return symbol(Sym.uses_);}
<YYINITIAL> "type"			{ return symbol(Sym.type_);}
<YYINITIAL> "array"			{ return symbol(Sym.array_);}
<YYINITIAL> "of"			{ return symbol(Sym.of_);}
<YYINITIAL> "var"			{ return symbol(Sym.var_);}
<YYINITIAL> "const"			{ return symbol(Sym.const_);}
<YYINITIAL> "function"			{ return symbol(Sym.function_);}
<YYINITIAL> "begin"			{ return symbol(Sym.begin_);}
<YYINITIAL> "end"			{ return symbol(Sym.end_);}
<YYINITIAL> "procedure"			{ return symbol(Sym.procedure_);}
<YYINITIAL> "record"			{ return symbol(Sym.record_);}

<YYINITIAL> "break"			{ return symbol(Sym.break_);}
<YYINITIAL> "if"			{ return symbol(Sym.if_);}
<YYINITIAL> "then"			{ return symbol(Sym.then_);}
<YYINITIAL> "else"			{ return symbol(Sym.else_);}
<YYINITIAL> "with"			{ return symbol(Sym.with_);}
<YYINITIAL> "do"			{ return symbol(Sym.do_);}
<YYINITIAL> "continue"			{ return symbol(Sym.continue_);}
<YYINITIAL> "exit"			{ return symbol(Sym.exit_);}
<YYINITIAL> "case"			{ return symbol(Sym.case_);}
<YYINITIAL> "default"			{ return symbol(Sym.default_);}
<YYINITIAL> "while"			{ return symbol(Sym.while_);}
<YYINITIAL> "repeat"			{ return symbol(Sym.repeat_);}
<YYINITIAL> "until"			{ return symbol(Sym.until_);}
<YYINITIAL> "for"			{ return symbol(Sym.for_);}
<YYINITIAL> "to"			{ return symbol(Sym.to_);}
<YYINITIAL> "downto"			{ return symbol(Sym.downto_);}
<YYINITIAL> "write"			{ return symbol(Sym.write_);}
<YYINITIAL> "writeln"			{ return symbol(Sym.writeln_);}
<YYINITIAL> "read"			{ return symbol(Sym.read_);}

<YYINITIAL>{

\" 					{ string.setLength(0); yybegin(STRING); }
\' 					{ string.setLength(0); yybegin(CHAR); }

";"					{return symbol(Sym.puntoycoma);}
","					{return symbol(Sym.coma);}
"..."					{return symbol(Sym.puntos3);}
".."					{return symbol(Sym.puntos2);}
":"					{return symbol(Sym.dospuntos);}

"("					{return symbol(Sym.parIzquierda);}
")"					{return symbol(Sym.parDerecha);}
"["					{return symbol(Sym.corcheteIzquierda);}
"]"					{return symbol(Sym.corcheteDerecha);}
"."					{return symbol(Sym.punto);}
//"?"					{return symbol(Sym.interrogacion);}


//Operadores Aritméticos
"+"                 {return symbol(Sym.mas);}
"-"                 {return symbol(Sym.menos);}
"*"                 {return symbol(Sym.asterisco);}  
"/"                 {return symbol(Sym.diagonal);}
"%"                 {return symbol(Sym.modulo);}
"^"                 {return symbol(Sym.potencia);}

//Operadores Relacionales 
">"                 {return symbol(Sym.mayorque);}
"<"                 {return symbol(Sym.menorque);}
">="                {return symbol(Sym.mayorigual);}
"<="                {return symbol(Sym.menorigual);}
"<>"                {return symbol(Sym.diferente);}
"="                 {return symbol(Sym.igual);}

//Operadores Lógicos
"and"               {return symbol(Sym.and);}
"nand"              {return symbol(Sym.nand);}
"nor"               {return symbol(Sym.nor);}
"or"                {return symbol(Sym.or);}
"not"               {return symbol(Sym.not);}

//Operador Asignacion
":="                 {return symbol(Sym.asignacion);}

{entero}       		{ return symbol(Sym.entero, yytext());}
{decimal}		{ return symbol(Sym.decimal, yytext());}
{identificador}         { return symbol(Sym.id, yytext());}

/* Espacios en Blanco */
{espacioBlanco}         { if(this.isError){this.valorError += " ";} }

}

<STRING> {
\"                   { yybegin(YYINITIAL);
					   return symbol(Sym.tstring, string.toString()); }
[^\"\\]+               { string.append( yytext() ); }
\\\"                 { string.append('\"'); }
\\\'                 { string.append('\''); }
\\\\                 { string.append('\\'); }
\\0                  { string.append('\0'); }
\\a                  { string.append((char)7); }
\\b                  { string.append('\b'); }
\\f                  { string.append('\f'); }
\\t                  { string.append('\t'); }
\\n                  { string.append('\n'); }
\\r                  { string.append('\r'); }
\\v                  { string.append((char)11); }
}

<CHAR> {
\'                   { yybegin(YYINITIAL);
					   return symbol(Sym.tchar, string.toString()); }
[^\'\\]+             { string.append( yytext() ); }
\\\"                 { string.append('\"'); }
\\\'                 { string.append('\''); }
\\\\                 { string.append('\\'); }
\\0                  { string.append('\0'); }
\\a                  { string.append((char)7); }
\\b                  { string.append('\b'); }
\\f                  { string.append('\f'); }
\\t                  { string.append('\t'); }
\\n                  { string.append('\n'); }
\\r                  { string.append('\r'); }
\\v                  { string.append((char)11); }
}

/* ERRORES LEXICOS */
.		{ 
                    if(!this.isError){
                        this.isError = true;
                        this.columnaError = yycolumn+1;
                    }
                    this.valorError += yytext(); 
                }











