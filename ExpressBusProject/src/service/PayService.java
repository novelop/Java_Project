package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dao.PayDao;
import util.ScanUtil;
import util.View;

public class PayService {
	// 싱글톤 패턴
		private PayService() {
			}
		private static PayService instanse;

		public static PayService getInstance() {
			if (instanse == null) {
				instanse = new PayService();
			}
			return instanse;
		}
		public static int sum;
		private PayDao payDao = PayDao.getInstance();
		
		public int payment() {
			sum = 0;
			for(Map<String, Object> p : BusService.busPick) {
				int busId =(int)p.get("SELECT_RUN");
			    Map<String, Object> map = payDao.selectPay(busId);
			    sum +=  Integer.parseInt(String.valueOf(map.get("PRICE")));
			}
			System.out.println("============== 결제 페이지 ==============");
			System.out.println("총 결제 금액 : " + sum );
			System.out.println("1.카드결제 0.취소>");
			int input = ScanUtil.nextInt();
			
		    switch(input) {
		    case 1: return View.CARD_PAY;
		    case 0: return View.LOGINHOME;
		    }
			return View.PAYMENT;
		}

		public int cardPay() {
			boolean flag = true;
			do {
				System.out.println("카드번호를 입력해주세요 ex) 1111-1111-1111-1111 >");
				String cardNo = ScanUtil.nextLine();
				String regex = "[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(cardNo);

				if (!m.matches()) {
					flag = false;
					System.out.println("카드번호의 형식이 잘못되었습니다.");
				} else {
					flag = true;
				}
			} while (!flag);
			
			do {
				System.out.println("카드번호를 비밀번호 앞 두자리를 입력해주세요>");
				String cardNo = ScanUtil.nextLine();
				String regex = "[0-9]{2}";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(cardNo);

				if (!m.matches()) {
					flag = false;
					System.out.println("잘못입력하였습니다.");
				} else {
					flag = true;
				}
			} while (!flag);
			
			do {
				System.out.println("카드유효기간을 입력해주세요 ex) 22/02 >");
				String cardNo = ScanUtil.nextLine();
				String regex = "[0-2]{1}[2-9]{1}/[0-1]{1}[0-9]{1}";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(cardNo);
				if (!m.matches()) {
					flag = false;
					System.out.println("잘못입력하였습니다.");
				} else {
					flag = true;
				}
			} while (!flag);
			
			do {
				System.out.println("cvc번호 3자리를 입력해주세요>");
				String cardNo = ScanUtil.nextLine();
				String regex = "[0-9]{3}";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(cardNo);

				if (!m.matches()) {
					flag = false;
					System.out.println("잘못입력하였습니다.");
				} else {
					flag = true;
				}
			} while (!flag);
			String memId = (String) MemberService.LoginMember.get("MEM_ID");
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("MEM_ID", memId);
			int result = payDao.insertReservation(param);
			
			if (result < 0) {
				System.out.println("결제 실패");
				return View.LOGINHOME;
			}
			for (Map<String, Object> pick : BusService.runs) {
				int runId = ((int) pick.get("RUN_ID"));
				String day = (String) pick.get("RIDE_DATE");
				param = new HashMap<String, Object>();
				param.put("RUN_ID", runId);
				param.put("RIDE_DATE", day);
				int result2 = payDao.insertReserInfo(param);
				if (result2 < 0) {
					System.out.println("결제 실패");
					return View.LOGINHOME;
				}
			}

				for (Map<String, Object> pickSeat : BusService.busPick) {
					int runNo = (int) pickSeat.get("SELECT_RUN");
					String seat = (String) pickSeat.get("SEAT_ID");
					String day = (String) pickSeat.get("START_DAY");
					param = new HashMap<String, Object>();
					param.put("RUN_ID", runNo);
					param.put("SEAT_ID", seat);
					param.put("RIDE_DATE", day);
					int result3 = payDao.insertSeat(param);
					if (result3 < 0) {
						System.out.println("결제 실패");
						return View.LOGINHOME;
					}
				}
				int result4 = payDao.updateSum();
				
				if (result4 < 0) {
					System.out.println("결제 실패");
					return View.LOGINHOME;
				}
				
				int result5 = payDao.insertPayment();
				
				if (result5 < 0) {
					System.out.println("결제 실패");
					return View.LOGINHOME;
				}
				
				System.out.println("결제가 완료되었습니다.");
				BusService.busPick = new ArrayList<Map<String,Object>>();
				return View.MY_RESERVATION;

		}

		public int refund() {
			System.out.println("환불할 예매번호를 입력해주세요>");
			int reservationNo = ScanUtil.nextInt();
			System.out.println("정말로 환불하시겠습니까? (Y/N)>");
			String yn = ScanUtil.nextLine();
			if (yn.equals("Y")) {
				Map<String, Object> map = payDao.selectSum(reservationNo);
				System.out.println("총 환불금액은 " + map.get("PRICE_SUM") +"원 입니다." );
                int result = payDao.insertRefund(); //환불테이블 insert
                int result2 = payDao.deletePayment(reservationNo); //결제테이블 delete
                int result3 = payDao.deleteReservSeat(reservationNo); //예매좌석 테이블 delete
                int result4 = payDao.deleteReservInfo(reservationNo); //예매상세 테이블 delete
                int result5 = payDao.deleteReservation(reservationNo); //예매테이블 delete
                
                
				if (0 < result || 0 < result2 || 0 < result3 || 0 < result4 || 0 < result5) {
					System.out.println("환불되었습니다.");
					return View.MY_RESERVATION;
				} else {
					System.out.println("환불 실패");
				}

			}
			return View.LOGINHOME;
		}
		
}