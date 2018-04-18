package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import vos.ApartamentoVO;
import vos.CancelacionMasivaVO;
import vos.ClienteVO;
import vos.HabitacionHoteleraVO;
import vos.HabitacionUniversitariaVO;
import vos.HabitacionVO;
import vos.HostalVO;
import vos.ReservaVO;
import vos.ViviendaEsp;

/**
 * Clase DAO que se conecta la base de datos usando JDBC para resolver los requerimientos de la aplicacion.
 * @author Grupo C-13
 */
public class DAOReserva {
	
	//----------------------------------------------------
	// Constantes
	//----------------------------------------------------	
	
	/**
	 * Constante que hace referencia al usuario Oracle usado
	 * para generar las tablas.
	 */
	public static final String USUARIO = "ISIS2304A871810";
	
	//----------------------------------------------------
	// Atributos
	//----------------------------------------------------	
	/**
	 * Arraylits de recursos que se usan para la ejecucion de sentencias SQL
	 */
	private ArrayList <Object> resources;

	/**
	 * Atributo que genera la conexion a la base de datos
	 */
	private Connection conn;
	
	/**
	 * Clase DAO del cliente, atributo usado para verificar la seguridad de la aplicacion.
	 */
	private DAOCliente cliente;
	
	//----------------------------------------------------
	// Constructor inicializaci�n
	//----------------------------------------------------
	
	public DAOReserva(){
		resources = new ArrayList<>();
		cliente = new DAOCliente();
	}
	
	//----------------------------------------------------
	// Metodos Arquitectura REST: 
	//----------------------------------------------------
	
	// GET BY ID
	/**
	 * Metodo para obtener un cliente con un identificador especifico en la base de datos.
	 * @param id Identificador del Cliente que se desea buscar en la base de datos.
	 * @return Un objeto de tipo ClienteVO con sus atributos. 
	 * @throws SQLException En caso de que exista un error en la conexi�n o en la sentencia SQL.
	 * @throws Exception En caso de que exista un error en el metodo en general. 
	 */
	public ClienteVO getCliente (Long id) throws SQLException, Exception {
		
		ClienteVO clienteRetorno = null;

		String sql = String.format("SELECT * FROM %1$s.CLIENTES WHERE ID_CLIENTE = %2$d", USUARIO, id); 
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		
		resources.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		if(rs.next()) {
			clienteRetorno = convertResultSetToClienteVO(rs);
		}

		return clienteRetorno;	
	}
	
	// GET BY ID
	/**
	 * Metodo para obtener un cliente con un identificador especifico en la base de datos.
	 * @param id Identificador del Cliente que se desea buscar en la base de datos.
	 * @return Un objeto de tipo ClienteVO con sus atributos. 
	 * @throws SQLException En caso de que exista un error en la conexi�n o en la sentencia SQL.
	 * @throws Exception En caso de que exista un error en el metodo en general. 
	 */
	public ReservaVO getReserva (Long idCliente, Long idReserva) throws SQLException, Exception {
		
		ReservaVO objetoRetorno = null;

		String sql = String.format("SELECT * FROM %1$s.CLIENTES NATURAL INNER JOIN %1$s.RESERVAS WHERE ID_CLIENTE = %2$d AND ID_RESERVA = %3$d", USUARIO, idCliente, idReserva); 
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		
		resources.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		if(rs.next()) {
			objetoRetorno = convertResultSetToReservaVO(rs);
		}
		return objetoRetorno;	
	}
	
