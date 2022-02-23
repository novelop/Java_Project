package dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import util.JDBCUtil;

public class NoticeDao {
	
	//싱글톤패턴 사용
	private NoticeDao() {
		
	}
	private static NoticeDao instanse; 
	public static NoticeDao getInstance() {
		if(instanse == null) { 
			instanse = new NoticeDao(); 
		}
		return instanse; 
	}
	
	public List<Map<String, Object>> selectNoticeList(){
		String sql = "SELECT A.NOTICE_NO,"
				+ " A.NOTICE_TITLE,"
				+ " B.SYS_NAME,"
				+ " TO_CHAR(A.NOTICE_DATE, 'MM/DD') AS NOTICE_DATE"
				+ " FROM NOTICE A"
				+ " LEFT OUTER JOIN SYSTEM B ON A.SYS_ID = B.SYS_ID"
				+ " ORDER BY 1 DESC";
		return JDBCUtil.selectList(sql);
	}
	
	public int insertNotice(Map<String, Object> param){
		String sql = "INSERT INTO NOTICE VALUES ("
				+ " (SELECT NVL(MAX(NOTICE_NO), 0) + 1 FROM NOTICE),"
				+ " SYSDATE, ?, ?)";
		List<Object> _param = new ArrayList<Object>();
		_param.add(param.get("NOTICE_TITLE"));
		_param.add(param.get("SYS_ID"));
		
		return JDBCUtil.update(sql, _param);
	}

	public int updateNotice(Map<String, Object> param) {
		String sql = "UPDATE NOTICE"
				+ " SET NOTICE_TITLE = ?"
				+ " WHERE NOTICE_NO = ?"; 
		
		List<Object> _param = new ArrayList<Object>();
		_param.add(param.get("NOTICE_TITLE"));
		_param.add(param.get("NOTICE_NO"));
		
		return JDBCUtil.update(sql, _param);
	}
	
	public int deleteNotice(Map<String, Object> param) {
		String sql = "DELETE FROM NOTICE"
				+ " WHERE NOTICE_NO = ?";
		
		List<Object> _param = new ArrayList<Object>();
		_param.add(param.get("NOTICE_NO"));
		
		return JDBCUtil.update(sql, _param);
	}

	public Map<String, Object> printNotice() {
		String sql = "SELECT NOTICE_TITLE,"
				+ " TO_CHAR(NOTICE_DATE, 'MM/DD') AS NOTICE_DATE"
				+ " FROM NOTICE"
				+ " WHERE NOTICE_NO = (SELECT NVL(MAX(NOTICE_NO), 0) FROM NOTICE)";
		return JDBCUtil.selectOne(sql);
	}
}