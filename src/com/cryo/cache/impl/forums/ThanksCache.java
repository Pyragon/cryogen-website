package com.cryo.cache.impl.forums;

import com.cryo.cache.CachedItem;
import com.cryo.db.impl.ForumConnection;
import com.cryo.db.impl.GlobalConnection;
import com.cryo.entities.forums.Thanks;
import com.cryo.modules.account.entities.Account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ThanksCache extends CachedItem {

    private HashMap<Integer, Integer> thanksGivenCount;
    private HashMap<Integer, Integer> thanksReceivedCount;
    private HashMap<Integer, ArrayList<Account>> thanks;

    private HashMap<Integer, Long> thanksCountTimes;
    private HashMap<Integer, Long> thanksTimes;

    public ThanksCache() {
        super("thanks-cache");
        thanksGivenCount = new HashMap<>();
        thanksReceivedCount = new HashMap<>();
        thanks = new HashMap<>();
        thanksCountTimes = new HashMap<>();
        thanksTimes = new HashMap<>();
    }

    @Override
    public Object getCachedData(Object... values) {
        String opcode = (String) values[0];
        int id = (int) values[1];
        if(hasExpired(values)) fetchNewData(values);
        if(hasExpired(values)) fetchNewData(values);
        if(opcode.equals("count-given")) {
            if(!thanksGivenCount.containsKey(id)) return null;
            return thanksGivenCount.get(id);
        } else if(opcode.equals("count-received")) {
            if(!thanksReceivedCount.containsKey(id)) return null;
            return thanksReceivedCount.get(id);
        }
        if(!thanks.containsKey(id)) return null;
        return thanks.get(id);
    }

    @Override
    public void fetchNewData(Object... values) {
        String opcode = (String) values[0];
        int id = (int) values[1];
        if(opcode.equals("count-given")) {
            int count = ForumConnection.connection().selectCount("thanks", "account_id=?", id);
            thanksGivenCount.put(id, count);
            thanksCountTimes.put(id, System.currentTimeMillis()+getCacheTimeLimit());
        } else if(opcode.equals("count-received")) {
            int count = ForumConnection.connection().selectCount("thanks", "author_id=?", id);
            thanksReceivedCount.put(id, count);
            thanksCountTimes.put(id, System.currentTimeMillis()+getCacheTimeLimit());
        } else {
            ArrayList<Thanks> thanks = ForumConnection.connection().selectList("thanks", "post_id=?", Thanks.class, id);
            ArrayList<Account> accounts = thanks.stream().map(t -> GlobalConnection.connection().selectClass("player_data", "id=?", Account.class, t.getAccountId())).collect(Collectors.toCollection(ArrayList::new));
            this.thanks.put(id, accounts);
            thanksTimes.put(id, System.currentTimeMillis()+getCacheTimeLimit());
        }
    }

    public void clear() {
        thanksTimes.clear();
        thanksCountTimes.clear();
    }

    @Override
    public boolean hasExpired(Object... values) {
        String opcode = (String) values[0];
        int id = (int) values[1];
        if(opcode.contains("count")) {
            if(!thanksCountTimes.containsKey(id)) return true;
            return thanksCountTimes.get(id) > System.currentTimeMillis();
        }
        if(!thanksTimes.containsKey(id)) return true;
        return thanksTimes.get(id) > System.currentTimeMillis();
    }

    @Override
    public long getCacheTimeLimit() {
        return 120_000;
    }
}
