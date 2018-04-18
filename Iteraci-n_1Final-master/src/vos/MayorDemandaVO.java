package vos;

import java.util.Date;

public class MayorDemandaVO {

	//----------------------------------------------------
	// Atributos
	//----------------------------------------------------	
	
	private Date fechaMayor;
	private Integer max;
	
	//----------------------------------------------------
	// Constructor
	//----------------------------------------------------	
	public MayorDemandaVO(Date pFecha, Integer pMax){
		this.fechaMayor = pFecha;
		this.max = pMax;
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

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}
}