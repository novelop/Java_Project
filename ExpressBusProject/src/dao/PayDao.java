package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import util.JDBCUtil;

public class PayDao {
	// 싱글톤 패턴
			private PayDao() {
				}
			private static PayDao instanse;

			public static PayDao getInstance() {
				if (instanse == null) {
					instanse = new PayDao();
				}
				return instanse;
			}
			//가격 불러오기 
			public Map<String, Object> selectPay(int busId){
				String sql = " SELECT PRICE"
						+ "   FROM RUN"
						+ "  WHERE RUN_ID = ?";
				
				List<Object> _param = new ArrayList<Object>();
				_param.add(busId);
				return JDBCUtil.selectOne(sql, _param);
			}
			//예매테이블 insert
			public int insertReservation(Map<String, Object> param) {
				String sql = " INSERT INTO RESERVATION(RESERVATION_NO,MEM_ID)"
						+ "   VALUES(NVL((SELECT MAX(RESERVATION_NO) FROM RESERVATION)+ 1, 1),?)";

				List<Object> _param = new ArrayList<Object>();
				_param.add(param.get("MEM_ID"));
				return JDBCUtil.update(sql, _param);

			}
			//예매상세 insert -----------
			public int insertReserInfo(Map<String, Object> param) {
				String sql = "INSERT INTO RESERV_INFO"
						+ "   VALUES((SELECT MAX(RESERVATION_NO) FROM RESERVATION),?,?)";

				List<Object> _param = new ArrayList<Object>();
				_param.add(param.get("RUN_ID"));
				_param.add(param.get("RIDE_DATE"));
				return JDBCUtil.update(sql, _param);

			}
			
			//예매좌석 insert
			public int insertSeat(Map<String, Object> param) {
				String sql = "INSERT INTO RESERV_SEAT"
						+ "   VALUES(?,(SELECT BUS_ID"
						+ "                  FROM RUN"
						+ "                 WHERE RUN_ID = ?),NVL((SELECT MAX(RESERVATION_NO) FROM RESERVATION), 1),?,?)";

				List<Object> _param = new ArrayList<Object>();
				_param.add(param.get("SEAT_ID"));
				_param.add(param.get("RUN_ID"));
				_param.add(param.get("RUN_ID"));
				_param.add(param.get("RIDE_DATE"));
				return JDBCUtil.update(sql, _param);

			}
			
			//총금액 업데이트 
			public int updateSum() {
				String sql = "UPDATE RESERVATION"
						+ "   SET PRICE_SUM = (SELECT SUM(B.PRICE)"
						+ "                      FROM RESERV_SEAT A,RUN B"
						+ "                     WHERE A.RUN_ID = B.RUN_ID"
						+ "                       AND A.RESERVATION_NO = (SELECT MAX(RESERVATION_NO) FROM RESERVATION))"
						+ "  WHERE RESERVATION_NO = (SELECT MAX(RESERVATION_NO) FROM RESERVATION)";

				return JDBCUtil.update(sql);
			}
			
			public int insertPayment() {
				String sql = "INSERT INTO PAYMENT"
						+ "  VALUES((SELECT MAX(RESERVATION_NO) FROM RESERVATION),TO_DATE(SYSDATE))";

				return JDBCUtil.update(sql);
			}
			//환불테이블 insert
			
			public int insertRefund() {
				String sql = "INSERT INTO REFUND"
						+ "  VALUES(NVL((SELECT MAX(RESERVATION_NO) FROM REFUND)+ 1, 1),TO_DATE(SYSDATE))";

				return JDBCUtil.update(sql);
			}
			
			//환불금액 불러오기 
			public Map<String, Object> selectSum(int reservationNo){
				String sql = "SELECT PRICE_SUM"
						+ "  FROM RESERVATION"
						+ "  WHERE RESERVATION_NO =?";
				
				List<Object> _param = new ArrayList<Object>();
				_param.add(reservationNo);
				return JDBCUtil.selectOne(sql, _param);
			}
			//결제 삭제
			public int deletePayment(int reservationNo) {
				String sql = "DELETE PAYMENT"
						+ " WHERE RESERVATION_NO = ?";

				List<Object> _param = new ArrayList<Object>();
				_param.add(reservationNo);
				return JDBCUtil.update(sql, _param);
			}
			//예매좌석 delete
			public int deleteReservSeat(int reservationNo) {
				String sql = "DELETE RESERV_SEAT"
						+ " WHERE RESERVATION_NO = ?";

				List<Object> _param = new ArrayList<Object>();
				_param.add(reservationNo);
				return JDBCUtil.update(sql, _param);
			}
			//예매상세 delete
			public int deleteReservInfo(int reservationNo) {
				String sql = "DELETE RESERV_INFO"
						+ " WHERE RESERVATION_NO = ?";

				List<Object> _param = new ArrayList<Object>();
				_param.add(reservationNo);
				return JDBCUtil.update(sql, _param);
			}
			//예매 delete
			public int deleteReservation(int reservationNo) {
				String sql = "DELETE RESERVATION"
						+ " WHERE RESERVATION_NO = ?";

				List<Object> _param = new ArrayList<Object>();
				_param.add(reservationNo);
				return JDBCUtil.update(sql, _param);
			}
			
			
}