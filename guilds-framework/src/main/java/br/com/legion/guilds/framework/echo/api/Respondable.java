package br.com.legion.guilds.framework.echo.api;

public interface Respondable<T extends Response> {

    T getResponse();

    void setResponse(T response);
}
