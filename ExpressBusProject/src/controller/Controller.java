package controller;


import service.AdminService;
import service.BusService;
import service.MemberService;
import service.NoticeService;
import service.PayService;
import service.ReservationService;
import util.ScanUtil;
import util.View;

public class Controller {

	public static void main(String[] args) {

		new Controller().start();
	}

	private MemberService memberService = MemberService.getInstance();
	private BusService busService = BusService.getInstance();
	private AdminService adminService = AdminService.getInstance(); 
	private NoticeService noticeService = NoticeService.getInstance(); 
	private ReservationService reservationService = ReservationService.getInstance(); 
	private PayService payService = PayService.getInstance(); //ㅎㅈ추가용

	private void start() {
		//화면을 이동시켜주는 메서드
		int view = View.HOME;
		
		while(true) {
			switch(view) {
			case View.HOME: view = home(); break; 
			case View.LOGIN: view = memberService.login(); break;
			case View.JOIN: view = memberService.join(); break;
			case View.LOGINHOME: view = memberService.loginHome(); break;
			case View.BUS_SEARCH: view = busService.busSearch(); break;
			case View.BUS_RESERVATION: view = busService.busReservation(); break;
			case View.ADMINHOME: view = adminService.adminlogin(); break;
			case View.SELECT_SEAT: view = busService.seat(); break;
			case View.INSERT_CS_ANSWER: view = adminService.csAnswerInsert();  break;
			case View.DELETE_CS_ANSWER: view = adminService.csAnswerDelete();  break;
			case View.UPDATE_CS_ANSWER: view = adminService.csAnswerUpdate();  break;
			case View.MY_CS: view = memberService.myCsList(); break;
			case View.MY_CS_READ: view = memberService.myCsRead(); break;
			case View.MY_CS_ANSWER: view = memberService.myCsAnswer(); break;
			case View.CS_ANSWER: view = adminService.csAnswer();  break;
			case View.CS_LIST: view = adminService.csList();  break;
			case View.CS_READ: view = adminService.csRead();  break;			
			case View.DELETE_CS: view = adminService.csDelete();  break;			
			case View.NOTICE_LIST: view = noticeService.noticeList();  break;
			case View.NOTICE_INSERT: view = noticeService.noticeInsert();  break;
			case View.NOTICE_UPDATE: view = noticeService.noticeUpdate();  break;
			case View.NOTICE_DELETE: view = noticeService.noticeDelete();  break;
			case View.MEM_NOTICE: view = memberService.notice();  break;
			case View.TICKET_INFO: view = reservationService.ticketInfo();  break;
			case View.ROUND: view = busService.round();  break;
			case View.MY_INSERT: view = memberService.myInsert(); break;
			case View.MY_UPDATE: view = memberService.myUpdate(); break;
			case View.MY_DELETE: view = memberService.myDelete(); break;
			case View.MY_RESERVATION: view = reservationService.myTicket();  break;
			case View.MEM_RESERV_READ: view =adminService.memReservRead();  break;   
			case View.MYPAGE: view =memberService.mypage(); break; 
			case View.MY_INFO_UPDATE: view = memberService.myInfoUpdate(); break; 
			case View.PAYMENT: view = payService.payment();  break; //혜지 추가
			case View.CARD_PAY: view = payService.cardPay();  break; //혜지추가 
			case View.MEM_INFO: view = adminService.memInfo(); break; //민규추가
			case View.MEM_INFO_READ: view = adminService.memInfoRead(); break; //민규추가
			case View.REFUND: view = payService.refund(); break; //ㅎㅈ추가
			}
		}
	}

	private int home() {
		System.out.println("\n🚍🚍고속버스 예약 시스템🚍🚍");
		System.out.print("\n1.로그인  2.회원가입  0.프로그램 종료>");
		int input = ScanUtil.nextInt();
		
		switch(input) {
		case 1: return View.LOGIN; 
		case 2: return View.JOIN;
		case 0:
			System.out.println("프로그램이 종료되었습니다.");
			System.exit(0);
		}
		
		return View.HOME;
	}

}