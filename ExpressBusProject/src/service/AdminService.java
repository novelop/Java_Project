package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.AdminDao;
import util.ScanUtil;
import util.View;

public class AdminService {
	
	//싱글톤패턴 사용
	private AdminService() {
		
	}
	private static AdminService instanse; 
	public static AdminService getInstance() {
		if(instanse == null) { 
			instanse = new AdminService(); 
		}
		return instanse; 
	}

	private AdminDao adminDao = AdminDao.getInstance();
	public static Map<String, Object> answer;
	public static Map<String, Object> admin ;
	public static List<Map<String, Object>> csList;
	public static Map<String, Object> cs;
	public static List<Map<String, Object>> sysTic; // 지현 추가
	
	
	//관리자 로그인 홈 메서드
	public int adminlogin() {
		System.out.print("\n[🏢관리자 홈]\n1.예약내역  \t2.회원조회  \t3.고객센터  "
				+ "\n4.공지사항 관리  \t5.로그아웃  \t0.프로그램종료>");
		int input = ScanUtil.nextInt();
		
		switch (input) {
		case 1: return View.MEM_RESERV_READ;
		case 2: return View.MEM_INFO;
		case 3: return View.CS_LIST;
		case 4: return View.NOTICE_LIST;
		case 5: 
			MemberService.LoginMember = null; //로그인 정보를 저장했던 변수의 값을 없앤다.
			return View.HOME;
		case 0:
			System.out.println("프로그램이 종료되었습니다.");
			System.exit(0);
		}
		return View.ADMINHOME; //잘못 입력시 재귀호출
	}
	
	//혜지님
	public int csList() {
		csList = adminDao.selectCsList();
		System.out.println("\n=========== 문의사항 목록 ==============");
		System.out.println("번호\t제목\t작성자\t작성일");
	    for(Map<String, Object> cs: csList) {
	        System.out.println(cs.get("CS_NO")+
	        		"\t" + cs.get("CS_TITLE")+
	        		"\t" + cs.get("MEM_NAME")+
	        		"\t" + cs.get("REG_DATE"));
	    }
	    System.out.println("====================================");
	    System.out.println("1.조회  0.돌아가기>");
	    
	    int input = ScanUtil.nextInt();
		switch(input) {
		case 1: return View.CS_READ;
		case 0: return View.ADMINHOME;
		}
		
		return View.CS_LIST;
	}
	
	public int csRead() {
		System.out.println("게시글 번호>");
		int csNo = ScanUtil.nextInt();
		
		cs = adminDao.selectCs(csNo);
	
		return View.CS_ANSWER;
	}
	
	public int csAnswer() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("CS_NO", cs.get("CS_NO"));
	    answer = adminDao.selectAnswer(param); // csNo는 선택한 문의사항글의 번호
		//답변이 없는 경우 
		if(answer == null) {
			System.out.println("======================================");
			System.out.println("번호\t: " + cs.get("CS_NO"));
			System.out.println("--------------------------------------");
			System.out.println("작성자\t: "+ cs.get("MEM_NAME"));
			System.out.println("--------------------------------------");
			System.out.println("작성일\t: "+ cs.get("REG_DATE"));
			System.out.println("--------------------------------------");
			System.out.println("제목\t: "+ cs.get("CS_TITLE"));
			System.out.println("--------------------------------------");
			System.out.println("내용\t: "+ cs.get("CS_CONTENT"));
			System.out.println("=============== 답변 ==================");
			System.out.println("1.답변 등록 2.문의사항삭제 0.목록");
			int input = ScanUtil.nextInt();
			switch (input) {
			case 1: return View.INSERT_CS_ANSWER;
			case 2: return View.DELETE_CS;
			case 0: return View.CS_LIST;
			}
		}else {	//답변이 있는 경우
			//답변 출력 
			System.out.println("======================================");
			System.out.println("번호\t: " + cs.get("CS_NO"));
			System.out.println("--------------------------------------");
			System.out.println("작성자\t: "+ cs.get("MEM_NAME"));
			System.out.println("--------------------------------------");
			System.out.println("작성일\t: "+ cs.get("REG_DATE"));
			System.out.println("--------------------------------------");
			System.out.println("제목\t: "+ cs.get("CS_TITLE"));
			System.out.println("--------------------------------------");
			System.out.println("내용\t: "+ cs.get("CS_CONTENT"));
			System.out.println("=============== 답변 ==================");
			System.out.println("작성일 : " + answer.get("REG_DATE"));
			System.out.println("내용 : " + answer.get("CS_ANSWER"));
			System.out.println("======================================");
			System.out.println("1.답변수정 2.답변삭제 3.문의사항삭제 0.목록");
			int input = ScanUtil.nextInt();
			switch (input) {
			case 1: return View.UPDATE_CS_ANSWER;
			case 2: return View.DELETE_CS_ANSWER;
			case 3: return View.DELETE_CS;
			case 0: return View.CS_LIST;
			}
		}
		
