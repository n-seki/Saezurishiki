package com.seki.saezurishiki.control;

import android.support.annotation.NonNull;

import com.seki.saezurishiki.R;

import twitter4j.Relationship;

/**
 *
 */
public class RelationshipModel {

    private boolean loginUserFollowing = false;
    private boolean loginUserFollowed = false;
    private boolean blocking = false;
    private boolean loginUserMuting = false;
    private boolean isYourself;
    private final long userId;

    public RelationshipModel(long userId) {
        this.userId = userId;
    }

    public void update(@NonNull Relationship relationship) {

        if (relationship.getSourceUserId() == this.userId) {
            this.isYourself = true;
            return;
        }

        loginUserMuting = relationship.isSourceMutingTarget();

        if (relationship.isSourceBlockingTarget()) {
            loginUserFollowing = false;
            loginUserFollowed = false;
            blocking = true;
            return;
        }

        if (relationship.isSourceFollowingTarget() && relationship.isSourceFollowedByTarget()) {
            loginUserFollowing = true;
            loginUserFollowed = true;
        }
        else if (relationship.isSourceFollowingTarget()) {
            loginUserFollowing = true;
            loginUserFollowed = false;
        }
        else if (relationship.isSourceFollowedByTarget()) {
            loginUserFollowing = false;
            loginUserFollowed = true;
        }
        else {
            loginUserFollowing = false;
            loginUserFollowed = false;
        }
    }

    public int toStringResource() {

        if (this.isYourself) {
            return R.string.showing_loginUser;
        }

        if (this.blocking) {
            return R.string.blocking_state_message;
        }

        if (this.loginUserFollowing && this.loginUserFollowed) {
            return R.string.follow_each_other_message;
        }

        if (this.loginUserFollowing) {
            return R.string.login_user_folloing_only_message;
        }

        if (this.loginUserFollowed) {
            return R.string.login_user_followed_only_message;
        }

        return R.string.non_relation_message;
    }


    public boolean isFollowByLoginUser() {
        return this.loginUserFollowing;
    }

    public void onFollowedByLoginUser() {
        this.loginUserFollowing = true;
    }

    public boolean isYourself() {
        return isYourself;
    }

    public void onRemovedByLoginUser() {
        this.loginUserFollowing = false;
    }

    public void onBlock() {
        this.loginUserFollowing = false;
        this.loginUserFollowed = false;
        this.blocking = true;
    }

    public void onReleaseBlock() {
        this.blocking = false;
    }

    public boolean isBlocking() {
        return this.blocking;
    }

    public boolean isMutualFollow() {
        return this.loginUserFollowing && this.loginUserFollowed;
    }

//
//    public void onFollowingLoginUser() {
//        this.loginUserFollowed = true;
//    }
//
//    public void onRemovingLoginUser() {
//        this.loginUserFollowed = false;
//    }
}
