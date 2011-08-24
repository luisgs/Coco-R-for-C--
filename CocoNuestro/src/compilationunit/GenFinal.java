package compilationunit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class GenFinal {
    BufferedWriter bw;
    File archiEscri=null;
    String temporal;
    String operacion,op1,op2,op3;
    String etiquetasputs="";
    int num_param_actual = 0;
    int c_etiqueta;


public GenFinal(LinkedList<tupla_Tercetos> colaTercetos, Tablas tabla, String fichero) {
    
    int desp_total;  //variable para el desplazamiento total de las tablas de simbolos
    archiEscri= new File(fichero);
    tupla_Tercetos tupla_actual;
    String terceto_actual;
    TablaSimbolos ambito_actual;
    //cola para ir metiendo los metodos a los que se llama
    LinkedList<String> colaMetodos = new LinkedList<String> (); 
    Simbolo simbolo;
    TablaSimbolos tabla_aux;
    c_etiqueta = 0;
    
    System.out.println("Comienza la fase de generacion de codigo objeto");
    //preparamos el fichero que contendra el codigo objeto
    try
    	{
        bw= new BufferedWriter(new FileWriter(fichero));
    	}
    catch (IOException e) 
    	{
         System.out.println("Error fichero de salida para Codigo Objeto.");
    	}
    

    //inicializamos el codigo objeto y lo dejamos todo preparado para leer los
    //tercetos del main
    try {
        bw.write("ORG 0\n");
        // Inicializamos la pila al maximo puesto que es decreciente
        // y la guardamos en el puntero de pila
        bw.write ("MOVE #65535, .SP\n");
        bw.write ("MOVE .SP, .IX\n");
        
        /* creamos el RA de la clase que contiene el metodo principal, dejando
         * hueco para todos sus atributos, despues guardamos el IX, que apuntará
         * al primer atributo de la clase que contiene el metodo main
         * para luego poder acceder cogiendo el desplazamiento de la tabla
         * de simbolos */
        tabla_aux = tabla.GetAmbitoGlobal();  //buscamos la tabla de la clase del metodo principal
        desp_total = tabla_aux.GetDesplazamiento(); //cogemos el desp de la tabla de simbolos global
        bw.write ("ADD #-" + desp_total + ", .SP\n"); //sumamos desp_total de la tabla de simbolos padre al SP
        bw.write("MOVE .A, .SP\n"); //actualizamos SP
        bw.write("PUSH .IX\n");  	//guardamos el IX para saber donde empiezan los atributos de la tabla de simbolos padre
        bw.write ("MOVE .SP, .IX\n");  //actualizamos IX
        
        //Vamos a buscar el main para que el PC
        //Si el analisis semantico ha validado el codigo, dentro del ambito global deberia estar el objeto main
        simbolo = tabla_aux.GetSimbolo("main");
        String etiqueta_main;
        etiqueta_main = simbolo.GetEtiqueta();
        bw.write("CALL /" + etiqueta_main + " ; VAMOS AL MAIN\n");
        bw.write("POP .IX ; Recuperamos el marco de pila\n");
        bw.write("MOVE .IX, .SP\n");
        bw.write("HALT ;Cuando se vuelva del Main se terminara la ejecucion\n");
                
        /*
         * Bucle para imprimir toda la cola de tercetos!
         */
        System.out.println("-----------------------------------");
        System.out.println("Elementos de la lista "+colaTercetos);
        System.out.println("Tamano de la lista:"+colaTercetos.size());
        Iterator<tupla_Tercetos> it = colaTercetos.iterator();
        tupla_Tercetos tupla_temp;
        while (it.hasNext()) {
            //this.separar(it.next().GetTerceto());
        	tupla_actual = it.next();
            System.out.println("Terceto: "+tupla_actual.GetTerceto());
            System.out.println("Desplazamiento de la tabla para temp:"+tupla_actual.GetAmbitoActual().GetDesplazamiento());
            //System.out.println("Ambito_actual: "+it.next().GetAmbitoActual());
            ProcesarTerceto(tupla_actual, tabla);
        }
        System.out.println("-----------------------------------");
        
        /*
         * Ponemos un HALT, si se acaban los tercetos es final de MAIN
         * Nota:podemos hacer un RET pero ahorramos problemas ocn un HALT
         */
        bw.write("RET; final de main");
        // Importante! sino no se guarda nada en el fichero!
        bw.close();
    }
       
    catch (IOException e)
    	{
    	System.out.println("Tranquilo vaquero");
    	}
    }


private void ProcesarTerceto (tupla_Tercetos tupla_actual, Tablas tabla) {	
	// Obtenemos los dos valores de la tupla
	String terceto_actual= tupla_actual.GetTerceto();	// Almacenara el String emitido por el GCI
	TablaSimbolos ambitoterceto = tupla_actual.GetAmbitoActual();
	
	// Separamos los operando del terceto. operador, op1, op2...
	this.separar(terceto_actual);
	
	System.out.println("PROCESAR TERCETO->"+operacion);
	
	if (operacion.equals("ASIGNACION")) {				// caso de asignar un entero a algo
    	EjecutarAsignacion(op1, op2, ambitoterceto);	// paso el destino(op1) y el valor(op2)
	} else if (operacion.equals("ETIQUETA_SUBPROGRAMA")) {
		ComienzoSubprograma(op1, ambitoterceto);		// op1: nombre de la etiqueta
	} else if (operacion.equals("ASIGNA")) {	// "caracola"->temp
		EjecutarAsigna(op1, op2, ambitoterceto);
	} else {
		System.err.println("Operaion Terceto no contemplado->"+operacion);
	}

}

//***********************************************************************************************
/*
 * 
 */
private void EjecutarAsigna (String op1, String op2, TablaSimbolos ambito_terceto) {
	try {
		Simbolo simbolo_op1 = ambito_terceto.GetSimbolo(op1);
		//bw.write("DATA "+op2+"\n");
		bw.write("WRSTR #-" + simbolo_op1.GetDesplazamiento() + "[.IX]\n");
		bw.write("etiqueta del temporal:"+simbolo_op1.GetNombre());
		// TODO
		// tenemos que meter cadenas como DATA en posiciones de memoria q no dañan
	} catch (IOException e) {
        System.err.println("Error: Ejecutar Asigna.");		
    }
}

/*
 * Ejecutar Asignacion es para casos donde el valor a asignar sea un ENTERO!
 */
private void EjecutarAsignacion(String op1, String op2, TablaSimbolos ambito_terceto)	{
	try {
		Simbolo simbolo_op1 = ambito_terceto.GetSimbolo(op1);
		bw.write("MOVE #"+op2+",#-" + simbolo_op1.GetDesplazamiento() + "[.IX]\n");
	} catch (IOException e) {
        System.err.println("Error: Ejecutar Asignacion.");		
    }
}

/*
 *	Crear el nuevo marco de pila, añade la etiqueta al codigo ensamblador 
 */
private void ComienzoSubprograma (String subprograma, TablaSimbolos ambito_terceto) {
	try {
		// Recuperamos el desplazamiento para le Marco de pila
		int despl_local=ambito_terceto.GetDesplazamiento();		
		// Escribimos la etiqueta
		bw.write(subprograma.toLowerCase() +":\n");		// tiene q ser en minusculas!!
		bw.write("MOVE .SP, .IX\n");					// Base del marco de pila
		bw.write("ADD #-" + despl_local + ", .SP\n");	// Techo del Marco de pila
		bw.write("MOVE .A, .SP\n");
	} catch (IOException e) {
		System.err.println("Error: Comienzo Subprograma.");
	}
}
 
/*
 * Operacion q dado un terceto-> ASIGNACION, temp0, 10-> separa cada uno en un operando global
 */
private void separar(String linea)	{
    int u= linea.indexOf(",");
    this.operacion=linea.substring(0,u); //cogemos la operación
    linea=linea.substring(u+1);
    
    u= linea.indexOf(",");
    op1=linea.substring(0,u);
    linea=linea.substring(u+1);

    u= linea.indexOf(",");
    op2=linea.substring(0,u);
    linea=linea.substring(u+1);

    op3=linea.substring(0,linea.indexOf("\n"));
}
    
    
    public void traducir(Tablas tabla)
    	{
    	
    	}


}
