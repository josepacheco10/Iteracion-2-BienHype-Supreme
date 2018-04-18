package dao;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import vos.Alojamiento_ReservaVO;
import vos.AnalisisOperacionVO;
import vos.ApartamentoVO;
import vos.CancelacionMasivaVO;
import vos.ClienteVO;
import vos.MayorDemandaVO;
import vos.MayorGananciaVO;
import vos.ReservaVO;
import vos.ServicioVO;

/**
 * Clase DAO que se conecta la base de datos usando JDBC para resolver los requerimientos de la aplicacion.
 * @author Grupo C-13
 */
public class DAOApartamento {

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
	private ArrayList<Object> resources;

	/**
	 * Atributo que genera la conexion a la base de datos
	 */
	private Connection conn;
	
	// DAO externos necesarios para realizar requerimientos:
	/**
	 * DAO de la reserva que se desea manipular dentro de los requerimientos.
	 */
	private DAOReserva daoReserva;
	
	/**
	 * Dao de los servicios
	 */
	private DAOServicio servicios;
	
	//----------------------------------------------------
	// Constructor inicialización
	//----------------------------------------------------
	
	public DAOApartamento (){
		resources = new ArrayList<>();
	}
	
	//----------------------------------------------------
	// Metodos Arquitectura REST: 
	//----------------------------------------------------
	
	// GET BY ID
	/**
	 * Metodo para obtener un apartamento con un identificador especifico asociado a un operador dado en la base de datos.
	 * @param idOperador Identificador del Operador que se desea buscar en la base de datos.
	 * @param idApartamento Identificador del apartamento que se desea buscar.
	 * @return Un objeto de tipo ClienteVO con sus atributos. 
	 * @throws SQLException En caso de que exista un error en la conexión o en la sentencia SQL.
	 * @throws Exception En caso de que exista un error en el metodo en general. 
	 */
	public ApartamentoVO getApartamento (Long idOperador, Long idApartamento) throws SQLException, Exception {
		
		ApartamentoVO objetoRetorno = null;

		String sql = String.format("SELECT * FROM %1$s.APARTAMENTOS NATURAL INNER JOIN %1$s.ALOJAMIENTOAPARTAMENTO NATURAL INNER JOIN %1$s.ALOJAMIENTOS NATURAL INNER JOIN %1$s.RESERVAS WHERE ID_OPERADOR = %2$d AND ID_APARTAMENTO = %3$d", USUARIO, idOperador, idApartamento); 
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		
		resources.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		if(rs.next()) {
			objetoRetorno = convertResultSetToApartamentoVO(rs);
		}
		return objetoRetorno;	
	}
	
