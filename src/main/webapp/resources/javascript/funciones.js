PrimeFaces.locales['es'] = {
    closeText: 'Cerrar',
    prevText: 'Anterior',
    nextText: 'Siguiente',
    monthNames: ['Enero','Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
    monthNamesShort: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun','Jul','Ago','Sep','Oct','Nov','Dic'],
    dayNames: ['Domingo','Lunes','Martes','Miércoles','Jueves','Viernes','Sábado'],
    dayNamesShort: ['Dom','Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab'],
    dayNamesMin: ['D','L','M','I','J','V','S'],
    weekHeader: 'Semana',
    firstDay: 1,
    isRTL: false,
    showMonthAfterYear: false,
    yearSuffix: '',
    timeOnlyTitle: 'Sólo hora',
    timeText: 'Tiempo',
    hourText: 'Hora',
    minuteText: 'Minuto',
    secondText: 'Segundo',
    currentText: 'Fecha actual',
    ampm: false,
    month: 'Mes',
    week: 'Semana',
    day: 'Día',
    allDayText : 'Todo el día'
};	

function mostrarElemento(idElemento){
	   document.getElementById(idElemento).style.display="block";
	}
	 
function ocultarElemento(idElemento){
	   document.getElementById(idElemento).style.display="none";
	}
function toggleOverlay(){
	var overlay = document.getElementById('overlay');
	overlay.style.zIndex="3000";
	if(overlay.style.display == "block"){
	overlay.style.display = "none";
	} else {
	overlay.style.display = "block";
	}
}
function desbloquear(){
	var overlay = document.getElementById('overlayFran');
	if(overlay.style.display == "block"){
	overlay.style.display = "none";
	}
}
function bloquear(){
	var overlay = document.getElementById('overlayFran');
	overlay.style.display = "block";

}

function enviarFinalizacion(){
	window.parent.postMessage('Finalizacion', '*');
}
function enviarError(){
	window.parent.postMessage('Error', '*');
}
function enviarCancelacion(){
	window.parent.postMessage('Cancelacion', '*');
}

function removerOrden(namespaceProv) {

		var obj = document.getElementById(namespaceProv+"cantidadTransacciones");
		if (obj != null) {
			
			obj.className="ui-state-default ui-sortable-column ui-resizable-column";		
			obj.lastChild.className="ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";
			
			obj = null;
		
			obj = document.getElementById(namespaceProv+"valorBruto");
			obj.className="ui-state-default ui-sortable-column ui-resizable-column";		
			obj.lastChild.className="ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";
		
			obj = null;
		
			obj = document.getElementById(namespaceProv+"valorNeto");
			obj.className="ui-state-default ui-sortable-column ui-resizable-column";		
			obj.lastChild.className="ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";
		}   
}

function removerOrdenAuto(namespaceProv) {

	var obj = document.getElementById(namespaceProv+"cantidadTransacciones");
	if (obj != null) {
		
		if (obj.className == "ui-state-default ui-sortable-column ui-resizable-column") {
			obj.className="ui-state-default ui-sortable-column ui-resizable-column";		
			obj.lastChild.className="ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";
		}
		obj = null;
	
		obj = document.getElementById(namespaceProv+"valorBruto");
		if (obj.className == "ui-state-default ui-sortable-column ui-resizable-column") {
			obj.className="ui-state-default ui-sortable-column ui-resizable-column";		
			obj.lastChild.className="ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";
		}
		obj = null;
	
		obj = document.getElementById(namespaceProv+"valorNeto");
		if (obj.className == "ui-state-default ui-sortable-column ui-resizable-column") {
			obj.className="ui-state-default ui-sortable-column ui-resizable-column";		
			obj.lastChild.className="ui-sortable-column-icon ui-icon ui-icon-carat-2-n-s";
		}
	}   
}

$(document).unbind('keydown').bind('keydown', function (event) {
    var doPrevent = false;
    if (event.keyCode === 8) {
        var d = event.srcElement || event.target;
        if ((d.tagName.toUpperCase() === 'INPUT' && (d.type.toUpperCase() === 'TEXT' || d.type.toUpperCase() === 'PASSWORD' || d.type.toUpperCase() === 'FILE')) 
             || d.tagName.toUpperCase() === 'TEXTAREA') {
            doPrevent = d.readOnly || d.disabled;
        }
        else {
            doPrevent = true;
        }
    }else if(event.keyCode === 13){
    	 doPrevent = true;
    }

    if (doPrevent) {
        event.preventDefault();
    }
});