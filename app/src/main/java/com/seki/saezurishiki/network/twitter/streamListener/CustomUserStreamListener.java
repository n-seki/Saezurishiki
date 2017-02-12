package com.seki.saezurishiki.network.twitter.streamListener;

/**
 * カスタマイズUserStreamListener<br>
 * ユーザーストリームの通知を受け取りたいクラスはこのinterfaceを実装して
 * Listener登録すること
 *
 * @author seki
 */
public interface CustomUserStreamListener
extends StatusUserStreamListener, DirectMessageUserStreamListener, UserStreamUserListener{
}
