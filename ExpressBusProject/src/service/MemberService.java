package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dao.AdminDao;
import dao.MemberDao;
import dao.NoticeDao;
import util.ScanUtil;
import util.View;

public class MemberService {
	
	//싱글톤 패턴
	private MemberService() {
	}
	private static MemberService instanse; 
	public static MemberService getInstance() { 
		if(instanse == null) { 
			instanse = new MemberService();
		}
		return instanse; 
	}
	
	public static Map<String, Object> LoginMember; //로그인 된 정보를 기억해줄 변수
	public static List<Map<String, Object>> myCsList; //나의 문의사항 목록
	public static Map<String, Object> myCs; //나의 문의사항 상세
	public static Map<String, Object> myAnswer; //나의 문의사항 답변
	int currentBoardNo; // 지현 추가
	
	//MemberDao 호출
	private MemberDao memberDao = MemberDao.getInstance(); 
	private AdminDao adminDao = AdminDao.getInstance(); 
	private NoticeDao noticeDao = NoticeDao.getInstance();
	
	//회원가입 메서드
	public int join() {
		System.out.println("\n========== 회원가입 ==========");
		Map<String, Object> param = new HashMap<String, Object>();
		boolean flag = true;
		
		//아이디 입력 및 정규식
		do {
			System.out.print("아이디[4~20자의 영문 소문자,숫자]>");
			String memId = ScanUtil.nextLine();
			String regex = "[a-z0-9]{4,20}";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(memId);
			
			if(!m.matches()) {
				flag = false;
				System.out.println("아이디의 형식이 잘못되었습니다.");
			} else {
				flag = true;
				param.put("MEM_ID", memId);
				
				//아이디 중복확인
				Map<String,Object> idCheck = memberDao.idCheck(memId);
				if(idCheck != null) {
					flag = false;
					System.out.println("이미 존재하는 아이디입니다.");
				}
			}
		} while(!flag);
		
		//비밀번호 입력 및 정규식
		do {
			System.out.print("비밀번호[4~20자의 영문 소문자,숫자]>");
			String password = ScanUtil.nextLine();
			String regex = "[a-z0-9]{4,20}";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(password);
			
			if(!m.matches()) {
				flag = false;
				System.out.println("비밀번호의 형식이 잘못되었습니다.");
			} else {
				flag = true;
				param.put("PASSWORD", password);
			}
		} while(!flag);
		
		//이름 입력
		System.out.print("이름>");
		String memName = ScanUtil.nextLine();
		param.put("MEM_NAME", memName);
		
		//생년월일 입력 및 정규식
		do {
			System.out.print("생년월일[ex.1999/01/23]>");
			String birthday = ScanUtil.nextLine();
			
			String regex = "[0-9]{4}/[0-1]{1}[0-9]{1}/[0-3]{1}[0-9]{1}";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(birthday);
			
			if(!m.matches()) {
				flag = false;
				System.out.println("생년월일의 형식이 잘못되었습니다.");
			} else {
				flag = true;
				param.put("BIRTHDAY", birthday);
			}
		} while(!flag);
		
		//쿼리 실행
		int result = memberDao.insertMember(param); 
		if(0 < result) {
			System.out.println("회원가입 성공");
		}else {
			System.out.println("회원가입 실패");
		}
		
		return View.HOME;
	}

	//로그인 메서드
	public int login() {
		System.out.println("========== 로그인 ==========");
		System.out.print("아이디>");
		String memId = ScanUtil.nextLine();
		System.out.print("비밀번호>");
		String password = ScanUtil.nextLine();
		
		//관리자 여부 확인
		Map<String,Object> memberCheck = memberDao.memberCheck(memId, password);
		if(memberCheck != null) {
			//관리자일 경우
			Map<String,Object> member = adminDao.login(memId, password);
			if(member == null) {
				System.out.println("관리자 계정의 비밀번호를 잘못 입력하셨습니다.");
			}else {
				System.out.println("관리자 계정 로그인 성공");
				LoginMember = member; //로그인 된 정보를 변수에 저장
				return View.ADMINHOME; //로그인 성공하면 관리자 홈을 호출				
			}
			return View.LOGIN;//로그인 실패시 다시 로그인하도록 리턴
		}

		//일반회원의 경우
		Map<String,Object> member = memberDao.selectMember(memId, password);
		if(member == null) {
			System.out.println("아이디 혹은 비밀번호를 잘못 입력하셨습니다.");
		}else {
			System.out.println("로그인 성공");
			LoginMember = member; 
			return View.LOGINHOME; //로그인 성공하면 로그인홈을 호출
		}
		return View.LOGIN;//로그인 실패시 다시 로그인하도록 리턴
	}

