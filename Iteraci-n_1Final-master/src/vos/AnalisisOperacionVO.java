package vos;

import org.codehaus.jackson.annotate.JsonProperty;

public class AnalisisOperacionVO {

	//----------------------------------------------------
	// Atributos
	//----------------------------------------------------
	
	@JsonProperty (value = "fechasMayorDemanda")
	private String fechasMayorDemanda;
	
	@JsonProperty (value = "fechasMayorIngreso")
	private String fechasMayorIngreso;
	
	@JsonProperty (value = "fechasMenorDemanda")
	private String fechasMenorDemanda;
	
	//----------------------------------------------------
	// Constructor
	//----------------------------------------------------
	
	public AnalisisOperacionVO (@JsonProperty (value = "fechasMayorDemanda") String p1,
								@JsonProperty (value = "fechasMayorIngreso") String p2,
								@JsonProperty (value = "fechasMenorDemanda") String p3) {
		
		this.fechasMayorDemanda = p1;
		this.fechasMayorIngreso = p2;
		this.fechasMenorDemanda = p3;	
	}

	//----------------------------------------------------
	// Metodos
	//----------------------------------------------------
	
	public String getFechasMayorDemanda() {
		return fechasMayorDemanda;
	}

	public void setFechasMayorDemanda(String fechasMayorDemanda) {
		this.fechasMayorDemanda = fechasMayorDemanda;
	}

	public String getFechasMayorIngreso() {
		return fechasMayorIngreso;
	}

	public void setFechasMayorIngreso(String fechasMayorIngreso) {
		this.fechasMayorIngreso = fechasMayorIngreso;
	}

	public String getFechasMenorDemanda() {
		return fechasMenorDemanda;
	}

	public void setFechasMenorDemanda(String fechasMenorDemanda) {
		this.fechasMenorDemanda = fechasMenorDemanda;
	}
}