package kr.ac.hansung.somebody;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite를 지원하는 API들을 사용하기 위한 DBAdapter 클래스
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBDBAdapter {

	private DatabaseHelper helper; // SQLiteHelper를 상속받은 DatabaseHelper의 변수

	private SQLiteDatabase db; // db변수

	private static final String DB_NAME = "somebody.db"; // DB file 이름
	private static final int DB_VERSION = 1; // DB version
	private static String TABLE_NAME; // Table 이름

	// 사용자 정보 Table attributes
	private final static String INFO_CREATE_COMPONENT = "user_id string primary key , nick string , weight integer , age integer , "
			+ "gender boolean , height integer, level integer, exp integer, goal_weight integer ,"
			+ "goal_term integer , bmr double , bmi double , act integer , rcmd_intake double ,"
			+ "diet_rcmd_intake double , total_remove_cal double , diet_rcmd_intake_day double, point";
	// 일별 정보 Table attributes
	private final static String DAY_CREATE_COMPONENT = "user_id string, day_date date, intake_cal double, "
			+ "used_cal double, day_goal_result string, "
			+ "now_cal double, now_status integer, join_room_id integer";

	// 사용자 정보 Table을 만들기 위한 SQL문
	public static final String SQL_CREATE_INFO = "create table info ("
			+ INFO_CREATE_COMPONENT + ")";
	// 일별 정보 Table을 만들기 위한 SQL문
	public static final String SQL_CREATE_DAY = "create table daydata ("
			+ DAY_CREATE_COMPONENT + ")";

	private final Context cxt; // Context정보를 받기 위한 변수

	/**
	 * SQLite사용을 위한 HelperClass
	 * 
	 * @author Jaewon Lee(jaewon87@naver.com)
	 * @version 1.0
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		/**
		 * DatabaseHelper의 생성자 (기본)
		 * 
		 * @param context
		 *            Database를 생성할 Context
		 */
		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		/**
		 * DatabaseHelper의 생성자 (수동)
		 * 
		 * @param context
		 *            Database를 생성할 Context
		 * @param name
		 *            Database 이름
		 * @param factory
		 *            CursorFactory형 factory
		 * @param version
		 *            Database version
		 */
		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(SQL_CREATE_INFO);
			db.execSQL(SQL_CREATE_DAY);
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}

	/**
	 * SBDBAdapter의 생성자
	 * 
	 * @param cxt
	 *            - Database를 생성할 Context
	 * @param sql
	 *            - Table을 생성하기 위한 SQL문
	 * @param tablename
	 *            - Table 이름
	 */
	public SBDBAdapter(Context cxt, String sql, String tablename) {
		this.cxt = cxt;
		TABLE_NAME = tablename;
	}

	/**
	 * DB를 Open하는 메소드 (Table이 생성되있는지 확인하고 없으면 Table까지 생성)
	 * 
	 * @return 생성된 DBAdapter객체
	 * @throws SQLException
	 */
	public SBDBAdapter openDB() throws SQLException {
		helper = new DatabaseHelper(cxt);
		db = helper.getWritableDatabase();
		return this;
	}

	/**
	 * DB를 Close하는 메소드
	 */
	public void close() {
		helper.close();
	}

	/**
	 * ContentValue형 values를 Table에 Insert
	 * 
	 * @param values
	 *            ContentValue형 객체
	 * @return 새로운 rowID를 반환, 에러시 -1
	 */
	public long insertTable(ContentValues values) {
		return db.insert(TABLE_NAME, null, values);
	}

	public long insertTable(String table_name, ContentValues values) {
		return db.insert(table_name, null, values);
	}

	/**
	 * Table에서 조건(Where절)에 따라 data를 Delete
	 * 
	 * @param pkColumn
	 *            비교하는 해당 Column의 이름
	 * @param pkData
	 *            비교하는 값
	 * @return Delete 성공시, True 그렇지 않으면 False
	 */
	public boolean deleteTable(String pkColumn, long pkData) {
		return db.delete(TABLE_NAME, pkColumn + "=" + pkData, null) > 0;
	}

	/**
	 * Table에서 Query에 따라 data를 Select
	 * 
	 * @param columns
	 *            select를 수행할 Columns
	 * @param selection
	 *            조건(Where절)
	 * @param selectionArgs
	 *            selection을 나타내기 위한 args
	 * @param groupBy
	 *            groupBy절
	 * @param having
	 *            having절
	 * @param orderBy
	 *            orderBy절
	 * @return Select된 Cursor객체
	 */

	public Cursor selectTable(String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		return db.query(TABLE_NAME, columns, selection, selectionArgs, groupBy,
				having, orderBy);
	}

	public Cursor selectTable(String table_name, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		return db.query(table_name, columns, selection, selectionArgs, groupBy,
				having, orderBy);
	}

	/**
	 * Table에서 조건(Where절)에 따라 data를 Update
	 * 
	 * @param values
	 *            ContentValue형 객체
	 * @param pkColumn
	 *            비교하는 해당 Column의 이름
	 * @param pkData
	 *            비교하는 값
	 * @return Update 성공시 True, 그렇지 않으면 False
	 */
	public boolean updateTable(ContentValues values, String pkColumn,
			long pkData) {
		return db.update(TABLE_NAME, values, pkColumn + "=" + pkData, null) > 0;
	}

	/**
	 * Table에서 조건절을 한꺼번에 받아 data를 Update
	 * 
	 * @param values
	 *            ContentValue형 객체
	 * @param where
	 *            where절
	 * @return Update 성공시 True, 그렇지 않으면 False
	 */
	public boolean updateTable(ContentValues values, String where) {
		return db.update(TABLE_NAME, values, where, null) > 0;
	}

	public boolean updateTable(String tabel_name, ContentValues values,
			String pkColumn, long pkData) {
		return db.update(tabel_name, values, pkColumn + "=" + pkData, null) > 0;
	}

	public boolean updateTable(String tabel_name, ContentValues values,
			String where) {
		return db.update(tabel_name, values, where, null) > 0;
	}
}
