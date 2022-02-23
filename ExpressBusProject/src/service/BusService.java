package service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dao.BusDao;
import dao.NoticeDao;
import util.ScanUtil;
import util.View;

public class BusService {

	//싱글톤 패턴
	private BusService() {
	}
	private static BusService instanse; 
	public static BusService getInstance() { 
		if(instanse == null) { 
			instanse = new BusService();
		}
		return instanse; 
	}
	
	private BusDao busDao = BusDao.getInstance(); 
	private NoticeDao noticeDao = NoticeDao.getInstance();
	
	//변수 생성(사용자가 입력한 값을 저장할 변수! 맵으로 생성할까?)
	public static String startTerminal;
	public static String endTerminal;
	public static String startDay;
	public static int seatNo; // 인트로 수정
	public static int selectRun;
	public static Boolean roundTrip = false;
	
	public static int selectSeat;// 지현 추가
	public static String seatId;    // 지현 추가
	public static int busid;  //지현 추가
	
	public static List<Map<String, Object>> busPick = new ArrayList<Map<String,Object>>();
	public static List<Map<String, Object>> runs = new ArrayList<Map<String,Object>>();
	Map<String,Object> run = new HashMap<String, Object>();
	
	//버스조회 메서드
	public int busSearch() {
		//공지사항 출력
		System.out.println();
		Map<String, Object> notice = noticeDao.printNotice();
		
		if(notice != null) {
			System.out.println("👨‍✈️공지 사항👨‍✈️ " + notice.get("NOTICE_TITLE") + "("+notice.get("NOTICE_DATE")+")");
		}
		
		//편도 or 왕복 선택(미완성 : 좌석 선택 후에 구현 가능)
		System.out.println("왕복 여부를 선택해주세요.");
		System.out.print("1.편도\t2.왕복\t0.돌아가기>");
		int round = ScanUtil.nextInt();
		
		if(round == 2) {
			roundTrip = true;
		}else if(round >= 3 || round == 0) {
			return View.LOGINHOME;
		}
		
		//출발지 선택
		List<Map<String, Object>> startList = busDao.selectStart();
		
		System.out.println("\n================================");
		System.out.println("번호\t출발할 터미널");
		System.out.println("--------------------------------");
		for(Map<String, Object> list : startList) {
			System.out.println(list.get("ROWNUM") 
					+ "\t" +list.get("TERMINAL_NAME"));
		}
		System.out.println("================================");
		
		System.out.print("출발지를 선택해주세요(번호입력)>");
		int start = ScanUtil.nextInt();
		
		//숫자로 선택받은 값을 다시 TERMINAL_NAME로 변환
		for(int i = 0; i < startList.size(); i++) {
			if(start == ((BigDecimal)startList.get(i).get("ROWNUM")).intValue()) {
				startTerminal = (String)startList.get(i).get("TERMINAL_NAME");
			}
		}
		if(startTerminal == null) {
			System.out.println("잘못 입력하셨습니다.");
			return View.LOGINHOME;
		}
		System.out.println("출발지로 " + startTerminal + "을 선택하셨습니다.");
		
		//도착지 선택(출발지에서 입력한 터미널은 제외!)
		List<Map<String, Object>> endList = busDao.selectEnd(startTerminal);
		
		System.out.println("\n================================");
		System.out.println("번호\t도착할 터미널");
		System.out.println("--------------------------------");
		for(Map<String, Object> list : endList) {
			System.out.println(list.get("ROWNUM") 
					+ "\t" +list.get("TERMINAL_NAME"));
		}
		System.out.println("================================");
		
		System.out.print("도착지를 선택해주세요(번호입력)>");
		int end = ScanUtil.nextInt();
		
		//숫자로 선택받은 값을 다시 TERMINAL_NAME로 변환
		for(int i = 0; i < endList.size(); i++) {
			if(end == ((BigDecimal)endList.get(i).get("ROWNUM")).intValue()) {
				endTerminal = (String)endList.get(i).get("TERMINAL_NAME");
			} 
		}
		if(endTerminal == null) {
			System.out.println("잘못 입력하셨습니다.");
			return View.LOGINHOME;
		}
		System.out.println("도착지로 " + endTerminal + "을 선택하셨습니다.");
		
		//좌석수 입력
		System.out.print("\n예매할 좌석수를 입력해주세요(숫자입력)>");
		seatNo = ScanUtil.nextInt();
		
		//출발일 입력(정규식 필요)
		boolean flag = true;
		do {
			System.out.print("\n승차일을 입력해주세요(ex.2022/09/01)>");
			startDay = ScanUtil.nextLine();
			
			String regex = "[0-9]{4}/[0-1]{1}[0-9]{1}/[0-3]{1}[0-9]{1}";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(startDay);
			
			if(!m.matches()) {
				flag = false;
				System.out.println("승차일의 형식이 잘못되었습니다.");
			} else {
				flag = true;
			}
		} while(!flag);
		
	
		return View.BUS_RESERVATION;
	}

