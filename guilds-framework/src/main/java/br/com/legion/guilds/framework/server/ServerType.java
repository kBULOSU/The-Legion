package br.com.legion.guilds.framework.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ServerType {

    LOBBY("Lobby"),
    MAIN("Principal");

    private final String id;

}
