package vos;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

public class Alojamiento_ReservaVO {

	//----------------------------------------------------
	// Atributos
	//----------------------------------------------------
	
	@JsonProperty (value = "id")
	private Long id;
	
	@JsonProperty (value = "fechaUltimoRegistro")
	private Date fechaUltimoRegistro;
	
	@JsonProperty (value = "nombre")
	private String nombre;
	
	//----------------------------------------------------
	// Constructor
	//----------------------------------------------------
	
	public Alojamiento_ReservaVO(@JsonProperty (value = "id") Long pId, 
								 @JsonProperty (value = "fechaUltimoRegistro") Date pFecha, 
								 @JsonProperty (value = "nombre")String pNombre){
		this.id = pId;
		this.fechaUltimoRegistro = pFecha;
		this.nombre = pNombre;
	}

	//----------------------------------------------------
	// Metodos
	//----------------------------------------------------
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getFechaUltimaVisita() {
		return fechaUltimoRegistro;
	}

	public void setFechaUltimaVisita(Date fechaUltimaVisita) {
		this.fechaUltimoRegistro = fechaUltimaVisita;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
