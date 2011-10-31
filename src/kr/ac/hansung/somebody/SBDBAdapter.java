package kr.ac.hansung.somebody;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite�� �����ϴ� API���� ����ϱ� ���� DBAdapter Ŭ����
 * 
 * @author Jaewon Lee(jaewon87@naver.com)
 * @version 1.0
 */
public class SBDBAdapter {

	private DatabaseHelper helper; // SQLiteHelper�� ��ӹ��� DatabaseHelper�� ����

	private SQLiteDatabase db; // db����

	private static final String DB_NAME = "somebody.db"; // DB file �̸�
	private static final int DB_VERSION = 1; // DB version
	private static String TABLE_NAME; // Table �̸�

	// ����� ���� Table attributes
	private final static String INFO_CREATE_COMPONENT = "user_id string primary key , nick string , weight integer , age integer , "
			+ "gender boolean , height integer, level integer, exp integer, goal_weight integer ,"
			+ "goal_term integer , bmr double , bmi double , act integer , rcmd_intake double ,"
			+ "diet_rcmd_intake double , total_remove_cal double , diet_rcmd_intake_day double, point";
	// �Ϻ� ���� Table attributes
	private final static String DAY_CREATE_COMPONENT = "user_id string, day_date date, intake_cal double, "
			+ "used_cal double, day_goal_result string, "
			+ "now_cal double, now_status integer, join_room_id integer";

	// ����� ���� Table�� ����� ���� SQL��
	public static final String SQL_CREATE_INFO = "create table info ("
			+ INFO_CREATE_COMPONENT + ")";
	// �Ϻ� ���� Table�� ����� ���� SQL��
	public static final String SQL_CREATE_DAY = "create table daydata ("
			+ DAY_CREATE_COMPONENT + ")";

	private final Context cxt; // Context������ �ޱ� ���� ����

	/**
	 * SQLite����� ���� HelperClass
	 * 
	 * @author Jaewon Lee(jaewon87@naver.com)
	 * @version 1.0
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		/**
		 * DatabaseHelper�� ������ (�⺻)
		 * 
		 * @param context
		 *            Database�� ������ Context
		 */
		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		/**
		 * DatabaseHelper�� ������ (����)
		 * 
		 * @param context
		 *            Database�� ������ Context
		 * @param name
		 *            Database �̸�
		 * @param factory
		 *            CursorFactory�� factory
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
	 * SBDBAdapter�� ������
	 * 
	 * @param cxt
	 *            - Database�� ������ Context
	 * @param sql
	 *            - Table�� �����ϱ� ���� SQL��
	 * @param tablename
	 *            - Table �̸�
	 */
	public SBDBAdapter(Context cxt, String sql, String tablename) {
		this.cxt = cxt;
		TABLE_NAME = tablename;
	}

	/**
	 * DB�� Open�ϴ� �޼ҵ� (Table�� �������ִ��� Ȯ���ϰ� ������ Table���� ����)
	 * 
	 * @return ������ DBAdapter��ü
	 * @throws SQLException
	 */
	public SBDBAdapter openDB() throws SQLException {
		helper = new DatabaseHelper(cxt);
		db = helper.getWritableDatabase();
		return this;
	}

	/**
	 * DB�� Close�ϴ� �޼ҵ�
	 */
	public void close() {
		helper.close();
	}

	/**
	 * ContentValue�� values�� Table�� Insert
	 * 
	 * @param values
	 *            ContentValue�� ��ü
	 * @return ���ο� rowID�� ��ȯ, ������ -1
	 */
	public long insertTable(ContentValues values) {
		return db.insert(TABLE_NAME, null, values);
	}

	public long insertTable(String table_name, ContentValues values) {
		return db.insert(table_name, null, values);
	}

	/**
	 * Table���� ����(Where��)�� ���� data�� Delete
	 * 
	 * @param pkColumn
	 *            ���ϴ� �ش� Column�� �̸�
	 * @param pkData
	 *            ���ϴ� ��
	 * @return Delete ������, True �׷��� ������ False
	 */
	public boolean deleteTable(String pkColumn, long pkData) {
		return db.delete(TABLE_NAME, pkColumn + "=" + pkData, null) > 0;
	}

	/**
	 * Table���� Query�� ���� data�� Select
	 * 
	 * @param columns
	 *            select�� ������ Columns
	 * @param selection
	 *            ����(Where��)
	 * @param selectionArgs
	 *            selection�� ��Ÿ���� ���� args
	 * @param groupBy
	 *            groupBy��
	 * @param having
	 *            having��
	 * @param orderBy
	 *            orderBy��
	 * @return Select�� Cursor��ü
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
	 * Table���� ����(Where��)�� ���� data�� Update
	 * 
	 * @param values
	 *            ContentValue�� ��ü
	 * @param pkColumn
	 *            ���ϴ� �ش� Column�� �̸�
	 * @param pkData
	 *            ���ϴ� ��
	 * @return Update ������ True, �׷��� ������ False
	 */
	public boolean updateTable(ContentValues values, String pkColumn,
			long pkData) {
		return db.update(TABLE_NAME, values, pkColumn + "=" + pkData, null) > 0;
	}

	/**
	 * Table���� �������� �Ѳ����� �޾� data�� Update
	 * 
	 * @param values
	 *            ContentValue�� ��ü
	 * @param where
	 *            where��
	 * @return Update ������ True, �׷��� ������ False
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