	// GET ALL
	/**
	 * Metodo para obtener todas los apartamentos de un operador con un identificador especifico en la base de datos.
	 * @param id Identificador del Operador que tiene los apartamentos y se desea buscar en la base de datos.
	 * @return Una lista de objetos de tipo ApartamentoVO con sus atributos. 
	 * @throws SQLException En caso de que exista un error en la conexión o en la sentencia SQL.
	 * @throws Exception En caso de que exista un error en el metodo en general. 
	 */
	public ArrayList <ApartamentoVO> getApartamentos (Long idOperador) throws SQLException, Exception {
		
		ArrayList <ApartamentoVO> listaRetorno = new ArrayList<>();
		String sql = String.format("SELECT * FROM %1$s.APARTAMENTOS NATURAL INNER JOIN %1$s.ALOJAMIENTOAPARTAMENTO NATURAL INNER JOIN %1$s.ALOJAMIENTOS NATURAL INNER JOIN %1$s.RESERVAS WHERE ID_OPERADOR = %2$d", USUARIO, idOperador);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		resources.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			listaRetorno.add(convertResultSetToApartamentoVO(rs));
		}
		return listaRetorno;
	}
	
	
	/**
	 * Obtiene todos los apartamentos del sistema sin restricciones.
	 * @return ArrayList con los apartamentos existentes en la base de datos.
	 * @throws SQLException En caso de que exista un problema en la sentencia o la conexion.
	 * @throws Exception En caso de que exista un problema en general.
	 */
	public ArrayList <ApartamentoVO> getApartamentosUnique() throws SQLException, Exception {
		
		ArrayList <ApartamentoVO> listaRetorno = new ArrayList<>();
		String sql = String.format("SELECT * FROM %1$s.APARTAMENTOS NATURAL INNER JOIN %1$s.ALOJAMIENTOAPARTAMENTO NATURAL INNER JOIN %1$s.ALOJAMIENTOS NATURAL INNER JOIN %1$s.RESERVAS WHERE CAPACIDAD + 1 < %2$s", USUARIO, ApartamentoVO.CAPACIDAD_MAX_APTO);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		resources.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			listaRetorno.add(convertResultSetToApartamentoVO(rs));
		}
		return listaRetorno;
	}
	
	//DELETE
	/**
	 * Elimina el cliente asociado con el identificador dado de la Base de Datos.
	 * @param oldUser Cliente referencia, el cual se desea borrar (haciendo uso de su ID)
	 * @throws SQLException En caso de que exista un problema con la conexion o la sentencia SQL
	 * @throws Exception En caso de que existan un problema en el metodo en general.
	 */
	public void deleteAlojamiento (Long idOperador, Long idApartamento) throws SQLException, Exception {
		daoReserva = new DAOReserva();
		ApartamentoVO apartamento = getApartamento(idOperador, idApartamento);
		if (apartamento != null) {
			
			if (apartamento.getIdReservas() == 0){
				String SQL = String.format("DELETE FROM %1$s.APARTAMENTOS WHERE ID_APARTAMENTO = %2$d AND ID_OPERADOR = %3$d", USUARIO, idApartamento, idOperador);
				System.out.println(SQL);
				
				PreparedStatement prepStmt = conn.prepareStatement(SQL);
				resources.add(prepStmt);
				prepStmt.executeQuery();
			}
			
			else if (apartamento.getIdReservas() != 0){
				
				Long identificadorReserva = apartamento.getIdReservas();
				Date fechaFinReserva = daoReserva.getReservaUniqueFecha(identificadorReserva);
				Date hoy = new Date();
				
				if (hoy.after(fechaFinReserva)){
					String SQL = String.format("DELETE FROM %1$s.APARTAMENTOS WHERE ID_APARTAMENTO = %2$d AND ID_OPERADOR = %3$d", USUARIO, idApartamento, idOperador);
					
					System.out.println(SQL);
					PreparedStatement prepStmt = conn.prepareStatement(SQL);
					resources.add(prepStmt);
					prepStmt.executeQuery();
				}
			}
			else {throw new Exception("No es posible retirar el alojamiento ya que este cuenta con reservas vigentes");}
		}
		else {throw new Exception("El apartamentos que desea eliminar no existe o no está asociado a usted.");}
	}
	
	
	// TODO PARA ACORDARME DONDE ESTA LA SEGUNDA IT: GGGGG.
	//-------------------------------------------------------
	// 	ITERACION NO.2
	//-------------------------------------------------------
	
	// RF09
	public ArrayList <ReservaVO> deshabilitarApartamento (Long idOperador, Long idAlojamiento) throws SQLException, Exception{
		
		// Dado que no se especifica que retornar, se procede a retornar las reservas re-asociadas.
		ArrayList <ReservaVO> retorno = new ArrayList<>();
		
		// Verifico si el apartamento en verdad existe.
		ApartamentoVO existe = getApartamento(idOperador, idAlojamiento);
		
		if (existe != null){
			
			// Se cambia el valor de la disponibilidad del alojamiento:
			actualizarDisponibilidad(idAlojamiento, false);
			
			// Se reedirigen las reservas a otros alojamientos:
			
			
			// Se eliminan las reservas asociadas a ese alojamiento: 
			ArrayList<CancelacionMasivaVO> cancelaciones = new ArrayList<>();
			retorno = getReservasApto(idOperador, idAlojamiento);
			
			for (ReservaVO reserva : retorno){
				CancelacionMasivaVO cancel = new CancelacionMasivaVO(reserva.getId(), "Motivos externos");
				cancelaciones.add(cancel);
			}
			
			deleteMasivaReservasAlojamiento(idOperador, idAlojamiento, cancelaciones);
		}
		return retorno;
	}
	
	/**
	 * Obtiene todas las reservas asociadas a un apartamento dado
	 * @param idApartamento Identificador del apartamento solicitado
	 * @return Lista de las reservas solicitadas.
	 * @throws SQLException En caso de que se genere un error en la transaccion.
	 */
	public ArrayList <ReservaVO> getReservasApto (Long idOperador, Long idApartamento) throws SQLException{
		
		ArrayList <ReservaVO> listaRetorno = new ArrayList<>();
		String sql = String.format("SELECT * FROM %1$s.APARTAMENTOS_RESERVAS WHERE ID_APARTAMENTO = %2$d AND ID_OPERADOR = %3$d", USUARIO, idApartamento, idOperador);
		
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		resources.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			listaRetorno.add(convertResultSetToReservaVO(rs));
		}
		
		return listaRetorno;
	}
	
	// RF10
	/**
	 * Actualiza el valor de disponibilidad de un alojamiento a activo.
	 * @param pIdAlojamiento
	 * @throws SQLException
	 */
	public void reHabilitarAlojamiento (Long pIdAlojamiento) throws SQLException{
		actualizarDisponibilidad(pIdAlojamiento, true);
	}
	
	/**
	 * Transaccion para actualizar la disponibilidad de un apartamento.
	 * @param pIdApartamento Apartamento que se desea actualizar.
	 * @param pDisponible Si el apartamento pasa a estar disponible o no.
	 * @throws SQLException En caso de un error en la transaccion.
	 */
	public void actualizarDisponibilidad (Long pIdApartamento, boolean pDisponible) throws SQLException{
		
		int disponibleInt = pDisponible == true ? 1:0;
		
		StringBuilder sql = new StringBuilder();
		sql.append(String.format("UPDATE %s.APARTAMENTOS ", USUARIO));
		sql.append(String.format("SET IS_DISPONIBLE = %1$s", disponibleInt));
		sql.append(String.format("WHERE ID_APARTAMENTO = %1$d", pIdApartamento));
		System.out.println(sql);

		PreparedStatement prepStmt = conn.prepareStatement(sql.toString());
		resources.add(prepStmt);
		prepStmt.executeQuery();
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
	
	//----------------------------------------------------
	// Metodos transferencia RS -> VO
	//----------------------------------------------------
	
	public ApartamentoVO convertResultSetToApartamentoVO (ResultSet resultSet) throws SQLException, Exception {
		
		servicios = new DAOServicio();
		Long id = resultSet.getLong("ID_APARTAMENTO");
		Integer pCapacidad = resultSet.getInt("CAPACIDAD");
		String pDireccion = resultSet.getString("DIRECCION");
		String pNombre = resultSet.getString("NOMBRE");
		Double pCosto = resultSet.getDouble("COSTO");
		String pTipoAlojamiento = resultSet.getString("TIPO_ALOJAMIENTO");
		ArrayList <ServicioVO> pServicios = servicios.getServiciosApto(id);
		Long pIdReserva = resultSet.getLong("ID_RESERVA");
		Long pIdOp = resultSet.getLong("ID_OPERADOR");
		Double pCostoAdmin = resultSet.getDouble("COSTO_ADMIN");
		Boolean pIsAmoblado = resultSet.getBoolean("IS_AMOBLADO");
		Boolean pDisponible = resultSet.getBoolean("DISPONIBLE");
		
		ApartamentoVO objRetorno = new ApartamentoVO(id, pCapacidad, pDireccion, pNombre, pCosto, pTipoAlojamiento, 
				pServicios, pIdReserva, pIsAmoblado, pCostoAdmin, pIsAmoblado, pIdOp, pDisponible);
		
		return objRetorno;
	}
	
	
	//----------------------------------------------------
	// Metodos transferencia RS -> VO: Demás clases
	//----------------------------------------------------
	
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
	
	//----------------------------------------------------------
	// RF4 - RF5 (Se incluyen operaciones masivas)
	//----------------------------------------------------------
	
	// GET Reserva
	// GET BY ID
	/**
	 * Metodo para obtener un cliente con un identificador especifico en la base de datos.
	 * @param id Identificador del Cliente que se desea buscar en la base de datos.
	 * @return Un objeto de tipo ClienteVO con sus atributos. 
	 * @throws SQLException En caso de que exista un error en la conexión o en la sentencia SQL.
	 * @throws Exception En caso de que exista un error en el metodo en general. 
	 */
	public ReservaVO getReservaByAlojamiento (Long idOperador, Long idAlojamiento, Long idReserva) throws SQLException, Exception {
		
		ReservaVO objetoRetorno = null;

		String sql = String.format("SELECT * FROM %1$s.APARTAMENTOS_RESERVAS WHERE ID_OPERADOR = %2$d AND ID_APARTAMENTO = %3$d AND ID_RESERVA = %4$d", USUARIO, idOperador, idAlojamiento, idReserva); 
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		
		resources.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		if(rs.next()) {
			objetoRetorno = convertResultSetToReservaVO(rs);
		}
		return objetoRetorno;	
	}
	
	
	//DELETE
	/**
	 * Elimina el cliente asociado con el identificador dado de la Base de Datos.
	 * @param oldUser Cliente referencia, el cual se desea borrar (haciendo uso de su ID)
	 * @throws SQLException En caso de que exista un problema con la conexion o la sentencia SQL
	 * @throws Exception En caso de que existan un problema en el metodo en general.
	 */
	public void deleteReservaByAlojamiento (Long idOperador, Long idAlojamiento, Long idReserva) throws SQLException, Exception{
		
		ReservaVO existe = getReservaByAlojamiento(idOperador, idAlojamiento, idReserva);
		if (existe != null) {
				String SQL = String.format("DELETE FROM %1$s.APARTAMENTOS_RESERVAS WHERE ID_RESERVA = %2$d AND ID_APARTAMENTO = %3$d AND ID_OPERADOR = %4$d", USUARIO, idReserva, idAlojamiento, idAlojamiento);
				System.out.println(SQL);
				PreparedStatement prepStmt = conn.prepareStatement(SQL);
				resources.add(prepStmt);
				prepStmt.executeQuery();
				//conn.commit();
		}
		else {throw new Exception("La reserva no existe, por favor revise su solicitud.");}
	}

	// Borrado masivo de acuerdo al identificador del alojamiento:
	// DELETE MASIVA:
	/**
	 * Se elimina una lista masiva de reservas asociadas a un cliente especifico.
	 * @param pIDCliente Cliente que desea realizar la cancelacion.
	 * @param reservas Lista de reservas que se desea eliminar.
	 * @throws SQLException En caso de que exista un problema con la sentencia SQL.
	 * @throws Exception En caso de que exista un problema en general.
	 */
	public void deleteMasivaReservasAlojamiento (Long idOperador, Long pIdAlojamiento, ArrayList<CancelacionMasivaVO> reservas) throws SQLException, Exception{
	
		// Lista de las reservas (dadas) que en verdad existen en la base de datos.
		ArrayList<ReservaVO> reservasExistentes = new ArrayList<>();
				
		// Ciclo para verificar la existencia de todas las reservas.
		for (CancelacionMasivaVO cancelacion: reservas){
			ReservaVO existe = getReservaByAlojamiento(idOperador, pIdAlojamiento, cancelacion.getIdReservaCancelar());
			if (existe != null){
				reservasExistentes.add(existe);
			}
		}
		
		// En caso de que existan todas las reservas, se eliminan todas de la lista dada.
		// En caso de que no, se creo una lista auxiliar para obtener las reservas que 
		// existen para asi eliminar estas e ignorar aquellas inexistentes.
		
		for (ReservaVO cancelacionDos: reservasExistentes){
			deleteReservaByAlojamiento(idOperador, pIdAlojamiento, cancelacionDos.getId());
		}
	}
	
	
	// TODO: RFC
	//-------------------------------------------------------
	// 	Requerimientos funcionales de consulta (RFC):
	//-------------------------------------------------------
	
	// RFC7: 
	public AnalisisOperacionVO analisisInformacion() throws SQLException{
		
		// INIC
		AnalisisOperacionVO retorno = new AnalisisOperacionVO("X", "XX", "XXX");
		
		// Fecha mayor demanda:
		MayorDemandaVO mayorDemanda = obtenerMayorDemanda();
		
		// Fecha mayor ganancia:
		MayorGananciaVO mayorGanancia = obtenerMayorGanancia();
		
		// Fecha menor demanda: se usa la misma clase, pero se cambia la sentencia.
		MayorDemandaVO menorDemanda = obtenerMenorDemanda();
		
		// Mayor demanda:
		retorno.setFechasMayorDemanda("La fecha con mayor demanda fue: " 
		+ mayorDemanda.getFechaMayor().toString() + " con un total de clientes de: " + mayorDemanda.getMax());
		
		// Mayor gancia: 
		retorno.setFechasMayorIngreso("La fecha con mayor ganancia fue: "
		+ mayorGanancia.getFechaMayor().toString() + " con una ganancia de: " + mayorGanancia.getCostoGanado());
		
		// Menor demanda:
		retorno.setFechasMenorDemanda("La fecha con menor demanda fue: " 
		+ menorDemanda.getFechaMayor().toString() + " con un total de clientes de: " + mayorDemanda.getMax());
		
		
		return retorno;
	}
	
	//-------------------------------------------------------
	// 	Requerimientos funcionales de consulta RFC): Aux
	//-------------------------------------------------------
	
	//------------------ MAYOR DEMANDA -------------------------------
	
	public MayorDemandaVO obtenerMayorDemanda() throws SQLException{
		MayorDemandaVO retorno = new MayorDemandaVO(new Date(), 0);
		
		String sql = String.format("SELECT * FROM (SELECT FECHA_INICIO_RESERVA, COUNT(*) AS MAXIMO" +
								   " FROM %1$s.APARTAMENTOS_RESERVAS" + 
								   " GROUP BY FECHA_INICIO_RESERVA" +
								   " ORDER BY MAXIMO DESC) WHERE ROWNUM = 1;",	 
								   USUARIO); 
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		resources.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		if(rs.next()) {retorno = convertResultSetToMayorDemanda(rs);}
		return retorno;
	}
	
	/**
	 * Convertir un resultado de una consulta en un objeto de Mayor Demanda.
	 * @param resultSet Resultado de la consulta.
	 * @return Un objeto de la clase dada.
	 * @throws SQLException En caso de error en la sentencia
	 */
	public MayorDemandaVO convertResultSetToMayorDemanda(ResultSet resultSet) throws SQLException {
		Date pFecha = resultSet.getDate("FECHA_INICIO_RESERVA");
		Integer pMax = resultSet.getInt("MAXIMO");
		MayorDemandaVO create = new MayorDemandaVO(pFecha, pMax);
		return create;
	}
	
	
	//------------------ MAYOR GANANCIA -------------------------------
	
	public MayorGananciaVO obtenerMayorGanancia() throws SQLException{
		MayorGananciaVO retorno = new MayorGananciaVO(new Date(), 0.0);
		
		String sql = String.format("SELECT (COSTO + COSTO_ADMIN) AS MAXIMO, FECHA_INICIO_RESERVA FROM %1$s.APARTAMENTOS_RESERVAS WHERE ROWNUM = 1 ORDER BY MAXIMO DESC", USUARIO); 
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		resources.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		if(rs.next()) {retorno = convertResultSetToMayorGanancia(rs);}
		return retorno;
	}
	
	public MayorGananciaVO convertResultSetToMayorGanancia(ResultSet resultSet) throws SQLException {
		Date pFecha = resultSet.getDate("FECHA_INICIO_RESERVA");
		Double pMax = resultSet.getDouble("MAXIMO");
		MayorGananciaVO create = new MayorGananciaVO(pFecha, pMax);
		return create;
	}
	
	//------------------ MENOR DEMANDA -------------------------------
	
	public MayorDemandaVO obtenerMenorDemanda() throws SQLException{
		MayorDemandaVO retorno = new MayorDemandaVO(new Date(), 0);
		
		String sql = String.format("SELECT * FROM (SELECT FECHA_INICIO_RESERVA, COUNT(*) AS MAXIMO" +
								   " FROM %1$s.APARTAMENTOS_RESERVAS" + 
								   " GROUP BY FECHA_INICIO_RESERVA" +
								   " ORDER BY MAXIMO ASC) WHERE ROWNUM = 1;",	 
								   USUARIO); 
		PreparedStatement prepStmt = conn.prepareStatement(sql);
		resources.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		if(rs.next()) {retorno = convertResultSetToMayorDemanda(rs);}
		return retorno;
	}
	
	//-------------------------------------------------------------------------------------------
	// RFC8: 
	
	public ArrayList<ClienteVO> getClientesFrecuentes (Long idAlojamiento) throws Exception {
		
		ArrayList <ClienteVO> retorno = new ArrayList<>();
		
		String sql = String.format("SELECT * FROM %1$s.CLIENTES NATURAL INNER JOIN "
				+ "(SELECT ID_CLIENTE, ID_APARTAMENTO, NOCHES_RESERVA FROM %1$s.APARTAMENTOS_RESERVAS) "
				+ "WHERE ID_APARTAMENTO = %2$d. AND NOCHES_RESERVA >= 15", USUARIO, idAlojamiento);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		resources.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			retorno.add(convertResultSetToClienteVO(rs));
		}
		
		ArrayList<ClienteVO> clientesPorCantidadReservas = getClientesByReservas(idAlojamiento);
		
		if(clientesPorCantidadReservas.isEmpty()){
			return retorno;
		}
		
		else {
			for(ClienteVO cl : clientesPorCantidadReservas){retorno.add(cl);}
			return retorno;
		}
		
	}
	
	//----------------------- GET CLIENTES RESERVA > 3 -----------------------------------------
	
	public ArrayList<ClienteVO> getClientesByReservas (Long idAlojamiento) throws Exception{
		
		ArrayList <ClienteVO> retorno = new ArrayList<>();
		
		String sql = String.format("SELECT * FROM %1$s.CLIENTES NATURAL INNER JOIN(SELECT ID_CLIENTE, "
				+ "COUNT(*) AS CANTIDAD FROM (SELECT * FROM %1$s.CLIENTES NATURAL "
				+ "INNER JOIN(SELECT ID_CLIENTE, ID_APARTAMENTO, NOCHES_RESERVA FROM %1$s.APARTAMENTOS_RESERVAS) WHERE ID_APARTAMENTO = %2$d) GROUP BY ID_CLIENTE) WHERE CANTIDAD >= 3", USUARIO, idAlojamiento);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		resources.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			retorno.add(convertResultSetToClienteVO(rs));
		}
		
		return retorno;
	}

	/**
	 * Metodo que transforma el resultado obtenido de una consulta SQL (sobre la tabla Clientes) en una instancia de la clase Cliente.
	 * @param resultSet ResultSet con la informacion de un cliente que se obtuvo de la base de datos.
	 * @return Objeto de tipo Cliente, cuyos atributos corresponden a los valores asociados a un registro particular de la tabla Cliente.
	 * @throws SQLException Si existe algun problema al extraer la informacion del ResultSet.
	 */
	public ClienteVO convertResultSetToClienteVO (ResultSet resultSet) throws SQLException, Exception{
		
		daoReserva = new DAOReserva();
		
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
	
	//-------------------------------------------------------------------------------------------
    // RFC9:
	public ArrayList<Alojamiento_ReservaVO> getAlojamientoFracasado () throws Exception{
		
		ArrayList<Alojamiento_ReservaVO> objetoRetorno = new ArrayList<>();
		
		Date fechaActual = new Date();
		
		ArrayList<Alojamiento_ReservaVO> order = getAlojamientosOrdenados();
		for (Alojamiento_ReservaVO aptos: order){
			int diasDiferencia = (int) ((fechaActual.getTime() - aptos.getFechaUltimaVisita().getTime())/86400000);
			if(diasDiferencia > 30){objetoRetorno.add(aptos);}
		}
		
		return objetoRetorno;	
	}
	
	public ArrayList<Alojamiento_ReservaVO> getAlojamientosOrdenados() throws Exception{
		
		ArrayList <Alojamiento_ReservaVO> retorno = new ArrayList<>();
		
		String sql = String.format("SELECT * FROM (SELECT * FROM %1$s.APARTAMENTOS_RESERVAS ORDER BY FECHA_FIN_RESERVA ASC)", USUARIO);

		PreparedStatement prepStmt = conn.prepareStatement(sql);
		resources.add(prepStmt);
		ResultSet rs = prepStmt.executeQuery();

		while (rs.next()) {
			retorno.add(convertToAlojamientoReserva(rs));
		}
		
		return retorno;
	}
	
	
	public Alojamiento_ReservaVO convertToAlojamientoReserva (ResultSet resultSet) throws SQLException {
		Long id = resultSet.getLong("ID_APARTAMENTO");
		String pNombre = resultSet.getString("NOMBRE");
		Date pFin = resultSet.getDate("FECHA_FIN_RESERVA");
		Alojamiento_ReservaVO retorno = new Alojamiento_ReservaVO(id, pFin, pNombre);
		return retorno;
	}
}