	//일반회원 로그인홈 메서드
	public int loginHome() {
		BusService.busPick = new ArrayList<Map<String,Object>>();
		BusService.runs = new ArrayList<Map<String,Object>>();
		System.out.print("\n[🏚홈]\n1.버스조회 \t 2.내승차권확인 \t 3.마이페이지 \t 4.고객센터"
				+ "\n5.공지사항확인 \t 6.로그아웃 \t 0.프로그램종료>");
		int input = ScanUtil.nextInt();
		
		switch (input) {
		case 1: return View.BUS_SEARCH;
		case 2: return View.MY_RESERVATION; //혜지 추가 
		case 3: return View.MYPAGE;
		case 4: return View.MY_CS;
		case 5: return View.MEM_NOTICE;
		case 6: 
			MemberService.LoginMember = null; //로그인 정보를 저장했던 변수의 값을 없앤다.
			return View.HOME;
		case 0:
			System.out.println("프로그램이 종료되었습니다.");
			System.exit(0);
		}
		return View.LOGINHOME; //잘못 입력시 재귀호출
	}

	//나의 문의사항 목록 
	   public int myCsList() {
		   String memId = (String)LoginMember.get("MEM_ID");
		   Map<String, Object> param = new HashMap<String, Object>();
		   param.put("MEM_ID",memId);
		   myCsList = memberDao.selectCsList(param);
		   System.out.println("============ 나의 문의사항 목록 ============");
		   System.out.println("번호\t제목\t작성일");
		    for(Map<String, Object> cs: myCsList) {
		        System.out.println(cs.get("CS_NO")+
		        		"\t" + cs.get("CS_TITLE")+
		        		"\t" + cs.get("REG_DATE"));
		    }
		    System.out.println("=======================================");
		    System.out.println("1.조회 2.등록 0.돌아가기>");
		    int input = ScanUtil.nextInt();
		  		switch(input) {
		  		case 1: return View.MY_CS_READ;  
		  		case 2: return View.MY_INSERT;
		  		case 0: return View.LOGINHOME;
		  		}
		   return View.MY_CS;
	   }
	 //나의 문의사항 상세조회
	   public int myCsRead() {
		   System.out.println("게시글 번호>");
		   int csNo = ScanUtil.nextInt();
		   currentBoardNo = csNo;
		   String memId = (String)LoginMember.get("MEM_ID");
		   Map<String, Object> param = new HashMap<String, Object>();   
		   param.put("CS_NO",csNo);
		   param.put("MEM_ID",memId);
		   myCs = memberDao.selectCs(param);

		   return View.MY_CS_ANSWER;
	  }
	 //나의 문의사항 상세조회 및 답변 출력 
	  public int myCsAnswer() { 
		  Map<String, Object> param = new HashMap<String, Object>();
		  param.put("CS_NO", myCs.get("CS_NO"));
		  myAnswer = memberDao.selectAnswer(param);
		  	System.out.println("======================================");
			System.out.println("번호\t: " + myCs.get("CS_NO"));
			System.out.println("--------------------------------------");
			System.out.println("작성일\t: "+ myCs.get("REG_DATE"));
			System.out.println("--------------------------------------");
			System.out.println("제목\t: "+ myCs.get("CS_TITLE"));
			System.out.println("--------------------------------------");
			System.out.println("내용\t: "+ myCs.get("CS_CONTENT"));
			System.out.println("================ 답변 =================");
			if(myAnswer != null) {
				System.out.println("작성일 : " + myAnswer.get("REG_DATE"));
				System.out.println("내용 : " + myAnswer.get("CS_ANSWER"));
				System.out.println("---------------------------------------");
			}else {
			     System.out.println("답변이 없습니다. ");
			     System.out.println("--------------------------------------");
			}
		    System.out.println("1.문의사항 수정 2.문의사항 삭제 0.돌아가기>");
		    int input = ScanUtil.nextInt();
		  		switch(input) {
		  		case 1: return View.MY_UPDATE;
		  		case 2: return View.MY_DELETE;
		  		case 0: return View.MY_CS;
		  		}
		   return View.MY_CS;
			
	  }

	  
	  //등록
	  public int myInsert() {
			
		String memId = (String) MemberService.LoginMember.get("MEM_ID");
		System.out.print("제목>");
		String csTitle = ScanUtil.nextLine();
		System.out.print("내용>");
		String csContent = ScanUtil.nextLine();			

		int result = memberDao.insertBoard(memId,csTitle,csContent);
			
		if(0 < result) {
			System.out.println("게시글 등록 성공");
		}else {
			System.out.println("게시글 등록 실패");
		}
		
			return View.MY_CS;
	  }
	  
