package analizador;
import java.io.*;
//import org.antlr.runtime.CommonTokenStream;
import antlr.collections.AST;
import antlr.ANTLRException;
import antlr.Token;
import antlr.TokenStreamException;

public class Compilador {
	
	public static void main (String args[]) throws TokenStreamException {
		try {
	        System.out.println("Empieza a dar tokens:");
			FileInputStream fis = new FileInputStream("/home/pirois/Documentos/compiladores/practica_compilador/compilador_code/Proyecto/src/analizador/cod_fuente1");
			analizador Analizador = new analizador(fis);
			Token  token = Analizador.nextToken();
			while(token.getType() != Token.EOF_TYPE) {
				System.out.println(token);
				token = Analizador.nextToken();
			}
		}catch(FileNotFoundException fnfe) {
			System.err.println("No se encontró el fichero");
		}
	}
}