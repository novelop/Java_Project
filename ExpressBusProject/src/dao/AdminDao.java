package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import util.JDBCUtil;

public class AdminDao {
	
	//싱글톤패턴 사용
	private AdminDao() {
		
	}
	private static AdminDao instanse; 
	public static AdminDao getInstance() {
		if(instanse == null) { 
			instanse = new AdminDao(); 
		}
		return instanse; 
	}
	
	//문의사항 목록
	public List<Map<String, Object>> selectCsList() {
		String sql = "    SELECT A.CS_NO,"
				+ "              B.MEM_NAME,"
				+ "              TO_CHAR(A.CS_DATE,'YY/MM/DD') AS REG_DATE,"
				+ "              A.CS_TITLE"
				+ "      FROM CS A"
				+ "      LEFT OUTER JOIN MEMBER B ON(A.MEM_ID = B.MEM_ID)"
				+ "      ORDER BY A.CS_NO DESC";
	
		return JDBCUtil.selectList(sql);
	}
	
	//문의사항 상세보기 
	public Map<String, Object> selectCs(int csNo) {
		String sql = "    SELECT A.CS_NO,"
				+ "              B.MEM_NAME,"
				+ "              TO_CHAR(A.CS_DATE,'YY/MM/DD') AS REG_DATE,"
				+ "              A.CS_TITLE,"
				+ "              A.CS_CONTENT"
				+ "      FROM CS A"
				+ "      LEFT OUTER JOIN MEMBER B ON(A.MEM_ID = B.MEM_ID)"
				+ "     WHERE A.CS_NO = ?";
		
		List<Object> _param = new ArrayList<Object>();
		_param.add(csNo);
		return JDBCUtil.selectOne(sql,_param);
	}
	 //문의사항 삭제 
		public int deleteCs(Map<String, Object> param) {

			String sql = "  DELETE CS" + "      WHERE CS_NO = ?";

			List<Object> _param = new ArrayList<Object>();
			_param.add(param.get("CS_NO"));
			return JDBCUtil.update(sql, _param);
		}
	//답변 보기 n번째 행 출력
	public Map<String, Object> selectAnswer(Map<String, Object> param) {
		String sql = " SELECT CS_ANSWER,"
				+ "           TO_CHAR(ANSWER_DATE,'YY/MM/DD') AS REG_DATE,"
				+ "           CS_ANSWER"
				+ "      FROM ANSWER"
				+ "      WHERE CS_NO =?";

		List<Object> _param = new ArrayList<Object>();
		_param.add(param.get("CS_NO"));
		return JDBCUtil.selectOne(sql, _param);
	}
    //답변 등록 
	public int insertAnswer(Map<String, Object> param) {

		String sql = "INSERT INTO ANSWER(CS_NO,CS_ANSWER,ANSWER_DATE,SYS_ID)"
				+ "      VALUES ((SELECT CS_NO FROM CS WHERE CS_NO = ?),"
				+ "      ?,TO_DATE(SYSDATE),?)";

		List<Object> _param = new ArrayList<Object>();
		_param.add(param.get("CS_NO"));
		_param.add(param.get("CS_ANSWER"));
		_param.add(param.get("SYS_ID"));
		return JDBCUtil.update(sql, _param);

	}
    //답변 삭제 
	public int deleteAnswer(Map<String, Object> param) {

		String sql = "  DELETE ANSWER" + "      WHERE CS_NO = ?";

		List<Object> _param = new ArrayList<Object>();
		_param.add(param.get("CS_NO"));
		return JDBCUtil.update(sql, _param);
	}
    //답변 수정 
	public int updateAnswer(Map<String, Object> param) {

		String sql = "   UPDATE ANSWER"
				+ "         SET CS_ANSWER = ?"
				+ "       WHERE CS_NO = ?";

		List<Object> _param = new ArrayList<Object>();
		_param.add(param.get("CS_ANSWER"));
		_param.add(param.get("CS_NO"));
		return JDBCUtil.update(sql, _param);
	}
		
	//로그인 dao
	public Map<String, Object> login(String memId, String password) {
		String sql = "SELECT SYS_ID, SYS_PASSWORD, SYS_NAME"
				+ " FROM SYSTEM"
				+ " WHERE SYS_ID = ?"
				+ " AND SYS_PASSWORD = ?";
		List<Object> param = new ArrayList<Object>();
		param.add(memId);
		param.add(password);
		
		return JDBCUtil.selectOne(sql, param);
	}
	
