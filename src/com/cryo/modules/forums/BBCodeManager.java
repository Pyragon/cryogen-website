package com.cryo.modules.forums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.forums.BBCode;
import com.cryo.entities.forums.Post;
import com.cryo.modules.account.entities.Account;
import com.cryo.utils.Utilities;

import org.apache.commons.lang3.StringEscapeUtils;

import de.neuland.jade4j.Jade4J;

public class BBCodeManager {

    private ArrayList<BBCode> bbcodes;

    public void load() {
        bbcodes = ForumConnection.connection().selectList("bbcodes", BBCode.class);
    }

    public boolean isBetween(ArrayList<Integer[]> list, int start, int end) {
        for (Integer[] val : list)
            if (start > val[0] && end < val[1])
                return true;
        return false;
    }

    public String getFormattedPost(String regex, String replacement, String post) {
        try {
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(post);
            int startPos = -1;
            int endPos = -1;
            HashMap<Integer, String> groups = new HashMap<>();
            if (matcher.find()) {
                startPos = matcher.start();
                endPos = matcher.end();
                for (int i = 1; i < matcher.groupCount() + 1; i++)
                    groups.put(i - 1, matcher.group(i));
            } else {
                System.out.println("no find");
                return post;
            }
            Pattern replacements = Pattern.compile("\\$\\{\\d+(\\|\\d+)?\\}");
            Matcher rMatcher = replacements.matcher(replacement);
            while (rMatcher.find()) {
                String group = rMatcher.group();
                int groupId;
                if (!group.contains("|"))
                    groupId = Integer.parseInt(group.substring(2, group.length() - 1));
                else {
                    String[] split = group.substring(2, group.length() - 1).split("\\|");
                    int first = Integer.parseInt(split[0]);
                    if (first > matcher.groupCount() || groups.get(first).equals(""))
                        groupId = Integer.parseInt(split[1]);
                    else
                        groupId = first;
                }
                replacement = replacement.replace(group, groups.get(groupId));
            }
            post = post.substring(0, startPos) + replacement + post.substring(endPos);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return post;
    }

    public ArrayList<String> getCSS(String post) {
        ArrayList<String> list = new ArrayList<>();
        for (BBCode code : bbcodes) {
            Pattern pattern = Pattern.compile(code.getRegex(), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(post);
            if (matcher.find()) 
                list.add(code.getCSS());
        }
        return list;
    }

    public String getFormattedPost(Account account, String post) {
        post = StringEscapeUtils.escapeHtml4(post);
        ArrayList<Integer[]> noBetween = new ArrayList<>();
        ArrayList<Integer> noCheck = new ArrayList<>();
        wh: while(true) {
            int startPos = -1;
            int endPos = -1;
            BBCode useCode = null;
            for(BBCode code : bbcodes) {
                Pattern pattern = Pattern.compile(code.getRegex(), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(post);
                if(matcher.find()) {
                    if(isBetween(noBetween, matcher.start(), matcher.end())) continue;
                    if(noCheck.contains(matcher.start())) continue;
                    if(startPos == -1 || matcher.start() < startPos) {
                        startPos = matcher.start();
                        endPos = matcher.end();
                        useCode = code;
                    }
                }
            }
            if(startPos == -1) break;
            HashMap<Integer, String> groups = new HashMap<>();
            Pattern pattern = Pattern.compile(useCode.getRegex(), Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(post);
            if(matcher.find()) {
                for(int i = 1; i < matcher.groupCount()+1; i++)
                    groups.put(i-1, matcher.group(i));
            }
            String replacement = useCode.getReplacement();
            Pattern replacements = Pattern.compile("\\$\\{\\d+(\\|\\d+)?\\}");
            Matcher rMatcher = replacements.matcher(replacement);
            if (useCode.getName().equals("Quote")) {
                try {
                    String postString = groups.get(0);
                    int postId;
                    try {
                        postId = Integer.parseInt(postString);
                    } catch(Exception e) {
                        noCheck.add(startPos);
                        continue wh;
                    }
                    Post postDao = ForumConnection.connection().selectClass("posts", "id=?", Post.class, postId);
                    if(postDao == null) {
                        noCheck.add(startPos);
                        continue wh;
                    }
                    if(!postDao.getThread().getSubForum().getPermissions().canReadThread(postDao.getThread(), account)) {
                        noCheck.add(startPos);
                        continue wh;
                    }
                    HashMap<String, Object> model = new HashMap<>();
                    model.put("post", postDao);
                    String html = Jade4J.render("./source/modules/forums/admin/bbcodes/impl/quote.jade", model);
                    post = post.substring(0, startPos) + html + post.substring(endPos, post.length());
                    continue wh;
                } catch(Exception e) {
                    e.printStackTrace();
                    noCheck.add(startPos);
                    continue wh;
                }
            } else if(useCode.getName().equals("Post Count")) {
                try {
                String postCountString = groups.get(0);
                int postCount;
                try {
                    postCount = Integer.parseInt(postCountString);
                } catch(Exception e) {
                    noCheck.add(startPos);
                    continue wh;
                }
                HashMap<String, Object> model = new HashMap<>();
                model.put("count", postCount);
                model.put("post", groups.get(1));
                model.put("user", account);
                String html = Jade4J.render("./source/modules/forums/admin/bbcodes/impl/post_count.jade", model);
                post = post.substring(0, startPos) + html + post.substring(endPos, post.length());
                continue wh;
                } catch(Exception e) {
                    e.printStackTrace();
                    noCheck.add(startPos);
                    continue wh;
                }
            }
            while(rMatcher.find()) {
                String group = rMatcher.group();
                int groupId;
                if(!group.contains("|"))
                    groupId = Integer.parseInt(group.substring(2, group.length()-1));
                else {
                    String[] split = group.substring(2, group.length()-1).split("\\|");
                    int first = Integer.parseInt(split[0]);
                    if(first > matcher.groupCount())
                        groupId = Integer.parseInt(split[1]);
                    else groupId = first;
                }
                replacement = replacement.replace(group, groups.get(groupId));
            }
            if(!useCode.isAllowNested()) noBetween.add(new Integer[] { startPos, startPos+replacement.length() });
            post = post.substring(0, startPos)
                +replacement
                +post.substring(endPos, post.length());
        }
        if(post.contains("{{name}}"))
            post = post.replaceAll("\\{\\{name\\}\\}", account == null ? "Guest" : ForumUtils.crownUser(account, 14, 15));
        return post;
    }

}