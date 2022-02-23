package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import service.MemberService;
import util.JDBCUtil;

public class MemberDao {
	
	//싱글톤패턴 사용
	private MemberDao() {
		
	}
	private static MemberDao instanse; 
	public static MemberDao getInstance() {
		if(instanse == null) { 
			instanse = new MemberDao(); 
		}
		return instanse; 
	}
	
	//회원가입 dao
	public int insertMember(Map<String, Object> param) {
		String sql = "INSERT INTO MEMBER"
				+ " VALUES (?, ?, ?, ?)";
		List<Object> _param = new ArrayList<Object>();
		_param.add(param.get("MEM_ID"));
		_param.add(param.get("PASSWORD"));
		_param.add(param.get("MEM_NAME"));
		_param.add(param.get("BIRTHDAY"));
		
		return JDBCUtil.update(sql, _param);
	}
	
	//아이디 중복확인 dao
	public Map<String, Object> idCheck(String memId) {
		String sql = "SELECT A.MEM_ID, B.SYS_ID"
				+ " FROM MEMBER A, SYSTEM B"
				+ " WHERE A.MEM_ID = ?"
				+ " OR B.SYS_ID = ?";
		List<Object> param = new ArrayList<Object>();
		param.add(memId);
		param.add(memId);
		
		return JDBCUtil.selectOne(sql, param);
	}
	
	//로그인 dao
	public Map<String, Object> selectMember(String memId, String password) {
		String sql = "SELECT MEM_ID, PASSWORD, MEM_NAME, BIRTHDAY"
				+ " FROM MEMBER"
				+ " WHERE MEM_ID =?"
				+ " AND PASSWORD = ?";
		List<Object> param = new ArrayList<Object>();
		param.add(memId);
		param.add(password);
		
		return JDBCUtil.selectOne(sql, param);
	}
	
	//관리자-일반회원 확인 dao
	public Map<String, Object> memberCheck(String memId, String password) {
		String sql = "SELECT *"
				+ "  FROM SYSTEM"
				+ " WHERE SYS_ID IN ?";
		List<Object> param = new ArrayList<Object>();
		param.add(memId);
		
		return JDBCUtil.selectOne(sql, param);
	}


	//문의사항 답변보기 
	public Map<String, Object> selectAnswer(Map<String, Object> param) {
		String sql = " SELECT CS_ANSWER,"
				+ "           TO_CHAR(ANSWER_DATE,'YY/MM/DD') AS REG_DATE,"
				+ "           CS_ANSWER"
				+ "      FROM ANSWER"
				+ "      WHERE CS_NO = ?";

		List<Object> _param = new ArrayList<Object>();
		_param.add(param.get("CS_NO"));
		return JDBCUtil.selectOne(sql, _param);
	}
	
	//나의 문의사항 목록 
	 public List<Map<String, Object>> selectCsList(Map<String, Object> param) {
			String sql = "   SELECT A.CS_NO,"
					+ "             A.CS_TITLE,"
					+ "             TO_CHAR(A.CS_DATE,'YY/MM/DD') AS REG_DATE"
					+ "           FROM CS A , MEMBER B"
					+ "          WHERE A.MEM_ID = B.MEM_ID"
					+ "            AND A.MEM_ID = ?"
					+ "		     ORDER BY A.CS_NO DESC";
	
			List<Object> _param = new ArrayList<Object>();
			_param.add(param.get("MEM_ID"));
			return JDBCUtil.selectList(sql,_param);
		}
	 
	//나의 문의사항 상세보기 
	 public Map<String, Object> selectCs(Map<String, Object> param) {
			String sql = "    SELECT CS_NO,"
					+ "              TO_CHAR(CS_DATE,'YY/MM/DD') AS REG_DATE,"
					+ "              CS_TITLE,"
					+ "              CS_CONTENT"
					+ "      FROM CS"
					+ "      WHERE CS_NO = ?";
				
			List<Object> _param = new ArrayList<Object>();
			_param.add(param.get("CS_NO"));
			return JDBCUtil.selectOne(sql,_param);
		}
	 
	 
		//나의 게시판 등록
			public int insertBoard(String memId,String csTitle, String csContent) {
				String sql = "INSERT INTO CS"
						+ "	VALUES ("
						+ "		(SELECT NVL(MAX(CS_NO), 0) + 1 FROM CS)"
						+ "		,?,SYSDATE,?, ?"
						+ "	)";
				
				List<Object> param = new ArrayList<Object>();
				param.add(memId);
				param.add(csTitle);
				param.add(csContent);
				
				
				return JDBCUtil.update(sql, param);
			}
		 
		 
		//나의 문의사항 수정
		public int updateBoard(int CS_NO, String csTitle, String csContent) {
			String sql = "UPDATE CS"
					+ "		 SET CS_TITLE = ?"
					+ "		   , CS_CONTENT = ?"
					+ "	   WHERE CS_NO = ?";
			
			List<Object> param = new ArrayList<Object>();
			param.add(csTitle);
			param.add(csContent);
			param.add(CS_NO);
			
			return JDBCUtil.update(sql, param);
		}


		//나의 문의사항 삭제
		public int deleteBoard(int CS_NO) {
			String sql = "DELETE FROM CS"
					+ "	   WHERE CS_NO = ?";
			
			List<Object> param = new ArrayList<Object>();
			param.add(CS_NO);
			
			return JDBCUtil.update(sql, param);
		}

		 // 마이페이지 내 정보 수정  -> 지현 02/17 추가
		public int myInfoUpdate( String Uppassword, String Upname, String Upbirthday,String 
				memId ) {
			String sql = "UPDATE MEMBER"
					+ "		 SET PASSWORD = ?"
					+ "        , MEM_NAME = ?"
					+ "        , BIRTHDAY = ?"
					+ "	   WHERE MEM_ID = ?";
			
			List<Object> param = new ArrayList<Object>();
		
			param.add(Uppassword);
			param.add(Upname);
			param.add(Upbirthday);
			param.add(memId);    //기존 id
			
			return JDBCUtil.update(sql, param);
		}

		//내 정보 리스트
		public Map<String, Object> myInfoList() {
			String sql = "SELECT A.MEM_ID"
					+ "		   , A.PASSWORD"
					+ "		   , A.MEM_NAME"
					+ "		   , A.BIRTHDAY"
					+ "        , B.MEM_ID"
					+ "		FROM MEMBER A"
					+ "		LEFT OUTER JOIN MEMBER B ON A.MEM_ID = B.MEM_ID"
					+ "	   WHERE B.MEM_ID = ?";
			
			List<Object> param = new ArrayList<Object>();
			param.add(MemberService.LoginMember.get("MEM_ID"));
			
			return JDBCUtil.selectOne(sql, param);
		}

		 
}