	  //수정
	  public int myUpdate() {
		int boardNo = currentBoardNo;
		System.out.print("제목>");
		String title = ScanUtil.nextLine();
		System.out.print("내용>");
		String content = ScanUtil.nextLine();
		
		int result = memberDao.updateBoard(boardNo, title, content);
		
		if(0 < result) {
			System.out.println("게시글 수정 성공");
		}else {
			System.out.println("게시글 수정 실패");
		}
		
		return View.MY_CS;
	  }

	  //삭제
	  public int myDelete() {
		System.out.print("정말 삭제하시겠습니까?>");
		String yn = ScanUtil.nextLine();
		
		if(yn.equals("Y")) {
			int CS_NO = currentBoardNo;
			int result = memberDao.deleteBoard(CS_NO);
			
			if(0 < result) {
				System.out.println("게시글 삭제 성공");
			}else {
				System.out.println("게시글 삭제 실패");
			}
		}
		
		return View.MY_CS;
	  }
 
	  //개인회원 공지확인 - 민규
	  public int notice() {
			List<Map<String, Object>> boardList = noticeDao.selectNoticeList();
			
			System.out.println("=====================================");
			System.out.println("번호\t제목\t\t작성자\t작성일");
			System.out.println("-------------------------------------");
			for(Map<String, Object> board : boardList) {
				System.out.println(board.get("NOTICE_NO")
						+ "\t" + board.get("NOTICE_TITLE")
						+ "\t" + board.get("SYS_NAME")
						+ "\t" + board.get("NOTICE_DATE"));
			}
			System.out.println("=====================================");
			System.out.print("\n0.돌아가기>");
			
			int input = ScanUtil.nextInt();
			
			switch(input) {
			case 0: return View.LOGINHOME;
			}
			
			return View.MEM_NOTICE;
		}

	  
		// 마이페이지 -> 지현 02/17 추가
		public int mypage() {

			Map<String, Object> MyList = memberDao.myInfoList();

			System.out.println("======================================");
			System.out.println("아이디\t: " + MyList.get("MEM_ID"));
			System.out.println("--------------------------------------");
			System.out.println("비밀번호\t: " + MyList.get("PASSWORD"));
			System.out.println("--------------------------------------");
			System.out.println("이름\t: " + MyList.get("MEM_NAME"));
			System.out.println("--------------------------------------");
			System.out.println("생년월일\t: " + MyList.get("BIRTHDAY"));
			System.out.println("======================================");
			System.out.println("1.내정보 수정  0.돌아가기>");
			int input = ScanUtil.nextInt();
			switch (input) {
			case 1:
				return View.MY_INFO_UPDATE;

			case 0:
				return View.LOGINHOME;
			}
			return View.MYPAGE;

		}

		public int myInfoUpdate() {

			String Uppassword;
			String Upbirthday;

			// 비밀번호 입력 및 정규식
			boolean flag = true;
			do {
				System.out.print("회원의 비밀번호를 입력해주세요.>");
				Uppassword = ScanUtil.nextLine();
				String regex = "[a-z0-9]{4,20}";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(Uppassword);

				if (!m.matches()) {
					flag = false;
					System.out.println("비밀번호가 일치하지 않습니다..");
				}else {
					flag = true;
				}
			} while (!flag);

			System.out.print("이름 수정>");
			String Upname = ScanUtil.nextLine();

			// 생년월일 입력 및 정규식
			do {
				System.out.print("생년월일 수정[ex.1999/01/23]>");
				Upbirthday = ScanUtil.nextLine();

				String regex = "[0-9]{4}/[0-1]{1}[0-9]{1}/[0-3]{1}[0-9]{1}";
				Pattern p = Pattern.compile(regex);
				Matcher m = p.matcher(Upbirthday);

				if (!m.matches()) {
					flag = false;
					System.out.println("생년월일의 형식이 잘못되었습니다.");
				}else {
					flag = true;
				}
			} while (!flag);

			String memId = (String) MemberService.LoginMember.get("MEM_ID");

			int result = memberDao.myInfoUpdate(Uppassword, Upname, Upbirthday, memId);

			if (0 < result) {
				System.out.println("회원정보가 수정되었습니다.");
			} else {
				System.out.println("회원정보 수정에 실패하였습니다.");
			}

			return View.MYPAGE;

		}
}