//Prueba de la recursividad usando una funcion factorial
//prueba de evaluacion de expresiones y uso de la sentencia if-else

int factorial (int numero)
	{
	if (numero != 1)
		{
		return numero * factorial(numero - 1);
		}
	else
		{
		return 1; 
		}
	}

void main ()
	{
	int numero = 3;
	cout << "El factorial de " << numero << " es " << factorial(numero);
	numero += 2;
	cout << "El factorial de " << numero << " es " << factorial(numero);
	}