	// GET ALL
	/**
	 * Metodo para obtener todas las reservas de un cliente con un identificador especifico en la base de datos.
	 * @param id Identificador del Cliente que tiene las reservas y se desea buscar en la base de datos.
	 * @return Una lista de objetos de tipo ReservaVO con sus atributos. 
	 * @throws SQLException En caso de que exista un error en la conexi�n o en la sentencia SQL.
	 * @throws Exception En caso de que exista un error en el metodo en general. 
	 */
	public ArrayList <ReservaVO> getReservasCliente (Long idCliente) throws SQLException, Exception {
		
		ArrayList <ReservaVO> listaRetorno = new ArrayList<>();
		String sql = String.format("SELECT * FROM %1$s.CLIENTES NATURAL INNER JOIN %1$s.RESERVAS WHERE ID_CLIENTE = %2$d", USUARIO, idCliente);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		resources.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			listaRetorno.add(convertResultSetToReservaVO(rs));
		}
		return listaRetorno;
	}
	
	
	// POST
	/**
	 * Crea un cliente en la base de datos a trav�s de un VO del mismo tipo.
	 * @param newObject Reserva referencia para poder crear la informacion en la BD.
	 * @throws SQLException En caso de que exista un problema en la conexion o en la sentencia SQL.
	 * @throws Exception En caso de que exista un problema con el metodo en general.
	 */
	public ReservaVO createReserva (Long pIdCliente, ReservaVO newObject) throws SQLException, Exception {
		
		cliente = new DAOCliente();
		ClienteVO buscado = getCliente(pIdCliente);
		
		if(buscado != null) {
			
			// Apartamento: arrendada por minimo 30 dias.
			if(buscado.getPreferenciasAlojamiento() == "APARTAMENTO"){
				
				if (newObject.getNochesReserva() <30){
					throw new Exception("La habitacion debe ser arrendada por minimo una noche.");
				}
				
				ArrayList<ApartamentoVO> arrayObj = cliente.getClientePreferenciasApto(pIdCliente);
				boolean hayLibres = !arrayObj.isEmpty();
				
				if(hayLibres == false){
					throw new Exception("No hay apartamentos libres para generar la reserva.");
				}	
			}
			
			// Vivienda Esporadica: puede ser arrendada 30 veces al a�o.
			else if (buscado.getPreferenciasAlojamiento() == "VIVIENDAESP"){
				
				ArrayList<ViviendaEsp> arrayObj = cliente.getClientePreferenciasViviendaEsp(pIdCliente);
				boolean hayLibres = !arrayObj.isEmpty();
				
				if(hayLibres == false){
					throw new Exception("No hay viviendas libres para generar la reserva.");
				}
				
				ViviendaEsp vivReserva = arrayObj.get(0);
				
				if (vivReserva.getNochesAnio() + newObject.getNochesReserva() > 30){
					throw new Exception("Esta vivienda ya ha sido arrendada su maximo de veces");
				}
			}
			
			
			else if (buscado.getPreferenciasAlojamiento() == "HOSTAL"){
				ArrayList<HostalVO> arrayObj = cliente.getClientePreferenciasHostal(pIdCliente);
				boolean hayLibres = !arrayObj.isEmpty();
				
				if(hayLibres == false){
					throw new Exception("No hay hostales libres para generar la reserva.");
				}
			}
			
			else if (buscado.getPreferenciasAlojamiento() == "HOTEL"){
				ArrayList<HabitacionHoteleraVO> arrayObj = cliente.getClientePreferenciasHotel(pIdCliente);
				boolean hayLibres = !arrayObj.isEmpty();
				if(hayLibres == false){
					throw new Exception("No hay hoteles libres para generar la reserva.");
				}
			}
			
			// Habitacion: minimo por 30 dias.
			else if (buscado.getPreferenciasAlojamiento() == "HABITACIONHOTELERA"){
				
				if (newObject.getNochesReserva() <30){
					throw new Exception("La habitacion debe ser arrendada por minimo una noche.");
				}
				
				ArrayList <HabitacionVO> arrayObj = cliente.getClientePreferenciasHabitacion(pIdCliente);
				boolean hayLibres = !arrayObj.isEmpty();
				
				if(hayLibres == false){
					throw new Exception("No hay viviendas libres para generar la reserva.");
				}
			}
			
			// Vivienda Universitaria: arrendada para tipos de personas y minimo una noche.
			else if (buscado.getPreferenciasAlojamiento() == "HABITACIONUNIV"){
				
				if (newObject.getNochesReserva() < 1){
					throw new Exception("Esta vivienda debe ser arrendada por minimo una noche.");
				}
				
				if (buscado.getRelacionUniandes() == "persona_estudiante" || buscado.getRelacionUniandes() == "persona_profesor"
						|| buscado.getRelacionUniandes() == "persona_empleado" || buscado.getRelacionUniandes() == "persona_visitante") {
					
					ArrayList <HabitacionUniversitariaVO> arrayObj = cliente.getClientePreferenciasViviendaUniv(pIdCliente);
					boolean hayLibres = !arrayObj.isEmpty();
					
					if(hayLibres == false){
						throw new Exception("No hay viviendas libres para generar la reserva.");
					}
				}
			}
		}
		
		else {throw new Exception("El cliente solicitado no existe en la base de datos.");}

		//----------------------------------------------------------------------------------
		// En caso de que haya un alojamiento libre de su preferencia, se genera la reserva
		//----------------------------------------------------------------------------------
		newObject.setIdClienteTitular(pIdCliente);
		String SQL = String.format("INSERT INTO %1$s.RESERVAS VALUES (" + newObject.getId() + ", " + newObject.getFechaInicioReserva()
																+ ", " + newObject.getFechaFinReserva() + ", " + buscado.getId()  
																+ ", " + newObject.getIdAlojamientoReserva() + ", " 
																+ newObject.getNochesReserva() + ")",
				USUARIO,
				newObject.getId(),
				newObject.getFechaInicioReserva(),
				newObject.getFechaFinReserva(),
				buscado.getId(),
				newObject.getIdAlojamientoReserva(),
				newObject.getNochesReserva());
		
		System.out.println(SQL);
		PreparedStatement prepStmt = conn.prepareStatement(SQL);
		resources.add(prepStmt);
		prepStmt.executeQuery();
		return newObject;
	}
	
	//DELETE
	/**
	 * Elimina el cliente asociado con el identificador dado de la Base de Datos.
	 * @param oldUser Cliente referencia, el cual se desea borrar (haciendo uso de su ID)
	 * @throws SQLException En caso de que exista un problema con la conexion o la sentencia SQL
	 * @throws Exception En caso de que existan un problema en el metodo en general.
	 */
	public void deleteReserva (Long idCliente, Long idReserva) throws SQLException, Exception{
		
		ReservaVO existe = getReserva(idCliente, idReserva);
		double costoCancelacion = 0;
		
		if (existe != null) {
			
			Long idClienteX = existe.getIdClienteTitular();
			ClienteVO clienteObj = getCliente(idClienteX);
			
			if (clienteObj != null) {
				
				if (clienteObj.getPreferenciasAlojamiento() == "Apartamento" ||
				    clienteObj.getPreferenciasAlojamiento() == "Habitacion" ||
				    clienteObj.getPreferenciasAlojamiento() == "Hotel") {
					
					Date fechaReserva = existe.getFechaInicioReserva();
					Date fechaCancelacion = new Date();
					int diasDiferencia = (int) ((fechaCancelacion.getTime()-fechaReserva.getTime())/86400000);
					
					if (diasDiferencia < 7) {
						
					}
					
					else {
						
					}	
				}
				
				else if (clienteObj.getPreferenciasAlojamiento() == "Viv_Esp" ||
						 clienteObj.getPreferenciasAlojamiento() == "Viv_Univ"||
						 clienteObj.getPreferenciasAlojamiento() == "Hostal") {
					
					Date fechaReserva = existe.getFechaInicioReserva();
					Date fechaCancelacion = new Date();
					int diasDiferencia = (int) ((fechaCancelacion.getTime()-fechaReserva.getTime())/86400000);
					
					if (diasDiferencia < 3) {
						
					}
					
					else {
						
					}
					
				}
				
				String SQL = String.format("DELETE FROM %1$s.RESERVAS WHERE ID_RESERVA = %2$d AND ID_CLIENTE = %3$d ", USUARIO, idReserva, idCliente);
				System.out.println(SQL);
				PreparedStatement prepStmt = conn.prepareStatement(SQL);
				resources.add(prepStmt);
				prepStmt.executeQuery();
				//conn.commit();
				
				System.out.println("El cliente deber� pagar " + costoCancelacion);
			}
		}
		
		else {throw new Exception("La reserva no existe, por favor revise su solicitud.");}
	}
	
	// GET UNIQUE
	public Date getReservaUniqueFecha (Long idReserva) throws SQLException, Exception {
		
		Date objetoRetorno = null;

		String sql = String.format("SELECT FECHA_FINAL_RESERVA FROM %1$s.RESERVAS WHERE ID_RESERVA = %2$d", USUARIO, idReserva); 
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		
		resources.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		if(rs.next()) {
			objetoRetorno = convertResultSetToFechaFinal(rs);
		}
		return objetoRetorno;	
	}
	
	// TODO: PARA ACORDARME DONDE ESTA LA ITERACION 2: GGG.
	//-----------------------------------------------------------------
	// ITERACION NO.2
	//-----------------------------------------------------------------
	
	// CREATE MASIVA
	/**
	 * Crea una reserva de manera masiva, es decir, muchas reservas de manera secuencial.
	 * @param pIDCliente Cliente que desea realizar la reserva.
	 * @param reservas Lista de reservas que se desean crear.
	 * @return La lista de reservas que se crearon en la BD.
	 * @throws SQLException En caso de un error en la transacci�n SQL.
	 * @throws Exception En caso de un error en general.
	 */
	public ArrayList<ReservaVO> createMasivaReservas (Long pIDCliente, ArrayList<ReservaVO> reservas) throws SQLException, Exception{
		
		for (ReservaVO reservaCreate : reservas){
			createReserva(pIDCliente, reservaCreate);
		}
		return reservas;
	}
	
	// DELETE MASIVA:
	/**
	 * Se elimina una lista masiva de reservas asociadas a un cliente especifico.
	 * @param pIDCliente Cliente que desea realizar la cancelacion.
	 * @param reservas Lista de reservas que se desea eliminar.
	 * @throws SQLException En caso de que exista un problema con la sentencia SQL.
	 * @throws Exception En caso de que exista un problema en general.
	 */
	public void deleteMasivaReservas (Long pIDCliente, ArrayList<CancelacionMasivaVO> reservas) throws SQLException, Exception{
	
		// Lista de las reservas (dadas) que en verdad existen en la base de datos.
		ArrayList<ReservaVO> reservasExistentes = new ArrayList<>();
				
		// Ciclo para verificar la existencia de todas las reservas.
		for (CancelacionMasivaVO cancelacion: reservas){
			ReservaVO existe = getReserva(pIDCliente, cancelacion.getIdReservaCancelar());
			
			if (existe != null){
				reservasExistentes.add(existe);
			}
		}
		
		// En caso de que existan todas las reservas, se eliminan todas de la lista dada.
		// En caso de que no, se creo una lista auxiliar para obtener las reservas que 
		// existen para asi eliminar estas e ignorar aquellas inexistentes.
		
		for (ReservaVO cancelacionDos: reservasExistentes){
			
			Long idClienteX = cancelacionDos.getIdClienteTitular();
			ClienteVO clienteObj = getCliente(idClienteX);
			
			if (clienteObj != null){
				deleteReserva(pIDCliente, cancelacionDos.getId());
			}
		}
	}
	
	//----------------------------------------------------
	// Metodos conexion - Auxiliares
	//----------------------------------------------------
	
	/**
	 * Metodo encargado de inicializar la conexion del DAO a la Base de Datos a partir del parametro <br/>
	 * <b>Postcondicion: </b> el atributo conn es inicializado <br/>
	 * @param connection la conexion generada en el TransactionManager para la comunicacion con la Base de Datos
	 */
	public void setConn(Connection connection){
		this.conn = connection;
	}
	
	/**
	 * Metodo que cierra todos los recursos que se encuentran en el arreglo de recursos<br/>
	 * <b>Postcondicion: </b> Todos los recurso del arreglo de recursos han sido cerrados.
	 */
	public void cerrarRecursos() {
		for(Object ob : resources){
			if(ob instanceof PreparedStatement)
				try {((PreparedStatement) ob).close();} 
				catch (Exception ex) {ex.printStackTrace();}
		}
	}
	
	/**
	 * Metodo que transforma el resultado obtenido de una consulta SQL (sobre la tabla Clientes) en una instancia de la clase Cliente.
	 * @param resultSet ResultSet con la informacion de un cliente que se obtuvo de la base de datos.
	 * @return Objeto de tipo Cliente, cuyos atributos corresponden a los valores asociados a un registro particular de la tabla Cliente.
	 * @throws SQLException Si existe algun problema al extraer la informacion del ResultSet.
	 */
	public ReservaVO convertResultSetToReservaVO (ResultSet resultSet) throws SQLException {
		
		Long pID = resultSet.getLong("ID_RESERVA");
		Integer pNoches = resultSet.getInt("NOCHES_RESERVA");
		Date pInicio = resultSet.getDate("FECHA_INICIO_RESERVA");
		Date pFin = resultSet.getDate("FECHA_FIN_RESERVA");
		Long pCedulaCliente = resultSet.getLong("CEDULA");
		Long pIdCliente = resultSet.getLong("ID_CLIENTE");
		String pNombreAlojamiento = resultSet.getString("PREFERENCIA_ALOJAMIENTO");
		Long pIdAlojamiento = resultSet.getLong("ID_ALOJAMIENTO");
		
		ReservaVO objRetorno = new ReservaVO(pID, pNoches, pInicio, pFin, pIdCliente, pCedulaCliente, pIdAlojamiento, pNombreAlojamiento);
		return objRetorno;
	}
	
	
	public Date convertResultSetToFechaFinal (ResultSet resultSet) throws SQLException {
		Date pFin = resultSet.getDate("FECHA_FINAL_RESERVA");
		return pFin;
	}
	
	
	/**
	 * Metodo que transforma el resultado obtenido de una consulta SQL (sobre la tabla Clientes) en una instancia de la clase Cliente.
	 * @param resultSet ResultSet con la informacion de un cliente que se obtuvo de la base de datos.
	 * @return Objeto de tipo Cliente, cuyos atributos corresponden a los valores asociados a un registro particular de la tabla Cliente.
	 * @throws SQLException Si existe algun problema al extraer la informacion del ResultSet.
	 */
	public ClienteVO convertResultSetToClienteVO (ResultSet resultSet) throws SQLException, Exception{

		Long id = resultSet.getLong("ID_CLIENTE");
		Integer pEdad = resultSet.getInt("EDAD");
		String pApellido = resultSet.getString("APELLIDO");
		String pNombre = resultSet.getString("NOMBRE");
		String pPreferencias = resultSet.getString("PREFERENCIA_ALOJAMIENTO");
		String pRelacionUniandes = resultSet.getString("RELACION_UNIANDES");
		ArrayList <ReservaVO> pReservas = new ArrayList<>() ;

		ClienteVO objRetorno = new ClienteVO(id, pEdad, pApellido, pNombre, pPreferencias, pRelacionUniandes, pReservas);
		return objRetorno;
	}
}