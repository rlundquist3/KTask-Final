package com.example.ktask;

import android.provider.BaseColumns;

public final class FeedReaderHelper {

	public FeedReaderHelper(){}

	public static abstract class FeedEntry implements BaseColumns {
		public static final String TABLE_NAME = "my_table";
		public static final String COLUMN_NAME_ENTRY_ID = "entryid";
		public static final String COLUMN_NAME_TITLE = "title";
		public static final String COLUMN_NAME_COMPLETE = "complete";
		public static final String COLUMN_NAME_DUE = "due";
		public static final String COLUMN_NAME_REMIND = "remind";
		public static final String COLUMN_NAME_DESCRIPTION = "description";
		public static final String COlUMN_NAME_NULLABLE = null;
	}
}
