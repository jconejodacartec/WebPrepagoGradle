
/**
*
* Modal popUpCampo
*/
function initpopUpCampo() {
	window.alert("Entre");
	$("#popUpCampo").modal({
			opacity : 50,
			height:140,
			overlayCss: {backgroundColor:"#222"},
			modal : true,
			escClose: true
			 
	});
}

/**
 * Metodo encargado de validar que se ingresen solo digitos en un input y que no
 * supere un tamano dado
 * 
 * @param tamano
 * 			Tamano maximo que va a tener el campo, si es -1 no va a tener limite
 * @param evento
 * 			Variable que almacena la referencia al objeto Event
 * @param objeto
 * 			variable que almacena la referencia al input que se esta validando
 * @returns {Boolean}
 */
function validarDigitos(tamano, event, objeto) {
	// 48 a 57 son los digitos
	// 8 boton de borrar
	
//	var arrayTeclas = [46, 8, 9, 27, 13, 110, 190, 32];
	
	var bEstaEnArray = isInArray( event.keyCode, [46, 8, 9, 27, 13, 110, 32, 37, 38] );
	var bEstaEnArrayC = isInArray( event.charCode, [46, 8, 9, 27, 13, 110, 32, 37, 38, 118] );
	
	
	
	// si esta definido el atributo "keyCode"
	if (event.keyCode) {
		console.log("KeyCode: " + event.keyCode);
		if ( (event.keyCode >= 48) && (event.keyCode <= 57) || (event.keyCode == 8) || bEstaEnArray ||
	             // Allow: Ctrl+A
	            (event.which == 65 && event.ctrlKey === true) ||
	             // Allow: Ctrl+C
	            (event.which == 67 && event.ctrlKey === true) ||
	             // Allow: Ctrl+X
	            (event.which == 88 && event.ctrlKey === true)) {
			
			if( (event.keyCode == 8) || bEstaEnArray  ||
		             // Allow: Ctrl+A
		            (event.which == 65 && event.ctrlKey === true) ||
		             // Allow: Ctrl+C
		            (event.which == 67 && event.ctrlKey === true) ||
		             // Allow: Ctrl+X
		            (event.which == 88 && event.ctrlKey === true)){
				return true;
			}
			
			if ( tamano != -1 ) {
				if (objeto.value.length < tamano) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	
	// si esta definido el atributo "charCode"
	if (event.charCode) {
		console.log("Charcode: " + event.charCode);
		if ( (event.charCode >= 48) && (event.charCode <= 57) || (event.charCode == 8) || bEstaEnArrayC ||
	             // Allow: Ctrl+A
	            (event.which == 65 && event.ctrlKey === true) ||
	             // Allow: Ctrl+C
	            (event.which == 67 && event.ctrlKey === true) ||
	             // Allow: Ctrl+X
	            (event.which == 88 && event.ctrlKey === true)) {
			
			if( (event.charCode == 8) || bEstaEnArrayC ||
		             // Allow: Ctrl+A
		            (event.which == 65 && event.ctrlKey === true) ||
		             // Allow: Ctrl+C
		            (event.which == 67 && event.ctrlKey === true) ||
		             // Allow: Ctrl+X
		            (event.which == 88 && event.ctrlKey === true)) {
				return true;
			}
			
			if ( tamano != -1 ) {
				if (objeto.value.length < tamano) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
}

function isInArray(valor, array) {
	var retorno = false;
	for ( var i = 0 ; i < array.length ; i++ ) {
		if ( array[i] == valor ) {
			retorno = true;
			break;
		}
	}
	
	return retorno;
}