	// 관리자 전체 승차권 조회 dao
	public List<Map<String, Object>> selectMyTicketList() {
		String sql = " SELECT DISTINCT A.RESERV_DATE," 
				+ "        B.COM_NAME," 
				+ "        D.ST," 
				+ "        E.ET,"
				+ "        TO_CHAR(C.START_TIME,'HH24:MI') AS START_TIME,"
				+ "        TO_CHAR(C.END_TIME,'HH24:MI') AS END_TIME," 
				+ "        A.SEAT_ID,"
				+ "        A.RESERVATION_NO,"
				+ "        B.GRADE" 
				+ "   FROM RESERV_SEAT A,BUS B,RUN C,RESERV_INFO F,RESERVATION G,MEMBER H,"
				+ "                             (SELECT TERMINAL_ID AS SID,"
				+ "                                     TERMINAL_NAME AS ST"
				+ "                                FROM TERMINAL) D,"
				+ "                             (SELECT TERMINAL_ID AS EID,"
				+ "                                     TERMINAL_NAME AS ET"
				+ "                                FROM TERMINAL) E" + "  WHERE A.RUN_ID = F.RUN_ID"
				+ "    AND F.RUN_ID = C.RUN_ID" + "    AND C.START_TERMINAL = D.SID" + "    AND C.END_TERMINAL = E.EID"
				+ "    AND C.BUS_ID = B.BUS_ID" + "   AND A.RESERVATION_NO = G.RESERVATION_NO"
						+ " ORDER BY A.RESERVATION_NO";

		return JDBCUtil.selectList(sql);
	}

	public List<Map<String, Object>> memeInfo() {
		String sql = "SELECT MEM_ID, MEM_NAME, BIRTHDAY"
				+ "  FROM MEMBER";
		return JDBCUtil.selectList(sql);
		
	}
	
	//상세조회 1
	public Map<String, Object> memInfoRead(String memId) {
		String sql = "SELECT MEM_ID, MEM_NAME, BIRTHDAY"
				+ "  FROM MEMBER"
				+ "  WHERE MEM_ID = ?";
		List<Object> param = new ArrayList<Object>();
		param.add(memId);
		return JDBCUtil.selectOne(sql, param);
	}
	//상세조회 2
	public List<Map<String, Object>> memInfoReadReserv(String memId) {
		
		String sql = "SELECT DISTINCT A.RESERV_DATE,"
				+ "        B.COM_NAME,"
				+ "        D.ST,"
				+ "        E.ET,"
				+ "        TO_CHAR(C.START_TIME,'HH24:MI') AS START_TIME,"
				+ "        TO_CHAR(C.END_TIME,'HH24:MI') AS END_TIME,"
				+ "        A.SEAT_ID,"
				+ "        A.RESERVATION_NO,"
				+ "        C.PRICE,"
				+ "        B.GRADE"
				+ "   FROM RESERV_SEAT A,BUS B,RUN C,RESERV_INFO F,RESERVATION G,MEMBER H,(SELECT TERMINAL_ID AS SID,"
				+ "                                     TERMINAL_NAME AS ST"
				+ "                                FROM TERMINAL) D,"
				+ "                             (SELECT TERMINAL_ID AS EID,"
				+ "                                     TERMINAL_NAME AS ET"
				+ "                                FROM TERMINAL) E"
				+ "  WHERE A.RUN_ID = F.RUN_ID"
				+ "    AND F.RUN_ID = C.RUN_ID"
				+ "    AND C.START_TERMINAL = D.SID"
				+ "    AND C.END_TERMINAL = E.EID"
				+ "    AND C.BUS_ID = B.BUS_ID"
				+ "   AND A.RESERVATION_NO = G.RESERVATION_NO"
				+ "    AND G.MEM_ID = ?"
				+ "    ORDER BY A.RESERV_DATE";
		
//		String sql = "SELECT TO_CHAR(F.RIDE_DATE,'YY/MM/DD') AS RIDE_DATE,"
//				+ "				B.COM_NAME,"
//				+ "				D.ST,"
//				+ "				E.ET,"
//				+ "				A.RESERVATION_NO,"
//				+ "				B.GRADE,"
//				+ "				ROWNUM"
//				+ "  FROM RESERV_SEAT A, BUS B, RUN C, RESERV_INFO F, RESERVATION G,"
//				+ "        (SELECT TERMINAL_ID AS SID,"
//				+ "                TERMINAL_NAME AS ST"
//				+ "           FROM TERMINAL) D,"
//				+ "		(SELECT TERMINAL_ID AS EID,"
//				+ "				TERMINAL_NAME AS ET"
//				+ "		   FROM TERMINAL) E"
//				+ " WHERE A.RUN_ID = F.RUN_ID"
//				+ "   AND F.RUN_ID = C.RUN_ID"
//				+ "   AND C.START_TERMINAL = D.SID"
//				+ "   AND C.END_TERMINAL = E.EID"
//				+ "   AND C.BUS_ID = B.BUS_ID"
//				+ "   AND A.RESERVATION_NO = G.RESERVATION_NO"
//				+ "   AND G.MEM_ID = ?";
		List<Object> param = new ArrayList<Object>();
		param.add(memId);
		
		return JDBCUtil.selectList(sql, param);
	}


	
		
}