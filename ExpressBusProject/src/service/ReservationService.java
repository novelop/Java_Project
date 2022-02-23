package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.ReservationDao;
import util.ScanUtil;
import util.View;

public class ReservationService {
	// 싱글톤 패턴
	private ReservationService() {
		}
	private static ReservationService instanse;

	public static ReservationService getInstance() {
		if (instanse == null) {
			instanse = new ReservationService();
		}
		return instanse;
	}
	public static int reservNo;
	public static List<Map<String, Object>> myTic;
	private ReservationDao reservationDao = ReservationDao.getInstance();
    
	public int ticketInfo() {
		for (Map<String, Object> p : BusService.busPick) {
			int runNo = (int)p.get("SELECT_RUN"); // 출력할 운행번호
			Map<String, Object> tic = reservationDao.selectTicketInfo(runNo);
			System.out.println("------------------ 티켓 정보 ------------------");
			System.out.println("날짜" + p.get("START_DAY"));// 날짜
			System.out.println("[" + tic.get("COM_NAME") + "]" + " " + tic.get("ST") + "(" + tic.get("START_TIME") + ")"
					+ " -> " + tic.get("ET") + "(" + tic.get("END_TIME") + ")");// [금남고속] 대전복합터미널(18:32) -> 부산터미널(21:45)
			System.out.println(tic.get("GRADE") + " " + p.get("SEAT_ID"));
			System.out.println(tic.get("PRICE") + "원");
			System.out.println("--------------------------------------------");
		}
		System.out.println("1.결제 0.취소>");
		int input = ScanUtil.nextInt();
		
	    switch(input) {
	    case 1: return View.PAYMENT;
	    case 0: return View.LOGINHOME;
	    }
		
		return View.TICKET_INFO;
	}
	
	public int myTicket() {
		 String memId = (String)MemberService.LoginMember.get("MEM_ID");
		 Map<String, Object> param = new HashMap<String, Object>();
		 param.put("MEM_ID",memId);
		 myTic = reservationDao.selectMyTicketList(param);
		    System.out.println("================= 내 승차권 정보 ================= ");
		 boolean pass = true;
		for(Map<String, Object> tic: myTic) {
			pass = false;
			System.out.println(tic.get("RESERV_DATE"));
			System.out.println("[" + tic.get("COM_NAME") + "]" + " " + tic.get("ST") + "(" + tic.get("START_TIME") + ")" + 
				      " -> " + tic.get("ET") + "(" + tic.get("END_TIME") + ")");//[금남고속] 대전복합터미널(18:32) -> 부산터미널(21:45)
			System.out.println("좌석정보 " + tic.get("GRADE") +" " + tic.get("SEAT_ID"));
		    System.out.println("예매번호 " + tic.get("RESERVATION_NO"));
			System.out.println("----------------------------------------------");
		}
        if(pass == false) {
        	System.out.println("1.환불하기 0.돌아가기>");
    		int input = ScanUtil.nextInt();
    		
    	    switch(input) {
    	    case 1: return View.REFUND;
    	    case 0:  return View.LOGINHOME;//로그인페이지
    	    }
        }else {
        	System.out.println("발권된 승차권이 없습니다.");
        	System.out.println("0.홈>");
    		int input = ScanUtil.nextInt();
    		
    	    switch(input) {
    	    case 0:return View.LOGINHOME;//로그인페이지
    	    }
        }
		
		return View.MY_RESERVATION;
	}
	
	
}