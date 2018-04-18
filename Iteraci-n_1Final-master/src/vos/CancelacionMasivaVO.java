package vos;

import org.codehaus.jackson.annotate.JsonProperty;

public class CancelacionMasivaVO {

	//----------------------------------------------------
	// Atributos
	//----------------------------------------------------	
		
	@JsonProperty (value = "idReservaCancelar")
	private Long idReservaCancelar;
	
	@JsonProperty (value = "isAmoblado")
	private String razonCancelacion;
	
	//----------------------------------------------------
	// Constructor
	//----------------------------------------------------
	
	public CancelacionMasivaVO(@JsonProperty (value = "idReservaCancelar") Long pIdCanc,
							   @JsonProperty (value = "razonCancelacion") String pRazon){
		
		this.idReservaCancelar = pIdCanc;
		this.razonCancelacion = pRazon;
	}

	
	//----------------------------------------------------
	// Métodos
	//----------------------------------------------------
	
	public Long getIdReservaCancelar() {
		return idReservaCancelar;
	}

	public void setIdReservaCancelar(Long idReservaCancelar) {
		this.idReservaCancelar = idReservaCancelar;
	}

	public String getRazonCancelacion() {
		return razonCancelacion;
	}

	public void setRazonCancelacion(String razonCancelacion) {
		this.razonCancelacion = razonCancelacion;
	}
}