	//버스 운행목록 선택 메서드
	public int busReservation() {
		//출발일을 new Date와 비교
		SimpleDateFormat fm = new SimpleDateFormat("yyyy/MM/dd"); //사용자가 입력한 날짜의 포멧
		Date today = new Date(); //new Date 생성
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		today = cal.getTime();  //new Date 에서 시간 정보를 삭제하고 다시 초기화
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("START_TERMINAL", startTerminal);
		param.put("END_TERMINAL", endTerminal);
		
		try {
			Date date = new Date(fm.parse(startDay).getTime());
			if(date.compareTo(today) > 0) {
				System.out.println("\n승차일 : " + startDay);
				
				List<Map<String, Object>> run = busDao.selectRunAfter(param);
				
				System.out.println("======================================================");
				System.out.println("운행번호\t버스번호\t등급\t출발시간\t도착시간\t운임요금\t남은좌석");
				System.out.println("------------------------------------------------------");
				for(Map<String, Object> list : run) {
					System.out.print(list.get("RUN_ID")
							+ "\t" + list.get("BUS_ID") + "번"
							+ "\t" + list.get("GRADE")
							+ "\t" + list.get("ST_TIME")
							+ "\t" + list.get("EN_TIME")
							+ "\t" + list.get("PRICE") + "원");
					
					//남은 좌석 수 출력
					String param3 = (String)list.get("BUS_ID");
					Map<String,Object> remainSeat = busDao.remainSeat(param3, startDay);
					System.out.println("\t" + remainSeat.get("REMAIN")+"좌석"); 
				}
			} else if(date.compareTo(today) < 0) {
				System.out.println("지나간 날의 버스는 조회할 수 없습니다.");
				return View.LOGINHOME;
			} else if(date.compareTo(today) == 0) {
				System.out.println("\n승차일 : " + startDay);
				
				List<Map<String, Object>> run = busDao.selectRun(param);
				
				System.out.println("======================================================");
				System.out.println("운행번호\t버스번호\t등급\t출발시간\t도착시간\t운임요금\t남은좌석");
				System.out.println("------------------------------------------------------");
				for(Map<String, Object> list : run) {
					System.out.print(list.get("RUN_ID")
							+ "\t" + list.get("BUS_ID") + "번"
							+ "\t" + list.get("GRADE")
							+ "\t" + list.get("ST_TIME")
							+ "\t" + list.get("EN_TIME")
							+ "\t" + list.get("PRICE") + "원");
					
					//남은 좌석 수 출력
					String param3 = (String)list.get("BUS_ID");
					Map<String,Object> remainSeat = busDao.remainSeat(param3, startDay);
					System.out.println("\t" + remainSeat.get("REMAIN")+"좌석"); 
				}
			} 
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		System.out.println("======================================================");
		System.out.print("\n1.예매  0.돌아가기>");
		int input = ScanUtil.nextInt();
		
		switch(input) {
		case 1: 
			System.out.println("예매하고자 하는 운행번호를 입력해주세요>"); 
			selectRun = ScanUtil.nextInt();
			return View.SELECT_SEAT;
		case 0: return View.LOGINHOME;
		}
	
		return View.BUS_RESERVATION; //잘못 입력시 재귀호출
	}
	
	// 지현
	public int seat() {
		Map<String, Object> tic = new HashMap<String, Object>(); // 결제로 넘겨주기 위한 map
		List<String> list = new ArrayList<>(); // 내가 고르고 있는 좌석을 담아두기 위한 list
		
		for (int j = 0; j < seatNo; j++) {
			// 조회한 운행번호의 버스번호 불러오기
			Map<String, Object> run = busDao.ReserveRun(selectRun);
			busid = Integer.parseInt(String.valueOf(run.get("BUS_ID")));

			// 좌석 불러오기
			String emptySeat = "□";
			String reservedSeat = "■";

			List<Map<String, Object>> seatList = busDao.selectSeat(); // 버스번호의 전체좌석
			List<Map<String, Object>> reservation = busDao.myReservation(selectRun, startDay); // 예약 좌석

			int count = seatList.size(); // 전체좌석수 길이

			System.out.println("\n-------- "+ run.get("BUS_ID")+"버스 ----------");
			System.out.println();
			for (int i = 1; i < seatList.size() + 1; i++) {
				System.out.print(seatList.get(i - 1).get("SEAT_ID"));
				
				a: if(reservation != null) {
					for(int k = 0; k < reservation.size(); k++) {
						if(seatList.get(i - 1).get("SEAT_ID").toString()
								.equals(reservation.get(k).get("SEAT_ID").toString())) {
							System.out.print(reservedSeat);
							break a;
						}
					}
					if (list.contains(seatList.get(i - 1).get("SEAT_ID").toString())){
						System.out.print(reservedSeat);
					}
					else {
						System.out.print(emptySeat);
					} 
				}
				
				if(reservation == null) {
					if (list.contains(seatList.get(i - 1).get("SEAT_ID").toString())){
						System.out.print(reservedSeat);
					}
					else {
						System.out.print(emptySeat);
					}
				}
				if ((count - i) % 9 == 0)
					System.out.println();
				if ((count - i) % 18 == 0)
					System.out.println();
				
			}
				
			System.out.println("----------------------------");

			System.out.println();
			System.out.println("============= 좌석 선택 =============");
			if(seatNo > 1) {
				System.out.print((j+1) + "번째 좌석을 선택해주세요(번호입력)>");
			} else {
				System.out.print("좌석을 선택해주세요(번호입력)>");
			}
			seatId = ScanUtil.nextLine();

			// 민규 추가
			if(reservation != null) {
				if(list.contains(seatId)) {
					do {
						System.out.println("\n이미 선택한 좌석입니다. 다시 좌석을 선택해주세요>");
						seatId = ScanUtil.nextLine();
					   }while(list.contains(seatId));
				}
				   
			 
				for(int k = 0; k < reservation.size(); k++) {
					if(reservation.get(k).get("SEAT_ID").toString().equals(seatId)) {
						do {
						System.out.println("\n이미 예약된 좌석입니다. 다시 좌석을 선택해주세요>");
						seatId = ScanUtil.nextLine();
						}while(reservation.get(k).get("SEAT_ID").toString().equals(seatId));
					}
				 }

					System.out.print("\n예매하시겠습니까? (Y/N)>");
			}
			
			if(reservation == null && !list.contains(seatId)) {
				System.out.print("\n예매하시겠습니까? (Y/N)>");
			}
			
			String yn = ScanUtil.nextLine();
			if (yn.equals("Y")) {
				tic = new HashMap<String, Object>();
				tic.put("START_DAY", startDay);
				tic.put("SEAT_ID", seatId);
				tic.put("SELECT_RUN", selectRun);
				busPick.add(tic);
				list.add(seatId);
				
			} else {
				return View.LOGINHOME;
			}
		}
		run = new HashMap<String, Object>();
		run.put("RUN_ID",selectRun);
		run.put("RIDE_DATE",startDay);
		run.put("RIDE_DATE", startDay);
		runs.add(run);
		if (roundTrip == true) {
			return View.ROUND;
		}
		return View.TICKET_INFO;

	}

	//왕복을 위한 메서드
	public int round() {
		//반복중지
		roundTrip = false;
		
		//출발지와 도착지 교환
		String temp = startTerminal;
		startTerminal = endTerminal;
		endTerminal = temp;
		
		System.out.println("\n[돌아오는 버스]");
		System.out.println("출발지 : " + startTerminal);
		System.out.println("도착지 : " + endTerminal);
		
		//좌석수 입력
		System.out.print("\n예매할 좌석수를 입력해주세요(숫자입력)>");
		seatNo = ScanUtil.nextInt();
		
		//출발일 입력(정규식 필요)
		boolean flag = true;
		do {
			System.out.print("\n가는날을 입력해주세요(ex.2022/09/01)>");
			startDay = ScanUtil.nextLine();
			
			String regex = "[0-9]{4}/[0-1]{1}[0-9]{1}/[0-3]{1}[0-9]{1}";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(startDay);
			
			if(!m.matches()) {
				flag = false;
				System.out.println("가는날의 형식이 잘못되었습니다.");
			} else {
				flag = true;
			}
		} while(!flag);
		return View.BUS_RESERVATION;
	}   
	
	
}