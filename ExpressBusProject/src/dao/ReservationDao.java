package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import util.JDBCUtil;


public class ReservationDao {
	//싱글톤 패턴
		private ReservationDao() {
		}
		private static ReservationDao instanse; 
		public static ReservationDao getInstance() { 
			if(instanse == null) { 
				instanse = new ReservationDao();
			}
			return instanse; 
		}
	
		//승차권 ticketInfo
		public Map<String, Object> selectTicketInfo(int runNo) {
			String sql = "    SELECT   M.COM_NAME,"
					+ "                TO_CHAR(A.START_TIME,'HH24:MI') AS START_TIME,"
					+ "                TO_CHAR(A.END_TIME,'HH24:MI') AS END_TIME,"
					+ "                B.ST,"
					+ "                F.ET,"
					+ "                M.GRADE,"
					+ "                A.PRICE"
					+ "           FROM RUN A ,(SELECT TERMINAL_ID AS SID,"
					+ "                               TERMINAL_NAME AS ST"
					+ "                          FROM TERMINAL) B,"
					+ "                        (SELECT TERMINAL_ID AS EID,"
					+ "                                TERMINAL_NAME AS ET"
					+ "                          FROM TERMINAL) F,"
					+ "                          BUS M"
					+ "           WHERE A.BUS_ID = M.BUS_ID"
					+ "           AND A.START_TERMINAL = B.SID"
					+ "           AND A.END_TERMINAL = F.EID"
					+ "           AND A.RUN_ID = ?";					
			
			List<Object> param = new ArrayList<Object>();
			param.add(runNo);
			return JDBCUtil.selectOne(sql, param);
		}
		
		//나의 승차권 조회 
		public List<Map<String, Object>> selectMyTicketList(Map<String, Object> param) {
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
					+ "                                FROM TERMINAL) E"
					+ "  WHERE A.RUN_ID = F.RUN_ID"
					+ "    AND F.RUN_ID = C.RUN_ID"
					+ "    AND C.START_TERMINAL = D.SID"
					+ "    AND C.END_TERMINAL = E.EID"
					+ "    AND C.BUS_ID = B.BUS_ID"
					+ "   AND TO_DATE(A.RESERV_DATE)>= TO_DATE(SYSDATE)"
					+ "   AND A.RESERVATION_NO = G.RESERVATION_NO"
					+ "   AND G.MEM_ID = ?"
					+ " ORDER BY A.RESERVATION_NO";					
			
			List<Object> _param = new ArrayList<Object>();
			_param.add(param.get("MEM_ID"));
			return JDBCUtil.selectList(sql, _param);
		}
		
}