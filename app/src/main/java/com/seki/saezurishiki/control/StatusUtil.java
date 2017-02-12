package com.seki.saezurishiki.control;

import com.seki.saezurishiki.entity.TweetEntity;

import java.util.ArrayList;
import java.util.List;

import twitter4j.UserMentionEntity;

public final class StatusUtil {

    private StatusUtil() {}


    public static List<String> getAllUserMentionName(TweetEntity status, long loginUserId) {
        if (status == null) {
            throw new NullPointerException("status is null!");
        }

        UserMentionEntity[] users = status.userMentionEntities;

        if (users == null || users.length == 0) {
            List<String> nameList = new ArrayList<>();
            nameList.add(status.user.getScreenName());
            return nameList;
        }

        final List<String> userList = new ArrayList<>();
        userList.add(status.user.getScreenName());

        for (UserMentionEntity user : users) {
            if (user.getId() == status.user.getId() ||
                user.getId() == loginUserId) {
                continue;
            }

            userList.add(user.getScreenName());
        }

        return userList;
    }


    public static List<Long> getAllUserMentionId(TweetEntity status, long loginUserId) {
        if (status == null) {
            throw new NullPointerException("status is null!");
        }

        UserMentionEntity[] users = status.userMentionEntities;

        if (users == null || users.length == 0) {
            List<Long> nameList = new ArrayList<>();
            nameList.add(status.user.getId());
            return nameList;
        }

        final List<Long> userList = new ArrayList<>();
        userList.add(status.user.getId());

        for (UserMentionEntity user : users) {
            if (user.getId() == status.user.getId() ||
                    user.getId() == loginUserId) {
                continue;
            }

            userList.add(user.getId());
        }

        return userList;
    }
}
