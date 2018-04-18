package vos;

import org.codehaus.jackson.annotate.JsonProperty;

public class OcupacionTotalVO {

	//----------------------------------------------------
	// Atributos
	//----------------------------------------------------	
	
	@JsonProperty (value = "Identificador")
	private Long Identificador;
	
	@JsonProperty (value = "Ocupacion")
	private String Ocupacion;
	
	@JsonProperty (value = "Nombre")
	private String Nombre;
	
	
	//----------------------------------------------------
	// Constructor
	//----------------------------------------------------
	
	public OcupacionTotalVO(@JsonProperty (value = "Identificador") Long pID,
							@JsonProperty (value = "Ocupacion") String pOcupacion,
							@JsonProperty (value = "Nombre") String pNombre){
		this.Identificador = pID;
		this.Ocupacion = pOcupacion;
		this.Nombre = pNombre;
	}

	//----------------------------------------------------
	// Metodos
	//----------------------------------------------------

	public Long getIdentificador() {
		return Identificador;
	}


	public void setIdentificador(Long identificador) {
		Identificador = identificador;
	}


	public String getOcupacion() {
		return Ocupacion;
	}


	public void setOcupacion(String ocupacion) {
		Ocupacion = ocupacion;
	}

	public String getNombre() {
		return Nombre;
	}

	public void setNombre(String nombre) {
		Nombre = nombre;
	}
}