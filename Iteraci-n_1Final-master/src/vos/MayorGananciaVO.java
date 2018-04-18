package vos;

import java.util.Date;

public class MayorGananciaVO {

	//----------------------------------------------------
	// Atributos
	//----------------------------------------------------	
	
	private Date fechaMayor;
	private Double costoGanado;
	
	//----------------------------------------------------
	// Constructor
	//----------------------------------------------------	
	
	public MayorGananciaVO (Date pFecha, Double pMax){
		this.fechaMayor = pFecha;
		this.costoGanado = pMax;
	}

	//----------------------------------------------------
	// Metodos
	//----------------------------------------------------	
	
	public Date getFechaMayor() {
		return fechaMayor;
	}

	public void setFechaMayor(Date fechaMayor) {
		this.fechaMayor = fechaMayor;
	}

	public Double getCostoGanado() {
		return costoGanado;
	}

	public void setCostoGanado(Double costoGanado) {
		this.costoGanado = costoGanado;
	}
}