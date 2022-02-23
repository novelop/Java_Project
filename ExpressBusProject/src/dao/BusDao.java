package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import service.BusService;
import util.JDBCUtil;

public class BusDao {

	//싱글톤패턴 사용
	private BusDao() {
		
	}
	private static BusDao instanse; 
	public static BusDao getInstance() {
		if(instanse == null) { 
			instanse = new BusDao(); 
		}
		return instanse; 
	}
	
	//출발지 dao
	public List<Map<String, Object>> selectStart() {
		String sql = "SELECT ROWNUM,"
				+ "       TERMINAL_NAME"
				+ "  FROM TERMINAL";
		return JDBCUtil.selectList(sql);
	}
	
	//도착지 dao
	public List<Map<String, Object>> selectEnd(String startTerminal) {
		String sql = "SELECT ROWNUM,"
				+ "       TERMINAL_NAME"
				+ "  FROM TERMINAL"
				+ " WHERE TERMINAL_NAME != ?";
		List<Object> param = new ArrayList<Object>();
		param.add(startTerminal);
		
		return JDBCUtil.selectList(sql, param);
	}
	
	//출발일이 오늘 이후인 예약 dao
	public List<Map<String, Object>> selectRunAfter(Map<String, Object> param) {

		String sql = "SELECT A.RUN_ID,"
				+ "       A.BUS_ID,"
				+ "		  B.GRADE,"
				+ "       TO_CHAR(A.START_TIME,'HH24:MI') AS ST_TIME,"
				+ "       TO_CHAR(A.END_TIME,'HH24:MI') AS EN_TIME,"
				+ "       A.PRICE"
				+ "  FROM RUN A, BUS B"
				+ " WHERE A.BUS_ID = B.BUS_ID"
				+ "   AND START_TERMINAL IN(SELECT TERMINAL_ID"
				+ "                             FROM TERMINAL"
				+ "                            WHERE TERMINAL_NAME = ?)"
				+ "   AND END_TERMINAL IN(SELECT TERMINAL_ID"
				+ "                             FROM TERMINAL"
				+ "                            WHERE TERMINAL_NAME = ?)";
		
		List<Object> _param = new ArrayList<Object>();
		_param.add(param.get("START_TERMINAL"));
		_param.add(param.get("END_TERMINAL"));
		
		return JDBCUtil.selectList(sql, _param);
	}
	
	//예약 dao
	public List<Map<String, Object>> selectRun(Map<String, Object> param) {
		
		String sql = "SELECT A.RUN_ID,"
				+ "       A.BUS_ID,"
				+ "		  B.GRADE,"
				+ "       TO_CHAR(A.START_TIME,'HH24:MI') AS ST_TIME,"
				+ "       TO_CHAR(A.END_TIME,'HH24:MI') AS EN_TIME,"
				+ "       A.PRICE"
				+ "  FROM RUN A, BUS B"
				+ " WHERE A.BUS_ID = B.BUS_ID"
				+ "   AND START_TERMINAL IN(SELECT TERMINAL_ID"
				+ "                             FROM TERMINAL"
				+ "                            WHERE TERMINAL_NAME = ?)"
				+ "   AND END_TERMINAL IN(SELECT TERMINAL_ID"
				+ "                             FROM TERMINAL"
				+ "                            WHERE TERMINAL_NAME = ?)"
				+ "   AND TO_CHAR(START_TIME,'HH24:MI') > TO_CHAR(SYSDATE,'HH24:MI')";
		
		List<Object> _param = new ArrayList<Object>();
		_param.add(param.get("START_TERMINAL"));
		_param.add(param.get("END_TERMINAL"));
		
		return JDBCUtil.selectList(sql, _param);
	}

	// 남은좌석 수 출력 dao
	public Map<String, Object> remainSeat(String param3, String startDay) {
		String sql = "SELECT A.RA - B.RB AS REMAIN" 
				+ "  	FROM (SELECT COUNT(BUS_ID) AS RA" 
						+ "	 	FROM SEAT"
				+ "    		   WHERE BUS_ID = ?) A,"
				+ " 		 (SELECT NVL(COUNT(BUS_ID),0) AS RB" 
				+ "	   			FROM RESERV_SEAT"
				+ "   		   WHERE BUS_ID = ?"
				+ "     		 AND RESERV_DATE = ?) B";
		List<Object> _param = new ArrayList<Object>();
		_param.add(param3);
		_param.add(param3);
		_param.add(startDay);
		return JDBCUtil.selectOne(sql, _param);
	}

	// 지현
	// 조회한 운행번호의 버스번호 찾기 dao
	public Map<String, Object> ReserveRun(int selectRun) {
		String sql = "SELECT RUN_ID, BUS_ID" 
				+ "     FROM RUN" 
				+ "    WHERE RUN_ID=?";

		List<Object> param = new ArrayList<Object>();
		param.add(selectRun);

		return JDBCUtil.selectOne(sql, param);
	}

	// 운행버스의 전체좌석 dao
	public List<Map<String, Object>> selectSeat() {
		String sql = "SELECT BUS_ID, SEAT_ID " 
				+ "     FROM SEAT" 
				+ "    WHERE BUS_ID=?" 
				+ "    ORDER BY 2";

		List<Object> param = new ArrayList<Object>();
		param.add(BusService.busid);

		return JDBCUtil.selectList(sql, param);
	}

	// 예약 좌석
	public List<Map<String, Object>> myReservation(int selectRun, String startDay) {
		String sql = "SELECT SEAT_ID, BUS_ID, RESERVATION_NO, RUN_ID, RESERV_DATE"
				+ "  FROM RESERV_SEAT"
				+ " WHERE RUN_ID = ?"
				+ "   AND RESERV_DATE = ?";

		List<Object> param = new ArrayList<Object>();
		param.add(selectRun);
		param.add(startDay);
		return JDBCUtil.selectList(sql, param);
	}

}