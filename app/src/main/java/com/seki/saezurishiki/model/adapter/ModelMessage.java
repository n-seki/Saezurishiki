package com.seki.saezurishiki.model.adapter;


import com.seki.saezurishiki.entity.UserEntity;

public class ModelMessage {
    public final ModelActionType type;
    public final Object data;
    public final Exception exception;

    public final UserEntity source;
    public final UserEntity target;

    private ModelMessage(ModelActionType type, Object data, UserEntity source, UserEntity target, Exception e) {
        this.type = type;
        this.data = data;
        this.exception = e;

        this.source = source;
        this.target = target;
    }

    public static ModelMessage of(ModelActionType type, Object data) {
        return new ModelMessage(type, data, null, null, null);
    }

    public static ModelMessage of(ModelActionType type, Object data, UserEntity source, UserEntity target) {
        return new ModelMessage(type, data, source, target, null);
    }

    public static ModelMessage error(Exception e) {
        return new ModelMessage(ModelActionType.ERROR, null, null, null, e);
    }

}
