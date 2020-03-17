package com.cryo.modules.forums.bbcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cryo.db.impl.ForumConnection;
import com.cryo.entities.forums.BBCode;
import com.cryo.utils.Utilities;

public class BBCodeManager {

    private ArrayList<BBCode> bbcodes;

    public void load() {
        bbcodes = ForumConnection.connection().selectList("bbcode", BBCode.class);
    }

    public String getFormattedPost(String post) {
        while(true) {
            int startPos = -1;
            int endPos = -1;
            BBCode useCode = null;
            for(BBCode code : bbcodes) {
                Pattern pattern = Pattern.compile(code.getRegex());
                Matcher matcher = pattern.matcher(post);
                if(matcher.find()) {
                    if(startPos == -1 || matcher.start() < startPos) {
                        startPos = matcher.start();
                        endPos = matcher.end();
                        useCode = code;
                    }
                }
            }
            if(startPos == -1) break;
            HashMap<Integer, String> groups = new HashMap<>();
            Pattern pattern = Pattern.compile(useCode.getRegex());
            Matcher matcher = pattern.matcher(post);
            if(matcher.find()) {
                for(int i = 1; i < matcher.groupCount()+1; i++)
                    groups.put(i-1, matcher.group(i));
            }
            String replacement = useCode.getReplacement();
            Pattern replacements = Pattern.compile("\\$\\{\\d+(\\|\\d+)?\\}");
            Matcher rMatcher = replacements.matcher(replacement);
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
            post = post.substring(0, startPos)
                +replacement
                +post.substring(endPos, post.length());
        }
        return post;
    }

}