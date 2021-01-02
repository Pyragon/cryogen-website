package com.cryo.managers.tasks;

import com.cryo.entities.Task;
import com.cryo.entities.WebStart;
import com.cryo.entities.WebStartSubscriber;
import com.cryo.entities.accounts.HSData;
import com.cryo.modules.account.AccountUtils;

import java.util.List;

import static com.cryo.Website.getConnection;

@WebStartSubscriber
public class RefreshHighscoresTask extends Task {

    public RefreshHighscoresTask() {
        super("* * * %30");
    }

    @Override
    public void run() {
        refreshOverallRanks();
    }

    @WebStart
    public static void refreshOverallRanks() {
        List<HSData> hsdata = getConnection("cryogen_global").selectList("highscores", null, "ORDER BY total_level DESC, total_xp DESC, total_xp_stamp DESC", HSData.class, null);
        int rank = 1;
        for(HSData data : hsdata)
            AccountUtils.getOverallRanks().put(data.getUsername(), rank++);
    }
}
