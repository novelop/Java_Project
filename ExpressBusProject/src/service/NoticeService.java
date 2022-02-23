package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.NoticeDao;
import util.ScanUtil;
import util.View;

public class NoticeService {
	
	//싱글톤패턴 사용
	private NoticeService() {
		
	}
	private static NoticeService instanse; 
	public static NoticeService getInstance() {
		if(instanse == null) { 
			instanse = new NoticeService(); 
		}
		return instanse; 
	}
	
	//NoticeDao 호출
	private NoticeDao noticeDao = NoticeDao.getInstance();
	
	public static int readBoard; //조회중인 게시글 번호를 기억해줄 변수
	
	public int noticeList() {
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
		System.out.print("\n1.등록  2.수정  3.삭제  0.돌아가기>");
		
		int input = ScanUtil.nextInt();
		
		switch(input) {
		case 1: return View.NOTICE_INSERT;
		case 2: return View.NOTICE_UPDATE;
		case 3: return View.NOTICE_DELETE;
		case 0: return View.ADMINHOME;
		}
		
		return View.NOTICE_LIST;
	}

	public int noticeInsert() {
		System.out.println("제목>");
		String title = ScanUtil.nextLine();
		String sysId = (String) MemberService.LoginMember.get("SYS_ID");
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("NOTICE_TITLE", title);
		param.put("SYS_ID", sysId);
		
		int result = noticeDao.insertNotice(param);
		
		if(0 < result) {
			System.out.println("공지사항 등록이 완료되었습니다.");
		}else {
			System.out.println("공지사항 등록에 실패하였습니다.");
		}
		
		return View.NOTICE_LIST;
	}

	public int noticeUpdate() {
		System.out.println("수정할 공지사항 번호>");
		int boardNo = ScanUtil.nextInt();
		System.out.println("제목>");
		String title = ScanUtil.nextLine();
		
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("NOTICE_TITLE", title);
		param.put("NOTICE_NO", boardNo);
		
		int result = noticeDao.updateNotice(param);
		
		if(0 < result) {
			System.out.println("공지사항 수정이 완료되었습니다.");
		}else {
			System.out.println("공지사항 수정에 실패하였습니다.");
		}
		
		return View.NOTICE_LIST;
	}

	public int noticeDelete() {
		System.out.println("삭제할 공지사항 번호>");
		int boardNo = ScanUtil.nextInt();
		System.out.print("정말 삭제하시겠습니까?(Y or y)>");
		String yn = ScanUtil.nextLine();
		if(yn.equals("Y") || yn.equals("y")) {

			Map<String, Object> param = new HashMap<String, Object>();
			param.put("NOTICE_NO", boardNo);
			
			int result = noticeDao.deleteNotice(param);
			
			if(0 < result) {
				System.out.println("공지사항이 삭제되었습니다.");
			}else {
				System.out.println("공지사항 삭제에 실패하였습니다.");
			}
		}

		return View.NOTICE_LIST;
	}
}