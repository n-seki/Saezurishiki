package com.seki.saezurishiki.model.adapter;


public class ModelMessage {
    public final ModelActionType type;
    public final Object data;
    public final Exception exception;

    private ModelMessage(ModelActionType type, Object data, Exception e) {
        this.type = type;
        this.data = data;
        this.exception = e;
    }

    public static ModelMessage of(ModelActionType type, Object data) {
        return new ModelMessage(type, data, null);
    }

    public static ModelMessage error(Exception e) {
        return new ModelMessage(ModelActionType.ERROR, null, e);
    }

}