		return View.CS_ANSWER;
	}
	
	public int csAnswerInsert() {
		String sysId = (String) MemberService.LoginMember.get("SYS_ID");
		System.out.println("답변입력>");
		String content = ScanUtil.nextLine();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("CS_NO", cs.get("CS_NO"));
		param.put("CS_ANSWER",content);
		param.put("SYS_ID",sysId);
		
		int result = adminDao.insertAnswer(param); 
		
		if(0 < result) {
			System.out.println("답변이 등록되었습니다.");
		}else {
			System.out.println("답변 등록에 실패하였습니다.");
		}
		return View.CS_ANSWER;
	}
	
	public int csAnswerDelete() {
		System.out.println("정말로 삭제하시겠습니까?");
  	    String yn = ScanUtil.nextLine();
  	    Map<String, Object> param = new HashMap<String, Object>();
	    param.put("CS_NO", cs.get("CS_NO"));
  	   if(yn.equals("Y")) {
  		   
  		   int result = adminDao.deleteAnswer(param);
  		   
  		   if(0 < result) {
  				System.out.println("답변이 삭제되었습니다.");
  			}else {
  				System.out.println("답변삭제에 실패하였습니다.");
  			}
  			
  	   }
  	   return View.CS_ANSWER;
	}
	public int csAnswerUpdate() {
		System.out.println("답변수정>");
		String content = ScanUtil.nextLine();
  	    Map<String, Object> param = new HashMap<String, Object>();
	    param.put("CS_NO", cs.get("CS_NO"));
	    param.put("CS_ANSWER",content);
  	   
		int result = adminDao.updateAnswer(param);

		if (0 < result) {
			System.out.println("답변이 수정되었습니다.");
		} else {
			System.out.println("답변수정에 실패하였습니다.");
		}
  	   
  	   return View.CS_ANSWER;
	}
	public int csDelete() {
		System.out.println("정말로 삭제하시겠습니까?");
		String yn = ScanUtil.nextLine();
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("CS_NO", cs.get("CS_NO"));
		if (yn.equals("Y")) {
			if (answer != null) { //답변이 있는 경우 
				int result1 = adminDao.deleteAnswer(param);
				int result2 = adminDao.deleteCs(param);
				if (0 < result2 && 0 < result1) {
					System.out.println("문의사항이 삭제되었습니다.");
					return View.CS_LIST;
				} else {
					System.out.println("문의사항 삭제에 실패하였습니다.");
				}

			} else {
				int result = adminDao.deleteCs(param);
				if (0 < result) {
					System.out.println("문의사항이 삭제되었습니다.");
					return View.CS_LIST;
				} else {
					System.out.println("문의사항 삭제에 실패하였습니다.");
				}
			}
		}
		return View.CS_ANSWER;
	}
	

	// 관리자 전체 예약내역 조회 - 지현추가 (2.16)
	public int memReservRead() {

		sysTic = adminDao.selectMyTicketList();
		System.out.println("\n-------------------------------- 회원 예약 조회 ------------------------------------------------");
		for (Map<String, Object> tic : sysTic) {
			System.out.print("예매번호 " + tic.get("RESERVATION_NO") + " | ");
			System.out.print( tic.get("GRADE")+" | ");
			System.out.print("[" + tic.get("COM_NAME") + "]" + " " + tic.get("ST") + "(" + tic.get("START_TIME") + ")"
					+ " -> " + tic.get("ET") + "(" + tic.get("END_TIME") + ") | ");// [금남고속] 대전복합터미널(18:32) ->
																					// 부산터미널(21:45)
			System.out.print("좌석번호: " + tic.get("SEAT_ID") + " | ");
			System.out.println("예매일:" + tic.get("RESERV_DATE"));
			System.out.println("---------------------------------------------------------------------------------------------");
		}

		return View.ADMINHOME;
	}
	
	//관리자 전체 회원정보 조회 - 민규(0217)
	public int memInfo() {
		List<Map<String,Object>> memeInfo = adminDao.memeInfo();
		
		System.out.println("\n===========================");
		System.out.println("회원아이디\t회원명\t생년월일");
		System.out.println("---------------------------");
		for(Map<String, Object> list : memeInfo) {
			System.out.println(list.get("MEM_ID")
					+ "\t" + list.get("MEM_NAME")
					+ "\t" + list.get("BIRTHDAY"));
		}
		System.out.println("===========================");
		
		System.out.print("\n1.상세조회  0.돌아가기>");
		int input = ScanUtil.nextInt();
		
		switch(input) {
		case 1: return View.MEM_INFO_READ;
		case 0: return View.ADMINHOME;
		}
		
		return View.ADMINHOME;
	}
	
	//관리자 회원정보 상세조회
	public int memInfoRead() {
		System.out.print("\n조회하고자 하는 회원의 아이디를 입력해주세요>"); 
		String memId = ScanUtil.nextLine();
		
		Map<String, Object> memInfo = adminDao.memInfoRead(memId);
		List<Map<String, Object>> memInfoRe = adminDao.memInfoReadReserv(memId);
		
		if(memInfo == null) {
			System.out.println("잘못 입력하셨습니다.");
			 return View.MEM_INFO_READ;
		}
		
		System.out.println("==============================================");
		System.out.println("회원아이디\t: " + memInfo.get("MEM_ID"));
		System.out.println("회원이름\t: " + memInfo.get("MEM_NAME"));
		System.out.println("생년월일\t: " + memInfo.get("BIRTHDAY"));
		System.out.println("예매내역");
		System.out.println("----------------------------------------------");
		if(memInfoRe == null) {
			System.out.println("*예매내역 없음*");
		} else {
			for(Map<String, Object> reserv: memInfoRe) {
			System.out.println("[" + reserv.get("COM_NAME") 
			+ "-" + reserv.get("GRADE") + "] " + reserv.get("ST") +" -> " + reserv.get("ET") 
			+ " " + reserv.get("RESERV_DATE"));
			}
		}
		System.out.println("==============================================");
		
		System.out.print("\n0.돌아가기>");
		int input = ScanUtil.nextInt();
		switch(input) {
		case 0: return View.MEM_INFO;
		}
		return View.ADMINHOME;
	}


	
	
}