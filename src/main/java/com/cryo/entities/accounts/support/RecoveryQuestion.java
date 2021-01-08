package com.cryo.entities.accounts.support;

import com.cryo.entities.MySQLDao;
import com.cryo.entities.MySQLDefault;
import com.cryo.entities.annotations.WebStart;
import com.cryo.entities.annotations.WebStartSubscriber;
import lombok.Data;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import static com.cryo.Website.getConnection;

@Data
@WebStartSubscriber
public class RecoveryQuestion extends MySQLDao {

    @Getter
    private static HashMap<Integer, RecoveryQuestion> questions;

    @MySQLDefault
    private final int id;
    private final String question;
    @MySQLDefault
    private final Timestamp added;
    @MySQLDefault
    private final Timestamp updated;

    @WebStart
    public static void loadQuestions() {
        questions = new HashMap<>();
        List<RecoveryQuestion> questions = getConnection("cryogen_recovery").selectList("questions", RecoveryQuestion.class);
        questions.forEach(q -> RecoveryQuestion.questions.put(q.id, q));
    }
}
