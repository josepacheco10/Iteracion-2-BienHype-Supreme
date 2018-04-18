package vos;

import org.codehaus.jackson.annotate.JsonProperty;

public class UsoAlojandesVO {

	//----------------------------------------------------
	// Atributos
	//----------------------------------------------------
		
	@JsonProperty (value = "cantidadNoches")
	private Integer cantidadNoches;
	
	@JsonProperty (value = "nombreCliente")
	private String nombreCliente;
	
	@JsonProperty (value = "tipoCliente")
	private String tipoCliente;
		
	//----------------------------------------------------
	// Constructor
	//----------------------------------------------------
	
	public UsoAlojandesVO (@JsonProperty (value = "id") Integer pcantidadNoches, 
						   @JsonProperty (value = "nombreCliente") String pNombre,
						   @JsonProperty (value = "tipoCliente") String pTipoCliente){
		
		this.cantidadNoches = pcantidadNoches;
		this.nombreCliente = pNombre;
		this.tipoCliente = pTipoCliente;
	}

	
	//----------------------------------------------------
	// Metodos
	//----------------------------------------------------
	
	public Integer getCantidadNoches() {
		return cantidadNoches;
	}

	public void setCantidadNoches(Integer cantidadNoches) {
		this.cantidadNoches = cantidadNoches;
	}

	public String getNombreCliente() {
		return nombreCliente;
	}

	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}


	public String getTipoCliente() {
		return tipoCliente;
	}


	public void setTipoCliente(String tipoCliente) {
		this.tipoCliente = tipoCliente;
	}
}