package com.orbit.blocker.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.orbit.blocker.data.model.Question;
import com.orbit.blocker.data.model.QuizTopic;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class QuestionDao_Impl implements QuestionDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Question> __insertionAdapterOfQuestion;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<Question> __deletionAdapterOfQuestion;

  private final EntityDeletionOrUpdateAdapter<Question> __updateAdapterOfQuestion;

  public QuestionDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfQuestion = new EntityInsertionAdapter<Question>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `questions` (`id`,`topic`,`prompt`,`choices`,`correctIndex`,`explanation`,`seeded`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Question entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.quizTopicToString(entity.getTopic());
        statement.bindString(2, _tmp);
        statement.bindString(3, entity.getPrompt());
        final String _tmp_1 = __converters.choicesToString(entity.getChoices());
        statement.bindString(4, _tmp_1);
        statement.bindLong(5, entity.getCorrectIndex());
        if (entity.getExplanation() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getExplanation());
        }
        final int _tmp_2 = entity.getSeeded() ? 1 : 0;
        statement.bindLong(7, _tmp_2);
      }
    };
    this.__deletionAdapterOfQuestion = new EntityDeletionOrUpdateAdapter<Question>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `questions` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Question entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfQuestion = new EntityDeletionOrUpdateAdapter<Question>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `questions` SET `id` = ?,`topic` = ?,`prompt` = ?,`choices` = ?,`correctIndex` = ?,`explanation` = ?,`seeded` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Question entity) {
        statement.bindLong(1, entity.getId());
        final String _tmp = __converters.quizTopicToString(entity.getTopic());
        statement.bindString(2, _tmp);
        statement.bindString(3, entity.getPrompt());
        final String _tmp_1 = __converters.choicesToString(entity.getChoices());
        statement.bindString(4, _tmp_1);
        statement.bindLong(5, entity.getCorrectIndex());
        if (entity.getExplanation() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getExplanation());
        }
        final int _tmp_2 = entity.getSeeded() ? 1 : 0;
        statement.bindLong(7, _tmp_2);
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final Question question, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfQuestion.insertAndReturnId(question);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<Question> questions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfQuestion.insert(questions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final Question question, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfQuestion.handle(question);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Question question, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfQuestion.handle(question);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Question>> observeAll() {
    final String _sql = "SELECT * FROM questions ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"questions"}, new Callable<List<Question>>() {
      @Override
      @NonNull
      public List<Question> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTopic = CursorUtil.getColumnIndexOrThrow(_cursor, "topic");
          final int _cursorIndexOfPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "prompt");
          final int _cursorIndexOfChoices = CursorUtil.getColumnIndexOrThrow(_cursor, "choices");
          final int _cursorIndexOfCorrectIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctIndex");
          final int _cursorIndexOfExplanation = CursorUtil.getColumnIndexOrThrow(_cursor, "explanation");
          final int _cursorIndexOfSeeded = CursorUtil.getColumnIndexOrThrow(_cursor, "seeded");
          final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Question _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final QuizTopic _tmpTopic;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfTopic);
            _tmpTopic = __converters.stringToQuizTopic(_tmp);
            final String _tmpPrompt;
            _tmpPrompt = _cursor.getString(_cursorIndexOfPrompt);
            final List<String> _tmpChoices;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfChoices);
            _tmpChoices = __converters.stringToChoices(_tmp_1);
            final int _tmpCorrectIndex;
            _tmpCorrectIndex = _cursor.getInt(_cursorIndexOfCorrectIndex);
            final String _tmpExplanation;
            if (_cursor.isNull(_cursorIndexOfExplanation)) {
              _tmpExplanation = null;
            } else {
              _tmpExplanation = _cursor.getString(_cursorIndexOfExplanation);
            }
            final boolean _tmpSeeded;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfSeeded);
            _tmpSeeded = _tmp_2 != 0;
            _item = new Question(_tmpId,_tmpTopic,_tmpPrompt,_tmpChoices,_tmpCorrectIndex,_tmpExplanation,_tmpSeeded);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Question>> observeByTopic(final QuizTopic topic) {
    final String _sql = "SELECT * FROM questions WHERE topic = ? ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.quizTopicToString(topic);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"questions"}, new Callable<List<Question>>() {
      @Override
      @NonNull
      public List<Question> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTopic = CursorUtil.getColumnIndexOrThrow(_cursor, "topic");
          final int _cursorIndexOfPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "prompt");
          final int _cursorIndexOfChoices = CursorUtil.getColumnIndexOrThrow(_cursor, "choices");
          final int _cursorIndexOfCorrectIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctIndex");
          final int _cursorIndexOfExplanation = CursorUtil.getColumnIndexOrThrow(_cursor, "explanation");
          final int _cursorIndexOfSeeded = CursorUtil.getColumnIndexOrThrow(_cursor, "seeded");
          final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Question _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final QuizTopic _tmpTopic;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTopic);
            _tmpTopic = __converters.stringToQuizTopic(_tmp_1);
            final String _tmpPrompt;
            _tmpPrompt = _cursor.getString(_cursorIndexOfPrompt);
            final List<String> _tmpChoices;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfChoices);
            _tmpChoices = __converters.stringToChoices(_tmp_2);
            final int _tmpCorrectIndex;
            _tmpCorrectIndex = _cursor.getInt(_cursorIndexOfCorrectIndex);
            final String _tmpExplanation;
            if (_cursor.isNull(_cursorIndexOfExplanation)) {
              _tmpExplanation = null;
            } else {
              _tmpExplanation = _cursor.getString(_cursorIndexOfExplanation);
            }
            final boolean _tmpSeeded;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfSeeded);
            _tmpSeeded = _tmp_3 != 0;
            _item = new Question(_tmpId,_tmpTopic,_tmpPrompt,_tmpChoices,_tmpCorrectIndex,_tmpExplanation,_tmpSeeded);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAll(final Continuation<? super List<Question>> $completion) {
    final String _sql = "SELECT * FROM questions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Question>>() {
      @Override
      @NonNull
      public List<Question> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTopic = CursorUtil.getColumnIndexOrThrow(_cursor, "topic");
          final int _cursorIndexOfPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "prompt");
          final int _cursorIndexOfChoices = CursorUtil.getColumnIndexOrThrow(_cursor, "choices");
          final int _cursorIndexOfCorrectIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctIndex");
          final int _cursorIndexOfExplanation = CursorUtil.getColumnIndexOrThrow(_cursor, "explanation");
          final int _cursorIndexOfSeeded = CursorUtil.getColumnIndexOrThrow(_cursor, "seeded");
          final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Question _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final QuizTopic _tmpTopic;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfTopic);
            _tmpTopic = __converters.stringToQuizTopic(_tmp);
            final String _tmpPrompt;
            _tmpPrompt = _cursor.getString(_cursorIndexOfPrompt);
            final List<String> _tmpChoices;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfChoices);
            _tmpChoices = __converters.stringToChoices(_tmp_1);
            final int _tmpCorrectIndex;
            _tmpCorrectIndex = _cursor.getInt(_cursorIndexOfCorrectIndex);
            final String _tmpExplanation;
            if (_cursor.isNull(_cursorIndexOfExplanation)) {
              _tmpExplanation = null;
            } else {
              _tmpExplanation = _cursor.getString(_cursorIndexOfExplanation);
            }
            final boolean _tmpSeeded;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfSeeded);
            _tmpSeeded = _tmp_2 != 0;
            _item = new Question(_tmpId,_tmpTopic,_tmpPrompt,_tmpChoices,_tmpCorrectIndex,_tmpExplanation,_tmpSeeded);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object count(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM questions";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object seededCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM questions WHERE seeded = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object randomQuestions(final int limit, final List<? extends QuizTopic> topics,
      final int allTopics, final Continuation<? super List<Question>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("\n");
    _stringBuilder.append("        SELECT * FROM questions");
    _stringBuilder.append("\n");
    _stringBuilder.append("        WHERE (");
    _stringBuilder.append("?");
    _stringBuilder.append(" = 1 OR topic IN (");
    final int _inputSize = topics.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append("))");
    _stringBuilder.append("\n");
    _stringBuilder.append("        ORDER BY RANDOM()");
    _stringBuilder.append("\n");
    _stringBuilder.append("        LIMIT ");
    _stringBuilder.append("?");
    _stringBuilder.append("\n");
    _stringBuilder.append("        ");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 2 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, allTopics);
    _argIndex = 2;
    for (QuizTopic _item : topics) {
      final String _tmp = __converters.quizTopicToString(_item);
      _statement.bindString(_argIndex, _tmp);
      _argIndex++;
    }
    _argIndex = 2 + _inputSize;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Question>>() {
      @Override
      @NonNull
      public List<Question> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTopic = CursorUtil.getColumnIndexOrThrow(_cursor, "topic");
          final int _cursorIndexOfPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "prompt");
          final int _cursorIndexOfChoices = CursorUtil.getColumnIndexOrThrow(_cursor, "choices");
          final int _cursorIndexOfCorrectIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "correctIndex");
          final int _cursorIndexOfExplanation = CursorUtil.getColumnIndexOrThrow(_cursor, "explanation");
          final int _cursorIndexOfSeeded = CursorUtil.getColumnIndexOrThrow(_cursor, "seeded");
          final List<Question> _result = new ArrayList<Question>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Question _item_1;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final QuizTopic _tmpTopic;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfTopic);
            _tmpTopic = __converters.stringToQuizTopic(_tmp_1);
            final String _tmpPrompt;
            _tmpPrompt = _cursor.getString(_cursorIndexOfPrompt);
            final List<String> _tmpChoices;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfChoices);
            _tmpChoices = __converters.stringToChoices(_tmp_2);
            final int _tmpCorrectIndex;
            _tmpCorrectIndex = _cursor.getInt(_cursorIndexOfCorrectIndex);
            final String _tmpExplanation;
            if (_cursor.isNull(_cursorIndexOfExplanation)) {
              _tmpExplanation = null;
            } else {
              _tmpExplanation = _cursor.getString(_cursorIndexOfExplanation);
            }
            final boolean _tmpSeeded;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfSeeded);
            _tmpSeeded = _tmp_3 != 0;
            _item_1 = new Question(_tmpId,_tmpTopic,_tmpPrompt,_tmpChoices,_tmpCorrectIndex,_tmpExplanation,_tmpSeeded);
            _result.add(_item_1);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
