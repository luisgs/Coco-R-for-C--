// Prueba Incorrecta

// Declaracion de subprograma o funcion
int funcionAnodina (int n) {
	int uno = 1, cero = 0;	// variables locales inicializadas
	
	// intrucciones operacionales
	uno = cero * "cadena";	// TIPOS ERRONEOS
	uno = uno + 34 - 23 / 30 - cero * n;
	
	return uno;
}

// Punto ppal del programa
int main (int n) {
	int variable;
	// Asignacion de valor cierto ó falso
	variable = n * funcionAnodina(n);

//	return variable;
	return true;	// tipos de devolucion erroneo
}
