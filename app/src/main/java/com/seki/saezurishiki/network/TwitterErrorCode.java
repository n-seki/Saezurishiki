package com.seki.saezurishiki.network;

/**
 * TwitterExceptionで通知されるErrorCode一覧
 * @author seki
 */
public class TwitterErrorCode {

    private TwitterErrorCode() {}

    public static final int PAGE_NOT_EXIST = 34;

    public static final int NOT_AUTHORIZED_TO_SEE_STATUS = 179;

    public static final int STATUS_IS_DUPLICATE = 187;

    public static final int RATE_LIMIT_EXCEEDED = 88;

    public static final int CANNOT_MUTE_YOURSELF = 271;

    public static final int BAD_GATEWAY = 502;
}
