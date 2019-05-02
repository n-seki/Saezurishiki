package com.seki.saezurishiki.presenter.activity;

import android.os.Handler;
import android.os.Looper;

import com.seki.saezurishiki.control.RelationshipModel;
import com.seki.saezurishiki.control.ScreenNav;
import com.seki.saezurishiki.entity.TweetEntity;
import com.seki.saezurishiki.entity.UserEntity;
import com.seki.saezurishiki.model.UserScreenModel;
import com.seki.saezurishiki.model.adapter.ModelMessage;
import com.seki.saezurishiki.model.util.ModelObservable;
import com.seki.saezurishiki.model.util.ModelObserver;

import twitter4j.Relationship;

public class UserPresenter implements ModelObserver {

    private final View view;
    private final RelationshipModel relationship;
    private final long ownerId;
    private UserEntity owner;
    private final UserScreenModel model;

    public interface View {
        void setupActionBar(UserEntity owner);
        void setupBioInformation(UserEntity owner);
        void setRelationshipText(int text);
        void disableFollowButton();
        void setFollowButton();
        void setRemoveButton();
        void updateOptionMenu();
        void displayFragment(ScreenNav screenNav, UserEntity owner);
        void displayFollowRequestDialog(UserEntity user);
        void displayFollowDialog(UserEntity user, boolean isFollow);
        void showReleaseBlockDialog(UserEntity user);
        void showBlockUserDialog(UserEntity user);
        void showCompleteBlockMessage();
        void showCompleteDestroyBlockMessage();
        void showCompleteSendFollowRequestMessage();
        void showCompleteFollowMessage();
        void showCompleteRemoveMessage();
        void showCompletePostTweetMessage();
        void finishActivity();
        void removeCurrentScreen();
        void removeAllScreen();
        void updateTitle(UserEntity user);
    }

    public UserPresenter(View view, UserScreenModel model, long ownerId) {
        this.view = view;
        this.model = model;
        this.ownerId = ownerId;
        this.relationship = new RelationshipModel(this.ownerId);
    }

    public void onResume() {
        this.model.addObserver(this);
    }

    public void onPause() {
        this.model.removeObserver(this);
    }

    public void loadOwner() {
        if (this.owner != null) {
            return;
        }
        this.model.getUser(this.ownerId);
    }

    private void onLoadOwner(UserEntity user) {
        this.owner = user;
        this.view.setupActionBar(user);
        this.view.setupBioInformation(user);
        this.loadRelationShip();
    }

    private void loadRelationShip() {
        this.model.getRelationship(this.ownerId);
    }

    private void onLoadRelationship(Relationship relation) {
        this.relationship.update(relation);
        this.updateRelationshipView();
    }

    private void updateRelationshipView() {
        this.view.setRelationshipText(this.relationship.toStringResource());

        if (relationship.isYourself()) {
            this.view.disableFollowButton();
        } else if (this.relationship.isFollowByLoginUser()) {
            this.view.setRemoveButton();
        } else {
            this.view.setFollowButton();
        }

        this.view.updateOptionMenu();
    }

    public void onClickFollowButton() {
        if (owner.isProtected() && !this.relationship.isFollowByLoginUser()) {
            this.view.displayFollowRequestDialog(this.owner);
            return;
        }

        this.view.displayFollowDialog(this.owner, this.relationship.isFollowByLoginUser());
    }

    public void onClickButtonList(ScreenNav screenNav) {
        this.view.displayFragment(screenNav, this.owner);
    }

    public void onSelectReleaseBlock() {
        this.view.showReleaseBlockDialog(this.owner);
    }

    public void onSelectBlock() {
        this.view.showBlockUserDialog(this.owner);
    }

    public void onHomePressed(boolean hasScreen) {
        if (!hasScreen) {
            this.view.finishActivity();
            return;
        }

        this.view.removeAllScreen();
        this.view.updateTitle(this.owner);
    }

    public void onBackPressed(boolean hasScreen) {
        if (!hasScreen) {
            this.view.finishActivity();
            return;
        }

        this.view.removeCurrentScreen();
        this.view.updateTitle(this.owner);
    }

    public void follow() {
        this.model.follow(this.ownerId);
    }

    private void onFollow(UserEntity user) {
        if (user.isProtected()) {
            this.onSendFollowRequest(user);
            return;
        }
        this.view.showCompleteFollowMessage();
        this.relationship.onFollowedByLoginUser();
        this.updateRelationshipView();
    }

    public void remove() {
        this.model.remove(this.ownerId);
    }

    private void onRemove(UserEntity user) {
        this.view.showCompleteRemoveMessage();
        this.relationship.onRemovedByLoginUser();
        this.updateRelationshipView();
    }

    public void sendFollowRequest() {
        this.model.follow(this.ownerId);
    }

    private void onSendFollowRequest(UserEntity user) {
        this.view.showCompleteSendFollowRequestMessage();
    }

    public void block() {
        this.model.block(this.ownerId);

    }

    private void onBlock(UserEntity user) {
        this.relationship.onBlock();
        this.updateRelationshipView();
        this.view.showCompleteBlockMessage();
    }

    public void destroyBlock() {
        this.model.destroyBlock(this.ownerId);
    }

    private void onDestroyBlock(UserEntity user) {
        this.relationship.onReleaseBlock();
        this.updateRelationshipView();
        this.view.showCompleteDestroyBlockMessage();
    }

    private void onPostTweet(TweetEntity tweet) {
        this.view.showCompletePostTweetMessage();
    }

    public boolean isBlocking() {
        return this.relationship.isBlocking();
    }

    @Override
    public void update(ModelObservable observable, ModelMessage message) {
        new Handler(Looper.getMainLooper()).post(() -> this.dispatch(message));
    }

    private void dispatch(ModelMessage message) {
        switch (message.type) {
            case LOAD_USER:
                this.onLoadOwner((UserEntity)message.data);
                break;

            case LOAD_RELATIONSHIP:
                this.onLoadRelationship((Relationship)message.data);
                break;

            case COMPLETE_FOLLOW:
                this.onFollow((UserEntity)message.data);
                break;

            case COMPLETE_REMOVE:
                this.onRemove((UserEntity)message.data);
                break;

            case COMPLETE_BLOCK:
                this.onBlock((UserEntity)message.data);
                break;

            case COMPLETE_DESTROY_BLOCK:
                this.onDestroyBlock((UserEntity)message.data);
                break;

            case COMPLETE_POST_TWEET:
                this.onPostTweet((TweetEntity)message.data);
                break;

            default:
                break;
        }
    }

}
